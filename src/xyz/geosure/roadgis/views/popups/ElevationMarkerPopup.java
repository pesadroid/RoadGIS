package xyz.geosure.roadgis.views.popups;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.model.StationInfo;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.GeoUtils;
import xyz.geosure.roadgis.utils.VectorUtils;
import xyz.geosure.roadgis.views.JCheckBoxGroup;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class ElevationMarkerPopup extends PopupWindow{

	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtEle = new JTextField("0");
	JCheckBox line;          // station,landmark option
	JCheckBox curve;         // station,landmark option
	JCheckBox tangent;       // station,landmark option

	StationInfo sInfo ;         // landmark station info
	private JLabel mX, mY, parentID ;    // popElevationMarkForm
	

	public ElevationMarkerPopup(RoadGISApplication app, StationInfo station) {
		this.app = app;
		this.sInfo = station;
	}
	
	public void build() {
		// open a frame
			popup = new RoadGISPopup(sInfo.title) ;
			popup.setLocation(150,40) ;
			popup.setSize(300,200) ;
			popup.validate() ;
			popup.setVisible(true) ;
			popup.setResizable(false) ;

			ActionListener frame_ok_listener = new ActionListener() {
				public void actionPerformed(ActionEvent aev) {
					System.out.println("txtEle.text=" + txtEle.getText()) ;
					if (txtEle.getText().length()>0) {  // 11/9/06 added
						sInfo.elevation = new Float(txtEle.getText()).floatValue(); 
						MarkerType segment_type =MarkerType.NONE;
						if ( line.isSelected() == true) {
							segment_type = MarkerType.LINE ;
							sInfo.line_option=true ;
							sInfo.curve_option=false ;
							sInfo.tangent_option=false ;
							// =========== 11/9/06 added
							if (sInfo.initial_state != segment_type) {
								// from curve or tangent to line
								Vector2D vec1, vec2 ;
								double L1, L2 ;
								int i = sInfo.parentId ;
								vec1 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint());
								vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
								L1 = vec1.length();
								L2 = vec2.length();
								app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(GeoUtils.makePoint(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getX() + vec1.getX() * L2 / L1, app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getY() + vec1.getY() * L2 / L1));
							}   // if (sInfo.initial_state

						} else if (curve.isSelected() == true ) {
							segment_type = MarkerType.CURVE;
							sInfo.line_option=false ;
							sInfo.curve_option=true ;
							sInfo.tangent_option=false ;
							// =========== 11/9/06 added
							if (sInfo.initial_state != segment_type) {
								// from line or tangent to curve
								// update new point on curve
								Vector2D vec2 ;
								double L1, L2 ;
								int i = sInfo.parentId ;
								vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
								L1 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() * app.getHorizontalDesign().getRoadDesign().getImageScale();
								L2 = vec2.length();
								app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(GeoUtils.makePoint(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getX() + vec2.getX() * L1 / L2,	app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getY() + vec2.getY() * L1 / L2));
							}   // if (sInfo.initial_state
						} else {
							// tangent point
							segment_type = MarkerType.TANGENT;
							sInfo.line_option=false ;
							sInfo.curve_option=false ;
							sInfo.tangent_option=true ;
						}

						if (sInfo.CheckBox_edit == true ) {
							// edit existing landmark
							int index ;
							index = sInfo.dataIndex ;
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(index).setLocation(app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(index).setElevation(sInfo.elevation);
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(index).setParentIndex(sInfo.parentId);
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(index).setSegmentType(segment_type);
							
							
						} else {
							//app.getHorizontalDesign().getRoadDesign().getElevationMarks()[app.getHorizontalDesign().getRoadDesign().getElevationMarkCount()] = new AlignmentMarker();
							// create new landmark
							AlignmentMarker landmark = new AlignmentMarker(app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker(), sInfo.elevation, new Integer(sInfo.parentId).byteValue(), segment_type);
							app.getHorizontalDesign().getRoadDesign().addElevationMark(landmark);
							
							//app.getHorizontalDesign().getRoadDesign().setElevationMarkCount(app.getHorizontalDesign().getRoadDesign().getElevationMarkCount() + 1);
							// save # of data in log buffer
							app.getHorizontalDesign().push2MarkLogBuffer(app.getHorizontalDesign().getRoadDesign().getElevationMarkCount());
						}
						popup.dispose() ;
						//repaint();
					} else {
						// txtEle is empty
						app.getHorizontalDesign().popMessageBox("Elevation Data", "Please specify station elevation!");
					}
				}   // action performed
			} ;
			ActionListener frame_cancel_listener = new ActionListener() {
				public void actionPerformed(ActionEvent aev) {
					popup.dispose() ;
				}
			} ;

			ItemListener frame_line_listener = new ItemListener() {
				public void itemStateChanged(ItemEvent ie) {
					double x1, y1 ;
					String str="";
					str = mX.getText() ;
					x1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
					str = mY.getText() ;
					y1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
					new ElevationMarkerPopup(app,sInfo).getLineMarkLocation(GeoUtils.makePoint(x1, y1));
				}
			} ;
			ItemListener frame_curve_listener = new ItemListener() {
				public void itemStateChanged(ItemEvent ie) {
					double x1, y1 ;
					String str="";
					str = mX.getText() ;
					x1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
					str = mY.getText() ;
					y1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
					getCurveMarkLocation(GeoUtils.makePoint(x1, y1));
				}
			} ;

			popup.setLayout(new GridBagLayout()) ;
			// Create a constrains object, and specify default values
			GridBagConstraints c = new GridBagConstraints() ;
			c.fill = GridBagConstraints.BOTH ; // component grows in both directions
			c.weightx = 1.0 ; c.weighty = 1.0 ;

			c.gridx = 0 ; c.gridy = 0; c.gridwidth = 1 ; c.gridheight = 1 ;
			c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
			String unitStr = "";
			if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==1) {
				unitStr = "(ft)";
			} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==2) {
				unitStr = "(m)";
			}  
			JLabel lblEle = new JLabel("Elevation " + unitStr) ;
			//frame.setBackground(new Color(200, 200, 200)) ;
			popup.add(lblEle,c) ;
			c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
			txtEle.setForeground(Color.BLACK) ;
			popup.add(txtEle,c) ;

			c.gridx = 1 ; c.gridy = 0; c.gridwidth = 1 ;
			JButton btn_ok = new JButton(" OK ") ;
			popup.add(btn_ok, c) ;
			btn_ok.addActionListener(frame_ok_listener) ;
			c.gridx = 1 ; c.gridy = 1;
			JButton btn_cancel = new JButton(" Cancel ") ;
			popup.add(btn_cancel, c) ;
			btn_cancel.addActionListener(frame_cancel_listener) ;

			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new GridLayout(3, 1));
			JCheckBoxGroup checks = new JCheckBoxGroup();
			line=new JCheckBox("Line Segment", sInfo.line_option);
			checks.add(line);
			line.addItemListener(frame_line_listener) ;
			curve=new JCheckBox("Curve Segment", sInfo.curve_option);

			checks.add(curve);
			curve.addItemListener(frame_curve_listener) ;

			tangent=new JCheckBox("Tangent Point", sInfo.tangent_option);
			checks.add(tangent);
			radioButtonPanel.add(line);
			radioButtonPanel.add(curve);
			radioButtonPanel.add(tangent);
			c.gridx = 0 ; c.gridy = 2; c.gridwidth = 1 ; c.gridheight = 3;
			popup.add(radioButtonPanel, c) ;
			c.gridx = 1 ; c.gridy = 2; c.gridwidth = 1 ; c.gridheight = 1;
			String sx=new Float(sInfo.location.getX()).toString();
			String sy=new Float(sInfo.location.getY()).toString();
			mX = new JLabel("X:"+sx) ; 
			popup.add(mX, c) ;
			c.gridx = 1 ; c.gridy = 3; c.gridwidth = 1 ; c.gridheight = 1;
			mY = new JLabel("Y:"+sy);
			popup.add(mY, c) ;
			c.gridx = 1 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1;
			parentID = new JLabel(sInfo.parentId+"") ;
			popup.add(parentID, c) ;

			//ButtonGroup bgroup = new ButtonGroup();
			//bgroup.add(txtRadius);
			//bgroup.add(lblEle);
			//bgroup.add(maybeButton);
	}

	// find nearest circle segment based on mouse click if exists
	public void getCurveMarkLocation(Point ptf ) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist, cosine, diff, min_diff ;
		boolean acceptElevation = false ;
		int min_diff_index = -1 ;
		min_diff = 9999f ;
		for (i=0; i<app.getHorizontalDesign().gethRoadDataCount(); i++) {
			if (app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() > 0) { 
				// curve segment
				dist = VectorUtils.distanceOf(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), ptf);
				diff = Math.abs(dist - app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() * app.getHorizontalDesign().getRoadDesign().getImageScale()) ;
				//System.out.println("i, diff="+i+", "+diff) ;
				if (diff <= app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPenWidth() * Math.sqrt(2)/app.getHorizontalDesign().getDraw_scale()) {
					// click on curve
					acceptElevation = true;
					Vector2D vec2 ;
					double L1, L2 ;
					vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), ptf);
					L1 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() * app.getHorizontalDesign().getRoadDesign().getImageScale();
					L2 = vec2.length();
					app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(GeoUtils.makePoint(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getX() + vec2.getX() * L1 / L2,		app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getY() + vec2.getY() * L1 / L2));
					// found segment
				} else { // 10/17/06 added
					if (diff < min_diff) {
						min_diff = diff ;
						min_diff_index = i ;
					}
				}
			}   // curve
			if (acceptElevation == true) {
				break;
			}
		}
		if (acceptElevation == false ) {
			if (min_diff<=20f && min_diff_index>=0) {   // 10/17/06 added
				// take min dist diff less than 20 pixels if any exists
				sInfo.parentId = min_diff_index ;
				parentID.setText(ConversionUtils.CStr(min_diff_index));               
			} else {
				sInfo.parentId = -1 ;
				parentID.setText("None") ;  // marker does not land on any segment
			}
		} else {
			sInfo.parentId = i ;
			parentID.setText(ConversionUtils.CStr(i));
		}
	}   // getCurveMarkLocation
	

	// find nearest line segment based on mouse click, if any
	public void getLineMarkLocation(Point ptf ) {
		// transform pt from screen pixel to actual unit
		int i ;
		double dist, cosine ;
		boolean acceptElevation = false;
		for (i=0; i<app.getHorizontalDesign().gethRoadDataCount(); i++) {
			if (app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() < 0) { 
				// line segment
				dist = VectorUtils.distanceOf(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), ptf);
				if (dist <= app.getHorizontalDesign().getEndMarkSize() * Math.sqrt(2)/app.getHorizontalDesign().getDraw_scale()) { 
					acceptElevation = true;
					app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint());
				} else {
					dist = VectorUtils.distanceOf(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint(), ptf);
					if (dist <= app.getHorizontalDesign().getEndMarkSize() * Math.sqrt(2)/app.getHorizontalDesign().getDraw_scale()) { 
						acceptElevation = true;
						app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint());
					} else {
						// not selecting end points
						Vector2D vec1, vec2 ;
						double L1, L2 ;
						vec1 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint());
						vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(), ptf);
						L1 = vec1.length();
						L2 = vec2.length();
						cosine = VectorUtils.getCosTheta(vec1, vec2);
						double dist2line = VectorUtils.calcDist2Line(ptf, app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint(),								app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint()) ;
						if (cosine >= 0.99f && L2 <= L1 &&
								(dist2line <= app.getHorizontalDesign().getEndMarkSize() * Math.sqrt(2) /app.getHorizontalDesign().getDraw_scale())) { // // 1 degree close to line segment
							acceptElevation = true;
							app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(GeoUtils.makePoint(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getX() + vec1.getX() * L2 / L1,  app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getY() + vec1.getY() * L2 / L1));
						}   // if cosine
					}   // if distance
				}   // if radius
			}   // line
			if (acceptElevation == true) {
				break;
			}
		}
		if (acceptElevation == false ) {
			sInfo.parentId = -1 ;
			parentID.setText("None") ;  // marker does not land on any segment
		} else {
			sInfo.parentId = i ;
			parentID.setText(ConversionUtils.CStr(i));
		}
	}   // getLineMarkLocation
}
