import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

public class friendsRequest extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public friendsRequest() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		for(int i = 0;i < platform_interface.waitingList.size();++i) {
			//需要读入申请人的头像就好了，from database
			String name=platform_interface.waitingList.get(i);
			data_base db = new data_base();
			db.getConn();
			String sql = "SELECT * FROM user_table WHERE name = ?";
			ResultSet rs = db.executeQuery(sql,new String[] {name});
			int pic=0;
			try {
				if(rs!=null&&rs.next())
				{
					pic=rs.getInt("pic");
					//System.out.println("pic="+pic);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scrollPane.add(new requestItem(pic, name));
			scrollPane.repaint();
		}
	}
}
