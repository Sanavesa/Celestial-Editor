/***************************************************************************************************************************
 * Class:		BrushTool.java
 * Author:		Mohammad Alali
 * 
 * Description: A generic base class for creating brush tools that provides all the required functionality for properly
 * 				operating the brush.
 * 	
 * Attributes: 	
 * 				BrushType brushType
 * 				imageView imageView
 * 		
 * Methods:		
 * 				void setImage(Image)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.brushTool;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import sanavesa.gui.canvas.PixelatedCanvas;

/** The type of brush */
enum BrushType
{
	PRIMARY_ONLY,
	SECONDARY_ONLY,
	SIZE_ONLY,
}


/**
 * A generic base class for creating brush tools that provides all the required functionality for properly
 * operating the brush.
 * @author Mohammad Alali1
 */
public abstract class BrushTool
{
	/** The brush type */
	protected BrushType brushType = BrushType.PRIMARY_ONLY;
	
	/** The image icon of the brush */ 
	protected ImageView imageView = null;
	
	/** The label of the brush, which acts as a root */
	protected Label label = null;
	
	/** The brush manager */
	protected BrushToolManager brushToolManager = null;
	
	/** Setup some cool effects */
	protected static final DropShadow highlightEffect = new DropShadow(16, 0, 0, Color.YELLOW);
	protected static final DropShadow selectedEffect = new DropShadow(16, 0, 0, Color.AQUA);
	
	/** Constructs a new brush and automates the selection logic. */
	public BrushTool(Image image, BrushToolManager brushToolManager, BrushType brushType)
	{
		this.brushToolManager = brushToolManager;
		this.brushType = brushType;
		setImage(image);
		
		brushToolManager.primaryToolProperty().addListener((args, oldTool, newTool) -> updateHighlights(oldTool, newTool));
		brushToolManager.secondaryToolProperty().addListener((args, oldTool, newTool) -> updateHighlights(oldTool, newTool));
		brushToolManager.sizeToolProperty().addListener((args, oldTool, newTool) -> updateHighlights(oldTool, newTool));
	}
	
	/** Returns the root label of the brush */
	public final Label getRoot()
	{
		return label;
	}
	
	/**
	 * Sets the image of the brush to the specified argument. It will register all the event handlers onto that image
	 * and set it up to work with the brush manager.
	 * @param image		brush's image
	 */
	public void setImage(Image image)
	{
		// Only register event handling if the image passed in wasn't null
		if(image != null)
		{
			imageView = new ImageView(image);
			label = new Label();
			label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			label.setGraphic(imageView);
			
			// To make it look organized
			imageView.setPickOnBounds(true);
			imageView.setPreserveRatio(true);
			imageView.setFitWidth(32);
			label.setPickOnBounds(true);

			// Register event handling
			label.setOnMousePressed(e -> onMousePressedOnTool(e));
			label.setOnMouseEntered(e -> onMouseEnterOnTool(e));
			label.setOnMouseExited(e -> onMouseExitOnTool(e));
		}
	}
	
	/** Called when the user presses on the tool in the GUI */
	public void onMousePressedOnTool(MouseEvent e)
	{
		switch(brushType)
		{
		case PRIMARY_ONLY:
			brushToolManager.setPrimaryTool(this);
			break;
		case SECONDARY_ONLY:
			brushToolManager.setSecondaryTool(this);
			break;
		case SIZE_ONLY:
			brushToolManager.setSizeTool(this);
			break;
		}
	}
	
	/** Called when the user hovers its mouse over the icon of the tool. Sets up highlights */
	public void onMouseEnterOnTool(MouseEvent e)
	{
		switch(brushType)
		{
		case PRIMARY_ONLY:
			if(this != brushToolManager.getPrimaryTool())
			{
				label.setEffect(highlightEffect);
			}
			break;
			
		case SECONDARY_ONLY:
			if(this != brushToolManager.getSecondaryTool())
			{
				label.setEffect(highlightEffect);
			}
			break;
			
		case SIZE_ONLY:
			if(this != brushToolManager.getSizeTool())
			{
				label.setEffect(highlightEffect);
			}
			break;
		}
	}
	
	/** Called when the user hovers its mouse out of the icon of the tool */
	public void onMouseExitOnTool(MouseEvent e)
	{
		switch(brushType)
		{
		case PRIMARY_ONLY:
			if(this != brushToolManager.getPrimaryTool())
			{
				label.setEffect(null);
			}
			break;
			
		case SECONDARY_ONLY:
			if(this != brushToolManager.getSecondaryTool())
			{
				label.setEffect(null);
			}
			break;
			
		case SIZE_ONLY:
			if(this != brushToolManager.getSizeTool())
			{
				label.setEffect(null);
			}
			break;
		}
	}
	
	/** Updates the tool highlighting */
	public void updateHighlights(BrushTool oldTool, BrushTool newTool)
	{
		if(oldTool != null)
			oldTool.label.setEffect(null);
		
		if(newTool != null)
			newTool.label.setEffect(selectedEffect);
	}
	
	/** Called when mouse moves on canvas */
	public void onMouseMovedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY) {}
	
	/** Called the mouse click is registered on canvas */
	public void onMousePressedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY) {}
	
	/** Called the mouse click is dragged on canvas */
	public void onMouseDraggedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY) {}
	
	/** Called the mouse click is released on canvas */
	public void onMouseReleasedOnCanvas(PixelatedCanvas canvas, MouseEvent e, double snappedMouseX, double snappedMouseY) {}

	/** Called the mouse enters the canvas */
	public void onMouseEnteredCanvas(PixelatedCanvas canvas, MouseEvent e) {}
	
	/** Called the mouse exits the canvas */
	public void onMouseExitedCanvas(PixelatedCanvas canvas, MouseEvent e) {}
}
