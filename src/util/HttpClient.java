package util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClient {
	
	private List<String> cookies=new ArrayList<String>();

	public String SendGet(String url, Map<String, String> params,Map<String,String> header) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url + (params == null ? "" : mapToString(params)))
					.openConnection();
			setHeader(conn,header);
			InputStream in = conn.getInputStream();
			saveCookie(conn);
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String result = "";
			String line = null;
			while ((line = bf.readLine()) != null) {
				result += line+"\n";
			}
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getRedirectAddr(String url, Map<String, String> params,Map<String,String> header) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url + (params == null ? "" : mapToString(params)))
					.openConnection();
			conn.setInstanceFollowRedirects(false);
			setHeader(conn,header);
			//InputStream in = conn.getInputStream();
			conn.connect();
			saveCookie(conn);
			return conn.getHeaderField("Location");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void saveCookie(HttpURLConnection conn) {
		List<String> tmp=conn.getHeaderFields().get("Set-Cookie");
		if(tmp!=null){
			for(String str : tmp){
				String tmpCookie=str.split(";")[0];
				if(!tmpCookie.endsWith("=")){
					cookies.add(str.split(";")[0]);
				}
			}
		}
		
	}

	private void setHeader(HttpURLConnection conn,Map<String,String> header) {
		if(cookies.size() > 0){
		    conn.setRequestProperty("Cookie",String.join("; ", cookies));    
		}
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		
	}

	public void SaveData(String url, Map<String, String> params,Map<String,String> header, String path) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url + (params == null ? "" : mapToString(params)))
					.openConnection();
			setHeader(conn, header);
			InputStream in = conn.getInputStream();
			saveCookie(conn);

			FileOutputStream fos = new FileOutputStream(path);
			byte[] b = new byte[1024];
			while ((in.read(b)) != -1) {
				fos.write(b);
			}
			in.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String mapToString(Map<String, String> params) {
		String result = "";
		for (Map.Entry<String, String> entry : params.entrySet()) {
			result += "&" + entry.getKey() + "=" + entry.getValue();
		}
		return result;
	}
}




