package xml.completion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.xerces.xs.XSElementDeclaration;

/**
 * An extension to ElementDecl specific for w3c xsd schemas, that stores the entire
 * XSElementDeclaration for later reference.
 * 
 * @author ezust
 *
 */
public class XsdElementDecl extends ElementDecl
{
	XSElementDeclaration xsed; 

	public XsdElementDecl(XSElementDeclaration element, CompletionInfo completionInfo,
		String name, String content) 
	{
		super(completionInfo, name, content);
		xsed = element;
	}
	/**
	 * Returns a List of ElementDecl objects which are equivalent to this one, if it is indeed
	 * an abstract class.
	 */
	public List findReplacements() 
	{	
		if (xsed.getAbstract() == false) return null;
		String subGroupName = name;
		LinkedList retval = new LinkedList();
		Iterator itr = completionInfo.elements.iterator();
		while (itr.hasNext()) try 
		{
			XsdElementDecl element = (XsdElementDecl) itr.next();
			XSElementDeclaration subGroup = element.xsed.getSubstitutionGroupAffiliation();
			if (subGroup != null && subGroup.getName().equals(subGroupName)) 
			{
				retval.add(element);
			}
		}
		catch (NullPointerException npe) {}
		catch (ClassCastException cce) {}
		return retval;
	}

	

}
