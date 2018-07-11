/***************************************************************************************************************************
 * Class:		Frame.java
 * Author:		Mohammad Alali
 * 
 * Description: A frame represents an editable image. The pixels in the frame's image are represented by the Pixel class. 
			 	It is not coupled with any GUI library. It implements the ISerializable interface which enables
			 	the class to be saved and loaded from an external file.
 * 	
 * Attributes: 	
 * 				static int frameWidth
 * 				static int frameHeight
 * 				boolean visibility
 * 				string name
 * 				Set<Pixel> pixels
 * 		
 * Methods:		
 * 				Pixel findPixel(Predicate<Pixel>)
 * 				List<Pixel> findPixels(Predicate<Pixel>)
 * 				void save(ObjectOutputStream)
 * 				void reset()
 * 				void load(ObjectInputStream)
 * 
 ***************************************************************************************************************************/

package sanavesa.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import sanavesa.command.Commands;

/**
 * A frame represents an editable image. The pixels
 * in the frame's image are represented by {@link Pixel}. 
 * <p>
 * It is not coupled with any GUI library.
 * It implements the {@link ISerializable} interface which enables
 * the class to be saved and loaded from an external file.
 * </p>
 * @author Mohammad Alali
 */
public class Frame implements ISerializable
{
	
	/** The class serialization ID, used for file IO */
	private static final long serialVersionUID = -5058047010572404651L;

	/**
	 * The frame width of all the frames, initialized to 64.
	 * Minimum value is 1, no maximum value. Resizable with {@link #resizeFrame(int, int)}
	 */
	private static IntegerProperty frameWidth = new SimpleIntegerProperty(64);
	
	/**
	 * The frame height of all the frames, initialized to 64.
	 * Minimum value is 1, no maximum value. Resizable with {@link #resizeFrame(int, int)}
	 */
	private static IntegerProperty frameHeight = new SimpleIntegerProperty(64);
	
	/** The name of the frame */
	private StringProperty name = new SimpleStringProperty(this, "name", "");
	
	/** The visibility of the frame. (true = displayed, off = hidden) */
	private BooleanProperty visibility = new SimpleBooleanProperty(this, "visibility", true);
	
	/**
	 * The set of pixels in the frame. It has distinct elements
	 * and no duplicates of any pixels. Also, it only contains
	 * pixels that are within in the the location space [0, 0] and
	 * [{@link #frameWidth} - 1, {@link #frameHeight} - 1].
	 */
	private SetProperty<Pixel> pixels = new SimpleSetProperty<Pixel>(this, "pixels", null);
	
	/** Consists of the commands the user has done. Used for undo. */
	private Stack<Commands> commands = new Stack<>();
	
	/**
	 * Creates a new visible frame with the specified name and an empty set of pixels.
	 * @param newName the name of the frame
	 */
	public Frame(String newName)
	{
		this(newName, FXCollections.observableSet(new HashSet<Pixel>()), true);
	}
	
	/**
	 * Creates a new frame with the specified name and set of pixels.
	 * @param newName		the name of the frame
	 * @param newPixels		the set of pixels of the frame
	 * @param isVisible		whether the frame is visible or not
	 */
	public Frame(String newName, ObservableSet<Pixel> newPixels, boolean isVisible)
	{
		setName(newName);
		setPixels(newPixels);
		setVisibility(isVisible);
		
		// Listen to the changes of the frame's width and height
		frameWidthProperty().addListener(e -> onFrameWidthChanged());
		frameHeightProperty().addListener(e -> onFrameHeightChanged());
	}
	
	/**
	 * Changes the name of the frame
	 * @param newName the new name of the frame
	 */
	public void setName(String newName)
	{
		name.set(newName);
	}
	
	/**
	 * @return the name of the frame
	 */
	public String getName()
	{
		return name.get();
	}
	
	/**
	 * @return the string property of the frame's name
	 * @see StringProperty
	 */
	public StringProperty nameProperty()
	{
		return name;
	}
	
	/**
	 * Sets the visibility of the frame (true visible, false hidden)
	 * @param isVisible		whether the frame is visible or not
	 */
	public void setVisibility(Boolean isVisible)
	{
		visibility.set(isVisible);
	}
	
	/**
	 * @return the visibility of the frame (true visible, false hidden) 
	 */
	public Boolean getVisibility()
	{
		return visibility.get();
	}
	
	/**
	 * 
	 * @return the visibility property of the frame
	 * @see BooleanProperty
	 */
	public BooleanProperty visibilityProperty()
	{
		return visibility;
	}
	
	/**
	 * Sets the frame's pixel's set to <code>newPixels</code>.
	 * @param newPixels 	the frame's new set of pixels
	 */
	public void setPixels(ObservableSet<Pixel> newPixels)
	{
		pixels.set(newPixels);
	}
	
	/**
	 * @return a set that contains all pixels in the frame
	 * @see #pixels
	 * @see Pixel
	 */
	public ObservableSet<Pixel> getPixels()
	{
		return pixels.get();
	}
	
	/**
	 * @return a set property that contains all pixels in the frame
	 * @see #pixels
	 * @see SetProperty
	 * @see Pixel
	 */
	public SetProperty<Pixel> pixelsProperty()
	{
		return pixels;
	}
	
	/**
	 * Adds a pixel to the set of pixels in the frame.
	 * <p>
	 * If the pixel already exists in the set, it will not be added.
	 * If the pixel being added has position lower than [0, 0] or greater than 
	 * [{@link #frameWidth} - 1,{@link #frameHeight} - 1], it will not be added.
	 * </p>
	 * @param p		The pixel to be added to the set of pixels
	 * @see #pixels
	 */
	public void addPixel(Pixel p)
	{
		// Abort early if the pixel is not within the frame view bounds
		if(p.getX() < 0 || p.getY() < 0 || p.getX() > getFrameWidth() - 1 || p.getY() > getFrameHeight() - 1)
			return;
		
		pixels.add(p);
	}
	
//	/**
//	 * Adds a pixel to the set of pixels in the frame.
//	 * <p>
//	 * If the pixel already exists in the set, it will not be added.
//	 * If the pixel being added has position lower than [0, 0] or greater than 
//	 * [{@link #frameWidth} - 1,{@link #frameHeight} - 1], it will not be added.
//	 * </p>
//	 * @param p				The pixel to be added to the set of pixels
//	 * @param recordAction	Determines if this action will be recorded for undo
//	 * @see #pixels
//	 */
//	public void addPixel(Pixel p, boolean recordAction)
//	{
//		// Abort early if the pixel is not within the frame view bounds
//		if(p.getX() < 0 || p.getY() < 0 || p.getX() > getFrameWidth() - 1 || p.getY() > getFrameHeight() - 1)
//			return;
//		
//		if(pixels.add(p) && recordAction)
//			commands.push(new DrawCommand(this, p));
//	}
	
	/**
	 * Removes a pixel from the set of pixels in the frame. If the pixel doesn't 
	 * exist in the set, the function call would do nothing.
	 * @param p		The pixel to be removed from the set of pixels
	 * @see #pixels
	 */
	public void removePixel(Pixel p)
	{
		pixels.remove(p);
	}
	
//	/**
//	 * Removes a pixel from the set of pixels in the frame. If the pixel doesn't 
//	 * exist in the set, the function call would do nothing.
//	 * @param p		The pixel to be removed from the set of pixels
//	 * @param recordAction	Determines if this action will be recorded for undo
//	 * @see #pixels
//	 */
//	public void removePixel(Pixel p, boolean recordAction)
//	{
//		if(pixels.remove(p) && recordAction)
//			commands.push(new EraseCommand(this, p));
//	}
	
	/**
	 * Removes all pixels from the set of pixels in the frame.
	 * @see #pixels
	 */
	public void clearPixels()
	{
		pixels.clear();
	}
	
	/**
	 * This method is called internally after the frame's width has been changed.
	 * It will remove all pixels that are out of bound. Pixels that have a 
	 * x-coordinate outside the boundary <code>[0, {@link #frameWidth} - 1]</code> are removed.
	 * @see #frameWidth
	 */
	private void onFrameWidthChanged()
	{
		Pixel p = null;
		for(Iterator<Pixel> iterator = pixels.iterator(); iterator.hasNext();)
		{
			p = iterator.next();
			// Compare the x coordinate of the pixel with the new boundary
			if(p.getX() < 0 || p.getX() > getFrameWidth() - 1)
			{
				// Remove because it is outside the boundary [0, frameWidth - 1]
				iterator.remove();
			}
		}
	}
	
	/**
	 * This method is called internally after the frame's height has been changed.
	 * It will remove all pixels that are out of bound. Pixels that have a 
	 * y-coordinate outside the boundary [0, {@link #frameHeight} - 1] are removed.
	 * @see #frameHeight
	 */
	private void onFrameHeightChanged()
	{
		Pixel p = null;
		for(Iterator<Pixel> iterator = pixels.iterator(); iterator.hasNext();)
		{
			p = iterator.next();
			// Compare the y coordinate of the pixel with the new boundary
			if(p.getY() < 0 || p.getY() > getFrameHeight() - 1)
			{
				// Remove because it is outside the boundary [0, frameHeight - 1]
				iterator.remove();
			}
		}
	}
	
	/**
	 * This function is used to selectively choose a a single pixel out
	 * of all of the frame's pixels. Example use would be returning a 
	 * pixel that has an x-coordinate greater than 5.
	 * <p> Use {@link #findPixels(Predicate)} if you wish to return all matches. </p>
	 * @param predicate		a (boolean-valued) function that will be checked against each
	 * 						pixel in the frame's pixels.
	 * @return 	the first pixel in the frame that matches the criteria of the predicate.
	 * 			Will return null if the predicate is null. If there are no matches, it will
	 *			return null.
	 * @see Predicate
	 * @see Pixel
	 */
	public Pixel findPixel(Predicate<Pixel> predicate)
	{
		// Abort early if the predicate is nothing
		if(predicate == null)
			return null;
				
		try
		{
			// If an element exists, return it
			return pixels.stream().filter(predicate).findFirst().get();
		}
		catch(NoSuchElementException e)
		{
			// Didn't find a match, return null
			return null;
		}
	}
	
	/**
	 * This function is used to selectively choose pixels out
	 * of all of the frame's pixels. Example use would be returning a 
	 * list of pixel that have an x-coordinate greater than 5.
	 * <p> Use {@link #findPixel(Predicate)} if you wish to return a single match. </p>
	 * @param predicate		a (boolean-valued) function that will be checked against each
	 * 						pixel in the frame's pixels.
	 * @return 	A list of pixels in the frame that match the criteria of the predicate.
	 * 			Will return null if the predicate is null. If there are no matches, it will
	 *			return an empty list.
	 * @see Predicate
	 * @see Pixel
	 */
	public List<Pixel> findPixels(Predicate<Pixel> predicate)
	{
		// Abort early if the predicate is nothing
		if(predicate == null)
			return null;
		
		return pixels.stream().filter(predicate).collect(Collectors.<Pixel>toList());
	}
	
	/**
	 * Note that no pixel in the frame has an x-coordinate less
	 * than 0 or greater than frameWidth - 1.
	 * @return 	the width of all frames
	 */
	public static int getFrameWidth()
	{
		return frameWidth.get();
	}
	
	/**
	 * Note that no pixel in the frame has an y-coordinate less
	 * than 0 or greater than frameHeight - 1
	 * 
	 * @return 	the height of all frames
	 */
	public static int getFrameHeight()
	{
		return frameHeight.get();
	}
	
	/**
	 * @return 	the property that handles the frame's width
	 * @see		IntegerProperty
	 */
	public static IntegerProperty frameWidthProperty()
	{
		return frameWidth;
	}
	
	/**
	 * @return 	the property that handles the frame's height
	 * @see		IntegerProperty
	 */
	public static IntegerProperty frameHeightProperty()
	{
		return frameHeight;
	}
	
	/**
	 * Resizes all of the frames to the specified width and height.
	 *  <p>
	 * If the new frame size has a width or height of 0 or less,
	 * then it shall do nothing. The new frame boundary is defined to be 
	 * [0, 0] to [newWidth - 1, newHeight - 1]. All pixels in all frames that have 
	 * a position that is outside of this boundary shall be removed.
	 *  </p>
	 * @param 	newWidth 	The new width of all the frames.
	 * 						Must be greater than or equal to 1.
	 * @param 	newHeight 	The new height of all the frames.
	 * 						Must be greater than or equal to 1.
	 */
	public static void resizeFrame(int newWidth, int newHeight)
	{
		if(newWidth < 1 || newHeight < 1)
			return;
		
		frameWidth.set(newWidth);
		frameHeight.set(newHeight);
	}
	
	/** Export the frame's data into the file stream. */
	@Override
	public void save(ObjectOutputStream out) throws IOException
	{
		out.writeInt(frameWidth.get());
		out.writeInt(frameHeight.get());
		out.writeObject(name.get());
		out.writeBoolean(visibility.get());
		
		Pixel[] pixelsArray = pixels.toArray(new Pixel[0]);
		int size = pixelsArray.length;
		out.writeInt(size);
		
		for(int i = 0; i < size; i++)
		{
			pixelsArray[i].save(out);
		}
		
	}

	/** Resets the frame's data to the default values. */
	@Override
	public void reset()
	{
		frameWidth.set(64);
		frameHeight.set(64);
		name.set("");
		visibility.set(true);
		pixels.clear();
	}

	/** Imports the frame's data from the file stream. */
	@Override
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		frameWidth.set(in.readInt());
		frameHeight.set(in.readInt());
		name.set((String) in.readObject());
		visibility.set(in.readBoolean());
		
		int size = in.readInt();
		pixels.clear();
		for(int i = 0; i < size; i++)
		{
			Pixel p = new Pixel(0, 0, 0, null);
			p.load(in);
			pixels.add(p);
		}
	}
	
	/** Undos the last action the user did on this frame. */
	public void undo()
	{
		if(commands.size() > 0)
		{
			commands.pop().undo();
		}
	}

	/**
	 * Consists of the commands the user has done. Used for undo.
	 * @return		the commands stack 
	 */
	public Stack<Commands> getCommands()
	{
		return commands;
	}
}