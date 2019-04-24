package v01;

import java.io.*;
import java.sql.*;

public class Launcher 
{
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
	{
		Class.forName("org.mariadb.jdbc.Driver");
		java.sql.Connection connection = DriverManager.getConnection("jdbc:mariadb://212.227.203.214:3306/trading?user=ADMIN&password=Chiyanoyuuki1512.");
		Statement st = connection.createStatement();
		
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String d = t.toString();
		int i = Integer.parseInt(d.substring(8,10));
		System.out.println("DAY : " + i);
		if(i>1&&i<26)
		{
			d = d.substring(0,8)+String.format("%02d", i-1)+" 00:00:00";
			String d2 = d.substring(0,8)+String.format("%02d", i)+" 00:00:00";
			System.out.println(d + " <==> " + d2);
			
			ResultSet rs = st.executeQuery("SELECT V.ID, V.VAL AS VALEUR, V.DATE FROM INDICES I, VALEURS_INDICES V WHERE I.ID=V.ID AND DATE >= '"+d+"' AND DATE < '"+d2+"' ORDER BY V.ID, DATE");
			
			File tmpDir = new File("./cache/"+d.substring(0,10)+".csv");
			if(tmpDir.exists()) {System.out.println("LE FICHIER EXISTE DEJA");System.exit(0);}
			FileWriter fw = new FileWriter(tmpDir);
			
			ResultSetMetaData meta = rs.getMetaData();
			for(int x=1;x<meta.getColumnCount()+1;x++)
			{
				fw.write(meta.getColumnLabel(x)+";");
			}
			fw.write("\r\n");
			
			int cpt = 0;
			while(rs.next())
			{
				cpt++;
				for(int x=1;x<meta.getColumnCount()+1;x++)
				{
					fw.write(rs.getString(x)+";");
				}
				fw.write("\r\n");
				if(cpt%10000==0)System.out.println(cpt);
			}
			
			fw.close();
			System.out.println("WRITING END");
			
			rs = st.executeQuery("DELETE FROM VALEURS_INDICES WHERE DATE >= '"+d+"' AND DATE < '"+d2+"'");
		}
		else
		{
			System.out.println("ERREUR : MANUELLEMENT AUJOURD'HUI");
		}
		
	}
}
