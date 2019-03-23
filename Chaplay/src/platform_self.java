import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class platform_self extends JPanel {

	/**
	 * Create the panel.
	 */
	private static final long serialVersionUID = 1L;
	person_info myself;
	JLabel name_label;
	JLabel signature_label;
	JLabel head_label;//头像
	char pic;//用哪一张头像图片
	
	//以下为个人信息的背景设置
	ImageIcon background;  
	JLabel imgLabel;
	//private String link = "./images/self_background.jpg";
	
	platform_self(person_info myself){
		super();
		this.setLayout(null);
		this.setPreferredSize(new Dimension(340, 150));
		this.myself = myself;
		this.pic = (char) (myself.getpic() + 'A');
		System.out.println(myself.getpic());
		head_label = new JLabel(new ImageIcon("images/" + pic + ".jpg"));
		head_label.setBounds(10, 40, 50, 50);
		name_label = new JLabel(myself.getname());
		name_label.setBounds(70, 42, 100, 20);
		name_label.setFont(new Font("宋体",Font.BOLD, 16));  
		name_label.setForeground(Color.white);
        signature_label = new JLabel(myself.getsignature());  
        signature_label.setBounds(70, 70, 80, 20);  
        signature_label.setForeground(Color.white);  
		this.add(head_label);
		this.add(name_label);
		this.add(signature_label);
		backGround();
		this.setVisible(true);
	}
	
	//link为图片的地址
	@SuppressWarnings("deprecation")
	public void backGround(/*String link*/)  
	{ 
		background = new ImageIcon("images/selfInfo.jpg");
	    imgLabel = new JLabel(background);  
	    imgLabel.setBounds(0, 0,  background.getIconWidth(), background.getIconHeight());  
	    //buttom=(JPanel)this.getContentPane();  
	    //设置为透明的  
	    this.setOpaque(false); 
	    //置于底层
	    this.add(imgLabel, new Integer(Integer.MIN_VALUE));
	    //this.getLayeredPane().add(imgLabel , new Integer(Integer.MIN_VALUE));  
	} 

}
