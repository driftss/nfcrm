package com.sd.farmework.services.util.wx.msg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WechatSendMsg {
	private static String wxAppId = "wx1d3b41e646164be4"; // ΢��AppId
	private static String wxSecret = "555b435983fb071ee219e109359f9cd9"; // ΢����Կ
	private static String wxServer = "https://api.weixin.qq.com"; // ΢�ŷ�������ַ

    //��ȡ΢�ŷ��ص�access_token
    private static String getAccess_token() {
        String access_token=null;
        StringBuffer action =new StringBuffer();
        action.append(wxServer+"/cgi-bin/token?grant_type=client_credential")
        .append("&appid="+wxAppId) //���÷���ŵ�appid
        .append("&secret="+wxSecret); //���÷���ŵ��ܳ�
        URL url;
        try {
              //ģ��get����
            url = new URL(action.toString());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();        
            http.setRequestMethod("GET");        
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");    
            http.setDoInput(true);
            InputStream is =http.getInputStream();
            int size =is.available();
            byte[] buf=new byte[size];
            is.read(buf);
            String resp =new String(buf,"UTF-8");
            JSONObject json = JSONObject.fromObject(resp);
            Object object =json.get("access_token");
            if(object !=null){
                  access_token =String.valueOf(object);
            }
            System.out.println("getAccess_token:"+access_token);
            return access_token;
        } catch (MalformedURLException e) {
            e.printStackTrace();
             return access_token;
             
        } catch (IOException e) {
            e.printStackTrace();
             return access_token;
         
        }
    }
    //��ȡ�÷�����µ��û���
    public static JSONArray getOpenids(){
        JSONArray array =null;
        String urlstr =wxServer+"/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
        urlstr =urlstr.replace("ACCESS_TOKEN", getAccess_token());
        urlstr =urlstr.replace("NEXT_OPENID", "");
        URL url;
        try {
            url = new URL(urlstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();        
            http.setRequestMethod("GET");        
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");    
            http.setDoInput(true);
            InputStream is =http.getInputStream();
            int size =is.available();
            byte[] buf=new byte[size];
            is.read(buf);
            String resp =new String(buf,"UTF-8");
            JSONObject jsonObject =JSONObject.fromObject(resp);
            System.out.println("getOpenids:"+jsonObject.toString());
            array =jsonObject.getJSONObject("data").getJSONArray("openid");
            return array;
        } catch (MalformedURLException e) {
            e.printStackTrace();
             return array;
             
        } catch (IOException e) {
            e.printStackTrace();
             return array;
        }
    }
    //�����û����openId��ȡ�û���ϸ����
    public JSONObject getUserOpenids(String openId){
        String urlstr =wxServer+"/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        urlstr =urlstr.replace("ACCESS_TOKEN", getAccess_token());
        urlstr =urlstr.replace("OPENID", openId);
        urlstr =urlstr.replace("NEXT_OPENID", "");
        URL url;
        try {
            url = new URL(urlstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();        
            http.setRequestMethod("GET");        
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");    
            http.setDoInput(true);
            InputStream is =http.getInputStream();
            int size =is.available();
            byte[] buf=new byte[size];
            is.read(buf);
            String resp =new String(buf,"UTF-8");
            JSONObject jsonObject =JSONObject.fromObject(resp);
            System.out.println("getUserOpenids:"+jsonObject.toString());
            return jsonObject;
        } catch (MalformedURLException e) {
            e.printStackTrace();
             return null;
             
        } catch (IOException e) {
            e.printStackTrace();
             return null;
        }
    }
     /**
      * ���͸�ָ��ĳ��OpenId
      * @param openId
      */
    public static void testsendTextByOpenids(String openId){
       String urlstr =wxServer+"/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN"; //���Ϳͷ�ͼ����Ϣ
        urlstr =urlstr.replace("ACCESS_TOKEN", getAccess_token());
        String reqjson =createGroupText(getOpenids(),openId);
        try {
            URL httpclient =new URL(urlstr);
            HttpURLConnection conn =(HttpURLConnection) httpclient.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(2000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");    
            conn.setDoOutput(true);        
            conn.setDoInput(true);
            conn.connect();
            OutputStream os= conn.getOutputStream();    
            System.out.println("ccccc:"+reqjson);
            os.write(reqjson.getBytes("UTF-8"));//�������    
            os.flush();
            os.close();
            
            InputStream is =conn.getInputStream();
            int size =is.available();
            byte[] jsonBytes =new byte[size];
            is.read(jsonBytes);
            String message=new String(jsonBytes,"UTF-8");
            System.out.println("testsendTextByOpenids:"+message);
         
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    //�������͵�����
    private static String createGroupText(JSONArray array,String openId){
         JSONObject gjson =new JSONObject();
         //JSONObject json = getUserOpenids(array.get(3).toString()); //array�������û������е��û�,�÷�����ӡarray����һ���û�����ϸ��Ϣ
         gjson.put("touser", openId);
         gjson.put("msgtype", "news");
         JSONObject news =new JSONObject();
         JSONArray articles =new JSONArray();
         JSONObject list =new JSONObject();
         list.put("title","����"); //����
         list.put("description","���ͼ�ļ��"); //����
         list.put("url","http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E8%B5%B5%E4%B8%BD%E9%A2%96&oq=%E8%B5%B5%E4%B8%BD%E9%A2%96&rsp=-1"); //���ͼ��������ת�ĵ�ַ
         list.put("picurl","http://n.sinaimg.cn/eladies/transform/20160907/q6aR-fxvqctu6458264.jpg"); //ͼ�����ӵ�ͼƬ
         articles.add(list);
         news.put("articles", articles);
         JSONObject text =new JSONObject();
         text.put("test1", "������");
         gjson.put("text", text);
         gjson.put("news", news);
         
        return gjson.toString();
    }
	public static void main(String[] args) {
	       String openId="oFYpzwQIXaJSOfnJEb1oeBLINW9w";
         
		  testsendTextByOpenids(openId);
		  
	}     
}
