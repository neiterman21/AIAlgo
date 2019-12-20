/**
 * 
 */

/**
 * @author evgeny
 * 17 Nov 2019
 */
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomQuery {
	String query_line;
	protected ArrayList<Bvar> bayes_net;
	
	public RandomQuery(ArrayList<Bvar> bayes_net_) {
		bayes_net = bayes_net_;
	}
	
	public void print() {}
	
	protected boolean isAncestor(Bvar father , Bvar son) {
		if(father.equals(son)) return true;
		for (Bvar v : son.parents) {
			if(isAncestor(father,v)) return true;
		}
		return false;
	}
	
	public static RandomQuery QueryBuilder(String query_line , ArrayList<Bvar> bayes_net) {
		Matcher m = Pattern.compile("P\\((.+)\\|(.*)\\),(.*)").matcher(query_line);
		if (m.find()) return new VariableEliminationQuery(m.group(1) ,m.group(2), m.group(3) ,bayes_net );
		
		m = Pattern.compile("(.+)\\|(.*)").matcher(query_line);
		if (m.find()) return new BayesBallQuery(m.group(1) ,m.group(2) ,bayes_net);	 
		
		return null;
	}
	public void Solve(FileWriter fr) throws IOException{}
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
	
	@SuppressWarnings("unchecked")
	public BayesBallQuery(ArrayList<VarVal> givan_ , Bvar v1 , Bvar v2 , ArrayList<Bvar> bayes_net_){
		super(bayes_net_);
		dep1 = v1;
		dep2 = v2;
		givan = (ArrayList<VarVal>)givan_.clone();
	}
	
	@Override
	public void Solve(FileWriter fr) throws IOException {
		reset(); //clean the network 
		color(); //set the givan variables
			
		if (haspath(dep1, dep2, true) || haspath(dep1, dep2, false)) {
			fr.write("no"+ "\n");
			System.out.println("no");
		}
		else {
			fr.write("yes"+ "\n");
			System.out.println("yes");
		}
	}

	
	private void reset() {
		for(Bvar b : bayes_net) {
			b.reset();
		}
	}
	
	private void color() {
		for (VarVal v : givan) {
			Bvar.getBvarByName(bayes_net,v.name).meta_data.colored = true;
		}
	}
	
	private boolean haspath(Bvar s , Bvar t , boolean going_up) {
		//Found a path
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
			else { //I am uncolored
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
	ArrayList<Factor> factors = new ArrayList<Factor>();
	static int mul = 0;
	static int add = 0;
	
	public VariableEliminationQuery(String var_ ,String givan_ ,String hiden_ ,ArrayList<Bvar> bayes_net_) {
		super(bayes_net_);
		//set main var
		var = new VarVal(var_.split("=")[0], var_.split("=")[1]);
		//set givan
		String[] given_vars = givan_.split(",",0);	

		for (String v : given_vars) {
			String[] pv = v.split("=", 2);
			if(pv.length < 2) break;
			givan.add(new VarVal(pv[0],pv[1]));
			Bvar bv = Bvar.getBvarByName(bayes_net_,v);
			if(bv != null)
				bv.value = pv[1];	
		}

		//set hiden vars
		String[] hiden_vars = hiden_.split("-",0);
		for (String h : hiden_vars) {
			Bvar v = Bvar.getBvarByName(bayes_net_,h);
			if(v != null)
				hiden.add(v);
		}
			
	}
	
	@Override
	public void Solve(FileWriter fr) throws IOException{
		removeIrelevant();
		creatFactors();
		removeEvidance();
		
		for (Bvar h : hiden) {
			eliminate_var(h);
		}
		Factor f = joinOn(new Bvar(var.name));
		if(hiden.size() > 0)
			f.normalize();
		
		System.out.println(new DecimalFormat("#0.00000").format(f.getProb(var)) +"," +add + "," +mul);
		fr.write(new DecimalFormat("#0.00000").format(f.getProb(var)) +"," +add + "," +mul + "\n");
		add = 0;
		mul = 0;
	}	
	public void creatFactors() {
		for (Bvar b : bayes_net) {
			factors.add(new Factor(b));
		}	
	}
	
	//checks if hidden variables are ancestors of main query or the evidence. 
	//will remove them from bays net if irelevant
	public void removeIrelevant() { 
		Iterator<Bvar> itr = hiden.iterator();
		while(itr.hasNext()) {
			boolean isrelevant = false;
			Bvar h = itr.next();
			for(VarVal v : givan) {
				Bvar check = Bvar.getBvarByName(bayes_net, v.name);
				if(isAncestor(h,check)) isrelevant = true;
			}
			Bvar check = Bvar.getBvarByName(bayes_net, var.name);
			if(isAncestor(h,check)) isrelevant = true;
			
			if(!isrelevant) {
				bayes_net.remove(h);
				itr.remove();
			}
		}
	}
	
	private void  removeEvidance() {
		for (VarVal e: givan) {
			Iterator<Factor> itr = factors.iterator();
			while (itr.hasNext()) {
				Factor f = itr.next();
				f.removeEvidance(e);
				if(f.table.size() == 1) {
					itr.remove();
				}
			}
		}
	}
	
	private Factor joinOn(Bvar h) {
		ArrayList<Factor> to_join = new ArrayList<Factor>();
		Iterator<Factor> itr = factors.iterator();
		while (itr.hasNext()) {
			
			Factor f = itr.next();
			if(f.contains(h)) {
				to_join.add(f);
				itr.remove();
			}
		}
		Collections.sort(to_join);
		
		Factor joinf = new Factor();
		for (Factor f : to_join) {
			joinf.Join(f ,h);
		}
		return joinf;
	}

	private void eliminate_var(Bvar h) {
		Factor joinf = joinOn(h);
		joinf.sumOut(h);
		factors.add(joinf);		
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





