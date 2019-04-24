package Launcher;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class test {
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		ArrayList<String> a = new ArrayList<String>();
		
		a.add("c");
		a.add("e");
		a.add(1,"d");
		a.add(0,"b");
		a.remove(0);
		
		System.out.println(a);
	}
}
