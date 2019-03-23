import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class ChatClient {
	
	private Socket clientSocket;
	//输出流，给服务器发送消息
	private PrintWriter pw;
	//接受从服务器发送的消息的线程
	ExecutorService exec;
	
	BufferedReader br;
	
	public Socket getclientSocket() {
		return clientSocket;
	}
	
	public ChatClient(String IP) {
		try {
			//和服务器IP地址建立连接
			clientSocket = new Socket(IP, 6790);
			br = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//如果登陆成功则返回1，否则返回0并且输出错误信息
	public boolean start(String nickname) {
		try {
			//判断是否重复登陆
			if(checkIfLog(nickname) == false)
				return false;
			
			// 接收服务器端发送过来的信息的线程启动
			exec = Executors.newCachedThreadPool();
            exec.execute(new ListenerServer());
			
			// 建立输出流，给服务端发信息
			pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			
			//创建，登陆成功
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		} /*finally {
			if (clientSocket !=null) {
				try {
					clientSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}*/
		return false;
	}


	//向服务器发送消息
	public void sendMsg(String fName, String msg) {
		String str = new String("@" + fName + ":" + msg);
		pw.println(str);
	}
	
	//发送联机游戏消息
	public void sendGameMsg(String str) {
		/*StringTokenizer st = new StringTokenizer(str, "@");
		String header = st.nextToken();
		str = str.substring(header.length());
		str = platform_interface.myInfo.getname() + str;
		str = header + str;*/
		pw.println(str);
	}
	
	//传送游戏的得分
	public void sendGameScore(String game, String score) {
		String str = new String("Score@" + game + "@" + score);
		pw.println(str);
	}
	
	//判断好友是否在线
	public boolean judgeOnline(String nickname) throws Exception{
		//创建输出流
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
		//创建输入流
		BufferedReader br = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		pw.println("$$$" + nickname);
		String pass = br.readLine();
		System.out.println(pass);
		if (pass != null && pass.equals("Online")) {
			//好友目前处于上线状态
			return true;
		} 
		else {
			//如果处于线下状态，目前无法进行聊天
			return false;
		}
	}
	
	//检查是否已经登陆
	public boolean checkIfLog(String nickname) throws Exception {
		//创建输出流
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
		//创建输入流
		/*BufferedReader br = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));*/
		
		if (nickname.trim().equals("")) {
			//这里到时候返回一个字符串，用于登录界面输出错误信息				
			System.out.println("昵称不得为空");
			return false;
		} 
		else {	
			pw.println(nickname);
			//从服务器获取是否有重复的信命
			String pass = br.readLine();
			if (pass != null && (!pass.equals("OK"))) {
				//重复登陆
				//这里到时候返回一个字符串，用于登录界面输出错误信息
				System.out.println("该账号已被登陆，请确认您账号信息的安全！");
				return false;
			} 
			else {
				return true;
			}
		}
	}

	
	// 循环读取服务端发送过来的信息并输出到客户端的控制台
 	class ListenerServer implements Runnable {

 		/*//线程安全锁
 		private byte[] lock = new byte[0];*/
 		
 		@Override
 		public void run() {
 			try {
 				String msgString;
 				//将得到的消息放进自己的放入当中去
 				int index = 0, k;
 				String fName;
 				
 				while(true) {
 					//这里可能需要增加一个循坏读的操作
 					while((msgString = br.readLine())!= null) {
 						//System.out.println("receive: " + msgString);
 						//如果是弹弹堂联机游戏
 						if(msgString.startsWith("DDTANK@")) {
 							msgString = msgString.substring("DDTANK@".length());
 							System.out.println("enterDDTank:" + msgString);
 							synchronized(platform_interface.curDDtank.lock) {
 								platform_interface.curDDtank.strRead = msgString;
 								platform_interface.curDDtank.parseMessage();
 							}	
 						continue;
 						}
					
 						//如果是服务器发送的分数消息
 						if(msgString.startsWith("score@")) {
 							msgString = msgString.substring("SCORE@".length());
 							//System.out.println(msgString);
 							StringTokenizer st = new StringTokenizer(msgString, "@");
 							if(platform_interface.Ddp_player1 != null && platform_interface.Ddp_player2 != null &&platform_interface.Ddp_player3 != null
 									&& platform_interface.Ddt_player1 != null && platform_interface.Ddt_player2 != null && platform_interface.Ddt_player3 != null
 									&& platform_interface.Txz_player1 != null && platform_interface.Txz_player2 != null && platform_interface.Txz_player3 != null
 									&& platform_interface.Ld_player1 != null && platform_interface.Ld_player2 != null && platform_interface.Ld_player3 != null) {
 								platform_interface.Ddp_player1.setText(st.nextToken());
 								platform_interface.Ddp_player2.setText(st.nextToken());
 								platform_interface.Ddp_player3.setText(st.nextToken());
 								
 								platform_interface.Ddt_player1.setText(st.nextToken());
 								platform_interface.Ddt_player2.setText(st.nextToken());
 								platform_interface.Ddt_player3.setText(st.nextToken());
 								
 								platform_interface.Txz_player1.setText(st.nextToken());
 								platform_interface.Txz_player2.setText(st.nextToken());
 								platform_interface.Txz_player3.setText(st.nextToken());
 								
 								platform_interface.Ld_player1.setText(st.nextToken());
 								platform_interface.Ld_player2.setText(st.nextToken());
 								platform_interface.Ld_player3.setText(st.nextToken());
 							}	
 							continue;
 						}
 						
 						//发起联机对战申请
 						if(msgString.startsWith("CONNECT@")) {
 							//格式CONNECT@NAME
 							msgString = msgString.substring("CONNECT@".length());
 							int res = JOptionPane.showConfirmDialog(null, "Do you want to play DDtank with " + msgString, "Game fight!",
 									JOptionPane.YES_NO_OPTION);
 							//如果同意一起玩儿游戏
 							if(res == JOptionPane.YES_OPTION) { 
 								//platform_interface.curDDtank.onlinePK();
 								sendGameMsg("ACCEPT@" + msgString + "@" + platform_interface.myInfo.getname());
 								platform_interface.curDDtank.enemyName = msgString;
 							}	
 							//拒绝
 							else {
 								sendGameMsg("REFUSE@" + msgString);
 							}	
 							continue;
 						}
					
 						if(msgString.startsWith("GameNum@")) {
 							String tmp = msgString.substring("GameNum@".length());
 							platform_interface.curDDtank.strRead = tmp;
 							if(tmp.equals("1")) {
 								platform_interface.curDDtank.onlinePK();
 							}
						
 							continue;
 						}
					
 						if(msgString.startsWith("REFUSE")) {
 							platform_interface.curDDtank.strRead = "-1";
 							continue;
 						}
					
 						//System.out.println("client:" + msgString);
 						//获取发送方的名字
 						k = msgString.indexOf(":");
 						if(k < 0)
 							break;
 						fName = msgString.substring(0, k);
 						
 						//如果是好友申请
 						if(fName.equals("Request")) {
 							fName = msgString.substring(k + 1);
 							platform_interface.waitingList.add(fName);
 							
 							break;
 						}
					
 						//如果是申请被通过
 						if(fName.equals("Agree by")) {
 							//创建文件，记录聊天记录
 							platform_interface.fileList.add(new RandomAccessFile(fName + ".txt", "rw"));
						
						
 							fName = msgString.substring(k + 1);
 							//这里需要从数据库中导入信息
 							//更新fList，myInfo等信息
 							platform_interface.myInfo.addfriends_name(fName);
 							user_data dao = new user_data();
 							dao.getConn();
 							dao.editUser(platform_interface.myInfo);
 							person_info temp_user=new person_info();
 							temp_user=dao.getUserByName(fName);
 							int index1 = platform_interface.fList.size();
 							//增加好友条目
 							platform_interface.fList.add(new platform_interface.friendList(fName,temp_user.getgender(),temp_user.getpic(),
 									temp_user.getsignature(), index1));
 							platform_interface.addfriend(temp_user.getpic(), fName, temp_user.getsignature(), index1);
 							break;
 						}
 						//搜索聊天记录方案
 						int file_index = -1;
 						for(int i = 0;i < platform_interface.fList.size();++i) {
 							if(platform_interface.fList.get(i).getname().equals(fName)) {
 								file_index = i;
 								break;
 							}
 						}
					
 						//如果没有找到，说明出现了问题
 						if(file_index == -1) {
 							System.out.println("fnum" + platform_interface.fList.size());
 							System.out.println("fName:" + fName);
 							System.out.println("friend not found!");
 							continue;
 						}	
					
 						RandomAccessFile ras = platform_interface.fileList.get(file_index);
 						ras.seek(ras.length());
 						//System.out.println(platform_interface.fList.size());
 						//如果是聊天信息
 						for(int j = 0;j < platform_interface.fList.size();++j) {
 							if(platform_interface.fList.get(j).getname().equals(fName)) {									
 								index = j;
 								msgString = msgString.substring(k + 1);
 								
 								ras.write(new String(0 + ":").getBytes());//表明是对方发送的
 								ras.write(msgString.getBytes());//写入聊天记录
 								ras.write("\r\n".getBytes());
								
 								platform_interface.fList.get(index).msgList.add(msgString);
 								platform_interface.fList.get(index).usrList.add(0);
 								if(index == platform_interface.friend_current) {
 									platform_interface.addTextMessage(msgString, 0, platform_interface.fList.get(index).getpic(), platform_interface.fList.size());
 									platform_interface.msgPanel.repaint();
 									platform_interface.msgScrollPane.repaint();
 								}
 								break;
 							}
 						}
 					}
 				}
 			} catch(Exception e) {
 				e.printStackTrace();
 			}
 		}
	}

}