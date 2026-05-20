package com.competition.training.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExcelExportUtil {
    private ExcelExportUtil() {}

    public static void writeTimeLogs(List<Map<String, Object>> rows, OutputStream out) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("展示时长记录");
            String[] headers = {"日期", "院校", "队伍", "队员", "模块", "技能点", "展示时长（分钟）", "完成状态", "今日产出", "遇到问题", "是否需要协助", "记录状态", "提交时间"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            for (int r = 0; r < rows.size(); r++) {
                Map<String, Object> row = rows.get(r);
                Row excelRow = sheet.createRow(r + 1);
                Object[] values = {
                        row.get("recordDate"), row.get("institutionName"), row.get("teamName"), row.get("memberName"),
                        row.get("categoryName"), row.get("taskName"), row.get("durationMinutes"), row.get("progressStatus"),
                        row.get("resultDesc"), row.get("problemDesc"), Boolean.TRUE.equals(row.get("needSupport")) ? "是" : "否",
                        Boolean.TRUE.equals(row.get("isVoided")) ? "已作废" : "正常", row.get("createdAt")
                };
                for (int c = 0; c < values.length; c++) {
                    excelRow.createCell(c).setCellValue(values[c] == null ? "" : values[c].toString());
                }
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
        }
    }
}
