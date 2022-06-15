@Doo package doo.foo.hoo;


@Hoo(name="Hoo", value="hoo")
public class Annotations {
    public Annotations() {
        @Foo byte a;
        @Foo short b;
        @Foo int c;
        @Foo long d;
        @Foo char e;
        @Foo float f;
        @Foo double g;
        @Foo boolean h;
        @Foo int[] i;
        
        
    }
    
    @MyAnnotation(name="someName",  value = "Hello World")
    public void doSomething(){}
    
    public static void doSomethingElse(@MyAnnotation(name="aName", value="aValue") String parameter){
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    
    public @interface MyAnnotation {
        public String name();
        public String value();
    }
}

