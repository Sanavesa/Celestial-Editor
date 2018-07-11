/***************************************************************************************************************************
 * Interface:	ISerializable.java
 * Author:		Mohammad Alali
 * 
 * Description: An interface that must be implemented for all classes that need to be saved or loaded by File IO. This 
 * 				interface is used in conjunction with the Project class. This class extends Serializable, which makes it
 * 				compatible with all classes that support it.
 * 	
 * Attributes: 	
 * 				N/A
 * 		
 * Methods:		
 * 				void save(ObjectOutputStream)
 * 				void reset()
 * 				void load(ObjectInputStream)
 * 
 ***************************************************************************************************************************/

package sanavesa.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * An interface that must be implemented for all classes that 
 * need to be saved or loaded by File IO. This interface is used
 * in conjunction with the {@link Project} class.
 * <p>
 * This class extends {@link Serializable}, which makes it
 * compatible with all classes that support it.
 * @author Mohammad Alali
 */
public interface ISerializable extends Serializable
{
	/**
	 * Saves the object's data to the file stream.
	 * @param out	the object output stream of the file
	 * @throws IOException
	 */
	public void save(ObjectOutputStream out) throws IOException;
	
	/** Resets the instance properties to the default values */
	public void reset();
	
	/**
	 * Loads the object's data from the file stream.
	 * @param in	the object input stream of the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException;
}