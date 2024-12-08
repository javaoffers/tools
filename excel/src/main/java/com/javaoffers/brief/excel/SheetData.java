package com.javaoffers.brief.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//每一个sheet数据封装
    public class SheetData {
        private String titile;
        private List<Map<String, Object>> rowsData = new ArrayList<>();

        public String getTitile() {
            return titile;
        }

        public void setTitile(String titile) {
            this.titile = titile;
        }

        public List<Map<String, Object>> getRowsData() {
            return rowsData;
        }

        public void setRowsData(List<Map<String, Object>> rowsData) {
            this.rowsData = rowsData;
        }

        public void addRowData(Map<String, Object> rowData_) {
            rowsData.add(rowData_);
        }
    }