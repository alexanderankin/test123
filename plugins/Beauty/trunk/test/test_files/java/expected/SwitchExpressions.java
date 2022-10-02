class SwitchExpressions {
    int fn1(int n) {
        var r = switch (n) {
            case 1, 2, 3 + 3, k, C, SC1.C -> 3 + SC1.C;
            case 20 -> 3 + 4 + C - k;
            case 21 ->
                {
                int ff = 222;
                yield ff;
                }
            case 22 ->
                {
                yield 33 + 3;
                }
            case 99 ->
                {
                throw new RuntimeException("");
                }
            default -> 0;
        };
        return r;
    }

    void fn6() {
        switch (1) {
            case 1 ->
                {
                }
        }
    }

    void fn7() {
        switch (1) {
            case 1 ->
                {
                }
            case 2 ->
                {
                }
        }
    }

    void fn8() {
        var i = 1;
        switch (1) {
        }
        var f = 2;

        switch (2) {
            case 2 ->
                {
                f = 3;
                }
        }
    }

    void fn9(String s) {
        switch (s) {
            case "" ->
                {
                }
            default ->             {
            }
        }
    }

    void fn10() {
        var i = switch (1) {
            case 1 ->

                switch (2) {
                case 2 -> 0;
                default -> 2;
                };
            default -> 2;
        };
    }

}
