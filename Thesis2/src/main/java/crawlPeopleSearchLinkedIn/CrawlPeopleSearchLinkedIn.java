package crawlPeopleSearchLinkedIn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.HelperFunctions;

//adapted from http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
public class CrawlPeopleSearchLinkedIn {
	
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",		  	
  			LINKEDIN_USERNAME = "", //Fill in your LinkedIn username
		  	LINKEDIN_PASSWORD = "", //Fill in your LinkedIn password
		  	LINKEDIN_URL = "https://www.linkedin.com/",
		  	LINKEDIN_SUBMIT_URL = "https://www.linkedin.com/uas/login-submit",
		  	LINKEDIN_PEOPLE_SEARCH_QUERY = "https://www.linkedin.com/vsearch/p?type=people&orig=FCTD&pageKey=voltron_people_search_internal_jsp&search=Search&f_G=nl%3A0&"
					+ "pt=people&f_I=0,1,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,"
					+ "42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,"
					+ "85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,"
					+ "121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148&"
					+ "openFacets=G,I&keywords=";
	
	private List<String> cookies;
	private HttpsURLConnection conn;
 	 
	public static void main(String[] args) throws Exception {
		
		System.out.println(LINKEDIN_PEOPLE_SEARCH_QUERY);
 			
		CrawlPeopleSearchLinkedIn http = new CrawlPeopleSearchLinkedIn();
	 
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
	 
		// 1. Send a "GET" request, so that you can extract the form's data.
		String page = http.GetPageContent(LINKEDIN_URL);		
		String postParams = http.getFormParams(page, LINKEDIN_USERNAME, LINKEDIN_PASSWORD);
			 
		// 2. Construct above post's content and then send a POST request for authentication
		http.sendPost(LINKEDIN_SUBMIT_URL, postParams);
		 
		// 3. success then do a people search
		BufferedReader br = new BufferedReader(new FileReader(HelperFunctions.SKILLS_INDUSTRIES_SORTED_ON_CODES_CSV));
		String line, keyword, result;
		String[] entry;
		int filename = 0;
		BufferedWriter bw;
		result = http.GetPageContent(LINKEDIN_PEOPLE_SEARCH_QUERY);  //Get the distribution of industries between people in the Netherlands
		bw = new BufferedWriter(new FileWriter(HelperFunctions.DIRECTORY_CRAWLED_PEOPLE_SEARCH_PAGES + filename + ".html"));
		bw.write(result);
		bw.close();
		while ((line = br.readLine()) != null) {
			filename++;
			if (filename > 15211) {//
				entry = line.split("\t");
				keyword = URLEncoder.encode(entry[1], "UTF-8");
				if (keyword.contains("+")) {
					//System.out.println(peopleSearch + "\"" + keyword + "\""); //debug
				    result = http.GetPageContent(LINKEDIN_PEOPLE_SEARCH_QUERY + "\"" + keyword + "\"");
				} else {
					result = http.GetPageContent(LINKEDIN_PEOPLE_SEARCH_QUERY + keyword);
					//System.out.println(peopleSearch + keyword);
				}			
				bw = new BufferedWriter(new FileWriter(HelperFunctions.DIRECTORY_CRAWLED_PEOPLE_SEARCH_PAGES + filename + ".html"));			
				bw.write(result);
				bw.close();	
			}
		}
		br.close();
	  }
 
	private void sendPost(String url, String postParams) throws Exception {
 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "www.linkedin.com");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "https://www.linkedin.com/");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
	 
		conn.setDoOutput(true);
		conn.setDoInput(true);
	 
		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		
		wr.writeBytes(postParams);
		
		wr.flush();
		wr.close();
	 			 
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close(); 
	}
 
	private String GetPageContent(String url) throws Exception {
 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// default is GET
		conn.setRequestMethod("GET");
	 
		conn.setUseCaches(false);
	 
		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");		
		if (cookies != null) {
			for (String cookie : this.cookies) {			
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
	 
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append(System.getProperty("line.separator"));
		}
		in.close();
	 
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
	 
		return response.toString();	 
	}
 
	private String getFormParams(String html, String username, String password) throws UnsupportedEncodingException {
  
		Document doc = Jsoup.parse(html);
				
		// LinkedIn form id
		Element loginform = doc.getElementById("login");
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");					
			if (key.equals("session_key")) {
				value = username; 
			}else if (key.equals("session_password")){
				value = password;				
			}
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}
	 
		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}
 
	@SuppressWarnings("unused")
	private List<String> getCookies() {
		return cookies;
	}
 
	private void setCookies(List<String> cookies) {
		this.cookies = cookies;
	} 
}