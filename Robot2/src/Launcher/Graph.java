package Launcher;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Graph 
{
	private Indice indice;
	private double quotient,quotientx;
	private ArrayList<Val> vals;
	private Val min, max;
	private double average;
	
	public Graph(Indice indice)
	{
		this.indice = indice;
		vals = indice.vals();
	}
	
	public void render(GameContainer container, Graphics g)
    {    	
    	g.setColor(Color.gray);
    	g.fillRect(Game.TAILLEINDICES+20, 40, Game.TAILLEGRAPHX, Game.TAILLEGRAPHY+20);
    	g.setColor(Color.white);
    	g.drawRect(Game.TAILLEINDICES+20, 40, Game.TAILLEGRAPHX, Game.TAILLEGRAPHY+20);
    	
    	float cpt = 0;
    	float lastx=0, lasty=0;
    	quotient = indice.quotient();
		quotientx = 1.0*(Game.TAILLEGRAPHX-10)/vals.size()*1.0;
    	max = indice.max();
    	min = indice.min();
		average = indice.average();
    	
    	for(Val v : vals)
    	{
    		float x = (float) (Game.TAILLEINDICES+25+quotientx*cpt++);
    		float y = (float) (50+Game.TAILLEGRAPHY-((v.val()-min.val())*quotient));
    		if(lasty!=0)
    		{
    			if(lasty>y)g.setColor(Color.green);
    			else g.setColor(Color.red);
    			g.drawLine(lastx, lasty, x, y);
    		}
    		lastx = x;
    		lasty = y;
    		
    		if(indice.depart()==v)
    		{
        		g.setColor(Color.blue);
        		g.drawRect(x, 40, 1, Game.TAILLEGRAPHY+20);
    		}
    	}
    	
    	
    	ArrayList<MoyenneMobile> mms = indice.mms();
    	for(MoyenneMobile mm:mms)
    	{
        	g.setColor(mm.color());
    		ArrayList<Double> mob = mm.vals();
    		float lastx2 = 0;
    		float lasty2 = 0;
    		for(int i=0;i<mob.size();i++)
    		{
    			double d = mob.get(i);
    			float x2 = (float) (Game.TAILLEINDICES+25+quotientx*i);
        		float y2 = (float) (50+Game.TAILLEGRAPHY-((d-min.val())*quotient));
        		if(d>0)
        		{
        			if(lasty2!=0)
            		{
            			g.drawLine(lastx2, lasty2, x2, y2);
            		}
            		lastx2 = x2;
            		lasty2 = y2;
        		}
    		}
    	}
		
    	g.setColor(Color.green);
    	float y = (float) (50+Game.TAILLEGRAPHY-((average-min.val())*quotient));
    	g.drawLine(Game.TAILLEINDICES+25, y, Game.TAILLEINDICES+25+Game.TAILLEGRAPHX-10, y);
    	g.setColor(Color.white);
    	//g.drawString(indice.nom(),Game.TAILLEINDICES+Game.TAILLEGRAPHX-150, Game.TAILLEFPS+10);
    	g.drawString(indice.minDate(), Game.TAILLEINDICES+25, Game.TAILLEGRAPHY+60);
    	g.drawString(indice.maxDate(), Game.TAILLEINDICES+Game.TAILLEGRAPHX-180, Game.TAILLEGRAPHY+60);
    	g.drawString(""+max.val(), 					Game.TAILLEINDICES+25, Game.TAILLEFPS+10);
    	g.drawString(String.format("%.2f",average), Game.TAILLEINDICES+25, y-20);
    	g.drawString(""+min.val(), 					Game.TAILLEINDICES+25, Game.TAILLEFPS+10+Game.TAILLEGRAPHY);
    	g.drawString("Valeurs : "+vals.size(), Game.TAILLEINDICES+25, Game.TAILLEGRAPHY+120);
    }
}
