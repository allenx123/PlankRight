package com.minime.plank;

/**
 * Author: Allen Xu
 */

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Utility {
	 //utility function to output debug message
	 public static void debug(String msg) {
		 System.out.println(msg);
	 }
	 //utility function to log an error
	 public static void error(Exception e) {
		 e.printStackTrace();
	 }
	//utility function to retrieve html resource file into a string
	 public static String getMsg(int fileResourceID, Context context) {
		 	String message = null;
				InputStream inputStream = null;
				try {
					inputStream = context.getResources().openRawResource(fileResourceID);
					byte[] reader = new byte[inputStream.available()];
					while (inputStream.read(reader) != -1) {}
					message = new String(reader);
				} catch(IOException e) {
					error(e);
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							error(e);
						}
					}
				}
				
				return message;

	}	
	  /**
	   * 1. return a substring of String s that starts from beginMark (exclusive)
	   *    and end before endMark (exclusive)
	   * 2. if beginMark is not found, return null
	   * 3. if endMark is not found, return s from beginMark (exclusive)
	   * 4. if beginMark==null, will call substring(s, 0, endMark)
	   * 5. if endMark==null, will call substring(s, beginMark)
	   */
	  public static String substring(String s, String beginMark, String endMark) {
		
		if (s==null)
			return null;
		
	    if (beginMark==null)
	      return substring(s, 0, endMark);
	    else if (endMark==null)
	      return substring(s, beginMark);

	    String sub = substring(s, beginMark);
	    if (sub==null) return null;
	    sub = substring(sub, 0, endMark);
	    return sub;
	  }
	  /**
	   * return a substring of s that includes every character
	   * except the last n characters
	   * if n>=s.length, return ""
	   * if n<=0, return s
	   * @param s
	   * @param n last n characters
	   * @return
	   */
	  public static String substring(String s, int n) {
		  if (s==null)
			  return null;
		  else if (s.isEmpty())
			  return "";
		  else if (n<=0)
			  return s;
		  else if (n>=s.length())
			  return "";
		  else {
			  int length = s.length();
			  String temp = "";
			  for (int i=0; i<length-n; i++) {
				  temp = temp+s.charAt(i);
			  }
			  return temp;
		  }
	  }
	  /**
	   * return a substring of String s that starts from beginIndex (inclusive)
	   * and end before endMark (exclusive)
	   * if endMark is not found, return s.substring(beginIndex)
	   */
	  public static String substring(String s, int beginIndex, String endMark) {
	    if (s==null) return null;
	    String sub = s.substring(beginIndex);
	    int endIndex = sub.indexOf(endMark);
	    if (endIndex==-1)
	      return sub;
	    else
	      return sub.substring(0, endIndex);
	  }
	  /**
	   * return a substring of String s that starts from beginMark (exclusive)
	   * if beginMark is not found, return null
	   */
	  public static String substring(String s, String beginMark) {
	    int beginIndex = s.indexOf(beginMark);
	    if (beginIndex==-1)
	      return null;
	    else
	      return s.substring(beginIndex+beginMark.length());
	  }

}
