package v01;

import java.io.File;

import org.newdawn.slick.*;

public class Game extends BasicGame 
{	
    public Game() {super("Robot de Trading Charles");}
    
    private String s = "je suis je suis et je suis";
    private int pos = 0;

    @Override
    public void init(GameContainer container) throws SlickException 
    {	
    	
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException 
    {
    	
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	play();
    }
    
    private void play()
    {    	
    	while(pos<s.length())
    	{
    		if		(playit(3))		pos+=3;
    		else if	(playit(2))		pos+=2;
    		else 	{playit(1);		pos+=1;}
    	}
    }
    
    private boolean playit(int nb)
    {
    	if(pos+nb<=s.length())
    	{
        	String file = "./lettres/"+s.substring(pos,pos+nb).replaceAll(" ", "_")+".wav";
    		if(new File(file).exists())
    		{
    			try 
    			{
    				System.out.println(nb + " ==> " + file);
    				Sound s = new Sound(file);
    				s.play((float)(1.05),1);
    				while(s.playing()) {}
    				s.stop();
    				return true;
    			} catch (Exception e) {System.out.println("Erreur");}
    		}
    	}
		return false;
    }
}