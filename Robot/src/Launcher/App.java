package Launcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.newdawn.slick.*;

public class App 
{
	public static String path = "C:/Users/ASC Arma/Desktop/Trading/Java/";
	public static int nbVals = 100;
	public static Statement SQL;
	public final static int width = 800;
	public final static int height = 500;
	
	public App() throws SlickException
	{		
		try 
		{
			Class.forName("org.mariadb.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mariadb://212.227.203.214/trading?user=ADMIN&password=Chiyanoyuuki1512.");
			SQL = connection.createStatement();
		} catch (SQLException | ClassNotFoundException e1) {e1.printStackTrace();}
		
		AppGameContainer app = new AppGameContainer(new Game(), width, height, false);
		app.setTargetFrameRate(60);
    	//app.setShowFPS(false);
    	app.start();
	}
	
	 public static void main(String[] args) throws SlickException {App app = new App();}
}
