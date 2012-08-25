package com.bendod.androlbg.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

public class Network
{

	private static boolean followRedirects = false;

	public static void Network() {
	}
	
	private static HttpURLConnection request(String uri) throws IOException {
		URL url = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setInstanceFollowRedirects(followRedirects );
		return con;
	}

	public static HttpURLConnection postRequest(String uri, String params) throws IOException {
		HttpURLConnection con = request(uri);
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setFixedLengthStreamingMode(params.getBytes().length);
		OutputStream out = con.getOutputStream();
		out.write(params.getBytes());
		out.close();		
		return con;
	}

	public static HttpURLConnection getRequest(String uri, String params) throws IOException {
		return request(uri + "?" + params);
	}
	
	public static String getResponseData(final InputStream in) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		r.close();
		return total.toString();
	}

	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo(); 
		if (netInfo != null && netInfo.isConnected()) { 
			return true; 
		}
		return false;
	}
	
}
