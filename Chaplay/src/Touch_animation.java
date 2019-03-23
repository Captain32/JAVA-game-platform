

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
	private JButton buttona = new JButton("��ʼ");
	private JButton buttonb = new JButton("�˳�");
	private JLabel label1 = new JLabel("����");
	private JLabel label2 = new JLabel("ʱ��");
	private JTextField textarea1 = new JTextField(10);//��ʾ����
	private JTextField textarea2 = new JTextField(10);//��ʾʱ��
	private JProgressBar jindu = new JProgressBar();
	private Timer timer; //��ʱ��������ʱ�������
	private int times = 0; //��Ϸ�Ѿ����е�ʱ��
	private RoomPanel roomPanel = new RoomPanel();
	
	//����ͼƬ�Ŀ�Ⱥ͸߶�
	private final int backgroundWidth = 960;
	private final int backgroundHeight = 540;
	//ͼ������ĶԽǶ��������
	private final int minX = 224;
	private final int maxX = 736;
	private final int minY = 40;
	private final int maxY = 552;
	//ͼƬ�ĳߴ磬����Ĵ�С��ͼƬ�ĸ���	
	private final int PICTURE_WIDTH = 64;
	private final int PICTURE_HEIGHT = 64;
	private final int matrixR = 8, matrixC = 8;
	private final int pictureCnt = 7;
	private int pictures[][] = new int[matrixR][matrixC];
	private final int EMPTY = 7;
	private Random rand = new Random();
	private boolean isDoubleClicked = false; //�Ƿ�ѡ��������ͼƬ
	private int cClicked, rClicked; //��һ�ε�����ť������
	private int cCur, rCur; //Ŀǰ������ڵ�λ��
	private int grade = 0; //��ҵķ���
	
	//ͼƬ����
	private BufferedImage pool; //�������ղ���
	private BufferedImage room; //������ռ�ò���
	private BufferedImage roomBackground; //ͼƬ����
	private BufferedImage pictureImage[] = new BufferedImage[pictureCnt]; //�洢imgcnt��ͼƬ	
	
	//��������
	private boolean isAnimation; //�Ƿ��ڶ���״̬
	private boolean isExchanging; //�Ƿ��ڽ���״̬
	private boolean isExplosion; //�Ƿ��ڱ�ը״̬
	private boolean isFalling;
	private Timer animation; //�����Ķ�ʱ��
	
	private BufferedImage explosionImage[]; //����ͼƬ
	private final int explosionCnt = 25; //һ��25֡
	private int explosionCur; //��ǰ��֡��
	private boolean explosion[][]; //��ը����
	
	private final int changeCnt = 32;
	private boolean changeBack;
	private int changeCur;
	private int sr, sc, dr, dc; //����ͼ�������
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
		textarea2.setText(Integer.toString(100) + "��");
		textarea2.setEditable(false);
		backPanel.add(label2);
		backPanel.add(textarea2);
		backPanel.add(buttonb);
		MyListener mylisten = new MyListener(); //�Զ���ļ�����
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
			System.out.println("���³�ʼ��");
			initPictureMatrix();
		} while (globalSearch(1)); //ֱ��������������������� */
		isExplosion = false;
		isFalling = false;
		isExchanging = false;
		isAnimation = false;
		changeBack = false;
		
		upsetPictureMatrix();
	//	print(); //��ӡ��pictures����Ϣ	
		timer = new Timer(1000, new TimeListener());
		timer.start();
		grade = 0;
		repaint();
	}
	
	
	//���ر���ͼƬ�����е�ͼ��
	private void initImage()
	{
		// TODO Auto-generated method stub
		//���ض�������
	 	animation = new Timer(10, new Animation());
		try
		{
			String path = ".\\Touch_material\\";
			//��ȡ��ըͼƬ
			explosionImage = new BufferedImage[explosionCnt];
			for (int i = 0; i < explosionCnt; i++)
				explosionImage[i] = ImageIO.read(new File(path + "explosion\\explosion" + i + ".png"));
			//��ɫ
			String name[] = { "alice", "kaguya", "marisa", 
							 "patchouli", "reimu", "youmu", "yuyuko" };
			String pictureFormat = ".png";
			for (int i = 0; i < pictureCnt; i++)
			{
				pictureImage[i] = ImageIO.read(new File(path + name[i] + pictureFormat));
			}
			//����
			roomBackground = ImageIO.read(new File(".\\Touch_material\\background.jpg"));
			pool = ImageIO.read(new File(path + "2.png"));
			room = ImageIO.read(new File(path + "1.png"));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "��ȡͼƬ��������Ŀ¼���Ƿ����ͼƬ��", 
					"��ȡ����", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			GameOver();
		}
		//��ʼ��explosion����
		explosion = new boolean[matrixC][matrixR];
		for (int i = 0; i < matrixC; i++)
			for (int j = 0; j < matrixR; j++)
				explosion[i][j] = false;
		//�趨ͼƬ����ʾλ��
		for (int i = 0; i < matrixR; i++)
		{
			for (int j = 0; j < matrixC; j++)
			{
				pictureX[i][j] = minX + PICTURE_WIDTH * j;
				pictureY[i][j] = minY + PICTURE_HEIGHT * i;;
			}
		}
		
		
		//���÷������Ѵ�С
		roomPanel.setPreferredSize(new Dimension(roomBackground.getWidth(),
												roomBackground.getHeight()));
		roomPanel.addMouseMotionListener(new MyMouseMotionListener());
		roomPanel.addMouseListener(new MyMouseListener());
		
		setTitle("�Զ���");
		pack();
		setResizable(false);
		setVisible(true);
	}

	//�ж��Ƿ���3�����ϵ���������ͬ���鰴ť
	//kinds[0]Ϊ1��ʾ����������10��ʾ�������� 11 ��ʾ���ݶ�����
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
		//��������г�������������
		if (linked >= 3)
		{
			kinds[0] += 1;
		}
		
		//��������		
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
		//�������������ͬ������������
		if (linked >= 3)
		{
			kinds[0] += 10;
		}
		
		if (kinds[0] == 0)
			return false;
		return true;
	}
	
	//���������ø�ɶ��ɶ��
	private void removeLinked(int y, int x, int kind)
	{
		if (pictures[y][x] == EMPTY) return;
		int n = 0;
		int tmp;
		//��������
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
		//����
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
	
	//flag == 1ʱ����ɨ�裬flag == 2ʱ��������
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
					//	System.out.println("������");
						removeLinked(y, x, kinds[0]);
						isFalling = true;
						
						return true;
					}
				}
			}			
		}
		return false;
	}

	//ͼƬ�½�
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
		//���������Ӧ
		globalSearch(2);
	}
	
	//����ͼƬ
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
				//	System.out.println("���ڵ�");
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

	//���Խ������
	private void testExchanging()
	{
		int kinds[] = { 0 };
		if (isThreeLinked(sr, sc, kinds))
		{
		//	System.out.println("������");
			animation.stop();
			isExchanging = false;
			removeLinked(sr, sc, kinds[0]);
			isFalling = true;
		}
		else if (isThreeLinked(dr, dc, kinds))
		{
		//	System.out.println("������");
			animation.stop();
			isExchanging = false;
			removeLinked(dr, dc, kinds[0]);
			isFalling = true;
		}
		else
		{
			//��������
			changeBack = true;
			isExchanging = true;
		}
	}
	
	//���²���ͼ��
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

	//����ͼ��
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
			if (isAnimation) //�������ĵ�������
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

		private void setMouseCurHand(int x, int y) //�ı������ʾ��ͼ��
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
				//��ʱ������
				buttona.setEnabled(true);
				int answer = JOptionPane.showConfirmDialog(null, "��Ϸ�������Ƿ����", "��Ϸ����",
													JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION)
					init();
				else
					GameOver();
			}
			else
			{
				textarea2.setText(Integer.toString(100 - times / 2) + "��");
			}
			//�趨����
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
	
	//��ʾ��Ϣ
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
			//���Ʊ���ͼƬ
			g2d.drawImage(roomBackground, 0, 0,
					roomBackground.getWidth(), roomBackground.getHeight(), null);
			
			//���ƽ�����
			g2d.drawImage(pool, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0, 
					pool.getWidth(), pool.getHeight(), null);
			g2d.drawImage(pool, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0, 
					16, pool.getHeight(), null);
			g2d.drawImage(room, roomBackground.getWidth() / 2 - pool.getWidth() / 2, 0,
					(room.getWidth() * times / 2) / 100, room.getHeight(), null); 
			
			//����ͼ��
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
			g2d.setColor(Color.BLUE); //����Ҫ��һ��
			g2d.drawRect(minX + PICTURE_WIDTH * cClicked, minY + PICTURE_HEIGHT * rClicked,
					PICTURE_WIDTH, PICTURE_HEIGHT);
			//������
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
