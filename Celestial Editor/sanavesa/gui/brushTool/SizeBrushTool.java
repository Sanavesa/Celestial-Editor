/***************************************************************************************************************************
 * Class:		SizeBrushTool.java
 * Author:		Mohammad Alali
 * 
 * Description: This class handles the user interaction between mouse input and changing the brush size.
 * 	
 * Attributes: 	
 * 				int brushSize
 * 		
 * Methods:		
 * 				void onMousePressedOnTool
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.brushTool;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

/**
 * This class handles the user interaction between mouse input and changing the brush size.
 * @author Mohammad Alali
 */
public class SizeBrushTool extends BrushTool
{
	public final int brushSize;
	
	public SizeBrushTool(Image image, BrushToolManager brushToolManager,  BrushType brushType, int brushSize, int keybind)
	{
		// Setup the size brush tool
		super(image, brushToolManager, brushType);
		this.brushSize = brushSize;
		label.setTooltip(new Tooltip("[" + keybind + "] Brush Size.\nAffects pencil and eraser only."));
	}
	
	@Override
	// Change the brush size on press
	public void onMousePressedOnTool(MouseEvent e)
	{
		super.onMousePressedOnTool(e);
		brushToolManager.setBrushSize(brushSize);
	}
}
