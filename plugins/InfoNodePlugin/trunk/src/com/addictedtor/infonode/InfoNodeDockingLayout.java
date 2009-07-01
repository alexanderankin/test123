/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the InfoNodePlugin plugin for jedit
 *
 * The InfoNodePlugin plugin for jedit is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The InfoNodePlugin plugin for jedit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the InfoNodePlugin plugin for jedit. If not, see <http://www.gnu.org/licenses/>.
 */
package com.addictedtor.infonode;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

import com.addictedtor.infonode.perspective.Perspective;

public class InfoNodeDockingLayout extends DockingLayout {

	@SuppressWarnings("unused")
	private InfoNodeDockingWindowManager wm; 
	
	private Perspective perspective ; 
	
	private File file ; 
	
	public InfoNodeDockingLayout( ) {
		this( null ) ; 
	}
	
	public InfoNodeDockingLayout( InfoNodeDockingWindowManager wm ) {
		this.wm = wm; 
		file = null; 
	}
	
	@Override
	public boolean loadLayout(String baseName, int viewIndex) {
		try{
			String layout = getLayoutName(baseName, viewIndex) ; 
			File file_ = new File( getLayoutDir(), layout) ;
			if( ! file_.exists() ){
				PerspectiveTools.createDefaultPerspective( file_ ) ;
			}
			file = file_; 
			return true; 
		} catch( Exception e){
			return false; 
		}
	}
	
	public void apply(InfoNodeDockingWindowManager wm ){
		this.wm = wm; 
		perspective = new Perspective( wm ); 
		if( file != null && file.exists() ){
			perspective.load(file);
		}
	}

	@Override
	public boolean saveLayout(String baseName, int viewIndex) {
		try {
			perspective.save( perspective.getPerspective( getLayoutName(baseName, viewIndex)) );
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "InfoNodePlugin";
	}

	@Override
	public String[] getSavedLayouts() {
		File dir = getLayoutDir() ;
		if( !dir.exists() ){
			boolean result = dir.mkdir();
			if( !result ){
				JOptionPane.showMessageDialog( jEdit.getActiveView(), "could not create the directory + " +  dir.getAbsolutePath() ) ;
			}
		}
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		if( files == null){
			return null; 
		}
		String[] layouts = new String[files.length];
		for (int i = 0; i < files.length; i++)
			layouts[i] = files[i].getName();
		return layouts;
	}

	public static File getLayoutDir() {
		return EditPlugin.getPluginHome(InfoNodePlugin.class) ;
	}

	@Override
	public String getLayoutFilename(String baseName, int viewIndex) {
		File layout = new File( getLayoutDir(), "baseName" + "-" + viewIndex + "xml" ) ;
		return layout.getAbsolutePath() ;
	}
	
	private static String getLayoutName(String basename, int viewIndex){
		String out = basename ;
		if( viewIndex >= 0){
			out = out + "-" + viewIndex + ".xml" ;
		}
		return out; 
	}

}
