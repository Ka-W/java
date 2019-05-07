package jung;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Test2 {
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		System.out.println("Double.MIN_VALUE " + Double.MIN_VALUE);
		KendallsCorrelation kr = new KendallsCorrelation();
		KendallsCorrelation2 kr2 = new KendallsCorrelation2();
		SpearmansCorrelation sr = new SpearmansCorrelation();
		PearsonsCorrelation pr = new PearsonsCorrelation();
		double[] a = new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
		double[] b = new double[] {8.0, 7.0, 3.0, 4.0, 5.0, 6.0, 2.0, 1.0};
		double[] e = new double[] {1.0, 2.0, 3.0, 9.5, 5.0, 6.0, 7.0, 9.5};
		double[] f = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		//double[] g = new double[] {{1, 3, 4, .., 5, 10, 18, 17, .., ..};
		double[] g = new double[] {1, 3, 4, 2000000.5, 5, 10, 17, 18, 19, 2000000.5};
		double[] h = new double[] {1, 3, 4, 1200000.5, 5, 10, 1200000.5, 1200000.5, 1200000.5, 1200000.5};
		double[] j = new double[] {1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 9, 10, 11, 12, 13};
		double[] k = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2, 2, 2, 2, 2};
		double[] c = new double[4000000];
		double[] d = new double[4000000];
		for(int i = 0; i < 100000; i++) {
			if(i < 20) {
				c[i] = i+1;
			}
			if(i < 100000) {
				d[i] = i;
			}
			//if(i > 99999) {
				//d[i] = i;
			//}
		}
		
		
		//double z = rank(y);
		System.out.println(kr.correlation(a, b));
		//System.out.println(kr2.correlation(a, b));
		System.out.println(sr.correlation(a, b));
		System.out.println(pr.correlation(a, b));
		System.out.println();		
		/*//System.out.println(kr.correlation(c, d));
		//System.out.println(sr.correlation(c, d));
		//System.out.println(pr.correlation(c, d));	
		System.out.println();
		System.out.println(kr.correlation(f, g));
		System.out.println(kr2.correlation(f, g));
		System.out.println(sr.correlation(f, g));
		System.out.println(pr.correlation(f, g));
		System.out.println();	
		System.out.println(kr.correlation(f, h));
		System.out.println(kr2.correlation(f, h));
		System.out.println(sr.correlation(f, h));
		System.out.println(pr.correlation(f, h));
		System.out.println();
		System.out.println(kr.correlation(k, j));
		System.out.println(kr2.correlation(k, j));
		System.out.println(sr.correlation(k, j));
		System.out.println(pr.correlation(k, j));
		System.out.println();*/
		
		/*int result = 0;
		for(int i = 1; i < 100; i++) {
			result = result + i;
		}
		System.out.println(result);
		
		
		int x = 1;
		for(x = 1; x < 4000000; x <<= 1) {         // x = x << 2;
			System.out.println(x);
		}*/
	}

}
