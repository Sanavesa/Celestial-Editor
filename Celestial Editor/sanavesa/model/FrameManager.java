package sanavesa.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class FrameManager
{
	private final ReadOnlyIntegerWrapper selectedFrameIndex;
	private final ReadOnlyListWrapper<Frame> frames;
	
	public FrameManager()
	{
		selectedFrameIndex = new ReadOnlyIntegerWrapper(0);
		frames = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	}
	
	public final int getSelectedFrameIndex()
	{
		return selectedFrameIndex.get();
	}
	
	public final void setSelectedFrameIndex(int newIndex)
	{
		selectedFrameIndex.set(newIndex);
	}
	
	public final ReadOnlyIntegerProperty selectedFrameIndexProperty()
	{
		return selectedFrameIndex.getReadOnlyProperty();
	}
	
	public final ObservableList<Frame> getFrames()
	{
		return frames.get();
	}
	
	public final void setFrames(ObservableList<Frame> newFrames)
	{
		frames.set(newFrames);
	}
	
	public final ReadOnlyListProperty<Frame> framesProperty()
	{
		return frames.getReadOnlyProperty();
	}
}
