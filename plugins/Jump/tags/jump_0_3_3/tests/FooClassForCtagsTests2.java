package tests;

import java.util.Properties;

public class FooClassForCtagsTests2 {
	
    private String login;
    private byte[] connection;
    private Properties props;

    public FooClassForCtagsTests2() {}

	public byte[] getConnection() {
		return connection;
	}

	public void setConnection(byte[] connection) {
		this.connection = connection;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}
}
