package sanavesa.experimental;

import sanavesa.util.Observable;
import sanavesa.util.Observer;

public class ObserverDemo
{
	public static void main(String[] args)
	{
		long time = System.nanoTime();
		
		Observable<Integer> name = new Observable<>(5);
		Observer<Integer> watchName = new Observer<Integer>()
		{
			@Override
			public void onChanged(Observable<Integer> observable, Integer oldValue, Integer newValue)
			{
				System.out.println("Observer #1 says " + newValue);
			}
		};
		Observer<Integer> fucker = new Observer<Integer>()
		{
			@Override
			public void onChanged(Observable<Integer> observable, Integer oldValue, Integer newValue)
			{
				System.out.println("Observer #2 says " + newValue);
				
				if(newValue == 70)
					observable.deleteObserver(this);
			}
		};
		
		name.addObserver(watchName);
		name.addObserver(fucker);
		name.set(50);
		name.set(60);
		name.set(70);
		name.set(100);
		
		System.out.println("Took " + (System.nanoTime() - time));
	}
}