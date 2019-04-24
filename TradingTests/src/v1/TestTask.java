package v1;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class TestTask extends TimerTask
{
	private int i;
	
	public TestTask(int i)
	{
		this.i = i;
	}
	
	@Override
	public void run() 
	{
		System.out.println(new Date() + " ["+i+"] Execution de ma tache");
	}
}
