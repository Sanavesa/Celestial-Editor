package sanavesa.model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public final class Project
{
	private final ReadOnlyStringWrapper name;
	private final LayerManager layerManager;
	private final FrameManager frameManager;
	
	public Project(String name)
	{
		this.name = new ReadOnlyStringWrapper(name);
		layerManager = new LayerManager();
		frameManager = new FrameManager();
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
	
	public final LayerManager getLayerManager()
	{
		return layerManager;
	}
	
	public final FrameManager getFrameManager()
	{
		return frameManager;
	}
}
