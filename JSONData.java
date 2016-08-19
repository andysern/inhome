package wtbapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * ���ݰ�����json�ķ�ʽ����װ
 * ���ݹ��������ݰ��ǣ�
 * {"nameId":clientName,"dataType":type,"data":revData}
 * clientName:�Ƿ�����������ͻ��˵�Ψһʶ���
 * dataType:�����ݵ�����,'0'Ϊ��������'1'Ϊ������Ϣ(������)
 */

public class JSONData {

	String name = null;
	String data = null;
	String type = null;
	String sendData = null;
	JSONObject jsonObject = null;
	JSONArray jsonArray = null;
	
	public void setData(String a){
		try {
			jsonObject = new JSONObject(a);
			name = jsonObject.getString("nameId");
			type = jsonObject.getString("dataType");
			data = jsonObject.getString("data");
			
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public String getData(){
		return data;
	}
	
	public String getSendData(){
		return sendData;
	}
	
	public void setSendData(String name,String type,String data){
		this.sendData = "{'nameId':'"+name+"','dataType':'"+type+"','data':'"+data+"'}";
	}
}
