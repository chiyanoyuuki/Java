package v1;

public class IndiceVal 
{
	private double val;
	private String date;
	
	public IndiceVal(double val, String date)
	{
		this.val = val;
		this.date = date;
	}
	
	public IndiceVal() {}
	
	public double getVal() {return this.val;}
	public String getDate() {return this.date;}
	
	public void setVal(double val) {this.val=val;}
	public void setDate(String date) {this.date=date;}
	
	 @Override
	 public String toString() {
	  return 
			 "val : " + this.val +
			 "date : " + this.date
			 ;
	 }
}