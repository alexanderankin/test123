import codebook.java.ApiDownloader;
public class CodeBookPluginTests {
	public static void main(String[] args) {
		String[] info = codebook.java.JavaRunner.getClassAndPackage("java.lang.String");
		if (info == null) System.out.println("Sorry.");
		System.out.println(info[0]+", "+info[1]);
	}
}