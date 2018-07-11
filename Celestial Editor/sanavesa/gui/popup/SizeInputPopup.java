/***************************************************************************************************************************
 * Class:		SizeInputPopup.java
 * Author:		Mohammad Alali
 * 
 * Description:	A subclass of Popup. This class represents a double tuple input popup that returns a numeric tuple.
 * 				This class implements the IKeyMapping interface, which allows specific keys to be mapped to certain actions.
 * 	
 * Attributes: 	
 * 				Nothing Interesting
 * 		
 * Methods:		
 * 				void setupTextField1(String, String)
 * 				void setupTextField2(String, String)
 * 				void setupOkButton(String)
 * 				void setupCancelButton(String)
 * 				void setupInstructionLabel(String)
 * 				int getResponse1()
 * 				int getResponse2()
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.popup;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import sanavesa.gui.IKeyMapping;
import sanavesa.source.Frame;

/**
 * A subclass of {@link Popup}. This class represents an input
 * popup that returns a a numeric tuple.
 * <p>
 * This class implements the {@link IKeyMapping} interface, which
 * allows specific keys to be mapped to certain actions.
 * </p>
 * @author Mohammad Alali
 */
public class SizeInputPopup extends Popup implements IKeyMapping
{
	/** The text field in which the user inputs his string in */
	private final TextField textField1, textField2;
	
	/** The Yes / No buttons */
	private final Button okButton, cancelButton;
	
	/** The helper labels */
	private final Label instructionLabel1, instructionLabel2;
	
	/** The string input of the user */
	private String response1 = "", response2 = "";
	
	/**
	 * Creates a new text input popup with the specified parameters.
	 * @param windowTitle	the popup's window's title
	 */
	public SizeInputPopup(String windowTitle)
	{
		super();
		
		// Cool shadow for the text
		DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
		
		// Setup text field 1
		textField1 = new TextField();
		textField1.setEffect(shadow);
		textField1.setMinWidth(237);
		textField1.setMinHeight(25);
		textField1.relocate(194, 30);
		textField1.textProperty().addListener((args, oldText, newText) -> onTextFieldTextChanged(textField1, oldText, newText));
		// Called when the text field box has been deselected
		textField1.focusedProperty().addListener((args, oldFocus, newFocus) -> onTextFieldDeselected(textField1, Frame.getFrameWidth()));
		
		// Setup text field 2
		textField2 = new TextField();
		textField2.setEffect(shadow);
		textField2.setMinWidth(237);
		textField2.setMinHeight(25);
		textField2.relocate(194, 60);
		textField2.textProperty().addListener((args, oldText, newText) -> onTextFieldTextChanged(textField2, oldText, newText));
		// Called when the text field box has been deselected
		textField2.focusedProperty().addListener((args, oldFocus, newFocus) -> onTextFieldDeselected(textField2, Frame.getFrameHeight()));
		
		textField1.requestFocus();
		
		// Setup ok button
		okButton = new Button();
		okButton.relocate(100, 115);
		okButton.setMinWidth(80);
		okButton.setMinHeight(25);
		okButton.setCursor(Cursor.HAND);
		okButton.setOnAction(e -> onOkButtonClicked());
		
		// Setup cancel button
		cancelButton = new Button();
		cancelButton.relocate(237, 115);
		cancelButton.setMinWidth(80);
		cancelButton.setMinHeight(25);
		cancelButton.setCursor(Cursor.HAND);
		cancelButton.setOnAction(e -> onCancelButtonClicked());
		
		// Setup instruction label
		instructionLabel1 = new Label();
		instructionLabel1.setEffect(shadow);
		instructionLabel1.setStyle("-fx-font-size: 16");
		instructionLabel1.relocate(43, 30);
		
		
		// Setup instruction label
		instructionLabel2 = new Label();
		instructionLabel2.setEffect(shadow);
		instructionLabel2.setStyle("-fx-font-size: 16");
		instructionLabel2.relocate(43, 60);
		
		// Setup key binds
		initializeKeyMap(scene);

		// Setup stage
		stage.setTitle(windowTitle);
		
		// Setup root pane
		root.setMinWidth(473);
		root.setMinHeight(144);
		root.getChildren().addAll(textField1, textField2, okButton, cancelButton, instructionLabel1, instructionLabel2);
	}
	
	/**
	 * Customizes the text field with the specified parameters.
	 * @param defaultText	the starting default text inside the input field
	 * @param promptText	the helper text to display inside the input field when it is empty
	 */
	public void setupTextField1(String defaultText, String promptText)
	{
		// Setup text field 1
		textField1.setText(defaultText);
		textField1.setPromptText(promptText);
		textField1.requestFocus();
		textField1.selectAll();
	}
	
	/**
	 * Customizes the text field with the specified parameters.
	 * @param defaultText	the starting default text inside the input field
	 * @param promptText	the helper text to display inside the input field when it is empty
	 */
	public void setupTextField2(String defaultText, String promptText)
	{
		// Setup text field 1
		textField2.setText(defaultText);
		textField2.setPromptText(promptText);
	}
	
	/**
	 * Customizes the OK button.
	 * @param buttonText	the text to display for the OK button.
	 */
	public void setupOkButton(String buttonText)
	{
		okButton.setText(buttonText);
	}
	
	/**
	 * Customizes the NO button.
	 * @param buttonText	the text to display for the NO button.
	 */
	public void setupCancelButton(String buttonText)
	{
		cancelButton.setText(buttonText);
	}
	
	/**
	 * Customizes the instructions label, which annotates what the input field is about.
	 * @param labelText		the text to display the instructions to the user
	 */
	public void setupInstructionLabel1(String labelText)
	{
		instructionLabel1.setText(labelText);
	}
	
	/**
	 * Customizes the instructions label, which annotates what the input field is about.
	 * @param labelText		the text to display the instructions to the user
	 */
	public void setupInstructionLabel2(String labelText)
	{
		instructionLabel2.setText(labelText);
	}
	
	/** Determines if the text is valid. Constraints are numerical only, positive and negative */
	private boolean isTextValid(String text)
	{
		// We are allowed to have negatives
		if(text.equals("-"))
			return true;
		
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
	private void onTextFieldTextChanged(TextField textField, String newText, String oldText)
	{
		// The try-catch is necessary for the numeric-parsing of the text
		try
		{
			// Check if input is valid
			// If it is invalid, throw an Invalid number format input exception 
			if(!isTextValid(newText))
				throw new NumberFormatException();
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
	private void onTextFieldDeselected(TextField textField, int defaultValue)
	{
		// If the text is empty when deselected,
		// revert back to the actual component value
		if(textField.getText().length() == 0)
		{
			textField.setText(Integer.toString(defaultValue));
		}
	}
	
	/** Called when the NO button is clicked */
	private void onCancelButtonClicked()
	{
		response1 = "";
		response2 = "";
		stage.close();
	}

	/** Called when the OK button is clicked */
	private void onOkButtonClicked()
	{
		response1 = textField1.getText();
		response2 = textField2.getText();
		stage.close();
	}

	/**
	 * @return the input field response of the user 
	 */
	public String getResponse1()
	{
		return response1;
	}

	
	/**
	 * @return the input field response of the user 
	 */
	public String getResponse2()
	{
		return response2;
	}

	/**
	 * Sets up the key bindings.
	 * Pressing [ENTER] shall fire the button's action, 
	 * or if the text field was selected, then the OK button's action is fired.
	 * Pressing [ESC] shall fire the NO button's action, closing the window. 
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		// Setup keyboard shortcuts
		scene.addEventFilter(KeyEvent.KEY_PRESSED, e ->
		{
			if(e.getCode() == KeyCode.ENTER)
			{
				Node focusedNode = scene.getFocusOwner();
				if((focusedNode == okButton) || (focusedNode == textField1) || (focusedNode == textField2))
				{
					okButton.fire();
				}
				else if(focusedNode == cancelButton)
				{
					cancelButton.fire();
				}
			}
			else if(e.getCode() == KeyCode.ESCAPE)
			{
				cancelButton.fire();
			}
		});
	}

}
