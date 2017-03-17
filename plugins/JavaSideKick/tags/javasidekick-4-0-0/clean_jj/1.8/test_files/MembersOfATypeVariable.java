// Members of a Type Variable
package TypeVarMembers;

class C { 
    public    void mCPublic()    {}
    protected void mCProtected() {} 
              void mCPackage()   {}
    private   void mCPrivate()   {} 
} 

interface I {
    void mI();
}

class CT extends C implements I {
    public void mI() {}
}

class Test {
    <T extends C & I> void test(T t) {
        // fails on this line encountered "(" expected one of a bunch of things
        // see MethodInvocation
        t.mI();           // OK
        t.mCPublic();     // OK 
        t.mCProtected();  // OK 
        t.mCPackage();    // OK
        t.mCPrivate();    // Compile-time error
    } 
}