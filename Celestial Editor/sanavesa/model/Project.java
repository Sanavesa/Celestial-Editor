package sanavesa.model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class Project
{
	private final ReadOnlyStringWrapper name;
	private final LayerManager layerManager;
	private final FrameManager frameManager;

	public Project(final String name)
	{
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		this.name = new ReadOnlyStringWrapper(name);
		layerManager = new LayerManager();
		frameManager = new FrameManager();
	}

	public final String getName()
	{
		return name.get();
	}

	public final void setName(final String newName)
	{
		setName(newName, true);
	}

	public final void setName(final String newName, final boolean isUndoable)
	{
		if (newName == null)
			throw new IllegalArgumentException("newName cannot be null");

		if (isUndoable)
		{
			ProjectNameCommand command = new ProjectNameCommand(this, newName);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			name.set(newName);
		}
	}

	public final ReadOnlyStringProperty nameProperty()
	{
		return name.getReadOnlyProperty();
	}

	public final LayerManager getLayerManager()
	{
		return layerManager;
	}

	public final FrameManager getFrameManager()
	{
		return frameManager;
	}

	private final class ProjectNameCommand implements ICommand
	{
		private final Project project;
		private final String newName;
		private String oldName;

		public ProjectNameCommand(final Project project, final String newName)
		{
			this.project = project;
			this.newName = newName;
		}

		@Override
		public final void execute()
		{
			oldName = project.getName();
			project.setName(newName, false);
		}

		@Override
		public final void undo()
		{
			project.setName(oldName, false);
		}
	}
}
