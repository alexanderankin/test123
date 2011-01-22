package xml.translate;
/* 
	extracted from http://jing-trang.googlecode.com/svn/tags/V20091111/mod/resolver/src/main/com/thaiopensource/resolver/xml/sax/SAX.java
	all changes are marked with ELL comments
	
	Copyright (c) 2001-2003 Thai Open Source Software Center Ltd
	All rights reserved.

	SAX was provided under these conditions :

		Redistribution and use in source and binary forms, with or without
		modification, are permitted provided that the following conditions are
		met:
		
			Redistributions of source code must retain the above copyright
			notice, this list of conditions and the following disclaimer.
		
			Redistributions in binary form must reproduce the above copyright
			notice, this list of conditions and the following disclaimer in
			the documentation and/or other materials provided with the
			distribution.
		
			Neither the name of the Thai Open Source Software Center Ltd nor
			the names of its contributors may be used to endorse or promote
			products derived from this software without specific prior written
			permission.
		
		THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
		"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
		LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
		A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
		CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
		EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
		PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
		PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
		LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
		NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
		SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	__________ END CONDITIONS ___________
	
	modifications Copyright (c) 2010 Eric Le Lay
	under the same license terms

 */
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.AbstractResolver;
import com.thaiopensource.resolver.BasicResolver;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.xml.ExternalIdentifier;
import com.thaiopensource.resolver.xml.ExternalEntityIdentifier;
import com.thaiopensource.resolver.xml.ExternalDTDSubsetIdentifier;
import com.thaiopensource.resolver.xml.sax.SAX;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import java.io.IOException;


public final class EntityResolverWrapper extends AbstractResolver {
    private final EntityResolver entityResolver;
    private final EntityResolver2 entityResolver2;
    private final boolean promiscuous;
    
    public EntityResolverWrapper(EntityResolver entityResolver, boolean promiscuous) {
    	this.entityResolver = entityResolver;
    	if (entityResolver instanceof EntityResolver2)
    		entityResolver2 = (EntityResolver2)entityResolver;
    	else
    		entityResolver2 = null;
    	this.promiscuous = promiscuous;
    }
    
    public void resolve(Identifier id, Input input) throws IOException, ResolverException {
    	if (input.isResolved())
    		return;
    	String publicId;
    	String entityName = null;
    	if (id instanceof ExternalIdentifier) {
    		publicId = ((ExternalIdentifier)id).getPublicId();
    		if (id instanceof ExternalEntityIdentifier)
    			entityName = ((ExternalEntityIdentifier)id).getEntityName();
    		else if (id instanceof ExternalDTDSubsetIdentifier)
    			entityName = "[dtd]";
    	}
    	else {
    		if (!promiscuous)
    			return;
    		publicId = null;
    	}
    	try {
    		InputSource inputSource;
    		if (entityName != null && entityResolver2 != null)
    		inputSource = entityResolver2.resolveEntity(entityName,
    			publicId,
    			id.getBase(),
    			id.getUriReference());
    		else
    			inputSource = entityResolver.resolveEntity(publicId, getSystemId(id));
    		if (inputSource != null)
    			SAX.setInput(input, inputSource);
    	}
    	catch (SAXException e) {
    		throw SAX.toResolverException(e);
    	}
    }
    
    
    static String getSystemId(Identifier id) {
    	try {
    		return BasicResolver.resolveUri(id);
    	}
    	catch (ResolverException e) { }
    	return id.getUriReference();
    }
    
    // ELL: adding an implementation of open()
    public void open(Input input) throws IOException, ResolverException {
    	if(input.isUriDefinitive() && !input.isOpen()){
    		InputSource inputSource;
    		try {
				if (entityResolver2 != null) {
					inputSource = entityResolver2.resolveEntity(null, //entity name
						null,  // public Id
						null,  // base uri
						input.getUri());
				} else {
					inputSource = entityResolver.resolveEntity(null, // public Id
						input.getUri());
				}
			}
			catch (SAXException e) {
				throw SAX.toResolverException(e);
			}
    	
    		if (inputSource != null) SAX.setInput(input, inputSource);
    	}
    }
    // ELL: end changes
}

