class TestObjectCreation {
    public static void main(String[] args) {
        /* A Point is explicitly created
           using newInstance: */
        Point p = null;
        try {
            p = (Point)Class.forName("Point").newInstance();
        } catch (Exception e) {
            System.out.println(e);
        }

        /* An array is implicitly created 
           by an array constructor: */
        Point a[] = { new Point(0,0), new Point(1,1) };

        /* Strings are implicitly created 
           by + operators: */
        System.out.println("p: " + p);
        System.out.println("a: { " + a[0] + ", " + a[1] + " }");
    
        /* An array is explicitly created
           by an array creation expression: */
        String sa[] = new String[2];
        sa[0] = "he"; sa[1] = "llo";
        System.out.println(sa[0] + sa[1]);
    }
}