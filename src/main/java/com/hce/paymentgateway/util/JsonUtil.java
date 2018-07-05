package com.hce.paymentgateway.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import org.apache.commons.lang.StringUtils;

public class JsonUtil {

    public static String toJson(Object obj) {
        if(obj == null) {
            return null;
        }
        String result = null;
        try {
            IOUtils.DEFAULT_PROPERTIES.put(IOUtils.FASTJSON_COMPATIBLEWITHJAVABEAN, true);
            TypeUtils.compatibleWithJavaBean = true;
            result = JSONObject.toJSONString(obj);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T>T parseObject(String json, Class<T> clazz) {
        if(StringUtils.isBlank(json)) {
            return null;
        }
        T result = null;
        try {
            IOUtils.DEFAULT_PROPERTIES.put(IOUtils.FASTJSON_COMPATIBLEWITHJAVABEAN, true);
            TypeUtils.compatibleWithJavaBean = true;
            result = JSONObject.parseObject(json, clazz);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
