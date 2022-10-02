/**
 * https://openjdk.java.net/jeps/406
 */
class PatternMatching4switchExp {
    void f( int i ) {
    }

    void f1( Object obj ) {
        switch ( obj ) {
            case null -> f( 0 );
            case String s -> f( 1 );
            case int [] a -> f( 2 );
            default -> f( -1 );
        }
    }

    void f2( Object obj ) {
        switch ( obj ) {
            case null -> f( 0 );
            case Long l -> f( 1 );
            case Integer i -> f( 1 );
            case int [] a -> f( 2 );
            default -> f( -1 );
        }
    }

    void f3( Object o ) {
        switch ( o ) {
            case null:
            case Long l:
                f( 0 );
                break;

            default:
                break;
        }
    }

    enum E1 {
        var;

    }

    void f4() {
        var var = E1.var;
        switch ( var ) {
            case var:
                return;

            default:
                break;
        }

        switch ( var ) {
            case var ->
            {
            }

            default -> {
            }
        }
    }

    int f5( Number n ) {
        return switch ( n ) {
            case Long l && l.intValue() == 1 && l.byteValue() == 1 -> l.byteValue();
            case Long var -> var.byteValue();
            case Integer i -> i.byteValue();
            default -> throw new RuntimeException( "" );
        };
    }

    Function<Integer, String> f6( Object obj ) {
        boolean b = true;
        return switch ( obj ) {
            case String var && b -> t -> var;
            default -> t -> "Default string";
        };
    }

    int dummy() {
        return 0;
    }

    Function<Integer, String> f7( Object obj ) {
        boolean b = true;
        boolean b2 = true;
        boolean b3 = true;
        return switch ( obj ) {
            case ( ( ( String s ) && ( b && b2 ) ) && s.length() > 0 && dummy() == 1 ) -> t -> s;
            case ( ( ( Integer i && b && b2 ) && ( b && b2 ) ) && b3 && ( b && b2 ) ) -> t -> "";
            case ( ( ( Integer i && b && b2 ) && ( b && b2 ) ) && b3 && ( b && b2 && !b3 ) ) ->
            {
                yield t -> "";
            }

            case final Long l && ( b ? b2 : b3 ) ->
            {
                yield t -> "";
            }

            default -> t -> "Default string";
        };
    }

    void f8( Object o, int i ) {
        switch ( i ) {
            case 1, 2 :
            case 3, 4 :
            {
            }

        }
        switch ( o ) {
            case Number b:
{
            }

            default:
{
            }
        }
        var f = switch ( o ) {
            case final I2 l :
            {
                yield switch ( o ) {
                    case Byte b -> 1;
                    default -> 0;
                };
            }

            default : {
                yield 1;
            }
        };
    }

}

