package jdiff.util;

import org.gjt.sp.jedit.jEdit;

/**
 * An extension of Diff to be able to apply some jEdit properties while
 * keeping the original Diff original.
 */
public class JDiffDiff extends Diff {
    public JDiffDiff( Object[] a, Object[] b ) {
        super( a, b );
        heuristic = jEdit.getBooleanProperty( "jdiff.heuristic", false );
        no_discards = jEdit.getBooleanProperty( "jdiff.no_discards", false );
    }
}