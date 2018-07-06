package com.hce.paymentgateway.util;

import com.hce.paymentgateway.api.dbs.Instr;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

/**
 * @Author Heling.Yao
 * @Date 14:59 2018/5/24
 */
public class DBSDataFormat {

    public static String format(Instr instr) {
        try {
            Class<? extends Instr> clazz = instr.getClass();
            Field[] orderFields = orderFields(clazz);

            StringBuffer rb = new StringBuffer();
            rb.append("\"");
            for (Field field : orderFields) {
                field.setAccessible(true);
                Object value = field.get(instr);
                if (value == null) {
                    rb.append("\",\"");
                } else {
                    rb.append(value.toString()).append("\",\"");
                }
            }
            String result = rb.toString();
            result = result.substring(0, result.length() - 2);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Instr>T parse(String value, Class<T> clazz) {
        if(StringUtils.isBlank(value)) {
            return null;
        }
        String[] fv = value.split(",");

        try {
            T instr = clazz.newInstance();
            Field[] orderFields = orderFields(clazz);
            for (int i = 0; i < fv.length; i++) {
                orderFields[i].setAccessible(true);
                orderFields[i].set(instr, fv[i]);
            }
            return instr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field[] orderFields(Class<? extends Instr> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Field[] orderFields = new Field[fields.length];
        for (Field field : fields) {
            Order order = field.getAnnotation(Order.class);
            orderFields[order.order() - 1] = field;
        }
        return orderFields;
    }

}
