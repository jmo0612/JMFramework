package com.thowo.jmframework.db;

import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimi on 6/2/2017.
 */

public class jmoResultSet {
    private boolean isLocal;
    ResultSet rs;
    Cursor c;

    public jmoResultSet(boolean isLocal, ResultSet rs, Cursor c){
        this.rs=rs;
        this.c=c;
        this.isLocal=isLocal;
    }

    public int getColumnCount(){
        int ret=-1;
        if(this.isLocal){
            try {
                ret=c.getColumnCount();
            } catch (Exception e) {
                ret=-1;
            }
        }else {
            try {
                ret= rs.getMetaData().getColumnCount();
            } catch (SQLException e) {
                ret=-1;
            }
        }
        return ret;
    }

    public int getRowCount(){
        int ret=0;
        if(this.isLocal){
            try {
                ret=c.getCount();
            } catch (Exception e) {
                ret=0;
            }

        }else {
            try {
                ret= rs.getRow();
            } catch (SQLException e) {
                ret=0;
            }
        }
        return ret;
    }

    @SuppressWarnings("deprecation")
    private int convertedDataTypeGinger(String columnName){
        int ret=-1;
        CursorWrapper cw = (CursorWrapper)c;

        Class<?> cursorWrapper = CursorWrapper.class;
        Field mCursor = null;
        try {
            mCursor = cursorWrapper.getDeclaredField("mCursor");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mCursor.setAccessible(true);
        AbstractWindowedCursor abstractWindowedCursor = null;
        try {
            abstractWindowedCursor = (AbstractWindowedCursor)mCursor.get(cw);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        CursorWindow cursorWindow = abstractWindowedCursor.getWindow();
        int pos = abstractWindowedCursor.getPosition();
        if (cursorWindow.isLong(pos, c.getColumnIndex(columnName))) {
            ret=jmoRowObject.JMO_INT;
        } else if (cursorWindow.isFloat(pos, c.getColumnIndex(columnName))) {
            ret=jmoRowObject.JMO_DOUBLE;
        } else if (cursorWindow.isString(pos, c.getColumnIndex(columnName))) {
            ret=jmoRowObject.JMO_STRING;
        } else if (cursorWindow.isBlob(pos, c.getColumnIndex(columnName))) {
            ret=jmoRowObject.JMO_OBJECT;
        }else{
            ret=-1;
        }
        return ret;
    }

    public int convertedDataType(String columnName){
        int ret=-1;
        if(this.isLocal){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                try {
                    int cT=c.getType(c.getColumnIndex(columnName));
                    if(cT==Cursor.FIELD_TYPE_BLOB){
                        ret=jmoRowObject.JMO_OBJECT;
                    }else if(cT==Cursor.FIELD_TYPE_FLOAT){
                        ret=jmoRowObject.JMO_DOUBLE;
                    }else if(cT==Cursor.FIELD_TYPE_INTEGER){
                        ret=jmoRowObject.JMO_INT;
                    }else if(cT==Cursor.FIELD_TYPE_STRING){
                        ret=jmoRowObject.JMO_STRING;
                    }else{
                        ret=-1;
                    }
                } catch (Exception e) {
                    ret=-1;
                }
            }else{
                ret=convertedDataTypeGinger(columnName);
            }
        }else {
            try {
                int rsT=rs.getMetaData().getColumnType(rs.findColumn(columnName));
                if(rsT== Types.CHAR || rsT== Types.VARCHAR || rsT== Types.LONGVARCHAR){
                    ret=jmoRowObject.JMO_STRING;
                }else if(rsT== Types.NUMERIC || rsT== Types.DECIMAL){
                    ret=jmoRowObject.JMO_DOUBLE;
                }
                else if(rsT== Types.BIT){
                    ret=jmoRowObject.JMO_INT;
                }else if(rsT== Types.TINYINT){
                    ret=jmoRowObject.JMO_INT;
                }else if(rsT== Types.SMALLINT){
                    ret=jmoRowObject.JMO_INT;
                }else if(rsT== Types.INTEGER){
                    ret=jmoRowObject.JMO_INT;
                }else if(rsT== Types.BIGINT){
                    ret=jmoRowObject.JMO_INT;
                }else if(rsT== Types.REAL){
                    ret=jmoRowObject.JMO_DOUBLE;
                }else if(rsT== Types.FLOAT || rsT== Types.DOUBLE){
                    ret=jmoRowObject.JMO_DOUBLE;
                }else if(rsT== Types.BINARY || rsT== Types.VARBINARY || rsT== Types.LONGVARBINARY){
                    ret=jmoRowObject.JMO_OBJECT;
                }else if(rsT== Types.DATE){
                    ret=jmoRowObject.JMO_OBJECT;
                }else if(rsT== Types.TIME){
                    ret=jmoRowObject.JMO_OBJECT;
                }else if(rsT== Types.TIMESTAMP){
                    ret=jmoRowObject.JMO_OBJECT;
                }else{
                    ret=-1;
                }
            } catch (SQLException e) {
                ret=-1;
            }
        }
        return ret;
    }

    @SuppressWarnings("deprecation")
    public Object getValueGinger(String columnName) {
        Object ret = null;
        CursorWrapper cw = (CursorWrapper)c;

        Class<?> cursorWrapper = CursorWrapper.class;
        Field mCursor = null;
        try {
            mCursor = cursorWrapper.getDeclaredField("mCursor");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mCursor.setAccessible(true);
        AbstractWindowedCursor abstractWindowedCursor = null;
        try {
            abstractWindowedCursor = (AbstractWindowedCursor)mCursor.get(cw);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        CursorWindow cursorWindow = abstractWindowedCursor.getWindow();
        int pos = abstractWindowedCursor.getPosition();
        if (cursorWindow.isLong(pos, c.getColumnIndex(columnName))) {
            ret=c.getInt(c.getColumnIndex(columnName));
        } else if (cursorWindow.isFloat(pos, c.getColumnIndex(columnName))) {
            ret=c.getDouble(c.getColumnIndex(columnName));
        } else if (cursorWindow.isString(pos, c.getColumnIndex(columnName))) {
            ret=c.getString(c.getColumnIndex(columnName));
        } else if (cursorWindow.isBlob(pos, c.getColumnIndex(columnName))) {
            ret=c.getBlob(c.getColumnIndex(columnName));
        }else{
            ret=null;
        }
        return ret;
    }

    public Object getValue(String columnName){
        Object ret=null;
        if(this.isLocal){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                try {
                    int cT=c.getType(c.getColumnIndex(columnName));
                    if(cT==Cursor.FIELD_TYPE_BLOB){
                        ret=c.getBlob(c.getColumnIndex(columnName));
                    }else if(cT==Cursor.FIELD_TYPE_FLOAT){
                        ret=c.getDouble(c.getColumnIndex(columnName));
                    }else if(cT==Cursor.FIELD_TYPE_INTEGER){
                        ret=c.getInt(c.getColumnIndex(columnName));
                    }else if(cT==Cursor.FIELD_TYPE_STRING){
                        ret=c.getString(c.getColumnIndex(columnName));
                    }else{
                        ret=null;
                    }
                } catch (Exception e) {
                    ret=null;
                }
            }else{
                ret=getValueGinger(columnName);
            }

        }else {
            try {
                int rsT=rs.getMetaData().getColumnType(rs.findColumn(columnName));
                if(rsT== Types.CHAR || rsT== Types.VARCHAR || rsT== Types.LONGVARCHAR){
                    ret=rs.getString(columnName);
                }else if(rsT== Types.NUMERIC || rsT== Types.DECIMAL){
                    ret=rs.getBigDecimal(columnName);
                }
                else if(rsT== Types.BIT){
                    ret=rs.getBoolean(columnName);
                }else if(rsT== Types.TINYINT){
                    ret=rs.getByte(columnName);
                }else if(rsT== Types.SMALLINT){
                    ret=rs.getShort(columnName);
                }else if(rsT== Types.INTEGER){
                    ret=rs.getInt(columnName);
                }else if(rsT== Types.BIGINT){
                    ret=rs.getLong(columnName);
                }else if(rsT== Types.REAL){
                    ret=rs.getFloat(columnName);
                }else if(rsT== Types.FLOAT || rsT== Types.DOUBLE){
                    ret=rs.getDouble(columnName);
                }else if(rsT== Types.BINARY || rsT== Types.VARBINARY || rsT== Types.LONGVARBINARY){
                    ret=rs.getBytes(columnName);
                }else if(rsT== Types.DATE){
                    ret=rs.getDate(columnName);
                }else if(rsT== Types.TIME){
                    ret=rs.getTime(columnName);
                }else if(rsT== Types.TIMESTAMP){
                    ret=rs.getTimestamp(columnName);
                }else{
                    ret=null;
                }
            } catch (SQLException e) {
                ret=null;
            }
        }
        return ret;
    }

    public String getColumnName(int columnIndex){
        String ret=null;
        if(this.isLocal){
            try {
                ret=c.getColumnName(columnIndex);
            } catch (Exception e) {
                ret=null;
            }
        }else {
            try {
                ret= rs.getMetaData().getColumnName(columnIndex);
            } catch (SQLException e) {
                ret=null;
            }
        }
        return ret;
    }

    public String getString(String columnName){
        String ret=null;
        if(this.isLocal){
            try {
                ret=c.getString(c.getColumnIndex(columnName));
            } catch (Exception e) {
                ret=null;
            }
        }else {
            try {
                ret= rs.getString(columnName);
            } catch (SQLException e) {
                ret=null;
            }
        }
        return ret;
    }
    public int getInt(String columnName){
        int ret;
        if(this.isLocal){
            try {
                ret=c.getInt(c.getColumnIndex(columnName));
            } catch (Exception e) {
                ret=0;
            }

        }else {
            try {
                ret= rs.getInt(columnName);
            } catch (SQLException e) {
                ret=0;
            }
        }
        return ret;
    }
    public Double getDouble(String columnName){
        Double ret;
        if(this.isLocal){
            try {
                ret=c.getDouble(c.getColumnIndex(columnName));
            } catch (Exception e) {
                ret=0.0;
            }


        }else {
            try {
                ret= rs.getDouble(columnName);
            } catch (SQLException e) {
                ret=0.0;
            }
        }
        return ret;
    }

    public boolean next(){
        boolean ret=false;
        if(this.isLocal){
            try {
                ret=c.moveToNext();
            } catch (Exception e) {
                ret=false;
            }

        }else {
            try {
                ret=rs.next();
            } catch (SQLException e) {
                ret=false;
            }
        }
        return ret;
    }

    public boolean prev(){
        boolean ret=false;
        if(this.isLocal){
            try {
                ret=c.moveToPrevious();
            } catch (Exception e) {
                ret=false;
            }

        }else {
            try {
                ret=rs.previous();
            } catch (SQLException e) {
                ret=false;
            }
        }
        return ret;
    }

    public boolean first(){
        boolean ret=false;
        if(this.isLocal){
            try {
                ret=c.moveToFirst();
            } catch (Exception e) {
                ret=false;
            }

        }else {
            try {
                ret=rs.first();
            } catch (SQLException e) {
                ret=false;
            }
        }
        return ret;
    }
    public boolean last(){
        boolean ret=false;
        if(this.isLocal){
            try {
                ret=c.moveToLast();
            } catch (Exception e) {
                ret=false;
            }

        }else {
            try {
                ret=rs.last();
            } catch (SQLException e) {
                ret=false;
            }
        }
        return ret;
    }

}
