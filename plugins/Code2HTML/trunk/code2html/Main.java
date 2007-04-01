/*
 * Main.java
 * Copyright (c) 2002 Andre Kaplan
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
package code2html;

import java.awt.Color;
import java.awt.Font;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import java.util.Vector;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.syntax.ParserRule;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.util.Log;

import com.microstar.xml.XmlException;
import com.microstar.xml.XmlParser;

import code2html.html.HtmlCssStyle;
import code2html.html.HtmlDocument;
import code2html.html.AbstractGutter;
import code2html.html.HtmlPainter;
import code2html.html.HtmlStyle;

import code2html.syntax.ParserRuleSet;
import code2html.syntax.TokenMarker;
import code2html.syntax.XModeHandler;


/**
 *  Starts the program when run from the command line
 *
 * @author     Andre Kaplan
 * @version    0.5
 * @todo       Log exceptions
 * @todo       Put better comand line parser
 */
public class Main {
    private static FilePropertyAccessor propertyAccessor =
        new FilePropertyAccessor();


    /**
     *  The main program
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args) {
        Log.init(false, Log.ERROR);

        int exit = parseCommandLine(args);

        if (exit != 0) {
            usage();
        }

        System.exit(exit);
    }


    /**
     *  Mark tokens for the specified <code>Reader</code>
     *
     * @param  config       The configuration to use for this conversion
     * @param  tokenMarker  A marker to fdefine token styles
     * @param  r            A Reader to read from
     * @param  w            A Writer to write to
     */
    public static void markTokens(Config config,
                                  TokenMarker tokenMarker,
                                  Reader r,
                                  Writer w) {
        HtmlStyle style = config.getStyle();
        AbstractGutter gutter = config.getGutter();
        HtmlPainter painter = config.getPainter();

        try {
            BufferedReader reader = new BufferedReader(r);
            BufferedWriter writer = new BufferedWriter(w);

            HtmlDocument document = new HtmlDocument(
                Main.getProperty("view.bgColor", "#ffffff"),
                Main.getProperty("view.fgColor", "#000000"),
                painter.getSyntaxStyles(),
                style,
                gutter,
                "Code2HTML",
                jEdit.getProperty("plugin.code2html.line.separator"));

            document.htmlOpen(writer);

            Segment seg = new Segment();
            String line = null;
            TokenMarker.LineContext context = null;

            for (int lnr = 1; (line = reader.readLine()) != null; lnr++) {
                TokenList tokenList = new TokenList();

                seg.offset = 0;
                seg.count = line.length();
                seg.array = line.toCharArray();

                // Prepare tokenization
                tokenList.lastToken = null;

                ParserRule oldRule;
                ParserRuleSet oldRules;

                if (context == null) {
                    oldRule = null;
                    oldRules = null;
                } else {
                    oldRule = context.inRule;
                    oldRules = context.rules;
                }

                context = tokenMarker.markTokens(context, tokenList, seg);
                SyntaxToken syntaxTokens = SyntaxTokenUtilities.convertTokens(
                    tokenList.getFirstToken());
                painter.setPos(0);
                painter.paintSyntaxLine(writer, lnr, seg, syntaxTokens);
                writer.newLine();
            }

            document.htmlClose(writer);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            // log me
        }
    }


    /**
     *  Parse the command line and respond appropriately
     *
     * @param  args  The command line argumants
     * @return       an exit status
     * @todo         Probably should be renamed to run or somesuch
     * @todo         this method should be broken down into its components,
     *      probably dumping more functionality/glue into main(String[])
     * @todo         log
     */
    public static int parseCommandLine(String[] args) {
        String scheme = null;
        String catalog = null;
        Vector files = new Vector();

        CommandLineConfig.Arguments arguments =
            new CommandLineConfig.Arguments();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ((arg == null) || (arg.length() == 0)) {
                continue;
            }

            if (arg.equals("--help")) {
                usage();
                return 0;
            } else if (arg.startsWith("--catalog=")) {
                catalog = arg.substring("--catalog=".length());
            } else if (arg.startsWith("--scheme=")) {
                scheme = arg.substring("--scheme=".length());
            } else if (arg.startsWith("--css=")) {
                String css = arg.substring("--css=".length()).toLowerCase();

                if ("yes".equals(css)) {
                    arguments.useCSS = true;
                } else if ("no".equals(css)) {
                    arguments.useCSS = false;
                } else {
                    return 1;
                }
            } else if (arg.startsWith("--gutter=")) {
                String gutter = arg.substring(
                    "--gutter=".length()).toLowerCase();

                if ("yes".equals(gutter)) {
                    arguments.showGutter = true;
                } else if ("no".equals(gutter)) {
                    arguments.showGutter = false;
                } else {
                    return 1;
                }
            } else if (arg.startsWith("--gutter-size=")) {
                String gutterSize = arg.substring("--gutter-size=".length());

                try {
                    arguments.gutterSize = Integer.parseInt(gutterSize);
                    if (arguments.gutterSize <= 0) {
                        return 1;
                    }
                } catch (NumberFormatException nfe) {
                    // log me
                    return 1;
                }
            } else if (arg.startsWith("--tab-size=")) {
                String tabSize = arg.substring("--tab-size=".length());

                try {
                    arguments.tabSize = Integer.parseInt(tabSize);
                    if (arguments.tabSize <= 0) {
                        return 1;
                    }
                } catch (NumberFormatException nfe) {
                    // log me
                    return 1;
                }
            } else if (arg.startsWith("--wrap=")) {
                String wrap = arg.substring("--wrap=".length());

                try {
                    arguments.wrap = Integer.parseInt(wrap);

                    if (arguments.wrap < 0) {
                        return 1;
                    }
                } catch (NumberFormatException nfe) {
                    // log me
                    return 1;
                }
            } else {
                files.addElement(arg);
            }
        }

        if (catalog == null) {
            Log.log(Log.ERROR, Main.class, "--catalog option is mandatory");
            return 1;
        }

        try {
            loadScheme(scheme);
        } catch (FileNotFoundException fnfe) {
            Log.log(Log.ERROR, Main.class, fnfe);
            return 1;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, Main.class, ioe);
            return 1;
        }

        loadXModeErrors();
        loadModeCatalog(catalog, false);

        Mode[] modes = ModeUtilities.getModes();

        SyntaxStyle[] styles =
            StyleUtilities.loadStyles(propertyAccessor, "monospaced", 12, true);
        arguments.styles = styles;
        arguments.propertyAccessor = propertyAccessor;

        try {
            for (int i = 0; i < files.size(); i++) {
                Config config = new CommandLineConfig(arguments);

                String file = (String) files.elementAt(i);
                Mode mode = ModeUtilities.getMode("text");
                File f = new File(file);

                for (int j = 0; j < modes.length; j++) {
                    if (modes[j].accept(f.getName(), "")) {
                        mode = modes[j];
                        break;
                    }
                }

                if (mode == null) {
                    Log.log(Log.WARNING, Main.class,
                        "Could not find mode for [" + file + "]");
                    continue;
                }

                Reader reader = new FileReader(file);
                Writer writer = new FileWriter(file + ".html");

                markTokens(config, mode.getTokenMarker(), reader, writer);
            }
        } catch (IOException ioe) {
            Log.log(Log.ERROR, Main.class, ioe);
            return 1;
        }

        return 0;
    }


    /**
     *  Display a usage message to stdout
     */
    public static void usage() {
        String ls = System.getProperty("line.separator");

        System.out.println(
            "Usage: java code2html.Main OPTION... --catalog=FILE FILE..." + ls +
            ls +
            "Options:" + ls +
            "  --help             print this help file" + ls +
            "  --scheme=FILE      scheme file" + ls +
            "  --css=yes|no       use cascading style sheets. Default: [yes]" +
            ls +
            "  --gutter=yes|no    display line number. Default: [yes]" + ls +
            "  --gutter-size=INT  gutter size > 0. Default: [3]" + ls +
            "  --tab-size=INT     tab size > 0. Default: [8]" + ls +
            "  --wrap=INT         line wrapping. Default: [0]");
    }


    /**
     *  Loads a mode catalog file.
     *
     * @param  path      The path of the catalog
     * @param  resource  true when the path is a resource URI
     * @since            jEdit 3.2pre2
     */
    private static void loadModeCatalog(String path, boolean resource) {
        Log.log(Log.MESSAGE, Main.class, "Loading mode catalog file " + path);

        ModeCatalogHandler handler = new ModeCatalogHandler(
            MiscUtilities.getParentOfPath(path), resource);
        XmlParser parser = new XmlParser();
        parser.setHandler(handler);

        try {
            InputStream _in;

            if (resource) {
                _in = Main.class.getResourceAsStream(path);
            } else {
                _in = new FileInputStream(path);
            }

            BufferedReader in = new BufferedReader(
                new InputStreamReader(_in));
            parser.parse(null, null, in);
        } catch (XmlException xe) {
            int line = xe.getLine();
            String message = xe.getMessage();
            Log.log(Log.ERROR, Main.class, path + ":" + line + ": " + message);
        } catch (Exception e) {
            Log.log(Log.ERROR, Main.class, e);
        }
    }


    /**
     *  Loads a scheme to hilight code in
     *
     * @param  scheme                     The path to the scheme
     * @exception  FileNotFoundException  When the file cannot be found
     * @exception  IOException            When the file cannot be read
     */
    private static void loadScheme(String scheme)
         throws FileNotFoundException, IOException {
        InputStream in = null;

        if (scheme == null) {
            in = Main.class.getResourceAsStream(
                "/schemes/default.jedit-scheme");
        } else {
            in = new FileInputStream(scheme);
        }

        propertyAccessor.loadProps(in);
    }


    /**
     *  Loads XMode errors
     */
    private static void loadXModeErrors() {
        propertyAccessor.setProperty(
            "xmode-error.title",
            "XML Parse Error");
        // {2} is the column number, but it's not too useful so we don't show it
        propertyAccessor.setProperty(
            "xmode-error.message",
            "An error occurred while parsing {0}, line {1}:\n\t{3}");
        propertyAccessor.setProperty(
            "xmode-error.dtd", "The DTD could not be loaded ({0})");
        propertyAccessor.setProperty(
            "xmode-error.termchar-invalid",
            "The value of the AT_CHAR attribute is invalid ({0})");
        propertyAccessor.setProperty(
            "xmode-error.doctype-invalid",
            "Expected a document type of MODE, found {0}");
        propertyAccessor.setProperty(
            "xmode-error.empty-tag",
            "The {0} tag cannot be empty");
        propertyAccessor.setProperty(
            "xmode-error.token-invalid",
            "The token type {0} is invalid");
        propertyAccessor.setProperty(
            "xmode-error.empty-keyword",
            "The keyword text cannot be empty");
    }


    /**
     *  Gets the property of the class
     *
     * @param  name The property key
     * @return       The property value
     */
    public final static String getProperty(String name) {
        return propertyAccessor.getProperty(name);
    }


    /**
     *  Gets the property of the class
     *
     * @param  name The property key
     * @param  def Default value
     * @return       The property value
     */
    public final static String getProperty(String name, String def) {
        return propertyAccessor.getProperty(name, def);
    }


    /**
     *  Gets the property of the class
     *
     * @param  name The property key
     * @param  args  Returns the property with the specified name. The elements
     *      of the args array are substituted into the value of the property in
     *      place of strings of the form {n}, where n is an index in the array.
     *      You can find out more about this feature by reading the
     *      documentation for the format method of the 
     *      <code>java.text.MessageFormat<code> class.
     * @return       The property value
     */
    public final static String getProperty(String name, Object[] args) {
        return propertyAccessor.getProperty(name, args);
    }
}

