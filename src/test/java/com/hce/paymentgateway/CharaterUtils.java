package com.hce.paymentgateway;



import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author Heling.Yao
 * @Date 16:14 2018/5/25
 */

import java.util.Random;
 
public class CharaterUtils {
 
	public static String getRandomString(int length){
		String str="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random=new Random();
		
		StringBuffer sb=new StringBuffer();
		
		for(int i=0;i<length;i++){
			
			SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
			String temp = sf.format(new Date());
			
			int number =random.nextInt(62);
			
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getRandomString(5));
	}
}
