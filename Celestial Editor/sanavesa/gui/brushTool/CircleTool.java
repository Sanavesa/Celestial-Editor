package sanavesa.gui.brushTool;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import sanavesa.command.DrawCommand;
import sanavesa.command.MultiCommand;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.util.MathUtil;

public class CircleTool extends BrushTool
{
	private double startX = 0, startY = 0;
	private double endX = 0, endY = 0;
	
	public CircleTool(Image image, BrushToolManager brushToolManager, BrushType brushType)
	{
		super(image, brushToolManager, brushType);
		label.setTooltip(new Tooltip("[C] Circle tool.\nBrush size doesn't affect.\nLMB if selected."));
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
		endX = snappedMouseX;
		endY = snappedMouseY;
		
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		// Draw a display
		double radius = Math.round(Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)));
		
		double x = radius;
	    double y = 0;
	    double err = 0;

	    while (x >= y)
	    {
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX + x, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY + y, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX + y, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY + x, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX - y, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY + x, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX - x, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY + y, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX - x, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY - y, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX - y, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY - x, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX + y, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY - x, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());
	    	
	    	canvas.drawOnTemporaryCanvas(
	        		MathUtil.snapValue(snappedMouseX + x, canvas.getCanvasToFrameScaleX()),
	        		MathUtil.snapValue(snappedMouseY - y, canvas.getCanvasToFrameScaleY()),
	        		canvas.getCanvasToFrameScaleX(),
	        		canvas.getCanvasToFrameScaleY());

	        if (err <= 0)
	        {
	            y += 1;
	            err += 2*y + 1;
	        }
	        if (err > 0)
	        {
	            x -= 1;
	            err -= 2*x + 1;
	        }
	    }
		
	}
	
	@Override
	public void onMouseReleasedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		endX = snappedMouseX;
		endY = snappedMouseY;
		
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		// Used for undo'ing
		MultiCommand<DrawCommand> multiCommands = new MultiCommand<DrawCommand>(canvas.getFrameDisplay().getSelectedFrame());
		
		double radius = Math.round(Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)));
		
		double x = radius;
	    double y = 0;
	    double err = 0;

	    while (x >= y)
	    {
	    	DrawCommand cmd;
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX + x, snappedMouseY + y));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX + y, snappedMouseY + x));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX - y, snappedMouseY + x));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX - x, snappedMouseY + y));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX - x, snappedMouseY - y));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX - y, snappedMouseY - x));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX + y, snappedMouseY - x));
	        multiCommands.getCommands().add(cmd);
	        
	        cmd = new DrawCommand(canvas.getFrameDisplay().getSelectedFrame(), canvas.draw(snappedMouseX + x, snappedMouseY - y));
	        multiCommands.getCommands().add(cmd);

	        if (err <= 0)
	        {
	            y += 1;
	            err += 2*y + 1;
	        }
	        if (err > 0)
	        {
	            x -= 1;
	            err -= 2*x + 1;
	        }
	    }
	    
	    canvas.getFrameDisplay().getSelectedFrame().getCommands().add(multiCommands);
	}
}
