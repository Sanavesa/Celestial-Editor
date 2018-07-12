package sanavesa.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;
import sanavesa.model.versionControl.VersionControl;

public final class FileManager
{
	private FileManager()
	{
	}

	public final static void Serialize(final File file, final Project project) throws FileNotFoundException, IOException
	{
		if (file == null)
			throw new IllegalArgumentException("file cannot be null");

		if (project == null)
			throw new IllegalArgumentException("project cannot be null");

		try (final FileOutputStream fout = new FileOutputStream(file);
				final ObjectOutputStream oos = new ObjectOutputStream(fout))
		{
			// Project name
			oos.writeObject(project.getName());

			// Number of Layers
			final int numLayers = project.getLayerManager().getLayers().size();
			oos.writeInt(numLayers);

			// Selected Layer Index
			oos.writeInt(project.getLayerManager().getSelectedLayerIndex());

			// Iterate through each layer
			for (final Layer layer : project.getLayerManager().getLayers())
			{
				// UUID
				oos.writeObject(layer.getUniqueLayerIdentifier());

				// Name
				oos.writeObject(layer.getName());

				// Color
				final Color c = layer.getColor();
				oos.writeDouble(c.getRed());
				oos.writeDouble(c.getGreen());
				oos.writeDouble(c.getBlue());
				oos.writeDouble(c.getOpacity());

				// Visibility
				oos.writeBoolean(layer.getVisibility());

				// Depth
				oos.writeInt(layer.getDepth());
			}

			// Number of Frames
			final int numFrames = project.getFrameManager().getFrames().size();
			oos.writeInt(numFrames);

			// Selected Frame Index
			oos.writeInt(project.getFrameManager().getSelectedFrameIndex());

			// Iterate through each frame
			for (final Frame frame : project.getFrameManager().getFrames())
			{
				// Number of Pixels
				oos.writeInt(frame.getPixels().size());

				// Visibility
				oos.writeBoolean(frame.getVisibility());

				// Iterate through each pixel
				for (final Pixel pixel : frame.getPixels())
				{
					// X
					oos.writeInt(pixel.getX());

					// Y
					oos.writeInt(pixel.getY());

					// Layer UUID
					oos.writeObject(pixel.getLayer().getUniqueLayerIdentifier());

					// Brightness Factor
					oos.writeDouble(pixel.getBrightnessFactor());
				}
			}
		}
	}

	public final static Project Deserialize(final File file)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		if (file == null)
			throw new IllegalArgumentException("file cannot be null");

		try (final FileInputStream fin = new FileInputStream(file);
				final ObjectInputStream ois = new ObjectInputStream(fin))
		{
			final Project project = new Project("");

			// Project name
			project.setName((String) ois.readObject(), false);

			// Number of Layers
			final int numLayers = ois.readInt();

			// Selected Layer Index
			final int selectedLayer = ois.readInt();
			project.getLayerManager().setSelectedLayerIndex(selectedLayer, false);

			// Temporary Map to lookup Layers fast
			final Map<String, Layer> lookupLayers = new HashMap<>(numLayers);

			// Iterate through each layer
			for (int i = 0; i < numLayers; i++)
			{
				// UUID
				final String uniqueID = (String) ois.readObject();

				// Name
				final String name = (String) ois.readObject();

				// Color
				final double r = ois.readDouble();
				final double g = ois.readDouble();
				final double b = ois.readDouble();
				final double a = ois.readDouble();
				final Color color = new Color(r, g, b, a);

				// Visibility
				final boolean visibility = ois.readBoolean();

				// Depth
				final int depth = ois.readInt();

				// Add layer
				final Layer layer = new Layer(name, color, visibility, depth, uniqueID);
				project.getLayerManager().addLayer(layer, false);
				lookupLayers.put(uniqueID, layer);
			}

			// Number of Frames
			final int numFrames = ois.readInt();

			// Selected Frame Index
			final int selectedFrame = ois.readInt();
			project.getFrameManager().setSelectedFrameIndex(selectedFrame, false);

			// Iterate through each frame
			for (int i = 0; i < numFrames; i++)
			{
				// Number of Pixels
				final int numPixels = ois.readInt();

				// Visibility
				final boolean visibility = ois.readBoolean();

				// Add frame
				final Frame frame = new Frame(visibility);
				project.getFrameManager().addFrame(frame, false);

				// Iterate through each pixel
				for (int j = 0; j < numPixels; j++)
				{
					// X
					final int x = ois.readInt();

					// Y
					final int y = ois.readInt();

					// Layer UUID
					final String layerUUID = (String) ois.readObject();

					// Lookup Layer
					final Layer layer = lookupLayers.get(layerUUID);

					// Brightness Factor
					final double brightnessFactor = ois.readDouble();

					// Add pixel
					final Pixel pixel = new Pixel(x, y, layer, brightnessFactor);
					frame.addPixel(pixel);
				}
			}

			// Reset undo's and redo's
			VersionControl.getInstance().clearRedos();
			VersionControl.getInstance().clearUndos();

			return project;
		}
	}
}
