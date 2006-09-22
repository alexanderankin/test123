package xsearch;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
public class XSearch extends Object
{
	public static final String DOCKABLE_NAME = "xsearch";
	public static final int SEARCH_TYPE_SINGLE = 1;
	public static final int SEARCH_TYPE_CURRENT_BUFFER = 2;
	public static final int SEARCH_TYPE_ALL_BUFFERS = 3;
	public static final int SEARCH_TYPE_DIRECTORY = 4;
	public static final int SEARCH_TYPE_PROJECT = 5;

	public static final int SEARCH_PART_NONE = 0;
	public static final int SEARCH_PART_SUFFIX= 1;
	public static final int SEARCH_PART_PREFIX= 2;
	public static final int SEARCH_PART_WHOLE_WORD=3;
	
	public static final int SEARCH_IN_OUT_NONE = 0;
	public static final int SEARCH_IN_OUT_INSIDE = 1;
	public static final int SEARCH_IN_OUT_OUTSIDE = 2;

	public static final boolean FIND_OPTION_SILENT = true;
	
	public static void searchInProject(View view, JEditTextArea textArea, boolean quick) 
	{
		if (quick) {
			SearchAndReplace.quickXfind(view, textArea, 
				xsearch.XSearch.SEARCH_TYPE_PROJECT);
		}
		else {
			xsearch.XSearchPanel.showSearchPanel(view,textArea.getSelectedText(),
				xsearch.XSearch.SEARCH_TYPE_PROJECT);
		}
	}
}
