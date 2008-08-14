package findfile;

/**
 * A set of options to define the file search to be performed.
 * @author Nicholas O'Leary
 * @version $Id: SearchOptions.java,v 1.1.1.1 2003/11/20 17:19:14 olearyni Exp $
 */
public class SearchOptions
{
   public int sortOrder;
   
   /** The root path for the search. */
   public String path;
   
   /** The file filter to apply. */
   public String filter;
   
   /** Whether to recurse into subdirectories. */
   public boolean recursive;
   
   /** Whether to open the files automatically. */
   public boolean openResults;
   
   private int resultCount = 0;
   
   public void setResultCount(int i)
   {
      resultCount = i;
   }
   
   public String toString()
   {
      return filter +
             " in " +
             path +
             " (" + 
             resultCount + 
             " result" + 
             ((resultCount!=1)?"s":"") + 
             ")";
   }
}