package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HelperFunctions {
	
	
	public static final String CURRENT_WORKING_DIRECTORY = System.getProperty("user.dir"),
			FILE_SEPARATOR = System.getProperty("file.separator"),
			LN = System.getProperty("line.separator"),
	    	RESOURCES = CURRENT_WORKING_DIRECTORY + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "resources" + FILE_SEPARATOR, 
	    	LOG_FILE = RESOURCES + "logFiles" + FILE_SEPARATOR,
	    	EXPERIMENT_RESULTS_LIST = RESOURCES + "EXPERIMENT_RESULTS_LIST",
	    	EXPERIMENT_RESULTS_LIST_T = RESOURCES + "EXPERIMENT_RESULTS_LIST_T",
	    	INDUSTRY_GROUPS_SORTED_ON_CODES_CSV = RESOURCES  + "industryCodesGroupsSortedOnCodesCSV.csv",
	    	INDUSTRY_GROUPS_SORTED_ON_GROUPS_CSV = RESOURCES  + "industryCodesGroupsSortedOnGroupsCSV.csv",
	    	SKILLS_INDUSTRIES_CSV = RESOURCES + "skillsIndustryCodesRelatedSkillsCSV.csv",
	    	SKILLS_INDUSTRIES_SORTED_ON_CODES_CSV = RESOURCES  + "skillsIndustryCodesRelatedSkillsSortedOnCodesCSV.csv",
	    	SKILL_INDUSTRY_DISTRIBUTIONS_NETHERLANDS_CSV = RESOURCES + "skillIndustryDistributionsNetherlands.csv",
 	    	HTML_SKILL_PAGES_ZIP = RESOURCES + "htmlpages.zip",
	    	HTML_CRAWLED_PEOPLE_SEARCH_PAGES_ZIP = RESOURCES + "crawledPeopleSearchPagesLinkedIn.zip",
	    	REDIRECTS_CSV = RESOURCES + "redirectsLinkedInSkillsCSV.csv",
	    	XSL_FILE = RESOURCES + "skillsIndustryCodesRelatedSkillsToCSV_XSL.xsl",
	    	LIVE_JOURNAL_GZIP = RESOURCES + "com-lj.ungraph.txt.gz",
	    	LIVE_JOURNAL_GZIP_T = RESOURCES + "com-lj.ungraphT.txt.gz",
	    	ORKUT_GZIP = RESOURCES + "com-orkut.ungraph.txt.gz",
	    	FACEBOOK_GJOKA_GZIP = RESOURCES + "Facebook_Gjoka_uni-socialgraph-anonymized.txt.gz",
	    	FACEBOOK_EGO_GZIP = RESOURCES + "facebook_combined.txt.gz",	    	
	    	LIVE_JOURNAL_CONNECTIONS_INT_ARRAY = RESOURCES + "liveJournalIntArray",
	    	LIVE_JOURNAL_CONNECTIONS_INT_ARRAY_T = RESOURCES + "liveJournalIntArrayT",
	    	LIVE_JOURNAL_NODES_INT_ARRAY = RESOURCES + "liveJournalNodesIntArray",
	    	LIVE_JOURNAL_NODES_INT_ARRAY_T = RESOURCES + "liveJournalNodesIntArrayT",
	    	LIVE_JOURNAL_INDUSTRY_NODE_SKILL_INT_ARRAY = RESOURCES + "LiveJournal_industry_NodeSkill2dArray_1",
	    	LIVE_JOURNAL_INDUSTRY_NODE_SKILL_INT_ARRAY_T = RESOURCES + "LiveJournal_T_industry_NodeSkill2dArray_1",
	    	JUNG_GRAPH = RESOURCES + "jungGraph",
	    	JUNG_GRAPH_T = RESOURCES + "jungGraphT",
	    	JUNG_PAGERANK_T = RESOURCES + "jungPageRankT",
	    	JUNG_PAGERANK = RESOURCES + "jungPageRank",
	    	ORKUT_INT_CONNECTIONS_ARRAY = RESOURCES + "orkutIntArray",	    	
	    	ORKUT_INT_NODES_ARRAY = RESOURCES + "orkutNodesIntArray",
	    	FACEBOOK_GJOKA_CONNECTIONS_INT_ARRAY = RESOURCES + "facebookGjokaIntArray",
	    	FACEBOOK_GJOKA_NODES_INT_ARRAY = RESOURCES + "facebookGjokaNodesIntArray",
	    	FACEBOOK_EGO_CONNECTIONS_INT_ARRAY = RESOURCES + "facebookEgoIntArray",
	    	FACEBOOK_EGO_NODES_INT_ARRAY = RESOURCES + "facebookEgoNodesIntArray",
	    	SKILL_DISTRIBUTION_FLOAT_ARRAY = RESOURCES + "skillDistributionFloatArray",
	    	DIRECTORY_CRAWLED_PEOPLE_SEARCH_PAGES = CURRENT_WORKING_DIRECTORY + HelperFunctions.FILE_SEPARATOR + "crawledPeopleSearchPagesLinkedIn" + HelperFunctions.FILE_SEPARATOR;
	    	
			
	
	
	private HelperFunctions(){
		throw new AssertionError();
	}
		
		
	public static String leftAlign(String s, int numberOfSpaces) { //adapted from: http://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java/391978#391978
	    return String.format("%" + numberOfSpaces + "s%s", "", s);  
	}	  
	
	
	public static ArrayList<String> fileToArrayList(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ArrayList<String> result = new ArrayList<String>();
		String line;
		
		while ((line = in.readLine()) != null) {
			result.add(line);
		}
			
		in.close();
		return result;
	}
	
	
	//from: http://stackoverflow.com/questions/10404698/saving-arrays-to-the-hard-disk
	public static Object loadObject(String path) throws IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
	    Object result = null;
		try {
			result = (Object) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    in.close();
		return result;
	}	
	
	
	//from: http://stackoverflow.com/questions/10404698/saving-arrays-to-the-hard-disk:
	public static void saveObject(Object o, String path) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
		out.writeObject(o);
	    out.close();
	}
	
	
	//from: http://andreinc.net/2010/12/12/serialize-java-objects-using-gzip-streams-gzipinputstream-and-gzipoutputstream/
	public static void saveObjectGZipped(Object vlo, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gos);
		
		oos.writeObject(vlo);
		
		oos.close();
		gos.close();
		fos.close();		
	}
	
	//from: http://andreinc.net/2010/12/12/serialize-java-objects-using-gzip-streams-gzipinputstream-and-gzipoutputstream/
	public static Object loadObjectGZipped(String fileName) throws IOException {
		Object obj = null;
		FileInputStream fis = new FileInputStream(fileName);
		GZIPInputStream gis = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gis);
		
		try {
			obj = ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ois.close();
		gis.close();
		fis.close();
		
		return obj;
	}
	
	
	//Fisher–Yates shuffle: http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
	//From: http://en.algoritmy.net/article/43676/Fisher-Yates-shuffle:
	/**
	* An improved version (Durstenfeld) of the Fisher-Yates algorithm with O(n) time complexity
	* Permutes the given array
	* @param array array to be shuffled
	*/	
	public static void fisherYatesShuffle(int[] array) {
		Random r = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			int index = r.nextInt(i);
			//swap
			int tmp = array[index];
			array[index] = array[i];
			array[i] = tmp;
		}	
	}	
}
