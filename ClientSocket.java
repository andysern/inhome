package wtbapi;

//import wtbapi.WTBServer;

import java.util.*;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;
import java.io.*;
import java.net.Socket;
import wtbapi.JSONData;








import java.net.UnknownHostException;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

public class ClientSocket {
	private Socket socket = null;
	private static String myName = "wtb";
	private JSONData jd = null;
	
	
	
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		for(int i = 0;i<5;i++){
		ClientSocket cs = new ClientSocket();
		cs.setSocket("127.0.0.1", 2888);
		ClientReader cr = cs.new ClientReader();
		cr.setSocket(cs.getSocket());
		System.out.println(cs.getSocket());
		cr.start();
		}
	}
	
	/**
	 * ----------------------------�ڲ���------------------------------------
	 * @param args
	 */

	private class ClientReader extends Thread{
		DataInputStream in = null;
		DataOutputStream out = null;
		Socket socket = null;
		String content = null;
		
		private void setSocket(Socket socket){
			this.socket = socket;
			
			
		}
		
		public void run(){
			
			try {
				in = new DataInputStream(this.socket.getInputStream());
				out = new DataOutputStream(this.socket.getOutputStream());
			
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				System.out.println("reader��������:"+e.toString());
				e.printStackTrace();
			}
			
			//�Ȼ�ȡ�Լ�������
			try {
				out.writeUTF("{'nameId':"+myName+",'dataType':'1','data':'1111111'}");
				out.flush();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
			
			
			while(true){
				try {
					content = in.readUTF();
					if(content!=null){
						jd = new JSONData();
						jd.setData(content);
						myName = jd.getName();
						System.out.println(content);
						content=null;
						
						new Thread(new Runnable(){
							public void run(){
								try {
									Scanner typeIn = new Scanner(System.in);
									String dataIn = typeIn.next(); 
									out.writeUTF("{'nameId':"+myName+",'dataType':'1','data':"+dataIn+"}");
									out.flush();
								} catch (IOException e) {
									// TODO �Զ����ɵ� catch ��
									e.printStackTrace();
								}
								
							}
							
						}).start();
						
					}
					
				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
					break;
				}
				
				}
			}
		
		}
		
		
	
	
	private class ClientWriter{}
	
	
	
	/**
	 * ---------------------------��ķ�������---------------------------------
	 * @param args
	 */
	public void ClientSocket(){
		
		System.out.println("��ʼ������");
	}
	
	private void setSocket(String ip,int port){
		try {
			this.socket = new Socket(ip,port);
			System.out.println("�����ɹ�");
		} catch (UnknownHostException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	
	private Socket getSocket(){
		return this.socket;
	}

}
