package jung;

import java.io.Serializable;

//adapted from: http://www.grotto-networking.com/JUNG/JUNG2-Tutorial.pdf
public class Node implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	public int id,
			   industryCode;
	
	/*public double prior,
				  score,
				  rank;*/
		 
	public Node(int id) {
		this.id = id;		 
	}
	
	public Node(int id, int industryCode) {
		this.id = id;
		this.industryCode = industryCode;
	}
	
	@Override
	public String toString() {
		return "id: " + id + ", industryCode: " + industryCode;
	}
}
