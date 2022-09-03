public class Annotations implements IfA, IfB {
    @Valid
    private List<@NotNull String> property;
}
