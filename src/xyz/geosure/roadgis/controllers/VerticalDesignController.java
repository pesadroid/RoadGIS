package xyz.geosure.roadgis.controllers;
/*
 * vDrawArea.java
 * Vertical curve design class.
 *
 * Created on March 23, 2006, 1:14 PM
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
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.model.RoadDesign;
import xyz.geosure.roadgis.model.RoadGeoDB;
import xyz.geosure.roadgis.model.VCurve;
import xyz.geosure.roadgis.model.Vector3D;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.Quaternion;
import xyz.geosure.roadgis.utils.VectorUtils;
import xyz.geosure.roadgis.utils.print.PrintText;
import xyz.geosure.roadgis.views.DoubleBufferedPanel;
import xyz.geosure.roadgis.views.ProgressBar;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.toolbars.VerticalDesignToolbar;
import xyz.geosure.roadgis.views.toolbars.VerticalStatusbar;

public class VerticalDesignController extends DoubleBufferedPanel     implements MouseListener,  MouseMotionListener{
	private RoadGISApplication app = null;

	private RoadDesign roadDesign = new RoadDesign();              // shared data variables
	
	public int hRoadDataCount ;                     // horizontal design data count
	VerticalDesignToolbar tb;                                    // toolbar
	public VerticalStatusbar sb;                                  // status bar
	final int grid = 8;                             // drawarea grid size
	final int offsetX = 80 ;                        // draw area offset X
	final int offsetY = 30;                         // draw area offset Y
	final int data_viewer_Height = 360;             // draw area width
	final int data_viewer_Width = 480;              // draw area height
	final String newLine = "\n";                    // line separator
	int toolbarIndex = 0 ;                          // toolbar index
	final int GRID_HDIV = 60;                       // horizontal grid dividion
	final int GRID_VDIV = 60;                       // vertical grid division
	double ComputeStepSize = 10f;                    // 10 ft or 10 m
	Font myFont = new Font("Times New Roman", Font.PLAIN, 12);

	int gradeLogIndex = -1;
	int[] gradeLogBuffer = new int[16] ;            // undo, redo log
	//boolean display_dot_max = false;
	//boolean display_dot_min =false;
	double max_ele, min_ele ;                        // min/max elevation in elevation profile 
	double plotScaleX=0f, plotScaleY=0f ;            // plot scale X & Y
	int Xmin, Ymin, gridSizeX, gridSizeY ;          // drawing variables
	boolean isGradeConstructOn  = false;            // grade construction flag
	boolean viewElevationProfile = true;            // view elevation profile flag
	boolean viewMassDiagram = false;                // view mass diagram flag, 5/2/06 added
	double SSD;                                      // stop sight distance
	double Lm_crestK, Lm_sagK ;                      // horizontal distance required to effect 1% of change in slope
	boolean valid_vCurve = false;                   // valid vertical curve flag
	double max_hDist ;                               // horizontal/projected road length
	double total_roadLen ;                           // total road length
	MouseEvent me0, me1 ;                           // DB Null
	final int marker_size = 3;                      // end mark draw size
	double[] CutandFill ;                            // cur and fill data array
	double[] accuMass ;                              // accumulated mass
	double[] designElevation ;                       // design elevation profile
	int fillCutSteps ;                              // # of cut & fill steps
	RoadGeoDB[] vrmlPoints_roadCenter;              // Road center geometry DB
	Image eleImg = null;                            // elevation profile label image
	Image fillcutImg = null;                        // cut/fill profile label image
	// 4/4/06 added
	int PVI_index = -1;                             // PVI modification index

	// window frame =================
	RoadGISPopup frame_msgbox, frame_msgboxYesNo, frame_msgbox_viewVRML ;
	RoadGISPopup frame_msgboxClearAll, frame_msgboxClearCurves;
	RoadGISPopup frame_report = null ;

	RoadGISPopup frame_editVertCurveLen ;   // 10/9/06
	JFrame frmStationTable = new JFrame("View Station Data") ;
	JFrame frmDesignedElevationTable = new JFrame("View Designed Elevation Data") ;
	JFrame frmMassTable = new JFrame("View Mass Data") ;
	JFrame frmCutFillTable = new JFrame("View Cut and Fill Data") ;

	JComboBox listVertCurves ;         // Vertical Curve Index
	JLabel lblSSD ;                  // SSD label display in popVertCurveLenEdit
	JTextField txtCurveLen;          // vertical curvev length

	// mouse double click variables
	private boolean displayDoubleClickString = false;
	// The number of milliseconds that should bound a double click.
	static private long mouseDoubleClickThreshold = 300L;
	// The previous mouseUp event//s time.
	private long mouseUpClickTime = 0;
	private String stationPrintStr ;
	private String elevationPrintStr ;
	private String massPrintStr ;
	private String fill_cutPrintStr ;

	Runnable runThread0 = null ;    // stop on red light
	public Thread tSetReport ;
	public boolean setReport_flag = false ; // accessed from toolbarV
	public boolean setCurvelenEdit_flag = false ;   // accessed from toolbarV
	private boolean noCurve2Edit_flag = false ;
	private boolean popMsgBox_flag = false ;
	//private boolean popMsgBox_viewVRML_flag = false ;   // 11/15/06 added
	private String msgBox_title = "" ;
	private String msgBox_message = "" ;
	//private String msgBox_viewVRML_title = "" ;     // 11/15/06 added
	//private String msgBox_viewVRML_message = "" ;   // 11/15/06 added
	
	public VerticalDesignController(RoadGISApplication app)	{
		this.app = app;
	}
	public VerticalDesignController(RoadGISApplication app, VerticalDesignToolbar t, VerticalStatusbar s)	{
		this.app = app;
		tb = t;
		sb = s;
		setBackground(Color.white);
		t.parent = this;  
		s.parent = this;
		this.setBackground(new Color(220,220,220));
	}    

	// Object initialization
	public void init() {
		addMouseListener(this);
		addMouseMotionListener(this);

		// compute elevation step size
		//     if (myDB.getPreferredUnit()==1) {   // US unit
		//         ComputeStepSize = 10f ; // 10 ft
		//     } else if (myDB.getPreferredUnit()==2) {    // metric unit
		//          ComputeStepSize = 3.0f ;    // 3 meter
		//      }

		// prepare X, Y axis label min & max
		int i ;
		double my_ele=0f ;
		double dist=0f;
		double accu_dist=0f ;
		min_ele = 99999f;
		max_ele = 0f;
		Point lastMark ;
		MarkerType lastMarkType ;

		//roadDesign.setvConstructMarkCount(0);
		valid_vCurve = false;
		roadDesign.setvCurveCount(0);
		me0 = null;
		roadDesign.setImageScale(roadDesign.getContourImageResolution() / roadDesign.getContourScale());  //  // pixel/ft
		for (i=0;i<roadDesign.getElevationMarkCount();i++){
			// determine max min elevation
			my_ele = roadDesign.getElevationMarks().get(i).getElevation();
			if( my_ele > max_ele) { 
				max_ele = my_ele;
			}
			if (my_ele < min_ele) { 
				min_ele = my_ele;
			}

			// calculate accumulated distance, based on line/curve/tangent point
			if (i == 0) { 
				accu_dist = 0f;
			} else {
				lastMark = roadDesign.getElevationMarks().get(i - 1).getLocation();
				//System.out.println("X="+ConversionUtils.CStr(lastMark.getX())+", Y="+ConversionUtils.CStr(lastMark.getY())+", scale="+ConversionUtils.CStr(myDB.imageScale));

				lastMarkType = roadDesign.getElevationMarks().get(i-1).getMarkerType();
				switch (roadDesign.getElevationMarks().get(i).getMarkerType()) {
				case LINE:  // line
					// linear distance
					dist = VectorUtils.distanceOf(lastMark, roadDesign.getElevationMarks().get(i).getLocation()) / roadDesign.getImageScale();
					break;
				case CURVE:  // curve
					dist = calculateArcLength(i);    // radius in feet already
					break;
				case TANGENT:  // tangent point, i>0
					if (lastMarkType == MarkerType.LINE || lastMarkType == MarkerType.TANGENT ) {
						//previous point belongs to a line
						// linear distance
						dist = VectorUtils.distanceOf(lastMark, roadDesign.getElevationMarks().get(i).getLocation()) / roadDesign.getImageScale();
					} else if (lastMarkType == MarkerType.CURVE) { 
						// previous point belongs to a curve
						dist = calculateArcLength(i);
					}
					break;
				}   // switch
				//System.out.println("dist="+ConversionUtils.CStr(dist));
				accu_dist += dist;   // distance from landmark 0
			}
			roadDesign.getElevationMarks().get(i).setDistance(accu_dist);
			//System.out.println("accudist="+ConversionUtils.CStr(myDB.getElevationMarks().get(i).getDistance()));
		} // for 
		max_hDist = accu_dist;   // in  ft or meter

		//System.out.println("max_hDist="+ConversionUtils.CStr(max_hDist));

		// determine plot scale in both X & Y directions
		gridSizeY = ConversionUtils.CInt(Math.ceil((max_ele - min_ele) / 4f));
		Ymin = ConversionUtils.CInt(10 * Math.floor((min_ele-gridSizeY/2) / 10f));  // 4/5/06 modified

		gridSizeX = ConversionUtils.CInt(Math.ceil(max_hDist / 7f));
		plotScaleX = GRID_HDIV / gridSizeX;
		plotScaleY = GRID_VDIV / gridSizeY;

		double d1, d2, e1, e2, grade ;
		boolean gradeLimitFlag = false;
		d1 = roadDesign.getElevationMarks().get(0).getDistance();
		e1 = roadDesign.getElevationMarks().get(0).getElevation();
		for (i=1; i< roadDesign.getElevationMarkCount(); i++) {
			d2 = roadDesign.getElevationMarks().get(i).getDistance();
			e2 = roadDesign.getElevationMarks().get(i).getElevation();
			grade = (e2 - e1) / (d2 - d1);
			roadDesign.getElevationMarks().get(i).setGrade(grade);
			e1 = e2;
			d1 = d2;
			if ( ((Math.abs(grade) > roadDesign.getGradeLimit()) || (Math.abs(grade) < roadDesign.getMinGrade()) )
					&& (gradeLimitFlag==false) ) {
				gradeLimitFlag = true;
				//sb.setStatusBarText(1, "Grade calculation from horizontal design exceed grade limit.") ;
			}
		}

		push2GradeLogBuffer(roadDesign.getvConstructMarkCount());
		// ---------------------------------------------------
		double V1 ;
		if (roadDesign.getPreferredUnit() == 1) {
			V1 = roadDesign.getSpeedLimit() * 5280f / 3600f; // ft/sec
			SSD = V1 * roadDesign.getReactionTime() + V1 * V1 / (2 * roadDesign.getVehDecel());
			// ceil to 5
			SSD = ConversionUtils.CInt(Math.ceil(SSD / 5)) * 5;

			// compute crest curve length using AASHTO formula, assume Lm > SSD
			Lm_crestK = SSD*SSD / 2158f;              // US unit
			Lm_sagK = SSD*SSD / (400f + 3.5f * SSD);   // US unit
		} else if (roadDesign.getPreferredUnit() == 2) {
			V1 = roadDesign.getSpeedLimit() * 1000f / 3600f; // m/sec
			SSD = V1 * roadDesign.getReactionTime() + V1 * V1 / (2f * roadDesign.getVehDecel());
			// ceil to 5
			SSD = ConversionUtils.CInt(Math.ceil(SSD / 5)) * 5;

			// compute crest curve length using AASHTO formula, assume Lm > SSD
			Lm_crestK = SSD*SSD / 658f ;              // metric unit
			Lm_sagK = SSD*SSD / (120f + 3.5f * SSD);   // metric unit
		}
		URL url = getClass().getResource("/resources/" + "elevationtext.png");
		eleImg = Toolkit.getDefaultToolkit().getImage(url);
		url = getClass().getResource("/resources/" +"fillcuttext.png");
		fillcutImg = Toolkit.getDefaultToolkit().getImage(url);

		repaint();

		// =======================================================================
		// bring vertical design to top display thread
		// =====================================================================
		runThread0 = new Runnable() {
			public void run() {
				while (true) {
					if (popMsgBox_flag){
						popMessageBox1(msgBox_title, msgBox_message);
						popMsgBox_flag = false ;
						//    } else if (popMsgBox_viewVRML_flag){
						//        popMessageBoxViewVRML1(msgBox_viewVRML_title, msgBox_viewVRML_message);
						//        popMsgBox_viewVRML_flag = false ;
					} else if (setCurvelenEdit_flag) {
						newstatus(3, " Modify Curve Length") ;
						setCurvelenEdit_flag = false ;
					} else if (setReport_flag){
						newstatus(7, " Generate Report");
						setReport_flag = false ;
					} else if (noCurve2Edit_flag) {
						if (!valid_vCurve && roadDesign.getvCurveCount()>0) {
							// construction lines exist but no curves generated
							popMessageBox("No Vertical Curve(s)", "Please generate vertical curve(s) first!") ;
						} else {
							popMessageBox("No Vertical Curve(s)", "Please design your vertical curve first!") ;
						}
						noCurve2Edit_flag = false ;
					} else {
						tSetReport.yield();
						try {Thread.sleep(200) ;}
						catch (InterruptedException ie) {} ;
					}
				}
			}   // void run
		} ; // runThread 0
		tSetReport = new Thread(runThread0, "VertCurveDesign") ;
		tSetReport.start() ;
	}

	public void paint(Graphics gr) 
	{
		int i, x1=0, x2=0, y1=0, y2=0 ;
		Graphics2D g = (Graphics2D)gr ;
		Rectangle r = bounds();
		if(grid>0)
		{
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(1));
			g.drawRect(5,5, 600, 430);  // border
			g.setColor(Color.blue);
			g.setStroke(new BasicStroke(2));
			g.drawRect(80,30, 480, 360);
			// graph background color
			g.setColor(new Color(255,227,206)); //255,227,206
			g.fillRect(82, 32, 476, 356);

			// grid line
			g.setColor(Color.lightGray);
			g.setStroke(new BasicStroke(1));

			for (i=1;i<8;i++){
				g.drawLine(80+i*GRID_HDIV, 32, 80+i*GRID_HDIV, 388);
			}
			for (i=1;i<6;i++){
				g.drawLine(82, 30+i*GRID_VDIV, 558, 30+i*GRID_VDIV);
			}

			// paint X, Y axes labels
			g.setColor(Color.blue);

			//System.out.println("gridSizeX="+ConversionUtils.CStr(gridSizeX));
			//System.out.println("gridSizeY="+ConversionUtils.CStr(gridSizeY));
			for (i=0;i<7;i++){  // Y axis
				if (viewMassDiagram) {
					g.drawString(ConversionUtils.CStr(i*gridSizeY + Ymin), 30, 35+(6-i)*GRID_VDIV);
				} else {
					g.drawString(ConversionUtils.CStr(i*gridSizeY + Ymin), 40, 35+(6-i)*GRID_VDIV);
				}
			}
			for (i=0;i<9; i++) {    // X axis
				g.drawString(ConversionUtils.CStr(i*gridSizeX), 70+i*GRID_HDIV, 410) ;
			}
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(2));
			String unitStrY = "";
			String unitStr = "";
			if (roadDesign.getPreferredUnit()==1){
				if (viewMassDiagram) {
					unitStrY="yd^3";
				} else {
					unitStrY="ft";
				}
				unitStr="ft";
			} else if (roadDesign.getPreferredUnit()==2){
				if (viewMassDiagram) {
					unitStrY="m^3";
				} else {
					unitStrY="m";
				}
				unitStr="m";
			}
			g.drawString("("+unitStrY+")", 40, 20); // y axis unit label
			g.drawString("Distance ("+unitStr+")", 275, 430);

			// ===============================================
			if (viewElevationProfile) {
				// plot elevation data
				// Y JLabel
				g.drawImage(eleImg, 10,210-47,this) ;
				//plot out elevation vs. distance curve
				double grade ;
				for (i=0; i<roadDesign.getElevationMarkCount(); i++) {
					if (i == 0) { 
						// draw a starting mark only
						x1 = transform2DrawX(roadDesign.getElevationMarks().get(i).getDistance());
						y1 = transform2DrawY(roadDesign.getElevationMarks().get(i).getElevation());
						g.setColor(Color.magenta);
						g.setStroke(new BasicStroke(2));
						g.drawPolygon(getDnTriangleShape(x1, y1, marker_size));
					} else {
						// draw a line & end mark
						x2 = transform2DrawX(roadDesign.getElevationMarks().get(i).getDistance());
						y2 = transform2DrawY(roadDesign.getElevationMarks().get(i).getElevation());
						//draw same color on elevation landmarks, comment out on 2/23/06
						grade = Math.abs(roadDesign.getElevationMarks().get(i).getGrade());
						g.setColor(Color.orange);
						if ( (grade > roadDesign.getGradeLimit()) || (grade < roadDesign.getMinGrade()) ) {
							g.drawLine( x1, y1, x2, y2);
						} else {
							//g.DrawLine(blu_pen2, x1, y1, x2, y2)
							g.drawLine( x1, y1, x2, y2);
						}
						g.setColor(Color.magenta);
						g.drawPolygon( getDnTriangleShape(x2, y2, marker_size));
						x1 = x2;
						y1 = y2;
					}
				} // for elevationMarkCount
				// legend
				g.drawPolygon(getDnTriangleShape(offsetX+20, offsetY+10, marker_size));
				g.setFont(myFont);
				g.drawString("Station Landmark", offsetX+30, offsetY+10+myFont.getSize() / 2);
				// title
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawString("Grade Design", 250, 20);

				double d1, d2, e1, e2, my_grade ;
				int dg_x1=0, dg_y1=0, dg_x2=0, dg_y2=0;
				// redraw constructed grade lines &/ design curves
				if (roadDesign.getvConstructMarkCount() > 0) {
					for (i=0;i<roadDesign.getvConstructMarkCount();i++) {
						if (i == 0) { 
							x1 = transform2DrawX(roadDesign.getVerticalConstructMarks().get(i).getDistance());
							y1 = transform2DrawY(roadDesign.getVerticalConstructMarks().get(i).getElevation());
							g.setColor(Color.green);
							g.setStroke(new BasicStroke(2));
							g.drawPolygon(getUpTriangleShape(x1, y1, marker_size));
						} else {
							x2 = transform2DrawX(roadDesign.getVerticalConstructMarks().get(i).getDistance());
							y2 = transform2DrawY(roadDesign.getVerticalConstructMarks().get(i).getElevation());
							my_grade = Math.abs(roadDesign.getVerticalConstructMarks().get(i).getGrade());
							if ( (my_grade > roadDesign.getGradeLimit()) || (my_grade < roadDesign.getMinGrade())|| roadDesign.getVerticalConstructMarks().get(i).isPointOfVerticalTangentAndCurvatureOverlapping()) { 
								g.setColor(Color.red);
								g.drawLine(x1, y1, x2, y2);
							} else {
								g.setColor(Color.darkGray);
								g.drawLine(x1, y1, x2, y2);
							}
							g.setColor(Color.green);
							g.drawPolygon(getUpTriangleShape(x2, y2, marker_size));
							x1 = x2;
							y1 = y2;
						}
						g.setColor(Color.green);
						g.drawPolygon( getUpTriangleShape(offsetX+150, offsetY+10, marker_size));
						g.setFont(myFont);
						g.drawString("PVI", offsetX+160, offsetY+10 + myFont.getSize() / 2);

						if(valid_vCurve && i > 0 ) {
							if (i == 1) { 
								// draw 1st line from start point to PVC0
								dg_x1 = transform2DrawX(roadDesign.getVerticalConstructMarks().get(0).getDistance());
								dg_y1 = transform2DrawY(roadDesign.getVerticalConstructMarks().get(0).getElevation());
								dg_x2 = transform2DrawX(roadDesign.getVerticalCurves()[0].getPVC());
								dg_y2 = transform2DrawY(roadDesign.getVerticalCurves()[0].getPVC_Elevation());
								g.setColor(Color.blue);
								g.setStroke(new BasicStroke(2));
								g.drawLine(dg_x1, dg_y1, dg_x2, dg_y2);
								dg_x1 = dg_x2;
								dg_y1 = dg_y2;
							} else { // i>0
								// draw designed vertical curve
								int steps, j ;
								double step_size ;
								// draw PVC mark
								g.setColor(Color.red);
								g.setStroke(new BasicStroke(2));
								g.drawRect( dg_x1 - marker_size, dg_y1 - marker_size, ConversionUtils.CInt(2*marker_size), ConversionUtils.CInt(2*marker_size));
								g.drawPolygon( getRectangleShape( offsetX+220, offsetY+10, marker_size));
								g.drawString("PVC",  offsetX+230, offsetY+10+ConversionUtils.CInt(myFont.getSize() / 2));

								steps = ConversionUtils.CInt(0.5f * roadDesign.getVerticalCurves()[i-2].getCurveLen() * plotScaleX);
								step_size = roadDesign.getVerticalCurves()[i-2].getCurveLen() / steps;
								for (j=1; j<=steps; j++) {
									dg_x2 = transform2DrawX(roadDesign.getVerticalCurves()[i - 2].getPVC() + j * step_size);
									dg_y2 = transform2DrawY(roadDesign.getVerticalCurves()[i - 2].getDX_Elevation(j * step_size));
									g.setColor(Color.blue);
									g.setStroke(new BasicStroke(2));
									g.drawLine(dg_x1, dg_y1, dg_x2, dg_y2);
									dg_x1 = dg_x2;
									dg_y1 = dg_y2;
								}   //Next j
								// draw PVT mark
								g.setColor(Color.black);
								g.setStroke(new BasicStroke(2));
								g.drawPolygon(getDiamondShape(dg_x1, dg_y1, marker_size));
								g.drawPolygon(getDiamondShape(offsetX+290, offsetY+10, marker_size));
								g.drawString("PVT", offsetX+300, offsetY+10+ConversionUtils.CInt(myFont.getSize() / 2));

								// draw tangent line to next curve, PVT(i-1) to PVC(i)
								if (i == roadDesign.getvConstructMarkCount() - 1) { 
									// last condtruction point not PVI
									dg_x2 = transform2DrawX(roadDesign.getVerticalConstructMarks().get(i).getDistance());
									dg_y2 = transform2DrawY(roadDesign.getVerticalConstructMarks().get(i).getElevation());
								} else {
									dg_x2 = transform2DrawX(roadDesign.getVerticalCurves()[i - 1].getPVC());
									dg_y2 = transform2DrawY(roadDesign.getVerticalCurves()[i - 1].getPVC_Elevation());
								}
								g.setColor(Color.blue);
								g.setStroke(new BasicStroke(2));
								g.drawLine(dg_x1, dg_y1, dg_x2, dg_y2); 
								dg_x1 = dg_x2;
								dg_y1 = dg_y2;
							} // if i==1
						} // if valid_vCurve
					} //Next i
				}   // if getvConstructMarkCount()

				// draw temporary construction line
				if (isGradeConstructOn && me0 != null &&  me1 != null) { 
					//g.DrawRectangle(grn_pen2, me0.getX() - 2, me0.getY() - 2, 4, 4)
					//g.DrawRectangle(grn_pen2, me1.getX() - 2, me1.getY() - 2, 4, 4)
					d1 = transform2Distance(me0.getX());
					e1 = transform2Elevation(me0.getY());
					d2 = transform2Distance(me1.getX());
					e2 = transform2Elevation(me1.getY());

					my_grade = (e2 - e1) / (d2 - d1);

					//Dim currentPen As Pen
					if ( (Math.abs(my_grade)>roadDesign.getGradeLimit()) || (Math.abs(my_grade)<roadDesign.getMinGrade()) ){
						g.setColor(Color.red);
					} else {
						g.setColor(Color.black);
					}
					g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())));
					g.drawLine(me0.getX(), me0.getY(), me1.getX(), me1.getY());
					//   ToolTip1.getSuperElevation()tToolTip(data_viewer, "grade= " & (Math.Round(my_grade * 1000) / 1000).ToString() & _
					//   ", dist= " & Math.Round(d2).ToString & ", ele= " & Math.Round(e2).ToString)
				}
				// data button ============================
				if (valid_vCurve && !isGradeConstructOn) {
					g.setColor(Color.yellow);

					g.fillRect(570,9,31,14);  // data button
					g.setStroke(new BasicStroke(1));
					g.setColor(Color.black);
					//g.drawRect(569,8,33,16);
					g.drawLine(602,8,602,24) ;
					g.drawLine(569,24,602,24) ;
					g.setColor(Color.white);
					g.drawLine(569,8,602,8) ;
					g.drawLine(569,8,569,24) ;

					g.setStroke(new BasicStroke(2));
					g.setFont(myFont);
					g.setColor(Color.blue);
					g.drawString("Data", 575, 20);
				}
				// ==================================================
			} else if (viewMassDiagram) {    // view mass diagram
				// Y JLabel
				//g.drawImage(massdiagramImg, 10,210-47,this) ;
				// title

				g.setColor(Color.yellow);

				g.fillRect(570,9,31,14);  // data button
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.black);
				//g.drawRect(569,8,33,16);
				g.drawLine(602,8,602,24) ;
				g.drawLine(569,24,602,24) ;
				g.setColor(Color.white);
				g.drawLine(569,8,602,8) ;
				g.drawLine(569,8,569,24) ;
				g.setStroke(new BasicStroke(2));
				g.setFont(myFont);
				g.setColor(Color.blue);
				g.drawString("Data", 575, 20);

				g.setStroke(new BasicStroke(2));
				g.drawString("Mass Diagram", 250, 20);

				// draw a zero line
				y1 = transform2DrawY(0);
				g.setColor(Color.green);
				g.setStroke(new BasicStroke(1));
				g.drawLine(offsetX+1, y1, offsetX+data_viewer_Width-1, y1);
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawString("0", offsetX+5, y1);

				// view mass diagram mode
				if (fillCutSteps > 0) { 
					// calculation is ready
					// view cut/fill profile
					x1 = transform2DrawX(0);
					y1 = transform2DrawY(accuMass[0]);
					for (i = 1 ; i<fillCutSteps ; i++) {
						x2 = transform2DrawX(i * ComputeStepSize);
						y2 = transform2DrawY(accuMass[i]);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(2));
						g.drawLine( x1, y1, x2, y2);
						x1 = x2;
						y1 = y2;
					}
				}   // if fillCutSteps > 0

				// ==================================================
			} else {    // view cut fill profile

				g.setColor(Color.yellow);

				g.fillRect(570,9,31,14);  // data button
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.black);
				//g.drawRect(569,8,33,16);
				g.drawLine(602,8,602,24) ;
				g.drawLine(569,24,602,24) ;
				g.setColor(Color.white);
				g.drawLine(569,8,602,8) ;
				g.drawLine(569,8,569,24) ;
				g.setStroke(new BasicStroke(2));
				g.setFont(myFont);
				g.setColor(Color.blue);
				g.drawString("Data", 575, 20);

				// Y JLabel
				g.drawImage(fillcutImg, 10,210-47,this) ;
				// title
				g.setFont(myFont);
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawString("Fill-Cut Profile", 250, 20);
				// draw a zero line
				y1 = transform2DrawY(0);
				g.setColor(Color.green);
				g.setStroke(new BasicStroke(1));
				g.drawLine(offsetX+1, y1, offsetX+data_viewer_Width-1, y1);
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawString("0", offsetX+5, y1);
				// max fill/cut
				y1 = transform2DrawY(roadDesign.getMaxFill());
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(1));
				g.drawLine(offsetX+1, y1, offsetX+data_viewer_Width-1, y1);
				y1 = transform2DrawY(-roadDesign.getMaxCut());
				g.drawLine(offsetX+1, y1, offsetX+data_viewer_Width-1, y1);
				// draw legend
				g.drawLine( offsetX+20, offsetY+10, offsetX+40, offsetY+10);
				g.setColor(Color.red);
				g.drawString("Max Cut / Fill Limit", offsetX+55, offsetY+10 + myFont.getSize() / 2);
				// draw legend
				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawLine( offsetX+220, offsetY+10, offsetX+240, offsetY+10);
				g.drawString("Cut / Fill Curve", offsetX+255, offsetY+10 + myFont.getSize() / 2);

				// view cut/fill profile mode, // viewElevationProfile = false
				if (fillCutSteps > 0) { 
					// calculation is ready
					// view cut/fill profile
					x1 = transform2DrawX(0);
					y1 = transform2DrawY(CutandFill[0]);
					for (i = 1 ; i<fillCutSteps ; i++) {
						x2 = transform2DrawX(i * ComputeStepSize);
						y2 = transform2DrawY(CutandFill[i]);
						g.setColor(Color.blue);
						g.setStroke(new BasicStroke(2));
						g.drawLine( x1, y1, x2, y2);
						x1 = x2;
						y1 = y2;
					}
				}   // if fillCutSteps > 0
			}   // end if elevation or mass diagram or cut/fill profile
			/*
            g.setColor(new Color(113,255,113)); // light green

            for(int i=grid;i<r.height;i+=grid)
                g.drawLine(0,i,r.width,i);
            for(int i=grid;i<r.width;i+=grid)
                g.drawLine(i,0,i,r.height);

            g.setColor(new Color(208,208,208));

            for(int i=grid*10;i<r.height;i+=grid*10)
                g.drawLine(0,i,r.width,i);
            for(int i=grid*10;i<r.width;i+=grid*10)
                g.drawLine(i,0,i,r.height);
			 */
		}   // draw grid
	}    
	public void newstatus(int index, String str)
	{
		toolbarIndex = index ;
		sb.setStatusBarText(0, str) ;

		switch (toolbarIndex) {
		case 0: // construct on
			if (!viewElevationProfile) {
				viewElevation();
			}
			isGradeConstructOn = true;
			//sb.setStatusBarText(0, "Grade construction tool ON") ; //Status: 
			//edit_undo.Enabled = True
			//edit_redo.Enabled = True
			break ;
		case 1: // construct off
			if (!viewElevationProfile) {
				viewElevation();
			}
			isGradeConstructOn = false;
			//sb.setStatusBarText(0, "Grade construction tool OFF"); //Status: 
			//edit_undo.Enabled = False
			//edit_redo.Enabled = False
			break ;
		case 2: // calc PVI
			if (!viewElevationProfile) {
				viewElevation();
			}
			vertAlign();
			//sb.setStatusBarText(0, str) ; //Status: 
			break ;
		case 3: // modify vertical curve
			if (!viewElevationProfile) {
				viewElevation();
			}
			popVertCurveLenEdit();
			break ;
		case 4: // elevation profile
			viewElevation();
			break ;
		case 5: // fill and cut profile
			viewFillCut();
			break ;
		case 6: // mass diagram
			view_MassDiagram(); 
			break ;
		case 7: // report
			popReport();
			//sb.setStatusBarText(0, str) ; //Status: 
			break ;
		case 8: // anomation
			popAnimation3D();
			//sb.setStatusBarText(0, str) ; //Status: 
			break ;
		}
		repaint();
	}

	// generate 3D animation model & display it on web browser
	public void popAnimation3D() {
		if (valid_vCurve) {
			// generate 3D VRML model
			createVRMLFile();
			try
			{
				String osinfo = System.getProperty("os.name");
				String osarch = System.getProperty("os.arch");
				//System.out.println(osinfo+","+osarch);
				String filename="" ;
				if (osinfo.indexOf("Windows")>=0) {
					//    filename = "c:\\roaddesign.html" ;  // "C:\\Documents and Settings\\All Users\\Desktop\\roaddesign.html"
					String username = System.getProperty("user.name");
					filename =  "C:\\Documents and Settings\\"+username+"\\Desktop\\roaddesign.html" ;
					boolean exists = (new File(filename)).exists();
					if (!exists) { 
						// if desktop is not available, use default directory
						filename = "c:\\roaddesign.html" ; 
					}
				} else {    //if (osinfo.indexOf("Linux")>=0){
					filename = "roaddesign.html" ;
				}
				//AppletContext ac = myApplet.getAppletContext(); 
				//File file = new File(filename);
				//URL u = file.toURL();   //new URL("file:/c:/roaddesign.html");
				//          System.out.println(myApplet.toString() + ", url="+u.toString());
				//ac.showDocument(u, "_blank");
				//_blank to open page in new window		
				popMessageBox("3D Animation", 
						"*** If 3D animation window doesn't pop up \n" + 
								"automatically, please open roaddesign.html file\n" +
								"manually.\n" +
								"Windows: on PC Destop or in C:\\ directory, \n" + 
								"    Mac: in HD directory, \n" +
								"  Linux: in root directory"
						);
			}
			catch (Exception e){
				//do nothing
				System.out.println(e.toString());
				sb.setStatusBarText(1, e.toString()) ; //"Error: "+

			} // end of try
		} else {
			// invalid vertical curve
			popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
		}    
	}
	/** Creates a new instance of vDrawArea */
	public VerticalDesignController() {
	}

	public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
	}

	public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
		if (PVI_index >= 0) {
			// update PVI end point
			roadDesign.getVerticalConstructMarks().get(PVI_index).setDistance(transform2Distance(mouseEvent.getX()));
			roadDesign.getVerticalConstructMarks().get(PVI_index).setElevation(transform2Elevation(mouseEvent.getY()));

			// 10/13/06 added
			roadDesign.getVerticalConstructMarks().get(PVI_index).setPVTnCurvatureOverlap(false) ;
			if ( (PVI_index+1<roadDesign.MAX_MARKERS) && (roadDesign.getVerticalConstructMarks().get(PVI_index+1) != null) ) {
				roadDesign.getVerticalConstructMarks().get(PVI_index+1).setPVTnCurvatureOverlap(false) ;
			}
			//myDB.getvConstructMarks().get(PVI_index+2].PVTnC_Overlap = false ;

			repaint();
			System.out.println("here" + roadDesign.getVerticalConstructMarks().get(PVI_index).getDistance() + "m, "+ roadDesign.getVerticalConstructMarks().get(PVI_index).getElevation());
		}
	}

	public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
	}

	public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
	}

	public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
		if (isGradeConstructOn) { 
			//Me.Cursor.Current = System.Windows.Forms.Cursors.Cross
		} else {
			//Me.Cursor.Current = System.Windows.Forms.Cursors.Arrow
		}
		me1 = mouseEvent;
		repaint();
		//update();
	}

	public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
		if ((isGradeConstructOn==false) && (viewElevationProfile==true)) { 
			// not in cut/fill construction mode
			int i, x0=0, y0=0, x1, y1, indexFound, segFound ;
			double cosine ;
			double sf;
			Vector2D v1, v2 ;
			indexFound = -1;
			segFound = -1;
			for (i=0;i<roadDesign.getElevationMarkCount();i++) {
				x1 = transform2DrawX(roadDesign.getElevationMarks().get(i).getDistance());
				y1 = transform2DrawY(roadDesign.getElevationMarks().get(i).getElevation());
				if (i == 0) { 
					x0 = x1;
					y0 = y1;
				}
				if (VectorUtils.distanceOf(new GeometryFactory().createPoint(new Coordinate(x1, y1)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY()))) <= 2 * marker_size) { 
					indexFound = i;
					break;

				} else if (i > 0) { 
					// check if segment selected
					v1 = VectorUtils.vector(new GeometryFactory().createPoint(new Coordinate(x0, y0)), new GeometryFactory().createPoint(new Coordinate(x1, y1)));
					v2 = VectorUtils.vector(new GeometryFactory().createPoint(new Coordinate(x0, y0)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY())));
					cosine = VectorUtils.getCosTheta(v1, v2);
					sf = Math.abs(v2.getX() / v1.getX());
					if ((cosine >= 0.99f) && (sf <= 1)) { // Then // < 1 degree, item selected
						sb.setStatusBarText(0, "(" + ConversionUtils.CStr(i + 1) + ") grade= " + ConversionUtils.CStr(roadDesign.getElevationMarks().get(i).getGrade()));
						break;
					}
					x0 = x1;
					y0 = y1;

				}
			} // for i
			if (indexFound >= 0) { 
				sb.setStatusBarText(0, "(" + ConversionUtils.CStr(indexFound + 1) + ") Dist=" + ConversionUtils.CStr(roadDesign.getElevationMarks().get(indexFound).getDistance()) + ", Ele=" + ConversionUtils.CStr(roadDesign.getElevationMarks().get(indexFound).getElevation()));
			} else {
				for (i=0;i<roadDesign.getvCurveCount();i++) {
					x1 = transform2DrawX(roadDesign.getVerticalCurves()[i].getPVC());
					y1 = transform2DrawY(roadDesign.getVerticalCurves()[i].getPVC_Elevation());
					if (VectorUtils.distanceOf(new GeometryFactory().createPoint(new Coordinate(x1, y1)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY()))) <= 2 * marker_size) { 
						indexFound = i;
						sb.setStatusBarText(0, "(" + ConversionUtils.CStr(indexFound + 1) + ") PVC=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVC())+ ", Ele=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVC_Elevation()));
						break;
					}
					x1 = transform2DrawX(roadDesign.getVerticalCurves()[i].getPVT());
					y1 = transform2DrawY(roadDesign.getVerticalCurves()[i].getPVT_Elevation());
					if (VectorUtils.distanceOf(new GeometryFactory().createPoint(new Coordinate(x1, y1)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY()))) <= 2 * marker_size) { 
						indexFound = i;
						sb.setStatusBarText(0, "(" + ConversionUtils.CStr(indexFound + 1)+ ") PVT=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVT())+ ", Ele=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVT_Elevation()));
						break;
					}
					x1 = transform2DrawX(roadDesign.getVerticalCurves()[i].getPVI());
					y1 = transform2DrawY(roadDesign.getVerticalCurves()[i].getPVI_e());
					if (VectorUtils.distanceOf(new GeometryFactory().createPoint(new Coordinate(x1, y1)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY()))) <= 2 * marker_size) { 
						indexFound = i;
						sb.setStatusBarText(0, "(" + ConversionUtils.CStr(indexFound + 1)+ ") PVI=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVI())+ ", Ele=" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[indexFound].getPVI_e()));
						break;
					}

				}   //Next i

			}   // if indexFound >= 0
			// 4/4/06 added
			if (valid_vCurve == false) { 
				// no vertical curve was generated yet, 4/4/06 added
				for (i=0; i<roadDesign.getvConstructMarkCount();i++) {
					x1 = transform2DrawX(roadDesign.getVerticalConstructMarks().get(i).getDistance());
					y1 = transform2DrawY(roadDesign.getVerticalConstructMarks().get(i).getElevation());
					if (VectorUtils.distanceOf(new GeometryFactory().createPoint(new Coordinate(x1, y1)), new GeometryFactory().createPoint(new Coordinate(mouseEvent.getX(), mouseEvent.getY()))) <= 2 * marker_size) { 
						// found vConstructMark end point, 4/4/06
						PVI_index = i;
						//System.out.println("Found "+PVI_index);
						break ;
					}   //End If
				}   //Next i
			}   // valid_vCurve

		} //if ((isGradeConstructOn==false) && (viewElevationProfile==true))
	}

	public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
		long eventTime = System.currentTimeMillis();
		long timeDiff;

		timeDiff = eventTime - mouseUpClickTime;

		if (timeDiff<300L) {
			// double click
			// Display the event time information.
			//System.out.println ("Current event :" + eventTime +
			//    ":   Previous event :" + mouseUpClickTime +
			//    ":   Difference :" + timeDiff + ":");
			isGradeConstructOn = false;
			sb.setStatusBarText(0, "Grade construction tool OFF"); //Status: 
		} else {
			mouseUp(mouseEvent);
		}
		mouseUpClickTime = eventTime;
	}

	public void mouseUp(java.awt.event.MouseEvent mouseEvent) {   
		// added 4/4/06
		if (PVI_index > 0) { 
			// update previous grade info
			double d1, d2, e1, e2, my_grade ;
			d1 = roadDesign.getVerticalConstructMarks().get(PVI_index - 1).getDistance();
			e1 = roadDesign.getVerticalConstructMarks().get(PVI_index - 1).getElevation();
			d2 = roadDesign.getVerticalConstructMarks().get(PVI_index).getDistance();
			e2 = roadDesign.getVerticalConstructMarks().get(PVI_index).getElevation();
			my_grade = (e2 - e1) / (d2 - d1);
			roadDesign.getVerticalConstructMarks().get(PVI_index).setGrade(my_grade);
			// update next grade
			if (PVI_index < roadDesign.getvConstructMarkCount() - 1) {
				d1 = roadDesign.getVerticalConstructMarks().get(PVI_index + 1).getDistance();
				e1 = roadDesign.getVerticalConstructMarks().get(PVI_index + 1).getElevation();
				my_grade = (e1 - e2) / (d1 - d2);
				roadDesign.getVerticalConstructMarks().get(PVI_index + 1).setGrade(my_grade);
			}

			//updateVCurve(PVI_index);  // comment out 2/28/07
			if (PVI_index > 1) {    // update previous curve
				updateVCurve(PVI_index - 1);
			}
			if (PVI_index < roadDesign.getvConstructMarkCount() - 2) {
				// update next curve
				updateVCurve(PVI_index + 1);
			}
			PVI_index = -1;  // reset PVI_index
		}   // PVI_index>0

		// 10/10/06 added
		// ========================
		// view mass diagram or view fill cut
		int mx, my ;
		mx = mouseEvent.getX() ;
		my = mouseEvent.getY() ;
		if (mx>=570 && mx <=600 && my>=9 && my<=23) {
			if (viewElevationProfile == false) {
				// click Data
				if (viewMassDiagram) {
					//saveMassDiagram() ;
					popMassData() ;
					//popMessageBox("Save Mass Diagram", "Save Mass Diagram") ;
				} else {
					// view fill amd cut
					//saveCutAndFill() ;
					popCutAndFillData() ;
					//popMessageBox("Save Fill Cut Data", "Save Fill Cut Data") ;
				}
			} else {
				// designed elevation profile
				popDesignedElevationData() ;
			}
		}   // if click data box

		// ========================
		if (isGradeConstructOn) { //
			if ((me0==null)&&(roadDesign.getvConstructMarkCount() > 1)) { 
				me0 = mouseEvent;
				popVertCurveExists("Vertical Curve Design","Current vertical curve design exists.\nStart new design?");

			} else if (mouseEvent.getX()>=82 && mouseEvent.getX()<= 558
					&& mouseEvent.getY()>=32 && mouseEvent.getY()<= 388 ) {

				// cut/fill construction mode
				AlignmentMarker marker = new AlignmentMarker();
				marker.setDistance(transform2Distance(mouseEvent.getX()));
				marker.setElevation(transform2Elevation(mouseEvent.getY()));
				roadDesign.addVerticalConstructionMarker(marker);
				
				if (roadDesign.getvConstructMarkCount() > 0) { 
					// save grade info 
					double d1, d2, e1, e2, my_grade ;
					d1 = transform2Distance(me0.getX());
					e1 = transform2Elevation(me0.getY());
					d2 = transform2Distance(mouseEvent.getX());
					e2 = transform2Elevation(mouseEvent.getY());

					my_grade = (e2 - e1) / (d2 - d1);
					roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount()).setGrade(my_grade);
					if (roadDesign.getvConstructMarkCount() > 1 ) {
						// initialize vertical curve element
						roadDesign.getVerticalCurves()[roadDesign.getvCurveCount()] = new VCurve(roadDesign.getMinVCurveLen());
						// calculate length of crest/sag curves
						double G1, G2, grade_diff_A ;
						G1 = 100f * roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount() - 1).getGrade();
						G2 = 100f * roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount()).getGrade();
						if ( (G1 != 0) && (G2 != 0) ) { 
							grade_diff_A = G1 - G2;
							if (grade_diff_A > 0) { 
								roadDesign.getVerticalCurves()[roadDesign.getvCurveCount()].setCurveLen(Lm_crestK * grade_diff_A, SSD);
							} else if (grade_diff_A < 0) { 
								roadDesign.getVerticalCurves()[roadDesign.getvCurveCount()].setCurveLen(Lm_sagK * Math.abs(grade_diff_A), SSD);
							} else {
								// no grade changes, no vertical alignment
								roadDesign.getVerticalCurves()[roadDesign.getvCurveCount()].setCurveLen(0, SSD);
							}
						} else {
							//both grades are 0, no vertical alignment
							roadDesign.getVerticalCurves()[roadDesign.getvCurveCount()].setCurveLen(0, SSD);
						}   // if G1, G2
						roadDesign.setvCurveCount(roadDesign.getvCurveCount() + 1);
					}   // if myDB.getvConstructMarkCount() > 1
					repaint();
					//data_viewer.Invalidate()
				}   //if myDB.getvConstructMarkCount() > 0

				//roadDesign.setvConstructMarkCount(roadDesign.getvConstructMarkCount() + 1);
				me0 = mouseEvent;
				// save # of data in log buffer
				push2GradeLogBuffer(roadDesign.getvConstructMarkCount());

			}   // if me != null
		} // if isGradeConstructOn
	}

	public void updateVCurve(int index) {
		// calculate length of crest/sag curves
		double G1, G2, grade_diff_A ;
		G1 = 100 * roadDesign.getVerticalConstructMarks().get(index - 1).getGrade();
		G2 = 100 * roadDesign.getVerticalConstructMarks().get(index).getGrade();
		if (G1 != 0 && G2 != 0) { 
			grade_diff_A = G1 - G2;
			if (grade_diff_A > 0 ) {
				//System.out.println("1 index="+index) ;
				roadDesign.getVerticalCurves()[index-1].setCurveLen(Lm_crestK * grade_diff_A, SSD);
			} else if (grade_diff_A < 0) { 
				//System.out.println("2 index="+index) ;
				roadDesign.getVerticalCurves()[index-1].setCurveLen(Lm_sagK * Math.abs(grade_diff_A), SSD);
			} else {
				// no grade changes, no vertical alignment
				roadDesign.getVerticalCurves()[index-1].setCurveLen(0, SSD);
			}
		} else {
			//both grades are 0, no vertical alignment
			roadDesign.getVerticalCurves()[index-1].setCurveLen(0, SSD) ;
		}

	}   // update VCurve

	public double checkVCurveLen(int index, double L) {
		// calculate length of crest/sag curves
		double Lm = 0f ;
		double G1, G2, grade_diff_A ;
		G1 = 100 * roadDesign.getVerticalConstructMarks().get(index - 1).getGrade();
		G2 = 100 * roadDesign.getVerticalConstructMarks().get(index).getGrade();
		if (G1 != 0 && G2 != 0) { 
			grade_diff_A = G1 - G2;
			if (grade_diff_A > 0 ) {
				Lm = roadDesign.getVerticalCurves()[index - 1].checkCrestLm(roadDesign.getPreferredUnit(), grade_diff_A, SSD, L); 
			} else if (grade_diff_A < 0) { 
				Lm = roadDesign.getVerticalCurves()[index - 1].checkSagLm(roadDesign.getPreferredUnit(), Math.abs(grade_diff_A), SSD, L); 
			} else {
				// no grade changes, no vertical alignment
				Lm = roadDesign.getMinVCurveLen() ;
			}
		} else {
			//both grades are 0, no vertical alignment
			Lm = roadDesign.getMinVCurveLen() ;
		}
		return Lm ;
	}   // check min vertical Curve length

	public double calculateArcLength(int idx) {
		Point curve_ctr ;
		int segIndex = roadDesign.getElevationMarks().get(idx).getParentIndex();
		//System.out.println("idx="+idx+", parent index="+segIndex) ;
		double myRadius = roadDesign.getHorizontalAlignmentSegments().get(segIndex).getRadius();
		double dist ;
		if (myRadius <= 0f ) {
			// wrong landmark DB, pop error message
			String str = new Integer(idx+1).toString();
			popMessageBox("Station Elevation Data","Invalid station elevation data (" + str + "). Please check station elevation data.");
			return -999;
		} else {
			curve_ctr = roadDesign.getHorizontalAlignmentSegments().get(segIndex).getStartPoint();
			double theta ;
			Vector2D vec1, vec2 ;
			vec1 = VectorUtils.vector(curve_ctr, roadDesign.getElevationMarks().get(idx).getLocation());
			vec2 = VectorUtils.vector(curve_ctr, roadDesign.getElevationMarks().get(idx-1).getLocation());
			theta = Math.acos(VectorUtils.getCosTheta(vec1, vec2));
			dist = myRadius * theta ;// curve distance
			return new Float(dist).doubleValue();
		}
	} // calculateArcLength

	/** Pop up a window to display message */   
	public void popMessageBox(String caption, String message) {
		msgBox_title = caption ;
		msgBox_message = message ;
		popMsgBox_flag = true ;
	}

	private void popMessageBox1(String caption, String message) {
		// open a frame
		frame_msgbox = new RoadGISPopup(caption) ;
		frame_msgbox.setLocation(500,5) ;
		frame_msgbox.setSize(300,150) ;
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

		//JButton btn_ok = new JButton(" OK ") ;
		//frame_msgbox.add("South",btn_ok) ;
		//btn_ok.addActionListener(frame_msgbox_ok_listener) ;
		//     frame_msgbox.invalidate();
		frame_msgbox.show() ;
		frame_msgbox.toFront() ;
	} // popMessageBox1    

	/** Pop up a window to display message */   
	/*
    public void popMessageBoxViewVRML(String caption, String message) {
        msgBox_viewVRML_title = caption ;
        msgBox_viewVRML_message = message ;
        popMsgBox_viewVRML_flag = true ;
    }

    private void popMessageBoxViewVRML1(String caption, String message) {
        // open a frame
        frame_msgbox_viewVRML = new myWindow(caption) ;
        frame_msgbox_viewVRML.setLocation(500,5) ;
        frame_msgbox_viewVRML.setSize(300,200) ;
        frame_msgbox_viewVRML.validate() ;
        frame_msgbox_viewVRML.setVisible(true) ;
        frame_msgbox_viewVRML.setResizable(false);
        //frame_msgbox_viewVRML.show() ;

        ActionListener frame_msgbox_ok_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {
                // view VRML file
                 try
                {
                    AppletContext a = myApplet.getAppletContext(); 
                    URL u = new URL("file:/c:/roaddesign.html"); 
                    a.showDocument(u,"_blank");
                    //_blank to open page in new window		
                }
                catch (Exception e){
                        //do nothing
                    sb.setStatusBarText(1, "Error: Manual file "+e.toString()) ;
                }   // try
                frame_msgbox_viewVRML.dispose() ;
            }
        } ;
        ActionListener frame_msgbox_close_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {
                frame_msgbox_viewVRML.dispose() ;
            }
        } ;

        frame_msgbox_viewVRML.setLayout(new GridBagLayout()) ;
        GridBagConstraints c = new GridBagConstraints() ;
        c.fill = GridBagConstraints.BOTH ; // component grows in both directions
        c.weightx = 1.0 ; c.weighty = 1.0 ;

        c.gridx = 0 ; c.gridy = 0; c.gridwidth = 4 ; c.gridheight = 4 ;
        c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides


        TextArea myTitle = new TextArea(message, 3, 60) ;
        myTitle.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
        myTitle.setForeground(new Color(0,0,218)) ;
        frame_msgbox_viewVRML.setBackground(new Color(200, 200, 200)) ;
        frame_msgbox_viewVRML.add(myTitle, c) ;

        c.gridx = 0 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1 ;
        frame_msgbox_viewVRML.add(new JLabel(" "),c) ;
        c.gridx = 1 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1 ;
        frame_msgbox_viewVRML.add(new JLabel(" "),c) ;
        c.gridx = 2 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1 ;
        JButton btn_ok = new JButton(" View ") ;
        frame_msgbox_viewVRML.add(btn_ok, c) ;
        btn_ok.addActionListener(frame_msgbox_ok_listener) ;
        JButton btn_close = new JButton(" Close ") ;
        c.gridx = 3 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1 ;
        frame_msgbox_viewVRML.add(btn_close, c) ;
        btn_close.addActionListener(frame_msgbox_close_listener) ;

   //     frame_msgbox.invalidate();
        frame_msgbox_viewVRML.show() ;
        frame_msgbox_viewVRML.toFront() ;
    } // popMessageBoxViewVRML 
	 */
	
	 
	public void push2GradeLogBuffer(int _myLandmarkCount){

		// save # of data into log buffer
		if (gradeLogIndex == gradeLogBuffer.length - 1) { 
			// buffer fulled
			// shift forward by 1
			int i ;
			for (i=0; i<gradeLogIndex; i++) {
				gradeLogBuffer[i] = gradeLogBuffer[i + 1];
			}
			gradeLogBuffer[gradeLogIndex] = _myLandmarkCount;
		} else {
			gradeLogIndex += 1;
			gradeLogBuffer[gradeLogIndex] = _myLandmarkCount;
		}
	}

	public int popGradeLogBuffer() {
		// pop the current # of landmark data from log buffer
		if (gradeLogIndex > 0) {
			gradeLogIndex -= 1;
			return gradeLogBuffer[gradeLogIndex];
		} else {
			return -99;
		}
	}  // popGradeLogBuffer

	private int transform2DrawY(double value) {
		return offsetY+data_viewer_Height - ConversionUtils.CInt((value - Ymin) * plotScaleY);
	}

	private int transform2DrawX(double value) {
		return offsetX+ConversionUtils.CInt(value*plotScaleX);
	}
	// transform from screen clicked y pixel to elevation in ft/m
	private double transform2Elevation(int value) {
		return Ymin + (offsetY+data_viewer_Height - value) / plotScaleY;
	}
	// transform from screen clicked x pixel to distance // ft/m
	private double transform2Distance(int value) {
		return ((value-offsetX) / plotScaleX);
	}
	public Polygon getDiamondShape(int x , int y , int size ) {
		Polygon myPolygon = new Polygon();

		myPolygon.addPoint(x + size, y) ;
		myPolygon.addPoint(x, y + size) ;
		myPolygon.addPoint(x - size, y);
		myPolygon.addPoint(x, y - size);
		myPolygon.addPoint(x + size, y) ;
		return myPolygon;
	}
	public Polygon getRectangleShape(int x , int y , int size ) { 
		Polygon myPolygon = new Polygon();
		myPolygon.addPoint(x + size, y + size);
		myPolygon.addPoint(x - size, y + size);
		myPolygon.addPoint(x - size, y - size);
		myPolygon.addPoint(x + size, y - size);
		myPolygon.addPoint(x + size, y + size);
		return myPolygon;
	}
	public Polygon getDnTriangleShape(int x , int y , int size ) { 
		Polygon myPolygon = new Polygon();
		myPolygon.addPoint(x, y + size);
		myPolygon.addPoint(x - size, y - size);
		myPolygon.addPoint(x + size, y - size);
		myPolygon.addPoint(x, y + size);
		return myPolygon;
	}
	public Polygon getUpTriangleShape(int x , int y , int size ) { 
		Polygon myPolygon = new Polygon();
		myPolygon.addPoint(x + size, y + size);
		myPolygon.addPoint(x - size, y + size);
		myPolygon.addPoint(x, y - size);
		myPolygon.addPoint(x + size, y + size);
		return myPolygon;
	}
	public void popVertCurveExists(String caption, String message) {
		// open a frame
		frame_msgboxYesNo = new RoadGISPopup(caption) ;
		frame_msgboxYesNo.setLocation(400,200) ;
		frame_msgboxYesNo.setSize(300,150) ;
		frame_msgboxYesNo.validate() ;
		frame_msgboxYesNo.setVisible(true) ;
		frame_msgboxYesNo.setResizable(false) ;

		ActionListener frame_msgbox_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				//roadDesign.setvConstructMarkCount(1);
				valid_vCurve = false;
				roadDesign.setvCurveCount(0);
				frame_msgboxYesNo.dispose() ;
				me0=null;
				repaint();
			}
		} ;
		ActionListener frame_msgbox_no_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				frame_msgboxYesNo.dispose() ;
			}
		} ;

		frame_msgboxYesNo.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;

		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 2 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
		JTextArea myMsg = new JTextArea(message,3,60) ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		frame_msgboxYesNo.setBackground(new Color(200, 200, 200)) ;
		frame_msgboxYesNo.add(myMsg,c) ;
		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Yes ") ;
		frame_msgboxYesNo.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_msgbox_yes_listener) ;
		c.gridx = 1 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		frame_msgboxYesNo.add(btn_no, c) ;
		btn_no.addActionListener(frame_msgbox_no_listener) ;

		frame_msgboxYesNo.invalidate();
		frame_msgboxYesNo.show() ;
		frame_msgboxYesNo.toFront() ;

	} // popClearLandMark

	/** Pop vertical curve edit screen if curves exist */    
	public void popVertCurveLenEdit() { // 10/9/06
		if (roadDesign.getvCurveCount()>0 && valid_vCurve) {
			// open a frame
			frame_editVertCurveLen = new RoadGISPopup("Edit Vertical Curve") ;
			frame_editVertCurveLen.setLocation(250,40) ;
			frame_editVertCurveLen.setSize(280,180) ;
			frame_editVertCurveLen.validate() ;
			frame_editVertCurveLen.setVisible(true) ;
			frame_editVertCurveLen.setResizable(false) ;

			ActionListener frame_editVertCurveLen_save_listener = new ActionListener() {
				public void actionPerformed(ActionEvent aev) {
					int index = listVertCurves.getSelectedIndex();
					double val = new Float(txtCurveLen.getText()).doubleValue() ;
					if (val<roadDesign.getMinVCurveLen()) {
						String unitStr = "" ;
						if (roadDesign.getPreferredUnit()==1) {
							unitStr = " (ft)";
						} else if (roadDesign.getPreferredUnit()==2) {
							unitStr = " (m)";
						} 
						popMessageBox("Vertical Curve Length", "Value less than minimum vertical curve length, \n"+ConversionUtils.CStr(roadDesign.getMinVCurveLen())+unitStr) ;
						txtCurveLen.setText(ConversionUtils.CStr(roadDesign.getVerticalCurves()[index].getCurveLen())) ;
					} else {
						roadDesign.getVerticalCurves()[index].setCurveLen(val, SSD) ;
						vertAlign();
					}
					//frame_editVertCurveLen.dispose() ;

				}
			} ;
			// check Lm based on AASHTO green book, chapter 3
			ActionListener frame_editVertCurveLen_check_listener = new ActionListener() {
				public void actionPerformed(ActionEvent aev) {
					int index = listVertCurves.getSelectedIndex();
					double val = new Float(txtCurveLen.getText()).doubleValue() ;
					double Lm = checkVCurveLen(index+1, val) ;
					String unitStr = "" ;
					if (roadDesign.getPreferredUnit()==1) {
						unitStr = " (ft)";
					} else if (roadDesign.getPreferredUnit()==2) {
						unitStr = " (m)";
					} 
					popMessageBox("Vertical Curve Length", "Minimum vertical curve length,\nLm = "+ConversionUtils.CStr(Lm)+unitStr) ;                  
				}
			} ;
			ActionListener frame_editVertCurveLen_done_listener = new ActionListener() {
				public void actionPerformed(ActionEvent aev) {
					// update fill cut profile after modifying vertical curve
					fillCutSteps = calcFillCutProfile() ;   // 10/13/06 added
					frame_editVertCurveLen.dispose() ;
					//frame_report.dispose() ;
				}
			} ;
			ItemListener curveIndex_listener = new ItemListener() {
				public void itemStateChanged(ItemEvent ie) {
					int index = listVertCurves.getSelectedIndex();
					txtCurveLen.setText(ConversionUtils.CStr(roadDesign.getVerticalCurves()[index].getCurveLen())) ;
					//System.out.println("index="+index) ;
				}
			} ;

			frame_editVertCurveLen.setLayout(new GridBagLayout()) ;
			// Create a constrains object, and specify default values
			GridBagConstraints c = new GridBagConstraints() ;
			c.fill = GridBagConstraints.BOTH ; // component grows in both directions

			c.gridx = 0 ; c.gridy = 0; c.gridwidth = 1 ; c.gridheight = 1 ;
			c.insets = new Insets(5,5,0,5) ; // 5-pixel margins on all sides
			String unitStr = "";
			if (roadDesign.getPreferredUnit()==1) {
				unitStr = " (ft)";
			} else if (roadDesign.getPreferredUnit()==2) {
				unitStr = " (m)";
			} 

			lblSSD = new JLabel(ConversionUtils.CStr(SSD)+ unitStr) ;
			txtCurveLen = new JTextField(ConversionUtils.CStr(roadDesign.getVerticalCurves()[0].getCurveLen())) ;
			frame_editVertCurveLen.add(new JLabel("Curve Index"),c) ;

			c.insets = new Insets(1,5,5,5) ; 
			c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
			String[] curveStrings = new String[roadDesign.getvCurveCount()];
			for (int i=0; i<roadDesign.getvCurveCount(); i++) {
				curveStrings[i] = "Curve #" + (i+1);
			}
			listVertCurves = new JComboBox(curveStrings);

			listVertCurves.addItemListener(curveIndex_listener) ;
			frame_editVertCurveLen.add(listVertCurves, c) ;

			c.insets = new Insets(5,5,0,5) ; 
			c.gridx = 0; c.gridy = 2; 
			frame_editVertCurveLen.add(new JLabel("Stop Sight Distance" ), c) ;
			c.gridy = 3 ; 
			c.insets = new Insets(1,5,5,5) ; 
			frame_editVertCurveLen.add(lblSSD, c) ;

			c.insets = new Insets(5,5,0,5) ; 
			c.gridx = 1 ; c.gridy = 0; c.gridwidth = 1 ;
			frame_editVertCurveLen.add(new JLabel("Curve Length " + unitStr),c) ;
			c.insets = new Insets(1,5,5,5) ; 
			c.gridy = 1; 
			frame_editVertCurveLen.add(txtCurveLen, c) ;

			c.insets = new Insets(5,5,0,5) ; 
			c.gridx = 1 ; c.gridy = 2; c.gridwidth = 1 ;
			JButton btn_save = new JButton("Save") ;
			frame_editVertCurveLen.add(btn_save, c) ;
			btn_save.addActionListener(frame_editVertCurveLen_save_listener) ;

			c.gridx = 1 ; c.gridy = 3;
			c.insets = new Insets(5,5,0,5) ; 
			JButton btn_check = new JButton("Check Min Len") ;
			frame_editVertCurveLen.add(btn_check, c) ;
			btn_check.addActionListener(frame_editVertCurveLen_check_listener) ;

			c.gridx = 1 ; c.gridy = 4;
			c.insets = new Insets(5,5,5,5) ; 
			JButton btn_done = new JButton("Done") ;
			frame_editVertCurveLen.add(btn_done, c) ;
			btn_done.addActionListener(frame_editVertCurveLen_done_listener) ;

			frame_editVertCurveLen.invalidate();
			frame_editVertCurveLen.show() ;
			frame_editVertCurveLen.toFront() ;

		} else {
			noCurve2Edit_flag = true ;
		}

	} // popVertCurveLenEdit

	public void vertAlign(){
		if (roadDesign.getvCurveCount() > 0) { 
			fillCutSteps = 0;
			// at least 1 vertical curve exists
			// compute locations of PVC, PVT using lengths and ensure that PVC(i) > PVT(i-1)
			int i, vCurveFlag ;
			vCurveFlag = 0;
			valid_vCurve = false;
			double dist;
			double x1=0f;
			double y1=0f;
			double x2=0f, y2=0f ;

			for (i=0; i<roadDesign.getvCurveCount();i++) {
				roadDesign.getVerticalCurves()[i].setPVI(roadDesign.getVerticalConstructMarks().get(i+1).getDistance());

				double grade1 = roadDesign.getVerticalConstructMarks().get(i).getGrade();
				double grade2 = roadDesign.getVerticalConstructMarks().get(i).getGrade();
				if(i+1<roadDesign.getvCurveCount()){
					grade1 = roadDesign.getVerticalConstructMarks().get(i+1).getGrade();  
					grade2 = roadDesign.getVerticalConstructMarks().get(i+1).getGrade();              	
				}
				if(i+2<roadDesign.getvCurveCount()){
					grade2 = roadDesign.getVerticalConstructMarks().get(i + 2).getGrade();
				}
				roadDesign.getVerticalCurves()[i].calcPVI(roadDesign.getVerticalConstructMarks().get(i+1).getElevation(), grade1, grade2);

				// check if PVC(i) & PVT(i-1) overlaps
				if (i > 0) { // 
					if (roadDesign.getVerticalCurves()[i].getPVC() < roadDesign.getVerticalCurves()[i-1].getPVT()) { 
						popMessageBox("Construct PVC, PVT", "Adjacent curves " + ConversionUtils.CStr(i) + " and " + ConversionUtils.CStr(i + 1) + " overlap!\nPlease redesign vertical curves.\n"+ roadDesign.getVerticalCurves()[i].getPVC() + " vs " + roadDesign.getVerticalCurves()[i-1].getPVT());
						vCurveFlag = 0;
						roadDesign.getVerticalConstructMarks().get(i).setPVTnCurvatureOverlap(true);
						roadDesign.getVerticalConstructMarks().get(i+1).setPVTnCurvatureOverlap(true);
						roadDesign.getVerticalConstructMarks().get(i + 2).setPVTnCurvatureOverlap(true);
					}   // overlap occurs
				}   // i>0

				// calculate road length/PVC, PVT distance
				if (i == 0) { 
					// first curve
					x1 = roadDesign.getVerticalConstructMarks().get(i).getDistance();
					y1 = roadDesign.getVerticalConstructMarks().get(i).getElevation();
					x2 = roadDesign.getVerticalCurves()[i].getPVC();
					y2 = roadDesign.getVerticalCurves()[i].getPVC_Elevation();
					dist = new Float(Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1))).doubleValue();
					roadDesign.getVerticalCurves()[i].setPVC_Distance(dist);
				} else {
					x1 = roadDesign.getVerticalCurves()[i - 1].getPVT();
					y1 = roadDesign.getVerticalCurves()[i - 1].getPVT_Elevation();
					x2 = roadDesign.getVerticalCurves()[i].getPVC();
					y2 = roadDesign.getVerticalCurves()[i].getPVC_Elevation();
					dist = new Float(Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1))).doubleValue();
					roadDesign.getVerticalCurves()[i].setPVC_Distance(roadDesign.getVerticalCurves()[i - 1].getPVT_Distance() + dist);

				}
				dist = calcCurveRoadLength(i);
				roadDesign.getVerticalCurves()[i].setPVT_Distance(roadDesign.getVerticalCurves()[i].getPVC_Distance() + dist);

			}   // Next i
			// last linear segment
			// last curve
			x1 = roadDesign.getVerticalCurves()[roadDesign.getvCurveCount() - 1].getPVT();
			y1 = roadDesign.getVerticalCurves()[roadDesign.getvCurveCount() - 1].getPVT_Elevation();
			x1 = roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount() - 1).getDistance();
			y1 = roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount() - 1).getElevation();
			dist = new Float(Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1))).doubleValue();
			total_roadLen = roadDesign.getVerticalCurves()[roadDesign.getvCurveCount() - 1].getPVT_Distance() + dist;

			// if curve is valid, create our own construction curve
			if (vCurveFlag == 0) { 
				valid_vCurve = true;
				//data_viewer.Invalidate()
				// calculate cut/fill profile
				fillCutSteps = calcFillCutProfile() ;
				//btnRoad3D.Enabled = True
			}
		} else {
			//btnRoad3D.Enabled = False
			popMessageBox("Vertical Curve Design","Cannot construct vertical curve. \nRequire at least 2 construction lines!");
		}   //if (myDB.getvCurveCount() > 0)
	}   // vertAlign
	private double calcCurveRoadLength(int index) {
		int i, numSteps ;
		double stepSize, xi, x1, y1, x2, y2 ;
		double yi;
		double len = 0f;
		x1 = roadDesign.getVerticalCurves()[index].getPVC();
		y1 = roadDesign.getVerticalCurves()[index].getPVC_Elevation();
		x2 = roadDesign.getVerticalCurves()[index].getPVT();

		numSteps = ConversionUtils.CInt(Math.ceil((x2 - x1) / ComputeStepSize));  // calculate every 10 ft/2m
		stepSize = (x2 - x1) / numSteps;
		for (i=1;i<numSteps;i++) {
			xi = i * stepSize + x1;
			yi = calcDesignElevation(xi);
			len += Math.sqrt(stepSize*stepSize  + (yi - y1)*(yi - y1));
			y1 = yi;
		} // Next
		return len;
	}    // calcCurveRoadLength

	// proposed road elevation
	public double calcDesignElevation(double dist_j) {
		// input distance from start point, in ft/m
		// return designed elevation in ft/m
		double elev = 0f;
		if (dist_j > max_hDist) {
			elev = roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount() - 1).getElevation();
		} else {
			int i ;
			double xPVC, xPVT ;
			double dx ;
			for (i=0;i<roadDesign.getvCurveCount();i++) {
				xPVC = roadDesign.getVerticalCurves()[i].getPVC();
				xPVT = roadDesign.getVerticalCurves()[i].getPVT();
				dx = dist_j - xPVC;
				if (dx <= 0) { 
					elev = roadDesign.getVerticalCurves()[i].getPVC_Elevation() + dx * roadDesign.getVerticalCurves()[i].get_G1();
					break;
				} else if (dx > 0 && dist_j < xPVT) { // vertical curve
					elev = roadDesign.getVerticalCurves()[i].getDX_Elevation(dx);
					break;
				} else if ((i == roadDesign.getvCurveCount() - 1) && (dist_j >= xPVT)) {   // last grade
					dx = dist_j - xPVT;
					elev = roadDesign.getVerticalCurves()[i].getPVT_Elevation() + dx * roadDesign.getVerticalCurves()[i].get_G2();
				}
			}   //Next i
		}
		return elev;
	}   //calcDesignElevation

	public int calcFillCutProfile() {
		// create a cut/fill profile with increments of every 10 ft/m
		int fillcut_steps ;
		fillcut_steps = ConversionUtils.CInt(Math.ceil(max_hDist / ComputeStepSize));
		CutandFill = new double[fillcut_steps] ;
		accuMass = new double[fillcut_steps] ;
		designElevation = new double[fillcut_steps] ;

		int i ;
		for (i=0;i<fillcut_steps;i++) {
			CutandFill[i] = 0f;
			CutandFill[i] = calcOriginalElevation(i * ComputeStepSize) - calcDesignElevation(i * ComputeStepSize);
			// 5/2/06 added
			if (i == 0) { 
				if (roadDesign.getPreferredUnit() == 1) {   // US unit
					accuMass[i] = CutandFill[i] * ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f) / 27f ;
				} else if (roadDesign.getPreferredUnit() == 2) { // metric
					accuMass[i] = CutandFill[i] * ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f);
				}
			} else {
				if (roadDesign.getPreferredUnit() == 1) {   // US unit
					accuMass[i] = CutandFill[i] * ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f) / 27f + accuMass[i-1];
				} else if (roadDesign.getPreferredUnit() == 2) { // metric
					accuMass[i] = CutandFill[i] * ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f) + accuMass[i-1];
				}
			}
		} // Next

		return fillcut_steps;
	} // calcFillCutProfile

	// original land elevation
	public double calcOriginalElevation(double distx) {
		double elev = 0f;
		if (distx > max_hDist) { 
			elev = roadDesign.getElevationMarks().get(roadDesign.getElevationMarkCount() - 1).getElevation();
		} else {
			int i ;
			double dx ;
			for (i=1;i<roadDesign.getElevationMarkCount();i++) {
				if ((distx >= roadDesign.getElevationMarks().get(i - 1).getDistance()) && (distx < roadDesign.getElevationMarks().get(i).getDistance())) {
					// do linear interpolation
					dx = distx - roadDesign.getElevationMarks().get(i - 1).getDistance();
					elev = roadDesign.getElevationMarks().get(i - 1).getElevation() + dx * roadDesign.getElevationMarks().get(i).getGrade();
					break;
				}
			}   //Next i
		}
		return elev;
	}   // calcOriginalElevation

	public void viewFillCut() {
		if (CutandFill==null) {
			fillCutSteps = calcFillCutProfile();
		}
		if (valid_vCurve) { 
			tb.setConstructEnabled(false) ;
			//btnFillCut.Image = ImageList1.Images(2)
			viewElevationProfile = false;
			viewMassDiagram=false ;     // 5/2/06 added
			sb.setStatusBarText(0, "View fill/cut profile") ; //Status: 
			//ele_data.Text = "Fill-Cut Profile"
			int i ;
			double my_fc, max_fc, min_fc ;
			min_fc = 99999f;
			max_fc = 0f;
			for (i=0;i<fillCutSteps;i++) {
				my_fc = CutandFill[i];
				if (my_fc > max_fc) {
					max_fc = my_fc;
				} else if (my_fc < min_fc) {
					min_fc = my_fc;
				}
			}   //Next i

			// update plot scale in Y directions
			gridSizeY = ConversionUtils.CInt(Math.ceil((max_fc - min_fc) / 4));
			Ymin = ConversionUtils.CInt(10f * Math.floor((min_fc-gridSizeY/2) / 10f));

			plotScaleY = GRID_VDIV / gridSizeY;

			// update X, Y axes labels
			/*
            elevation_0.Text = Ymin.ToString
            elevation_1.Text = (1 * gridSizeY + Ymin).ToString()
            elevation_2.Text = (2 * gridSizeY + Ymin).ToString()
            elevation_3.Text = (3 * gridSizeY + Ymin).ToString()
            elevation_4.Text = (4 * gridSizeY + Ymin).ToString()
            elevation_5.Text = (5 * gridSizeY + Ymin).ToString()
            elevation_6.Text = (6 * gridSizeY + Ymin).ToString()
            lbl_ele.Visible = False
			 */
			//ToolTip1.getSuperElevation()tToolTip(btnFillCut, "View elevation profile")
			//data_viewer.Invalidate()
			repaint();
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) { 
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}
	}   // vire fillcut
	public void view_MassDiagram() {
		if (accuMass==null) {
			fillCutSteps = calcFillCutProfile();
		}
		if (valid_vCurve) { 
			tb.setConstructEnabled(false) ;
			//btnFillCut.Image = ImageList1.Images(2)
			viewElevationProfile = false;
			viewMassDiagram=true ;
			sb.setStatusBarText(0, "View mass diagram") ; //Status: 
			//ele_data.Text = "Fill-Cut Profile"
			int i ;
			double my_fc, max_fc, min_fc ;
			min_fc = 99999f;
			max_fc = 0f;
			for (i=0;i<fillCutSteps;i++) {
				my_fc = accuMass[i];
				if (my_fc > max_fc) {
					max_fc = my_fc;
				} else if (my_fc < min_fc) {
					min_fc = my_fc;
				}
			}   //Next i

			// update plot scale in Y directions
			gridSizeY = ConversionUtils.CInt(Math.ceil((max_fc - min_fc) / 4));
			Ymin = ConversionUtils.CInt(1000f * Math.floor((min_fc-gridSizeY/2) / 1000f));

			plotScaleY = GRID_VDIV / gridSizeY;

			// update X, Y axes labels
			/*
            elevation_0.Text = Ymin.ToString
            elevation_1.Text = (1 * gridSizeY + Ymin).ToString()
            elevation_2.Text = (2 * gridSizeY + Ymin).ToString()
            elevation_3.Text = (3 * gridSizeY + Ymin).ToString()
            elevation_4.Text = (4 * gridSizeY + Ymin).ToString()
            elevation_5.Text = (5 * gridSizeY + Ymin).ToString()
            elevation_6.Text = (6 * gridSizeY + Ymin).ToString()
            lbl_ele.Visible = False
			 */
			//ToolTip1.getSuperElevation()tToolTip(btnFillCut, "View elevation profile")
			//data_viewer.Invalidate()
			repaint();
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) { 
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}
	}   // view mass diagram

	public void viewElevation(){
		tb.setConstructEnabled(true) ;
		if (valid_vCurve) { 
			//btnFillCut.Image = ImageList1.Images(3)
			viewElevationProfile = true;
			viewMassDiagram=false ;     // 5/2/06 added
			//ele_data.Text = "Grade Design"
			sb.setStatusBarText(0, "View elevation profile"); //Status: 

			// update plot scale in Y directions
			gridSizeY = ConversionUtils.CInt(Math.ceil((max_ele - min_ele) / 4));
			Ymin = ConversionUtils.CInt(10f * Math.floor((min_ele-gridSizeY/2) / 10f));

			plotScaleY = GRID_VDIV / gridSizeY;

			// update X, Y axes labels
			/*
            elevation_0.Text = Ymin.ToString
            elevation_1.Text = (1 * gridSizeY + Ymin).ToString()
            elevation_2.Text = (2 * gridSizeY + Ymin).ToString()
            elevation_3.Text = (3 * gridSizeY + Ymin).ToString()
            elevation_4.Text = (4 * gridSizeY + Ymin).ToString()
            elevation_5.Text = (5 * gridSizeY + Ymin).ToString()
            elevation_6.Text = (6 * gridSizeY + Ymin).ToString()
            lbl_ele.Visible = True
            ToolTip1.getSuperElevation()tToolTip(btnFillCut, "View fill/cut profile")
			 */
			//data_viewer.Invalidate()
			repaint();
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) {// Then
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}
	}   // view elevation
	public void tool_gradeON(){
		isGradeConstructOn = true;
		sb.setStatusBarText(0,  "Grade construction tool ON"); //Status: 
	}

	public void tool_gradeOFF() {
		isGradeConstructOn = false;
		sb.setStatusBarText(0, "Grade construction tool OFF"); //Status: 
	}

	public void edit_undo() {
		int bufData ;
		bufData = popGradeLogBuffer();
		if (bufData >= 0) { 
			//roadDesign.setvConstructMarkCount(bufData);
			roadDesign.setvCurveCount(roadDesign.getvConstructMarkCount()-1);
			sb.setStatusBarText(0, "Undo last construction point") ; //Status: 
		} else {
			sb.setStatusBarText(0, "Cannot undo") ; //Status: 
		}
		repaint();
	}

	public void setStatusBarText(int id, String message){
		sb.setStatusBarText(id, message) ;
	}

	public void edit_redo() {
		if (gradeLogIndex < gradeLogBuffer.length - 1) { 
			if (gradeLogBuffer[gradeLogIndex + 1] > 0 ) {
				// log info exists
				gradeLogIndex += 1;
				//roadDesign.setvConstructMarkCount(gradeLogBuffer[gradeLogIndex]);
				roadDesign.setvCurveCount(roadDesign.getvConstructMarkCount()-1);
				sb.setStatusBarText(0, "Redo last construction point") ; //Status: 
			} else {
				sb.setStatusBarText(0, "Cannot redo") ; //Status: 
			}
		} else {
			sb.setStatusBarText(0, "Cannot redo") ; //Status: 
		}
		repaint();
	}   // edit_redo
	/** Pop up a window to display message */    
	public void popClearAllDesign(String caption, String message) {
		// open a frame
		frame_msgboxClearAll = new RoadGISPopup(caption) ;
		//frame_msgboxClearAll.setLocation(400,200) ;
		frame_msgboxClearAll.setSize(300,150) ;
		frame_msgboxClearAll.setCenter() ;
		frame_msgboxClearAll.validate() ;
		frame_msgboxClearAll.setVisible(true) ;

		ActionListener frame_msgbox_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				//roadDesign.setvConstructMarkCount(0);
				valid_vCurve = false;
				roadDesign.setvCurveCount(0);
				me0 = null;
				frame_msgboxClearAll.dispose() ;

				sb.setStatusBarText(0, "Design cleared") ; //Status: 
			}
		} ;
		ActionListener frame_msgbox_no_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				frame_msgboxClearAll.dispose() ;
			}
		} ;

		frame_msgboxClearAll.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;

		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 2 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
		JLabel myMsg = new JLabel(message) ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		frame_msgboxClearAll.setBackground(new Color(200, 200, 200)) ;
		frame_msgboxClearAll.add(myMsg,c) ;
		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Yes ") ;
		frame_msgboxClearAll.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_msgbox_yes_listener) ;
		c.gridx = 1 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		frame_msgboxClearAll.add(btn_no, c) ;
		btn_no.addActionListener(frame_msgbox_no_listener) ;

		frame_msgboxClearAll.invalidate();
		frame_msgboxClearAll.show() ;
		frame_msgboxClearAll.toFront() ;

	} // popClearAll
	/** Pop up a window to display message */    
	public void popClearVCurves(String caption, String message) {
		if (!viewElevationProfile) {
			viewElevation() ;
		}
		// open a frame
		frame_msgboxClearCurves = new RoadGISPopup(caption) ;
		//frame_msgboxClearCurves.setLocation(400,200) ;
		frame_msgboxClearCurves.setSize(300,150) ;
		frame_msgboxClearCurves.setCenter() ;
		frame_msgboxClearCurves.validate() ;
		frame_msgboxClearCurves.setVisible(true) ;

		ActionListener frame_msgbox_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				valid_vCurve = false;
				/*
                for (int i=0; i<myDB.vCurveCount+2; i++) {
                    myDB.getvConstructMarks().get(i).PVTnC_Overlap = false ;
                }
                sb.setStatusBarText(1, "") ; // clear error if any previously
				 */
				sb.setStatusBarText(0, "Vertical curves cleared") ; //Status: 
				frame_msgboxClearCurves.dispose() ;
				repaint();
			}
		} ;
		ActionListener frame_msgbox_no_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				frame_msgboxClearCurves.dispose() ;
			}
		} ;

		frame_msgboxClearCurves.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;

		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 2 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
		JLabel myMsg = new JLabel(message) ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		frame_msgboxClearCurves.setBackground(new Color(200, 200, 200)) ;
		frame_msgboxClearCurves.add(myMsg,c) ;
		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Yes ") ;
		frame_msgboxClearCurves.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_msgbox_yes_listener) ;
		c.gridx = 1 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		frame_msgboxClearCurves.add(btn_no, c) ;
		btn_no.addActionListener(frame_msgbox_no_listener) ;

		frame_msgboxClearCurves.invalidate();
		frame_msgboxClearCurves.show() ;
		frame_msgboxClearCurves.toFront() ;

	} // pop Clear Vertical curves

	public void file_open(){
		FileInputStream fis=null;
		DataInputStream br=null;
		int i=0;
		double x=0f, y=0f ;
		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Load Vertical Curve Design", FileDialog.LOAD);
			fd.setFile("*.vcw");
			fd.show();
			String dir = fd.getDirectory() ;
			String filename = fd.getFile() ;
			String fullpath = dir + filename ;
			fd.dispose();

			if (filename != null && dir != null) {
				//System.out.println("open filename="+fullpath);
				//reset draw settings

				fis = new FileInputStream(fullpath);
				br = new DataInputStream( new BufferedInputStream(fis,512)); 

				// 1 - get saved vertical construction grade lines
				int vmarkCount = br.readInt();
				//roadDesign.setvConstructMarkCount();
				
				//     System.out.println("getvConstructMarkCount()"+myDB.getvConstructMarkCount());
				push2GradeLogBuffer(roadDesign.getvConstructMarkCount());
				for (i=0;i<roadDesign.getvConstructMarkCount();i++) {
					if (roadDesign.getVerticalConstructMarks().get(i)==null) { 
						
						roadDesign.addVerticalConstructionMarker(new AlignmentMarker());
					}
					x = br.readFloat();
					y = br.readFloat();
					roadDesign.getVerticalConstructMarks().get(i).setLocation(new GeometryFactory().createPoint(new Coordinate(x, y)));
					roadDesign.getVerticalConstructMarks().get(i).setElevation(br.readFloat());
					roadDesign.getVerticalConstructMarks().get(i).setParentIndex(br.readByte());
					roadDesign.getVerticalConstructMarks().get(i).setMarkerType(MarkerType.values()[br.readByte()]);
					roadDesign.getVerticalConstructMarks().get(i).setDistance(br.readFloat());
					roadDesign.getVerticalConstructMarks().get(i).setGrade(br.readFloat());
				}
				// 2 - get vertical curves
				roadDesign.setvCurveCount(br.readInt());
				for (i=0; i<roadDesign.getvCurveCount(); i++) {
					if (roadDesign.getVerticalCurves()[i]==null) { 
						roadDesign.getVerticalCurves()[i] = new VCurve(roadDesign.getMinVCurveLen());
					}
					roadDesign.getVerticalCurves()[i].setCurveLen(br.readFloat(), 0);
					roadDesign.getVerticalCurves()[i].setPVC(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVC_Elevation(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVC_Distance(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVT(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVT_Elevation(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVT_Distance(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVI(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPVI_e(br.readFloat());
					roadDesign.getVerticalCurves()[i].setMinMaxElevation(br.readFloat());
					roadDesign.getVerticalCurves()[i].setMinMaxEleDist(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPara_a(br.readFloat());
					roadDesign.getVerticalCurves()[i].setPara_b(br.readFloat());
					roadDesign.getVerticalCurves()[i].set_G1(br.readFloat());
					roadDesign.getVerticalCurves()[i].set_G2(br.readFloat());
				}
				// 3- 5/1/06 added to load unit info
				int savedUnit ;
				if (br.available() >0 ) {
					savedUnit = br.readInt() ;
					if (savedUnit == 1 && roadDesign.getPreferredUnit() == 2) { 
						// change from US to metric
						for (i=0; i<roadDesign.getvConstructMarkCount(); i++) {
							roadDesign.getVerticalConstructMarks().get(i).setElevation(roadDesign.getVerticalConstructMarks().get(i).getElevation() * roadDesign.FT2M);
							roadDesign.getVerticalConstructMarks().get(i).setDistance(roadDesign.getVerticalConstructMarks().get(i).getDistance() * roadDesign.FT2M);
						}
						for (i=0; i<roadDesign.getvCurveCount(); i++) {
							roadDesign.getVerticalCurves()[i].setCurveLen(roadDesign.getVerticalCurves()[i].getCurveLen() * roadDesign.FT2M, 0);
						}

					} else if (savedUnit == 2 && roadDesign.getPreferredUnit() == 1) { 
						// change from metric to US
						for (i=0; i<roadDesign.getvConstructMarkCount(); i++) {
							roadDesign.getVerticalConstructMarks().get(i).setElevation(roadDesign.getVerticalConstructMarks().get(i).getElevation() / roadDesign.FT2M);
							roadDesign.getVerticalConstructMarks().get(i).setDistance(roadDesign.getVerticalConstructMarks().get(i).getDistance() / roadDesign.FT2M);
						}
						for (i=0; i<roadDesign.getvCurveCount(); i++) {
							roadDesign.getVerticalCurves()[i].setCurveLen(roadDesign.getVerticalCurves()[i].getCurveLen() / roadDesign.FT2M, 0);
						}

					}   // if (savedUnit == 1 && myDB.getPreferredUnit() == 2) 
				}   //End If br.available
				vertAlign() ; // calc total road length, etc
				valid_vCurve = true;

				br.close();
				fis.close();
			}   // end if fullpath
			repaint();
		}   // try
		catch (Exception e){
			//do nothing
			System.out.println("Load Vertical Curve Design File:"+e.toString());
			sb.setStatusBarText(1, "Loading Vertical Curve, "+e.toString()) ; //Error: 

		} // try        
	}   // file open
	public void file_save(){
		FileOutputStream fos=null;
		DataOutputStream w=null;

		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Save Vertical Curve Design", FileDialog.SAVE);
			fd.setFile("*.vcw");
			/*           
             fd.setFilenameFilter(new FilenameFilter(){
                public boolean accept(File dir, String name){
                  return (name.endsWith(".vcw")) ;  // || name.endsWith(".gif"));
                  }
            });
			 */
			fd.show();

			String fullpath=fd.getDirectory()+fd.getFile();

			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				if (fullpath.indexOf(".vcw")<0) {
					fullpath += ".vcw" ;
				}
				fos = new FileOutputStream(fullpath);
				w = new DataOutputStream( new BufferedOutputStream(fos,512)); 

				// 1 - save grade construction lines
				int i ;
				w.writeInt(roadDesign.getvConstructMarkCount());
				for (i=0; i<roadDesign.getvConstructMarkCount();i++) {
					w.writeDouble(roadDesign.getVerticalConstructMarks().get(i).getLocation().getX());
					w.writeDouble(roadDesign.getVerticalConstructMarks().get(i).getLocation().getY());
					w.writeDouble(roadDesign.getVerticalConstructMarks().get(i).getElevation());
					w.writeByte(roadDesign.getVerticalConstructMarks().get(i).getParentIndex());
					w.writeByte(roadDesign.getVerticalConstructMarks().get(i).getMarkerType().ordinal());
					w.writeDouble(roadDesign.getVerticalConstructMarks().get(i).getDistance());
					w.writeDouble(roadDesign.getVerticalConstructMarks().get(i).getGrade());
				}   //Next
				w.flush();
				// 2 - vertical curves DB
				w.writeInt(roadDesign.getvCurveCount());
				for (i=0;i<roadDesign.getvCurveCount();i++) { 
					w.writeDouble(roadDesign.getVerticalCurves()[i].getCurveLen());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVC());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVC_Elevation());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVC_Distance());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVT());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVT_Elevation());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVT_Distance());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVI());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPVI_e());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getMinMaxElevation());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getMinMaxEleDist());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPara_a());
					w.writeDouble(roadDesign.getVerticalCurves()[i].getPara_b());
					w.writeDouble(roadDesign.getVerticalCurves()[i].get_G1());
					w.writeDouble(roadDesign.getVerticalCurves()[i].get_G2());
				}
				// 3 - 5/1/06 added to save unit info
				w.writeInt(roadDesign.getPreferredUnit());

				w.flush();
				w.close();
			}
			fos.close();
			fd.dispose();
		}
		catch (Exception e){
			//do nothing
			System.out.println("Save Vertical Curve Design File:"+e.toString());
			sb.setStatusBarText(1, "Saving Vertical Curve, "+e.toString()) ; //Error: 
		} // try

	}// file save

	public void saveElevationProfile(){  // save elevation profile data to a file
		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Save Designed Elevation Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				String unitStr ;
				if (roadDesign.getPreferredUnit()==1) {
					unitStr = " (ft)" ;
				} else {
					unitStr=" (m)" ;
				}
				String dataStr = "Distance"+unitStr+", Elevation"+unitStr+"\n";
				if (fillCutSteps > 0) {
					for (int i=0; i<fillCutSteps; i++) {
						dataStr += ConversionUtils.CStr(i*ComputeStepSize) + "," + ConversionUtils.CStr(designElevation[i]) + "\n" ;
					}
				}
				out.write(dataStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			System.out.println("Save Designed Elevation Data:"+e.toString());
			sb.setStatusBarText(1, "Saving Designed Elevation Data, "+e.toString()) ; //Error: 
		} // try


	}// file save elevation profile

	public void saveMassDiagram(){  // save mass diagram data to a file
		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Save Mass Diagram Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				String unitStr="", unitStr3="" ; 
				if (roadDesign.getPreferredUnit()==1) {
					unitStr = " (ft)" ;
					unitStr3 = " (yd^3)" ;
				} else {
					unitStr=" (m)" ;
					unitStr3=" (m^3)" ;
				}
				String dataStr = "Distance"+unitStr+", Mass"+unitStr3+"\n";
				if (fillCutSteps > 0) {
					for (int i=0; i<fillCutSteps; i++) {
						dataStr += ConversionUtils.CStr(i*ComputeStepSize) + "," + ConversionUtils.CStr(accuMass[i]) + "\n" ;
					}
				}
				out.write(dataStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			System.out.println("Save Mass Diagram Data:"+e.toString());
			sb.setStatusBarText(1, "Saving Mass Diagram Data, "+e.toString()) ; //Error: 
		} // try


	}// file save mass diagram

	public void saveCutAndFill(){  // save mass diagram data to a file
		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Save Cut and Fill Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				String unitStr="" ; 
				if (roadDesign.getPreferredUnit()==1) {
					unitStr = " (ft)" ;
				} else {
					unitStr=" (m)" ;
				}
				String dataStr = "Distance"+unitStr+", Cut/Fill"+unitStr+"\n";
				if (fillCutSteps > 0) {
					for (int i=0; i<fillCutSteps; i++) {
						dataStr += ConversionUtils.CStr(i*ComputeStepSize) + "," + ConversionUtils.CStr(CutandFill[i]) + "\n" ;
					}
				}
				out.write(dataStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			System.out.println("Save Cut and Fill Data:"+e.toString());
			sb.setStatusBarText(1, "Saving Cut and Fill Data, "+e.toString()) ; //Error: 
		} // try


	}// file save cut and fill

	public void file_saveReport(){  // save report file
		if (valid_vCurve) { 
			try
			{
				FileDialog fd=new FileDialog(new JFrame(),"Save Report", FileDialog.SAVE);
				fd.setFile("*.txt");
				fd.show();
				String fullpath=fd.getDirectory()+fd.getFile();
				fd.dispose();
				//System.out.println("filepath="+fullpath);
				if(fullpath!=null) {
					BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
					String reportStr = generateReport();
					out.write(reportStr);
					out.flush();
					out.close();
				}
			}
			catch (Exception e){
				//do nothing
				System.out.println("Save Report File:"+e.toString());
				sb.setStatusBarText(1, "Saving Report, "+e.toString()) ; //Error: 
			} // try
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) {// Then
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}

	}// file save report

	public void file_printReport(){  // print report file
		if (valid_vCurve) { 
			String reportStr = generateReport();
			try
			{
				//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
				PrintText printReport = new PrintText() ;
				printReport.print(reportStr) ;

			}
			catch (Exception e){
				//do nothing
				System.out.println("Print Report:"+e.toString());
				sb.setStatusBarText(1, "Print Report, "+e.toString()) ; //Error: 
			} // try
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) {// Then
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}

	}// file print report

	/** Pop up a window to display report */   
	public void popReport() {
		if (valid_vCurve) { 
			if (frame_report != null) {
				frame_report.dispose() ;
			}
			frame_report = new RoadGISPopup("Roadway Geometry Design Report") ;
			frame_report.setSize(450,600) ;
			frame_report.setCenter() ;
			String message = "" ;
			JTextArea myReport = new JTextArea() ;
			if (frame_report.isShowing()==false)
			{
				message = generateReport();

				//System.out.println(message) ;
				//frame_report.setLocation(250,5) ;

				frame_report.setSize(450,600) ;
				//frame_report.validate() ;
				frame_report.setVisible(true) ;
				frame_report.setResizable(true);
				//frame_report.setCenter() ;
				//frame_report.show() ;
				// file menu
				JMenuBar menu_bar = new JMenuBar() ;
				JMenu menu_file = new JMenu("File") ;
				JMenuItem file_savereport = new JMenuItem("Save Report") ;
				JMenuItem file_printreport = new JMenuItem("Print") ;
				JMenuItem separator = new JMenuItem("-") ;
				JMenuItem file_close = new JMenuItem("Close") ;
				menu_file.add(file_savereport) ;   // add menu items
				menu_file.add(file_printreport) ;   // add menu items
				menu_file.add(separator) ;   // add menu items
				menu_file.add(file_close) ;   // add menu items

				menu_bar.add(menu_file) ;     // add menu
				frame_report.setJMenuBar(menu_bar) ;

				file_savereport.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent aev) {
								file_saveReport();  
								setStatusBarText(0, "Save Report") ;
							} // actionPerformed
						} // ActionListener
						) ; // file save report
				file_printreport.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent aev) {
								file_printReport();  
								setStatusBarText(0, "Print Report") ;
							} // actionPerformed
						} // ActionListener
						) ; // file save report
				file_close.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent aev) {
								frame_report.dispose();
							} // actionPerformed
						} // ActionListener
						) ; // file Close
				/*
                ActionListener frame_report_ok_listener = new ActionListener() {
                    public void actionPerformed(ActionEvent aev) {

                        frame_report.dispose() ;
                    }
                } ;
				 */
				frame_report.setLayout(new BorderLayout(1,1)) ;
				myReport.setText(message) ;
				myReport.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
				myReport.setForeground(new Color(0,0,218)) ;
				frame_report.setBackground(new Color(200, 200, 200)) ;
				frame_report.add("Center",myReport) ;

				//JButton btn_ok = new JButton(" OK ") ;
				//frame_report.add("South",btn_ok) ;
				//btn_ok.addActionListener(frame_report_ok_listener) ;
				//frame_report.invalidate();
				//System.out.println(message) ;
				frame_report.show() ;
			}   // frmReport is showing
			//frame_report.toFront() ;
		} else {
			// no valid vertical curves created
			if (roadDesign.getvCurveCount() > 0) { 
				popMessageBox("Vertical Curve Design","Please perform vertical curve alignment first!");
			} else {
				popMessageBox("Vertical Curve Design","No construction lines. \nPlease use the construction button to \ncreate vertical curve construction lines first!");
			}
		}
	} // popReport

	public String generateReport() {
		String rpt  = "";
		String unit_str="" ;

		rpt = "Roadway Geometry Design Report" + newLine + newLine;
		rpt += "Vertical Curve Design Summary" + newLine;
		if (roadDesign.getPreferredUnit() == 1) { 
			unit_str = " ft";
			rpt += "Total road length = " + ConversionUtils.CStr(total_roadLen) + " ft = " + ConversionUtils.CStr(total_roadLen / 5280f) + " miles." + newLine;
		} else if (roadDesign.getPreferredUnit() == 2) { 
			unit_str = " m";
			rpt += "Total road length = " + ConversionUtils.CStr(total_roadLen) + " m = " + ConversionUtils.CStr(total_roadLen / 1000f) + " Km." + newLine;
		}
		int i ;
		for (i = 1; i< roadDesign.getvConstructMarkCount(); i++) {
			rpt += "Grade(" + i + ") = " + ConversionUtils.CStr(roadDesign.getVerticalConstructMarks().get(i).getGrade() * 100f) + "%" + newLine;
		}   //Next i
		rpt += newLine + "Curves Location and Elevation " + newLine;
		double pvcd, pvtd, clen ;
		for (i = 0 ; i<roadDesign.getvCurveCount() ; i++) {
			pvcd = roadDesign.getVerticalCurves()[i].getPVC_Distance();
			pvtd = roadDesign.getVerticalCurves()[i].getPVT();
			clen = pvtd - pvcd;
			rpt += "Curve(" + ConversionUtils.CStr(i + 1) + ") Length = " + ConversionUtils.CStr(clen) + unit_str + newLine;
			rpt += "             PVC (distance, elevation) = (" + ConversionUtils.CStr(pvcd) + ", " + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getPVC_Elevation()) + ")" + unit_str + newLine;
			rpt += "             PVI (dist_prj, elevation) = (" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getPVI()) + ", " + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getPVI_e()) + ")" + unit_str + newLine;
			rpt += "             PVT (distance, elevation) = (" + ConversionUtils.CStr(pvtd) + ", " + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getPVT_Elevation()) + ")" + unit_str + newLine;
			if (roadDesign.getVerticalCurves()[i].get_G2() - roadDesign.getVerticalCurves()[i].get_G1() > 0) { 
				rpt += "  Min. ";
			} else {
				rpt += "  Max. ";
			}
			rpt += "elevation(distance, elevation) = (" + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getMinMaxEleDist()) + ", " + ConversionUtils.CStr(roadDesign.getVerticalCurves()[i].getMinMaxElevation()) + ")" + unit_str + newLine;
			rpt += newLine;
		}   //Next i

		rpt += newLine;
		rpt += getHorizonDesignSummary();
		rpt += newLine;
		rpt += "Cut and Fill Summary" + newLine;
		int size ;
		double totalCut = 0f ;
		double totalFill = 0f ;
		size = CutandFill.length ;
		for (i = 0; i< size ; i++) {   // actual elevation profile - designed curve
			if (CutandFill[i] < 0 ) {
				totalFill += CutandFill[i];
			} else if (CutandFill[i] > 0) { 
				totalCut += CutandFill[i];
			}
		}

		if (roadDesign.getPreferredUnit() == 1) { 
			totalFill *= ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f) / 27f;
			totalCut *= ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f) / 27f;
			rpt += "Total Volume to Fill = " + ConversionUtils.CStr(totalFill) + " cu. yd" + newLine;
			rpt += "Total Volume to Cut  = " + ConversionUtils.CStr(totalCut) + " cu. yd" + newLine;
			rpt += "Cut and Fill Balance = " + ConversionUtils.CStr(totalCut + totalFill) + " cu. yd" + newLine;
		} else if (roadDesign.getPreferredUnit() == 2) { 
			totalFill *= ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f);
			totalCut *= ComputeStepSize * (roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth() * 2f);
			rpt += "Total Volume to Fill = " + ConversionUtils.CStr(totalFill) + " cu. meter" + newLine;
			rpt += "Total Volume to Cut  = " + ConversionUtils.CStr(totalCut) + " cu. meter" + newLine;
			rpt += "Cut and Fill Balance = " + ConversionUtils.CStr(totalCut + totalFill) + " cu. meter" + newLine;
		}
		//System.out.println("total_cut="+totalCut) ;
		//System.out.println("total_fill="+totalFill) ;
		//System.out.println("balance="+ConversionUtils.CStr(totalCut + totalFill)) ;

		rpt += newLine ;
		return rpt;
	}   // generate report

	public String getHorizonDesignSummary() {
		int i, curveidx, hAlignIdx ;
		String rpt, unit_str="" ;
		double dist;
		double cord_dist ;
		double myRadius;
		Point t1=new GeometryFactory().createPoint(new Coordinate(0f,0f));
		Point t2=new GeometryFactory().createPoint(new Coordinate(0f,0f));
		rpt = "Horizontal Geometry Design Summary" + newLine;
		curveidx = 0;
		if (roadDesign.getPreferredUnit() == 1) { 
			unit_str = " ft";
		} else if (roadDesign.getPreferredUnit() == 2) { 
			unit_str = " m";
		}
		for (i = 0 ; i< hRoadDataCount; i++) {
			myRadius = roadDesign.getHorizontalAlignmentSegments().get(i).getRadius();
			if (myRadius > 0) { 
				curveidx += 1;
				rpt += "Curve(" + curveidx + ")" + newLine;
				rpt += "   Radius = " + ConversionUtils.CStr(roadDesign.getHorizontalAlignmentSegments().get(i).getRadius()) + unit_str + newLine;
				hAlignIdx = findTangentDBIndex(i);
				if (hAlignIdx >= 0) {
					t1 = roadDesign.getHorizontalAlignmentMarks().get(hAlignIdx).getLocation();
					t2 = roadDesign.getHorizontalAlignmentMarks().get(hAlignIdx + 1).getLocation();
				}
				cord_dist = VectorUtils.distanceOf(t1, t2) / roadDesign.getImageScale() ;  // ft

				Point curve_ctr ;
				curve_ctr = roadDesign.getHorizontalAlignmentSegments().get(i).getStartPoint();
				double theta, theta_deg ;
				Vector2D vec1, vec2 ;
				vec1 = VectorUtils.vector(curve_ctr, t1);
				vec2 = VectorUtils.vector(curve_ctr, t2);
				theta = new Float(Math.acos(VectorUtils.getCosTheta(vec1, vec2))).doubleValue();
				theta_deg = ConversionUtils.CInt(theta * 18000 / Math.PI) / 100;  // degree 2 decimal points
				dist = myRadius * theta;  // curve distance

				rpt += "   Curve Length = " + ConversionUtils.CStr(dist) + unit_str + newLine;
				rpt += "   Central Angle = " + ConversionUtils.CStr(theta_deg) + " degrees" + newLine;
				rpt += "   Cord Length = " + ConversionUtils.CStr(cord_dist) + unit_str + newLine;
				double superE=0.0 ;
				if (roadDesign.getPreferredUnit() == 1) { 
					superE = (roadDesign.getSpeedLimit()*roadDesign.getSpeedLimit() / 15f / roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() - roadDesign.getSideFrictionCoef()) * 100f;
				} else if (roadDesign.getPreferredUnit() == 2) { 
					superE = (roadDesign.getSpeedLimit()*roadDesign.getSpeedLimit() / 127f / roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() - roadDesign.getSideFrictionCoef()) * 100f;
				}
				// or using MPH speed, AASHTO 2004, pp.146 Eq 3-10
				//superE = (speedLimit ^ 2 / 15 / hRoadData(i).getRadius() - sideFrictionCoef) * 100
				// check if superelevation <0
				if (superE < 0) {
					superE = 0;
				}

				superE = ConversionUtils.CInt(Math.ceil(superE / 2)) * 2; // round to 2%//s
				rpt += "   Superelevation = " + ConversionUtils.CStr(superE) + "% " + newLine;
				if (superE > roadDesign.getMaxSuperelevation() * 100f) { 
					rpt += "*** Warning: Exceed max. superelevation " + ConversionUtils.CStr(roadDesign.getMaxSuperelevation() * 100f)+ "% ***" + newLine;
				}

				double maxCSpd ;
				if (roadDesign.getPreferredUnit() == 1 ) {
					maxCSpd = new Double(Math.sqrt(roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() * 15f * (roadDesign.getSideFrictionCoef() + superE / 100f))).doubleValue();
					rpt += "   Max. Curve Speed = " + ConversionUtils.CStr(Math.round(maxCSpd)) + " MPH" + newLine;
				} else if (roadDesign.getPreferredUnit() == 2) { 
					maxCSpd = new Double(Math.sqrt(roadDesign.getHorizontalAlignmentSegments().get(i).getRadius() * 127f * (roadDesign.getSideFrictionCoef() + superE / 100f))).doubleValue();
					rpt += "   Max. Curve Speed = " + ConversionUtils.CStr(Math.round(maxCSpd)) + " Km/h" + newLine;
				}
				rpt += newLine;
			} // if
		}
		return rpt;
	}   // getHorizonDesignSummary

	public int findTangentDBIndex(int parent) {
		int i, foundIdx ;
		foundIdx = -1;
		for (i = 0 ; i<roadDesign.getNumberOfHorizontalAlignmentMarks(); i++) {
			if (roadDesign.getHorizontalAlignmentMarks().get(i).getParentIndex() == parent) { 
				foundIdx = i;
				break;
			}
		}   //Next
		return foundIdx;
	}    //findTangentDBIndex

	// VRML file generation =================================
	public void createVRMLFile(){
		int i, db_size ;
		double x1, y1, x2, y2, dist=0f ;
		int dbCount = 0;
		Point lastMark ;
		MarkerType lastMarkType ;
		boolean isLineSeg =false ;
		// compute # of data points needed to store 3D data at every ComputeStepSize
		db_size = ConversionUtils.CInt(Math.ceil(total_roadLen / ComputeStepSize));
		vrmlPoints_roadCenter = new RoadGeoDB[db_size];

		ProgressBar ProgressBar1  =
				new ProgressBar("Creating 3D Model ...", 400);
		ProgressBar1.show();
		// init starting point data
		lastMark = roadDesign.getElevationMarks().get(0).getLocation();
		lastMarkType = roadDesign.getElevationMarks().get(0).getMarkerType();
		x1 = lastMark.getX() / roadDesign.getImageScale();
		y1 = lastMark.getY() / roadDesign.getImageScale();
		//       frmProgress.ProgressBar1.Maximum = elevationMarkCount
		dbCount=0 ;
		for (i = 1 ; i< roadDesign.getElevationMarkCount() ; i++) {
			ProgressBar1.updateProgress();
			x2 = roadDesign.getElevationMarks().get(i).getLocation().getX() / roadDesign.getImageScale(); // ft/m
			y2 = roadDesign.getElevationMarks().get(i).getLocation().getY() / roadDesign.getImageScale();
			switch(roadDesign.getElevationMarks().get(i).getMarkerType()) {
			case LINE:  // line
				// linear distance
				dist = VectorUtils.distanceOf(lastMark, roadDesign.getElevationMarks().get(i).getLocation()) / roadDesign.getImageScale();
				isLineSeg = true;
				break;
			case CURVE:  // curve
				dist = calculateArcLength(i) ;   // radius in feet already
				isLineSeg = false;
				break;
			case TANGENT:  // tangent point, i>0
				if ((lastMarkType == MarkerType.LINE) || (lastMarkType == MarkerType.TANGENT)) { 
					//previous point belongs to a line
					// linear distance
					dist = VectorUtils.distanceOf(lastMark, roadDesign.getElevationMarks().get(i).getLocation()) / roadDesign.getImageScale();
					isLineSeg = true;
				} else if (lastMarkType == MarkerType.CURVE) { //Then
					// previous point belongs to a curve
					dist = calculateArcLength(i);
					isLineSeg = false;
				}
				break;
			}   //End Select
			int numSteps, j ;
			if (isLineSeg) { //Then
				numSteps = ConversionUtils.CInt(Math.floor(dist / ComputeStepSize));
				// line segment interpolation
				double vx, vy, dist_j ;
				vx = (x2 - x1) / dist;   // line vector for segment i
				vy = (y2 - y1) / dist;
				//System.out.println("dbCount="+dbCount);

				for (j = 0; j<= numSteps; j++) {
					vrmlPoints_roadCenter[dbCount] = new RoadGeoDB();
					dist_j = roadDesign.getElevationMarks().get(i - 1).getDistance() + j * ComputeStepSize;
					vrmlPoints_roadCenter[dbCount].Load(x1 + vx * j * ComputeStepSize, y1 + vy * j * ComputeStepSize, calcDesignElevation(dist_j), 0);
					dbCount += 1;
				}   //Next j
			} else {
				// curve segment interpolation every ComputeStepSize
				int parentIndex ;
				double cRadius, d_theta, dx, dy, cx, cy ;
				double alpha1, alpha2, d_alpha ;
				Point center ;
				parentIndex = roadDesign.getElevationMarks().get(i).getParentIndex();
				cRadius = roadDesign.getHorizontalAlignmentSegments().get(parentIndex).getRadius();
				center = roadDesign.getHorizontalAlignmentSegments().get(parentIndex).getStartPoint();
				cx = center.getX() / roadDesign.getImageScale();  // convert to ft or m
				cy = center.getY() / roadDesign.getImageScale();
				d_theta = ComputeStepSize / cRadius; // angle increment in radian, unsigned
				dx = x1 - cx;
				dy = y1 - cy;
				alpha1 = new Double(Math.atan2(dy, dx)).doubleValue();  // return start point angle of curve [-pi, pi]
				dx = x2 - cx;
				dy = y2 - cy;
				alpha2 = new Double(Math.atan2(dy, dx)).doubleValue();  // return start point angle of curve [-pi, pi]
				d_alpha = alpha2 - alpha1;
				if (d_alpha > Math.PI ) {   //Then
					d_alpha = new Double(d_alpha - 2 * Math.PI).doubleValue();
				} else if (d_alpha < -Math.PI) {    // Then
					d_alpha = new Double(2 * Math.PI + d_alpha).doubleValue();
				}   //End If
				numSteps = ConversionUtils.CInt(Math.floor(Math.abs(d_alpha / d_theta))) ;
				d_theta = d_alpha / numSteps;    // signed delta theta
				double rx, ry ;
				double superE=0f;
				double dist_j;
				// calculate superelevation
				if (roadDesign.getPreferredUnit()==1) {
					superE = ((roadDesign.getSpeedLimit() * 1.467f)*(roadDesign.getSpeedLimit() * 1.467f) / 32.2f / cRadius - roadDesign.getSideFrictionCoef()) * 100f;
				} else if (roadDesign.getPreferredUnit()==2) {
					superE = ((roadDesign.getSpeedLimit()*roadDesign.getSpeedLimit()) / 127f / cRadius - roadDesign.getSideFrictionCoef()) * 100f;                    
				}
				superE = (ConversionUtils.CInt(superE / 2) + 1) * 0.02f; // ceiling round to 2%//s

				for (j=0;j<numSteps;j++) {
					rx = new Double(cx + cRadius * Math.cos(alpha1 + j * d_theta)).doubleValue();
					ry = new Double(cy + cRadius * Math.sin(alpha1 + j * d_theta)).doubleValue();
					vrmlPoints_roadCenter[dbCount] = new RoadGeoDB();
					dist_j = roadDesign.getElevationMarks().get(i - 1).getDistance() + cRadius * Math.abs(j * d_theta);
					vrmlPoints_roadCenter[dbCount].Load(rx, ry, calcDesignElevation(dist_j), superE);
					dbCount += 1;
				}   //Next j

			}   // End If
			x1 = x2;
			y1 = y2;
			lastMark = roadDesign.getElevationMarks().get(i).getLocation();
			lastMarkType = roadDesign.getElevationMarks().get(i).getMarkerType();

		}   //Next i
		// save last data point
		vrmlPoints_roadCenter[dbCount] = new RoadGeoDB();
		vrmlPoints_roadCenter[dbCount].Load(x1, y1, roadDesign.getVerticalConstructMarks().get(roadDesign.getvConstructMarkCount() - 1).getElevation(), 0);
		dbCount += 1;

		//       frmProgress.ProgressBar1.Maximum = dbCount
		// smooth superelevation if abs(difference) greater that 0.01
		double d_se ;
		// up edge
		for (i=1; i<dbCount; i++) {
			d_se = vrmlPoints_roadCenter[i].getSuperElevation() - vrmlPoints_roadCenter[i - 1].getSuperElevation();
			if (d_se > 0.01f) { // Then
				vrmlPoints_roadCenter[i].setSuperElevation( vrmlPoints_roadCenter[i - 1].getSuperElevation() + 0.01f);
			}   //End If
			//          frmProgress.ProgressBar1.Value = i
			ProgressBar1.updateProgress();
		}   //Next
		// down edge
		for (i=dbCount-1;i>=1; i--) {
			d_se = vrmlPoints_roadCenter[i].getSuperElevation() - vrmlPoints_roadCenter[i - 1].getSuperElevation();
			if (d_se < -0.01f) {// Then
				vrmlPoints_roadCenter[i - 1].setSuperElevation( vrmlPoints_roadCenter[i].getSuperElevation() + 0.01f);
			}
			//            frmProgress.ProgressBar1.Value = i
			ProgressBar1.updateProgress();
		}   //Next

		// ====================================
		// create left & right shoulder curve/boundary
		RoadGeoDB[] vrmlPoints_roadLeft = new RoadGeoDB[dbCount] ;
		RoadGeoDB[] vrmlPoints_roadRight = new RoadGeoDB[dbCount] ;
		RoadGeoDB temp_pointR = new RoadGeoDB();
		RoadGeoDB temp_pointL = new RoadGeoDB();
		double vec_px, vec_py ; // vector perpenticular to p(i-1)-p(i)
		double rdx, rdy, sLen ;  // road segment length
		vrmlPoints_roadRight[0] = new RoadGeoDB();
		vrmlPoints_roadLeft[0] = new RoadGeoDB();
		for (i=1; i<dbCount; i++) {
			//        frmProgress.ProgressBar1.Value = i
			ProgressBar1.updateProgress();
			vrmlPoints_roadRight[i] = new RoadGeoDB();
			vrmlPoints_roadLeft[i] = new RoadGeoDB();

			rdx = vrmlPoints_roadCenter[i].getX() - vrmlPoints_roadCenter[i - 1].getX();
			rdy = vrmlPoints_roadCenter[i].getY() - vrmlPoints_roadCenter[i - 1].getY();
			sLen = Math.sqrt(rdx*rdx + rdy*rdy);
			vec_px = new Double(rdy / sLen).doubleValue();
			vec_py = new Double(-rdx / sLen).doubleValue();
			// right side ---------------
			vrmlPoints_roadRight[i].Load(vrmlPoints_roadCenter[i].getX() + (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_px, 
					vrmlPoints_roadCenter[i].getY() + (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_py, 
					vrmlPoints_roadCenter[i].getElevation(), 
					vrmlPoints_roadCenter[i].getSuperElevation());

			temp_pointR.Load(vrmlPoints_roadCenter[i - 1].getX() + (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_px, 
					vrmlPoints_roadCenter[i - 1].getY() + (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_py, 
					vrmlPoints_roadCenter[i - 1].getElevation(), 
					vrmlPoints_roadCenter[i - 1].getSuperElevation());
			if (i > 1) {    // Then
				vrmlPoints_roadRight[i - 1].setX( 0.5f * (vrmlPoints_roadRight[i - 1].getX() + temp_pointR.getX()));
				vrmlPoints_roadRight[i - 1].setY( 0.5f * (vrmlPoints_roadRight[i - 1].getY() + temp_pointR.getY()));
				vrmlPoints_roadRight[i - 1].setElevation( 0.5f * (vrmlPoints_roadRight[i - 1].getElevation() + temp_pointR.getElevation()));
			} else if (i == 1) {	// Then
				vrmlPoints_roadRight[0].Load(temp_pointR.getX(), temp_pointR.getY(), temp_pointR.getElevation(), temp_pointR.getSuperElevation());
			}   //End If
			// left side ---------------
			vrmlPoints_roadLeft[i].Load(vrmlPoints_roadCenter[i].getX() - (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_px, 
					vrmlPoints_roadCenter[i].getY() - (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_py, 
					vrmlPoints_roadCenter[i].getElevation(), 
					vrmlPoints_roadCenter[i].getSuperElevation());

			temp_pointL.Load(vrmlPoints_roadCenter[i - 1].getX() - (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_px, 
					vrmlPoints_roadCenter[i - 1].getY() - (0.5f * roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth() + roadDesign.getPreferredShoulderWidth()) * vec_py,
					vrmlPoints_roadCenter[i - 1].getElevation(), 
					vrmlPoints_roadCenter[i - 1].getSuperElevation());
			if (i > 1)  {// Then
				vrmlPoints_roadLeft[i - 1].setX( 0.5f * (vrmlPoints_roadLeft[i - 1].getX() + temp_pointL.getX()));
				vrmlPoints_roadLeft[i - 1].setY( 0.5f * (vrmlPoints_roadLeft[i - 1].getY() + temp_pointL.getY()));
				vrmlPoints_roadLeft[i - 1].setElevation(0.5f * (vrmlPoints_roadLeft[i - 1].getElevation() + temp_pointL.getElevation()));
			} else if (i == 1) {    // Then
				vrmlPoints_roadLeft[0].Load(temp_pointL.getX(), temp_pointL.getY(), temp_pointL.getElevation(), temp_pointL.getSuperElevation());
			}   //End If

		}   //Next

		// =====================================================
		// create VRML file
		String myVrml_str="", point_str="", base_point_str = "", index_str="" ;
		String texCoordIndexStr="" ;
		String imageStr="" ;
		double init_vehPos=0.0f ;
		if (roadDesign.getPreferredUnit() == 1) { 
			init_vehPos = (0.5f*roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth()  - 0.65f * roadDesign.getPreferredLaneWidth()) * roadDesign.FT2M;
		} else if (roadDesign.getPreferredUnit() == 2) {  // Then // metric
			init_vehPos = 0.5f*roadDesign.getPreferredRoadLaneSizes() * roadDesign.getPreferredLaneWidth()  - 0.65f * roadDesign.getPreferredLaneWidth();
		}   //End If

		myVrml_str = "#VRML V2.0 utf8" + newLine;
		myVrml_str += "WorldInfo {title \"ITS Interdisciplinary Lab Course Prototype\"" + newLine;
		myVrml_str += "info [\"(c) Copyright 2006 ITS Lab, Center For Transportation Studies, University of Minnesota\" ]}";
		myVrml_str += "NavigationInfo {headlight FALSE avatarSize [.2 1.6 .7]}" + newLine;
		myVrml_str += "Background {skyColor [0.0 0.2 0.7, 0.0 0.5 1.0, 1.0 1.0 1.0]" + newLine;
		myVrml_str += "skyAngle [ 1.309, 1.571 ]" + newLine ;
		myVrml_str += "groundColor [0.1 0.10 0.0, 0.4 0.25 0.2, 0.6 0.60 0.6,]}" + newLine;
		myVrml_str += "DirectionalLight { ambientIntensity 0.9  intensity 0.9 color 0.7 0.7 0.6 direction -1 -1 -1 on TRUE }";

		//myVrml_str += " Background {skyColor [0.0 0.2 0.7,0.0 0.5 1.0,1.0 1.0 1.0]"
		//myVrml_str += " skyAngle [ 1.309, 1.571 ] groundColor [0.1 0.10 0.0,0.4 0.25 0.2,0.6 0.60 0.6,]"
		//myVrml_str += " groundAngle [ 1.309, 1.571 ] "
		//myVrml_str += " topUrl ""sky_top.jpg"" "
		//myVrml_str += " frontUrl ""sky_fb.jpg"" "
		//myVrml_str += " leftUrl ""sky_lr.jpg"" "
		//myVrml_str += " backUrl ""sky_fb.jpg"" "
		//myVrml_str += " rightUrl ""sky_lr.jpg""} "

		int mid = ConversionUtils.CInt(dbCount / 2);
		Vector3D startPos=null, endPos=null ;
		Vector3D lookat=null ;
		if (roadDesign.getPreferredUnit() == 1) {// Then
			startPos = new Vector3D(init_vehPos + vrmlPoints_roadCenter[0].getX() * roadDesign.FT2M, 1 + vrmlPoints_roadCenter[0].getElevation() * roadDesign.FT2M, vrmlPoints_roadCenter[0].getY() * roadDesign.FT2M);
			endPos = new Vector3D(init_vehPos + vrmlPoints_roadCenter[dbCount - 1].getX() * roadDesign.FT2M, 1 + vrmlPoints_roadCenter[dbCount - 1].getElevation() * roadDesign.FT2M, vrmlPoints_roadCenter[dbCount - 1].getY() * roadDesign.FT2M);
			lookat = new Vector3D(init_vehPos + vrmlPoints_roadCenter[1].getX() * roadDesign.FT2M, 1 + vrmlPoints_roadCenter[1].getElevation() * roadDesign.FT2M, vrmlPoints_roadCenter[1].getY() * roadDesign.FT2M);
		} else if (roadDesign.getPreferredUnit() == 2) {  // Then
			startPos = new Vector3D(init_vehPos + vrmlPoints_roadCenter[0].getX(), 1 + vrmlPoints_roadCenter[0].getElevation(), vrmlPoints_roadCenter[0].getY());
			endPos = new Vector3D(init_vehPos + vrmlPoints_roadCenter[dbCount - 1].getX(), 1 + vrmlPoints_roadCenter[dbCount - 1].getElevation(), vrmlPoints_roadCenter[dbCount - 1].getY());
			lookat = new Vector3D(init_vehPos + vrmlPoints_roadCenter[1].getX(), 1 + vrmlPoints_roadCenter[1].getElevation(), vrmlPoints_roadCenter[1].getY());
		} //End If
		Vector3D upAxis = new Vector3D(0, 1, 0);
		String viewpoint ;
		viewpoint = convertCameraModel(startPos, lookat, upAxis);

		myVrml_str += " Viewpoint { position " + startPos.toStr() + " orientation " + viewpoint + " fieldOfView 0.785 description \"Start\"} " + newLine;
		// optional 2nd viewpoint
		//If myUnit = 1 Then
		//point_str = (vrmlPoints_roadCenter(0).getX() * FT2M).ToString & " " _
		//    & (20 + vrmlPoints_roadCenter(0).getElevation() * FT2M).ToString & " " _
		//    & (vrmlPoints_roadCenter(0).getY() * FT2M).ToString
		//ElseIf myUnit = 2 Then
		//    point_str = (vrmlPoints_roadCenter(0).getX()).ToString & " " _
		//        & (20 + vrmlPoints_roadCenter(0).getElevation()).ToString & " " _
		//        & (vrmlPoints_roadCenter(0).getY()).ToString
		//End If
		//myVrml_str += " Viewpoint { position " & point_str & " orientation 1 0 0 -0.57 fieldOfView 0.785 description ""Aerial Camera 1""} " & newLine
		point_str = "";
		base_point_str = "" ;
		index_str = "";
		texCoordIndexStr = "";
		String keyStr, keyValStr, keyRadStr, keyValStrReverse, keyRadStrReverse ;
		keyStr = "";
		keyValStr = "" ;     // position string
		keyValStrReverse = "" ;  // reverse positioin string
		if (roadDesign.getPreferredUnit() == 1) {// Then
			for (i=0; i<dbCount; i++) {
				// left/center/right points
				point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY() * roadDesign.FT2M) + ", " + newLine;
				point_str += ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY() * roadDesign.FT2M) + ", " + newLine;
				point_str += (vrmlPoints_roadRight[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY() * roadDesign.FT2M) + ", " + newLine;

				base_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY() * roadDesign.FT2M) + ", " + newLine;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX() * roadDesign.FT2M) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY() * roadDesign.FT2M) + ", " + newLine;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX() * roadDesign.FT2M) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY() * roadDesign.FT2M) + ", " + newLine;
				base_point_str += (vrmlPoints_roadRight[i].getX() * roadDesign.FT2M) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY() * roadDesign.FT2M) + ", " + newLine;
				base_point_str += (vrmlPoints_roadRight[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY() * roadDesign.FT2M) + ", " + newLine;

				// animation key fraction & key value for vehicle location
				keyStr += ConversionUtils.CStr(i / dbCount) + ", " + newLine;

				keyValStr += ConversionUtils.CStr((vrmlPoints_roadCenter[i].getX()) * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr((vrmlPoints_roadCenter[i].getElevation()) * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr((vrmlPoints_roadCenter[i].getY()) * roadDesign.FT2M) + ", " + newLine;
				// reverse travel path for opposit dir veh
				keyValStrReverse = ConversionUtils.CStr((vrmlPoints_roadCenter[i].getX()) * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr((vrmlPoints_roadCenter[i].getElevation()) * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr((vrmlPoints_roadCenter[i].getY()) * roadDesign.FT2M) + ", " + keyValStrReverse + newLine;

				//             frmProgress.ProgressBar1.Value = i
				ProgressBar1.updateProgress();
			}   //Next
		} else if (roadDesign.getPreferredUnit() == 2) {  // Then // metric
			for (i=0; i<dbCount; i++) {
				// left/center/right points
				point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY()) + ", " + newLine ;
				point_str += ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY()) + ", " + newLine;
				point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY()) + ", " + newLine;

				base_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY()) + ", " + newLine ;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX()) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY()) + ", " + newLine ;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX()) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY()) + ", " + newLine;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX()) + " 0 " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY()) + ", " + newLine;
				base_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY()) + ", " + newLine;

				// animation key fraction & key value for vehicle location
				keyStr += ConversionUtils.CStr(i / dbCount) + ", " + newLine;

				keyValStr += ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY()) + ", " + newLine;

				keyValStrReverse = ConversionUtils.CStr(vrmlPoints_roadCenter[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadCenter[i].getY()) + ", " + keyValStrReverse + newLine;

				//            frmProgress.ProgressBar1.Value = i
				ProgressBar1.updateProgress();
			}   //Next
		}   //End If
		// ===================================================
		// Road Barrier data points
		String barrier_point_str = "";
		double barrier_height = 1f;  // 1 meter
		if (roadDesign.getPreferredUnit() == 1) { // Then // US customary
			// left/right barrier data points
			for (i=0;i<dbCount;i++) {
				//          frmProgress.ProgressBar1.Value = i
				ProgressBar1.updateProgress();
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation() * roadDesign.FT2M + barrier_height) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY() * roadDesign.FT2M) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY() * roadDesign.FT2M) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY() * roadDesign.FT2M) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX() * roadDesign.FT2M) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation() * roadDesign.FT2M + barrier_height) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY() * roadDesign.FT2M) + ", " + newLine;
			}   //Next
		} else if (roadDesign.getPreferredUnit() == 2) {// Then // metric
			// left/right barrier data points, from 3N to 4N-1
			for (i=0; i<dbCount;i++) {
				//         frmProgress.ProgressBar1.Value = i
				ProgressBar1.updateProgress();
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation() + barrier_height) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY()) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadLeft[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadLeft[i].getY()) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY()) + ", " + newLine;
				barrier_point_str += ConversionUtils.CStr(vrmlPoints_roadRight[i].getX()) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getElevation() + barrier_height) + " " 
						+ ConversionUtils.CStr(vrmlPoints_roadRight[i].getY()) + ", " + newLine;
			}   //Next
		}   //End If

		keyRadStr = "" ;
		keyRadStrReverse = "" ;
		double dvx, dvy ;
		String ang_str="" ;
		double ang, init_ang, end_ang ;
		dvx = (vrmlPoints_roadRight[dbCount - 1].getX() - vrmlPoints_roadCenter[dbCount - 1].getX());
		dvy = (vrmlPoints_roadRight[dbCount - 1].getY() - vrmlPoints_roadCenter[dbCount - 1].getY());
		end_ang = new Double(Math.PI - Math.atan2(dvy, dvx)).doubleValue();

		dvx = (vrmlPoints_roadRight[0].getX() - vrmlPoints_roadCenter[0].getX());
		dvy = (vrmlPoints_roadRight[0].getY() - vrmlPoints_roadCenter[0].getY());
		init_ang = new Double(Math.PI - Math.atan2(dvy, dvx)).doubleValue();

		// texture patching
		String barrier_index_str = "";
		String barrier_texCoordIndexStr = "";
		String base_left_wall_index_str = "" ;
		String base_right_wall_index_str = "" ;
		String base_front_wall_index_str = "" ;
		String base_rear_wall_index_str = "" ;
		String base_bottom_index_str = "" ;
		for (i = 0; i<=dbCount - 2; i++) {
			// left quad road
			index_str += ConversionUtils.CStr(3 * i) + "," + ConversionUtils.CStr(3 * i + 1) + "," + ConversionUtils.CStr(3 * (i + 1) + 1) + "," + ConversionUtils.CStr(3 * (i + 1)) + "," + ConversionUtils.CStr(3 * i) + ",-1," + newLine;
			// right quad road
			index_str +=  ConversionUtils.CStr(3 * i + 1) + "," +  ConversionUtils.CStr(3 * i + 2) + "," +  ConversionUtils.CStr(3 * (i + 1) + 2) + "," +  ConversionUtils.CStr(3 * (i + 1) + 1) + "," +  ConversionUtils.CStr(3 * i + 1) + ",-1," + newLine;

			// base left wall
			base_left_wall_index_str += ConversionUtils.CStr(5*(i+1)) + "," + ConversionUtils.CStr(5*(i+1) + 1) + "," + ConversionUtils.CStr(5 * i + 1) + "," + ConversionUtils.CStr(5 * i) + "," + ConversionUtils.CStr(5*(i+1)) + ",-1," + newLine;
			// right base wall
			base_right_wall_index_str += ConversionUtils.CStr(5*i+4) + "," + ConversionUtils.CStr(5*i + 3) + "," + ConversionUtils.CStr(5 * (i+1)+3) + "," + ConversionUtils.CStr(5 *(i+1)+4) + "," + ConversionUtils.CStr(5*i+4) + ",-1," + newLine;
			// bottom base
			base_bottom_index_str += ConversionUtils.CStr(5*i+3) + "," + ConversionUtils.CStr(5*i+1) + "," + ConversionUtils.CStr(5*(i+1)+1) + "," + ConversionUtils.CStr(5*(i+1)+3) + "," + ConversionUtils.CStr(5*i+3) + ",-1," + newLine;

			// left barrier quad
			barrier_index_str +=  ConversionUtils.CStr(4 * i) + "," +  ConversionUtils.CStr(4 * i + 1) + "," +  ConversionUtils.CStr(4 * (i + 1) + 1) + "," +  ConversionUtils.CStr(4 * (i + 1)) + "," +  ConversionUtils.CStr(4 * i) + ",-1," + newLine;
			// right barrier quad 
			barrier_index_str +=  ConversionUtils.CStr(4 * i + 2) + "," +  ConversionUtils.CStr(4 * i + 3) + "," +  ConversionUtils.CStr(4 * (i + 1) + 3) + "," +  ConversionUtils.CStr(4 * (i + 1) + 2) + "," +  ConversionUtils.CStr(4 * i + 2) + ",-1," + newLine;

			// texture coordinate index str
			texCoordIndexStr += "1,0,3,2,1,-1,  0,1,2,3,4,-1" + newLine;
			barrier_texCoordIndexStr += "3,0,1,2,3,-1,  1,2,3,0,1,-1" + newLine;
			//        frmProgress.ProgressBar1.Value = i
			ProgressBar1.updateProgress();

			// animation for vehicle rotation
			dvx = (vrmlPoints_roadRight[i].getX() - vrmlPoints_roadCenter[i].getX());
			dvy = (vrmlPoints_roadRight[i].getY() - vrmlPoints_roadCenter[i].getY());
			ang = new Double(Math.PI - Math.atan2(dvy, dvx)).doubleValue();
			//If ang < 0 Then
			//ang += 2 * Math.PI
			//End If
			ang_str = ConversionUtils.CStr(ang) ;
			keyRadStr += " 0.0 1.0 0.0 " + ang_str + newLine;
			keyRadStrReverse = " 0.0 1.0 0.0 " + ang_str + keyRadStrReverse + newLine;
		}   //Next
		// wall front
		base_front_wall_index_str = "0,1,2,3,4" ;
		// wall rear
		base_rear_wall_index_str = ConversionUtils.CStr(5*(dbCount-1))+","+ConversionUtils.CStr(5*(dbCount-1)+1)+","+
				ConversionUtils.CStr(5*(dbCount-1)+2)+","+ConversionUtils.CStr(5*(dbCount-1)+3)+","+ConversionUtils.CStr(5*(dbCount-1)+4)+"," ;

		keyRadStr += " 0.0 1.0 0.0 " + ang_str + newLine;
		keyRadStrReverse = " 0.0 1.0 0.0 " + ang_str + keyRadStrReverse + newLine;

		// select road pavement image file
		switch (ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())) {
		case 2:  // 2 lane highway
			imageStr = "\"http://128.101.111.90/Lab_Mod/roadtexture1.png\", \"roadtexture1.png\""  ;   // double quote "
			break;
		case 4:  // 4 lane highway
			imageStr = "\"http://128.101.111.90/Lab_Mod/roadtexture2.png\", \"roadtexture2.png\"" ;   // double quote "
			break;
		case 6:  // 6 lane highway
			imageStr = "\"http://128.101.111.90/Lab_Mod/roadtexture3.png\", \"roadtexture3.png\"" ;   // double quote "
			break;
		} 
		// road barrier image file
		String barrier_imageStr = "\"http://128.101.111.90/Lab_Mod/roadbarrier.png\", \"roadbarrier.png\"" ;    // double quote "

		// road wall dirt image file
		//String dirt_imageStr = "\"http://128.101.111.90/Lab_Mod/dirt.png\", \"dirt.png\"" ;    // double quote "

		// create EXTERNPROTO reference
		myVrml_str += " EXTERNPROTO toyota [][\"http://128.101.111.90/Lab_Mod/toyota_proto.wrl\", \"toyota_proto.wrl\" ]" + newLine;
		ProgressBar1.updateProgress();
		// ================================================
		// create road geometry vrml string
		myVrml_str += getShapeTextureStr("Road", point_str, index_str, imageStr, texCoordIndexStr);
		// left/right wall
		String wall_color = "0 0.8 0" ;
		String bottom_color = "0.8 0.8 0" ;
		myVrml_str += getShapeMaterialStr("LTBaseWall", base_point_str, base_left_wall_index_str, wall_color, true);
		myVrml_str += getShapeMaterialStr("RTBaseWall", base_point_str, base_right_wall_index_str, wall_color, false);
		myVrml_str += getShapeMaterialStr("FTBaseWall", base_point_str, base_front_wall_index_str, wall_color, false);
		myVrml_str += getShapeMaterialStr("RRBaseWall", base_point_str, base_rear_wall_index_str, wall_color, false);
		myVrml_str += getShapeMaterialStr("BTBaseWall", base_point_str, base_bottom_index_str, bottom_color, false);

		myVrml_str += getShapeTextureStr("Barrier", barrier_point_str, barrier_index_str, barrier_imageStr, barrier_texCoordIndexStr);
		ProgressBar1.updateProgress();
		//dvx = (vrmlPoints_roadRight(0).getX() - vrmlPoints_roadCenter(0).getX())
		//dvy = (vrmlPoints_roadRight(0).getY() - vrmlPoints_roadCenter(0).getY())
		//ang_str = (0.5 * Math.PI + Math.Atan2(dvy, dvx)).ToString
		ang_str = ConversionUtils.CStr(init_ang);
		// create vehicle viewpoint vrml string
		myVrml_str += getVehicleStr("Veh", startPos.toStr(), ang_str);

		// create no viewpoint vehicle moving from opposite side
		myVrml_str += getReverseVehicleStr("VehR", endPos.toStr(), ConversionUtils.CStr(end_ang));
		ProgressBar1.updateProgress();
		// ================================================
		// animation clock
		double travelTime=0f ;
		if (roadDesign.getPreferredUnit() == 1) { 
			travelTime = total_roadLen * 3600f / 5280f / roadDesign.getSpeedLimit(); // ft/MPH (sec)
		} else if (roadDesign.getPreferredUnit() == 2) { 
			travelTime = total_roadLen * 3.6f / roadDesign.getSpeedLimit(); // m/Km/h (sec)
		}

		myVrml_str += "DEF Clock TimeSensor { cycleInterval " + ConversionUtils.CStr(travelTime) + " loop TRUE}" + newLine;
		myVrml_str += "DEF TravelPath PositionInterpolator { key [ " + keyStr + " ] keyValue [ " + keyValStr + " ]}" + newLine;
		myVrml_str += "DEF RotatePath OrientationInterpolator { key [ " + keyStr + " ] keyValue [ " + keyRadStr + " ]}" + newLine;
		myVrml_str += "DEF ReverseTravelPath PositionInterpolator { key [ " + keyStr + " ] keyValue [ " + keyValStrReverse + " ]}" + newLine;
		myVrml_str += "DEF ReverseRotatePath OrientationInterpolator { key [ " + keyStr + " ] keyValue [ " + keyRadStrReverse + " ]}" + newLine;

		myVrml_str += "ROUTE Clock.fraction_changed TO TravelPath.set_fraction" + newLine;
		myVrml_str += "ROUTE TravelPath.value_changed TO Veh.set_translation" + newLine;
		myVrml_str += "ROUTE Clock.fraction_changed TO RotatePath.set_fraction" + newLine;
		myVrml_str += "ROUTE RotatePath.value_changed TO Veh.set_rotation" + newLine;

		// reverse path
		myVrml_str += "ROUTE Clock.fraction_changed TO ReverseTravelPath.set_fraction" + newLine;
		myVrml_str += "ROUTE ReverseTravelPath.value_changed TO VehR.set_translation" + newLine;
		myVrml_str += "ROUTE Clock.fraction_changed TO ReverseRotatePath.set_fraction" + newLine;
		myVrml_str += "ROUTE ReverseRotatePath.value_changed TO VehR.set_rotation" + newLine;
		ProgressBar1.updateProgress();
		// database created, save to a temp file
		String filename="" ;
		try {
			// check os info, Linux, Mac or Windows
			String osinfo = System.getProperty("os.name");
			String osarch = System.getProperty("os.arch");
			//System.out.println(osinfo+","+osarch);

			if (osinfo.indexOf("Windows")>=0) {
				//filename = "C:\\vrml_db" ;
				String username = System.getProperty("user.name");
				filename = "C:\\Documents and Settings\\"+username+"\\Desktop\\vrml_db" ;
			} else {    //if (osinfo.indexOf("Linux")>=0){
				filename = "vrml_db" ;
			} 
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(myVrml_str);
			out.flush();
			out.close();
		} catch (IOException e) {

			String err_msg = e.toString() ;
			//System.out.println("vDrawArea:createVRMLFile:"+err_msg);
			if (err_msg.indexOf("FileNotFound")>0) {
				filename = "C:\\vrml_db" ;
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(filename));
					out.write(myVrml_str);
					out.flush();
					out.close();
					sb.setStatusBarText(1, "Restore animation file in root directory!") ; 
				} catch (IOException ioe) {
					System.out.println("vDrawArea:createVRMLFile:"+ioe.toString());   
					sb.setStatusBarText(1, "Creating 3D Model, "+err_msg) ; //Error:
				}
			} // if file not found
		}
		ProgressBar1.dispose();
	}   // createVRMLFile

	// generate vehicle VRML string
	private String getVehicleStr(String vehName, String translate, String angle) {
		String veh_str ;
		double lane_width_m ;
		double camera_offset ;
		int i ;

		if (roadDesign.getPreferredUnit() == 1) { 
			// US customary unit
			lane_width_m = roadDesign.getPreferredLaneWidth() * roadDesign.FT2M;
		} else {
			lane_width_m = roadDesign.getPreferredLaneWidth();
		}
		veh_str = "DEF " + vehName + " Transform {" +
				" translation " + translate +
				" rotation 0 1 0 " + angle +
				" children [";
		camera_offset = 1.75f;
		for (i = 0 ;i< roadDesign.getPreferredRoadLaneSizes() / 2 ;i++){
			camera_offset = 1.5f + lane_width_m * i;
			veh_str += "DEF VEH_VIEW" + ConversionUtils.CStr(i + 1) + " Viewpoint {" +
					" description \"Lane" + ConversionUtils.CStr(i + 1) + "\" orientation 1 0 0 -0.03  position " + ConversionUtils.CStr(camera_offset) + " 1.5 -1.4 }" + newLine;
		}   //Next i
		veh_str += "DEF VEH_BIRDVIEW Viewpoint {" +
				" description \"Vehicle Top Bird View\" orientation 1 0 0 -1.57  position " + ConversionUtils.CStr(camera_offset) + " 40 0 }";

		//include a vehice
		veh_str += "Transform { rotation 0 1 0 -3.14 translation 1.75 0 0 children [toyota {}]}";
		veh_str += "]}";

		return veh_str + newLine;
	}   //getVehicleStr

	// generate opposite direction vehicle string
	private String getReverseVehicleStr(String vehName, String translate, String angle) {
		String veh_str ;

		veh_str = "DEF " + vehName + " Transform {" +
				" translation " + translate +
				" rotation 0 1 0 " + angle +
				" children [";

		//include a vehice
		veh_str += "Transform { rotation 0 1 0 0 translation -1.75 0 0 children [toyota {}]}";
		veh_str += "]}";

		return veh_str + newLine;
	}   //getReverseVehicleStr

	// generate VRML shape texture string
	private String getShapeTextureStr( 
			String name , 
			String points , 
			String coordIndex , 
			String imageName , 
			String texCoordIndexStr ) {
		String vrml_str ;
		vrml_str = "DEF " + name + " Shape{";
		vrml_str += " appearance Appearance{texture ImageTexture{url[" + imageName + "]}}" + newLine;
		vrml_str += " geometry IndexedFaceSet{ ccw FALSE creaseAngle .785 " + newLine;
		vrml_str += "  coord Coordinate{point[";
		// point data here
		vrml_str += points;
		vrml_str += "]}" + newLine;
		vrml_str += "  coordIndex[";
		// coordinate index here;
		vrml_str += coordIndex;
		vrml_str += "]" + newLine;
		//vrml_str += "  normal Normal{vector [0 1 0]}" + newLine

		// texture mapping
		vrml_str += "texCoord TextureCoordinate {point [0 0, 1 0, 1 1, 0 1, 0 0]}" + newLine;
		vrml_str += "texCoordIndex [ " + texCoordIndexStr + " ]" + newLine;
		// end of geometry;
		vrml_str += " }" + newLine;
		// end of shape
		vrml_str += "}" + newLine;

		return vrml_str;
	}   //getShapeStr

	// create VRML shape material string
	private String getShapeMaterialStr( 
			String name , 
			String points , 
			String coordIndex , 
			String colorStr,
			boolean def_flag
			) {
		String vrml_str ;
		vrml_str = "DEF " + name + " Shape{";
		vrml_str += " appearance Appearance{material Material{diffuseColor " + colorStr + "}}" + newLine;
		vrml_str += " geometry IndexedFaceSet{ ccw FALSE creaseAngle .785 " + newLine;
		if (def_flag) {
			vrml_str += " coord DEF road_base Coordinate{ point[";
			// point data here
			vrml_str += points;
			vrml_str += "]}" + newLine;
		} else {
			// use
			vrml_str += " coord USE road_base" + newLine;
		}
		vrml_str += "  coordIndex[";
		// coordinate index here;
		vrml_str += coordIndex;
		vrml_str += "]" + newLine;
		//vrml_str += "  normal Normal{vector [0 1 0]}" + newLine

		// end of geometry;
		vrml_str += " }" + newLine;
		// end of shape
		vrml_str += "}" + newLine;

		return vrml_str;
	}   //getShapeStr

	// create VRML camera look at vector
	public String convertCameraModel(Vector3D pos, Vector3D at, Vector3D up) {
		Vector3D n, tempV, v, normAxis, newY ;
		Quaternion normQuat, invNormQuat, yQuat, newYQuat, rotYQuat, rotQuat ;
		double tempD ;
		n = at.vSub(pos);
		n = n.vUnit();
		up = up.vUnit();
		tempD = up.vDot(n);
		tempV = n.vScale(tempD);
		v = up.vSub(tempV);
		v = v.vUnit();
		normAxis = new Vector3D(n.getY(), -n.getX(), 0);
		if (normAxis.vDot(normAxis) < 0.00000001) {
			if (n.getZ() > 0.0) {
				normQuat = new Quaternion(new Vector3D(0.0f, 1.0f, 0.0f), 1.0f);
			} else {
				normQuat = new Quaternion(new Vector3D(0.0f, 0.0f, 0.0f), 1.0f);
			}
		} else {
			normAxis = normAxis.vUnit();
			normQuat = buildRotateQuaternion(normAxis, -n.getZ());
		}
		invNormQuat = new Quaternion(normQuat.vectPart.vScale(-1), normQuat.realPart);
		yQuat = new Quaternion(new Vector3D(0.0f, 1.0f, 0.0f), 0.0f);
		newYQuat = normQuat.QQMul(yQuat);
		newYQuat = newYQuat.QQMul(invNormQuat);
		newY = newYQuat.vectPart;
		tempV = newY.vCross(v);
		if (tempV.vDot(tempV) < 0.00000001) {
			tempV = new Vector3D(0.0f, -v.getZ(), v.getY());
			if (tempV.vDot(tempV) < 0.00000001) {
				tempV = new Vector3D(v.getZ(), 0.0f, -v.getX());
			}   //End If
		}   //End If
		tempV = tempV.vUnit();
		//      alert(tempV.x+" "+tempV.y+" "+tempV.z); // ***DEBUG
		rotYQuat = buildRotateQuaternion(tempV, newY.vDot(v));
		rotQuat = rotYQuat.QQMul(normQuat);
		return rotQuat.toAxisAngle();
	}   //convertCameraModel

	// VRML viewpoint calculation
	public Quaternion buildRotateQuaternion(Vector3D axis, double cosAngle) {
		double angle;
		double sinHalfAngle, cosHalfAngle ;
		angle = 0.0;
		sinHalfAngle = 0.0f;
		cosHalfAngle = 0.0f;
		Quaternion r ;
		if (cosAngle > 1.0) {
			cosAngle = 1.0f;
		}
		if (cosAngle < -1.0) {
			cosAngle = -1.0f;
		}
		angle = Math.acos(cosAngle);
		sinHalfAngle = new Double(Math.sin(angle / 2.0)).doubleValue();;
		cosHalfAngle = new Double(Math.cos(angle / 2.0)).doubleValue();
		r = new Quaternion(axis.vScale(sinHalfAngle), cosHalfAngle);
		return r;
	}   //buildRotateQuaternion

	// List station data in a table
	public void popStationData(){
		frmStationTable = new JFrame("View Station Data") ;
		frmStationTable.setSize(360, 300);
		//Make sure we have nice window decorations.
		//      frmStationTable.setDefaultLookAndFeelDecorated(true);
		frmStationTable.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_savereport = new JMenuItem("Save Data") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_savereport) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		menu_bar.add(menu_file) ;     // add menu
		frmStationTable.setJMenuBar(menu_bar) ;

		file_savereport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						station_saveData();  
						sb.setStatusBarText(0, "Save Station Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						station_printData();  
						sb.setStatusBarText(0, "Print Station Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						frmStationTable.dispose();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		String unitStr; 
		if (roadDesign.getPreferredUnit()==1) {
			unitStr = " (ft)" ;
		} else {
			unitStr=" (m)" ;
		}
		String[] headers = { "Station ID", "Distance"+unitStr, "Elevation"+unitStr, "Grade" };
		int[] fieldSize = {15, 15, 15, 15} ;
		stationPrintStr = PrintText.StrFormat(0,"ID", fieldSize[0]) +
				PrintText.StrFormat(0,"Distance"+unitStr, fieldSize[1]) +
				PrintText.StrFormat(0,"Elavation"+unitStr, fieldSize[2]) + 
				PrintText.StrFormat(0, "Grade", fieldSize[3]) + "\n" ;

		String[][] data = new String[roadDesign.getElevationMarkCount()][headers.length];
		int i;
		double grade ;
		for (i=0;i<roadDesign.getElevationMarkCount(); i++) {
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(roadDesign.getElevationMarks().get(i).getDistance()*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(roadDesign.getElevationMarks().get(i).getElevation()*1000f)/1000f);
			if (i==0) {
				data[i][3] = "N/A" ;
			} else {
				grade = (roadDesign.getElevationMarks().get(i).getElevation()-roadDesign.getElevationMarks().get(i-1).getElevation())/(roadDesign.getElevationMarks().get(i).getDistance()-roadDesign.getElevationMarks().get(i-1).getDistance()) ;
				data[i][3] = ConversionUtils.CStr(Math.round(grade*1000f)/1000f);
			}

			for (int j=0; j<4; j++){
				stationPrintStr += PrintText.StrFormat(0,data[i][j].toString(), fieldSize[j]) ;
			}
			stationPrintStr += "\n" ;
		}   // for i
		JTable table = new JTable(data, headers) {
			// override isCellEditable method, , 11/13/06
			public boolean isCellEditable(int row, int column) {
				// all un-editable
				return false;
			}    // isCellEditable method
		} ;
		table.setPreferredScrollableViewportSize(new Dimension(360, 300));
		table.setColumnSelectionAllowed(true) ;
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		frmStationTable.add(scrollPane);
		//Get the column model.
		javax.swing.table.TableColumnModel colModel = table.getColumnModel();
		//Get the column at index pColumn, and set its preferred width.
		colModel.getColumn(0).setPreferredWidth(24);   


		//Display the window.
		Dimension screen = getToolkit().getDefaultToolkit().getScreenSize();
		double top = 0.5*(screen.getWidth()-frmStationTable.getWidth());
		double left = 0.5*(screen.getHeight()-frmStationTable.getHeight());
		int x = new Double(top).intValue();
		int y = new Double(left).intValue();
		frmStationTable.setLocation(x, y);

		frmStationTable.pack();
		frmStationTable.setVisible(true);
		frmStationTable.show();

	}   // popStationData ;

	// display elevation profile data including vertical curves etc.
	public void popDesignedElevationData(){
		frmDesignedElevationTable = new JFrame("View Designed Elevation Data") ;
		frmDesignedElevationTable.setSize(300, 250);
		//Make sure we have nice window decorations.
		//      frmDesignedElevationTable.setDefaultLookAndFeelDecorated(true);
		frmDesignedElevationTable.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_savedata = new JMenuItem("Save Data") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_savedata) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		menu_bar.add(menu_file) ;     // add menu
		frmDesignedElevationTable.setJMenuBar(menu_bar) ;

		file_savedata.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						saveElevationProfile();  
						sb.setStatusBarText(0, "Save Designed Elevation Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						elevation_printData();  
						sb.setStatusBarText(0, "Print Mass Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						frmDesignedElevationTable.dispose();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		String unitStr="" ; 
		if (roadDesign.getPreferredUnit()==1) {
			unitStr = " (ft)" ;
		} else {
			unitStr=" (m)" ;
		}
		String[] headers = { "Data", "Distance"+unitStr, "Elevation"+unitStr };
		int[] fieldSize = {6, 16, 16} ;
		elevationPrintStr = PrintText.StrFormat(0,"Data", fieldSize[0]) +
				PrintText.StrFormat(0,"Distance"+unitStr, fieldSize[1]) +
				PrintText.StrFormat(0,"Elevation"+unitStr, fieldSize[2]) + "\n" ;

		String[][] data = new String[fillCutSteps][headers.length];
		int i;
		for (i=0;i<fillCutSteps; i++) {
			designElevation[i] = calcDesignElevation(i*ComputeStepSize) ;
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(i*ComputeStepSize*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(designElevation[i]*1000f)/1000f);

			for (int j=0; j<3; j++){
				elevationPrintStr += PrintText.StrFormat(0,data[i][j].toString(), fieldSize[j]) ;
			}
			elevationPrintStr += "\n" ;
		}   // for i
		JTable table = new JTable(data, headers) {
			// override isCellEditable method, , 11/13/06
			public boolean isCellEditable(int row, int column) {
				// all un-editable
				return false;
			}    // isCellEditable method
		} ;
		table.setPreferredScrollableViewportSize(new Dimension(300, 250));
		table.setColumnSelectionAllowed(true) ;
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		frmDesignedElevationTable.add(scrollPane);
		//Get the column model.
		javax.swing.table.TableColumnModel colModel = table.getColumnModel();
		//Get the column at index pColumn, and set its preferred width.
		colModel.getColumn(0).setPreferredWidth(24);   


		//Display the window.
		Dimension screen = getToolkit().getDefaultToolkit().getScreenSize();
		double top = 0.5*(screen.getWidth()-frmDesignedElevationTable.getWidth());
		double left = 0.5*(screen.getHeight()-frmDesignedElevationTable.getHeight());
		int x = new Double(top).intValue();
		int y = new Double(left).intValue();
		frmDesignedElevationTable.setLocation(x, y);

		frmDesignedElevationTable.pack();
		frmDesignedElevationTable.setVisible(true);
		frmDesignedElevationTable.show();

	}   // popDesignedElevationData ;

	// display mass diagram data profile, 
	// mass diagram is an integral/accumulation of cut/fill profile
	public void popMassData(){
		frmMassTable = new JFrame("View Mass Data") ;
		frmMassTable.setSize(300, 250);
		//Make sure we have nice window decorations.
		//      frmMassTable.setDefaultLookAndFeelDecorated(true);
		frmMassTable.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_savereport = new JMenuItem("Save Data") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_savereport) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		menu_bar.add(menu_file) ;     // add menu
		frmMassTable.setJMenuBar(menu_bar) ;

		file_savereport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						saveMassDiagram();  
						sb.setStatusBarText(0, "Save Mass Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						mass_printData();  
						sb.setStatusBarText(0, "Print Mass Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						frmMassTable.dispose();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		String unitStr="", unitStr3="" ; 
		if (roadDesign.getPreferredUnit()==1) {
			unitStr = " (ft)" ;
			unitStr3 = " (yd^3)" ;
		} else {
			unitStr=" (m)" ;
			unitStr3=" (m^3)" ;
		}
		String[] headers = { "Data", "Distance"+unitStr, "Mass"+unitStr3 };
		int[] fieldSize = {6, 16, 16} ;
		massPrintStr = PrintText.StrFormat(0,"Data", fieldSize[0]) +
				PrintText.StrFormat(0,"Distance"+unitStr, fieldSize[1]) +
				PrintText.StrFormat(0,"Mass"+unitStr3, fieldSize[2]) + "\n" ;

		String[][] data = new String[fillCutSteps][headers.length];
		int i;
		for (i=0;i<fillCutSteps; i++) {
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(i*ComputeStepSize*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(accuMass[i]*1000f)/1000f);

			for (int j=0; j<3; j++){
				massPrintStr += PrintText.StrFormat(0,data[i][j].toString(), fieldSize[j]) ;
			}
			massPrintStr += "\n" ;
		}   // for i
		JTable table = new JTable(data, headers) {
			// override isCellEditable method, , 11/13/06
			public boolean isCellEditable(int row, int column) {
				// all un-editable
				return false;
			}    // isCellEditable method
		} ;
		table.setPreferredScrollableViewportSize(new Dimension(300, 250));
		table.setColumnSelectionAllowed(true) ;
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		frmMassTable.add(scrollPane);
		//Get the column model.
		javax.swing.table.TableColumnModel colModel = table.getColumnModel();
		//Get the column at index pColumn, and set its preferred width.
		colModel.getColumn(0).setPreferredWidth(24);   


		//Display the window.
		Dimension screen = getToolkit().getDefaultToolkit().getScreenSize();
		double top = 0.5*(screen.getWidth()-frmMassTable.getWidth());
		double left = 0.5*(screen.getHeight()-frmMassTable.getHeight());
		int x = new Double(top).intValue();
		int y = new Double(left).intValue();
		frmMassTable.setLocation(x, y);

		frmMassTable.pack();
		frmMassTable.setVisible(true);
		frmMassTable.show();

	}   // popMassData ;

	// display cut and fill data profile
	public void popCutAndFillData(){
		frmCutFillTable = new JFrame("View Cut and Fill Data") ;
		frmCutFillTable.setSize(300, 250);
		//Make sure we have nice window decorations.
		//      frmCutFillTable.setDefaultLookAndFeelDecorated(true);
		frmCutFillTable.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_savereport = new JMenuItem("Save Data") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_savereport) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		menu_bar.add(menu_file) ;     // add menu
		frmCutFillTable.setJMenuBar(menu_bar) ;

		file_savereport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						saveCutAndFill();  
						sb.setStatusBarText(0, "Save Cut and Fill Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						cut_fill_printData();  
						sb.setStatusBarText(0, "Print Cut and Fill Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						frmCutFillTable.dispose();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		String unitStr="" ; 
		if (roadDesign.getPreferredUnit()==1) {
			unitStr = " (ft)" ;
		} else {
			unitStr=" (m)" ;
		}
		String[] headers = { "Data", "Distance"+unitStr, "Cut/Fill"+unitStr };
		int[] fieldSize = {6, 16, 16} ;
		fill_cutPrintStr = PrintText.StrFormat(0,"Data", fieldSize[0]) +
				PrintText.StrFormat(0,"Distance"+unitStr, fieldSize[1]) +
				PrintText.StrFormat(0,"Cut/Fill"+unitStr, fieldSize[2]) + "\n" ;

		String[][] data = new String[fillCutSteps][headers.length];
		int i;
		for (i=0;i<fillCutSteps; i++) {
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(i*ComputeStepSize*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(CutandFill[i]*1000f)/1000f);

			for (int j=0; j<3; j++){
				fill_cutPrintStr += PrintText.StrFormat(0,data[i][j].toString(), fieldSize[j]) ;
			}
			fill_cutPrintStr += "\n" ;
		}   // for i
		JTable table = new JTable(data, headers) {
			// override isCellEditable method, , 11/13/06
			public boolean isCellEditable(int row, int column) {
				// all un-editable
				return false;
			}    // isCellEditable method
		} ;
		table.setPreferredScrollableViewportSize(new Dimension(300, 250));
		table.setColumnSelectionAllowed(true) ;
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		frmCutFillTable.add(scrollPane);
		//Get the column model.
		javax.swing.table.TableColumnModel colModel = table.getColumnModel();
		//Get the column at index pColumn, and set its preferred width.
		colModel.getColumn(0).setPreferredWidth(24);   


		//Display the window.
		Dimension screen = getToolkit().getDefaultToolkit().getScreenSize();
		double top = 0.5*(screen.getWidth()-frmCutFillTable.getWidth());
		double left = 0.5*(screen.getHeight()-frmCutFillTable.getHeight());
		int x = new Double(top).intValue();
		int y = new Double(left).intValue();
		frmCutFillTable.setLocation(x, y);

		frmCutFillTable.pack();
		frmCutFillTable.setVisible(true);
		frmCutFillTable.show();

	}   // popCutAndFillData ;

	public void station_printData(){  // print station data
		try
		{
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(stationPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print Station Data:"+e.toString());
			sb.setStatusBarText(1, "Print Station Data, "+e.toString()) ; //Error: 
		} // try

	}// station print data

	public void elevation_printData(){  // print designed elevation profile data
		try
		{
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(elevationPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print Designed Elevation Data:"+e.toString());
			sb.setStatusBarText(1, "Print Designed Elevation Data, "+e.toString()) ; //Error: 
		} // try

	}// designed elevation profile print data

	public void mass_printData(){  // print mass diagram data
		try
		{
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(massPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print Mass Data:"+e.toString());
			sb.setStatusBarText(1, "Print Mass Data, "+e.toString()) ; //Error: 
		} // try

	}// mass diagram print data

	public void cut_fill_printData(){  // print cut and fill data
		try
		{
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(fill_cutPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print Cut and Fill Data:"+e.toString());
			sb.setStatusBarText(1, "Print Cut and Fill Data, "+e.toString()) ; //Error: 
		} // try

	}// cut and fill print data

	public void station_saveData(){  // save Station data to file
		try
		{
			FileDialog fd=new FileDialog(new JFrame(),"Save Station Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				//String reportStr = generateReport();
				out.write(stationPrintStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			//do nothing
			System.out.println("Save Landmark Data File:"+e.toString());
			sb.setStatusBarText(1, "Saving Landmark Data, "+e.toString()) ; //Error: 
		} // try

	}// station save data
	public RoadDesign getRoadDesign() {
		return roadDesign;
	}
	public void setRoadDesign(RoadDesign roadDesign) {
		this.roadDesign = roadDesign;
	}


}   // vDrawArea

