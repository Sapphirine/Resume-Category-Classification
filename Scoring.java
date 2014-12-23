import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class  Scoring {
	public static void main(String[] args) throws FileNotFoundException, IOException{
		//output setup   
		FileWriter fw = new FileWriter(new File("./sales_score.txt").getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
		FileWriter fw2 = new FileWriter(new File("./customer_service_score.txt").getAbsoluteFile());
	    BufferedWriter bw2 = new BufferedWriter(fw2);
		//read the pdf filename list
		BufferedReader br1 = new BufferedReader(new FileReader("FileName.txt"));
		String pdfFileName;
	    //load in stop words
	    HashSet<String> stopWordsSet = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("stop_words.txt"));
		try {
			String line= br.readLine();
			String[] tokens = line.split(" ");
			for (int i=0;i<tokens.length;i++){
				stopWordsSet.add(tokens[i]);
			}
		}finally{
			br.close();
			System.out.println("stop words loaded.");
		}
		//load sales scoring words
	    Hashtable<String, Integer> SalesScoringWords = new Hashtable<String, Integer>();
		BufferedReader br2 = new BufferedReader(new FileReader("sales_scoring_words.txt"));
		try {
			String line;
			while((line = br2.readLine())!=null){
				String[] tokens = line.split(" ");
				for (int i=0;i<tokens.length;i++){
					if(tokens.length==4){
						SalesScoringWords.put(tokens[0]+" "+tokens[1]+" "+tokens[2], Integer.parseInt(tokens[3]));
					}
					else if(tokens.length==3) {
						SalesScoringWords.put(tokens[0]+" "+tokens[1], Integer.parseInt(tokens[2]));
					}
					else if(tokens.length==2){
						SalesScoringWords.put(tokens[0],Integer.parseInt(tokens[1]));
					}
					else System.out.println("scoring words input length error.");
				}
			}
		}finally{
			br2.close();
			System.out.println("sales scoring words loaded.");
		}
		
		//load customer service scoring words
	    Hashtable<String, Integer> CustomerServiceScoringWords = new Hashtable<String, Integer>();
		BufferedReader br3 = new BufferedReader(new FileReader("customer_service_scoring_words.txt"));
		try {
			String line;
			while((line = br3.readLine())!=null){
				String[] tokens = line.split(" ");
				for (int i=0;i<tokens.length;i++){
					if(tokens.length==4){
						CustomerServiceScoringWords.put(tokens[0]+" "+tokens[1]+" "+tokens[2], Integer.parseInt(tokens[3]));
					}
					else if(tokens.length==3) {
						CustomerServiceScoringWords.put(tokens[0]+" "+tokens[1], Integer.parseInt(tokens[2]));
					}
					else if(tokens.length==2){
						CustomerServiceScoringWords.put(tokens[0],Integer.parseInt(tokens[1]));
					}
					else System.out.println("scoring words input length error.");
				}
			}
		}finally{
			br3.close();
			System.out.println("Customer service scoring words loaded.");
		}
		
		//read resume documents
		System.out.println("Parsing ...");
		while((pdfFileName = br1.readLine())!=null){
			//parse PDF
			//System.out.println("Processing "+pdfFileName+".pdf");
			PDFParser parser = null;
		    PDDocument pdDoc = null;
		    COSDocument cosDoc = null;
		    PDFTextStripper pdfStripper;
		    String parsedText;
		    File file = new File("./pdf/"+pdfFileName+".pdf");
		    try{
		    		parser = new PDFParser(new FileInputStream(file));
		    }catch(Exception e){
		    		
		    }
		    //try PDF
		    try{
			    parser.parse();
			    cosDoc = parser.getDocument();
			    pdfStripper = new PDFTextStripper();
			    pdDoc = new PDDocument(cosDoc);
			    parsedText = pdfStripper.getText(pdDoc);
			//try Doc
		    }catch(Exception e){
		    		WordExtractor extractor = null ;
		    		FileInputStream fis=new FileInputStream(file.getAbsolutePath());
		    		try{
		    			HWPFDocument document=new HWPFDocument(fis);
		    			extractor = new WordExtractor(document);
		    		}catch(Exception ee){
		    			//System.out.println("extract word failed.######");
		    			continue;
		    		}
		    		parsedText = extractor.getText();
		    }
		    //filtering
		    	parsedText = parsedText		    	
		    				.replaceAll("\\&"," ")
		    				.replaceAll("\\!"," ")
					    .replaceAll("\\+"," ")
					    	.replaceAll("[^A-Za-z0-9. ]+", "")
					    .replaceAll("\\p{P}", "")
					    .replaceAll("\\d","")
					    .replaceAll("( )+", " ")
					    .toLowerCase();
		    //System.out.println("done parse.");
		    if (cosDoc != null) cosDoc.close();
		    if (pdDoc != null) pdDoc.close();
		    
		    //remove stop words & counting
		    String[] token = parsedText.split(" ");
		    int length = token.length;
		    if (length<50) continue;
		    int SalesScore = 0;
		    int CustomerServiceScore = 0;
		    String foundSalesWords = "";
		    String foundCustomerServiceWords="";
		    for(int i=0;i<length;i++){
		    		if (!stopWordsSet.contains(token[i]) && token[i].length()>2 && token[i].length()<20){
		    			//count sales score
		    			if(i < token.length-2){
		    				if (SalesScoringWords.containsKey(token[i]+" "+token[i+1]+" "+token[i+2])){
			    				SalesScore+=SalesScoringWords.get(token[i]+" "+token[i+1]+" "+token[i+2]);
			    				foundSalesWords+=token[i]+" "+token[i+1]+" "+token[i+2]+",";
			    				i+=2;
			    			}else if (SalesScoringWords.containsKey(token[i]+" "+token[i+1])){
			    				SalesScore+=SalesScoringWords.get(token[i]+" "+token[i+1]);
			    				foundSalesWords+=token[i]+" "+token[i+1]+",";
			    				i++;
			    			}
			    			else if (SalesScoringWords.containsKey(token[i])){
			    					SalesScore+=SalesScoringWords.get(token[i]);
			    					foundSalesWords+=token[i]+",";
			    			}
		    			}else if(i == token.length-2){
			    			if (SalesScoringWords.containsKey(token[i]+" "+token[i+1])){
			    				SalesScore+=SalesScoringWords.get(token[i]+" "+token[i+1]);
			    				foundSalesWords+=token[i]+" "+token[i+1]+",";
			    				i++;
			    			}
			    			else if (SalesScoringWords.containsKey(token[i])){
			    					SalesScore+=SalesScoringWords.get(token[i]);
			    					foundSalesWords+=token[i]+",";
			    			}
		    			}else if(i == token.length-1){
		    				if (SalesScoringWords.containsKey(token[i])){
		    					SalesScore+=SalesScoringWords.get(token[i]);
		    					foundSalesWords+=token[i]+",";
		    				}
		    			}
		    			//count customer service score
		    			if(i < token.length-2){
		    				if (CustomerServiceScoringWords.containsKey(token[i]+" "+token[i+1]+" "+token[i+2])){
		    					CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]+" "+token[i+1]+" "+token[i+2]);
		    					foundCustomerServiceWords+=token[i]+" "+token[i+1]+" "+token[i+2]+",";
			    				i+=2;
			    			}else if (CustomerServiceScoringWords.containsKey(token[i]+" "+token[i+1])){
			    				CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]+" "+token[i+1]);
			    				foundCustomerServiceWords+=token[i]+" "+token[i+1]+",";
			    				i++;
			    			}
			    			else if (CustomerServiceScoringWords.containsKey(token[i])){
			    				CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]);
			    				foundCustomerServiceWords+=token[i]+",";
			    			}
		    			}else if(i == token.length-2){
			    			if (CustomerServiceScoringWords.containsKey(token[i]+" "+token[i+1])){
			    				CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]+" "+token[i+1]);
			    				foundCustomerServiceWords+=token[i]+" "+token[i+1]+",";
			    				i++;
			    			}
			    			else if (CustomerServiceScoringWords.containsKey(token[i])){
			    				CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]);
			    				foundCustomerServiceWords+=token[i]+",";
			    			}
		    			}else if(i == token.length-1){
		    				if (CustomerServiceScoringWords.containsKey(token[i])){
		    					CustomerServiceScore+=CustomerServiceScoringWords.get(token[i]);
		    					foundCustomerServiceWords+=token[i]+",";
		    				}
		    			}
		    		}
		    }
		    bw.write(pdfFileName+" "+SalesScore+"  Found: "+foundSalesWords+"\n");
		    bw2.write(pdfFileName+" "+CustomerServiceScore+"  Found: "+foundCustomerServiceWords+"\n");
		    //System.out.println("           Sales Score = "+SalesScore);
		    //System.out.println("Customer Service Score = "+CustomerServiceScore);
		    //System.out.println();
		}
		br1.close();
		bw.close();
		bw2.close();
		System.out.println("Done.");
	}
}