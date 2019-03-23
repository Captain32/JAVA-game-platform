import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ScrollPaneConstants;

public class platform_interface extends JFrame {
	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JLabel friend_background;
	public static JPanel panel;
	public static JScrollPane scrollPane;
	private static JLabel lblNewLabel_1;
	public static JPanel main_interface;
	private static JPanel pic_pane;
	private static JTextPane send_msg_1;
	private static JLabel lblEmoji_1;
	private static JButton btnSend;
	private static JPanel panel_2;
	private static JPanel panel_3;
	private static JLabel chat_with;
	private static JMenuBar menuBar;
	private static JMenu mnAboutUs;
	private static JMenuItem mntmSwitchAccount;
	private static JSeparator separator_1;
	private static JMenuItem mntmExit;
	private static JSeparator separator_2;
	private static JMenu mnNewMenu;
	private static JMenuItem mntmAboutUs;
	private static JSeparator separator_3;
	private static JPanel game_panel;
	private static JPanel game1;
	private JLabel Dandantang_name;
	private JPanel Dandantang_ranking;
	static JLabel Ddt_Ranking1;
	static JLabel Ddt_player1;
	static JLabel Ddt_Ranking2;
	static JLabel Ddt_player2;
	static JLabel Ddt_Ranking3;
	static JLabel Ddt_player3;
	private JButton enter_Dandantang;
	private JPanel game2;
	private JLabel DuiDuiPeng_name;
	private JButton enter_Ddp;
	static JPanel Ddp_ranking;
	static JLabel Ddp_Ranking1;
	static JLabel Ddp_player1;
	static JLabel Ddp_Ranking2;
	static JLabel Ddp_player2;
	static JLabel Ddp_Ranking3;
	static JLabel Ddp_player3;
	
	private JPanel game3;
	private JLabel Tuixiangzi_name;
	private JButton enter_Txz;
	private JPanel Txz_ranking;
	static JLabel Txz_Ranking1;
	static JLabel Txz_player1;
	static JLabel Txz_Ranking2;
	static JLabel Txz_player2;
	static JLabel Txz_Ranking3;
	static JLabel Txz_player3;
	private JPanel game4;
	private JLabel Leidian_name;
	private JButton enter_Ld;
	private JPanel Ld_ranking;
	static JLabel Ld_Ranking1;
	static JLabel Ld_player1;
	static JLabel Ld_Ranking2;
	static JLabel Ld_player2;
	static JLabel Ld_Ranking3;
	static JLabel Ld_player3;

	public static ChatClient clientSocket;
	private static platform_self self_info;
	public static person_info myInfo;
	public static ArrayList<friendList> fList = new ArrayList<>();
	public static ArrayList<RandomAccessFile> fileList = new ArrayList<>();//记录聊天记录
	private static ImageIcon icon[] = new ImageIcon[3]; 
	public static int friend_current;
	public static List<String> waitingList = new ArrayList<>();//申请等待的名单
	private static int width, height;
	
	//联网游戏弹弹堂
	public static DDtank curDDtank;
	
	//暂时退出，可能是切换到账号注册界面
	void exit_() {
		this.dispose();
	}
	
	void Enabled() {
		//this.enable();
	}
	
	void Reabled() {
		//this.disable();
	}
	
	//初始化界面
	public void initialize(person_info myInfo) throws Exception {
		for(int i = 0;i < 3;++i) {
			char x = (char)(i + 'A');
			icon[i] = new ImageIcon(new String("images/" + x + ".jpg"));
		}
		
		platform_interface.myInfo = myInfo;
		
		setExtendedState(this.getExtendedState() | JFrame.EXIT_ON_CLOSE);  //最大化
		setSize(Toolkit.getDefaultToolkit().getScreenSize()); 
		setTitle("Enjoy your Chaplay!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 1500, 1200);
		width = 1500;
		height = 1200;
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setPreferredSize(new Dimension(340, platform_interface.HEIGHT - 10));
		panel.setLayout(new BorderLayout(0, 0));
		
		//主界面，到时候需要在这里设立广播站，播报最新的排名消息，和上线消息
		main_interface = new JPanel();
		contentPane.add(main_interface, BorderLayout.CENTER);

		ImageIcon icon = new ImageIcon("./images/logo.png");
		Image img = icon.getImage();
        img = img.getScaledInstance(this.getWidth() - 680, 150, Image.SCALE_DEFAULT);  
        //System.out.println(this.getWidth());1500
        icon.setImage(img);
		main_interface.setLayout(new BorderLayout(0, 0));
		
		pic_pane = new JPanel();
		main_interface.add(pic_pane, BorderLayout.NORTH);
		pic_pane.setPreferredSize(new Dimension(this.getWidth() - 680, 150));
		/*System.out.println(this.getWidth() - 680);
		System.out.println(this.getHeight());*/
		JLabel lblNewLabel = new JLabel("");
		pic_pane.add(lblNewLabel);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(340, 0,pic_pane.getWidth(), pic_pane.getHeight());
		lblNewLabel.setIcon(icon);//插入图片
		
		/*
		 //测试，添加好友列表
		friend_column fc = new friend_column(0, "holmosaint", 0);
		fc.lb_mood.setBackground(Color.WHITE);
		*/

		//初始化聊天窗口
		init_chat();
		
		//初始化好友界面
		init_friendList();

		//初始化菜单
		init_menu();
		
		//初始化游戏菜单
		init_game();
		
		contentPane.setVisible(true);
	}
	
	//初始化菜单
	public void init_menu() {
		//菜单栏
		menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		
		mnAboutUs = new JMenu("Account");
		menuBar.add(mnAboutUs);
		
		//切换账户
		mntmSwitchAccount = new JMenuItem("Switch account");
		mntmSwitchAccount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int k = JOptionPane.showConfirmDialog(null, "Are you sure to switch an account?", "Exit", JOptionPane.YES_NO_OPTION);
				//退出操作，这里我不知道是不是应该进行保存信息
				if(k == JOptionPane.YES_OPTION) {
					log_in_interface frame = new log_in_interface();
					frame.setVisible(true);
					exit_();
				}
			}
		});
		mnAboutUs.add(mntmSwitchAccount);
		
		//分隔符
		separator_1 = new JSeparator();
		mnAboutUs.add(separator_1);
		
		mntmExit = new JMenuItem("Exit");
		//点击工具栏的Exit键之后的操作
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					int k = JOptionPane.showConfirmDialog(null, "Are you sure to exit?", "Exit", JOptionPane.YES_NO_OPTION);
					//退出操作，这里我不知道是不是应该进行保存信息
					if(k == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
			}	
		});	
		mnAboutUs.add(mntmExit);
				
		//分隔符
		separator_2 = new JSeparator();
		mnAboutUs.add(separator_2);
		
		mnAddfriends = new JMenu("Add friends");
		menuBar.add(mnAddfriends);
		
		mntmSearch = new JMenuItem("Search");
		//点击进行好友搜索
		mntmSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				search_friends sf = new search_friends();
				sf.setVisible(true);
			}
		});
		mnAddfriends.add(mntmSearch);
		
		mnNewMenu = new JMenu("Other");
		menuBar.add(mnNewMenu);
		
		mntmAboutUs = new JMenuItem("About us");
		//点击about us之后的操作
		mntmAboutUs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Enabled();
				AboutUS_interface ai = new AboutUS_interface();
				ai.setVisible(true);
				//Reabled();
				//System.out.println("ok");
			}
		});
		mnNewMenu.add(mntmAboutUs);
		
		//分隔符
		separator_3 = new JSeparator();
		mnNewMenu.add(separator_3);
		
		mnMessage = new JMenu("Message");
		menuBar.add(mnMessage);
		
		mntmFriendsRequest = new JMenuItem("Friends request");
		//查看好友申请列表
		mntmFriendsRequest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//如果没有好友申请hhh
				if(waitingList.isEmpty()) {
					JOptionPane.showMessageDialog(null, " No requests!\n Nobody wants to be friends with you hhh! ",
							" Be more attractive ", JOptionPane.ERROR_MESSAGE);
				}
				else {
					//呈现申请的界面
					friendsRequest fR = new friendsRequest();
					fR.setVisible(true);
					
					platform_interface.this.repaint();
				}
			}
		});
		mnMessage.add(mntmFriendsRequest);
	}	
	
	//初始化游戏板块
	public void init_game() {
		game_panel = new JPanel();
		game_panel.setPreferredSize(new Dimension(340, 150));
		contentPane.add(game_panel, BorderLayout.EAST);
		game_panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		//弹弹堂
		game1 = new JPanel();
		game_panel.add(game1);
		game1.setLayout(new BorderLayout(0, 0));
		
		Dandantang_name = new JLabel("\u5F39 \u5F39 \u5802");
		Dandantang_name.setFont(new Font("微软雅黑", Font.BOLD, 18));
		game1.add(Dandantang_name, BorderLayout.NORTH);
		
		Dandantang_ranking = new JPanel();
		game1.add(Dandantang_ranking, BorderLayout.CENTER);
		Dandantang_ranking.setLayout(new GridLayout(6, 1, 0, 0));
		
		Ddt_Ranking1 = new JLabel("1st place");
		Ddt_Ranking1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_Ranking1);
		
		Ddt_player1 = new JLabel("waiting for you");
		Ddt_player1.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddt_player1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_player1);
		
		Ddt_Ranking2 = new JLabel("2nd place");
		Ddt_Ranking2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_Ranking2);
		
		Ddt_player2 = new JLabel("waiting for you");
		Ddt_player2.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddt_player2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_player2);
		
		Ddt_Ranking3 = new JLabel("3rd place");
		Ddt_Ranking3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_Ranking3);
		
		Ddt_player3 = new JLabel("waiting for you");
		Ddt_player3.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddt_player3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Dandantang_ranking.add(Ddt_player3);
		
		enter_Dandantang = new JButton("ENTER THE GAME!");
		enter_Dandantang.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				curDDtank = new DDtank();
			}
		});
		enter_Dandantang.setFont(new Font("Showcard Gothic", Font.PLAIN, 18));
		game1.add(enter_Dandantang, BorderLayout.SOUTH);
		
		
		//对对碰
		game2 = new JPanel();
		game_panel.add(game2);
		game2.setLayout(new BorderLayout(0, 0));
		
		DuiDuiPeng_name = new JLabel("\u5BF9 \u5BF9 \u78B0");
		DuiDuiPeng_name.setFont(new Font("微软雅黑", Font.BOLD, 18));
		game2.add(DuiDuiPeng_name, BorderLayout.NORTH);
		
		enter_Ddp = new JButton("ENTER THE GAME!");
		enter_Ddp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new Touch_animation();
			}
		});
		enter_Ddp.setFont(new Font("Showcard Gothic", Font.PLAIN, 18));
		game2.add(enter_Ddp, BorderLayout.SOUTH);
		
		Ddp_ranking = new JPanel();
		game2.add(Ddp_ranking, BorderLayout.CENTER);
		Ddp_ranking.setLayout(new GridLayout(6, 1, 0, 0));
		
		Ddp_Ranking1 = new JLabel("1st place");
		Ddp_Ranking1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_Ranking1);
		
		Ddp_player1 = new JLabel("waiting for you");
		Ddp_player1.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddp_player1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_player1);
		
		Ddp_Ranking2 = new JLabel("2nd place");
		Ddp_Ranking2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_Ranking2);
		
		Ddp_player2 = new JLabel("waiting for you");
		Ddp_player2.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddp_player2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_player2);
		
		Ddp_Ranking3 = new JLabel("3rd place");
		Ddp_Ranking3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_Ranking3);
		
		Ddp_player3 = new JLabel("waiting for you");
		Ddp_player3.setHorizontalAlignment(SwingConstants.RIGHT);
		Ddp_player3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ddp_ranking.add(Ddp_player3);
		
		//推箱子
		game3 = new JPanel();
		game_panel.add(game3);
		game3.setLayout(new BorderLayout(0, 0));
		
		Tuixiangzi_name = new JLabel("\u63A8 \u7BB1 \u5B50");
		Tuixiangzi_name.setFont(new Font("微软雅黑", Font.BOLD, 18));
		game3.add(Tuixiangzi_name, BorderLayout.NORTH);
		
		enter_Txz = new JButton("ENTER THE GAME!");
		enter_Txz.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new Box_Pusher();
			}
		});
		enter_Txz.setFont(new Font("Showcard Gothic", Font.PLAIN, 18));
		game3.add(enter_Txz, BorderLayout.SOUTH);
		
		Txz_ranking = new JPanel();
		game3.add(Txz_ranking, BorderLayout.CENTER);
		Txz_ranking.setLayout(new GridLayout(6, 1, 0, 0));
		
		Txz_Ranking1 = new JLabel("1st place");
		Txz_Ranking1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_Ranking1);
		
		Txz_player1 = new JLabel("waiting for you");
		Txz_player1.setHorizontalAlignment(SwingConstants.RIGHT);
		Txz_player1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_player1);
		
		Txz_Ranking2 = new JLabel("2nd place");
		Txz_Ranking2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_Ranking2);
		
		Txz_player2 = new JLabel("waiting for you");
		Txz_player2.setHorizontalAlignment(SwingConstants.RIGHT);
		Txz_player2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_player2);
		
		Txz_Ranking3 = new JLabel("3rd place");
		Txz_Ranking3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_Ranking3);
		
		Txz_player3 = new JLabel("waiting for you");
		Txz_player3.setHorizontalAlignment(SwingConstants.RIGHT);
		Txz_player3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Txz_ranking.add(Txz_player3);
		
		//雷电
		game4 = new JPanel();
		game_panel.add(game4);
		game4.setLayout(new BorderLayout(0, 0));
		
		Leidian_name = new JLabel("\u96F7 \u7535");
		Leidian_name.setFont(new Font("微软雅黑", Font.BOLD, 18));
		game4.add(Leidian_name, BorderLayout.NORTH);
		
		enter_Ld = new JButton("ENTER THE GAME!");
		enter_Ld.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new Thunder();
			}
		});
		enter_Ld.setFont(new Font("Showcard Gothic", Font.PLAIN, 18));
		game4.add(enter_Ld, BorderLayout.SOUTH);
		
		Ld_ranking = new JPanel();
		game4.add(Ld_ranking, BorderLayout.CENTER);
		Ld_ranking.setLayout(new GridLayout(6, 1, 0, 0));
		
		Ld_Ranking1 = new JLabel("1st place");
		Ld_Ranking1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_Ranking1);
		
		Ld_player1 = new JLabel("waiting for you");
		Ld_player1.setHorizontalAlignment(SwingConstants.RIGHT);
		Ld_player1.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_player1);
		
		Ld_Ranking2 = new JLabel("2nd place");
		Ld_Ranking2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_Ranking2);
		
		Ld_player2 = new JLabel("waiting for you");
		Ld_player2.setHorizontalAlignment(SwingConstants.RIGHT);
		Ld_player2.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_player2);
		
		Ld_Ranking3 = new JLabel("3rd place");
		Ld_Ranking3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_Ranking3);
		
		Ld_player3 = new JLabel("waiting for you");
		Ld_player3.setHorizontalAlignment(SwingConstants.RIGHT);
		Ld_player3.setFont(new Font("Brush Script MT", Font.BOLD, 26));
		Ld_ranking.add(Ld_player3);
	}
	
	private static AffineTransform atf = new AffineTransform();

	private static FontRenderContext frc = new FontRenderContext(atf, true, true);
	private JPanel msgSendPane;
	public static JScrollPane msgScrollPane;
	private JMenu mnAddfriends;
	private JMenuItem mntmSearch;
	public static JMenu mnMessage;
	public static JMenuItem mntmFriendsRequest;
	public static friendsPane friends;
	private static JPanel panel_1;
	public static JPanel msgPanel;
	
	//获取字符串的高度
	public static int getStringHeight(String str, Font font) {
        if (str == null || str.isEmpty() || font == null) {
            return 0;
        }
        return (int) font.getStringBounds(str, frc).getHeight();

    }
	
	//获取字符串的宽度
    public static int getStringWidth(String str, Font font) {
        if (str == null || str.isEmpty() || font == null) {
            return 0;
        }
        return (int) font.getStringBounds(str, frc).getWidth();
    }
	
	/***
     * 添加文本消息，拥有气泡功能
     * 
     * @param messages
     *            要添加的消息
     * @param fla
     *            是否是自己发送的消息
     */
    public static void addTextMessage(String messages, int fla, int pic, int count) {
    	
    	int width = 700;//设置最大宽度
        Font font = new Font("宋体", Font.BOLD, 40);

        // 获取字符串的高度
        int sHeight = getStringHeight(messages, font);
        System.out.println("字符串取得的高度为" + sHeight);

        // 获取字符串的宽度
        int sWidth = getStringWidth(messages, font);
        System.out.println("字符串取得的宽度为" + sWidth);

        // 存储分行的字符串
        ArrayList<String> str = new ArrayList<String>();
        if (sWidth > width - 150) {
            int beginIndex=0;
            int endIndex=1;
            while(endIndex < messages.length()){
            	String s=messages.substring(beginIndex,endIndex);
            	if(getStringWidth(s, font) > (width - 100) || endIndex == messages.length() - 1) {
            		str.add(messages.substring(beginIndex,endIndex-1));
                    beginIndex=endIndex-1;            
                }
                endIndex++;
            }    
        } 
        else
        	str.add(messages);
        Dimension d = new Dimension(platform_interface.width - 680, sHeight * (str.size() + 1) + 30);
        JBubble jbubble;
        if(fla == 1)
        	jbubble = new JBubble(sHeight, getStringWidth(str.get(0), font), str, fla, 
        			icon[myInfo.getpic()].getImage(), d);
        else
        	jbubble = new JBubble(sHeight, getStringWidth(str.get(0), font), str, fla, 
        			icon[pic].getImage(), d);
        msgPanel.add(jbubble);
        //msgHistory.insertComponent(jbubble);
        //msgHistory.setCaretPosition(count);
        msgPanel.repaint();
    }
	
	//好友列表，记录好友的信息
	static class friendList extends person_info{
		//好友的序号
		int index;
		//消息的记录
		ArrayList<String> msgList = new ArrayList<>();
		//消息的标记（自己发的，还是好友发的）
		ArrayList<Integer> usrList = new ArrayList<>();
		
		friendList(String name, int gender, int pic, String signature, int index){ 
			super(name, gender, signature, pic);
			this.index = index;
		}
	}
	
	//在用户点击了好友的时候，能够把聊天记录甩上去
	public static void setChat_window(int index) {
		//清楚之前所有的聊天内容
		//msgHistory.setText("");
		msgPanel.removeAll();
		msgPanel.repaint();
		chat_with.setText("Chatting with " + fList.get(index).getname());
		System.out.println("hello name: " + fList.get(index).getname());
		friend_current = index;
		//添加新的聊天信息
		for(int i = 0;i < fList.get(index).msgList.size();++i) {
			addTextMessage(fList.get(index).msgList.get(i), (int)fList.get(index).usrList.get(i), 
					 fList.get(index).getpic(), i);
		}
	}
	
	//创建好友列表
	@SuppressWarnings("deprecation")
	public static void init_friendList() throws Exception {
		ImageIcon background = new ImageIcon("images/friendsList.jpg");
		friend_background = new JLabel(background);  
		friend_background.setBounds(0, 0,  background.getIconWidth(), background.getIconHeight());
		System.out.println("my friend_num is " + myInfo.getfriends_num());
		
		panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		//创建自我信息
		self_info = new platform_self(myInfo);
		panel_1.add(self_info, BorderLayout.NORTH);
		self_info.setPreferredSize(new Dimension(340, 150));
		
		lblNewLabel_1 = new JLabel("Friends List");
		panel_1.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setPreferredSize(new Dimension(340, 15));
		
				
		friends = new friendsPane();
		friends.setPreferredSize(new Dimension(340, platform_interface.height - 150));
		//System.out.println("height: " + height);是1200
		
				
		scrollPane = new JScrollPane(friends);
		panel.add(scrollPane, BorderLayout.CENTER);
		//置于底层
		scrollPane.add(friend_background, new Integer(Integer.MIN_VALUE));
		//设置为透明的  
		//scrollPane.setOpaque(false); 
		//一定要设置分布为null，否则出现的好友列表没有办法首次出现的时候就能刷新
		//scrollPane.setLayout(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setPreferredSize(new Dimension(340, platform_interface.height - 150));
		scrollPane.setVisible(true);
		friends.setVisible(true);
		scrollPane.repaint();
		panel.setVisible(true);
		

		//添加好友到好友列表
		for(int i = 0;i < myInfo.getfriends_num();++i) {
			//这里需要获取
			user_data dao = new user_data();
			dao.getConn();
			person_info temp_user=new person_info();
			temp_user=dao.getUserByName(myInfo.getfriends_name(i));
			fList.add(new friendList(temp_user.getname(), temp_user.getgender(),temp_user.getpic(),temp_user.getsignature(), i));
			fileList.add(new RandomAccessFile(temp_user.getname() + ".txt", "rw"));
			RandomAccessFile ras = null;
			try {
				//添加聊天记录
				ras = fileList.get(i);
				String msg;
				while((msg = ras.readLine()) != null) {
					String temp = msg.substring(0, 1);
					msg = msg.substring(2);
					if(temp.equals("0")) {
						fList.get(i).usrList.add(0);
					}
					else {
						fList.get(i).usrList.add(1);
					}
					fList.get(i).msgList.add(msg);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			friends.addFriends(temp_user.getname(),temp_user.getpic(),temp_user.getsignature(), i);//增加好友条目
		}
		
		//初始化聊天界面和第一个好友进行聊天
		if(myInfo.getfriends_num() > 0) {
			setChat_window(0);
			friend_current = 0;
		}
	}
	
	//添加好友到左侧的列表，并且增加鼠标的点击事件
	public static void addfriend(int pic, String name, String sig, int index) {
		friends.addFriends(name, pic, sig, index);
		friends.repaint();
	}
	
	//初始化聊天界面
	public void init_chat() {
		
		panel_3 = new JPanel();
		main_interface.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));

		msgPanel = new JPanel();
		//panel_3.add(msgPanel, BorderLayout.SOUTH);
		msgPanel.setPreferredSize(new Dimension(main_interface.getWidth(), 1000000000));
		
		chat_with = new JLabel("Chatting with ...");
		panel_3.add(chat_with, BorderLayout.NORTH);
		msgScrollPane = new JScrollPane(msgPanel);
		msgScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		msgScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel_3.add(msgScrollPane, BorderLayout.CENTER);
		//System.out.println("height:" + panel_3.getHeight());
		msgScrollPane.setAutoscrolls(true);
		//msgHistory.setBackground(Color.WHITE);
		msgPanel.setBackground(Color.WHITE);
		
		
		/*
		 * 发送信息界面
		 */
		msgSendPane = new JPanel();
		main_interface.add(msgSendPane, BorderLayout.SOUTH);
		msgSendPane.setLayout(new BorderLayout(0, 0));
		send_msg_1 = new JTextPane();
		send_msg_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//点击回车发送消息
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					String msg = send_msg_1.getText();
					//在自己的聊天界面添加信息
					fList.get(friend_current).msgList.add(msg);
					fList.get(friend_current).usrList.add(1);
					try {
						fileList.get(friend_current).write(new String(1 + ":" + msg + "\r\n").getBytes());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					addTextMessage(msg, 1, myInfo.getpic(), fList.get(friend_current).msgList.size());
					send_msg_1.setText("");
					//通过client发送信息
					clientSocket.sendMsg(fList.get(friend_current).getname(), msg);
				}
			}
		});
		send_msg_1.setFont(new Font("Charlemagne Std", Font.PLAIN, 14));
		msgSendPane.add(send_msg_1, BorderLayout.CENTER);
		send_msg_1.setPreferredSize(new Dimension(pic_pane.getWidth(), 200));
		send_msg_1.setLayout(new BorderLayout(0, 0));
		
		lblEmoji_1 = new JLabel("Emoji");
		msgSendPane.add(lblEmoji_1, BorderLayout.NORTH);
		lblEmoji_1.setFont(new Font("Franklin Gothic Heavy", Font.PLAIN, 14));
		lblEmoji_1.setHorizontalAlignment(SwingConstants.LEFT);
		
		panel_2 = new JPanel();
		msgSendPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setPreferredSize(new Dimension(msgSendPane.getWidth(), 25));
		panel_2.setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//点击send发送消息
				String msg = send_msg_1.getText();
				//在自己的聊天界面添加信息
				fList.get(friend_current).msgList.add(msg);
				fList.get(friend_current).usrList.add(1);
				RandomAccessFile ras = fileList.get(friend_current);
				if(ras == null) {
					System.out.println("friends not found!");//出现错误
					return;
				}
				try {
					ras.seek(ras.length());
					ras.write(new String(1 + ":").getBytes());
					ras.write(msg.getBytes());//写入聊天记录
					ras.write("\r\n".getBytes());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				addTextMessage(msg, 1, myInfo.getpic(), fList.get(friend_current).msgList.size());
				send_msg_1.setText("");
				//通过client发送信息
				clientSocket.sendMsg(fList.get(friend_current).getname(), msg);
			}
		});
		btnSend.setFont(new Font("Tekton Pro Ext", Font.PLAIN, 16));
		panel_2.add(btnSend);
		btnSend.setBounds(761, 2, 84, 25);
	}	
		
	public platform_interface(ChatClient clientSocket, person_info myInfo) throws Exception {
		platform_interface.clientSocket = clientSocket;
		
		//在这里需要得到好友的数量
		myInfo.setfriends_num(myInfo.getfriends_num());
		
		initialize(myInfo);//界面的初始化
	}
}
