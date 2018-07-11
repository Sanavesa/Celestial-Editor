package sanavesa.experimental;

import java.util.Observable;
import java.util.Observer;

public class ObserverDemo2
{
	public static void main(String[] args)
	{
		long time = System.nanoTime();
		Custom observable = new Custom();
		
		Observer o = new Observer()
		{
			@Override
			public void update(Observable o, Object arg)
			{
				int c = (int) arg;
				System.out.println("Observer #1 says " + c);
				
			}
		};
		
		Observer o2 = new Observer()
		{
			@Override
			public void update(Observable o, Object arg)
			{
				int c = (int) arg;
				System.out.println("Observer #2 says " + c);
				
				if(c == 70)
					o.deleteObserver(this);
				
			}
		};
		
		observable.addObserver(o);
		observable.addObserver(o2);
		
		observable.setValue(50);
		observable.setValue(60);
		observable.setValue(70);
		observable.setValue(100);
		
		System.out.println("Took " + (System.nanoTime() - time));
	}
}

class Custom extends Observable
{
	private int value = 0;

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
		setChanged();
		notifyObservers(value);
		clearChanged();
	}
}