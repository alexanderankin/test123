/**
 * https://openjdk.java.net/jeps/395
 */
class Records {
    interface I1 {
    }

    final record R1( @Dummy2 @Dummy int x ) {
        R1( int x ) {
            this.x = x;
        }

        enum E {
            ONE;

            record ER() {
            }
        }

        class C {
            record CR() {
            }

        }

        interface I {
            record IR (){
            }
        }

        private static final record R() implements I1 {
        }

        protected static final record R2() implements I1 {
        }

        public static final record R3() implements I1 {
        }

        static final record R4() implements I1 {
        }

    }

    record R2() {
        public @interface TM1 {
            record AR() {
            }
        }

    }

    record R3( int x, T y ) {
    }

    record R4( int x, T y ) implements I1 {
    }

    void fn1() {
        final record Pt( int x, int y ) implements I1, R1.I {
            void fn( T t ) {
            }

            <TT> void f() {
            }

            // final int x; implicitly defined
            Pt( int x, int y ) {
                this.x = x;
                this.y = y;
            }

            // private int c = 1; not allowed
            private static final int C = 1;    //allowed

            static class C {
            }

        }        Pt<Long, Long> p = new Pt<>( 1, 2 );
        p.fn( 1L );
    }

}

