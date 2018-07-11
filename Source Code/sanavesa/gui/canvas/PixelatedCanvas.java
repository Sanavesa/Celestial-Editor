/***************************************************************************************************************************
 * Class:		PixelatedCanvas.java
 * Author:		Mohammad Alali
 * 
 * Description: This class extends the capabilities of the JavaFX Canvas class in a way that makes it easier to use for 
 * 				pixelated images.
 * 	
 * Attributes: 	
 				double xScale
 				double yScale
 * 		
 * Methods:		
 * 				double getLuminosityFactor()
 * 				Color getColorWithFactor()
 * 				void resetView()
 * 				void redrawGridLines()
 * 				void shiftFrame(int, int)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.canvas;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import sanavesa.gui.IKeyMapping;
import sanavesa.gui.colorSelector.ColorSelector;
import sanavesa.gui.frameDisplay.FrameDisplay;
import sanavesa.gui.palette.Palette;
import sanavesa.source.Frame;
import sanavesa.source.Pixel;
import sanavesa.util.MathUtil;

/**
 * This class extends the capabilities of the JavaFX Canvas class in a way that
 * makes it easier to use for pixelated images.
 * @author Mohammad Alali
 */
public class PixelatedCanvas extends Canvas implements IKeyMapping
{
	
	/**
	 * The x-scale between the canvas:frame.
	 * The x-scale is how many horizontal pixels in the canvas equate to a single x-pixel in the frame.
	 */
	double xScale = 0.0;
	
	/** 
	 * The y-scale between the canvas:frame. 
	 * The yscale is how many vertical pixels in the canvas equate to a single y-pixel in the frame.
	 */
	private double yScale = 0.0;
	
	/** The graphics component used to draw onto the canvas */
	private GraphicsContext graphics = getGraphicsContext2D();
	
	/** The Frame Display which is used to retrieve the selected frame */
	private FrameDisplay frameDisplay;
	
	/** The Color Selector which is used to retrieve the selected color */
	private ColorSelector colorSelector;
	
	/** The Palette which is used to retrieve the selected layer */
	private Palette palette;
	
	/** The previous mouse location in canvas coordinates */
	private double mousePrevX = 0, mousePrevY = 0;
	
	/** The scale of the contents of the canvas */
	private DoubleProperty zoomScale = new SimpleDoubleProperty(this, "zoomScale", 1.0);
	
	/** The minimum amount of zoom scale possible, the farthest you can zoom out */
	private static final double minZoomScale = 0.1;
	
	/** The maximum amount of zoom scale possible, the farthest you can zoom in */
	private static final double maxZoomScale = 20.0;
	
	/** The speed of the zooming in or out */
	private double zoomingSpeed = 2.5;
	
	/** The speed of paning to mouse position when zooming in or out */ 
	private double zoomingPanSpeed = 50.0;
	
	/** Whether to display grid lines or not */
	private boolean displayGridLines = true;
	
	/**
	 * The <code>renderTimeline</code> will tick regularly and check the flag, {@link #hasRequestRedraw}, 
	 * and redraw the canvas if it's true. It will then set the flag to false.
	 * <p>
	 * The benefits of this method over immediately redrawing the canvas at every request is that
	 * we only group up multiple calls into a single redraw call, which reduces CPU usage drastically. 
	 */
	private Timeline renderTimeline = new Timeline();
	
	/**
	 * A flag that indicates if the canvas requires a redraw.
	 * The <code>renderTimeline</code> will tick regularly and check this flag, and redraw accordingly.
	 */
	private boolean hasRequestRedraw = false;
	
	/** The background color of the canvas. Note that the opacity field is ignored. */
	private Color canvasClearColor = Color.WHITE;
	
	/** The canvas for the grid lines. It will have the grid lines drawn only when necessary and cache it. */
	private Canvas gridLinesCanvas = null;
	
	/** The cached graphic context for {@link #gridLinesCanvas}. */
	private GraphicsContext gridGraphics = null;
	
	private GraphicsContext tempCanvasGraphics = null;
	
	/** The amount by which the previous frame has its opacity multiplied by */
	private double onionSkinningFactor = 0.5f;
	
	/** Whether onion skinning is toggled on or off */
	private boolean isOnionSkinning = true;
	
	/**
	 * Create a pixelated canvas with the specified size.
	 * @param width		the width in pixels of the canvas
	 * @param height	the height in pixels of the canvas
	 */
	public PixelatedCanvas(int width, int height, Canvas gridLinesCanvas, Canvas temporaryDrawCanvas)
	{
		// Automatically calculate the scale whenever canvas size changes
		widthProperty().addListener((args, oldW, newW) -> calculateScale());
		heightProperty().addListener((args, oldH, newH) -> calculateScale());
		Frame.frameWidthProperty().addListener((args, oldW, newW) -> calculateScale());
		Frame.frameHeightProperty().addListener((args, oldH, newH) -> calculateScale());
				
		// Set the width and height of the canvas
		setWidth(width);
		setHeight(height);
		
		// Set the cursor of the canvas
		setCursor(Cursor.CROSSHAIR);
		
		// Bind the actions to the canvas
		addEventFilter(MouseEvent.MOUSE_PRESSED, e -> onMousePressed(e));
		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> onMouseDragged(e));
		addEventFilter(ScrollEvent.SCROLL, e -> onMouseScroll(e));
		
		renderTimeline.setCycleCount(Timeline.INDEFINITE);
		renderTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), e -> render()));
		renderTimeline.play();
		
		// Bind the canvas scale to the calculated zoom scale
		scaleXProperty().bind(zoomScale);
		scaleYProperty().bind(zoomScale);
		
		// Setup the grid lines canvas, which shadows this canvas
		this.gridLinesCanvas = gridLinesCanvas;
		gridGraphics = gridLinesCanvas.getGraphicsContext2D();
		gridLinesCanvas.translateXProperty().bind(translateXProperty());
		gridLinesCanvas.translateYProperty().bind(translateYProperty());
		gridLinesCanvas.scaleXProperty().bind(scaleXProperty());
		gridLinesCanvas.scaleYProperty().bind(scaleYProperty());
		gridLinesCanvas.setMouseTransparent(true);
		gridLinesCanvas.widthProperty().bind(widthProperty());
		gridLinesCanvas.heightProperty().bind(heightProperty());
		redrawGridLines();
		
		// Setup the draw canvas which is used for temporary drawings and effects
		tempCanvasGraphics = temporaryDrawCanvas.getGraphicsContext2D();
		temporaryDrawCanvas.translateXProperty().bind(translateXProperty());
		temporaryDrawCanvas.translateYProperty().bind(translateYProperty());
		temporaryDrawCanvas.scaleXProperty().bind(scaleXProperty());
		temporaryDrawCanvas.scaleYProperty().bind(scaleYProperty());
		temporaryDrawCanvas.setMouseTransparent(true);
		temporaryDrawCanvas.widthProperty().bind(widthProperty());
		temporaryDrawCanvas.heightProperty().bind(heightProperty());
	}
	
	public boolean isOnionSkinning()
	{
		return isOnionSkinning;
	}

	public void setOnionSkinning(boolean isOnionSkinning)
	{
		this.isOnionSkinning = isOnionSkinning;
		this.requestRedraw();
	}
	
	public double getOnionSkinningFactor()
	{
		return onionSkinningFactor;
	}

	public void setOnionSkinningFactor(double onionSkinningFactor)
	{
		this.onionSkinningFactor = onionSkinningFactor;
		this.requestRedraw();
	}

	/**
	 * Render the canvas if someone requested a redraw.
	 * This method is called at a set inteval by {@link #renderTimeline}.
	 * If no one requested a redraw, nothing will happen.
	 */
	private void render()
	{
		// Redraw the canvas only if requested
		if(hasRequestRedraw)
		{
			redraw(frameDisplay.getSelectedFrame(), isOnionSkinning);
			hasRequestRedraw = false;
		}
	}
	
	/** Sets the frame display */
	public void setFrameDisplay(FrameDisplay frameDisplay)
	{
		this.frameDisplay = frameDisplay;
	}
	
	/** Sets the color selector */
	public void setColorSelector(ColorSelector colorSelector)
	{
		this.colorSelector = colorSelector;
	}
	
	/** Sets the palette */
	public void setPalette(Palette palette)
	{
		this.palette = palette;
	}
	
	/**
	 * This method will zoom in or zoom out, depending on the mouse wheel input.
	 * It will handle all the zooming and scaling math required.
	 * Called whenever the user scrolls his mouse wheel.
	 */
	private void onMouseScroll(ScrollEvent e)
	{
		// Scroll when the CTRL modifier isnt present
		if(e.isControlDown())
			return;
		
		// Calculates the new zooming scale by adding the amount we scrolled
		// (if deltaY is negative from mouse wheel down, we zoom out)
		double newScale = getZoomScale() + zoomingSpeed * (e.getDeltaY() / getHeight());
		double oldScale = getZoomScale();
		setZoomScale(newScale);
		
		// Only pan if we changed our zoom scale
		if(getZoomScale() != oldScale)
		{
			// Calculates the half width and height of the canvas which is used to normalize the mouse coordinates
			double halfWidth = getWidth() / 2;
			double halfHeight = getHeight() / 2;
			
			// Normalizes the mouse position to [-1,-1] to [1,1], with [0,0] being center of canvas
			double zoomX = (e.getX()  - halfWidth) / halfWidth;
			double zoomY = (e.getY() - halfHeight) / halfHeight;
			
			// Calculates the position we shall pan to, which is our position - the amount we want to pan
			// The negative sign is there because we want to invert the panning, pan to left moves us to the right, etc
			// The signum() is there so that zooming in moves towards the position and zooming out moves away from the position
			double newX = getTranslateX() - zoomingPanSpeed * zoomX * Math.signum(e.getDeltaY());
			double newY = getTranslateY() - zoomingPanSpeed * zoomY * Math.signum(e.getDeltaY());
			
			// Pans to the calculated position
			setTranslateX(newX);
			setTranslateY(newY);
		}
	}
	
	/**
	 * @return	the palette used in the program
	 */
	public Palette getPalette()
	{
		return palette;
	}

	/**
	 * Sets the zooming scale of the contents of the canvas.
	 * @param newScale	the scale of the canvas. Note it will be clamped between {@link #minZoomScale} and {@link #maxZoomScale}
	 */
	public void setZoomScale(double newScale)
	{
		newScale = MathUtil.clamp(newScale, minZoomScale, maxZoomScale);
		zoomScale.set(newScale);
	}
	
	/**
	 * @return the zooming scale. Where 1.0 is the original size, > 1.0 is upscaled (zoomed in), and < 1.0 is downscaled (zoomed out). 
	 */
	private double getZoomScale()
	{
		return zoomScale.get();
	}
	
	/** Requests that the canvas redraws its contents in the next tick */
	public void requestRedraw()
	{
		hasRequestRedraw = true;
	}
	
	/**
	 * DOCUMENT
	 * This shall apply the selected tool onto the canvas in the correct position.
	 * Middle Mouse Button (MMB) shall pan around the canvas.
	 * Called whenever the user holds the mouse and moves on the canvas.
	 */
	private void onMouseDragged(MouseEvent e)
	{
		// MMB : Pan Around in the canvas
		if(e.isMiddleButtonDown())
		{
			// Get mouse delta
			double deltaX = e.getX() - mousePrevX;
			double deltaY = e.getY() - mousePrevY;
			
			// Make it so that our pan speed scales up with our scale
			deltaX *= 0.5 * getScaleX(); 
			deltaY *= 0.5 * getScaleY();
			
			// Move to new position
			setTranslateX(getTranslateX() + deltaX);
			setTranslateY(getTranslateY() + deltaY);
		}
		
		// Store our current position as the next frame's previous position
		mousePrevX = e.getX();
		mousePrevY = e.getY();
	}
	
	/**
	 * Called whenever the user presses down on the canvas.
	 */
	private void onMousePressed(MouseEvent e)
	{
		// Assigns the mouse position to the nearest cell position in canvas coordinates
		mousePrevX = e.getX();
		mousePrevY = e.getY();
	}
	
 	/**
	 * Erases a pixel at the specified mouse coordinates in canvas position space.
	 * It will only erase pixels that are in the selected layer and frame.
	 * It will not erase if the frame is invisible.
	 * If no pixels are at the mouse position, nothing shall happen.
	 * @param canvasX	the x mouse position in canvas coordinates
	 * @param canvasY	the y mouse position in canvas coordinates
	 * @returns 		the erased pixel. Null if frame is invisible or cannot find pixel
	 */
	public Pixel erase(double canvasX, double canvasY)
	{
		// Convert the mouse position in canvas-coordinates to frame coordinates
		int frameX = convertCanvasXToFrameX(canvasX);
		int frameY = convertCanvasYToFrameY(canvasY);
		
		// Avoid erasing a pixel from the frame if frame was invisible
		if(!frameDisplay.getSelectedFrame().getVisibility())
			return null;
		
		// Attempt to find a pixel on the same layer at our mouse coordinates
		Pixel pixel = frameDisplay.getSelectedFrame().findPixel(p ->
			p.getX() == frameX && p.getY() == frameY && p.getLayer() == palette.getSelectedLayer());
		
		// If there was a pixel under our mouse, remove it 
		if(pixel != null)
		{
			// Remove pixel at frameX, frameY
			frameDisplay.getSelectedFrame().removePixel(pixel);
			
			// Redraw canvas because the pixel was erased
			requestRedraw();
		}
		
		return pixel;
	}
	
	/**
	 * Draws a pixel at the specified mouse coordinates in canvas position space.
	 * It will use the selected layer in the palette and draw onto the selected frame.
	 * It will not draw if the layer or frame are invisible.
	 * @param canvasX	the x mouse position in canvas coordinates
	 * @param canvasY	the y mouse position in canvas coordinates
	 * @return 			the drawn pixel. Null if frame or layer are invisible.
	 */
	public Pixel draw(double canvasX, double canvasY)
	{
		// Convert the mouse position in canvas-coordinates to frame coordinates
		int frameX = convertCanvasXToFrameX(canvasX);
		int frameY = convertCanvasYToFrameY(canvasY);
		
		// Avoid drawing a pixel onto the frame if the layer or frame was invisible
		if(!palette.getSelectedLayer().getVisibility() || 
			!frameDisplay.getSelectedFrame().getVisibility())
			return null;
		
		// Attempt to find a pixel on the same layer at our mouse coordinates
		Pixel pixel = frameDisplay.getSelectedFrame().findPixel(p ->
			(p.getX() == frameX) && 
			(p.getY() == frameY) && 
			(p.getLayer() == palette.getSelectedLayer()));
		
		// If there isn't a pixel there before, create one
		if(pixel == null)
		{
			// Add pixel at frameX, frameY
			pixel = new Pixel(frameX, frameY, 
					colorSelector.getLuminosityFactor() / 100, 
					palette.getSelectedLayer());
			
			frameDisplay.getSelectedFrame().addPixel(pixel);
		}
		// A pixel already exists at our mouse, so we shall update its properties
		else
		{
			pixel.setBrightnessFactor(colorSelector.getLuminosityFactor() / 100);
		}
		
		// Redraw the canvas because a pixel was drawn
		requestRedraw();
		return pixel;
	}
	
	/** Clears the entire canvas display to {@link #canvasClearColor}.*/
	private void clearCanvas()
	{
		// Clears the entire canvas with white
		graphics.setFill(canvasClearColor);
		graphics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	/** Clears the temporary canvas which is used for effects and temporary drawings */
	public void clearTemporaryCanvas()
	{
		// Clears the entire canvas with white
		tempCanvasGraphics.clearRect(0, 0, getWidth(), getHeight());
	}
	
	/**
	 * Draws the specified rectangle on the temporary canvas using the selected color in the palette.
	 * @param canvasX	the left coordinate of the rectangle
	 * @param canvasY	the top coordinate of the rectangle
	 * @param width		the width of the rectangle
	 * @param height	the height of the rectangle
	 */
	public void drawOnTemporaryCanvas(double canvasX, double canvasY, double width, double height)
	{
		tempCanvasGraphics.setFill(colorSelector.getColorWithFactor());
		tempCanvasGraphics.fillRect(canvasX, canvasY, width, height);
	}
		
	/**
	 * Erases the specified rectangle from the temporary canvas.
	 * @param canvasX	the left coordinate of the rectangle
	 * @param canvasY	the top coordinate of the rectangle
	 * @param width		the width of the rectangle
	 * @param height	the height of the rectangle
	 */
	public void eraseOnTemporaryCanvas(double canvasX, double canvasY, double width, double height)
	{
		tempCanvasGraphics.clearRect(canvasX, canvasY, width, height);
	}
	
	/** 
	 * Redraws the frame entirely in this order:
	 * <ol>
	 * <li>Clear the canvas to the selected background color</li>
	 * <li>Render the contents of the selected frame onto the canvas</li>
	 * <li>Render the grid lines, if toggled on</li>
	 * </ol> 
	 */
	private void redraw(Frame frame, boolean onionSkinning)
	{
		// To prevent errors
		if(frameDisplay == null || frame == null)
			return;
		
		// Clear the canvas before anything
		clearCanvas();
		
		// Draw the previous frame for onion skinning
		if(onionSkinning)
		{
			Frame previousFrame = frameDisplay.getPreviousFrameOf(frame);
			if(previousFrame != null)
			{
				// Draw the frame pixels only if it is visible
				if(previousFrame.getVisibility())
				{
					// Retrieve the pixels in the frame
					Pixel[] pixels = previousFrame.getPixels().toArray(new Pixel[0]);
					
					// Create a list from the array pixels
					List<Pixel> pixelsList = Arrays.asList(pixels);
					
					// Sort the list in respect to the layer's depth
					// Rendering background first, and then the layers in the foreground ontop
					pixelsList.sort(new Comparator<Pixel>()
					{
						@Override
						public int compare(Pixel a, Pixel b)
						{
							return Integer.compare(a.getLayer().getDepth(), b.getLayer().getDepth());
						}
					});
					
					// Draw each pixel correctly with respect to scaling and zooming
					for(Pixel pixel : pixelsList)
					{
						// Draw the pixel only if its layer is visible
						if(pixel.getLayer().getVisibility())
						{
							// Draw at half the opacity.
							Color actualPixelColor = pixel.getColor();
							Color modifiedPixelColor = Color.rgb(
									(int)(actualPixelColor.getRed()*255),
									(int)(actualPixelColor.getGreen()*255),
									(int)(actualPixelColor.getBlue()*255),
									actualPixelColor.getOpacity() * onionSkinningFactor);
							graphics.setFill(modifiedPixelColor);
							graphics.fillRect(
									convertFrameXToCanvas(pixel.getX()),
									convertFrameYToCanvas(pixel.getY()),
									xScale,
									yScale);
						}
					}
				}
			}
		}
		
		// Draw the frame pixels only if it is visible
		if(frame.getVisibility())
		{
			// Retrieve the pixels in the frame
			Pixel[] pixels = frame.getPixels().toArray(new Pixel[0]);
			
			// Create a list from the array pixels
			List<Pixel> pixelsList = Arrays.asList(pixels);
			
			// Sort the list in respect to the layer's depth
			// Rendering background first, and then the layers in the foreground ontop
			pixelsList.sort(new Comparator<Pixel>()
			{
				@Override
				public int compare(Pixel a, Pixel b)
				{
					return Integer.compare(a.getLayer().getDepth(), b.getLayer().getDepth());
				}
			});
			
			// Draw each pixel correctly with respect to scaling and zooming
			for(Pixel pixel : pixelsList)
			{
				// Draw the pixel only if its layer is visible
				if(pixel.getLayer().getVisibility())
				{
					graphics.setFill(pixel.getColor());
					graphics.fillRect(
							convertFrameXToCanvas(pixel.getX()),
							convertFrameYToCanvas(pixel.getY()),
							xScale,
							yScale);
				}
			}
		}
	}
	
	/**
	 * @param canvasX	the mouse X position in canvas coordinates.
	 * @return	the X position of the frame under that mouse position
	 */
	public int convertCanvasXToFrameX(double canvasX)
	{
		// Convert the x mouse position in canvas-coordinates to frame x-coordinates
		return (int) Math.round(canvasX / xScale);
	}
	
	/**
	 * @param canvasY	the mouse Y position in canvas coordinates.
	 * @return	the Y position of the frame under that mouse position
	 */
	public int convertCanvasYToFrameY(double canvasY)
	{
		// Convert the y mouse position in canvas-coordinates to frame y-coordinates
		return (int) Math.round(canvasY / yScale);
	}
	
	/**
	 * @param frameX	the X position in frame coordinates
	 * @return	the X position of the frame in canvas coordinates
	 */
	public double convertFrameXToCanvas(int frameX)
	{
		// Convert the frame x-coordinates into canvas-coordinates
		return frameX * xScale;
	}
	
	/**
	 * @param frameY	the Y position in frame coordinates
	 * @return	the Y position of the frame in canvas coordinates
	 */
	public double convertFrameYToCanvas(int frameY)
	{
		// Convert the frame y-coordinates into canvas-coordinates
		return frameY * yScale;
	}
	
	/**
	 * Calculates the x and y scale with respect to the canvas's size and the frame's size.
	 * For example, a frame size of 64x64 and a canvas size of 640x640 should return 
	 * an x,y scale of 10,10.
	 * @see {@link #xScale} and {@link #yScale}
	 */
	private void calculateScale()
	{
		xScale = (double) getWidth() / Frame.getFrameWidth();
		yScale = (double) getHeight() / Frame.getFrameHeight();
	}
	
	/** Resets the zooming and panning. Will recalculate the view */
	public void resetView()
	{
		// Reset padding
		setTranslateX(0.0);
		setTranslateY(0.0);
		
		// Reset zooming
		setZoomScale((32 * 1.5) / Math.max(Frame.getFrameWidth(), Frame.getFrameHeight()));
		
		// Recalculates the canvas:frame scale
		calculateScale();
	}
	
	/**
	 * @return the flag of displaying the grid lines. [true = showing grid, false = hiding]
	 */
	public boolean getDisplayGridLines()
	{
		return displayGridLines;
	}
	
	/**
	 * Sets the flag to display or hide the black grid lines.
	 * If the parameter <code>draw</code> is equal to the internal flag,
	 * nothing will occur. Otherwise, the grid line canvas will be redrawn immediately.
	 * @param draw	true to display, false to hide the grid lines
	 */
	public void setDisplayGridLines(boolean draw)
	{
		// Only change the internal flag if it is not equal to the parameter flag.
		// The reason behind this check is so that we dont do an unnecessary redraws.
		// TEMP
//		if(displayGridLines != draw)
//		{
			// Request a redraw to update the selection
			displayGridLines = draw;
			gridLinesCanvas.setVisible(draw);
//		}
	}
	
	/**
	 * Sets the background color of the canvas.
	 * Note that the opacity field of the color is ignored.
	 * @param color		The background color
	 */
	public void setCanvasClearColor(Color color)
	{
		// Strict the clear color to fully opaque colors by making them fully visible
		Color opaqueColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 1.0);
		canvasClearColor = opaqueColor;
		
		// Redraw the canvas to display the new canvas background color
		requestRedraw();
	}

	/** Renders the black grid lines onto the grid lines canvas */
	public void redrawGridLines()
	{
		double x = 0;
		double y = 0;
		double width = gridLinesCanvas.getWidth();
		double height = gridLinesCanvas.getHeight();
		
		// Clear previous grid lines content
		gridGraphics.clearRect(0, 0, width, height);
		
		// Only display grid lines when the flag is true
		if(getDisplayGridLines())
		{
			gridGraphics.clearRect(0, 0, width, height);
			gridGraphics.setFill(Color.BLACK);
			
			// Display the vertical grid lines, starting from the left, going to the right
			while(x <= width)
			{
				gridGraphics.fillRect(x, 0, 1, height);
				x += xScale;
			}
			
			// Create the horizontal grid lines, starting from the top going downwards
			while(y <= height)
			{
				gridGraphics.fillRect(0, y, width, 1);
				y += yScale;
			}
			
			// Draw the right most grid line, its not guaranteed to be included in the while loop due to rounding error
			gridGraphics.fillRect(width - 1, 0, 1, height);
			
			// Draw the bottom most grid line, its not guaranteed to be included in the while loop due to rounding error
			gridGraphics.fillRect(0, height - 1, width, 1);
		}
	}

	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.addEventFilter(KeyEvent.KEY_PRESSED, e ->
		{
			if(e.getCode() == KeyCode.R)
			{
				resetView();
			}
		});
	}

	/**
	 * @return the scaling factor between the canvas to the frame size
	 */
	public double getCanvasToFrameScaleX()
	{
		return xScale;
	}

	/**
	 * @return the scaling factor between the canvas to the frame size
	 */
	public double getCanvasToFrameScaleY()
	{
		return yScale;
	}

	/**
	 * @return returns the frame display used in the program
	 */
	public FrameDisplay getFrameDisplay()
	{
		return frameDisplay;
	}

	/**
	 * @return returns the color selector used in the program
	 */
	public ColorSelector getColorSelector()
	{
		return colorSelector;
	}

	/**
	 * Shifts the selected frame by the specified pixel amount. Any pixels outside the Frame view range, will be removed.
	 * @param shiftX	the amount of horizontal shift
	 * @param shiftY	the amount of vertical shift
	 */
	public void shiftFrame(int shiftX, int shiftY)
	{
		// Go through the pixels in the selected frame
		for(Iterator<Pixel> iterator = frameDisplay.getSelectedFrame().getPixels().iterator(); iterator.hasNext();)
		{
			Pixel p = iterator.next();
			
			// Calculate the position after the shift
			int newX = p.getX() + shiftX;
			int newY = p.getY() + shiftY;
			
			// Check if it is out of bounds, and if it is, remove it
			if(newX > Frame.getFrameWidth() || newY > Frame.getFrameHeight() || newX < 0 || newY < 0)
			{
				// Out of bounds, remove it
				iterator.remove();
			}
			else
			{
				// In bounds , shift the pixel it
				p.setX(newX);
				p.setY(newY);
			}
		}
		
		// Request a canvas redraw after the shifting has occured
		requestRedraw();
	}
}
