package com.hailin.zconfig.client;

import com.google.common.collect.Table;

import java.util.Date;


public interface QTable extends Table<String, String, String> {

    String getString(String row, String column);

    boolean getBoolean(String row, String column);

    boolean getBoolean(String row, String column, boolean defaultValue);

    byte getByte(String row, String column);

    byte getByte(String row, String column, byte defaultValue);

    short getShort(String row, String column);

    short getShort(String row, String column, short defaultValue);

    int getInt(String row, String column);

    int getInt(String row, String column, int defaultValue);

    long getLong(String row, String column);

    long getLong(String row, String column, long defaultValue);

    float getFloat(String row, String column);

    float getFloat(String row, String column, float defaultValue);

    double getDouble(String row, String column);

    double getDouble(String row, String column, double defaultValue);

    Date getDate(String row, String column);

    Date getDate(String row, String column, Date defaultValue);
}
