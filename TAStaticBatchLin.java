/*
 * This is the Class that call the TAGrid class and runs simulations as a batch
 * for the TA model that looks a just the lineage
 */

import java.io.*;

public class TAStaticBatchLin {

	double lineageSum[] = new double[100];
	double lineageSqrSum[] = new double[100];
	
	int replicates =20;
	
    public double getMean(double sum) {
		 return sum / replicates;
	}
	
	public double getStandardDeviation(double sum, double sqrSum) {
        double mean = getMean(sum);
		double dev = (sqrSum / replicates) - (mean * mean);
		if(dev>0){
        	return Math.sqrt(dev);
		}
		return 0.0;
    }


	public void iterate(int exp, int rep){
		double frac = (double)((exp+1)/100.0);
		int countLineage;
		int countClone;
		double avLineage;
		int lin=64*64;
		System.out.println(lin);
		int lineage[];
		TAGridStatic experiment = new TAGridStatic(64, 4, frac);
		for(int i=0; i<100; i++){
			lineage = new int[lin];
			countLineage =0;
			countClone =0;
			experiment.iterate();
			for (TACell c : experiment.tissue){
				lineage[c.lineage]++;
			}
			for(int j=1; j<lin; j++){
				if(lineage[j]>0)countClone++;
				countLineage+=lineage[j];
			}
			avLineage = (double)countLineage/(countClone*1.0);
			lineageSum[i]+=avLineage;
			lineageSqrSum[i]+=(avLineage*avLineage);
		}
	}
	
	public void setOfRuns(String file){
		for(int r=0; r<replicates; r++){
			iterate(9,r);
			System.out.print("["+r+"]");
		}
		System.out.println();
		outputData(file);
	}
	
		
	public void outputData(String file){
		try{
			BufferedWriter bufLineage = new BufferedWriter(new FileWriter(file+"Lineage.txt"));
			double frac=0.0;
			bufLineage.write("iteration Clones stdev");
			bufLineage.newLine();
			for(int i=0; i< 100; i++){
				bufLineage.write(i+" "+getMean(lineageSum[i])+" "+getStandardDeviation(lineageSum[i], lineageSqrSum[i])+" ");
				System.out.println(i+" "+getMean(lineageSum[i])+" "+getStandardDeviation(lineageSum[i], lineageSqrSum[i])+" ");
				bufLineage.newLine();	
			}
			bufLineage.newLine();
			bufLineage.newLine();
			bufLineage.close();
		}catch(IOException e){
		}
	}


	
	public static void main (String args[]) {		
		TAStaticBatchLin t = new TAStaticBatchLin();
		t.setOfRuns(args[0]);
	}
}
