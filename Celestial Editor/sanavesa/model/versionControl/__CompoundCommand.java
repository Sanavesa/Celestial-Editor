// __ prefix indicates unused!

package sanavesa.model.versionControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class __CompoundCommand implements ICommand
{
	private ArrayList<ICommand> commands;
	
	public __CompoundCommand(List<ICommand> commands)
	{
		this.commands = (ArrayList<ICommand>) commands;
	}
	
	public __CompoundCommand(ICommand... commands)
	{
		this.commands = (ArrayList<ICommand>) Arrays.asList(commands);
	}

	@Override
	public final void execute()
	{
		commands.forEach(cmd -> cmd.execute());
	}

	@Override
	public final void undo()
	{
		commands.forEach(cmd -> cmd.undo());
	}
	
	public final ArrayList<ICommand> getCommands()
	{
		return commands;
	}
}
