package ekeko_ajfx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author HAKAN
 * 
 */

public class CountingComments {
	/**
	 * Counting the number of comments in a given java file
	 * 
	 * @param path
	 * @return NOComments including multi-line & single-line & JavaDoc comments
	 * @throws IOException
	 */
	public int getComments(String path) throws IOException {

		String s;
		int comments = 0;
		boolean insideComment = false;
		BufferedReader in = null;

		in = new BufferedReader(new FileReader(path));

		while ((s = in.readLine()) != null) {
			s = s.trim();
			if (s.startsWith("/*") && s.endsWith("*/")) {
				// multi-line comments
				comments++;
			} else if (s.startsWith("//")) {
				// single-line comments
				comments++;
			} else if (s.startsWith("/*") && !s.endsWith("*/")) {
				// starting with "/*" than continue ...
				insideComment = true;
				comments++;
			} else if (!s.startsWith("/*") && s.endsWith("*/")) {
				// continue ... until ending with "*/"
				insideComment = false;
				comments++;
			} else if (insideComment) {
				// count comments if insideComment is true
				comments++;
			}
		}
		in.close();
		return comments;
	}
}
