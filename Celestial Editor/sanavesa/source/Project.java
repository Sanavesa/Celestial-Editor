/***************************************************************************************************************************
 * Class:		Project.java
 * Author:		Mohammad Alali
 * 
 * Description:	The Project class contains all objects that require File IO. The Project class has capabilities to save and 
 * 				load projects via the ISerializable interface.
 * 	
 * Attributes: 	
 * 				String name
 * 				ISerializable[] serialiazbles
 * 				File projectFile
 * 				FileChooser fileChooser
 * 		
 * Methods:		
 * 				void saveProject(File)
 * 				void loadProject()
 * 				void newProject()
 * 				void showSaveFileDialog()
 * 				void showOpenFileDialog()
 * 
 ***************************************************************************************************************************/


package sanavesa.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sanavesa.gui.popup.MessagePopup;

/**
 * The Project class contains all objects that require File IO.
 * The Project class has capabilities to save and load projects
 * via the {@link ISerializable} interface.
 * 
 * @author Mohammad Alali
 */
public class Project
{
	/** Title Name of the Project */
	private StringProperty name = new SimpleStringProperty(this, "name", "");
	
	/** The data in the project that require File IO */
	private ISerializable[] serializables = null;
	
	/** The file path of the project */
	private File projectFile = null;
	
	/** The file chooser of the project */
	private FileChooser fileChooser = new FileChooser();
	
	/**
	 * Creates a new project named "Untitled Project" and registers
	 * the specified objects to the File IO handling array. 
	 */
	public Project(ISerializable[] serializables)
	{
		this.serializables = serializables;
		
		// Initialize the file explorer for loading/saving the project
		fileChooser.setTitle("Project File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PXL", "*.pxl"));
		fileChooser.setInitialFileName("Untitled Project");
		
		newProject();
	}
	
	/**
	 * Sets the name of the project
	 * @param newName	the new name of the project
	 * @see #name
	 */
	public void setName(String newName)
	{
		name.set(newName);
	}
	
	/**
	 * @return the name of the project
	 * @see #name
	 */
	public String getName()
	{
		return name.get();
	}
	
	/**
	 * @return the string property of the project's name
	 * @see #name
	 */
	public StringProperty nameProperty()
	{
		return name;
	}
	
	/**
	 * Sets the project's file
	 * @param newProjectFile	the project's file
	 * @see #projectFile
	 */
	public void setProjectFile(File newProjectFile)
	{
		projectFile = newProjectFile;
		
		// Update the project name to the file's name
		if(newProjectFile != null)
			setName(newProjectFile.getName());
		else
			setName("Untitled Project");
	}
	
	/**
	 * @return the project's file
	 * @see #projectFile
	 */
	public File getProjectFile()
	{
		return projectFile;
	}
	
	/**
	 * Saves the project data to the assigned file.
	 * If <code>saveFile</code> is null, the method will do nothing.
	 * @params saveFile		the file to save the project to
	 */
	public void saveProject(File saveFile)
	{
		// Write data to file
		if(saveFile != null)
		{
			// Try with resources statement to auto-close streams
			try(
					FileOutputStream fileStream = new FileOutputStream(saveFile);
					ObjectOutputStream outputStream = new ObjectOutputStream(fileStream))
			{
				for(ISerializable s : serializables)
				{
					s.save(outputStream);
				}
				
				outputStream.close();
				fileStream.close();
				
				MessagePopup message = new MessagePopup("Saved project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Successfully saved " + saveFile.getName() + "!");
				message.show();
			}
			catch(FileNotFoundException e)
			{
				MessagePopup message = new MessagePopup("Failed to save project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Error: File not found!");
				message.show();
				e.printStackTrace();
			}
			catch(IOException e)
			{
				MessagePopup message = new MessagePopup("Failed to save project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Error: IO Exception!");
				message.show();
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Couldn't save as there isnt a file to save to!");
		}
	}
	
	/**
	 * Imports the project data from the {@link #projectFile}.
	 */
	public void loadProject()
	{
		// Load data from file
		if(projectFile != null)
		{
			// Try with resources statement to auto-close streams
			try(
					FileInputStream fileStream = new FileInputStream(projectFile);  
					ObjectInputStream inputStream = new ObjectInputStream(fileStream))
			{
				for(ISerializable s : serializables)
				{
					s.load(inputStream);
				}
				
				MessagePopup message = new MessagePopup("Loaded project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Successfully loaded " + projectFile.getName() + "!");
				message.show();
			}
			catch(FileNotFoundException e)
			{
				newProject();
				MessagePopup message = new MessagePopup("Failed to load project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Error: File not found!");
				message.show();
				e.printStackTrace();
			}
			catch(IOException e)
			{
				newProject();
				MessagePopup message = new MessagePopup("Failed to load project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Error: File is corrupted!");
				message.show();
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e)
			{
				newProject();
				MessagePopup message = new MessagePopup("Failed to load project!");
				message.setupOkButton("OK");
				message.setupDisplayLabel("Error: Class cannot be found!");
				message.show();
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Couldn't load as there isnt a file to load from!");
		}
	}
	
	/**
	 * Resets the project's fields, creating an empty project
	 * called "Untitled Project".
	 */
	public void newProject()
	{
		// Resets the project name
		setName("Untitled Project");
		
		// Resets the project's file path
		setProjectFile(null);
		
		// Reverts all serializables to their default values
		for(ISerializable s : serializables)
		{
			s.reset();
		}
	}
	
	/** 
	 * Displays a file explorer to select a location to save a file.
	 * @return	the file the user selected (null if cancelled)
	 */
	public File showSaveFileDialog()
	{
		fileChooser.setTitle("Save Project");
		
		// If have already loaded/saved a file previously, open the file explorer to that directory
		if(projectFile != null)
		{
			fileChooser.setInitialDirectory(projectFile.getParentFile());
		}
		// If not, open to the user's desktop directory
		else
		{
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		}
		
		return fileChooser.showSaveDialog(null);
	}
	
	/** 
	 * Displays a file explorer to select a location to open a file.
	 * @return	the file the user selected (null if cancelled)
	 */
	public File showOpenFileDialog()
	{
		fileChooser.setTitle("Open Project");
		
		// If have already loaded/saved a file previously, open the file explorer to that directory
		if(projectFile != null)
		{
			fileChooser.setInitialDirectory(projectFile.getParentFile());
		}
		// If not, open to the user's desktop directory
		else
		{
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		}
		
		return fileChooser.showOpenDialog(null);
	}
}
