// Chapter 6

public class HashSet<E> extends AbstractSet<E> { 
}

public class HashMap<K,V> extends AbstractMap<K,V> {
}

public class ThreadLocal<T> {
}

public interface Functor<T, X extends Throwable> {
    T eval() throws X;
}

interface ProcessStates {
    int PS_RUNNING   = 0;
    int PS_SUSPENDED = 1;
}

class Test {
    public static void main(String[] args) {
        Class c = System.out.getClass();
        System.out.println(c.toString().length() +
                           args[0].length() + args.length);
    }
}

class Test2 {
    public static void main(String[] args) {
        int i;
        class Local {
            {
                for (int i = 0; i < 10; i++)
                    System.out.println(i);
            }
        }
        new Local();
    }
}

class Test3 {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++)
            System.out.print(i + " ");
        for (int i = 10; i > 0; i--)
            System.out.print(i + " ");
        System.out.println();
    }
}