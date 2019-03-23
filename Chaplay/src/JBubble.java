import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.*;

public class JBubble extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int isself;
	JComponent c;
	ArrayList<String> msg;
	Image img;
	int biasX = 15;
	int biasY = 1;
	int strHeight, strWidth;
	
	JBubble(int height, int width, ArrayList<String> msg, int isself, Image img, Dimension d){
		//this.setPreferredSize(d);
		this.setPreferredSize(new Dimension(platform_interface.main_interface.getWidth() - 20, Math.max(50, height + 10)));
		this.msg = msg;
		this.isself  = isself;
		this.img = img;
		this.strWidth = width;
		this.strHeight = height;
	}
	
	public void paintComponent(Graphics g) {
		if(isself == 1) {
			//如果是自己发送的消息
			//画出消息箭头
			//biasX = this.getWidth() - this.strWidth;
			int tot_bias = this.getWidth() - (strWidth + biasX + 45);
			int x[] = new int[6], y[] = new int[6]; 
			x[0] = 0; y[0] = 0;
			x[1] = 0; y[1] = strHeight + 5;
			x[2] = strWidth + biasX / 2; y[2] = strHeight + 5;
			x[3] = strWidth + biasX / 2; y[3] = biasY;
			x[4] = strWidth + biasX; y[4] = 0;
			x[5] = 0; y[5] = 0;
			for(int i = 0;i < 6;++i)
				x[i] += tot_bias;
			g.setColor(Color.LIGHT_GRAY);
			g.fillPolygon(x, y, 6);
			
			/*g.setColor(Color.RED);
			//画出消息矩形框
			g.fillRoundRect(5, 5, strWidth, strHeight, 5, 5);*/
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("宋体", Font.BOLD, 30));
			int j = 0;
			for(String i : msg) {
				g.drawString(i, 5 + tot_bias, j * strHeight + this.strHeight);
				++j;
			}
			g.drawImage(img, tot_bias + strWidth + biasX + 5, 0, 40, 40, this);
		}
		else {
			//如果是好友发送的消息
			//画出消息箭头
			int x[] = new int[6], y[] = new int[6]; 
			x[0] = 45; y[0] = 0;
			x[1] = strWidth + biasX + 50; y[1] = 0;
			x[2] = strWidth + biasX + 50; y[2] = strHeight + 5;
			x[3] = biasX + 45; y[3] = strHeight + 5;
			x[4] = biasX + 45; y[4] = biasY;
			x[5] = 45; y[5] = 0;
			g.setColor(Color.PINK);
			g.fillPolygon(x, y, 6);

			/*g.setColor(Color.PINK);
			//画出消息矩形框
			g.fillRoundRect(70, 70, strWidth, strHeight, 5, 5);*/

			g.setColor(Color.BLACK);
			g.setFont(new Font("宋体", Font.BOLD, 30));
			int j = 0;
			for(String i : msg) {
				g.drawString(i, 45 + biasX, j * strHeight + this.strHeight);
				++j;
			}
			
			g.drawImage(img, 0, 0, 40, 40, this);
		}
	}
	
	/**
	 * @return the isself
	 */
	public int isIsself() {
		return isself;
	}

	/**
	 * @param isself the isself to set
	 */
	public void setIsself(int isself) {
		this.isself = isself;
	}
}