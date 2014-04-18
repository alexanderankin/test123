
class ColoredPoint extends Point {
    static final int WHITE = 0, BLACK = 1;
    int color;
    ColoredPoint(int x, int y) {
        super(x, y);
        this.color = BLACK;
    }
    ColoredPoint(int x, int y, int color) {
        super(x, y);
        this.color = color;
    }
}