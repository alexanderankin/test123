/*
 * EntityResolverImpl.java - Entity resolver
 *
 * Copyright (c) 2003 Robert McKinnon
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
package xslt;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import xml.CatalogManager;


/**
 * Entity resolver that makes use of {@link CatalogManager}.
 *@author Robert McKinnon
 */
public class EntityResolverImpl implements EntityResolver {
  private String inputPath;


  public EntityResolverImpl(String inputPath) {
    this.inputPath = inputPath;
  }


  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    try {
      return CatalogManager.resolve(inputPath, publicId, systemId);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }
}
