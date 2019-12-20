/**
 * @author evgeny
 * 14 Nov 2019
 */
public class ex1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		parser p = new parser("input2.txt");
		p.parse();

		for( RandomQuery q :p.queries) {
			q.print();
			q.Solve();
		}
	}

}
