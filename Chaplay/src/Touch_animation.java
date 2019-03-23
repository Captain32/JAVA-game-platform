

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Touch_animation extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel backPanel = new JPanel();
	private JButton buttona = new JButton("开始");
	private JButton buttonb = new JButton("退出");
	private JLabel label1 = new JLabel("分数");
	private JLabel label2 = new JLabel("时间");
	private JTextField textarea1 = new JTextField(10);//显示分数
	private JTextField textarea2 = new JTextField(10);//显示时间
	private JProgressBar jindu = new JProgressBar();
	private Timer timer; //定时器，控制时间进度条
	private int times = 0; //游戏已经进行的时间
	private RoomPanel roomPanel = new RoomPanel();
	
	//背景图片的宽度和高度
	private final int backgroundWidth = 960;
	private final int backgroundHeight = 540;
	//图形区域的对角顶点的坐标
	private final int minX = 224;
	private final int maxX = 736;
	private final int minY = 40;
	private final int maxY = 552;
	//图片的尺寸，界面的大小，图片的个数	
	private final int PICTURE_WIDTH = 64;
	private final int PICTURE_HEIGHT = 64;
	private final int matrixR = 8, matrixC = 8;
	private final int pictureCnt = 7;
	private int pictures[][] = new int[matrixR][matrixC];
	private final int EMPTY = 7;
	private Random rand = new Random();
	private boolean isDoubleClicked = false; //是否选中了两个图片
	private int cClicked, rClicked; //第一次单击按钮的坐标
	private int cCur, rCur; //目前鼠标所在的位置
	private int grade = 0; //玩家的分数
	
	//图片部分
	private BufferedImage pool; //进度条空部分
	private BufferedImage room; //进度条占用部分
	private BufferedImage roomBackground; //图片背景
	private BufferedImage pictureImage[] = new BufferedImage[pictureCnt]; //存储imgcnt张图片	
	
	//动画部分
	private boolean isAnimation; //是否处于动画状态
	private boolean isExchanging; //是否处于交换状态
	private boolean isExplosion; //是否处于爆炸状态
	private boolean isFalling;
	private Timer animation; //动画的定时器
	
	private BufferedImage explosionImage[]; //所有图片
	private final int explosionCnt = 25; //一共25帧
	private int explosionCur; //当前的帧数
	private boolean explosion[][]; //爆炸矩阵
	
	private final int changeCnt = 32;
	private boolean changeBack;
	private int changeCur;
	private int sr, sc, dr, dc; //交换图标的坐标
	private int pictureY[][] = new int[matrixR][matrixC];
	private int pictureX[][] = new int[matrixR][matrixC];
	
	public Touch_animation()
	{	
		this.setLayout(new BorderLayout());
		backPanel.setLayout(new FlowLayout());
		backPanel.add(buttona);
		backPanel.add(label1);
		backPanel.add(textarea1);
		textarea1.setEditable(false);
		textarea1.setText(Integer.toString(grade));
		textarea2.setText(Integer.toString(100) + "秒");
		textarea2.setEditable(false);
		backPanel.add(label2);
		backPanel.add(textarea2);
		backPanel.add(buttonb);
		MyListener mylisten = new MyListener(); //自定义的监听类
		buttona.addActionListener(mylisten);
		buttonb.addActionListener(mylisten);
		
		this.add(backPanel, BorderLayout.NORTH);
		this.add(roomPanel, BorderLayout.CENTER);
		initImage();
		init();
		isAnimation = true;
	}
	
	
	public void init()
	{
		/*	do
		{
			System.out.println("重新初始化");
			initPictureMatrix();
		} while (globalSearch(1)); //直到不出现三个相连的情况 */
		isExplosion = false;
		isFalling = false;
		isExchanging = false;
		isAnimation = false;
		changeBack = false;
		
		upsetPictureMatrix();
	//	print(); //打印出pictures的信息	
		timer = new Timer(1000, new TimeListener());
		timer.start();
		grade = 0;
		repaint();
	}
	
	
	//加载背景图片和所有的图案
	private void initImage()
	{
		// TODO Auto-generated method stub
		//加载动画部分
	 	animation = new Timer(10, new Animation());
		try
		{
			String path = ".\\Touch_material\\";
			//读取爆炸图片
			explosionImage = new BufferedImage[explosionCnt];
			for (int i = 0; i < explosionCnt; i++)
				explosionImage[i] = ImageIO.read(new File(path + "explosion\\explosion" + i + ".png"));
			//角色
			String name[] = { "alice", "kaguya", "marisa", 
							 "patchouli", "reimu", "youmu", "yuyuko" };
			String pictureFormat = ".png";
			for (int i = 0; i < pictureCnt; i++)
			{
				pictureImage[i] = ImageIO.read(new File(path + name[i] + pictureFormat));
			}
			//背景
			roomBackground = ImageIO.read(new File(".\\Touch_material\\background.jpg"));
			pool = ImageIO.read(new File(path + "2.png"));
			room = ImageIO.read(new File(path + "1.png"));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "读取图片错误！请检查目录下是否存在图片！", 
					"读取错误", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			GameOver();
		}
		//初始化explosion矩阵
		explosion = new boolean[matrixC][matrixR];
		for (int i = 0; i < matrixC; i++)
			for (int j = 0; j < matrixR; j++)
				explosion[i][j] = false;
		//设定图片的显示位置
		for (int i = 0; i < matrixR; i++)
		{
			for (int j = 0; j < matrixC; j++)
			{
				pictureX[i][j] = minX + PICTURE_WIDTH * j;
				pictureY[i][j] = minY + PICTURE_HEIGHT * i;;
			}
		}
		
		
		//设置房间的最佳大小
		roomPanel.setPreferredSize(new Dimension(roomBackground.getWidth(),
												roomBackground.getHeight()));
		roomPanel.addMouseMotionListener(new MyMouseMotionListener());
		roomPanel.addMouseListener(new MyMouseListener());
		
		setTitle("对对碰");
		pack();
		setResizable(false);
		setVisible(true);
	}

	//判断是否有3个以上的连续的相同方块按钮
	//kinds[0]为1表示横向消除，10表示纵向消除 11 表示横纵都消除
	private boolean isThreeLinked(int y, int x, int[] kinds)
	{
		int tmp;
		int linked = 1;
		kinds[0] = 0;
		if (x + 1 < matrixC)
		{
			tmp = x + 1;
			while (tmp < matrixC && pictures[y][x] == pictures[y][tmp])
			{
				linked++;
				tmp++;
			}
		}
		if (x - 1 >= 0)
		{
			tmp = x - 1;
			while (tmp >= 0 && pictures[y][x] == pictures[y][tmp])
			{
				linked++;
				tmp--;
			}
		}
		//如果横向有超过三个连续块
		if (linked >= 3)
		{
			kinds[0] += 1;
		}
		
		//检查纵向的		
		linked = 1;
		if (y + 1 < matrixR)
		{
			tmp = y + 1;
			while (tmp < matrixR && pictures[y][x] == pictures[tmp][x])
			{
				linked++;
				tmp++;
			}
		}
		if (y - 1 >= 0)
		{
			tmp = y - 1;
			while (tmp >= 0 && pictures[y][x] == pictures[tmp][x])
			{
				linked++;
				tmp--;
				
			}
		}
		//如果纵向连续相同超过三个方块
		if (linked >= 3)
		{
			kinds[0] += 10;
		}
		
		if (kinds[0] == 0)
			return false;
		return true;
	}
	
	//如其名，该干啥干啥的
	private void removeLinked(int y, int x, int kind)
	{
		if (pictures[y][x] == EMPTY) return;
		int n = 0;
		int tmp;
		//横向消除
		if (kind != 0)
		{
			if (kind % 10 == 1)
			{
				tmp = x + 1;
				while (tmp < matrixC && pictures[y][x] == pictures[y][tmp])
				{
					pictures[y][tmp] = EMPTY;
					n++;
					tmp++;
				}
				tmp = x - 1;
				while (tmp >= 0 && pictures[y][x] == pictures[y][tmp])
				{
					pictures[y][tmp] = EMPTY;
					n++;
					tmp--;
				}
			}
			if (kind / 10 == 1)
			{
				tmp = y + 1;
				while (tmp < matrixR && pictures[y][x] == pictures[tmp][x])
				{
					pictures[tmp][x] = EMPTY;
					n++;
					tmp++;
				}
				tmp = y - 1;
				while (tmp >= 0 && pictures[y][x] == pictures[tmp][x])
				{
					pictures[tmp][x] = EMPTY;
					n++;
					tmp--;
				}
			}
			
			pictures[y][x] = EMPTY;
		}
		grade += n * 10;
		//续命
		if (times >= n / 3)
			times -= n / 3;
		textarea1.setText(Integer.toString(grade));
		callAnimation();
	}
	
	private void callAnimation()
	{
		for (int i = 0; i < matrixR; i++)
			for (int j = 0; j < matrixC; j++)
				if (pictures[i][j] == EMPTY)
					explosion[i][j] = true;
		isExplosion = true;
		isAnimation = true;
		animation.start();
	}
	
	//flag == 1时仅仅扫描，flag == 2时加上消除
	private boolean globalSearch(int flag)
	{
		int[] kinds = { 0 };
		if (flag == 1)
		{
			for (int y = 0; y < matrixR; y++)
			{
				for (int x = 0; x < matrixC; x++)
				{
					if (pictures[y][x] != EMPTY && isThreeLinked(y, x, kinds))
					{
						return true;
					}
				}
			}
		}
		else
		{
			for (int y = 0; y < matrixR; y++)
			{
				for (int x = 0; x < matrixC; x++)
				{
					if (pictures[y][x] != EMPTY && isThreeLinked(y, x, kinds))
					{
					//	System.out.println("消除点");
						removeLinked(y, x, kinds[0]);
						isFalling = true;
						
						return true;
					}
				}
			}			
		}
		return false;
	}

	//图片下降
	private void downPictures()
	{
		int tmp;
		for (int i = matrixR - 1; i >= 0; i--)
		{
			for (int j = 0; j < matrixC; j++)
			{
				if (pictures[i][j] == EMPTY)
				{
					for (int k = i - 1; k >= 0; k--)
					{
						if (pictures[k][j] != EMPTY)
						{
							tmp = pictures[k][j];
							pictures[k][j] = pictures[i][j];
							pictures[i][j] = tmp;
							break;
						}
					}
				}
			}
		}
		initPictureMatrix();
		isFalling = false;
		repaint();
		//检测连锁反应
		globalSearch(2);
	}
	
	//交换图片
	protected void swapPictures(int x, int y)
	{
		// TODO Auto-generated method stub
		if ((x >= minX && x <= maxX) && (y >= minY && y <= maxY))
		{
			int cAbs, rAbs;
			cAbs = (x - minX) / PICTURE_WIDTH;
			rAbs = (y - minY) / PICTURE_HEIGHT;
		//	System.out.println("rAbs = " + rAbs + " cAbs = " + cAbs);
			if (!isDoubleClicked)
			{
			//	System.out.println("\nFirst Clicked");
				isDoubleClicked = true;
				cCur = cAbs;
				rCur = rAbs;
			}
			else
			{
				isDoubleClicked = false;
				System.out.printf("rCur = %d, cCur = %d, rAbs = %d, cAbs = %d\n", rCur, cCur, rAbs, cAbs);
				if ((1 == Math.abs(rCur - rAbs) && cCur == cAbs)
					|| (1 == Math.abs(cCur - cAbs) && rCur == rAbs))
				{
				//	System.out.println("相邻的");
					sr = rCur;
					sc = cCur;
					dr = rAbs;
					dc = cAbs;
					isExchanging = true;
					isAnimation = true;
					animation.start();
					
				/*	int tmp;
					tmp = pictures[rCur][cCur];
					pictures[rCur][cCur] = pictures[rAbs][cAbs];
					pictures[rAbs][cAbs] = tmp; */
				}
			}
		}
	}

	//测试交换结果
	private void testExchanging()
	{
		int kinds[] = { 0 };
		if (isThreeLinked(sr, sc, kinds))
		{
		//	System.out.println("消除点");
			animation.stop();
			isExchanging = false;
			removeLinked(sr, sc, kinds[0]);
			isFalling = true;
		}
		else if (isThreeLinked(dr, dc, kinds))
		{
		//	System.out.println("消除点");
			animation.stop();
			isExchanging = false;
			removeLinked(dr, dc, kinds[0]);
			isFalling = true;
		}
		else
		{
			//交换回来
			changeBack = true;
			isExchanging = true;
		}
	}
	
	//重新产生图案
	private void initPictureMatrix()
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < matrixR;  i++)
			for (int j = 0; j < matrixC; j++)
			{
				if (pictures[i][j] == EMPTY)
					pictures[i][j] = rand.nextInt(7);
			}
	}

	//打乱图案
	private void upsetPictureMatrix()
	{
		for (int i = 0; i < matrixR;  i++)
			for (int j = 0; j < matrixC; j++)
			{
				pictures[i][j] = rand.nextInt(7);
			}		
	}

	/*public static void main(String[] args)
	{
		Touch_animation frame = new Touch_animation();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}*/
	
	class MyListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == buttona)
			{
				buttona.setEnabled(false);
/*				jindu.setStringPainted(true);
				jindu.setMaximum(100);
				jindu.setMinimum(0); */
				grade = 0;
				textarea1.setText(Integer.toString(grade));
				init();
			}
			if (e.getSource() == buttonb)
			{
		//		System.out.println("end");
				GameOver();
			}

		}
	}
	
	class MyMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (isAnimation) //屏蔽鼠标的单击功能
				return; 
	//		System.out.println("X = " + e.getX());
	//		System.out.println("Y = " + e.getY());
			int x = e.getX(), y = e.getY();
			
			if ((x >= minX && x <= maxX) && (y >= minY && y <= maxY))
			{
				cClicked = (x - minX) / PICTURE_WIDTH;
				rClicked = (y - minY) / PICTURE_HEIGHT;
				repaint();
			}
			swapPictures(x, y);
			
		}
	}
	
	class MyMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();
			setMouseCurHand(x, y);
		}

		private void setMouseCurHand(int x, int y) //改变鼠标显示的图标
		{
			// TODO Auto-generated method stub
			if ((x >= minX && x <= maxX) && (y >= minY && y <= maxY))
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			else
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	class TimeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			times++;
			if (times > 200)
			{
				timer.stop();
				//定时器结束
				buttona.setEnabled(true);
				int answer = JOptionPane.showConfirmDialog(null, "游戏结束，是否继续", "游戏结束",
													JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION)
					init();
				else
					GameOver();
			}
			else
			{
				textarea2.setText(Integer.toString(100 - times / 2) + "秒");
			}
			//设定动画
			if (!isExplosion && !isFalling && !isExchanging)
				isAnimation = false;
			
			if (isFalling && !isExplosion)
				downPictures();
			
			repaint();
		}
	}
	
	class Animation implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (isExplosion)
			{
				explosionCur++;
				if (explosionCur >= explosionCnt)
				{
					explosionCur = 0;
					for (int i = 0; i < matrixR; i++)
						for (int j = 0; j < matrixC; j++)
							explosion[i][j] = false;
					isExplosion = false;
					animation.stop();
				}
				repaint();
			}
			if (isExchanging)
			{
				changeCur++;
				if (changeCur > changeCnt)
				{
					changeCur = 0;
					isExchanging = false;
					isAnimation = false;
					animation.stop();
					int tmp = pictures[sr][sc];
					pictures[sr][sc] = pictures[dr][dc];
					pictures[dr][dc] = tmp;
					
					pictureX[sr][sc] = minX + sc * PICTURE_WIDTH;
					pictureY[sr][sc] = minY + sr * PICTURE_HEIGHT;
					pictureX[dr][dc] = minX + dc * PICTURE_WIDTH;
					pictureY[dr][dc] = minY + dr * PICTURE_HEIGHT;
					
					if (!changeBack)
					{
						isExchanging = true;
						isAnimation = true;
						testExchanging();
						animation.start();
					}
					else
					{
						testExchanging();
						changeBack = false;
					}
				}
				/*
				 * sx = minX + sc * PICTURE_WDITH, sy = minY + sr * PICTURE_HEIGHT
				 * dx = minX + dc * PICTURE_WIDTH, dy = minY + dr * PICTURE_HEIGHT
				 */
				pictureX[sr][sc] = minX + sc * PICTURE_WIDTH + (dc - sc) * 2 * changeCur;
				pictureY[sr][sc] = minY + sr * PICTURE_HEIGHT + (dr - sr) * 2 * changeCur;
				pictureX[dr][dc] = minX + dc * PICTURE_WIDTH + (sc - dc) * 2 * changeCur;
				pictureY[dr][dc] = minY + dr * PICTURE_HEIGHT + (sr - dr) * 2 * changeCur;
				repaint();
			}
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
	
	//显示信息
	private void print()
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < matrixR; i++)
		{
			for (int j = 0; j < matrixC; j++)
			{
				System.out.print(pictures[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	class RoomPanel extends JPanel
	{
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			//绘制背景图片
			g2d.drawImage(roomBackground, 0, 0,
					roomBackground.getWidth(), roomBackground.getHeight(), null);
			
			//绘制进度条
			g2d.drawImage(pool, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0, 
					pool.getWidth(), pool.getHeight(), null);
			g2d.drawImage(pool, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0, 
					16, pool.getHeight(), null);
			g2d.drawImage(room, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0,
					(room.getWidth() * times / 2) / 100, room.getHeight(), null); 
			
			//绘制图案
			for (int i = 0; i < matrixR; i++)
			{
				for (int j = 0; j < matrixC; j++)
				{
					if (pictures[i][j] == EMPTY)
						continue;
					g2d.drawImage(pictureImage[pictures[i][j]], pictureX[i][j],
								pictureY[i][j], PICTURE_WIDTH, PICTURE_HEIGHT, null);
				}
			}
			g2d.setColor(Color.BLUE); //这里要改一下
			g2d.drawRect(minX + PICTURE_WIDTH * cClicked, minY + PICTURE_HEIGHT * rClicked,
					PICTURE_WIDTH, PICTURE_HEIGHT);
			//画动画
			if (isExplosion)
			{
				for (int i = 0; i < matrixC; i++)
					for (int j = 0; j < matrixR; j++)
						if (explosion[i][j])
							g2d.drawImage(explosionImage[explosionCur], minX + PICTURE_WIDTH * j, minY + PICTURE_HEIGHT * i, 
									PICTURE_WIDTH, PICTURE_HEIGHT, null);
			}
		}
	}

	public void GameOver()
	{
		timer.stop();
		dispose();
		emitString("" + grade);
	}
	
	public void emitString(String str)
	{
		System.out.println("SCORE@TOUCH@" + str);
		String Str = "SCORE@TOUCH@" + str;
		platform_interface.clientSocket.sendGameMsg(Str);
	}
}
