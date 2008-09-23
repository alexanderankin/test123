/*
* $Revision$
* $Date$
* $Author$
*
* Copyright (C) 2008 Eric Le Lay
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
*/

package cswilly.jeditPlugins.spell;

import java.util.*;

import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;

/**
 * utility methods to handle BufferSpellChecker "context-sensitive spellchecking".
 */
public class SyntaxHandlingManager{
	public static final String PROFILES_LIST_PROP = "spell-check-syntax-profiles-list";
	public static final String GLOBAL_DISABLE_PROP = "spell-check-syntax-disable";
	
	private static Map<String,Profile> modeToProfile = null;
	private static Profile defaultProfile = new Profile();

	public static class Profile{
		String name;
		byte[] tokenTypesToInclude;
		String[] modes;
		boolean isDefault;
		
		Profile(){
			this("",new byte[0]);
		}

		Profile(String name, byte[] tokenTypesToInclude){
			this.name = name;
			this.tokenTypesToInclude = tokenTypesToInclude;
			this.modes = new String[0];
			this.isDefault=false;
		}
	}
	
	private static void initModeToProfile(Profile[] profiles){
		modeToProfile = new HashMap<String,Profile>();
		for(Profile p:profiles){
			for(String mode:p.modes){
				modeToProfile.put(mode,p);
			}
			if(p.isDefault)defaultProfile = p;
		}
	}
	
	static Profile[] loadProfiles(){
		String profiles = jEdit.getProperty(PROFILES_LIST_PROP);
		String[] pro = profiles.split(",");
		Profile[] ret = new Profile[pro.length];
		for(int i=0;i<pro.length;i++){
			ret[i]=load(pro[i]);
		}
		return ret;
	}
	
	static void saveProfiles(Profile[] profiles){
		//save all profiles
		String profilesList = "";
		if(profiles.length>0){
			for(Profile p:profiles){
				profilesList+=","+p.name;
				save(p);
			}
			profilesList=profilesList.substring(1);
		}
		jEdit.setProperty(PROFILES_LIST_PROP,profilesList);
		initModeToProfile(profiles);
	}
	
	static Profile load(String profileName){
		String prefix = PROFILES_LIST_PROP+"."+profileName;
		//tokens
		String val = jEdit.getProperty(prefix+".tokens","");
		String[] types = val.split(",");
		List<String> allTypes = Arrays.asList(Token.TOKEN_TYPES);
		byte[] toInclude = new byte[types.length];
		for(int j = 0;j<types.length;j++){
			toInclude[j]=(byte)allTypes.indexOf(types[j]);
		}
		//name
		Profile p = new Profile(profileName,toInclude);
		//default
		p.isDefault = jEdit.getBooleanProperty(prefix+".default");
		//modes
		p.modes = jEdit.getProperty(prefix+".modes","").split(",");
		return p;
	}
	
	static void save(Profile p){
		String prefix = PROFILES_LIST_PROP+"."+p.name;
		//modes
		String val = "";
		if(p.modes.length>0){
			for(int i=0;i<p.modes.length;i++){
				val+=","+p.modes[i];
			}
			val=val.substring(1);
		}
		jEdit.setProperty(prefix+".modes",val);
		
		//tokens
		val = "";
		if(p.tokenTypesToInclude.length>0){
			for(int i=0;i<p.tokenTypesToInclude.length;i++){
				val+=","+Token.TOKEN_TYPES[p.tokenTypesToInclude[i]];
			}
			val=val.substring(1);
		}
		jEdit.setProperty(prefix+".tokens",val);
		
		//default
		if(p.isDefault)jEdit.setBooleanProperty(prefix+".default",true);
	}
	
	static void clearProfiles(){
		String[]oldProfiles = jEdit.getProperty(PROFILES_LIST_PROP,"").split(",");
		for(String pro:oldProfiles)clear(pro);
	}
	
	static void clear(String profileName){
		jEdit.unsetProperty(PROFILES_LIST_PROP+"."+profileName+".modes");
		jEdit.unsetProperty(PROFILES_LIST_PROP+"."+profileName+".tokens");
		jEdit.unsetProperty(PROFILES_LIST_PROP+"."+profileName+".default");
	}
	
	//for spell-checker
	public static byte[] getTokensToInclude(String mode){
		if(modeToProfile==null)initModeToProfile(loadProfiles());
		Profile p = modeToProfile.get(mode);
		System.out.println("profile for mode "+mode+":"+((p==null)?"null":p.name));
		if(p==null){
			System.out.println("defaultProfile :"+defaultProfile.name);
			return defaultProfile.tokenTypesToInclude;
		} else return p.tokenTypesToInclude;
	}
	
	public static boolean isSyntaxHandlingDisabled(){
		return jEdit.getBooleanProperty(GLOBAL_DISABLE_PROP);
	}
}
