package jimporter.importer;

public class ClassImporterFactory {
    public static ClassImporter getInstance(String mode) {
        ClassImporter toReturn;

        if (mode.toLowerCase().equals("jsp")) {
            toReturn = new JSPClassImporter();
        } else {
            toReturn = new JavaClassImporter();
        }

        return toReturn;
    }
}
