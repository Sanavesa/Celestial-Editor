package sanavesa.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class FrameManager
{
	private final ReadOnlyIntegerWrapper selectedFrameIndex;
	private final ReadOnlyListWrapper<Frame> frames;
	private final ObservableList<Frame> unmodifiableFrames;
	
	public FrameManager()
	{
		selectedFrameIndex = new ReadOnlyIntegerWrapper(0);
		frames = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
		unmodifiableFrames = FXCollections.unmodifiableObservableList(frames.get());
	}
	
	public final int getSelectedFrameIndex()
	{
		return selectedFrameIndex.get();
	}
	
	public final void setSelectedFrameIndex(int newIndex)
	{
		setSelectedFrameIndex(newIndex, true);
	}
	
	public final void setSelectedFrameIndex(int newIndex, boolean isUndoable)
	{
		if(isUndoable)
		{
			FrameManagerSelectedIndexCommand command = new FrameManagerSelectedIndexCommand(this, newIndex);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			selectedFrameIndex.set(newIndex);
		}
	}
	
	public final ReadOnlyIntegerProperty selectedFrameIndexProperty()
	{
		return selectedFrameIndex.getReadOnlyProperty();
	}
	
	public final ObservableList<Frame> getFrames()
	{
		return unmodifiableFrames;
	}
	
	public final void setFrames(ObservableList<Frame> newFrames)
	{
		setFrames(newFrames, true);
	}
	
	public final void setFrames(ObservableList<Frame> newFrames, boolean isUndoable)
	{
		if(newFrames == null)
			throw new IllegalArgumentException("newFrames cannot be null");
		
		if(isUndoable)
		{
			FrameManagerFramesCommand command = new FrameManagerFramesCommand(this, newFrames);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			frames.set(newFrames);
		}
	}
	
	public final ReadOnlyListProperty<Frame> framesProperty()
	{
		return frames.getReadOnlyProperty();
	}
	
	public final void addFrame(Frame frame)
	{
		if(frame == null)
			throw new IllegalArgumentException("frame cannot be null");
		
		FrameManagerFrameAddCommand command = new FrameManagerFrameAddCommand(this, frame);
		VersionControl.getInstance().executeCommand(command);
	}
	
	public final void removeFrame(Frame frame)
	{
		if(frame == null)
			throw new IllegalArgumentException("frame cannot be null");
		
		FrameManagerFrameRemoveCommand command = new FrameManagerFrameRemoveCommand(this, frame);
		VersionControl.getInstance().executeCommand(command);
	}
	
	private final class FrameManagerSelectedIndexCommand implements ICommand
	{
		private final FrameManager frameManager;
		private final int newIndex;
		private int oldIndex;
		
		public FrameManagerSelectedIndexCommand(FrameManager frameManager, int newIndex)
		{
			this.frameManager = frameManager;
			this.newIndex = newIndex;
		}
		
		@Override
		public final void execute()
		{
			oldIndex = frameManager.getSelectedFrameIndex();
			frameManager.setSelectedFrameIndex(newIndex, false);
		}

		@Override
		public final void undo()
		{
			frameManager.setSelectedFrameIndex(oldIndex, false);
		}
	}
	
	private final class FrameManagerFramesCommand implements ICommand
	{
		private final FrameManager frameManager;
		private final ObservableList<Frame> newFrames;
		private ObservableList<Frame> oldFrames;
		
		public FrameManagerFramesCommand(FrameManager frameManager, ObservableList<Frame> newFrames)
		{
			this.frameManager = frameManager;
			this.newFrames = newFrames;
		}

		@Override
		public void execute()
		{
			oldFrames = frameManager.getFrames();
			frameManager.setFrames(newFrames, false);
		}

		@Override
		public void undo()
		{
			frameManager.setFrames(oldFrames, false);
		}
	}
	
	private final class FrameManagerFrameAddCommand implements ICommand
	{
		private final FrameManager frameManager;
		private final Frame frame;
		
		public FrameManagerFrameAddCommand(FrameManager frameManager, Frame frame)
		{
			this.frameManager = frameManager;
			this.frame = frame;
		}
		
		@Override
		public final void execute()
		{
			frameManager.frames.add(frame);
		}

		@Override
		public final void undo()
		{
			frameManager.frames.remove(frame);
		}
	}
	
	private final class FrameManagerFrameRemoveCommand implements ICommand
	{
		private final FrameManager frameManager;
		private final Frame frame;
		
		public FrameManagerFrameRemoveCommand(FrameManager frameManager, Frame frame)
		{
			this.frameManager = frameManager;
			this.frame = frame;
		}
		
		@Override
		public final void execute()
		{
			frameManager.frames.remove(frame);
		}

		@Override
		public final void undo()
		{
			frameManager.frames.add(frame);
		}
	}
}
