package lrebelo.examples.simplesocketexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.*;

/**
 * Android APP that exemplifies the use of 
 * 	 Sockets & HTML POST
 *
 *	@author Luis Monteiro Rebelo
 *			l.rebelo@kingston.ac.uk
 *	@since October 2012
 */

public class MainActivity extends Activity {
	
	String tmp = "NOTHING!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button sendBt = (Button)findViewById(R.id.button1);
        Button sendPOST = (Button)findViewById(R.id.button2);
        
        final EditText ipBox = (EditText)findViewById(R.id.editText1);
        final EditText portBox = (EditText)findViewById(R.id.editText2);
        final EditText commandBox = (EditText)findViewById(R.id.editText3);
        
        final TextView returnBox = (TextView)findViewById(R.id.textView5);

        sendBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Editable ed  = ipBox.getText();
                Editable ed1  = portBox.getText();
                Editable ed2  = commandBox.getText();
                
                String port = ed1.toString();
            	int portInt = Integer.parseInt(port);
            	String ip = ed.toString();
            	String command = ed2.toString();
            	
            	ServerConn conn = new ServerConn(ip, portInt, command);
            	Thread sockT = new Thread(conn);
            	sockT.start();
            	
            	while(sockT.isAlive()){
            		
            	}
            	
            	Log.d("lrebelo_test", "msg back: "+tmp);
            	returnBox.setText(tmp);
            	
            }
		});
        
 
        sendPOST.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	Editable ed  = ipBox.getText();
                Editable ed2  = commandBox.getText();
                
            	String ip = ed.toString();
            	String command = ed2.toString();
            	
            	if((command.substring(0, 3)).equals("db;")){
            		
            		String[] msgB = command.split(";");
    				String c1 = msgB[1];
    				String c2 = msgB[2];
                	
    				HTTPConn conn = new HTTPConn(ip, c1, c2);
                	Thread httpT = new Thread(conn);
                	httpT.start();
                	
                	while(httpT.isAlive()){
                	}
                	returnBox.setText("Sent by http!");
            	}else{
            		returnBox.setText("command not correctly formated");
            	}
            	
            }
		});

    }
    
    
    class ServerConn implements Runnable 
    {
    	String ip;
    	int port;
    	String command;
    	
    	ServerConn(String ipBox, int portBox, String commandBox)
    	{
    		ip = ipBox;
    		port = portBox;
    		command = commandBox;
    	}

		public void run() {   

        	try {
        		Log.d("lrebelo_test", "ip: "+ip +" - port: "+port);
				Socket sendRecSock = new Socket(ip, port);
				
				PrintWriter outWr = new PrintWriter(sendRecSock.getOutputStream());
				
				outWr.print(command);
				outWr.flush();
				
				sendRecSock.shutdownOutput();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(sendRecSock.getInputStream()));
		        final String r = in.readLine();
		        tmp = r;
		        
		        Log.d("lrebelo_test", "msg back inTHREAD: "+r);
		        
		        sendRecSock.close();
		        
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
    }
    
    
    class HTTPConn implements Runnable 
    {
    	String ip;
    	String c1;
    	String c2;
    	
    	HTTPConn(String ipH, String msg1, String msg2){
    		ip = ipH;
    		c1 = msg1;
    		c2 = msg2;
    	}

		public void run() {
			
			HttpClient httpclient = new DefaultHttpClient();
        	
        	HttpPost post = new HttpPost("http://"+ip+"/Add.php");
        	
        	List<NameValuePair> msgTosend = new ArrayList<NameValuePair>();
        	msgTosend.add(new BasicNameValuePair("n", c1));
        	msgTosend.add(new BasicNameValuePair("m", c2));
        	
        	try {
				post.setEntity(new UrlEncodedFormEntity(msgTosend));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	
        	try {
				HttpResponse response = httpclient.execute(post);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
    }
    
}
