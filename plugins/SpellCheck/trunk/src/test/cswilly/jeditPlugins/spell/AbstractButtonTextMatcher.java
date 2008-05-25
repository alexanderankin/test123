package cswilly.jeditPlugins.spell;


//{{{ Imports

//{{{ 	Java Classpath
import javax.swing.*;
//}}}



//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
//}}}



///}}}

public class AbstractButtonTextMatcher
{
	static <T extends AbstractButton>  GenericTypeMatcher<T> withText(Class<T> classe,final String text){
		return new GenericTypeMatcher<T>(){
			@Override protected boolean isMatching(T button) {
				return text.equals(button.getText());
			}
		};
	}
}
