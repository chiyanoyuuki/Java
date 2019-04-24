package Launcher;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Graph 
{
	private Indice indice;
	
	public Graph(Indice indice)
	{
		this.indice = indice;
	}
	
	public void render(GameContainer container, Graphics g)
    {
    	ArrayList<Val> vals = indice.vals();
    	if(vals==null)return;
    	
    	g.setColor(Color.gray);
    	g.fillRect(Game.TAILLEINDICES+20, 40, Game.TAILLEGRAPHX, Game.TAILLEGRAPHY+20);
    	g.setColor(Color.white);
    	g.drawRect(Game.TAILLEINDICES+20, 40, Game.TAILLEGRAPHX, Game.TAILLEGRAPHY+20);
    	
    	Val max = indice.max();
    	Val min = indice.min();
    	double quotient = indice.quotient();
    	
    	int cpt = 0;
    	float lastx=0, lasty=0;
    	for(Val v : vals)
    	{
    		float x = Game.TAILLEINDICES+25+5*cpt++;
    		float y = (float) (50+Game.TAILLEGRAPHY-((v.val()-min.val())*quotient));
        	g.setColor(Color.red);
    		g.drawOval(x, y, 4, 4);
    		g.setColor(Color.white);
    		if(lasty!=0)g.drawLine(lastx+2, lasty+2, x+2, y+2);
    		lastx = x;
    		lasty = y;
    	}
    }
}
