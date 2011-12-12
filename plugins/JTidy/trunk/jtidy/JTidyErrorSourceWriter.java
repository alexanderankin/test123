/*
 * JTidyErrorSourceWriter.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package jtidy;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.FilterWriter;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.Log;

import org.w3c.tidy.Tidy;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;


public class JTidyErrorSourceWriter
    extends FilterWriter
{
    public static final String NAME = "JTidy";

    private StringBuffer buf;
    private Buffer buffer;

    private int errorCount = 0;
    private boolean emacsFormat = false;


    public JTidyErrorSourceWriter(Buffer buffer, Writer out) {
        super(out);

        this.buf = new StringBuffer();
        this.buffer = buffer;

        this.emacsFormat = jEdit.getBooleanProperty("jtidy.gnu-emacs", false);

        // Clear errors
        errorSource.clear();
        load();
    }


    public void write(int c) throws IOException
    {
        System.out.println("+++++ write c");
        buf.append(c);
        if (lineSeparator.indexOf(c) != -1) {
            this.showErrors();
        }
    }


    public void write(char[] cbuf, int off, int len) throws IOException
    {
        System.out.println("+++++ write cbuf");
        buf.append(cbuf, off, len);
        char c;
        for (int i = off, j = len - 1; j >= 0; i++, j--) {
            c = cbuf[i];
            if (lineSeparator.indexOf(c) != -1) {
                this.showErrors();
                break;
            }
        }
    }


    public void write(String str, int off, int len) throws IOException
    {
        char[] cbuf = new char[len];
        str.getChars(off, off + len, cbuf, 0);

        this.write(cbuf, 0, len);
    }


    public void close() throws IOException {
        if (buf.length() > 0) {
            buf.append(lineSeparator);
            this.showErrors();
        }
        out.close();
    }


    public int getErrorCount() {
        return this.errorCount;
    }


    private void showErrors() throws IOException {
        int len = this.buf.length();
        int start = 0;
        int end = -1;
main:   for (int i = 0; i < len; i++) {
            if (this.buf.charAt(i) != lineSeparatorChars[0]) {
                continue main;
            }

            if (    (lineSeparatorChars.length > 1)
                && (i < (len - lineSeparatorChars.length + 1))
            ) {
                int j = i + 1;
                int k = 1;
                for (; k < lineSeparatorChars.length; j++, k++) {
                    if (this.buf.charAt(j) != lineSeparatorChars[k]) {
                        continue main;
                    }
                }
            }

            end = i;

            if (end > start) {
                if (this.emacsFormat) {
                    this.showEmacsError(
                        this.buf.substring(start, end)
                    );
                } else {
                    this.showError(
                        this.buf.substring(start, end)
                    );
                }
            }
            end += lineSeparatorChars.length;
            start = end;
        }

        if (end > 0) {
            this.buf.delete(0, end);
        }
    }


    private void showError(String line) throws IOException {
        int type   = ErrorSource.WARNING;
        int lineNo = 0;
        int colNo  = 0;
        String msg = "";

        boolean success = false;

        if (!success && msgWarning != null) {
            try {
                Object[] o = msgWarning.parse(line);

                lineNo = ((Number) o[0]).intValue();
                colNo  = ((Number) o[1]).intValue();
                msg    = res_warning + ((String) o[9]);
                type   = ErrorSource.WARNING;

                success = true;
            } catch (Exception e) {
                // Log.log(Log.ERROR, this, e);
            }
        }

        if (!success && msgError != null) {
            try {
                Object[] o = msgError.parse(line);

                lineNo = ((Number) o[0]).intValue();
                colNo  = ((Number) o[1]).intValue();
                msg    = res_error + ((String) o[9]);
                type   = ErrorSource.ERROR;

                this.errorCount++;

                out.write(line);
                out.write(lineSeparator);

                success = true;
            } catch (Exception e) {
                // Log.log(Log.ERROR, this, e);
            }
        }

        if (!success) {
            out.write(line);
            out.write(lineSeparator);
        } else {
            /**/
            Log.log(Log.DEBUG, this,
                "addError: [" + type + "] [" + this.buffer.getName() + "] ["
                + lineNo + "," + colNo + "] [" + msg + "]");
            /**/
            errorSource.addError(type, this.buffer.getPath(),
                lineNo - 1, colNo - 1, colNo - 1, msg);
        }
    }


    private void showEmacsError(String line) throws IOException {
        int type   = ErrorSource.WARNING;
        String path = "";
        int lineNo = 0;
        int colNo  = 0;
        String msg = "";

        boolean success = false;

        if (!success && msgEmacsWarning != null) {
            try {
                Object[] o = msgEmacsWarning.parse(line);

                path   = (String) o[0];
                lineNo = ((Number) o[1]).intValue();
                colNo  = ((Number) o[2]).intValue();
                msg    = res_warning + ((String) o[9]);
                type   = ErrorSource.WARNING;

                success = true;
            } catch (Exception e) {
                // Log.log(Log.ERROR, this, e);
            }
        }

        if (!success && msgEmacsError != null) {
            try {
                Object[] o = msgEmacsError.parse(line);

                path   = (String) o[0];
                lineNo = ((Number) o[1]).intValue();
                colNo  = ((Number) o[2]).intValue();
                msg    = res_error + ((String) o[9]);
                type   = ErrorSource.ERROR;

                this.errorCount++;

                out.write(line);
                out.write(lineSeparator);

                success = true;
            } catch (Exception e) {
                // Log.log(Log.ERROR, this, e);
            }
        }

        if (!success) {
            out.write(line);
            out.write(lineSeparator);
        } else {
            /*
            Log.log(Log.DEBUG, this,
                "addError: [" + type + "] [" + this.buffer.getName() + "] ["
                + lineNo + "," + colNo + "] [" + msg + "]");
            */
            errorSource.addError(type, this.buffer.getPath(),
                lineNo - 1, colNo - 1, colNo, msg);
        }
    }


    public static void start() {
        errorSource = new DefaultErrorSource(NAME);
        ErrorSource.registerErrorSource(errorSource);
    }


    public static void stop() {
        ErrorSource.unregisterErrorSource(errorSource);
    }


    private static DefaultErrorSource errorSource;

    private static ResourceBundle resources = null;
    private static String res_line_column  = null;
    private static String res_emacs_format = null;
    private static String res_error   = null;
    private static String res_warning = null;

    private static MessageFormat msgError   = null;
    private static MessageFormat msgWarning = null;

    private static MessageFormat msgEmacsError   = null;
    private static MessageFormat msgEmacsWarning = null;

    private static boolean res_loaded = false;


    public static void load() {
        if (res_loaded) { return; }

        try {
            resources = ResourceBundle.getBundle(
                  "org/w3c/tidy/TidyMessages"
                , Locale.US // Messages provided in English
                , Tidy.class.getClassLoader()
            );

            res_line_column  = resources.getString("line_column");
            res_emacs_format = resources.getString("emacs_format");
            res_error   = resources.getString("error");
            res_warning = resources.getString("warning");

            msgError =
                new MessageFormat(res_line_column + res_error + "{9}");
            msgWarning =
                new MessageFormat(res_line_column + res_warning + "{9}");

            msgEmacsError =
                new MessageFormat(res_emacs_format + " " + res_error + "{9}");
            msgEmacsWarning =
                new MessageFormat(res_emacs_format + " " + res_warning + "{9}");

            res_loaded = true;
        } catch (Exception e) {
            Log.log(Log.ERROR, JTidyErrorSourceWriter.class, e);
            res_line_column = res_error = res_warning =  null;
        }
    }


    private static String lineSeparator =
        (String) System.getProperty("line.separator");

    private static char[] lineSeparatorChars =
        System.getProperty("line.separator").toCharArray();
}

