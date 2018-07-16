package com.hce.paymentgateway;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public final static String PARENTID = "HKHCEH";
	public final static String CUSTOMERID = "HKHCEHXXXXXX";
	public final static String SUBSIDIARY_HKHCEH = "HKHCEH";
	public final static String SUBSIDIARY_HKBRHCEC = "HKBRHCEC";
	public final static String SUBSIDIARY_SWIFT_BIC_HKHCEH = "DHBKHKHH";
	public final static String SUBSIDIARY_SWIFT_BIC_HKBRHCEC = "DBSSHKHH";
	public final static String SUBSIDIARY_INNER_CODE_HKHCEH = "9992";
	public final static String SUBSIDIARY_INNER_CODE_HKBRHCEC = "9991";
	public final static String[] SUBSIDIARIES = {SUBSIDIARY_HKHCEH, SUBSIDIARY_HKBRHCEC};
	public final static String RESULT_FAILURE = "FAILURE";
	public final static String ENV_TEST = "test";
	public final static String ENV_PRO = "pro";
	public static Map<String, String> subsidiaryMap;
	static {
		subsidiaryMap = new HashMap<String, String>(4);
		subsidiaryMap.put(SUBSIDIARY_INNER_CODE_HKHCEH, SUBSIDIARY_HKHCEH);
		subsidiaryMap.put(SUBSIDIARY_INNER_CODE_HKBRHCEC, SUBSIDIARY_HKBRHCEC);
		subsidiaryMap.put(SUBSIDIARY_HKHCEH, SUBSIDIARY_INNER_CODE_HKHCEH);
		subsidiaryMap.put(SUBSIDIARY_HKBRHCEC, SUBSIDIARY_INNER_CODE_HKBRHCEC);
		subsidiaryMap.put(SUBSIDIARY_SWIFT_BIC_HKHCEH, SUBSIDIARY_INNER_CODE_HKHCEH);
		subsidiaryMap.put(SUBSIDIARY_SWIFT_BIC_HKBRHCEC, SUBSIDIARY_INNER_CODE_HKBRHCEC);
	}
}