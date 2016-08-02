
/*
 * Copyright (c) 2007, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ise.plugin.svn.gui;


import ise.plugin.svn.data.RepositoryData;

import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.jEdit;


/**
 * ComboBox to choose a repository.  Fills itself from jEdit properties like
 * this:
 * propertyPrefix.name.1=repository name (friendly name)
 * propertyPrefix.url.1=repository url
 * propertyPrefix.username.1=username for repository, can be null
 * propertyPrefix.password.1=password for username
 * The value of propertyPrefix.name will be displayed in the combo box dropdown.
 */
public class RepositoryComboBox extends JComboBox <RepositoryData> {

    // default prefix
    private static final String propertyPrefix = "ise.plugins.svn.repository.";
    protected static final String SELECT_NAME = jEdit.getProperty( "ips.select_name", "-- Select --" );
    public static final RepositoryData SELECT = new RepositoryData( SELECT_NAME, null, null, null );
    DefaultComboBoxModel<RepositoryData> dropdownModel = null;    // holds the display names
    // holds the repository info, keyed by name
    HashMap<String, RepositoryData> propertyMap = new HashMap<String, RepositoryData>();


    // load a list of key/hashtable pairs
    public RepositoryComboBox() {
        load( null );
    }


    private void load( RepositoryData selected ) {

        // fetch the property values
        String url = null;
        int i = 0;
        do {

            {

                {
                    String name = jEdit.getProperty( propertyPrefix + "name." + i );
                    url = jEdit.getProperty( propertyPrefix + "url." + i );
                    String username = jEdit.getProperty( propertyPrefix + "username." + i );
                    String password = jEdit.getProperty( propertyPrefix + "password." + i );

                    // must have url at minimum, this also signals the end of the loop
                    if ( url == null ) {
                        break;
                    }


                    // set name to be same as url, this is partly for backward compatibility
                    // since the first release didn't allow naming a repository
                    if ( name == null ) {
                        name = url;
                    }


                    // add to the property lookup table
                    if ( !propertyMap.containsKey( name ) ) {
                        RepositoryData data = new RepositoryData( name, url, username, password );
                        propertyMap.put( name, data );
                    }


                    ++i;
                }
            }
        }


        while ( url != null );

        // sort and fill the combo box model
        List<RepositoryData> values = new ArrayList<RepositoryData>( propertyMap.values() );
        Collections.sort( values, new Comparator<RepositoryData>(){

            public int compare( RepositoryData a, RepositoryData b ) {
                if ( a == null && b == null ) {
                    return 0;
                }


                if ( a == null && b != null ) {
                    return 1;
                }


                if ( a != null && b == null ) {
                    return -1;
                }


                return a.getName().compareToIgnoreCase( b.getName() );
            }
        } );
        dropdownModel = new DefaultComboBoxModel<RepositoryData>( values.toArray( new RepositoryData [0]  ) );

        // add and choose the 'select' choice
        if ( dropdownModel.getSize() > 0 && dropdownModel.getIndexOf( SELECT ) < 0 ) {
            dropdownModel.insertElementAt( SELECT, 0 );
        }


        setModel( dropdownModel );
        if ( dropdownModel.getSize() > 0 ) {
            if ( selected != null ) {
                setSelectedItem( selected.getName() );
            }
            else {
                setSelectedIndex( 0 );
            }
        }
    }


    @Override
    public void setEditable( boolean editable ) {

        // never editable
        super.setEditable( false );
    }


    // item might be a repository url, so lookup the corresponding repository name
    @Override
    public void setSelectedItem( Object item ) {
        if ( item == null ) {
            return;
        }


        String url = item.toString();
        for (String key : propertyMap.keySet()) {
            RepositoryData rd = propertyMap.get(key);
            String comp_url = rd.getURL();
            if ( url.equals( comp_url ) || url.startsWith( comp_url ) || comp_url.startsWith( url ) ) {
                super.setSelectedItem( key );
                return;
            }
        }
        super.setSelectedItem( item );
    }


    public void addRepository( RepositoryData data ) {
        addItem( data );
    }


    /**
     * Adds a value to the combo box.
     * @param value a value to add to the list
     */
    @Override
    public void addItem( RepositoryData value ) {
        if ( value == null || !( value instanceof RepositoryData ) || SELECT_NAME.equals( value.getName() ) ) {
            return;
        }


        RepositoryData data = ( RepositoryData )value;
        if ( data.getURL() == null ) {
            return;
        }


        if ( data.getName() == null ) {
            data.setName( data.getURL() );
        }


        propertyMap.put( data.getName(), data );
        save( data );
    }


    public void removeRepository( RepositoryData value ) {
        if ( value == null || !( value instanceof RepositoryData ) ) {
            return;
        }


        RepositoryData data = ( RepositoryData )value;
        if ( data.getURL() == null ) {
            return;
        }


        if ( data.getName() == null ) {
            data.setName( data.getURL() );
        }


        super.removeItem( data.getName() );
        propertyMap.remove( data.getName() );
        save( null );
    }


    // could return null if SELECT is the current selection
    public RepositoryData getSelectedRepository() {
        RepositoryData rd = ( RepositoryData )super.getSelectedItem();
        return propertyMap.get( rd.getName() );
    }


    /**
     * Saves the current list in the combo box to the jEdit property file.
     */
    public void save( RepositoryData selected ) {
        if ( selected != null ) {
            String name = selected.getName() == null ? selected.getURL() : selected.getName();
            propertyMap.put( name, selected );
        }


        // clear the old property values, this removes the property values from
        // the jEdit property file for all repository data, new values will be
        // written below.
        String url = null;
        int i = 0;
        do {

            {

                {
                    jEdit.unsetProperty( propertyPrefix + "name." + i );
                    jEdit.unsetProperty( propertyPrefix + "url." + i );
                    jEdit.unsetProperty( propertyPrefix + "username." + i );
                    jEdit.unsetProperty( propertyPrefix + "password." + i );
                    ++i;
                }
            }
        }


        while ( url != null );

        // save the new values
        i = 0;
        for ( String key : propertyMap.keySet() ) {
            if ( key != null ) {
                RepositoryData value = propertyMap.get(key);
                if ( value != null ) {
                    if ( value.getURL() == null ) {
                        continue;
                    }


                    if ( value.getName() == null ) {
                        value.setName( value.getURL() );
                    }


                    jEdit.setProperty( propertyPrefix + "name." + i, value.getName() );
                    jEdit.setProperty( propertyPrefix + "url." + i, value.getURL() );
                    if ( value.getUsername() != null ) {
                        jEdit.setProperty( propertyPrefix + "username." + i, value.getUsername() );
                    }


                    if ( value.getPassword() != null ) {
                        jEdit.setProperty( propertyPrefix + "password." + i, value.getPassword() );
                    }
                }
            }


            ++i;
        }
        load( selected );
    }
}
