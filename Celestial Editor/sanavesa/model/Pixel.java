package sanavesa.model;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;

public final class Pixel
{
	private final ReadOnlyIntegerWrapper x;
	private final ReadOnlyIntegerWrapper y;
	private final ReadOnlyObjectWrapper<Layer> layer;
	private final ReadOnlyDoubleWrapper brightnessFactor;
	
	public Pixel(int x, int y, Layer layer, double brightnessFactor)
	{
		this.x = new ReadOnlyIntegerWrapper(x);
		this.y = new ReadOnlyIntegerWrapper(x);
		this.layer = new ReadOnlyObjectWrapper<>(layer);
		this.brightnessFactor = new ReadOnlyDoubleWrapper(brightnessFactor);
	}
	
	public final int getX()
	{
		return x.get();
	}
	
	public final void setX(int newX)
	{
		x.set(newX);
	}

	public final ReadOnlyIntegerProperty xProperty()
	{
		return x.getReadOnlyProperty();
	}
	
	public final int getY()
	{
		return y.get();
	}
	
	public final void setY(int newY)
	{
		y.set(newY);
	}

	public final ReadOnlyIntegerProperty yProperty()
	{
		return y.getReadOnlyProperty();
	}
	
	public final Layer getLayer()
	{
		return layer.get();
	}
	
	public final void setLayer(Layer newLayer)
	{
		layer.set(newLayer);
	}

	public final ReadOnlyObjectProperty<Layer> layerProperty()
	{
		return layer.getReadOnlyProperty();
	}
	
	public final double getBrightnessFactor()
	{
		return brightnessFactor.get();
	}
	
	public final void setBrightnessFactor(double newBrightnessFactor)
	{
		brightnessFactor.set(newBrightnessFactor);
	}

	public final ReadOnlyDoubleProperty brightnessFactorProperty()
	{
		return brightnessFactor.getReadOnlyProperty();
	}
	
	public final Color computeColor()
	{
		Color computedColor;
		Color layerColor = getLayer().getColor();
		double brightness = getBrightnessFactor();
		
		// The lightnessFactor [-1, 1].
		// Make lighter
		if(brightness >= 0)
		{
			// New_Saturation = Saturation * (1 - lightnessGUIValue)
			// New_Brightness = Brightness + lightnessGUIValue * (1 - Brightness)
			computedColor = Color.hsb(
					layerColor.getHue(),
					layerColor.getSaturation() * (1 - brightness),
					layerColor.getBrightness() + brightness * (1 - layerColor.getBrightness()),
					layerColor.getOpacity());
		}
		// Make darker
		else
		{
			// New_Brightness = Brightness * (1 + lightnessGUIValue)
			computedColor = Color.hsb(
					layerColor.getHue(),
					layerColor.getSaturation(),
					layerColor.getBrightness() * (1 + brightness),
					layerColor.getOpacity());
		}
		
		return computedColor;
	}
}