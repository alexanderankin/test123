import java.util.*;


enum Color {
    RED, GREEN, BLUE;

    Color() {
        colorMap.put(toString(), this);
    }


    static final Map<String, Color> colorMap = new HashMap<String, Color>();



    static {
        for ( Color c : Color.values()) {
            colorMap.put(c.toString(), c);
        }
    }
}

enum E1 {
    ONE;

}

enum E {
    provides;

}