package xyz.geosure.roadgis.model;
/*
 * RoadGeoDB.java
 * This class is used to store road geometry info every 10 ft or 10 meter
 * that will later be used to create 3D VRML model DB
 *
 * Created on March 17, 2006, 1:33 PM
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

public class RoadGeoDB {
	private double X ;          // X coordinate
	
	private double Y ;          // Y coordinate
	private double elevation ;        // Elevation
	private double superElevation ;         // superelevation

	/** Creates a new instance of RoadGeoDB */
	public RoadGeoDB() {
	}
	public RoadGeoDB(double _x, double _y, double _ele,double _se ){
		X = _x;
		Y = _y;
		elevation = _ele;
		superElevation = _se;
	}
	public void Load(double d, double e, double f,double _se ){
		X = d;
		Y = e;
		elevation = f;
		superElevation = _se;
	}
	
	public double getX() {
		return X;
	}
	public void setX(double x) {
		X = x;
	}
	public double getY() {
		return Y;
	}
	public double getElevation() {
		return elevation;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public double getSuperElevation() {
		return superElevation;
	}
	public void setSuperElevation(double superElevation) {
		this.superElevation = superElevation;
	}
	public void setY(double y) {
		Y = y;
	}
	public String toTextString() {
		String str ;
		str = Double.toString(X) + "," + Double.toString(Y) + "," + Double.toString(elevation) + "," + Double.toString(superElevation) ;
		return str;
	}
}
