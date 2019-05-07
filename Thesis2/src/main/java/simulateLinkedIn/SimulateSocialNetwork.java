package simulateLinkedIn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import utils.HelperFunctions;

public class SimulateSocialNetwork {
	
	
	//private final static String NODE_SEPERATOR_IN_FILE = "\t";
	
	private final static int NR_OF_INDUSTRIES = 148; 
							 
							 
	//private static int nrOfDutchLinkedInMembers;	
							 
	
	public static void main(String[] args) throws IOException {		
		//int[] orkutConnections = getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.ORKUT_GZIP, HelperFunctions.ORKUT_INT_ARRAY);
		//int[] orkutNodes = getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.ORKUT_GZIP, HelperFunctions.ORKUT_INT_ARRAY), HelperFunctions.ORKUT_NODES_INT_ARRAY);
		
		int[] liveJournalConnections = getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.LIVE_JOURNAL_GZIP, HelperFunctions.LIVE_JOURNAL_CONNECTIONS_INT_ARRAY, 1);
		System.out.println(liveJournalConnections.length);
		int[] liveJournalNodes = getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(liveJournalConnections, HelperFunctions.LIVE_JOURNAL_NODES_INT_ARRAY);
		System.out.println(liveJournalNodes.length);
		//In one line:
		//int[] liveJournalNodes = getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.LIVE_JOURNAL_GZIP, HelperFunctions.LIVE_JOURNAL_CONNECTIONS_INT_ARRAY), HelperFunctions.LIVE_JOURNAL_NODES_INT_ARRAY);
		
		int[] facebookGjokaConnections = getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.FACEBOOK_GJOKA_GZIP, HelperFunctions.FACEBOOK_GJOKA_CONNECTIONS_INT_ARRAY, 2);		
		System.out.println(facebookGjokaConnections.length);
		int[] facebookGjokaNodes = getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(facebookGjokaConnections, HelperFunctions.FACEBOOK_GJOKA_NODES_INT_ARRAY);
		System.out.println(facebookGjokaNodes.length);
		
		int[] facebookEgoConnections = getSocialNetworkConnectionsIntArrayFromDiskOrGZip(HelperFunctions.FACEBOOK_EGO_GZIP, HelperFunctions.FACEBOOK_EGO_CONNECTIONS_INT_ARRAY, 3);		
		System.out.println(facebookEgoConnections.length);
		int[] facebookEgoNodes = getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(facebookEgoConnections, HelperFunctions.FACEBOOK_EGO_NODES_INT_ARRAY);
		System.out.println(facebookEgoNodes.length);
		
		
		/*float[] skillDistributionArray = getSkillDistributionArrayFromDiskOrCSV(HelperFunctions.SKILL_INDUSTRY_DISTRIBUTIONS_NETHERLANDS_CSV, HelperFunctions.SKILL_DISTRIBUTION_FLOAT_ARRAY, (NR_OF_SKILLS+1)*(NR_OF_INDUSTRIES+1));
				
		//for a certain number of simulation runs: {
		int run = 1;
		int[][] industryNode2dArray = toIndustryNode2dArray(skillDistributionArray, liveJournalNodes);
		int[][] industry_NodeSkill2dArray = toIndustryNodeSkill2dArray(skillDistributionArray, industryNode2dArray, liveJournalNodes);
		HelperFunctions.saveObject(industryNode2dArray, HelperFunctions.RESOURCES + "LiveJournal_" + "industryNode2dArray_" + run);		
		HelperFunctions.saveObject(industry_NodeSkill2dArray, HelperFunctions.RESOURCES + "LiveJournal_" + "industry_NodeSkill2dArray_" + run);*/
		
		//run++
		//}		
	}
		
		
	private static int[] saveSocialNetworkGZipAsIntArrayOnDisk(String gzipFile, String path, int fileFormat) throws IOException {
	 	String line;
		String[] edge;
		int[] result = new int[0];
		int current, previous = -1, 
			nrOfEdges, length = 0; 
		BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipFile))));
		String nodeSeperator;
				
		
		if(fileFormat == 1 || fileFormat == 3) {
			nodeSeperator = "\t";
			//determine number of edges:
			if(fileFormat == 1)  {				
				while ((line = br.readLine()) != null) {
					if(line.startsWith("# Nodes: ")) { 
						//nrOfNodes = Integer.valueOf(line.split(" ")[2]);
						nrOfEdges = Integer.valueOf(line.split(" ")[4]);
						length = nrOfEdges;
						break;
					} 
				}
				br.readLine();
			} else if(fileFormat == 3) {
				nodeSeperator =  " ";
				while ((line = br.readLine()) != null) {
					length++;
				}
				br.close();
				br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipFile))));
			}
			
			//determine how large array 'result'  has to be:
			while ((line = br.readLine()) != null) {
				if ((current = Integer.valueOf(line.split(nodeSeperator)[0])) != previous) {			
					length = length + 1; //nrOFEdges + 'unique start nodes with connections in file'
					previous = current;
				}			
			}
			br.close();			
			
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipFile))));
			result = new int[length];
			while ((line = br.readLine()) != null) {
				if(!line.startsWith("#")) {//read comments away							
					break;
				}
			}					
			edge = line.split(nodeSeperator);
			current = Integer.valueOf(edge[0]);
			previous = current;
			result[0] = -current; 
			result[1] = Integer.valueOf(edge[1]);			
			int i = 1;
			while ((line = br.readLine()) != null) {
				i++;
				edge = line.split(nodeSeperator);
				current = Integer.valueOf(edge[0]);
				if (current != previous) { 
					result[i] = -current;
					previous = current;
					i++;								
				}
				result[i] = Integer.valueOf(edge[1]);				
			}
		
		} else if(fileFormat == 2) {
			nodeSeperator =  " ";
			//determine how large array 'result'  has to be:
			while ((line = br.readLine()) != null) {
				length = length + line.split(nodeSeperator).length - 1; //minus one because file format is: <uid> <#times sampled> <friend_uid_1> <friend_uid_2> .. <friend_uid_j>
			}
			br.close();
			
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipFile))));
			result = new int[length];
			int i = 0;
			//int k = 0; //debug
			while ((line = br.readLine()) != null) {
				//if(k++ > 10) break; //debug
				edge = line.split(nodeSeperator);
				//System.out.println("edge.length " + edge.length); //debug
				for (int j = 0; j < edge.length; j++ ) {
					if(j == 0) {
						//System.out.println(-Integer.valueOf(edge[0])); //debug
						result[i++] = -Integer.valueOf(edge[0]);
					}else if (j > 1) { //omit j=1 because that represents <#times sampled> field of file
						result[i++] = Integer.valueOf(edge[j]);
						//System.out.println(Integer.valueOf(edge[j])); //debug
					}					
				}				
			}			
		}
		br.close();
		HelperFunctions.saveObject(result, path);
		return result;
	}
	
	
	private static int[] getSocialNetworkConnectionsIntArrayFromDiskOrGZip(String gzipFile, String path, int fileFormat) throws IOException {
		if(new File(path).exists()) {			
			return (int[]) HelperFunctions.loadObject(path);
		} else {
			return saveSocialNetworkGZipAsIntArrayOnDisk(gzipFile, path, fileFormat);
		}
	}
	
	/*private static float[] saveCSV_SkillDistributionIntoArray(String skillDistributionCSV, String path, int length) throws IOException {
		String line;
		float[] result = new float[length];
		BufferedReader br = new BufferedReader(new FileReader(skillDistributionCSV));
		String[] splittedRowArray;
		int i = 0, nrOfDutchLinkedInMembers = 0;		
		
		while ((line = br.readLine()) != null) {
			splittedRowArray = line.split("\t");
			if(i == 0) {
				nrOfDutchLinkedInMembers = Integer.valueOf(splittedRowArray[2]);
			}
			for(String value : splittedRowArray) {
				result[i++] = (float) Integer.valueOf(value)/nrOfDutchLinkedInMembers;				
			}				
		}
		br.close();		
		HelperFunctions.saveObject(result, path);
		
		return result;
	}*/
	
	
	/*private static float[] getSkillDistributionArrayFromDiskOrCSV(String skillDistributionCSV, String path, int length) throws IOException {
		if(new File(path).exists()) {			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
		    float[] result = null;
			try {
				result = (float[]) in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    in.close();
			return result;
		} else {
			return saveCSV_SkillDistributionIntoArray(skillDistributionCSV, path, length);
		}
	}*/
	
	
	private static int[] industryNodeArray(float[] skillDistributionArray, int[] nodeArray) {
		int[][] result = new int[NR_OF_INDUSTRIES+1][];
		int k = 0, 
			nodeArrayLength = nodeArray.length, 
			nrOfNodesWithThisIndustry;		
		
		HelperFunctions.fisherYatesShuffle(nodeArray);
		for(int i = 0; i < NR_OF_INDUSTRIES+1; i++) {
			if(i == 2) {
				result[i] = new int[0];
			} else {
				if(i == 148) { //all nodes left get industry code 148 because the rounding could produce a bit too many or too few nodes that are already assigned to an industry 
					nrOfNodesWithThisIndustry = nodeArrayLength - k;
				} else { 					
					nrOfNodesWithThisIndustry = Math.round(skillDistributionArray[i]*nodeArrayLength);
				}
				result[i] = new int[nrOfNodesWithThisIndustry];
				for(int j = 0; j < nrOfNodesWithThisIndustry; j++) {
					result[i][j] = nodeArray[k++];
				}
			}
		}		
		return result;
	}
	
	
	//@SuppressWarnings("unchecked")
	/*private static int[][] toIndustryNodeSkill2dArray(float[] skillDistributionArray, int[][] industryNodeArray2D, int[] nodes) {
		int nrOfNodes = nodes.length;		
		int result[][] = new int[NR_OF_INDUSTRIES+1][];
		List<List<Integer>> nodeSkillList;
		//List<Integer>[] nodeSkillList; 		
		
		int distribution, position, skillNumber, counter, nrOfSkills, totalSizenodeSkillList, industryNodeArray2DLength;
		int[] industryNodeArray2DCopy;
						
		for(int i = 0; i < NR_OF_INDUSTRIES + 1; i++) {			
			if(i == 2) {				
				result[i] = new int[0];				
			} else {
				totalSizenodeSkillList = 0;
				counter = 0;
				skillNumber = 1;
				
				industryNodeArray2DCopy = industryNodeArray2D[i].clone();
				Arrays.sort(industryNodeArray2DCopy);
				
				industryNodeArray2DLength = industryNodeArray2D[i].length;
				nodeSkillList = new ArrayList<List<Integer>>(industryNodeArray2DLength);
				for(int z = 0; z < industryNodeArray2DLength; z++) {
					nodeSkillList.add(z, new ArrayList<Integer>());
				}
				//'first row' of skillIndustryDistributionsNetherlands.csv are the totals so start at 'second row' (=first skill) and loop per 'column' (=industry code):							
				for(int j = i + NR_OF_INDUSTRIES + 1; j < skillDistributionArray.length; j += NR_OF_INDUSTRIES + 1) {
					distribution = Math.round(skillDistributionArray[j]*nrOfNodes);
										
					if(distribution > 0) {
						HelperFunctions.fisherYatesShuffle(industryNodeArray2D[i]);
						for(int k = 0; k < distribution; k++){							
							position = Arrays.binarySearch(industryNodeArray2DCopy, industryNodeArray2D[i][k]);							
							if (nodeSkillList.get(position).size() < 50) {								
								nodeSkillList.get(position).add(skillNumber);									
							} else{ //node has already the maximum of 50 skills, 								
								distribution++;	//so assign skill to next node in the shuffled array	
								System.out.println("distribution increased: " + industryNodeArray2D[i][k] + "," + skillNumber); //debug									
							}
						}						
					}
					skillNumber++;					
				}
				System.out.println("this was industry: " + i); //debug			
			
				for(int k = 0; k < industryNodeArray2DLength; k++) {
					totalSizenodeSkillList += nodeSkillList.get(k).size();					
				}
				totalSizenodeSkillList += industryNodeArray2DLength;
												
				result[i] = new int[totalSizenodeSkillList];				
				for(int k = 0; k < industryNodeArray2DLength; k++) {
					nrOfSkills = nodeSkillList.get(k).size();	
					result[i][counter++] = -industryNodeArray2DCopy[k]; //to distinguish between skills and nodes, node IDs get negative number (or 0 if ID is 0). 
					for(int m = 0; m < nrOfSkills; m++) {
						result[i][counter++] = nodeSkillList.get(k).get(m); //skill numbers are positive (> 0)						
					}					
				}				
			}
		}		
		return result;
	}*/	
	
	
	private static int[] getSocialNetworkNodesIntArrayFromDiskOrFromSocialNetworkConnectionsIntArray(int[] array, String path) throws IOException {
		if(new File(path).exists()) {
			return (int[]) HelperFunctions.loadObject(path);
		}else {
			return saveSocialNetworkNodesIntArrayOnDisk(array, path);
		}	    
	}
	
	
	private static int[] saveSocialNetworkNodesIntArrayOnDisk(int[] array, String path) throws IOException {
		Set<Integer> nodes = new HashSet<Integer>(60000000, 1); 
		System.out.println("start"); //debug
		//int j = 0; //debug
	    for(int node : array) {
	    	/*j++; //debug
	    	if(j%1000000 == 0) { //debug
	    		System.out.println(" nodes.size(): " + nodes.size()); //debug
	    		System.out.println(" J           : " + j); //debug
	    	} //debug */
	    	if(node < 0) {
	    		node = -node;
	    	}
	    	nodes.add(node);
	    }
	    
	    //System.out.println("nodes.size()++++++++++++++++++++++++++++++++++" + nodes.size()); //debug
	    
	    int[] result = new int[nodes.size()];
	    int i = 0;
	    for (int uniqueNode : nodes) {
	        result[i++] = uniqueNode;	        
	    }	
	    	    
	    HelperFunctions.saveObject(result, path);
	    
	    return result;
	}	
}