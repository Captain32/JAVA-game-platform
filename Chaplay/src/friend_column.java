import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class friend_column extends JPanel {
	
	private static final long serialVersionUID = 1L;
	 
	public JButton jButton = null;//显示好友头像；
 
	public JLabel lb_nickName = null;//显示昵称；
 
	private int pic;
 
	private String nickname = null;
 
	public JLabel lb_mood = null;//显示心情；
	
	public int index;
	
	private String sig;
	
	public friend_column(int pic, String nickname, String sig,int index) {
		//super();
		setBackground(Color.WHITE);
		this.pic = pic;//头像编号（有多种方法可以实现，这种最简单）
		this.nickname = nickname;//昵称；
		this.sig = sig;
		this.index = index;

		//点击好友之后，创建聊天窗口
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					//如果在线，则创建聊天界面
					System.out.println("change window");
					platform_interface.setChat_window(index);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		initialize();
		this.setPreferredSize(new Dimension(340, 60));
		jButton.setVisible(true);
		lb_mood.setVisible(true);
		lb_nickName.setVisible(true);
		this.setVisible(true);
		repaint();
	}
 
	private void initialize() {
		//心情
		lb_mood = new JLabel();
		lb_mood.setBounds(new Rectangle(51, 30, 248, 20));
		lb_mood.setFont(new Font("Dialog", Font.PLAIN, 12));
		lb_mood.setText(sig);
		lb_mood.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				exchangeEnter();//鼠标移入改变背景颜色
				lb_mood.setToolTipText(lb_mood.getText());
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				exchangeExited();//鼠标移出恢复背景颜色
			}
		});
		
		//昵称
		lb_nickName = new JLabel();
		lb_nickName.setBounds(new Rectangle(52, 10, 80, 20));
		lb_nickName.setFont(new Font("Dialog", Font.BOLD, 14));
		lb_nickName.setText(nickname);
		
		this.setSize(new Dimension(340, 61));
		this.setLayout(null);
		this.add(getJButton(), null);
		this.add(lb_nickName, null);
		this.add(lb_mood, null);
		lb_nickName.addMouseListener(new java.awt.event.MouseAdapter() { 
			public void mouseExited(java.awt.event.MouseEvent e) {
				exchangeExited();//鼠标移出模板区，改变背景颜色；
			}
 
			public void mouseEntered(java.awt.event.MouseEvent e) {
				exchangeEnter();//鼠标移进模板区，改变背景颜色；
			}
		});
		//repaint();
		
		this.setBackground(null);
	}
	
	private void exchangeEnter() {
		this.setBackground(new Color(100, 200, 200));
	}
 
	private void exchangeExited() {
		this.setBackground(null);
	}
 
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(8, 10, 40, 40));
			jButton.setBackground(new Color(236, 255, 236));
			char tmp = (char)(pic + 'A');
			ImageIcon icon = new ImageIcon(new String("images\\"+ tmp + ".jpg"));
			jButton.setIcon(icon);
			jButton.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mouseExited(java.awt.event.MouseEvent e) {  
					exchangeExited();//鼠标移出模板区，改变背景颜色；
				} 
				public void mouseEntered(java.awt.event.MouseEvent e) {  
					exchangeEnter();//鼠标移进模板区，改变背景颜色；
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					platform_interface.setChat_window(index);
				}
			});
		}
		return jButton;
	}
}
