package com.wanhutong.backend.common.utils;

import org.apache.commons.io.input.BOMInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class InterfaceUtil {

	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @return
	 */
	public static String PostRequestForXML(String postData,String postUrl){
		return PostRequest(postData,postUrl,"text/xml; charset=utf-8");
	}
	
	public static String PostRequestForXML(String p12FilePath, String p12Pwd, String postData, String postUrl) {
		return PostRequest(p12FilePath, p12Pwd, postData, postUrl, "text/xml; charset=utf-8");
	}

	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @return
	 */
	public static String PostRequestForJSON(String postData,String postUrl){
		return PostRequest(postData,postUrl,"application/json; charset=utf-8");
	}
	
	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @param contenttype
	 * @return
	 */
	public static String PostRequest(String postData,String postUrl,String contenttype){
		return Request(postData,postUrl,contenttype,"POST");
	}
	
	public static String PostRequest(String p12FilePath, String p12Pwd, String postData, String postUrl,
			String contenttype) {
		return Request(p12FilePath, p12Pwd, postData, postUrl, contenttype, "POST");
	}

	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @return
	 */
	public static String GetRequestForXML(String postData,String postUrl){
		return GetRequest(postData,postUrl,"text/xml; charset=utf-8");
	}
	
	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @return
	 */
	public static String GetRequestForJSON(String postData,String postUrl){
		return GetRequest(postData,postUrl,"application/json; charset=utf-8");
	}
	
	
	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @param contenttype
	 * @return
	 */
	public static String GetRequest(String postData,String postUrl,String contenttype){
		return Request(postData,postUrl,contenttype,"GET");
	}
	
	/**
	 * 传入请求参数数据和接口地址获取第三方平台提供的数据
	 * @param postData
	 * @param postUrl
	 * @param contenttype
	 * @param method
	 * @return
	 */
	public static String Request(String postData,String postUrl,String contenttype,String method){
		try {
			URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", contenttype);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput("POST".equals(method)?true:false);

            
            if("POST".equals(method)){
                conn.setRequestProperty("Content-Length", "" + postData.length());
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                out.write(postData);
                out.flush();
                out.close();
            }


            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("connect failed!");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            
//            InputStream is = conn.getInputStream();
//            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//            swapStream.write("feff".getBytes("UTF-16"), 0, 3);
//            byte[] buff = new byte[1];  
//	        int rc = 0;
//	        rc = is.read(buff, 0, 1);
//	        swapStream.write(buff, 0, rc);
//	        buff = new byte[2]; 
//	        byte[] oldbuff = new byte[2]; 
//	        boolean turnFlg = false;
//	        while ((rc = is.read(buff, 0, 2)) > 0) {  
//	        	if(buff[0] !=0){
//	        		oldbuff[0] = buff[0];
//	        		turnFlg =true;
//	        	}else {
//	        		if(turnFlg){
//	        			oldbuff[0] = buff[0];
//	        			turnFlg=false;
//	        		}
//	        	}
//	        	
//	        	if(oldbuff[1] != 0)swapStream.write(oldbuff);
//	        	
//        		oldbuff[0] = buff[0];
//	        	oldbuff[1] = buff[1];
//	              
//	        }  
//	        swapStream.flush();
//	        swapStream.close();
//            System.out.println(swapStream.toString("UTF-16"));
            
            BOMInputStream bomIn = new BOMInputStream(conn.getInputStream(),false);
//            System.out.println( bomIn.hasBOM());
//            System.out.println(bomIn.getBOMCharsetName());
            BufferedReader in = new BufferedReader(new InputStreamReader(bomIn, "UTF-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
            
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}	
		return "";
	}
	
	public static String Request(String p12FilePath, String p12Pwd, String postData, String postUrl, String contenttype,
			String method) {
		try {

			URL url = new URL(postUrl);
			KeyStore ks = KeyStore.getInstance("PKCS12");
			char[] password = p12Pwd.toCharArray();
			FileInputStream instream = new FileInputStream(new File(p12FilePath));
			ks.load(instream, password);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, password);
			SSLContext ssl = SSLContext.getInstance("TLS");
			ssl.init(kmf.getKeyManagers(), null, null);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssl.getSocketFactory());
			conn.setRequestMethod(method);
			conn.setRequestProperty("Content-Type", contenttype);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput("POST".equals(method) ? true : false);

			if ("POST".equals(method)) {
				conn.setRequestProperty("Content-Length", "" + postData.length());
				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				out.write(postData);
				out.flush();
				out.close();
			}

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("connect failed!");
				return "";
			}
			// 获取响应内容体
			String line, result = "";

			// InputStream is = conn.getInputStream();
			// ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			// swapStream.write("feff".getBytes("UTF-16"), 0, 3);
			// byte[] buff = new byte[1];
			// int rc = 0;
			// rc = is.read(buff, 0, 1);
			// swapStream.write(buff, 0, rc);
			// buff = new byte[2];
			// byte[] oldbuff = new byte[2];
			// boolean turnFlg = false;
			// while ((rc = is.read(buff, 0, 2)) > 0) {
			// if(buff[0] !=0){
			// oldbuff[0] = buff[0];
			// turnFlg =true;
			// }else {
			// if(turnFlg){
			// oldbuff[0] = buff[0];
			// turnFlg=false;
			// }
			// }
			//
			// if(oldbuff[1] != 0)swapStream.write(oldbuff);
			//
			// oldbuff[0] = buff[0];
			// oldbuff[1] = buff[1];
			//
			// }
			// swapStream.flush();
			// swapStream.close();
			// System.out.println(swapStream.toString("UTF-16"));

			BOMInputStream bomIn = new BOMInputStream(conn.getInputStream(), false);
			// System.out.println( bomIn.hasBOM());
			// System.out.println(bomIn.getBOMCharsetName());
			BufferedReader in = new BufferedReader(new InputStreamReader(bomIn, "UTF-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			in.close();
			return result;

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return "";
	}

	public static String RequestForUTF16(String postData,String postUrl,String contenttype,String method){
		try {
			URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", contenttype);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput("POST".equals(method)?true:false);

            
            if("POST".equals(method)){
                conn.setRequestProperty("Content-Length", "" + postData.length());
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                out.write(postData);
                out.flush();
                out.close();
            }


            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("connect failed!");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            swapStream.write("feff".getBytes("UTF-16"), 0, 3);
            byte[] buff = new byte[1];  
	        int rc = 0;
	        rc = is.read(buff, 0, 1);
	        swapStream.write(buff, 0, rc);
	        buff = new byte[2]; 
	        byte[] oldbuff = new byte[2]; 
	        boolean turnFlg = false;
	        while ((rc = is.read(buff, 0, 2)) > 0) {  
	        	if(buff[0] !=0){
	        		oldbuff[0] = buff[0];
	        		turnFlg =true;
	        	}else {
	        		if(turnFlg){
	        			oldbuff[0] = buff[0];
	        			turnFlg=false;
	        		}
	        	}
	        	
	        	if(oldbuff[1] != 0)swapStream.write(oldbuff);
	        	
        		oldbuff[0] = buff[0];
	        	oldbuff[1] = buff[1];
	              
	        }  
	        swapStream.flush();
	        swapStream.close();
            System.out.println(swapStream.toString("UTF-16"));
            
//            BOMInputStream bomIn = new BOMInputStream(conn.getInputStream(),false);
////            System.out.println( bomIn.hasBOM());
////            System.out.println(bomIn.getBOMCharsetName());
//            BufferedReader in = new BufferedReader(new InputStreamReader(bomIn, "UTF-8"));
//            while ((line = in.readLine()) != null) {
//                result += line + "\n";
//            }
//            in.close();
            return swapStream.toString("UTF-16");
            
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}	
		return "";
	}
	
	/**
	 * ssl请求
	 * @param requestUrl 路径
	 * @param requetMethod 方法
	 * @param outputStr 数据
	 * @return
	 */
	public static String httpsRequst(String requestUrl,String requetMethod,String outputStr){
		StringBuffer buffer=new StringBuffer();
		String result ="";
		try
		{
			//创建SSLContext对象，并使用我们指定的新人管理器初始化
			TrustManager[] tm={new MyX509TrustManager()};
			SSLContext sslcontext=SSLContext.getInstance("SSL","SunJSSE");
			sslcontext.init(null, tm, new java.security.SecureRandom());
			//从上述SSLContext对象中得到SSLSocktFactory对象
			SSLSocketFactory ssf=sslcontext.getSocketFactory();
			
			URL url=new URL(requestUrl);
			HttpsURLConnection httpUrlConn=(HttpsURLConnection)url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			//设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requetMethod);
			
			if("GET".equalsIgnoreCase(requetMethod))
				httpUrlConn.connect();
			
			//当有数据需要提交时
			if(null!=outputStr)
			{
			OutputStream outputStream=httpUrlConn.getOutputStream();
			//注意编码格式，防止中文乱码
			outputStream.write(outputStr.getBytes("UTF-8"));
			outputStream.close();
			}
			
			//将返回的输入流转换成字符串
			InputStream inputStream=httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"utf-8");
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			
			
			String str=null;
			while((str = bufferedReader.readLine()) !=null)
			{ 
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			//释放资源
			inputStream.close();
			inputStream=null;
			httpUrlConn.disconnect();
			result=buffer.toString();
		}
		catch (ConnectException ce) {
			ce.printStackTrace();
		}
		catch (Exception e) {  
			e.printStackTrace();
		}
		return result;
	}
	
	 private static class MyX509TrustManager implements X509TrustManager
	 {

	 	@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
	 			throws CertificateException {

	 	}

	 	@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
	 			throws CertificateException {

	 	}

	 	@Override
		public X509Certificate[] getAcceptedIssuers() {
	 		return null;
	 	}
	 }
	 
	   
	 
}
