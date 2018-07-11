/***************************************************************************************************************************
 * Interface:	IKeyMapping.java
 * Author:		Mohammad Alali
 * 
 * Description: This interface enables the inheriting class to handle events when specific keys are pressed; enables ease 
 * 				of configuration for Key Binding.
 * 	
 * Attributes: 	N/A
 * 		
 * Methods:		
 * 				void initializeKeyMap(Scene scene)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui;

import javafx.scene.Scene;

/**
 * This interface enables the inheriting class to handle events
 * when specific keys are pressed; enables ease of configuration
 * for Key Binding.
 * @author Mohammad Alali
 */
public interface IKeyMapping
{
	/**
	 * Initializes the listening of events for the keyboard shortcuts.
	 * @param scene the main scene of the application
	 */
	public void initializeKeyMap(Scene scene);
}
