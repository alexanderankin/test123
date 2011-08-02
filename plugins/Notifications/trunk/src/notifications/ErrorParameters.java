package notifications;

import java.awt.Component;

class ErrorParameters
{
	Component comp;
	String path;
	String messageProp;
	Object[] args;
	public ErrorParameters(Component comp, String path, String messageProp, Object[] args)
	{
		this.comp = comp;
		this.path = path;
		this.messageProp = messageProp;
		this.args = args;
	}
}