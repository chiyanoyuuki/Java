package Launcher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.*;

public class App 
{
	public static String path 		= "C:/Users/ASC Arma/Desktop/Trading/Java/";
	public static int nbVals 		= 5000;
	public final static int width 	= 900;
	public final static int height 	= 500;
	
	public static Statement SQL;
	public static HashMap<String,ArrayList<Indice>> indices;
	public static ArrayList<String> indicesNames;
	
	private String method;
	
	public App()
	{		
		method = "initBDD";
		initBDD();
		method = "initIndices";
		initIndices();
		method = "initApp";
		initApp();
	}
	
	private void initBDD()
	{
		start();
		try 
		{
			Class.forName("org.mariadb.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mariadb://212.227.203.214/trading?user=ADMIN&password=Chiyanoyuuki1512.");
			SQL = connection.createStatement();
		} catch (Exception e) {error();}
		end();
	}
	
	private void initIndices()
	{
		start();
		indices = new HashMap<String,ArrayList<Indice>>();
		indicesNames = new ArrayList<String>();
    	try
    	{
    		init("cfd");
    		init("futures");
    		//init("actions");
    		//init("matieres");
    	}
    	catch(Exception e) {error();}
		end();
	}
	
	private void initApp()
	{
		start();
		try
    	{
			AppGameContainer app = new AppGameContainer(new Game(), width, height, false);
			app.setTargetFrameRate(60);
	    	app.start();
    	}
		catch(Exception e) {error();}
		end();
	}
	
	private void init(String type) throws SQLException, IOException
    {
		indicesNames.add(type);
    	ResultSet rs = App.SQL.executeQuery("SELECT * FROM LISTE_"+type);
    	ArrayList<Indice> tmpindices = new ArrayList<Indice>();
    	while(rs.next())
    	{
    		Indice indice = new Indice(type,rs.getInt(1),rs.getString(3));
    		indice.start();
    		if(indice.vals().size()>nbVals/10)
    			tmpindices.add(indice);
    	}
    	indices.put(type, tmpindices);
    }
	
	private void start	()	{System.out.println("START  : " + method);}
	private void end	()	{System.out.println("END    : " + method);}
	private void error	()	{System.out.println("ERREUR : " + method);}
	public static void main(String[] args) throws SlickException {@SuppressWarnings("unused")App app = new App();}
}
