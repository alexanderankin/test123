/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package buildtools.msg;


import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;


/**
 * Represents a way to decompile a class.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class DecompileClassMessage extends EBMessage.NonVetoable {

    private String classname = null;
    private String filename  = null;


    /**
     * Create an instance with the source and the classname you want to
     * decompile.
     *
     * @param source               the source of this message.
     * @param classname            the full specified class name of the class
     *                             you want to decompile.
     * @param destinationFilename  where the results of the decompilation
     *                             should be put. The file and the path to
     *                             the file need not exist; JavaInsight
     *                             creates them if necessary.
     */
    public DecompileClassMessage(EBComponent source, String classname, String destinationFilename) {
        super(source);

        if (classname == null) {
            throw new IllegalArgumentException("classname may not be null");
        }

        this.classname = classname;
        this.filename = destinationFilename;
    }


    /**
     * Create an instance with the source and the classname you want to
     * decompile. The result of the decompilation is being put to a
     * temporary directory, specified by
     * <code>buildtools.MiscUtils.getTempDir("JavaInsight")</code>.
     *
     * @param source     the source of this message.
     * @param classname  the full specified class name of the class you want
     *                   to decompile.
     * @see  buildtools.MiscUtils#getTempDir(java.lang.String)
     */
     public DecompileClassMessage(EBComponent source, String classname) {
        this(source, classname, null);
     }


    /**
     * Get's the classname.
     */
    public String getClassName() {
        return this.classname;
    }


    /**
     * Return's the filename where the results of the class decompilation are
     * being put.
     *
     * @return the filename, may be null.
     */
    public String getFileName() {
        return this.filename;
    }


    public void setFileName(String destinationFilename) {
        this.filename = destinationFilename;
    }

}


/*
$Log$
Revision 1.3  2001/04/08 21:25:59  dmoebius
new release 0.3

Revision 1.2  2000/11/03 05:25:41  akaplan
Removed dead code, doc update

Revision 1.1.1.1  2000/10/29 15:12:49  andre
JavaInsight initial import

Revision 1.1.1.1  2000/01/17 03:41:15  burtonator
init

Revision 1.3  2000/01/09 09:26:36  burton
...

Revision 1.2  2000/01/09 06:10:53  burton
added an option to add an object to DecompileClassMessage

Revision 1.1  2000/01/08 10:09:24  burton
init
*/
