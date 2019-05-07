package jung;

import com.google.common.base.Function;

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

//From: https://sites.google.com/site/nirajatweb/home/technical_and_coding_stuff/page-rank-with-prior
public class TestPageRankWithPriors {
	double [][] weigth = new double[10][10];
	int [] prior = new int[10];
	double [][] Text_wd_weight = new double[1000][1000];
	int [] prior_text_index = new int [25]; 

	public void calculate_PRP() { 
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				weigth[i][j]=0.0000000;
			}
		}
		weigth[0][1] = 1;
		weigth[0][2] = 1;
		weigth[0][3] = 1;
		 
		weigth[1][0] = 1;
		weigth[1][2] = 1;
		weigth[1][8] = 1;
		 
		weigth[2][1] = 1;
		weigth[2][2] = 1;
		weigth[2][9] = 1;
		 
		weigth[3][1] = 1;
		weigth[3][4] = 1;
		weigth[3][5] = 1;
		 
		weigth[4][3] = 1;
		weigth[4][5] = 1;
		weigth[4][9] = 1;
		 
		weigth[5][3] = 1;
		weigth[5][4] = 1;
		weigth[5][6] = 1;
		 
		weigth[6][5] = 1;
		weigth[6][7] = 1;
		weigth[6][8] = 1;
		 
		weigth[7][6] = 1;
		weigth[7][8] = 1;
		weigth[7][9] = 1;
		 
		weigth[8][1] = 1;
		weigth[8][6] = 1;
		weigth[8][7] = 1;
		 
		weigth[9][2] = 1;
		weigth[9][4] = 1;
		weigth[9][7] = 1;
		 
		    
		prior[0]=1; 
		prior[1]=1;
		prior[2]=1;
		prior[3]=1;
		prior[4]=1;
		prior[5]=1;
		prior[6]=1;
		prior[7]=1;
		prior[8]=1;
		prior[9]=1;
		Graph<Integer, String> g = new DirectedSparseGraph<Integer, String> ();
		
		for(int i=0;i<10;i++){
			g.addVertex(i);
		}
		 
		for(int r=0;r<10;r++) {
			for(int c=0;c<10;c++) {
				if(weigth[r][c]>0.0000000) {
					String tmp_edge = r+"->"+c;
					g.addEdge(tmp_edge, r, c, EdgeType.DIRECTED);
				}
			}
		 }
		    
		Function<String, Double> edge_weigths = new Function<String, Double>() {
			@Override
		    public Double apply(String e) {
				String[] split = e.split("->");           
		        return weigth[Integer.parseInt(split[0])][Integer.parseInt(split[1])];
		    }           
		};

		Function<Integer, Double> vertex_prior = new Function<Integer, Double>() {            
			@Override
		    public Double apply(Integer v) {                        
				return (double) prior[v];            
			}           
		};


		PageRankWithPriors<Integer, String> prp = new PageRankWithPriors<Integer, String>(g, edge_weigths, vertex_prior, 0.7);        
		//prp.setHyperedgesAreSelfLoops(true); 
		prp.getVertexPriors();
		prp.setMaxIterations(300);
				
		for(int i=0;i<10;i++){
			System.out.println("vertex " + i + ": " + vertex_prior.apply(i)); //print out vertex priors
			
		}
		
		
		
		//prp.setTolerance(0.100000);
		//prp.step();
		//prp.step();
		//prp.getMaxIterations();
		prp.evaluate();
		System.out.println("# of iterations system used "+prp.getIterations());
		    
		System.out.println("Vertex -0 "+prp.getVertexScore(0));
		System.out.println("Vertex -1 "+prp.getVertexScore(1));
		System.out.println("Vertex -2 "+prp.getVertexScore(2));
		System.out.println("Vertex -3 "+prp.getVertexScore(3));
		System.out.println("Vertex -4 "+prp.getVertexScore(4));
		System.out.println("Vertex -5 "+prp.getVertexScore(5));
		System.out.println("Vertex -6 "+prp.getVertexScore(6));
		System.out.println("Vertex -7 "+prp.getVertexScore(7));
		System.out.println("Vertex -8 "+prp.getVertexScore(8));
		System.out.println("Vertex -9 "+prp.getVertexScore(9));
	}

	public static void main(String[] args) {
		TestPageRankWithPriors prwp = new TestPageRankWithPriors();
		prwp.calculate_PRP();
		System.out.println("Operation complete");
	}	
}
