package com.hce.paymentgateway.validate;

/**
 * @Author Heling.Yao
 * @Date 16:14 2018/5/30
 */
public enum DataType {

    N("N", "^[0-9]*$", "数字0-9"),
    A("A", "^[A-Za-z]+$", "字母"),
    AN("AN", "^[A-Za-z0-9]+$", "字母数字"),
    AND("AND", "^[A-Za-z0-9-]+$", "字母数字-"),
    S("S", "^[A-Za-z0-9-/?:().,'+]+$", "特殊字符"),
    G("G", "^[A-Za-z0-9-!\"#$%&'()*+,/:;<'=>?@\\^_{|}~\\[\\]]+$", "特殊字符"),
    EMAIL("EMAIL", "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$", "特殊字符"),
    ANC("ANC", "^[A-Za-z0-9\u4e00-\u9fa5]+$", "字母汉字"),
    MONEY("自定义", "(^[1-9](\\d+)?(\\.\\d{1,2})?$)|(^0$)|(^\\d\\.\\d{1,2}$)", "金额"),
    ANY("ANY", "[\\s\\S]*", "以上字符");

    private String dataType;
    private String regex;
    private String description;

    DataType(String dataType, String regex, String description) {
        this.dataType = dataType;
        this.regex = regex;
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DataType{" +
                "dataType='" + dataType + '\'' +
                ", regex='" + regex + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
