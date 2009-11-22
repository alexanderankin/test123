/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectbuilder.utils
import java.util.zip.ZipFile
import org.gjt.sp.util.Log


/**
 *
 * @author elberry
 */
public class ZipUtils {
   private static void copyStream(InputStream iStream, OutputStream oStream) {
      byte[] buffer = []
      int len

      while((len = iStream.read(buffer)) >= 0) {
         oStream.write(buffer, 0, len)
      }

      iStream.close()
      oStream.close()
   }
   static boolean extract(File src, File dest) {
      ZipFile zipFile = new ZipFile(src);
      zipFile.entries().each { entry ->
         if(entry.isDirectory()) {
            File dir = new File(dest, entry.name)
            println("Unzipping directory: ${dir.path}")
            Log.log(Log.DEBUG, ZipUtils.class, "Unzipping directory: ${dir.path}")
            dir.mkdir()
         } else {
            println("Unzipping file: ${entry.name}")
            Log.log(Log.DEBUG, ZipUtils.class, "Unzipping file: ${entry.name}")
            copyStream(zipFile.getInputStream(entry), new File(entry.name).newOutputStream())
         }
      }
   }
}

