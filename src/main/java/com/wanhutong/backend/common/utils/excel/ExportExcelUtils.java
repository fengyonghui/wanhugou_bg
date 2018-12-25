package com.wanhutong.backend.common.utils.excel;

import com.wanhutong.backend.common.utils.SystemPath;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class ExportExcelUtils {

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * @param workbook
     * @param sheetNum   (sheet的位置，0表示第一个表格中的第一个sheet)
     * @param sheetTitle （sheet的名称）
     * @param headers    （表格的标题）
     * @param result     （表格的数据）
     * @param fileName   （输出流）
     * @throws Exception
     * @Title: exportExcel
     * @Description: 导出Excel的方法
     * @author: evan @ 2014-01-09
     */
    public void exportExcel(SXSSFWorkbook workbook, int sheetNum,
                            String sheetTitle, String[] headers, List<List<String>> result,
                            String fileName) throws Exception {
        // 生成一个表格
        sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle
//                ,HSSFWorkbook.ENCODING_UTF_16
        );
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);

        // 产生表格标题行
        Row row = sheet.createRow(0);
        row.setHeightInPoints(16);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());
        }
        // 遍历集合数据，产生数据行
        if (result != null) {
            int index = 1;
            for (List<String> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(str);
                    cellIndex++;
                }
                index++;
            }
        }
    }

    /**
     * @param workbook
     * @param sheetNum   (sheet的位置，0表示第一个表格中的第一个sheet)
     * @param sheetTitle （sheet的名称）
     * @param headers    （表格的标题）
     * @param result     （表格的数据）
     * @param fileName   （输出流）
     * @throws Exception
     * @Title: exportExcel
     * @Description: 导出Excel的方法
     * @author: evan @ 2014-01-09
     */
    public void exportExcel2(SXSSFWorkbook workbook, int sheetNum,
                            String sheetTitle, String[] headers, List<List<String>> result,
                            String fileName) throws Exception {
        // 生成一个表格
        sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle
//                ,HSSFWorkbook.ENCODING_UTF_16
        );
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 2);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region1 = new CellRangeAddress(0, 0, 3, 7);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region2 = new CellRangeAddress(0, 0, 8, 10);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        sheet.addMergedRegion(region);
        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        // 产生表格标题行
        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(50);
        Cell cell0 = row0.createCell(0);
        cell0.setCellStyle(style);
//        String url = "static/jingle/image/logo.png";
        String url = "D:\\dev\\wanhugou_bg\\src\\main\\webapp\\static\\jingle\\image\\logo.png";
        Drawing patriarch = sheet.createDrawingPatriarch();
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(new File(url));
        ImageIO.write(bufferedImage,"png",byteArrayOut);
        HSSFClientAnchor anchor = new HSSFClientAnchor();
        patriarch.createPicture(anchor,workbook.addPicture(byteArrayOut.toByteArray(),SXSSFWorkbook.PICTURE_TYPE_PNG));


        XSSFRichTextString s = new XSSFRichTextString(patriarch.toString());
        cell0.setCellValue(s.toString());
        Cell cell1 = row0.createCell(3);
        cell1.setCellStyle(style);
        XSSFRichTextString s1 = new XSSFRichTextString("云仓月度盘点表");
        cell1.setCellValue(s1);
        Cell cell2 = row0.createCell(7);
        cell2.setCellStyle(style);
        XSSFRichTextString s2 = new XSSFRichTextString("www.wanhutong.com");
        cell2.setCellValue(s2);
        Row row = sheet.createRow(1);
        row.setHeightInPoints(16);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());
        }
        // 遍历集合数据，产生数据行
        if (result != null) {
            int index = 2;
            for (List<String> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(str);
                    cellIndex++;
                }
                index++;
            }
        }
    }


//    @SuppressWarnings("unchecked")
//    public static void main(String[] args) {
//        try {
//            OutputStream out = new FileOutputStream("D:\\test.xls");
//            List<List<String>> data = new ArrayList<List<String>>();
//            for (int i = 1; i < 5; i++) {
//                List rowData = new ArrayList();
//                rowData.add(String.valueOf(i));
//                rowData.add("东霖柏鸿");
//                data.add(rowData);
//            }
//            String[] headers = {"ID", "用户名"};
//            ExportExcelUtils eeu = new ExportExcelUtils();
//            SXSSFWorkbook workbook = new SXSSFWorkbook();
//            eeu.exportExcel(workbook, 0, "上海", headers, data, out);
//            eeu.exportExcel(workbook, 1, "深圳", headers, data, out);
//            eeu.exportExcel(workbook, 2, "广州", headers, data, out);
//            //原理就是将所有的数据一起写入，然后再关闭输入流。
//            workbook.write(out);
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}  