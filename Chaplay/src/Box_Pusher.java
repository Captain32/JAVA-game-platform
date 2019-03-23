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
			else //Ĭ��ѡ��һ��
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
		//��ɫ������
		//X����������Y��������
		int manX = 0;
		int manY = 0;
		byte map[][];
		int grade;
		//������췽�����ڳ�������
		//��������ֻ��Ҫ�˵�λ�ú͵�ͼ�ĵ�ǰ״̬
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
		
		//������췽�����ڱ������
		//�ָ���ͼ�������Ҫ�˵�λ�ã���ͼ�ĵ�ǰ״̬�͹ؿ���
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
		//����
		private Image iBuffer = null;
		private Graphics gBuffer = null;
		
		//�������
		private Timer timer = new Timer(1000, new TimeListener()); //��ʱ��������ʱ�������
		private int totalTime;
		private final int maxTime = 6; //һ�������
		private int grade = 0;
		//row, column �����˵��кš��к�
		//leftX, leftY�������Ͻ�ͼƬ��λ�ã�����ͼƬ��(0,0)���꿪ʼ
		private int row = 7, column = 7, leftX = 0, leftY = 0;
		//���ص�ͼ��������
		private int mapRow = 0, mapColumn = 0;
		//width, height������Ļ�Ĵ�С
		private int width = 0, height = 0;
		private boolean acceptKey = true;
		private boolean clear = false;
		//�������õ���ͼƬ
		private Image pic[] = null;
		private byte[][] map = null;
		private ArrayList<Map> list = new ArrayList<Map>();
		private Image background = null;
		
		//һЩ����
		final byte BLANK = 0, WALL = 1, BOX = 2, BOXONEND = 3, END = 4, MANDOWN = 5,
				MANLEFT = 6, MANRIGHT = 7, MANUP = 8, GRASS = 9,
				MANDOWNONEND = 10, MANLEFTONEND = 11, MANRIGHTONEND = 12,
				MANUPONEND = 13;
		//ͼƬ�Ĵ�С������������
		final int pngSize = 64;
		
		public GameFrame()
		{
			super("��������Ϸ");
			setSize(10 * pngSize, 10 * pngSize);
			setVisible(true);
			setResizable(false);
			setLocation(300, 20);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container cont = getContentPane();
			cont.setLayout(null);
			cont.setBackground(Color.black);
			//���ʼ��13��ͼƬ
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
		
		//��ʼ������grade��Ϸ��ͼ����ջ�����Ϣ�б�list,����getMapSizeAndPosition()��ȡ��Ϸ�����С����ʵ��Ϸ�����Ͻ�λ��leftX leftY
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
		
		//����Ҫ��ʾ��ͼƬ
		public void getPic()
		{
			pic = new Image[14];
			for (int i = 0; i <= 13; i++)
			{
				pic[i] = Toolkit.getDefaultToolkit().getImage(".\\Box_material\\pic" + i + ".jpg");
			}
			background = Toolkit.getDefaultToolkit().getImage(".\\Box_material\\background.jpg");
		}
		
		//�ж��������ڵ�λ����GRASS����END
		public byte grassOrEnd(byte man)
		{
			byte result = GRASS;
			if (man == MANDOWNONEND || man == MANUPONEND || man == MANRIGHTONEND || man == MANLEFTONEND)
				result = END;
			return result;
		}
		
		//�����ĸ������������ƶ�
		private void moveUp()
		{
			//ǰ����ǽ��
			if (map[row - 1][column] == WALL)
				return;
			//ǰ����BOX��BOXONEND
			if (map[row - 1][column] == BOX || map[row - 1][column] == BOXONEND)
			{
				//��ǰǰλ
				if (map[row - 2][column] == END || map[row - 2][column] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row - 2][column] == END? BOXONEND : BOX;
					byte manTemp = map[row - 1][column] == BOX? MANUP : MANUPONEND;
					
					//�˺����Ӷ���ǰ�ƶ�һ��
					map[row - 2][column] = boxTemp;
					map[row - 1][column] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//�޸�����
					row--;
				}
			}
			//ǰ����GRASS��END
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
			//ǰ����ǽ��
			if (map[row + 1][column] == WALL)
				return;
			//ǰ����BOX��BOXONEND
			if (map[row + 1][column] == BOX || map[row + 1][column] == BOXONEND)
			{
				//��ǰǰλ
				if (map[row + 2][column] == END || map[row + 2][column] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row + 2][column] == END? BOXONEND : BOX;
					byte manTemp = map[row + 1][column] == BOX? MANDOWN : MANDOWNONEND;
					
					//�˺����Ӷ���ǰ�ƶ�һ��
					map[row + 2][column] = boxTemp;
					map[row + 1][column] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//�޸�����
					row++;
				}
			}
			//ǰ����GRASS��END
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
			//ǰ����ǽ��
			if (map[row][column - 1] == WALL)
				return;
			//ǰ����BOX��BOXONEND
			if (map[row][column - 1] == BOX || map[row][column - 1] == BOXONEND)
			{
				//��ǰǰλ
				if (map[row][column - 2] == END || map[row][column - 2] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row][column - 2] == END? BOXONEND : BOX;
					byte manTemp = map[row][column - 1] == BOX? MANLEFT : MANLEFTONEND;
					
					//�˺����Ӷ���ǰ�ƶ�һ��
					map[row][column - 2] = boxTemp;
					map[row][column - 1] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//�޸�����
					column--;
				}
			}
			//ǰ����GRASS��END
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
			//ǰ����ǽ��
			if (map[row][column + 1] == WALL)
				return;
			//ǰ����BOX��BOXONEND
			if (map[row][column + 1] == BOX || map[row][column + 1] == BOXONEND)
			{
				//��ǰǰλ
				if (map[row][column + 2] == END || map[row][column + 2] == GRASS)
				{
					Map currMap = new Map(row, column, map);
					list.add(currMap);
					byte boxTemp = map[row][column + 2] == END? BOXONEND : BOX;
					byte manTemp = map[row][column + 1] == BOX? MANRIGHT : MANRIGHTONEND;
					
					//�˺����Ӷ���ǰ�ƶ�һ��
					map[row][column + 2] = boxTemp;
					map[row][column + 1] = manTemp;
					map[row][column] = grassOrEnd(map[row][column]);
					//�޸�����
					column++;
				}
			}
			//ǰ����GRASS��END
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
		
		//�ж��Ƿ����
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
		
		//����ͼ��
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
			gBuffer.setFont(new Font("΢���ź�", Font.BOLD, 30));
			gBuffer.drawString("�����ǵ�" + String.valueOf(grade + 1) + "��", 25, 60);
			gBuffer.drawString("ʣ��" + totalTime + "��", 200, 60);
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
		
		//��ʾ��Ϣ�Ի���
		public void DisplayToast(String str)
		{
			JOptionPane.showMessageDialog(null, str, "��ʾ", JOptionPane.ERROR_MESSAGE);
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
					DisplayToast("�����ٳ����ˣ�");
				}
			}
			else
			{
				DisplayToast("�˹�����ɣ����ܳ�����");
			}
		}
	
		//��ȡ��һ��
		public void nextGrade()
		{
			if (grade >= MapFactory.getCount() - 1)
			{
				DisplayToast("��ϲ��ͨ���ˣ�");
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
					JOptionPane.showMessageDialog(this, "��ϲͨ�����һ��");
				}
				else
				{
					//��ʾ������һ��
					String msg = "��ϲ��ͨ����" + grade + "�أ�����\n�Ƿ�Ҫ������һ��?";
					int type = JOptionPane.YES_NO_CANCEL_OPTION;
					String title = "����";
					int choice = 0;
					choice = JOptionPane.showConfirmDialog(null, msg, title, type);
					if (choice == 1)
					{
						this.dispose();
					}
					else if (choice == 0)
					{
						//������һ��
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
			JOptionPane.showMessageDialog(null, "��Ϸ����");
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
