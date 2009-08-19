/*
 *  Copyright (c) 2009, Eric Berry <elberry@gmail.com>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  	* Redistributions of source code must retain the above copyright notice, this
 *  	  list of conditions and the following disclaimer.
 *  	* Redistributions in binary form must reproduce the above copyright notice,
 *  	  this list of conditions and the following disclaimer in the documentation
 *  	  and/or other materials provided with the distribution.
 *  	* Neither the name of the Organization (TellurianRing.com) nor the names of
 *  	  its contributors may be used to endorse or promote products derived from
 *  	  this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package plugindeveloper;

class PropsKeys {

   private String activate = "plugin.%s.activate";
   private String author = "plugin.%s.author";
   private String depends = "plugin.%s.depends.%s";
   private String description = "plugin.%s.description";
   private String docs = "plugin.%s.docs";
   private String files = "plugin.%s.files";
   private String jars = "plugin.%s.jars";
   private String longDescription = "plugin.%s.longdescription";
   private String name = "plugin.%s.name";
   private String usePluginHome = "plugin.%s.usePluginHome";
   private String version = "plugin.%s.version";

   public PropsKeys(String pluginClass) {
      activate = String.format(activate, pluginClass);
      author = String.format(author, pluginClass);
      depends = String.format(depends, pluginClass, "%s");
      description = String.format(description, pluginClass);
      docs = String.format(docs, pluginClass);
      files = String.format(files, pluginClass);
      jars = String.format(jars, pluginClass);
      longDescription = String.format(longDescription, pluginClass);
      name = String.format(name, pluginClass);
      usePluginHome = String.format(usePluginHome, pluginClass);
      version = String.format(version, pluginClass);
   }

   String activate() {
      return activate;
   }

   String author() {
      return author;
   }

   String depends(int index) {
      return String.format(depends, index);
   }

   String description() {
      return description;
   }

   String docs() {
      return docs;
   }

   String files() {
      return files;
   }

   String jars() {
      return jars;
   }

   String longDescription() {
      return longDescription;
   }

   String name() {
      return name;
   }

   String usePluginHome() {
      return usePluginHome;
   }

   String version() {
      return version;
   }
}
// These properties are for jEdit - Programmer's Text Editor.
// Load this file in jEdit to see what they do.
// ::folding=explicit:mode=javas:noTabs=false:collapseFolds=4::
