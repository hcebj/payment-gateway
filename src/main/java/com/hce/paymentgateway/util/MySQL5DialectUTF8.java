package com.hce.paymentgateway.util;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * @Author Heling.Yao
 * @Date 9:39 2018/5/28
 */
public class MySQL5DialectUTF8 extends MySQL5InnoDBDialect {

    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

}
