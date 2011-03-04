/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.io.*;

/**
 * Writes a file using the function file format.
 *
 * @version   $Revision$
 */
public class FunctionWriter {

    private String name = null;
    private String desc = null;
    private String func = null;
    private File file = null;
    private boolean append = false;
    private boolean isConstant = false;
    private String LS = System.getProperty("line.separator");

    /**
     * Constructor for FunctionWriter
     *
     * @param f the file to write
     */
    public FunctionWriter(File f) {
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        file = f;
    }

    /**
     * Constructor for FunctionWriter
     *
     * @param f the file to write
     * @param name the name of the function
     * @param desc a description of the function
     * @param func the function itself
     */
    public FunctionWriter(File f, String name, String desc, String func) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or zero length.");
        }
        if (desc == null || desc.length() == 0) {
            throw new IllegalArgumentException("Description cannot be null or zero length.");
        }
        if (func == null || func.length() == 0) {
            throw new IllegalArgumentException("Function cannot be null or zero length.");
        }
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        this.name = name;
        this.desc = desc;
        this.func = func;
        this.file = f;
    }

    /**
     * Writes the function file using the file, name, description and
     * function given in the constructors.
     */
    public void write() throws IOException {
        write(name, desc, func);
    }

    /**
     * Writes the function file using the give file, name, and description to
     * the file given in the constructors.
     *
     * @param name the name of the function
     * @param desc a description of the function
     * @param func the function itself
     */
    public void write(String name, String desc, String func) throws IOException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or zero length.");
        }
        if (desc == null || desc.length() == 0) {
            throw new IllegalArgumentException("Description cannot be null or zero length.");
        }
        if (func == null || func.length() == 0) {
            throw new IllegalArgumentException("Function cannot be null or zero length.");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
        bw.write("<name>" + LS);
        bw.write(name + LS);
        bw.write("</name>" + LS);
        bw.write("<description>" + LS);
        bw.write(desc + LS);
        bw.write("</description>" + LS);
        if (isConstant) {
            bw.write("<constant>" + LS);
        }
        else {
            bw.write("<function>" + LS);
        }
        bw.write(func + LS);
        if (isConstant) {
            bw.write("</constant>" + LS);
        }
        else {
            bw.write("</function>" + LS);
        }
        bw.flush();
        bw.close();
    }

    public void setAppend(boolean b) {
        append = b;
    }

    public boolean getAppend() {
        return append;
    }

    public void setConstant(boolean b) {
        isConstant = b;
    }

    public boolean isConstant() {
        return isConstant;
    }
}

