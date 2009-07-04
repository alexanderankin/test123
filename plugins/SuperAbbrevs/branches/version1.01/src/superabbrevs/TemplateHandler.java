package superabbrevs;

public interface TemplateHandler {

	public abstract boolean isInTempateMode();

	public abstract boolean selectNextAbbrev();

	public abstract void selectPrevAbbrev();

	public abstract void stopTemplateMode();

}