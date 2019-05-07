package jung;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.google.common.base.Function;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;

public class RunExperimentCallable implements Callable<ExperimentResult> {
	private static final Logger logger = Logger.getLogger(jung.RunExperiment.class.getName());

    private final int experimentID, max_iterations;
	private final double reduction, alpha, tolerance;
	private final boolean knowledgeIntesive;
	private Graph<Node, Integer> g;
	private List<Node> rankingOriginalGraph;
	private Comparator<Node> nodeScoreComparator, nodeIDComparator;	
	//private double[][] topK_Rank_ArraysOriginalGraphSortedOnID;
	//private int[][] topK_ID_ArraysOriginalGraphSortedOnID;
	        
	RunExperimentCallable(final double reduction, final boolean knowledgeIntesive, Graph<Node, Integer> g, Comparator<Node> nodeScoreComparator, Comparator<Node> nodeIDComparator, /*double[][] topK_Rank_ArraysOriginalGraphSortedOnID, int[][] topK_ID_ArraysOriginalGraphSortedOnID,*/ List<Node> rankingOriginalGraph, final double alpha, final double tolerance, final int max_iterations, final int experimentID) {
    	this.reduction = reduction;
    	this.knowledgeIntesive = knowledgeIntesive;
    	this.g = g;
    	this.nodeScoreComparator = nodeScoreComparator;
    	this.nodeIDComparator = nodeIDComparator;
    	this.experimentID = experimentID;    	
    	//this.topK_Rank_ArraysOriginalGraphSortedOnID = topK_Rank_ArraysOriginalGraphSortedOnID;
    	//this.topK_ID_ArraysOriginalGraphSortedOnID = topK_ID_ArraysOriginalGraphSortedOnID;
    	this.alpha = alpha;
    	this.tolerance = tolerance;
    	this.max_iterations = max_iterations;
    	this.rankingOriginalGraph = rankingOriginalGraph;
    }	

	@Override
	public ExperimentResult call() {		
		Graph<Node, Integer> sampledGraph;
		if(knowledgeIntesive) {
			sampledGraph = Jung.sampleGraphKnowledgeIntensive(reduction, g);
		} else {
			sampledGraph = Jung.sampleGraphRandomly(reduction, g);
		}
		
		//List<Node> rankingSampledGraph = Jung.calculatePageRankWithPriors(sampledGraph, nodeScoreComparator, alpha, tolerance, max_iterations); //ranked on score
		List<Node> rankingSampledGraph = Jung.calculateMyPageRankWithPriors(sampledGraph, alpha, nodeIDComparator, nodeScoreComparator); //ranked on score
		sampledGraph = null; //faster garbage collection??????
		//Collections.sort(rankingSampledGraph, nodeIDComparator);
		ExperimentResult result = new ExperimentResult(experimentID, Jung.calculateKendallsAndSpearmansCorrelationCoefficientsSampledGraphOriginalGraph(/*topK_Rank_ArraysOriginalGraphSortedOnID, topK_ID_ArraysOriginalGraphSortedOnID,*/ rankingOriginalGraph, rankingSampledGraph, nodeScoreComparator, nodeIDComparator), reduction, knowledgeIntesive);
		
		rankingSampledGraph = null;
		clearObjectReferences();
		System.gc(); //faster garbage collection??????
		
		return result;
	}
	
	
	private void clearObjectReferences() {
		g = null;
    	nodeScoreComparator = null;
    	nodeIDComparator = null;    	
    	//topK_ID_ArraysOriginalGraphSortedOnID = null;
    	//topK_Rank_ArraysOriginalGraphSortedOnID = null;
	}
}
