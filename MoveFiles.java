import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MoveFiles {

	public static void main(String[] args) throws IOException {
    		InputStream inStream = null;
    		OutputStream outStream = null;
		BufferedReader br1 = new BufferedReader(new FileReader("sales_score.txt"));
		String fileName_score;
		
		//create folders
		File dir = new File("0_19");
		dir.mkdir();
		dir = new File("20_29");
		dir.mkdir();
		dir = new File("30_39");
		dir.mkdir();
		dir = new File("40_max");
		dir.mkdir();
		
		while((fileName_score = br1.readLine())!=null){
			
			//parse the input file
			String[] token = fileName_score.split(" ");
			File afile =new File("./pdf/"+token[0]+".pdf");	//input file
			inStream = new FileInputStream(afile);
			
			//determine destination
			File bfile;
			if (Integer.parseInt(token[1])>=40){
				 bfile =new File("./40_max/"+token[0]+".pdf");
			}else if (Integer.parseInt(token[1])>=30){
				 bfile =new File("./30_39/"+token[0]+".pdf");
			}else if (Integer.parseInt(token[1])>=20){
				 bfile =new File("./20_29/"+token[0]+".pdf");
			}else{
				 bfile =new File("./0_19/"+token[0]+".pdf");
			}
			
			//write to the destination
			outStream = new FileOutputStream(bfile);
			int length;
			byte[] buffer = new byte[60000000];
			while ((length = inStream.read(buffer)) > 0){
				outStream.write(buffer, 0, length);
    	    		}
			
    	    		inStream.close();
    	    		outStream.close();
		}
		System.out.println("Files copied successful!");

	}
}
