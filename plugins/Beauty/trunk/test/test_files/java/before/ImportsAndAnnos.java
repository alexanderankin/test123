package var.var.sealed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@interface Dummy {
}

@interface Dummy2 {
}

@Target({ElementType.TYPE, ElementType.TYPE_USE})

@interface Dummy3 {
}
