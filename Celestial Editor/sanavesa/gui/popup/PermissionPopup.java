/***************************************************************************************************************************
 * Class:		PermissionPopup.java
 * Author:		Mohammad Alali
 * 
 * Description:	A subclass of Popup. This class represents a [Yes / No] permission popup that returns a boolean value.
 * 				This class implements the IKeyMapping interface, which allows specific keys to be mapped to certain actions.
 * 	
 * Attributes: 	
 * 				Nothing Interesting
 * 		
 * Methods:		
 * 				void setupOkButton(String)
 * 				void setupCancelButton(String)
 * 				void setupInstructionLabel(String)
 * 				boolean getResponse()
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.popup;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sanavesa.gui.IKeyMapping;

/**
 * A subclass of {@link Popup}. This class represents a [Yes / No] 
 * permission popup that returns a boolean value.
 * <p>
 * This class implements the {@link IKeyMapping} interface, which
 * allows specific keys to be mapped to certain actions.
 * </p>
 * @author Mohammad Alali
 */
public class PermissionPopup extends Popup implements IKeyMapping
{
	/** The Buttons used in this popup. [Yes / No] */
	private final Button okButton, cancelButton;
	
	/** The label displaying the question */
	private final Label displayLabel;
	
	/** The response answer of the user whether he clicked yes or no */
	private boolean response = false;
	
	/**
	 * Creates a new permission popup with the specified parameters.
	 * @param windowTitle	the popup's window's title
	 */
	public PermissionPopup(String windowTitle)
	{
		super();
		
		DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
		
		// Setup ok button
		okButton = new Button();
		okButton.relocate(35, 80);
		okButton.setMinWidth(80);
		okButton.setMinHeight(25);
		okButton.setCursor(Cursor.HAND);
		okButton.setOnAction(e -> onOkButtonClicked());
		
		// Setup cancel button
		cancelButton = new Button();
		cancelButton.relocate(140, 80);
		cancelButton.setMinWidth(80);
		cancelButton.setMinHeight(25);
		cancelButton.setCursor(Cursor.HAND);
		cancelButton.setOnAction(e -> onCancelButtonClicked());
		
		// Setup display label
		displayLabel = new Label();
		displayLabel.setEffect(shadow);
		displayLabel.setStyle("-fx-font-size: 16");
		displayLabel.relocate(80, 30);
		
		// Center the display text, using runLayer because getWidth returns 0 in the instance it is created
		Platform.runLater(() ->
		{
			displayLabel.setLayoutX(scene.getWidth() / 2 - displayLabel.getWidth() / 2);
		});
		
		// Setup keyboard shortcuts
		initializeKeyMap(scene);

		// Setup stage
		stage.setTitle(windowTitle);
		
		// Setup root pane
		root.setMinWidth(250);
		root.setMinHeight(130);
		root.getChildren().addAll(okButton, cancelButton, displayLabel);
	}
	
	/**
	 * Customizes the [OK] Button in the popup.
	 * @param buttonText	the text to display for the [OK] button.
	 */
	public void setupOkButton(String buttonText)
	{
		okButton.setText(buttonText);
	}
	
	/**
	 * Customizes the [NO] Button in the popup.
	 * @param buttonText	the text to display for the [NO] button.
	 */
	public void setupCancelButton(String buttonText)
	{
		cancelButton.setText(buttonText);
	}
	
	/**
	 * Customizes the display label in the popup.
	 * @param labelText		the text to display for the question
	 */
	public void setupDisplayLabel(String labelText)
	{
		displayLabel.setText(labelText);
	}

	/** Called when the [NO] button is clicked */
	private void onCancelButtonClicked()
	{
		response = false;
		stage.close();
	}

	/** Called when the [OK] button is clicked */
	private void onOkButtonClicked()
	{
		response = true;
		stage.close();
	}
	
	/**
	 * @return the response of the user. true = pressed [OK], false = pressed [No]
	 */
	public boolean getResponse()
	{
		return response;
	}

	/**
	 * Sets up the key bindings.
	 * The [ENTER] key should fire the selected button's action, 
	 * whereas the [ESC] key should close the popup and set response=false. 
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.setOnKeyPressed(e ->
		{
			if(e.getCode() == KeyCode.ENTER)
			{
				Node focusedNode = scene.getFocusOwner();
				if(focusedNode == okButton)
					okButton.fire();
				else if(focusedNode == cancelButton)
					cancelButton.fire();
			}
			else if(e.getCode() == KeyCode.ESCAPE)
				cancelButton.fire();
		});
	}

}
