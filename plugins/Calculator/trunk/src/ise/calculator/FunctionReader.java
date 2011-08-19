/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.io.*;

/**
 * Reads a file in the function format.
 *
 * @version   $Revision$
 */
public class FunctionReader {

    private String name = "";
    private String desc = "";
    private StringBuilder func;
    private String cmd = "";
    private boolean isConstant = false;
    private String LS = System.getProperty("line.separator");

    /**
     * Reads the given file.
     *
     * @param filename the file to read
     */
    public FunctionReader(String filename) throws IOException {
        this(new File(System.getProperty("calc.home"), filename));
    }

    /**
     * Reads the given file.
     *
     * @param f the file to read
     */
    public FunctionReader(File f) throws IOException {
        if (f.length() == 0) {
            throw new IOException("Zero length file: " + f.getAbsolutePath());
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        String filename = f.getName();
        cmd = filename.substring(0, filename.lastIndexOf('.'));

        // file format:
        // line 0: <name>
        // line 1: some text
        // line 2: </name>
        // line 3: <description>
        // line 4: description
        // line 5: </description>
        // line 6: <function>
        // next lines: commands
        // last line: </function>

        br.readLine();        // <function>
        name = br.readLine().trim();        // name
        br.readLine();        // </function>
        br.readLine();        // <description>
        desc = br.readLine();        // description
        br.readLine();        // </description>
        String line = br.readLine();        // <function> or <constant>
        isConstant = line.trim().equals("<constant>") || cmd.startsWith("const");
        func = new StringBuilder();
        line = "";
        while (line != null) {
            line = br.readLine();
            if (line == null || line.trim().equals("</function>") || line.trim().equals("</constant>")) {
                break;
            }
            func.append(line).append(LS);
        }
        br.close();
    }

    /**
     * Gets the name of the function.
     *
     * @return   The name value
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the function.
     *
     * @return   The description value
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Gets the function steps.
     *
     * @return   The function steps.
     */
    public String getFunction() {
        return func.toString();
    }

    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Gets the command string for the function, this is the file name minus
     * any extension.
     *
     * @return   The command value
     */
    public String getCommand() {
        return cmd;
    }
}

