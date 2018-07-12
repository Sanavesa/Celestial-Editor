package sanavesa.model;

import java.util.UUID;

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
	private final String uniqueLayerIdentifier;
	private final ReadOnlyStringWrapper name;
	private final ReadOnlyObjectWrapper<Color> color;
	private final ReadOnlyBooleanWrapper visibility;
	private final ReadOnlyIntegerWrapper depth;

	public Layer(final String name, final Color color, final boolean visibility, final int depth)
	{
		this(name, color, visibility, depth, UUID.randomUUID().toString());
	}

	public Layer(final String name, final Color color, final boolean visibility, final int depth,
			final String uniqueLayerIdentifier)
	{
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");

		if (color == null)
			throw new IllegalArgumentException("color cannot be null");

		if (uniqueLayerIdentifier == null)
			throw new IllegalArgumentException("UUID cannot be null");

		this.name = new ReadOnlyStringWrapper(name);
		this.color = new ReadOnlyObjectWrapper<>(color);
		this.visibility = new ReadOnlyBooleanWrapper(visibility);
		this.depth = new ReadOnlyIntegerWrapper(depth);
		this.uniqueLayerIdentifier = uniqueLayerIdentifier;
	}

	public final String getUniqueLayerIdentifier()
	{
		return uniqueLayerIdentifier;
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

	public final void setColor(final Color newColor)
	{
		setColor(newColor, true);
	}

	public final void setColor(final Color newColor, final boolean isUndoable)
	{
		if (newColor == null)
			throw new IllegalArgumentException("newColor cannot be null");

		if (isUndoable)
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

	public final void setVisibility(final boolean newVisibility)
	{
		setVisibility(newVisibility, true);
	}

	public final void setVisibility(final boolean newVisibility, final boolean isUndoable)
	{
		if (isUndoable)
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

	public final void setDepth(final int newDepth)
	{
		setDepth(newDepth, true);
	}

	public final void setDepth(final int newDepth, final boolean isUndoable)
	{
		if (isUndoable)
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

		public LayerNameCommand(final Layer layer, final String newName)
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

		public LayerColorCommand(final Layer layer, final Color newColor)
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
		private boolean oldVisibility;

		public LayerVisibilityCommand(final Layer layer, final boolean newVisibility)
		{
			this.layer = layer;
			this.newVisibility = newVisibility;
		}

		@Override
		public final void execute()
		{
			oldVisibility = layer.getVisibility();
			layer.setVisibility(newVisibility, false);
		}

		@Override
		public final void undo()
		{
			layer.setVisibility(oldVisibility, false);
		}
	}

	private final class LayerDepthCommand implements ICommand
	{
		private final Layer layer;
		private final int newDepth;
		private int oldDepth;

		public LayerDepthCommand(final Layer pixel, final int newDepth)
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