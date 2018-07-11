/***************************************************************************************************************************
 * Class:		Layer.java
 * Author:		Mohammad Alali
 * 
 * Description:	A Layer represents a color layer with visibility and depth features. The Layer class complements Pixel's usage. 
 * 				It is not coupled with any GUI library. It implements the ISerializable interface which enables the class to be 
 * 				saved and loaded from an external file.
 * 	
 * Attributes: 	
 * 				boolean visibility
 * 				Color color
 * 				string name
 * 				int depth
 * 		
 * Methods:		
 * 				void save(ObjectOutputStream)
 * 				void reset()
 * 				void load(ObjectInputStream)
 * 
 ***************************************************************************************************************************/

package sanavesa.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * A Layer represents a color layer with visibility and depth features.
 * The Layer class complements {@link Pixel}'s usage. 
 * <p>
 * It is not coupled with any GUI library.
 * It implements the {@link ISerializable} interface which enables
 * the class to be saved and loaded from an external file.
 * </p>
 * @author Mohammad Alali
 */
public class Layer implements ISerializable
{
	/** The class serialization ID, used for file IO */
	private static final long serialVersionUID = 4878304384802360086L;

	/** The name of the layer */ 
	private StringProperty name = new SimpleStringProperty(this, "name", "");
	
	/** The color of the layer */
	private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(this, "color", Color.TRANSPARENT);
	
	/** The visibility (on or off) of the layer */ 
	private BooleanProperty visibility = new SimpleBooleanProperty(this, "visibility", true);
	
	/**
	 * The depth of the layer. Lower values of depth are rendered first, making it appear in the background.
	 * Depth isn't bounded between any range. A higher depth layer will appear over a lower depth layer.
	 */
	private IntegerProperty depth = new SimpleIntegerProperty(this, "depth", 0);
	
	/**
	 * Creates a new Layer with the specified parameters.
	 * @param newName		the name of the layer
	 * @param newColor		the color of the layer
	 * @param isVisible		whether the layer is visible or not
	 * @param newDepth		the depth of the layer
	 */
	public Layer(String newName, Color newColor, boolean isVisible, int newDepth)
	{
		setName(newName);
		setColor(newColor);
		setVisibility(isVisible);
		setDepth(newDepth);
	}
	
	/**
	 * Creates a new layer with the specified parameters, and by default is visible with a depth of 0.
	 * @param newName		the name of the layer
	 * @param newColor		the color of the layer
	 */
	public Layer(String newName, Color newColor)
	{
		this(newName, newColor, true, 0);
	}
	
	/**
	 * Sets the name of the layer
	 * @param newName	the new name of the layer
	 */
	public void setName(String newName)
	{
		name.set(newName);
	}
	
	/**
	 * @return the name of the layer
	 */
	public String getName()
	{
		return name.get();
	}
	
	/**
	 * @return the name property of the layer
	 */
	public StringProperty nameProperty()
	{
		return name;
	}
	
	/**
	 * Sets the color of a layer.
	 * @param newColor	the new color of the layer
	 */
	public void setColor(Color newColor)
	{
		color.set(newColor);
	}
	
	/**
	 * @return the color of the layer
	 */
	public Color getColor()
	{
		return color.get();
	}
	
	/**
	 * @return the color property of the layer
	 */
	public ObjectProperty<Color> colorProperty()
	{
		return color;
	}
	
	/**
	 * Sets the visibility of the layer (true visible, false hidden)
	 * @param isVisible		whether the layer is visible or not
	 */
	public void setVisibility(Boolean isVisible)
	{
		visibility.set(isVisible);
	}
	
	/**
	 * @return the visibility of the layer (true visible, false hidden)
	 */
	public Boolean getVisibility()
	{
		return visibility.get();
	}
	
	/**
	 * @return the visibility property of the layer
	 */
	public BooleanProperty visibilityProperty()
	{
		return visibility;
	}
	
	/**
	 * Set the depth of the layer. Higher depth is displayed over a lower depth.
	 * @param newDepth	the new depth of the layer
	 */
	public void setDepth(int newDepth)
	{
		depth.set(newDepth);
	}
	
	/**
	 * @return the depth of the layer
	 */
	public int getDepth()
	{
		return depth.get();
	}
	
	/**
	 * @return the depth property of the layer
	 */
	public IntegerProperty depthProperty()
	{
		return depth;
	}

	/** Export the layer's data into the file stream. */
	@Override
	public void save(ObjectOutputStream out) throws IOException
	{
		out.writeObject(name.get());
		out.writeBoolean(visibility.get());
		out.writeInt(depth.get());
		out.writeDouble(color.get().getRed());
		out.writeDouble(color.get().getGreen());
		out.writeDouble(color.get().getBlue());
		out.writeDouble(color.get().getOpacity());
	}

	/** Resets the layer's data to the default values. */
	@Override
	public void reset()
	{
		setName("");
		setVisibility(true);
		setDepth(0);
		setColor(Color.BLACK);
	}

	/** Export the layer's data from the file stream. */
	@Override
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		setName((String) in.readObject());
		setVisibility(in.readBoolean());
		setDepth(in.readInt());
		
		double r = in.readDouble();
		double g = in.readDouble();
		double b = in.readDouble();
		double a = in.readDouble();
		setColor(new Color(r, g, b, a));
	}
}