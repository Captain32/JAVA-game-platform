import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**总体思想：
 * 将用户的昵称作为索引的标志
 * 进而实现用户之间的通信
 * 分为私聊和广播
 * 广播用于：挑衅和播报最新的记录
 */


public class ChatServer {	
	
	private ServerSocket serverSocket;
	
	/**
	* 创建线程池来管理客户端的连接线程
	* 避免系统资源过度浪费
	*/
	private ExecutorService exec;
	
	// 存放客户端之间私聊的信息
	private Map<String, PrintWriter> storeInfo;
	
	public ChatServer() {
		try {
			//建立服务器socket端口
			serverSocket = new ServerSocket(6790); 
			storeInfo = new HashMap<String, PrintWriter>();
			exec = Executors.newCachedThreadPool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 将客户端的信息以Map形式存入集合中
	private synchronized void putIn(String key,PrintWriter value) {
		storeInfo.put(key, value);
	}
	
	// 将给定的输出流从共享集合中删除
	private synchronized void remove(String  key) {
		storeInfo.remove(key);
		System.out.println("当前在线人数为："+ storeInfo.size());
	}
	
	// 将给定的消息转发给所有客户端
	private synchronized void sendToAll(String message) {
		for(PrintWriter out: storeInfo.values()) {
			out.println(message);
		}
		//System.out.println("fuck");
	}
	
	// 将给定的消息转发给私聊的客户端
	private synchronized void sendToSomeone(String name, String message) {
		//将对应客户端的聊天信息取出作为私聊内容发送出去
		PrintWriter pw = storeInfo.get(name); 
		//System.out.println(pw);
		//System.out.println(message);
		if(pw != null) pw.println(message);	
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("等待客户端连接... ... ");
				Socket socket = serverSocket.accept();

				// 获取客户端的ip地址
				InetAddress address = socket.getInetAddress();
				System.out.println("客户端：“" + address.getHostAddress() + "”连接成功！ ");
				/*
				 * 启动一个线程，由线程来处理客户端的请求，这样可以再次监听
				 * 下一个客户端的连接
				 */
				exec.execute(new ServerClient(socket)); //通过线程池来分配线程
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* 该线程体用来处理给定的某一个客户端的消息，循环接收客户端发送
	* 的每一个字符串，并输出到控制台
	*/
	class ServerClient implements Runnable {

		private Socket socket;
		private String name;
				
		public ServerClient(Socket socket) {
			this.socket = socket;
			//this.run();
		}
		
		// 创建内部类来获取昵称
		private String getName() throws Exception {
			try {
				//服务端的输入流读取客户端发送来的昵称输出流
				BufferedReader bReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
				//服务端将昵称验证结果通过自身的输出流发送给客户端
				PrintWriter ipw = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);

				//读取客户端发来的昵称
				//可以用来检测是否是重复登陆
				while(true) {
					String nameString = bReader.readLine();
					//如果读入的姓名为空或者已经登陆，然后一直处于准备读信息的状态
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
				* 通过客户端的Socket获取客户端的输出流
				* 用来将消息发送给客户端
				*/
				PrintWriter pw = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				/*
				* 将客户昵称和其所说的内容存入共享集合HashMap中
				*/
				name = getName();
				putIn(name, pw);
				Thread.sleep(100);
				
				// 服务端通知所有客户端，某用户上线
				sendToAll("[系统通知] “" + name + "”已上线");


				//System.out.println("hello!");
				/*
				* 通过客户端的Socket获取输入流
				* 读取客户端发送来的信息
				*/
				BufferedReader bReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String msgString = null;
				
				while(true) {
					//System.out.println("煞笔");
					while((msgString = bReader.readLine()) != null) {
						//申请添加好友信息（格式：@friends request from + name）
						if(msgString.startsWith("@friends request from ")) {
							String toName = msgString.substring(new String("@friends request from ").length(),
									msgString.length());
							//如果好友在线
							if(storeInfo.containsKey(toName)) {
								sendToSomeone(toName, msgString);
							}
						}
						// 检验是否为私聊（格式：@发送对象昵称:内容）
						else if(msgString.startsWith("@")) {
							int index = msgString.indexOf(":");
							if(index >= 0) {
								//获取昵称
								String toName = msgString.substring(1, index);
								String info = msgString.substring(index + 1);
								if(info.startsWith("@friends request from ")) {
									//如果是好友申请
									info = info.substring(new String("@friends request from ").length());
									info = "Request:" + name;//发送给用户的格式："Request:" + name
									//System.out.println(toName);
									//System.out.println(info);
									//这里可能需要加上写txt的过程
								}
								//如果是同意申请
								else if(info.startsWith("Agree by:")) {
									System.out.println(toName);
									System.out.println(info);
								}
								//如果是聊天内容
								else info =  name + ":"+ info;//发送给用户的格式：name + ":" + msg
								//将私聊信息发送出去
								sendToSomeone(toName, info);
							}
						}
						//用户发送请求，查询该好友是否上线
						else if(msgString.startsWith("$$$")) {
							String theName = msgString.substring(3, msgString.length());
							if(storeInfo.containsKey(theName)) {
								pw.println("Online");
							}
							else pw.println("Offline");
						}
						else {
							// 遍历所有输出流，将该客户端发送的信息转发给所有客户端
							System.out.println(name+"："+ msgString);
							sendToAll(name+"："+ msgString);
						}
					}	
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				remove(name);
				// 通知所有客户端，某某客户已经下线
				sendToAll("[系统通知] "+name + "已经下线了。");
				
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