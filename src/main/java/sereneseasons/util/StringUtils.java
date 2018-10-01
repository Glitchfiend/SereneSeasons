package sereneseasons.util;

public class StringUtils {

	public static String toNBTConformKey(String keyStr) {
		StringBuilder builder = new StringBuilder();
		boolean startWithCapital = true;
		
		for( int i = 0; i < keyStr.length(); i ++ ) {
			char c = keyStr.charAt(i);
			if( Character.isAlphabetic(c) ) {
				if( startWithCapital )
					builder.append(Character.toUpperCase(c) );
				else
					builder.append(c);
				startWithCapital = false;
			}
			else {
				if( Character.isDigit(c) )
					builder.append(c);
				else
					startWithCapital = true;
			}
		}
		
		return builder.toString();
	}

}
