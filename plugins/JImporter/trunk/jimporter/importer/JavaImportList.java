/*
 *  JavaImportList.java - A collection of imports from a java file.
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.importer;

import gnu.regexp.*;

import java.util.ArrayList;
import java.util.Iterator;
import org.gjt.sp.jedit.Buffer;
import javax.imageio.IIOImage;

/**
 * List of the import source lines present in the current file.
 *
 * @author Matthew Flower
 */
public class JavaImportList extends ImportList {

	/**
	 * Standard constructor.
	 */
	public JavaImportList() {
	}

	/**
	 * Standard constructor.
	 *
	 * @param sourceBuffer a <code>Buffer</code> object that we are going to parse
	 * to find import statements.
	 */
	public JavaImportList(Buffer sourceBuffer) {
		setSourceBuffer(sourceBuffer);
	}

	/**
	 * Find all of the import statements in the current file and add them to our
	 * import list.
	 */
	public void parseImports() {
		RE re;

		//Find the last import statement
		try {
			re = new RE("[^][[:space:]]*import[[:space:]]+([[:alnum:].$_*]*);", RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);

			REMatchEnumeration me = re.getMatchEnumeration(sourceBuffer.getText(0, sourceBuffer.getLength()));

			while (me.hasMoreMatches()) {
				ImportItem ii = new ImportItem(me.nextMatch());
				startingOffset = Math.min(ii.getStartLocation(), startingOffset);
				endingOffset = Math.max(ii.getEndLocation(), endingOffset);
				addImport(ii);
			}

			sourceBufferParsed = true;
		} catch (gnu.regexp.REException e) {
			throw new RuntimeException("Unexpected error while creating regular expression: " + e);
		}
	}

	/**
	 * See if the import listed in "className" has already been imported.
	 *
	 * @param className The name of the class we are going to check the presence
	 * of in the import list.
	 * @param importList The list of imports that currently exists in the file.
	 * @return a <code>boolean</code> value indicating whether the class was
	 * already in the import list.
	 */
	public boolean checkForDuplicateImport(String className, ImportList importList) {
		//Strip out the "import " part of the classname
		className = className.substring(7);
		//Strip out anything unusual from the end of the import
		className = className.substring(0, className.lastIndexOf(";"));

		boolean duplicateFound = false;
		String importPrefix = ".*[[:space:]]*import[[:space:]]+";

		//First, we need to find the package.* version of the classname.
		int lastPeriod = className.lastIndexOf(".");
		boolean searchCombinedImport = (lastPeriod == -1);
		String combinedImport = className.substring(0, lastPeriod) + ".*;";

		//Set up the regex for the uncombined import
		String uncombinedImport = className + ";";

		//Do a little bit of regex cleanup before we create the regex's
		combinedImport = combinedImport.replaceAll("\\.", "[.]");
		combinedImport = combinedImport.replaceAll("\\*", "[*]");
		uncombinedImport = uncombinedImport.replaceAll("\\.", "[.]");
		uncombinedImport = uncombinedImport.replaceAll("\\*", "[*]");

		//Now that we have done our replacements, we won't screw up the premade regex
		combinedImport = importPrefix + combinedImport + ".*";
		uncombinedImport = importPrefix + uncombinedImport + ".*";

		//Set up the regular expressions that we will use to see if we have a match
		try {
			RE singleView = new RE(uncombinedImport, RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
			RE combinedView = new RE(combinedImport, RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);

			//Iterate through the importlist looking for a match
			Iterator it = this.iterator();
			while (it.hasNext()) {
				ImportItem ii = (ImportItem)it.next();

				if ((singleView.isMatch(ii.getImportStatement())) ||
				(combinedView.isMatch(ii.getImportStatement()))) {
					duplicateFound = true;
					break;
				}
			}
		} catch (REException ree) {
			System.out.println("Unexpected REException found while trying to check for " +
			"a duplicate import statement.  (Highly irregular!)" + ree);
		}

		return duplicateFound;
	}
}


