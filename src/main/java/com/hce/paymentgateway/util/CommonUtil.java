package com.hce.paymentgateway.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
	/**
	 * @param strDate
	 * @param dateForm1 
	 * @param dateForm2
	 * @return
	 * @throws ParseException
	 */
	public static String getFormatDate(String strDate , String dateForm1, String dateForm2) throws ParseException  {
		SimpleDateFormat formatter1  = new SimpleDateFormat(dateForm1);
	    SimpleDateFormat formatter2  = new SimpleDateFormat(dateForm2);
	    Date date = formatter1.parse(strDate);
	    strDate = formatter2.format(date);
	    return strDate;
    }
}