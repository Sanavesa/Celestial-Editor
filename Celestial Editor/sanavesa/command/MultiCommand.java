package sanavesa.command;

import java.util.ArrayList;

import sanavesa.source.Frame;

public class MultiCommand<T extends Commands> extends Commands
{
	private ArrayList<T> commands = new ArrayList<>();
	
	public MultiCommand(Frame affectedFrame)
	{
		super(affectedFrame);
	}

	public ArrayList<T> getCommands()
	{
		return commands;
	}

	public void setCommands(ArrayList<T> commands)
	{
		this.commands = commands;
	}

	@Override
	public void undo()
	{
		for(T command : commands)
		{
			command.undo();
		}
	}
}
