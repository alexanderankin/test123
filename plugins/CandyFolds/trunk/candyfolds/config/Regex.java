/* % [{
% (C) Copyright 2010 Nicolas Carranza and individual contributors.
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
package candyfolds.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.Segment;

public final class Regex{
	private String value;
	private Pattern pattern;
	private Matcher matcher;

	public synchronized boolean matches(Segment segment) {
		if(matcher==null) {
			if(pattern==null)
				return true;
			else {
				matcher=pattern.matcher(segment);
				//Log.log(Log.NOTICE, this, "matcher setted");
			}
		}else
			matcher.reset(segment);
		//long nanos=System.nanoTime();
		boolean r=matcher.lookingAt();
		//L.fine("regex time="+(System.nanoTime()-nanos)); // find() is slooow!
		return r;
	}

	public void setValue(String value)
	throws PatternSyntaxException {
		if(value!=null) {
			value=value.trim();
			this.pattern=Pattern.compile(value);
		}	else
		this.pattern=null;
		this.value=value;
		matcher=null;
	}

	public String getValue() {
		return value;
	}

}