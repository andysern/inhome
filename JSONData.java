package wtbapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 数据包，以json的方式来封装
 * 传递过来的数据包是：
 * {"nameId":clientName,"dataType":type,"data":revData}
 * clientName:是服务器分配给客户端的唯一识别号
 * dataType:是数据的类型,'0'为心跳包，'1'为报告信息(可扩充)
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
			// TODO 自动生成的 catch 块
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
