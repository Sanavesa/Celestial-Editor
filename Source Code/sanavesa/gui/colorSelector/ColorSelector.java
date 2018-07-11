/***************************************************************************************************************************
 * Class:		ColorSelector.java
 * Author:		Mohammad Alali
 * 
 * Description: This class graphically displays the selected color and allows the user to interactively changed the color 
 * 				components. The color is in HSB color space. Implements the {@link IGraphicalInterface} and the IKeyMapping
 * 				interfaces to allow for cohesive GUI support and Key Binding.
 * 	
 * Attributes: 	
 * 				ColorSelectorRow hueRow
 * 				ColorSelectorRow saturationRow
 * 				ColorSelectorRow brightnessRow
 * 				ColorSelectorRow opacityRow
 * 				ColorSelectorRow luminosityRow
 * 		
 * Methods:		
 * 				double getLuminosityFactor()
 * 				Color getColorWithFactor()
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.colorSelector;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sanavesa.gui.IGraphicalInterface;
import sanavesa.gui.IKeyMapping;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.util.MathUtil;

/**
 * This class graphically displays the selected color and allows the user
 * to interactively changed the color components. The color is in HSB color space.
 * <p>
 * Implements the {@link IGraphicalInterface} and the {@link IKeyMapping} interfaces
 * to allow for cohesive GUI support and Key Binding.
 * </p>
 * @author Mohammad Alali
 */
public class ColorSelector implements IGraphicalInterface, IKeyMapping
{
	/** The root pane of the color selector GUI. */
	private Pane rootPane = new Pane();
	
	/** The grid pane holding the color components */
	private GridPane gridPane = new GridPane();
	
	/**
	 * The resultant colors of the the selected color and
	 * the selected color with the lightness factor.
	 */
	private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(this, "color", Color.TRANSPARENT), 
			colorWithFactor = new SimpleObjectProperty<Color>(this, "colorWithFactor", Color.TRANSPARENT);
	
	/**
	 * The rectangles used for purely visual effect to
	 * display the selected color and the selected color
	 * with the lightness factor.
	 */
	private Rectangle rectColor = new Rectangle(),
		rectColorWithFactor = new Rectangle();
	
	/** The color components used to manipulate the selected color. */
	private ColorSelectorRow hueRow, saturationRow, brightnessRow,
				opacityRow, luminosityFactorRow;
	
	/**
	 * The labels for the selected color and the selected color
	 * with the lightness factor.
	 */
	private Label labelColor = new Label(),
			labelColorWithFactor = new Label();
	
	
	private ColorPicker colorPicker = new ColorPicker();
	
	/**
	 * Initiliazes the ColorSelector's GUI at the specified location.
	 */
	@Override
	public void initializeLayout()
	{
		// Setup the labels text and their tooltips
		labelColor.setText("Selected Color");
		labelColor.setTooltip(new Tooltip("This is the color of your layer. It is affected by the Hue, Saturation, Brightness, and Opacity sliders."));
		labelColorWithFactor.setText("Color With Luminosity Factor");
		labelColorWithFactor.setTooltip(new Tooltip("This is the color of your brush. It is the color of layer modified by the luminosity factor."));
		
		// Relocate the grid pane and setup its spacing
		gridPane.relocate(0, 64);
		gridPane.setVgap(5);
		gridPane.setHgap(10);
		
		// Setup the color gui rows
		setupColorRows();
		
		// Setup the color rectangles
		setupColorRectangles();
		
		// Update the colors from the GUI
		updateColors();
		
		// TEMP: Temporary hack for color picker to work
		colorPicker.valueProperty().addListener((args, oldC, newC) -> 
		{
			if(color.get() != newC)
				setColor(newC);
		});
		
		// Add all of the GUI elements to the root node of the color selector
		rootPane.getChildren().addAll(rectColor, rectColorWithFactor, 
				gridPane, labelColor, labelColorWithFactor);
	}
	
	/**
	 * Initializes the GUI for the R,G,B,A and lightness components. It will also
	 * hook up interaction and adds each row to the grid pane.
	 */
	private void setupColorRows()
	{
		// Initialize the GUI rows
		hueRow = new ColorSelectorRow(0, 0, 360, "Hue", 60, "Hue Component [0-360]"
				+ "\nStands for the color value.");
		
		saturationRow = new ColorSelectorRow(100, 0, 100, "Saturation", 25, "Saturation Component [0-100]"
				+ "\nStands for how washed away your the color is."
				+ "\nA value of 0 makes the color fully greyscale.");
		
		brightnessRow = new ColorSelectorRow(100, 0, 100, "Brightness", 25, "Brightness Component [0-100]"
				+ "\nStands for how black your color is."
				+ "\nA value of 0 makes the color fully black and dark.");
		
		opacityRow = new ColorSelectorRow(10, 0, 100, "Opacity", 25, "Opacity Component [0-100]"
				+ "\nA value of 0 makes the color fully transparent."
				+ "\nA value of 100 makes the color fully opaque.");
		
		luminosityFactorRow = new ColorSelectorRow(0, -100, 100, "Luminosity Factor", 50, 
				"Luminosity Factor [-100 - 100]"
				+ "\nThis modifies the brush color only."
				+ "\n\nA factor of 0 uses the color of the layer."
				+ "\nA positive value makes the color lighter."
				+ "\nA negative value makes the color darker."
				+ "\n\n[CTRL + Mouse Wheel Down] Decrement by 5"
				+ "\n[CTRL + Middle Mouse Button] Reset to 0"
				+ "\n[CTRL + Mouse Wheel Up] Increment by 5");
		
		// Update the color when any of the rows change its value
		hueRow.valueProperty().addListener((e, oldV, newV) -> onColorChanged());
		saturationRow.valueProperty().addListener((e, oldV, newV) -> onColorChanged());
		brightnessRow.valueProperty().addListener((e, oldV, newV) -> onColorChanged());
		opacityRow.valueProperty().addListener((e, oldV, newV) -> onColorChanged());
		luminosityFactorRow.valueProperty().addListener((e, oldV, newV) -> onColorChanged());
		
		// Add the rows to the the gridpane
		hueRow.addToGridPane(gridPane, 0);
		saturationRow.addToGridPane(gridPane, 1);
		brightnessRow.addToGridPane(gridPane, 2);
		opacityRow.addToGridPane(gridPane, 3);
		luminosityFactorRow.addToGridPane(gridPane, 4);
		
		// TEMP
		gridPane.add(colorPicker, 1, 5);
	}
	
	/**
	 * Updates the color and the lightness affected color from the GUI.
	 */
	private void updateColors()
	{
		// Calculate the color from the GUI
		
		color.set(Color.hsb(
				hueRow.getValue(),
				saturationRow.getValue() / 100, 
				brightnessRow.getValue() / 100,
				opacityRow.getValue()/ 100));
		
		// The lightnessFactor [-1, 1].
		double lightnessFactor = luminosityFactorRow.getValue() / 100;
		
		// Make lighter
		if(lightnessFactor >= 0)
		{
			// New_Saturation = Saturation * (1 - lightnessGUIValue)
			// New_Brightness = Brightness + lightnessGUIValue * (1 - Brightness)
			colorWithFactor.set(Color.hsb(
					color.get().getHue(),
					color.get().getSaturation() * (1 - lightnessFactor),
					color.get().getBrightness() + lightnessFactor * (1- color.get().getBrightness()),
					color.get().getOpacity()));
		}
		// Make darker
		else
		{
			// New_Brightness = Brightness * (1 + lightnessGUIValue)
			colorWithFactor.set(Color.hsb(
					color.get().getHue(),
					color.get().getSaturation(),
					color.get().getBrightness() * (1 + lightnessFactor),
					color.get().getOpacity()));
		}
	}
	
	/**
	 * Sets the color of the color selector to the specified color.
	 * This will change the row's component's value.
	 * @param c		the new color
	 */
	public void setColor(Color c)
	{
		// The builtin Color class returns [0,1] for saturation, brightness, opacity
		// And returns [0, 360] for the hue
		
		// Our rows sliders use [0, 360] for hue, [0, 100] for the saturation, brightness, opacity
		hueRow.setValue(c.getHue());
		saturationRow.setValue(c.getSaturation() * 100);
		brightnessRow.setValue(c.getBrightness() * 100);
		opacityRow.setValue(c.getOpacity() * 100);
	}

	/**
	 * Recalculates the colors from the GUI.
	 * And, sets the rectangles to their new colors.
	 * Called when the color has changed.
	 */
	private void onColorChanged()
	{
		// Recalculates the colors from the GUI
		updateColors();
		
		// Sets the rectangles to their new colors
		rectColor.setFill(color.get());
		rectColorWithFactor.setFill(colorWithFactor.get());
		
		// TEMP
		if(colorPicker.getValue() != color.get())
			colorPicker.setValue(color.get());
	}
	
	/**
	 * Setups the color rectangles to have half the
	 * width of the grid pane.
	 */
	private void setupColorRectangles()
	{
		// Using runLater to allow the gridPane's width to be set
		// appropriately and not return 0 when calling getWidth.
		Platform.runLater(() ->
		{
			double halfWidth = gridPane.getWidth()/2;
			rectColor.setWidth(halfWidth);
			rectColor.setHeight(32);
			rectColor.relocate(0, 16);
			labelColor.relocate(0, 0);
			
			rectColorWithFactor.setWidth(halfWidth);
			rectColorWithFactor.setHeight(32);
			rectColorWithFactor.relocate(halfWidth, 16);
			labelColorWithFactor.relocate(halfWidth, 0);
		});
	}

	/**
	 * @return the selected color without the modification
	 * 			of the lighness factor
	 */
	public Color getColor()
	{
		return color.get();
	}
	
	/**
	 * @return the color property
	 */
	public ObjectProperty<Color> colorProperty()
	{
		return color;
	}
	
	/**
	 * @return the color with lightness factor property
	 */
	public ObjectProperty<Color> colorWithFactorProperty()
	{
		return colorWithFactor;
	}
	
	/**
	 * @return the selected color with the modification
	 * 			of the lighness factor
	 */
	public Color getColorWithFactor()
	{
		return colorWithFactor.get();
	}

	/**
	 * The root node of the ColorSelector's GUI
	 */
	@Override
	public Pane getRoot()
	{
		return rootPane;
	}

	/**
	 * @return the luminosty factor value in the gui, which is constrained [-100, 100]
	 */
	public double getLuminosityFactor()
	{
		return luminosityFactorRow.getValue();
	}
	
	/**
	 * Sets up the key bindings.
	 * Where [CTRL + WHEEL UP] increments the lightness by 5,
	 * [CTRL + MMB] resets the lightness to 0,
	 * and [CTRL + WHEEL DOWN] decrements the lightness by 5.
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.addEventFilter(ScrollEvent.SCROLL, event ->
		{
			// Increment lightness by 5 : ctrl + wheel up
			if(event.getDeltaY() > 0 && event.isControlDown())
				luminosityFactorRow.setValue(luminosityFactorRow.getValue() + 5);
			
			// Decrement lightness by 5 : ctrl + wheel down
			if(event.getDeltaY() < 0 && event.isControlDown())
				luminosityFactorRow.setValue(luminosityFactorRow.getValue() - 5);
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
		{
			// Reset lightness to 0 : ctrl + mmb
			if(event.isMiddleButtonDown() && event.isControlDown())
				luminosityFactorRow.setValue(0);
		});
	}

	/** Sets the luminosity factor value, which is in the range [-100, 100]. */
	public void setLuminosityFactor(double newValue)
	{
		luminosityFactorRow.setValue(MathUtil.clamp(newValue, -100.0, 100.0));
	}
}
