package tests;

public class FooClassForCtagsTests {
	
    private String name;
    private byte[] password;

    public FooClassForCtagsTests(String name, byte[] pass) {
        this.name = name;
        password = pass;
    }

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(byte[] bytes) {
        password = bytes;
    }

    public String toString() {
        return name;
    }
}
