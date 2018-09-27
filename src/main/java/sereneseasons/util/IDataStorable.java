package sereneseasons.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IDataStorable
{
	/**
	 * Called when writing the object to stream.
	 * 
	 * @param os output stream.
	 * @throws IOException thrown if some I/O operation failed. 
	 */
    public void writeToStream(ObjectOutputStream os) throws IOException;

	/**
	 * Called when reading the object from stream.
	 * 
	 * @param os output stream.
	 * @throws IOException thrown if some I/O operation failed. 
	 */
    public void readFromStream(ObjectInputStream is) throws IOException;
}
