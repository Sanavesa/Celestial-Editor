/***************************************************************************************************************************
 * Class:		TopToolBar.java
 * Author:		Mohammad Alali
 * 
 * Description: This class will create the brush tools and the interaction required. It will handle input.
 * 	
 * Attributes: 	
 * 				Nothing Interesting
 * 		
 * Methods:		
 * 				Nothing Intersting
 * 
 ***************************************************************************************************************************/

package sanavesa.gui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.gui.frameDisplay.FrameDisplay;
import sanavesa.gui.popup.MessagePopup;
import sanavesa.gui.popup.PermissionPopup;
import sanavesa.gui.popup.SizeInputPopup;
import sanavesa.source.Frame;
import sanavesa.source.Pixel;
import sanavesa.source.Project;

/**
 * Contains the GUI in the top tool bar and handles the user interaction.
 * @author Mohammad Alali
 */
public class TopToolBar implements IGraphicalInterface, IKeyMapping
{
	// Loads all the images used for the tools from the system
	private static final Image imgSave = new Image("/art/Save.png");
	private static final Image imgOpen = new Image("/art/Open.png");
	private static final Image imgNew = new Image("/art/New.png");
	private static final Image imgGridLines = new Image("/art/GridLines.png");
	private static final Image imgExport = new Image("/art/Export.png");
	private static final Image imgPlay = new Image("/art/Play.png");
	private static final Image imgStop = new Image("/art/Stop.png");
	private static final Image imgResetView = new Image("art/ResetView.png");
	private static final Image imgResize = new Image("art/ResizeArrow.png");
	private static final Image imgShift = new Image("art/ShiftArrow.png");
	private static final Image imgOnionSkin = new Image("art/OnionSkin.png");
	
	// Declare the references
	private Project project = null;
	private FrameDisplay frameDisplay = null;
	private PixelatedCanvas canvas = null;
	
	// Create the toolbar
	private ToolBar toolBar = new ToolBar();
	
	// Create the buttons of the top tool bar
	private Button btnSave = new Button("Save");
	private Button btnSaveAs = new Button("Save As");
	private Button btnOpen = new Button("Open");
	private Button btnNew = new Button("New");
	private Button btnExportSelected = new Button("Export Selected");
	private Button btnExportAll = new Button("Export All");
	private Button btnGridLines = new Button("Toggle Grid Lines");
	private Button btnResetView = new Button("Reset View");
	private Button btnResizeFrame = new Button("Resize");
	private Button btnShiftFrame = new Button("Shift");
	private Button btnOnionSkin = new Button("Toggle Onion Skinning");
	private Label labelOnionSkinFactor = new Label();
	private Slider sliderOnionSkinFactor = new Slider(0.0, 1.0, 0.0);
	private Button btnPlayAnimation = new Button("Play");
	private Button btnStopAnimation = new Button("Stop");
	private Label labelAnimationSpeed = new Label();
	private Slider sliderAnimationSpeed = new Slider(0, 1000, 150);
	private Timeline animationTimeline = new Timeline();
	private Label canvasBackgroundColorLabel = new Label("Background Color");
	private ColorPicker canvasBackgroundColorPicker = new ColorPicker(Color.WHITE);

	/** Creates the top tool bar */
	public TopToolBar(Project project, FrameDisplay frameDisplay, PixelatedCanvas canvas)
	{
		this.project = project;
		this.frameDisplay = frameDisplay;
		this.canvas = canvas;
	}
	
	@Override
	public void initializeLayout()
	{
		animationTimeline.setCycleCount(Timeline.INDEFINITE);
		animationTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(150), e -> cycleFramesInAnimation()));
		
		// Load the images and set the ratio
		ImageView saveImgView = new ImageView(imgSave);
		saveImgView.setPreserveRatio(true);
		saveImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView saveAsImgView = new ImageView(imgSave);
		saveAsImgView.setPreserveRatio(true);
		saveAsImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView openImgView = new ImageView(imgOpen);
		openImgView.setPreserveRatio(true);
		openImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView newImgView = new ImageView(imgNew);
		newImgView.setPreserveRatio(true);
		newImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView gridLinesImgView = new ImageView(imgGridLines);
		gridLinesImgView.setPreserveRatio(true);
		gridLinesImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView exportAllImgView = new ImageView(imgExport);
		exportAllImgView.setPreserveRatio(true);
		exportAllImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView exportSelectedImgView = new ImageView(imgExport);
		exportSelectedImgView.setPreserveRatio(true);
		exportSelectedImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView playImgView = new ImageView(imgPlay);
		playImgView.setPreserveRatio(true);
		playImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView stopImgView = new ImageView(imgStop);
		stopImgView.setPreserveRatio(true);
		stopImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView resizeImgView = new ImageView(imgResize);
		resizeImgView.setPreserveRatio(true);
		resizeImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView shiftImgView = new ImageView(imgShift);
		shiftImgView.setPreserveRatio(true);
		shiftImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView onionSkinImgView = new ImageView(imgOnionSkin);
		onionSkinImgView.setPreserveRatio(true);
		onionSkinImgView.setFitHeight(20);
		
		// Load the images and set the ratio
		ImageView resetViewImgView = new ImageView(imgResetView);
		resetViewImgView.setPreserveRatio(true);
		resetViewImgView.setFitHeight(20);
		
		// Set the Graphic of each button
		btnSave.setGraphic(saveImgView);
		btnSaveAs.setGraphic(saveAsImgView);
		btnOpen.setGraphic(openImgView);
		btnNew.setGraphic(newImgView);
		btnGridLines.setGraphic(gridLinesImgView);
		btnExportAll.setGraphic(exportAllImgView);
		btnExportSelected.setGraphic(exportSelectedImgView);
		btnPlayAnimation.setGraphic(playImgView);
		btnStopAnimation.setGraphic(stopImgView);
		btnResizeFrame.setGraphic(resizeImgView);
		btnShiftFrame.setGraphic(shiftImgView);
		btnResetView.setGraphic(resetViewImgView);
		btnOnionSkin.setGraphic(onionSkinImgView);
		
		// Disable focus, makes it look better
		btnSave.setFocusTraversable(false);
		btnSaveAs.setFocusTraversable(false);
		btnOpen.setFocusTraversable(false);
		btnNew.setFocusTraversable(false);
		btnGridLines.setFocusTraversable(false);
		btnExportAll.setFocusTraversable(false);
		btnExportSelected.setFocusTraversable(false);
		btnPlayAnimation.setFocusTraversable(false);
		btnStopAnimation.setFocusTraversable(false);
		btnResizeFrame.setFocusTraversable(false);
		btnShiftFrame.setFocusTraversable(false);
		btnResetView.setFocusTraversable(false);
		btnOnionSkin.setFocusTraversable(false);
		
		// Descriptive Tool tips over each button
		btnSave.setTooltip(new Tooltip("[CTRL + S] Saves the project."));
		btnSaveAs.setTooltip(new Tooltip("[CTRL + ALT + S] Saves the project to a different location."));
		btnNew.setTooltip(new Tooltip("[CTRL + N] Creates a new project."));
		btnOpen.setTooltip(new Tooltip("[CTRL + O] Opens a project."));
		btnExportAll.setTooltip(new Tooltip("[CTRL + E] Exports all frames."));
		btnExportSelected.setTooltip(new Tooltip("[CTRL + F] Exports the selected frame."));
		btnGridLines.setTooltip(new Tooltip("[G] Toggles the visibility of the grid lines."));
		btnPlayAnimation.setTooltip(new Tooltip("[CTRL + SPACE] Starts or stops the animation."));
		btnStopAnimation.setTooltip(new Tooltip("[CTRL + SPACE] Starts or stops the animation."));
		btnResetView.setTooltip(new Tooltip("[R] Resets the zooming and panning of the view."));
		btnResizeFrame.setTooltip(new Tooltip("[Y] Sets the size of all of the frames.\nRemoves all pixels outside of the view."));
		btnShiftFrame.setTooltip(new Tooltip("[U] Shifts all of the pixels in the selected frame by the given amount.\nRemoves all pixels outside of the view."));
		btnOnionSkin.setTooltip(new Tooltip("[V] Toggles the visibility of onion skinning."));
		
		// Button action setup
		btnSave.setOnAction(e -> onBtnSaveClicked());
		btnSaveAs.setOnAction(e -> onBtnSaveAsClicked());
		btnOpen.setOnAction(e -> onBtnOpenClicked());
		btnNew.setOnAction(e -> onBtnNewClicked());
		btnExportAll.setOnAction(e -> onBtnExportAllClicked());
		btnExportSelected.setOnAction(e -> onBtnExportSelectedClicked());
		btnGridLines.setOnAction(e -> canvas.setDisplayGridLines(!canvas.getDisplayGridLines()));
		btnPlayAnimation.setOnAction(e -> onBtnPlayAnimationClicked());
		btnStopAnimation.setOnAction(e -> onBtnStopAnimationClicked());
		btnResetView.setOnAction(e -> canvas.resetView());
		btnResizeFrame.setOnAction(e -> onBtnResizeFrameClicked());
		btnShiftFrame.setOnAction(e -> onBtnShiftFrameClicked());
		btnOnionSkin.setOnAction(e -> canvas.setOnionSkinning(!canvas.isOnionSkinning()));
		
		sliderOnionSkinFactor.valueProperty().addListener((args, oldV, newV) ->
		{
			canvas.setOnionSkinningFactor(newV.doubleValue());
			labelOnionSkinFactor.setText(String.format("Factor: %.2f", newV.doubleValue()));
		});
		sliderOnionSkinFactor.setTranslateY(5);
		sliderOnionSkinFactor.setMajorTickUnit(0.25);
		sliderOnionSkinFactor.setShowTickMarks(true);
		sliderOnionSkinFactor.setMinorTickCount(4);
		sliderOnionSkinFactor.setPrefWidth(100);
		
		sliderOnionSkinFactor.setValue(canvas.getOnionSkinningFactor());
		labelOnionSkinFactor.setPadding(new Insets(0, 5, 0, 5));
		labelOnionSkinFactor.setTooltip(new Tooltip("The opacity factor for the onion skinning."));
		
		sliderAnimationSpeed.valueProperty().addListener((args, oldV, newV) -> onAnimationSpeedChanged(newV.doubleValue()));
		sliderAnimationSpeed.setTranslateY(5);
		sliderAnimationSpeed.setMajorTickUnit(250);
		sliderAnimationSpeed.setShowTickMarks(true);
		sliderAnimationSpeed.setMinorTickCount(4);
		sliderAnimationSpeed.setPrefWidth(100);
		
		labelAnimationSpeed.textProperty().bind(sliderAnimationSpeed.valueProperty().asString("Speed: %.0f ms"));
		labelAnimationSpeed.setPadding(new Insets(0, 5, 0, 5));
		labelAnimationSpeed.setTooltip(new Tooltip("The animation playback speed in milliseconds."));
		
		canvasBackgroundColorPicker.valueProperty().addListener((args, oldColor, newColor) ->
		{
			// Update the pixelated canvas background color
			canvas.setCanvasClearColor(newColor);
		});
		
		// Updates the size of the frame and centers it on view
		int newWidth = Frame.getFrameWidth();
		int newHeight = Frame.getFrameHeight();
		
		Frame.resizeFrame(newWidth, newHeight);
		canvas.setWidth(Program.pixelScale * newWidth);
		canvas.setHeight(Program.pixelScale * newHeight);
		canvas.setTranslateX(0);
		canvas.setTranslateY(0);
		canvas.setZoomScale((32 * 1.5) / Math.max(newWidth, newHeight));
		canvas.requestRedraw();
		canvas.redrawGridLines();
		
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		
		// Add all the graphics to the toolbar
		toolBar.getItems().addAll(
				btnNew, btnOpen, btnSave, btnSaveAs,
				new Separator(Orientation.HORIZONTAL),
				btnExportAll, btnExportSelected,
				new Separator(Orientation.HORIZONTAL),
				canvasBackgroundColorLabel, canvasBackgroundColorPicker, btnGridLines,
				new Separator(Orientation.HORIZONTAL),
				btnResetView, btnResizeFrame, btnShiftFrame,
				new Separator(Orientation.HORIZONTAL),
				btnOnionSkin, labelOnionSkinFactor, sliderOnionSkinFactor,
				new Separator(Orientation.HORIZONTAL),
				//pane,
				btnPlayAnimation, btnStopAnimation, labelAnimationSpeed,
				sliderAnimationSpeed);
	}
	
	/** Called when the resize button is clicked */
	private void onBtnResizeFrameClicked()
	{
		// Display tuple popup
		SizeInputPopup popup = new SizeInputPopup("Resize Frames");
		popup.setupCancelButton("Cancel");
		popup.setupInstructionLabel1("Frame Width");
		popup.setupInstructionLabel2("Frame Height");
		popup.setupOkButton("Resize");
		popup.setupTextField1(String.valueOf(Frame.getFrameWidth()), "Frame Width");
		popup.setupTextField2(String.valueOf(Frame.getFrameHeight()), "FrameHeight");
		popup.show();
		
		// Attempt to resize the frame
		try{
			int newWidth = Integer.parseInt(popup.getResponse1());
			int newHeight = Integer.parseInt(popup.getResponse2());
			
			// Reize the frame
			Frame.resizeFrame(newWidth, newHeight);
			
			canvas.setWidth(Program.pixelScale * newWidth);
			canvas.setHeight(Program.pixelScale * newHeight);
			
			canvas.setTranslateX(0);
			canvas.setTranslateY(0);
			
			// Reset view of the canvas and redraw it 
			canvas.setZoomScale((32 * 1.5) / Math.max(newWidth, newHeight));
			canvas.requestRedraw();
			canvas.redrawGridLines();
		}
		catch(Exception e)
		{
		}
	}

	/** Called when the shift button is clicked */
	private void onBtnShiftFrameClicked()
	{
		SizeInputPopup popup = new SizeInputPopup("Shift Selected Frame");
		popup.setupCancelButton("Cancel");
		popup.setupInstructionLabel1("X-Shift Amount");
		popup.setupInstructionLabel2("Y-Shift Amount");
		popup.setupOkButton("Shift");
		popup.setupTextField1("0", "X-Shift");
		popup.setupTextField2("0", "Y-Shift");
		popup.show();
		try{
			int shiftX = Integer.parseInt(popup.getResponse1());
			int shiftY = Integer.parseInt(popup.getResponse2());
			canvas.shiftFrame(shiftX, shiftY);
			canvas.requestRedraw();
			canvas.redrawGridLines();
		}
		catch(Exception e)
		{
		}
	}

	/** Called when the animation speed slider is modified */
	private void onAnimationSpeedChanged(double newSpeed)
	{
		if(newSpeed != 0)
			animationTimeline.setRate(150 / newSpeed);
		else
			animationTimeline.setRate(0);
	}

	/** Cycles through the frames as in an animation */
	private void cycleFramesInAnimation()
	{
		ListView<Frame> frames = frameDisplay.getListViewFrames();
		
		if(frames.getSelectionModel().getSelectedIndex() < frames.getItems().size() - 1)
			frameDisplay.getListViewFrames().getSelectionModel().selectNext();
		else
			frameDisplay.getListViewFrames().getSelectionModel().select(0);
	}

	/** Called when the stop button is clicked */
	private void onBtnStopAnimationClicked()
	{
		animationTimeline.stop();
	}

	/** Called when the play button is clicked */
	private void onBtnPlayAnimationClicked()
	{
		animationTimeline.play();
	}
	
	/** Called when the export selected button is clicked */
	private void onBtnExportSelectedClicked()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.setTitle("Choose Location to Export Image to");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG (*.png)", "|*.png"));
		
		WritableImage writableImage = null;
		RenderedImage renderedImage = null;
		
		File exportFile = fileChooser.showSaveDialog(null);
		Frame frame = frameDisplay.getSelectedFrame();
		
		// If our target file we chose isnt empty, export png
		if(exportFile != null)
		{
			try
			{
				writableImage = exportFrame(frame);
				
				renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
				
				ImageIO.write(renderedImage, "png", exportFile);
				
				MessagePopup message = new MessagePopup("Successfully exported!");
				message.setupOkButton("Ok");
				message.setupDisplayLabel("Successfully exported image to " + exportFile.getAbsolutePath() + "!");
				message.show();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				MessagePopup message = new MessagePopup("Failed To Export!");
				message.setupOkButton("Ok");
				message.setupDisplayLabel("Failed to export image!");
				message.show();
				System.out.println("Failed to export!");
			}
		}
	}

	/** Called when the export all button is clicked */
	private void onBtnExportAllClicked()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		directoryChooser.setTitle("Choose Location to Export Images to");
		
		File directory = directoryChooser.showDialog(null);
		
		WritableImage writableImage = null;
		RenderedImage renderedImage = null;
		
		File file = null;
		
		// If we chose a directory, export the png
		if(directory != null)
		{
			try
			{
				for(Frame frame : frameDisplay.getFrames())
				{
					writableImage = exportFrame(frame);
					
					renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
					file = new File(directory.getAbsolutePath() + "//" + frame.getName() + ".png");
					ImageIO.write(renderedImage, "png", file);
				}
				MessagePopup message = new MessagePopup("Successfully exported!");
				message.setupOkButton("Ok");
				message.setupDisplayLabel("Successfully exported images to " + directory.getAbsolutePath() + "!");
				message.show();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				MessagePopup message = new MessagePopup("Failed To Export!");
				message.setupOkButton("Ok");
				message.setupDisplayLabel("Failed to export images!");
				message.show();
				System.out.println("Failed to export!");
			}
		}
	}

	/** Called when the new button is clicked */
	private void onBtnNewClicked()
	{
		PermissionPopup popup = new PermissionPopup("New Project");
		popup.setupCancelButton("Cancel");
		popup.setupOkButton("New Project");
		popup.setupDisplayLabel("Are you sure?");
		popup.show();
		
		if(popup.getResponse())
		{
			canvas.resetView();
			project.newProject();
		}
	}
	
	/** Called to load .PXL files. */
	public void loadPXLFile(String filePath)
	{
		File file = new File(filePath);
		if(!file.exists())
			return;
		
		canvas.resetView();
		project.setProjectFile(file);
		project.loadProject();
		
		canvas.setWidth(Program.pixelScale * Frame.getFrameWidth());
		canvas.setHeight(Program.pixelScale * Frame.getFrameHeight());
		
		canvas.setTranslateX(0);
		canvas.setTranslateY(0);
		
		canvas.setZoomScale((32 * 1.5) / Math.max(Frame.getFrameWidth(), Frame.getFrameHeight()));
		canvas.requestRedraw();
		canvas.redrawGridLines();
	}

	/** Called when the open button is clicked */
	private void onBtnOpenClicked()
	{
		File loadFile = project.showOpenFileDialog();
		
		if(loadFile != null)
		{
			canvas.resetView();
			project.setProjectFile(loadFile);
			project.loadProject();
			
			canvas.setWidth(Program.pixelScale * Frame.getFrameWidth());
			canvas.setHeight(Program.pixelScale * Frame.getFrameHeight());
			
			canvas.setTranslateX(0);
			canvas.setTranslateY(0);
			
			canvas.setZoomScale((32 * 1.5) / Math.max(Frame.getFrameWidth(), Frame.getFrameHeight()));
			canvas.requestRedraw();
			canvas.redrawGridLines();
		}
	}

	/** Called when the save as button is clicked */
	private void onBtnSaveAsClicked()
	{
		File saveFile = project.showSaveFileDialog();
		
		if(saveFile != null)
		{
			canvas.resetView();
			project.setProjectFile(saveFile);
			project.saveProject(saveFile);
		}
	}

	/** Called when the save button is clicked */
	private void onBtnSaveClicked()
	{
		if(project.getProjectFile() == null)
		{
			File saveFile = project.showSaveFileDialog();
			project.setProjectFile(saveFile);
		}
		
		project.saveProject(project.getProjectFile());
	}

	/**
	 * @return the toolbar root
	 */
	public ToolBar getToolBar()
	{
		return toolBar;
	}

	@Override
	public void initializeKeyMap(Scene scene)
	{
		// Set up the key shortcuts
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			switch(event.getCode())
			{
			case S:
				if(event.isControlDown() && !event.isAltDown())
					btnSave.fire();
				else if(event.isControlDown() && event.isAltDown())
					btnSaveAs.fire();
				break;
				
			case O:
				if(event.isControlDown())
					btnOpen.fire();
				break;
				
			case N:
				if(event.isControlDown())
					btnNew.fire();
				break;
				
			case E:
				if(event.isControlDown())
					btnExportAll.fire();
				break;
				
			case F:
				if(event.isControlDown())
					btnExportSelected.fire();
				break;
			
			case G:
				btnGridLines.fire();
			break;
			
			case V:
				btnOnionSkin.fire();
				break;
			
			case SPACE:
				if(event.isControlDown())
				{
					if(animationTimeline.getStatus() == Animation.Status.RUNNING)
						btnStopAnimation.fire();
					else
						btnPlayAnimation.fire();
				}
				break;
			case Y:
				btnResizeFrame.fire();
				break;
			case U:
				btnShiftFrame.fire();
				break;
			default:
				break;
			}
		});
	}
	
	public WritableImage exportFrame(Frame frame)
	{
		WritableImage writableImage = new WritableImage(Frame.getFrameWidth(), Frame.getFrameHeight());
		
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

			Canvas miniCanvas = new Canvas(Frame.getFrameWidth(), Frame.getFrameHeight());
			GraphicsContext canvasGraphics = miniCanvas.getGraphicsContext2D();
			
			canvasGraphics.clearRect(0, 0, miniCanvas.getWidth(), miniCanvas.getHeight());
			
			// Draw each pixel in order of ascending depth (background first, foreground last)
			for(Pixel pixel : pixelsList)
			{
				// Draw the pixel only if its layer is visible
				if(pixel.getLayer().getVisibility())
				{
					canvasGraphics.setFill(pixel.getColor());
					canvasGraphics.fillRect(pixel.getX(), pixel.getY(), 1, 1);
				}
			}
			
			SnapshotParameters sp = new SnapshotParameters();
			sp.setFill(Color.TRANSPARENT); // Have a transparent background
			miniCanvas.snapshot(sp, writableImage);
		}
		
		return writableImage;
	}
}
