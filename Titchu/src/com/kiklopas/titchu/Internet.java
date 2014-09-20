package com.kiklopas.titchu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.HttpParametersUtils;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class Internet {
	public Vector<String> lines;
	Runnable onFinish;
	String status;
	private HttpResponseListener listener;
	private HttpRequest con;
	private HashMap<String, String> param;
	private boolean isActive = false;
	private String response;
	ExecutorService executor = Executors.newFixedThreadPool(1);
	Thread t;
	long dt = 0;
	
	Runnable listenerRunnable;
	public boolean sync = false;
	
	Internet(){
		init();
	}
	private void init() {
		lines= new Vector<String>();
		param = new HashMap<String, String>();
		con = new HttpRequest(HttpMethods.POST);
		status = "null";
		listenerRunnable = new Runnable(){
			@Override
			public void run() {
				//dt = -System.nanoTime();
				try{
	            	status = "success";
	            	String[] l = response.split("&");
		  	        for(int i=0;i<l.length;i++){
		  	        	lines.add(l[i]);
		  	        	//System.out.println(l[i]);
		  	        }
		  	        //isActive = false;
		  	        if( onFinish != null )onFinish.run();
	            }
	            catch(Exception e){status="failed";System.out.print("INTERNET EXCEPTION\n");e.printStackTrace();}
				//dt += System.nanoTime();
				//System.out.print("Time:"+dt+" - > "+ Thread.currentThread().getName() + "\n");
			}
		};
		listener = new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				response = httpResponse.getResultAsString();
				listenerRunnable.run();
				//executor.execute(listenerRunnable);
	        }
	        public void failed(Throwable t) {
	                status = "failed";
	                isActive = false;
	                clear();
	        }
		};
	}
	public void setURL(String name){
		con.setUrl(name);	
	}
	public void setParam(String name, String value){
		param.put(name, value);
	}
	public String getFirstLine(){
		return (lines.size() == 0 )? null : lines.get(0);
	}
	public boolean request(){
		  if( isActive ){System.out.print("Unexpected Request while Busy!");return false;}
		  if( con.getUrl() == null ){System.out.print("URL not set!");return false;}
		  isActive = true;

		  synchronized (this){
		  status = "pending";
		  con.setContent(HttpParametersUtils.convertHttpParameters(param));
		 
		  ///// NEW METHOD
		  //System.out.print(HttpParametersUtils.convertHttpParameters(param)+"\n\n");
		  response = executePost(con.getUrl(),HttpParametersUtils.convertHttpParameters(param));
		  listenerRunnable.run();
		  isActive = false;
		  ///////
		  //Gdx.net.sendHttpRequest (con, listener);
		  }
	      return true;
	}
	public void clear() {
		onFinish = null;
		lines.clear();
		status = "null";
		isActive = false;
		sync = false;
		param.clear(); // Replaced the next line!
		//param = new HashMap<String,String>();
	}
	public boolean isActive() {
		return isActive;
	}
	public boolean isBusy() {
		return isActive;
	}
	/*
	 * Shouldnt be used! ..
	 */
	public void disable(){
		isActive = false;
	}
	public void printLines(){
		System.out.println("PRINTLINES");
		for(int i=0;i<lines.size();i++){
			System.out.println(lines.get(i));
		}
		System.out.println("END2");
		return;	
	}
	public static String executePost(String targetURL, String urlParameters)
	  {
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", 
	           "application/x-www-form-urlencoded");
	      connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");  
	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);
	      //Send request
	      DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
	      wr.writeBytes (urlParameters);
	      wr.flush ();
	      wr.close ();
	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	      }
	      rd.close();
	      return response.toString();

	    } catch (Exception e) {

	      e.printStackTrace();
	      return null;

	    } finally {

	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	  }
}
