/***************************************************************************************************************************
 * Interface:	IGraphicalInterface.java
 * Author:		Mohammad Alali
 * 
 * Description: This interface is required by all base GUI classes for them to be displayed on the screen with ease.
 * 	
 * Attributes: 	N/A
 * 		
 * Methods:		
 * 				void initializeLayout()
 * 				Pane getRoot()	
 * 
 ***************************************************************************************************************************/
package sanavesa.gui;

import javafx.scene.layout.Pane;

/**
 * This interface is required by all base GUI classes for them to be displayed on the screen with ease.
 * @author Mohammad Alali
 */
public interface IGraphicalInterface
{
	/** Called once in the initialization stage of the program to setup the GUI layout of a class. */
	public void initializeLayout();
	
	/**
	 * @return the root node of the graphical interface
	 */
	default public Pane getRoot() { return null; }
}
