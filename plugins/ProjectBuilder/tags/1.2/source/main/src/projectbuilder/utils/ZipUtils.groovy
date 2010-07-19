/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectbuilder.utils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import org.gjt.sp.util.Log

/**
 *
 * @author elberry
 */
public class ZipUtils {

   public static void copyStream(InputStream iStream, OutputStream oStream) throws IOException {
      byte[] buffer = new byte[4096]
      int len

      while ((len = iStream.read(buffer)) >= 0) {
      	 Log.log(Log.DEBUG, ZipUtils.class, len)
         oStream.write(buffer, 0, len)
      }

      iStream.close()
      oStream.close()
   }

   public static boolean extract(File src, File dest) throws ZipException, IOException {
      ZipFile zipFile = new ZipFile(src)
      zipFile.entries().each { entry ->
         if (entry.isDirectory()) {
            File dir = new File(dest, entry.name)
            Log.log(Log.DEBUG, ZipUtils.class, "Unzipping directory: ${dir.path}")
            dir.mkdir()
         } else {
            File file = new File(dest, entry.name)
            if(file.createNewFile()) {
               Log.log(Log.DEBUG, ZipUtils.class, "Unzipping file: ${entry.name}")
               copyStream(zipFile.getInputStream(entry), file.newOutputStream())
            }
         }
      }
      return true
   }
}

