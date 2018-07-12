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

	public final void setSelectedLayerIndex(final int newIndex)
	{
		setSelectedLayerIndex(newIndex, true);
	}

	public final void setSelectedLayerIndex(final int newIndex, final boolean isUndoable)
	{
		if (isUndoable)
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

	public final void setLayers(final ObservableList<Layer> newLayers)
	{
		setLayers(newLayers, true);
	}

	public final void setLayers(final ObservableList<Layer> newLayers, final boolean isUndoable)
	{
		if (newLayers == null)
			throw new IllegalArgumentException("newLayers cannot be null");

		if (isUndoable)
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

	public final void addLayer(final Layer layer)
	{
		addLayer(layer, true);
	}

	public final void addLayer(final Layer layer, final boolean isUndoable)
	{
		if (layer == null)
			throw new IllegalArgumentException("layer cannot be null");

		if (isUndoable)
		{
			LayerManagerLayerAddCommand command = new LayerManagerLayerAddCommand(this, layer);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			layers.add(layer);
		}
	}

	public final void removeLayer(final Layer layer)
	{
		removeLayer(layer, true);
	}

	public final void removeLayer(final Layer layer, final boolean isUndoable)
	{
		if (layer == null)
			throw new IllegalArgumentException("layer cannot be null");

		if (isUndoable)
		{
			LayerManagerLayerRemoveCommand command = new LayerManagerLayerRemoveCommand(this, layer);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			layers.remove(layer);
		}
	}

	private final class LayerManagerSelectedIndexCommand implements ICommand
	{
		private final LayerManager layerManager;
		private final int newIndex;
		private int oldIndex;

		public LayerManagerSelectedIndexCommand(final LayerManager layerManager, final int newIndex)
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

		public LayerManagerLayersCommand(final LayerManager layerManager, final ObservableList<Layer> newLayers)
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

		public LayerManagerLayerAddCommand(final LayerManager layerManager, final Layer layer)
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

		public LayerManagerLayerRemoveCommand(final LayerManager layerManager, final Layer layer)
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
