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
	 * ��ʽΪ��Label("Request from ") + Label("name")
	 * ��ť��"Agree"��"Refuse"
	 */
	//��Ҫ�����ݿ��л�ȡͷ��
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
		//���ͬ���������
		btnAgree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//�Ƴ�������ť
				requestItem.this.remove(btnAgree);
				requestItem.this.remove(btnRefuse);
				//����label
				requestItem.this.add(lblAgree);
				requestItem.this.repaint();
				
				//�����뷽������Ϣ������ͬ�����󣬶Է�Ҳ��Ҫ���д����޸ĺ����б���Ϣ
				//��ʽΪ���Է�name + ":Agree by$" + myName
				platform_interface.clientSocket.sendMsg(name, "Agree by:" + platform_interface.myInfo.getname());
				
				
				//����Ҫ�������ݿ�����ߵĺ����б�
				//ɾ���ȴ��б��е��û�
				platform_interface.waitingList.remove(name);
				
				//myinfo������Ҫ�޸ĺ��ѵ�����
				platform_interface.myInfo.addfriends_name(name);
				
				user_data dao = new user_data();
				dao.getConn();
				dao.editUser(platform_interface.myInfo);
				person_info temp_user=new person_info();
				temp_user=dao.getUserByName(name);
				int index1 = platform_interface.fList.size();
				
				//���Ӻ�����Ŀ
				platform_interface.fList.add(new platform_interface.friendList(name,temp_user.getgender(),temp_user.getpic(),
						temp_user.getsignature(), index1));
				
				platform_interface.addfriend(temp_user.getpic(), name, temp_user.getsignature(), index1);
			}
		});
		btnAgree.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnAgree.setBounds(261, 10, 89, 16);
		add(btnAgree);
		
		btnRefuse = new JButton("Refuse");
		//����ܾ���������
		btnRefuse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//�Ƴ�������ť
				requestItem.this.remove(btnAgree);
				requestItem.this.remove(btnRefuse);
				//����label
				requestItem.this.add(lblRefuse);
				requestItem.this.repaint();
				//ɶ������������ˬhhh
			}
		});
		btnRefuse.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnRefuse.setBounds(261, 34, 89, 16);
		add(btnRefuse);
		
		this.repaint();
	}

}
