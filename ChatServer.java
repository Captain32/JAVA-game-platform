import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
	
	public ChatServer() {
		try {
			//����������socket�˿�
			serverSocket = new ServerSocket(6790); 
			storeInfo = new HashMap<String, PrintWriter>();
			exec = Executors.newCachedThreadPool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ���ͻ��˵���Ϣ��Map��ʽ���뼯����
	private synchronized void putIn(String key,PrintWriter value) {
		storeInfo.put(key, value);
	}
	
	// ��������������ӹ�������ɾ��
	private synchronized void remove(String  key) {
		storeInfo.remove(key);
		System.out.println("��ǰ��������Ϊ��"+ storeInfo.size());
	}
	
	// ����������Ϣת�������пͻ���
	private synchronized void sendToAll(String message) {
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
						//������Ӻ�����Ϣ����ʽ��@friends request from + name��
						if(msgString.startsWith("@friends request from ")) {
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