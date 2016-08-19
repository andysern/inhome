package wtbapi;

import java.util.*;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wtbapi.JSONData;








public class WTBServer {


	//private WTBServer wtbSer = null;
	private ServerSocket server = null;						//服务端
	private Socket socket = null;							//客户端
	private static MapSocket mapSocket = null;						//客户端连接socket存储 
	//private WTBServer.SocketHeart socketHeart = null;		//客户端心跳检测
	private String ClientName = null;						//客户端的名字，可根据自己的情况设置前半部分
	private static int ClientNum = 0;						//客户端连接数

	
	
	
	private static  Map<String,Socket> map = new HashMap<String,Socket>();			//全局存储客户端连接socket
	private static Map<String,Integer> mapHeart = new HashMap<String,Integer>();		//全局存储客户端socket心跳
	
	
	
	/**
	 * --------------------------内部类部分-----------------------
	 * 
	 * 
	 * 
	 */
	

	
	/**
	 * 服务器返回数据的获得接口
	 * @author andysern
	 *
	 */
	interface RevDataListener{

		public void RevDataCallback();
				
	}

	/**
	 * 通过服务器发送数据给客户端的接口
	 */
	interface SendDataListener{}
	
	

	
	/**
	 * 检查心跳包的类
	 * @author andysern
	 * WTBServer.mapHeart：name为连接名字
	 * 心跳的规则是：Integer初始化为0，每隔5分钟（这个时间可改变）秒遍历这个map
	 * 将还是0的连接去掉
	 * 
	 */
	private class SocketHeart{
		
		private void setHeart(String name){
			WTBServer.mapHeart.put(name, 0);
		}
		
		private synchronized boolean checkHeart(String name){
			
			boolean isLive = false;
			//	遍历mapHeart，检查心跳
		   	Set<Entry<String, Integer>> setH = WTBServer.mapHeart.entrySet();
			
				  for(Iterator<Entry<String, Integer>> iterH = setH.iterator(); iterH.hasNext();)
				  {
				   Map.Entry entryH = (Map.Entry)iterH.next();			   
				   String keyNameH = (String)entryH.getKey();
				  // Socket value = (Socket)entry.getValue();
			   
				  }
			return isLive;
			}
		}
		
	
	
	/**存储客户端连接的map操作类
	 * 
	 * @author andysern
	 *
	 */
	
	private class MapSocket{
		String name = null;
		Socket socket = null;
		Map map = null;
		
		public void MapSocket(){
		
		}
		
		
		public synchronized void putSocket(String name,Socket socket){
			//判断是否存在这个连接
			boolean isHave = false;	
			String state = null;
			
			//做个map的遍历，看是否已经存在这个名字的连接
			Set<Entry<String, Socket>> set = WTBServer.map.entrySet();
			
			  for(Iterator<Entry<String, Socket>> iter = set.iterator(); iter.hasNext();)
			  {
			   Map.Entry entry = (Map.Entry)iter.next();			   
			   String keyName = (String)entry.getKey();
			   // Socket value = (Socket)entry.getValue();
			   
			   /**
			    * 检查此连接是否已经存在，如果存在就检查其是否还有心跳，如果有心跳
			    * 就不用插入，如果没有心跳，就建立这个连接，并存入WTBServer.map中
			    * 并且，因为mapHeart是检验其心跳的，所以，每次只需检验Heart，而无须
			    * 两个MAP都遍历
			    */
				   if(keyName==name){
					   /**
					    * 此连接存在,会存在集中情况呢？判定为掉线用户
					    * 1.这是一个新来的连接（怎么会是新连接？名字不会重复）
					    * 2.掉线的客户端再尝试连接
					    */
					   WTBServer.map.put(name,socket);
					  isHave = true;	
					  System.out.println("旧连接重连");
						  }
				   }
		  
			  if(isHave == false){
				//没有这个连接，则放入连接map中
				  WTBServer.map.put(name,socket);
				  System.out.println("新连接");
				  //wtbSer.socketHeart.setHeart(name);				  
			  }	
			  
		}
			  
		
		public synchronized Map getSocket(String name){
			map.clear();
			map.put(name,WTBServer.map.get(name)) ;
			
			return this.map;
		}
		
		public Map getServerMap(){
			return WTBServer.map;
		}
	
		
		
	}
	
	
	
	/**	处理读取输入流的类
	 * 
	 * @author andysern
	 *
	 */

	public class SocketReader extends Thread{
		DataInputStream in = null;
		Socket socket = null;
		String name = null;
		String type = null;
		String data = null;
		RevDataListener rdl ;
		String RevContent = null;
	
		
		public void setSocket(Socket socket){
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				System.out.println("read 创建 in 失败");
				e.printStackTrace();
			}
			
			
			
		}
	
		private void setRevContent(String cont){
			this.RevContent = cont;
		}
		
		private String getRevContent(){
			return this.RevContent;
		}
		
		private String getSocketName(){
			return this.name;
		}
		
		private String getSocketType(){
			return this.type;
		}
		
		private String getSocketData(){
			return this.data;
		}
		
		
		@SuppressWarnings("unused")
		private void setRevDataListener(RevDataListener a){
			this.rdl = a;
			

		}
		
		public void run(){
			String con = null;
			JSONData jd = new JSONData();
			
			
			while(true){
				
				try {
					this.RevContent = in.readUTF();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					System.out.println("read-in出错:"+e.toString());
					try{
					socket.close();
					break;
					}catch(IOException e1){
						System.out.println("read socke关闭出错"+e1.toString());
					}
				}
				
				if((con = getRevContent())!=null){
					
					jd.setData(con);
					name = jd.getName();
					data = jd.getData();
					type = jd.getType();
					System.out.println("收到来自"+name+"的消息:"+data);
					if(name.equals("wtb")){
						
						
						name = CreateClientName();
						try {
							//DataOutputStream out = new DataOutputStream(socket.getOutputStream());
							jd.setSendData(name, type,"hello,"+name);
							//out.writeUTF(jd.getSendData());
							//out.flush();
							WTBServer.SocketWriter write = new WTBServer().new SocketWriter();
							write.setSocket(socket);
							write.setContent(jd.getSendData());
							write.start();
							
							
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							System.out.print("read run的问题:"+e.toString());
							e.printStackTrace();
						}
						
					}else System.out.println("名字不是wtb");
					
					//WTBServer.MapSocket mapSocket1 = new WTBServer().new MapSocket();
					mapSocket.putSocket(name, socket);
					//wtbSer.map.put(name, socket);
					System.out.println(map);
					rdl.RevDataCallback();
					this.RevContent = null;
					//System.out.println(mapSocket.getServerMap());
				}
				
			}
			
			
		}
		
	}
	
	
	
	/**处理传送输出流的类
	 * 
	 * @author andysern
	 *
	 */
	
	private class SocketWriter extends Thread{
		DataOutputStream out = null;
		Socket socket = null;
		String content = null;
		SendDataListener sdl = null;
		String state = null;
		
		public void setSocket(Socket socket){
			this.socket = socket;
			
			try {
				//in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				System.out.println("writer出错:"+e.toString());
			}
		}
		
		
		private void setContent(String str){
			this.content = str;
		}
		
		private String getWriteState(){
			return this.state;
		}
		
		private void setSendDataListener(SendDataListener a){
			this.sdl = a;
		}
		
		public void run(){
			try {
				
				out.writeUTF(this.content);
				out.flush();
				//this.state = in.readUTF();
				
				
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				System.out.print("write run的问题:"+e.toString());
			}
		
		}
		
		
		
	}
	
	
	
	/**
	 * ----------------------------主类方法部分-------------------------
	 */
	
	public static void main(String[] args){
		WTBServer wtb = new WTBServer();
		wtb.WTBServerT();//初始化没有创建起
		wtb.CreateServer(2888);

		
		
		
	}
	
	/**
	 * 初始化
	 */
	public void WTBServerT(){
		//wtbSer = new WTBServer();
		mapSocket = new WTBServer().new MapSocket();
		System.out.println("初始化结束");
	}
	/**
	 * 服务器监听方法
	 * 阻塞监听，所有接入的客户端存入到MapSocket类中，其类里有一个全局变量map
	 * 
	 */
	 private void CreateServer(int port){
		 
		 try{
		 server = new ServerSocket(port);
		 System.out.println("服务器创建端口"+port+"成功");
		 }catch(Exception ex){
			 System.out.println("创建服务器监听出错:"+ex.toString());
			 System.out.println("正在重新创建");
			 try{
				 server = new ServerSocket(port+1);
				 System.out.println("创建服务器端口"+(port+1)+"成功");
			 }catch(Exception ex2){
				 System.out.println("再次创建失败"+ex2.toString());
			 }
		 }
		 
		 Thread se = new Thread(new Runnable(){
			 
			 public void run(){
				 while(true){
				 		
				 		//进入阻塞，监听客户端的socket进入
				 		try{
				 		socket = server.accept();
				 		}catch(Exception ex){
				 			System.out.println("客户端接收出错:"+ex.toString());
				 		}
				 		
				 		
				 		
				 		if(socket!=null){
				 			System.out.println(socket);
				 			try {
				 				//创建一个新的socket来存入map
				 				Socket socketC = new Socket();
				 				
								//DataInputStream in = new DataInputStream(socket.getInputStream());
								//DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								
								//String aa = in.readUTF();
								
								WTBServer.SocketReader read1 = new WTBServer().new SocketReader();
					 			//read1.setSocket(socket,in,out);
								read1.setSocket(socket);
					 			read1.setRevDataListener(new RevDataListener(){
					 				public void RevDataCallback(){
					 					System.out.println("回调函数");
					 				}
					 			});
					 			read1.start();
					 			
					 			
					 			 
					 			//System.out.println(aa);
					 			socket = null;
							} catch (Exception e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
				 			
				 			
				 			
				 			
				 		
				 			
				 		}
				 		
				 	}
			 }
		 });
		 se.start();
	
	 }
	 
	 private synchronized String CreateClientName(){
		 ClientName = "wtb"+(ClientNum++);
		 return ClientName;
	 }
	 

	
}
