package uk.co.antroy.latextools; 

import gnu.regexp.*;

import java.io.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

 class NavigationList
    implements Comparable {

    //~ Instance/static variables .............................................

    int importance = 0;
    int lowestLevel;
    String title;
    List list = new ArrayList();
    static SortedSet navData = new TreeSet();
    
    //~ Constructors ..........................................................

    NavigationList(String s) {
      this.title = s;
    }

    NavigationList(String s, int imp) {
      this.title = s;
      this.importance = imp;
    }

    //~ Methods ...............................................................

    public int compareTo(NavigationList be) {

      return this.importance - be.importance;
    }

    public int compareTo(Object o) {

      return compareTo((NavigationList) o);
    }

    public boolean equals(Object o) {

      return equals((NavigationList) o);
    }

    public boolean equals(NavigationList n) {

      return this.toString().equals(n.toString());
    }

    public int hashCode() {

      return title.hashCode();
    }

    public String toString() {

      return title;
    }

    void setLowestLevel(int lev) {
      lowestLevel = lev;
    }

    int getLowestLevel() {

      return lowestLevel;
    }

    public boolean add(Object o){
      return list.add(o);
    }
 
     public Iterator iterator(){
       return list.iterator();
     }
     
     public int size(){
       return list.size();
     }
   
    String getTitle() {

      return title;
    }
    
    public static SortedSet getNavigationData() {

    try {

      InputStream istr = NavigationList.class.getResourceAsStream("/default.nav");
      
      if (istr != null){
        Reader reader = new InputStreamReader(istr);
        loadNavigationFile(reader);
      }else{
        Log.log(Log.DEBUG, NavigationList.class,"NavigationList not read");
      }
    } catch (Exception e) {
      Log.log(Log.ERROR, NavigationList.class,"NavigationList: " + e.getMessage());
    }

    File navDir = new File(jEdit.getProperty("options.navigation.userdir"));
    if (navDir.exists()) {

      try {

        File[] files = navDir.listFiles(new FileFilter() {
          public boolean accept(File f) {
            return f.toString().endsWith(".nav");
          }
        });

        for (int i = 0; i < files.length; i++) {
          loadNavigationFile(new FileReader(files[i]));
        }
      } catch (Exception e) {
          Log.log(Log.ERROR, NavigationList.class, e.toString());
          e.printStackTrace();
      }
    }
//    Log.log(Log.DEBUG, NavigationList.class,"NAVDATA");
//    for (Iterator it = navData.iterator(); it.hasNext();){
//        NavigationList nl = (NavigationList) it.next();
//        for (Iterator it2 = nl.iterator(); it2.hasNext();){
//            Log.log(Log.DEBUG, NavigationList.class, nl.toString() + " ITEM: " + it2.next());
//        }
//    }
//
    
    return new TreeSet(navData);
  }
    
    
    private static void loadNavigationFile(Reader reader) {

    try {

      BufferedReader in = new BufferedReader(reader);
      String nextLine = in.readLine().trim();

      while (nextLine != null) {

        int lowestLevel = 0;

        if (nextLine.length() > 1 && nextLine.startsWith("@")) {

          if (nextLine.endsWith("0")) {

            NavigationList nl = new NavigationList(nextLine.substring(1, 
                                                                      nextLine.length() - 1));
            navData.remove(nl);
            nextLine = in.readLine();
          } else {

            int importance = Integer.parseInt(nextLine.substring(
                                                    nextLine.length() - 1));
            NavigationList nl = new NavigationList(nextLine.substring(1, 
                                                                      nextLine.length() - 1), 
                                                   importance);
            navData.remove(nl);
            navData.add(nl);
            nextLine = in.readLine().trim();

            while (nextLine.length() > 3 && nextLine.indexOf(":")>0) {

              StringTokenizer st = new StringTokenizer(nextLine,":");
              if (st.countTokens()!=6) {
                nextLine = in.readLine().trim();
                continue;
              }
              int lev = Integer.parseInt(st.nextToken());
              lowestLevel = Math.max(lowestLevel, lev);
              nl.add(new TagPair(st.nextToken(), st.nextToken(), 
                                 Integer.parseInt(st.nextToken()),  
                                 st.nextToken(), lev, 
                                 Integer.parseInt(st.nextToken())));
              nextLine = in.readLine().trim();
            }

            nl.setLowestLevel(lowestLevel);
            navData.add(nl);
          }
        } else {
          nextLine = in.readLine();
        }
      }
    } catch (Exception e) {
            Log.log(Log.ERROR, NavigationList.class, "NavigationList error" + e);
            e.printStackTrace();
    }

//    for (Iterator it = navData.iterator(); it.hasNext();){
//        NavigationList nl = (NavigationList) it.next();
//        for (Iterator it2 = nl.iterator(); it2.hasNext();){
//            Log.log(Log.DEBUG, NavigationList.class, nl.toString() + " ITEM: " + it2.next());
//        }
//    }
 }

  }
