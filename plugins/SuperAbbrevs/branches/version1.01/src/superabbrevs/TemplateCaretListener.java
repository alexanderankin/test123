package superabbrevs;

import javax.swing.event.CaretListener;

public interface TemplateCaretListener extends CaretListener {

	public abstract void startListening();

	public abstract void stopListening();

}