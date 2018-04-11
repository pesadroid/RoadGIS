package xyz.geosure.roadgis.utils;
/*
 * Quaternion.java
 *
 * Created on March 17, 2006, 2:32 PM
 */

import xyz.geosure.roadgis.model.Vector3D;

public class Vector {
    public Vector3D vectPart ;
    public double realPart ;
  
    /** Creates a new instance of Quaternion */
    public Vector() {
    }
    
    public Vector(Vector3D vec, double angle) {
        vectPart = vec ;
        realPart = angle ;
    }
    public Vector QQMul(Vector q2) {
        Vector r ;
        Vector3D tempV ;
        r = new Vector(vectPart.vCross(q2.vectPart), this.realPart * q2.realPart - this.vectPart.vDot(q2.vectPart)) ;
        tempV = this.vectPart.vScale(q2.realPart);
        r.vectPart = tempV.vAdd(r.vectPart);
        tempV = q2.vectPart.vScale(this.realPart);
        r.vectPart = tempV.vAdd(r.vectPart);
        return r;
    }
    public String toAxisAngle() {
        double halfAngle, sinHalfAngle ;
        double rotAngle ;
        Vector3D rotAxis ;
        halfAngle = new Double(Math.acos(this.realPart));//.floatValue();
        sinHalfAngle = new Double(Math.sin(halfAngle));//.floatValue();
        rotAngle = 2.0f * halfAngle;
        if ( (sinHalfAngle < 0.00000001) && sinHalfAngle > -0.00000001 ){
            rotAxis = new Vector3D(1, 0, 0);
        } else {
            sinHalfAngle = 1f / sinHalfAngle;
            rotAxis = this.vectPart.vScale(sinHalfAngle);
        }
        return rotAxis.toStr() + Double.toString(rotAngle) ;
    }

}
