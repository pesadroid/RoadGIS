package xyz.geosure.roadgis.utils;

public class ConversionUtils {

	public  static int CInt(double val){
		return new Double(val).intValue();
	}
	public  static  int CInt(float val){
		return new Float(val).intValue();
	}
	public  static int CInt(String str){
		return new Float(str).intValue();
	}
	public static  float CFloat(String str){
		return new Float(str).floatValue();
	}   
	public static String CStr(double d){
		return new Float(d).toString();
	}   
	public static String CStr(int val){
		return new Integer(val).toString();
	}   
 
}
