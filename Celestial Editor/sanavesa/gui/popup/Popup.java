/***************************************************************************************************************************
 * Class:		Popup.java
 * Author:		Mohammad Alali
 * 
 * Description:	This class is a base class for all popups to extend. It allows for easier customization and creation of popups.
 * 	
 * Attributes: 	
 * 				Stage stage
 * 				Scene scene
 * 				Pane root
 * 		
 * Methods:		
 * 				void show()
 * 
 ***************************************************************************************************************************/
package sanavesa.gui.popup;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class is a base class for all popups to extend.
 * It allows for easier customization and creation of popups. 
 * @author Mohammad Alali
 */
public abstract class Popup
{
	/** The stage of the popup */
	protected final Stage stage;
	
	/** The scene of the popup */
	protected final Scene scene;
	
	/** The root layout node of the popup */
	protected final Pane root;
	
	/**
	 * Initializes the popup. Must be called in subclasses!
	 */
	public Popup()
	{
		root = new Pane();
		scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.centerOnScreen();
		stage.setResizable(false);
	}
	
	/** Displays the popup window */
	public void show()
	{
		stage.showAndWait();
	}
	
	/**
	 * @return the root node of the popup
	 */
	public final Pane getRoot()
	{
		return root;
	}
	
	/**
	 * @return the stage of the popup
	 */
	public final  Stage getStage()
	{
		return stage;
	}
	
	/**
	 * @return the scene of the popup
	 */
	public final Scene getScene()
	{
		return scene;
	}
	
}
