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

public final class Layer
{
	private final ReadOnlyStringWrapper name;
	private final ReadOnlyObjectWrapper<Color> color;
	private final ReadOnlyBooleanWrapper visibility;
	private final ReadOnlyIntegerWrapper depth;
	
	public Layer(String name, Color color, boolean visibility, int depth)
	{
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
		name.set(newName);
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
		color.set(newColor);
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
		visibility.set(newVisibility);
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
		depth.set(newDepth);
	}
	
	public final ReadOnlyIntegerProperty depthProperty()
	{
		return depth.getReadOnlyProperty();
	}
}