package sanavesa.util;

import java.util.ArrayList;
import java.util.Iterator;

public class Observable<T>
{
	private ArrayList<Observer<T>> observers = new ArrayList<>();
	private ArrayList<Observer<T>> deletedObservers = new ArrayList<>();
	private T value;
	
	public Observable(T initialValue)
	{
		value = initialValue;
	}
	
	public void addObserver(Observer<T> observer)
	{
		observers.add(observer);
	}
	
	public void deleteObserver(Observer<T> observer)
	{
		deletedObservers.add(observer);
	}
	
	public int countObservers()
	{
		return observers.size();
	}
	
	public void deleteObservers()
	{
		deletedObservers.clear();
		deletedObservers.addAll(observers);
	}
	
	public T get()
	{
		return value;
	}
	
	public void set(T newValue)
	{
		T oldValue = value;
		value = newValue;
		
		// Notify observers
		for(Iterator<Observer<T>> iterator = observers.iterator(); iterator.hasNext();)
		{
			Observer<T> item = iterator.next();
			if(deletedObservers.contains(item))
			{
				deletedObservers.remove(item);
				iterator.remove();
			}
			else
			{
				item.onChanged(this, oldValue, newValue);
			}
		}
	}
	
	@Override
	public String toString()
	{
		if(value == null)
			return "null";
		return value.toString();
	}
}