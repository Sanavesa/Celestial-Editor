package sanavesa.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class LayerManager
{
	private final ReadOnlyIntegerWrapper selectedLayerIndex;
	private final ReadOnlyListWrapper<Layer> layers;
	
	public LayerManager()
	{
		selectedLayerIndex = new ReadOnlyIntegerWrapper(0);
		layers = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	}
	
	public final int getSelectedLayerIndex()
	{
		return selectedLayerIndex.get();
	}
	
	public final void setSelectedLayerIndex(int newIndex)
	{
		selectedLayerIndex.set(newIndex);
	}
	
	public final ReadOnlyIntegerProperty selectedLayerIndexProperty()
	{
		return selectedLayerIndex.getReadOnlyProperty();
	}
	
	public final ObservableList<Layer> getLayers()
	{
		return layers.get();
	}
	
	public final void setFrames(ObservableList<Layer> newLayers)
	{
		layers.set(newLayers);
	}
	
	public final ReadOnlyListProperty<Layer> layersProperty()
	{
		return layers.getReadOnlyProperty();
	}
}
