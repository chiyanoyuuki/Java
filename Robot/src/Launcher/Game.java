package Launcher;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.input.*;
import org.newdawn.slick.*;

public class Game extends BasicGame 
{	
	public static final int TAILLEINDICES = 200;
	public static final int TAILLEFPS = 30;
	public static final int TAILLEGRAPHX = App.width - TAILLEINDICES - 40;
	public static final int TAILLEGRAPHY = 300;
	public static final int TAILLESCROLL = 15;
	
	public static int selected;
	
	private HashMap<String,ArrayList<Indice>> indices;
	private Graph graph;
	private int i, nb;
	private long timer, timer2;
	private boolean init = true;
	private int x, y;
	private String selectedCategory;
	private String initEnCours;
	private int scroll;
	private double quotientScroll;
	
    public Game() {super("Robot de Trading Charles");}

    @Override
    public void init(GameContainer container) throws SlickException 
    {	
    	indices = new HashMap<String,ArrayList<Indice>>();
    	try
    	{
    		init("cfd");
    		init("futures");
    		init("actions");
    		init("matieres");
    	}
    	catch(Exception e) {error("Init");}
    	String first = getNom(0);
    	selectedCategory = first;
    	initEnCours = first;
    	selected = 0;
    	quotientScroll=1.0*(App.height-TAILLEFPS)/this.indices.get(selectedCategory).size();
    	System.out.println(quotientScroll);
    	graph = new Graph(indices.get(selectedCategory).get(0));
    	timer = System.currentTimeMillis();
    	timer2 = System.currentTimeMillis();
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException 
    {
    	g.setColor(Color.darkGray);
    	g.fillRect(0, 0, App.width, App.height);
    	g.setColor(Color.gray);
    	g.fillRect(TAILLEINDICES-TAILLESCROLL, TAILLEFPS, TAILLESCROLL, App.height);
    	g.setColor(Color.white);
    	g.fillRect(TAILLEINDICES-TAILLESCROLL, (float)(TAILLEFPS+scroll*quotientScroll*1.0), TAILLESCROLL, (float)(23*quotientScroll));
    	g.setLineWidth(2);
    	g.drawLine(0, TAILLEFPS, App.width, TAILLEFPS);
    	g.drawLine(TAILLEINDICES, 0, TAILLEINDICES, App.height);
    	
    	graph.render(container, g);
    	
    	int cpt = 0;
    	Iterator<String> it = indices.keySet().iterator();
    	while(it.hasNext())
    	{
    		String s = it.next();
    		int tailleCategory = (App.width-TAILLEINDICES)/this.indices.size();
    		if(x>TAILLEINDICES+cpt*tailleCategory&&x<TAILLEINDICES+(cpt+1)*tailleCategory&&y<TAILLEFPS)
    		{
    			g.setColor(Color.orange);
    			g.drawRect(TAILLEINDICES + tailleCategory*cpt, 0, tailleCategory, TAILLEFPS);
    			g.setColor(Color.white);
    		}
    		if(s.contentEquals(selectedCategory))
    		{
    			g.setColor(Color.red);
    			g.drawRect(TAILLEINDICES + tailleCategory*cpt, 0, tailleCategory, TAILLEFPS);
    			g.setColor(Color.white);
    		}
    		if(!this.indices.get(s).get(0).isinit())g.setColor(Color.gray);
    		g.drawString(s,TAILLEINDICES + 20 + tailleCategory*cpt++,10);
    		g.setColor(Color.white);
    	}
    	
    	cpt = 2;
    	ArrayList<Indice> indices = this.indices.get(selectedCategory);
    	for(Indice i:indices)
    	{
    		if(x<TAILLEINDICES-TAILLESCROLL&&y>cpt*20-(scroll*20)&&y<(cpt+1)*20-(scroll*20)&&i.isinit())
        	{
        		g.setColor(Color.orange);
        		g.drawRect(0,cpt*20-(scroll*20),TAILLEINDICES-TAILLESCROLL,20);
        		g.setColor(Color.white);
        	}
        	if(cpt-2==selected)
        	{
        		g.setColor(Color.red);
        		g.drawRect(0,cpt*20-(scroll*20),TAILLEINDICES-TAILLESCROLL,20);
        		g.setColor(Color.white);
        	}
        	if(i.isinit())g.drawString(i.nom(), 20, cpt++*20-(scroll*20));
    	}
    	g.setColor(Color.white);
    	if(init)g.drawString("Initialisation en cours : "+initEnCours,TAILLEINDICES+20,App.height-40);
    	
    	g.setColor(Color.darkGray);
    	g.fillRect(0, 0, TAILLEINDICES-2, TAILLEFPS);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	x=Mouse.getX();
    	y=App.height-Mouse.getY();
    	Input c = container.getInput();
    	
    	if(init)
    	{
    		if(nb<this.indices.size())
    		{
    			ArrayList<Indice> indices = this.indices.get(getNom(nb));
    			
    			if(i<indices.size()) 
        		{
        			if((initEnCours.equals("actions")&&System.currentTimeMillis()-timer>500)||(!initEnCours.equals("actions")&&System.currentTimeMillis()-timer>200))
        			{
        				Indice indice = indices.get(i++);
        				initEnCours=indice.type();
        				indice.start();
        				timer=System.currentTimeMillis();
        			}
        		}
    			else
    			{
    				nb++;i=0;
    			}
    		}
    		else {show("Init Over");init=false;}
    	}
    	

    	ArrayList<Indice> indices = this.indices.get(selectedCategory);
    	
    	if(c.isMousePressed(0))
    	{
    		if(x>TAILLEINDICES&&y<TAILLEFPS)
    		{
    			int tmp = (x-TAILLEINDICES)/((App.width-TAILLEINDICES)/this.indices.size());
    			String cat = getNom(tmp);
    			if(this.indices.get(cat).get(0).isinit())
    			{
    				selectedCategory = cat;
        			selected=0;
        			scroll=0;
        	    	quotientScroll=1.0*(App.height-TAILLEFPS)/this.indices.get(selectedCategory).size();
        			Indice indice = this.indices.get(selectedCategory).get(selected);
        			graph = new Graph(indice);
    			}
    		}
    		
    		if(x<TAILLEINDICES-TAILLESCROLL&&y/20-2+scroll>-1&&y/20-2+scroll<indices.size())
    		{    			
    			Indice i = indices.get(y/20-2+scroll);
    			if(i.isinit())
    			{
    				int tmp = y/20-2+scroll;
        			selected=tmp;
        			graph = new Graph(indices.get(selected));
    			}
    		}
    	}
    	
    	int scroll = Mouse.getDWheel();    	
    	if(scroll<0&&this.scroll<indices.size()-23)
    	{
    		if(x<TAILLEINDICES&&y>TAILLEFPS)
    		{
        		this.scroll++;
    		}
    	}
    	if(scroll>0&&this.scroll>0)
    	{
    		if(x<TAILLEINDICES&&y>TAILLEFPS)
    		{
        		this.scroll--;
    		}
    	}
    	
    	/*if(System.currentTimeMillis()-timer2>2000)
    	{
    		indices.get(selected).update();
    		timer2 = System.currentTimeMillis();
    	}*/
    }
    
    private String getNom(int i)
    {
    	Iterator<String> it = this.indices.keySet().iterator();
		String s = it.next();
		for(int p=0;p<i;p++) {s = it.next();}
		return s;
    }
    
    private void init(String type) throws SQLException, IOException
    {
    	ResultSet rs = App.SQL.executeQuery("SELECT * FROM LISTE_"+type);
    	ArrayList<Indice> indices = new ArrayList<Indice>();
    	while(rs.next())
    	{
    		indices.add(new Indice(type,rs.getInt(1),rs.getString(3)));
    	}
    	this.indices.put(type, indices);
    }
    
    private void show	(String message){System.out.println("INFO   : " + message);}
    private void error	(String error)	{System.out.println("ERREUR : " + error);}
}