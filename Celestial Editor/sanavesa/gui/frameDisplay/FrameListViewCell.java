package sanavesa.gui.frameDisplay;

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
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.source.Frame;

/**
 * This class is a representation of a single cell item in the listView
 * of the {@link FrameDisplay} class, which represents a Frame that can be
 * visible/hidden with its name displayed.
 * @author Mohammad Alali
 */
public class FrameListViewCell extends ListCell<Frame>
{
	/** Load the visible eye image */
	private static final Image visibleEyeImage = new Image("/art/VisibleEye.png");
	
	/** Load the invisible eye image */
	private static final Image invisibleEyeImage = new Image("/art/InvisibleEye.png");
	
	/** The Canvas used, to redraw the canvas when the visibility is toggled */
	private PixelatedCanvas canvas = null;
	
	/**
	 * Creates a new list cell for the frame list view.
	 * @param canvas	the main canvas used
	 */
	public FrameListViewCell(PixelatedCanvas canvas)
	{
		this.canvas = canvas;
	}
	
	/** Called automatically by JavaFX everytime the cell item needs to be refreshed. */
	@Override
	protected void updateItem(Frame frame, boolean empty)
	{
		super.updateItem(frame, empty);
		if(frame == null || empty)
		{
			setText(null);
			setGraphic(null);
		}
		else
		{
			HBox hBox = new HBox(10);
			ImageView imageView = new ImageView();
			Label cellLabel = new Label();
			Region region = new Region();
			
			setTooltip(new Tooltip(
					"[CTRL + 1, Up Arrow]: Select the previous frame"
					+"\n[CTRL + 1, Down Arrow]: Select the next frame"));
			
			// Setup the label of the frame
			setupLabel(cellLabel, frame);
			
			// Setup the image display
			setupImageView(imageView, cellLabel, frame);
			
			// Separator Region
			HBox.setHgrow(region, Priority.ALWAYS);
			
			// Change the cursor when the mouse is above the image
			imageView.setCursor(Cursor.HAND);
			
			// Setup Mouse Entered
			imageView.setOnMouseEntered(e -> onMouseEnteredImageView(imageView));
			
			// Setup Mouse Exit
			imageView.setOnMouseExited(e -> onMouseExitedImageView(imageView));
			
			// Setup Mouse Click
			imageView.setOnMouseClicked(e -> onMouseClickedImageView(e, imageView, frame));
			
			// Layout of the item
			hBox.getChildren().setAll(cellLabel, region, imageView);
			
			setGraphic(hBox);
		}
	}
	
	/** Set up the label of the frame cell item to always show the name of the frame */
	private void setupLabel(Label cellLabel, Frame frame)
	{
		cellLabel.textProperty().bind(frame.nameProperty());
		cellLabel.setPadding(new Insets(4, 0, 0, 0)); // Move the label abit down
	}
	
	/** Set up the image view of the frame cell item to show the visibility status of the frame*/
	private void setupImageView(ImageView imageView, Label cellLabel, Frame frame)
	{
		imageView.setPreserveRatio(true);
		imageView.setFitHeight(24);
		onFrameVisibilityChanged(frame.getVisibility(), imageView, cellLabel, frame);
		
		frame.visibilityProperty().addListener((args, oldVisibility, newVisibility) ->
		{
			onFrameVisibilityChanged(newVisibility, imageView, cellLabel, frame);
		});
	}
	
	/** Called everytime the mouse enters the image view */
	private void onMouseEnteredImageView(ImageView imageView)
	{
		imageView.setEffect(new DropShadow(20, Color.YELLOW));
	}
	
	/** Called everytime the mouse enters the image view */
	private void onMouseExitedImageView(ImageView imageView)
	{
		imageView.setEffect(null);
	}
	
	/** Called everytime the frame changes visibility */
	private void onFrameVisibilityChanged(boolean newVisibility, ImageView imageView, Label cellLabel, Frame frame)
	{
		if(newVisibility)
		{
			imageView.setImage(visibleEyeImage);
			cellLabel.setOpacity(1);
		}
		else
		{
			imageView.setImage(invisibleEyeImage);
			cellLabel.setOpacity(0.5);
		}
		
		// Redraw the canvas
		if(isSelected())
		{
			canvas.requestRedraw();
		}
	}
	
	/** Called everytime the mouse clicks the image view */
	private void onMouseClickedImageView(MouseEvent e, ImageView imageView, Frame frame)
	{
		if(e.getButton() == MouseButton.PRIMARY)
		{
			// If visible -> make it invisible
			if(frame.getVisibility())
			{
				frame.setVisibility(false);
			}
			// If invisible -> make it visible
			else
			{
				frame.setVisibility(true);
			}
		}
	}
}