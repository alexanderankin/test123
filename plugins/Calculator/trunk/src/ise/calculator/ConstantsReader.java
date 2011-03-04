/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.util.*;
import java.io.*;

/**
 * This is a utility class for reading in a file with constants and converting
 * that file to the calculator format. Not used by the calculator.
 */
public class ConstantsReader {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("c:/My Documents/software/src/calculator/allascii.txt"));
            String line;
            int cnt = 0;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(cnt++ + " line = " + line);

                StringTokenizer st = new StringTokenizer(line, ":");
                String name = st.nextToken().trim();
                String value = st.nextToken().trim();
                String uncertainty = st.nextToken().trim();
                String units = "";
                if (st.hasMoreTokens()) {
                    units = st.nextToken().trim();
                }

                value = value.replaceAll(" ", "");
                value = value.replaceAll("e", "E");
                value = value.replaceAll("[.][.][.]", "");
                uncertainty = uncertainty.replaceAll(" ", "");

                File calc_dir = new File(System.getProperty("user.home"), ".calc");
                File f = File.createTempFile("const", ".calc", calc_dir);
                FileWriter fw = new FileWriter(f);
                fw.write("<name>\n");
                fw.write(name + "\n");
                fw.write("</name>\n");
                fw.write("<description>\n");
                fw.write("<html>Constant: " + name + "<br>Value: " + value + "<br>Uncertainty: " + uncertainty + "<br>Units: " + units + "\n");
                fw.write("</description>\n");
                fw.write("<function>\n");
                fw.write(value + "\n");
                fw.write("</function>\n");
                fw.flush();
                fw.close();

                /*
            System.out.println( "<name>\n" );
            System.out.println( name + "\n" );
            System.out.println( "</name>\n<description>\n" );
            System.out.println( "<html>Constant: " + name + "<br>Value: " + value + "<br>Uncertainty: " + uncertainty + "<br>Units: " + units + "\n" );
            System.out.println( "</description>\n<function>" + value + "</function>\n" );
                */
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
