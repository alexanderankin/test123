/*
 * 18/01/2000 v1.1
 *
 * Utilities.java - Some utilities for XInsert
 * Copyright (C) 1999 Romain Guy (version 1.1 additions Dominic Stolerman)
 * powerteam@chez.com
 * www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.io.*;
import java.util.*;
import java.awt.Toolkit;
import org.gjt.sp.jedit.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utilities {
  /** This constant defines an open dialog box. */
  public static final int OPEN = 0;
  /** This constant defines a save dialog box. */
  public static final int SAVE = 1;

  /**
   * We may need to load and display images.
   * @param picture The path to the image
   * @param source The class 'root'
   * @return An <code>ImageIcon</code>
   */

  public static ImageIcon getIcon(String picture) {
    return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Utilities.class.getResource(picture)));
  }

  /**
   * Display a confirm message in a dialog box.
   * @param parent The View parent
   * @param title The title
   * @param message The message to display
   */
/*
  public static int showConfirm(View parent, String title, String message) {
    return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  }
*/
  /**
   * Display a sample message in a dialog box.
   * @param message The message to display
   */

  /* public static void showMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
  } */

  /**
   * Display an error message in a dialog box.
   * @param message The message to display
   */

  /* public static void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  } */

  /**
   * Display a sample message in a dialog box.
   * @param message The message to display
   */

  /* public static void showMessage(String title, String message) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  } */

  /**
   * Constructs a new path from current user path. This is an easy way to get a path
   * if the user specified, for example, "..\Java" as new path. This method will return
   * the argument if this one is a path to a root (i.e, if <code>change</code> is equal
   * to C:\Jdk, constructPath will return C:\Jdk).
   * @param change The modification to apply to the path
   * @see Utils#beginsWithRoot(String), Utils#getRoot(String)
   */

  /* public static String constructPath(String change) {
    if (beginsWithRoot(change))
      return change;

    String newPath = getUserDirectory();

    char current;
    char lastChar = '\0';
    boolean toAdd = false;
    StringBuffer buf = new StringBuffer(change.length());

    for (int i = 0; i < change.length(); i++) {
      switch ((current = change.charAt(i))) {
        case '.':
          if (lastChar == '.') {
            String parent = (new File(newPath)).getParent();
            if (parent != null) newPath = parent;
          } else if (lastChar != '\0' && lastChar != '\\' && lastChar != '/') buf.append('.');
          lastChar = '.';
          break;
        case '\\': case '/':
          if (lastChar == '\0')
            newPath = getRoot(newPath);
          else {
            if (!newPath.endsWith("\\"))
              newPath += File.separator + buf.toString();
            else
              newPath += buf.toString();
            buf = new StringBuffer();
            toAdd = false;
          }
	  lastChar = '\\';
          break;
        case '~':
          if (i < change.length() - 1) {
            if (change.charAt(i + 1) == '\\' || change.charAt(i + 1) == '/')
              newPath = System.getProperties().getProperty("user.home");
            else
              buf.append('~');
          } else if (i == 0)
            newPath = System.getProperties().getProperty("user.home");
          else
            buf.append('~');
          lastChar = '~';
          break;
	default:
          lastChar = current;
          buf.append(current);
          toAdd = true;
          break;
      }
    }

    if (toAdd) {
      if (!newPath.endsWith(File.separator))
        newPath += File.separator + buf.toString();
      else
        newPath += buf.toString();
    }

    return newPath;
  }

  /**
   * It can be necessary to check if a path specified by the user is an absolute
   * path (i.e C:\Gfx\3d\Utils is absolute whereas ..\Jext is relative).
   * @param path The path to check
   * @return <code>true</code> if <code>path</code> begins with a root name
   */
/*
  public static boolean beginsWithRoot(String path) {
    File roots[] = (new File(path)).listRoots();
    for (int i = 0; i < roots.length; i++)
      if (path.regionMatches(true, 0, roots[i].getPath(), 0, roots[i].getPath().length())) return true;
    return false;
  }

  /**
   * Returns user directory.
   */

  public static String getUserDirectory() {
    return System.getProperty("user.dir");
  }

  /**
   * Returns user's home directory.
   */
/*
  public static String getHomeDirectory() {
    return System.getProperty("user.home");
  }

  /**
   * It can be necessary to determine which is the root of a path.
   * For example, the root of D:\Projects\Java is D:\.
   * @param path The path used to get a root
   * @return The root which contais the specified path
   */
/*
  public static String getRoot(String path) {
    File roots[] = (new File(path)).listRoots();
    for (int i = 0; i < roots.length; i++)
      if (path.startsWith(roots[i].getPath())) return roots[i].getPath();
    return path;
  }

  /**
   * Returns the number of leading white space characters in the
   * specified string.
   * @param str The string
   */
/*
  public static int getLeadingWhiteSpace(String str) {
    int whitespace = 0;
    loop:  for (; whitespace < str.length(); ) {
      switch(str.charAt(whitespace)) {
        case ' ': case '\t':
          whitespace++;
          break;
        default:
          break loop;
      }
    }
    return whitespace;
  }

  /**
   * When the user has to specify file names, he can use wildcards (*, ?). This methods
   * handles the usage of these wildcards.
   * @param s Wilcards
   * @param sort Set to true will sort file names
   * @return An array of String which contains all files matching <code>s</code>
   * in current directory.
   */

  public static String[] getWildCardMatches(String s, boolean sort) {
    return getWildCardMatches(null, s, sort);
  }

   /**
   * When the user has to specify file names, he can use wildcards (*, ?). This methods
   * handles the usage of these wildcards.
   * @param path The path were to search
   * @param s Wilcards
   * @param sort Set to true will sort file names
   * @return An array of String which contains all files matching <code>s</code>
   * in current directory.
   */

  public static String[] getWildCardMatches(String path, String s, boolean sort) {
    String args = new String(s.trim());
    String files[];
    Vector filesThatMatchVector = new Vector();
    String filesThatMatch[];

    if (path == null)
      files = (new File(getUserDirectory())).list();
    else
      files = (new File(path)).list();

    for (int i = 0; i < files.length; i++) {
      if (match(args, files[i])) {
        File temp = new File(getUserDirectory(), files[i]);
        filesThatMatchVector.addElement(new String(temp.getName()));
      }
    }

    filesThatMatch = new String[filesThatMatchVector.size()];
    filesThatMatchVector.copyInto(filesThatMatch);

    if (sort) sortStrings(filesThatMatch);

    return filesThatMatch;
  }

  /**
   * This method can determine if a String matches a pattern of wildcards
   * @param pattern The pattern used for comparison
   * @param string The String to be checked
   * @return true if <code>string</code> matches <code>pattern</code>
   */

  public static boolean match(String pattern, String string) {
    for (int p = 0; ; p++) {
      for (int s = 0; ; p++, s++) {
        boolean sEnd = (s >= string.length());
        boolean pEnd = (p >= pattern.length() || pattern.charAt(p) == '|');
        if (sEnd && pEnd)
          return true;
        if (sEnd || pEnd)
          break;
        if (pattern.charAt(p) == '?')
          continue;
        if (pattern.charAt(p) == '*') {
          int i;
          p++;
          for (i = string.length(); i >= s; --i)
            if (match(pattern.substring(p), string.substring(i))) return true;
          break;
        }
        if (pattern.charAt(p) != string.charAt(s))
          break;
      }
      p = pattern.indexOf('|', p);
      if (p == -1)
        return false;
    }
  }

  /**
   * Quick sort an array of Strings.
   * @param string Strings to be sorted
   */

  public static void sortStrings(String[] strings) {
    sortStrings(strings, 0, strings.length - 1);
  }

  /**
   * Quick sort an array of Strings.
   * @param a Strings to be sorted
   * @param lo0 Lower bound
   * @param hi0 Higher bound
   */

  public static void sortStrings(String a[], int lo0, int hi0) {
    int lo = lo0;
    int hi = hi0;
    String mid;

    if (hi0 > lo0) {
      mid = a[(lo0 + hi0) / 2];

      while (lo <= hi) {
        while (lo < hi0 && a[lo].compareTo(mid) < 0)
          ++lo;

        while (hi > lo0 && a[hi].compareTo(mid) > 0)
          --hi;

        if (lo <= hi) {
          swap(a, lo, hi);
          ++lo;
          --hi;
        }
      }

      if (lo0 < hi)
        sortStrings(a, lo0, hi);

      if (lo < hi0)
        sortStrings(a, lo, hi0);
    }
  }

  /**
   * Swaps two Strings.
   * @param a The array to be swapped
   * @param i First String index
   * @param j Second String index
   */

  public static void swap(String a[], int i, int j) {
    String T;
    T = a[i];
    a[i] = a[j];
    a[j] = T;
  }

  /**
   * Because a lot of people still use JDK 1.1, we need this method
   * to create an array of Files from an array of String.
   * @param names Names of the files
   * @param construct Set it to true if names does not contain full paths
   * @return An array of Files
   */
/*
  public static File[] listFiles(String[] names, boolean construct) {
    File[] files = new File[names.length];

    String path = getUserDirectory();

    if (construct) {
      if (!path.endsWith(File.separator))
        path += File.separator;
    }

    for (int i = 0; i < files.length; i++) {
      if (construct)
        files[i] = new File(path + names[i]);
      else
        files[i] = new File(names[i]);
    }

    return files;
  }

  /**
   * Create a blank String.
   * @param len Amount of spaces contained in the String
   * @return A blank <code>String</code>
   */
/*
  public static String createWhiteSpace(int len) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < len; i++)
      buf.append(' ');
    return buf.toString();
  }

  /**
   * Some String can be too long to be correctly displayed on the screen.
   * Mainly when it is a path to a file. This method truncate a String.
   * @param longString The <code>String</code> to be truncated
   * @param maxLength The maximum length of the <code>String</code>
   * @return The truncated string
   */
/*
  public static String getShortStringOf(String longString, int maxLength) {
    int len = longString.length();
    if (len < maxLength)
      return longString;
    else
      return longString.substring(0, maxLength / 2) + "..." + longString.substring(len - (maxLength / 2));
  }

	
	/**
   * Checks if a <code>String</code> matches true. Values that return true are:
	 * <ul><li>true</li>
	 * <li>yes</li>
	 * <li>1</li><li>on</li></ul>
	 * Values that return false are:
	 * <ul><li>false</li>
	 * <li>no</li>
	 * <li>0</li><li>off</li></ul>
	 * If the input is null or none of these values it returns the default value.
   * 
   * @param input The String to check
	 * @param defVal The Default value
	 * @since v1.1
   */
	public static boolean checkIfTrue(String input, boolean defVal) {
		if(input == null) return defVal;
		if(input.equalsIgnoreCase("true") 
			|| input.equalsIgnoreCase("yes") 
			|| input.equalsIgnoreCase("1") 
			|| input.equalsIgnoreCase("on")) 
				return true;
		else if(input.equalsIgnoreCase("false") 
			|| input.equalsIgnoreCase("no") 
			|| input.equalsIgnoreCase("0") 
			|| input.equalsIgnoreCase("off")) 
				return false;
		return defVal;
	}

	public static String replace(String input, String oldPart, String newPart) {
    
    
		int len = oldPart.length();
    int newLen = newPart.length();
		int start = 0;
		while((start = input.indexOf(oldPart, start)) != -1) {
			input = input.substring(0, start) + newPart + input.substring(start + len);
      start += newLen; //else if oldPart appears in newPart replaces it --> infinaite loop
		}
		return input;
	}
	
	public static String[] findStrings(String input) {
		char quot1 = '\"';
		char quot2 = '\'';
		Vector v = new Vector(10);
		StreamTokenizer sT = new StreamTokenizer(new StringReader(input));
		sT.quoteChar(quot1);
		sT.quoteChar(quot2);
		int i;
		try {
		while((i = sT.nextToken()) != sT.TT_EOF) {
			if(i == sT.TT_WORD || i == quot1 || i == quot2) {
				v.add(sT.sval);
			}
			
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] strings = new String[v.size()];
		v.copyInto(strings);
		return strings;
	}
  
  public static String trimStart(String input) {
    int str = 0;
    char[] val = input.toCharArray();
    while ((str < input.length()) && (val[str] <= ' '))  {
      str++;
    }
    return (str > 0) ? input.substring(str) : input;
  }
}

// End of Utilities.java

