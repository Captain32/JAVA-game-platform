import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Random;

import javax.swing.*;
import javax.imageio.ImageIO;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class DDtank extends JFrame
{
	private static final long serialVersionUID = 1L;

	//面板部分
	private RoomPanel roomPanel = new RoomPanel(); 
	
	//进度条们
	private JProgressBar remainTime = new JProgressBar(); //续命条
	private JProgressBar remainStrength = new JProgressBar(); //体力条
	private JProgressBar powerbar = new JProgressBar();   //投掷的力度
	
	//人物相关常数
	private final int maxStrength = 15; //最多15点体力
	private final int maxHealth = 100; //最多100血
	private final int maxPower = 100;  //力度上限是100
	
	//事件处理及定时器
	private final int limitTime = 30; //每个玩家限定时间是假的30s
	private final int frequency = 100; //刷新频率
	private Timer timer = new Timer(10, new TimeListener()); //定时器，控制时间进度条
	private int times; //游戏已经进行的时间
	private int totalTimes; //游戏总进行的时间
	private int light;	//游戏目前是白天还是夜晚，1表示白天，0表示夜晚
	private MyKeyListener myKeyListener = new MyKeyListener();
	private boolean firstPressed = true;  //是否是第一次按下鼠标
	private boolean mousePressed = false; //是否按住鼠标
	private boolean angleChangeable = true;
	private boolean gameOver = false;
	
	//其他UI界面
	private double calAngle = 0; //用于计算的角度
	private int angle = 0; //目前瞄准的角度
	private final int fontSize = 16;
	private Font myFont = new Font("楷体", Font.BOLD, fontSize);
	private int power = 0; //投掷的力度
	private int powerVary = 1; //力度变化的方向
	
	//瞄准线的属性
	private int arrowxs = 0;
	private int arrowys = 0;
	private int arrowxe = 0;
	private int arrowye = 0;
	
	//抛物线的属性
	private double bulletAngle;    //子弹的角度
	private double bulletX, bulletY;   //子弹的坐标
	private double bulletVx, bulletVy; //子弹两个方向的速度
	private final double accelerate = 10;  //重力加速度
	private boolean bulletFlying = false;
	private final double bias = 0.01; //偏差
	private BufferedImage curBullet; //当前子弹的帧
	//连续进攻次数
	private int fireTimes = 1;
	
	//图片加载
	//1.背景部分
	private int mapIndex; //当前的地图
	private BufferedImage bgdDay; //白天背景
	private BufferedImage bgdNight; //夜晚背景
	private int bgdWidth, bgdHeight; //背景图片的宽度和高度
	//2.动画特效部分
	private final int explosionKind = 6;
	private final int explosionCnt = 25;
	private final int explosionWidth = 96, explosionHeight = 96; //爆炸特效的宽长
	private BufferedImage explosion[][] = new BufferedImage[explosionKind][explosionCnt]; //爆炸的特效
	
	private final int bulletKind = 6;
	private final int bulletCnt = 16;
	private final int bulletWidth = 32, bulletHeight = 32;      //子弹图片的宽长
	private BufferedImage bullet[][] = new BufferedImage[bulletKind][bulletCnt];    //子弹的图片
	
	private final int selCnt = 8;
	private int cursel = 0;    //目前箭头的帧数
	private final int selWidth = 32, selHeight = 32; //箭头图标的宽度和长度
	private BufferedImage sel[] = new BufferedImage[selCnt];    //角色头上的选中图标
	private final int healthWidth = 48, healthHeight = 6;
	private final int healthCnt = 2; //血条图片数
	private BufferedImage healthImage[] = new BufferedImage[healthCnt]; //血条图片
	private final int powerCnt = 2;
	private final int powerWidth = 900, powerHeight = 36;
	private BufferedImage powerImage[] = new BufferedImage[powerCnt];
	private BufferedImage wallImage;
	
	//3.地板等物体的部分
	private final int wallRow = 34, wallColumn = 64;   //墙壁矩阵
	private final int wallWidth = 16, wallHeight = 16;
	private BufferedImage ground;  //地板
	private int wallCnt; //墙壁的数量
	private Walls[] walls;

	//角色名
	private String roleName[] = { "reimu", "alice" };
	private int nameCnt;
	
	//定义角色
	private int turns = 0;   //轮到角色的编号
	private final int roleCnt = 2; //暂定只有两个玩家	
	private Role roles[] = new Role[roleCnt];
	
	//爆炸特效
	private int explosionNum = explosionCnt; //目前的帧数
	private double explosionX, explosionY; //爆炸的位置
	
	//毛玉
	private BufferedImage maoyuImage[];   //图片
	private final int maoyuCnt = 16; //总帧数
	private Maoyu maoyu;
	private final int maoyuWidth = 32, maoyuHeight = 48;
	
	//道具
	private final int skillCnt = 3; //道具个数
	private BufferedImage skillImage[];
	private final int skillWidth = 36, skillHeight = 36;
	
	//buff特效
	private final int buffCnt = 13;
	private BufferedImage buffImage[];
	private int buffCur; //当前的帧
	private final int buffWidth = 48, buffHeight = 48;
	private final int buffIntra = 5;
	private boolean buff = false;
	private BufferedImage chooseImage[];
	private int choice;
	
	//音效
	private SoundPlayer backgroundMusic;
	private SoundPlayer explosionMusic;
	private SoundPlayer winMusic;
	private SoundPlayer loseMusic;
	private SoundPlayer buffMusic;
	private SoundPlayer fireMusic;
	private int bgmCur;
	private final int bgmTime = 9000;
	private Thread bgmThread;
	
	//定义地图的边界(以角色为标准)
	private final int upBound = 0;
	private final int downBound = 544;
	private final int leftBound = 0;
	private final int rightBound = 1024; 
	
	//风力系统
	private final int maxWindForce = 5; //风力最大五级
	private int curWindForce = 0; //当前的风力
	private BufferedImage windImage[] = new BufferedImage[2];
	
	//AI模式
	private boolean aiMode;
	
	//联网模式
	private boolean netMode;
	private int myTurn;
	private boolean messageGot;
	public String strRead;
	private String playerName;
	
	//是否显示菜单
	private boolean showMenu;
	private int menuChoose;
	private final int menuChoiceCnt = 4;
	private int menuSelCur;
	private final int menuSelCnt = 4;
	private BufferedImage menuSelImage[] = new BufferedImage[menuSelCnt];
	private final int menuSelWidth = 32, menuSelHeight = 32;
	private final int menuLeft = 420;
	private final int menuUp = 200;
	private Font menuFont = new Font("华文琥珀", Font.BOLD, 32);
	
	//联机对战记录对手的名字
	public String enemyName; 
	
	public DDtank()
	{
		//进度条
		//续命条
		remainTime.setMaximum(limitTime);
		remainTime.setMinimum(0);
		remainTime.setStringPainted(true);
		remainTime.setString("血槽为空，请勿施救");
		remainTime.setBackground(Color.white);
		remainTime.setForeground(Color.LIGHT_GRAY);
		remainTime.setFont(myFont);
		//体力条
		remainStrength.setMaximum(maxStrength);
		remainStrength.setMinimum(0);
		remainStrength.setStringPainted(true);
		remainStrength.setString("剩余体力");
		remainStrength.setBackground(Color.LIGHT_GRAY);
		remainStrength.setForeground(Color.blue);
		remainStrength.setFont(myFont);
		remainStrength.setValue(5);
		remainStrength.setVisible(true); 

	//	power = new JProgressBar();   //投掷的力度
		
		//添加监听
		this.setFocusable(true);
		this.addKeyListener(myKeyListener);

		//设置地图
		mapIndex = 3;
		
		//加载图片
		nameCnt = roleName.length; //确定角色名的数量
		
		String path = ".\\DDtank_material\\";
		try
		{	
			//1.加载背景
			bgdDay = ImageIO.read(new File(path + "day.jpg"));
			bgdNight = ImageIO.read(new File(path + "night.jpg"));
			bgdWidth = bgdDay.getWidth();
			bgdHeight = bgdDay.getHeight();
			//2.加载动画图片
			for (int i = 0; i < explosionKind; i++)
				for (int j = 0; j < explosionCnt; j++)
					explosion[i][j] = ImageIO.read(new File(path + "explosion_" + i + "\\explosion" + j + ".png"));
			for (int i = 0; i < bulletKind; i++)
				for (int j = 0; j < bulletCnt; j++)
					bullet[i][j] = ImageIO.read(new File(path + "bullet_" + i + "\\bullet" + j + ".png"));
			for (int i = 0; i < selCnt; i++)
				sel[i] = ImageIO.read(new File(path + "sel" + i + ".png"));
			for (int i = 0; i < healthCnt; i++)
				healthImage[i] = ImageIO.read(new File(path + "health" + i + ".png"));
			for (int i = 0; i < powerCnt; i++)
				powerImage[i] = ImageIO.read(new File(path + "power" + i + ".png"));
			//3.加载障碍
			ground = ImageIO.read(new File(path + "ground.png"));
			
			wallImage = ImageIO.read(new File(path + "wall1.png"));
			
			//4.加载毛玉
			maoyuImage = new BufferedImage[maoyuCnt];
			for (int i = 0; i < maoyuCnt; i++)
				maoyuImage[i] = ImageIO.read(new File(path + "maoyu\\maoyu" + i + ".png"));
			
			//5.加载道具
			skillImage = new BufferedImage[skillCnt];
			for (int i = 0; i < skillCnt; i++)
			{
				skillImage[i] = ImageIO.read(new File(path + "skill\\skill" + i + ".png"));
			}
			//6.加载buff
			buffImage = new BufferedImage[buffCnt];
			chooseImage = new BufferedImage[buffCnt];
			for (int i = 0; i < buffCnt; i++)
			{
				buffImage[i] = ImageIO.read(new File(path + "buff\\buff" + i + ".png"));
				chooseImage[i] = ImageIO.read(new File(path + "buff\\sel" + i + ".png"));
			}
			//7.风力图片
			windImage[0] = ImageIO.read(new File(path + "wind\\left.png"));
			windImage[1] = ImageIO.read(new File(path + "wind\\right.png"));
			
			//8.菜单有关的图片
			for (int i = 0; i < menuSelCnt; i++)
				menuSelImage[i] = ImageIO.read(new File(path + "menuSel\\menuSel" + i + ".png"));
		}
		catch (IOException e)
		{
			//加载失败之后的操作
			JOptionPane.showMessageDialog(null, "读取图片错误！请检查目录下是否存在图片！", 
									"读取错误", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			Exit();
		}
		
		//加载音效
		backgroundMusic = new SoundPlayer(path + "music\\bgm.mp3");
		explosionMusic = new SoundPlayer(path + "music\\explosion.mp3");
		winMusic = new SoundPlayer(path + "music\\win.mp3");
		loseMusic = new SoundPlayer(path + "music\\lose.mp3");
		buffMusic = new SoundPlayer(path + "music\\buff.mp3");
		fireMusic = new SoundPlayer(path + "music\\fire.mp3");
		bgmCur = 0;
		
		//对roomPanel进行设置
		System.out.printf("bgdWidth = %d, bgdHeight = %d\n", bgdWidth, bgdHeight);
		roomPanel.setPreferredSize(new Dimension(bgdWidth, bgdHeight));
		roomPanel.add(remainStrength, BorderLayout.WEST);
		
		//定义玩家和ai
		maoyu = new Maoyu(22 * wallWidth, 18 * wallHeight - maoyuHeight, 18 * wallWidth, 26 * wallWidth, 2, 2);
		roles[0] = new Role(0, leftBound + 48, downBound - 46, true, 3, 3);
		roles[1] = new Role(1, rightBound - 2 * 48, downBound - 46, false, 2, 2);
		
		//对DDtank本身进行设置
		this.setLayout(new BorderLayout());
		this.add(roomPanel, BorderLayout.CENTER);
		this.add(remainTime,BorderLayout.NORTH);
		this.setTitle("一个弹弹堂游戏");
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		
		//设置时钟
		times = limitTime * frequency;
		totalTimes = 0;
		
		//设置菜单的显示
		showMenu = true;
		menuChoose = 0;
		menuSelCur = 0;
		timer.start();
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent arg0)
			{
		 		// TODO Auto-generated method stub
				timer.stop();
				dispose();
				bgmThread.stop();
			}
				
		});
	}
	
	//更换玩家
	public void changeTurns()
	{
		if (gameOver)
		{
			GameOver();
			return;
		}
		turns = 1 - turns;
		reinit();
		
		if (aiMode && turns == 1)
		{
			maoyu.fireable = true;
		}
	}
	
	//重新初始化
	public void reinit()
	{
		power = 0;
		fireTimes = 1;
		
		if (!aiMode)
		{
			roles[turns].strength = maxStrength; //补魔 补魔
			roles[turns].moveable = true;
			roles[turns].fireable = true;
			roles[turns].motionCur = 0;
			roles[turns].damage = roles[turns].fixedDamage;
			
			roles[1 - turns].moveable = true;
		}
		else
		{
			if (turns == 0)
			{
				roles[turns].damage = roles[turns].fixedDamage;
				roles[turns].strength = maxStrength; //补魔 补魔
				roles[turns].moveable = true;
				roles[turns].fireable = true;
				roles[turns].motionCur = 0;
			}
			else
			{
				roles[1 - turns].moveable = true;
			}
		}
		if (netMode)
		{
			if (turns != myTurn)
				messageGot = false;
			else
				messageGot = true;
		}
		
		//设定一些属性
		angleChangeable = true;
		firstPressed = true;
		bulletFlying = false;
		explosionNum = explosionCnt;
		gameOver = false;
		
		//设定风力
		curWindForce = Math.abs(new Random().nextInt()) % (maxWindForce * 2 + 1);
		curWindForce -= maxWindForce;
		if(netMode)
			curWindForce = 0;
		
		//最后重启定时器
		times = limitTime * frequency;
		timer.start();
	}
	
	//判定碰撞
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
	
	//抛物线计算
	private void parabola()
	{
		if (bulletFlying)
		{
			bulletX = bulletX + bulletVx / frequency;
			bulletY = bulletY + bulletVy / frequency;
			bulletVy += accelerate;
			//考虑风力
			bulletVx += curWindForce;
			
			bulletAngle = Math.atan(bulletVy / bulletVx);
			
			BufferedImage[] cbullet;
			if (aiMode)
			{
				if (turns == 0)
					cbullet = bullet[roles[turns].bulletIndex];
				else
					cbullet = bullet[maoyu.bulletIndex];
			}
			else
			{
				cbullet = bullet[roles[turns].bulletIndex];
			}
			
			if (bulletVx > bias)
			{
				if (bulletAngle < -bias)
				{
					if (bulletAngle < -0.66)
						curBullet = cbullet[1];
					else if (bulletAngle < -0.33)
						curBullet = cbullet[2];
					else 
						curBullet = cbullet[3];
				}
				else if (bulletAngle > bias)
				{
					if (bulletAngle <= 0.33)
						curBullet = cbullet[5];
					else if (bulletAngle <= 0.66)
						curBullet = cbullet[6];
					else
						curBullet = cbullet[7];
				}
				else
					curBullet = cbullet[4];		
			}
			else if (bulletVx < -bias)
			{
				if (bulletAngle < -bias)
				{
					if (bulletAngle < -0.66)
						curBullet = cbullet[9];
					else if (bulletAngle < -0.33)
						curBullet = cbullet[10];
					else
						curBullet = cbullet[11];
				}
				else if (bulletAngle > bias)
				{
					if (bulletAngle <= 0.33)
						curBullet = cbullet[13];
					else if (bulletAngle <= 0.66)
						curBullet = cbullet[14];
					else
						curBullet = cbullet[15];
				}
				else
					curBullet = cbullet[12];
			}
			else
			{
				if (bulletVy > 0)
					curBullet = cbullet[8];
				else
					curBullet = cbullet[0];	
			}
			
		/*	System.out.println("X = " + bulletX);
			System.out.println("Y = " + bulletY);
			System.out.println("Vx = " + bulletVx);
			System.out.println("Vy = " + bulletVy); */
			
			//如果撞到地图边界
			boolean crashing = bulletX <= leftBound || bulletX >= rightBound - bulletWidth 
					|| bulletY <= upBound || bulletY >= downBound;
					
			crashing = crashing || crash((int)bulletX, (int)bulletY, bulletWidth, bulletHeight, 
					leftBound, downBound + bulletHeight, rightBound - leftBound, wallHeight);
			
			//是否撞人
			if (!aiMode && (turns == 0) && crash(roles[1].x, roles[1].y, roles[1].width, roles[1].height, 
					(int)bulletX, (int)bulletY, bulletWidth, bulletHeight))
			{
				crashing = true;
				roles[1].health -= roles[0].damage;
				if (roles[1].health <= 0)
				{
					roles[1].health = 0;
					gameOver = true;
				}
			}
			if ((turns == 1) && crash(roles[0].x, roles[0].y, roles[0].width, roles[0].height, 
					(int)bulletX, (int)bulletY, bulletWidth, bulletHeight))
			{
				crashing = true;
				if (aiMode)
					roles[0].health -= maoyu.damage;
				else
					roles[0].health -= roles[1].damage;
				if (roles[0].health <= 0)
				{
					roles[0].health = 0;
					gameOver = true;
				}
			}
			
			//是否撞到毛玉
			if (aiMode && (turns == 0) && 
				crash(maoyu.x, maoyu.y, maoyuWidth, maoyuHeight, (int)bulletX, (int)bulletY, bulletWidth, bulletHeight))
			{
				crashing = true;
				maoyu.health -= roles[turns].damage;
				if (maoyu.health <= 0)
				{
					maoyu.health = 0;
					gameOver = true;
				}
			}
			//是否撞墙
			for (int i = 0; i < wallCnt; i++)
			{
				if (walls[i].visible == false)
					continue;
				if (crash((int)bulletX, (int)bulletY, bulletWidth, bulletHeight, 
						walls[i].x, walls[i].y, wallWidth, wallHeight))
				{
					if (walls[i].stable == false)
						walls[i].visible = false;
					crashing = true;
				}
			}
			
			if (crashing)
			{
				explosionMusic.Play();
				bulletFlying = false;
				explosionNum = 0;
				explosionX = bulletX + (bulletWidth - explosionWidth) / 2;
				explosionY = bulletY + (bulletHeight - explosionHeight) / 2;
				
		//		System.out.println("crash");
			}
		}
	}
	
	//加载地图
	private void loadMap()
	{
		wallCnt = 0;
		for (int i = 0; i < wallRow; i++)
			for (int j = 0; j < wallColumn; j++)
				if (MapFactory.map[mapIndex][i / 2][j / 2] != 0)
					wallCnt++;
		walls = new Walls[wallCnt];
		
		int cnt = 0;
		for (int i = 0; i < wallRow; i++)
			for (int j = 0; j < wallColumn; j++)
			{
				if (MapFactory.map[mapIndex][i / 2][j / 2] == 1)
				{
					walls[cnt++] = new Walls(j * wallWidth, i * wallHeight, wallImage, false);
				}
				else if (MapFactory.map[mapIndex][i / 2][j / 2] == 2)
				{
					walls[cnt++] = new Walls(j * wallWidth, i * wallHeight, wallImage, true);
				}
			}
	}
	
	//开始网络网络模式
	synchronized public void onlinePK() {
		aiMode = false;
		netMode = true;
		mapIndex = 2;
		playerName = new String(strRead);
		myTurn = Integer.valueOf(strRead);
		strRead = null;
		if (myTurn == -1)
		{
			JOptionPane.showMessageDialog(null, "连接失败");
			return;
		}
		playerName = enemyName;
		messageGot = false;
		loadMap();
		showMenu = false;
		reinit();
	}
	
	class waiting extends JFrame {
		private static final long serialVersionUID = 1L;

		waiting(){
			this.setTitle("Waiting...");
			this.setPreferredSize(new Dimension(100, 80));
			this.setLocation(400, 600);
			JLabel label = new JLabel();
			label.setText("Waiting for your friends to respond...");
			label.setFont(new Font("宋体", 15, Font.BOLD));
			this.add(label);
			label.setVisible(true);
			this.setVisible(true);
		}
	}
	
	//键盘监听
	class MyKeyListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			if (showMenu)
			{
				if (key == KeyEvent.VK_ENTER)
				{
					if (menuChoose == menuChoiceCnt - 1)
					{
						timer.stop();
						dispose();
						return;
					}
					else if (menuChoose == 0) //双人对战游戏
					{
						aiMode = false;
						netMode = false;
						mapIndex = 2;
						loadMap();
					}
					else if (menuChoose == 1) //AI模式
					{
						aiMode = true;
						netMode = false;
						mapIndex = 3;
						loadMap();
					}
					else if (menuChoose == 2) //联网模式
					{
						playerName = JOptionPane.showInputDialog("请输入需要联网的玩家");
						emitString("CONNECT", "");
						enemyName = playerName;
						aiMode = false;
						netMode = true;
						mapIndex = 2;
						//等待好友接受申请
						waiting wa = new waiting();
						wa.setPreferredSize(new Dimension(1000, 600));
						while (strRead == null) mySleep(10);
						wa.setVisible(false);
						playerName = new String(strRead);
						playerName = enemyName;
						myTurn = Integer.valueOf(strRead);
						strRead = null;
						if (myTurn == -1)
						{
							JOptionPane.showMessageDialog(null, "连接失败");
							return;
						}
						messageGot = false;
						loadMap();
					}
					showMenu = false;
					reinit();
				}
				else if (key == KeyEvent.VK_UP)
				{
					menuChoose--;
					if (menuChoose < 0)
						menuChoose = 0;
				}
				else if (key == KeyEvent.VK_DOWN)
				{
					menuChoose++;
					if (menuChoose >= menuChoiceCnt)
					{
						menuChoose = menuChoiceCnt - 1;
					}
				}
				
				return;
			}
			
			if (key == KeyEvent.VK_LEFT)
			{
			//	System.out.println("Move Left");
				if (netMode)
				{
					if (turns == myTurn)
						roles[turns].moveLeft();
				}
				else if (!aiMode || turns == 0)
					roles[turns].moveLeft();
				
			}
			if (key == KeyEvent.VK_RIGHT)
			{
			//	System.out.println("Move Right");
				if (netMode)
				{
					if (turns == myTurn)
						roles[turns].moveRight();
				}
				else if (!aiMode || turns == 0)
					roles[turns].moveRight();
			}
			//暂停
			if (key == KeyEvent.VK_P)
			{
				if (timer.isRunning())
				{
					timer.stop();
					repaint();
				}
				else
					timer.start();
			}
			//使用道具部分
			if (netMode)
			{
				if (turns == myTurn)
				{
					if (firstPressed == false)
						return;
					if (key == KeyEvent.VK_0)
					{
						roles[turns].buff_0();
					}
					if (key == KeyEvent.VK_1)
					{
						roles[turns].buff_1();
					}
					if (key == KeyEvent.VK_2)
					{
						roles[turns].buff_2();
					}
				}
			}
			else if (!aiMode || turns == 0)
			{
				if (firstPressed == false)
					return;
				if (key == KeyEvent.VK_0)
				{
					roles[turns].buff_0();
				}
				if (key == KeyEvent.VK_1)
				{
					roles[turns].buff_1();
				}
				if (key == KeyEvent.VK_2)
				{
					roles[turns].buff_2();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//鼠标监听
	class MyMouseListener implements MouseListener
	{
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			if (showMenu || (aiMode && turns == 1) || (netMode && turns != myTurn))
				return;
			
			if (firstPressed)
				roles[turns].fire();
			firstPressed = false;
			mousePressed = false;
			angleChangeable = true;
		}
		
		@Override
		public void mousePressed(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			if (showMenu || (aiMode && turns == 1) || (netMode && turns != myTurn))
				return;
			
			mousePressed = true;
			angleChangeable = false;
			roles[turns].moveable = false;
			//手动暂停
			if (bulletFlying)
			{
				if (timer.isRunning())
					timer.stop();
				else
					timer.start();
			}
		}
		
		@Override
		public void mouseExited(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	//鼠标移动监听
	class MyMouseMotionListener implements MouseMotionListener
	{
		@Override
		public void mouseMoved(MouseEvent e)
		{
			// TODO Auto-generated method stub
			int x = e.getX(), y = e.getY();
			double len;
			
			if (roles[turns].face == 1)
			{
				arrowxe = (x >= arrowxs)? x : arrowxs;
				arrowye = (y <= arrowys)? y : arrowys;
				if (arrowxe == arrowxs && arrowye == arrowys)
					arrowxe = arrowxs + 1;
			}
			else
			{
				arrowxe = (x <= arrowxs)? x : arrowxs;
				arrowye = (y <= arrowys)? y : arrowys;
				if (arrowxe == arrowxs && arrowye == arrowys)
					arrowxe = arrowxs - 1;
			}
			
			
			len = Math.sqrt((arrowys - arrowye) * (arrowys - arrowye) + (arrowxs - arrowxe) * (arrowxs - arrowxe));
			if (angleChangeable)
			{
				calAngle = (Math.acos((double) (arrowxe - arrowxs) / len));
				angle = (int)(calAngle / 3.14 * 180);
				/*if (netMode && turns == myTurn)
				{
					emitString("DDTANK", "A" + calAngle);
				}*/
			}
		//	repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	//定时器监听
	class TimeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			totalTimes++;
			if (showMenu)
			{
				if (totalTimes >= 10)
				{
					repaint();
					totalTimes = 0;
				}
				return;
			}
			
			times--;
			//总时间每两分钟归零一次
			if (totalTimes > 12 * frequency)
			{
				totalTimes = 0;
				light = 1 - light; //在白天和晚上间切换
			}
			if (times < 0)
			{
				//定时器结束
				if (aiMode)
				{
					roles[0].moveable = false;
					roles[0].fireable = false;

				}
				else if (netMode && turns == myTurn)
				{
					roles[turns].moveable = false;
					roles[turns].fireable = false;
				}
				else
				{
					roles[turns].moveable = false;
					roles[turns].fireable = false;
				}
				
				if (fireTimes <= 1 && bulletFlying == false && explosionNum == explosionCnt)
					changeTurns();
			/*	//弹出选择窗口
				int answer = JOptionPane.showConfirmDialog(null, "游戏结束，是否继续", "游戏结束",
						JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION);
					//初始化
				else
					System.exit(0);  */
			}
			else
			{
				remainTime.setString("你的生命剩下(naiive)" + times / frequency + "s蛤蛤蛤蛤蛤");
				remainTime.setValue(times / frequency);
			}
			
			//处理力度条
			if (mousePressed && firstPressed)
			{
				power += powerVary;
				/*if (netMode && turns == myTurn)
					emitString("DDTANK", "P" + power);*/
				
				if (power >= 100)
					powerVary = -1;
				if (power <= 1)
					powerVary = 1;
			}
			
			//瞄准线
			if ((aiMode && turns == 0) || (netMode && turns == myTurn) || (!aiMode && !netMode))
			{
				arrowxs = roles[turns].x + roles[turns].width / 2;
				arrowys = roles[turns].y + roles[turns].height / 2;
			}

			//角色头上的箭头
			cursel++; 
			if (cursel >= 160)
				cursel = 0;
			//buff
			if (buff)
			{
				buffCur++;
				if (buffCur >= buffCnt * buffIntra)
					buff = false;
			}
			
			//计算抛物线的属性
			parabola();
			
			//动态人物
			for (int i = 0; i < 2; i++)
				if (roles[i].moveable)
					roles[i].motion();
				else
					roles[i].curImage = roles[i].personImage[roles[i].cur];
			
			//决定是否切换玩家
			if (firstPressed == false && bulletFlying == false && explosionNum == explosionCnt)
			{
				if (fireTimes <= 1)
					changeTurns();
				else
				{
					if (aiMode)
					{
						if (turns == 1)
							maoyu.fire();
						else {
							roles[0].fireable = true;
							roles[0].fire();
						}
						fireTimes--;
					}
					else
					{
						roles[turns].fireable = true;
						roles[turns].fire();
						fireTimes--;
					}			
				}
			}
			
			//处理bgm
			//System.out.println(bgmCur + "");
			if (bgmCur == 0)
			{
				bgmThread = backgroundMusic.Play();
				bgmCur++;
			}
			else
			{
				bgmCur++;
				if (bgmCur >= bgmTime)
					bgmCur = 0;
			}

			//毛玉
			if (aiMode)
				maoyu.motion();
			
			/*if (netMode && (turns != myTurn))
			{
				parseMessage();
				/*
				if (messageGot == false)
				{
					messageGot = true;
					new readThread().start();
				}*/
			//}*/
			
			repaint();
		}
	}
	
	//游戏面板
	class RoomPanel extends JPanel
	{
		static final double arrowLen = 48;
		 
		public RoomPanel()
		{
			super();
			this.setBackground(Color.white);
			this.setLayout(new BorderLayout());
			
			//设置鼠标监听
			this.addMouseListener(new MyMouseListener());
			this.addMouseMotionListener(new MyMouseMotionListener());
		}
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			//根据light确定选取哪个作为背景
			if (light == 1)
			{
				g2d.setColor(Color.CYAN);
				g2d.drawImage(bgdDay, 0, 0, bgdWidth, bgdHeight, null);
			}
			else
			{
				g2d.setColor(Color.CYAN);
				g2d.drawImage(bgdNight, 0, 0, bgdWidth, bgdHeight, null);
			}
			
			if (showMenu) //如果显示菜单
			{
				drawMenu(g2d);
				return;
			}
			
			//画角色
			g2d.drawImage(roles[0].curImage, roles[0].x, roles[0].y, roles[0].width, roles[0].height, null);
			if (!aiMode)
			{
				g2d.drawImage(roles[1].curImage, roles[1].x, roles[1].y, roles[1].width, roles[1].height, null);
				g2d.drawImage(roles[turns].figure, 0, 0, roles[turns].figureWidth, roles[turns].figureHeight, null);
			}
			else
				g2d.drawImage(roles[0].figure, 0, 0, roles[0].figureWidth, roles[0].figureHeight, null);
			
			//画毛玉
			if (aiMode)
				g2d.drawImage(maoyu.curImage, maoyu.x, maoyu.y, maoyuWidth, maoyuHeight, null);
			
			//铺地板
			for (int x = leftBound; x <= rightBound + roles[turns].width - wallWidth; x += wallWidth)
					g2d.drawImage(ground, x, downBound, wallWidth, wallHeight, null);
			
			//画障碍
			for (int i = 0; i < wallCnt; i++)
			{
				if (walls[i].visible)
					g2d.drawImage(walls[i].image, walls[i].x, walls[i].y, wallWidth, wallHeight, null);
			}
			//画角色头上的东西
			g2d.setFont(myFont);
			//画生命
			g2d.drawImage(healthImage[0], roles[0].x, roles[0].y - 2 * healthHeight, healthWidth, healthHeight, null);
			g2d.drawImage(healthImage[1], roles[0].x, roles[0].y - 2 * healthHeight, 
					healthWidth * roles[0].health / maxHealth, healthHeight, null);
			
			if (!aiMode)
			{
				g2d.drawImage(healthImage[0], roles[1].x, roles[1].y - 2 * healthHeight, healthWidth, healthHeight, null);
				g2d.drawImage(healthImage[1], roles[1].x, roles[1].y - 2 * healthHeight, 
						healthWidth * roles[1].health / maxHealth, healthHeight, null);
			}
			else
			{
				g2d.drawImage(healthImage[0], maoyu.x, maoyu.y - healthHeight, maoyuWidth, healthHeight, null);
				g2d.drawImage(healthImage[1], maoyu.x, maoyu.y - healthHeight, 
						maoyuWidth * maoyu.health / maxHealth, healthHeight, null);			
			}
			
			if (!aiMode || turns == 0)
			{
				//画体力
			//	g2d.drawString("s=" + roles[turns].strength, roles[turns].x, 
			//			roles[turns].y - selHeight - 2 * fontSize);
				//画角度
				g2d.drawString(angle + "°", roles[turns].x + roles[turns].width / 2 + roles[turns].face * roles[turns].width / 2,
							roles[turns].y);
				//画箭头
				g2d.drawImage(sel[cursel * 5 / frequency], roles[turns].x + (roles[turns].width - selWidth) / 2, 
							roles[turns].y - selHeight - 2 * healthHeight, selWidth, selHeight, null);
				//画瞄准线			
				drawAL(arrowxs, arrowys, arrowxe, arrowye, g2d);
				
				//画技能(左边)
				if (turns == 0)
				{
					for (int i = 0; i < skillCnt; ++i)
					{
						g2d.drawString(i + "", 0, i * skillHeight + skillHeight / 3 + bgdHeight / 3);
						g2d.drawImage(skillImage[i], 0, i * skillHeight + bgdHeight / 3, 
								skillWidth, skillHeight, null);
					}
					//画buff
					if (buff)
					{
						g2d.drawImage(buffImage[buffCur / buffIntra], roles[0].x, roles[0].y, buffWidth, buffHeight, null);
						g2d.drawImage(chooseImage[buffCur / buffIntra], 
								0, choice * skillHeight + bgdHeight / 3, skillWidth, skillHeight, null);
					}
				}
				//画右边的技能
				else if (turns == 1 && !aiMode)
				{
					for (int i = 0; i < skillCnt; ++i)
					{
						g2d.drawString(i + "", rightBound - skillWidth, 
								i * skillHeight + skillHeight / 3 + bgdHeight / 3);
						g2d.drawImage(skillImage[i], rightBound - skillWidth, i * skillHeight + bgdHeight / 3, 
								skillWidth, skillHeight, null);
					}
					if (buff)
						g2d.drawImage(buffImage[buffCur / buffIntra], roles[1].x, roles[1].y, buffWidth, buffHeight, null);
				}
			}

			//画力度条
			g2d.drawImage(powerImage[1], 4 * wallWidth, downBound + wallHeight, (int) ((power / 100.0) * powerWidth), powerHeight, null);
			g2d.drawImage(powerImage[0], 4 * wallWidth, downBound + wallHeight, powerWidth, powerHeight, null);
			
			//画风力
			if (curWindForce >= 0)
			{
				g2d.drawImage(windImage[1], (bgdWidth - bulletWidth) / 2, 1 * wallHeight, bulletWidth, bulletHeight, null);
				g2d.drawString("东风" + curWindForce + "级", (bgdWidth) / 2 - 2 * wallWidth, 4 * wallHeight);
			}
			else
			{
				g2d.drawImage(windImage[0], (bgdWidth - bulletWidth) / 2, 1 * wallHeight, bulletWidth, bulletHeight, null);
				g2d.drawString("西风" + (-curWindForce) + "级", (bgdWidth) / 2 - 2 * wallWidth, 4 * wallHeight);
			}
			
			//让子弹飞
			if (bulletFlying)
			{
				g2d.drawImage(curBullet, (int)bulletX, (int)bulletY, bulletWidth, bulletHeight, null);
			//	g2d.fillOval((int)bulletX, (int)bulletY, 10, 10);
			}
			
			//爆炸特效
			if (explosionNum < explosionCnt && explosionNum >= 0)
			{
				if (aiMode)
				{
					if (turns == 0)
						g2d.drawImage(explosion[roles[turns].explosionIndex][explosionNum++], 
								(int)explosionX, (int)explosionY, explosionWidth, explosionHeight, null);
					else //轮到AI
						g2d.drawImage(explosion[maoyu.explosionIndex][explosionNum++], 
								(int)explosionX, (int)explosionY, explosionWidth, explosionHeight, null);
				}
				else
				{
					g2d.drawImage(explosion[roles[turns].explosionIndex][explosionNum++], 
							(int)explosionX, (int)explosionY, explosionWidth, explosionHeight, null);
				}
			}
			
			//暂停
			if (timer.isRunning() == false)
			{
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.setFont(new Font("楷体", Font.BOLD, 48));
				g2d.drawString("你暂停了游戏,按“P”继续", (rightBound + leftBound) / 4, (upBound + downBound) / 2);
			}
		}
		
		@SuppressWarnings("deprecation")
		public void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)
		{
			//限定箭头的长度
			double tmpLen = Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
			ex = (int)(sx + (arrowLen / tmpLen) * (ex - sx));
			ey = (int)(sy + (arrowLen / tmpLen) * (ey - sy));
			
			double H = 8; // 箭头高度  
			double L = 4; // 底边的一半  
			int x3 = 0;  
			int y3 = 0;  
			int x4 = 0;  
			int y4 = 0;  
			double awrad = Math.atan(L / H); // 箭头角度  
			double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度  
			double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);  
			double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);  
			double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点  
			double y_3 = ey - arrXY_1[1];  
			double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点  
			double y_4 = ey - arrXY_2[1];  
			
			Double X3 = new Double(x_3);  
			x3 = X3.intValue();  
			Double Y3 = new Double(y_3);  
			y3 = Y3.intValue();  
			Double X4 = new Double(x_4);  
			x4 = X4.intValue();  
			Double Y4 = new Double(y_4);  
			y4 = Y4.intValue();  
			// 画线  
			g2.drawLine(sx, sy, ex, ey);  
			//  
			GeneralPath triangle = new GeneralPath();  
			triangle.moveTo(ex, ey);  
			triangle.lineTo(x3, y3);  
			triangle.lineTo(x4, y4);  
			triangle.closePath();  
			//实心箭头  
			g2.fill(triangle);  
		}	
		private double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
		{
			double mathstr[] = new double[2];  
	        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度  
	        double vx = px * Math.cos(ang) - py * Math.sin(ang);  
	        double vy = px * Math.sin(ang) + py * Math.cos(ang);  
	        if (isChLen) {  
	            double d = Math.sqrt(vx * vx + vy * vy);  
	            vx = vx / d * newLen;  
	            vy = vy / d * newLen;  
	            mathstr[0] = vx;  
	            mathstr[1] = vy;  
	        }  
	        return mathstr;  
		}
	}
	
	private void mySleep(int mill)
	{
		try
		{
			Thread.sleep(mill);
		}
		catch (InterruptedException e) 
		{ 
		
		}
	}
	
	//游戏结束...
	public void GameOver()
	{
		if (aiMode)
		{
			if (maoyu.health <= 0)
				winMusic.Play();
			else
				loseMusic.Play();
		}
		else
		{
			winMusic.Play();
		}
		
		if (netMode)
		{
			playerName = "DDTANK";
			if (roles[myTurn].health <= 0)
				emitString("SCORE", "0");
			else
				emitString("SCORE", "1");
		}
		
		int answer = JOptionPane.showConfirmDialog(null, "游戏结束，是否继续", "游戏结束",
				JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION)
		{
			reinit();
			//恢复角色生命
			maoyu.health = maxHealth;
			roles[0].health = maxHealth;
			roles[1].health = maxHealth;
			roles[0].x = leftBound + 48;
			roles[1].x = rightBound - 48;
			//恢复地图
			for (int i = 0; i < wallCnt; i++)
				walls[i].visible = true;
		}
		else
		{
			timer.stop();
			dispose();	
			bgmThread.stop();
		}
	}
	public void Exit()
	{
		timer.stop();
		bgmThread.stop();
		dispose();
	}
	//AI
	private void testAI()
	{
		mySleep(1000);
		angleChangeable = false;
		roles[1].moveable = false;
		firstPressed = false;
		if (roles[0].x < roles[1].x)
		{
			roles[1].moveLeft();
			calAngle = 3 * 3.14 / 4;
			power = calPower(calAngle, roles[0].x - roles[1].x, 0);
		}
		else if (roles[0].x > roles[1].x)
		{
			roles[1].moveRight();
			calAngle = 3.14 / 4;
			power = calPower(calAngle, roles[0].x - roles[1].x, 0);
		}
		roles[1].fire();
	//	changeTurns();
	}
	
	//计算力度
	private int calPower(double angle, int dx, int dy)
	{
		int power;
		double u = Math.abs(Math.sin(angle));
		double v = Math.abs(Math.cos(angle));
		double tmp;
		
		if (dy > 0)
		{
			for (power = 1; power < 100; power++)
			{
				tmp = (power * u + Math.sqrt(power * power * u * u + 20 * dy)) * power * v;
				if (Math.abs(tmp - Math.abs(10 * dx)) <= roles[0].figureWidth / 2)
					return power;
			}
		}
		else if (dy < 0)
		{
			for (power = 1; power < 100; power++)
			{
				tmp = power * power * u * u + 20 * dy;
				if (tmp < 0)
					return 100;
				else
				{
					tmp = Math.sqrt(tmp);
					tmp += power * u;
					tmp *= (power * v);
					if (Math.abs(tmp - Math.abs(10 * dx)) <= roles[0].figureWidth / 2)
						return power;
				}
			}
		}
		
		power = (int)Math.sqrt(Math.abs((5 * dx) / (u * v))); 
		if (power > 100)
			power = 100;
		return power;
	}
	
	//菜单栏
	private void drawMenu(Graphics2D g2d)
	{
		g2d.setFont(menuFont);
		g2d.setColor(Color.orange);
		g2d.drawString("双人对战", menuLeft, menuUp);
		g2d.drawString("对战AI", menuLeft, menuUp + menuSelHeight);
		g2d.drawString("联机对战", menuLeft, menuUp + 2 * menuSelHeight);
		g2d.drawString("退出游戏", menuLeft, menuUp + 3 * menuSelHeight);
		g2d.drawImage(menuSelImage[menuSelCur], menuLeft - menuSelWidth, 
					menuUp + menuChoose * menuSelHeight - menuSelHeight / 2, null);
		menuSelCur++;
		if (menuSelCur >= menuSelCnt)
			menuSelCur = 0;
	}
	
	/*public static void main(String[] args)
	{
		new DDtank();
	}*/
	
	//发送和接受信号
	public void emitString(String mode, String str)
	{
		System.out.println(mode + "@" + playerName + "@" + str);
		String Str = mode + "@" + playerName + "@" + str;
		platform_interface.clientSocket.sendGameMsg(Str);
	}
	/*
	public void receiveString()
	{
		Scanner scan = new Scanner(System.in);
	    strRead = scan.next();
	} 
	//接受信号的线程
	private class readThread extends Thread
	{
		public void run()
		{
			receiveString();
			parseMessage();
		}
	}
	*/
	
	public Object lock = new Object();//同步更新str的信号
	
	//解析信号
	public void parseMessage()
	{
		if (strRead == null)
			return;
		if (strRead.substring(0, 1).equals("B"))
		{
			int buffNum = Integer.valueOf(strRead.substring(1));
			if (buffNum == 0)
				roles[turns].buff_0();
			else if (buffNum == 1)
				roles[turns].buff_1();
			else if (buffNum == 2)
				roles[turns].buff_2();
			messageGot = false;

		}
		else if (strRead.substring(0, 1).equals("M"))
		{
			if (strRead.substring(1).equals("LEFT"))
			{
				roles[turns].moveLeft();
			}
			else
			{
				roles[turns].moveRight();
			}
			messageGot = false;

		}
		else if (strRead.substring(0, 1).equals("A"))
		{
			calAngle = Double.valueOf(strRead.substring(1));
			System.out.println("angel: " + calAngle);
			messageGot = false;

		}
		else if (strRead.substring(0, 1).equals("P"))
		{
			power = Integer.valueOf(strRead.substring(1));
			System.out.println("power: " + power);
			messageGot = false;

		}
		else if (strRead.equals("FIRE"))
		{
			//System.out.println("fireable:" + roles[turns].fireable);
			/*System.out.println("firstPressed: " + firstPressed);
			System.out.println("bulletFlying: " + bulletFlying);
			System.out.println("explosionNum == explosionCnt: " + (explosionNum == explosionCnt));*/
			/*System.out.println("turns: " + turns);
			System.out.println("myTurn: " + myTurn);*/
			roles[turns].fire();
			firstPressed = false;

		}
	}
	
	
	//Role定义一个角色的属性
	private class Role
	{
		int id;	      //人物的ID，对应角色
		int strength; //每个角色限定步数
		int health;	  //角色的生命值
		int damage;	  //角色的伤害值
		int fixedDamage;
		int x, y; 	//角色的坐标
		int face;   //角色的朝向，朝左为-1,朝右为1
		int cur = 0;	//目前的帧ID
		boolean moveable; //可否移动
		boolean fireable; //可否开火

		BufferedImage curImage; //当前的帧
		BufferedImage personImage[]; //人物的动作图片
		BufferedImage figure;    //大图

		final int personCnt = 8; //图片数
		final int width = 48, height = 48; //图片的尺寸
		final int figureWidth = 128, figureHeight = 128;
		final int step = 6;   //人物步长
		final int inter = 200; //间隔时间
		//人物动态
		final int motionCnt = 5; //扰动图片数
		final int motionIntra = 20;  //切换间隔
		final int motionInter = 200; //扰动间隔
		BufferedImage motionImage[]; //扰动帧
		int motionCur; //当前扰动帧数
		
		//explosion和bullet的种类
		int explosionIndex;
		int bulletIndex;
		
		public Role(int _id, int _x, int _y, boolean faceRight, int eI, int bI) //人物的初始化部分
		{
			id = _id;
			strength = maxStrength; //每个角色最多走N步
			moveable = true;
			fireable = true;
			health = maxHealth;
			fixedDamage = 20;
			damage = 20;
			x = _x; 
			y = _y;
			motionCur = 0;
			//加载角色图片
			try
			{
				personImage = new BufferedImage[personCnt];
				String path = ".\\DDtank_material\\";
				int tmp = personCnt / 2;
				for (int i = 0; i < tmp; i++)
					personImage[i] = ImageIO.read(new File(path + roleName[id] + "\\left" + i + ".png"));
				for (int j = 0; j < tmp; j++)
					personImage[tmp + j] = ImageIO.read(new File(path + roleName[id] + "\\right" + j + ".png"));
				//加载大图
				figure = ImageIO.read(new File(path + roleName[id] + "\\figure.png"));
				//加载动态图
				motionImage = new BufferedImage[motionCnt];
				for (int i = 0; i < motionCnt; i++)
					motionImage[i] = ImageIO.read(new File(path + roleName[id] + "\\motion" + i + ".png"));
			}
			catch (IOException e)
			{
				//加载失败之后的操作
				JOptionPane.showMessageDialog(null, "读取背景图片错误！请检查目录下是否存在图片！", 
										"读取错误", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				Exit();;
			}
			
			if (faceRight)
			{
				cur = personCnt / 2;
				curImage = personImage[personCnt / 2];
				face = 1;
			}
			else
			{
				cur = 0;
				curImage = personImage[0];
				face = -1;
			}
			explosionIndex = eI;
			bulletIndex = bI;
		}
		public Role()
		{
			this(0, 0, downBound, true, 0, 0);
		}
		
		//角色移动
		public void moveLeft() //向左移动一步
		{	
			if (moveable)
			{
				strength--;
				if (strength < 0)
				{
					moveable = false;
					strength = 0;
					return;
				}
				
				if (netMode && turns == myTurn)
				{
					emitString("DDTANK", "MLEFT");
				}
				
				int tmp = personCnt / 2;
			
				face = -1;
				cur++;
				if (cur >= tmp)
					cur = 0;
				x -= step;
				if (x < leftBound)
					x = leftBound;
				curImage = personImage[cur];
			}
		}
		
		public void moveRight()
		{	
			if (moveable)
			{
				strength--;
				if (strength < 0)
				{
					moveable = false;
					strength = 0;
					return;
				}
				
				if (netMode && turns == myTurn)
				{
					emitString("DDTANK", "MRIGHT");
				}
				
				int tmp = personCnt / 2;
			
				face = 1;
				cur++;
				if (cur < tmp || cur >= personCnt)
					cur = tmp;
				x += step;
				if (x > rightBound - width)
					x = rightBound - width;
				curImage = personImage[cur];
			}
		}
	
		//角色使用道具
		public void buff_0()
		{	
			if (strength < 10)
				return;
			if (netMode && turns == myTurn)
			{
				emitString("DDTANK", "B0");
			}
			
			buffMusic.Play();
			choice = 0;
			fireTimes = 3;
			buff = true;
			buffCur = 0;
			damage /= 2;
			strength -= 10;
		}
		public void buff_1()
		{
			
			if (strength < 10)
				return;
			if (netMode && turns == myTurn)
			{
				emitString("DDTANK", "B1");
			}
			
			buffMusic.Play();
			choice = 1;
			damage = damage + damage / 2;
			buff = true;
			buffCur = 0;
			strength -= 10;
		}
		public void buff_2()
		{	
			if (strength < 5)
				return;
			if (netMode && turns == myTurn)
			{
				emitString("DDTANK", "B2");
			}
			
			buffMusic.Play();
			choice = 2;
		    damage = damage + damage / 5;
			buff = true;
			buffCur = 0;
			strength -= 5;
		}
		//发射炮弹
		public void fire()
		{
			if (fireable)
			{
				if (netMode && turns == myTurn)
				{
					emitString("DDTANK", "A" + calAngle);
					emitString("DDTANK", "P" + power);
					emitString("DDTANK", "FIRE");
				}
				
				fireMusic.Play();
				bulletFlying = true;
				bulletX = x + width / 2;
				bulletY = y + height / 2;
				bulletVx = (power * 10.0 * Math.cos(calAngle));
				bulletVy = -(power * 10.0 * Math.sin(calAngle));
				fireable = false;
		//		System.out.println("cos = " + Math.cos(calAngle) + " sin = " + Math.sin(calAngle));
			}
		}
		
		//人物动态
		public void motion()
		{
			motionCur++;
			if (motionCur >= motionInter + motionCnt * motionIntra)
				motionCur = 0;
			if (motionCur >= motionInter)
			{
				curImage = motionImage[(motionCur - motionInter) / motionIntra];
			}
			else
			{
				curImage = personImage[cur];
			}
				
		}
	}
	
	//障碍类
	private class Walls
	{
		int x, y;
		boolean visible;
		boolean stable;
		BufferedImage image;
		Walls(int x_, int y_, BufferedImage image_, boolean stable_)
		{
			x = x_;
			y = y_;
			visible = true;
			stable = stable_;
			image = image_;
		}
	}
	
	//毛玉类，一个电脑AI
	private class Maoyu
	{
		int x, y;
		int xmin, xmax;
		int damage;    //伤害值
		int health;    //生命值
		int cur;       //当前的帧
		
		boolean fireable;
		BufferedImage curImage;
		double leftAngle = 3 * 3.14 / 4;
		double rightAngle = 3.14 / 4;
		
		final int step = 4; //步长
		final int intra = 10;
		final int inter = maoyuCnt * intra;
		
		int explosionIndex;
		int bulletIndex;
		
		public Maoyu(int _x, int _y, int _xmin, int _xmax, int eI, int bI)
		{
			x = _x;
			y = _y;
			health = 100;
			damage = 10;
			cur = 0;
			xmin = _xmin;
			xmax = _xmax;
			fireable = false;
			curImage = maoyuImage[0];
			explosionIndex = eI;
			bulletIndex = bI;
		}
		public void motion()
		{
			cur += 1;
			if (cur >= inter) //移动超过一定时限，开始打人
			{
				cur = 0;
				curImage = maoyuImage[cur / intra];
				if (fireable)
					AI();
				return;
			}
			if (curImage != maoyuImage[cur / intra])
			{
				curImage = maoyuImage[cur / intra];
				int move = Math.abs(new Random().nextInt()) % 2;
				int tmpX, tmpY;
				if (move == 0) //左移
				{
					tmpX = x - step;
					if (tmpX < xmin)
						tmpX = xmin;
					x = tmpX;
				}
				else
				{
					tmpX = x + step;
					if (tmpX > xmax)
						tmpX = xmax;
					x = tmpX;
				}
			}
		}
		public void AI()
		{
			int dx = x - roles[0].x + (roles[0].width / 2);
			int dy = roles[0].y - y + roles[0].height;
		//	System.out.println("dx = " + dx + " dy = " + dy);
			if (dx > 0)
			{
				power = calPower(leftAngle, dx, dy);
				calAngle = leftAngle;
			}
			else
			{
				power = calPower(rightAngle, dx, dy);
				calAngle = rightAngle;
			}
		//	System.out.println("power = " + power);
			
			fire();
		}
		public void fire()
		{
			fireMusic.Play();
			firstPressed = false;
			bulletFlying = true;
			fireable = false;
			bulletX = x;
			bulletY = y;
			bulletVx = (power * 10.0 * Math.cos(calAngle));
			bulletVy = -(power * 10.0 * Math.sin(calAngle));
		}		
	}
	
	//音乐类，用于播放音乐
	private class SoundPlayer implements Runnable
	{
		String fileName;
		public SoundPlayer(String fileName_) 
		{
			fileName = fileName_;
		}
		public Thread Play()
		{	
			Thread t = new Thread(this);
			t.start();
			return t;
		}
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			BufferedInputStream buffer = null;
			try
			{
				buffer = new BufferedInputStream(new FileInputStream(fileName));  
			}
			catch (IOException e) 
			{
				//加载失败之后的操作
				JOptionPane.showMessageDialog(null, "读取音源错误！请检查目录下是否存在音源！", 
										"IOE错误", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				Exit();
			}
			Player player = null;
			try
			{
				if (buffer == null)
				{
					JOptionPane.showMessageDialog(null, "buffer为空！请检查目录下是否存在音源！", 
							"播放错误", JOptionPane.ERROR_MESSAGE);
					Exit();
				}
				player = new Player(buffer); 
			}
			catch (JavaLayerException e) 
			{
				//加载失败之后的操作
				JOptionPane.showMessageDialog(null, "读取音源错误！请检查目录下是否存在音源！", 
										"JLE错误", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				Exit();
			}
			try
			{
				if (player == null)
				{
					JOptionPane.showMessageDialog(null, "player为空！请检查目录下是否存在音源！", 
							"播放错误", JOptionPane.ERROR_MESSAGE);
					Exit();
				}
				player.play();
			}
			catch (JavaLayerException e)
			{
				//加载失败之后的操作
				JOptionPane.showMessageDialog(null, "播放音源错误！请检查目录下是否存在音源！", 
										"播放错误", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				Exit();
			}
		}
	}
	
	//地图类库，存储地图
	static class MapFactory
	{
		//17 * 31的map
		 static byte map[][][] = {
		 {
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			 { 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
			 { 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
			 { 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
		 },
		 {
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 },
			 { 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 },
			 { 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 },
			 { 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }			 
		 },
		 {
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }	 
		 },
		 {
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			 { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
		 }
		 };
	}

}
