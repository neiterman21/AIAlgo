/**
 * 
 */

/**
 * @author evgeny
 * 15 Nov 2019
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Iterator; 


import java.lang.UnsupportedOperationException;
import java.io.*; 
public class parser {

	public ArrayList<Bvar> variables = new ArrayList<Bvar>(); //list of all the variables in the bayes network
	public ArrayList<RandomQuery> queries = new ArrayList<RandomQuery>(); 
	Bvar hot_var; //variable curently working on
	
	String input_file = "../input.txt";
		
	Matcher m; 
	
	public  parser(String input)
	{
		input_file = input;
	}
	
	public parser() {}
	

	
	private  ArrayList<Bvar> creatVariables(String st) {
		String[] variables = st.split(",", 0);
		ArrayList<Bvar> vars = new ArrayList<Bvar>();
		for (String var : variables) {
			Bvar new_bvar = new Bvar(var);
			vars.add(new_bvar) ;
		}

		return vars;
	}
	
	private void setVariableValues(String line) {
		m = Pattern.compile("Values:").matcher(line);
		//set var posible values 
		if (m.find()) {
			String values_string = line.split(" ",0)[1];
			String[] values = values_string.split(",", 0);
			for (String val : values) {
				hot_var.values.add(val);
			}
					
		}
		//set var parrents 
		m = Pattern.compile("Parents:").matcher(line);
		//set var posible values 
		if (m.find()) {
			String parents_string = line.split(" ",0)[1];
			String[] parents = parents_string.split(",", 0);
			if (parents[0].compareTo("none") == 0) return;
			for (String parent : parents) {			
				hot_var.parents.add(Bvar.getBvarByName(variables,parent));
			}
					
		}		
		return;
	}
	
	private void setCPTValues(String line) {
		String[] attrs = line.split(",", 0);
		ArrayList<VarVal> parents_values = new ArrayList<VarVal>();
		ArrayList<ValProb> value_probs  = new ArrayList<ValProb>();
		Iterator itr_prt = hot_var.parents.iterator();

		for (int i = 0; i < hot_var.parents.size() ; i++) parents_values.add(new VarVal(((Bvar)itr_prt.next()).name , attrs[i]));
		
		for (int i = hot_var.parents.size(); i < attrs.length ; i+=2) {
			value_probs.add(new ValProb( attrs[i].substring(1,attrs[i].length()), Double.parseDouble(attrs[i+1])));
		}
		hot_var.cpt.entries.add(new CPT_entry(parents_values , value_probs));
	}
	
	private void setQueris(String line) throws Exception {
		queries.add(RandomQuery.QueryBuilder(line, variables) );
		return;
	}
	
	private void setChildren() {
		for (Bvar v : variables) {
			for (Bvar pc : variables) {
				if (pc.parents.contains(new Bvar(v.name)))
					v.children.add(pc);
			}
		}
	}
	
	public void parse() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(input_file)); 
		String st; 
		boolean on_var = false , on_cpt = false , on_queries = false;
		
		while ((st = br.readLine()) != null) {
			//System.out.println("D " + st);
			//ignore empty lines
			m = Pattern.compile("^\\s*$").matcher(st);
			if (m.find()) continue;
			
			//look for all the variables name in the second row and creat them	
			m = Pattern.compile("Variables:").matcher(st);
			if (m.find()) {
				variables = creatVariables(st.split(" ",0)[1]);
				continue;
			}
			
			//put the hot var in its place
			m = Pattern.compile("Var (\\w)").matcher(st);
			if (m.find()) {
				hot_var = Bvar.getBvarByName(variables,m.group(1)); 
				on_var = true; on_cpt = false;
				continue;
			}
			//put the hot var in its place
			m = Pattern.compile("CPT:").matcher(st);
			if (m.find()) {
				assert(on_var);
				on_cpt = true;
				continue;
			}
			//start parsing wueries mode
			m = Pattern.compile("Queries").matcher(st);
			if (m.find()) {
				on_var 		= false;
				on_cpt 		= false;
				on_queries 	= true;
				continue;
			}
			
			
			if (on_var && !on_cpt) setVariableValues(st);
			if (on_var && on_cpt)  setCPTValues(st);
			if (on_queries)  setQueris(st);
					
		} 
		setChildren();
	}
	
	public static void main(String[] args) throws Exception {
		parser p = new parser("../input.txt");
		p.parse();
		
		for( Bvar v :p.variables) {
			//v.print();
		}
		
		for( RandomQuery q :p.queries) {
			//q.print();
			q.Solve();
		}
		
		
	}

}
