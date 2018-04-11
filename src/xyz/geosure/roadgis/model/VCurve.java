package xyz.geosure.roadgis.model;
/*
 * VCurve.java
 * Vertical curve database.
 *
 * Created on March 17, 2006, 1:41 PM
 */

/**
 * @author  Chen-Fu Liao
 * Sr. Systems Engineer
 * ITS Institute, ITS Laboratory
 * Center For Transportation Studies
 * University of Minnesota
 * 200 Transportation and Safety Building
 * 511 Washington Ave. SE
 * Minneapolis, MN 55455
 */
public class VCurve {
    private double curveLen ; // linear curve length
    private double PVC ;   // point of the vertical curve (initial point)
    private double PVC_e ; // elevation at PVC
    private double PVC_dist ;  // distance of PVC from starting point
    private double PVT ;   // point of vertical tangent (final point of the vertical curve)
    private double PVT_e ; // elevation at PVT
    private double PVT_dist ;  // distance of PVT from starting point
    private double PVI ;   // point of vertical intersection
    private double PVI_e ; // elevation at PVI
    private double ele_minmax ;     // min/max elevation of the vertical curve
    private double dist_Eminmax ;   // location where the min/max elevation occurrs
    private double para_a ;
    private double para_b ;    // y=ax^2+bx+c, c=pvc_e
    private double Grade1 ;    // grade1
    private double Grade2 ;    // grade2
    
    private double minVCurveLen;
    /** Creates a new instance of vCurve */
    
    public VCurve(double minCurLen){
        minVCurveLen = minCurLen ;
    }
    public void setCurveLen(double val , double ssd ){
        // make sure the curve length is >= stopping sight distance
        if (val < ssd) {
            curveLen = ssd;
        } else {
            curveLen = val;
        }
        System.out.println(curveLen + "?" + val + ":" + ssd + "<");
        // round the curve to 10' ?
        // check min vertical curve limit
        if (curveLen < minVCurveLen) { 
            curveLen = minVCurveLen;
        }
    }

    // check min curve length for crest curve
    public double checkCrestLm(int unit, double _A, double _SSD, double _Len) {
        double val = 0f ;
        double constant = 0f ;
        if (unit==1) {  // US unit
            constant = 2158f ;
        } else if (unit==2) {
            // Metric
            constant = 658f ;
        }
        if (_SSD<_Len) {
            val = _SSD*_SSD*_A/constant ;
        } else {
            val = 2*_SSD-constant/_A ;
        }
        return val ;
    }
    
    // check min curve length for sag curve
    public double checkSagLm(int unit, double _A, double _SSD, double _Len) {
        double val = 0f ;
        double constant = 0f ;
        if (unit==1) {  // US unit
            constant = 400f ;
        } else if (unit==2) {
            // Metric
            constant = 120f ;
        }
        if (_SSD<_Len) {
            val = _SSD*_SSD*_A/(constant+3.5f*_SSD) ;
        } else {
            val = 2*_SSD-(constant+3.5f*_SSD)/_A ;
        }
        return val ;
    }

    public void setPVC(double val ){
        PVC = val;
    }
    public void setPVC_Elevation(double val ){
        PVC_e = val;
    }
    public void setPVC_Distance(double dist ){
        PVC_dist = dist;
    }
    public void setPVT(double val){
        PVT = val;
    }
    public void setPVT_Elevation(double val ){
        PVT_e = val;
    }
    public void setPVT_Distance(double val ){
        PVT_dist = val;
    }
    public void setPVI(double val ){
        PVI = val;
        PVC = PVI - 0.5f * curveLen;
        PVT = PVI + 0.5f * curveLen;
    }
    public void setPVI_e(double pvi_elevation ){
        PVI_e = pvi_elevation;
    }
    public void setPara_a(double val ){
        para_a = val;
    }
    public void setPara_b(double val ){
        para_b = val;
    }
    public void set_G1(double val ){
        Grade1 = val;
    }
    public void set_G2(double val ){
        Grade2 = val;
    }
    public void calcPVI(double pvi_elevation, double g1, double g2 ){
        Grade1 = g1;
        Grade2 = g2;
        PVI_e = pvi_elevation;
        PVC_e = PVI_e - 0.5f * curveLen * g1;
        PVT_e = PVI_e + 0.5f * curveLen * g2;
        // calculate min max elevations of the curve and their locations
        double dx ;
        dx = -g1 * curveLen / (g2 - g1);
        if (dx > curveLen) {
            dx = curveLen;
        } else if (dx < 0 ) {
            dx = 0;
        }
        para_a = 0.5f * (g2 - g1) / curveLen;
        para_b = g1;
        ele_minmax = PVC_e + para_b * dx + para_a * dx * dx;
        dist_Eminmax = dx + PVC;
    }
    
    public double getDX_Elevation(double dx ){
        return PVC_e + para_b * dx + para_a * dx * dx;
    }
    public void setMinMaxElevation(double val ) {
        ele_minmax = val;
    }
    public void setMinMaxEleDist(double val ){
        dist_Eminmax = val;
    }

    public double getCurveLen() {
        return curveLen;
    }
    public double getPVC() {
        return PVC;
    }
    public double getPVC_Elevation() {
        return PVC_e;
    }
    public double getPVC_Distance() {
        return PVC_dist;
    }
    public double getPVT() {
        return PVT;
    }
    public double getPVT_Elevation() {
        return PVT_e;
    }
    public double getPVT_Distance() {
        return PVT_dist;
    }
    public double getPVI() {
        return PVI;
    }
    public double getPVI_e() {
        return PVI_e;
    }
    public double getMinMaxElevation() {
        return ele_minmax;
    }
    public double getMinMaxEleDist() {
        return dist_Eminmax;
    }
    public double getPara_a() {
        return para_a;
    }
    public double getPara_b() {
        return para_b;
    }
    public double getPara_c() {
        return PVC_e;
    }
    public double get_G1() {
        return Grade1;
    }
    public double get_G2() {
        return Grade2;
    }
}
