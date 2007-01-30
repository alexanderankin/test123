package superabbrevs.template;

public abstract class SelectableField implements Field {

	private int offset;
	
	public abstract int getLength();

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset(){
		return offset;
	}

	public boolean inField(int pos){
		return offset <= pos && pos <= offset + getLength(); 
	}
}
