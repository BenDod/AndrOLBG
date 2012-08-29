package com.bendod.androlbg.connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Pair;

import com.bendod.androlbg.AndrOLBGApplication;
import com.bendod.androlbg.R;
import com.bendod.androlbg.Settings;
import com.bendod.androlbg.enumerations.StatusCode;
import com.bendod.androlbg.network.Network;
import com.bendod.androlbg.utils.Utils;

public class Login {

	private static boolean actualLoginStatus = false;
	private static String actualUserName = "";
	private static int actualUserId = -1;
	private static int actualUnsettledTips = -1;
	private static int actualVirtualMoney = -1;
    private static String actualStatus = "";
    
    public static CookieManager cookieManager = new CookieManager();  
        
	public static StatusCode login() throws IOException, URISyntaxException {
		final Pair<String, String> login = Settings.getLogin();

		if (login == null || login.first.isEmpty() || login.second.isEmpty()) {
            Login.setActualStatus(AndrOLBGApplication.getInstance().getString(R.string.err_login));
            return StatusCode.NO_LOGIN_INFO_STORED;
        }
		
		Login.setActualStatus(AndrOLBGApplication.getInstance().getString(R.string.init_login_popup_working));
		
		CookieHandler.setDefault(cookieManager);
		
		HttpURLConnection con = Network.getRequest("http://www.olbg.com/members/", null);
		InputStream in = con.getInputStream();
		String loginData = null;
		if(con.getResponseCode()==HttpURLConnection.HTTP_OK){
			loginData = Network.getResponseData(in);			
			if(Utils.isBlank(loginData)){
				return StatusCode.CONNECTION_FAILED; // no login page
	        }
			if (Login.getLoginStatus(loginData)) {
				return StatusCode.NO_ERROR; // logged in
			}
		}
		if(con.getResponseCode()==HttpURLConnection.HTTP_UNAVAILABLE){
			return StatusCode.MAINTENANCE;
		}
		in.close();
		con.disconnect();
				
		cookieManager.getCookieStore().removeAll();
		
		String params="pword="+URLEncoder.encode(login.second,"UTF-8")+
				"&uname="+URLEncoder.encode(login.first,"UTF-8");
		
		String uri = "http://www.olbg.com/members/login/login.php";
		con = Network.postRequest(uri, params);
		in = con.getInputStream();
		if(con.getResponseCode()==HttpURLConnection.HTTP_MOVED_TEMP){
		 	loginData = Network.getResponseData(in);
			in.close();
			if(Utils.isBlank(loginData)){
				con.disconnect();
				uri = new URI(uri).resolve(con.getHeaderField("Location")).toString();
				con = Network.getRequest(uri , null);
				in = con.getInputStream();
				if(con.getResponseCode()==HttpURLConnection.HTTP_OK){
					loginData = Network.getResponseData(in);
				}					
			}
			if (Login.getLoginStatus(loginData)) {
				return StatusCode.NO_ERROR; // logged in
			}	
			if (loginData.contains("You entered an invalid username and password combination.")) {
				return StatusCode.WRONG_LOGIN_DATA; // wrong login
			}
		}
		in.close();
		con.disconnect();
		
		return StatusCode.UNKNOWN_ERROR; // can't login
	}
	
	public static StatusCode logout() {
		URL url;
		HttpURLConnection con = null;
		InputStream in;
		//String logoutData;
		try {
			url = new URL("http://www.olbg.com/members/logout.php");
			con = (HttpURLConnection) url.openConnection();
			in = con.getInputStream();
			//logoutData = in.toString();
			in.close();
			if(con.getResponseCode()==503){
				return StatusCode.MAINTENANCE;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}	
		cookieManager.getCookieStore().removeAll();
        return StatusCode.NO_ERROR;
	}
	
	public static int getActualUserId() {
        return actualUserId;
    }

	static void setActualUserId(final int found) {
        actualUserId = found;
    }

	public static int getActualUnsettledTips() {
        return actualUnsettledTips;
    }

	public static void setActualUnsettledTips(final int found) {
        actualUnsettledTips = found;
    }

	public static int getActualVirtualMoney() {
        return actualVirtualMoney;
    }

	public static void setActualVirtualMoney(final int found) {
		actualVirtualMoney = found;
    }

    public static String getActualStatus() {
        return actualStatus;
    }

	private static void setActualStatus(final String status) {
        actualStatus = status;
    }
	
	public static boolean isActualLoginStatus() {
        return actualLoginStatus;
    }

    private static void setActualLoginStatus(boolean loginStatus) {
        actualLoginStatus = loginStatus;
    }

    public static String getActualUserName() {
        return actualUserName;
    }

    private static void setActualUserName(String userName) {
        actualUserName = userName;
    }

    		
	/**
     * Check if the user has been logged in when he retrieved the data.
     *
     * @param page
     * @return <code>true</code> if user is logged in, <code>false</code> otherwise
     */
    public static boolean getLoginStatus(final String page) {
        if(Utils.isBlank(page)){
        	return false;
        }
        
        setActualStatus(AndrOLBGApplication.getInstance().getString(R.string.init_login_popup_ok));

        setActualLoginStatus(Utils.matches(page, OLBGConstants.PATTERN_LOGIN_NAME));
        if (isActualLoginStatus()) {
        	setActualUserName(Utils.getMatch(page, OLBGConstants.PATTERN_LOGIN_NAME, true, "???"));
        	int tmpUserId = Integer.parseInt(Utils.getMatch(page, OLBGConstants.PATTERN_USER_ID, true, "-1"));
        	if(tmpUserId!=-1){
        		setActualUserId(tmpUserId);
        	}
        	int tmpUnsettledTips = Integer.parseInt(Utils.getMatch(page, OLBGConstants.PATTERN_UNSETTLED_TIPS, true, "-1"));
        	if(tmpUnsettledTips!=-1){
        		setActualUnsettledTips(tmpUnsettledTips);
        	}
        	setActualVirtualMoney(Integer.parseInt(Utils.getMatch(page, OLBGConstants.PATTERN_VIRTUAL_MONEY, true, "0").replaceAll("[,.]", "")));
            return true;
        }
        setActualStatus(AndrOLBGApplication.getInstance().getString(R.string.init_login_popup_failed));
        return false;
    }

    public static String postRequestLogged(final String uri, final String params) throws IOException, URISyntaxException {
    	HttpURLConnection conn = Network.postRequest(uri, params);
    	InputStream in = conn.getInputStream();
    	String data = null;
    	if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
    		data = Network.getResponseData(in);
    	}
    	conn.disconnect();        
        in.close();

        if (!getLoginStatus(data)) {
            if (login() == StatusCode.NO_ERROR) {
            	conn = Network.postRequest(uri, params);
            	in = conn.getInputStream();
            	if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                	data = Network.getResponseData(in);
            	}else{
            		data = null;
            	}
                conn.disconnect();
                in.close();
            } else {
            	return null;
            }
        }
        return data;
	}
    
    public static String getRequestLogged(final String uri, final String params, final Boolean checkLogin) 
			throws IOException, URISyntaxException {
    	HttpURLConnection conn = Network.getRequest(uri, params);
    	InputStream in = conn.getInputStream();
    	String data = null;
    	if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
    		data = Network.getResponseData(in);
    	}
    	conn.disconnect();        
        in.close();

        if (checkLogin && !getLoginStatus(data)) {
            if (login() == StatusCode.NO_ERROR) {
            	conn = Network.getRequest(uri, params);
            	in = conn.getInputStream();
            	if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                	data = Network.getResponseData(in);
            	}else{
            		data = null;
            	}
                conn.disconnect();
                in.close();
            } else {
            	return null;
            }
        }
        return data;
    }
}
