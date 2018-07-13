package com.hce.paymentgateway;

import com.hce.paymentgateway.api.hce.AccountTransferRequest;
import com.hce.paymentgateway.service.TransactionService;
import com.hce.paymentgateway.service.impl.AccountTransferService;
import com.hce.paymentgateway.util.Constant;
import com.hce.paymentgateway.util.JsonUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.UUID;

/**
 * @Author Heling.Yao
 * @Date 16:14 2018/5/25
 */
public class SimpleTest {

    public static void main(String[] args) throws IllegalAccessException {
        /*AccountTransferRequest request = new AccountTransferRequest();
        request.setProductType(Constant.ACCOUNT_TRANSFER);
        request.setTransId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        request.setPaymentDate("25052018");
        System.out.println(JsonUtil.toJson(request));

        System.out.println(TransactionService.class.isAssignableFrom(AccountTransferService.class));

        ParameterizedType type = (ParameterizedType) (new AccountTransferService()).getClass().getGenericSuperclass();
        System.out.println(type);*/
//        generateJson();
        boolean match = "".matches("[\\s\\S]*");
        System.out.println(match);
    }

    public static void generateJson() throws IllegalAccessException {
        Field[] fields = AccountTransferRequest.class.getDeclaredFields();
        AccountTransferRequest request = new AccountTransferRequest();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            field.set(request, field.getName() + "--" + i);
        }
        request.setTransId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        request.setTransTime(new Date());
        //request.setApplicationId("HCE");
        request.setProductType("ACT");
//        request.setPaymentOrgId("123");

        System.out.println(JsonUtil.toJson(request));
    }

}
