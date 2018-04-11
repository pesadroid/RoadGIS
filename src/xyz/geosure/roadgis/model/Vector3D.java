package xyz.geosure.roadgis.model;
/*
 * Vector3D.java
 * 3D Vector class. Used to create 3D model.
 *
 * Created on March 17, 2006, 2:17 PM
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
import java.lang.Math ;

public class Vector3D {
    private double X ;
    private double Y ;
    private double Z ;
    /** Creates a new instance of Vector3D */
    public Vector3D() {
    }
    public Vector3D(double _x, double _y, double _z) {
        X = _x ;
        Y = _y ;
        setZ(_z) ;
    }
    public Vector3D vAdd(Vector3D vec) {
        Vector3D v ;
        v = new Vector3D(X + vec.X, Y + vec.Y, getZ() + vec.getZ()) ;
        return v ;
    }
    public Vector3D vSub(Vector3D vec) {
       Vector3D v ;
        v = new Vector3D(X - vec.X, Y - vec.Y, getZ() - vec.getZ());
        return v;
    }
    public double vDot(Vector3D vec) {
        return (X * vec.X + Y * vec.Y + getZ() * vec.getZ());
    }
    public Vector3D vCross(Vector3D vec) {
        Vector3D v;
        v = new Vector3D((Y * vec.getZ() - getZ() * vec.Y), (getZ() * vec.X - X * vec.getZ()), (X * vec.Y - Y * vec.X));
        return v;
    }
    public Vector3D vScale(double sf ) {
        return new Vector3D(sf * X, sf * Y, sf * getZ());
    }
    public double vLen() {
        return new Double(Math.sqrt(X*X + Y*Y + getZ()*getZ())).doubleValue() ;
    }
    public Vector3D vUnit() {
        double sf ;
        sf = 1 / vLen() ;
        return new Vector3D(sf * X, sf * Y, sf * getZ()) ;
    }
    public String toStr() {
        return Double.toString(X) + " " + Double.toString(Y) + " " + Double.toString(getZ()) + " " ;
    }
	public double getX() {
		return X;
	}
	public void setX(double z) {
		X = z;
	}

	public double getY() {
		return Y;
	}
	public void setY(double z) {
		Y = z;
	}

	public double getZ() {
		return Z;
	}
	public void setZ(double z) {
		Z = z;
	}
}


