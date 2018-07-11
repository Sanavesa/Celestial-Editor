/***************************************************************************************************************************
 * Class:		LayerListViewCell.java
 * Author:		Mohammad Alali
 * 
 * Description: This class is a representation of a single cell item in the listView of the Palette class, which represents 
 * 				a Layer that can be visible/hidden with its name and color displayed.
 * 	
 * Attributes: 	
 * 				static Image visibielEyeImage
 * 				static Image invisibleEyeImage
 * 		
 * Methods:		
 * 				Nothing Interesting
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.palette;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sanavesa.gui.frameDisplay.FrameDisplay;
import sanavesa.source.Layer;

/**
 * This class is a representation of a single cell item in the listView
 * of the {@link Palette} class, which represents a Layer that can be
 * visible/hidden with its name and color displayed.
 * @author Mohammad Alali
 */
public class LayerListViewCell extends ListCell<Layer>
{
	/** Load the visible eye image */
	private static final Image visibleEyeImage = new Image("/art/VisibleEye.png");
	
	/** Load the invisible eye image */
	private static final Image invisibleEyeImage = new Image("/art/InvisibleEye.png");
	
	/** The frame display used to redraw the canvas when toggling visibility */
	private FrameDisplay frameDisplay = null;
	
	/**
	 * Initialize a new cell item with the specified parameters.
	 * @param frameDisplay	the main frame display
	 */
	public LayerListViewCell(FrameDisplay frameDisplay)
	{
		this.frameDisplay = frameDisplay;
	}
	
	/** Called automatically by JavaFX everytime the cell item needs to be refreshed. */
	@Override
	protected void updateItem(Layer layer, boolean empty)
	{
		super.updateItem(layer, empty);
		if(layer == null || empty)
		{
			setText(null);
			setGraphic(null);
		}
		else
		{
			// Create the graphics
			HBox hBox = new HBox(10);
			Circle colorDisplay = new Circle(12);
			ImageView imageView = new ImageView();
			Label cellLabel = new Label();
			Region region = new Region();
			
			setTooltip(new Tooltip(
					"[CTRL + 2, Up Arrow]: Select the previous layer"
					+"\n[CTRL + 2, Down Arrow]: Select the next layer"));
			
			// Setup the color display of the layer
			setupColorDisplay(colorDisplay, layer);
			
			// Setup the label of the layer
			setupLabel(cellLabel, layer);
			
			// Setup the image display
			setupImageView(imageView, cellLabel, colorDisplay, layer);
			
			// Separator Region
			HBox.setHgrow(region, Priority.ALWAYS);
			
			// Change the cursor when the mouse is above the image
			imageView.setCursor(Cursor.HAND);
			
			// Setup Mouse Entered
			imageView.setOnMouseEntered(e -> onMouseEnteredImageView(imageView));
			
			// Setup Mouse Exit
			imageView.setOnMouseExited(e -> onMouseExitedImageView(imageView));
			
			// Setup Mouse Click
			imageView.setOnMouseClicked(e -> onMouseClickedImageView(e, imageView, layer));
			
			// Layout of the item
			hBox.getChildren().setAll(colorDisplay, cellLabel, region, imageView);
			
			setGraphic(hBox);
		}
	}
	
	/** Sets up the color display of the cell item to always show the layer's color */
	private void setupColorDisplay(Circle colorDisplay, Layer layer)
	{
		colorDisplay.fillProperty().bind(layer.colorProperty());
	}
	
	/** Sets up the cell label of the cell item to always show the layer's name */
	private void setupLabel(Label cellLabel, Layer layer)
	{
		cellLabel.textProperty().bind(layer.nameProperty());
		cellLabel.setPadding(new Insets(4, 0, 0, 0)); // Move the label abit down
	}
	
	/** Sets up the image view display of the cell item to always show the layer's visibility status */
	private void setupImageView(ImageView imageView, Label cellLabel, Circle colorDisplay, Layer layer)
	{
		imageView.setPreserveRatio(true);
		imageView.setFitHeight(24);
		onLayerVisibilityChanged(layer.getVisibility(), imageView, cellLabel, colorDisplay, layer);
		
		layer.visibilityProperty().addListener((args, oldVisibility, newVisibility) ->
		{
			onLayerVisibilityChanged(newVisibility, imageView, cellLabel, colorDisplay, layer);
		});
	}
	
	/** Called whenever the visibility of the layer changes */
	private void onLayerVisibilityChanged(boolean newVisibility, ImageView imageView, Label cellLabel, Circle colorDisplay, Layer layer)
	{
		if(newVisibility)
		{
			imageView.setImage(visibleEyeImage);
			cellLabel.setOpacity(1);
			colorDisplay.setOpacity(1);
		}
		else
		{
			imageView.setImage(invisibleEyeImage);
			cellLabel.setOpacity(0.5);
			colorDisplay.setOpacity(0.5);
		}
		
		// Redraw the canvas
		frameDisplay.requestCanvasRedraw();
	}
	
	/** Called every time the mouse enters the image view */
	private void onMouseEnteredImageView(ImageView imageView)
	{
		imageView.setEffect(new DropShadow(20, Color.YELLOW));
	}
	
	/** Called every time the mouse leaves the image view */
	private void onMouseExitedImageView(ImageView imageView)
	{
		imageView.setEffect(null);
	}
	
	/** Called every time the mouse clicks on the image view */
	private void onMouseClickedImageView(MouseEvent e, ImageView imageView, Layer layer)
	{
		if(e.getButton() == MouseButton.PRIMARY)
		{
			// If visible -> make it invisible
			if(layer.getVisibility())
			{
				layer.setVisibility(false);
			}
			// If invisible -> make it visible
			else
			{
				layer.setVisibility(true);
			}
		}
	}
}