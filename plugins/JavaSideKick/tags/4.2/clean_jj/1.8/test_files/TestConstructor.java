package test.a.b.c;


import java.util.ArrayList;
import static java.lang.Math;
import javax.swing.*;





public class TestConstructor {
    
    public String someString = "whatever";
    
    private static final int a;
    
    // test arrays
    
    
    // test constructor
    public TestConstructor() {
        
    }
    
    public TestConstructor(int a) {
        this(a, null);
    }
    
    public TestConstructor(int a, String b) {
        this.a = a;
        b = b.substring(3);
    }
    
    public int someMethod() {
        int c = 6;
        int d = 7;
        int e = c + d;
        this.someString = "abc";
        return e + d * c;
    }
    
    private void methodB(int param1, String param2) {
        methodC(param1, param2);    
    }

    private static void methodC(int param1, String p2) {
           
    }
    
    private void methodD() throws IOException, Exception {
        GUIUtilites.path = "whatever";
    }
    
    private static class InnerClass1 {
        
    }
}