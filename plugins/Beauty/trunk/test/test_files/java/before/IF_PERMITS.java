
class IF_PERMITS {
    final class T1 implements I1 {

    }

    final class T2 implements I1 {

    }

    interface I2 {
    }

    sealed interface I1 extends I2 permits T1, T2 {

    }

}