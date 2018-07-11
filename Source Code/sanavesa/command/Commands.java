package sanavesa.command;

import sanavesa.source.Frame;

public abstract class Commands
{
	protected final Frame affectedFrame;
	
	public Commands(Frame affectedFrame)
	{
		super();
		this.affectedFrame = affectedFrame;
	}

	public Frame getAffectedFrame()
	{
		return affectedFrame;
	}
	
	public abstract void undo();
}
