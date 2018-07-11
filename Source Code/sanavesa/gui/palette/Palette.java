/***************************************************************************************************************************
 * Class:		Palette.java
 * Author:		Mohammad Alali
 * 
 * Description: The Palette class represents a glorified ListView that displays all the layers used in the program.
 * 	
 * Attributes: 	
 * 				static List<Layer> loadedLayers
 * 				boolean keyCombo
 * 		
 * Methods:		
 * 				Layer getSelectedLayer()
 * 				void save(ObjectOutputStream)
 * 				void reset()
 * 				void load(ObjectInputStream)
 * 
 ***************************************************************************************************************************/

package sanavesa.gui.palette;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import sanavesa.gui.IGraphicalInterface;
import sanavesa.gui.IKeyMapping;
import sanavesa.gui.colorSelector.ColorSelector;
import sanavesa.gui.frameDisplay.FrameDisplay;
import sanavesa.gui.popup.PermissionPopup;
import sanavesa.gui.popup.TextInputPopup;
import sanavesa.source.Frame;
import sanavesa.source.ISerializable;
import sanavesa.source.Layer;
import sanavesa.source.Pixel;

/**
 * The Palette class represents a glorified ListView that displays all
 * the layers used in the program.
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
public class Palette implements IGraphicalInterface, IKeyMapping, ISerializable
{
	/** A flag used for key shortcut combinations */
	private boolean keyCombo = false;
	
	/**
	 * The list of layers that is populated when loading from a file.
	 * This list is used so that pixels can select their appropriate layer
	 * upon loading phase.
	 * */
	public static final List<Layer> loadedLayers = new ArrayList<Layer>();
	
	/** The class serialization ID, used for file IO */
	private static final long serialVersionUID = -4832116557630698491L;
	
	/** The layout root node of the GUI */
	private Pane rootPane = new Pane();
	
	/** The list view that contains all the layers in the program */
	private ListView<Layer> listView = new ListView<Layer>(FXCollections.observableArrayList());
	
	/** The delete layer button */
    private Button deleteButton = new Button("Delete Layer");
    
    /** The add layer button */
    private Button addButton = new Button("Add Layer");
    
    /** The clear layer button */
    private Button clearButton = new Button("Clear Layer");
    
    /** The rename layer button */
    private Button renameButton = new Button("Rename Layer");
    
    /** The move up layer button */
    private Button moveUpButton = new Button("Move Layer Up");
    
    /** The move down layer button */
    private Button moveDownButton = new Button("Move Layer Down");
    
    /** The color selector GUI used to retrieve/set the current color */
    private ColorSelector colorSelector = null;
    
    /** The frame display GUI used to redraw canvas when toggling layer's visibility or deleting layers */
    private FrameDisplay frameDisplay = null;
    
    /** Create a palette with the specified reference parameters. */
    public Palette(ColorSelector colorSelector, FrameDisplay frameDisplay)
    {
    	this.colorSelector = colorSelector;
    	this.frameDisplay = frameDisplay;
    }
    
    /** Initialize the Palette's GUI Layout. */
    @Override
    public void initializeLayout()
	{
    	// Create a label for this gui
    	Label paletteLabel = new Label();
    	paletteLabel.setText("Palette");
    	paletteLabel.setStyle("-fx-font-weight: bold");
    	paletteLabel.setFont(new Font("Arial", 16));
    	
    	// Add the gui to the root of palette
    	rootPane.getChildren().addAll(listView, addButton, deleteButton, clearButton, renameButton, moveDownButton, moveUpButton, paletteLabel);
    	
    	// Make it such that we cant use [TAB] selection in the list view 
    	listView.setFocusTraversable(false);
    	
    	// Scroll through the elements using the mouse
    	// Using eventFilter instead of setOnScroll to block
    	// the default javaFX implementation from interfering with the scrolling
    	listView.addEventFilter(ScrollEvent.SCROLL, e -> onListViewScrolled(e));
    	
    	// Setup the visual display of the items in the list view
    	listView.setCellFactory(new Callback<ListView<Layer>, ListCell<Layer>>()
    	{
			@Override
			public ListCell<Layer> call(ListView<Layer> param)
			{
				return new LayerListViewCell(frameDisplay);
			}
		});
    	
    	// Called when the selected item changes
    	listView.getSelectionModel().selectedItemProperty().addListener(
    			(args, oldSelectedLayer, newSelectedLayer) -> onSelectionChanged(oldSelectedLayer, newSelectedLayer));
    	
    	// Called when the add button is clicked
    	addButton.setOnAction(e -> onAddButtonClicked());
    	addButton.setFocusTraversable(false);
    	addButton.setTooltip(new Tooltip("[CTRL + 2, A]: Adds a new layer"));
    	
    	// Called when the remove button is clicked
    	deleteButton.setOnAction(e -> onDeleteButtonClicked());
    	deleteButton.setFocusTraversable(false);
    	deleteButton.setTooltip(new Tooltip("[CTRL + 2, X]: Deletes the selected layer"));
    	
    	// Called when the clearbutton is clicked
    	clearButton.setOnAction(e -> onClearButtonClicked());
    	clearButton.setFocusTraversable(false);
    	clearButton.setTooltip(new Tooltip("[CTRL + 2, C]: Clears the selected layer's pixels in all frames."));
    	
    	// Called when the rename button is clicked
    	renameButton.setOnAction(e -> onRenameButtonClicked());
    	renameButton.setFocusTraversable(false);
    	renameButton.setTooltip(new Tooltip("[CTRL + 2, R]: Renames the selected layer"));
    	
    	// Called when the move up button is clicked
    	moveUpButton.setOnAction(e -> onMoveUpButtonClicked());
    	moveUpButton.setFocusTraversable(false);
    	moveUpButton.setTooltip(new Tooltip("[CTRL + 2, Left Arrow]: Moves the selected layer up"));
    	
    	// Called when the move down button is clicked
    	moveDownButton.setOnAction(e -> onMoveDownButtonClicked());
    	moveDownButton.setFocusTraversable(false);
    	moveDownButton.setTooltip(new Tooltip("[CTRL + 2, Right Arrow]: Moves the selected layer down"));
    	
    	// Add the default layer
    	listView.getItems().add(new Layer("Default Layer", Color.BLACK, true, 0));
    	
    	// Select the top most layer
    	listView.getSelectionModel().select(0);
    	
    	// Setup height of list view
    	listView.setMinHeight(200);
    	listView.setPrefHeight(Double.MAX_VALUE);
    	listView.setMaxHeight(400);
    	
    	// Position and Assign the sizes for the buttons
    	// Using runLater because the sizes need atleast a frame cycle to compute in JavaFX
    	Platform.runLater(() ->
    	{
    		double startY = listView.getHeight() + paletteLabel.getHeight();
    		double btnWidth = listView.getWidth() * 0.45;
    		double btnHeight = 16;
    		
    		// Setup the palette's label position
    		paletteLabel.relocate(listView.getWidth() / 2 - paletteLabel.getWidth() / 2, 0);
    		
    		listView.relocate(0, paletteLabel.getHeight());
    		
    		// Set up Add Button position and size
    		addButton.relocate(0, startY + 8);
    		addButton.setMinWidth(btnWidth);
    		addButton.setMinHeight(btnHeight);
    		
    		// Set up Delete Button position and size
    		deleteButton.relocate(listView.getWidth() - btnWidth, startY + 8);
    		deleteButton.setMinWidth(btnWidth);
    		deleteButton.setMinHeight(btnHeight);
    		
    		// Set up Clear Button position and size
    		clearButton.relocate(0, startY + 8 + (btnHeight + 16));
    		clearButton.setMinWidth(btnWidth);
    		clearButton.setMinHeight(btnHeight);
    		
    		// Set up Rename Button position and size
    		renameButton.relocate(listView.getWidth() - btnWidth, startY + 8 + (btnHeight + 16));
    		renameButton.setMinWidth(btnWidth);
    		renameButton.setMinHeight(btnHeight);
    		
    		// Set up Move Up Button position and size
    		moveUpButton.relocate(0, startY + 8 + 2 * (btnHeight + 16));
    		moveUpButton.setMinWidth(btnWidth);
    		moveUpButton.setMinHeight(btnHeight);
    		
    		// Set up Rename Button position and size
    		moveDownButton.relocate(listView.getWidth() - btnWidth, startY + 8 + 2 * (btnHeight + 16));
    		moveDownButton.setMinWidth(btnWidth);
    		moveDownButton.setMinHeight(btnHeight);
    	});
    }
    
    /**
     * Sets the selected layer of the palette.
     * @param layer the layer to set
     */
    public void setSelectedLayer(Layer layer)
    {
    	listView.getSelectionModel().select(layer);
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

	/** Called when the list selection changes */
    private void onSelectionChanged(Layer oldSelectedLayer, Layer newSelectedLayer)
    {
		// Get selected item index
		int selectedItemIndex = listView.getSelectionModel().getSelectedIndex();
		
		// Disable the move up button if selecting top most layer
		moveUpButton.setDisable(selectedItemIndex == 0);
		
		// Disable the move down button if selecting the bottom most layer
		moveDownButton.setDisable(selectedItemIndex == listView.getItems().size() - 1);
		
		// Disable delete button if selecting no layer or only 1 layer remains 
		deleteButton.setDisable(newSelectedLayer == null || 
				listView.getItems().size() == 1);
		
		// Disable rename button if selecting no layer
		renameButton.setDisable(newSelectedLayer == null);
		
		// Remove the binding of the old layer to the color selector
		if(oldSelectedLayer != null)
			oldSelectedLayer.colorProperty().unbind();
		
		// Update the binding of the new selected layer to the color selector
		if(newSelectedLayer != null)
		{
			colorSelector.setColor(newSelectedLayer.getColor());
			newSelectedLayer.colorProperty().bind(colorSelector.colorProperty());
			listView.scrollTo(newSelectedLayer);
		}
    }
    
    /**
     * Swaps two items in the list view.
     * @param firstIndex	the first index to swap
     * @param secondIndex	the second index to swap
     */
    private void swapLayers(int firstIndex, int secondIndex)
    {
    	Layer firstLayer = listView.getItems().get(firstIndex);
		Layer secondLayer = listView.getItems().get(secondIndex);
		
		listView.getItems().set(secondIndex, firstLayer);
		listView.getItems().set(firstIndex, secondLayer);
		
		// Swap the depths of the layers
		firstLayer.setDepth(secondIndex);
		secondLayer.setDepth(firstIndex);
	}

    /** Clicked when the delete button is clicked */
    private void onDeleteButtonClicked()
    {
    	PermissionPopup popup = new PermissionPopup("Delete Layer");
    	popup.setupCancelButton("Cancel");
    	popup.setupOkButton("Remove");
    	popup.setupDisplayLabel("Are you sure?");
    	popup.show();
    	boolean response = popup.getResponse();
    	
    	if(response)
    	{
    		// Delete the selected layer
    		Layer selectedLayer = listView.getSelectionModel().getSelectedItem();
    		listView.getItems().remove(selectedLayer);
    		
    		onLayerDeleted(selectedLayer);
    		onLayerListModified();
    	}
    }
    
    /** Clicked when the clear button is clicked */
    private void onClearButtonClicked()
    {
    	PermissionPopup popup = new PermissionPopup("Clear All Pixels Using Layer");
    	popup.setupCancelButton("Cancel");
    	popup.setupOkButton("Clear");
    	popup.setupDisplayLabel("Are you sure?");
    	popup.show();
    	boolean response = popup.getResponse();
    	
    	if(response)
    	{
    		// Clear all pixels using the selected layer
    		Layer selectedLayer = listView.getSelectionModel().getSelectedItem();
    		
    		for(Frame frame : frameDisplay.getFrames())
    		{
    			for(Iterator<Pixel> iterator = frame.getPixels().iterator(); iterator.hasNext();)
    			{
    				Pixel p = iterator.next();
    				if(p.getLayer() == selectedLayer)
    				{
    					iterator.remove();
    					frame.removePixel(p);
    				}
    			}
    		}
    		
    		frameDisplay.requestCanvasRedraw();
    	}
    }
    
    /** Clicked when the add button is clicked */
    private void onAddButtonClicked()
    {
    	final ObservableList<Layer> layers = listView.getItems();
		final String[] prohibitedStrings = new String[layers.size()];
    	
    	// All previous layer names are invalid
    	for(int i = 0; i < layers.size(); i++)
    	{
    		prohibitedStrings[i] = layers.get(i).getName();
    	}
    	
    	TextInputPopup popup = new TextInputPopup("Add a New Layer");
    	popup.setupCancelButton("Cancel");
    	popup.setupInstructionLabel("Layer's Name:");
    	popup.setupOkButton("Add");
    	popup.setupTextField("NewLayer" + (layers.size()+1), "Enter a layer name", prohibitedStrings, 
    			3, 24, "Layer's name is too short!", "Layer's name is too long!", "Layer's name is already in use!");
    	popup.show();
    	
    	String response = popup.getResponse().trim(); 
    	if(response.length() > 0)
    	{
    		// Add the layer
    		Layer addedLayer = new Layer(response, Color.RED, true, 0);
    		layers.add(addedLayer);
    		listView.getSelectionModel().select(addedLayer);
    		updateLayerDepths();
    	}
    }
    
    /** Clicked when the rename button is clicked */
    private void onRenameButtonClicked()
    {
    	final ObservableList<Layer> layers = listView.getItems();
		final String[] prohibitedStrings = new String[layers.size() - 1];
    	Layer selectedLayer = listView.getSelectionModel().getSelectedItem();
    	
    	// All previous layer names are invalid, except for our current selected layer, which should be valid
    	int prohibitedStringsIndex = 0;
    	for(int i = 0; i < layers.size(); i++)
    	{
    		Layer layer = layers.get(i);
    		if(selectedLayer != layer)
    		{
    			prohibitedStrings[prohibitedStringsIndex++] = layer.getName();
    		}
    	}
    	
    	TextInputPopup popup = new TextInputPopup("Rename Layer");
    	popup.setupCancelButton("Cancel");
    	popup.setupInstructionLabel("New Layer's Name:");
    	popup.setupOkButton("Rename");
    	popup.setupTextField(selectedLayer.getName(), "Enter a new layer name", prohibitedStrings, 
    			3, 24, "Layer's name is too short!", "Layer's name is too long!", "Layer's name is already in use!");
    	popup.show();
    	
    	String response = popup.getResponse().trim(); 
    	if(response.length() > 0)
    	{
    		// Rename the layer
    		selectedLayer.setName(response);
    	}
    }
    
    /** Clicked when the move up button is clicked */
    private void onMoveUpButtonClicked()
    {
    	int currentLayerIndex = listView.getSelectionModel().getSelectedIndex();
		int upperLayerIndex = listView.getSelectionModel().getSelectedIndex() - 1;
		
		// Swap layers
		swapLayers(currentLayerIndex, upperLayerIndex);
		
		// Follow our initial selection
		listView.getSelectionModel().select(upperLayerIndex);
		
		onLayerListModified();
    }
    
    /** Clicked when the move down button is clicked */
    private void onMoveDownButtonClicked()
    {
    	int currentLayerIndex = listView.getSelectionModel().getSelectedIndex();
		int lowerLayerIndex = listView.getSelectionModel().getSelectedIndex() + 1;
		
		// Swap layers
		swapLayers(currentLayerIndex, lowerLayerIndex);
		
		// Follow our in itial selection
		listView.getSelectionModel().select(lowerLayerIndex);
	
		onLayerListModified();
    }
    
    /**
     * Called whenever a layer is deleted.
     * All pixels using the deleted layer will be deleted too.
     */ 
    private void onLayerDeleted(Layer layer)
    {
    	// Remove all pixels in that used this layer
		for(Frame frame : frameDisplay.getFrames())
		{
			for(Iterator<Pixel> iterator = frame.getPixels().iterator(); iterator.hasNext();)
			{
				Pixel pixel = iterator.next();
				if(pixel.getLayer() == layer)
				{
					iterator.remove();
				}
			}
		}
    }
    
    /** Whenever the palette list is modified, request a canvas redraw */
    private void onLayerListModified()
    {
    	frameDisplay.requestCanvasRedraw();
    }
    
    /** Updates all layers in the palette to match their index in the list */
    private void updateLayerDepths()
    {
    	// Set the depth of the layers to its position in the list
    	ObservableList<Layer> layers = listView.getItems();
    	for(int i = 0; i < layers.size(); i++)
    	{
    		layers.get(i).setDepth(i);
    	}
    }

    /** Returns the root pane of the Palette's GUI */
	@Override
	public Pane getRoot()
	{
		return rootPane;
	}
	
	/**
	 * @return the selected layer in the GUI, null if not selecting an item
	 */
	public Layer getSelectedLayer()
	{
		return listView.getSelectionModel().getSelectedItem();
	}

	/**
	 * Sets up the key binding shortcuts.
	 * <ul>
	 * <li> [Shift + 2, A] : Adds a Layer </li>
	 * <li> [Shift + 2, X] : Deletes a Layer </li>
	 * <li> [Shift + 2, R] : Renames a Layer </li>
	 * <li> [Shift + 2, C] : Clears a Layer </li>
	 * <li> [CTRL + 2, Left] : Moves a Layer in the hierarchy up by 1 </li>
	 * <li> [CTRL + 2, Right] : Moves a Layer in the hierarchy down by 1 </li>
	 * <li> [CTRL + 2, Up] : Select the previous Layer </li>
	 * <li> [CTRL + 2, Down] : Select the next Layer </li>
	 * </ul>
	 */
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			switch(event.getCode())
			{
			case DIGIT2:
				if(event.isControlDown())
					keyCombo = true;
				break;
			// Add layer
			case A:
				if(keyCombo)
				{
					addButton.fire();
					keyCombo = false;
				}
				break;
			// Delete layer
			case X:
				if(keyCombo)
				{
					deleteButton.fire();
					keyCombo = false;
				}
				break;
			// Rename layer
			case R:
				if(keyCombo)
				{
					renameButton.fire();
					keyCombo = false;
				}
				break;
				
			// Clear layer
			case C:
				if(keyCombo)
				{
					clearButton.fire();
					keyCombo = false;
				}
				break;
			// Select layer Above
			case UP:
				if(keyCombo)
				{
					listView.getSelectionModel().selectPrevious();
					keyCombo = false;
				}
				break;
			// Select layerBelow
			case DOWN:
				if(keyCombo)
				{
					listView.getSelectionModel().selectNext();
					keyCombo = false;
				}
				break;	
			// Move layer Above
			case LEFT:
				if(keyCombo)
				{
					moveUpButton.fire();
					keyCombo = false;
				}
				break;
			// Move layer Below
			case RIGHT:
				if(keyCombo)
				{
					moveDownButton.fire();
					keyCombo = false;
				}
				break;
			default:
				keyCombo = false;
				break;
			}
		});
	}
	
	/** Saves the palette data to a file stream */
	@Override
	public void save(ObjectOutputStream out) throws IOException
	{
		// Saves each layer in the palette to the stream
		ObservableList<Layer> layers = listView.getItems();
		int size = layers.size(); 
		out.writeInt(size);
		for(int i = 0; i < size; i++)
		{
			layers.get(i).save(out);
		}
	}

	/** Loads the palette data from a file stream */
	@Override
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// Clear the layers, and load them 1 by 1 into the listView
		ObservableList<Layer> layers = listView.getItems();
		int size = in.readInt();
		layers.clear();
		for(int i = 0; i < size; i++)
		{
			Layer layer = new Layer("", null, true, 0);
			layer.load(in);
			loadedLayers.add(layer);
			layers.add(layer);
		}
		listView.getSelectionModel().select(0);
	}
	
	/** Resets the palette to the default values */
	@Override
	public void reset()
	{
		// Clears the layers, and adds in a default black layer
		ObservableList<Layer> layers = listView.getItems();
		layers.setAll(new Layer("Default Layer", Color.RED, true, 0));
		listView.getSelectionModel().select(0);
		
		// Sets the luminosity factor of the color selector to 0
		colorSelector.setLuminosityFactor(0);
	}
}