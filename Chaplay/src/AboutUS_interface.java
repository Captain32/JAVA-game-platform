import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;

public class AboutUS_interface extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AboutUS_interface() {
		this.setAlwaysOnTop(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		//contentPane.setOpaque(false);
		contentPane.setVisible(true);
		setContentPane(contentPane);
		
		getContentPane().setLayout(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(439, 250);
		
		JTextArea jta = new JTextArea();
		String str = "This is the final project in the course Java\n Programming produced by Tricycle \n"
				+ "Group.\n\n "
				+ "We sincerely hope the little program can bring you much joy!\n\n "
				+ "Enjoy your time!\n";
		jta.setText(str);
		jta.setLineWrap(true);
		jta.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		//���ı�������ӹ�����
		JScrollPane jsp = new JScrollPane(jta);
		jsp.setOpaque(false);
		jsp.setAutoscrolls(true);
		jsp.setFocusCycleRoot(true);
		jsp.setBackground(Color.WHITE);
		//���þ��δ�С.��������Ϊ(�������ϽǺ�����x,�������Ͻ�������y�����γ��ȣ����ο��)
		jsp.setBounds(getWidth() / 3 + 10, 20, 250, 165);
		//�����ù���������Ҫ��ʱ������ʽ
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jta.setEditable(false);
		contentPane.add(jsp);
		
		JLabel lblNewLabel = new JLabel("New label");
		contentPane.add(lblNewLabel);
		
		ImageIcon icon = new ImageIcon("./images/register_background.jpg");
		lblNewLabel.setIcon(icon);
		lblNewLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
	}

}
