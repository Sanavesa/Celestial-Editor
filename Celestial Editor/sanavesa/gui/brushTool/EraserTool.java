package sanavesa.gui.brushTool;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import sanavesa.command.EraseCommand;
import sanavesa.command.MultiCommand;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.source.Pixel;

public class EraserTool extends BrushTool
{
	
	public EraserTool(Image image, BrushToolManager brushToolManager, BrushType brushType)
	{
		super(image, brushToolManager, brushType);
		label.setTooltip(new Tooltip("[RMB] Eraser tool.\nBrush size does affect.\nCannot be changed from RMB."));
	}
	
	@Override
	public void onMousePressedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		// Used for undo'ing
		MultiCommand<EraseCommand> multiCommands = new MultiCommand<EraseCommand>(canvas.getFrameDisplay().getSelectedFrame());
		
		double size = brushToolManager.getBrushSize();
		for(double i = -size/2; i < size/2; i++)
		{
			for(double j = -size/2; j < size/2; j++)
			{
				Pixel p = canvas.erase(snappedMouseX + i * canvas.getCanvasToFrameScaleX(), snappedMouseY + j *canvas.getCanvasToFrameScaleY());
				
				if(p != null)
				{
					EraseCommand cmd = new EraseCommand(canvas.getFrameDisplay().getSelectedFrame(), p);
					multiCommands.getCommands().add(cmd);
				}
			}
		}
		
		canvas.getFrameDisplay().getSelectedFrame().getCommands().add(multiCommands);
	}
	
	@Override
	public void onMouseDraggedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		onMousePressedOnCanvas(canvas, e, snappedMouseX, snappedMouseY);
	}
}
