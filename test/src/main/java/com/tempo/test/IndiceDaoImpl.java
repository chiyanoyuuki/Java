package com.tempo.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;

@Repository
public class IndiceDaoImpl 
{
	private JdbcTemplate jdbcTemplate;
	 
	@Autowired
	public void setDataSource(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource);}
	
	@Autowired
	private Environment env;    
    
	public List<Indice> getIndices(String type)
	{
		String SQL = "SELECT I.ID, P.ID AS PAYS, I.NOM FROM liste_"+type+" I, refs_PAYS P WHERE I.PAYS = P.ID ORDER BY I.NOM";
		List<Indice> indices = jdbcTemplate.query(SQL,new BeanPropertyRowMapper<Indice>(Indice.class));   
		System.out.println("Liste de "+type+" récupérée");
		return indices;
	}
	public List<IndiceVal> getIndicesVals(int ID, String TIME,String type)
	{
		List<IndiceVal> indices = new ArrayList<IndiceVal>();
		if(!TIME.equals("Today"))
		{
			try
			{
				String files = "*.csv";
				if(TIME.equals("Year")) 
				{
					int curYear = Calendar.getInstance().get(Calendar.YEAR);
					files = curYear+"-*.csv";
				}
				else if(TIME.equals("Month"))
				{
					Calendar c = Calendar.getInstance();
					int curYear = c.get(Calendar.YEAR);
					int curMonth = c.get(Calendar.MONTH)+1;
					files = curYear+"-"+String.format("%02d", curMonth)+"-*.csv";
				}
				String commande = "sed -n \"/^"+ID+";.*/"+getParam(ID, files,type)+"\" "+files;
		        indices.addAll(getIndicesComm(commande,type));
			} catch (IOException | ClassNotFoundException | SQLException | InterruptedException e) 
			{System.out.println("PROBLEME LORS DE LA RECUPERATION DANS LES FICHIERS");}
		}
		int modulo = getModulo(ID,type);
		String SQL = "SELECT LIGNES.VAL, LIGNES.DATE FROM(SELECT ROW_NUMBER() OVER(ORDER BY DATE) AS NUM, VAL, DATE FROM vals_"+type+" WHERE ID="+ID+") LIGNES WHERE MOD(LIGNES.NUM,"+modulo+")=0";
		System.out.println(SQL);
		indices.addAll(jdbcTemplate.query(SQL,new BeanPropertyRowMapper<IndiceVal>(IndiceVal.class)));   
		return indices;
	}
	public List<IndiceVal> getIndicesNewVals(int ID, String DATE,String type)
	{
		String SQL = "SELECT VAL, DATE FROM vals_"+type+" WHERE ID="+ID+" AND DATE>'"+DATE+"' ORDER BY DATE";
		System.out.println(SQL);
		List<IndiceVal> indices = jdbcTemplate.query(SQL,new BeanPropertyRowMapper<IndiceVal>(IndiceVal.class));   
		return indices;
	}
	public List<String> getIndicesTotal(int ID,String type) 
	{
		List<String> totaux = new ArrayList<String>();
		
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH)+1;
		try
		{
			String allTime = getCount(ID,"*.csv",type);
			String year = getCount(ID,curYear+"-*.csv",type);
			String month = getCount(ID,curYear+"-"+String.format("%02d", curMonth)+"-*.csv",type);
			totaux.add(allTime);totaux.add(year);totaux.add(month);
		} catch (IOException | ClassNotFoundException | SQLException | InterruptedException e) 
		{System.out.println("PROBLEME LORS DE LA RECUPERATION DANS LES FICHIERS");}
		
		String SQL = "SELECT COUNT(*) AS NB FROM vals_"+type+" WHERE ID="+ID;
		List<Map<String,Object>> result = jdbcTemplate.queryForList(SQL);
		String day = ""+result.get(0).get("NB");
		totaux.add(day);
		
		return totaux;
	}
	public String getPays(String type) 
	{
		String SQL = "SELECT * FROM REFS_PAYS WHERE ID IN (SELECT DISTINCT PAYS FROM LISTE_"+type+")";
		List<Map<String,Object>> result = jdbcTemplate.queryForList(SQL);
		return mapToString(result);
	}
	
	private String mapToString(List<Map<String,Object>> result)
	{
		List<String> lignes = new ArrayList<String>();
		for(Map<String,Object> ligne:result)
		{
			Iterator<String> it = ligne.keySet().iterator();
			List<String> datas = new ArrayList<String>();
			while(it.hasNext())
			{
				String data = ligne.get(it.next()).toString();
				if(!data.matches("[0-9]+"))data = "\""+data+"\"";
				datas.add(data);
			}
			lignes.add(datas.toString());
		}
		return lignes.toString();
	}
	private String getCount(int ID, String files,String type) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		String commande = "sed -n \"/^"+ID+";.*/p\" "+files+" | wc -l";
		BufferedReader reader = run(commande,type);
		String nbLines = reader.readLine();
		return nbLines;
	}
	private int getModulo(int ID, String type)
	{
		String SQL = "SELECT COUNT(*) AS NB FROM vals_"+type+" WHERE ID="+ID;
		List<Map<String,Object>> result = jdbcTemplate.queryForList(SQL);
		int nb = Integer.parseInt(""+result.get(0).get("NB"));
		
		int modulo = 1;
        int tmp = nb;
		while(tmp>2000)
		{
			modulo++;
			tmp = nb/modulo;
		}
		
		return modulo;
	}
	private String getParam(int ID, String files,String type) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		String commande = "sed -n \"/^"+ID+";.*/p\" "+files+" | wc -l";
		BufferedReader reader = run(commande,type);
		int nbLines = Integer.parseInt(reader.readLine());
		int cpt = 1;
		String param = "{p;";
		int tmp = nbLines;
		while(tmp>2000)
		{
			cpt++;
			param += "n;";
			tmp = nbLines/cpt;
		}
		
		param +="}";
		reader.close();
		return param;
	}
	private List<IndiceVal> getIndicesComm(String commande, String type) throws IOException, ClassNotFoundException, SQLException, InterruptedException
	{
        List<IndiceVal> indices = new ArrayList<IndiceVal>();
		BufferedReader reader = run(commande,type);
        String line;
        while ((line = reader.readLine()) != null) 
        {
        	String[] tmp = line.split(";");
        	indices.add(new IndiceVal(Double.parseDouble(tmp[1]),tmp[2]));
        }
		reader.close();
		return indices;
	}
	private BufferedReader run(String s,String type) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		String path = env.getProperty("app.path")+"cache/"+type+"/";
		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",s);
		pb.directory(new File(path));
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader;
	}
	
}
