/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.data;

/**
 * Data object for information about a repository: a name for the repository,
 * url for the repository, and username/password to connect to the repository.
 */
public class RepositoryData extends CheckoutData {
    private String name = "";

    public RepositoryData() {}

    public RepositoryData( String name, String url,
            String username,
            String password ) {
        super();
        this.name = name;
        setURL( url );
        setUsername( username );
        setPassword( password );
    }

    public RepositoryData(RepositoryData data) {
        super();
        if (data != null) {
            name = data.getName() == null ? "" : data.getName();
            setURL(new String(data.getURL()));
            setUsername(data.getUsername() == null ? null : new String(data.getUsername()));
            setPassword(data.getPassword() == null ? null : new String(data.getPassword()));
        }
    }

    public String toString() {
        return "RepositoryData[name=" + getName() + ", url=" + getURL() + ", username=" + getUsername() + ", password=" + getPassword() + "]";
    }

    /**
     * Returns the value of the name of the repository.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of name.
     * @param name a name for this repository.
     */
    public void setName( String name ) {
        this.name = name;
    }

}
