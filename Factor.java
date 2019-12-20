/**
 * 
 */

/**
 * @author evgeny
 * 18 Dec 2019
 */

import java.util.*; 

class Factor_entry {	
	double prob;
	ArrayList<VarVal> variables = new ArrayList<VarVal>();
	ArrayList<VarVal> evidance = new ArrayList<VarVal>();
	
	public String on(Bvar b) {
		VarVal v = new VarVal(b.name , "");
		if(evidance.contains(v)) {
			return evidance.get(evidance.indexOf(v)).val;
		}
		if(variables.contains(v)) {
			return variables.get(variables.indexOf(v)).val;
		}
		return "";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) { 
            return true; 
        }
        /* Check if o is an instance of Complex or not 
        "null instanceof [type]" also returns false */
      if (!(o instanceof Factor_entry)) { 
          return false; 
      }
   // typecast o to Complex so that we can compare data members  
      Factor_entry c = (Factor_entry) o; 
		for (VarVal v : variables) {
			for (VarVal cv : c.variables) {
				if(v.name.equals(cv.name) && !v.val.equals(cv.val)) return false;
			}
		}
		for (VarVal v : evidance) {
			for (VarVal cv : c.evidance) {
				if(v.name.equals(cv.name) && !v.val.equals(cv.val)) return false;
			}
		}
		return true;
	}
	
	void Print() {
		for (VarVal v : evidance) {
			System.out.print(v.val + "\t");
		}
		for (VarVal v : variables) {
			System.out.print(v.val + "\t");
		}
		System.out.println(prob);
	}
	
	public boolean removeEvidance(VarVal e) {
		int index = evidance.indexOf(e);
		if(index != -1 && !evidance.get(index).val.equals(e.val)) return true;
		
		index = variables.indexOf(e);
		if(index != -1 && !variables.get(index).val.equals(e.val)) return true;

		return false;
	}
}

public class Factor implements Comparable<Factor>{
	ArrayList<Bvar> variablesList;
	ArrayList<Bvar> evidanceList;
	ArrayList<Factor_entry> table;
	
	// Default Constructor
	public Factor() {
		this.variablesList = new ArrayList<Bvar>();
		this.evidanceList = new ArrayList<Bvar>();
		this.table = new ArrayList<Factor_entry>();
	}
	
	// Copy Constructor 
	public Factor(Factor f) {
		this.variablesList = f.variablesList;
		this.table = f.table;
		this.evidanceList = f.evidanceList;
	}
	public Factor(Bvar v) {
		this.variablesList = new ArrayList<Bvar>();
		this.evidanceList = new ArrayList<Bvar>();
		this.table = new ArrayList<Factor_entry>();
		
		
		this.variablesList.add(new Bvar(v.name));
		for(Bvar e : v.parents) {
			this.evidanceList.add(new Bvar(e.name));
		}
		for (CPT_entry e : v.cpt.entries) {
			for (ValProb vp : e.value_probs) {
				Factor_entry fentry = new Factor_entry();
				fentry.prob = vp.prob;
				for(VarVal evidan : e.parents_values)
					fentry.evidance.add(new VarVal(evidan));
				fentry.variables.add(new VarVal(v.name, vp.val));
				table.add(fentry);
			}
		}
	}
	
	public void normalize() {
		double prob_sum = table.get(0).prob;
		
		for (int i = 1; i< table.size() ; i++) {
			VariableEliminationQuery.add++;
			prob_sum += table.get(i).prob;
		}
		for (Factor_entry e : table) {
			e.prob = e.prob/prob_sum;
		}
	}
	
	public double getProb(VarVal v) {
		for (Factor_entry e : table) {
			if(e.variables.contains(v)) {
				VarVal check = e.variables.get(e.variables.indexOf(v));
				if(!check.val.equals(v.val)) continue;
				return e.prob;
			}
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public void Join(Factor f, Bvar on) {
		if (table.size() == 0) {
			this.variablesList = f.variablesList;
			this.table = f.table;
			this.evidanceList = f.evidanceList;
			return;
		}
		if (!evidanceList.contains(on) || !f.evidanceList.contains(on)) { //the variable is in both evidance lists
			if(evidanceList.contains(on)) evidanceList.remove(on);
			if(f.evidanceList.contains(on)) f.evidanceList.remove(on);
		}

		for (Bvar b : f.evidanceList) {
			if (!evidanceList.contains(b)) evidanceList.add(b);
		}
		for (Bvar b : f.variablesList) {
			if (!variablesList.contains(b)) variablesList.add(b);
		}
		
		ArrayList<Factor_entry> new_table = new ArrayList<Factor_entry>();
		for(Factor_entry entry : table) {
			for(Factor_entry fentry : f.table) {
				if(entry.on(on).equals(fentry.on(on))) {
					Factor_entry new_entry = new Factor_entry();
					new_entry.prob = entry.prob * fentry.prob;	
					VariableEliminationQuery.mul++;
					new_entry.variables = (ArrayList<VarVal>) entry.variables.clone();
					new_entry.variables.addAll(fentry.variables);
					for(VarVal e : entry.evidance) {
						if(new_entry.variables.contains(e) || new_entry.evidance.contains(e)) continue;
						new_entry.evidance.add(e);
					}
					for(VarVal e : fentry.evidance) {
						if(new_entry.variables.contains(e) || new_entry.evidance.contains(e)) continue;
						new_entry.evidance.add(e);
					}		
					new_table.add(new_entry);			
				}
			}
		}
		table = new_table;	
	}
	
	public void sumOut(Bvar b) {
		VarVal eliminate = new VarVal(b.name , "");
		variablesList.remove(b);
		ArrayList<Factor_entry> new_table = new ArrayList<Factor_entry>();
		for(Factor_entry e : table) {
			e.variables.remove(eliminate);
		}
		Iterator<Factor_entry> itr1 = table.iterator();
		Iterator<Factor_entry> itr2 = table.iterator();
		while(itr1.hasNext()) {
			Factor_entry merge = itr1.next();
			double sum_prob = 0;
			ArrayList<Factor_entry> toremove = new ArrayList<Factor_entry>();
			for(Factor_entry merge2 : table) {
				if(merge.equals(merge2)) {
					toremove.add(merge2);	
					if(sum_prob == 0) 
						sum_prob = merge2.prob;
					else {
						sum_prob = sum_prob +  merge2.prob;
						VariableEliminationQuery.add++;			
					}
				}
			}
			if(toremove.size() > 0) {
				Factor_entry e = new Factor_entry();
				e.evidance = merge.evidance;
				e.variables = merge.variables;
				e.prob = sum_prob;
				new_table.add(e);
				table.removeAll(toremove);
			}
			itr1 = table.iterator();
			itr2 = table.iterator();
		}
		table = new_table;
	}
	
	public void removeEvidance(VarVal e) {
	//	variablesList.remove(new Bvar(e.name));
		Iterator<Factor_entry> itr = table.iterator();
		while (itr.hasNext()) {
			Factor_entry entry = itr.next();

            if (entry.removeEvidance(e)) {
            	itr.remove();
            }
        }
		
	}
	
	public boolean contains(Bvar b) {
		return variablesList.contains(b) || evidanceList.contains(b);
	}
	
	void Print() {
		for (Bvar b : evidanceList) {
			System.out.print(b.name + "\t");
		}
		System.out.print("##");
		
		for (Bvar b : variablesList) {
			System.out.print(b.name + "\t");
		}
		System.out.println("prob");
		
		for (Factor_entry e : table) {
			e.Print();
		}
		System.out.println();
	}
	
	@Override     
	public int compareTo(Factor candidate) {          
	    return this.table.size() - candidate.table.size(); 
	  }  
	
	
}
