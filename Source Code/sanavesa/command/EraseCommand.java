package sanavesa.command;

import sanavesa.source.Frame;
import sanavesa.source.Layer;
import sanavesa.source.Pixel;

public class EraseCommand extends Commands
{
	private final int pixelX, pixelY;
	private final Layer pixelLayer;
	private final double pixelBrightnessFactor;
	
	public EraseCommand(Frame affectedFrame, Pixel pixel)
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
		Pixel pixel = new Pixel(pixelX, pixelY, pixelBrightnessFactor, pixelLayer);
		affectedFrame.addPixel(pixel);
	}
}
