package com.ejs.birthchart.data;

public class ChartEntry {
    private int id;
    private String name;
    private int chartType;
    private String fecha1;
    private String timezone1;
    private String fecha2;
    private String timezone2;
    private int locationType;
    private String ciudadPais;
    private String location;
    private String house;
    private String zodiacal;

    public ChartEntry(int id, String name, int chartType, String fecha1, String timezone1,
                      String fecha2, String timezone2, int locationType, String ciudadPais,
                      String location, String house, String zodiacal) {
        this.id = id;
        this.name = name;
        this.chartType = chartType;
        this.fecha1 = fecha1;
        this.timezone1 = timezone1;
        this.fecha2 = fecha2;
        this.timezone2 = timezone2;
        this.locationType = locationType;
        this.ciudadPais = ciudadPais;
        this.location = location;
        this.house = house;
        this.zodiacal = zodiacal;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return name;
    }

    public int getChartType() {
        return chartType;
    }

    public String getFecha1() {
        return fecha1;
    }

    public String getTimezone1() {
        return timezone1;
    }

    public String getFecha2() {
        return fecha2;
    }

    public String getTimezone2() {
        return timezone2;
    }

    public int getLocationType() {
        return locationType;
    }

    public String getCiudadPais() {
        return ciudadPais;
    }

    public String getLocation() {
        return location;
    }

    public String getHouse() {
        return house;
    }

    public String getZodiacal() {
        return zodiacal;
    }

    public String toString() {
        return name;
    }
}