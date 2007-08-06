/*
 * FilterOptions.java - Immutable filter options for JBrowse
 *
 * Copyright (c) 1999-2001 George Latkiewicz, Andre Kaplan
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


package sidekick.java.options;


/**
 * Interface for accessing Filter options for JBrowse
 * @author George Latkiewicz
 * @author Andre Kaplan
 * @version $Id$
**/
public interface FilterOptions
{
    // Filter options (WHAT)
    boolean getShowImports();
    boolean getShowFields();
    boolean getShowVariables();
    boolean getShowPrimitives();
    boolean getShowInitializers();
    boolean getShowGeneralizations();
    boolean getShowThrows();

    int getTopLevelVisIndex();
    int getMemberVisIndex();
}

