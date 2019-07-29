package com.thowo.jmframework.component;

import com.thowo.jmframework.JmoFormatCollection;
import com.thowo.jmframework.JmoFunctions;
import com.thowo.jmframework.db.jmoRowObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimi on 11/25/2017.
 */


public class JMExcelMD {
    private JMExcel xls;
    private List<jmoRowObject> rObjs;
    private List<List> masterKeyFields;
    private List<String> masterTemplateSheetNames;
    private List masterTopOfDetails; /*boolean*/
    private List<List> masterFieldIds; /*int*/
    private List<List> masterIsFormulaDetail; /*boolean*/
    private List cols;//int
    private int resultStartRow;
    private String resultSheetName;

    private int rObjIndex;
    private int masterIndex;



    private List keyFields; //int
    private List fieldIds;//int
    private List<CellStyle> cellStyles;
    private int row;

    private JMExcelMD next;
    private JMExcelMD prev;
    private JMExcelMD nextAll;
    private JMExcelMD prevAll;
    private JMExcelMD master;
    private List<JMExcelMD> details;
    private JMExcelMD nextPos;
    private JMExcelMD prevPos;

    public JMExcelMD(JMExcel xls, List<jmoRowObject> rObjs, List<List> masterKeyFields/*int*/, List<String> masterTemplateSheetNames,
                     List masterTopOfDetails/*boolean*/, List<List> masterFieldIds/*int*/, List<List> masterFieldIdStringFormat/*int JmoFormatCollection*/,List<List> masterIsFormulaDetail/*boolean*/,
                     List cols /*int*/, int resultStartRow /*for all sheet templates*/, String resultSheetName){
        this.xls=xls;
        this.rObjs=rObjs;
        this.masterKeyFields=masterKeyFields;
        this.masterTemplateSheetNames=masterTemplateSheetNames;
        this.masterTopOfDetails=masterTopOfDetails;
        this.masterFieldIds=masterFieldIds;
        this.masterIsFormulaDetail=masterIsFormulaDetail;
        this.cols=cols;
        this.resultStartRow=resultStartRow;
        this.resultSheetName=resultSheetName;

    }

    public void make(){
        if(!isValid())return;
        rObjIndex=0;
        masterIndex=masterKeyFields.size()-1;
        generalInit();
        initNextDetail();
        JMExcelMD firstMaster=getFirstMaster();
        firstMaster.generatePositions();
        JMExcelMD first=findFirst();
        first.write(first.resultStartRow);
        firstMaster.makeFormulaMaster();
        removeTemplateSheets();
    }


    private String getFormulaCopied(Sheet sheet, Cell org, Cell dest){
        String formula;
        try {
            formula= org.getCellFormula();
        }catch (IllegalStateException e){
            return"";
        }
        HSSFEvaluationWorkbook wbE=HSSFEvaluationWorkbook.create((HSSFWorkbook)sheet.getWorkbook());
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
        return formula;
    }

    private void setFormulaMaster(Cell cell, List detailRows){
        if(detailRows==null)return;
        String formula;
        try {
            formula= cell.getCellFormula();
        }catch (IllegalStateException e){
            return;
        }

        final String identifier="$"+ xls.getColString(cell.getColumnIndex()) +"$1";

        String dets="";
        for(int i=0;i<detailRows.size();i++){
            if(!dets.equals(""))dets+=",";
            dets+=xls.getColString(cell.getColumnIndex())+String.valueOf((int)detailRows.get(i)+1);
        }


        formula=replaceSubStrings(formula,identifier,dets);

        cell.setCellFormula(formula);
    }

    private String replaceSubStrings(String str,String subStr, String newStr){
        String ret=str;
        final int len=subStr.length();
        if(str.length()<len){
            return str;
        }
        List indexes=new ArrayList();
        for(int i=0;i<str.length();i++){
            if(i+len>str.length())break;
            if(str.substring(i,i+len).equals(subStr)){
                indexes.add(i);
                i+=len-1;
            }
        }
        if(indexes.size()>0){
            int wlk=0;
            ret="";
            for(int i=0;i<indexes.size();i++){
                int curInd=(int)indexes.get(i);
                if(curInd>wlk){
                    String bef=str.substring(wlk,curInd);
                    wlk=curInd+len;
                    ret+=bef+newStr;
                }
            }
            if(wlk<str.length()){
                ret+=str.substring(wlk,str.length());
            }
        }
        return ret;
    }

    private void makeFormulaMaster(){
        if(details!=null){
            List rows=new ArrayList();
            for(int i=0;i<details.size();i++){
                rows.add(details.get(i).row);
            }
            for(int i=0;i<cols.size();i++){
                if((boolean)masterIsFormulaDetail.get(masterIndex).get(i)){
                    setFormulaMaster(xls.jmoCell(xls.getWorkbook().getSheet(resultSheetName),row,(int)cols.get(i)),rows);
                }
            }
            for(int i=0;i<details.size();i++){
                details.get(i).makeFormulaMaster();
            }
        }
    }

    public void copyCell(Cell org, Cell dest){
        if(org==null || dest==null)return;
        try {
            switch (org.getCellType()){
                case Cell.CELL_TYPE_BOOLEAN:
                    dest.setCellValue(org.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    dest.setCellValue(org.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    String formula=getFormulaCopied(org.getSheet(),org,dest);
                    if(!formula.equals("")){
                        dest.setCellFormula(formula);
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    dest.setCellValue(org.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    dest.setCellValue(org.getStringCellValue());
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            JmoFunctions.trace(e.getMessage());
        }
    }

    private void setCellStringValue(Cell cell, jmoRowObject rObj, List comFieldIds, List comFieldIdStringFormats){
        if(comFieldIds==null)return;
        boolean formated=true;
        if(comFieldIdStringFormats==null){
            formated=false;
        }else{
            if(comFieldIds.size()!=comFieldIdStringFormats.size())return;
        }
        String result;
        try {
            result= cell.getStringCellValue();
        }catch (IllegalStateException e){
            return;
        }

        final String identifier="jmoFIELD";

        for(int i=0;i<comFieldIds.size();i++){
            if(!formated){
                result=replaceSubStrings(result,identifier+String.valueOf(i),rObj.getDBString((int)comFieldIds.get(i)));
            }else{
                result=replaceSubStrings(result,identifier+String.valueOf(i),rObj.getDBString((int)comFieldIds.get(i),(int)comFieldIdStringFormats.get(i)));
            }
        }

        cell.setCellValue(result);
    }

    private void write(int row){
        this.row=row;
        Sheet sheet=xls.getWorkbook().getSheet(resultSheetName);
        for(int wCol=0;wCol<cols.size();wCol++){
            Sheet org=xls.getWorkbook().getSheet(masterTemplateSheetNames.get(masterIndex));
            Cell orgCell=xls.jmoCell(org,resultStartRow,(int)cols.get(wCol));

            Cell cell=xls.jmoCell(sheet,row,(int)cols.get(wCol));
            cell.setCellStyle(cellStyles.get(wCol));

            if(fieldIds.get(wCol)!=null){
                if(fieldIds.get(wCol).getClass()==ArrayList.class){
                    List tmp=(List) fieldIds.get(wCol);
                    if(tmp!=null){
                        cell.setCellValue(orgCell.getStringCellValue());
                        //setCellStringValue(cell,rObjs.get(rObjIndex),tmp);
                    }
                }else{
                    int fieldIndex=(int)fieldIds.get(wCol);
                    xls.setValue(cell,rObjs.get(rObjIndex),fieldIndex);
                }
            }else{
                copyCell(orgCell,cell);
            }
        }
        if(nextPos!=null)nextPos.write(row+1);


    }

    private JMExcelMD findFirst(){
        JMExcelMD ret=this;
        while (ret.prevPos!=null){
            ret=ret.prevPos;
        }
        return ret;
    }


    private JMExcelMD getFirstMaster(){
        JMExcelMD ret=this;
        while (ret!=null){
            if(ret.master==null)break;
            ret=ret.master;
        }
        return ret;
    }

    private void insertDetailsPosition(){
        if(details.size()>0){
            JMExcelMD oldPrevPos=prevPos;
            JMExcelMD oldNextPos=nextPos;
            if((boolean)masterTopOfDetails.get(masterIndex)){
                for(int i=0;i<details.size();i++){
                    if(i==0){
                        nextPos=details.get(0);
                        details.get(0).prevPos=this;
                    }else{
                        details.get(i).prevPos=details.get(i-1);
                        details.get(i-1).nextPos=details.get(i);
                        if(i==details.size()-1){
                            details.get(i).nextPos=oldNextPos;
                            if(oldNextPos!=null)oldNextPos.prevPos=details.get(i);
                        }
                    }
                }
            }else{
                for(int i=0;i<details.size();i++){
                    if(i==0){
                        details.get(i).prevPos=oldPrevPos;
                        if(oldPrevPos!=null)oldPrevPos.nextPos=details.get(i);
                    }else{
                        details.get(i).prevPos=details.get(i-1);
                        details.get(i-1).nextPos=details.get(i);
                        if(i==details.size()-1){
                            details.get(i).nextPos=this;
                            prevPos=details.get(i);
                        }
                    }
                }
            }
        }
    }

    private void generatePositions(){
        if(details!=null){
            insertDetailsPosition();
            for(int i=0;i<details.size();i++){
                details.get(i).generatePositions();
            }
        }
    }

    private void initMaster(){
        if(masterIndex>0){
            if(master==null){
                if(prevAll!=null){
                    if(isTheSameRObjKey(prevAll.master.keyFields,prevAll.rObjIndex,rObjIndex)){
                        master=prevAll.master;
                        if(master.details.size()>0){
                            master.details.get(master.details.size()-1).next=this;
                            this.prev=master.details.get(master.details.size()-1);
                        }
                        master.details.add(this);
                    }else{
                        master=newMaster(rObjIndex,masterIndex-1);
                        prevAll.master.nextAll=master;
                        master.prevAll=prevAll.master;
                        if(isTheSameRObjKey(masterKeyFields.get(master.masterIndex-1),master.rObjIndex,master.prevAll.rObjIndex)){
                            master.prev=master.prevAll;
                            master.prev.next=master;
                        }
                        master.details=new ArrayList<JMExcelMD>();
                        master.details.add(this);
                        master.initMaster();
                    }
                }else{
                    master=newMaster(rObjIndex,masterIndex-1);
                    master.details=new ArrayList<JMExcelMD>();
                    master.details.add(this);
                    master.initMaster();
                }

            }
        }
    }

    private JMExcelMD newMaster(int rObjIndex, int masterIndex){
        //master=new JMExcelMD(xls,rObjs,masterKeyFields,masterTemplateSheetNames,masterTopOfDetails,
        //        masterFieldIds,masterIsFormulaDetail,cols, resultStartRow, resultSheetName);
        master.rObjIndex=rObjIndex;
        master.masterIndex=masterIndex;
        master.generalInit();
        return master;
    }

    private void initNextDetail(){
        initMaster();
        if(rObjIndex+1<rObjs.size()){
            //nextAll=new JMExcelMD(xls,rObjs, masterKeyFields,masterTemplateSheetNames,masterTopOfDetails,
            //        masterFieldIds,masterIsFormulaDetail,cols,resultStartRow,resultSheetName);
            nextAll.rObjIndex=rObjIndex+1;
            nextAll.masterIndex=masterIndex;
            nextAll.generalInit();
            nextAll.prevAll=this;

            if(isTheSameMasterRObjKey(rObjIndex,nextAll.rObjIndex)){
                next=nextAll;
                next.prev=this;
            }
            nextAll.initNextDetail();
        }
    }

    private boolean isTheSameRObjKey(List keyFields, int rObjIndex1, int rObjIndex2){
        boolean ret=true;
        for(int i=0;i<keyFields.size();i++){
            int keyField=(int)keyFields.get(i);
            jmoRowObject obj1=rObjs.get(rObjIndex1);
            jmoRowObject obj2=rObjs.get(rObjIndex2);
            ret=ret && (obj1.getValue(keyField)==obj2.getValue(keyField));
            if(!ret)return false;
        }
        return ret;
    }

    private boolean isTheSameMasterRObjKey(int rObjIndex1, int rObjIndex2){
        boolean ret=true;
        if(masterIndex<=0)return false;
        int ourMasterIndex=masterIndex-1;
        for(int i=0;i<masterKeyFields.get(ourMasterIndex).size();i++){
            int keyField=(int)masterKeyFields.get(ourMasterIndex).get(i);
            jmoRowObject obj1=rObjs.get(rObjIndex1);
            jmoRowObject obj2=rObjs.get(rObjIndex2);
            ret=ret && (obj1.getValue(keyField)==obj2.getValue(keyField));
            if(!ret)return false;
        }
        return ret;
    }

    private void generalInit(){
        keyFields=masterKeyFields.get(masterIndex);
        fieldIds=masterFieldIds.get(masterIndex);
        Sheet sheet=xls.getWorkbook().getSheet(masterTemplateSheetNames.get(masterIndex)); //masterSheetTemplate.get(masterIndex);

        Row row=sheet.getRow(resultStartRow);
        if (row==null){
            JmoFunctions.trace("ERROR ROW NULL");
            return;
        }
        cellStyles=new ArrayList<CellStyle>();
        for(int i=0;i<cols.size();i++){
            int cId=(int)cols.get(i);
            CellStyle cellStyle=sheet.getWorkbook().createCellStyle();
            Cell tmp=row.getCell(cId);
            if(tmp==null){
                JmoFunctions.trace("ERROR COLUMN NULL");
                return;
            }
            cellStyle.cloneStyleFrom(tmp.getCellStyle());
            cellStyles.add(cellStyle);
        }

    }

    private boolean isValid(){
        if(xls==null)return false;
        if(rObjs==null)return false;
        if(masterKeyFields==null)return false;
        if(masterTemplateSheetNames==null)return false;
        if(masterTopOfDetails==null)return false;
        if(masterFieldIds==null)return false;
        if(masterIsFormulaDetail==null)return false;
        if(cols==null)return false;
        if(resultSheetName==null || resultSheetName.equals(""))return false;
        if(xls.getWorkbook().getSheet(resultSheetName)==null)return false;
        if(masterKeyFields.size()+masterTemplateSheetNames.size()+masterTopOfDetails.size()+masterFieldIds.size()+
                masterIsFormulaDetail.size()!=masterKeyFields.size()*5)return false;
        for(int i=0;i<masterFieldIds.size();i++){
            if(masterFieldIds.get(i).size()!=masterIsFormulaDetail.get(i).size())return false;
        }
        for(int i=0;i<masterTemplateSheetNames.size();i++){
            if(xls.getWorkbook().getSheet(masterTemplateSheetNames.get(i))==null)return false;
        }
        return true;
    }

    private void removeTemplateSheets(){
        for(int i=0;i<masterTemplateSheetNames.size();i++){
            xls.getWorkbook().removeSheetAt(xls.getWorkbook().getSheetIndex(masterTemplateSheetNames.get(i)));
        }
    }
}
