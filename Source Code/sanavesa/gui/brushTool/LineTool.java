package sanavesa.gui.brushTool;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import sanavesa.command.DrawCommand;
import sanavesa.command.MultiCommand;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.source.Pixel;
import sanavesa.util.MathUtil;

public class LineTool extends BrushTool
{
	private double startX = 0, startY = 0;
	private double endX = 0, endY = 0;
	
	
	public LineTool(Image image, BrushToolManager brushToolManager, BrushType brushType)
	{
		super(image, brushToolManager, brushType);
		label.setTooltip(new Tooltip("[L] Line tool.\nBrush size doesn't affect.\nLMB if selected."));
	}
	
	@Override
	public void onMousePressedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		startX = snappedMouseX;
		startY = snappedMouseY;
		
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
	}
	
	@Override
	public void onMouseDraggedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		endX = snappedMouseX;
		endY = snappedMouseY;
		
		// draw the final result on temp canvas
		
		int distance = (int) Math.round(Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)));
		double angle = Math.atan2(endY - startY, endX - startX);
		
		for(int k = 0; k <= distance; k += 2)
		{
			double x = startX + Math.cos(angle) * k;
			double y = startY + Math.sin(angle) * k;
			
			canvas.drawOnTemporaryCanvas(
					MathUtil.snapValue(x, canvas.getCanvasToFrameScaleX()),
					MathUtil.snapValue(y, canvas.getCanvasToFrameScaleY()),
					canvas.getCanvasToFrameScaleX(),
					canvas.getCanvasToFrameScaleY());
		}
	}
	
	@Override
	public void onMouseReleasedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		endX = snappedMouseX;
		endY = snappedMouseY;
		
		// Used for undo'ing
		MultiCommand<DrawCommand> multiCommands = new MultiCommand<DrawCommand>(canvas.getFrameDisplay().getSelectedFrame());
		
		double x0, y0, x1, y1;
		if(startX == endX)
		{
			// No slope
			// Draw vertical line
			for(double m = Math.min(startY, endY); m < Math.max(startY, endY); m++)
			{
				Pixel p = canvas.draw(startX, m);
				DrawCommand cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), p);
				multiCommands.getCommands().add(cmd);
			}
			
			return;
		}
		else if(Math.min(startX, endX) == startX)
		{
			// positive slope
			x0 = startX;
			y0 = startY;
			x1 = endX;
			y1 = endY;
		}
		else
		{
			// Negative slope
			x0 = endX;
			y0 = endY;
			x1 = startX;
			y1 = startY;
		}
		
		double dx = x1 - x0;
		double dy = y1 - y0;
		double distance = Math.sqrt(dx * dx + dy * dy);
		
		double y = y0;
		for(double x = x0; x < x1; x+=dx/distance)
		{
			Pixel p = canvas.draw(x, y);
			DrawCommand cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), p);
			multiCommands.getCommands().add(cmd);
			y += dy / distance;
		}
		
		canvas.getFrameDisplay().getSelectedFrame().getCommands().add(multiCommands);
	}
}