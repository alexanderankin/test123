/*
 *  CommonControlsPlugin.java
 *  Copyright (C) 2000, 2001 Sergey V. Udaltsov
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import org.gjt.sp.jedit.*;

import common.threads.WorkerThreadPool;

/**
 *Description of the Class
 *
 * @author     svu
 * @created    2001
 */
public class CommonControlsPlugin extends EditPlugin
{

	public void stop()
	{
		WorkerThreadPool.getSharedInstance().shutdown();
	}

}

