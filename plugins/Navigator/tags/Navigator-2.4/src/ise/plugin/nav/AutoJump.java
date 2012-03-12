package ise.plugin.nav;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.View;

public class AutoJump extends EBMessage
{
	public static final Object STARTED = "STARTED";
	public static final Object ENDED = "ENDED";
	private Object what;

	public AutoJump(View view, Object what)
	{
		super(view);
		this.what = what;
	}
	public View getView()
	{
		return (View) getSource();
	}
	public Object getWhat()
	{
		return what;
	}
	public String paramString()
	{
		return "what=" + what + super.paramString();
	}
}
