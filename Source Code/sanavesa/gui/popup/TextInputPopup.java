/***************************************************************************************************************************
 * Class:		TextInputPopup.java
 * Author:		Mohammad Alali
 * 
 * Description:	A subclass of Popup. This class represents a text input popup that returns a string.
 * 				This class implements the {@link IKeyMapping} interface, which allows specific keys to be mapped to certain actions.
 * 	
 * Attributes: 	
 * 				Nothing Interesting
 * 		
 * Methods:		
 * 				void setupOkButton(String)
 * 				void setupCancelButton(String)
 * 				void setupInstructionLabel(String)
 * 				String getResponse()
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

/**
 * A subclass of {@link Popup}. This class represents a text input
 * popup that returns a string.
 * <p>
 * This class implements the {@link IKeyMapping} interface, which
 * allows specific keys to be mapped to certain actions.
 * </p>
 * @author Mohammad Alali
 */
public class TextInputPopup extends Popup implements IKeyMapping
{
	/** The text field in which the user inputs his string in */
	private final TextField textField;
	
	/** The Yes / No buttons */
	private final Button okButton, cancelButton;
	
	/** The helper label along with the error/warning label */
	private final Label instructionLabel, errorLabel;
	
	/** The array of string the user isn't allowed to enter */
	private String[] prohibitedStrings = null;
	
	/** The text to display when the user enters a prohibited string */
	private String errorProhibitedText = "";
	
	/** The text to display when the user enters a short string */
	private String errorShortText = "";
	
	/** The text to display when the user enters a long string */
	private String errorLongText = "";
	
	/** The string input of the user */
	private String response = "";
	
	/** The minimum string length allowed for the text input */
	private int minTextLength = 0;
	
	/** The maximum string length allowed for the text input */
	private int maxTextLength = 16;
	
	/**
	 * Creates a new text input popup with the specified parameters.
	 * @param windowTitle	the popup's window's title
	 */
	public TextInputPopup(String windowTitle)
	{
		super();
		
		DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
		
		// Setup text field
		textField = new TextField();
		textField.setEffect(shadow);
		textField.setMinWidth(237);
		textField.setMinHeight(25);
		textField.relocate(194, 30);
		textField.textProperty().addListener((args, oldText, newText) -> onTextFieldTextChanged(newText));
		
		// Setup ok button
		okButton = new Button();
		okButton.relocate(100, 90);
		okButton.setMinWidth(80);
		okButton.setMinHeight(25);
		okButton.setCursor(Cursor.HAND);
		okButton.setOnAction(e -> onOkButtonClicked());
		
		// Setup cancel button
		cancelButton = new Button();
		cancelButton.relocate(237, 90);
		cancelButton.setMinWidth(80);
		cancelButton.setMinHeight(25);
		cancelButton.setCursor(Cursor.HAND);
		cancelButton.setOnAction(e -> onCancelButtonClicked());
		
		// Setup instruction label
		instructionLabel = new Label();
		instructionLabel.setEffect(shadow);
		instructionLabel.setStyle("-fx-font-size: 16");
		instructionLabel.relocate(43, 30);
		
		// Setup error label
		errorLabel = new Label("");
		errorLabel.setEffect(shadow);
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12; -fx-font-style: italic");
		errorLabel.relocate(198, 61);
		
		// Setup key binds
		initializeKeyMap(scene);

		// Setup stage
		stage.setTitle(windowTitle);
		
		// Setup root pane
		root.setMinWidth(473);
		root.setMinHeight(144);
		root.getChildren().addAll(textField, okButton, cancelButton, instructionLabel, errorLabel);
	}
	
	/**
	 * Customizes the text field with the specified parameters.
	 * @param defaultText	the starting default text inside the input field
	 * @param promptText	the helper text to display inside the input field when it is empty
	 * @param prohibitedStrings		the array of strings the user is prohibited to enter
	 * @param minTextLength		the minimum amount of characters allowed for the input field
	 * @param maxTextLength		the maximum amount of characters allowed for the input field
	 * @param errorShortText	the error message to display when the user enters a short string
	 * @param errorLongText		the error message to display when the user enters a long string
	 * @param errorProhibitedText	the error message to display when the user enters a prohibited string
	 */
	public void setupTextField(String defaultText, String promptText, String[] prohibitedStrings, int minTextLength,
			int maxTextLength, String errorShortText, String errorLongText, String errorProhibitedText)
	{
		this.prohibitedStrings = prohibitedStrings;
		this.minTextLength = minTextLength;
		this.maxTextLength = maxTextLength;
		this.errorShortText = errorShortText;
		this.errorLongText = errorLongText;
		this.errorProhibitedText = errorProhibitedText;
		
		// Setup text field
		textField.setText(defaultText);
		textField.setPromptText(promptText);
		textField.requestFocus();
		textField.selectAll();
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
	public void setupInstructionLabel(String labelText)
	{
		instructionLabel.setText(labelText);
	}

	/**
	 * Called when the text of the input field changes.
	 * @param newText	the new text of the input field
	 */
	private void onTextFieldTextChanged(String newText)
	{
		// // If the new text is a prohibited text, disable the Ok button and display the error label
		// Otherwise, enable the okay button and hide the error label
		
		// Text is short, disable ok button, display error label
		if(newText.length() < minTextLength)
		{
			okButton.setDisable(true);
			errorLabel.setText(errorShortText);
		}
		// Text is long, disable ok button, display error label
		else if(newText.length() > maxTextLength)
		{
			okButton.setDisable(true);
			errorLabel.setText(errorLongText);
		}
		// Has valid-length text entered, but check if invalid or valid string
		else
		{
			boolean isProhibitedText = false;
			
			if(prohibitedStrings != null)
			{
				for(String s : prohibitedStrings)
				{
					if(s.matches(newText))
					{
						isProhibitedText = true;
						break;
					}
				}
			}
			
			// Entered prohibited text,
			// Disable ok button and display error
			if(isProhibitedText)
			{
				okButton.setDisable(true);
				errorLabel.setText(errorProhibitedText);
			}
			// Entered valid text
			// Enable ok button and hide error
			else
			{
				okButton.setDisable(false);
				errorLabel.setText("");
			}
		}
	}

	/** Called when the NO button is clicked */
	private void onCancelButtonClicked()
	{
		response = "";
		stage.close();
	}

	/** Called when the OK button is clicked */
	private void onOkButtonClicked()
	{
		response = textField.getText();
		stage.close();
	}

	/**
	 * @return the input field response of the user 
	 */
	public String getResponse()
	{
		return response;
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
				if((focusedNode == okButton) || (focusedNode == textField))
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
