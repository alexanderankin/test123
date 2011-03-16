/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.io.*;

/**
 * This utility class creates a single file with all functions and constants
 * stored in $calc.home/.calc. The 'main' method packs all *.calc files in
 * $calc.home/.calc, the 'unpack' method is called from the CalculatorPanel
 * on startup if necessary.
 */
public class FunctionPackager {

    private String LS = System.getProperty("line.separator");

    public void pack() {
        try {
            File calc_dir = new File(System.getProperty("calc.home"), ".calc");
            File[] functions = calc_dir.listFiles (new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".calc");
                }
            });
            File outfile = new File(calc_dir, "all.txt");
            for (int i = 0; i < functions.length; i++) {
                if (functions[ i].length() == 0) {
                    continue;
                }
                FunctionReader fr = new FunctionReader(functions[ i]);
                FunctionWriter fw = new FunctionWriter(outfile);
                fw.setConstant(fr.isConstant());
                fw.setAppend(true);
                fw.write(fr.getName(), fr.getDescription(), fr.getFunction());
            }
        }
        catch (Exception e) {           // NOPMD
            //e.printStackTrace();
        }
    }

    public void unpack(InputStream is) {
        try {
            File calc_dir = new File(System.getProperty("calc.home"), ".calc");
            calc_dir.mkdirs();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while (line != null) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.equals("") || line.length() == 0) {
                    continue;
                }
                sb.append(line).append(LS);
                if (line.equals("</function>") || line.equals("</constant>")) {
                    File f = File.createTempFile("calc", ".calc", calc_dir);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    bw.write(sb.toString());
                    bw.flush();
                    bw.close();
                    sb = new StringBuffer();
                }
            }

        }
        catch (Exception e) {       // NOPMD
            //e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FunctionPackager fp = new FunctionPackager();
        fp.pack();
    }
}
