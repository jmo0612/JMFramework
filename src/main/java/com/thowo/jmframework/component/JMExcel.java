package com.thowo.jmframework.component;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.thowo.jmframework.JmoFormatCollection;
import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.db.jmoRowObject;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jimi on 10/31/2017.
 */

public class JMExcel {
    Workbook wb;
    List<Cell> editedCell=new ArrayList<Cell>();
    List editedCellType=new ArrayList();

    public JMExcel(Context context, int xlsResId){
        InputStream is = context.getResources().openRawResource(xlsResId);
        try {
            wb=new HSSFWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
            JmoFunctions.trace("IO Error : " + e.getMessage());
        }
    }

    public JMExcel(File file){
        if(file!=null){
            try {
                InputStream is=new FileInputStream(file);
                wb=new HSSFWorkbook(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JmoFunctions.trace("Inputstream Error : " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                JmoFunctions.trace("IO Error : " + e.getMessage());
            }
        }
    }


    public JMExcel(){
        wb=new HSSFWorkbook();
        //wb=new XSSFWorkbook();
    }

    public Workbook getWorkbook(){
        return wb;
    }

    public void saveWorkbook(File file){
        HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
        if(file!=null){
            try {
                JmoFunctions.deleteFile(file);
                FileOutputStream out=new FileOutputStream(file);
                wb.write(out);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JmoFunctions.trace(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                JmoFunctions.trace(e.getMessage());
            }
        }
    }

    private Cell jmoCell(Sheet sheet, String cell){
        int r=-1;
        int c=-1;
        String check="SUM("+cell+")";
        HSSFEvaluationWorkbook wbE=HSSFEvaluationWorkbook.create((HSSFWorkbook)wb);
        try {
            Ptg[] ptgs= FormulaParser.parse(check,wbE, FormulaType.CELL,sheet.getWorkbook().getSheetIndex(sheet));
            if( ptgs[0] instanceof RefPtgBase )    //base class for cell reference "things"
            {
                RefPtgBase ref = (RefPtgBase)ptgs[0];
                r=ref.getRow();
                c=ref.getColumn();
            }
        }catch (FormulaParseException e){
            return null;
        }

        return jmoCell(sheet,r,c);
    }

    public Cell jmoCell(Sheet sheet, int row, int col){
        if(sheet==null)return null;
        if(row<0 || col<0)return null;
        Row r=sheet.getRow(row);
        if(r==null)r=sheet.createRow(row);
        if(r==null)return null;
        Cell c=r.getCell(col);
        if(c==null)c=r.createCell(col);
        if(c==null)return null;
        return c;
    }

    private char get26Char(int num){
        num+=65;
        return (char) num;
    }

    private int getAlphabetVal(char alp){
        int asc=(int) alp;
        if(asc>=97 && asc<=122){
            return asc-96;
        }else if(asc>=65 && asc<=90){
            return asc-64;
        }else return -1;
    }

    public String getColString(int colNum){
        if(colNum<0)return "";
        return String.valueOf(get26Char(colNum));
    }

    public int getColNum(String colName){
        if(colName.equals(""))return -1;
        int digit=colName.length()-1;
        if(digit<0)return -1;
        int walk=0;
        int val=0;
        while (digit>=0){
            int tmp=getAlphabetVal(colName.charAt(walk));
            if(tmp==-1)break;
            val+=tmp* Math.pow(26,digit);
            digit--;
            walk++;
        }

        return val-1;
    }

    public void writeQuery(List<jmoRowObject> rObjs){
        writeQuery(rObjs,"New Sheet",true,0,null,null);
    }

    public void writeQuery(List<jmoRowObject> rObjs, String sheetTitle, boolean setFormat, int startRow, List colIds, List fieldIds){
        boolean makeHeader=true;
        if(rObjs==null)return;
        int colCount=rObjs.get(0).getCount();
        if(fieldIds!=null){
            colCount=fieldIds.size();
            for(int i=0;i<fieldIds.size();i++){
                String tmp=String.valueOf(fieldIds.get(i));
                int ind=0; //==================================================== IF ERROR FIRST INDEX ALWAYS
                try {
                    ind=Integer.parseInt(tmp);
                }catch (Exception e){
                    ind=rObjs.get(0).getIndex(tmp);
                }
                fieldIds.set(i,ind);
            }
        }
        if(colIds!=null){
            makeHeader=false;
            startRow-=1;
            if(colIds.size()<colCount && colIds.size()!=0)colCount=colIds.size();
            for(int i=0;i<colIds.size();i++){
                String tmp=String.valueOf(colIds.get(i));
                int ind=0;
                try {
                    ind=Integer.parseInt(tmp);
                }catch (Exception e){
                    ind=getColNum(tmp);
                }
                colIds.set(i,ind);
            }
        }
        if(colCount==0)return;
        if(colIds==null){
            colIds=new ArrayList();
            for(int i=0;i<colCount;i++){
                colIds.add(i);
            }
        }
        if(fieldIds==null){
            fieldIds=new ArrayList();
            for(int i=0;i<colCount;i++){
                fieldIds.add(i);
            }
        }

        if(wb==null)return;
        Sheet sheet=wb.getSheet(sheetTitle);
        if(sheet==null){
            sheet=wb.createSheet(sheetTitle);
        }

        editedCell=new ArrayList<Cell>();
        editedCellType=new ArrayList();


        if(makeHeader){
            for(int i=0;i<colCount;i++){
                Cell cell=jmoCell(sheet,startRow,(int)colIds.get(i));
                jmoRowObject rObj=rObjs.get(0);
                cell.setCellValue(rObj.getColumnName((int)fieldIds.get(i)));

                Font font = wb.createFont();
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);

                CellStyle cellStyle=wb.createCellStyle();

                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                cellStyle.setFont(font);


                cell.setCellStyle(cellStyle);
                makeCellBorder(cell);
            }
        }


        for(int i=0;i<rObjs.size();i++){
            jmoRowObject rObj=rObjs.get(i);
            for(int j=0;j<colCount;j++){
                Cell cell=jmoCell(sheet,i+1+startRow,(int)colIds.get(j));
                if(setFormat){
                    DataFormat dataFormat=wb.createDataFormat();
                    CellStyle cellStyle= wb.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    cellStyle.setDataFormat(dataFormat.getFormat(getDefaultFormat(rObj.getDataType((int)fieldIds.get(j)))));

                    cell.setCellStyle(cellStyle);
                }
                setValue(cell,rObj,(int)fieldIds.get(j));
                adjustCellWidth(sheet,cell);
                makeCellBorder(cell);
            }
        }

    }

    public void writeQueryMD(List<jmoRowObject> rObjs, String sheetTitle, boolean setFormat, List colIds, List masterRowInd, List<List> masterFieldName, List<List> masterColIds){
        List<List> masterFormula= new ArrayList<List>();

    }

    public void tes(){
        if(wb==null)return;
        Sheet sheet=wb.getSheet("Tes");
        if(sheet==null){
            sheet=wb.createSheet("Tes");
        }

        Cell cellOrg1=jmoCell(sheet,"D5");
        Cell cellOrg2=jmoCell(sheet,"K3");

        Cell cellDest1=jmoCell(sheet,"E5");
        Cell cellDest2=jmoCell(sheet,"K4");

        copyFormula(sheet,cellOrg1,cellDest1);
        copyFormula(sheet,cellOrg2,cellDest2);


        //JmoFunctions.trace("COL: "+ JmoFormatCollection.stringName(new ArrayList<String>(Arrays.asList("Prof","Dr")),".","jimi H.M. mondow",new ArrayList<String>(Arrays.asList("ST","MM")),",",JmoFormatCollection.JMO_STRING_FIRST_CAPS));
    }

    private void copyFormula(Sheet sheet, Cell org, Cell dest){
        String formula="";
        try {
            formula= org.getCellFormula();
        }catch (IllegalStateException e){
            return;
        }
        HSSFEvaluationWorkbook wbE=HSSFEvaluationWorkbook.create((HSSFWorkbook)wb);
        Ptg[] ptgs= FormulaParser.parse(formula,wbE, FormulaType.CELL,sheet.getWorkbook().getSheetIndex(sheet));
        int rowAdd=dest.getRowIndex()-org.getRowIndex();
        int colAdd=dest.getColumnIndex()-org.getColumnIndex();

        for(Ptg ptg : ptgs){
            if(ptg instanceof AreaPtgBase){
                AreaPtgBase ref=(AreaPtgBase) ptg;
                if(ref.isFirstColRelative()){
                    ref.setFirstColumn(ref.getFirstColumn()+colAdd);
                }
                if(ref.isLastColRelative()){
                    ref.setLastColumn(ref.getLastColumn()+colAdd);
                }
                if(ref.isFirstRowRelative()){
                    ref.setFirstRow(ref.getFirstRow()+rowAdd);
                }
                if(ref.isLastRowRelative()){
                    ref.setLastRow(ref.getLastRow()+rowAdd);
                }
            }else if(ptg instanceof RefPtgBase){
                RefPtgBase ref= (RefPtgBase) ptg;
                if(ref.isColRelative()){
                    ref.setColumn(ref.getColumn()+colAdd);
                }
                if(ref.isRowRelative()){
                    ref.setRow(ref.getRow()+rowAdd);
                }
            }
        }

        formula= FormulaRenderer.toFormulaString(wbE,ptgs);
        JmoFunctions.trace("FORMULA: " + formula);
        dest.setCellFormula(formula);
    }
/*
    private void createCells(List<jmoRowObject> rObjs, Sheet sheet){
        if(rObjs==null)return;
        for(int i=0;i<rObjs.size();i++){
            Row row=sheet.createRow(i);
            for(int j=0;j<rObjs.get(0).getCount();j++){
                Cell cell=row.createCell(j);
            }
        }
    }
*/


    public void setValue(Cell cell, jmoRowObject rObj, int colIndex, int JMO_STRING_JmoFormatCollection){
        if(cell==null)return;
        if(rObj==null)return;
        if(colIndex<0 || colIndex>=rObj.getCount())return;
        try {
            if(rObj.getDataType(colIndex)==jmoRowObject.JMO_STRING){
                editedCell.add(cell);
                editedCellType.add(jmoRowObject.JMO_STRING);
                cell.setCellValue(rObj.getDBString(colIndex,JMO_STRING_JmoFormatCollection));
            }
        }catch (Exception e){
            JmoFunctions.trace(e.getMessage());
        }
    }

    public void setValue(Cell cell, jmoRowObject rObj, int colIndex){
        if(cell==null)return;
        if(rObj==null)return;
        if(colIndex<0 || colIndex>=rObj.getCount())return;
        try {
            switch (rObj.getDataType(colIndex)){
                case jmoRowObject.JMO_BOOLEAN:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_BOOLEAN);
                    cell.setCellValue(Boolean.valueOf(rObj.getDBString(colIndex)));
                    break;
                case jmoRowObject.JMO_DATE:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_DATE);
                    cell.setCellValue(JmoFormatCollection.dateFromString(rObj.getDBString(colIndex)));
                    break;
                case jmoRowObject.JMO_DATETIME:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_DATETIME);
                    cell.setCellValue(JmoFormatCollection.dateTimeFromString(rObj.getDBString(colIndex)));
                    break;
                case jmoRowObject.JMO_DOUBLE:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_DOUBLE);
                    cell.setCellValue(Double.valueOf(rObj.getDBString(colIndex)));
                    break;
                case jmoRowObject.JMO_INT:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_INT);
                    cell.setCellValue(Integer.valueOf(rObj.getDBString(colIndex)));
                    break;
                default:
                    editedCell.add(cell);
                    editedCellType.add(jmoRowObject.JMO_STRING);
                    cell.setCellValue(rObj.getDBString(colIndex));
                    break;
            }
        }catch (Exception e){
            JmoFunctions.trace(e.getMessage());
        }
    }

    private void adjustCellWidth(Sheet sheet){

    }

    private void adjustCellWidth(Sheet sheet, Cell cell){
        int maxColW=sheet.getColumnWidth(cell.getColumnIndex());
        String content="";
        int ind=editedCell.indexOf(cell);
        if(ind<0)return;


        switch ((int)editedCellType.get(ind)){
            case jmoRowObject.JMO_BOOLEAN:
                content=String.valueOf(cell.getBooleanCellValue());
                break;
            case jmoRowObject.JMO_DATE:
                content=JmoFormatCollection.stringFromDate(cell.getDateCellValue(),cell.getCellStyle().getDataFormatString());
                break;
            case jmoRowObject.JMO_DATETIME:
                content=JmoFormatCollection.stringFromdateTime(cell.getDateCellValue(),cell.getCellStyle().getDataFormatString());
                break;
            case jmoRowObject.JMO_DOUBLE:
                content=String.valueOf(cell.getNumericCellValue());
                break;
            case jmoRowObject.JMO_INT:
                content=String.valueOf(cell.getNumericCellValue());
                break;
            default:
                content=String.valueOf(cell.getStringCellValue());
                break;
        }

        if(content==null)return;

        JmoFunctions.trace(content);

        int charTot=content.length();
        int defColW=2048;
        int maxChar=8;
        // 2048 def
        int m=charTot/maxChar;
        int s=charTot%maxChar;

        int newColW=defColW;

        if(m==0){
            m=1;
        }else{
            if(s>0)m+=1;
        }

        if(m>5){
            newColW=5*defColW;
        }else{
            newColW=m*defColW;
        }
        if(newColW>maxColW)sheet.setColumnWidth(cell.getColumnIndex(),newColW);

        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    private String getDefaultFormat(int dataType){
        switch (dataType){
            case jmoRowObject.JMO_DATE:
                return JmoFormatCollection.strFormat(JmoFormatCollection.JMO_DATE_MEDIUM);
                //return "[$-en-US]d-mmm-yyyy;@";
            case jmoRowObject.JMO_DATETIME:
                return JmoFormatCollection.strFormat(JmoFormatCollection.JMO_DATETIME_MEDIUM);
            case jmoRowObject.JMO_DOUBLE:
                return JmoFormatCollection.strFormat(JmoFormatCollection.JMO_NUMBER);
            default:
                return "General";
        }
    }

    private void makeCellBorder(Cell cell){
        makeCellBorder(cell,CellStyle.BORDER_THIN,CellStyle.BORDER_THIN,CellStyle.BORDER_THIN,CellStyle.BORDER_THIN);
    }

    private void makeCellBorder(Cell cell, short left, short right, short top, short bottom){
        if(cell==null)return;
        if(wb==null)return;

        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());

        cellStyle.setBorderLeft(left);
        cellStyle.setBorderRight(right);
        cellStyle.setBorderTop(top);
        cellStyle.setBorderBottom(bottom);
        cell.setCellStyle(cellStyle);

    }

    public void openFile(File file, Activity activity){
        Uri path=Uri.fromFile(file);
        Intent excelOpenintent = new Intent(Intent.ACTION_VIEW);
        excelOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        excelOpenintent.setDataAndType(path, "application/xls");
        try {
            activity.startActivity(excelOpenintent);
        }catch (ActivityNotFoundException e){
            JmoFunctions.trace(e.getMessage());
        }
    }

    private void writeCell(){

    }

}
