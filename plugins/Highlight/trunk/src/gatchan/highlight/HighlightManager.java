package gatchan.highlight;

/**
 * The highlight manager.
 *
 * @author Matthieu Casanova
 */
public interface HighlightManager {

  /**
   * Add a Highlight listener.
   *
   * @param listener the new listener
   */
  void addHighlightChangeListener(HighlightChangeListener listener);

  /**
   * Remove a highlight listener.
   *
   * @param listener the listener to be removed
   */
  void removeHighlightChangeListener(HighlightChangeListener listener);

  /**
   * alert the listeners that the highlights changed
   */
  void fireHighlightChangeListener(boolean highlightEnable);

  /**
   * Returns the number of highlights.
   *
   * @return how many highlights are in
   */
  int countHighlights();

  /**
   * Return the Highlight at index i.
   *
   * @param i the index of the highlight
   * @return a highlight
   */
  Highlight getHighlight(int i);

  /**
   * Add a Highlight in the list.
   *
   * @param highlight the highlight to be added
   */
  void addElement(Highlight highlight);

  void dispose();

  /**
   * Enable or disable the highlights.
   *
   * @param highlightEnable the news status
   */
  void setHighlightEnable(boolean highlightEnable);

  /**
   * If the highlights must not be displayed it will returns false.
   *
   * @return returns true if highlights are displayed, false otherwise
   */
  boolean isHighlightEnable();

  /** remove all Highlights. */
  void removeAll();

  void removeRow(int index);
}
