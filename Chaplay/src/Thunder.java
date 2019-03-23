

import java.io.InputStream;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.desktop.SystemEventListener;
import java.util.ArrayList;
import java.util.Queue;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.tools.Tool;

import javax.security.auth.kerberos.KerberosPrincipal;
import javax.swing.*;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Thunder extends JFrame
{
	private static final long serialVersionUID = 1L;

	//子弹类
	class Bullet
	{
		static final int BULLET_STEP_Y = 16;
		static final int BULLET_WIDTH = 32;
		public int m_posX = 0;
		public int m_posY = -20;
		
		//是否要更新子弹
		boolean mFacus = true;
		private Image pic[] = null;
		
		//当前帧的ID
		private int mPlayId = 0;
		public Bullet()
		{
			pic = new Image[4];
			for (int i = 0; i < 4; i++)
				pic[i] = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\bullet_" 
																+ i + ".png");
		}
		
		//初始化坐标
		public void init(int x, int y)
		{
			m_posX = x;
			m_posY = y;
			mFacus = false;
		}
		
		//绘制子弹
		public void DrawBullet(Graphics g, JPanel i)
		{
			if (mFacus)
			{
				g.drawImage(pic[mPlayId++], m_posX, m_posY, (ImageObserver) i);
				if (mPlayId == 4) mPlayId = 0;
			}
		}
		
		//更新子弹的坐标点
		public void UpdateBullet()
		{
			if (mFacus)
				m_posY -= BULLET_STEP_Y;
		}
	}

	//敌机类
	class Enemy
	{
		//敌机状态
		public static final int ENEMY_ALIVE_STATE = 0;
		public static final int ENEMY_DEATH_STATE = 1;
		
		//敌机的Y速度
		static final int ENEMY_STEP_Y = 8;
		
		public int m_posX = 0;
		public int m_posY = 0;
		
		//敌机的图片
		Image pic;
		
		//敌机状态
		public int mAnimState = ENEMY_ALIVE_STATE;
		private Image enemyExplorePic[] = new Image[6];
		
		//当前的帧
		public int mPlayId = 0;
		
		public Enemy()
		{
			for (int i = 0; i < 6; i++)
			{
				enemyExplorePic[i] = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\bomb_enemy_" 
																			+ i + ".png");
			}
			pic = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\e1_0.png");
		}
		public void init(int x, int y)
		{
			m_posX = x;
			m_posY = y;
			mAnimState = ENEMY_ALIVE_STATE;
			mPlayId = 0;
		}
		
		//绘制敌机
		public void DrawEnemy(Graphics g, JPanel i)
		{
			//当死亡动画播放完毕，不再绘制敌机
			if (mAnimState == ENEMY_DEATH_STATE && mPlayId < 6)
			{
				g.drawImage(enemyExplorePic[mPlayId], m_posX, m_posY, (ImageObserver) i);
				mPlayId++;
				return;
			}
			else if (mAnimState == ENEMY_ALIVE_STATE)
			{
				g.drawImage(pic, m_posX, m_posY, (ImageObserver) i);
			}
		}
		
		//更新坐标
		public void UpdateEnemy()
		{
			m_posY += ENEMY_STEP_Y;
		}
	}

	//界面部分
	class GamePanel extends JPanel
	{
		//外部Thunder实例的“指针”
		private Thunder thunder;
		
		//屏幕的宽高 
		private final int mScreenWidth = 512;
		private final int mScreenHeight = 512;
		
		//游戏背景图片
		private Image mBitMenuBG0 = null;
		private Image mBitMenuBG1 = null;
		
		//记录两张图片的Y坐标
		private int mBitposY0 = 0;
		private int mBitposY1 = 0;
		
		//玩家分数
		int score;
		
		//字体
		private final int fontSize = 24;
		private Font myFont = new Font("楷体", Font.BOLD, fontSize);
		
		//子弹对象的数量
		final static int BULLET_POOL_COUNT = 15;
		
		//飞机移动的步长
		final static int PLAN_STEP = 16;
		
		//敌人对象的数量
		final static int ENEMY_POOL_COUNT = 4;
		
		//敌人飞机的偏移量
		final static int ENEMY_POS_OFF = 64;
		
		//游戏的定时器
		private Timer timer = new Timer(100, new TimeListener()); //定时器
		private int presentTime = 0;
		
		//线程循环标志
		private boolean mIsRunning = false;
		
		//飞机在屏幕中的坐标
		public int mAirposX = 0;
		public int mAirposY = -64;
		
		//敌机对象数组
		Enemy mEnemy[] = null;
		//子弹对象组
		Bullet mBullet[] = null;
		//存储空闲子弹
		ArrayList<Bullet> reserveBullet = new ArrayList();
		//正在飞行的子弹
		ArrayList<Bullet> flyingBullet = new ArrayList();
		//飞机的帧
		private boolean boom;
		private int bombNum;
		private final int bombCnt = 10;
		BufferedImage bomb[] = new BufferedImage[bombCnt];
		Image myPlanePic[];
		//飞机的当前帧号
		public int myPlaneId = 0;
		
		public GamePanel(Thunder thunder_)
		{
			thunder = thunder_;
			
			setPreferredSize(new Dimension(mScreenWidth, mScreenHeight));
			//设定焦点在本窗体
			setFocusable(true);
			addKeyListener(new MyKeyListener());
			init();
			mIsRunning = true;
			setVisible(true);
		}

		//init()用于初始化各种对象
		private void init()
		{
			score = 0;
			
			try
			{
				mBitMenuBG0 = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\map_0.png");
				mBitMenuBG1 = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\map_1.png");
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "读取背景图片错误！请检查目录下是否存在图片！", "读取错误", 
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			mBitposY0 = 0;
			mBitposY1 = -mScreenHeight;
			
			//玩家的飞机坐标
			mAirposX = mScreenWidth / 2 + 32;
			mAirposY = mScreenHeight - 64;
			boom = false;
			bombNum = -1;
			//初始化玩家飞机相关的6张图片对象
			myPlanePic = new Image[6];
			try
			{
				for (int i = 0; i < 6; i++)
					myPlanePic[i] = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\plan_"
																	+ i + ".png");
				for (int i = 0; i < bombCnt; i++)
					bomb[i] = ImageIO.read(new File(".\\Thunder_material\\bomb_" + i + ".png"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			//创建敌机对象
			mEnemy = new Enemy[ENEMY_POOL_COUNT];
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			{
				mEnemy[i] = new Enemy();
				mEnemy[i].init(i * ENEMY_POS_OFF, i * ENEMY_POS_OFF - 256);
			}
			
			//创建子弹类对象
			mBullet = new Bullet[BULLET_POOL_COUNT];
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				mBullet[i] = new Bullet();
			//将子弹全部加入队列之中
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				reserveBullet.add(mBullet[i]);
			
			//设置当前时间
			presentTime = 0;
			//开始定时器
			timer.start();
		}
		
		//重新初始化
		private void reinit()
		{
			mBitposY0 = 0;
			mBitposY1 = -mScreenHeight;
			
			//玩家的飞机坐标
			mAirposX = mScreenWidth / 2 + 32;
			mAirposY = mScreenHeight - 64;
			
			boom = false;
			bombNum = -1;
			//设置当前时间
			presentTime = 0;
			//开始定时器
			timer.start();			
		}
			
		//绘图
		public void paint(Graphics g)
		{
			//画背景
			g.drawImage(mBitMenuBG0, 0, mBitposY0, this);
			g.drawImage(mBitMenuBG1, 0, mBitposY1, this);
			
			//画角色
			g.drawImage(myPlanePic[myPlaneId], mAirposX, mAirposY, this);
			//画爆炸
			if (boom)
				g.drawImage(bomb[bombNum], mAirposX - 12, mAirposY - 12, this);
			
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				mBullet[i].DrawBullet(g, this);
			
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
				mEnemy[i].DrawEnemy(g, this);
			
			//画分数
			g.setFont(myFont);
			g.setColor(Color.WHITE);
			g.drawString("分数:" + score, 0, 32);
		}
		
		//更新背景
		private void updateBg()
		{
			int movDis = 16;
			//背景滚动
			int initPlace = -mScreenHeight;
			mBitposY0 += movDis;
			mBitposY1 += movDis;
			if (mBitposY0 == mScreenHeight)
				mBitposY0 = initPlace;
			if (mBitposY1 == mScreenHeight)
				mBitposY1 = initPlace;
			
			//更新子弹的位置坐标
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
			{
				mBullet[i].UpdateBullet();
				if (mBullet[i].mFacus && mBullet[i].m_posY <= 0)
				{
					mBullet[i].mFacus = false;
					reserveBullet.add(mBullet[i]); //对于飞出地图边界的bullet放入队列中
				}
			}
			//确认是否发射新的子弹
			if (presentTime == 10)
			{
				presentTime = 0;
				emitBullet();
			}
			
			//更新敌机的位置
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			{
				mEnemy[i].UpdateEnemy();
				
				if (mEnemy[i].mAnimState == Enemy.ENEMY_DEATH_STATE && mEnemy[i].mPlayId == 6
					|| mEnemy[i].m_posY >= mScreenHeight)
				{
					mEnemy[i].init(UtilRandom(0, ENEMY_POOL_COUNT) * ENEMY_POS_OFF, 0);
				}
			}
			
			//碰撞检测
			Collision();
			
			myPlaneId++;
			if (myPlaneId == 6)
				myPlaneId = 0;
			if (boom)
			{
				bombNum++;
				if (bombNum >= bombCnt)
					GameOver();
			}
			
		}
		
		//生成随机数
		private int UtilRandom(int botton, int top)
		{
			return ((Math.abs(new Random().nextInt()) % (top - botton) + botton));
		}
		
		//判断是否碰撞
		public void Collision()
		{
			//子弹和敌人的碰撞
			int cnt = flyingBullet.size();
			Bullet tmp;
			for (int i = 0; i < cnt; i++)
			{
				for (int j = 0; j < ENEMY_POOL_COUNT; j++)
				{
					tmp = flyingBullet.get(i);
					if (tmp.mFacus && tmp.m_posX >= mEnemy[j].m_posX && tmp.m_posX <= mEnemy[j].m_posX + 32
						&& tmp.m_posY >= mEnemy[j].m_posY && tmp.m_posY <= mEnemy[j].m_posY + 64)
					{
						mEnemy[j].mAnimState = Enemy.ENEMY_DEATH_STATE;
						score += 10;//打死一个加10分
						tmp.mFacus = false;
						reserveBullet.add(tmp);
						flyingBullet.remove(tmp);
						return;
					}
				}
			}
			//自机和敌人的碰撞
			for (int j = 0; j < ENEMY_POOL_COUNT; j++)
			{
				//如果撞到敌机，游戏结束
				if (crash(mEnemy[j].m_posX, mEnemy[j].m_posY, 32, 32, mAirposX, mAirposY, 32, 32))
				{
					boom = true;
					return;
				}
			}
		}
		
		//碰撞原理
		private boolean crash(int objx1, int objy1, int objWidth1, int objHeight1, 
				int objx2, int objy2, int objWidth2, int objHeight2)
		{
			int left = objx1, right = objx1 + objWidth1;
			int up = objy1, down = objy1 + objHeight1; //谁能想到up其实是小于down呢
			int x = objx2, y = objy2;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			x = objx2 + objWidth2;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			y = objy2 + objHeight2;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			x = objx2;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;

			left = objx2;
			right = objx2 + objWidth2;
			up = objy2;
			down = objy2 + objHeight2;
			x = objx1;
			y = objy1;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			x = objx1 + objWidth1;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			y = objy1 + objHeight1;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;
			x = objx1;
			if (x <= right && x >= left && y >= up && y <= down)
				return true;		

			return false;
		}
		
		//发射子弹
		public void emitBullet()
		{
			if (!reserveBullet.isEmpty())
			{
				//提取list的一个元素
			//	System.out.println(reserveBullet.size());
				Bullet tmp = reserveBullet.get(reserveBullet.size() - 1);
				reserveBullet.remove(reserveBullet.size() - 1);
				tmp.mFacus = true;
				tmp.m_posX = mAirposX + 10;
				tmp.m_posY = mAirposY;
				flyingBullet.add(tmp);
			}
		}
		
		//键盘监听
		class MyKeyListener implements KeyListener
		{
			public void keyPressed(KeyEvent e)
			{
				if (boom)
					return;
				int key = e.getKeyCode();
			//	System.out.println(key);
				if (key == KeyEvent.VK_UP)
				{
					mAirposY -= PLAN_STEP;
					if (mAirposY < 0)
						mAirposY = 0;
				}
				if (key == KeyEvent.VK_DOWN)
				{
					mAirposY += PLAN_STEP;
					if (mAirposY > mScreenHeight - 64)
						mAirposY = mScreenHeight - 64;
				}
				if (key == KeyEvent.VK_LEFT)
				{
					mAirposX -= PLAN_STEP;
					if (mAirposX < 0)
						mAirposX = 0;
				}
				if (key == KeyEvent.VK_RIGHT)
				{
					mAirposX += PLAN_STEP;
					if (mAirposX > mScreenWidth - 64)
						mAirposX = mScreenWidth - 64;
				}
				if (key == KeyEvent.VK_P)
				{
					if (mIsRunning)
						mIsRunning = false;
					else
						mIsRunning = true;
				}
				
			//	System.out.println(mAirposX + ":" + mAirposY);
			}
			@Override
			public void keyReleased(KeyEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyTyped(KeyEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
		}
		
		//定时监听
		class TimeListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (mIsRunning)
				{
					presentTime++;
					updateBg();
					repaint();
				}
			}
		}
		
		//游戏结束
		public void GameOver()
		{
			timer.stop();
			
			int answer = JOptionPane.showConfirmDialog(null, "游戏结束，是否继续", "游戏结束",
					JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION)
			{
				reinit();
			}
			else
			{
				thunder.dispose();
			}
			emitString("" + score);
		}
	}

	public Thunder()
	{
		setTitle("飞行射击游戏");
		GamePanel panel = new GamePanel(this);
		Container contentPane = getContentPane();
		contentPane.add(panel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		pack();
	}
	
	/*public static void main(String[] args)
	{
		Thunder e1 = new Thunder();
	}*/
	
	public void emitString(String str)
	{
		System.out.println("SCORE@THUNDER@" + str);
		String Str = "SCORE@THUNDER@" + str;
		platform_interface.clientSocket.sendGameMsg(Str);
	}
}
