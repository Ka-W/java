package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;



public class XSLT_Util {
	
	private static List<String> industries,
								redirects;		
			
	
	public XSLT_Util() throws IOException {
		industries = fileToArrayListAndReplaceAmpersand(HelperFunctions.INDUSTRY_GROUPS_SORTED_ON_CODES_CSV);  //& changed to &amp;	
		redirects = HelperFunctions.fileToArrayList(HelperFunctions.REDIRECTS_CSV);
	}
	
	
	public static String getIndustryCode(String industry) {
		String[] entry;
		
		for (String line : industries) {
			entry = line.split("\t");
			
			if (industry.equals("Nonprofit Organization Management")) {
				return "100";  //as Non-Profit Organization Management in file and http://developer.linkedin.com/documents/industry-codes
			}
			if (entry[2].equals(industry)) {
				return entry[0];
			}
			
			
		}
		System.out.println("error: " + industry);
		return null;  
	}
	
	
	public static String replaceNotAllowedCharactersInJenaWithUnicodeAndRedirects(String skillName) {
		String result = replaceNotAllowedCharactersInJenaWithUnicode(skillName);
		String[] redirectArray;
		for (String redirect : redirects) {
			redirectArray = redirect.split("\t");
			if (skillName.equals(redirectArray[0])) {
				result = redirectArray[1];
				System.out.println(redirectArray[0] +  " -> " + redirectArray[1]);
			}
		}
		return result;		
	}
	
	
	public static String replaceNotAllowedCharactersInJenaWithUnicode(String skillName) {
		if(skillName.charAt(0) == '.') {
			skillName = "%2e".concat(skillName.substring(1));		
		}		
		skillName = skillName.replace("*", "%2a");		
		
		return skillName;
	}
	
			
	public static String replaceAmpersand(String input) {		
		return input.replace("&amp;", "&");
	}
	
	
	//not used...
	public static String replace(String input, String pattern, String replacement) {			
		return input.replace(pattern, replacement);
	}
	
	
	//not used...
	public static String changeWikipediaLinkToDBpediaLink(String wikipediaURL) { //http://dbpedia.org/resource/BeanShell
		//wikipediaURL of form: http://www.linkedin.com/redir/redirect?url=http%3A%2F%2Fen%2Ewikipedia%2Eorg%2Fwiki%2FOS%2F2&urlhash=Td1P
		try {
			String result = URLDecoder.decode(wikipediaURL, "UTF-8");			
			return "http://dbpedia.org/resource/" + result.substring(result.indexOf("wiki/")+5, result.indexOf('&'));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return wikipediaURL;
		}		
	}
	
	
	private static ArrayList<String> fileToArrayListAndReplaceAmpersand(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ArrayList<String> result = new ArrayList<String>();
		String line;
		
		while ((line = in.readLine()) != null) {
			result.add(line.replace("&", "&amp;"));
		}
			
		in.close();
		return result;
	}
}
