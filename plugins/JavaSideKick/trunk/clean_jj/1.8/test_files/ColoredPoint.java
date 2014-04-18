
class ColoredPoint extends Point {
    static final int WHITE = 0, BLACK = 1;
    int color;
    void coloredPoint(int x, int y) {
        this(x, y, WHITE);
    }
    void coloredPoint(int x, int y, int color) {
        super(x, y);
        this.color = color;
    }
}