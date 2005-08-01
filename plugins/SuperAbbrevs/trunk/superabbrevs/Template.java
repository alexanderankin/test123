package superabbrevs;

import java.util.*;

public class Template {
	private ArrayList fields; 
	private int[] indexs;
	private int currentIndex;
	private static final int LASTINDEX = Integer.MAX_VALUE;
	
	
	/*
	* find the end index of the field
	*/
	private int getField(String template, int offset) throws NotFoundException{
		boolean escape = false;
		int end = offset;
		boolean found = false;
		while (!found && end < template.length()){
			end = template.indexOf('}', end+1);
			//if not escaped 
			found = template.charAt(end-1) != '\\';
		}
		if (!found)
			throw new NotFoundException();
		else 
			return end;
	}
	
	private int getIndex(String template, int offset){
		int end = offset;
		boolean moreDigits = true;
		while(moreDigits && end < template.length()){
			if (!Character.isDigit(template.charAt(end))){
				moreDigits = false;
			}else {
				end++;
			}
		}
		return end;
	}
	
	public String parse(String template, int offset){
		//rewrite
		
		StringBuffer output = new StringBuffer(); 
		fields = new ArrayList();
		TreeSet indexs = new TreeSet();
		
		Hashtable defaults = new Hashtable(); 
		
		currentIndex = 0;
		boolean escape = false;
		try{
			for (int i=0;i<template.length(); i++){
				char c = template.charAt(i);
				switch (c){
					case '$':
						if (!escape) {
							
							if (i+1<template.length()){
								char c1 = template.charAt(i+1);
								if (c1 == '{'){
									try{
										int indexEnd = getIndex(template,i+2);
										String sIndex = template.substring(i+2,indexEnd);
										Integer indexO = new Integer(sIndex);
										String defaultField = (String)defaults.get(indexO);
										String field = "";
										int index = indexO.intValue();
										int end;
										if (template.charAt(indexEnd) != '}'){
											end = getField(template,indexEnd);
											field = template.substring(indexEnd+1,end);
											defaults.put(indexO,field);
											int start = output.length() + offset;
											
											field = (defaultField!=null)?defaultField:field;
											
											fields.add(new Range(index,start,start+field.length()));
											indexs.add(new Integer(index));
											output.append(field); 
											
											i=end;
										}else{
											int start = output.length() + offset;
											
											end = start;
											if (defaultField!=null) {
												end = start+defaultField.length();
												output.append(defaultField);
											} 
											
											fields.add(new Range(index,start,end));
											indexs.add(new Integer(index));
											i=indexEnd;
										}
									}catch (NumberFormatException e) {
										//throw away the block if i doesn't have a index 
									}
									
								}else if (Character.isDigit(c1)){
									
									int indexEnd = getIndex(template,i+1);
									String sIndex = template.substring(i+1,indexEnd);
									Integer indexO = new Integer(sIndex);
									int index = indexO.intValue();
									int start = output.length() + offset;
											
									String defaultField = (String)defaults.get(indexO);
									int end = start;
									if (defaultField!=null) {
										end = start+defaultField.length();
										output.append(defaultField);
									} 
									
									fields.add(new Range(index,start,end));
									indexs.add(new Integer(index));
									
									i=indexEnd-1;
								}else if (i+3<template.length() &&
										  template.substring(i+1,i+4).equals("end")){
									int start = output.length()+offset;
									fields.add(new Range(LASTINDEX,start,start));
									indexs.add(new Integer(LASTINDEX));
									i=i+3;
								} else {
									output.append(c);
								}
							}else {
								output.append(c);
							}
						}else {
							output.append(c);
							escape = false;
						}
					break;
					case '\\':
						escape = true;
					break;
					default: 
						output.append(c);
				}
			}
		}catch (NotFoundException e) {
			//the variable was not ended 
		}
		
		Iterator iter = indexs.iterator();
		int i = 0;
		this.indexs = new int[indexs.size()];
		while(iter.hasNext()){
			int index = ((Integer)iter.next()).intValue();
			this.indexs[i] = index;
			i++;
		}
		return output.toString();
	}
	
	private Range getFieldByIndex(int index) {
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			Range r = (Range) iter.next();
			if (r.getIndex() == index){
				return r;
			}
		}
		return null;
	}
	
	private int getIndexSearch(int caret, int fromIndex, int toIndex) 
		throws OutOffRangeException
	{	
		if (toIndex < fromIndex) {
			throw new OutOffRangeException();
		}
		int mid = (toIndex+fromIndex)/2;
		Range midRange = (Range)fields.get(mid);
		if (midRange.getFrom() <= caret && caret <= midRange.getTo()){
			return mid;
		} else if (toIndex == fromIndex) {
			throw new OutOffRangeException();
		} else if (caret < midRange.getFrom()) {
			return getIndexSearch(caret, fromIndex, mid-1);
		} else {
			return getIndexSearch(caret, mid+1, toIndex);
		}
		
	}
	
	private int getIndex(int caret) throws OutOffRangeException {
		return getIndexSearch(caret,0,fields.size()-1);
	}
	
	public Range getField(int caret) throws OutOffRangeException {
		int i = getIndex(caret);
		return (Range)fields.get(i);
	}
	
	public void insert(int caret, int length) throws OutOffRangeException {
		Range range = getField(caret);
		int index = range.getIndex();
		
		if (LASTINDEX == index){
			//end
			throw new OutOffRangeException();
		}
		
		int newLength = range.length()+length;
		int offset = 0;
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			Range r = (Range) iter.next();
			r.move(offset);
			if (r.getIndex() == index){
				
				offset += newLength - r.length();
				r.resize(newLength);
			}
		}
	}
	
	public void delete(int caret, int length) throws OutOffRangeException {
		Range range = getField(caret);
		int index = range.getIndex();
		
		if (LASTINDEX == index){
			//end
			throw new OutOffRangeException();
		}
		
		int newLength = range.length()-length;
		int offset = 0;
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			Range r = (Range) iter.next();
			r.move(offset);
			if (r.getIndex() == index){
				
				offset += newLength - r.length();
				r.resize(newLength);
			}
		}
	}
	
	public Range getCurrentRange(){
		
		if (currentIndex<indexs.length){
			return getFieldByIndex(indexs[currentIndex]);
		}else {
			return null;
		} 
	}
	
	public Range getNextRange(){
		if (currentIndex+1 < indexs.length){
			currentIndex++;
		}else {
			currentIndex = 0;
		}
		return getCurrentRange();
	}
	
	public Range getPrevRange(){
		if (0 < currentIndex){
			currentIndex--;
		}else {
			currentIndex = indexs.length-1;
		}
		return getCurrentRange();
	}
	
	public boolean inTemplate(int caret){
		try {
			getIndex(caret);
			return true;
		} catch (OutOffRangeException e) {
			return false;
		}
	}
	
	public ArrayList getFields(int index){
		ArrayList result = new ArrayList();
		
		Iterator iter = fields.iterator();
		while (iter.hasNext()) {
			Range r = (Range) iter.next();
			if (r.getIndex() == index){
				result.add(r);
			}
		}
		return result;
	}
}
