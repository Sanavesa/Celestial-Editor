package sanavesa.model.versionControl;

import java.util.Stack;

public final class VersionControl
{
	private static VersionControl instance = null;
	private final Stack<ICommand> undoCommands;
	private final Stack<ICommand> redoCommands;
	
	private VersionControl()
	{
		undoCommands = new Stack<>();
		redoCommands = new Stack<>();
	}
	
	public final static VersionControl getInstance()
	{
		if(instance == null)
			instance = new VersionControl();
		
		return instance;
	}
	
	public final int undoCount()
	{
		return undoCommands.size();
	}
	
	public final int redoCount()
	{
		return redoCommands.size();
	}
	
	public final void executeCommand(final ICommand command)
	{
		undoCommands.add(command);
		command.execute();
	}
	
	public final void redo()
	{
		if(!redoCommands.isEmpty())
		{
			ICommand command = redoCommands.pop();
			command.execute();
			undoCommands.push(command);
		}
	}
	
	public final void undo()
	{
		if(!undoCommands.isEmpty())
		{
			ICommand command = undoCommands.pop();
			command.undo();
			redoCommands.push(command);
		}
	}
	
	public final void clearUndos()
	{
		undoCommands.clear();
	}
	
	public final void clearRedos()
	{
		redoCommands.clear();
	}
}
