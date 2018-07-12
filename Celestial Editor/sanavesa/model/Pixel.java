package sanavesa.model;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;
import sanavesa.model.versionControl.ICommand;
import sanavesa.model.versionControl.VersionControl;

public final class Pixel
{
	@Override
	public String toString()
	{
		return "(" + getX() + "," + getY() + ")";
	}

	private final ReadOnlyIntegerWrapper x;
	private final ReadOnlyIntegerWrapper y;
	private final ReadOnlyObjectWrapper<Layer> layer;
	private final ReadOnlyDoubleWrapper brightnessFactor;

	public Pixel(final int x, final int y, final Layer layer, final double brightnessFactor)
	{
		if (layer == null)
			throw new IllegalArgumentException("layer cannot be null");

		this.x = new ReadOnlyIntegerWrapper(x);
		this.y = new ReadOnlyIntegerWrapper(y);
		this.layer = new ReadOnlyObjectWrapper<>(layer);
		this.brightnessFactor = new ReadOnlyDoubleWrapper(brightnessFactor);
	}

	public final int getX()
	{
		return x.get();
	}

	public final void setX(final int newX)
	{
		setX(newX, true);
	}

	public final void setX(final int newX, final boolean isUndoable)
	{
		if (isUndoable)
		{
			PixelXCommand command = new PixelXCommand(this, newX);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			x.set(newX);
		}
	}

	public final ReadOnlyIntegerProperty xProperty()
	{
		return x.getReadOnlyProperty();
	}

	public final int getY()
	{
		return y.get();
	}

	public final void setY(final int newY)
	{
		setY(newY, true);
	}

	public final void setY(final int newY, final boolean isUndoable)
	{
		if (isUndoable)
		{
			PixelYCommand command = new PixelYCommand(this, newY);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			y.set(newY);
		}
	}

	public final ReadOnlyIntegerProperty yProperty()
	{
		return y.getReadOnlyProperty();
	}

	public final Layer getLayer()
	{
		return layer.get();
	}

	public final void setLayer(final Layer newLayer)
	{
		setLayer(newLayer, true);
	}

	public final void setLayer(final Layer newLayer, final boolean isUndoable)
	{
		if (newLayer == null)
			throw new IllegalArgumentException("newLayer cannot be null");

		if (isUndoable)
		{
			PixelLayerCommand command = new PixelLayerCommand(this, newLayer);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			layer.set(newLayer);
		}
	}

	public final ReadOnlyObjectProperty<Layer> layerProperty()
	{
		return layer.getReadOnlyProperty();
	}

	public final double getBrightnessFactor()
	{
		return brightnessFactor.get();
	}

	public final void setBrightnessFactor(final double newBrightnessFactor)
	{
		setBrightnessFactor(newBrightnessFactor, true);
	}

	public final void setBrightnessFactor(final double newBrightnessFactor, final boolean isUndoable)
	{
		if (isUndoable)
		{
			PixelBrightnessFactorCommand command = new PixelBrightnessFactorCommand(this, newBrightnessFactor);
			VersionControl.getInstance().executeCommand(command);
		}
		else
		{
			brightnessFactor.set(newBrightnessFactor);
		}
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
		if (brightness >= 0)
		{
			// New_Saturation = Saturation * (1 - lightnessGUIValue)
			// New_Brightness = Brightness + lightnessGUIValue * (1 -
			// Brightness)
			computedColor = Color.hsb(layerColor.getHue(), layerColor.getSaturation() * (1 - brightness),
					layerColor.getBrightness() + brightness * (1 - layerColor.getBrightness()),
					layerColor.getOpacity());
		}
		// Make darker
		else
		{
			// New_Brightness = Brightness * (1 + lightnessGUIValue)
			computedColor = Color.hsb(layerColor.getHue(), layerColor.getSaturation(),
					layerColor.getBrightness() * (1 + brightness), layerColor.getOpacity());
		}

		return computedColor;
	}

	private final class PixelXCommand implements ICommand
	{
		private final Pixel pixel;
		private final int newX;
		private int oldX;

		public PixelXCommand(final Pixel pixel, final int newX)
		{
			this.pixel = pixel;
			this.newX = newX;
		}

		@Override
		public final void execute()
		{
			oldX = pixel.getX();
			pixel.setX(newX, false);
		}

		@Override
		public final void undo()
		{
			pixel.setX(oldX, false);
		}
	}

	private final class PixelYCommand implements ICommand
	{
		private final Pixel pixel;
		private final int newY;
		private int oldY;

		public PixelYCommand(final Pixel pixel, final int newY)
		{
			this.pixel = pixel;
			this.newY = newY;
		}

		@Override
		public final void execute()
		{
			oldY = pixel.getY();
			pixel.setY(newY, false);
		}

		@Override
		public final void undo()
		{
			pixel.setY(oldY, false);
		}
	}

	private final class PixelLayerCommand implements ICommand
	{
		private final Pixel pixel;
		private final Layer newLayer;
		private Layer oldLayer;

		public PixelLayerCommand(final Pixel pixel, final Layer newLayer)
		{
			this.pixel = pixel;
			this.newLayer = newLayer;
		}

		@Override
		public final void execute()
		{
			oldLayer = pixel.getLayer();
			pixel.setLayer(newLayer, false);
		}

		@Override
		public final void undo()
		{
			pixel.setLayer(oldLayer, false);
		}
	}

	private final class PixelBrightnessFactorCommand implements ICommand
	{
		private final Pixel pixel;
		private final double newBrightnessFactor;
		private double oldBrightnessFactor;

		public PixelBrightnessFactorCommand(final Pixel pixel, final double newBrightnessFactor)
		{
			this.pixel = pixel;
			this.newBrightnessFactor = newBrightnessFactor;
		}

		@Override
		public final void execute()
		{
			oldBrightnessFactor = pixel.getBrightnessFactor();
			pixel.setBrightnessFactor(newBrightnessFactor, false);
		}

		@Override
		public final void undo()
		{
			pixel.setBrightnessFactor(oldBrightnessFactor, false);
		}
	}
}