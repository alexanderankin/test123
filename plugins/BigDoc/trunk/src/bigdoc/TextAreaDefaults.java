
/*
 * TextAreaDefaults.java - Encapsulates default values for various settings
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
package bigdoc;


import java.awt.Color;

import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.jEdit;


/**
 * Encapsulates default settings for a text area. This can be passed
 * to the constructor once the necessary fields have been filled out.
 * The advantage of doing this over calling lots of set() methods after
 * creating the text area is that this method is faster.
 */
public class TextAreaDefaults {

    private static TextAreaDefaults DEFAULTS;
    public InputHandler inputHandler;
    public SyntaxDocument document;
    public boolean editable;
    public boolean caretVisible;
    public boolean caretBlinks;
    public boolean blockCaret;
    public int electricScroll;
    public int cols;
    public int rows;
    public SyntaxStyle[] styles;
    public Color caretColor;
    public Color selectionColor;
    public Color lineHighlightColor;
    public boolean lineHighlight;
    public Color bracketHighlightColor;
    public boolean bracketHighlight;
    public Color eolMarkerColor;
    public boolean eolMarkers;
    public boolean paintInvalid;
    public JPopupMenu popup;

    /**
     * Returns a new TextAreaDefaults object with the default values filled
     * in.
     */
    public static TextAreaDefaults getDefaults() {
        if ( DEFAULTS == null ) {
            DEFAULTS = new TextAreaDefaults();

            DEFAULTS.inputHandler = new DefaultInputHandler();
            DEFAULTS.inputHandler.addDefaultKeyBindings();
            DEFAULTS.document = new SyntaxDocument();
            DEFAULTS.editable = false;

            DEFAULTS.caretVisible = true;
            DEFAULTS.caretBlinks = jEdit.getBooleanProperty( "view.caretBlink" );
            DEFAULTS.electricScroll = 3;

            DEFAULTS.cols = 80;
            DEFAULTS.rows = 25;
            DEFAULTS.styles = SyntaxUtilities.getDefaultSyntaxStyles();
            DEFAULTS.caretColor = jEdit.getColorProperty( "view.caretColor" );
            DEFAULTS.selectionColor = jEdit.getColorProperty( "view.selectionColor" );
            DEFAULTS.lineHighlightColor = jEdit.getColorProperty( "view.lineHighlightColor" );
            DEFAULTS.lineHighlight = jEdit.getBooleanProperty( "view.lineHighlight", true );
            DEFAULTS.bracketHighlightColor = jEdit.getColorProperty( "view.structureHighlightColor" );
            DEFAULTS.bracketHighlight = jEdit.getBooleanProperty( "view.structureHighlight", true );
            DEFAULTS.eolMarkerColor = jEdit.getColorProperty( "view.eolMarkerColor" );
            DEFAULTS.eolMarkers = jEdit.getBooleanProperty( "view.eolMarkers", false );
            DEFAULTS.paintInvalid = true;
        }

        return DEFAULTS;
    }
}
