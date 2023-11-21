package com.ejs.birthchart.data;

public class dataMoonCal {

    int typeMoon, distance, moonAge, moonAgePorc, day, moonPhase;
    int moonPhasedouble;
    double illumination;
    String positionMoon, sunrise, sunset, moonrise, moonset, morningbluehour, morninggoldenhour, eveningbluehour, eveninggoldenhour;

    public dataMoonCal(int moonPhasedouble, int day, int distance, double illumination, int moonAge, int moonAgePorc, int moonPhase, int typeMoon, String positionMoon, String sunrise, String sunset, String moonrise, String moonset, String morningbluehour, String morninggoldenhour, String eveningbluehour, String eveninggoldenhour) {
        this.distance = distance;
        this.day = day;
        this.illumination = illumination;
        this.moonAge = moonAge;
        this.moonAgePorc = moonAgePorc;
        this.moonPhase = moonPhase;
        this.moonPhasedouble = moonPhasedouble;
        this.typeMoon = typeMoon;
        this.positionMoon = positionMoon;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.moonrise = moonrise;
        this.moonset = moonset;
        this.morningbluehour = morningbluehour;
        this.morninggoldenhour = morninggoldenhour;
        this.eveningbluehour = eveningbluehour;
        this.eveninggoldenhour = eveninggoldenhour;
    }

    public dataMoonCal() {
    }

    public int getDay(){return day;}

    public void setDay (int day){this.day = day;}

    public int getDistance(){return distance;}

    public void setDistance (int distance){this.distance = distance;}

    public double getIllumination(){return illumination;}

    public void setIllumination(double illumination){this.illumination=illumination;}

    public int getMoonAge(){return moonAge;}

    public void setMoonAge (int moonAge){this.moonAge = moonAge;}

    public int getMoonAgePorc(){return moonAgePorc;}

    public void setMoonAgePorc(int moonAgePorc){this.moonAgePorc=moonAgePorc;}

    public int getMoonPhase(){return moonPhase;}

    public void setMoonPhase(int moonPhase){this.moonPhase = moonPhase;}

    public int getMoonPhasedouble(){return moonPhasedouble;}

    public void setMoonPhasedouble(int moonPhasedouble){this.moonPhasedouble = moonPhasedouble;}

    public int getTypeMoon(){return typeMoon;}

    public void setTypeMoon(int typeMoon){this.typeMoon = typeMoon;}

    public String getPositionMoon(){return positionMoon;}

    public void setPositionMoon(String positionMoon){this.positionMoon = positionMoon;}

    public String getSunrise(){return sunrise;}

    public void setSunrise(String sunrise){this.sunrise = sunrise;}

    public String getSunSet(){return sunset;}

    public void setSunSet(String sunset){this.sunset = sunset;}

    public String getMoonRise(){return moonrise;}

    public void setMoonRise(String moonrise){this.moonrise = moonrise;}

    public String getMoonSet(){return moonset;}

    public void setMoonSet(String moonset){this.moonset = moonset;}

    public void setMorningBlueHour(String morningbluehour){this.morningbluehour = morningbluehour;}
    public String getMorningbluehour(){return morningbluehour;}

    public String getMorninggoldenhour(){return morninggoldenhour;}

    public void setMorninggoldenhour(String morninggoldenhour){this.morninggoldenhour = morninggoldenhour;}

    public String getEveningbluehour(){return eveningbluehour;}

    public void setEveningbluehour(String eveningbluehour){this.eveningbluehour = eveningbluehour;}

    public String getEveninggoldenhour(){return eveninggoldenhour;}

    public void setEveninggoldenhour(String eveninggoldenhour){this.eveninggoldenhour = eveninggoldenhour;}

}
