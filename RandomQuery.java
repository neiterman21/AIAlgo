/**
 * 
 */

/**
 * @author evgeny
 * 17 Nov 2019
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomQuery {
	String query_line;
	protected ArrayList<Bvar> bayes_net;
	
	public RandomQuery(ArrayList<Bvar> bayes_net_) {
		bayes_net = bayes_net_;
	}
	
	public void print() {}
	
	public static RandomQuery QueryBuilder(String query_line , ArrayList<Bvar> bayes_net) {
		Matcher m = Pattern.compile("P\\((.+)\\|(.+)\\),(.*)").matcher(query_line);
		if (m.find()) return new VariableEliminationQuery(m.group(1) ,m.group(2), m.group(3) ,bayes_net );
		
		m = Pattern.compile("(.+)\\|(.*)").matcher(query_line);
		if (m.find()) return new BayesBallQuery(m.group(1) ,m.group(2) ,bayes_net);	 
		
		return null;
	}
	public void Solve() {}
}

class BayesBallQuery extends RandomQuery {
	Bvar dep1 , dep2;
	ArrayList<VarVal> givan = new ArrayList<VarVal>();
	
	public BayesBallQuery(String dependancy_ , String givan_ , ArrayList<Bvar> bayes_net_) {
		super(bayes_net_);
		dep1 = Bvar.getBvarByName(bayes_net_,dependancy_.split("-", 2)[0]);
		dep2 = Bvar.getBvarByName(bayes_net_,dependancy_.split("-", 2)[1]);
		
		
		String[] given_vars = givan_.split(",",0);
		
		for (String v : given_vars) {
			String[] pv = v.split("=", 2);
			if(pv.length < 2) return;
			givan.add(new VarVal(pv[0],pv[1]));
		}
	}
	
	@Override
	public void Solve() {
		for(Bvar b : bayes_net) {
			b.reset();
		}
			
		for (VarVal v : givan) {
			Bvar.getBvarByName(bayes_net,v.name).meta_data.colored = true;
		}
		if (haspath(dep1, dep2, true) || haspath(dep1, dep2, false)) {
			System.out.println("no");
		}
		else {
			System.out.println("yes");
		}
		
		
	}
	
	private boolean haspath(Bvar s , Bvar t , boolean going_up) {
		if (s.equals(t)) return true;
		if(going_up) { //going up
			s.meta_data.visited_up = true;
			if (s.meta_data.colored == true) { //I am colored 
				return false;
			}
			else { //I am uncolored				
				for (Bvar chield : s.children) {
					if (chield.meta_data.visited_down == false && haspath(chield , t , false)) return true;
				}
				for (Bvar parent : s.parents) {
					if (parent.meta_data.visited_up == false && haspath(parent , t , true)) return true;
				}
			}
		}
		else { //going down
			s.meta_data.visited_down = true;
			if (s.meta_data.colored == true) { //I am colored 
				for (Bvar parent : s.parents) {
					if (parent.meta_data.visited_up == false && haspath(parent , t , true)) return true;
				}
			}
			else {
				//I am colored
				for (Bvar chield : s.children) {
					if (chield.meta_data.visited_down == false && haspath(chield , t , false)) return true;
				}
			}
		}
		
		
		return false;
	}
	
	@Override
	public void print() {
		System.out.println("BayesBallQuery:");
		System.out.println("dependancy: " + dep1.name + " " + dep2.name );
		for (VarVal vv : givan) {
			System.out.println("givan: " + vv.name + " = " + vv.val);
		}
		System.out.println("");
	}
}

class VariableEliminationQuery extends RandomQuery {
	
	VarVal var;
	ArrayList<VarVal> givan = new ArrayList<VarVal>();
	ArrayList<Bvar> hiden = new ArrayList<Bvar>();
	
	public VariableEliminationQuery(String var_ ,String givan_ ,String hiden_ ,ArrayList<Bvar> bayes_net_) {
		super(bayes_net_);
		//set main var
		var = new VarVal(var_.split("=")[0], var_.split("=")[1]);
		//set givan
		String[] given_vars = givan_.split(",",0);		
		for (String v : given_vars) {
			String[] pv = v.split("=", 2);
			if(pv.length < 2) return;
			givan.add(new VarVal(pv[0],pv[1]));
		}
		//set hiden vars
		String[] hiden_vars = hiden_.split("-",0);
		for (String h : hiden_vars)
			hiden.add(Bvar.getBvarByName(bayes_net_,h));
	}

	@Override
	public void print() {
		System.out.println("VariableEliminationQuery:");
		System.out.println("var: " + var.name + " = " + var.val);
		System.out.println("givan:");
		for (VarVal vv : givan) {
			System.out.print( vv.name + " = " + vv.val);
		}
		System.out.println("");
		System.out.println("hiden:");
		for (Bvar h : hiden) {
			System.out.print(h.name + " ");
		}
		System.out.println("");
	}	
}





