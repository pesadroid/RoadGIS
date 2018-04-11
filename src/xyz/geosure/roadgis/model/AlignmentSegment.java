package xyz.geosure.roadgis.model;
/*
 * Renamed from Data2D.java
 * Data2D class define the horizontal line/curve design segments.
 * radius > 0 indicate a curve segment, and
 * radius <0 indicate a line segment
 *
 * Created on March 17, 2006, 12:08 PM
 * Modified 11/8/06
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
import java.awt.Color;

import com.vividsolutions.jts.geom.Point;

public class AlignmentSegment {
    private Point startPoint ;  // line segment start point or center of a curve. renamed by Felix Kiptum 09 April 2018 Previously, this was point1
    private Point endPoint ;  // line segment end point - rename by Felix Kiptum 09 April 2018. Previously, this was point2
    private Point midPoint ;  // line segment midpoint - added by Felix Kiptum 09 April 2018
    private Point segmentCentre ;  // line segment midpoint - added by Felix Kiptum 09 April 2018 :) sorry for the US folks, center ...
    private double tangentAngle ;  // tangent - added by Felix Kiptum 09 April 2018
    private byte tangentFlag ;  // tangent - added by Felix Kiptum 09 April 2018
    private double radius = -1 ;                // radius, -1  for line data
    private Color penColor ;
    private float penWidth ;
    private boolean selected ;      // flag to indicate if segment is selected
    private boolean deleted = false;       // flag to indicate if this segment is deleted, 4/21/06 added
    private Color highlightedColor ;    // complimentary color of penColor

    /** Creates a new instance of Data2D */
    public AlignmentSegment() {
        //startPoint = new Point(0f,0f) ;
        //endPoint = new Point(0f,0f) ;
        selected = false ;
        deleted = false ;
    }
    public void saveData(Point p1, Point p2, Color _penColor, float _penWidth) {
        startPoint = p1 ;
        endPoint = p2 ;
        radius = -1 ;
        penColor = _penColor ;
        penWidth = _penWidth ;
        selected = false ;
        highlightedColor = new Color(255 - penColor.getRed(), 255 - penColor.getGreen(), 
            255 - penColor.getBlue()) ;
    }
    public void saveData(Point p1 , Point p2, Point p3 , Color _penColor , float _penWidth) {
    	 startPoint = p1 ;
         midPoint =p2 ;
         endPoint = p3 ;
        radius = -1 ;
        penColor = _penColor ;
        penWidth = _penWidth ;
        selected = false ;
        highlightedColor = new Color(255 - penColor.getRed(), 255 - penColor.getGreen(), 255 - penColor.getBlue()) ;
    }   
    public void saveData( Point p1, float r , Color _penColor , float _penWidth ) {
        startPoint = p1 ;
        radius = r ;
        setMidPoint(null) ;
        endPoint = null ;
        penColor = _penColor ;
        penWidth = _penWidth ;
        selected = false ;
        highlightedColor = new Color(255 - penColor.getRed(), 255 - penColor.getGreen(), 255 - penColor.getBlue()) ;
    }
    
    public void updateCurveCenter( Point ptr) {
        startPoint = ptr ;
    }
    public Point getPoint1() {
        return startPoint ;
    }
    public Point getPoint2() {
        return endPoint ;
    }
    public double getRadius() {
        return radius;
    }
    // return pen color
    public Color getPenColor() {
        if (selected) {
            return highlightedColor ;
        } else {
            return penColor;
        }

    }
    // return pen width
    public float getPenWidth() {
        return penWidth ;
    }
    //edit line item end points
    public void modifyPoint(int id , Point newPoint){
        switch (id) {
            case 1:  //' point1
                startPoint = newPoint ;
                break;
            case 2:  //' point 2
                endPoint = newPoint ;
                break;
        }

    }
    // return select flag
    public boolean isSelected() {
        return selected ;
    }
    // toggle selected item
    public void selectItem() {
        if (selected) {
            selected = false ;
        } else {
            selected = true ;
        }
    }
    // select item, set item select flag
    public void setItemSelect(boolean state) {
        selected = state ;
    }
    // unselect item
    public void unSelectItem() {
        selected = false ;
    }
    // select item, set item select flag
    public void selectItemSet(boolean  state ){
        selected = state ;
    }
    // reset item data class
    public void reset() {
        startPoint = null;    // line segment start point or center of a curve
        endPoint = null;    // line segment end point
        radius = -1;                 // radius, -1  for line data
        penColor = Color.blue;
        penWidth = 2;
        selected = false;
        highlightedColor = Color.yellow;
    }
    // set line item start point
    public void setStartPoint(Point point){
        startPoint = point;
    }
    // set line item end point
    public void setEndPoint(Point point){
        endPoint = point;
    }
    //set curve item radius
    public void setRadius(double d ){
        radius = d ;
    }
    // set pen width
    public void setPenWidth(float pw ){
        penWidth = pw;
    }
    // set pen color
    public void setPenColor(Color pc ){
        penColor = pc;
        highlightedColor = new Color(255 - penColor.getRed(), 255 - penColor.getGreen(), 255 - penColor.getBlue());
    }
    // save line-curve tangent point to DB
    public void saveTangentAngle(byte flag , Point pt ){
        // calculate absolute angle of tangent point ref to center of circle
        double dx, dy ;
        double theta ;
        dx = pt.getX() - startPoint.getX() ;
        dy = pt.getY() - startPoint.getY();
        this.setTangentAngle(new Double(Math.atan2(dy, dx)));//.floatValue() ;  // [-pi, pi] in radian
        this.setTangentFlag(flag);
        
    }   // saveYangebtAngle
    // deleted selected item
    public void delete(){
        // outside draw boundary
        startPoint = null;   // line segment start point or center of a curve
        setMidPoint(null);  // line segment end point
        endPoint = null;  // line segment end point
        radius = -9999;                         // radius, -1  for line data
        selected = false;
        deleted = true;
    }
    // return item deleted flag
    public boolean isDeleted() {
        return deleted;
    }
    // reset item delete flag, undelete
    public void resetDeleteFlag() {
        deleted = false ;
    }
	public Point getMidPoint() {
		return midPoint;
	}
	public void setMidPoint(Point midPoint) {
		this.midPoint = midPoint;
	}
	public Point getSegmentCentre() {
		return segmentCentre;
	}
	public void setSegmentCentre(Point segmentCentre) {
		this.segmentCentre = segmentCentre;
	}
	public double getTangentAngle() {
		return tangentAngle;
	}
	public void setTangentAngle(double tangentAngle) {
		this.tangentAngle = tangentAngle;
	}
	public byte getTangentFlag() {
		return tangentFlag;
	}
	public void setTangentFlag(byte tangentFlag) {
		this.tangentFlag = tangentFlag;
	}
	public Point getStartPoint() {
		return startPoint;
	}

	public Point getEndPoint() {
		return midPoint;
	}
}
