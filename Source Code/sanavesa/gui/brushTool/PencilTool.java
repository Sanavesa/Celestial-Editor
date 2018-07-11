package sanavesa.gui.brushTool;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import sanavesa.command.DrawCommand;
import sanavesa.command.MultiCommand;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.source.Pixel;
import sanavesa.util.MathUtil;

public class PencilTool extends BrushTool
{
	
	public PencilTool(Image image, BrushToolManager brushToolManager,  BrushType brushType)
	{
		super(image, brushToolManager, brushType);
		label.setTooltip(new Tooltip("[D] Pencil tool.\nBrush size does affect.\nLMB if selected."));
	}
	
	@Override
	public void onMouseMovedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		double size = brushToolManager.getBrushSize();
		canvas.drawOnTemporaryCanvas(
				MathUtil.snapValue(snappedMouseX - size/2 * canvas.getCanvasToFrameScaleX(), canvas.getCanvasToFrameScaleX()),
				MathUtil.snapValue(snappedMouseY - size/2 * canvas.getCanvasToFrameScaleX(), canvas.getCanvasToFrameScaleY()), 
				brushToolManager.getBrushSize() * canvas.getCanvasToFrameScaleX(),
				brushToolManager.getBrushSize() *canvas.getCanvasToFrameScaleY());
	}
	
	@Override
	public void onMouseExitedCanvas(PixelatedCanvas canvas, MouseEvent e)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
	}
	
	@Override
	public void onMousePressedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		// Used for undo'ing
		MultiCommand<DrawCommand> multiCommands = new MultiCommand<DrawCommand>(canvas.getFrameDisplay().getSelectedFrame());
		
		double size = brushToolManager.getBrushSize();
		for(double i = -size/2; i < size/2; i++)
		{
			for(double j = -size/2; j < size/2; j++)
			{
				Pixel p = canvas.draw(snappedMouseX + i * canvas.getCanvasToFrameScaleX(), snappedMouseY + j *canvas.getCanvasToFrameScaleY());
				DrawCommand cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), p);
				multiCommands.getCommands().add(cmd);
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
