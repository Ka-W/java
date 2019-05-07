package jung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Function;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import utils.HelperFunctions;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;



public class Jung {
	private static final Logger logger = Logger.getLogger(jung.RunExperiment.class.getName());
	
	Jung() {
		throw new AssertionError();
	}
	
		
	public static Graph<String, Integer> generateLinkedInOntologyAsJungGraph() throws IOException {				
		String[] current,
			     previous = null;

		ArrayList<String> industries = HelperFunctions.fileToArrayList(HelperFunctions.INDUSTRY_GROUPS_SORTED_ON_GROUPS_CSV); //same file format as INDUSTRIES_SORTED_ON_GROUPS but sorted on first column (industryCodes)
		//We are not going to use skills:
		//ArrayList<String> skills = HelperFunctions.fileToArrayList(HelperFunctions.SKILLS_INDUSTRIES_SORTED_ON_CODES_CSV); //file format sorted on first column (industryCodes)
				
		int edgeIDcounter = 0;
				
		Graph<String, Integer> result = new UndirectedSparseGraph<String, Integer>();
		
		//add edges 'industry group' - 'industry code' and 'top concept' - 'industry group'		
		for(int i=0; i < industries.size(); i++) {
			current = industries.get(i).split("\t");
			result.addEdge(edgeIDcounter++, current[0], current[3]); //current[0] = 'industry code', current[3] = 'industry group'
			if(i > 0) {
				previous = industries.get(i-1).split("\t");				
			}			 
			if(i == 0 || !previous[3].equals(current[3])) { //no duplicate industry groups
				result.addEdge(edgeIDcounter++, "topConcept", current[3]);
			}
		}
		
		//add edges 'industry code' - 'skill'. We are not going to use skills:
		/*for(String skill : skills) {
			result.addEdge(edgeIDcounter++, skill.split("\t")[0], skill.split("\t")[2]); //skill.split("\t")[0] = skill, skill.split("\t")[0] = industry
		}*/
						
		return result;
	}
	
	
	public static Graph<Node, Integer> constructJungGraphWithPriors(String sourceSkill, DijkstraDistance<String, Integer> dijkstraDistance, Map<Integer, Number> distanceMap, int[] nodeConnections, int[][] industry_nodeSkills) {
		int nodeOrSkill;
		Node node = null;
		Map<Integer, Node> nodes = new HashMap<Integer, Node>();		
		int shortestDistance = 10;
		int distance;		
		double prior = 0.0;
		int length;
		
		//---------set priors for the Vertexs:
		for(int i = 0; i < industry_nodeSkills.length; i++) {
			length = industry_nodeSkills[i].length;
			for(int j = 0; j < length; j++) {				
				nodeOrSkill = industry_nodeSkills[i][j];
				if(nodeOrSkill < 1) { //it is a Vertex					
					shortestDistance = 10; //reset shortestDistance
					node = new Node(-nodeOrSkill);
					nodes.put(-nodeOrSkill, node);
					if(j == length-1 || industry_nodeSkills[i][j+1] < 1) { //if current Vertex is last item of the array or if next item is a Vertex then it has no skills
						if(i == 0) { //Vertex has no industry
							node.prior = 0.0;							
						} else { //Vertex has an industry
							node.prior = 6/dijkstraDistance.getDistance(sourceSkill, String.valueOf(i)).doubleValue()+1;							
						}
					} 																	
				} else { //it is a skill
					if(shortestDistance != 0) {
						distance = distanceMap.get(nodeOrSkill).intValue();
						if(distance < shortestDistance) {
							shortestDistance = distance;
							prior = 3 - shortestDistance/2;							
						}
					}
												
					if(j == length-1) { //if last item in array, then no more skills need to be evaluated, so set the prior
						node.prior = prior;						
					} else if(industry_nodeSkills[i][j+1] < 1) { //if next item is a Vertex, then no more skills need to be evaluated, so set the prior
						node.prior = prior;						
					} 
				}
			}					
		}
			
		
		//---------create the graph:
		int edgeIDcounter = 0;
		Integer	first = new Integer(VertexConnections[0]);
		Graph<Vertex, Integer> result = new UndirectedSparseGraph<Vertex, Integer>();
				
		for(int VertexID : VertexConnections) {			
			if(VertexID < 1) {
				first = -VertexID;
			} else {				
				result.addEdge(edgeIDcounter++, Vertexs.get(first), Vertexs.get(VertexID));				
			}			
		}
		
		return result;
	}
	
	
	public static Map<Integer, Number> generateDistanceMap(DijkstraDistance<String,Integer> distance, ArrayList<String> skills, String sourceSkill) {
		int counter = 1;		
		Map<Integer, Number> result = new HashMap<Integer, Number>(); 
		
		String targetSkill;
		for(String skill : skills) {
			targetSkill = skill.split("\t")[0];
			result.put(counter++, distance.getDistance(sourceSkill, targetSkill));
		}
				
		return result;
	}
	
	
	public static List<Node> calculatePageRankWithPriors(Graph<Node, Integer> g, Comparator<Node> c, double alpha, double tolerance, int iterations) {		
		//logger.log(Level.INFO, "start timer for calculating PageRank:"); //debug
		logger.log(Level.INFO, "TEST TEST TEST start timer for calculating PageRank: TEST TEST TEST\n"); //debug
		long start = System.currentTimeMillis(); //debug
		final double totalPrior = getTotalPrior(g);
		Function<Node, Double> priorTransformer = new Function<Node,Double>() {			
			public Double apply(Node node) {				
				return node.prior/totalPrior;
			}
		};
		
		/*final int totalEdges = g.getEdgeCount();
		Transformer<Integer, Number> edgeWeightTransformer = new Transformer<Integer, Number>() {			
			public Double transform(Integer edge) {				
				return 1.0/totalEdges;
			}
		};*/
		
		
		PageRankWithPriors<Node ,Integer> pr = new PageRankWithPriors<Node, Integer>(g, priorTransformer, alpha);
		pr.setTolerance(tolerance);
		pr.setMaxIterations(iterations);
		logger.log(Level.INFO, "number of iterations before: " + pr.getIterations());
		pr.evaluate();
		logger.log(Level.INFO, "number of iterations end: " + pr.getIterations());		
		logger.log(Level.INFO, "Total time to calculate PageRank: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
		
		logger.log(Level.INFO, "start timer sorting results PageRank:"); //debug
		start = System.currentTimeMillis(); //debug
		Collection<Node> nodes = g.getVertices();
		
		for(Node n  : nodes) {			
			n.score = pr.getVertexScore(n);			
		}
		
		List<Node> nodeRanking = new ArrayList<Node>(nodes);								
		Collections.sort(nodeRanking, c);
				
		logger.log(Level.INFO, "Total time sorting results PageRank: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));		
		
		//assign ranking number:
		logger.log(Level.INFO, "start timer assigning ranking:"); //debug
		start = System.currentTimeMillis(); //debug
		int nrOfVertexs = g.getVertexCount();	
		Node node;		
		double previousScore = 2.0; 
		double previousRanking = 0;
		for(int i = 0; i < nrOfVertexs; i++) {
			node = nodeRanking.get(i);
			//logger.log(Level.INFO, previousRanking + ", " + previousScore); //debug
			if(node.score == previousScore) {
				node.rank = previousRanking;
			} else {
				node.rank = i+1; //start ranking with 1
				previousRanking = node.rank;
				previousScore = node.score;
			}
			//--||debug
			if(i < 11 || i == 98 || i == 99 || i == 100 || i == 998 || i == 999 || i == 1000 || i == 9998 || i == 9999 || i == 10000 || i == 99998 || i == 99999 || i == 100000) {
				logger.log(Level.INFO, i + ", Vertex rank: " + node.rank);
			}
			//--||debug
		}
		logger.log(Level.INFO, "Total time assigning ranking: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		
		
		return nodeRanking;
	}
	
	
	//rankingSampledGraphList sorted on rank and topK_ArraysOriginalGraphSortedOnID[0] is whole graph
	public static double[] calculateKendallsAndSpearmansCorrelationCoefficientsSampledGraphOriginalGraph(/*double[][] topK_Rank_ArraysOriginalGraphSortedOnID, int[][] topK_ID_ArraysOriginalGraphSortedOnID,*/ List<Vertex> rankingOriginalGraphList, List<Vertex> rankingSampledGraphList, Comparator<Vertex> VertexScoreComparator, Comparator<Vertex> VertexIDComparator) {
		logger.log(Level.INFO, "start timer for creating rankingSampledGraphArray:"); //debug
		long start = System.currentTimeMillis(); //debug		
		/*int nrOfVertexsOriginalGraph = topK_ID_ArraysOriginalGraphSortedOnID[0].length;
		int nrOfVertexsSampledGraph = rankingSampledGraphList.size();
		double[] rankingSampledGraphArray = new double[nrOfVertexsOriginalGraph];
						
		//create an array with ranks of sampled graph:
		int rankingOfVertexNotPresentInSampledGraph = rankingSampledGraphList.size() + 1;
		int k = 0, previousK = 0;
		Vertex Vertex = rankingSampledGraphList.get(k);
		for(int j = 0; j < nrOfVertexsOriginalGraph; j++) {
			if(k < nrOfVertexsSampledGraph  && previousK < k) {
				Vertex = rankingSampledGraphList.get(k);
				previousK = k;
			}
			if(topK_ID_ArraysOriginalGraphSortedOnID[0][j] == Vertex.id) {
				rankingSampledGraphArray[j] = Vertex.rank;
				k++;				
			} else {
				rankingSampledGraphArray[j] = rankingOfVertexNotPresentInSampledGraph;
			}
		}
		logger.log(Level.INFO, "Total time creating rankingSampledGraphArray: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug*/
		
		logger.log(Level.INFO, "start timer for calculating Kendall:"); //debug
		start = System.currentTimeMillis(); //debug	
		KendallsCorrelation2 kc = new KendallsCorrelation2();
		SpearmansCorrelation sc = new SpearmansCorrelation();
		double[] result = new double[6]; //12 
		
		
		/*result[0] = kc.correlation(topK_Rank_ArraysOriginalGraphSortedOnID[0], rankingSampledGraphArray);
		result[6] = sc.correlation(topK_Rank_ArraysOriginalGraphSortedOnID[0], rankingSampledGraphArray);
						
		Collections.sort(rankingSampledGraphList, VertexScoreComparator);
		
		for(int g = 0; g < 100; g++) { //debug
			logger.log(Level.INFO, rankingSampledGraphList.get(g));
		}
		int position, size;
		for(int i = 1; i < 6; i++) {
			size =  (int) (1000000/Math.pow(10, i)); //top 100.000, 10.000, 1000, 100, 10
			rankingSampledGraphArray = new double[size];*/ 
			
			/*for(int j = 0; j < size; j++) {
				Vertex = rankingSampledGraphList.get(j);
				if(Arrays.binarySearch(topK_ID_ArraysOriginalGraphSortedOnID[i], Vertex.id) < 0) {
					rankingSampledGraphArray[j] = size;
				} else {
					rankingSampledGraphArray[j] = Vertex.rank;
				}
			}*/
			
			/*for(int j = 0; j < size; j++) {
				if(j < nrOfVertexsSampledGraph) {
					Vertex = rankingSampledGraphList.get(j);
					position = Arrays.binarySearch(topK_ID_ArraysOriginalGraphSortedOnID[i], Vertex.id); 
					if(position >= 0) {
						rankingSampledGraphArray[position] = Vertex.rank;
					}
				}
			}
			for(int j = 0; j < size; j++) {
				if(rankingSampledGraphArray[j] == 0.0) {
					rankingSampledGraphArray[j] = size;
				}
			}
			
			
			result[i] = kc.correlation(topK_Rank_ArraysOriginalGraphSortedOnID[i], rankingSampledGraphArray);
			result[i+6] = sc.correlation(topK_Rank_ArraysOriginalGraphSortedOnID[i], rankingSampledGraphArray);*/
			/*if(Double.isNaN(correlationKC)) {
				result[i] = 0.0;
			}else {
				result[i] = correlationKC;
			}*/
		Collections.sort(rankingOriginalGraphList, VertexIDComparator);
		Collections.sort(rankingSampledGraphList, VertexIDComparator);
		for(int i = 0; i < 6; i++) {
			result[i] = kc.correlation(rankingOriginalGraphList, rankingSampledGraphList, rankingOriginalGraphList.size());
			//result[6] = sc.correlation(topK_Rank_ArraysOriginalGraphSortedOnID[0], rankingSampledGraphArray);
			
			logger.log(Level.INFO, "kendall " + i + ": " + result[i]); //debug
			//logger.log(Level.INFO, "spearman " + i + ": " + result[i+6]); //debug
		}		
		
		logger.log(Level.INFO, "Total time calculating Kendall: " + ((System.currentTimeMillis() - start)/1000 + " seconds")); //debug
		
		return result;
	}
	
	
	//adapted from: http://stackoverflow.com/questions/10470213/how-to-copy-a-graph-in-jung-2-0-framework
	public static Graph<Node, Integer> copyGraph(Graph<Node, Integer> src) {
		logger.log(Level.INFO, "start timer for copying graph:"); //debug
		long start = System.currentTimeMillis(); //debug
		Graph<Node, Integer> result = new UndirectedSparseGraph<Node, Integer>();

	    for (Integer edge : src.getEdges()) {
	        result.addEdge(edge, src.getIncidentVertices(edge));
	    }
	    
	    logger.log(Level.INFO, "Total time copying graph: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
	    return result;
	}
	
	
	public static Graph<Node, Integer> sampleGraphRandomly(double reduction, Graph<Node, Integer> g)  {
		int nrOfNodes = g.getVertexCount();
		int numberOfNodesToRemove = (int) Math.round(reduction * nrOfNodes / 100);		
		if(numberOfNodesToRemove < 1 || numberOfNodesToRemove >= nrOfNodes) {
			return g;
		}	
		Graph<Node, Integer> result = copyGraph(g);
		logger.log(Level.INFO, "start timer for sampling graph randomly:"); //debug
		long start = System.currentTimeMillis(); //debug
		
		
		List<Node> verticesList = new ArrayList<Node>(result.getVertices());
		Collections.shuffle(verticesList);
		for(int i = 0; i < numberOfNodesToRemove; i++) {
			result.removeVertex(verticesList.get(i));				
		}
		logger.log(Level.INFO, "Total time sampling graph randomly: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
		return result;
	}	
	
	
	public static Graph<Node, Integer> sampleGraphKnowledgeIntensive(double reduction, Graph<Node, Integer> g)  {
		int nrOfVertices = g.getVertexCount();
		int numberOfNodessToRemove = (int) Math.round(reduction * nrOfVertices / 100);		
		if(numberOfNodessToRemove < 1 || numberOfNodessToRemove >= nrOfVertices) {
			return g;
		}
		
		Graph<Node, Integer> result = copyGraph(g);
		logger.log(Level.INFO, "start timer for sampling graph knowledge intensive:"); //debug
		long start = System.currentTimeMillis(); //debug
		int total = 0,			
			n = 6,
			vertexCount;
		double prior = 0.0;
		
		while(total < numberOfNodessToRemove) {
			vertexCount = getVertexCountFilteredForPrior(result.getVertices(), prior); 
			if(total + vertexCount < numberOfNodessToRemove) {
				total += vertexCount;
				removeAllVerticesWithPrior(result, prior);				
				if(n >= 2) {
					prior = 6 / n;
					n = n - 2;
				} else {
					prior = 12.0;
				}
			} else {
				break;
			}
		}		
		if(total < numberOfNodessToRemove) {
			numberOfNodessToRemove = numberOfNodessToRemove - total;
			Graph<Node, Integer> filteredGraph = filterGraphForPrior(prior, result);
			
			List<Node> verticesList = new ArrayList<Node>(filteredGraph.getVertices());
			Collections.shuffle(verticesList);
			
			for(int i = 0; i < numberOfNodessToRemove; i++) {
				result.removeVertex(verticesList.get(i));				
			}			
		}
		
		logger.log(Level.INFO, "Total time sampling graph knowledge intensive: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
		return result;
	}
	
	
	private static Graph<Node, Integer> filterGraphForPrior(final double prior, Graph<Node, Integer> g) {
		logger.log(Level.INFO, "start timer for filterGraphForPrior:"); //debug
		long start = System.currentTimeMillis(); //debug
		Predicate<Node> predicate = new Predicate<Node>() {
			@Override
            public boolean apply(Node node) {				
                //logger.log(Level.INFO, Vertex.prior);
				return node.prior == prior;
            }
        };
		VertexPredicateFilter<Node, Integer> filter = new VertexPredicateFilter<Node, Integer>(predicate);
		Graph<Node, Integer> result = filter.apply(g);
		
		logger.log(Level.INFO, "Total time filterGraphForPrior: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
		return result;
	}
	
	
	private static int getVertexCountFilteredForPrior(Collection<Node> nodes, double prior) {
		logger.log(Level.INFO, "start timer for getVertexCountFilteredForPrior:"); //debug
		long start = System.currentTimeMillis(); //debug
		int result = 0;
		
		for(Node node : nodes) {
			if(node.prior == prior) {
				result++;
			}
		}
		
		logger.log(Level.INFO, "Total time getVertexCountFilteredForPrior: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));
		return result;	
	}
	
	
	private static void removeAllVerticesWithPrior(Graph<Node, Integer> g, double prior) {				
		logger.log(Level.INFO, "start timer for removeAllVerticesWithPrior:"); //debug
		long start = System.currentTimeMillis(); //debug
		List<Node> nodesList = new ArrayList<Node>(g.getVertices()); //this transformation of Collection g.getVertices() is needed to prevent java.util.ConcurrentModificationException
		
		for(Node node : nodesList) {
			if(node.prior == prior) {
				g.removeVertex(node);
			}
		}
		logger.log(Level.INFO, "Total time removeAllVerticesWithPrior: " + ((System.currentTimeMillis() - start)/1000 + " seconds"));		
	}
	
	
	public static double getTotalPrior(Hypergraph<Node, Integer> g) {
		double result = 0;		
		
		for(Node node : g.getVertices()) {
			result = result + node.prior;
		}
		
		return result;
	}
}


/*
	logger.log(Level.INFO, g.getVertexCount());
	logger.log(Level.INFO, g.getEdgeCount());
	logger.log(Level.INFO, result.getVertexCount());
	logger.log(Level.INFO, result.getEdgeCount());
	logger.log(Level.INFO, result.getVertexCount() ); //debug
	logger.log(Level.INFO, result.getEdgeCount() ); //debug
	logger.log(Level.INFO, g.getVertexCount() ); //debug
	logger.log(Level.INFO, g.getEdgeCount() ); //debug
	logger.log(Level.INFO, result.getVertexCount() ); //debug
	logger.log(Level.INFO, result.getEdgeCount() ); //debug
	logger.log(Level.INFO, g.getVertexCount() ); //debug
	
	if((edgeIDcounter%1000) == 0) {  //debug
					logger.log(Level.INFO, edgeIDcounter);
				}
	
	//logger.log(Level.INFO, i + " ,no skill industry 0"); //debug
	//logger.log(Level.INFO, i + ", no skill 1");
	
	//logger.log(Level.INFO, Vertexs.size()); //debug
	//logger.log(Level.INFO, Vertexs.get(VertexID).prior); //debug
*/
  
