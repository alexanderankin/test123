class Yield {
    int f(Object o) {
        final var yield = 1;
        return switch (o) {
            case Long l ->
                {
                // var yield = 1;
                yield yield;
                }
            default ->             {
                yield yield;
            }
        };
    }

    int yield(int yield) {
        return yield;
    }

}
