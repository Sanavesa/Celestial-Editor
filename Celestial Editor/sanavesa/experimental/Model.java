package sanavesa.experimental;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Model
{
	private final IntegerProperty result = new SimpleIntegerProperty(0);
	
	public void add(int a, int b)
	{
		result.set(a + b);
	}
	
	public int getResult()
	{
		return result.get();
	}
	
	public IntegerProperty resultProperty()
	{
		return result;
	}
}
