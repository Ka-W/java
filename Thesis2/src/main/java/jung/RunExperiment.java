package jung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

import com.google.common.base.Function;

import utils.HelperFunctions;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;


public class RunExperiment {
	
	private static final Logger logger = Logger.getLogger(jung.RunExperiment.class.getName());
	//private static final Logger rootLogger = Logger.getLogger("");
	private final static String SOURCE_SKILL = "Music";
	private final static int NR_OF_THREADS = 3,
							 MAX_ITERATIONS = 300;
	private final static double ALPHA = 0.85,
							    TOLERANCE = 0.00000001;
							    
	
	public static void main(String[] args) throws IOException {
		//logger.setUseParentHandlers(false);
		//LogManager.getLogManager().reset();
		//FileHandler fh = new FileHandler(HelperFunctions.LOG_FILE + "log" + System.currentTimeMillis());  
		//ConsoleHandler sh = new ConsoleHandler();
        //SimpleFormatter formatter = new SimpleFormatter();  
        //fh.setFormatter(formatter);
        //rootLogger.addHandler(fh);
        
		logger.log(Level.INFO, "start timer running experiment:\n"); //debug
		//System.out.println(System.getProperty("java.util.logging.config.file"));
		//System.out.println(System.getProperty("user.dir"));
		long start2 = System.currentTimeMillis(); //debug
		
		ArrayList<String> skills = HelperFunctions.fileToArrayList(HelperFunctions.SKILLS_INDUSTRIES_SORTED_ON_CODES_CSV); //file format sorted on first column (industryCodes)
		logger.log(Level.INFO, "start timer for loading int[] nodeConnections:"); //debug
		long start = System.currentTimeMillis(); //debug		
		int[] nodeConnections = (int []) HelperFunctions.loadObject(HelperFunctions.LIVE_JOURNAL_INT_ARRAY_T); 
		int[][] industry_nodeSkills =(int [][]) HelperFunctions.loadObject(HelperFunctions.LIVE_JOURNAL_INDUSTRY_NODE_SKILL_INT_ARRAY_T);
		logger.log(Level.INFO, "Total time loading int[] nodeConnections: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		Comparator<Node> nodeIDComparator = new Comparator<Node>() {
	        @Override 
	        public int compare(Node v1, Node v2) {//ascending order
	        	if(v1.id > v2.id) {
	        		return 1;
	   		 	} else { 
	   		 		return -1;
	   		 	}
	        }

	    };
	   Comparator<Node> nodeScoreComparator = new Comparator<Node>() {
	        @Override 
	        public int compare(Node v1, Node v2) {//descending order
	        	if(v1.score < v2.score) {
	        		return 1;
	   		 	} else if(v1.score > v2.score) {
	   		 		return -1;
	   		 	} else {
		   		 	if(v1.id > v2.id) { //ascending order
		        		return 1;
		   		 	} else {
		   		 		return -1;
		   		 	}
	   		 	}
	        }

	    };
	   	   
	  	DijkstraDistance<String, Integer> dijkstraDistance = new DijkstraDistance<String,Integer>(Jung.generateLinkedInOntologyAsJungGraph());
		Map<Integer, Number> distanceMap = Jung.generateDistanceMap(dijkstraDistance, skills, SOURCE_SKILL);
		
		logger.log(Level.INFO, "start timer constructing jung graph:"); //debug
		start = System.currentTimeMillis(); //debug
		Graph<Node, Integer> g  = Jung.constructJungGraphWithPriors(SOURCE_SKILL, dijkstraDistance, distanceMap, nodeConnections, industry_nodeSkills);
		logger.log(Level.INFO, "Total time constructing jung graph: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		
					
		//create array with node IDs in ascending order
		//int nrOfNodes = g.getVertexCount();
	    	    
		//Graph<Node, Integer> sampledGraph = Jung.sampleGraphRandomly(99.8, g);  //debug
		//List<Node> ranking = Jung.calculatePageRankWithPriors(sampledGraph, nodeIDComparator); //debug	
		logger.log(Level.INFO, "start timer calculating pagerank:"); //debug
		start = System.currentTimeMillis(); //debug
		List<Node> ranking = Jung.calculateMyPageRankWithPriors(g, ALPHA, nodeIDComparator, nodeScoreComparator); //debug
		logger.log(Level.INFO, "Total time calculating pagerank: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		
		
		for(int i= 0; i < 100; i++) { //debug
			logger.log(Level.INFO, ranking.get(i).toString());
		}
		
		/*[] idArraySortedOnRankOriginalGraph = new int[nrOfNodes]; 
		//double[] rankingArraySortedOnRank = new double[nrOfNodes];
		for(int j = 0; j < nrOfNodes; j++) {
			idArraySortedOnRankOriginalGraph[j] = ranking.get(j).id;
			//rankingArraySortedOnRank[j] = ranking.get(j).rank;			
		}
		
		
		int[][] topK_ID_ArraysOriginalGraphSortedOnID = new int[6][];		
		for(int j = 0; j < 6; j++) {
			if (j > 0) {
				nrOfNodes = (int) (1000000/Math.pow(10, j)); //top 100.000, 10.000, 1000, 100, 10
			}
			topK_ID_ArraysOriginalGraphSortedOnID[j] = new int[nrOfNodes];
			for(int h = 0; h < nrOfNodes; h++) {
				topK_ID_ArraysOriginalGraphSortedOnID[j][h] = ranking.get(h).id;
			}
			Arrays.sort(topK_ID_ArraysOriginalGraphSortedOnID[j]);		
		}
		
		
		//sort on nodeID
		Collections.sort(ranking, nodeIDComparator);
		//create an array with ranks of original graph:
		int size = g.getVertexCount();
		int counter;
		nrOfNodes = size;
		double[][] topK_Rank_ArraysOriginalGraphSortedOnID = new double[6][];
		for(int j = 0; j < 6; j++) {
			counter = 0;
			if(j > 0) {
				size = (int) (1000000/Math.pow(10, j)); //top 100.000, 10.000, 1000, 100, 10
			}
			topK_Rank_ArraysOriginalGraphSortedOnID[j] = new double[size];
			for(int h = 0; h < nrOfNodes; h++) {				
				if(j > 0) {
					if(counter < size) {
						Node node = ranking.get(h);
						if(node.rank < size + 1) {
							topK_Rank_ArraysOriginalGraphSortedOnID[j][counter++] = node.rank;
						}
					}
				}else {
					topK_Rank_ArraysOriginalGraphSortedOnID[j][h] = ranking.get(h).rank;
				}
			}			
		}*/
		
		
		ExecutorService exec = Executors.newFixedThreadPool(NR_OF_THREADS);
		Collection<Future<ExperimentResult>> tasks = new ArrayList<Future<ExperimentResult>>();
		
		//RunExperimentCallable(double reduction, boolean knowledgeIntesive, Graph<Node, Integer> g, Comparator<Node> nodeScoreComparator, Comparator<Node> nodeIDComparator, int[] nodeIDsOriginalGraph, double[][] rankingArraysOriginalGraph, int[] idArraySortedOnRankOriginalGraph, int experimentID) {
		logger.log(Level.INFO, "add tasks........"); //debug
		for(int k = -5; k < 47; k++) {			
			//if(k == 0) {
			if(k > -5 && k < 1) {				
				tasks.add(exec.submit(new RunExperimentCallable(0, false, g, nodeScoreComparator, nodeIDComparator, /*topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID,*/ranking, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(0, true, g, nodeScoreComparator, nodeIDComparator, /*topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID,*/ranking, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			}/* else if(k > 0 && k < 6) {
				tasks.add(exec.submit(new RunExperimentCallable(10, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(10, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 5 && k < 11) {
				tasks.add(exec.submit(new RunExperimentCallable(20, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(20, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));;
			} else if(k > 10 && k < 16) {
				tasks.add(exec.submit(new RunExperimentCallable(30, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(30, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 15 && k < 21) {
				tasks.add(exec.submit(new RunExperimentCallable(40, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(40, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 20 && k < 26) {
				tasks.add(exec.submit(new RunExperimentCallable(50, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(50, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 25 && k < 31) {
				tasks.add(exec.submit(new RunExperimentCallable(60, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(60, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 30 && k < 36) {
				tasks.add(exec.submit(new RunExperimentCallable(70, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(70, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 35 && k < 41) {
				tasks.add(exec.submit(new RunExperimentCallable(80, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(80, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
			} else if(k > 40 && k < 46) {
				tasks.add(exec.submit(new RunExperimentCallable(90, false, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));
				tasks.add(exec.submit(new RunExperimentCallable(90, true, g, nodeScoreComparator, nodeIDComparator, topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID, ALPHA, TOLERANCE, MAX_ITERATIONS, k)));										
			}*/
		}
		//tasks.add(new RunExperimentCallable(90, false, g, nodeScoreComparator, nodeIDComparator, rankingArrayGraph, k));
		logger.log(Level.INFO, "start threads........"); //debug
		logger.log(Level.INFO, "nr of tasks is " + tasks.size()); //debug
		exec.shutdown();
						
		List<ExperimentResult> experimentResults = new ArrayList<ExperimentResult>();
		for(Future<ExperimentResult> result : tasks){
			try {
				experimentResults.add(result.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		logger.log(Level.INFO, "start timer saving experiment results list:"); //debug
		start = System.currentTimeMillis(); //debug
		HelperFunctions.saveObject(experimentResults, HelperFunctions.EXPERIMENT_RESULTS_LIST + "2");
		logger.log(Level.INFO, "Total time saving experiment results list: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		
		logger.log(Level.INFO, "Total time running Experiment: " + ((System.currentTimeMillis() - start2)/1000 + " seconds\n")); //debug
		
		for(ExperimentResult experimentResult : experimentResults) {
			logger.log(Level.INFO, experimentResult.toString());			
		}
		
		
	}
}		