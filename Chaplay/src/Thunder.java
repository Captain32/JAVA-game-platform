

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

	//�ӵ���
	class Bullet
	{
		static final int BULLET_STEP_Y = 16;
		static final int BULLET_WIDTH = 32;
		public int m_posX = 0;
		public int m_posY = -20;
		
		//�Ƿ�Ҫ�����ӵ�
		boolean mFacus = true;
		private Image pic[] = null;
		
		//��ǰ֡��ID
		private int mPlayId = 0;
		public Bullet()
		{
			pic = new Image[4];
			for (int i = 0; i < 4; i++)
				pic[i] = Toolkit.getDefaultToolkit().getImage(".\\Thunder_material\\bullet_" 
																+ i + ".png");
		}
		
		//��ʼ������
		public void init(int x, int y)
		{
			m_posX = x;
			m_posY = y;
			mFacus = false;
		}
		
		//�����ӵ�
		public void DrawBullet(Graphics g, JPanel i)
		{
			if (mFacus)
			{
				g.drawImage(pic[mPlayId++], m_posX, m_posY, (ImageObserver) i);
				if (mPlayId == 4) mPlayId = 0;
			}
		}
		
		//�����ӵ��������
		public void UpdateBullet()
		{
			if (mFacus)
				m_posY -= BULLET_STEP_Y;
		}
	}

	//�л���
	class Enemy
	{
		//�л�״̬
		public static final int ENEMY_ALIVE_STATE = 0;
		public static final int ENEMY_DEATH_STATE = 1;
		
		//�л���Y�ٶ�
		static final int ENEMY_STEP_Y = 8;
		
		public int m_posX = 0;
		public int m_posY = 0;
		
		//�л���ͼƬ
		Image pic;
		
		//�л�״̬
		public int mAnimState = ENEMY_ALIVE_STATE;
		private Image enemyExplorePic[] = new Image[6];
		
		//��ǰ��֡
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
		
		//���Ƶл�
		public void DrawEnemy(Graphics g, JPanel i)
		{
			//����������������ϣ����ٻ��Ƶл�
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
		
		//��������
		public void UpdateEnemy()
		{
			m_posY += ENEMY_STEP_Y;
		}
	}

	//���沿��
	class GamePanel extends JPanel
	{
		//�ⲿThunderʵ���ġ�ָ�롱
		private Thunder thunder;
		
		//��Ļ�Ŀ�� 
		private final int mScreenWidth = 512;
		private final int mScreenHeight = 512;
		
		//��Ϸ����ͼƬ
		private Image mBitMenuBG0 = null;
		private Image mBitMenuBG1 = null;
		
		//��¼����ͼƬ��Y����
		private int mBitposY0 = 0;
		private int mBitposY1 = 0;
		
		//��ҷ���
		int score;
		
		//����
		private final int fontSize = 24;
		private Font myFont = new Font("����", Font.BOLD, fontSize);
		
		//�ӵ����������
		final static int BULLET_POOL_COUNT = 15;
		
		//�ɻ��ƶ��Ĳ���
		final static int PLAN_STEP = 16;
		
		//���˶��������
		final static int ENEMY_POOL_COUNT = 4;
		
		//���˷ɻ���ƫ����
		final static int ENEMY_POS_OFF = 64;
		
		//��Ϸ�Ķ�ʱ��
		private Timer timer = new Timer(100, new TimeListener()); //��ʱ��
		private int presentTime = 0;
		
		//�߳�ѭ����־
		private boolean mIsRunning = false;
		
		//�ɻ�����Ļ�е�����
		public int mAirposX = 0;
		public int mAirposY = -64;
		
		//�л���������
		Enemy mEnemy[] = null;
		//�ӵ�������
		Bullet mBullet[] = null;
		//�洢�����ӵ�
		ArrayList<Bullet> reserveBullet = new ArrayList();
		//���ڷ��е��ӵ�
		ArrayList<Bullet> flyingBullet = new ArrayList();
		//�ɻ���֡
		private boolean boom;
		private int bombNum;
		private final int bombCnt = 10;
		BufferedImage bomb[] = new BufferedImage[bombCnt];
		Image myPlanePic[];
		//�ɻ��ĵ�ǰ֡��
		public int myPlaneId = 0;
		
		public GamePanel(Thunder thunder_)
		{
			thunder = thunder_;
			
			setPreferredSize(new Dimension(mScreenWidth, mScreenHeight));
			//�趨�����ڱ�����
			setFocusable(true);
			addKeyListener(new MyKeyListener());
			init();
			mIsRunning = true;
			setVisible(true);
		}

		//init()���ڳ�ʼ�����ֶ���
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
				JOptionPane.showMessageDialog(null, "��ȡ����ͼƬ��������Ŀ¼���Ƿ����ͼƬ��", "��ȡ����", 
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			mBitposY0 = 0;
			mBitposY1 = -mScreenHeight;
			
			//��ҵķɻ�����
			mAirposX = mScreenWidth / 2 + 32;
			mAirposY = mScreenHeight - 64;
			boom = false;
			bombNum = -1;
			//��ʼ����ҷɻ���ص�6��ͼƬ����
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
			//�����л�����
			mEnemy = new Enemy[ENEMY_POOL_COUNT];
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			{
				mEnemy[i] = new Enemy();
				mEnemy[i].init(i * ENEMY_POS_OFF, i * ENEMY_POS_OFF - 256);
			}
			
			//�����ӵ������
			mBullet = new Bullet[BULLET_POOL_COUNT];
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				mBullet[i] = new Bullet();
			//���ӵ�ȫ���������֮��
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				reserveBullet.add(mBullet[i]);
			
			//���õ�ǰʱ��
			presentTime = 0;
			//��ʼ��ʱ��
			timer.start();
		}
		
		//���³�ʼ��
		private void reinit()
		{
			mBitposY0 = 0;
			mBitposY1 = -mScreenHeight;
			
			//��ҵķɻ�����
			mAirposX = mScreenWidth / 2 + 32;
			mAirposY = mScreenHeight - 64;
			
			boom = false;
			bombNum = -1;
			//���õ�ǰʱ��
			presentTime = 0;
			//��ʼ��ʱ��
			timer.start();			
		}
			
		//��ͼ
		public void paint(Graphics g)
		{
			//������
			g.drawImage(mBitMenuBG0, 0, mBitposY0, this);
			g.drawImage(mBitMenuBG1, 0, mBitposY1, this);
			
			//����ɫ
			g.drawImage(myPlanePic[myPlaneId], mAirposX, mAirposY, this);
			//����ը
			if (boom)
				g.drawImage(bomb[bombNum], mAirposX - 12, mAirposY - 12, this);
			
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
				mBullet[i].DrawBullet(g, this);
			
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
				mEnemy[i].DrawEnemy(g, this);
			
			//������
			g.setFont(myFont);
			g.setColor(Color.WHITE);
			g.drawString("����:" + score, 0, 32);
		}
		
		//���±���
		private void updateBg()
		{
			int movDis = 16;
			//��������
			int initPlace = -mScreenHeight;
			mBitposY0 += movDis;
			mBitposY1 += movDis;
			if (mBitposY0 == mScreenHeight)
				mBitposY0 = initPlace;
			if (mBitposY1 == mScreenHeight)
				mBitposY1 = initPlace;
			
			//�����ӵ���λ������
			for (int i = 0; i < BULLET_POOL_COUNT; i++)
			{
				mBullet[i].UpdateBullet();
				if (mBullet[i].mFacus && mBullet[i].m_posY <= 0)
				{
					mBullet[i].mFacus = false;
					reserveBullet.add(mBullet[i]); //���ڷɳ���ͼ�߽��bullet���������
				}
			}
			//ȷ���Ƿ����µ��ӵ�
			if (presentTime == 10)
			{
				presentTime = 0;
				emitBullet();
			}
			
			//���µл���λ��
			for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			{
				mEnemy[i].UpdateEnemy();
				
				if (mEnemy[i].mAnimState == Enemy.ENEMY_DEATH_STATE && mEnemy[i].mPlayId == 6
					|| mEnemy[i].m_posY >= mScreenHeight)
				{
					mEnemy[i].init(UtilRandom(0, ENEMY_POOL_COUNT) * ENEMY_POS_OFF, 0);
				}
			}
			
			//��ײ���
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
		
		//���������
		private int UtilRandom(int botton, int top)
		{
			return ((Math.abs(new Random().nextInt()) % (top - botton) + botton));
		}
		
		//�ж��Ƿ���ײ
		public void Collision()
		{
			//�ӵ��͵��˵���ײ
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
						score += 10;//����һ����10��
						tmp.mFacus = false;
						reserveBullet.add(tmp);
						flyingBullet.remove(tmp);
						return;
					}
				}
			}
			//�Ի��͵��˵���ײ
			for (int j = 0; j < ENEMY_POOL_COUNT; j++)
			{
				//���ײ���л�����Ϸ����
				if (crash(mEnemy[j].m_posX, mEnemy[j].m_posY, 32, 32, mAirposX, mAirposY, 32, 32))
				{
					boom = true;
					return;
				}
			}
		}
		
		//��ײԭ��
		private boolean crash(int objx1, int objy1, int objWidth1, int objHeight1, 
				int objx2, int objy2, int objWidth2, int objHeight2)
		{
			int left = objx1, right = objx1 + objWidth1;
			int up = objy1, down = objy1 + objHeight1; //˭���뵽up��ʵ��С��down��
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
		
		//�����ӵ�
		public void emitBullet()
		{
			if (!reserveBullet.isEmpty())
			{
				//��ȡlist��һ��Ԫ��
			//	System.out.println(reserveBullet.size());
				Bullet tmp = reserveBullet.get(reserveBullet.size() - 1);
				reserveBullet.remove(reserveBullet.size() - 1);
				tmp.mFacus = true;
				tmp.m_posX = mAirposX + 10;
				tmp.m_posY = mAirposY;
				flyingBullet.add(tmp);
			}
		}
		
		//���̼���
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
		
		//��ʱ����
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
		
		//��Ϸ����
		public void GameOver()
		{
			timer.stop();
			
			int answer = JOptionPane.showConfirmDialog(null, "��Ϸ�������Ƿ����", "��Ϸ����",
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
		setTitle("���������Ϸ");
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
