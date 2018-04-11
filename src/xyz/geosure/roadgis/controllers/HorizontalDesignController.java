package xyz.geosure.roadgis.controllers;
/*
 * HorizontalDesignController.java
 * Purpose is to provide an API for Horizontal Design Functions.
 *
 * Created on March 21, 2018, Felix Kiptum
 * based on hDrawArea
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTextArea;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.CircularString;
import org.geotools.geometry.jts.CurvedGeometryFactory;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import com.vividsolutions.jts.math.Vector2D;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.model.AlignmentSegment;
import xyz.geosure.roadgis.model.ContourImage;
import xyz.geosure.roadgis.model.RoadDesign;
import xyz.geosure.roadgis.model.StationInfo;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.GeoUtils;
import xyz.geosure.roadgis.utils.VectorUtils;
import xyz.geosure.roadgis.utils.print.PrintUtilities;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.myFrame;
import xyz.geosure.roadgis.views.frames.LandmarksDataFrame;
import xyz.geosure.roadgis.views.frames.TangentDataFrame;
import xyz.geosure.roadgis.views.frames.VerticalAlignmentFrame;
import xyz.geosure.roadgis.views.popups.AboutPopup;
import xyz.geosure.roadgis.views.popups.ClearAllPopup;
import xyz.geosure.roadgis.views.popups.ContourSettingsPopup;
import xyz.geosure.roadgis.views.popups.CurveRadiusPopup;
import xyz.geosure.roadgis.views.popups.CurveSettingsEditPopup;
import xyz.geosure.roadgis.views.popups.CurveSettingsPopup;
import xyz.geosure.roadgis.views.popups.DesignSettingsPopup;
import xyz.geosure.roadgis.views.popups.ElevationMarkerEditPopup;
import xyz.geosure.roadgis.views.popups.ElevationMarkerInsertPopup;
import xyz.geosure.roadgis.views.popups.ElevationMarkerPopup;
import xyz.geosure.roadgis.views.popups.LandmarksClearPopup;
import xyz.geosure.roadgis.views.popups.SaveVerticalDesignPopup;
import xyz.geosure.roadgis.views.popups.SegmentDeletePopup;
import xyz.geosure.roadgis.views.popups.TangentDeletePopup;
import xyz.geosure.roadgis.views.toolbars.HorizontalStatusbar;

public class HorizontalDesignController   {
	private RoadGISApplication app = null;

	private FeatureLayer gridLinesLayer = null;
	private FeatureLayer designLinesLayer = null;
	private FeatureLayer designMarksLayer = null;
	private FeatureLayer radialLinesLayer = null;
	private FeatureLayer pointsLayer = null;
	private GridCoverageLayer DEMLayer = null;

	//private DefaultFeatureCollection geometricDesignCollection = null;
	private ListFeatureCollection designLinesCollection = null;
	private ListFeatureCollection radialElementsCollection = null;
	private ListFeatureCollection designMarksCollection = null;
	private ContourImage contourImage = null;

	private HorizontalStatusbar horizontalStatusBar;           // status bar
	final int grid = 8;           // drawarea grid size
	final String NO_MAP_MSG = "No contour image loaded.\n\nPlease import contour map as background\nimage first!" ;

	VerticalDesignController vDesign = new VerticalDesignController(app);

	private DirectPosition2D startPoint, midPoint, endPoint;	
	private boolean line_started = false ;
	private boolean curve_started = false ;
	private boolean midpoint_flag = false;

	// popups here
	private RoadDesign roadDesign = new RoadDesign() ;
	private DesignSettingsPopup designSettingsPopup = null;
	private ContourSettingsPopup contourSettingsPopup = null;

	private CurveSettingsPopup curveSettingsPopup = null;
	private CurveSettingsEditPopup curveSettingsEditPopup = null;	
	private CurveRadiusPopup curveRadiusPopup = null;

	private ElevationMarkerEditPopup elevationMarkerEditPopup = null;
	private ElevationMarkerPopup elevationMarkerPopup = null;
	private ElevationMarkerInsertPopup elevationMarkerInsertPopup = null;

	private LandmarksDataFrame landmarkDataPopup = null;
	private LandmarksClearPopup landmarksClearPopup = null; 

	private VerticalAlignmentFrame verticalAlignmentPopup = null;

	private SaveVerticalDesignPopup saveVerticalDesignPopup = null;	

	private ClearAllPopup clearAllPopup = null;	
	private TangentDataFrame tangentDataPopup = null;
	private TangentDeletePopup tangentDeletePopup = null;
	private SegmentDeletePopup deleteSegmentPopup = null ;

	private AboutPopup aboutPopup = null;


	private int toolbarIndex = 0 ;


	public boolean modification_started = false ;


	private int delta=50;
	//Graphics g ;
	public String contourImageFilepath ;
	public boolean mouseHoldDown = false ;
	private float draw_scale  = 1.0f ;
	int dataSelIndex = -1 ;

	// Horizontal geometry DB
	private int hRoadDataCount = 0;  // number of segments (line/curve)
	private int segLogIndex  = -1;
	private int[] segLogBuffer = new int[16] ;   // undo, redo log
	private int markLogIndex = -1 ;
	private int[] markLogBuffer = new int[16] ;  // undo, redo log
	private int endMarkSize  = 2 ;   // square end mark, actual size (red) = 2*endMarkSize square

	// other variables
	int myAlpha = 255 ; // declare a Alpha variable
	//Dim hAlignMarkerPen, elevationMarkerPen, currentPen As Pen
	Point modificationInfo ;
	String design_filename = "" ;
	StationInfo sInfo ;         // landmark station info

	int idSegment ;
	private String tangentPrintStr ;    // save and print tangent (PC, PT) data

	// window frame =================
	RoadGISPopup frmAbout, frame_clearLandmarks ;
	RoadGISPopup frame_deleteSegment, frame_msgboxClearAll; 
	RoadGISPopup frmInsertElevationMarker ;     // 2/28/07
	RoadGISPopup frame_deleteTangent ;
	RoadGISPopup frame_saveVDesign ;    //11/13/06 added

	public myFrame frmVerticalAlign = new myFrame() ;

	PrintUtilities hd_pu, vd_pu ;


	//PageFormat printPageFormat = new PageFormat() ;
	Runnable runThread0 = null ;    // stop on red light
	public Thread tSetValign ;
	private boolean valign_flag = false ; // accessed from toolbar class
	private boolean deleteTangent_flag = false ;
	private boolean popCurveSettings_flag = false ;
	private boolean popMsgBox_flag = false ;
	private boolean viewRoadOnly_flag = false ;
	private String msgBox_title = "" ;
	private String msgBox_message = "" ;
	private String item_clicked_str = "" ;      // used for right mouse delete

	//==================================================================
	// class initialization

	public HorizontalDesignController(RoadGISApplication app)    {

		this.app = app;

		//Construct the GIS Layers needed for the design to work
		try {

			//geometricDesignCollection = new DefaultFeatureCollection("construction_lines",LINE_TYPE);
			designMarksCollection = new ListFeatureCollection(GeoUtils.getRoadMarkerType(roadDesign.getDefaultEPSG()));
			designLinesCollection = new ListFeatureCollection(GeoUtils.getRoadSegmentFeatureType(roadDesign.getDefaultEPSG()));
			radialElementsCollection = new ListFeatureCollection(GeoUtils.getRoadRadialFeatureType(roadDesign.getDefaultEPSG()));

			radialLinesLayer = new FeatureLayer(radialElementsCollection, GeoUtils.getGridLineStyle());
			radialLinesLayer.setTitle("Radial Elements");
			app.getMapContent().addLayer(radialLinesLayer);

			designLinesLayer = new FeatureLayer(designLinesCollection, GeoUtils.getLineStyle());
			designLinesLayer.setTitle("Road Alignments");

			app.getMapContent().addLayer(designLinesLayer);

			designMarksLayer = new FeatureLayer(designMarksCollection, GeoUtils.getMarkerStyle());
			designMarksLayer.setTitle("Construction Marks");			
			app.getMapContent().addLayer(designMarksLayer);


		} catch (Exception e) {
			e.printStackTrace();
		}

		setHorizontalStatusBar(new HorizontalStatusbar()) ;

		//setBackground(Color.white);

		// =======================================================================
		// bring vertical design to top display thread
		// =====================================================================
		runThread0 = new Runnable() {
			public void run() {
				while (true) {
					if (popMsgBox_flag){
						popMessageBox1(msgBox_title, msgBox_message);
						popMsgBox_flag = false ;
					} else if (valign_flag){
						//newstatus(10, " Vertical Curve Design");
						valign_flag = false ;
					} else if (deleteTangent_flag) {
						popDeleteTangent("Delete Tangent Data","Do you want to delete tangent data pair?");
						deleteTangent_flag = false ;
					} else if (popCurveSettings_flag) {
						popCurveSettings(); 
						popCurveSettings_flag = false ;
					} else {
						tSetValign.yield();
						try {Thread.sleep(100) ;}
						catch (InterruptedException ie) {} ;
					}
				}
			}   // void run
		} ; // runThread 0
		tSetValign = new Thread(runThread0, "VerticalAlign") ;
		tSetValign.start() ;


	}

	// object initialization
	public void init(int flag) {

		frmAbout = new RoadGISPopup();

		roadDesign.setImageScale((float)roadDesign.getContourImageResolution() / (float)roadDesign.getContourScale());  //  // pixel/ft
		if (flag==0) {
			//roadDesign.setHorizontalAlignmentMarkCount(0);
			//roadDesign.setElevationMarkCount(0);
			//roadDesign.setvConstructMarkCount(0);
			roadDesign.setCurrentElevationMarker(GeoUtils.makePoint(0, 0));
		}
		roadDesign.setCurveRadius(roadDesign.getMinHCurveRadius()) ;   // set to min
		//animationFlag = false;

		int i ;
		for (i=0;i<segLogBuffer.length;i++) {
			segLogBuffer[i] = -1;
			markLogBuffer[i] = -1;
		}
		push2SegLogBuffer(hRoadDataCount); 
		push2MarkLogBuffer(roadDesign.getElevationMarkCount()); 

		getHorizontalStatusBar().setStatusBarText(3, new Float(Math.round(getDraw_scale()*10f)/10f).toString()) ;

		if(null != contourImage) {
			contourImage.process();
		}
	}

	// methods
	public Point searchSegmentDB(Point pointToFind ) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist;
		Point data = GeoUtils.makePoint(-1,-1);
		for (i=0 ; i<hRoadDataCount; i++){
			// check point 1 or center of radius if a circle
			dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
			//System.out.println("end pt1="+dist) ;
			if (dist <= getEndMarkSize()*2f/getDraw_scale() ) {  //Math.sqrt(2), 10/11/06
				data = GeoUtils.makePoint(i,i);
				break;
			}
			// check poitn 2 
			dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint(), pointToFind);
			//System.out.println("end pt2="+dist) ;
			if (dist <= getEndMarkSize()*2f/getDraw_scale()) { //Math.sqrt(2), 10/11/06

				data = GeoUtils.makePoint(i,2);
				break;
			}
		}//end for
		return data;
	}

	public int popSegLogBuffer() {
		// pop the current # of data from log buffer
		if (segLogIndex > 0) {
			segLogIndex -= 1;
			return segLogBuffer[segLogIndex];
		} else {
			return -99;
		}
	}

	public int popMarkLogBuffer(){
		// pop the current # of landmark data from log buffer
		if (markLogIndex > 0) {
			markLogIndex -= 1;
			return markLogBuffer[markLogIndex];
		} else {
			return -99;
		}
	}

	public void push2SegLogBuffer(int _myhRoadDataCount){
		// save # of data into log buffer
		if (segLogIndex == segLogBuffer.length - 1 ) {
			// buffer fulled
			// shift forward by 1
			int i ;
			for (i=0; i<segLogIndex; i++) {
				segLogBuffer[i] = segLogBuffer[i + 1];
			}
			segLogBuffer[segLogIndex] = _myhRoadDataCount;
		} else {
			segLogIndex += 1;
			segLogBuffer[segLogIndex] = _myhRoadDataCount;
		}
	}

	public void push2MarkLogBuffer(int _myLandmarkCount ) {

		// save # of data into log buffer
		if (markLogIndex == markLogBuffer.length - 1) { 
			// buffer fulled
			// shift forward by 1
			int i;
			for (i=0; i<markLogIndex;i++){
				markLogBuffer[i] = markLogBuffer[i + 1];
			}
			markLogBuffer[markLogIndex] = _myLandmarkCount;
		} else {
			markLogIndex += 1;
			markLogBuffer[markLogIndex] = _myLandmarkCount;
		}
	}

	// search the landmark DB and find where the index of inserted marker located
	private int findMarkerInsertIndex() {
		int myIndex = -1 ;
		Vector2D vec1, vec2 ;
		for (int i=0; i<roadDesign.getElevationMarkCount()-1; i++) {
			vec1 = VectorUtils.vector(roadDesign.getCurrentElevationMarker(), roadDesign.getElevationMarks().get(i).getLocation());
			vec2 = VectorUtils.vector(roadDesign.getCurrentElevationMarker(), roadDesign.getElevationMarks().get(i+1).getLocation());
			if (VectorUtils.vectorDOT(vec1, vec2)<0) {
				myIndex = i+1 ;
				break ;
			}   // end if
		}   // end for
		return myIndex ;
	}


	// used to isolate right click selected item(s)
	private void parseClickedStr() {
		int end ;
		String myStr = item_clicked_str ;
		// reset all setect items
		edit_unselectAll() ;
		while (myStr.length()>0) {
			end = myStr.indexOf(",", 0) ;
			int idx = ConversionUtils.CInt(myStr.substring(0, end)) ;
			myStr = myStr.substring(end+1) ;
			roadDesign.getHorizontalAlignmentSegments().get(idx).setItemSelect(true); 
		}
	}

	public void checkItemSelect(Point pointToFind ){
		// transform pt from screen pixel to actual unit
		int i ;
		double dist, cosine ;
		item_clicked_str = "" ;
		for (i = 0 ; i< hRoadDataCount; i++) {
			if (roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() < 0) {
				// line segment
				// end point 1
				dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
				//System.out.println("dist1=" + dist) ;
				if (dist <= getEndMarkSize() * Math.sqrt(2) /getDraw_scale()) {
					roadDesign.getHorizontalAlignmentSegments().get(i).selectItem();
					item_clicked_str += i+"," ;
				} else {
					// check end point 2
					dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint(), pointToFind);
					//System.out.println("dist2=" + dist) ;
					if (dist <= getEndMarkSize() * Math.sqrt(2) /getDraw_scale()) {
						roadDesign.getHorizontalAlignmentSegments().get(i).selectItem();
						item_clicked_str += i+"," ;
					} else {
						// not selecting end points
						cosine = VectorUtils.getCosTheta(VectorUtils.vector(roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint(), pointToFind), 
								VectorUtils.vector(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind));
						//System.out.println("cosine=" + cosine) ;
						// added 11/1/06
						double dist2line = VectorUtils.calcDist2Line(pointToFind, roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint()) ;
						//System.out.println("dist 2 line=" + dist2line) ;
						if ((cosine <= -0.99f) && 
								(dist2line <= getEndMarkSize() * Math.sqrt(2) /getDraw_scale())) { // ~180 degree, item highlighted
							roadDesign.getHorizontalAlignmentSegments().get(i).selectItem();
							item_clicked_str += i+"," ;
						}
					}
				}
				//System.out.println("Check item selected = "+myDB.gethRoadData()[i].isSelected()) ;
			} else {
				// curve segment
				dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
				//System.out.println("dist="+new Float(dist).toString());
				if ((Math.abs(dist - roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() * roadDesign.getImageScale())) <= roadDesign.getHorizontalAlignmentSegments().get(i).getPenWidth() * Math.sqrt(2)/getDraw_scale()) {
					// item highlighted
					roadDesign.getHorizontalAlignmentSegments().get(i).selectItem();
					item_clicked_str += i+"," ;
				}
			}
		}   // for
		//System.out.println("Check item selected") ;
	}   // checkItemSelect

	public int checkMarkLocation(Point pointToFind ) {
		// transform pt from screen pixel to actual unit
		int i, myIndex ;
		double dist, cosine ;
		boolean acceptElevation = false;
		myIndex=-1 ;
		double minDist=9999f ;
		for (i=0; i<hRoadDataCount; i++) {
			if (roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() < 0) { 
				// line segment
				dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
				if (dist <= getEndMarkSize() * Math.sqrt(2)/getDraw_scale()) { 
					acceptElevation = true;
					roadDesign.setCurrentElevationMarker(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint());
				} else {
					dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint(), pointToFind);
					if (dist <= getEndMarkSize() * Math.sqrt(2)/getDraw_scale()) { 
						acceptElevation = true;
						roadDesign.setCurrentElevationMarker(roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint());
					} else {
						Vector2D vec1;
						// not selecting end points
						Vector2D vec2 ;
						double L1, L2 ;
						vec1 = VectorUtils.vector(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint());
						vec2 = VectorUtils.vector(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
						L1 = vec1.length();//VectorUtils.vectorLen();
						L2 = vec2.length();//VectorUtils.vectorLen(vec2);
						cosine = VectorUtils.getCosTheta(vec1, vec2);
						//System.out.println("cosine="+cosine) ;
						//System.out.println("L1, L2"+L1 + ", "+ L2) ;
						double dist2line = VectorUtils.calcDist2Line(pointToFind, roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(),
								roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint()) ;
						if (cosine >= 0.99f && L2 <= L1  && 
								(dist2line <= getEndMarkSize() * Math.sqrt(2) /getDraw_scale())) { // // 1 degree close to line segment
							acceptElevation = true;
							Point marker = GeoUtils.makePoint(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint().getX()+ vec1.getX()* L2 / L1,  roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint().getY()+ vec1.getY()* L2 / L1);
							roadDesign.setCurrentElevationMarker(marker);
						}
					}
				}
			} else {
				// curve segment
				dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
				//System.out.println("curve dist="+dist) ;
				//System.out.println("diff="+Math.abs(dist - myDB.gethRoadData()[i].getRadius() * myDB.imageScale)) ;
				//System.out.println("limit="+myDB.gethRoadData()[i].getPenWidth() /draw_scale) ;
				if (Math.abs(dist - roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() * roadDesign.getImageScale()) <= roadDesign.getHorizontalAlignmentSegments().get(i).getPenWidth() * Math.sqrt(2)/getDraw_scale()) {
					// click on curve
					acceptElevation = true;
					Vector2D vec2 ;
					double L1, L2 ;
					vec2 = VectorUtils.vector(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), pointToFind);
					L1 = roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() * roadDesign.getImageScale();
					L2 = vec2.length();
					Point marker = GeoUtils.makePoint(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint().getX()+ vec2.getX()* L1 / L2, roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint().getY()+ vec2.getY()* L1 / L2);

					roadDesign.setCurrentElevationMarker(marker);
				}
			}
			if (acceptElevation == true) {
				if (minDist > dist) { //Then
					minDist = dist;
					myIndex = i;
				}
				acceptElevation = false;
				//break;
			}
		}
		//if (acceptElevation == false ) {
		//    return -1 ;  // marker does not land on any segment
		//} else {
		//    return i;
		//}
		return myIndex ;
	}   //checkMarkLocation

	public int checkTangentLandmarks(Point pointToFind) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist ;
		boolean foundTangentPoint = false ;
		for (i=0;i<roadDesign.getHorizontalAlignmentMarkCount();i++){
			// check tangent landmark database
			dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentMarks().get(i).getLocation(), pointToFind);
			if (dist <= getEndMarkSize() * Math.sqrt(2) /getDraw_scale()) {
				foundTangentPoint = true;
				roadDesign.setCurrentElevationMarker(roadDesign.getHorizontalAlignmentMarks().get(i).getLocation());
				break;
			}
		}
		if (foundTangentPoint == false ) {
			return -1 ;  // marker does not land on any tangent points
		} else {
			return roadDesign.getHorizontalAlignmentMarks().get(i).getParentIndex();
		}
	}   // checkTangentMark

	public int edit_undo() {
		if (toolbarIndex==4 || toolbarIndex==5) {
			int bufData ;
			bufData = popSegLogBuffer();
			if (bufData >= 0) {
				hRoadDataCount = bufData;
			}
		} else if (toolbarIndex==7) {
			int bufData ;
			bufData = popMarkLogBuffer();
			if (bufData >= 0 ) {
				//roadDesign.setElevationMarkCount(bufData);
			}
		}
		//repaint();
		return (0);
	}

	public int edit_redo() {
		if (toolbarIndex==4 || toolbarIndex==5) {
			if (segLogIndex < segLogBuffer.length - 1) { 
				if (segLogBuffer[segLogIndex + 1] >= 0) {
					// log info exists
					segLogIndex += 1;
					hRoadDataCount = segLogBuffer[segLogIndex];
				}
			}
		} else if (toolbarIndex==7) {
			if (markLogIndex < markLogBuffer.length - 1) { 
				if (markLogBuffer[markLogIndex + 1] > 0) { 
					// log info exists
					markLogIndex += 1;
					//roadDesign.setElevationMarkCount(markLogBuffer[markLogIndex]);
				}
			}
		}
		//repaint();
		return(0);
	}

	public void edit_unselectAll() {
		int i ;
		for (i=0; i<hRoadDataCount;i++){
			roadDesign.getHorizontalAlignmentSegments().get(i).unSelectItem();
		}
		//repaint();
	}

	public void edit_selectAll() {
		int i ;
		for (i=0; i<hRoadDataCount;i++){
			roadDesign.getHorizontalAlignmentSegments().get(i).setItemSelect(true); 
		}
		//repaint();
	}

	// reset scales when loading new design
	public void view_RESET() {
		viewRoadOnly_flag = false ; // 11/22/06 added
		//translate.getX()= 0;
		//translate.getY()= 0;
		//scaledxlate.getX()= 0;
		//scaledxlate.getY()= 0;
		//setDraw_scale(1f);
		getHorizontalStatusBar().setStatusBarText(3, new Float(Math.round(getDraw_scale()*10f)/10f).toString()) ;
		//repaint();
	}

	public int checkhAlignTangent(Point pointToFind) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist ;
		boolean foundTangentPoint=false ;
		for (i=0;i<roadDesign.getHorizontalAlignmentMarkCount();i++) {
			// check tangent landmark database
			dist = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentMarks().get(i).getLocation(), pointToFind);
			if (dist <= getEndMarkSize() * Math.sqrt(2.0)/getDraw_scale()) { 
				foundTangentPoint = true;
				break;
			}
		}
		if (foundTangentPoint == false) {
			return -1;   // marker does not land on any tangent points
		} else {
			return i ;  
		}
	}   // checkhAlignTangent

	public int checkElevationLandmarks(Point pointToFind) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist ;
		boolean foundLandmark=false ;
		for (i=0; i<roadDesign.getElevationMarkCount();i++) {
			// check tangent landmark database
			dist = VectorUtils.distanceOf(roadDesign.getElevationMarks().get(i).getLocation(), pointToFind);
			if (dist <= roadDesign.getElevationMarkerSize() * Math.sqrt(2.0)/getDraw_scale()) { 
				foundLandmark = true;
				roadDesign.setCurrentElevationMarker(roadDesign.getElevationMarks().get(i).getLocation());
				break;
			}
		}
		if (foundLandmark == false ) {
			return -1 ;  // marker does not land on any elevation landmark points
		} else {
			return i;
		}
	} //  checkElevationLandmarks

	public void tool_curvehAlignMarks() {
		int i, selectedCurve=-1 ;
		int[] selectedLines = new int[2];
		int selLineIdx = 0;
		int selCurveIdx = 0;
		for (i=0; i<hRoadDataCount; i++) {
			if (roadDesign.getHorizontalAlignmentSegments().get(i).isSelected()) {
				// item selected
				if (roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() < 0) { 
					// line
					selectedLines[selLineIdx] = i;
					selLineIdx += 1;
				} else {
					// curve
					selectedCurve = i;
					selCurveIdx += 1;
				}
			}
		} // for i

		// check if 2 lines & 1 curve are selected
		if ((selLineIdx == 2) && (selCurveIdx == 1)) { 
			Point pt1;
			Point pt2 ;  // 2 tangent points on the curve
			calculateCurveCenter(selectedLines[0], selectedLines[1], selectedCurve);

			pt1 = calculateTangentPoint(selectedLines[0], selectedCurve); 
			roadDesign.getHorizontalAlignmentSegments().get(selectedCurve).saveTangentAngle((byte)1, pt1);
			pt2 = calculateTangentPoint(selectedLines[1], selectedCurve); 
			roadDesign.getHorizontalAlignmentSegments().get(selectedCurve).saveTangentAngle((byte)2, pt2); 

			// save tangent points
			AlignmentMarker startMarker = new AlignmentMarker(pt1, 0.0, selectedCurve, MarkerType.TANGENT);
			AlignmentMarker endMarker = new AlignmentMarker(pt2, 0.0, selectedCurve, MarkerType.TANGENT);
			roadDesign.addHorizontalAlignmentMarker(startMarker);
			roadDesign.addHorizontalAlignmentMarker(endMarker);
			
			// unselect segments
			roadDesign.getHorizontalAlignmentSegments().get(selectedLines[0]).unSelectItem();
			roadDesign.getHorizontalAlignmentSegments().get(selectedLines[1]).unSelectItem();
			roadDesign.getHorizontalAlignmentSegments().get(selectedCurve).unSelectItem();

		} else {
			popMessageBox("Horizontal Alignment","Please select 2 linear and 1 curve segments first!");
		}
		//repaint();
		//PictureBox1.Invalidate()
	}   // tool_curvehAlignMarks

	public void tool_property() {
		int i, selectedItems=0 ;
		String propertyStr = "" ;
		String unitStr = "" ;
		if (roadDesign.getPreferredUnit()==1 ) {
			unitStr = " (ft) " ;
		} else if (roadDesign.getPreferredUnit()==2 ) {
			unitStr = " (m) " ;
		}
		for (i=0; i<hRoadDataCount; i++) {
			if (roadDesign.getHorizontalAlignmentSegments().get(i).isSelected()) {
				// item selected
				if (roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() < 0) { 
					// line
					double len = VectorUtils.distanceOf(roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint(), roadDesign.getHorizontalAlignmentSegments().get(i).getEndPoint()) ;
					propertyStr += "("+ConversionUtils.CStr(i+1)+") Line, Length=" +
							ConversionUtils.CStr(len/roadDesign.getImageScale()) + unitStr + "\n" ;
				} else {
					// curve
					propertyStr += "("+ConversionUtils.CStr(i+1)+") Circle, Radius=" + 
							ConversionUtils.CStr(roadDesign.getHorizontalAlignmentSegments().get(i).getRadius()) + unitStr + "\n" ;
				}   //System.out.println("imageScale = "+myDB.imageScale) ;
				selectedItems++ ;
			}
		} // for i

		// check if 2 lines & 1 curve are selected
		if (selectedItems>0) { 
			popMessageBox("Properties", propertyStr);
		} else {
			popMessageBox("Properties","Please select a line or curve segment first!");
		}
		//repaint();

	}   // tool_property

	// check station segment type continuity, 11/16/06
	public void tool_checkStation() {

		String status = "" ;
		status = checkLandmarks() ;
		/*
        int i;
        int last_type=myDB.getElevationMarks().get(0].getSegmentType() ;
        int cur_type=0 ;
        for (i=1; i<myDB.getElevationMarkCount(); i++) {
            cur_type = myDB.getElevationMarks().get(i].getSegmentType() ;
            if (cur_type != 3 && last_type != cur_type && last_type != 3) {
                // not tangent, different type and not tangent previously
                if (status.length()>0) {
                    status = status + ", " + i ;
                } else {
                    status += i ;
                }
            }
            last_type = cur_type ;

        } // for i
		 */
		if (status.length()==0) { 
			popMessageBox("Check Station Data", "Station data OK!");
		} else {
			popMessageBox("Check Station Data", "Station data error at station "+status+".");
		}

		//repaint();

	}   // tool_checkStation

	private String checkLandmarks() {
		int i;
		String status = "" ;
		MarkerType last_type=roadDesign.getElevationMarks().get(0).getSegmentType() ;
		MarkerType cur_type=MarkerType.NONE ;
		for (i=1; i<roadDesign.getElevationMarkCount(); i++) {
			cur_type = roadDesign.getElevationMarks().get(i).getSegmentType() ;
			if (cur_type != MarkerType.TANGENT && last_type != cur_type && last_type != MarkerType.TANGENT) {
				// not tangent, different type and not tangent previously
				if (status.length()>0) {
					status = status + ", " + i ;
				} else {
					status += i ;
				}
			}
			last_type = cur_type ;
		} // for i
		return status ;

	}   // checkLandmarks

	//Calculate the Centre of the Curve
	public void calculateCurveCenter(int lineIndex1, int lineIndex2, int curveIndex) {

		Point p1, p2, p3, p4, pc ;
		double a1, a2, b1, b2 ;
		double c2;
		double c1;
		double rad;
		double L1;
		double L2;
		double den ;
		Point[] Center = new Point[4] ;
		p1 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex1).getStartPoint();
		p2 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex1).getEndPoint();
		p3 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex2).getStartPoint();
		p4 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex2).getEndPoint();
		pc = roadDesign.getHorizontalAlignmentSegments().get(curveIndex).getStartPoint();
		rad = roadDesign.getHorizontalAlignmentSegments().get(curveIndex).getRadius() * roadDesign.getImageScale();
		a1 = p2.getY()- p1.getY();
		b1 = p1.getX()- p2.getX();
		a2 = p4.getY()- p3.getY();
		b2 = p3.getX()- p4.getX();
		L1 = VectorUtils.distanceOf(p1, p2);
		L2 = VectorUtils.distanceOf(p3, p4);
		int i ;
		for (i=0; i<4; i++) {
			Center[i] = GeoUtils.makePoint(0,0);
		}
		den = (a1 * b2 - a2 * b1);
		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()+ L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()+ L2 * rad;
		// case 1
		Center[0] = GeoUtils.makePoint( (c2 * b1 - c1 * b2) / den,  -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()+ L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()- L2 * rad;
		// case 2
		Center[1]= GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()- L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()+ L2 * rad;
		// case 3
		Center[2] = GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()- L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()- L2 * rad;
		// case 4
		Center[3] = GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		int index = 0;
		double min_dist = 99999f;
		double dist ;
		// find the closest one
		for (i=0; i<4; i++) {
			dist = VectorUtils.distanceOf(pc, Center[i]);
			if (dist < min_dist) {
				min_dist = dist;
				index = i;
			}
			//debugWindow.Text &= i & " (Xc, Yc)=" & Center(i).getX()& ", " & Center(i).getY()& " dist=" & dist & vbCrLf
		}
		//debugWindow.Text &= i & " (PXo, PYo)=" & pc.getX()& ", " & pc.getY()& vbCrLf

		if (min_dist < 40 && min_dist >= 0) {    // change from 20 to 40 , 2/12/2007
			roadDesign.getHorizontalAlignmentSegments().get(curveIndex).updateCurveCenter(Center[index]);
		} else {
			popMessageBox("calculateCurveCenter", "Please move curve closer to line segments!");
		}

	} //calculateCurveCenter

	// calc & return tangent point of a line & a circle
	public Point calculateTangentPoint(int lineIndex, int curveIndex) {
		double dx, dy, xt, yt, xy, len2 ;
		Point p1, p2, pc ;

		p1 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex).getStartPoint();
		p2 = roadDesign.getHorizontalAlignmentSegments().get(lineIndex).getEndPoint();
		pc = roadDesign.getHorizontalAlignmentSegments().get(curveIndex).getMidPoint();
		xy = p2.getY()* p1.getX()- p1.getY()* p2.getX();
		dx = p2.getX()- p1.getX();
		dy = p2.getY()- p1.getY(); 
		len2 = (dx*dx + dy*dy);
		xt = (dy * xy + (pc.getY()* dy + pc.getX()* dx) * dx) / len2;
		yt = (-dx * xy + (pc.getY()* dy + pc.getX()* dx) * dy) / len2;
		return GeoUtils.makePoint(xt, yt);
	}   // calculateTangentPoint

	/** Pop up a window to display message */    
	public void popElevationMarkerForm() {
		if (null != contourImage){
			elevationMarkerPopup = new ElevationMarkerPopup(app, sInfo);
			elevationMarkerPopup.build();
			elevationMarkerPopup.show();

		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popElevationMarkerForm

	/** Pop up a window to display message */    
	public void popInsertElevationMarker() {
		if (null != contourImage){
			// open a frame

			elevationMarkerInsertPopup = new ElevationMarkerInsertPopup(app, sInfo);

			elevationMarkerInsertPopup.build();
			elevationMarkerInsertPopup.show();

		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popInsertElevationMarker

	/** Pop up a window to display message */    
	public void popEditElevationMarker() {

		elevationMarkerEditPopup = new ElevationMarkerEditPopup(app, sInfo);	
		elevationMarkerEditPopup.build();
		elevationMarkerEditPopup.show();
	} // popEditElevationMarker


	/** Pop up a window to display curve setting */    
	public void popCurveSettings() {
		if (null != contourImage){

			curveSettingsPopup = new CurveSettingsPopup(app, roadDesign.getCurveRadius());	
			curveSettingsPopup.build();
			curveSettingsPopup.show();
		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popCurveSettings

	/** Pop up a window to display contour image setting */    
	public void popSettingsContour() {		

		contourSettingsPopup = new ContourSettingsPopup(app);	
		contourSettingsPopup.build();
		contourSettingsPopup.show();

	} // popSettingsContour

	public void popSettingsDesign() {
		designSettingsPopup = new DesignSettingsPopup(app);	
		designSettingsPopup.build();
		designSettingsPopup.show();
	}

	/** Pop up a window to edit current curve setting */    
	public void popEditCurveSettings() {
		curveSettingsEditPopup = new CurveSettingsEditPopup(app, idSegment);	
		curveSettingsEditPopup.build();
		curveSettingsEditPopup.show();

	} // popEditCurveSettings

	/** Pop up a window to display message */   
	public void popMessageBox(String caption, String message) {
		msgBox_title = caption ;
		msgBox_message = message ;
		popMsgBox_flag = true ;
	}

	private void popMessageBox1(String caption, String message) {
		// open a frame
		RoadGISPopup frame_msgbox = new RoadGISPopup(caption) ;
		//frame_msgbox.setLocation(400,50) ;
		frame_msgbox.setSize(310,150) ;
		frame_msgbox.setCenter() ;
		frame_msgbox.validate() ;
		frame_msgbox.setVisible(true) ;
		frame_msgbox.setResizable(false);
		//frame_msgbox.show() ;
		/*
        ActionListener frame_msgbox_ok_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {

                frame_msgbox.dispose() ;
            }
        } ;
		 */
		frame_msgbox.setLayout(new BorderLayout(1,1)) ;
		JTextArea myTitle = new JTextArea(message, 3, 60) ;
		myTitle.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		myTitle.setForeground(new Color(0,0,218)) ;
		frame_msgbox.setBackground(new Color(200, 200, 200)) ;
		frame_msgbox.add("Center",myTitle) ;

		//Button btn_ok = new Button(" OK ") ;
		//frame_msgbox.add("South",btn_ok) ;
		//btn_ok.addActionListener(frame_msgbox_ok_listener) ;
		//frame_msgbox.invalidate();
		frame_msgbox.show() ;
		frame_msgbox.toFront() ;
	} // popMessageBox

	public void print(){
		// print current frame
		//PrintUtilities.printComponent(this) ;    //, printPageFormat); 
		//PrintUtilities pu = new PrintUtilities(this) ;

		//hd_pu = new PrintUtilities(this) ;
		hd_pu.print();
	}   // print

	public void printPageSetup(){
		// print current frame
		//        PrintUtilities.printPageSetup() ;   
		//        PrintUtilities pu = new PrintUtilities(this) ;
		//hd_pu = new PrintUtilities(this) ;
		hd_pu.printPageSetup();
	}   // printPageSetup


	/** Pop up a window to display message */    
	public void popClearLandmarks(String caption, String message) {
		if (null != contourImage){


			landmarksClearPopup = new LandmarksClearPopup(app, caption, message);		
			landmarksClearPopup.build();
			landmarksClearPopup.show();

		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popClearLandMark

	/** Pop up a window to display message */    
	public void popDeleteSegment(String caption, String message) {
		if (null != contourImage){

			deleteSegmentPopup = new SegmentDeletePopup(app, caption, message);	
			deleteSegmentPopup.build();
			deleteSegmentPopup.show();


		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popDeleteSegments

	/** Pop up a window to display message */    
	public void popDeleteTangent(String caption, String message) {

		tangentDeletePopup = new TangentDeletePopup(app, dataSelIndex, caption, message);	
		tangentDeletePopup.build();
		tangentDeletePopup.show();
	} // popDeleteTangent

	/** Pop up a window to display & check using minimum curve radius */    
	public void popUpdateCurveRadius() {

		curveRadiusPopup = new CurveRadiusPopup(app, roadDesign.getMinHCurveRadius());	
		curveRadiusPopup.build();
		curveRadiusPopup.show();

	} // popUpdateCurveRadius

	/** Pop up a window to display message */    
	public void popClearAll(String caption, String message) {
		if (null != contourImage){

			clearAllPopup = new ClearAllPopup(app);	
			clearAllPopup.build();
			clearAllPopup.show();


		}
		else {
			popMessageBox("No Contour Map", NO_MAP_MSG);
		}

	} // popClearAll

	// Show Vertical Curve Design Panel
	public void popVerticalAlign(String _title) {
		if (frmVerticalAlign.isShowing()==false) 
		{

			verticalAlignmentPopup = new VerticalAlignmentFrame(app);

			verticalAlignmentPopup.build();
			verticalAlignmentPopup.show();
		}
		else {  // frmVerticalAlign already displayed
			frmVerticalAlign.show() ;
		}
		frmVerticalAlign.toFront();

	}   // popVerticalAlign

	public void popAbout(){
		if (frmAbout.isShowing()==false) {

			aboutPopup = new AboutPopup(app);			
			aboutPopup.build();
			aboutPopup.show();


		}
		else {
			frmAbout.show();
		}

	}

	public void popLandmarkData(){

		landmarkDataPopup = new LandmarksDataFrame(app);			
		landmarkDataPopup.build();
		landmarkDataPopup.show();
	}   // popLandmarkData ;

	public void popTangentData(){


		tangentDataPopup = new TangentDataFrame(app);			
		tangentDataPopup.build();
		tangentDataPopup.show();

	}   // pop Tangent (PC PT) Data ;

	public void popSaveVDesignB4Close() {       
		// open a frame
		saveVerticalDesignPopup = new SaveVerticalDesignPopup(app);			
		saveVerticalDesignPopup.build();
		saveVerticalDesignPopup.show();

	} // popSaveVDesignFileB4Close




	public void drawMyPoint(double x, double y) {

		// create features using the type defined
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(GeoUtils.getRoadMarkerType(roadDesign.getDefaultEPSG()));
		Point point = GeoUtils.makePoint(x, y);
		featureBuilder.add(point);
		featureBuilder.add(new Integer(designMarksCollection.size()+1));	
		SimpleFeature feature = featureBuilder.buildFeature(null);

		boolean addFeature = true;
		for(SimpleFeature element:designMarksCollection) {
			Point geom = (Point) element.getAttribute("location");
			if(point.isWithinDistance(geom, roadDesign.getSnapDistance())) {//
				addFeature = false;
				break;
			}
			//System.out.println("segment:" + element.getAttribute("index") + " is " + element.getDefaultGeometry());
		}

		
		if(addFeature) {
			System.out.println("drawMyPoint:" + point);
			//System.out.println("feature:" + feature);

			designMarksCollection.add(feature); //Add feature 1
			GeoUtils.refreshLayer(designMarksLayer);
			
		}

	}

	public void drawLine() {
		AlignmentSegment segment = new AlignmentSegment();
		segment.setStartPoint(GeoUtils.toJTSPoint(startPoint));
		segment.setEndPoint(GeoUtils.toJTSPoint(endPoint));
		roadDesign.addHorizontalAlignmentSegment(segment);
		hRoadDataCount ++;

		// debug
		//debugWindow.Text &= "P1=" & hRoadData(hRoadDataCount).getStartPoint.getX()& ", " & hRoadData(hRoadDataCount).getStartPoint.getY()
		//debugWindow.Text &= "P2=" & hRoadData(hRoadDataCount).getEndPoint.getX()& ", " & hRoadData(hRoadDataCount).getEndPoint.getY()& vbCrLf

		// save # of data in log buffer
		push2SegLogBuffer(hRoadDataCount);


		//Add TO the Markers DB
		if(null != DEMLayer) {
			DirectPosition2D pos = new DirectPosition2D(startPoint.getY(),startPoint.getX());//Note the Y and X because of a bug in how the TIF is saved - ordinate axes
			double[] elev =  DEMLayer.getCoverage().evaluate((DirectPosition)pos, new double[1]);
							
			System.out.println(" Elevation:" + elev[0]);
		}
		
		GeometryFactory geometryFactory = new GeometryFactory();
		ArrayList<Coordinate> positions = new ArrayList<Coordinate>();
		positions.add(GeoUtils.toCoordinate(startPoint));
		positions.add(GeoUtils.toCoordinate(endPoint));
		Coordinate[] coords = positions.toArray(new Coordinate[] {});
		LineString newLine = geometryFactory.createLineString(coords);

		// create features using the type defined
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(GeoUtils.getRoadSegmentFeatureType(roadDesign.getDefaultEPSG()));
		featureBuilder.add(newLine);
		System.out.println("newLine:" + newLine);

		SimpleFeature feature = featureBuilder.buildFeature(null);
		feature.setAttribute("index", designLinesCollection.size()+1);
		feature.setAttribute("type", "Straight Line");
		//feature.setAttribute("length", newLine.length(startPoint, endPoint));
		feature.setAttribute("length", newLine.getLength());
		feature.setAttribute("radius", 0.0);
		feature.setAttribute("center", null);
		designLinesCollection.add(feature); //Add feature 1


		if(null == feature.getDefaultGeometry()) {
			System.out.println( newLine + " is NULL");

		}else {

			//System.out.println("feature:" + feature);
			try {
				System.out.println( designLinesLayer.getFeatureSource().getFeatures().size() + " features in the layer");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//GeoUtils.refreshLayer(app.getMapContent(), designLinesLayer, designLinesCollection);
			GeoUtils.refreshLayer(designLinesLayer);

		}

		for(SimpleFeature element:designLinesCollection) {
			//System.out.println("segment:" + element.getAttribute("index") + " is " + element.getDefaultGeometry());
		}

	}

	public void drawCurve() {
		try {

			//Draw the radial lines
			//radialLinesLayer

			GeometryFactory geometryFactory = new GeometryFactory();
			CurvedGeometryFactory curvedFactory = new CurvedGeometryFactory(geometryFactory,Double.MAX_VALUE);
			PackedCoordinateSequence coords = new PackedCoordinateSequence.Double(new double[]{startPoint.getX(),startPoint.getY(),midPoint.getX(),midPoint.getY(),endPoint.getX(),endPoint.getY()}, 2 );

			CircularString arc = (CircularString) curvedFactory.createCurvedGeometry(coords);

			// create features using the type defined
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(GeoUtils.getRoadSegmentFeatureType(roadDesign.getDefaultEPSG()));
			featureBuilder.add(arc);

			int feature_id = designLinesCollection.size()+1;
			SimpleFeature feature = featureBuilder.buildFeature(null);
			feature.setAttribute("index", feature_id);
			feature.setAttribute("type", "Straight Line");
			feature.setAttribute("length", arc.getLength());			
			feature.setAttribute("radius", arc.getArcN(0).getRadius());
			feature.setAttribute("center", arc.getArcN(0).getCenter());

			designLinesCollection.add(feature); //Add feature 1
			GeoUtils.refreshLayer(designLinesLayer);

			//Now we add the structure Lines

			System.out.println("Handling the Radial Lines:");
			ArrayList<Coordinate> positions = new ArrayList<Coordinate>();
			positions.add(GeoUtils.toCoordinate(startPoint));
			positions.add(arc.getArcN(0).getCenter());
			positions.add(GeoUtils.toCoordinate(endPoint));
			Coordinate[] radialCoords = positions.toArray(new Coordinate[] {});
			LineString radialLine = geometryFactory.createLineString(radialCoords);
			featureBuilder = new SimpleFeatureBuilder(GeoUtils.getRoadRadialFeatureType(roadDesign.getDefaultEPSG()));
			featureBuilder.add(radialLine);
			SimpleFeature radial = featureBuilder.buildFeature(null);
			radial.setAttribute("index", designLinesCollection.size()+1);
			radial.setAttribute("feature", feature_id);
			radialElementsCollection.add(radial); //Add feature 1
			System.out.println("radialLine:" + radialLine);

			GeoUtils.refreshLayer(radialLinesLayer);


			AlignmentSegment segment = new AlignmentSegment();
			segment.setStartPoint(GeoUtils.toJTSPoint(startPoint));
			segment.setMidPoint(GeoUtils.toJTSPoint(midPoint));
			segment.setEndPoint(GeoUtils.toJTSPoint(endPoint));
			segment.setRadius(arc.getArcN(0).getRadius());
			segment.setSegmentCentre(GeoUtils.makePoint(arc.getArcN(0).getCenter()));

			roadDesign.addHorizontalAlignmentSegment(segment);
			hRoadDataCount ++;
			push2SegLogBuffer(hRoadDataCount);

		}catch(Exception e) {
			e.printStackTrace();
		}

		for(SimpleFeature feature2:designLinesCollection) {
			System.out.println("feature:" + feature2.getDefaultGeometry());
		}

	}


	// view horizontal road design only, w/o construction lines/circles
	public void viewRoadOnly() {
		viewRoadOnly_flag = true ;
		//repaint() ;
	}

	public void viewRoadDesign() {
		viewRoadOnly_flag = false ;
		//repaint() ;
	}

	public boolean lineHasStarted() {
		return line_started;
	}

	public void setLineStarted(boolean status) {
		this.curve_started = false;
		this.line_started = status;
	}

	public boolean isVerticalAlignmentOngoing() {
		return valign_flag;
	}

	public void setVerticalAlignmentStatus(boolean valign_flag) {
		this.valign_flag = valign_flag;
	}

	public boolean curveHasStarted() {
		return curve_started;
	}

	public void setCurveStarted(boolean status) {
		this.line_started = false;
		this.curve_started = status;
	}

	public boolean midpointFlagIsHigh() {
		return midpoint_flag;
	}

	public void setMidpointFlag(boolean status) {
		this.midpoint_flag = status;
	}

	public DirectPosition2D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(DirectPosition2D point) {

		boolean newPoint = true;
		for(SimpleFeature element:designMarksCollection) {
			Point geom = (Point) element.getAttribute("location");
			Point jtsPoint = GeoUtils.makePoint(point.getX(), point.getY());
			double distance = roadDesign.getSnapDistance();
			if (Math.abs(point.getX()) < 180 && Math.abs(point.getY())< 90) {//Likely Geographic
				distance = distance / 6378137.0;
			}
			if(jtsPoint.isWithinDistance(geom, distance)) {//
				newPoint = false;
				this.startPoint = new DirectPosition2D(geom.getX(), geom.getY());
				break;
			}
			//System.out.println("segment:" + element.getAttribute("index") + " is " + element.getDefaultGeometry());
		}
		if(newPoint) {
			this.startPoint = point;//new Coordinate(point.getX(), point.getY());

		}
		System.out.println("startPoint : " + startPoint);
	}

	public DirectPosition2D getMidPoint() {
		return midPoint;
	}

	public void setMidPoint(DirectPosition2D point) {
		boolean newPoint = true;
		for(SimpleFeature element:designMarksCollection) {
			Point geom = (Point) element.getAttribute("location");
			Point jtsPoint = GeoUtils.makePoint(point.getX(), point.getY());
			if(jtsPoint.isWithinDistance(geom, roadDesign.getSnapDistance())) {//
				newPoint = false;
				this.midPoint = new DirectPosition2D(geom.getX(), geom.getY());
				break;
			}
			//System.out.println("segment:" + element.getAttribute("index") + " is " + element.getDefaultGeometry());
		}
		if(newPoint) {
			this.midPoint = point;//new Coordinate(point.getX(), point.getY());
		}
		System.out.println("midPoint : " + midPoint);
	}

	public DirectPosition2D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(DirectPosition2D point) {
		boolean newPoint = true;
		for(SimpleFeature element:designMarksCollection) {
			Point geom = (Point) element.getAttribute("location");
			Point jtsPoint = GeoUtils.makePoint(point.getX(), point.getY());
			if(jtsPoint.isWithinDistance(geom, roadDesign.getSnapDistance())) {//
				newPoint = false;
				this.endPoint = new DirectPosition2D(geom.getX(), geom.getY());
				break;
			}
			//System.out.println("segment:" + element.getAttribute("index") + " is " + element.getDefaultGeometry());
		}
		if(newPoint) {
			this.endPoint = point;//new Coordinate(point.getX(), point.getY());
		}
		System.out.println("endPoint : " + endPoint);
	}


	public void setContourImage(Image image) {
		this.contourImage = (ContourImage) image;
	}

	public RoadDesign getRoadDesign() {
		return roadDesign;
	}

	public void updateSettings(RoadDesign myDB) {
		this.roadDesign = myDB;
	}

	public int gethRoadDataCount() {
		return hRoadDataCount;
	}

	public void sethRoadDataCount(int hRoadDataCount) {
		this.hRoadDataCount = hRoadDataCount;
	}

	public HorizontalStatusbar getHorizontalStatusBar() {
		return horizontalStatusBar;
	}

	public void setHorizontalStatusBar(HorizontalStatusbar horizontalStatusBar) {
		this.horizontalStatusBar = horizontalStatusBar;
	}

	public float getDraw_scale() {
		return draw_scale;
	}

	public void setDraw_scale(float draw_scale) {
		this.draw_scale = draw_scale;
	}

	public int getEndMarkSize() {
		return endMarkSize;
	}

	public void setEndMarkSize(int endMarkSize) {
		this.endMarkSize = endMarkSize;
	}



	public FeatureLayer getGridLinesLayer() {
		return gridLinesLayer;
	}

	public void setGridLinesLayer(SimpleFeatureSource source) {
		if(null == gridLinesLayer) {
			this.gridLinesLayer = new FeatureLayer(source, GeoUtils.getGridLineStyle());
			app.getMapContent().addLayer(gridLinesLayer);
		}
	}

	public FeatureLayer getPointsLayer() {
		return pointsLayer;
	}

	public void setPointsLayer(FeatureLayer pointsLayer) {
		this.pointsLayer = pointsLayer;
	}

	public GridCoverageLayer getDEMLayer() {
		return DEMLayer;
	}

	public void setDEMLayer(GridCoverageLayer dEMLayer) {
		DEMLayer = dEMLayer;
	}
}   // hDrawArea class
