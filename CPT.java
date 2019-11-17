/**
 * @author evgeny
 * 14 Nov 2019
 */
import java.util.ArrayList;
import java.util.Collections;

/*
 * represants a probabilety to get a spesific value of a variable
 */
class ValProb {
	public double prob;
	public String val;
	
	ValProb ( String val_ , double prob_ ) {
		prob = prob_;
		val = val_;
	}
}

/*
 * represants a probabilety to get a spesific value of a variable
 */
class VarVal {
	public String name;
	public String val;
	
	VarVal (String val_name_ , String val_) {
		name = val_name_;
		val = val_;
	}
}



/*
 * represents an entry in the CPT table. 
 * contains the givan parrents values as an array list
 * and hold the probaliletis of the random variable to get any of its values.
 */
class CPT_entry {
	
	public ArrayList<VarVal> parents_values = new ArrayList<VarVal>();
	public ArrayList<ValProb> value_probs  = new ArrayList<ValProb>();
	
	public CPT_entry(ArrayList<VarVal> parents_values_, ArrayList<ValProb> value_probs_) {
		parents_values = parents_values_;
		value_probs    = value_probs_;
	}
	
	public boolean contains(ArrayList<String> subset) {
		//for (String val)
		return true;
	}
	
	public void print() {
		for (VarVal p : parents_values) System.out.print(p.val + ",");
		for (ValProb vp : value_probs) System.out.print("=" + vp.val + "," + vp.prob);
		System.out.print("\n");
	}
}

public class CPT {
	
	public ArrayList<CPT_entry> entries = new ArrayList<CPT_entry>();
	
	public void addEntry(CPT_entry entry) {
		entries.add(entry);
	}
	
	public double getProb(ArrayList<String> known_dependencies , String val) {
		for (CPT_entry e : entries) {
			return  0.1;	
			}
		return 0.1;
	}
	
	public void print() {
		System.out.println("CPT:");
		for (CPT_entry e : entries) e.print();
		System.out.println("");
	}
	
}







