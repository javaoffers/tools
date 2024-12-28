package com.javaoffers.brief.excel;

public enum ExcelMark {
    WORK_BOOK(1,"workbook"),
    SHEET_NAME(2,"sheetName"),
    SHEET_LIST(3,"sheetList"),
    CREATE_SHEET(4,"createSheet"),
    CREATE_WORK_BOOK(5,"createWorkBook"),
    TITLE_PROCESS_ROW(6,"titleProcessRow"),
    TITLE_PROCESS_CELL(7,"titleProcessCell"),
    DATA_PROCESS_ROW(8,"dataProcessRow"),
    DATA_PROCESS_CELL(9,"dataProcessCell"),
    PROCESS_COLUM_CELL(10, "processColumCell"),
    PROCESS_COLUM_ROW(11, "processColumCell"),
    ;


    private int code;
    private String desc;

    ExcelMark(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
