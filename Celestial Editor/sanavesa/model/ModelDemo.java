package sanavesa.model;

import java.util.Arrays;

import javafx.scene.paint.Color;
import sanavesa.model.versionControl.VersionControl;

public class ModelDemo
{
	private static void setupFrameListeners(Frame frame)
	{
		frame.visibilityProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Frame Visibility: " + newV);
		});
		
		frame.pixelsProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Frame Pixels: " + Arrays.toString(newV.toArray()));
		});
	}
	
	private static void setupPixelListeners(Pixel pixel)
	{
		pixel.xProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Pixel X: " + newV);
		});
		
		pixel.yProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Pixel Y: " + newV);
		});
		
		pixel.layerProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Pixel Layer: " + newV);
		});
		
		pixel.brightnessFactorProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Pixel Brightness: " + newV);
		});
	}
	
	private static void setupLayerListeners(Layer layer)
	{
		layer.nameProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Name: " + newV);
		});
		
		layer.visibilityProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Visibility: " + newV);
		});
		
		layer.depthProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Depth: " + newV);
		});
		
		layer.colorProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Color: " + newV);
		});
	}
	
	private static void setupProjectListeners(Project project)
	{
		project.nameProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Project Name: " + newV);
		});
	}
	
	private static void setupFrameManagerListeners(FrameManager frameManager)
	{
		frameManager.framesProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Frame Manager Frames: " + Arrays.toString(newV.toArray()));
		});
		
		frameManager.selectedFrameIndexProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Frame Manager Selected Index: " + newV);
		});
	}
	
	private static void setupLayerManagerListeners(LayerManager layerManager)
	{
		layerManager.layersProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Manager Layers: " + Arrays.toString(newV.toArray()));
		});
		
		layerManager.selectedLayerIndexProperty().addListener((e, oldV, newV) ->
		{
			System.out.println("New Layer Manager Selected Index: " + newV);
		});
	}
	
	public static void main(String[] args)
	{
		Project project = new Project("Untitled Project");
		setupProjectListeners(project);
		
		setupFrameManagerListeners(project.getFrameManager());
		setupLayerManagerListeners(project.getLayerManager());
		
		Frame frame = new Frame(true);
		setupFrameListeners(frame);
		
		Layer layer = new Layer("Untitled Layer", Color.RED, true, 0);
		setupLayerListeners(layer);
		
		Pixel pixel = new Pixel(0, 0, layer, 0);
		setupPixelListeners(pixel);
		
		project.setName("Project Awesome");
		
		System.out.println("Add (0,0)");
		frame.addPixel(pixel);
		System.out.println("Add (1,2) and (3, 4)");
		frame.addPixels(
				new Pixel(1, 2, layer, 0),
				new Pixel(3, 4, layer, 0));
		System.out.println("Add (9,7)");
		frame.addPixel(new Pixel(9, 7, layer, 0));
		System.out.println("Remove (0, 0");
		frame.removePixel(pixel);
		
		while(VersionControl.getInstance().undoCount() > 0)
		{
			VersionControl.getInstance().undo();
			System.out.println("Undo Frame Pixels: " + Arrays.toString(frame.getPixels().toArray()));
		}
		
		while(VersionControl.getInstance().redoCount() > 0)
		{
			VersionControl.getInstance().redo();
			System.out.println("Redo Frame Pixels: " + Arrays.toString(frame.getPixels().toArray()));
		}
	}
}