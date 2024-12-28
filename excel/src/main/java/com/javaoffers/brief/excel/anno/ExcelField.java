package com.javaoffers.brief.excel.anno;

/**
 * 输出字段
 */
public @interface ExcelField {

    /**
     * 输出名称
     */
    String name() default "" ;

    /**
     * 指定长度
     */
    int length() default 0;
}
