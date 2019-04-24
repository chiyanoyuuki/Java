package Launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Indice 
{
	private ArrayList<Val> vals;
	private int ID;
	private String TYPE, NOM;
	private String maxDate;
	private boolean init = false;
	
	private Val max, min;
	private double quotient;
	
	public Indice(String type, int id, String nom)
	{
		this.TYPE = type;
		this.ID = id;
		this.NOM = nom;
	}
	
	public void start()
	{
		System.out.println();
		System.out.println(NOM + " : START");
		try
		{
			this.init();
			this.analyze();
			init = true;
		}
		catch(Exception e){error("INIT "+TYPE+ " "+NOM);}
	}
	
	private void analyze()
	{
		maxDate = vals.get(vals.size()-1).date();
		double min = 1000000;
		double max = 0;
		for(Val v: vals)
		{
			double tmp = v.val();
			if(tmp<min) {min=tmp;this.min = v;}
			if(tmp>max) {max=tmp;this.max = v;}
		}
		System.out.println(NOM + " : " + min + " <==> " + max);
		this.quotient = Game.TAILLEGRAPHY/(this.max.val()-this.min.val());
		System.out.println("Quotient : " + quotient);
	}
	
	private void init() throws SQLException, IOException, ClassNotFoundException, InterruptedException
	{
    	int max = App.nbVals;
		vals = new ArrayList<Val>();
		String SQL = "SELECT * FROM VALS_"+TYPE+" WHERE ID="+ID+" ORDER BY DATE DESC LIMIT " + max;
		System.out.println(SQL);
		ResultSet rs = App.SQL.executeQuery(SQL);
		while(rs.next())
    	{
			vals.add(0,new Val(rs.getDouble(2),rs.getString(3)));
    	}
		int nb = vals.size();
		System.out.println(nb +" from BDD");
    	if(nb<max)
    	{
    		initFics(max-nb);
    	}
	}
	
	public void update()
	{
		String SQL = "SELECT * FROM VALS_"+TYPE+" WHERE ID="+ID+" AND DATE>'"+maxDate+"' ORDER BY DATE LIMIT " + App.nbVals;
		try
		{
			ResultSet rs = App.SQL.executeQuery(SQL);
			while(rs.next())
	    	{
				Val v = new Val(rs.getDouble(2),rs.getString(3));
				vals.add(v);
				vals.remove(0);
				
				if(v.val()<min.val())
				{
					this.min = v;
					this.quotient = Game.TAILLEGRAPHY/(this.max.val()-this.min.val());
				}
				else if(v.val()>max.val())
				{
					this.max = v;
					this.quotient = Game.TAILLEGRAPHY/(this.max.val()-this.min.val());
				}				
	    	}
			maxDate = vals.get(vals.size()-1).date();
		}
		catch(Exception e) {error("UPDATE "+TYPE+ " "+NOM);}
	}
	
	private void initFics(int need) throws IOException, ClassNotFoundException, SQLException, InterruptedException
	{
		BufferedReader reader = getFics(need);
		String line;
        while ((line = reader.readLine()) != null) 
        {
        	String[] tmp = line.split(";");
        	vals.add(0,new Val(Double.parseDouble(tmp[1]),tmp[2]));
        }
		reader.close();
	}
	
	private BufferedReader getFics(int need) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		String path = App.path +"cache/"+TYPE+"/";
		String commande = "sed -n \"/^"+ID+";.*/p\" *.csv | tail -"+need;
		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",commande);
		pb.directory(new File(path));
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader;
	}
	
	public String nom()			{return this.NOM;}
	public boolean isinit()		{return this.init;}
	public String type()		{return this.TYPE;}
	public ArrayList<Val> vals(){return this.vals;}
	public Val max() 			{return this.max;}
	public Val min() 			{return this.min;}
	public double quotient() 	{return this.quotient;}

    private void show	(String message){System.out.println("INFO   : " + message);}
    private void error	(String error)	{System.out.println("ERREUR : " + error);}
}
