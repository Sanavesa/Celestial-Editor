package sanavesa.gui.brushTool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sanavesa.gui.canvas.PixelatedCanvas;
import sanavesa.util.MathUtil;

public class BrushToolManager
{
	private ObjectProperty<BrushTool> primaryTool = new SimpleObjectProperty<BrushTool>(this, "primaryTool", null);
	private ObjectProperty<BrushTool> secondaryTool = new SimpleObjectProperty<BrushTool>(this, "secondaryTool", null);
	private ObjectProperty<BrushTool> sizeTool = new SimpleObjectProperty<BrushTool>(this, "sizeTool", null);
	private PixelatedCanvas canvas = null;
	private double mousePrevX = 0, mousePrevY = 0;
	
	private int brushSize = 1;
	
	public BrushToolManager(PixelatedCanvas canvas)
	{
		this.canvas = canvas;
		
		// Bind the actions to the canvas
		canvas.setOnMouseDragged(e -> onMouseDragged(e));
		canvas.setOnMousePressed(e -> onMousePressed(e));
		canvas.setOnMouseReleased(e -> onMouseReleased(e));
		canvas.setOnMouseMoved(e -> onMouseMoved(e));
		canvas.setOnMouseEntered(e -> onMouseEntered(e));
		canvas.setOnMouseExited(e -> onMouseExited(e));
		
		// TEMP
//		primaryTool = new PencilTool(null, this);
//		primaryTool = new FillTool(null, this);
//		secondaryTool = new EraserTool(null, this);
	}
	
	private void onMouseEntered(MouseEvent e)
	{
		if(primaryTool != null)
		{
			primaryTool.get().onMouseEnteredCanvas(canvas, e);
		}
		
		if(secondaryTool != null)
		{
			secondaryTool.get().onMouseEnteredCanvas(canvas, e);
		}
	}

	private void onMouseExited(MouseEvent e)
	{
		if(primaryTool != null)
		{
			primaryTool.get().onMouseExitedCanvas(canvas, e);
		}
		
		if(secondaryTool != null)
		{
			secondaryTool.get().onMouseExitedCanvas(canvas, e);
		}
	}

	// DOCUMENT
	private void onMouseMoved(MouseEvent e)
	{
		if(primaryTool != null)
		{
			// Round the mouse position to the nearest cell in the canvas
			double canvasX = MathUtil.snapValueFloor(e.getX(), canvas.getCanvasToFrameScaleX());
			double canvasY = MathUtil.snapValueFloor(e.getY(), canvas.getCanvasToFrameScaleY());
			primaryTool.get().onMouseMovedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		mousePrevX = e.getX();
		mousePrevY = e.getY();
	}

	/**
	 * DOCUMENT
	 * This shall apply the selected tool onto the canvas in the correct position.
	 * Called whenever the user releases the mouse from on the canvas.
	 */
	private void onMouseReleased(MouseEvent e)
	{
		// Round the mouse position to the nearest cell in the canvas
		double canvasX = MathUtil.snapValueFloor(e.getX(), canvas.getCanvasToFrameScaleX());
		double canvasY = MathUtil.snapValueFloor(e.getY(), canvas.getCanvasToFrameScaleY());
		
		// Primary brush released
		if(e.getButton() == MouseButton.PRIMARY && primaryTool != null)
		{
			primaryTool.get().onMouseReleasedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		// Secondary brush on RMB
		if(e.getButton() == MouseButton.SECONDARY && primaryTool != null)
		{
			secondaryTool.get().onMouseReleasedOnCanvas(canvas, e, canvasX, canvasY);
		}
	}

	/**
	 * DOCUMENT
	 * This shall apply the selected tool onto the canvas in the correct position.
	 * Called whenever the user presses on the canvas.
	 */
	private void onMousePressed(MouseEvent e)
	{
		// Round the mouse position to the nearest cell in the canvas
		double canvasX = MathUtil.snapValueFloor(e.getX(), canvas.getCanvasToFrameScaleX());
		double canvasY = MathUtil.snapValueFloor(e.getY(), canvas.getCanvasToFrameScaleY());
		
		// Primary brush on LMB
		if(e.isPrimaryButtonDown() && primaryTool != null)
		{
			primaryTool.get().onMousePressedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		// Secondary brush on RMB
		if(e.isSecondaryButtonDown())
		{
			secondaryTool.get().onMousePressedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		mousePrevX = e.getX();
		mousePrevY = e.getY();
	}

	/**
	 * DOCUMENT
	 * This shall apply the selected tool onto the canvas in the correct position.
	 * Middle Mouse Button (MMB) shall pan around the canvas.
	 * Called whenever the user holds the mouse and moves on the canvas.
	 */
	private void onMouseDragged(MouseEvent e)
	{
		// Round the mouse position to the nearest cell in the canvas
		double canvasX = MathUtil.snapValueFloor(e.getX(), canvas.getCanvasToFrameScaleX());
		double canvasY = MathUtil.snapValueFloor(e.getY(), canvas.getCanvasToFrameScaleY());
		
		// Primary brush on LMB
		if(e.isPrimaryButtonDown() && primaryTool != null)
		{
			primaryTool.get().onMouseDraggedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		// Secondary brush on RMB
		if(e.isSecondaryButtonDown())
		{
			secondaryTool.get().onMouseDraggedOnCanvas(canvas, e, canvasX, canvasY);
		}
		
		mousePrevX = e.getX();
		mousePrevY = e.getY();
	}

	// DOCUMENT
	public BrushTool getPrimaryTool()
	{
		return primaryTool.get();
	}

	public void setPrimaryTool(BrushTool primaryTool)
	{
		this.primaryTool.set(primaryTool);
	}
	
	public ObjectProperty<BrushTool> primaryToolProperty()
	{
		return primaryTool;
	}
	
	public BrushTool getSecondaryTool()
	{
		return secondaryTool.get();
	}

	public void setSecondaryTool(BrushTool secondaryTool)
	{
		this.secondaryTool.set(secondaryTool);
	}
	
	public ObjectProperty<BrushTool> secondaryToolProperty()
	{
		return secondaryTool;
	}
	
	// DOCUMENT
	public int getBrushSize()
	{
		return brushSize;
	}

	public void setBrushSize(int brushSize)
	{
		this.brushSize = brushSize;
		if(primaryTool != null)
		{
			double canvasX = MathUtil.snapValueFloor(mousePrevX, canvas.getCanvasToFrameScaleX());
			double canvasY = MathUtil.snapValueFloor(mousePrevY, canvas.getCanvasToFrameScaleY());
			primaryTool.get().onMouseMovedOnCanvas(canvas, null, canvasX, canvasY);
		}
	}

	public BrushTool getSizeTool()
	{
		return sizeTool.get();
	}

	public void setSizeTool(BrushTool sizeTool)
	{
		this.sizeTool.set(sizeTool);
	}
	
	public ObjectProperty<BrushTool> sizeToolProperty()
	{
		return sizeTool;
	}
}
