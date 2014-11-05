package edu.temple.rollcall.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class API  {
	static final String APIBaseURL = "http://cis-linux2.temple.edu/~tud04734/api/";
	
	private static String makeAPICall(Context context, String api) throws ClientProtocolException, IOException {
    	AndroidHttpClient client = AndroidHttpClient.newInstance("Android", context);
    	HttpGet method = new HttpGet(APIBaseURL + api);
    	method.addHeader("Accept-Encoding", "gzip");
    	HttpResponse httpResponse = client.execute(method);
    	String response = extractHttpResponse(httpResponse);
    	client.close();
        return response.toString();
	}
	
	private static String extractHttpResponse(HttpResponse httpResponse) throws IllegalStateException, IOException{
    	InputStream instream = httpResponse.getEntity().getContent();
        Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding"); 
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream);
        }      
        BufferedReader r = new BufferedReader(new InputStreamReader(instream));      
        StringBuilder response = new StringBuilder();
        String line = "";       
        while ((line = r.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
	
	/**
	 * Get all upcoming sessions for the specified student.
	 * @param context
	 * @param student_id
	 * @return On success, returns a JSON object containing "status":"ok" and an array called "sessions".
	 * 		   If there are no upcoming sessions, return a JSON object containing "status":"empty".
	 * 		   On failure, returns a JSON object containing "status":"error" and a MySQL error number called "errno".
	 * See getsessionsforstudent.php for more details.
	 */
	public static JSONObject getSessionsForStudent(Context context, String student_id) {
		try {
			String responseStr = makeAPICall(context, "getsessionsforstudent.php?student_id=" + student_id);
			return new JSONObject(responseStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check in the specified student to the specified session.
	 * @param context
	 * @param student_id
	 * @param session_id
	 * @return On success, returns a JSON object containing "status":"ok".
	 * 		   On failure, returns a JSON object containing "status":"error" and a MySQL error number called "errno".
	 * See checkin.php for more details.
	 */
	public static JSONObject checkIn(Context context, String student_id, String session_id) {
		String responseStr;
		try {
			responseStr = makeAPICall(context, "checkin.php?student_id=" + student_id + "&session_id=" + session_id);
			return new JSONObject(responseStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check out the specified student from the specified session
	 * @param context
	 * @param student_id
	 * @param session_id
	 * @return On success, returns a JSON object containing "status":"ok".
	 * 		   On failure, returns a JSON object containing "status":"error" and a MySQL error number called "errno".
	 * See checkout.php for more details.
	 */
	public static boolean checkOut(Context context, String student_id, String session_id) {
		try {
			String response = makeAPICall(context, "checkout.php?student_id=" + student_id + "&session_id=" + session_id);
			if(response.equals("success")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Authenticate the email and password and provide account info.
	 * @param context
	 * @param email
	 * @param password
	 * @return On success, returns a JSON object containing "status":"ok" and account info in an object called "account".
	 * 		   If the password is incorrect, returns a JSON object containing "status":"incorrect_password".
	 * 		   If there is no account associated with the email, returns a JSON object containing "status":"incorrect_email".
	 * 		   On failure, returns a JSON object containing "status":"error" and a MySQL error number called "errno".
	 * See login.php for more details.
	 */
	public static JSONObject login(Context context, String email, String password) {
		try {
			String response = makeAPICall(context, "login.php?email=" + email + "&password=" + password);
			return new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}