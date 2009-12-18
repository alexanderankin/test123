package jdiff.util.patch;


public class Patch {

    public static String patch( String patch, String text ) throws Exception {
        String results = "";
        int patch_type = PatchUtils.getPatchType(patch);
        switch ( patch_type ) {
            case PatchUtils.NORMAL:
                results = Patch.patchNormal( patch, text );
                break;
            case PatchUtils.UNIFIED:
                results = Patch.patchUnified( patch, text );
                break;
            case PatchUtils.CONTEXT:
                throw new Exception("Unsupported patch file format, JDiff does not currently support 'context' diffs.");
            default:
                throw new Exception("Unsupported patch file format.");
        }
        return results;
    }

    public static String patchNormal( String patch, String text ) {
        return jdiff.util.patch.normal.Patch.patchNormal( patch, text );

    }

    public static String patchUnified( String patch, String text ) {
        return jdiff.util.patch.unified.Patch.patchUnified( patch, text );
    }
}