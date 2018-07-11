package sanavesa.model;

public class VersionControlDemo
{
	public static void main(String[] args)
	{
		VersionControl control = VersionControl.getInstance();
		
		MyDouble number = new MyDouble(9.0);
		
		System.err.println("Starts at " + number);
		control.executeCommand(new AddCommand(number));
		control.executeCommand(new AddCommand(number));
		control.executeCommand(new SubtractCommand(number));
		control.executeCommand(new SquareCommand(number));
		System.err.println("Done at " + number);
		
		while(control.undoCount() > 0)
			control.undo();
		
		System.err.println("Undo Ends at " + number);
		
		while(control.redoCount() > 0)
			control.redo();
		
		System.err.println("Redo Ends at " + number);
	}
}

class AddCommand implements ICommand
{
	private MyDouble x;
	
	public MyDouble getX()
	{
		return x;
	}
	
	public AddCommand(MyDouble x)
	{
		this.x = x;
	}

	@Override
	public void execute()
	{
		x.setValue(x.getValue() + 1);
		System.out.println("[Add] " + x);
	}

	@Override
	public void undo()
	{
		x.setValue(x.getValue() - 1);
		System.out.println("[Undo Add] " + x);
	}
}



class SubtractCommand implements ICommand
{
	private MyDouble x;
	
	public MyDouble getX()
	{
		return x;
	}
	
	public SubtractCommand(MyDouble x)
	{
		this.x = x;
	}

	@Override
	public void execute()
	{
		x.setValue(x.getValue() - 1);
		System.out.println("[Subtract] " + x);
	}

	@Override
	public void undo()
	{
		x.setValue(x.getValue() + 1);
		System.out.println("[Undo Subtract] " + x);
	}
}

class SquareCommand implements ICommand
{
	private MyDouble x;
	
	public MyDouble getX()
	{
		return x;
	}
	
	public SquareCommand(MyDouble x)
	{
		this.x = x;
	}

	@Override
	public void execute()
	{
		x.setValue(x.getValue() * x.getValue());
		System.out.println("[Square] " + x);
	}

	@Override
	public void undo()
	{
		x.setValue(Math.sqrt(x.getValue()));
		System.out.println("[Undo Square] " + x);
	}
}

class MyDouble
{
	private double value;
	
	public MyDouble(double value)
	{
		this.value = value;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}
	
	public double getValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return Double.toString(value);
	}
}
