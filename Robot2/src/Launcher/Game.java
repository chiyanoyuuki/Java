package Launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.lwjgl.input.*;
import org.newdawn.slick.*;

public class Game extends BasicGame 
{	
	public static final int TAILLEINDICES = 300;
	public static final int TAILLEFPS = 30;
	public static final int TAILLEGRAPHX = App.width - TAILLEINDICES - 40;
	public static final int TAILLEGRAPHY = 300;
	public static final int TAILLESCROLL = 15;
	
	public static Indice selected;
	private Graph graph;
	private int x, y;
	private String selectedCategory;
	private int scroll, nbtype, nbind;
	private double quotientScroll;
	
    public Game() {super("Robot de Trading Charles");}

    @Override
    public void init(GameContainer container) throws SlickException 
    {	
    	selectedCategory = App.indicesNames.get(0);
    	selected = App.indices.get(selectedCategory).get(0);
    	quotientScroll=1.0*(App.height-TAILLEFPS)/App.indices.get(selectedCategory).size();
    	graph = new Graph(App.indices.get(selectedCategory).get(0));
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
    	Iterator<String> it = App.indices.keySet().iterator();
    	while(it.hasNext())
    	{
    		String s = it.next();
    		int tailleCategory = (App.width-TAILLEINDICES)/App.indices.size();
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
    		g.drawString(s,TAILLEINDICES + 20 + tailleCategory*cpt++,10);
    		g.setColor(Color.white);
    	}
    	
    	cpt = 2;
    	ArrayList<Indice> indices = App.indices.get(selectedCategory);
    	for(Indice i:indices)
    	{
    		if(x<TAILLEINDICES-TAILLESCROLL&&y>cpt*20-(scroll*20)&&y<(cpt+1)*20-(scroll*20)&&i.isinit())
        	{
        		g.setColor(Color.orange);
        		g.drawRect(0,cpt*20-(scroll*20),TAILLEINDICES-TAILLESCROLL,20);
        		g.setColor(Color.white);
        	}
        	if(i==selected)
        	{
        		g.setColor(Color.red);
        		g.drawRect(0,cpt*20-(scroll*20),TAILLEINDICES-TAILLESCROLL,20);
        		g.setColor(Color.white);
        	}
        	g.setColor(i.getColor());
        	g.drawString(i.mvmt()+i.nom(), 20, cpt++*20-(scroll*20));
    	}
    	g.setColor(Color.white);
    	g.setColor(Color.darkGray);
    	g.fillRect(0, 0, TAILLEINDICES-2, TAILLEFPS);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	x=Mouse.getX();
    	y=App.height-Mouse.getY();
    	Input c = container.getInput();    	

    	ArrayList<Indice> indices = App.indices.get(selectedCategory);
    	
    	if(c.isMousePressed(0))
    	{
    		if(x>TAILLEINDICES&&y<TAILLEFPS)
    		{
    			int tmp = (x-TAILLEINDICES)/((App.width-TAILLEINDICES)/App.indices.size());
    			String cat = getNom(tmp);
    			if(App.indices.get(cat).get(0).isinit())
    			{
    				selectedCategory = cat;
        			selected=App.indices.get(selectedCategory).get(0);
        			scroll=0;
        	    	quotientScroll=1.0*(App.height-TAILLEFPS)/App.indices.get(selectedCategory).size();
        			graph = new Graph(selected);
    			}
    		}
    		
    		if(x<TAILLEINDICES-TAILLESCROLL&&y/20-2+scroll>-1&&y/20-2+scroll<indices.size())
    		{    			
    			Indice i = indices.get(y/20-2+scroll);
    			if(i.isinit())
    			{
    				int tmp = y/20-2+scroll;
        			selected=indices.get(tmp);
        			graph = new Graph(selected);
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
    	
    	
    	
        if(nbind<indices.size())indices.get(nbind++).update();
        else {nbind=0;}
        
        Collections.sort(indices);
    	
    	/*if(System.currentTimeMillis()-timer2>2000)
    	{
    		indices.get(selected).update();
    		timer2 = System.currentTimeMillis();
    	}*/
    }
    
    private String getNom(int i)
    {
    	Iterator<String> it = App.indices.keySet().iterator();
		String s = it.next();
		for(int p=0;p<i;p++) {s = it.next();}
		return s;
    }
}