package Recup;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Launcher
{
	public static void main(String[] args) 
	{
		String[][] SITES = new String[][] 
				{
					//0SITE																			1NOM BDD		2TABLE,	3PAYS,	4NOM,	5VAL	6TYPE
					{"https://www.investing.com/indices/indices-cfds",								"cfd",			"1",	"0",	"1",	"2", 	"indices"},
					{"https://www.investing.com/indices/indices-futures",							"futures",		"0",	"0",	"1",	"3",	"indices"},
					{"https://www.investing.com/indices/germany-30-components",						"actionsvals",	"1",	"0",	"1",	"2",	"actions"},
					{"https://www.investing.com/indices/investing.com-united-states-30-components",	"actionsvals",	"0",	"0",	"1",	"2",	"actions"},
					{"https://www.investing.com/indices/nq-100-components",							"actionsvals",	"1",	"0",	"1",	"2",	"actions"},
					{"https://www.investing.com/commodities/real-time-futures",						"matieresvals",	"0",	"0",	"1",	"3",	"matieres"}/*
					{"https://fr.investing.com/equities/united-states",			"actionsvals",	"0",	"0",	"1",	"2",	"actions"},
					{"https://fr.investing.com/equities/germany",				"actionsvals",	"0",	"0",	"1",	"2",	"actions"},
					{"https://fr.investing.com/equities/belgium",				"actionsvals",	"0",	"0",	"1",	"2",	"actions"},
					{"https://fr.investing.com/equities/switzerland",			"actionsvals",	"0",	"0",	"1",	"2",	"actions"},
					{"https://fr.investing.com/equities/united-kingdom",		"actionsvals",	"0",	"0",	"1",	"2",	"actions"},*/
				};
		String[] SITE = SITES[5];
		try 
		{
			Class.forName("org.mariadb.jdbc.Driver");
			java.sql.Connection sql = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/trading?user=root&password=chiyanoyuuki1512.");
			Statement st = sql.createStatement();
			org.jsoup.Connection c = Jsoup.connect(SITE[0]).ignoreHttpErrors(true);
			
			HashMap<String,Indice> indicestmp = new HashMap<String,Indice>();
			Elements lignes = c.get().select("table").get(Integer.parseInt(SITE[2])).select("tr");
			
			for(Element ligne:lignes)
			{
				Elements colonnes = ligne.select("td");
				List<String> tmp = colonnes.eachText();
				if(tmp.size()>0)
				{
					System.out.println(tmp);
					String pays = colonnes.get(Integer.parseInt(SITE[3])).selectFirst("span").attr("title");
					if(pays.equals(""))pays = "Monde";
					String SQL = "INSERT INTO PAYS VALUES ((SELECT COALESCE(MAX(ID)+1,0) FROM PAYS P2),'"+pays+"')";
					
					try{st.executeQuery(SQL);}catch(SQLException e) {}
					
					String nom;
					if(SITE[6].equals("actions")) nom = colonnes.get(Integer.parseInt(SITE[4])).selectFirst("a").text();
					else nom = tmp.get(Integer.parseInt(SITE[4]));
					
					double val = Double.parseDouble(tmp.get(Integer.parseInt(SITE[5])).replaceAll("\\.", "").replaceAll(",","."));
					indicestmp.put(nom+pays,new Indice(val,nom,pays));
					
					try{st.executeQuery("INSERT INTO "+SITE[6]+" VALUES ((SELECT COALESCE(MAX(ID)+1,0) FROM "+SITE[6]+" I2),(SELECT P.ID FROM PAYS P WHERE P.NOM='"+pays+"'),'"+nom+"')");}catch(SQLException e) {}
				}
			}
		} 
		catch 	(Exception e) 		{e.printStackTrace();} 

	}
}