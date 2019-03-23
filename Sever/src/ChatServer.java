import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**����˼�룺
 * ���û����ǳ���Ϊ�����ı�־
 * ����ʵ���û�֮���ͨ��
 * ��Ϊ˽�ĺ͹㲥
 * �㲥���ڣ����ƺͲ������µļ�¼
 */


public class ChatServer {	
	
	private ServerSocket serverSocket;
	
	/**
	* �����̳߳�������ͻ��˵������߳�
	* ����ϵͳ��Դ�����˷�
	*/
	private ExecutorService exec;
	
	// ��ſͻ���֮��˽�ĵ���Ϣ
	private Map<String, PrintWriter> storeInfo;
	
	private String changeRank(List<usrPoint> lst) {
		String msg = "";
		//System.out.println(lst.size());
		if(lst.size() != 0) {
			lst.sort(Comparator.reverseOrder());
		}
		int i = 0;
		for(i = 0;i < 3&&i < lst.size();++i) {
			msg += lst.get(i).name + "@";
			//System.out.println(lst.get(i).name);
		}
		for(; i < 3;++i) {
			msg += "Waiting for you!" + "@";
		}
		return msg;
	}
	
	public ChatServer() {
		try {
			//����������socket�˿�
			serverSocket = new ServerSocket(6790); 
			storeInfo = new HashMap<String, PrintWriter>();
			exec = Executors.newCachedThreadPool();
			Timer timer = new Timer();
			//ÿ��5���ӽ���һ�����򣬲������е���ҽ��з��ͣ���Ҹ���������Ϣ�����Ҳ��������
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//System.out.println("timer");
					String msg = "score@";
					msg += changeRank(duiduipeng);
					msg += changeRank(ddt);
					msg += changeRank(tuixiangzi);
					msg += changeRank(feiji);
					//System.out.println(msg);
					sendToAll(msg);
				}
			
			}, 0, 3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	List<usrPoint> tuixiangzi = new ArrayList<usrPoint>();
	List<usrPoint> ddt = new ArrayList<usrPoint>();
	List<usrPoint> duiduipeng = new ArrayList<usrPoint>();
	List<usrPoint> feiji = new ArrayList<usrPoint>();
	
	// ���ͻ��˵���Ϣ��Map��ʽ���뼯����
	private synchronized void putIn(String key,PrintWriter value) {
		storeInfo.put(key, value);
		/*File Tuixiangzi = new File(key + "BOX.txt");
		File DDtank = new File(key + "DDTANK.txt");
		File Duiduipeng = new File(key + "TOUCH.txt");
		File Feiji = new File(key + "THUNDER.txt");*/
		RandomAccessFile rasTui = null;
		RandomAccessFile rasDDt = null;
		RandomAccessFile rasDui = null;
		RandomAccessFile rasFei = null;
		try {
			rasTui = new RandomAccessFile(key + "BOX.txt", "rw");
			rasDDt = new RandomAccessFile(key + "DDTANK.txt", "rw");
			rasDui = new RandomAccessFile(key + "TOUCH.txt", "rw");
			rasFei = new RandomAccessFile(key + "THUNDER.txt", "rw");
			int tui, DDt, Dui, Fei;
			if(rasTui.length() != 0) {
				tui = Integer.parseInt(rasTui.readLine().toString());
				//System.out.println("tui: " + tui);
				tuixiangzi.add(new usrPoint(key, tui));
			}
			if(rasDDt.length() != 0) {
				DDt = Integer.parseInt(rasDDt.readLine().toString());
				ddt.add(new usrPoint(key, DDt));
			}
			if(rasDui.length() != 0) {
				Dui = Integer.parseInt(rasDui.readLine().toString());
				duiduipeng.add(new usrPoint(key, Dui));
			}
			if(rasFei.length() != 0) {
				Fei = Integer.parseInt(rasFei.readLine().toString());
				feiji.add(new usrPoint(key, Fei));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(rasTui != null) rasTui.close();
				if(rasDDt != null) rasDDt.close();
				if(rasDui != null) rasDui.close();
				if(rasFei != null) rasFei.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class usrPoint implements Comparable<usrPoint>{
		String name;
		int score;
		usrPoint(String name, int score){
			this.name = name;
			this.score = score;
		}
		@Override
		public int compareTo(usrPoint a) {
			// TODO Auto-generated method stub
			if(score > a.score)
				return -1;
			if(score < a.score)
				return 1;
			return name.compareTo(a.name);
		}
		
	}
	
	// ��������������ӹ�������ɾ��
	private synchronized void remove(String  key) {
		storeInfo.remove(key);
		System.out.println("��ǰ��������Ϊ��"+ storeInfo.size());
	}
	
	// ����������Ϣת�������пͻ���
	private synchronized void sendToAll(String message) {
		if(storeInfo.size() == 0)
			return;
		//System.out.println(message);
		for(PrintWriter out: storeInfo.values()) {
			out.println(message);
		}
		//System.out.println("fuck");
	}
	
	// ����������Ϣת����˽�ĵĿͻ���
	private synchronized void sendToSomeone(String name, String message) {
		//����Ӧ�ͻ��˵�������Ϣȡ����Ϊ˽�����ݷ��ͳ�ȥ
		PrintWriter pw = storeInfo.get(name); 
		//System.out.println(pw);
		//System.out.println(message);
		if(pw != null) pw.println(message);	
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("�ȴ��ͻ�������... ... ");
				Socket socket = serverSocket.accept();

				// ��ȡ�ͻ��˵�ip��ַ
				InetAddress address = socket.getInetAddress();
				System.out.println("�ͻ��ˣ���" + address.getHostAddress() + "�����ӳɹ��� ");
				/*
				 * ����һ���̣߳����߳�������ͻ��˵��������������ٴμ���
				 * ��һ���ͻ��˵�����
				 */
				exec.execute(new ServerClient(socket)); //ͨ���̳߳��������߳�
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* ���߳����������������ĳһ���ͻ��˵���Ϣ��ѭ�����տͻ��˷���
	* ��ÿһ���ַ����������������̨
	*/
	class ServerClient implements Runnable {

		private Socket socket;
		private String name;
				
		public ServerClient(Socket socket) {
			this.socket = socket;
			//this.run();
		}
		
		// �����ڲ�������ȡ�ǳ�
		private String getName() throws Exception {
			try {
				//����˵���������ȡ�ͻ��˷��������ǳ������
				BufferedReader bReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
				//����˽��ǳ���֤���ͨ���������������͸��ͻ���
				PrintWriter ipw = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);

				//��ȡ�ͻ��˷������ǳ�
				//������������Ƿ����ظ���½
				while(true) {
					String nameString = bReader.readLine();
					//������������Ϊ�ջ����Ѿ���½��Ȼ��һֱ����׼������Ϣ��״̬
					if ((nameString.trim().length() == 0) || storeInfo.containsKey(nameString)) {
						ipw.println("FAIL");
					} else {
						ipw.println("OK");
						return nameString;
					}
				}
			} catch(Exception e) {
				throw e;
			}
		}
		
        @Override
		public void run() {
			try {
				/*
				* ͨ���ͻ��˵�Socket��ȡ�ͻ��˵������
				* ��������Ϣ���͸��ͻ���
				*/
				PrintWriter pw = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				/*
				* ���ͻ��ǳƺ�����˵�����ݴ��빲����HashMap��
				*/
				name = getName();
				putIn(name, pw);
				Thread.sleep(100);
				
				// �����֪ͨ���пͻ��ˣ�ĳ�û�����
				sendToAll("[ϵͳ֪ͨ] ��" + name + "��������");


				//System.out.println("hello!");
				/*
				* ͨ���ͻ��˵�Socket��ȡ������
				* ��ȡ�ͻ��˷���������Ϣ
				*/
				BufferedReader bReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String msgString = null;
				
				while(true) {
					//System.out.println("ɷ��");
					while((msgString = bReader.readLine()) != null) {
						//SCORE@��Ϸ��@����ֵ
						if(msgString.startsWith("SCORE@")) {
							msgString = msgString.substring("SCORE@".length());
							StringTokenizer st = new StringTokenizer(msgString, "@");
							String gameName = st.nextToken();
							String score = st.nextToken() + "\r\n";
							RandomAccessFile ras = null;
							try {
								ras = new RandomAccessFile(name + gameName + ".txt", "rw");
								ras.seek(ras.length());
								//�����ļ�����
								ras.write(score.getBytes());
							}catch(Exception e){
								e.printStackTrace();
							}finally {
								if(ras != null)
									ras.close();
							}
							
						}
						//�ͺ��ѷ��𵯵�����Ϸ����
						else if(msgString.startsWith("CONNECT@")) {
							//CONNECT@�Է������@
							msgString = msgString.substring("CONNECT@".length());
							StringTokenizer st = new StringTokenizer(msgString, "@");
							String toName = st.nextToken();
							msgString = "CONNECT@" + name;
							if(storeInfo.containsKey(toName)) {
								sendToSomeone(toName, msgString);
							}
						}
						//ͬ������
						else if(msgString.startsWith("ACCEPT@")) {
							//��ʽ��ACCEPT@NAME1@NAME2
							msgString = msgString.substring("ACCEPT@".length());
							StringTokenizer st = new StringTokenizer(msgString, "@");
							String name1 = st.nextToken(), name2 = st.nextToken();
							sendToSomeone(name1, new String("GameNum@" + 0));
							sendToSomeone(name2, new String("GameNum@" + 1));
						}
						else if(msgString.startsWith("REFUSE@")) {
							//��ʽ��REFUSE@NAME
							msgString = msgString.substring("REFUSE@".length());
							sendToSomeone(msgString, "REFUSE");
						}
						//����ҽ�����Ϸʱ���ͨ��
						else if(msgString.startsWith("DDTANK@")) {
							System.out.println(msgString);
							//DDTANK@�Է������@��Ϸ��������
							msgString = msgString.substring("DDTANK@".length());
							StringTokenizer st = new StringTokenizer(msgString, "@");
							String toName = st.nextToken();
							System.out.println(toName);
							msgString = "DDTANK@" + st.nextToken();
							if(storeInfo.containsKey(toName)) {
								sendToSomeone(toName, msgString);
							}
						}
						//������Ӻ�����Ϣ����ʽ��@friends request from + name��
						else if(msgString.startsWith("@friends request from ")) {
							String toName = msgString.substring(new String("@friends request from ").length(),
									msgString.length());
							//�����������
							if(storeInfo.containsKey(toName)) {
								sendToSomeone(toName, msgString);
							}
						}
						// �����Ƿ�Ϊ˽�ģ���ʽ��@���Ͷ����ǳ�:���ݣ�
						else if(msgString.startsWith("@")) {
							int index = msgString.indexOf(":");
							if(index >= 0) {
								//��ȡ�ǳ�
								String toName = msgString.substring(1, index);
								String info = msgString.substring(index + 1);
								if(info.startsWith("@friends request from ")) {
									//����Ǻ�������
									info = info.substring(new String("@friends request from ").length());
									info = "Request:" + name;//���͸��û��ĸ�ʽ��"Request:" + name
									//System.out.println(toName);
									//System.out.println(info);
									//���������Ҫ����дtxt�Ĺ���
								}
								//�����ͬ������
								else if(info.startsWith("Agree by:")) {
									System.out.println(toName);
									System.out.println(info);
								}
								//�������������
								else info =  name + ":"+ info;//���͸��û��ĸ�ʽ��name + ":" + msg
								//��˽����Ϣ���ͳ�ȥ
								sendToSomeone(toName, info);
							}
						}
						//�û��������󣬲�ѯ�ú����Ƿ�����
						else if(msgString.startsWith("$$$")) {
							String theName = msgString.substring(3, msgString.length());
							if(storeInfo.containsKey(theName)) {
								pw.println("Online");
							}
							else pw.println("Offline");
						}
						else {
							// ������������������ÿͻ��˷��͵���Ϣת�������пͻ���
							System.out.println(name+"��"+ msgString);
							sendToAll(name+"��"+ msgString);
						}
					}	
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				remove(name);
				// ֪ͨ���пͻ��ˣ�ĳĳ�ͻ��Ѿ�����
				sendToAll("[ϵͳ֪ͨ] "+name + "�Ѿ������ˡ�");
				
				if(socket!=null) {
					try {
						System.out.println("null socket");
						socket.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}	
			}
		}
	}
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start();
	}
}