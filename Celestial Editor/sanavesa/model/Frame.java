package sanavesa.model;

import java.util.Arrays;
import java.util.Collection;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class Frame
{
	private final ReadOnlySetWrapper<Pixel> pixels;
	private final ObservableSet<Pixel> unmodifiablePixels;
	private final ReadOnlyBooleanWrapper visibility;

	public Frame(final boolean visibility)
	{
		pixels = new ReadOnlySetWrapper<>(FXCollections.observableSet());
		unmodifiablePixels = FXCollections.unmodifiableObservableSet(pixels.get());
		this.visibility = new ReadOnlyBooleanWrapper(visibility);
	}

	public final ObservableSet<Pixel> getPixels()
	{
		return unmodifiablePixels;
	}

	public final void setPixels(final ObservableSet<Pixel> newPixels)
	{
		setPixels(newPixels, true);
	}

	public final void setPixels(final ObservableSet<Pixel> newPixels, final boolean isUndoable)
	{
		if (newPixels == null)
			throw new IllegalArgumentException("newPixels cannot be null");

		if (isUndoable)
		{
			FramePixelsCommand command = new FramePixelsCommand(this, newPixels);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			pixels.set(newPixels);
		}
	}

	public final ReadOnlySetProperty<Pixel> pixelsProperty()
	{
		return pixels.getReadOnlyProperty();
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
			FrameVisibilityCommand command = new FrameVisibilityCommand(this, newVisibility);
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

	public final void addPixel(final Pixel pixel)
	{
		addPixel(pixel, true);
	}

	public final void addPixel(final Pixel pixel, final boolean isUndoable)
	{
		if (pixel == null)
			throw new IllegalArgumentException("pixel cannot be null");

		if (isUndoable)
		{
			FramePixelAddCommand command = new FramePixelAddCommand(this, pixel);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			pixels.add(pixel);
		}
	}

	public final void addPixels(final Pixel... collection)
	{
		addPixels(Arrays.asList(collection), true);
	}

	public final void addPixels(final boolean isUndoable, final Pixel... collection)
	{
		addPixels(Arrays.asList(collection), isUndoable);
	}

	public final void addPixels(final Collection<? extends Pixel> collection)
	{
		addPixels(collection, true);
	}

	public final void addPixels(final Collection<? extends Pixel> collection, final boolean isUndoable)
	{
		if (collection == null)
			throw new IllegalArgumentException("collection cannot be null");

		if (isUndoable)
		{
			FramePixelsAddCommand command = new FramePixelsAddCommand(this, collection);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			pixels.addAll(pixels);
		}
	}

	public final void removePixel(final Pixel pixel)
	{
		removePixel(pixel, true);
	}

	public final void removePixel(final Pixel pixel, final boolean isUndoable)
	{
		if (pixel == null)
			throw new IllegalArgumentException("pixel cannot be null");

		if (isUndoable)
		{
			FramePixelRemoveCommand command = new FramePixelRemoveCommand(this, pixel);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			pixels.remove(pixel);
		}
	}

	public final void removePixels(final Pixel... collection)
	{
		removePixels(Arrays.asList(collection), true);
	}

	public final void removePixels(final boolean isUndoable, final Pixel... collection)
	{
		removePixels(Arrays.asList(collection), isUndoable);
	}

	public final void removePixels(final Collection<? extends Pixel> collection)
	{
		removePixels(collection, true);
	}

	public final void removePixels(final Collection<? extends Pixel> collection, final boolean isUndoable)
	{
		if (collection == null)
			throw new IllegalArgumentException("collection cannot be null");

		if (isUndoable)
		{
			FramePixelsRemoveCommand command = new FramePixelsRemoveCommand(this, collection);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			pixels.removeAll(pixels);
		}
	}

	private final class FramePixelsCommand implements ICommand
	{
		private final Frame frame;
		private final ObservableSet<Pixel> newPixels;
		private ObservableSet<Pixel> oldPixels;

		public FramePixelsCommand(final Frame frame, final ObservableSet<Pixel> newPixels)
		{
			this.frame = frame;
			this.newPixels = newPixels;
		}

		@Override
		public final void execute()
		{
			oldPixels = frame.getPixels();
			frame.setPixels(newPixels, false);
		}

		@Override
		public final void undo()
		{
			frame.setPixels(oldPixels, false);
		}
	}

	private final class FrameVisibilityCommand implements ICommand
	{
		private final Frame frame;
		private final boolean newVisibility;
		private boolean oldVisibility;

		public FrameVisibilityCommand(final Frame frame, final boolean newVisibility)
		{
			this.frame = frame;
			this.newVisibility = newVisibility;
		}

		@Override
		public final void execute()
		{
			oldVisibility = frame.getVisibility();
			frame.setVisibility(newVisibility, false);
		}

		@Override
		public final void undo()
		{
			frame.setVisibility(oldVisibility, false);
		}
	}

	private final class FramePixelAddCommand implements ICommand
	{
		private final Frame frame;
		private final Pixel pixel;

		public FramePixelAddCommand(final Frame frame, final Pixel pixel)
		{
			this.frame = frame;
			this.pixel = pixel;
		}

		@Override
		public final void execute()
		{
			frame.pixels.add(pixel);
		}

		@Override
		public final void undo()
		{
			frame.pixels.remove(pixel);
		}
	}

	private final class FramePixelsAddCommand implements ICommand
	{
		private final Frame frame;
		private final Collection<? extends Pixel> pixels;

		public FramePixelsAddCommand(final Frame frame, final Collection<? extends Pixel> pixelsToAdd)
		{
			this.frame = frame;
			this.pixels = pixelsToAdd;
		}

		@Override
		public final void execute()
		{
			frame.pixels.addAll(pixels);
		}

		@Override
		public final void undo()
		{
			frame.pixels.removeAll(pixels);
		}
	}

	private final class FramePixelRemoveCommand implements ICommand
	{
		private final Frame frame;
		private final Pixel pixel;

		public FramePixelRemoveCommand(final Frame frame, final Pixel pixel)
		{
			this.frame = frame;
			this.pixel = pixel;
		}

		@Override
		public final void execute()
		{
			frame.pixels.remove(pixel);
		}

		@Override
		public final void undo()
		{
			frame.pixels.add(pixel);
		}
	}

	private final class FramePixelsRemoveCommand implements ICommand
	{
		private final Frame frame;
		private final Collection<? extends Pixel> pixels;

		public FramePixelsRemoveCommand(final Frame frame, final Collection<? extends Pixel> pixelsToAdd)
		{
			this.frame = frame;
			this.pixels = pixelsToAdd;
		}

		@Override
		public final void execute()
		{
			frame.pixels.removeAll(pixels);
		}

		@Override
		public final void undo()
		{
			frame.pixels.addAll(pixels);
		}
	}
}