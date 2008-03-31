/* % [{
% (C) Copyright 2008 Nicolas Carranza and individual contributors.
% See the CandyFolds-copyright.txt file in the CandyFolds distribution for a full
% listing of individual contributors.
%
% This file is part of CandyFolds.
%
% CandyFolds is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License,
% or (at your option) any later version.
%
% CandyFolds is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with CandyFolds.  If not, see <http://www.gnu.org/licenses/>.
% }] */
package candyfolds;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import candyfolds.config.Config;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

public class CandyFoldsPlugin
	extends EBPlugin {
	private static CandyFoldsPlugin INSTANCE;

	public static CandyFoldsPlugin getInstance() {
		return INSTANCE;
	}

	boolean stoped=true;
	private final Map<EditPane, TextAreaExt> editPaneToExt=new HashMap<EditPane, TextAreaExt>();
	private Config config;

	public CandyFoldsPlugin() {
		config=new Config();
	}

	Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config=config;
		for(TextAreaExt textAreaExt: editPaneToExt.values())
			textAreaExt.clearModeConfig();
	}

	@Override
	public void start() {
		if(!stoped) {
			//Log.log(Log.NOTICE, this, "already started");
			stop();
			return;
		}
		//Log.log(Log.NOTICE, this, "starting");
		INSTANCE=this;
		for(View view: jEdit.getViews())
			for(EditPane editPane: view.getEditPanes())
				addTextAreaExt(editPane);
		stoped=false;
	}

	private void addTextAreaExt(EditPane
	    editPane) {
		if(editPaneToExt.containsKey(editPane))
			return;
		TextAreaExt textAreaExt=new TextAreaExt(this, editPane);
		editPaneToExt.put(editPane, textAreaExt);
		//Log.log(Log.NOTICE, this, "textAreaExt added");
	}

	private void removeTextAreaExt(EditPane editPane) {
		TextAreaExt textAreaExt=editPaneToExt.get(editPane);
		if(editPane==null)
			return;
		textAreaExt.remove();
		editPaneToExt.remove(editPane);
		Log.log(Log.NOTICE, this, "textAreaExt removed");
	}

	private void removeAllTextAreaExts() {
		for(TextAreaExt textAreaExt: editPaneToExt.values())
			textAreaExt.remove();
		editPaneToExt.clear();
		//Log.log(Log.NOTICE, this, "all textAreaExts removed");
	}

	@Override
	public void stop() {
		if(stoped) {
			//Log.log(Log.NOTICE, this, "already stoped");
			return;
		}
		//Log.log(Log.NOTICE, this, "stoping");
		removeAllTextAreaExts();
		stoped=true;
	}

	@Override
	public void handleMessage( EBMessage m) {
		if(m instanceof EditPaneUpdate) {
			EditPaneUpdate editPaneUpdate=(EditPaneUpdate)m;
			Object updateType=editPaneUpdate.getWhat();
			//String logM=null;
			if(EditPaneUpdate.DESTROYED==updateType) {
				//logM="editPane was destroyed";
				removeTextAreaExt(editPaneUpdate.getEditPane());
			} else if(EditPaneUpdate.CREATED==updateType) {
				//logM="editPane was created";
				addTextAreaExt(editPaneUpdate.getEditPane());
			}
			//if(logM!=null)
			//Log.log(Log.NOTICE, this, logM+": "+editPaneUpdate.getEditPane());
		}
	}



}
