package xyz.geosure.roadgis.views.popups;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.model.StationInfo;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.views.JCheckBoxGroup;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class ElevationMarkerEditPopup extends PopupWindow{

	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtEle = new JTextField("0");
	JCheckBox line;          // station,landmark option
	JCheckBox curve;         // station,landmark option
	JCheckBox tangent;       // station,landmark option

	StationInfo sInfo ;         // landmark station info
	

	public ElevationMarkerEditPopup(RoadGISApplication app, StationInfo station) {
		this.app = app;
		this.sInfo = station;
	}
	
	public void build() {
		popup = new RoadGISPopup(sInfo.title) ;
		popup.setLocation(350,40) ;
		popup.setSize(300,200) ;
		popup.validate() ;
		popup.setVisible(true) ;
		popup.setResizable(false) ;

		ActionListener frame_ok_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				if (txtEle.getText().length()>0) {
					sInfo.elevation = new Float(txtEle.getText()).floatValue(); 
					MarkerType segment_type = MarkerType.NONE;
					if ( line.isSelected() == true) {
						segment_type = MarkerType.LINE ;
					} else if (curve.isSelected() == true ) {
						segment_type = MarkerType.CURVE;
					} else {
						segment_type = MarkerType.TANGENT;
					}
					if (sInfo.CheckBox_edit == true ) {
						// edit existing landmark
						int index ;
						index = sInfo.dataIndex ;
						
						app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(index).setLocation(app.getHorizontalDesign().getRoadDesign().getCurrentElevationMarker())
																								.setElevation(sInfo.elevation)
																								.setParentIndex(sInfo.parentId)
																								.setMarkerType(segment_type);
					} 
					/*else {
                            myDBgetElevationMarks().get(myDB.getElevationMarkCount()] = new MarkerDB();
                            // create new landmark
                            myDBgetElevationMarks().get(myDB.getElevationMarkCount()].setMarker(myDB.getCurrentElevationMarker(), sInfo.elevation, new Integer(sInfo.parentId).byteValue(), segment_type);
                            myDB.getElevationMarkCount() += 1;
                            // save # of data in log buffer
                            push2MarkLogBuffer(myDB.getElevationMarkCount());
                        }
					 */
					popup.dispose() ;
					//repaint();
				} else {
					// txtEle elevation field is empty
					app.getUIActionsHandler().popMessageBox("Elevation Data", "Please specify station elevation!");
				}
			}   // action performed
		} ;
		ActionListener frame_delete_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				// delete selected landmark
				// shift landmarkd by 1 starting from sInfo.insert index
				int index = sInfo.dataIndex ;
				double x, y ;
				Point ptf ;
				for (int i=index; i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount()-1; i++) {
					ptf = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getLocation() ;
					x = ptf.getX() ;
					y = ptf.getY() ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setLocation(new GeometryFactory().createPoint(new Coordinate(x, y))) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setElevation(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getElevation()) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setMarkerType(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getMarkerType()) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setDistance(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getDistance()) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setParentIndex(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getParentIndex()) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setGrade(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).getGrade()) ;
					app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setPVTnCurvatureOverlap(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i+1).isPointOfVerticalTangentAndCurvatureOverlapping()) ;
				}
				//app.getHorizontalDesign().getRoadDesign().setElevationMarkCount(app.getHorizontalDesign().getRoadDesign().getElevationMarkCount() - 1);
				// save # of data in log buffer
				//push2MarkLogBuffer(myDB.getElevationMarkCount());

				popup.dispose() ;
				//repaint();
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
		txtEle.setForeground(Color.RED) ;
		txtEle.setText(ConversionUtils.CStr(sInfo.elevation));
		popup.add(txtEle,c) ;

		c.gridx = 1 ; c.gridy = 0; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" OK ") ;
		popup.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_ok_listener) ;
		c.gridx = 1 ; c.gridy = 1;
		JButton btn_delete = new JButton(" Delete ") ;
		popup.add(btn_delete, c) ;
		btn_delete.addActionListener(frame_delete_listener) ;

		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new GridLayout(3, 1));
		JCheckBoxGroup checks = new JCheckBoxGroup();
		line=new JCheckBox("Line Segment", sInfo.line_option);
		checks.add(line);
		curve=new JCheckBox("Curve Segment", sInfo.curve_option);
		checks.add(curve);
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
		popup.add(new JLabel("X:"+sx), c) ;
		c.gridx = 1 ; c.gridy = 3; c.gridwidth = 1 ; c.gridheight = 1;
		popup.add(new JLabel("Y:"+sy), c) ;
		c.gridx = 1 ; c.gridy = 4; c.gridwidth = 1 ; c.gridheight = 1;
		popup.add(new JLabel(sInfo.parentId+""), c) ;

		//ButtonGroup bgroup = new ButtonGroup();
		//bgroup.add(txtRadius);
		//bgroup.add(lblEle);
		//bgroup.add(maybeButton);
	}
}
