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
	private char mdp = 'C';
	private String[][] SITES;
	private ArrayList<HashMap<String,Indice>> indices;
	private ArrayList<org.jsoup.Connection> conn;
	private java.sql.Connection sql;
	private Statement st;
	
	private int j,jmore;
	
	public Launcher()
	{
		if(new File("./").getAbsolutePath().contains("\\Java\\RecupIndices\\"))this.mdp='c';
		show("Programme lancé");
		init();
		recupIndices();
		loopVals();
	}
	
	private void loopVals()
	{
		int nb = 0;
		int i=0;
		show("Debut de la récupération");
		while(true)
		{
			nb++;
			for(int x=0;x<SITES.length;x++)
			{
				String[] SITE = SITES[x];
				try 
				{
					Thread.sleep(100);
					org.jsoup.Connection c = conn.get(x);
					Elements lignes = c.get().select("table").get(Integer.parseInt(SITE[2])).select("tr");
					
					for(Element ligne:lignes)
					{
						Elements colonnes = ligne.select("td");
						List<String> tmp = colonnes.eachText();
						if(tmp.size()>0)
						{
							String pays = colonnes.get(Integer.parseInt(SITE[3])).selectFirst("span").attr("title");
							String nom;
							if(pays.equals(""))pays = "Monde";
							if(SITE[1].equals("actions")) nom = colonnes.get(Integer.parseInt(SITE[4])).selectFirst("a").text().replaceAll("'","");
							else nom = tmp.get(Integer.parseInt(SITE[4]));
							double val = Double.parseDouble(tmp.get(Integer.parseInt(SITE[5])).replaceAll(",",""));
							indices.get(x).get(nom+pays).addVal(val,st,SITE[1]);
						}
					}
				} 
				catch 	(Exception e1) 		{show("ERREUR " + SITE[1] + " RECUPERATION VALEURS");}
			}
			if(nb%1000==0)
			{
				show("Nombre : " + nb);
				verifDate();
			}
		}
	}
	
	private void verifDate()
	{
		Calendar cal = Calendar.getInstance();
		int j = cal.get(Calendar.DAY_OF_MONTH);
		if(j==jmore)
		{
			BDD tmp = new BDD(SITES,st);
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1);
			jmore = cal.get(Calendar.DAY_OF_MONTH);	
		}
		
	}
	
	private void recupIndices()
	{
		for(String[] SITE : SITES)
		{
			try 
			{
				show("Connection " + SITE[1]);
				Thread.sleep(1000);
				org.jsoup.Connection c = Jsoup.connect(SITE[0]).ignoreHttpErrors(true);
				conn.add(c);
				
				HashMap<String,Indice> indicestmp = new HashMap<String,Indice>();
				Elements lignes = c.get().select("table").get(Integer.parseInt(SITE[2])).select("tr");
				
				for(Element ligne:lignes)
				{
					Elements colonnes = ligne.select("td");
					List<String> tmp = colonnes.eachText();
					if(tmp.size()>0)
					{
						String pays = colonnes.get(Integer.parseInt(SITE[3])).selectFirst("span").attr("title");
						if(pays.equals(""))pays = "Monde";
						String SQL = "INSERT INTO refs_PAYS VALUES ((SELECT COALESCE(MAX(ID)+1,0) FROM refs_PAYS P2),'"+pays+"')";
						
						try{st.executeQuery(SQL);}catch(SQLException e) {}
						
						String nom;
						if(SITE[1].equals("actions")) nom = colonnes.get(Integer.parseInt(SITE[4])).selectFirst("a").text().replaceAll("'","");
						else nom = tmp.get(Integer.parseInt(SITE[4]));
						
						double val = Double.parseDouble(tmp.get(Integer.parseInt(SITE[5])).replaceAll(",",""));
						indicestmp.put(nom+pays,new Indice(val,nom,pays));
						
						try{st.executeQuery("INSERT INTO liste_"+SITE[1]+" VALUES ((SELECT COALESCE(MAX(ID)+1,0) FROM liste_"+SITE[1]+" I2),(SELECT P.ID FROM refs_PAYS P WHERE P.NOM='"+pays+"'),'"+nom+"')");}catch(SQLException e) {}
					}
				}
				indices.add(indicestmp);
			} 
			catch 	(Exception e) 		{show("ERREUR : " + SITE[0] + " RECUPERATION INDICES");} 
		}
	}
	
	private void init()
	{
		SITES = new String[][] 
		{
			//0SITE																			1NOM BDD	2TABLE,	3PAYS,	4NOM,	5VAL
			{"https://www.investing.com/indices/indices-cfds",								"cfd",		"1",	"0",	"1",	"2"},
			{"https://www.investing.com/indices/indices-futures",							"futures",	"0",	"0",	"1",	"3"},
			{"https://www.investing.com/indices/germany-30-components",						"actions",	"1",	"0",	"1",	"2"},
			{"https://www.investing.com/indices/investing.com-united-states-30-components",	"actions",	"0",	"0",	"1",	"2"},
			{"https://www.investing.com/indices/nq-100-components",							"actions",	"1",	"0",	"1",	"2"},
			{"https://www.investing.com/commodities/real-time-futures",						"matieres",	"0",	"0",	"1",	"3"}
		};
		
		try 
		{
			Class.forName("org.mariadb.jdbc.Driver");
			sql = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/trading?user=root&password="+mdp+"hiyanoyuuki1512.");
			st = sql.createStatement();
			BDD tmp = new BDD(SITES,st);
		} 
		catch (SQLException e) 				{show("Erreur lors de la connection à la base de données");} 
		catch (ClassNotFoundException e) 	{show("Erreur driver BDD");}
		
		indices = new ArrayList<HashMap<String,Indice>>();
		conn = new ArrayList<org.jsoup.Connection>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1);
		jmore = cal.get(Calendar.DAY_OF_MONTH);	
	}
	
	private void show(String s)
	{
		Timestamp t = new Timestamp(System.currentTimeMillis());
		System.out.println(String.format("%-30s", t) + " | " + s);
	}
	
	public static void main(String[] args) {Launcher l = new Launcher();}
}