/**
 * Copyright Dean Hall 2006.
 *
 * CscopeFinder Plugin for jEdit
 *
 * This file is original to the CscopeFinder Plugin.
 * However, certain functions in this file were adapted from find.c
 * from Cscope 15.5 (http://cscope.sourceforge.net/) and converted to Java.
 *
 * This plugin provides functions that scan a cscope index file
 * for quick symbol and function searching in a C/C++ source tree.
 * Requires the cscope index file (cscope.out) to already exist using
 * the cscope command line utility version 15.5 or later.
 *
 * Cscope for Mac OS X and Linux can be downloaded here:
 *      http://cscope.sourceforge.net/
 *
 * Cscope for Win32 can be downloaded here:
 *      http://iamphet.nm.ru/cscope/
 *
 * Use the following command to create a compatible cscope index file:
 *      cscope -b -c -R
 *
 * Suggested key bindings:
 *
 * ==================================  ========    =======
 * Action                              Phonetic    Vi-like
 * ==================================  ========    =======
 * Find this C symbol                  C+t m       C+\ s
 * Find this global definition         C+t d       C+\ g
 * Find functions calling this         C+t c       C+\ c
 * Find functions called by this       C+t k       C+\ d
 * Find files #including this          C+t i       C+\ i
 * List All Functions                  C+t f
 * ==================================  ========    =======
 */

package cscopefinder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.System;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class
CscopeFinder
{
    //{{{ Cscope digraph tables, definitions, tokens and globals
    /* Cscope Digraph */
    /** The 16 most frequent first chars: " teisaprnl(of)=c" */
    private static final char[] dichar1 =
    {' ','t','e','i','s','a','p','r','n','l','(','o','f',')','=','c'};
    /** The 8 most frequent second chars using the above as first chars: " tnerpla" */
    private static final char[] dichar2 = {' ','t','n','e','r','p','l','a'};

    /* Cscope Definitions */
    private static final char ESC = '\033'; /**< Escape character */
    private static final char DEL = '\177'; /**< Delete character */
    private static final char DUMMYCHAR = ' '; /**< A dummy character */
    private static final int NUMLEN = 5;    /**< Line number length */
    private static final int PATHLEN = 250; /**< File pathname length */
    private static final int PATLEN = 250;  /**< Symbol pattern length */
    private static final int MSGLEN = PATLEN + 80; /**< Displayed message length */
    private static final int BUFSIZ = 4096; /**< Cross ref content buffer */
    private static final String REFFILE = "cscope.out"; /**< Cross-reference output file */

    /*
     * Cscope Tokens are used in the cscope index file to denote
     * the type of the symbol that follows the token
     */
    private static final char CLASSDEF   = 'c';
    private static final char DEFINE     = '#';
    private static final char DEFINEEND  = ')';
    private static final char ENUMDEF    = 'e';
    private static final char FCNCALL    = '`';
    private static final char FCNDEF     = '$';
    private static final char FCNEND     = '}';
    private static final char GLOBALDEF  = 'g';
    private static final char INCLUDE    = '~';
    private static final char MEMBERDEF  = 'm';
    private static final char NEWFILE    = '@';
    private static final char STRUCTDEF  = 's';
    private static final char TYPEDEF    = 't';
    private static final char UNIONDEF   = 'u';

    /* Cscope Globals */
    private static char global[] = {'<','g','l','o','b','a','l','>','\0'}; /**< Dummy global function name */
    private static char[] blockp;           /**< Pointer to current char in block */
    private static int blocki;              /**< Index into blockp */
    private static char[] block = new char[BUFSIZ + 2];  /**< Leave room for end-of-block mark */
    private static int blocklen;            /**< Length of disk block read */
    private static char blockmark;          /**< Mark character to be searched for */
    private static int blocknumber;         /**< Block number */

    private static FileReader symrefs;      /**< Cscope symbol crossref data file */
    private static File refsfound;          /**< References found file */
    private static File nonglobalrefs;      /**< Non-global references file */
    private static char[] cpattern = new char[PATLEN + 1]; /**< Compressed pattern */
    private static int fileversion = 0;     /**< Cscope cross-ref file version */
    //}}}

    //{{{ File globals
    private static JEditTextArea textArea;
    private static View currentView;
    private static Vector tagLines;
    private static int findType;
    private static String patternString;
    private static String indexPath;

    /* Added to help putref scan back to get the line number */
    private static final int LBBUFSIZ = BUFSIZ / 4; /**< Lookback buffer size */
    private static char[] lbbuf = new char[LBBUFSIZ]; /**< Loockback buffer */

    /* Configurable from plugin options */
    public static String indexFilename = REFFILE; /**< Cross-reference output file */

    /* Enumeration of the kind of search to perform */
    public static final int FIND_MIN = 0;           /**< Enum min */
    public static final int FIND_SYMBOL = 0;        /**< Find a C symbol */
    public static final int FIND_DEFINITION = 1;    /**< Find a global definition */
    public static final int FIND_CALLING = 2;       /**< Find funcs calling this func */
    public static final int FIND_CALLED_BY = 3;     /**< Find funcs called by this func */
    public static final int FIND_STRING = 4;        /**< Find a text string */
    public static final int FIND_REGEXP= 5;         /**< Find an egrep pattern */
    public static final int FIND_FILE = 6;          /**< Find a file */
    public static final int FIND_INCLUDE = 7;       /**< Find file that #include this file */
    public static final int FIND_ALL_FCNS = 8;     /**< Find all function definitions */
    public static final int FIND_MAX = 8;           /**< Enum max */
    //}}}

    //{{{ Cscope Action Function
    /**
     * Plugin action that performs the requested Cscope search.
     *
     * @param view      The view in which the action was initiated.
     * @param findtype  The kind of Cscope search to perform.
     * @param newView   True if the user wants the result to show in a new view;
     *                  False otherwise.
     * @param usePopup  True if the user wants the search results to appear in
     *                  a popup window.  If False, the results appear
     *                  in a dialog box.
     */
    public static void
    find(View view, int findtype, boolean newView, boolean usePopup)
    throws FileNotFoundException, IOException
    {
        /* Init configurables */
        indexFilename = jEdit.getProperty("cscopefinder.index-filename");
        if (indexFilename.length() == 0)
        {
            indexFilename = REFFILE;
        }

        /* Store some params to globals for use by other methods */
        currentView = view;
        findType = findtype;

        /* Ensure findType is a valid value */
        if ((findType < FIND_MIN) || (findType > FIND_MAX))
        {
            Macros.error(currentView, "CscopeFinder.java: find: Bad findType.");
            return;
        }

        /* Get the cscope index file to use */
        String cscopefn = getCscopeFilePath();
        if (cscopefn == null) return;

        /* Get the pattern to find from the word nearest the cursor */
        textArea = currentView.getTextArea();
        Selection[] currSelections = textArea.getSelection();
        if (currSelections.length == 0) { textArea.selectWord(); }
        else { textArea.moveCaretPosition(currSelections[0].getStart()); }
        patternString = textArea.getSelectedText();
        if (patternString == null || patternString == "")
        {
            /* The find-all-funcs search doesn't need a pattern string */
            if (findType == FIND_ALL_FCNS)
            {
                patternString = "dummy";
            }
            else
            {
                currentView.getToolkit().beep();
                Macros.error(currentView, "Nothing selected.");
                return;
            }
        }
        patternString = patternString.trim();
        char[] pattern = patternString.concat("\0").toCharArray();
        if (pattern == null) return;
        /* Pattern is not compressed; store search pattern in global cpattern */
        strcpy(cpattern, pattern);

        /* Start reading blocks of the cscope index file */
        blocknumber = 0;
        symrefs = new FileReader(cscopefn);
        if (readblock() == null)
        {
            symrefs.close();
            return;
        }

        /* Split the header */
        int i = 0;
        while (blockp[i++] != '\n') {}
        String cscopeheader = String.valueOf(blockp, 0, i);
        int ifirstspace = cscopeheader.indexOf(' ', 0);
        int isecondspace = cscopeheader.indexOf(' ', ifirstspace + 1);
        int ithirdspace = cscopeheader.indexOf(' ', isecondspace + 1);

        /* Verify cscope file ver >= 10 */
        fileversion = Integer.parseInt(cscopeheader.substring(ifirstspace + 1,
                                                              isecondspace));
        if (fileversion < 10)
        {
            Macros.error(currentView,
                         indexFilename + " is not version 10 or later.");
            symrefs.close();
            return;
        }

        /* Get the root path of the index */
        indexPath = cscopeheader.substring(isecondspace + 1, ithirdspace);
        if (indexPath.startsWith("$HOME"))
        {
            indexPath = indexPath.replaceFirst("\\$HOME",
                                               System.getProperty("user.home"));
        }

        /* Initialize result storage container */
        tagLines = new Vector();

        /* Perform desired cscope find */
        switch (findType)
        {
            case FIND_SYMBOL:
                findsymbol(pattern);
                break;

            case FIND_DEFINITION:
                finddef(pattern);
                break;

            case FIND_CALLING:
                findcalling(pattern);
                break;

            case FIND_CALLED_BY:
                findcalledby(pattern);
                break;

            case FIND_STRING:
            case FIND_REGEXP:
            case FIND_FILE:
                currentView.getToolkit().beep();
                Macros.error(currentView,
                             "That function is not yet supported.");
                break;

            case FIND_INCLUDE:
                findinclude(pattern);
                break;

            case FIND_ALL_FCNS:
                findallfcns(pattern);
                break;
        }

        /* Close the cscope index file */
        symrefs.close();

        /* If there are no search hits, beep and return */
        if (tagLines.size() == 0)
        {
            currentView.getToolkit().beep();
            return;
        }

        /* If there is exactly one hit, go straight to it */
        else if (tagLines.size() == 1)
        {
            TargetLine tagLine = (TargetLine)tagLines.elementAt(0);
            CscopeFinderPlugin.goToTagLine(currentView,
                                           tagLine,
                                           newView,
                                           tagLine.getTag());
        }

        /* If there are mutliple hits, let the user choose which to follow */
        else
        {
            // XXX: this should probably be done by the rendering component
            for(int j=0; j < tagLines.size(); j++)
                ((TargetLine)tagLines.elementAt(j)).setIndex(j + 1);

            if (usePopup)
            {
                new ChooseTargetListPopup(currentView, tagLines, newView);
            }
            else
            {
                new ChooseTargetListDialog(currentView, tagLines, newView);
            }
        }
    }
    //}}}

    //{{{ Action Helper Functions
    /**
     * Searches the current view's path (and its parents) for the cscope file
     *
     * @return The absolute path (with filename) to the cscope index file
     *         or @c null if the cscope index file is not found.
     */
    private static String
    getCscopeFilePath()
    {
        final String path_sep = File.separator;
        final Buffer buffer = currentView.getBuffer();
        String filePath = buffer.getDirectory();
        String cscopefn = path_sep + indexFilename;

        /* Check this and all parent dirs for cscope index file */
        while (filePath.lastIndexOf(path_sep) > 0)
        {
            File f = new File(filePath + cscopefn);

            /* Return path to cscope file if it exists */
            if (f.exists()) { return f.getAbsolutePath(); }

            /* Try parent directory */
            filePath = filePath.substring(0, filePath.lastIndexOf(path_sep));
        }

        /* No cscope file found */
        Macros.error(currentView,
            indexFilename +
            ": File not found.  Please run \"cscope -b -c -R\" at the base of your code.");
        return null;
    }

    /**
     * This Cscope func is re-purposed to use the CscopeFinder plugin module
     *
     * @param   seemore Unused
     * @param   file    The filename in which the reference was found
     * @param   func    The function in which the reference was found
     */
    private static void
    putref(int seemore, char[] file, char[] func)
    {
        int i = blocki;
        int linenum = 0;
        int ilinenumstart = 0;
        int ilinenumstop = 0;
        char[] theblockp = blockp;
        String linenumstr;
        String tagLineText;
        String filename = String.valueOf(file,
                                         0,
                                         String.valueOf(file).indexOf('\0'));
        String funcname = String.valueOf(func,
                                         0,
                                         String.valueOf(func).indexOf('\0'));

        /* Scan back to get the line number */
        while ((i > 0) && !((blockp[i] == '\n') && (blockp[i-1] == '\n')))
        {
            i--;
        }
        i++;

        /* If at the start of the block w/o finding the line number */
        if (i == 1)
        {
            /* Fill second half of lookback buffer with head of current block */
            System.arraycopy(blockp, 0, lbbuf, LBBUFSIZ/2, LBBUFSIZ/2);

            /* Adjust pointer and index to the lookback buffer */
            theblockp = lbbuf;
            i = LBBUFSIZ / 2;

            /* Scan back to get the line number */
            while ((i > 0) &&
                   !((theblockp[i] == '\n') && (theblockp[i-1] == '\n')))
            {
                i--;
            }
            i++;
        }

        /* Extract the line number from the text */
        ilinenumstart = i;
        while (theblockp[i++] != ' ') {}
        ilinenumstop = i - 1;
        linenumstr = String.valueOf(theblockp,
                                    ilinenumstart,
                                    ilinenumstop - ilinenumstart);
        linenum = Integer.parseInt(linenumstr);

        /* Target line text to show depends on search type */
        if (findType == FIND_ALL_FCNS)
        {
            tagLineText = funcname;
        }
        else if (findType == FIND_INCLUDE)
        {
            tagLineText = patternString;
        }
        else
        {
            /* Scan forward to get the remaining text */
            i = blocki;
            while ((i < BUFSIZ) && !((blockp[i] == '\n') && (blockp[i+1] == '\n')))
            {
                i++;
            }

            /* Show symbol and text from rest of line; removing Cscope markup */
            tagLineText = String.valueOf(blockp,
                                         blocki,
                                         i - blocki).replaceAll("[\t\n`]", "");

            /* Only find-called-by doesn't need the pattern string */
            if (findType != FIND_CALLED_BY)
            {
                tagLineText = patternString + tagLineText;
            }
        }

        /* If the filename is relative, prepend the index path */
        if (!filename.startsWith("/"))
        {
            filename = indexPath + File.separator + filename;
        }

        /* Show results in a line on the stack */
        tagLines.addElement(new TargetLine("",
                                           filename,
                                           linenumstr + ": " + tagLineText,
                                           linenum,
                                           filename));
    }
    //}}}

    //{{{ C stdlib Replacements

    /** Compares two strings for equality */
    private static boolean
    strequal(char[] p1, char[] p2)
    {
        return ((p1[0] == p2[0]) && (strcmp(p1, p2) == 0));
    }

    /** Returns the difference of two strings */
    private static int
    strcmp(char[] p1, char[] p2)
    {
        char c1 = 0;
        char c2 = 0;
        int s1i = 0;
        int s2i = 0;

        do
        {
            c1 = p1[s1i++];
            c2 = p2[s2i++];
            /* AIOOBE case handled by ensuring all strs have trailing "\0" */

            if (c1 == '\0')
            {
                return c1 - c2;
            }
        }
        while (c1 == c2);

        return c1 - c2;
    }

    /**
     * Copies the source string into the destination string
     * up to and including '\0'.
     *
     * @return the number of characters copied (excluding '\0').
     */
    private static int
    strcpy(char[] dest, char[] src)
    {
        int i;

        for (i=0; i<dest.length; i++)
        {
            dest[i] = src[i];
            if (src[i] == '\0') break;
        }
        return i;
    }
    //}}}

    //{{{ Cscope Helper Functions

    /**
     * Gets the next character in the cross-reference
     *
     * Note: blockp is assumed not to be null
     */
    private static char
    getrefchar()
    throws IOException
    {
        return (blockp[++blocki + 1] != '\0') ? blockp[blocki] :
                   (readblock() != null) ? blockp[blocki] : '\0';
    }

    /** Matches the pattern to the string */
    private static boolean
    match()
    throws IOException
    {
        /* Removed support for regexp */

        /* it is a string pattern */
        return((blockp[blocki] == cpattern[0]) && matchrest());
    }

    /** Matches the rest of the pattern to the name */
    private static boolean
    matchrest()
    throws IOException
    {
        int i = 1;

        skiprefchar();
        do {
            while (blockp[blocki] == cpattern[i]) {
                ++blocki;
                ++i;
            }
        } while (blockp[blocki + 1] == '\0' && ((i = readblock(blockp)) == 0) && blockp != null);

        if (blockp[blocki] == '\n' && cpattern[i] == '\0') {
            return(true);
        }
        return(false);
    }

    /** Puts the rest of the cross-reference line into the string */
    private static void
    putstring(char[] s)
    throws IOException
    {
        char[] cp;
        char c;
        int ci;
        int si = 0;

        setmark('\n');
        cp = blockp;
        ci = blocki;
        do {
            while ((c = cp[ci]) != '\n') {
                if (c > '\177') {
                    c &= 0177;
                    s[si++] = dichar1[c / 8];
                    s[si++] = dichar2[c & 7];
                }
                else
                {
                    s[si++] = c;
                }
                ++ci;
            }
        } while (cp[ci + 1] == '\0' && ((ci = readblock(cp)) == 0) && (cp != null));
        blockp = cp;
        blocki = ci;
        s[si] = '\0';
    }

    /** Reads a block of the cross-reference */
    private static char[]
    readblock()
    throws IOException
    {
        /* Fill first half of lookback buffer with tail of current block */
        System.arraycopy(block,BUFSIZ-LBBUFSIZ/2, lbbuf,0, LBBUFSIZ/2);

        /* read the next block */
        blocklen = symrefs.read(block, 0, BUFSIZ);
        blockp = block;
        blocki = 0;

        /* Java difference: read returns -1 at end-of-stream; this avoids AIOOBE */
        if (blocklen != -1)
        {
            /* add the search character and end-of-block mark */
            block[blocklen] = blockmark;
            block[blocklen + 1] = '\0';
        }

        /* return null on end-of-file */
        if (blocklen == -1) {
            blockp = null;
        }
        else {
            ++blocknumber;
        }
        return(blockp);
    }

    /**
     * Reads a block of the cross-reference
     *
     * This overload func was created to handle cases when ci needed
     * to be reset to 0 when a new block was read in.
     *
     * @param cp    Pointer to block of mem that is read in
     * @return      Index into cp of start of block (always 0)
     */
    private static int
    readblock(char[] cp)
    throws IOException
    {
        /* Fill first half of lookback buffer with tail of current block */
        System.arraycopy(block,BUFSIZ-LBBUFSIZ/2, lbbuf,0, LBBUFSIZ/2);

        /* read the next block */
        blocklen = symrefs.read(block, 0, BUFSIZ);
        blockp = block;
        blocki = 0;

        /* Java difference: read returns -1 at end-of-stream; this avoids AIOOBE */
        if (blocklen != -1)
        {
            /* add the search character and end-of-block mark */
            block[blocklen] = blockmark;
            block[blocklen + 1] = '\0';
        }

        /* return null on end-of-file */
        if (blocklen == -1) {
            blockp = null;
        }
        else {
            ++blocknumber;
        }

        /* Caller's pointer set to new block and index is set to zero */
        cp = blockp;
        return(0);
    }

    /** Scans past the next occurence of this character in the cross-reference */
    private static char []
    scanpast(char c)
    throws IOException
    {
        char[] cp;
        int ci;

        setmark(c);
        cp = blockp;
        ci = blocki;
        do {    /* innermost loop optimized to only one test */
            while (cp[ci] != c) {
                ++ci;
            }
        } while (cp[ci + 1] == '\0' && ((ci = readblock(cp)) == 0) && (cp != null));
        blockp = cp;
        blocki = ci;
        if (cp != null) {
            skiprefchar();  /* skip the found character */
        }
        return(blockp);
    }

    /** Sets the mark character for searching the cross-reference file */
    private static void
    setmark(char c)
    {
        blockmark = c;
        block[blocklen] = blockmark;
    }

    /**
     * Skips the next character in the cross-reference
     *
     * Note: that blockp is assumed not to be null and that
     * this macro will always be in a statement by itself
     */
    private static void
    skiprefchar()
    throws IOException
    {
        if (blockp[++blocki + 1] == '\0')  { readblock(); }
    }
    //}}}

    //{{{ CSCOPE FUNCTIONS

    //{{{   findsymbol(char[] pattern)
    /** Finds the symbol in the cross-reference */
    private static char[]
    findsymbol(char[] pattern)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];    /* source file name */
        char[] function = new char[PATLEN + 1]; /* function name */
        char[] macro = new char[PATLEN + 1];    /* macro name */
        char[] symbol = new char[PATLEN + 1];   /* symbol name */
        char[] cp;
        int ci;
        char[] s;
        char firstchar;     /* first character of a potential symbol */
        boolean fcndef = false;

        /* Removed support for inverted index */

        scanpast('\t');          /* find the end of the header */
        skiprefchar();          /* skip the file marker */
        putstring(file);        /* save the file name */
        strcpy(function, global);/* set the dummy global function name */
        strcpy(macro, global);/* set the dummy global macro name */

        /* find the next symbol */
        /* note: this code was expanded in-line for speed */
        /* other macros were replaced by code using cp instead of blockp */
        cp = blockp;
        ci = blocki;
        for (;;) {
            setmark('\n');
            do {    /* innermost loop optimized to only one test */
                while (cp[ci] != '\n') {
                    ++ci;
                }
            } while (cp[ci + 1] == '\0' && ((ci = readblock(cp)) == 0) && (cp != null));

            /* skip the found character */
            if (cp != null && cp[++ci + 1] == '\0') {
                ci = readblock(cp);
            }
            if (cp == null) {
                break;
            }
            /* look for a source file, function, or macro name */
            if (cp[ci] == '\t') {
                blockp = cp;
                blocki = ci;
                switch (getrefchar()) {

                case NEWFILE:       /* file name */

                    /* save the name */
                    skiprefchar();
                    putstring(file);

                    /* check for the end of the symbols */
                    if (file[0] == '\0') {
                        return null;
                    }
//                    progress("Search", searchcount, nsrcfiles);
                    /* FALLTHROUGH */

                case FCNEND:        /* function end */
                    strcpy(function, global);
                    // goto notmatched;    /* don't match name */
                    cp = blockp;
                    ci = blocki;
                    continue;

                case FCNDEF:        /* function name */
                    fcndef = true;
                    s = function;
                    break;

                case DEFINE:        /* macro name */
                    if (fileversion >= 10) {
                        s = macro;
                    }
                    else {
                        s = symbol;
                    }
                    break;

                case DEFINEEND:     /* macro end */
                    strcpy(macro, global);
                    // goto notmatched;
                    cp = blockp;
                    ci = blocki;
                    continue;

                case INCLUDE:           /* #include file */
                    // goto notmatched;    /* don't match name */
                    cp = blockp;
                    ci = blocki;
                    continue;

                default:        /* other symbol */
                    s = symbol;
                }
                /* save the name */
                skiprefchar();
                putstring(s);

                /* Removed support for regexp */

                /* match the symbol to the text pattern */
                if (strequal(pattern, s)) {
                    // goto matched;
                    // BEGIN copy of "matched" block
                    /* output the file, function or macro, and source line */
                    if ((strcmp(macro, global) != 0) && s != macro) {
                        putref(0, file, macro);
                    }
                    else if (fcndef == true || s != function) {
                        fcndef = false;
                        putref(0, file, function);
                    }
                    else {
                        putref(0, file, global);
                    }
                    if (blockp == null) {
                        return null;
                    }
                    // END copy of "matched:" block
                }
                // goto notmatched;
                cp = blockp;
                ci = blocki;
                continue;
            }

            /* Removed support for regexp */

            /* match the character to the text pattern */
            if (cp[ci] == cpattern[0]) {
                blockp = cp;
                blocki = ci;

                /* match the rest of the symbol to the text pattern */
                if (matchrest()) {
                    s = null;
            matched:
                    /* output the file, function or macro, and source line */
                    if ((strcmp(macro, global) != 0) && s != macro) {
                        putref(0, file, macro);
                    }
                    else if (fcndef == true || s != function) {
                        fcndef = false;
                        putref(0, file, function);
                    }
                    else {
                        putref(0, file, global);
                    }
                    if (blockp == null) {
                        return null;
                    }
                }
            notmatched:
                cp = blockp;
                ci = blocki;
            }
        }
        blockp = cp;
        blocki = ci;

        return null;
    }
    //}}}

    //{{{   finddef(char[] pattern)
    /** Finds the function definition or #define  */
    private static char[]
    finddef(char[] pattern)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];   /* source file name */

        /* Removed support for invertedindex */

        /* find the next file name or definition */
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case NEWFILE:
                skiprefchar(); /* save file name */
                putstring(file);
                if (file[0] == '\0') { /* if end of symbols */
                    return null;
                }
                break;

            case DEFINE:               /* could be a macro */
            case FCNDEF:
            case CLASSDEF:
            case ENUMDEF:
            case MEMBERDEF:
            case STRUCTDEF:
            case TYPEDEF:
            case UNIONDEF:
            case GLOBALDEF:            /* other global definition */
                skiprefchar(); /* match name to pattern */
                if (match()) {

                    /* output the file, function and source line */
                    putref(0, file, pattern);
                }
                break;
            }
        }

        return null;
    }
    //}}}

    //{{{   findallfcns(char[] pattern)
    /** Finds all functions */
    private static char[]
    findallfcns(char[] dummy)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];   /* source file name */
        char[] function = new char[PATLEN + 1];        /* function name */

        /* find the next file name or definition */
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case NEWFILE:
                skiprefchar(); /* save file name */
                putstring(file);
                if (file[0] == '\0') { /* if end of symbols */
                    return null;
                }
                /* FALLTHROUGH */

            case FCNEND:               /* function end */
                strcpy(function, global);
                break;

            case FCNDEF:
            case CLASSDEF:
                skiprefchar(); /* save function name */
                putstring(function);

                /* output the file, function and source line */
                putref(0, file, function);
                break;
            }
        }
        return null;
    }
    //}}}

    //{{{   findcalling(char[] pattern)
    /** Finds the functions calling this function */
    private static char[]
    findcalling(char[] pattern)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];    /* source file name */
        char[] function = new char[PATLEN + 1]; /* function name */
        char[][] tmpfunc = new char[10][PATLEN + 1];/* 10 temporary function names */
        char[] macro = new char [PATLEN + 1];   /* macro name */
        char[] tmpblockp;
        int morefuns, i;

        /* Removed support for invertedindex */

        /* find the next file name or function definition */
        macro[0] = '\0';  /* a macro can be inside a function, but not vice versa */
        tmpblockp = null;
        morefuns = 0;   /* one function definition is normal case */
        for (i = 0; i < 10; i++) (tmpfunc[i][0]) = '\0';
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case NEWFILE:       /* save file name */
                skiprefchar();
                putstring(file);
                if (file[0] == '\0') {    /* if end of symbols */
                    return null;
                }
                //progress("Search", searchcount, nsrcfiles);
                strcpy(function, global);
                break;

            case DEFINE:        /* could be a macro */
                if (fileversion >= 10) {
                    skiprefchar();
                    putstring(macro);
                }
                break;

            case DEFINEEND:
                macro[0] = '\0';
                break;

            case FCNDEF:        /* save calling function name */
                skiprefchar();
                putstring(function);
                for (i = 0; i < morefuns; i++)
                    if ( 0 == strcmp(tmpfunc[i], function) )
                        break;
                if (i == morefuns) {
                    strcpy(tmpfunc[morefuns], function);
                    if (++morefuns >= 10) { morefuns = 9; }
                }
                break;

            case FCNEND:
                for (i = 0; i < morefuns; i++)
                {
                    tmpfunc[i][0] = '\0';
                }
                morefuns = 0;
                break;

            case FCNCALL:       /* match function called to pattern */
                skiprefchar();
                if (match()) {

                    /* output the file, calling function or macro, and source */
                    if (macro[0] != '\0') {
                        putref(1, file, macro);
                    }
                    else {
                        tmpblockp = blockp;
                        for (i = 0; i < morefuns; i++) {
                            blockp = tmpblockp;
                            putref(1, file, tmpfunc[i]);
                        }
                    }
                }
            }
        }
        morefuns = 0;

        return null;
    }
    //}}}

    //{{{   findinclude(char[] pattern)
    /** Find files #including this file */
    private static char[]
    findinclude(char[] pattern)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];   /* source file name */

        /* Removed support for invertedindex */

        /* find the next file name or function definition */
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case NEWFILE:              /* save file name */
                skiprefchar();
                putstring(file);
                if (file[0] == '\0') { /* if end of symbols */
                    return null;
                }
                break;

            case INCLUDE:              /* match function called to pattern */
                skiprefchar();
                skiprefchar(); /* skip global or local #include marker */
                if (match()) {

                    /* output the file and source line */
                    putref(0, file, global);
                }
            }
        }

        return null;
    }
    //}}}

    //{{{   char[] findcalledby(char[] pattern)
    /** Find the functions called by this function
     *
     * @return null if nothing is found; non-null otherwise.
     */
    private static char[]
    findcalledby(char[] pattern)
    throws IOException
    {
        char[] file = new char[PATHLEN + 1];    /* source file name */
        char[] found_caller = null; /* seen calling function? */
        boolean macro = false;

        /* Removed support for invertedindex */

        /* find the function definition(s) */
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case NEWFILE:
                skiprefchar();  /* save file name */
                putstring(file);
                if (file[0] == '\0') {    /* if end of symbols */
                    return(found_caller);
                }
                //progress("Search", searchcount, nsrcfiles);
                break;

            case DEFINE:        /* could be a macro */
                if (fileversion < 10) {
                    break;
                }
                macro = true;
                /* FALLTHROUGH */

            case FCNDEF:
                skiprefchar();  /* match name to pattern */
                if (match()) {
                    found_caller = file;  /* DWH: non-null means true */
                    findcalledbysub(file, macro);
                }
                break;
            }
        }

        return (found_caller);
    }

    private static void
    findcalledbysub(char[] file, boolean macro)
    throws IOException
    {
        /* find the next function call or the end of this function */
        while (scanpast('\t') != null) {
            switch (blockp[blocki]) {

            case DEFINE:		/* #define inside a function */
                if (fileversion >= 10) {	/* skip it */
                    while (scanpast('\t') != null &&
                        blockp[blocki] != DEFINEEND)
                        ;
                }
                break;

            case FCNCALL:		/* function call */

                /* output the file name */
                //(void) fprintf(refsfound, "%s ", file);

                /* output the function name */
                skiprefchar();
                //putline(refsfound);
                //(void) putc(' ', refsfound);

                /* output the source line */
                //putsource(1, refsfound);

                putref(0, file, file/*dummy*/);
                break;

            case DEFINEEND:		/* #define end */

                //if (invertedindex == false) {
                    if (macro == true) {
                        return;
                    }
                    break;	/* inside a function */
                //}
                /* FALLTHROUGH */

            case FCNDEF:		/* function end (pre 9.5) */

                //if (invertedindex == false) break;
                break;
                /* FALLTHROUGH */

            case FCNEND:		/* function end */
            case NEWFILE:		/* file end */
                return;
            }
        }
    }
    //}}}
    //}}}
}