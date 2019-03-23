import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

public class requestItem extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JButton jButton;
	private JLabel lblRefuse;
	private JLabel lblAgree;
	private JLabel lblRequestFrom;
	private JLabel lblName;
	private JButton btnAgree;
	private JButton btnRefuse;
	char pic;
	/*
	 * 形式为：Label("Request from ") + Label("name")
	 * 按钮："Agree"，"Refuse"
	 */
	//需要从数据库中获取头像
	public requestItem(int pic, String name) {
		setBackground(Color.LIGHT_GRAY);
		setBounds(new Rectangle(360, 60));
		
		setLayout(null);
		jButton = new JButton();
		jButton.setBounds(new Rectangle(8, 10, 40, 40));
		jButton.setBackground(new Color(236, 255, 236));
		this.pic = (char)('A' + pic);
		jButton.setIcon(new ImageIcon("images/" + this.pic + ".jpg"));
		add(jButton);
		
		lblRequestFrom = new JLabel("Request from: ");
		lblRequestFrom.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblRequestFrom.setBounds(58, 10, 96, 40);
		add(lblRequestFrom);
		
		lblName = new JLabel("New label");
		lblName.setText(name);
		lblName.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblName.setBounds(164, 10, 112, 40);
		add(lblName);
		
		lblRefuse = new JLabel("Refuse!");
		lblRefuse.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblRefuse.setHorizontalAlignment(SwingConstants.CENTER);
		lblRefuse.setBounds(248, 10, 102, 40);
		//add(lblRefuse);
		
		lblAgree = new JLabel("Agree!");
		lblAgree.setHorizontalAlignment(SwingConstants.CENTER);
		lblAgree.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblAgree.setBounds(248, 10, 102, 40);
		//add(lblAgree);
		
		btnAgree = new JButton("Agree");
		//如果同意好友申请
		btnAgree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//移出两个按钮
				requestItem.this.remove(btnAgree);
				requestItem.this.remove(btnRefuse);
				//增加label
				requestItem.this.add(lblAgree);
				requestItem.this.repaint();
				
				//向申请方发送消息，表明同意请求，对方也需要进行处理，修改好友列表信息
				//格式为：对方name + ":Agree by$" + myName
				platform_interface.clientSocket.sendMsg(name, "Agree by:" + platform_interface.myInfo.getname());
				
				
				//还需要调整数据库和在线的好友列表
				//删除等待列表中的用户
				platform_interface.waitingList.remove(name);
				
				//myinfo当中需要修改好友的数量
				platform_interface.myInfo.addfriends_name(name);
				
				user_data dao = new user_data();
				dao.getConn();
				dao.editUser(platform_interface.myInfo);
				person_info temp_user=new person_info();
				temp_user=dao.getUserByName(name);
				int index1 = platform_interface.fList.size();
				
				//增加好友条目
				platform_interface.fList.add(new platform_interface.friendList(name,temp_user.getgender(),temp_user.getpic(),
						temp_user.getsignature(), index1));
				
				platform_interface.addfriend(temp_user.getpic(), name, temp_user.getsignature(), index1);
			}
		});
		btnAgree.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnAgree.setBounds(261, 10, 89, 16);
		add(btnAgree);
		
		btnRefuse = new JButton("Refuse");
		//如果拒绝好友申请
		btnRefuse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//移出两个按钮
				requestItem.this.remove(btnAgree);
				requestItem.this.remove(btnRefuse);
				//增加label
				requestItem.this.add(lblRefuse);
				requestItem.this.repaint();
				//啥都不用做，真爽hhh
			}
		});
		btnRefuse.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnRefuse.setBounds(261, 34, 89, 16);
		add(btnRefuse);
		
		this.repaint();
	}

}
