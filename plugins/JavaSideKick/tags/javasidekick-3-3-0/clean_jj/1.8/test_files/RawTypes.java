// RawTypes
// fails on this line, encountered <EOF> at col 14
class Outer<T> {
    T t;
    class Inner {
        T setOuterT(T t1) { 
            t = t1; 
            return t; 
        }
    }
}