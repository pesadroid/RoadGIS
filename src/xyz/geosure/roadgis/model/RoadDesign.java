package xyz.geosure.roadgis.model;
/*
 * SHARED.java
 * Shard data variable class.
 *
 * Created on March 20, 2006, 9:16 AM
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
/** Revision Log:
    Version 0.1: Mar. 14, 2006
         - Release Windows .NET version.
    Version 0.2: Apr. 04, 2006
         - Release web-based version.
         - Fix metric unit display error in [Design Settings] screen.
         - Fix 3D animation error when choosing metric unit.
         - Fix vehicle speed to reflect design speed in 3D animation.
         - Fix horizontal curve setting scaling factor error when updating image 
           resolution and map scale.
         - Add [edit - clear curves] and allow PVI modification in vertical curve 
           design.
         - Add [file - save report] in vertical curve design for saving report to 
           local disk drive.
    Version 0.2.1: April 21, 2006
         - Add [file - delete] function in horizontal design
    Version 0.2.2: May 1, 2006
         - Update contour image resolution & scale with corresponding unit selection
         - Update horizontal design curve radius & elevation data with corresponding unit selection
         - Include unit info while saving vertical curve file
         - Automatically convert vertical curve data to corresponding unit selected
    Version 0.2.3: July 1, 31 2006
         - Add view mass diagram feature in vertical curve design
         - Add print/save report file menu under report screen
         - Add print/save landmark data file menu under view station landmark window in horizontal design
         - Add print/save station data file menu under view stations window in vertical design
         - Add Cortona User's Guide web link under help menu in vertical design screen, 8/2
         - Add road base & walls in 3D model, 8/10/06
    Version 0.2.4: Oct. 10, 2006
         - Add min grade
         - Change image scale and map scale from integer to float
         - Allow users to specify desired vertical curve length and check min curve len, Lm
         - Add option to view & save mass diagram and cut-fill data to a text file
    Version 0.2.5: Nov. 1, 2006
         - Upgrade toolbar buttons to 3D style
         - Add horizontal curve alignment toolbar icon #7
         - Add designed vertical curve elevation data button & options in elevation profile screen
         - Remove deleted item(s), line or curve, when saving horizontal design to a file
         - Modify popElevationMarkerForm in hDrawArea to correct error line segment 
           selection when users select curve segment
         - Modify getLineMarkLocation & getCurveMarkLocation subroutines
         - Ask for saving horizontal design & vertical design before close 7 exit
         - Add view horizontal PC, PT data screen
         - Allow users to edit elevation in view station/landmark screen by addinig 
           Update Elevation option under Data file menu 
         - Save temperary roaddesign.html & vrml_db file to Windows Desktop, 
           "C:\\Documents and Settings\\All Users\\Desktop\\". Use "C:\\" dir,
           if desktop directory is not available or not found
         - Fix import contour image then cancel error
         - Add view "Road Design Ctl+A" and "Road Only Ctl+Z" options under horizontal design
           file-view menu
         - Add "check station data" option under horizontal design file-tool menu option
         - Add index help manual (using JavaHelp), Web contents & User's Guide under Help menu.
         - Comment out JavaHelp stuff in Geometry_DEsign & hDrawArea files; cause error when using 
           JRE1.6.0 2/12/07
         - Change min curve distance from 20 to 40 and add popup message if curve is farther 
           than 40 pixels from lines in hDrawArea subroutine, 2/12/07
    Version 0.3.0: Feb. 12, 2007
         - Comment additional function call updateVCurve(PVI_index) at line 925 in vDrawArea, 2/28/07
         - Change refresh icon to landmark insert icon to allow insert landmark stations, 3/1/07
         - Check station landmarks before entering vertical design, 3/1/07
         - Allow using right mouse click to edit/delete landmark when choosing insert landmark tool,
           3/1/07
         - Modify saveDesignFile subroutine in Geometry_Design class to update parentIndex accordingly
           when removing deleted segments, 3/1/07
    Version 0.3.1: Mar. 15, 2007 released
         - add mouse right click to delete items feature in horizontal design (See mouse_click() under
           default option in hDrawArea.java file. 12/21/07
         - Remove tangent points when curve is deleted. 12/21/07
    Version 0.3.2: Feb. 11, 2008 to be released
         - Change horizontal view landmark (X,Y) and view PC/PT data scale to reflect actual scale in ft or m.
 
 */
import java.awt.Color;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Point;

public class RoadDesign {
    public static final String VERSION = "Ver.0.3.2, Feb. 2008 (C)" ;   // Version string, recompile abouttextbox
    public static final String MANUAL_PATH = "http://street.umn.edu/Road/jmanualv032.pdf" ;  // user's manual file path
    public static final String CONTENTS_PATH = "http://street.umn.edu/Road/javahelp/roadWebMain.html" ;  // help set file path
    public static final int MAX_CURVES = 256  ;                     // MAX CURVES
    public static final int MAX_MARKERS = 2048  ;                   // MAX NUMBER OF ELEVATION MARKERS
    public static final int MAX_SEGMENTS = 2048   ;                 // MAX NUMBER OF LINE?CURVE SEGMENTS
    public static final float FT2M = 0.3048f       ;               // convert from foot to meter
    public static final float MPH2Kmh = 1.609344f   ;              // convert from MPH to Km/h

	private String defaultEPSG = "EPSG:4326";//Default EPSG
	//private String defaultEPSG = "EPSG:32736";//Default EPSG
	private float snapDistance = 10.0f;//20m
	private double gridBuffer = 2000.0;//1km buffer
	private double gridVertexSpacing = 500.0;//500m 
    
    private int myUnit = 1 ;                                 // 1 - US Customary, 2 - Metric, default to 1
    private float ContourImageResolution = 300f;  // DPI, 71 ; // deafult contour map resolution, DPI
    private float ContourScale = 2000f;   //769 ;            // default contour map scale
    private float imageScale = 0.0f ;                        // overall image scale in GUI display
    private Color myPenColor = Color.blue ;                  // this is a color the user selects
    private float myRoadLaneSizes = 2f ;                     // set pen/road lane # variable
    private float myLaneWidth = 12f ;                        // design lane width, 12 ft
    private float myShoulderWidth = 6f ;                     // design shoulder width, 6 ft
    private Color elevationMarkerColor = Color.green ;       // this is a color the user selects for markers
    private float elevationMarkerSize = 2f;                  // set marker size
    private float curveRadius;                               // curve radius
    private Point currentElevationMarker ;                 // current Mark XY position

    private ArrayList<AlignmentSegment> hRoadData = new ArrayList<AlignmentSegment>();// horizontal road design database. Converted from Fixed Array by Felix
    private ArrayList<AlignmentMarker> hAlignMarks = new ArrayList<AlignmentMarker>();;   // land marks DB up to 16 curves, horizontal curve tangent points
    private ArrayList<AlignmentMarker>  elevationMarks = new ArrayList<AlignmentMarker> ();   // elevation marks DB - horizontal design
    private ArrayList<AlignmentMarker>  vConstructMarks = new ArrayList<AlignmentMarker> () ; //  ' proposed construction marks DB - vertical design
    private VCurve[] verticalCurves = new VCurve[MAX_MARKERS - 2] ;  //' vertical curves
    private int vCurveCount ;                                        // vertical curve count

    // design control parameters
    private float gradeLimit = 0.05f ;                     // max grade limit default 5%
    private float speedLimit = 40f ;                       // speed limit MPH default 40MPH
    private float maxCut = 15f ;                           // depth of maximum cut, default 10 ft
    private float maxFill = 15f ;                          // height of maximum fill, default 10 ft
    private float vehDecel = 11.2f ;                       // vehicle deceleration rate, 11.2 ft/s/s
    private float reactionTime = 2.5f ;                    // drive reaction time 2.5 sec
    private float frictionCoef = 0.3f ;                    // friction coefficient
    private float sideFrictionCoef = 0.13f ;               // side friction coefficient
    private float minVCurveLen = 560f ;                    // minimum vertical curve length
    private float minHCurveRadius = 500f ;                 // minimum horizontal curve radius
    private float maxSuperelevation = 0.06f ;              // maximum superelevation, 6%
    private float minGrade = 0.0f ;                        // minimum grade 0.0%

    /** Creates a new instance of SHARED */
    public RoadDesign() {
    }
    
    public void RESET() {
        hRoadData = new ArrayList<AlignmentSegment>(); 
    }

	public int getPreferredUnit() {
		return myUnit;
	}

	public void setPreferredUnit(int myUnit) {
		this.myUnit = myUnit;
	}

	public float getContourImageResolution() {
		return ContourImageResolution;
	}

	public void setContourImageResolution(float contourImageResolution) {
		ContourImageResolution = contourImageResolution;
	}

	public float getContourScale() {
		return ContourScale;
	}

	public void setContourScale(float contourScale) {
		ContourScale = contourScale;
	}

	public float getImageScale() {
		return imageScale;
	}

	public void setImageScale(float imageScale) {
		this.imageScale = imageScale;
	}

	public Color getPreferredPenColor() {
		return myPenColor;
	}

	public void setPreferredPenColor(Color myPenColor) {
		this.myPenColor = myPenColor;
	}

	public float getPreferredRoadLaneSizes() {
		return myRoadLaneSizes;
	}

	public void setPreferredRoadLaneSizes(float myRoadLaneSizes) {
		this.myRoadLaneSizes = myRoadLaneSizes;
	}

	public float getPreferredLaneWidth() {
		return myLaneWidth;
	}

	public void setPreferredLaneWidth(float myLaneWidth) {
		this.myLaneWidth = myLaneWidth;
	}

	public float getPreferredShoulderWidth() {
		return myShoulderWidth;
	}

	public void setPreferredShoulderWidth(float myShoulderWidth) {
		this.myShoulderWidth = myShoulderWidth;
	}

	public Color getElevationMarkerColor() {
		return elevationMarkerColor;
	}

	public void setElevationMarkerColor(Color elevationMarkerColor) {
		this.elevationMarkerColor = elevationMarkerColor;
	}

	public float getElevationMarkerSize() {
		return elevationMarkerSize;
	}

	public void setElevationMarkerSize(float elevationMarkerSize) {
		this.elevationMarkerSize = elevationMarkerSize;
	}

	public float getCurveRadius() {
		return curveRadius;
	}

	public void setCurveRadius(float curveRadius) {
		this.curveRadius = curveRadius;
	}

	public Point getCurrentElevationMarker() {
		return currentElevationMarker;
	}

	public void setCurrentElevationMarker(Point currentElevationMarker) {
		this.currentElevationMarker = currentElevationMarker;
	}

	public ArrayList<AlignmentSegment> getHorizontalAlignmentSegments() {
		return hRoadData;
	}

	public void setHorizontalRoadData(ArrayList<AlignmentSegment> hRoadData) {
		this.hRoadData = hRoadData;
	}
	public void addHorizontalAlignmentSegment(AlignmentSegment segment) {		
		this.hRoadData.add(segment);
	}

	public ArrayList<AlignmentMarker> getHorizontalAlignmentMarks() {
		return hAlignMarks;
	}

	public void setHorizontalAlignmentMarks(ArrayList<AlignmentMarker> hAlignMarks) {
		this.hAlignMarks = hAlignMarks;
	}

	public void addHorizontalAlignmentMarker(AlignmentMarker marker) {		
		this.hAlignMarks.add(marker);
	}

	public int getHorizontalAlignmentMarkCount() {
		return hAlignMarks.size();
	}


	public ArrayList<AlignmentMarker>  getElevationMarks() {
		return elevationMarks;
	}

	public void setElevationMarks(ArrayList<AlignmentMarker>  elevationMarks) {
		this.elevationMarks = elevationMarks;
	}

	public void addElevationMark(AlignmentMarker marker) {		
		this.elevationMarks.add(marker);
	}

	public int getElevationMarkCount() {
		return elevationMarks.size();
	}

	public ArrayList<AlignmentMarker>  getVerticalConstructMarks() {
		return vConstructMarks;
	}

	public void setVerticalConstructMarks(ArrayList<AlignmentMarker>  vConstructMarks) {
		this.vConstructMarks = vConstructMarks;
	}

	public void addVerticalConstructionMarker(AlignmentMarker marker) {
		vConstructMarks.add(marker);
	}

	public int getvConstructMarkCount() {
		return vConstructMarks.size();
	}


	public VCurve[] getVerticalCurves() {
		return verticalCurves;
	}

	public void setVerticalCurves(VCurve[] verticalCurves) {
		this.verticalCurves = verticalCurves;
	}

	public int getvCurveCount() {
		return vCurveCount;
	}

	public void setvCurveCount(int vCurveCount) {
		this.vCurveCount = vCurveCount;
	}

	public float getGradeLimit() {
		return gradeLimit;
	}

	public void setGradeLimit(float gradeLimit) {
		this.gradeLimit = gradeLimit;
	}

	public float getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(float speedLimit) {
		this.speedLimit = speedLimit;
	}

	public float getMaxCut() {
		return maxCut;
	}

	public void setMaxCut(float maxCut) {
		this.maxCut = maxCut;
	}

	public float getMaxFill() {
		return maxFill;
	}

	public void setMaxFill(float maxFill) {
		this.maxFill = maxFill;
	}

	public float getVehDecel() {
		return vehDecel;
	}

	public void setVehDecel(float vehDecel) {
		this.vehDecel = vehDecel;
	}

	public float getReactionTime() {
		return reactionTime;
	}

	public void setReactionTime(float reactionTime) {
		this.reactionTime = reactionTime;
	}

	public float getFrictionCoef() {
		return frictionCoef;
	}

	public void setFrictionCoef(float frictionCoef) {
		this.frictionCoef = frictionCoef;
	}

	public float getSideFrictionCoef() {
		return sideFrictionCoef;
	}

	public void setSideFrictionCoef(float sideFrictionCoef) {
		this.sideFrictionCoef = sideFrictionCoef;
	}

	public float getMinVCurveLen() {
		return minVCurveLen;
	}

	public void setMinVCurveLen(float minVCurveLen) {
		this.minVCurveLen = minVCurveLen;
	}

	public float getMinHCurveRadius() {
		return minHCurveRadius;
	}

	public void setMinHCurveRadius(float minHCurveRadius) {
		this.minHCurveRadius = minHCurveRadius;
	}

	public float getMaxSuperelevation() {
		return maxSuperelevation;
	}

	public void setMaxSuperelevation(float maxSuperelevation) {
		this.maxSuperelevation = maxSuperelevation;
	}

	public float getMinGrade() {
		return minGrade;
	}

	public void setMinGrade(float minGrade) {
		this.minGrade = minGrade;
	}

	public String getDefaultEPSG() {
		return defaultEPSG;
	}


	public void setDefaultEPSG(String defaultEPSG) {
		this.defaultEPSG = defaultEPSG;
	}

	public float getSnapDistance() {
		if(myUnit == 1) {//Feet
			return snapDistance / FT2M;
		}else {
			return snapDistance;
		}
	}

	public void setSnapDistance(float snapDistance) {
		this.snapDistance = snapDistance;
	}

	public double getGridBuffer() {
		if(myUnit == 1) {//Feet
			return gridBuffer / FT2M;
		}else {
		return gridBuffer;
		}
	}

	public void setGridBuffer(double gridBuffer) {
		this.gridBuffer = gridBuffer;
	}

	public double getGridVertexSpacing() {
		if(myUnit == 1) {//Feet
			return gridVertexSpacing / FT2M;
		}else {
		return gridVertexSpacing;
		}
	}

	public void setGridVertexSpacing(double gridVertexSpacing) {
		this.gridVertexSpacing = gridVertexSpacing;
	}


}
