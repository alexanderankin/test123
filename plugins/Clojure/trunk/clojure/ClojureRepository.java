package clojure;

public class ClojureRepository {
	final static String ROOT_URL = "http://repo1.maven.org/maven2/org/clojure/";
	final static String CLOJURE_URL = ROOT_URL + "clojure/";

	public static String forLib(String lib) {
		return ROOT_URL + lib + "/";
	}
}
