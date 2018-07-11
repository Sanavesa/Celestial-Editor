package sanavesa.gui.brushTool;

import java.util.List;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.source.Pixel;

public class EyeDropperTool extends BrushTool
{
	
	public EyeDropperTool(Image image, BrushToolManager brushToolManager,  BrushType brushType)
	{
		super(image, brushToolManager, brushType);
		label.setTooltip(new Tooltip("[E] Eye Dropper tool.\nSelects the layer and luminosity factor of the pixel you click on.\nLMB if selected."));
	}
	
	@Override
	public void onMouseMovedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
	}
	
	// DOCUMENT
	@Override
	public void onMousePressedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		// Clear the temporary canvas
		canvas.clearTemporaryCanvas();
		
		int frameX = canvas.convertCanvasXToFrameX(snappedMouseX);
		int frameY = canvas.convertCanvasYToFrameY(snappedMouseY);
		List<Pixel> pixels = canvas.getFrameDisplay().getSelectedFrame().findPixels(p ->
			(p.getX() == frameX) && (p.getY() == frameY));
		
		Pixel chosenPixel = null;
		for(Pixel p : pixels)
		{
			if((chosenPixel == null) || (p.getLayer().getDepth() > chosenPixel.getLayer().getDepth()))
			{
				chosenPixel = p;
			}
		}
		
		
		if(chosenPixel != null)
		{
			canvas.getPalette().setSelectedLayer(chosenPixel.getLayer());
			canvas.getColorSelector().setLuminosityFactor(100 *chosenPixel.getBrightnessFactor());
		}
	}
	
	@Override
	public void onMouseDraggedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY)
	{
		onMousePressedOnCanvas(canvas, e, snappedMouseX, snappedMouseY);
	}
}
