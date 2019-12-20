import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author evgeny
 * 14 Nov 2019
 */
public class Ex1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		parser p = null;
		if(args.length > 0)
			 p = new parser(args[0]);
		else 
			 p = new parser("input2.txt");
		p.parse();
		File file = new File("output.txt");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            for( RandomQuery q :p.queries) {
    			q.Solve(fr);
    		}
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
	}

}
