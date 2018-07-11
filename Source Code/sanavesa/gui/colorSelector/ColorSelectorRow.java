/***************************************************************************************************************************
 * Class:		ColorSelectorRow.java
 * Author:		Mohammad Alali
 * 
 * Description: This class is used in conjunction with ColorSelector for graphically displaying the color components on the 
 * 				screen and being to interactive with them.
 * 	
 * Attributes: 	
 * 				double minValue
 * 				double maxValue
 * 		
 * Methods:		
 * 				void addToGridPane(GridPane, int)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.colorSelector;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import sanavesa.util.MathUtil;

/**
 * This class is used in conjunction with {@link #ColorSelector}
 * for graphically displaying the color components on the screen and
 * being to interactive with them.
 * @author Mohammad Alali
 */
public class ColorSelectorRow
{
	/** The value of the component */
	private final DoubleProperty value = new SimpleDoubleProperty(this, "value", 0);
	
	/** The text label of the component */
	private final Label label = new Label();
	
	/** The slider associated with the component */
	private final Slider slider = new Slider();
	
	/** The text field associated with the component */
	private final TextField textField = new TextField();
	
	/** The minimum and maximum value allowed for the component */
	private final double minValue, maxValue;
	
	/**
	 * Initialize a component with its GUI elements.
	 * @param startValue			the starting value of the component
	 * @param minValue				the minimum value of the component
	 * @param maxValue				the maximum value of the component
	 * @param labelText				the text of the label's component
	 * @param sliderMajorTickUnit	the value between major ticks on the slider
	 */
	public ColorSelectorRow(double startValue, double minValue, 
			double maxValue, String labelText, double sliderMajorTickUnit,
			String tooltipText)
	{
		// Create the tooltip for the label, slider, and input field
		Tooltip tooltip = new Tooltip(tooltipText);
		
		// Initialize the minimum and maximum values allowed for the component
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		// Set the value to the starting value
		value.set(startValue);
		
		// Setup the label
		label.setText(labelText);
		label.setTooltip(tooltip);
		
		// Setup the slider
		slider.setTooltip(tooltip);
		slider.setMin(minValue);
		slider.setMax(maxValue);
		slider.setValue(startValue);
		
		// Setup slider's GUI tick features
		slider.setMajorTickUnit(sliderMajorTickUnit);
		slider.setMinorTickCount(4);
		slider.setBlockIncrement(1);
		slider.setShowTickMarks(true);
		
		// Setup input field's text and width
		textField.setText(String.format("%.0f", startValue));
		textField.setTooltip(tooltip);
		textField.setMaxWidth(64);
		
		// Called when the value has changed
		value.addListener((args, oldV, newV) -> onValueChanged(newV.doubleValue()));
		
		// Called when slider's value has been changed
		slider.valueProperty().addListener((args, oldV, newV) -> onSliderChanged(newV.doubleValue()));
		
		// Called when the text field box has been changed
		textField.textProperty().addListener((args, oldText, newText) -> onTextFieldChanged(oldText, newText));
	
		// Called when the text field box has been deselected
		textField.focusedProperty().addListener((args, oldFocus, newFocus) -> onTextFieldDeselected());
	}

	/** Determines if the text is valid. Constraints are numerical only, positive and negative */
	private boolean isTextValid(String text)
	{
		// If we are allowed to have negatives, then negatives are valid
		if(text.equals("-"))
			return (minValue < 0);
		
		// Empty text is valid
		if(text.length() == 0)
			return true;
		
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	/**
	 * Updates the slider and the text field's values to reflect the new value change.
	 * Called when {@link #value} changes. 
	 * @param newValue	the new assigned value for the component
	 */
	private void onValueChanged(double newValue)
	{
		slider.setValue(newValue);
		
		// Displays the value with 0 decimal places
		textField.setText(String.format("%.0f", newValue));
	}
	
	/**
	 * Updates the value of the component to the slider's new value.
	 * Called when the value of {@link #slider} changes.
	 * @param newValue
	 */
	private void onSliderChanged(double newValue)
	{
		value.set(newValue);
	}
	
	/**
	 * Attempts to set the value of the component to the entered text while
	 * maintaning the restriction of the bounds [min, max] passed via the constructor.
	 * Called when the text value of {@link #textField} changes.
	 * <p>
	 * It will revert to the previous text of the text field when
	 * <ul>
	 * <li> a non-numeric value is entered </li>
	 * <li> no characters entered </li>
	 * </ul>
	 * </p>
	 * @param oldText	the previous text before the change
	 * @param newText	the target text after the change
	 */
	private void onTextFieldChanged(String oldText, String newText)
	{
		// The try-catch is necessary for the numeric-parsing of the text
		try
		{
			// Check if input is valid
			// If it is invalid, throw an Invalid number format input exception 
			if(!isTextValid(newText))
				throw new NumberFormatException();
			
			// Attempt to evaluate the input
			if(textField.getLength() > 0)
			{
				// If the new text is just a - sign, skip evaluation
				if(!newText.equals("-"))
				{
					// Attempt to parse a numeric value from the text
					double newValue = Integer.parseInt(newText);
					
					// If no exceptions has been thrown,
					// Bound the numeric value between [min, max]
					newValue = MathUtil.clamp(newValue, minValue, maxValue);
					
					// Set the component value to the text field's value
					value.set(newValue);
					
					// Update the text of the input field to 0 decimal places
					textField.setText(String.format("%.0f", newValue));
				}
			}
		}
		catch(Exception e)
		{
			// A non-numeric value was entered, so revert back to the old text
			textField.setText(oldText);
		}
	}
	
	/**
	 * Reverts the text of the text field when the text field has 
	 * been deselected and has no text in it. It will revert the text
	 * back to the actual value of the component.
	 * Called when the {@link #textField} is deselected / lost focus.
	*/
	private void onTextFieldDeselected()
	{
		// If the text is empty when deselected,
		// revert back to the actual component value
		if(textField.getText().length() == 0)
		{
			textField.setText(String.format("%.0f", value.get()));
		}
	}
	
	/**
	 * @return the value of component
	 * @see value
	 */
	public double getValue()
	{
		return value.get();
	}
	
	/**
	 * Sets the component's value while being bounded by [{@link #minValue}, {@link #maxValue}].
	 * @param newValue	the new value to set the component to
	 */
	public void setValue(double newValue)
	{
		value.set(MathUtil.clamp(newValue, minValue, maxValue));
	}
	
	/**
	 * @return the property of the component's value
	 * @see value 
	 */
	public DoubleProperty valueProperty()
	{
		return value;
	}

	/**
	 * Adds the GUI elements to a grid pane in the specified row in this fashion:
	 * <ul>
	 * <li> Component's Label on the 0th column </li>
	 * <li> Component's Slider on the 1st column </li>
	 * <li> Component's Text Field on the 2nd column </li>
	 * </ul>
	 * @param gridPane	the grid pane to add the GUI elements to
	 * @param row		the row at which the GUI elements will be added at
	 */
	public void addToGridPane(GridPane gridPane, int row)
	{
		// Add label on 0th column
		gridPane.add(label, 0, row);
		
		// Add slider on 1st column
		gridPane.add(slider, 1, row);
		
		// Add text field on 2nd column
		gridPane.add(textField, 2, row);
	}
}