package var.var.sealed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
class SwitchExpressions {
    int fn1(int n) {
         switch(n) {
             case 99 -> {
                throw new RuntimeException("");
            }
         };
    }
}
