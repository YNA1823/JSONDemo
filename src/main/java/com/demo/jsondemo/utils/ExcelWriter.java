package com.demo.jsondemo.utils;

import com.demo.jsondemo.model.Post;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel 写入工具
 * 用于将测试数据导出到 Excel 文件
 */
public class ExcelWriter {

    private static final String[] POST_HEADERS = {"userId", "title", "body"};
    private static final String[] INVALID_DATA_HEADERS = {"description", "userId", "title", "body"};

    /**
     * 将 Post 列表导出到 Excel
     */
    public static void exportPostsToExcel(List<Post> posts, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Posts");

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < POST_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(POST_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Post post : posts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(post.getUserId() != null ? post.getUserId() : 0);
                row.createCell(1).setCellValue(post.getTitle() != null ? post.getTitle() : "");
                row.createCell(2).setCellValue(post.getBody() != null ? post.getBody() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < POST_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将异常测试数据导出到 Excel
     */
    public static void exportInvalidDataToExcel(List<Map<String, Object>> invalidData, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("InvalidData");

            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < INVALID_DATA_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(INVALID_DATA_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Map<String, Object> item : invalidData) {
                Row row = sheet.createRow(rowNum++);
                String description = (String) item.get("description");
                row.createCell(0).setCellValue(description);

                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) item.get("data");

                Object userId = data.get("userId");
                row.createCell(1).setCellValue(userId != null ? userId.toString() : "null");

                Object title = data.get("title");
                row.createCell(2).setCellValue(title != null ? title.toString() : "null");

                Object body = data.get("body");
                row.createCell(3).setCellValue(body != null ? body.toString() : "null");
            }

            // 自动调整列宽
            for (int i = 0; i < INVALID_DATA_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将边界值测试数据导出到 Excel
     */
    public static void exportBoundaryDataToExcel(List<Post> boundaryPosts, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("BoundaryData");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle highlightStyle = createHighlightStyle(workbook);

            // 创建表头（增加一列描述）
            String[] headers = {"userId", "title", "body", "note"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Post post : boundaryPosts) {
                Row row = sheet.createRow(rowNum);

                Cell userIdCell = row.createCell(0);
                userIdCell.setCellValue(post.getUserId() != null ? post.getUserId() : 0);

                Cell titleCell = row.createCell(1);
                String title = post.getTitle() != null ? post.getTitle() : "";
                titleCell.setCellValue(title.length() > 50 ? title.substring(0, 50) + "..." : title);

                Cell bodyCell = row.createCell(2);
                String body = post.getBody() != null ? post.getBody() : "";
                bodyCell.setCellValue(body.length() > 50 ? body.substring(0, 50) + "..." : body);

                // 添加备注
                Cell noteCell = row.createCell(3);
                noteCell.setCellValue(generateNote(post));

                // 空值或特殊数据高亮
                if (title.isEmpty() || body.isEmpty() || title.equals("   ")) {
                    userIdCell.setCellStyle(highlightStyle);
                    titleCell.setCellStyle(highlightStyle);
                    bodyCell.setCellStyle(highlightStyle);
                }

                rowNum++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private static CellStyle createHighlightStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static String generateNote(Post post) {
        String title = post.getTitle() != null ? post.getTitle() : "";
        String body = post.getBody() != null ? post.getBody() : "";

        if (title.isEmpty() && body.isEmpty()) {
            return "空字符串";
        }
        if (title.equals("   ")) {
            return "纯空格";
        }
        if (title.contains("<script>") || body.contains("<script>")) {
            return "XSS测试";
        }
        if (title.contains("DROP") || body.contains("DROP")) {
            return "SQL注入测试";
        }
        if (title.contains("../") || body.contains("../")) {
            return "路径遍历测试";
        }
        if (title.length() > 100 || body.length() > 100) {
            return "超长字符串";
        }
        if (post.getUserId() != null && post.getUserId() <= 0) {
            return "边界userId";
        }
        return "边界值";
    }
}
