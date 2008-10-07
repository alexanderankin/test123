package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

public class TextField implements Field {
	
	private String text;
	
	public TextField(String text) {
		this.text = text;
	}

	public int getLength() {
		return text.length();
	}

    @Override
	public String toString() {
		return text;
	}

    public void accept(TemplateFieldVisitor visitor) {
        visitor.visit(this);
    }
}
