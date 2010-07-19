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

package projectbuilder.builder
import groovy.text.GStringTemplateEngine
/**
 *
 * @author eberry
 */
public class FileFactory extends AbstractFactory {

   private append
   private content
   private file
   private template
   private templateData
    
   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
      println("FileFactory - builder: ${builder} | name: ${name} | value: ${value} | attributes: ${attributes} | parent: ${builder.getCurrent()}")
      def parentFile = builder.getCurrent()
      def file = new File(parentFile, value)
      def fileNode = new FileFactory()
      fileNode.file = file
      return fileNode
   }

   void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
      // parent can only be directories.
      if(!parent.exists()) {
         println("FileFactory - creating parent: ${parent.path}")
         parent.mkdirs()
      }
      if(!node.file.exists()) {
         println("FileFactory - creating file: ${node.file.path}")
         node.file.getParentFile().mkdirs()
         node.file.createNewFile()
      }

      // now that the file has been created, let's deal with the templates
      // and content.
      if(node.content != null) {
         node.writeContent()
      } else if (node.template != null) {
         node.writeTemplate()
      }
   }

   boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ) {
      if(attributes["content"] != null && attributes["template"] != null) {
         // user has supplied both content and a template - pick one.
         throw new IllegalStateException("Content and a Template are specified for file: ${node.path} - only one is allowed.")
      }
      return true;
   }

   public boolean isLeaf() {
      return true
   }

   public getPath() {
      return file.path
   }

   private void writeContent() {
      println("writing content: ${content} | file: ${file.path}")
      if(append) {
         file.withWriterAppend { writer ->
            writer.write(content)
         }
      } else {
         file.withWriter { writer ->
            writer.write(content)
         }
      }
   }
   private void writeTemplate() {
      println("writing template: ${template} | file: ${file.path}")
      def templateFile = new File(template)
      def templateUrl = getClass().getResource(template)
      def engine = new GStringTemplateEngine()
      def templateContent = ""
      if(templateFile.exists()) {
         templateContent = engine.createTemplate(templateFile).make(templateData)
      } else if (templateUrl != null) {
         templateContent = engine.createTemplate(templateUrl).make(templateData)
      } else {
         throw new RuntimeException("Couldn't find template file: ${template}")
      }
      if(append) {
         file.withWriterAppend { writer ->
            writer.write(templateContent)
         }
      } else {
         file.withWriter { writer ->
            writer.write(templateContent)
         }
      }
   }
}