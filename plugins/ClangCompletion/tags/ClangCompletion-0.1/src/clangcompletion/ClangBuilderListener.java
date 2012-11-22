package clangcompletion;

public interface ClangBuilderListener
{
	public void errorRecieved(String line);
	
	public void outputRecieved(String line);
	
	public void exited();
}
