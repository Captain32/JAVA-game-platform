import javax.swing.JPanel;
import javax.swing.JLabel;
 
public class friendsPane extends JPanel {
 
	private static final long serialVersionUID = 1L;
  
	public friendsPane() {
		super();
	}
 
	public void addFriends(String name, int pic, String sig, int index) {
		System.out.println("we have enter add Friends");
		this.add(new JLabel().add(new friend_column(pic, name, sig, index)));
	}
	
}