package com.diancan.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R.integer;

public class MyDateUtils {
	 /** 
     * 根据日期获得星期 
     * @param date 
     * @return 
     */ 
	public static String getWeekOfDate(Date date) { 
	  String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" }; 
	  String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" }; 
	  Calendar calendar = Calendar.getInstance(); 
	  calendar.setTime(date); 
	  int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; 
	  return weekDaysName[intWeek]; 
	} 
	/** 
	  * 获得周一的日期 
	  * 
	  * @param date 
	  * @return 
	  */ 
	public static String getMonday(Date date) {
	
	  Calendar calendar = Calendar.getInstance();
	
	  calendar.setTime(date);
	
	  calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	  
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  return dateFormat.format(calendar.getTime());
	
	} 
	/** 
	  * 获得周三的日期 
	  * 
	  * @param date 
	  * @return 
	  */ 
	public static String getWednesday(Date date) {
	
	  Calendar calendar = Calendar.getInstance();
	
	  calendar.setTime(date);
	
	  calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  return dateFormat.format(calendar.getTime());
	
	} 
	    /** 
	  * 获得周五的日期 
	  * 
	  * @param date 
	  * @return 
	  */ 
	public static String getFriday(Date date) {
	
	  Calendar calendar = Calendar.getInstance();
	
	  calendar.setTime(date);
	
	  calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  return dateFormat.format(calendar.getTime()); 
	}
	
	/** 
	  * 当前日期前几天或者后几天的日期 
	  * @param n 
	  * @return 
	  */  
	public static String afterNDay(int n) {
	
	  Calendar calendar = Calendar.getInstance();
	
	  calendar.setTime(new Date());
	
	  calendar.add(Calendar.DATE, n);
	
	  Date date = calendar.getTime();
	
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  String s = dateFormat.format(date);
	
	  return s;
	
	} 
	
	/** 
	  * 判断两个日期是否在同一周 
	  * 
	  * @param date1 
	  * @param date2 
	  * @return 
	  */ 
	public static boolean isSameWeekDates(Date date1, Date date2) { 
	  Calendar cal1 = Calendar.getInstance(); 
	  Calendar cal2 = Calendar.getInstance(); 
	  cal1.setTime(date1); 
	  cal2.setTime(date2); 
	  int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR); 
	  if (0 == subYear) { 
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2 
	     .get(Calendar.WEEK_OF_YEAR)) 
	    return true; 
	  } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) { 
	   // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周 
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2 
	     .get(Calendar.WEEK_OF_YEAR)) 
	    return true; 
	  } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) { 
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2 
	     .get(Calendar.WEEK_OF_YEAR)) 
	    return true; 
	  } 
	  return false; 
	}

	/**
	 * 通过和当前日期比较获得时间段的字符串
	 * @param currentDate
	 * @param oldDate
	 * @return
	 */
	public static String getWeekString(Date currentDate,Date oldDate){
		String[] weekDaysName = { "本周", "上周", "两周前", "更早" }; 
		Calendar cal1 = Calendar.getInstance(); 
	    Calendar cal2 = Calendar.getInstance(); 
	    cal1.setTime(currentDate); 
	    cal2.setTime(oldDate);
	    int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
	    if(subYear==0){
	    	int i = cal1.get(Calendar.WEEK_OF_YEAR) - cal2.get(Calendar.WEEK_OF_YEAR);
	    	if(i==0||i==1){
	    		return weekDaysName[i];
	    	}
	    	else if(i>=2&&i<=4){
	    		i=2;
	    		return weekDaysName[i];
	    	}
	    	else{
	    		int yi = cal1.get(Calendar.MONTH)-cal2.get(Calendar.MONTH);
	    		if(yi==0){
	    			i=2;
	    			return weekDaysName[i];
	    		}
	    		else{
	    			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy年MM月");
	    			return sFormat.format(oldDate);
	    		}
	    	}
	    }
	    else if(1 == subYear && 11 == cal2.get(Calendar.MONTH)) { 
	 	   // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周 
	    	int i = cal1.get(Calendar.WEEK_OF_YEAR) - cal2.get(Calendar.WEEK_OF_YEAR);
	    	if(i==0||i==1){
	    		return weekDaysName[i];
	    	}
	    	else if(i>=2&&i<=4){
	    		i=2;
	    		return weekDaysName[i];
	    	}
	    	else{
	    		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy年MM月");
    			return sFormat.format(oldDate);
	    	}
	    }
	    else{
	    	
	    	return weekDaysName[3];
	    }
	}
	
	public static String getStringFormDate(Date currentDate,Date oldDate){
		Calendar cal1 = Calendar.getInstance(); 
	    Calendar cal2 = Calendar.getInstance(); 
	    cal1.setTime(currentDate); 
	    cal2.setTime(oldDate);
	    String strDate;
	    int yi = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
	    if(yi>0){
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	    	strDate = sdf.format(oldDate);
	    }
	    else{
	    	int di = cal1.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);
	    	if(di==0){
	    		strDate = "今天";
	    	}
	    	else if(di==1){
	    		strDate = "昨天";
	    	}
	    	else if(di==2){
	    		strDate = "前天";
	    	}
	    	else{
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		    	strDate = sdf.format(oldDate);
	    	}
	    }
	    
	    String weekString = getWeekOfDate(oldDate);
	    
	    return strDate+"  "+weekString;
	}
}
