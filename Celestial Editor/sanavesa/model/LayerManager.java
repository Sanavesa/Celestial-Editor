package sanavesa.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class LayerManager
{
	private final ReadOnlyIntegerWrapper selectedLayerIndex;
	private final ReadOnlyListWrapper<Layer> layers;
	private final ObservableList<Layer> unmodifiableLayers;
	
	public LayerManager()
	{
		selectedLayerIndex = new ReadOnlyIntegerWrapper(0);
		layers = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
		unmodifiableLayers = FXCollections.unmodifiableObservableList(layers.get());
	}
	
	public final int getSelectedLayerIndex()
	{
		return selectedLayerIndex.get();
	}
	
	public final void setSelectedLayerIndex(int newIndex)
	{
		setSelectedLayerIndex(newIndex, true);
	}
	
	public final void setSelectedLayerIndex(int newIndex, boolean isUndoable)
	{
		if(isUndoable)
		{
			LayerManagerSelectedIndexCommand command = new LayerManagerSelectedIndexCommand(this, newIndex);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			selectedLayerIndex.set(newIndex);
		}
	}
	
	public final ReadOnlyIntegerProperty selectedLayerIndexProperty()
	{
		return selectedLayerIndex.getReadOnlyProperty();
	}
	
	public final ObservableList<Layer> getLayers()
	{
		return unmodifiableLayers;
	}
	
	public final void setLayers(ObservableList<Layer> newLayers)
	{
		setLayers(newLayers, true);
	}
	
	public final void setLayers(ObservableList<Layer> newLayers, boolean isUndoable)
	{
		if(newLayers == null)
			throw new IllegalArgumentException("newLayers cannot be null");
		
		if(isUndoable)
		{
			LayerManagerLayersCommand command = new LayerManagerLayersCommand(this, newLayers);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			layers.set(newLayers);
		}
	}
	
	public final ReadOnlyListProperty<Layer> layersProperty()
	{
		return layers.getReadOnlyProperty();
	}
	
	public final void addLayer(Layer layer)
	{
		if(layer == null)
			throw new IllegalArgumentException("layer cannot be null");
		
		LayerManagerLayerAddCommand command = new LayerManagerLayerAddCommand(this, layer);
		VersionControl.getInstance().executeCommand(command);
	}
	
	public final void removeLayer(Layer layer)
	{
		if(layer == null)
			throw new IllegalArgumentException("layer cannot be null");
		
		LayerManagerLayerRemoveCommand command = new LayerManagerLayerRemoveCommand(this, layer);
		VersionControl.getInstance().executeCommand(command);
	}
	
	private final class LayerManagerSelectedIndexCommand implements ICommand
	{
		private final LayerManager layerManager;
		private final int newIndex;
		private int oldIndex;
		
		public LayerManagerSelectedIndexCommand(LayerManager layerManager, int newIndex)
		{
			this.layerManager = layerManager;
			this.newIndex = newIndex;
		}
		
		@Override
		public final void execute()
		{
			oldIndex = layerManager.getSelectedLayerIndex();
			layerManager.setSelectedLayerIndex(newIndex, false);
		}

		@Override
		public final void undo()
		{
			layerManager.setSelectedLayerIndex(oldIndex, false);
		}
	}
	
	private final class LayerManagerLayersCommand implements ICommand
	{
		private final LayerManager layerManager;
		private final ObservableList<Layer> newLayers;
		private ObservableList<Layer> oldLayers;
		
		public LayerManagerLayersCommand(LayerManager layerManager, ObservableList<Layer> newLayers)
		{
			this.layerManager = layerManager;
			this.newLayers = newLayers;
		}

		@Override
		public void execute()
		{
			oldLayers = layerManager.getLayers();
			layerManager.setLayers(newLayers, false);
		}

		@Override
		public void undo()
		{
			layerManager.setLayers(oldLayers, false);
		}
	}
	
	private final class LayerManagerLayerAddCommand implements ICommand
	{
		private final LayerManager layerManager;
		private final Layer layer;
		
		public LayerManagerLayerAddCommand(LayerManager layerManager, Layer layer)
		{
			this.layerManager = layerManager;
			this.layer = layer;
		}
		
		@Override
		public final void execute()
		{
			layerManager.layers.add(layer);
		}

		@Override
		public final void undo()
		{
			layerManager.layers.remove(layer);
		}
	}
	
	private final class LayerManagerLayerRemoveCommand implements ICommand
	{
		private final LayerManager layerManager;
		private final Layer layer;
		
		public LayerManagerLayerRemoveCommand(LayerManager layerManager, Layer layer)
		{
			this.layerManager = layerManager;
			this.layer = layer;
		}
		
		@Override
		public final void execute()
		{
			layerManager.layers.remove(layer);
		}

		@Override
		public final void undo()
		{
			layerManager.layers.add(layer);
		}
	}
}
