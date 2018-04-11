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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.model.StationInfo;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.VectorUtils;
import xyz.geosure.roadgis.views.JCheckBoxGroup;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class ElevationMarkerInsertPopup extends PopupWindow{

	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtEle = new JTextField("0");
	JCheckBox line;          // station,landmark option
	JCheckBox curve;         // station,landmark option
	JCheckBox tangent;       // station,landmark option

	StationInfo sInfo ;         // landmark station info
	private JLabel mX, mY, parentID ;    // popElevationMarkForm
	

	public ElevationMarkerInsertPopup(RoadGISApplication app, StationInfo station) {
		this.app = app;
		this.sInfo = station;
	}
	
	public void build() {
		popup = new RoadGISPopup(sInfo.title) ;
		popup.setLocation(150,40) ;
		popup.setSize(300,200) ;
		popup.validate() ;
		popup.setVisible(true) ;
		popup.setResizable(false) ;

		ActionListener frame_ok_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				//System.out.println("txtEle.text=" + txtEle.getText()) ;
				if (txtEle.getText().length()>0) {  // 11/9/06 added
					sInfo.elevation = new Float(txtEle.getText()).floatValue(); 
					MarkerType segment_type ;
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
							vec1 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1(), app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint2());
							vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1(), app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
							L1 = vec1.length();
							L2 = vec2.length();
							app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(new GeometryFactory().createPoint(new Coordinate(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1().getX() + vec1.getX() * L2 / L1,	app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1().getY() + vec1.getY() * L2 / L1)));
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
							vec2 = VectorUtils.vector(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1(), app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
							L1 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() * app.getHorizontalDesign().getRoadDesign().getImageScale();
							L2 = vec2.length();
							app.getHorizontalDesign().getRoadDesign().setCurrentElevationMarker(new GeometryFactory().createPoint(new Coordinate((app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1().getX() + vec2.getX() * L1 / L2),	(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPoint1().getY() + vec2.getY() * L1 / L2))));
						}   // if (sInfo.initial_state
					} else {
						// tangent point
						segment_type = MarkerType.TANGENT;
						sInfo.line_option=false ;
						sInfo.curve_option=false ;
						sInfo.tangent_option=true ;
					}

					AlignmentMarker marker = new AlignmentMarker();
					app.getHorizontalDesign().getRoadDesign().addElevationMark(marker);

					// shift landmarkd by 1 starting from sInfo.insert index
					double x, y ;
					Point ptf ;
					for (int i=app.getHorizontalDesign().getRoadDesign().getElevationMarkCount(); i>sInfo.insert; i--) {
						ptf = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getLocation() ;
						x = ptf.getX() ;
						y = ptf.getY() ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setLocation(new GeometryFactory().createPoint(new Coordinate(x, y) ));
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setElevation(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getElevation()) ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setSegmentType(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getSegmentType()) ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setDistance(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getDistance()) ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setParentIndex(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getParentIndex()) ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setGrade(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).getGrade()) ;
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setPVTnCurvatureOverlap(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i-1).isPointOfVerticalTangentAndCurvatureOverlapping()) ;
					}
					// insert new landmark
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(sInfo.insert).setLocation(app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker());
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(sInfo.insert).setElevation(sInfo.elevation);
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(sInfo.insert).setParentIndex(sInfo.parentId);
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(sInfo.insert).setSegmentType(segment_type);
					
					// save # of data in log buffer, no undo option
					//push2MarkLogBuffer(myDB.getElevationMarkCount());

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
				float x1, y1 ;
				String str="";
				str = mX.getText() ;
				x1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
				str = mY.getText() ;
				y1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
				new ElevationMarkerPopup(app,sInfo).getLineMarkLocation(new GeometryFactory().createPoint(new Coordinate(x1, y1)));
			}
		} ;
		ItemListener frame_curve_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				float x1, y1 ;
				String str="";
				str = mX.getText() ;
				x1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
				str = mY.getText() ;
				y1 = ConversionUtils.CFloat(str.substring(str.indexOf(":")+1)) ;
				new ElevationMarkerPopup(app,sInfo).getCurveMarkLocation(new GeometryFactory().createPoint(new Coordinate(x1, y1)));
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
		//txtEle.setForeground(Color.BLACK) ;
		txtEle.setForeground(Color.green) ;
		popup.add(txtEle,c) ;

		c.gridx = 1 ; c.gridy = 0; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Insert ") ;
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

}
