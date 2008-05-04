package jdiff.util.patch;


public class Patch {
    public static String patchNormal( String patch, String text ) {
        return jdiff.util.patch.normal.Patch.patchNormal( patch, text );

    }

    public static String patchUnified( String patch, String text ) {
        return jdiff.util.patch.unified.Patch.patchUnified( patch, text );
    }
}