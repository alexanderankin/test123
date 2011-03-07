/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.plugin.calculator;

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;

/**
 * A plugin to turn the rpn calculator into a jEdit plugin.
 */
public class CalculatorPlugin extends EditPlugin {
    /** Name for plugin manager */
    public static final String NAME = "Calculator";

    static {
        String dir = jEdit.getSettingsDirectory();
        if (dir == null) {
            dir = System.getProperty("user.home");
        }
        System.setProperty("calc.home", dir);
    }
}

