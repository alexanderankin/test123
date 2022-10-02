/**
 * https://openjdk.java.net/jeps/409
 */
class SealedClasses {
    interface I1 {
    }

    class C0 {
    }

    sealed class SC1 extends C0 implements I1 permits FC1, FC2 {
    }

    sealed class SC2 {
        void f() {
            var non = 1;
            var sealed = 2;
            var ns = non - sealed;
            var permits = 1;
            var record = 1;
        }

    }

    final class FC1 extends SC1 {
    }

    final class FC2 extends SC1 {
    }

    non-sealed class NSC1 extends SC2 {
    }

    class C1 extends NSC1 {
    }

}
