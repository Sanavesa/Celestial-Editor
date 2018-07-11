/***************************************************************************************************************************
 * Class:		MessagePopup.java
 * Author:		Mohammad Alali
 * 
 * Description:	A subclass of Popup. This class represents a message display popup that returns no value.
 * 				This class implements the IKeyMapping interface, which allows specific keys to be mapped to certain actions.
 * 	
 * Attributes: 	
 * 				Stage stage
 * 				Scene scene
 * 				Pane root
 * 		
 * Methods:		
 * 				void setupOkButton(String)
 * 				void setupCancelButton(String)
 * 				void setupInstructionLabel(String)
 * 
 ***************************************************************************************************************************/
package sanavesa.gui.popup;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sanavesa.gui.IKeyMapping;

/**
 * A subclass of {@link Popup}. This class represents a message
 * popup that returns no value.
 * <p>
 * This class implements the {@link IKeyMapping} interface, which
 * allows specific keys to be mapped to certain actions.
 * </p>
 * @author Mohammad Alali
 */
public class MessagePopup extends Popup implements IKeyMapping
{
	/** The button used in this popup */
	private final Button okButton;
	
	/** The label displaying the message */
	private final Label displayLabel;
	
	/**
	 * Creates a new message popup with the specified parameters.
	 * @param windowTitle	the popup's window's title
	 */
	public MessagePopup(String windowTitle)
	{
		super();
		
		DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
		
		// Setup ok button
		okButton = new Button();
		okButton.relocate(30, 80);
		okButton.setMinWidth(80);
		okButton.setMinHeight(25);
		okButton.setCursor(Cursor.HAND);
		okButton.setOnAction(e -> onOkButtonClicked());
		
		// Setup display label
		displayLabel = new Label();
		displayLabel.setEffect(shadow);
		displayLabel.setStyle("-fx-font-size: 16");
		displayLabel.relocate(80, 30);
		
		// Center the display text and button, 
		// using runLater because getWidth returns 0 in the instance it is created
		Platform.runLater(() ->
		{
			displayLabel.setLayoutX(scene.getWidth() / 2 - displayLabel.getWidth() / 2);
			okButton.setLayoutX(scene.getWidth() / 2 - okButton.getWidth() / 2);
		});
		
		// Setup keyboard shortcuts
		initializeKeyMap(scene);

		// Setup stage
		stage.setTitle(windowTitle);
		
		// Setup root pane
		root.setMinWidth(250);
		root.setMinHeight(130);
		root.getChildren().addAll(okButton, displayLabel);
	}
	
	/** Displays the popup window */
	@Override
	public void show()
	{
		stage.show();
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
	 * Customizes the display label in the popup.
	 * @param labelText		the text to display for the message
	 */
	public void setupDisplayLabel(String labelText)
	{
		displayLabel.setText(labelText);
	}

	/** Called when the [OK] button is clicked */
	private void onOkButtonClicked()
	{
		stage.close();
	}
	
	/**
	 * Sets up the key bindings.
	 * The [ENTER] or [ESC] key should fire the OK's button action, which closes the popup
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.setOnKeyPressed(e ->
		{
			if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE)
			{
				okButton.fire();
			}
		});
	}

}
