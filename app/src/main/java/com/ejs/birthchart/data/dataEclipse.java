package com.ejs.birthchart.data;

import io.github.cosinekitty.astronomy.EclipseEvent;
import io.github.cosinekitty.astronomy.EclipseKind;
import io.github.cosinekitty.astronomy.Time;

public class dataEclipse {

    Double Obscuration, Latitude, Longitude, Distance, SdPartial, SdPenum, SdTotal;
    EclipseKind Kind;
    Time Peak;
    EclipseEvent PartialBegin, PartialEnd, TotalBegin, TotalEnd;
    int image;

    public dataEclipse(int image, Double Obscuration, Double Latitude, Double Longitude, Double Distance, EclipseEvent TotalEnd, EclipseEvent PartialBegin, EclipseEvent PartialEnd, EclipseEvent TotalBegin, EclipseKind Kind, Time Peak, Double SdPartial, Double SdPenum, Double SdTotal) {
        this.Obscuration = Obscuration;
        this.image = image;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Distance = Distance;
        this.PartialBegin = PartialBegin;
        this.PartialEnd = PartialEnd;
        this.TotalBegin = TotalBegin;
        this.TotalEnd = TotalEnd;
        this.Kind = Kind;
        this.Peak = Peak;
        this.SdPartial = SdPartial;
        this.SdPenum = SdPenum;
        this.SdTotal = SdTotal; 
    }

    public dataEclipse() {
    }

    public int getImage(){return image;}
    public void setImage (int image){this.image = image;}

    public Double getObscuration(){return Obscuration;}
    public void setObscuration (Double Obscuration){this.Obscuration = Obscuration;}

    public Double getDistance(){return Distance;}
    public void setDistance (Double Distance){this.Distance = Distance;}

    public Double getLatitude(){return Latitude;}
    public void setILatitude(Double Latitude){this.Latitude=Latitude;}

    public Double getLongitude(){return Longitude;}
    public void setLongitude (Double Longitude){this.Longitude = Longitude;}

    public Double getSdPartial(){return SdPartial;}
    public void setSdPartial(Double SdPartial){this.SdPartial=SdPartial;}

    public Double getSdPenum(){return SdPenum;}
    public void setSdPenum(Double SdPenum){this.SdPenum = SdPenum;}

    public Double getSdTotal(){return SdTotal;}
    public void setSdTotal(Double SdTotal){this.SdTotal = SdTotal;}

    public EclipseEvent getPartialBegin(){return PartialBegin;}
    public void setPartialBegin(EclipseEvent PartialBegin){this.PartialBegin = PartialBegin;}

    public EclipseEvent getPartialEnd(){return PartialEnd;}
    public void setPartialEnd(EclipseEvent PartialEnd){this.PartialEnd = PartialEnd;}

    public EclipseEvent getTotalBegin(){return TotalBegin;}
    public void setTotalBegin(EclipseEvent TotalBegin){this.TotalBegin = TotalBegin;}

    public EclipseEvent getTotalEnd(){return TotalEnd;}
    public void setTotalEnd(EclipseEvent TotalEnd){this.TotalEnd = TotalEnd;}

    public EclipseKind getKind(){return Kind;}
    public void setKind(EclipseKind Kind){this.Kind = Kind;}

    public Time getPeak(){return Peak;}
    public void setPeak(Time Peak){this.Peak = Peak;}
}
