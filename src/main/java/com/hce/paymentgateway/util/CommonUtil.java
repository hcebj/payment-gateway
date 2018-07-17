package com.hce.paymentgateway.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


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
	
	/**
	 * 随机数生成
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length){
		
		String str="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random=new Random();
		
		StringBuffer sb=new StringBuffer();
		
		for(int i=0;i<length;i++){
						
			int number =random.nextInt(62);
			
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	
	/**
	 * 支付流水号生成
	 * @return
	 */
	public static String getNumberForPK(){
	    String id="";
	    SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
	    String temp = sf.format(new Date());
		//int random=(int) (Math.random()*10000);
		String random = String.format("%04d", (int) (Math.random()*10000));
		id=temp+random;
		return id;
	}
}