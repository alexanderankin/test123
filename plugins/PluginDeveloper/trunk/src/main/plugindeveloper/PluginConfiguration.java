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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  eberry
 */
public class PluginConfiguration {

   private static final Pattern CLASS_PATTERN = Pattern.compile("(?>plugin\\.)(.*)(?>\\.name)");
   private PropsKeys keys;
   private String pluginClass;
   private Properties pluginProperties;
   private File propsFile;

   private PluginConfiguration() {
      pluginProperties = new Properties();
   }

   public static PluginConfiguration load(String propsFilePath) throws FileNotFoundException, IOException {
      PluginConfiguration config = new PluginConfiguration();
      config.setPropsFile(new File(propsFilePath));
      config.setPluginProperties(new Properties());
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(config.propsFile);
         config.getPluginProperties()
               .load(fis);
      } finally {
         if (fis != null) {
            fis.close();
         }
      }
      for (Object key : config.pluginProperties.keySet()) {
         Matcher classMatcher = CLASS_PATTERN.matcher(key.toString());
         if (classMatcher.matches()) {
            config.setPluginClass(classMatcher.group(1));
            break;
         }
      }

      config.setKeys(new PropsKeys(config.pluginClass));
      return config;
   }

   public String getAuthor() {
      return getProperty(keys.author(), System.getProperty("user.name"));
   }

   public String getPluginClass() {
      return pluginClass;
   }

   public String getPluginName() {
      return getProperty(keys.name(), "Plugin Name");
   }

   public String getProperty(String key, String defaultValue) {
      return pluginProperties.getProperty(key, defaultValue);
   }

   public void save() throws IOException {
      FileOutputStream fos = null;
      try {
         fos = new FileOutputStream(propsFile);
         pluginProperties.store(fos, "General Plugin information for the " + getPluginName() + " plugin.");
      } finally {
         if (fos != null) {
            fos.close();
         }
      }
   }

   public void setAuthor(String author) {
      setProperty(keys.author(), author);
   }

   public void setPluginName(String pluginName) {
      setProperty(keys.name(), pluginName);
   }

   public void setVersion(String version) {
       setProperty(keys.version(), version);
   }

   public String getVersion() {
       return getProperty(keys.version(), "0.0");
   }

   public void setProperty(String key, String value) {
      pluginProperties.setProperty(key, value);
   }

   private PropsKeys getKeys() {
      return keys;
   }

   private Properties getPluginProperties() {
      return pluginProperties;
   }

   private File getPropsFile() {
      return propsFile;
   }

   private void setKeys(PropsKeys keys) {
      this.keys = keys;
   }

   private void setPluginClass(String pluginClass) {
      this.pluginClass = pluginClass;
   }

   private void setPluginProperties(Properties pluginProperties) {
      this.pluginProperties = pluginProperties;
   }

   private void setPropsFile(File propsFile) {
      this.propsFile = propsFile;
   }
}
// These properties are for jEdit - Programmer's Text Editor.
// Load this file in jEdit to see what they do.
// ::folding=explicit:mode=javas:noTabs=false:collapseFolds=4::
