package Recup;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Indice 
{
	private String nom;
	private double last;
	
	public Indice(double val, String nom, String pays)
	{
		this.nom = nom;
		this.last = val;
	}
	
	public void addVal(double d, Statement st, String type)
	{
		if(last!=d)
		{
			String SQL = "INSERT INTO vals_"+type+" (ID,VAL) VALUES ((SELECT ID FROM liste_"+type+" WHERE NOM='"+nom+"'),"+d+")";
			last = d;
			try 
			{
				st.executeQuery(SQL);
			} 
			catch (SQLException e) {show("Erreur insert new val : "+SQL);}
		}
	}
	
	private void show(String s)
	{
		Timestamp t = new Timestamp(System.currentTimeMillis());
		System.out.println(String.format("%-30s", t) + " | " + s);
	}
	
	public String toString() {return this.nom + " : " + this.last;}
}
