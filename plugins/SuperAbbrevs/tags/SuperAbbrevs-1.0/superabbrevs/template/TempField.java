package superabbrevs.template;

public class TempField implements Field {

	private Integer number;
	
	public TempField(Integer number){
		this.number = number;
	}
	public Integer getNumber() {
		return number;
	}	
	
	public String toString() {
		// sould never be shown
		return "<temp>";
	}
	
	public int getLength() {
		return 6;
	}
}
