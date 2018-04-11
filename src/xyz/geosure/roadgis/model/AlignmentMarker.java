package xyz.geosure.roadgis.model;

import com.vividsolutions.jts.geom.Point;

/*
 * MarkerDB.java
 * Station database class.
 *
 * Created on March 17, 2006, 1:23 PM
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

public class AlignmentMarker {
    private Point location  ; // marker/station location
    private double elevation = -1 ;  // elevation data in ft or meter
    private MarkerType type = MarkerType.NONE ;   // 1-line, 2-curve, 3-tangent//Modified from byte to ENUM by Felix, 09/Apr/2018
    private double distance ;  // distance from starting landmark
    private int parent ;     // index/id of parent segment from hRoadData DB
    private double grade ;     // grade from last to current landmark
    private boolean PVTnC_Overlap = false ;  // 4/4/06 added

	public enum MarkerType {NONE, LINE, CURVE, TANGENT };
	

    public AlignmentMarker() {
    	
    }
    
    /** Creates a new instance of MarkerDB 
     * @param index 
     * @param elev 
     * @param loc */
    public AlignmentMarker(Point loc, double elev, int index) {
        location = loc;
        elevation = elev;
        type = MarkerType.NONE;
        parent = index;
    }
    public AlignmentMarker(Point loc, double ele, int index, MarkerType seg_type) {
		
        location = loc;
        elevation = ele;
        type = seg_type;
        parent = index;
    }

    public Point getLocation(){
        return location;
    }
    public double getElevation(){
        return elevation;
    }
    public MarkerType getSegmentType() {
        return type;
    }
    public AlignmentMarker reset(){    // reset station data
        location = null;
        elevation = -1;
        type = MarkerType.NONE;
        return this;
    }
    public AlignmentMarker setLocation(Point point){
        location = point;
        return this;
    }
    public AlignmentMarker setElevation(double d ){
        elevation = d;
        return this;
    }
    public AlignmentMarker setSegmentType(MarkerType stype ){
        type = stype;
        return this;
    }
    public AlignmentMarker setDistance(double accu_dist){
        distance = accu_dist;
        return this;
    }
    public double getDistance() {
        return distance;
    }
    public AlignmentMarker setParentIndex(int parentId ) {
        parent = parentId;
        return this;
    }
    public int getParentIndex() {
        return parent;
    }
    public AlignmentMarker setGrade(double grade2 ){
        grade = grade2;
        return this;
    }
    public double getGrade() {
        return grade;
    }

	public boolean isPointOfVerticalTangentAndCurvatureOverlapping() {
		return PVTnC_Overlap;
	}

	public AlignmentMarker setPVTnCurvatureOverlap(boolean pVTnC_Overlap) {
		PVTnC_Overlap = pVTnC_Overlap;
        return this;
	}
}
