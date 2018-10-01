package sereneseasons.util;

public class JavaUtils {
	private JavaUtils() {}
	
	public static boolean isClassExisting(String className) {
	    try  {
	        Class.forName(className);
	        return true;
	    }  catch (ClassNotFoundException e) {
	        return false;
	    }
	}
}
