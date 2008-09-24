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

package cswilly.spell;

import java.util.List;
import java.util.ArrayList;

public class MockEngine implements Engine{
	private int index;
	private List<List<Result>> lres;
	private boolean stopped,ctxs;
	public MockEngine(){
		lres = new ArrayList<List<Result>>();
		index = 0;
		stopped = true;
		ctxs=true;
	}
	
	public void addResults(List<Result> res){
		lres.add(res);
	}
	
	public List<Result> checkLine(String line){
		return new ArrayList<Result>(lres.get(index++));
	}
	
	public void rewind(){
		index = 0;
	}
	
	public void stop(){stopped=true;}
	
	public boolean isStopped(){return stopped;}
	
	public void setContextSensitive(boolean b){
		if(!stopped)throw new IllegalStateException("can't change context-sensitivity when not stopped");
		ctxs = b;
	}
	
	
	public boolean isContextSensitive(){
		return ctxs;
	}
}
