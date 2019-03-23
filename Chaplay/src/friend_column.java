import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class friend_column extends JPanel {
	
	private static final long serialVersionUID = 1L;
	 
	public JButton jButton = null;//��ʾ����ͷ��
 
	public JLabel lb_nickName = null;//��ʾ�ǳƣ�
 
	private int pic;
 
	private String nickname = null;
 
	public JLabel lb_mood = null;//��ʾ���飻
	
	public int index;
	
	private String sig;
	
	public friend_column(int pic, String nickname, String sig,int index) {
		//super();
		setBackground(Color.WHITE);
		this.pic = pic;//ͷ���ţ��ж��ַ�������ʵ�֣�������򵥣�
		this.nickname = nickname;//�ǳƣ�
		this.sig = sig;
		this.index = index;

		//�������֮�󣬴������촰��
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					//������ߣ��򴴽��������
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
		//����
		lb_mood = new JLabel();
		lb_mood.setBounds(new Rectangle(51, 30, 248, 20));
		lb_mood.setFont(new Font("Dialog", Font.PLAIN, 12));
		lb_mood.setText(sig);
		lb_mood.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				exchangeEnter();//�������ı䱳����ɫ
				lb_mood.setToolTipText(lb_mood.getText());
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				exchangeExited();//����Ƴ��ָ�������ɫ
			}
		});
		
		//�ǳ�
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
				exchangeExited();//����Ƴ�ģ�������ı䱳����ɫ��
			}
 
			public void mouseEntered(java.awt.event.MouseEvent e) {
				exchangeEnter();//����ƽ�ģ�������ı䱳����ɫ��
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
					exchangeExited();//����Ƴ�ģ�������ı䱳����ɫ��
				} 
				public void mouseEntered(java.awt.event.MouseEvent e) {  
					exchangeEnter();//����ƽ�ģ�������ı䱳����ɫ��
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
