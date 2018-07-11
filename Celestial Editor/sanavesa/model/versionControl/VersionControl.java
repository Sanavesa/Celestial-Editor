package sanavesa.model.versionControl;

import java.util.Stack;

public class VersionControl
{
	private static VersionControl instance = null;
	private Stack<ICommand> undoCommands;
	private Stack<ICommand> redoCommands;
	
	private VersionControl()
	{
		undoCommands = new Stack<>();
		redoCommands = new Stack<>();
	}
	
	public static VersionControl getInstance()
	{
		if(instance == null)
			instance = new VersionControl();
		
		return instance;
	}
	
	public int undoCount()
	{
		return undoCommands.size();
	}
	
	public int redoCount()
	{
		return redoCommands.size();
	}
	
//	public void addCommand(ICommand command)
//	{
//		undoCommands.add(command);
//	}
	
	public void executeCommand(ICommand command)
	{
		undoCommands.add(command);
		command.execute();
	}
	
	public void redo()
	{
		if(!redoCommands.isEmpty())
		{
			ICommand command = redoCommands.pop();
			command.execute();
			undoCommands.push(command);
		}
	}
	
	public void undo()
	{
		if(!undoCommands.isEmpty())
		{
			ICommand command = undoCommands.pop();
			command.undo();
			redoCommands.push(command);
		}
	}
	
	public void clearUndos()
	{
		undoCommands.clear();
	}
	
	public void clearRedos()
	{
		redoCommands.clear();
	}
}
