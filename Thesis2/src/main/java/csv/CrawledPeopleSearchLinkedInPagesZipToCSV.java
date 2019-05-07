package csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import utils.HelperFunctions;


public class CrawledPeopleSearchLinkedInPagesZipToCSV {
	
    private final static int NR_OF_INDUSTRIES = 148; //including industry '0' indicating no industry filled in on profile. Industry 2 is not used by LinkedIn.
        
    private static int[] skillDistributionPerIndustry = new int[NR_OF_INDUSTRIES+1];  //+1 because of also including how often the skill occurs in total regardless of industry
    private static BufferedWriter bw;
    
    public static void main(String[] args) {   	
    	try {    		
			new File(HelperFunctions.SKILL_INDUSTRY_DISTRIBUTIONS_NETHERLANDS_CSV).delete();
			//from: http://www.javamex.com/tutorials/compression/zip_individual_entries.shtml#.UsVyPdLuJlw:			
			ZipFile zipFile = new ZipFile(HelperFunctions.HTML_CRAWLED_PEOPLE_SEARCH_PAGES_ZIP);
			
			InputStream input;
			StringBuilder htmlPage = new StringBuilder(1024);
		    byte[] buffer = new byte[1024];
		    int read = 0;
		    bw = new BufferedWriter(new FileWriter(HelperFunctions.SKILL_INDUSTRY_DISTRIBUTIONS_NETHERLANDS_CSV));
			for (int i = 0; i < zipFile.size(); i++) {			
				ZipEntry entry = zipFile.getEntry(i + ".html");
				input = zipFile.getInputStream(entry);
				while ((read = input.read(buffer, 0, 1024)) >= 0) { //transform InputStream to StringBuilder, from: http://stackoverflow.com/questions/13311856/write-zipentry-data-to-string
					htmlPage.append(new String(buffer, 0, read));
				}		       	       		
				transformLinkedInSearchResultsPageToCSV(htmlPage.toString());
		       	htmlPage.setLength(0);
		    }		    
			zipFile.close();
			bw.close();
    	} catch (IOException e) {
		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}		
	}
    
    
    private static void transformLinkedInSearchResultsPageToCSV(String htmlPage) throws IOException {
       	String json = htmlPage.substring(htmlPage.indexOf("-{")+1, htmlPage.indexOf("}-")+1); //get the json part of the html page
    	String cleanedJson = json.replace(":\\u002d1", ":\"\\u002d1\"");
				
		//http://docs.oracle.com/javaee/7/tutorial/doc/jsonp003.htm
		JsonReader reader = Json.createReader(new StringReader(cleanedJson));
		JsonStructure jsonst = reader.readObject();
		
		jsonToSkillDistributionPerIndustryArray(jsonst, null, false, -1); //navigateTree(jsonst, null);
		skillDistributionPerIndustryArrayToCSV();
    }
    
    
    //adapted from https://docs.oracle.com/javaee/7/tutorial/jsonp003.htm navigateTree(JsonValue tree, String key)
    private static void jsonToSkillDistributionPerIndustryArray(JsonValue tree, String key, boolean relevant, int value) {
    	switch(tree.getValueType()) {
	        case OBJECT:    			
    	        JsonObject object = (JsonObject) tree;    	       
    	        for (String name : object.keySet()) {
    	        	if (name.equals("isSelected")) {
    	        		relevant = object.getBoolean(name);
    	        	}
    	        }
    	        for (String name : object.keySet()) {
    	        	if (relevant && name.equals("count")) {
    	        		value = object.getInt(name);
    	        	}
    	        	jsonToSkillDistributionPerIndustryArray(object.get(name), name, relevant, value);
    	        }
    	        break;
    	    case ARRAY:
     	        JsonArray array = (JsonArray) tree;
     	        for (JsonValue val : array)
     	        	jsonToSkillDistributionPerIndustryArray(val, null, relevant, value);
     	        break;
    	    case STRING:
    	    	JsonString st = (JsonString) tree;
    	    	String industry;
    	        if (relevant && key.equals("value") && !(industry = st.getString()).equals("nl:0")) {    	        	   	        	
    	        	skillDistributionPerIndustry[Integer.parseInt(industry)] = value;
    	        } else if (relevant && key.equals("value") && (industry = st.getString()).equals("nl:0")) {    	        	
    	        	skillDistributionPerIndustry[2] = value;
    	        }
    	        break;
    	    case NUMBER:
    	    case TRUE:
    	    case FALSE:
    	    case NULL:    	    	
    	        break;    	
    	}
    }
    
    
    private static void skillDistributionPerIndustryArrayToCSV() throws IOException { //debug
    	for(int i = 0; i < NR_OF_INDUSTRIES + 1; i++) {
    		bw.write(String.valueOf(skillDistributionPerIndustry[i]));    		
    		if (i < NR_OF_INDUSTRIES) {
    			bw.write("\t");
    		} 
    	}
    	bw.write("\n");
    }
}