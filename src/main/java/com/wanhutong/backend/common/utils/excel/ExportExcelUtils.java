package com.wanhutong.backend.common.utils.excel;

import com.wanhutong.backend.common.config.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(4, 7000);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        workbook.setSheetName(sheetNum, sheetTitle
//                ,HSSFWorkbook.ENCODING_UTF_16
        );
        String url = Global.getProjectPath() + "/src/main/webapp/static/jingle/excel/云仓库存月度盘点表（应用模板）.xlsx";
        InputStream fileInputStream = new FileInputStream(new File(url));
        XSSFWorkbook modelWorkbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet modelSheet = modelWorkbook.getSheetAt(0);
//        modelWorkbook.getNumCellStyles();
//        workbook.createCellStyle();
        // 复制源表中的合并单元格
        MergerRegion(sheet,modelSheet);
        int firstRow = modelSheet.getFirstRowNum();
        int lastRow = modelSheet.getLastRowNum();
        for (int i = firstRow; i <= lastRow; i++) {
            Row rowCreate = sheet.createRow(i);
            XSSFRow modelRow = modelSheet.getRow(i);
            rowCreate.setRowStyle(modelRow.getRowStyle());
            if (i > 0) {
                rowCreate.setHeight((short) (25 * 20));
            }
            int firstCell = modelRow.getFirstCellNum();
            int lastCell = modelRow.getLastCellNum();
            for (int j = firstCell; j < lastCell; j++) {
                rowCreate.createCell(j);
                String strVal = modelRow.getCell(j) == null ? "" : modelRow.getCell(j).getStringCellValue();
                rowCreate.getCell(j).setCellValue(strVal);
                if (i <= 3) {
                    rowCreate.getCell(j).setCellStyle(cellStyle);
                }
            }
        }
        String pictureUrl = Global.getProjectPath() + "/src/main/webapp/static/jingle/image/logo.png";
        FileInputStream fis = new FileInputStream(pictureUrl);
        byte[] bytes = IOUtils.toByteArray(fis);
        int pictureIdx = workbook.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_PNG);
        fis.close();
        //创建一个顶级容器
        Drawing drawing = sheet.createDrawingPatriarch();
        CreationHelper helper = workbook.getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(1);
        anchor.setRow1(1);
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        int lastDataRowNum = 4;
        for (List<String> rowData : result) {
            Row row = sheet.createRow(lastDataRowNum);
            row.setHeight((short) (20 * 20));
            int lastDataCellNum = 1;
            for (String cellData : rowData) {
                row.createCell(lastDataCellNum).setCellValue(cellData);
                row.getCell(lastDataCellNum).setCellStyle(cellStyle);
                lastDataCellNum ++;
            }
            lastDataRowNum ++;
        }

        CellRangeAddress region = new CellRangeAddress(lastDataRowNum, lastDataRowNum, 2, 7);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region1 = new CellRangeAddress(lastDataRowNum, lastDataRowNum, 8, 9);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        CellRangeAddress region2 = new CellRangeAddress(lastDataRowNum, lastDataRowNum, 10, 11);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        sheet.addMergedRegion(region);
        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        sheet.createRow(lastDataRowNum).createCell(2).setCellValue("盘点人签字：");
        sheet.getRow(lastDataRowNum).createCell(8).setCellValue("负责人签字：");
        sheet.getRow(lastDataRowNum).setHeight((short) (20 * 20));
    }

    /**
     　 * 复制原有sheet的合并单元格到新创建的sheet
     　 *
     　 * @param sheetCreat
     　 *　　　　　 新创建sheet
     　 * @param sheet
     　 *　　　　　 原有的sheet
     　 */
    private static void MergerRegion(Sheet sheetCreat, Sheet sheet) {
        int sheetMergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress mergedRegionAt = sheet.getMergedRegion(i);
            sheetCreat.addMergedRegion(mergedRegionAt);
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