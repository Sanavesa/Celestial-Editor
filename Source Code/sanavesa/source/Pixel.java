/***************************************************************************************************************************
 * Class:		Pixel.java
 * Author:		Mohammad Alali
 * 
 * Description:	A pixel represents a single graphical unit used in Frame. It is not coupled with any GUI library. It implements 
 * 				the ISerializable interface which enables the class to be saved and loaded from an external file.
 * 	
 * Attributes: 	
 * 				int x
 * 				int y
 * 				double brightnessFactor
 * 				Layer layer
 * 		
 * Methods:		
 * 				Color getColor()
 * 				void save(ObjectOutputStream)
 * 				void load(ObjectInputStream)
 * 				void reset()
 * 
 ***************************************************************************************************************************/

package sanavesa.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.scene.paint.Color;
import sanavesa.gui.palette.Palette;
import sanavesa.util.MathUtil;

/**
 * A pixel represents a single graphical unit used in {@link Frame}.
 * <p>
 * It is not coupled with any GUI library.
 * It implements the {@link ISerializable} interface which enables
 * the class to be saved and loaded from an external file.
 * </p>
 * @author Mohammad Alali
 */
public class Pixel implements ISerializable
{
	/** The class serialization ID, used for file IO */
	private static final long serialVersionUID = -1996226191360147979L;

	/**
	 * The layer which contains the color and depth of the pixel.
	 * @see Layer
	 */
	private Layer layer = null;
	
	/**
	 * The x-location of the pixel in frame coordinates.
	 * <p>
	 * This value cannot be less than 0 and not more than
	 * <code>{@link Frame#getFrameWidth()} - 1</code>.
	 * </p>
	 */
	private int x = 0;
	
	/**
	 * The y-location of the pixel in frame coordinates.
	 * <p>
	 * This value cannot be less than 0 and not more than
	 * <code>{@link Frame#getFrameHeight()} - 1</code>.
	 * </p>
	 */
	private int y = 0;
	
	/**
	 * The brightness factor of the pixel's color.
	 * <p>
	 * The brightness factor's value is restricted in the range [-1, 1].
	 * <ul>
	 * <li> A value of 0 means the color of the pixel is the same as the layer. </li>
	 * <li> A value of 1 makes the color fully white. </li>
	 * <li> A value of -1 makes the color fully black. </li>
	 * <li> Values in between (0, 1) shall lighten by that amount. For example, 0.5 will lighten by 50%.
	 * <li> Values in between (0, -1) shall darken by that amount. For example, -0.5 will darken by 50%. 
	 * </ul>
	 * </p>
	 */
	private double brightnessFactor = 0;
	
	/**
	 * Creates a new pixel with the specified parameters.
	 * @param x					The x-position of the pixel in frame coordinates.
	 * @param y					The y-position of the pixel in frame coordinates.
	 * @param brightnessFactor	The brightnessFactor of the pixel's color.
	 * @param layer				The layer that represents the pixel's color.
	 * @see #x
	 * @see #y
	 * @see #brightnessFactor
	 * @see #layer
	 */
	public Pixel(int x, int y, double brightnessFactor, Layer layer)
	{
		this.x = x;
		this.y = y;
		this.brightnessFactor = brightnessFactor;
		this.layer = layer;
	}

	/**
	 * Copy/Clone constructor - copies the members of <code>other</code>.
	 * @param other		the other pixel to copy the attributes from
	 */
	public Pixel(Pixel other)
	{
		x = other.x;
		y = other.y;
		brightnessFactor = other.brightnessFactor;
		layer = other.layer;
	}
	
	/**
	 * Sets the X position of the pixel in frame coordination.
	 * The value will be bounded in the range [0, {@link Frame#getFrameWidth()} - 1]
	 * @param newX	the new X position of the pixel
	 */
	public void setX(int newX)
	{
		// Bound the x value between [0, frameWidth - 1]
		x = MathUtil.clamp(newX, 0, Frame.getFrameWidth() - 1);
	}
	
	/**
	 * Sets the Y position of the pixel in frame coordination.
	 * The value will be bounded in the range [0, {@link Frame#getFrameHeight()} - 1]
	 * @param newY	the new Y position of the pixel
	 */
	public void setY(int newY)
	{
		// Bound the y value between [0, frameHeight - 1]
		y = MathUtil.clamp(newY, 0, Frame.getFrameHeight() - 1);
	}
	
	/**
	 * Sets the brightness factor of the pixel's color.
	 * The brightness factor will be bounded in the range [-1, 1].
	 * @param newBrightnessFactor	the new brightness factor of the pixel
	 * @see #brightnessFactor
	 */
	public void setBrightnessFactor(double newBrightnessFactor)
	{
		// Bound the brightness value between [-1, 1]
		brightnessFactor = MathUtil.clamp(newBrightnessFactor, -1.0, 1.0);
	}
	
	/**
	 * Sets the X,Y location of the pixel in frame coordinates
	 * The position will be bounded in the frame coordinations
	 * [0,0] to [frameWidth - 1, frameHeight - 1].
	 * <p>
	 * See {@link #setX(int)} and {@link #setY(int)} if you wish to change
	 * X or Y only.
	 * </p>
	 * @param newX	the new X position of the pixel
	 * @param newY	the new Y position of the pixel
	 */
	public void setPosition(int newX, int newY)
	{
		setX(newX);
		setY(newY);
	}
	
	/**
	 * The X value will be in the range [0, {@link Frame#getFrameWidth()} - 1].
	 * @return the x location of the pixel in frame coordinates
	 * @see #x 
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * The Y value will be in the range [0, {@link Frame#getFrameHeight()} - 1].
	 * @return the y location of the pixel in frame coordinates
	 * @see #y
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * @return the brightness factor of the pixel 
	 * @see #brightnessFactor
	 */
	public double getBrightnessFactor()
	{
		return brightnessFactor;
	}
	
	/**
	 * @return the layer of the pixel
	 * @see #layer
	 */
	public Layer getLayer()
	{
		return layer;
	}

	/** Export the pixels's data into the file stream. */
	@Override
	public void save(ObjectOutputStream out) throws IOException
	{
		out.writeInt(x);
		out.writeInt(y);
		out.writeDouble(brightnessFactor);
		out.writeObject(layer.getName());
	}

	/** Resets the pixel's data to the default values. */
	@Override
	public void reset()
	{
		x = 0;
		y = 0;
		brightnessFactor = 0;
	}

	/** Imports the pixels's data from the file stream. */
	@Override
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		x = in.readInt();
		y = in.readInt();
		brightnessFactor = in.readDouble();
		String layerName = (String) in.readObject();
		layer = Palette.loadedLayers.stream().filter(e -> e.getName() == layerName).findFirst().get();
		System.out.println("Loaded Pixel: " + x + ", " + y);
	}
	
	/**
	 * @return the color of the layer with the brightness factor applied onto it.
	 */
	public Color getColor()
	{
		Color color;
		
		// The lightnessFactor [-1, 1].
		// Make lighter
		if(brightnessFactor >= 0)
		{
			// New_Saturation = Saturation * (1 - lightnessGUIValue)
			// New_Brightness = Brightness + lightnessGUIValue * (1 - Brightness)
			color = Color.hsb(
					layer.getColor().getHue(),
					layer.getColor().getSaturation() * (1 - brightnessFactor),
					layer.getColor().getBrightness() + brightnessFactor * (1- layer.getColor().getBrightness()),
					layer.getColor().getOpacity());
		}
		// Make darker
		else
		{
			// New_Brightness = Brightness * (1 + lightnessGUIValue)
			color = Color.hsb(
					layer.getColor().getHue(),
					layer.getColor().getSaturation(),
					layer.getColor().getBrightness() * (1 + brightnessFactor),
					layer.getColor().getOpacity());
		}
		
		return color;
	}
}