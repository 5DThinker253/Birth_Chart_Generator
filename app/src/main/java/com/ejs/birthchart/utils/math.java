package com.ejs.birthchart.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class math {
    /**
     * Funcion para redondear y limitar la cantidad de decimales
     *
     * @param value valor original en formato double
     * @param numberDecimal cantidad de decimales
     * @return devuelve el valor en formato double
     */
    public static double roundDecimal(double value, int numberDecimal){
        return BigDecimal.valueOf(value).setScale(numberDecimal, RoundingMode.HALF_UP).doubleValue();
    }
    /**
     * Funcion para redondear y limitar la cantidad de decimales
     *
     * @param value valor original en formato float
     * @param numberDecimal cantidad de decimales
     * @return devuelve el valor en formato float
     */
    public static float roundDecimal(float value, int numberDecimal){
        return BigDecimal.valueOf(value).setScale(numberDecimal, RoundingMode.HALF_UP).floatValue();
    }
    /**
     * Funcion para redondear y limitar la cantidad de decimales
     *
     * @param value valor original en formato long
     * @param numberDecimal cantidad de decimales
     * @return devuelve el valor en formato long
     */
    public static long roundDecimal(long value, int numberDecimal){
        return BigDecimal.valueOf(value).setScale(numberDecimal, RoundingMode.HALF_UP).longValue();
    }
}
