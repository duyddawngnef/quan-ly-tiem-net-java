package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ThongKeExcelExporter {

    private ThongKeExcelExporter() {
    }

    public static void exportThongKe(File file,
                                     String tieuDe,
                                     String kieuThongKe,
                                     LocalDate tuNgay,
                                     LocalDate denNgay,
                                     List<Map<String, Object>> data,
                                     double tongDoanhThu,
                                     double tongChiPhi,
                                     double tongLoiNhuan,
                                     int soPhienDangChoi) throws Exception {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ThongKe");

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorder(headerStyle);

            CellStyle textStyle = workbook.createCellStyle();
            setBorder(textStyle);

            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle moneyStyle = workbook.createCellStyle();
            setBorder(moneyStyle);
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

            int rowIndex = 0;

            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue(tieuDe);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            Row infoRow1 = sheet.createRow(rowIndex++);
            infoRow1.createCell(0).setCellValue("Kiểu thống kê:");
            infoRow1.createCell(1).setCellValue(kieuThongKe);
            infoRow1.createCell(2).setCellValue("Xuất lúc:");
            infoRow1.createCell(3).setCellValue(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            Row infoRow2 = sheet.createRow(rowIndex++);
            infoRow2.createCell(0).setCellValue("Từ ngày:");
            infoRow2.createCell(1).setCellValue(
                    tuNgay != null ? tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );
            infoRow2.createCell(2).setCellValue("Đến ngày:");
            infoRow2.createCell(3).setCellValue(
                    denNgay != null ? denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );

            Row infoRow3 = sheet.createRow(rowIndex++);
            infoRow3.createCell(0).setCellValue("Tổng doanh thu:");
            infoRow3.createCell(1).setCellValue(tongDoanhThu);
            infoRow3.createCell(1).setCellStyle(moneyStyle);

            infoRow3.createCell(2).setCellValue("Tổng chi phí:");
            infoRow3.createCell(3).setCellValue(tongChiPhi);
            infoRow3.createCell(3).setCellStyle(moneyStyle);

            Row infoRow4 = sheet.createRow(rowIndex++);
            infoRow4.createCell(0).setCellValue("Tổng lợi nhuận:");
            infoRow4.createCell(1).setCellValue(tongLoiNhuan);
            infoRow4.createCell(1).setCellStyle(moneyStyle);

            infoRow4.createCell(2).setCellValue("Phiên đang chơi:");
            infoRow4.createCell(3).setCellValue(soPhienDangChoi);

            rowIndex++;

            Row headerRow = sheet.createRow(rowIndex++);
            String[] headers = {"Thời gian", "Doanh thu", "Nhập hàng / Chi phí", "Lợi nhuận"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            if (data != null) {
                for (Map<String, Object> rowData : data) {
                    Row row = sheet.createRow(rowIndex++);

                    Cell c0 = row.createCell(0);
                    c0.setCellValue(String.valueOf(rowData.getOrDefault("ThoiGian", "")));
                    c0.setCellStyle(textStyle);

                    Cell c1 = row.createCell(1);
                    c1.setCellValue(toDouble(rowData.get("TongDoanhThu"), rowData.get("Thu")));
                    c1.setCellStyle(moneyStyle);

                    Cell c2 = row.createCell(2);
                    c2.setCellValue(toDouble(rowData.get("TongNhapHang"), rowData.get("Chi")));
                    c2.setCellStyle(moneyStyle);

                    Cell c3 = row.createCell(3);
                    c3.setCellValue(toDouble(rowData.get("LoiNhuan"), 0));
                    c3.setCellStyle(moneyStyle);
                }
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1024, 20000));
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static double toDouble(Object value, Object fallback) {
        Object actual = value != null ? value : fallback;
        if (actual instanceof Number) {
            return ((Number) actual).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(actual));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }
}