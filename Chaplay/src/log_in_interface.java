import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.SwingConstants;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class log_in_interface extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private ImagePanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private ImageIcon icon;
	class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		Dimension d;
        Image image;

        public ImagePanel(Dimension d, Image image) {
            super();
            this.d = d;
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, d.width, d.height, this);
            //ImageFrame.this.instance().repaint();
        }
    }
	
	public void addImageByRepaint() {
        contentPane = new ImagePanel(new Dimension(500, 305), icon.getImage());
        contentPane.setLayout(null);
        setContentPane(contentPane);

        addComponents();
        setVisible(true);
    }

	private void addComponents() {
		JLabel lblNickname = new JLabel("Nickname:");
		lblNickname.setBounds(100, 50, 113, 19);
		//lblNickname.setLocation(100, 150);
		contentPane.add(lblNickname);
		lblNickname.setBackground(Color.WHITE);
		lblNickname.setForeground(new Color(0, 0, 255));
		lblNickname.setHorizontalAlignment(SwingConstants.CENTER);
		lblNickname.setFont(new Font("宋体", Font.PLAIN, 16));
		
		textField = new JTextField();
		textField.setBounds(205, 50, 126, 30);
		contentPane.add(textField);
		textField.setColumns(20);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(100, 95, 113, 19);
		contentPane.add(lblPassword);
		lblPassword.setForeground(new Color(0, 0, 255));
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setFont(new Font("宋体", Font.PLAIN, 16));
		
		passwordField = new JPasswordField();
		passwordField.setBounds(205, 95, 126, 30);
		contentPane.add(passwordField);
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					log_in();
			}
		});
		passwordField.setColumns(20);
		
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.setBounds(170, 150, 158, 30);
		contentPane.add(btnLogIn);
		btnLogIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log_in();
			}
		});
		btnLogIn.setFont(new Font("宋体", Font.PLAIN, 16));
		btnLogIn.setForeground(new Color(0, 0, 255));
		
		JButton btnRegisterNow = new JButton("Register Now!");
		btnRegisterNow.setBounds(170, 190, 158, 30);
		contentPane.add(btnRegisterNow);
		btnRegisterNow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				register_interface reg = new register_interface();
				reg.setVisible(true);
			}
		});
		btnRegisterNow.setForeground(new Color(255, 0, 255));
		btnRegisterNow.setFont(new Font("宋体", Font.PLAIN, 16));

    }
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					log_in_interface frame = new log_in_interface();
					frame.setVisible(true);
					frame.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	void exit_() {
		this.dispose();
	}
	/**
	 * Create the frame.
	 */
	
	//检查登陆是否成功
	int login_succ(String name,String password) {
		int tl = name.length(),pl = password.length();
		if(tl == 0 || pl == 0) 
			return 0;//账号或密码为空
		/*如果还有其他的无法成功的情况*/
		data_base db = new data_base();
		db.getConn();
		String sql = "SELECT * FROM user_table WHERE name = ? AND password = ?";
		ResultSet rs = db.executeQuery(sql,new String[] {name,password});
		try {
			if(rs != null && rs.next())
				return -1;//可以登录
			else
				return 1;//登录失败
		} catch (SQLException e) {
			return 1;
		}
	}
	
	void log_in() {
		//在这里获取用户登陆信息，应当是要从数据库中获取的，注意需要判断信息是否正常获取
		//如果没有正常获取，需要重新进行登陆操作，并且弹出对话框告知具体的错误信息
		
		//以下为具体信息
		String name = textField.getText();//知道了账户信息用作查询信息的索引
		@SuppressWarnings("deprecation")
		String password = passwordField.getText();
		
		//在这里从数据库中获取相应的信息
		
		//这个到时候得看到底怎么获取本机的外网IP
		String IP = "localhost";
		
		//如果成功登陆
		if(login_succ(name, password) == -1) {
			user_data dao = new user_data();
			dao.getConn();
			person_info user=new person_info();
			user = dao.getUserByName(name);
			
			//进入主界面
			//创建client_socket
			ChatClient clientSocket = new ChatClient(IP);
			if(clientSocket.getclientSocket() != null && clientSocket.start(name)) {
				//如果成功登陆
				platform_interface platform;
				try {
					platform = new platform_interface(clientSocket, user);
					platform.setVisible(true);
					exit_();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			else if(clientSocket.getclientSocket() == null) {
				JOptionPane.showMessageDialog(null, " Connect refused!\n Please check your Internet connection! ",
						" Internet Error ", JOptionPane.ERROR_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, " Account already logged in!\nPlease check the "
						+ "security of your account! ", " Log In Error ", JOptionPane.ERROR_MESSAGE);
			}
		}
		//登陆失败
		else {
			JOptionPane.showMessageDialog(null, " Log in failed!\n Please check your account! ",
					" Log in Error ", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public log_in_interface() {
		setTitle("Welcome to ChaPlay");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(550, 270, 500, 305);
		getContentPane().setLayout(null);
		icon = new ImageIcon("images/登陆.jpg");
        icon.getImage();
		this.addImageByRepaint();
	}
}
