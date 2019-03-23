import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class register_interface extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField name;
	private JPasswordField passwordField;
	static final int name_length = 20;
	static final int password_length = 20;
	private JTextField signature;
	private int Pic, Gender;
	
	/**
	 * Create the frame.
	 */
	/*退出函数，只退出注册窗口*/
	void exit_() {
		this.dispose();
	}
	
	int register_succ() {
		String name = this.name.getText();
		String sig = this.signature.getText();
		@SuppressWarnings("deprecation")
		String password = passwordField.getText();
		int tl = name.length(), pl = password.length();
		if(tl == 0 || pl == 0) 
			return 0;
		else if(tl > name_length || pl > password_length)
			return 1;
		/*如果还有其他的无法成功的情况*/
		person_info user = new person_info();
		user.setname(name);
		user.setpassword(password);
		user.setgender(Gender);
		user.setpic(Pic);
		user.setsignature(sig);
		
		user_data dao = new user_data();
		dao.getConn();
		if(dao.addUser(user))
			return -1;//代表成功
		else
			return 1;
	}
	
	@SuppressWarnings("deprecation")
	public register_interface() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setFont(new Font("Texas Grunge Demo", Font.PLAIN, 20));
		setTitle("Register  your account!");
		setBounds(100, 100, 726, 428);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setOpaque(false); 
		contentPane.setLayout(null);
		
		
		JLabel lbBg = new JLabel();
		contentPane.add(lbBg);
		lbBg.setIcon(new ImageIcon("images/register_background.jpg"));
		lbBg.setBounds(5, 384, 702, 0);
		this.getLayeredPane().add(lbBg,new Integer(Integer.MIN_VALUE)); 
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 5, 702, 379);
		contentPane.add(panel);
		panel.setOpaque(false);
		panel.setLayout(null);
		
		JLabel lblNickname = new JLabel("Nickname:");
		lblNickname.setFont(new Font("宋体", Font.BOLD, 14));
		lblNickname.setBounds(219, 61, 89, 15);
		panel.add(lblNickname);
		
		name = new JTextField();
		name.setBounds(318, 58, 83, 21);
		panel.add(name);
		name.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("宋体", Font.BOLD, 14));
		lblPassword.setBounds(219, 98, 82, 15);
		panel.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(318, 98, 83, 21);
		panel.add(passwordField);
		
		JLabel lblGender = new JLabel("Gender:");
		lblGender.setFont(new Font("宋体", Font.BOLD, 14));
		lblGender.setBounds(219, 132, 82, 15);
		panel.add(lblGender);
		
		JRadioButton rdbtnBoy = new JRadioButton("Boy");
		rdbtnBoy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Gender = 0;
			}
		});
		rdbtnBoy.setBounds(319, 132, 50, 23);
		rdbtnBoy.setBackground(Color.WHITE);
		
		JRadioButton rdbtnGirl = new JRadioButton("Girl");
		rdbtnGirl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Gender = 1;
			}
		});
		rdbtnGirl.setBounds(318, 169, 51, 23);
		rdbtnGirl.setBackground(Color.WHITE);
		
		ButtonGroup gender = new ButtonGroup();
		gender.add(rdbtnGirl);
		gender.add(rdbtnBoy);
		panel.add(rdbtnBoy);
		panel.add(rdbtnGirl);
		
		//点击确定之后返回登陆界面
		JButton btnNewButton = new JButton("Confirm!");
		btnNewButton.setFont(new Font("宋体", Font.BOLD, 18));
		btnNewButton.setBounds(275, 317, 141, 38);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//这里需要检查数据库的注册信息，并且填写
				switch(register_succ()) {
				case -1://注册成功
					JOptionPane.showMessageDialog(null, "Register successfully! ", " Congratulations", JOptionPane.CLOSED_OPTION);
					//关闭窗口
					exit_();
					break;
				case 0://名字或密码为空
					JOptionPane.showMessageDialog(null, "Nickname or password empty!\n\rPlease try again!", "Failed", JOptionPane.WARNING_MESSAGE);
					break;
				case 1://名字或密码太长
					JOptionPane.showMessageDialog(null, "Nickname or password too long!\n\rPlease try again!", "Failed", JOptionPane.WARNING_MESSAGE);
					break;
				default:break;
				}
			}
		});
		panel.add(btnNewButton);
		
		JLabel lblSignature = new JLabel("Signature:");
		lblSignature.setFont(new Font("宋体", Font.BOLD, 14));
		lblSignature.setBounds(219, 208, 89, 15);
		panel.add(lblSignature);
		
		signature = new JTextField();
		signature.setBounds(318, 205, 216, 21);
		panel.add(signature);
		signature.setColumns(10);
		
		JLabel Portrait = new JLabel("New label");
		Portrait.setIcon(new ImageIcon("images\\A.jpg"));
		Portrait.setBounds(157, 234, 50, 50);
		panel.add(Portrait);
		
		JLabel portrait2 = new JLabel("New label");
		portrait2.setIcon(new ImageIcon("images\\B.jpg"));
		portrait2.setBounds(319, 234, 50, 50);
		panel.add(portrait2);
		
		JLabel portrait3 = new JLabel("New label");
		portrait3.setIcon(new ImageIcon("images\\C.jpg"));
		portrait3.setBounds(471, 234, 50, 50);
		panel.add(portrait3);
		
		JRadioButton rdbtnPortrait0 = new JRadioButton("");
		rdbtnPortrait0.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Pic = 0;
			}
		});
		rdbtnPortrait0.setBounds(173, 290, 19, 23);
		panel.add(rdbtnPortrait0);
		
		JRadioButton rdbtnPortrait1 = new JRadioButton("");
		rdbtnPortrait1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Pic = 1;
			}
		});
		rdbtnPortrait1.setBounds(334, 290, 19, 23);
		panel.add(rdbtnPortrait1);
		
		JRadioButton rdbtnPortrait2 = new JRadioButton("");
		rdbtnPortrait2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Pic = 2;
			}
		});
		rdbtnPortrait2.setBounds(486, 290, 19, 23);
		panel.add(rdbtnPortrait2);
		
		ButtonGroup pic = new ButtonGroup();
		pic.add(rdbtnPortrait0);
		pic.add(rdbtnPortrait1);
		pic.add(rdbtnPortrait2);
		
	}
}
