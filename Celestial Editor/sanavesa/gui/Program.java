/***************************************************************************************************************************
 * Class:		Program.java
 * Author:		Mohammad Alali
 * 
 * Description: The Program class contains the main method, which is the main entry point of the program.
 * 	
 * Attributes: 	
 * 				static int pixelScale
 * 		
 * Methods:		
 * 				static void main
 * 
 ***************************************************************************************************************************/
package sanavesa.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import sanavesa.gui.brushTool.BrushToolHotbar;
import sanavesa.gui.brushTool.BrushToolManager;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.gui.colorSelector.ColorSelector;
import sanavesa.gui.frameDisplay.FrameDisplay;
import sanavesa.gui.palette.Palette;
import sanavesa.gui.popup.PermissionPopup;
import sanavesa.source.Frame;
import sanavesa.source.ISerializable;
import sanavesa.source.Project;


/**
 * The Program class contains the main method, which
 * is the main entry point of the program.
 * @author Mohammad Alali
 */
public class Program extends Application
{
	private static String[] args;
	
	/** The main entry point of the program */
	public static void main(String[] args)
	{
		Program.args = args;
		launch(args);
	}
	
	public static int pixelScale = 16;
	
	/** Initializes the program's GUI */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane);
		
		// The canvas clipping pane
		StackPane pane = new StackPane();
		pane.relocate(0, 0);
		pane.setStyle("-fx-border-color: black;");
		pane.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		pane.setMinSize(32*26, 32*26);
        pane.setPrefSize(32*26, 32*26);
        pane.setMaxSize(32*26, 32*26);
        
        // Scale the program with the width 
		primaryStage.widthProperty().addListener((args, oldW, newW) ->
		{
			double scaleFactor = Math.min(newW.doubleValue() / primaryStage.getMinWidth(), primaryStage.getHeight() / primaryStage.getMinHeight());
			Scale scale = new Scale(scaleFactor, scaleFactor);
			scale.setPivotX(0);
			scale.setPivotY(0);
			pane.getTransforms().setAll(scale);
		});
		
		// Scale the program with the height
		primaryStage.heightProperty().addListener((args, oldH, newH) ->
		{
			double scaleFactor = Math.min(newH.doubleValue() / primaryStage.getMinHeight(), primaryStage.getWidth() / primaryStage.getMinWidth());
			Scale scale = new Scale(scaleFactor, scaleFactor);
			scale.setPivotX(0);
			scale.setPivotY(0);
			pane.getTransforms().setAll(scale);
		});
		
		// Set the minimum size of the program
		primaryStage.setMinWidth(1632);
		primaryStage.setMinHeight(1000);
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);	
		
		// Load in the icon for the application
		primaryStage.getIcons().add(new Image("art/Icon.png")); // Add in the icon
		
		// By default, start with a 64x64 image
		Frame.resizeFrame(64, 64);
		
		// Initialize the canvas
		Canvas gridLinesCanvas = new Canvas(Frame.getFrameWidth() * pixelScale, Frame.getFrameHeight() * pixelScale);
		Canvas temporaryDrawCanvas = new Canvas(Frame.getFrameWidth() * pixelScale, Frame.getFrameHeight() * pixelScale);
		PixelatedCanvas canvas = new PixelatedCanvas(Frame.getFrameWidth() * pixelScale, Frame.getFrameHeight() * pixelScale, gridLinesCanvas, temporaryDrawCanvas); //32*28 perfect
		canvas.initializeKeyMap(scene);
		
		// Ask for permission to close when pressing the X button
		primaryStage.setOnCloseRequest(e -> {
			PermissionPopup permissionToClose = new PermissionPopup("Exit Program");
			permissionToClose.setupDisplayLabel("Are you sure?");
			permissionToClose.setupCancelButton("Cancel");
			permissionToClose.setupOkButton("Exit");
			permissionToClose.show();
			if(permissionToClose.getResponse())
			{
				Platform.exit();
			}
			e.consume();
		});
		
		// Initialize the color selector
		ColorSelector colorSelector = new ColorSelector();
		colorSelector.initializeLayout();
		colorSelector.initializeKeyMap(scene);
		
		// Initialize the frame display
		FrameDisplay frameDisplay = new FrameDisplay(canvas);
		frameDisplay.initializeLayout();
		frameDisplay.initializeKeyMap(scene);
		
		// Initialize the palette
		Palette palette = new Palette(colorSelector, frameDisplay);
		palette.initializeLayout();
		palette.initializeKeyMap(scene);
		
		// Setup references of the canvas
		canvas.setPalette(palette);
		canvas.setFrameDisplay(frameDisplay);
		canvas.setColorSelector(colorSelector);

		// Request a canvas redraw whenever the color selector changes color
		colorSelector.colorProperty().addListener((args, oldColor, newColor) -> canvas.requestRedraw());
		
		// Create the project
		Project project = new Project(new ISerializable[]{palette, frameDisplay});
		
		// Create the top tool bar gui
		TopToolBar topToolBar = new TopToolBar(project, frameDisplay, canvas);
		topToolBar.initializeLayout();
		topToolBar.initializeKeyMap(scene);
		
		// Make the window have the same title as the project we are working on
		primaryStage.titleProperty().bind(Bindings.format("Celestial Editor - %s", project.nameProperty()));
		
		// Panes used for allignment
		Pane p1 = new Pane();
		HBox.setHgrow(p1, Priority.ALWAYS);
		Pane p2 = new Pane();
		HBox.setHgrow(p2, Priority.ALWAYS);
		Pane p3 = new Pane();
		HBox.setHgrow(p3, Priority.ALWAYS);
		Pane p4 = new Pane();
		HBox.setHgrow(p4, Priority.ALWAYS);

		// The right part of the GUI which contains the colorSelector and the palette
		HBox hBox1 = new HBox(p1, colorSelector.getRoot(), p2);
		HBox hBox2 = new HBox(p3, palette.getRoot(), p4);
		
		// Separates the the color selector and the palette
		Separator sep1 = new Separator(Orientation.HORIZONTAL);
		sep1.setPadding(new Insets(5, 0, 5, 0));
		
		// Create the right tool bar and adds in the color selector and palette
		ToolBar rightBar = new ToolBar();
		rightBar.setEffect(new DropShadow(16, 0, 0, Color.BLACK));
		rightBar.setOrientation(Orientation.VERTICAL);
		rightBar.getItems().addAll(
				hBox1, sep1, hBox2);
		
		
		// Create the brush tool manager and initializes it
		BrushToolManager brushToolManager = new BrushToolManager(canvas);
		BrushToolHotbar brushToolBar = new BrushToolHotbar(brushToolManager);
		brushToolBar.canvas = canvas;
		brushToolBar.initializeLayout();
		brushToolBar.initializeKeyMap(scene);
		
		// Creates the left part of the gui, which contains the frame display
		VBox leftVertical = new VBox(10);
		leftVertical.getChildren().addAll(
				frameDisplay.getRoot()
				);
		
		// Contains the frame display and the brush tool set
		HBox left = new HBox();
		left.getChildren().addAll(leftVertical, brushToolBar.getToolBar());
		HBox.setMargin(leftVertical, new Insets(10));
		
		// The clipping pane of the canvas
		Rectangle clipRect = new Rectangle(32*26, 32*26);
		pane.setClip(clipRect);
		pane.getChildren().addAll(canvas, temporaryDrawCanvas, gridLinesCanvas);
		
		// Setup the border pane
		borderPane.setLeft(left);
		borderPane.setRight(rightBar);
		borderPane.setTop(topToolBar.getToolBar());
		borderPane.setCenter(pane);
		BorderPane.setAlignment(pane, Pos.TOP_CENTER);
		BorderPane.setMargin(pane, new Insets(40,40,40,40));
		
		borderPane.setMinSize(scene.getWidth(), scene.getHeight());
		
		// Display program GUI
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Load any PXL files, if any
		if(Program.args.length > 0)
		{
			topToolBar.loadPXLFile(Program.args[0]);
		}
	}
}
