package jung;

import java.io.Serializable;
import java.util.Arrays;

public class ExperimentResult implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	public int id;
	public double[] KendallsAndSpearmansCorrelationCoefficientsArray;
	public double reduction;
	public boolean knowledgeIntesive;
	
	public ExperimentResult(int id, double[] KendallsCorrelationCoefficientsArray, double reduction, boolean knowledgeIntesive) {
		this.id = id;
		this.KendallsAndSpearmansCorrelationCoefficientsArray = KendallsCorrelationCoefficientsArray;		
		this.knowledgeIntesive = knowledgeIntesive;
		this.reduction = reduction;
	}
	
	public String toString() { // Always a good idea for debugging
		return id + ", " + Arrays.toString( Arrays.copyOfRange(KendallsAndSpearmansCorrelationCoefficientsArray, 0, 6)) + ", " + reduction + ", " + knowledgeIntesive + "\n" + id + ", " + Arrays.toString( Arrays.copyOfRange(KendallsAndSpearmansCorrelationCoefficientsArray, 6, 12)) + ", " + reduction + ", " + knowledgeIntesive; // JUNG2 makes good use of these.
	}
}
