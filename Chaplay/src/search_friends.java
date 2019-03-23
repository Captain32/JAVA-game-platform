import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class search_friends extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	
	//判断名字是否在数据库当中
	public boolean friendsFind(String name) {
		data_base db = new data_base();
		db.getConn();
		String sql = "SELECT * FROM user_table WHERE name = ?";
		ResultSet rs = db.executeQuery(sql,new String[] {name});
		try {
			if(rs != null && rs.next())
				return true;//可以登录
			else
				return false;//登录失败
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Create the frame.
	 */
	public search_friends() {
		setTitle("Searching friends");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(550, 270, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(202, 88, 137, 29);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblYourFriendsName = new JLabel("Your friends name: ");
		lblYourFriendsName.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblYourFriendsName.setBounds(62, 88, 130, 29);
		contentPane.add(lblYourFriendsName);
		
		JButton btnSendingRequest = new JButton("Sending request!");
		//发送好友申请
		btnSendingRequest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = textField.getText();
				
				//如果名字为空
				if(name.length() == 0) {
					JOptionPane.showMessageDialog(null, " Name empty!\n Please input your friends name! ",
							" Input Error ", JOptionPane.ERROR_MESSAGE);
				}
				//判断是否已经是好友
				else if(platform_interface.myInfo.getfriends_nameall().indexOf(name)>=0) {
					JOptionPane.showMessageDialog(null, " User " + name + " has been your friend!\n Please check your input! ",
							" Input Error ", JOptionPane.ERROR_MESSAGE);
					
				}
				//在这里判断名字是否在数据库当中
				else if(friendsFind(name)) {
					try {
						//发送申请格式：@friends request from + name
						platform_interface.clientSocket.sendMsg(name, 
								new String("@friends request from ") + platform_interface.myInfo.getname());
						JOptionPane.showMessageDialog(null, " Request sent successfully!\n Please wait for your friends to "
								+ "confirm! ",
								" Request sent successfully ", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(null, " User " + name + " not found!\n Please check your input! ",
							" Input Error ", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSendingRequest.setFont(new Font("HelveticaExt-Normal", Font.BOLD, 14));
		btnSendingRequest.setBounds(121, 181, 188, 29);
		contentPane.add(btnSendingRequest);
	}
}
