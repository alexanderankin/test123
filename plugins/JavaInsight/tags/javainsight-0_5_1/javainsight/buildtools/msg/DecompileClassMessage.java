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

package javainsight.buildtools.msg;


import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;


/**
 * Represents a way to decompile a class.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class DecompileClassMessage extends EBMessage {

    private String classname = null;
    private String destination = null;
    private String generatedFile = null;
    private boolean isGeneratingFile = true;
    private Throwable exception = null;


    /**
     * A message for notifying JavaInsight, that it should decompile a class
     * and store it to a certain file.
     *
     * @param source  the source of this message.
     * @param classname  the full specified class name of the class you want
     *        to decompile.
     * @param destination  where the results of the decompilation shall
     *        be put. The file and the path to the file need not exist;
     *        JavaInsight creates them if necessary. The destination may be
     *        either a directory, or a file. If it is null, JavaInsight puts
     *        it into a subdirectory called "JavaInsight" in the (system
     *        dependent) temp directory, as specified by the results of
     *        <code>buildtools.MiscUtils.getTempDir("JavaInsight")</code>.
     * @see buildtools.MiscUtils#getTempDir(java.lang.String)
     */
    public DecompileClassMessage(EBComponent source, String classname, String destination) {
        super(source);

        if (classname == null) {
            throw new IllegalArgumentException("classname may not be null");
        }

        this.classname = classname;
        this.destination = destination;
        this.isGeneratingFile = true;
    }


    /**
     * A message for notifying JavaInsight, that it should decompile a class
     * to a new jEdit buffer. The decompiled file is <i>not</i> stored on
     * the filesystem!
     *
     * @param source     the source of this message.
     * @param classname  the full specified class name of the class you want
     *                   to decompile.
     */
     public DecompileClassMessage(EBComponent source, String classname) {
        this(source, classname, null);
        this.isGeneratingFile = false;
     }


    /**
     * Gets the classname.
     */
    public String getClassName() {
        return classname;
    }


    /**
     * Returns the desired destination for the decompilation result.
     * This can be either a directory or a file, or null.
     *
     * @deprecated call <code>getDestination()</code> instead.
     * @return the filename, may be null.
     */
    public String getFileName() {
        return getDestination();
    }


    /**
     * Returns the desired destination for the decompilation result.
     * This can be either a directory or a file, or null.
     *
     * @return the destination, possibly null.
     * @author Dirk Moebius
     */
    public String getDestination() {
        return destination;
    }


    /**
     * After decompilation, if JavaInsight was invoked with a destination
     * file, return the filename of the generated file. Before decompilation
     * this method returns null.
     *
     * @return the filename, or null, if invoked before decompilation.
     * @author Dirk Moebius
     */
    public String getGeneratedFile() {
        return generatedFile;
    }


    /**
     * Called by JavaInsight; sets the generated file name.
     *
     * <b>Note:</b> This method is public due to an implementation side
     * effect. Please don't call this method. Thanks.
     *
     * @author Dirk Moebius
     */
    public void _setGeneratedFile(String generatedFile) {
        this.generatedFile = generatedFile;
    }


    /**
     * After decompilation, if any error occured during the decompilation
     * process, returns the exception associated with the decompilation error.
     *
     * @return the Exception (or Throwable), or null, if decompilation was
     *         successful.
     * @author Dirk Moebius
     */
    public Throwable getException() {
        return exception;
    }


    /**
     * Called by JavaInsight; sets the decompilation exception.
     *
     * <b>Note:</b> This method is public due to an implementation side
     * effect. Please don't call this method. Thanks.
     *
     * @author Dirk Moebius
     */
    public void _setException(Throwable exception) {
        this.exception = exception;
    }


    /**
     * Return whether JavaInsight generates a file for this class
     * decompilation results, as specified by <code>getFileName()</code>.
     *
     * @see #getFileName()
     * @author Dirk Moebius
     */
    public boolean isGeneratingFile() {
        return isGeneratingFile;
    }


    /**
     * Returns a string representation of this message's parameters.
     *
     * @author Dirk Moebius
     */
    public String paramString() {
        return super.paramString()
            + " classname=" + classname
            + " destination=" + destination
            + " isGeneratingFile=" + isGeneratingFile
            + " generatedFile=" + generatedFile
            + " exception=" + exception;
    }

}

