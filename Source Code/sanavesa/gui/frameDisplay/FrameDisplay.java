/***************************************************************************************************************************
 * Class:		FrameDisplay.java
 * Author:		Mohammad Alali
 * 
 * Description: The FrameDisplay class represents a glorified ListView that displays all the frames used in the program.
 * 	
 * Attributes: 	
 * 				boolean keyCombo
 * 		
 * Methods:		
 * 				Frame getSelectedFrame()
 * 				List<Frame> getFrames()
 * 				void save(ObjectOutputStream)
 * 				void reset()
 * 				void load(ObjectInputStream)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.frameDisplay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import sanavesa.gui.IGraphicalInterface;
import sanavesa.gui.IKeyMapping;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.gui.popup.PermissionPopup;
import sanavesa.gui.popup.TextInputPopup;
import sanavesa.source.Frame;
import sanavesa.source.ISerializable;
import sanavesa.source.Pixel;

/**
 * The FrameDisplay class represents a glorified ListView that displays all
 * the frames used in the program.
 * <p>
 * This class implements the following interfaces:
 * <ul>
 * <li> {@link IGraphicalInterface} for ease of GUI Display </li>
 * <li> {@link IKeyMapping} for ease of Key Binding </li>
 * <li> {@link ISerializable} for ease of file IO </li>
 * </ul>
 * </p>
 * @author Mohammad Alali
 */
public class FrameDisplay implements IGraphicalInterface, IKeyMapping, ISerializable
{
	/** A flag used for key shortcut combinations */
	private boolean keyCombo = false;
	
	/** The class serialization ID, used for file IO */
	private static final long serialVersionUID = 3590074009313013594L;
	
	/** The layout root node of the GUI */
	private Pane rootPane = new Pane();
	
	/** The list view that contains all the frames in the program */
	private ListView<Frame> listView = new ListView<Frame>(FXCollections.observableArrayList());
	
	/** The delete frame button */
    private Button deleteButton = new Button("Delete Frame");
    
    /** The add frame button */
    private Button addButton = new Button("Add Frame");
    
    /** The clear frame button */
    private Button clearButton = new Button("Clear Frame");
    
    /** The duplicate frame button */
    private Button duplicateButton = new Button("Duplicate Frame");
    
    /** The rename frame button */
    private Button renameButton = new Button("Rename Frame");
    
    /** The move up frame button */
    private Button moveUpButton = new Button("Move Frame Up");
    
    /** The move down frame button */
    private Button moveDownButton = new Button("Move Frame Down");
    
    /** The main canvas used in the program */
    private PixelatedCanvas canvas = null;
    
    /** Create a frame display with the specified reference parameter. */
    public FrameDisplay(PixelatedCanvas canvas)
	{
    	this.canvas = canvas;
	}
    
    /** Initialize the Frame Display's GUI Layout. */
    @Override
    public void initializeLayout()
	{
    	// Create a label for this gui
    	Label frameDisplayLabel = new Label();
    	frameDisplayLabel.setText("Frames");
    	frameDisplayLabel.setStyle("-fx-font-weight: bold");
    	frameDisplayLabel.setFont(new Font("Arial", 16));
    	
    	// Add the gui elements to the root of the frame display
    	rootPane.getChildren().addAll(listView, addButton, deleteButton, 
    			renameButton, moveDownButton, moveUpButton, duplicateButton, clearButton, frameDisplayLabel);

    	// Make it such that we cant use [TAB] selection in the list view
    	listView.setFocusTraversable(false);
    	
    	// Scroll through the elements using the mouse
    	// Using eventFilter instead of setOnScroll to block
    	// the default javaFX implementation from interfering with the scrolling
    	listView.addEventFilter(ScrollEvent.SCROLL, e -> onListViewScrolled(e));
    	
    	// Setup the visual display of the items in the list view
    	listView.setCellFactory(new Callback<ListView<Frame>, ListCell<Frame>>()
    	{
			@Override
			public ListCell<Frame> call(ListView<Frame> param)
			{
				return new FrameListViewCell(canvas);
			}
		});
    	
    	// Called when the selected item changes
    	listView.getSelectionModel().selectedItemProperty().addListener(
    			(args, oldSelectedFrame, newSelectedFrame) -> onSelectionChanged(oldSelectedFrame, newSelectedFrame));
    	
    	// Called when the add button is clicked
    	addButton.setOnAction(e -> onAddButtonClicked());
    	addButton.setFocusTraversable(false);
    	addButton.setTooltip(new Tooltip("[CTRL + 1, A]: Adds a new frame"));
    	
    	// Called when the remove button is clicked
    	deleteButton.setOnAction(e -> onDeleteButtonClicked());
    	deleteButton.setFocusTraversable(false);
    	deleteButton.setTooltip(new Tooltip("[CTRL + 1, X]: Deletes the selected frame"));
    	
    	// Called when the duplicate button is clicked
    	duplicateButton.setOnAction(e -> onDuplicateButtonClicked());
    	duplicateButton.setFocusTraversable(false);
    	duplicateButton.setTooltip(new Tooltip("[CTRL + 1, D]: Duplicates the selected frame"));
    	
    	// Called when the rename button is clicked
    	renameButton.setOnAction(e -> onRenameButtonClicked());
    	renameButton.setFocusTraversable(false);
    	renameButton.setTooltip(new Tooltip("[CTRL + 1, R]: Renames the selected frame"));
    	
    	// Called when the clear button is clicked
    	clearButton.setOnAction(e -> onClearButtonClicked());
    	clearButton.setFocusTraversable(false);
    	clearButton.setTooltip(new Tooltip("[CTRL + 1, C]: Renames the selected frame"));
    	
    	// Called when the move up button is clicked
    	moveUpButton.setOnAction(e -> onMoveUpButtonClicked());
    	moveUpButton.setFocusTraversable(false);
    	moveUpButton.setTooltip(new Tooltip("[CTRL + 1, Left Arrow]: Moves the selected frame up"));
    	
    	// Called when the move down button is clicked
    	moveDownButton.setOnAction(e -> onMoveDownButtonClicked());
    	moveDownButton.setTooltip(new Tooltip("[CTRL + 1, Right Arrow]: Moves the selected frame down"));
    	
    	// Add the default frame
    	listView.getItems().add(new Frame("Default Frame"));
    	
    	listView.setMinHeight(200);
    	listView.setPrefHeight(Double.MAX_VALUE);
    	listView.setMaxHeight(600);
    	
    	// Assign the sizes and positions for the buttons
    	// Using runLater because the sizes need atleast a frame cycle to compute in JavaFX
    	Platform.runLater(() ->
    	{
    		double startY = listView.getHeight() + frameDisplayLabel.getHeight();
    		double btnWidth = listView.getWidth() * 0.45;
    		double btnHeight = 16;
    		
    		// Setup the palette's label position
    		frameDisplayLabel.relocate(listView.getWidth() / 2 - frameDisplayLabel.getWidth() / 2, 0);
    		
    		listView.relocate(0, frameDisplayLabel.getHeight());
    		
    		// Duplicate Button
    		duplicateButton.relocate(0, startY + 8);
    		duplicateButton.setMinWidth(listView.getWidth());
    		duplicateButton.setMinHeight(btnHeight);
    		
    		// Add Button
    		addButton.relocate(0, startY + btnHeight + 24);
    		addButton.setMinWidth(btnWidth);
    		addButton.setMinHeight(btnHeight);
    		
    		// Delete Button
    		deleteButton.relocate(listView.getWidth() - btnWidth, startY + btnHeight + 24);
    		deleteButton.setMinWidth(btnWidth);
    		deleteButton.setMinHeight(btnHeight);
    		
    		// Clear Button
    		clearButton.relocate(0, startY + 8 + 2*(btnHeight + 16));
    		clearButton.setMinWidth(btnWidth);
    		clearButton.setMinHeight(btnHeight);
    		
    		// Rename Button
    		renameButton.relocate(listView.getWidth() - btnWidth, startY + 8 + 2*(btnHeight + 16));
    		renameButton.setMinWidth(btnWidth);
    		renameButton.setMinHeight(btnHeight);
    		
    		// Move Up Button
    		moveUpButton.relocate(0, startY + 8 + 3*(btnHeight + 16));
    		moveUpButton.setMinWidth(btnWidth);
    		moveUpButton.setMinHeight(btnHeight);
    		
    		// Move Down Button
    		moveDownButton.relocate(listView.getWidth() - btnWidth, startY + 8 + 3*(btnHeight + 16));
    		moveDownButton.setMinWidth(btnWidth);
    		moveDownButton.setMinHeight(btnHeight);
    	});
    }
    
    /** Called when the clear button is clicked */
    private void onClearButtonClicked()
	{
    	PermissionPopup popup = new PermissionPopup("Clear Frame Contents");
    	popup.setupCancelButton("Cancel");
    	popup.setupOkButton("Clear");
    	popup.setupDisplayLabel("Are you sure?");
    	popup.show();
    	boolean response = popup.getResponse();
    	
    	// User pressed ok
    	if(response)
    	{
    		// Clear the selected frame
    		Frame selectedFrame = listView.getSelectionModel().getSelectedItem();
    		selectedFrame.clearPixels();
    		canvas.requestRedraw();
    	}	
	}

	/** Called when duplicate button is clicked */
    private void onDuplicateButtonClicked()
	{
    	final ObservableList<Frame> frames = listView.getItems();
		final String[] prohibitedStrings = new String[frames.size()];
    	
    	// All previous frame names are invalid
    	for(int i = 0; i < frames.size(); i++)
    	{
    		prohibitedStrings[i] = frames.get(i).getName();
    	}
    	
    	TextInputPopup popup = new TextInputPopup("Duplicate a Frame");
    	popup.setupCancelButton("Cancel");
    	popup.setupInstructionLabel("Frame's Name:");
    	popup.setupOkButton("Add");
    	popup.setupTextField("NewFrame" + (frames.size()+1), "Enter a frame name", prohibitedStrings, 
    			3, 24, "Frame's name is too short!", "Frame's name is too long!", "Frame's name is already in use!");
    	popup.show();
    	
    	String response = popup.getResponse().trim(); 
    	if(response.length() > 0)
    	{
    		// Add the frame
    		ObservableSet<Pixel> pixels = FXCollections.observableSet(new HashSet<Pixel>());
    		for(Pixel p : listView.getSelectionModel().getSelectedItem().getPixels())
    		{
    			pixels.add(new Pixel(p));
    		}
    		
    		Frame duplicateFrame = new Frame(response, pixels, listView.getSelectionModel().getSelectedItem().getVisibility());
    		frames.add(duplicateFrame);
    		listView.getSelectionModel().select(duplicateFrame);
    	}
	}

    /** Called when the list selection changes */
	private void onSelectionChanged(Frame oldSelectedFrame, Frame newSelectedFrame)
    {
		// Get selected item index
		int selectedItemIndex = listView.getSelectionModel().getSelectedIndex();
		
		// Disable the move up button if selecting up most frame
		moveUpButton.setDisable(selectedItemIndex == 0);
		
		// Disable the move down button if selecting the bottom most frame
		moveDownButton.setDisable(selectedItemIndex == listView.getItems().size() - 1);
		
		// Disable delete button if selecting no frame or only 1 frame remains 
		deleteButton.setDisable(newSelectedFrame == null || 
				listView.getItems().size() == 1);
		
		// Disable rename button if selecting no frame
		renameButton.setDisable(newSelectedFrame == null);
		
		// Disable duplicate button if selecting no frame
		duplicateButton.setDisable(newSelectedFrame == null);
		
		// Redraw canvas when changing selection
		if(oldSelectedFrame != newSelectedFrame)
		{
			canvas.requestRedraw();
		}
		
		// Scroll to our new selection
		listView.scrollTo(newSelectedFrame);
    }
    
    /**
     * Swaps two items in the list view.
     * @param firstIndex	the first index to swap
     * @param secondIndex	the second index to swap
     */
    private void swapLayers(int firstIndex, int secondIndex)
    {
    	Frame firstFrame = listView.getItems().get(firstIndex);
		Frame secondFrame = listView.getItems().get(secondIndex);
		
		listView.getItems().set(secondIndex, firstFrame);
		listView.getItems().set(firstIndex, secondFrame);
	}

    /** Called when delete button is clicked */
    private void onDeleteButtonClicked()
    {
    	PermissionPopup popup = new PermissionPopup("Delete Frame");
    	popup.setupCancelButton("Cancel");
    	popup.setupOkButton("Remove");
    	popup.setupDisplayLabel("Are you sure?");
    	popup.show();
    	boolean response = popup.getResponse();
    	
    	// User pressed ok
    	if(response)
    	{
    		// Delete the selected frame
    		Frame selectedFrame = listView.getSelectionModel().getSelectedItem();
    		listView.getItems().remove(selectedFrame);
    	}
    }
    
    /** Called when add button is clicked */
    private void onAddButtonClicked()
    {
    	final ObservableList<Frame> frames = listView.getItems();
		final String[] prohibitedStrings = new String[frames.size()];
    	
    	// All previous frame names are invalid
    	for(int i = 0; i < frames.size(); i++)
    	{
    		prohibitedStrings[i] = frames.get(i).getName();
    	}
    	
    	TextInputPopup popup = new TextInputPopup("Add a New Frame");
    	popup.setupCancelButton("Cancel");
    	popup.setupInstructionLabel("Frame's Name:");
    	popup.setupOkButton("Add");
    	popup.setupTextField("NewFrame" + (frames.size()+1), "Enter a frame name", prohibitedStrings, 
    			3, 24, "Frame's name is too short!", "Frame's name is too long!", "Frame's name is already in use!");
    	popup.show();
    	
    	String response = popup.getResponse().trim(); 
    	if(response.length() > 0)
    	{
    		// Add the frame
    		Frame addedFrame = new Frame(response);
    		frames.add(addedFrame);
    		listView.getSelectionModel().select(addedFrame);
    	}
    }
    
    /** Called when rename button is clicked */
    private void onRenameButtonClicked()
    {
    	final ObservableList<Frame> frames = listView.getItems();
		final String[] prohibitedStrings = new String[frames.size() - 1];
    	Frame selectedFrame = listView.getSelectionModel().getSelectedItem();
    	
    	// All previous frames names are invalid, except for our current selected frame, which should be valid
    	int prohibitedStringsIndex = 0;
    	for(int i = 0; i < frames.size(); i++)
    	{
    		Frame frame = frames.get(i);
    		if(selectedFrame != frame)
    		{
    			prohibitedStrings[prohibitedStringsIndex++] = frame.getName();
    		}
    	}
    	
    	TextInputPopup popup = new TextInputPopup("Rename Frame");
    	popup.setupCancelButton("Cancel");
    	popup.setupInstructionLabel("New Frame's Name:");
    	popup.setupOkButton("Rename");
    	popup.setupTextField(selectedFrame.getName(), "Enter a new frame name", prohibitedStrings, 
    			3, 24, "Frame's name is too short!", "Frame's name is too long!", "Frame's name is already in use!");
    	popup.show();
    	
    	String response = popup.getResponse().trim(); 
    	if(response.length() > 0)
    	{
    		// Rename the frame
    		selectedFrame.setName(response);
    	}
    }
    
    /** Called when move up button is clicked */
    private void onMoveUpButtonClicked()
    {
    	int currentLayerIndex = listView.getSelectionModel().getSelectedIndex();
		int upperLayerIndex = listView.getSelectionModel().getSelectedIndex() - 1;
		
		// Swap layers
		swapLayers(currentLayerIndex, upperLayerIndex);
		
		// Follow our initial selection
		listView.getSelectionModel().select(upperLayerIndex);
    }
    
    /** Called when move down button is clicked */
    private void onMoveDownButtonClicked()
    {
    	int currentLayerIndex = listView.getSelectionModel().getSelectedIndex();
		int lowerLayerIndex = listView.getSelectionModel().getSelectedIndex() + 1;
		
		// Swap layers
		swapLayers(currentLayerIndex, lowerLayerIndex);
		
		// Follow our initial selection
		listView.getSelectionModel().select(lowerLayerIndex);
    }
    
    /** Returns the root pane of the Frame Display's GUI */
	@Override
	public Pane getRoot()
	{
		return rootPane;
	}
	
	/**
	 * @return the selected frame in the GUI, null if not selecting an item
	 */
	public Frame getSelectedFrame()
	{
		return listView.getSelectionModel().getSelectedItem();
	}
	
	/**
	 * @return the previous frame in the GUI, null if there is no previous
	 */
	public Frame getPreviousFrameOf(Frame frame)
	{
		int selectedIndex = listView.getItems().indexOf(frame);
		if(selectedIndex <= 0) // 0 = first, -1 = not found, both dont have previous
			return null;
		
		return listView.getItems().get(selectedIndex - 1);
	}

	/**
	 * Sets up the key binding shortcuts.
	 * <ul>
	 * <li> [CTRL + 1, A] : Adds a Frame </li>
	 * <li> [CTRL + 1, X] : Deletes a Frame </li>
	 * <li> [CTRL + 1, D] : Duplicates a Frame </li>
	 * <li> [CTRL + 1, C] : Clears a Frame </li>
	 * <li> [CTRL + 1, R] : Renames a Frame </li>
	 * <li> [CTRL + 1, Left] : Moves a Frame in the hierarchy up by 1 </li>
	 * <li> [CTRL+ 1, Right] : Moves a Frame in the hierarchy down by 1 </li>
	 * <li> [CTRL + 1, Up] : Select the previous Frame </li>
	 * <li> [CTRL + 1, Down] : Select the next Frame </li>
	 * </ul>
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			switch(event.getCode())
			{
			case DIGIT1:
				if(event.isControlDown())
					keyCombo = true;
				break;
			// Add frame
			case A:
				if(keyCombo)
				{
					addButton.fire();
					keyCombo = false;
				}
				break;
			// Delete frame
			case X:
				if(keyCombo)
				{
					deleteButton.fire();
					keyCombo = false;
				}
				break;
			// Duplicate frame
			case D:
				if(keyCombo)
				{
					duplicateButton.fire();
					keyCombo = false;
				}
				break;
			// Rename frame
			case R:
				if(keyCombo)
				{
					renameButton.fire();
					keyCombo = false;
				}
				break;
				
				// Clear frame
			case C:
				if(keyCombo)
				{
					clearButton.fire();
					keyCombo = false;
				}
				break;
			// Select frame Above
			case UP:
				if(keyCombo)
				{
					listView.getSelectionModel().selectPrevious();
					keyCombo = false;
				}
				break;
			// Select frame Below
			case DOWN:
				if(keyCombo)
				{
					listView.getSelectionModel().selectNext();
					keyCombo = false;
				}
				break;	
			// Move frame Above
			case LEFT:
				if(keyCombo)
				{
					moveUpButton.fire();
					keyCombo = false;
				}
				break;
			// Move frame Below
			case RIGHT:
				if(keyCombo)
				{
					moveDownButton.fire();
					keyCombo = false;
				}
				break;
			// Undo
			case Z:
				if(event.isControlDown())
				{
					getSelectedFrame().undo();
					requestCanvasRedraw();
				}
				break;
			default:
				keyCombo = false;
				break;
			}
		});
	}
	
	/** Request to redraw the selected frame */
	public void requestCanvasRedraw()
	{
		canvas.requestRedraw();
	}
	
	/** Returns the list of frames used in the program */
	public ObservableList<Frame> getFrames()
	{
		return listView.getItems();
	}

	/** Saves the frame display data to a file stream */
	@Override
	public void save(ObjectOutputStream out) throws IOException
	{
		// Saves each frame in the frame display's list view to the stream
		ObservableList<Frame> frames = listView.getItems();
		int size = frames.size(); 
		out.writeInt(size);
		for(int i = 0; i < size; i++)
		{
			frames.get(i).save(out);
		}
	}

	/** Loads the frame display data from a file stream */
	@Override
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// Clear the frames, and load them 1 by 1 into the listView
		ObservableList<Frame> frames = listView.getItems();
		int size = in.readInt();
		frames.clear();
		for(int i = 0; i < size; i++)
		{
			Frame f = new Frame("");
			f.load(in);
			frames.add(f);
		}
		listView.getSelectionModel().select(0);
	}
	
	/** Resets the frame display to the default values */
	@Override
	public void reset()
	{
		// Clear all frames, and add an empty default frame
		ObservableList<Frame> frames = listView.getItems();
		frames.setAll(new Frame("Default Frame"));
		listView.getSelectionModel().select(0);
	}
	
	/**
	 * @return the list of frames stored in the graphical list view of the frame display.
	 */
	public ListView<Frame> getListViewFrames()
	{
		return listView;
	}
	
	/**
     * Called when the user scrolls through the list view using his mouse.
     * The method will attempt to change the list selection, scrolling up shall select
     * the element above, and scrolling below shall select the element below. Note that
     * this scrolling doesn't loop around; i.e: if you reach the last element and keep scrolling
     * downwards, you won't be looped back to the top, you'll remain at the last element.
     * @param e	The scroll event
     */
	private void onListViewScrolled(ScrollEvent e)
	{
    	int curIndex = listView.getSelectionModel().getSelectedIndex();
    	
		// Mouse scrolled up so select previous layer
		if(e.getDeltaY() > 0)
		{
			listView.getSelectionModel().selectPrevious();
		}
		// Mouse scrolled down so select next layer
		else if(e.getDeltaY() < 0)
		{
			listView.getSelectionModel().selectNext();
		}
		
		// Scroll to the selected layer if we changed our selection
		int newIndex = listView.getSelectionModel().getSelectedIndex();
		if(curIndex != newIndex)
			listView.scrollTo(newIndex);
		
		e.consume();
	}
}