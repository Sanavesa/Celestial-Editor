package sanavesa.gui.brushTool;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import sanavesa.gui.IGraphicalInterface;
import sanavesa.gui.IKeyMapping;
import sanavesa.gui.canvas.PixelatedCanvas;

public class BrushToolHotbar implements IGraphicalInterface, IKeyMapping
{
	private BrushToolManager brushToolManager = null;
	private BrushTool[] brushTools = null;
	public PixelatedCanvas canvas;
	private ToolBar toolBar = new ToolBar();
	
	private static final Image imgPencil = new Image("/art/Pencil.png");
	private static final Image imgEraser = new Image("/art/Eraser.png");
	private static final Image imgLine = new Image("/art/Line.png");
	private static final Image imgCircle = new Image("/art/Circle.png");
	private static final Image imgSmallSize = new Image("/art/Pixel_1.png");
	private static final Image imgMediumSize = new Image("/art/Pixel_2.png");
	private static final Image imgLargeSize = new Image("/art/Pixel_3.png");
	private static final Image imgEyeDropper = new Image("/art/EyeDropper.png");
	
	public BrushToolHotbar(BrushToolManager brushToolManager)
	{
		this.brushToolManager = brushToolManager;
	}
	
	@Override
	public void initializeKeyMap(Scene scene)
	{
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			switch(event.getCode())
			{
			
			// Pencil
			case D:
				brushToolManager.setPrimaryTool(brushTools[0]);
				break;
				
			// Line
			case L:
				brushToolManager.setPrimaryTool(brushTools[2]);
				break;

			// Circle
			case C:
				brushToolManager.setPrimaryTool(brushTools[3]);
				break;
				
			// Eye Dropper
			case E:
				brushToolManager.setPrimaryTool(brushTools[4]);
				break;
				
			// Small brush size
			case DIGIT1:
				{
					brushToolManager.setSizeTool(brushTools[5]);
					SizeBrushTool t = (SizeBrushTool) brushTools[5];
					brushToolManager.setBrushSize(t.brushSize);
				}
				break;
				
			// Medium brush size
			case DIGIT2:
				{
					brushToolManager.setSizeTool(brushTools[6]);
					SizeBrushTool t = (SizeBrushTool) brushTools[6];
					brushToolManager.setBrushSize(t.brushSize);
				}
				break;
				
			// Large brush size
			case DIGIT3:
				{
					brushToolManager.setSizeTool(brushTools[7]);
					SizeBrushTool t = (SizeBrushTool) brushTools[7];
					brushToolManager.setBrushSize(t.brushSize);
				}
				break;
				
			default:
				break;
			}
		});
	}

	@Override
	public void initializeLayout()
	{
		
		brushTools = new BrushTool[]
			{
					new PencilTool(imgPencil, brushToolManager, BrushType.PRIMARY_ONLY),
					new EraserTool(imgEraser, brushToolManager, BrushType.SECONDARY_ONLY),
					new LineTool(imgLine, brushToolManager, BrushType.PRIMARY_ONLY),
					new CircleTool(imgCircle, brushToolManager, BrushType.PRIMARY_ONLY),
					new EyeDropperTool(imgEyeDropper, brushToolManager, BrushType.PRIMARY_ONLY),
					new SizeBrushTool(imgSmallSize, brushToolManager, BrushType.SIZE_ONLY, 1, 1),
					new SizeBrushTool(imgMediumSize, brushToolManager, BrushType.SIZE_ONLY, 5, 2),
					new SizeBrushTool(imgLargeSize, brushToolManager, BrushType.SIZE_ONLY, 9, 3)
			};
		
		toolBar.setOrientation(Orientation.VERTICAL);
		
		for(BrushTool bt : brushTools)
		{
			toolBar.getItems().add(bt.getRoot());
		}
		
		brushToolManager.setPrimaryTool(brushTools[0]); // Pencil By Default
		brushToolManager.setSecondaryTool(brushTools[1]); // Eraser by default
		brushToolManager.setSizeTool(brushTools[5]); // A single pixel size by default
	}
	
	public ToolBar getToolBar()
	{
		return toolBar;
	}

}
