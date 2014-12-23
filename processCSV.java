import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class processCSV {

	public static void main(String[] args) throws IOException {
		HashSet<String> ClearCutResume = new HashSet<String>();
		BufferedReader br;
		br = new BufferedReader(new FileReader("candidates.csv"));
	    FileWriter fw = new FileWriter(new File("Clear-Cut-Resume-List.txt").getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
		String line;
		int validCandidate = 0;
		CharSequence Cus = "Customer Service";
		CharSequence Sal = "Sales";
		while((line = br.readLine())!=null){
			String[] token = line.split(",");
			boolean CustomerService = false;
			boolean Sales = false;
			int Ccount=0;
			int Scount=0;
			for(int i=0;i<token.length;i++){
				System.out.println(token[i]);
				if (token[i]==null) continue;
				if (token[i].contains(Cus)) Ccount++;
				if (token[i].contains(Sal)) Scount++;
			}
			if (Ccount>=2) CustomerService=true;
			if (Scount>=2) Sales=true;
			if (CustomerService&&(!Sales)) {validCandidate++;ClearCutResume.add(token[0]);}
			if (Sales&&(!CustomerService)) {validCandidate++;ClearCutResume.add(token[0]);}
			//relaxed rule
			//if (Ccount==1 && Scount==0) validCandidate++;
			//if (Scount==1 && Ccount==0) validCandidate++;
			System.out.println("C = "+Ccount+"  S = "+Scount);
			System.out.println(token[0] + " " +CustomerService + " : "+ Sales);
		}
		
		for(String k:ClearCutResume){
		    bw.write(k+"\n");
		}
		
		System.out.println("Valid Candidate Count = "+ validCandidate);
		br.close();
		bw.close();
	}
}