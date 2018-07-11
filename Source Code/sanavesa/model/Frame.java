package sanavesa.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Frame
{
	private final ReadOnlyListWrapper<Pixel> pixels;
	private final ReadOnlyBooleanWrapper visibility;
	
	public Frame(boolean visibility)
	{
		pixels = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
		this.visibility = new ReadOnlyBooleanWrapper(visibility);
	}
	
	public final ObservableList<Pixel> getPixels()
	{
		return pixels.get();
	}
	
	public final void setPixels(ObservableList<Pixel> newPixels)
	{
		pixels.set(newPixels);
	}
	
	public final ReadOnlyListProperty<Pixel> pixelsProperty()
	{
		return pixels.getReadOnlyProperty();
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
}
