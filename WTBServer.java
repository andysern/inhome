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
	private ServerSocket server = null;						//�����
	private Socket socket = null;							//�ͻ���
	private static MapSocket mapSocket = null;						//�ͻ�������socket�洢 
	//private WTBServer.SocketHeart socketHeart = null;		//�ͻ����������
	private String ClientName = null;						//�ͻ��˵����֣��ɸ����Լ����������ǰ�벿��
	private static int ClientNum = 0;						//�ͻ���������

	
	
	
	private static  Map<String,Socket> map = new HashMap<String,Socket>();			//ȫ�ִ洢�ͻ�������socket
	private static Map<String,Integer> mapHeart = new HashMap<String,Integer>();		//ȫ�ִ洢�ͻ���socket����
	
	
	
	/**
	 * --------------------------�ڲ��ಿ��-----------------------
	 * 
	 * 
	 * 
	 */
	

	
	/**
	 * �������������ݵĻ�ýӿ�
	 * @author andysern
	 *
	 */
	interface RevDataListener{

		public void RevDataCallback();
				
	}

	/**
	 * ͨ���������������ݸ��ͻ��˵Ľӿ�
	 */
	interface SendDataListener{}
	
	

	
	/**
	 * �������������
	 * @author andysern
	 * WTBServer.mapHeart��nameΪ��������
	 * �����Ĺ����ǣ�Integer��ʼ��Ϊ0��ÿ��5���ӣ����ʱ��ɸı䣩��������map
	 * ������0������ȥ��
	 * 
	 */
	private class SocketHeart{
		
		private void setHeart(String name){
			WTBServer.mapHeart.put(name, 0);
		}
		
		private synchronized boolean checkHeart(String name){
			
			boolean isLive = false;
			//	����mapHeart���������
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
		
	
	
	/**�洢�ͻ������ӵ�map������
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
			//�ж��Ƿ�����������
			boolean isHave = false;	
			String state = null;
			
			//����map�ı��������Ƿ��Ѿ�����������ֵ�����
			Set<Entry<String, Socket>> set = WTBServer.map.entrySet();
			
			  for(Iterator<Entry<String, Socket>> iter = set.iterator(); iter.hasNext();)
			  {
			   Map.Entry entry = (Map.Entry)iter.next();			   
			   String keyName = (String)entry.getKey();
			   // Socket value = (Socket)entry.getValue();
			   
			   /**
			    * ���������Ƿ��Ѿ����ڣ�������ھͼ�����Ƿ������������������
			    * �Ͳ��ò��룬���û���������ͽ���������ӣ�������WTBServer.map��
			    * ���ң���ΪmapHeart�Ǽ����������ģ����ԣ�ÿ��ֻ�����Heart��������
			    * ����MAP������
			    */
				   if(keyName==name){
					   /**
					    * �����Ӵ���,����ڼ�������أ��ж�Ϊ�����û�
					    * 1.����һ�����������ӣ���ô���������ӣ����ֲ����ظ���
					    * 2.���ߵĿͻ����ٳ�������
					    */
					   WTBServer.map.put(name,socket);
					  isHave = true;	
					  System.out.println("����������");
						  }
				   }
		  
			  if(isHave == false){
				//û��������ӣ����������map��
				  WTBServer.map.put(name,socket);
				  System.out.println("������");
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
	
	
	
	/**	�����ȡ����������
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
				// TODO �Զ����ɵ� catch ��
				System.out.println("read ���� in ʧ��");
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
					// TODO �Զ����ɵ� catch ��
					System.out.println("read-in����:"+e.toString());
					try{
					socket.close();
					break;
					}catch(IOException e1){
						System.out.println("read socke�رճ���"+e1.toString());
					}
				}
				
				if((con = getRevContent())!=null){
					
					jd.setData(con);
					name = jd.getName();
					data = jd.getData();
					type = jd.getType();
					System.out.println("�յ�����"+name+"����Ϣ:"+data);
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
							// TODO �Զ����ɵ� catch ��
							System.out.print("read run������:"+e.toString());
							e.printStackTrace();
						}
						
					}else System.out.println("���ֲ���wtb");
					
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
	
	
	
	/**���������������
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
				// TODO �Զ����ɵ� catch ��
				System.out.println("writer����:"+e.toString());
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
				// TODO �Զ����ɵ� catch ��
				System.out.print("write run������:"+e.toString());
			}
		
		}
		
		
		
	}
	
	
	
	/**
	 * ----------------------------���෽������-------------------------
	 */
	
	public static void main(String[] args){
		WTBServer wtb = new WTBServer();
		wtb.WTBServerT();//��ʼ��û�д�����
		wtb.CreateServer(2888);

		
		
		
	}
	
	/**
	 * ��ʼ��
	 */
	public void WTBServerT(){
		//wtbSer = new WTBServer();
		mapSocket = new WTBServer().new MapSocket();
		System.out.println("��ʼ������");
	}
	/**
	 * ��������������
	 * �������������н���Ŀͻ��˴��뵽MapSocket���У���������һ��ȫ�ֱ���map
	 * 
	 */
	 private void CreateServer(int port){
		 
		 try{
		 server = new ServerSocket(port);
		 System.out.println("�����������˿�"+port+"�ɹ�");
		 }catch(Exception ex){
			 System.out.println("������������������:"+ex.toString());
			 System.out.println("�������´���");
			 try{
				 server = new ServerSocket(port+1);
				 System.out.println("�����������˿�"+(port+1)+"�ɹ�");
			 }catch(Exception ex2){
				 System.out.println("�ٴδ���ʧ��"+ex2.toString());
			 }
		 }
		 
		 Thread se = new Thread(new Runnable(){
			 
			 public void run(){
				 while(true){
				 		
				 		//���������������ͻ��˵�socket����
				 		try{
				 		socket = server.accept();
				 		}catch(Exception ex){
				 			System.out.println("�ͻ��˽��ճ���:"+ex.toString());
				 		}
				 		
				 		
				 		
				 		if(socket!=null){
				 			System.out.println(socket);
				 			try {
				 				//����һ���µ�socket������map
				 				Socket socketC = new Socket();
				 				
								//DataInputStream in = new DataInputStream(socket.getInputStream());
								//DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								
								//String aa = in.readUTF();
								
								WTBServer.SocketReader read1 = new WTBServer().new SocketReader();
					 			//read1.setSocket(socket,in,out);
								read1.setSocket(socket);
					 			read1.setRevDataListener(new RevDataListener(){
					 				public void RevDataCallback(){
					 					System.out.println("�ص�����");
					 				}
					 			});
					 			read1.start();
					 			
					 			
					 			 
					 			//System.out.println(aa);
					 			socket = null;
							} catch (Exception e) {
								// TODO �Զ����ɵ� catch ��
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
