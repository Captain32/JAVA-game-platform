import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class ChatClient {
	
	private Socket clientSocket;
	//���������������������Ϣ
	private PrintWriter pw;
	//���ܴӷ��������͵���Ϣ���߳�
	ExecutorService exec;
	
	BufferedReader br;
	
	public Socket getclientSocket() {
		return clientSocket;
	}
	
	public ChatClient(String IP) {
		try {
			//�ͷ�����IP��ַ��������
			clientSocket = new Socket(IP, 6790);
			br = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//�����½�ɹ��򷵻�1�����򷵻�0�������������Ϣ
	public boolean start(String nickname) {
		try {
			//�ж��Ƿ��ظ���½
			if(checkIfLog(nickname) == false)
				return false;
			
			// ���շ������˷��͹�������Ϣ���߳�����
			exec = Executors.newCachedThreadPool();
            exec.execute(new ListenerServer());
			
			// �����������������˷���Ϣ
			pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			
			//��������½�ɹ�
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


	//�������������Ϣ
	public void sendMsg(String fName, String msg) {
		String str = new String("@" + fName + ":" + msg);
		pw.println(str);
	}
	
	//����������Ϸ��Ϣ
	public void sendGameMsg(String str) {
		/*StringTokenizer st = new StringTokenizer(str, "@");
		String header = st.nextToken();
		str = str.substring(header.length());
		str = platform_interface.myInfo.getname() + str;
		str = header + str;*/
		pw.println(str);
	}
	
	//������Ϸ�ĵ÷�
	public void sendGameScore(String game, String score) {
		String str = new String("Score@" + game + "@" + score);
		pw.println(str);
	}
	
	//�жϺ����Ƿ�����
	public boolean judgeOnline(String nickname) throws Exception{
		//���������
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
		//����������
		BufferedReader br = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		pw.println("$$$" + nickname);
		String pass = br.readLine();
		System.out.println(pass);
		if (pass != null && pass.equals("Online")) {
			//����Ŀǰ��������״̬
			return true;
		} 
		else {
			//�����������״̬��Ŀǰ�޷���������
			return false;
		}
	}
	
	//����Ƿ��Ѿ���½
	public boolean checkIfLog(String nickname) throws Exception {
		//���������
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
		//����������
		/*BufferedReader br = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));*/
		
		if (nickname.trim().equals("")) {
			//���ﵽʱ�򷵻�һ���ַ��������ڵ�¼�������������Ϣ				
			System.out.println("�ǳƲ���Ϊ��");
			return false;
		} 
		else {	
			pw.println(nickname);
			//�ӷ�������ȡ�Ƿ����ظ�������
			String pass = br.readLine();
			if (pass != null && (!pass.equals("OK"))) {
				//�ظ���½
				//���ﵽʱ�򷵻�һ���ַ��������ڵ�¼�������������Ϣ
				System.out.println("���˺��ѱ���½����ȷ�����˺���Ϣ�İ�ȫ��");
				return false;
			} 
			else {
				return true;
			}
		}
	}

	
	// ѭ����ȡ����˷��͹�������Ϣ��������ͻ��˵Ŀ���̨
 	class ListenerServer implements Runnable {

 		/*//�̰߳�ȫ��
 		private byte[] lock = new byte[0];*/
 		
 		@Override
 		public void run() {
 			try {
 				String msgString;
 				//���õ�����Ϣ�Ž��Լ��ķ��뵱��ȥ
 				int index = 0, k;
 				String fName;
 				
 				while(true) {
 					//���������Ҫ����һ��ѭ�����Ĳ���
 					while((msgString = br.readLine())!= null) {
 						//System.out.println("receive: " + msgString);
 						//����ǵ�����������Ϸ
 						if(msgString.startsWith("DDTANK@")) {
 							msgString = msgString.substring("DDTANK@".length());
 							System.out.println("enterDDTank:" + msgString);
 							synchronized(platform_interface.curDDtank.lock) {
 								platform_interface.curDDtank.strRead = msgString;
 								platform_interface.curDDtank.parseMessage();
 							}	
 						continue;
 						}
					
 						//����Ƿ��������͵ķ�����Ϣ
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
 						
 						//����������ս����
 						if(msgString.startsWith("CONNECT@")) {
 							//��ʽCONNECT@NAME
 							msgString = msgString.substring("CONNECT@".length());
 							int res = JOptionPane.showConfirmDialog(null, "Do you want to play DDtank with " + msgString, "Game fight!",
 									JOptionPane.YES_NO_OPTION);
 							//���ͬ��һ�������Ϸ
 							if(res == JOptionPane.YES_OPTION) { 
 								//platform_interface.curDDtank.onlinePK();
 								sendGameMsg("ACCEPT@" + msgString + "@" + platform_interface.myInfo.getname());
 								platform_interface.curDDtank.enemyName = msgString;
 							}	
 							//�ܾ�
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
 						//��ȡ���ͷ�������
 						k = msgString.indexOf(":");
 						if(k < 0)
 							break;
 						fName = msgString.substring(0, k);
 						
 						//����Ǻ�������
 						if(fName.equals("Request")) {
 							fName = msgString.substring(k + 1);
 							platform_interface.waitingList.add(fName);
 							
 							break;
 						}
					
 						//��������뱻ͨ��
 						if(fName.equals("Agree by")) {
 							//�����ļ�����¼�����¼
 							platform_interface.fileList.add(new RandomAccessFile(fName + ".txt", "rw"));
						
						
 							fName = msgString.substring(k + 1);
 							//������Ҫ�����ݿ��е�����Ϣ
 							//����fList��myInfo����Ϣ
 							platform_interface.myInfo.addfriends_name(fName);
 							user_data dao = new user_data();
 							dao.getConn();
 							dao.editUser(platform_interface.myInfo);
 							person_info temp_user=new person_info();
 							temp_user=dao.getUserByName(fName);
 							int index1 = platform_interface.fList.size();
 							//���Ӻ�����Ŀ
 							platform_interface.fList.add(new platform_interface.friendList(fName,temp_user.getgender(),temp_user.getpic(),
 									temp_user.getsignature(), index1));
 							platform_interface.addfriend(temp_user.getpic(), fName, temp_user.getsignature(), index1);
 							break;
 						}
 						//���������¼����
 						int file_index = -1;
 						for(int i = 0;i < platform_interface.fList.size();++i) {
 							if(platform_interface.fList.get(i).getname().equals(fName)) {
 								file_index = i;
 								break;
 							}
 						}
					
 						//���û���ҵ���˵������������
 						if(file_index == -1) {
 							System.out.println("fnum" + platform_interface.fList.size());
 							System.out.println("fName:" + fName);
 							System.out.println("friend not found!");
 							continue;
 						}	
					
 						RandomAccessFile ras = platform_interface.fileList.get(file_index);
 						ras.seek(ras.length());
 						//System.out.println(platform_interface.fList.size());
 						//�����������Ϣ
 						for(int j = 0;j < platform_interface.fList.size();++j) {
 							if(platform_interface.fList.get(j).getname().equals(fName)) {									
 								index = j;
 								msgString = msgString.substring(k + 1);
 								
 								ras.write(new String(0 + ":").getBytes());//�����ǶԷ����͵�
 								ras.write(msgString.getBytes());//д�������¼
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