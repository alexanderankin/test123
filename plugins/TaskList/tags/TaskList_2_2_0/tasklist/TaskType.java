/*
* TaskType.java - TaskList plugin
* Copyright (C) 2001 Oliver Rutherfurd
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*
* $Id$
*/

package tasklist;

//{{{ imports
import java.net.URL;
import java.util.regex.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}

public class TaskType {
	//{{{ TaskType constructors
	public TaskType() {
		setName( "" );
		setPattern( "" );
		setSample( "" );
		setIgnoreCase( false );
		setIconPath( "Exclamation.gif" );
	}

	public TaskType( String name, String pattern, String sample,
	        boolean ignoreCase, String iconPath ) {
		this( name, pattern, sample, ignoreCase, iconPath, true );
	}

	public TaskType( String name, String pattern, String sample,
	        boolean ignoreCase, String iconPath, boolean active ) {
		setName( name );
		setPattern( pattern );
		setSample( sample );
		setIgnoreCase( ignoreCase );
		setIconPath( iconPath );	// will attempt to load icon too
		setActive( active );

		compileRE();
	} //}}}

	//{{{ extractTask() method
	public Task extractTask( Buffer buffer, String tokenText,
	        int line, int tokenOffset ) {
		Matcher match = this.re.matcher( tokenText );
		if ( !match.find() ) {
			return null;
		}

		String identifier = match.group( 1 );
		String comment = match.group( 2 );
		String text = tokenText.substring( match.start( 0 ) + 1 );
		int start = tokenOffset + match.start( 0 ) + 1;
		int end = start + text.length() - 1;
		return new Task( buffer,
		        icon,
		        line,
		        identifier,
		        comment,
		        text, 	// text to display in list: identifier, whitespace, and any comment
		        start,
		        end );
	} //}}}

	//{{{ get/setName() methods
	public String getName() {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
	} //}}}

	//{{{ get/setPattern() methods
	public String getPattern() {
		return this.pattern;
	}

	public void setPattern( String pattern ) {
		if ( this.pattern == null || !this.pattern.equals( pattern ) ) {
			this.pattern = pattern;
			compileRE();
		}
	} //}}}

	//{{{ get/setSample() methods
	public String getSample() {
		return this.sample;
	}

	public void setSample( String sample ) {
		this.sample = sample;
	} //}}}

	//{{{ get/setIgnoreCase() methods
	public boolean getIgnoreCase() {
		return this.ignoreCase;
	}

	public void setIgnoreCase( boolean ignoreCase ) {
		if ( this.ignoreCase != ignoreCase ) {
			this.ignoreCase = ignoreCase;
			this.reFlags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;
			compileRE();
		}
	} //}}}

	public Icon getIcon() {
		return this.icon;
	}

	//{{{ get/setIconPath() method
	public String getIconPath() {
		return this.iconPath;
	}

	public void setIconPath( String iconPath ) {
		if ( this.iconPath != iconPath || this.icon == null ) {
			this.iconPath = iconPath;
			Icon _icon = TaskType.loadIcon( iconPath );
			if ( _icon != null )
				this.icon = _icon;
		}
	} //}}}

	public boolean isActive() {
		return active;
	}

	public void setActive( boolean b ) {
		active = b;
	}

	//{{{ getREFlags() method
	public int getREFlags() {
		return reFlags;
	} //}}}

	//{{{ compileRE() method
	private void compileRE() {
		this.re = null;

		try {
			this.re = Pattern.compile( this.pattern, this.getREFlags() );
		}
		catch ( PatternSyntaxException e ) {
			Log.log( Log.ERROR, TaskType.class,
			        "Failed to compile task pattern: " + pattern +
			        e.toString() );
		}
	} //}}}

	//{{{ save() method
	public void save( int i ) {
		jEdit.setProperty( "tasklist.tasktype." + i + ".name", name );
		jEdit.setProperty( "tasklist.tasktype." + i + ".pattern", pattern );
		jEdit.setProperty( "tasklist.tasktype." + i + ".sample", sample );
		jEdit.setBooleanProperty( "tasklist.tasktype." + i + ".ignorecase", ignoreCase );
		jEdit.setProperty( "tasklist.tasktype." + i + ".iconpath", iconPath );
	} //}}}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;	
		}
		if (this == obj) {
			return true;	// same object	
		}
		if (obj instanceof TaskType) {
			TaskType type = (TaskType)obj;
			return getName().equals(type.getName());
		}
		return false;
	}
	
	public int hashCode() {
		return getName().hashCode();	
	}

	//{{{ toString() method
	public String toString() {
		return this.pattern;
	} //}}}

	//{{{ private members
	private Pattern re;
	private int reFlags;
	private String name;
	private String pattern;
	private String sample;
	private boolean ignoreCase;
	private String iconPath;
	private Icon icon;
	private boolean active;

	private boolean displayIdentifier = true;

	private static Hashtable<String, Icon> icons;
	//}}}

	//{{{ loadIcon() method
	/**
	* Loads an icon for later use
	* @param iconName a file name (start with 'file:' or resource path)
	*/
	public static Icon loadIcon( String iconName ) {
		//Log.log(Log.DEBUG, TaskType.class,
		//	"TaskType.loadIcon(" + iconName + ")");//##

		// check if there is a cached version first
		Icon icon = ( Icon ) icons.get( iconName );
		if ( icon != null )
			return icon;

		// load the icon
		if ( iconName.startsWith( "file:" ) ) {
			icon = new ImageIcon( iconName.substring( 5 ) );
		}
		else {
			URL url = TaskListPlugin.class.getResource( "/icons/" + iconName );
			if ( url == null ) {
				Log.log( Log.ERROR, TaskType.class,
				        "TaskType.loadIcon() - icon not found: " + iconName );
				return null;
			}

			icon = new ImageIcon( url );
		}

		icons.put( iconName, icon );
		return icon;
	} //}}}

	//{{{ static initializer
	static
	{
		icons = new Hashtable<String, Icon>();
		StringTokenizer st = new StringTokenizer( jEdit.getProperty( "tasklist.icons" ) );
		while ( st.hasMoreElements() ) {
			String icon = st.nextToken();
			loadIcon( icon );
		}
	} //}}}

}
