/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
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

package cswilly.jeditPlugins.spell.hunspellbridge;

// TODO: precise imports
import java.io.*;
import java.net.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import java.util.regex.Pattern;
import java.text.DateFormat;

import java.util.zip.*;
import java.util.Enumeration;
import java.util.Collections;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.ProgressObserver;

import cswilly.jeditPlugins.spell.SpellCheckPlugin;
import cswilly.spell.Dictionary;
import org.gjt.sp.jedit.GUIUtilities;

// TODO : use md5 sums to verify downloads
public class HunspellDictsManager{
	// TODO: in property
	public static final String OOO_DICTS = "http://ftp.services.openoffice.org/pub/OpenOffice.org/contrib/dictionaries/";
	private static final String INSTALLED_DICTS_PROP = "spell-check-hunspell-dicts";
	private static final Pattern VIRGULE_SPLIT = Pattern.compile(",");
	
	private List<Dictionary> availables;
	private List<Dictionary> installed;
	
	public HunspellDictsManager(){
		availables=null;
		installed=null;
	}
	
	public List<Dictionary> getAvailables(ProgressObserver po){
		if(availables == null){
			fetchAvailable(po);
		}
		return availables;
	}
	
	public List<Dictionary> getInstalled(){
		if(installed == null){
			initInstalled();
		}
		
		return Collections.unmodifiableList(installed);
	}

	private void initInstalled(){
		String[] installedL = VIRGULE_SPLIT.split(jEdit.getProperty(INSTALLED_DICTS_PROP,""));

		if(installed==null){
			installed = new ArrayList<Dictionary>(installedL.length);
		}else installed.clear();
			
		for(int i=0;i<installedL.length;i++){
			Dictionary d = createInstalledDict(installedL[i]);
			if(d!=null)installed.add(d);
			else Log.log(Log.ERROR,HunspellDictsManager.class,"Unable to init dict "+installedL[i]);
		}
	}

	Dictionary getInstalledDict(String name){
		if(installed==null){
			initInstalled();
		}

		for(Dictionary d:installed){
			if(d.getKey().equals(name))return d;
		}
		
		//second chance : no variant specified...
		for(Dictionary d:installed){
			if((d.lang+"_"+d.country).equals(name))return d;
		}
		
		return null;
	}
	
	private Dictionary createInstalledDict(String name){
		File home = SpellCheckPlugin.getHomeDir(null);
		
		//parse line
		String line = jEdit.getProperty(INSTALLED_DICTS_PROP+"."+name+".line","");
		
		assert(!"".equals(line));// TODO: handle this
		
		Dictionary dict = null;
		try{
			dict = parseLine(line);
		}catch(IllegalArgumentException iae){
			GUIUtilities.error(null,"spell-check-hunspell-error-installed-dict",new String[]{name,iae.getMessage()});
		}
		if(dict == null){
			return null;
		}
		assert(name.equals(dict.getKey()));
		
		//really installed (user didn't remove it or whatever)
		File d = new File(home,dict.archiveName);
		
		if(!d.exists()){
			Log.log(Log.ERROR,HunspellDictsManager.class,"directory "+d.getPath()+" doesn't exist");
			GUIUtilities.error(null,"spell-check-hunspell-error-installed-dict",new String[]{dict.getDescription(),d.getPath()+" doesn't exist"});
			return null;
		}
		if(!d.isDirectory()){
			Log.log(Log.ERROR,HunspellDictsManager.class,"file "+d.getPath()+" should be a directory");
			GUIUtilities.error(null,"spell-check-hunspell-error-installed-dict",new String[]{dict.getDescription(),d.getPath()+" should be a directory"});
			return null;
		}
		
		File f = new File(d,dict.variant+".dic");
		if(!f.exists()){
			Log.log(Log.ERROR,HunspellDictsManager.class,"file "+f.getPath()+" is missing");
			GUIUtilities.error(null,"spell-check-hunspell-error-installed-dict",new String[]{dict.getDescription(),f.getPath()+" is missing"});
			return null;
		}
		
		f = new File(d,dict.variant+".aff");
		if(!f.exists()){
			Log.log(Log.ERROR,HunspellDictsManager.class,"file "+f.getPath()+" is missing");
			GUIUtilities.error(null,"spell-check-hunspell-error-installed-dict",new String[]{dict.getDescription(),f.getPath()+" is missing"});
			return null;
		}
		
		//init other props
		dict.installed = true;
		
		String date = jEdit.getProperty(INSTALLED_DICTS_PROP+"."+name+".date","");
		try{
			long dat = Long.parseLong(date);
			dict.installedDate = new Date(dat);
		}catch(NumberFormatException nfe){
			Log.log(Log.ERROR,HunspellDictsManager.class,"invalid date property for "+name+":"+date);
			dict.lastModified = new Date(0L);
		}
		
		return dict;

	}
	
	private void fetchAvailable(ProgressObserver po){
		if(availables==null) availables = new ArrayList<Dictionary>();
		else availables.clear();
		if(installed==null)initInstalled();
		try{
			URL available_url = new URL(OOO_DICTS+"available.lst");
			URLConnection connect = available_url.openConnection();
			connect.connect();
			InputStream is = connect.getInputStream();
			
			po.setMaximum(connect.getContentLength());
			
			//copy to file
			File f = File.createTempFile("available","lst");
			OutputStream os = new FileOutputStream(f);
			
			boolean copied = IOUtilities.copyStream(
				po,
				is,
				os,
				true
				);
			if(!copied){
				Log.log(Log.ERROR,HunspellDictsManager.class,"Unable to download "+available_url.toString());
				GUIUtilities.error(null,"spell-check-hunspell-error-fetch",new String[]{"Unable to download file "+available_url.toString()});
				availables = null;
				return;
			}
			
			IOUtilities.closeQuietly(os);
			//read file
			String enc = connect.getContentEncoding();
			//System.out.println(connect.getHeaderFields());
			FileInputStream fis = new FileInputStream(f);
			Reader r;
			if(enc!=null){
				try{
					r = new InputStreamReader(fis,enc);
				}catch(UnsupportedEncodingException uee){
					r = new InputStreamReader(fis,"UTF-8");
				}
			}else{
				r = new InputStreamReader(fis,"UTF-8");
			}
			BufferedReader br = new BufferedReader(r);
			
			for(String line = br.readLine();line!=null;line=br.readLine()){
				Dictionary d = parseLine(line);
				if(d!=null){
					int ind = installed.indexOf(d);
					if(ind==-1){
						//not installed
						d.installed = false;
						availables.add(d);
					}else{
						Dictionary id = installed.get(ind);
						Date lmd = fetchLastModifiedDate(id.archiveName);
						if(lmd!=null){
							id.lastModified = lmd;
						}
					}
				}
			}
			
			IOUtilities.closeQuietly(fis);
		}catch(IOException ioe){
			if(ioe instanceof UnknownHostException){
				GUIUtilities.error(null,"spell-check-hunspell-error-unknownhost",new String[]{ioe.getMessage()});
			}else{
				GUIUtilities.error(null,"spell-check-hunspell-error-fetch",new String[]{ioe.getMessage()});
			}
			ioe.printStackTrace();
		}
	}
	
	private Date fetchLastModifiedDate(String archName){
		Date modifdate = null;
		URL url = null;
		try{
			url = new URL(OOO_DICTS+archName+".zip");
		}catch(MalformedURLException mfue){
			Log.log(Log.ERROR,HunspellDictsManager.class,"Invalid archive name : "+archName);
			GUIUtilities.error(null,"spell-check-hunspell-error-fetch",new String[]{mfue.getMessage()});
		}
		
		if(url!=null){
			try{
				URLConnection connect = url.openConnection();
				connect.connect();
				if(connect.getLastModified()==0){
					Log.log(Log.ERROR,HunspellDictsManager.class,"no lastModifiedDate for "+archName);
				}else{
					modifdate = new Date(connect.getLastModified());
					System.out.println("Modif date :"+DateFormat.getInstance().format(modifdate));
					return modifdate;
				}
			}catch(IOException ioe){
				GUIUtilities.error(null,"spell-check-hunspell-error-fetch",new String[]{ioe.getMessage()});
				ioe.printStackTrace();
			}
		}
		return modifdate;
	}
	
	private Dictionary parseLine(String line){
		String[] fields = VIRGULE_SPLIT.split(line);
		if(fields.length==5){
			Dictionary a = new Dictionary();
			a.lang = fields[0];
			a.country = fields[1];
			a.variant = fields[2];
			a.descr = fields[3];
			//remove .zip
			if(fields[4].endsWith(".zip")){
				a.archiveName = fields[4].substring(0,fields[4].length()-4);
			}else{
				Log.log(Log.ERROR,HunspellDictsManager.class,"Invalid archive name (not ending with .zip): '"+fields[4]+"'");
				throw new IllegalArgumentException("Invalid dictionary description");
			}
			return a;
		}else{
			Log.log(Log.ERROR,HunspellDictsManager.class,"Invalid dictionary description : {"+line+ "} ("+fields.length+")");
			throw new IllegalArgumentException("Invalid dictionary description");
		}
	}

	public boolean install(Dictionary d, ProgressObserver po){
		if(d.installed){
			po.setMaximum(100);
			po.setValue(100);
			return true;
		}
		return doInstallDict(d,po);
	}
	
	public boolean update(Dictionary d, ProgressObserver po){
		if(!d.installed || !d.isOutdated()){
			po.setMaximum(100);
			po.setValue(100);
			return true;
		}
		List<Dictionary> sharing = getSharingDicts(d);
		if(sharing.size()>1){
			boolean done = doUpdateDict(d,po);
			if(!done){
				for(Dictionary od:sharing){
					if(od==d)continue;
					if(!d.installed){//we don't know if it succeeded...
						od.installed = false;
						updateInstalledProp(od);
					}
				}
			}else{
				for(Dictionary od:sharing){
					od.installedDate = d.installedDate;
				}
			}
			return done;
		} else return doUpdateDict(d,po);
	}

	public boolean remove(Dictionary d){
		if(!d.installed)return true;
		List<Dictionary> sharing = getSharingDicts(d);
		//if one dictionary alone, sharing.size()==1 and we can delete files
		return doRemoveDict(d,sharing.size()==1);
	}

	private List<Dictionary> getSharingDicts(Dictionary d){
		if(installed==null){
			throw new IllegalStateException("Installed list should be initialised");
		}
		List<Dictionary> sharing = new ArrayList<Dictionary>(5);
		for(Dictionary od:installed){
			if(od.installed && d.getDirectory().equals(od.getDirectory())){
				sharing.add(od);
			}
		}
		return sharing;
	}
	
	private boolean doRemoveDict(Dictionary d, boolean removeFiles){
		File home = SpellCheckPlugin.getHomeDir(null);
		
		String dir = d.getDirectory();
		
		assert(dir!=null && !"".equals(dir));
		
		//files
		File installdir = new File(home,dir);
		
		if(!installdir.exists()){
			Log.log(Log.ERROR,HunspellDictsManager.class,d.getKey()+" is sayed to be installed but can't find it!");
		}else{
			//remove dir
			if(removeFiles){
				boolean deleted = false;
				try{
				deleted = deleteFileTree(installdir);
				}catch(IOException ioe){
					Log.log(Log.ERROR,HunspellDictsManager.class,"exception while removing "+d.getKey());
					Log.log(Log.ERROR,HunspellDictsManager.class,ioe);
				}
				assert(deleted);
			}else{
				Log.log(Log.MESSAGE,HunspellDictsManager.class,"not deleting files for "+d.getDirectory());
			}
		}

		//dictionary
		d.installed = false;

		updateInstalledProp(d);
		
		return true;
		
	}
	
	private boolean doUpdateDict(Dictionary d,ProgressObserver po){
		File home = SpellCheckPlugin.getHomeDir(null);
		
		String dir = d.getDirectory();
		
		assert(dir!=null && !"".equals(dir));
		
		//files
		File installdir = new File(home,dir);
		
		if(!installdir.exists()){
			Log.log(Log.ERROR,HunspellDictsManager.class,d.getKey()+" is sayed to be installed but can't find it!");
			
			d.installed = false;
			updateInstalledProp(d);
			return false;
		}else{
			//move dir
			File newinstall = new File(home,d.getDirectory()+"."+System.currentTimeMillis());
			for(int i=0;newinstall.exists();i++){
				newinstall = new File(home,newinstall.getName()+i);
			}
			if(!installdir.renameTo(newinstall)){
				Log.log(Log.ERROR,HunspellDictsManager.class,"exception while moving "+d.getKey()+" to "+newinstall.getPath());
				GUIUtilities.error(null,"spell-check-update-move.error",new String[]{d.getKey(),newinstall.getPath(),"rename failed"});
				return doRemoveDict(d,true) && doInstallDict(d,po);
			}
			
			//dictionary
			d.installed = false;
			
			updateInstalledProp(d);

			boolean inst = doInstallDict(d,po);
			if(!inst){
				try{
					if(deleteFileTree(installdir)){
						if(newinstall.renameTo(installdir)){
							//dictionary
							d.installed = true;
							updateInstalledProp(d);
						}else{
							Log.log(Log.ERROR,HunspellDictsManager.class,"unable to restore "+installdir.getPath());
							GUIUtilities.error(null,"spell-check-update-emergency.error",new String[]{d.getKey(),"restore failed"});
						}
					}else{
						Log.log(Log.ERROR,HunspellDictsManager.class,"unable to delete "+installdir.getPath());
						GUIUtilities.error(null,"spell-check-update-emergency.error",new String[]{d.getKey(),"cleaning failed"});
					}
				}catch(IOException ioe){
						Log.log(Log.ERROR,HunspellDictsManager.class,"unable to delete/rename "+newinstall.getPath()+" <- "+installdir.getPath());
						Log.log(Log.ERROR,HunspellDictsManager.class,ioe);
						GUIUtilities.error(null,"spell-check-update-emergency.error",new String[]{d.getKey(),ioe.getMessage()});
				}finally{
					updateInstalledProp(d);
				}
			}
			return inst;
		}
	}

	/**
	 * @return true if everything was deleted...
	 */
	private static boolean deleteFileTree(File root) throws IOException{
		if(!root.exists())return true;
		
		boolean deleted = true;
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(int i=0;i<files.length;i++){
				deleted |= deleteFileTree(files[i]);
			}
		}
		deleted |= root.delete();
		return deleted;
	}
	
	private boolean doInstallDict(Dictionary d, ProgressObserver po){
		File home = SpellCheckPlugin.getHomeDir(null);
		
		String dir = d.getDirectory();
		
		if(dir==null || "".equals(dir)){
			throw new IllegalArgumentException("Invalid dictionary description for "+d);
		}
		
		
		File extractdir = new File(home,dir);
		
		if(!extractdir.exists())extractdir.mkdir();
		if(!extractdir.isDirectory()){
			try{
			System.out.println("File "+extractdir.getCanonicalPath()+" exists");
			}catch(IOException ieo){}
			return false;
		}else{
			try{
			Log.log(Log.DEBUG,HunspellDictsManager.class,"Expanding to "+extractdir.getCanonicalPath());
			}catch(IOException ieo){}
		}
		

		File f = null;
		try{
			f = File.createTempFile(d.archiveName,"tmp");
		}catch(IOException ioe){
			System.out.println("Unable to create temp file :"+ioe);
			return false;
		}

		Date modifdate = null;

		po.setStatus(jEdit.getProperty("spell-check-hunspell-download"));
		try{
			URL url = new URL(OOO_DICTS+d.archiveName+".zip");
			URLConnection connect = url.openConnection();
			connect.connect();
			
			po.setMaximum(connect.getContentLength());
			po.setValue(0);
			//get last modified date
			modifdate = new Date(connect.getLastModified());

			InputStream is = connect.getInputStream();
			
			//copy to file
			OutputStream os = new FileOutputStream(f);
			
			boolean copied = IOUtilities.copyStream(po,
				is,
				os,
				true
				);

			IOUtilities.closeQuietly(os);

			if(!copied){
				po.setStatus(jEdit.getProperty("spell-check-hunspell-error"));
				return false;
			}
			
			
		}catch(IOException ioe){
			Log.log(Log.ERROR,HunspellDictsManager.class,"Download of "+d.getKey()+" failed");
			ioe.printStackTrace();
			GUIUtilities.error(null,"spell-check-hunspell-download.error",new String[]{ioe.getMessage()});
			return false;
		}
		
		Log.log(Log.DEBUG,HunspellDictsManager.class,"Download of "+d.archiveName+".zip finished");
		po.setStatus("spell-check-hunspell-install");
		po.setMaximum(3);
		po.setValue(1);
		//read file
		ZipFile zip = null;
		
		try{
			zip = new ZipFile(f);
		}catch(ZipException ze){
			Log.log(Log.ERROR,HunspellDictsManager.class,"Corrupted archive for "+d.getKey());
			GUIUtilities.error(null,"spell-check-hunspell-download.error",new String[]{ze.getMessage()});
			return false;
		}catch(IOException ioe){
			Log.log(Log.ERROR,HunspellDictsManager.class,"Corrupted archive for "+d.getKey());
			ioe.printStackTrace();
			GUIUtilities.error(null,"spell-check-hunspell-download.error",new String[]{ioe.getMessage()});
			return false;
		}
		
		Log.log(Log.DEBUG,HunspellDictsManager.class,"Downloaded archive seems OK");
		// TODO: md5sum
		po.setMaximum(zip.size()+1);
		int i=1;
		for(Enumeration<? extends ZipEntry> entries = zip.entries();entries.hasMoreElements();i++){
			ZipEntry ze = entries.nextElement();
			Log.log(Log.DEBUG,HunspellDictsManager.class,"Expanding : "+ze);
			po.setValue(i);
			assert(!ze.isDirectory());//quasi-certain that it's never a directory

			String fn = ze.getName();
			File outFile = new File(extractdir,fn);
			if(outFile.exists()){
				if(!outFile.delete()){
					Log.log(Log.ERROR,HunspellDictsManager.class,"unable to delete "+outFile);
					GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),"File "+outFile+" is in the way"});
					return false;
				}
			}
			try{
			if(!outFile.createNewFile()){
					Log.log(Log.ERROR,HunspellDictsManager.class,"unable to create "+outFile);
					GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),"unable to create file "+outFile});
					return false;
			}}catch(IOException ioe){
					Log.log(Log.ERROR,HunspellDictsManager.class,"unable to create "+outFile);
					Log.log(Log.ERROR,HunspellDictsManager.class,ioe);
					GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),ioe.getMessage()});
					return false;
			}
			OutputStream os = null;
			try{
			 os = new FileOutputStream(outFile);
			}catch(FileNotFoundException fnfe){
				Log.log(Log.ERROR,HunspellDictsManager.class,"file has disapeared :"+outFile);
				Log.log(Log.ERROR,HunspellDictsManager.class,fnfe);
				GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),fnfe.getMessage()});
				return false;
			}
			InputStream is = null;
			
			try{
				is = zip.getInputStream(ze);
			}catch(IOException ioe){
				Log.log(Log.ERROR,HunspellDictsManager.class,"unable to read archive entry "+ze);
				Log.log(Log.ERROR,HunspellDictsManager.class,ioe);
				GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),ioe.getMessage()});
				return false;
			}
			
			boolean copied = false;
			
			try{
				copied = IOUtilities.copyStream(
				null,
				is,
				os,
				true
				);
			}catch(IOException ioe){
				Log.log(Log.ERROR,HunspellDictsManager.class,"unable to expand archive entry "+ze);
				Log.log(Log.ERROR,HunspellDictsManager.class,ioe);
				GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),ioe.getMessage()});
				return false;
			}
			if(!copied){
				Log.log(Log.ERROR,HunspellDictsManager.class,"unable to expand archive entry "+ze);
				GUIUtilities.error(null,"spell-check-hunspell-install.error",new String[]{d.getKey(),"copy error"});
				return false;
			}
			IOUtilities.closeQuietly(is);
			IOUtilities.closeQuietly(os);
			
			Log.log(Log.DEBUG,HunspellDictsManager.class,"Done with expanding "+ze.getName());
		}
		
		Log.log(Log.DEBUG,HunspellDictsManager.class,"Done with expanding archive");
		
		//update dict
		
		d.installedDate = modifdate;
		d.lastModified = modifdate;
		d.installed = true;

		
		//init properties		

		updateInstalledProp(d);
		
		return true;
	}
	
	private void updateInstalledProp(Dictionary d){
		if(installed==null)throw new IllegalStateException("No installed list");
		
		if(d.installed){
			String line = d.lang+","+d.country+","+d.variant+","+d.descr+","+d.archiveName+".zip";
			jEdit.setProperty(INSTALLED_DICTS_PROP+"."+d.getKey()+".line",line);
			jEdit.setProperty(INSTALLED_DICTS_PROP+"."+d.getKey()+".date",String.valueOf(d.installedDate.getTime()));
			
			availables.remove(d);
			installed.add(d);
		}else{
			jEdit.unsetProperty(INSTALLED_DICTS_PROP+"."+d.getKey()+".line");
			jEdit.unsetProperty(INSTALLED_DICTS_PROP+"."+d.getKey()+".date");
			
			installed.remove(d);
			availables.add(d);
		}

		//installed dicts
		String installedS = ",";
		for(Dictionary od:installed){
			installedS+=od.getKey()+",";
		}
		jEdit.setProperty(INSTALLED_DICTS_PROP,installedS.substring(1));
	}
	
	public static class Dictionary extends cswilly.spell.Dictionary implements Comparable<Dictionary>{
		String lang;
		String country;
		String variant;
		String descr;
		//minus .zip
		private String archiveName;
		boolean installed;
		private boolean outdated;
		private Date lastModified;
		private Date installedDate;
		
		public Dictionary(){
			super("","");
		}
		
		public String getKey(){
			return lang+"_"+country+"."+variant;
		}
		
		public int compareTo(Dictionary other){
			return getKey().compareTo(other.getKey());
		}
		
		public boolean equals(Object o){
			if(o==null)return false;
			if(!(o instanceof Dictionary))return false;
			return getKey().equals(((Dictionary)o).getKey());
		}
		
		public String getDirectory(){
			return archiveName;
		}
		
		public String getFile(){
			return variant;
		}
		
		public boolean isOutdated(){
			return lastModified!=null && installedDate!=null
					&& lastModified.after(installedDate);
		}
		
		@Override
		public String getName(){
			return getKey();
		}
		
		@Override
		public String getDescription(){
			return descr;
		}
	}

}
