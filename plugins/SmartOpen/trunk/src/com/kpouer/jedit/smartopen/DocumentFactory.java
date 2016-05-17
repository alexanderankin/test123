/*
 * jEdit - Programmer's Text Editor
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2016 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.smartopen;

import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.gjt.sp.jedit.MiscUtilities;

/**
 * @author Matthieu Casanova
 */
class DocumentFactory
{
	public static final String FIELD_PATH = "path";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_NAME_CAPS = "name_caps";
	public static final String FIELD_EXTENSION = "extension";
	public static final String FIELD_FREQUENCY = "frequency";
	public static final String FIELD_FREQUENCY_STORED = "frequency_stored";
	private final NumericDocValuesField frequency;
    private final StoredField frequencyStored;
    private final StringField name_caps;
    private final TextField name;
    private final StringField path;
    private final Document document;
    private final StringField fileExtension;

    DocumentFactory()
    {
        path = new StringField(FIELD_PATH, "", Store.YES);
		name = new TextField(FIELD_NAME, "", Store.NO);
		name_caps = new StringField(FIELD_NAME_CAPS, "", Store.NO);
		fileExtension = new StringField(FIELD_EXTENSION, "", Store.NO);
		frequencyStored = new StoredField(FIELD_FREQUENCY_STORED, 1L);
		frequency = new NumericDocValuesField(FIELD_FREQUENCY, 1L);
		document = new Document();
		document.add(path);
		document.add(name);
		document.add(name_caps);
		document.add(fileExtension);
		document.add(frequency);
		document.add(frequencyStored);
    }

    // createDocument() method
    public Document createDocument(String path, long frequency)
    {
        String fileName = MiscUtilities.getFileName(path);
        this.path.setStringValue(path);
        name.setStringValue(fileName);
        name_caps.setStringValue(fileName);
        String extension = MiscUtilities.getFileExtension(path).toLowerCase();
        if (extension.startsWith("."))
            extension = extension.substring(1);
        fileExtension.setStringValue(extension);
        this.frequency.setLongValue(frequency);
        this.frequencyStored.setLongValue(frequency);
        return document;
    } //}}}
}
