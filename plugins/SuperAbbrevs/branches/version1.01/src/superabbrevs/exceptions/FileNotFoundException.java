package superabbrevs.exceptions;

public class FileNotFoundException extends Exception {
	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotFoundException(String message) {
		super(message);
	}

	public FileNotFoundException(Throwable cause) {
		super(cause);
	}
}
