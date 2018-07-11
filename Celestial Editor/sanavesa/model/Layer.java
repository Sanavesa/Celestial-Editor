package sanavesa.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.paint.Color;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class Layer
{
	private final ReadOnlyStringWrapper name;
	private final ReadOnlyObjectWrapper<Color> color;
	private final ReadOnlyBooleanWrapper visibility;
	private final ReadOnlyIntegerWrapper depth;
	
	public Layer(String name, Color color, boolean visibility, int depth)
	{
		if(name == null)
			throw new IllegalArgumentException("name cannot be null");
		
		if(color == null)
			throw new IllegalArgumentException("color cannot be null");
		
		this.name = new ReadOnlyStringWrapper(name);
		this.color = new ReadOnlyObjectWrapper<>(color);
		this.visibility = new ReadOnlyBooleanWrapper(visibility);
		this.depth = new ReadOnlyIntegerWrapper(depth);
	}
	
	public final String getName()
	{
		return name.get();
	}
	
	public final void setName(String newName)
	{
		setName(newName, true);
	}
	
	public final void setName(String newName, boolean isUndoable)
	{
		if(newName == null)
			throw new IllegalArgumentException("newName cannot be null");
		
		if(isUndoable)
		{
			LayerNameCommand command = new LayerNameCommand(this, newName);
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
	
	public final Color getColor()
	{
		return color.get();
	}
	
	public final void setColor(Color newColor)
	{
		setColor(newColor, true);
	}
	
	public final void setColor(Color newColor, boolean isUndoable)
	{
		if(newColor == null)
			throw new IllegalArgumentException("newColor cannot be null");
		
		if(isUndoable)
		{
			LayerColorCommand command = new LayerColorCommand(this, newColor);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			color.set(newColor);
		}
	}
	
	public final ReadOnlyObjectProperty<Color> colorProperty()
	{
		return color.getReadOnlyProperty();
	}
	
	public final boolean getVisibility()
	{
		return visibility.get();
	}
	
	public final void setVisibility(boolean newVisibility)
	{
		setVisibility(newVisibility, true);
	}
	
	public final void setVisibility(boolean newVisibility, boolean isUndoable)
	{
		if(isUndoable)
		{
			LayerVisibilityCommand command = new LayerVisibilityCommand(this, newVisibility);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			visibility.set(newVisibility);
		}
	}
	
	public final ReadOnlyBooleanProperty visibilityProperty()
	{
		return visibility.getReadOnlyProperty();
	}
	
	public final int getDepth()
	{
		return depth.get();
	}
	
	public final void setDepth(int newDepth)
	{
		setDepth(newDepth, true);
	}
	
	public final void setDepth(int newDepth, boolean isUndoable)
	{
		if(isUndoable)
		{
			LayerDepthCommand command = new LayerDepthCommand(this, newDepth);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			depth.set(newDepth);
		}
	}
	
	public final ReadOnlyIntegerProperty depthProperty()
	{
		return depth.getReadOnlyProperty();
	}
	
	private final class LayerNameCommand implements ICommand
	{
		private final Layer layer;
		private final String newName;
		private String oldName;
		
		public LayerNameCommand(Layer layer, String newName)
		{
			this.layer = layer;
			this.newName = newName;
		}
		
		@Override
		public final void execute()
		{
			oldName = layer.getName();
			layer.setName(newName, false);
		}

		@Override
		public final void undo()
		{
			layer.setName(oldName, false);
		}
	}
	
	private final class LayerColorCommand implements ICommand
	{
		private final Layer layer;
		private final Color newColor;
		private Color oldColor;
		
		public LayerColorCommand(Layer layer, Color newColor)
		{
			this.layer = layer;
			this.newColor = newColor;
		}
		
		@Override
		public final void execute()
		{
			oldColor = layer.getColor();
			layer.setColor(newColor, false);
		}

		@Override
		public final void undo()
		{
			layer.setColor(oldColor, false);
		}
	}
	
	private final class LayerVisibilityCommand implements ICommand
	{
		private final Layer layer;
		private final boolean newVisibility;
		
		public LayerVisibilityCommand(Layer layer, boolean newVisibility)
		{
			this.layer = layer;
			this.newVisibility = newVisibility;
		}
		
		@Override
		public final void execute()
		{
			layer.setVisibility(newVisibility, false);
		}

		@Override
		public final void undo()
		{
			layer.setVisibility(!newVisibility, false);
		}
	}
	
	private final class LayerDepthCommand implements ICommand
	{
		private final Layer layer;
		private final int newDepth;
		private int oldDepth;
		
		public LayerDepthCommand(Layer pixel, int newDepth)
		{
			this.layer = pixel;
			this.newDepth = newDepth;
		}
		
		@Override
		public final void execute()
		{
			oldDepth = layer.getDepth();
			layer.setDepth(newDepth, false);
		}

		@Override
		public final void undo()
		{
			layer.setDepth(oldDepth, false);
		}
	}
}