package jimporter.importer;
/*
 * Created by IntelliJ IDEA.
 * User: mflower
 * Date: Oct 8, 2002
 * Time: 11:54:09 PM
 * To change this template use Options | File Templates.
 */
public class ImportListFactory {
    public static ImportList getInstance(String mode) {
        ImportList toReturn = null;

        if (mode.toLowerCase().equals("jsp")) {
            toReturn = new JSPImportList();
        } else {
            toReturn = new JavaImportList();
        }

        return toReturn;
    }
}
