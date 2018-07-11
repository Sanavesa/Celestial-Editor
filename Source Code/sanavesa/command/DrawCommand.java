package sanavesa.command;

import sanavesa.source.Frame;
import sanavesa.source.Layer;
import sanavesa.source.Pixel;

public class DrawCommand extends Commands
{
	private final int pixelX, pixelY;
	private final Layer pixelLayer;
	private final double pixelBrightnessFactor;
	
	public DrawCommand(Frame affectedFrame, Pixel pixel)
	{
		super(affectedFrame);
		pixelX = pixel.getX();
		pixelY = pixel.getY();
		pixelLayer = pixel.getLayer();
		pixelBrightnessFactor = pixel.getBrightnessFactor();
	}

	public int getPixelX()
	{
		return pixelX;
	}

	public int getPixelY()
	{
		return pixelY;
	}

	public Layer getPixelLayer()
	{
		return pixelLayer;
	}

	public double getPixelBrightnessFactor()
	{
		return pixelBrightnessFactor;
	}

	@Override
	public void undo()
	{
		// Attempt to find the pixel
		Pixel pixel = affectedFrame.findPixel(p ->
			(p.getX() == pixelX) && 
			(p.getY() == pixelY) && 
			(p.getBrightnessFactor() == pixelBrightnessFactor) &&
			(p.getLayer() == pixelLayer));
		
		affectedFrame.removePixel(pixel);
	}
}
