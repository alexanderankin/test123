package treebufferswitcher.model;

public class PathItem {

    public final String title;
    public final int level;

    PathItem(String title, int level) {
        this.title = title;
        this.level = level;
    }

    @Override
    public String toString() {
        return title;
    }

}