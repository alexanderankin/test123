package treebufferswitcher.util;

public class PathUtils {

    public static String getParentOfPath(String path) {
        int lastDelimiterIndex = path.lastIndexOf('/');
        if (lastDelimiterIndex == -1)
            lastDelimiterIndex = path.lastIndexOf('\\');
        if (lastDelimiterIndex != -1) {
            return path.substring(0, lastDelimiterIndex);
        }
        return "";
    }

    public static String getLastPathComponent(String path) {
        int lastDelimiterIndex = path.lastIndexOf('/');
        if (lastDelimiterIndex == -1)
            lastDelimiterIndex = path.lastIndexOf('\\');
        if (lastDelimiterIndex != -1) {
            return path.substring(lastDelimiterIndex + 1);
        }
        return path;
    }

}