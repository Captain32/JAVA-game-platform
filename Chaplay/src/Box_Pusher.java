import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Box_Pusher
{
	static class MapFactory
	{
		 static byte sampleMap[][][] = {
			{
				{ 0, 0, 1, 1, 1, 0, 0, 0 },
				{ 0, 0, 1, 4, 1, 0, 0, 0 },
				{ 0, 0, 1, 9, 1 ,1 ,1, 1 },
				{ 1, 1, 1, 2, 9, 2, 4, 1 },
				{ 1, 4, 9, 2, 5, 1, 1, 1 },
				{ 1, 1, 1, 1, 2, 1, 0, 0 },
				{ 0, 0, 0, 1, 4, 1, 0, 0 },
				{ 0, 0, 0, 1, 1, 1, 0, 0 }
			},
			{
				{ 1, 1, 1, 1, 1, 0, 0, 0, 0 },
				{ 1, 9, 9, 5, 1, 0, 0, 0, 0 },
				{ 1, 9, 2, 2, 1, 0, 1, 1, 1 },
				{ 1, 9, 2, 9, 1, 0, 1, 4, 1 },
				{ 1, 1, 1, 9, 1, 1, 1, 4, 1 },
				{ 0, 1, 1, 9, 9, 9, 9, 4, 1 },
				{ 0, 1, 9, 9, 9, 1, 9, 9, 1 },
				{ 0, 1, 9, 9, 9, 1, 1, 1, 1 },
				{ 0, 1, 1, 1, 1, 1, 0, 0, 0 }
			},
			{
				{ 0, 0, 1, 1, 1, 1, 0, 0 },
				{ 0, 0, 1, 9, 9, 1, 1, 1 },
				{ 1, 1, 1, 9, 9, 9, 9, 1 },
				{ 1, 9, 9, 3, 4, 1, 9, 1 },
				{ 1, 9, 9, 2, 3, 9, 9, 1 },
				{ 1, 1, 1, 9, 8, 1, 1, 1 },
				{ 0, 0, 1, 9, 9, 1, 0, 0 },
				{ 0, 0, 1, 1, 1, 1, 0, 0 }
			}
			};
		static int count = sampleMap.length;
		public static byte[][] getMap(int grade)
		{
			byte temp[][];
			if (grade >= 0 && grade < count)
				temp = sampleMap[grade];
			else //默认选第一关
				temp = sampleMap[0];
			int row = temp.length;
			int column = temp[0].length;
			byte[][] result = new byte[row][column];
			for (int i = 0; i < row; i++)
				for (int j = 0; j < column; j++)
					result[i][j] = temp[i][j];
			return result;
		}
		public static int getCount()
		{
			return count;
		}
	}
	static class Map
	{
		//角色的坐标
		//X向右增长，Y向下增长
		int manX = 0;
		int manY = 0;
		byte map[][];
		int grade;
		//这个构造方法用于撤销操作
		//撤销操作只需要人的位置和地图的当前状态
		public Map(int manx, int many, byte[][] mmap)
		{
			this.manX = manx;
			this.manY = many;
			int row = mmap.length;
			int column = mmap[0].length;
			byte temp[][] = new byte[row][column];
			for (int i = 0; i < row; i++)
				for (int j = 0; j < column; j++)
					temp[i][j] = mmap[i][j];
			this.map = temp;
		}
		
		//这个构造方法用于保存操作
		//恢复地图的社会需要人的位置，地图的当前状态和关卡数
		public Map(int manx, int many, byte[][] mmap, int ggrade)
		{
			this(manx, many, mmap);
			this.grade = ggrade;
		}
		
		public int getManX()
		{
			return manX;
		}
		public int getManY()
		{
			return manY;
		}
		public byte[][] getMap()
		{
			return map;
		}
		public int getGrade()
		{
			return grade;
		}
	}
	static class GameFrame extends JFrame implements ActionListener, MouseListener, KeyListener
	{
		private static final long serialVersionUID = 1L;
		//缓冲
		private Image iBuffer = null;
		private Graphics gBuffer = null;
		
		//主面板类
		private Timer timer = new Timer(1000, new TimeListener()); //定时器，控制时间进度条
		private int totalTime;
		private final int maxTime = 6; //一共五分钟
		private int grade = 0;
		//row, column 记载人的行号、列号
		//leftX, leftY记载左上角图片的位置，避免图片从(0,0)坐标开始
		private int row = 7, column = 7, leftX = 0, leftY = 0;
		//记载地图的行列数
		private int mapRow = 0, mapColumn = 0;
		//width, height记载屏幕的大小
		private int width = 0, height = 0;
		private boolean acceptKey = true;
		private boolean clear = false;
		//程序所用到的图片
		private Image pic[] = null;
		private byte[][] map = null;
		private ArrayList<Map> list = new ArrayList<Map>();
		private Image background = null;
		
		//一些常数
		final byte BLANK = 0, WALL = 1, BOX = 2, BOXONEND = 3, END = 4, MANDOWN = 5,
				MANLEFT = 6, MANRIGHT = 7, MANUP = 8, GRASS = 9,
				MANDOWNONEND = 10, MANLEFTONEND = 11, MANRIGHTONEND = 12,
				MANUPONEND = 13;
		//图片的大小，都是正方形
		final int pngSize = 64;
		
		public GameFrame()
		{
			super("推箱子游戏");
			setSize(10 * pngSize, 10 * pngSize);
			setVisible(true);
			setResizable(false);
			setLocation(300, 20);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container cont = getContentPane();
			cont.setLayout(null);
			cont.setBackground(Color.black);
			//最初始的13张图片
			getPic();
			width = this.getWidth();
			height = this.getHeight();
			this.setFocusable(true);
			initMap();
			this.addKeyListener(this);
			this.addMouseListener(this);
			totalTime = maxTime;
			timer.start();
		}
		
		//初始化本关grade游戏地图，清空悔棋信息列表list,调用getMapSizeAndPosition()获取游戏区域大小及现实游戏的左上角位置leftX leftY
		public void initMap()
		{
			map = getMap(grade);
			list.clear();
			getMapSizeAndPosition();
			getManPosition();
		}

		public void printMap()
		{
			if (map != null)
			{
				for (int i = 0; i < map.length; i++)
				{
					for (int j = 0; j < map[0].length; j++)
						System.out.print(map[i][j] + " ");
					System.out.println();
				}
				System.out.println();
			}
			else
				System.out.println("map is null");
		}
		
		public void getManPosition()
		{
			for (int i = 0; i < map.length; i++)
			{
				for (int j = 0; j < map[0].length; j++)
				{
					if (map[i][j] == MANDOWN || map[i][j] == MANDOWNONEND
					|| map[i][j] == MANUP || map[i][j] == MANUPONEND
					|| map[i][j] == MANLEFT || map[i][j] == MANLEFTONEND
					|| map[i][j] == MANRIGHT || map[i][j] == MANRIGHTONEND)
					{
						row = i;
						column = j;
						break;
					}
				}
			}
		}
		
		public void getMapSizeAndPosition()
		{
			mapRow = map.length;
			mapColumn = map[0].length;
			leftX = (width - map[0].length * pngSize) / 2;
			leftY = (height - map.length * pngSize) / 2;
			//System.out.println("leftX = " + leftX);
			//System.out.println("leftY = " + leftY);
			//System.out.println("mapRow = " + mapRow);
			//System.out.println("mapColumn = " + mapColumn);
		}
		
		//加载要显示的图片
		public void getPic()
		{
			pic = new Image[14];
			for (int i = 0; i <= 13; i++)
			{
				pic[i] = Toolkit.getDefaultToolkit().getImage(".\\Box_material\\pic" + i + ".jpg");
			}
			background = Toolkit.getDefaultToolkit().getImage(".\\Box_material\\background.jpg");
		}
		
		//判断人物所在的位置是GRASS还是END
		public byte grassOrEnd(byte man)
		{
			byte result = GRASS;
			if (man == MANDOWNONEND || man == MANUPONEND || man == MANRIGHTONEND || man == MANLEFTONEND)
				result = END;
			return result;
		}
		
		//以下四个函数是人物移动
		private void moveUp()
		{
			//前方是墙壁
			if (map[row - 1][column] == WALL)
				return;
			//前方是BOX或BOXONEND
			if (map[row - 1][column] == BOX || map[row - 1][column] == BOXONEND)
			{
				//看前前位
				if (map[row - 2][column] == END || map[row - 2][column] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row - 2][column] == END? BOXONEND : BOX;
					byte manTemp = map[row - 1][column] == BOX? MANUP : MANUPONEND;
					
					//人和箱子都往前移动一步
					map[row - 2][column] = boxTemp;
					map[row - 1][column] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//修改坐标
					row--;
				}
			}
			//前方是GRASS或END
			else
			{
				if (map[row - 1][column] == GRASS || map[row - 1][column] == END)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte temp = map[row - 1][column] == END? MANUPONEND : MANUP;
					map[row - 1][column] = temp;
					map[row][column] = grassOrEnd(map[row][column]);
					row--;
				}
			}
			
			//printMap();
		}
		private void moveDown()
		{
			//前方是墙壁
			if (map[row + 1][column] == WALL)
				return;
			//前方是BOX或BOXONEND
			if (map[row + 1][column] == BOX || map[row + 1][column] == BOXONEND)
			{
				//看前前位
				if (map[row + 2][column] == END || map[row + 2][column] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row + 2][column] == END? BOXONEND : BOX;
					byte manTemp = map[row + 1][column] == BOX? MANDOWN : MANDOWNONEND;
					
					//人和箱子都往前移动一步
					map[row + 2][column] = boxTemp;
					map[row + 1][column] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//修改坐标
					row++;
				}
			}
			//前方是GRASS或END
			else
			{
				if (map[row + 1][column] == GRASS || map[row + 1][column] == END)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte temp = map[row + 1][column] == END? MANDOWNONEND : MANDOWN;
					map[row + 1][column] = temp;
					map[row][column] = grassOrEnd(map[row][column]);
					row++;
				}
			}
			
			//printMap();
		}
		private void moveLeft()
		{
			//前方是墙壁
			if (map[row][column - 1] == WALL)
				return;
			//前方是BOX或BOXONEND
			if (map[row][column - 1] == BOX || map[row][column - 1] == BOXONEND)
			{
				//看前前位
				if (map[row][column - 2] == END || map[row][column - 2] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row][column - 2] == END? BOXONEND : BOX;
					byte manTemp = map[row][column - 1] == BOX? MANLEFT : MANLEFTONEND;
					
					//人和箱子都往前移动一步
					map[row][column - 2] = boxTemp;
					map[row][column - 1] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//修改坐标
					column--;
				}
			}
			//前方是GRASS或END
			else
			{
				if (map[row][column - 1] == GRASS || map[row][column - 1] == END)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte temp = map[row][column - 1] == END? MANLEFTONEND : MANLEFT;
					map[row][column - 1] = temp;
					map[row][column] = grassOrEnd(map[row][column]);
					column--;
				}
			}
			
			//printMap();
		}
		private void moveRight()
		{
			//前方是墙壁
			if (map[row][column + 1] == WALL)
				return;
			//前方是BOX或BOXONEND
			if (map[row][column + 1] == BOX || map[row][column + 1] == BOXONEND)
			{
				//看前前位
				if (map[row][column + 2] == END || map[row][column + 2] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row][column + 2] == END? BOXONEND : BOX;
					byte manTemp = map[row][column + 1] == BOX? MANRIGHT : MANRIGHTONEND;
					
					//人和箱子都往前移动一步
					map[row][column + 2] = boxTemp;
					map[row][column + 1] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//修改坐标
					column++;
				}
			}
			//前方是GRASS或END
			else
			{
				if (map[row][column + 1] == GRASS || map[row][column + 1] == END)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte temp = map[row][column + 1] == END? MANRIGHTONEND : MANRIGHT;
					map[row][column + 1] = temp;
					map[row][column] = grassOrEnd(map[row][column]);
					column++;
				}
			}
			
			//printMap();
		}
		
		//判断是否过关
		public boolean isFinished()
		{
			for (int i = 0; i < mapRow; i++)
			{
				for (int j = 0; j < mapColumn; j++)
				{
					if (map[i][j] == END || map[i][j] == MANDOWNONEND
						|| map[i][j] == MANUPONEND || map[i][j] == MANLEFTONEND
						|| map[i][j] == MANRIGHTONEND)
						return false;
				}
			}
			return true;
		}
		
		//绘制图形
		public void paint(Graphics g)
		{
			if (iBuffer == null)
			{
				iBuffer = createImage(this.getSize().width, this.getSize().height);
				gBuffer = iBuffer.getGraphics();
			}
			
			if (clear)
			{
				gBuffer.clearRect(0, 0, 12 * pngSize, 12 * pngSize);
				clear = false;
			}
			gBuffer.drawImage(background, 0, 0, width, height, this);
			for (int i = 0; i < mapRow; i++)
				for (int j = 0; j < mapColumn; j++)
				{
					if (map[i][j] != 0)
						gBuffer.drawImage(pic[map[i][j]], leftX + j * pngSize, leftY + i * pngSize, this);
				}
			gBuffer.setColor(Color.LIGHT_GRAY);
			gBuffer.setFont(new Font("微软雅黑", Font.BOLD, 30));
			gBuffer.drawString("现在是第" + String.valueOf(grade + 1) + "关", 25, 60);
			gBuffer.drawString("剩余" + totalTime + "秒", 200, 60);
			g.drawImage(iBuffer, 0, 0, this);
		}
		
		public int getManX()
		{
			return row;
		}
		public int getManY()
		{
			return column;
		}
		public int getGrade()
		{
			return grade;
		}
		public byte[][] getMap(int grade)
		{
			return MapFactory.getMap(grade);
		}
		
		//显示信息对话框
		public void DisplayToast(String str)
		{
			JOptionPane.showMessageDialog(null, str, "提示", JOptionPane.ERROR_MESSAGE);
		}
		
		public void undo()
		{
			if (acceptKey)
			{
				if (list.size() > 0)
				{
					Map priorMap = (Map) list.get(list.size() - 1);
					map = priorMap.getMap();
					row = priorMap.getManX();
					column = priorMap.getManY();
					repaint();
					list.remove(list.size() - 1);
				}
				else
				{
					DisplayToast("不能再撤销了！");
				}
			}
			else
			{
				DisplayToast("此关已完成，不能撤销！");
			}
		}
	
		//获取下一关
		public void nextGrade()
		{
			if (grade >= MapFactory.getCount() - 1)
			{
				DisplayToast("恭喜你通关了！");
				acceptKey = false;
			}
			else
			{
				grade++;
				initMap();
				clear = true;
				repaint();
				acceptKey = true;
			}
		}
		
		public void priorGrade()
		{
			grade--;
			acceptKey = true;
			if (grade < 0)
				grade = 0;
			initMap();
			clear = true;
			repaint();
		}
		
		public void keyPressed(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_UP)
				moveUp();
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				moveDown();
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				moveLeft();
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				moveRight();
			repaint();
			if (isFinished())
			{
				acceptKey =false;
				if (grade == MapFactory.getCount() - 1)
				{
					JOptionPane.showMessageDialog(this, "恭喜通过最后一关");
				}
				else
				{
					//提示进入下一关
					String msg = "恭喜你通过第" + grade + "关！！！\n是否要进入下一关?";
					int type = JOptionPane.YES_NO_CANCEL_OPTION;
					String title = "过关";
					int choice = 0;
					choice = JOptionPane.showConfirmDialog(null, msg, title, type);
					if (choice == 1)
					{
						this.dispose();
					}
					else if (choice == 0)
					{
						//进入下一关
						acceptKey = true;
						nextGrade();
					}
				}
			}	
		}
		
		public void actionPerformed(ActionEvent arg0)
		{
			// TODO Auto-generated method stub
		}
		public void keyReleased(KeyEvent arg0)
		{
			// TODO Auto-generated method stub	
		}
		public void keyTyped(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
		}

		public void mouseClicked(MouseEvent e)
		{	
		}

		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				undo();
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
		public class TimeListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				totalTime--;
				repaint();
				if (totalTime <= 0)
				{
					timer.stop();
					emitString("" + grade);
					GameOver();
				}
			}
			
		}
		public void GameOver()
		{
			timer.stop();
			JOptionPane.showMessageDialog(null, "游戏结束");
			this.dispose();
		}
		public void emitString(String str)
		{
			System.out.println("SCORE@BOX@" + str);
			String Str = "SCORE@BOX@" + str;
			platform_interface.clientSocket.sendGameMsg(Str);
		}
		
	}
	/*public static void main(String[] args)
	{
		new GameFrame();
	}*/
	
	public Box_Pusher(){
		new GameFrame();
	}
}
