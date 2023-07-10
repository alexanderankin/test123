class Ids {
    class oo {
        class opens <T> {
            enum E {
                provides;

            }

            class provides <S> {
                void f() {
                    opens<Byte>.provides<Long> b1 = new opens<>().new provides<>() {
                    };
                    opens<Byte>.provides<Long> b2 = new opens().new provides() {
                    };
                }

                void g() {
                    E e = E.provides;
                    switch (e) {
                        case provides:
                            break;
                    }
                }

                <T> Object var() {
                    return null;
                }

                provides<Long> get() {
                    return null;
                }

                class with <S> {
                }

                static class SS <R> {
                    interface Sup <T> {
                        T get ();
                    }

                }

                void h() {
                    var o = get().<Long> var();
                    SS.Sup<provides<Long>.with<Long>> s = @Issue1897.Dum1 provides<Long>.with<Long> :: new;
                }

                class R {
                    <to> void f() {
                    }

                }

            }

        }

    }

    static class opens {
        enum requires {
            opens;

        }

        public static <T> void with(String s) {
        }

        interface with {
            default void f (){
            }
        }

        class exports implements with {
            void g() {
                with.super.f();
            }

        }

        @interface to {
        }

        class module {
            public static <T> void with(String s) {
                try {
                }
                catch ( Exception var) {
                }
            }

        }

        record provides(int to) {
            void f() {
                opens o = new opens();
                BiFunction<Long, Long, Long> b = (opens, with) -> 1L;
                Consumer<String> c = opens.module :: <Byte> with;
            }

        }

    }

}