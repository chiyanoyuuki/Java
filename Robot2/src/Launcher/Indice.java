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

public class Indice implements Comparable<Indice> 
{	
	private ArrayList<Val> vals;
	private ArrayList<MoyenneMobile> mms;
	private int ID;
	private String TYPE, NOM;
	private String minDate, maxDate;
	private boolean init = false;
	
	private Val max, min;
	private double quotient;
	private long timer;
	
	private double etat, isetat, pctetat, coef;
	private Val debetat,maxetat,minetat,depart;
	
	private double average, totavg;
	private double status;
	
	public Indice(String type, int id, String nom)
	{
		timer = System.currentTimeMillis();
		this.TYPE = type;
		this.ID = id;
		this.NOM = nom;
	}
	
	public void start()
	{
		System.out.println(TYPE + " : " + NOM);
		try
		{
			this.init();
			this.analyze();
			init = true;
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	private void analyze()
	{
		minDate = vals.get(0).date();
		maxDate = vals.get(vals.size()-1).date();
		double min = 1000000;
		double max = 0;
		mms = new ArrayList<MoyenneMobile>();
		mms.add(new MoyenneMobile(2));
		mms.add(new MoyenneMobile(5));
		mms.add(new MoyenneMobile(20));
		for(Val v: vals)
		{
			double tmp = v.val();
			totavg+=tmp;
			if(tmp<min) {min=tmp;this.min = v;}
			if(tmp>max) {max=tmp;this.max = v;}
		}
		average=totavg/vals.size();
		quotient = Game.TAILLEGRAPHY/(this.max.val()-this.min.val());
		etat = 0;
		debetat = vals.get(0);
		maxetat = debetat;
		minetat = debetat;
		
		for(Val v: vals)
		{
			check(v);
		}
	}
	
	private void check(Val v)
	{		
		for(MoyenneMobile mm : mms)
		{
			if(mm.add(v.val())){mm.remove(vals.get(mm.first()).val());}
		}
		
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
		
		quotient = Game.TAILLEGRAPHY/(this.max.val()-this.min.val());
		coef = (max.val()-min.val());
		isetat = coef*0.1;
		
		if(v.val()<minetat.val())minetat=v;
		if(v.val()>maxetat.val())maxetat=v;
		
		if(v.val()-debetat.val()-etat>=isetat||(v.val()-debetat.val()-etat)*-1>=isetat)
		{
			double tmp = v.val()-debetat.val()-etat;
			if(etat>0&&tmp<0)
			{
				debetat=maxetat;
				minetat=debetat;
				etat=v.val()-debetat.val();
			}
			else if(etat<0&&tmp>0)
			{
				debetat=minetat;
				maxetat=debetat;
				etat=v.val()-debetat.val();
			}
			else
			{
				etat = v.val()-debetat.val();
			}
		}
		pctetat = ((v.val()-debetat.val())/coef)*100;
		depart = debetat;
	}
	
	public ArrayList<MoyenneMobile> mms(){return mms;}
	public Val depart() {return depart;}
	
	private void init() throws SQLException, IOException, ClassNotFoundException, InterruptedException
	{
    	int max = App.nbVals;
		vals = new ArrayList<Val>();
		String SQL = "SELECT * FROM VALS_"+TYPE+" WHERE ID="+ID+" ORDER BY DATE DESC LIMIT " + max;
		//System.out.println(SQL);
		ResultSet rs = App.SQL.executeQuery(SQL);
		while(rs.next())
    	{
			vals.add(0,new Val(rs.getDouble(2),rs.getString(3)));
    	}
		int nb = vals.size();
		//System.out.println(nb +" from BDD");
    	if(nb<max)
    	{
    		initFics(max-nb);
    	}
	}
	
	public void update()
	{
		if(System.currentTimeMillis()-timer>200)
		{
			double valtmp = vals.get(vals.size()-1).val();
			String SQL = "SELECT * FROM VALS_"+TYPE+" WHERE ID="+ID+" AND DATE>'"+maxDate+"' ORDER BY DATE LIMIT " + App.nbVals;
			try
			{
				ResultSet rs = App.SQL.executeQuery(SQL);
				while(rs.next())
		    	{
					Val v = new Val(rs.getDouble(2),rs.getString(3));
					vals.add(v);
					totavg+=v.val();
					Val vc = vals.remove(0);
					totavg-=vc.val();
					check(v);
				}
				average=totavg/vals.size();
				minDate = vals.get(0).date();
				maxDate = vals.get(vals.size()-1).date();
			}
			catch(Exception e) {error("UPDATE "+TYPE+ " "+NOM);}
			double valtmp2 = vals.get(vals.size()-1).val();
			status = valtmp2-valtmp;
			timer = System.currentTimeMillis();
		}
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
	
	public double average()		{return this.average;}
	public String minDate()		{return this.minDate;}
	public String maxDate()		{return this.maxDate;}
	public Color getColor()		{if(status<0)return Color.red;else if(status>0)return Color.green;else return Color.white;}
	public String nom()			{return this.NOM;}
	public boolean isinit()		{return this.init;}
	public String type()		{return this.TYPE;}
	public ArrayList<Val> vals(){return this.vals;}
	public Val max() 			{return this.max;}
	public Val min() 			{return this.min;}
	public double quotient() 	{return this.quotient;}
	public double pct()			{return pctetat;}
	public String mvmt()		{return String.format("%8s",String.format("%.2f",pctetat)+"% ");}

    private void show	(String message){System.out.println("INFO   : " + message);}
    private void error	(String error)	{System.out.println("ERREUR : " + error);}

	public int compareTo(Indice o)
	{
		double etat1 = pct();
		double etat2 = o.pct();
		
		if(Double.isNaN(etat1)) etat1 = 0;
		if(Double.isNaN(etat2)) etat2 = 0;
		
		if(etat1<0)etat1=etat1*-1;
		if(etat2<0)etat2=etat2*-1;
		
		if(etat1>etat2)return -1;
		else if(etat1<etat2)return 1;
		else return 0;
	}
}
