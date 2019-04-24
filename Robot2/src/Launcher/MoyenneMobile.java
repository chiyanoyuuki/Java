package Launcher;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class MoyenneMobile 
{
	private ArrayList<Double> vals;
	private int div, nb;
	private double total;
	private Color color;
	
	public MoyenneMobile(int i)
	{
		nb=0;
		total=0;
		vals = new ArrayList<Double>();
		this.div = App.nbVals/i;
		int x = 155+i*10;
		color = new Color(x,x,x);
	}
	
	public boolean add(double d)
	{
		total+=d;
		nb++;
		
		if(nb>div)	return true;
		this.vals.add(total/nb);
		return false;
	}
	
	public void remove(double d)
	{
		total-=d;
		this.vals.add(total/div);
	}
	
	public int first(){return nb-div;}
	public Color color() {return color;}
	
	public ArrayList<Double> vals(){return vals;}
}
