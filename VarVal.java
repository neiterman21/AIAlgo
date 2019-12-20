/**
 * 
 */

/**
 * @author evgeny
 * 19 Dec 2019
 */
/*
 * represants a probabilety to get a spesific value of a variable
 */


public class VarVal {
	public String name;
	public String val;
	
	public VarVal (String val_name_ , String val_) {
		name = val_name_;
		val = val_;
	}
	
	public VarVal (VarVal copy) {
		name = copy.name;
		val = copy.val;
	}
	
	@Override
	public boolean equals(Object o) {
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        }
        /* Check if o is an instance of Complex or not 
        "null instanceof [type]" also returns false */
      if (!(o instanceof VarVal)) { 
          return false; 
      }
   // typecast o to Complex so that we can compare data members  
      VarVal comp = (VarVal) o; 
      
      return this.name.equals(comp.name);
	}
	
	@Override
    protected Object clone() throws CloneNotSupportedException {
		VarVal clone = null;
        try
        {
            clone = (VarVal) super.clone();
 
            //Copy new date object to cloned method
            clone.name = name;
            clone.val = val;
        } 
        catch (CloneNotSupportedException e) 
        {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
