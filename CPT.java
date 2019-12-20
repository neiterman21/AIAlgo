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
	
	public ValProb ( String val_ , double prob_ ) {
		prob = prob_;
		val = val_;
	}
	
	public ValProb(ValProb copy) {
		prob = copy.prob;
		val = copy.val;
	}
	
	public boolean equals(Object o) {
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        }
        /* Check if o is an instance of Complex or not 
        "null instanceof [type]" also returns false */
      if (!(o instanceof ValProb)) { 
          return false; 
      }
   // typecast o to Complex so that we can compare data members  
      ValProb comp = (ValProb) o; 
      
      return val.compareTo(comp.val) == 0;
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
	
	public CPT_entry(CPT_entry copy) {
		for (VarVal p : copy.parents_values) {
			parents_values.add(new VarVal(p));
		}
		
		for (ValProb p : copy.value_probs) {
			value_probs.add(new ValProb(p));
		}
	}
	
	boolean inParentsDiffVal(Bvar b) {
		for (VarVal v : parents_values) {
			if(v.name == b.name && v.val != b.value) return true;
		}
		return false;
	}
	
	public void print() {
		for (VarVal p : parents_values) System.out.print(p.val + ",");
		for (ValProb vp : value_probs) System.out.print("=" + vp.val + "," + vp.prob);
		System.out.print("\n");
	}
}

public class CPT {
	
	public ArrayList<CPT_entry> entries = new ArrayList<CPT_entry>();
	
	public CPT() {}
	
	public CPT(CPT copy) {
		for(CPT_entry e : copy.entries) {
			entries.add(new CPT_entry(e));
		}
	}
	
	public void addEntry(CPT_entry entry) {
		entries.add(entry);
	}
	
	public void print() {
		System.out.println("CPT:");
		for (CPT_entry e : entries) e.print();
		System.out.println("");
	}
	
}







