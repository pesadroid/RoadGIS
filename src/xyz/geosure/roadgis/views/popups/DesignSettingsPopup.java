package xyz.geosure.roadgis.views.popups;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.views.RoadGISPopup;

/** Pop up a window to display road design setting */  
public class DesignSettingsPopup extends PopupWindow{
	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtSpeed;         // design speed
	JTextField txtMaxcut;         // max cut
	JTextField txtMaxfill;         // max fill
	JTextField txtMaxgrade;         // max grade
	JTextField txtMingrade;          // min grade
	JTextField txtReactiontime;         // reaction time
	JTextField txtDecel;         // veh decel
	JTextField txtFricoef;         // friction coefficient
	JTextField txtSFricoef;         // side friction coefficient
	JTextField txtVCurLen;         // max vertical curve length
	JTextField txtHCurRadius;         // max horizontal curve radius
	JTextField txtMaxsuperE;         // max super elevation

	JComboBox listRoadwidth ;
	JTextField txtLanewidth;         // lane width
	JLabel lblRoadColor ;
	JComboBox listRoadColor ;
	JTextField txtShoulderwidth;         // shoulder width
	JTextField txtMarkersize;         // landmark size
	JLabel lblMarkerColor ;
	JComboBox listMarkerColor;           // end mark color
	JComboBox listUnit ;               // my unit

	JLabel lblUnit1, lblUnit2, lblUnit3, lblUnit4;
	JLabel lblUnit5, lblUnit6, lblUnit7, lblUnit8;

	public DesignSettingsPopup(RoadGISApplication app) {
		this.app = app;
	}
	public void build() {
		// open a frame
		popup = new RoadGISPopup("Design Settings") ;
		//frame.setLocation(330,80) ;
		popup.setSize(570,510) ;
		popup.setCenter() ;
		popup.validate() ;
		popup.setVisible(true) ;
		popup.setResizable(false);
		
		// save changes
		ActionListener frame_ok_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				app.getHorizontalDesign().getRoadDesign().setSpeedLimit(new Float(txtSpeed.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setMaxCut(new Float(txtMaxcut.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setMaxFill(new Float(txtMaxfill.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setGradeLimit(new Float(txtMaxgrade.getText()).floatValue()/100f);
				app.getHorizontalDesign().getRoadDesign().setMinGrade(new Float(txtMingrade.getText()).floatValue()/100f);

				app.getHorizontalDesign().getRoadDesign().setReactionTime(new Float(txtReactiontime.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setVehDecel(new Float(txtDecel.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setFrictionCoef(new Float(txtFricoef.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setSideFrictionCoef(new Float(txtSFricoef.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setMinVCurveLen(new Float(txtVCurLen.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setMinHCurveRadius(new Float(txtHCurRadius.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setMaxSuperelevation(new Float(txtMaxsuperE.getText()).floatValue()/100f);

				app.getHorizontalDesign().getRoadDesign().setPreferredRoadLaneSizes(Float.parseFloat((String)listRoadwidth.getSelectedItem()));
				app.getHorizontalDesign().getRoadDesign().setPreferredLaneWidth(new Float(txtLanewidth.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setPreferredShoulderWidth(new Float(txtShoulderwidth.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setPreferredPenColor(lblRoadColor.getBackground());

				app.getHorizontalDesign().getRoadDesign().setElevationMarkerSize(new Float(txtMarkersize.getText()).floatValue());
				app.getHorizontalDesign().getRoadDesign().setElevationMarkerColor(lblMarkerColor.getBackground());
				app.getHorizontalDesign().getRoadDesign().setPreferredUnit(new Integer(listUnit.getSelectedIndex()+1).intValue());
				popup.dispose() ;
			}
		} ;
		ActionListener frame_cancel_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				popup.dispose() ;
			}
		} ;
		/*
        ActionListener frame_roadColor_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {

            }
        } ;
        ActionListener frame_markerColor_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {

            }
        } ;
		 */
		ItemListener frame_roadColor_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				int index = listRoadColor.getSelectedIndex();
				switch (index-1) {
				case 0: // black
					lblRoadColor.setBackground(Color.BLACK);
					break ;
				case 1: // blue
					lblRoadColor.setBackground(Color.BLUE);
					break ;
				case 2: // cyan
					lblRoadColor.setBackground(Color.CYAN);
					break ;
				case 3: // darkgray
					lblRoadColor.setBackground(Color.DARK_GRAY);
					break ;
				case 4: // gray
					lblRoadColor.setBackground(Color.GRAY);
					break ;
				case 5: // green
					lblRoadColor.setBackground(Color.GREEN);
					break ;
				case 6: // light gray
					lblRoadColor.setBackground(Color.LIGHT_GRAY);
					break ;
				case 7: // magenta
					lblRoadColor.setBackground(Color.MAGENTA);
					break ;
				case 8: // orange
					lblRoadColor.setBackground(Color.ORANGE);
					break ;
				case 9: // pink
					lblRoadColor.setBackground(Color.PINK);
					break ;
				case 10: // red
					lblRoadColor.setBackground(Color.RED);
					break ;
				case 11: // white
					lblRoadColor.setBackground(Color.WHITE);
					break ;
				case 12: // yellow
					lblRoadColor.setBackground(Color.YELLOW);
					break ;
				}// switch
			}
		} ;
		ItemListener frame_markerColor_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				int index = listMarkerColor.getSelectedIndex();
				switch (index-1) {
				case 0: // black
					lblMarkerColor.setBackground(Color.BLACK);
					break ;
				case 1: // blue
					lblMarkerColor.setBackground(Color.BLUE);
					break ;
				case 2: // cyan
					lblMarkerColor.setBackground(Color.CYAN);
					break ;
				case 3: // darkgray
					lblMarkerColor.setBackground(Color.DARK_GRAY);
					break ;
				case 4: // gray
					lblMarkerColor.setBackground(Color.GRAY);
					break ;
				case 5: // green
					lblMarkerColor.setBackground(Color.GREEN);
					break ;
				case 6: // light gray
					lblMarkerColor.setBackground(Color.LIGHT_GRAY);
					break ;
				case 7: // magenta
					lblMarkerColor.setBackground(Color.MAGENTA);
					break ;
				case 8: // orange
					lblMarkerColor.setBackground(Color.ORANGE);
					break ;
				case 9: // pink
					lblMarkerColor.setBackground(Color.PINK);
					break ;
				case 10: // red
					lblMarkerColor.setBackground(Color.RED);
					break ;
				case 11: // white
					lblMarkerColor.setBackground(Color.WHITE);
					break ;
				case 12: // yellow
					lblMarkerColor.setBackground(Color.YELLOW);
					break ;
				}// switch
			}
		} ;
		ItemListener frame_unit_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				int i;
				int index = listUnit.getSelectedIndex();
				float oldImageScale, scaleErr ;
				switch (index) {
				case 0: // US unit
					// US unit
					lblUnit1.setText("MPH");    // speed
					lblUnit2.setText("ft");     // max cut
					lblUnit3.setText("ft");     // max fill
					lblUnit4.setText("ft/s/s"); // decel
					lblUnit5.setText("ft");     // min vertical curve len
					lblUnit6.setText("ft");     // min horizontal curve radius
					lblUnit7.setText("ft");     // lane width
					lblUnit8.setText("ft");     // shoulder width

					txtMaxcut.setText( ConversionUtils.CStr(ConversionUtils.CFloat(txtMaxcut.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtMaxfill.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtMaxfill.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtVCurLen.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtVCurLen.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtHCurRadius.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtHCurRadius.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtDecel.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtDecel.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtSpeed.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtSpeed.getText()) / app.getHorizontalDesign().getRoadDesign().MPH2Kmh));
					txtLanewidth.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtLanewidth.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));
					txtShoulderwidth.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtShoulderwidth.getText()) / app.getHorizontalDesign().getRoadDesign().FT2M));

					// 4/30/06 added change from metric to US
					app.getHorizontalDesign().getRoadDesign().setContourImageResolution(ConversionUtils.CInt(app.getHorizontalDesign().getRoadDesign().getContourImageResolution() * 2.54f));
					app.getHorizontalDesign().getRoadDesign().setContourScale(ConversionUtils.CInt(app.getHorizontalDesign().getRoadDesign().getContourScale() / 0.12f));
					oldImageScale = app.getHorizontalDesign().getRoadDesign().getImageScale() ;
					app.getHorizontalDesign().getRoadDesign().setImageScale((float)app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / (float)app.getHorizontalDesign().getRoadDesign().getContourScale());
					scaleErr = oldImageScale * app.getHorizontalDesign().getRoadDesign().FT2M / app.getHorizontalDesign().getRoadDesign().getImageScale() ;
					// update radius
					//int i ;
					if (app.getHorizontalDesign().getHorizontalAlignmentMarkCount() > 0) { 
						double radius ;
						for (i=0; i<app.getHorizontalDesign().getHorizontalAlignmentMarkCount(); i++) {
							radius = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius();
							if (radius > 0) { 
								app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).setRadius(radius / app.getHorizontalDesign().getRoadDesign().FT2M * scaleErr);
							}   //End If
						}   //Next // i
					}   //End If
					// update station elevation data
					if (app.getHorizontalDesign().getRoadDesign().getElevationMarkCount() > 0) {  // Then
						double elevation;
						double distance ;
						for (i=0; i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount(); i++) {
							elevation = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getElevation();
							distance = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getDistance();
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setElevation(elevation / app.getHorizontalDesign().getRoadDesign().FT2M);
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setDistance(distance / app.getHorizontalDesign().getRoadDesign().FT2M * scaleErr);
						}   //Next    ' i
					}   //End If

					break;
				case 1: // metric
					// metric unit
					lblUnit1.setText("Km/h");    // speed
					lblUnit2.setText("m");     // max cut
					lblUnit3.setText("m");     // max fill
					lblUnit4.setText("m/s/s"); // decel
					lblUnit5.setText("m");     // min vertical curve len
					lblUnit6.setText("m");     // min horizontal curve radius
					lblUnit7.setText("m");     // lane width
					lblUnit8.setText("m");     // shoulder width

					txtMaxcut.setText( ConversionUtils.CStr(ConversionUtils.CFloat(txtMaxcut.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtMaxfill.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtMaxfill.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtVCurLen.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtVCurLen.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtHCurRadius.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtHCurRadius.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtDecel.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtDecel.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtSpeed.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtSpeed.getText()) * app.getHorizontalDesign().getRoadDesign().MPH2Kmh));
					txtLanewidth.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtLanewidth.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));
					txtShoulderwidth.setText(ConversionUtils.CStr(ConversionUtils.CFloat(txtShoulderwidth.getText()) * app.getHorizontalDesign().getRoadDesign().FT2M));

					// 4/30/06 added change from US to metric
					app.getHorizontalDesign().getRoadDesign().setContourImageResolution(ConversionUtils.CInt(app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / 2.54f));
					app.getHorizontalDesign().getRoadDesign().setContourScale(ConversionUtils.CInt(app.getHorizontalDesign().getRoadDesign().getContourScale() * 0.12f));
					oldImageScale = app.getHorizontalDesign().getRoadDesign().getImageScale() ;
					app.getHorizontalDesign().getRoadDesign().setImageScale((float)app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / (float)app.getHorizontalDesign().getRoadDesign().getContourScale());
					scaleErr = oldImageScale / app.getHorizontalDesign().getRoadDesign().FT2M / app.getHorizontalDesign().getRoadDesign().getImageScale() ;
					// update radius
					//int i ;
					if (app.getHorizontalDesign().getHorizontalAlignmentMarkCount() > 0) { 
						double radius ;
						for (i=0; i<app.getHorizontalDesign().getHorizontalAlignmentMarkCount(); i++) {
							radius = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius();
							if (radius > 0) { 
								app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).setRadius(radius * app.getHorizontalDesign().getRoadDesign().FT2M * scaleErr);
							}   //End If
						}   //Next // i
					}   //End If
					// update station elevation data
					if (app.getHorizontalDesign().getRoadDesign().getElevationMarkCount() > 0) {  // Then
						double elevation;
						double distance ;
						for (i=0; i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount(); i++) {
							elevation = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getElevation();
							distance = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getDistance();
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setElevation(elevation * app.getHorizontalDesign().getRoadDesign().FT2M);
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setDistance(distance * app.getHorizontalDesign().getRoadDesign().FT2M * scaleErr);
						}   //Next    ' i
					}   //End If

					break;
				}   // switch
			}   // itemStateChanged
		} ; // frame_unit_listener
		
		String unitStr="", unitStr1="";
		if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 1) {
			unitStr = "MPH" ;
			unitStr1 = "ft" ;
		} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 2) { 
			unitStr = "Km/h" ;
			unitStr1 = "m" ;
		}

		popup.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;
		// row 0 
		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 1 ; c.gridheight = 1 ;
		c.insets = new Insets(0,5,0,5) ; // 5-pixel margins on all sides
		JLabel lblgen = new JLabel("General");
		lblgen.setForeground(Color.BLUE);
		Font myFont = new Font("Times New Roman", Font.BOLD, 14);
		lblgen.setFont(myFont);
		popup.add(lblgen,c);
		// row 1 Left ===================
		c.gridx = 0 ; c.gridy = 1;  c.insets = new Insets(0,5,5,5) ;
		popup.add( new JLabel("Speed Limit"),c);
		c.gridx = 1 ; c.insets = new Insets(0,0,5,5) ;
		txtSpeed= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getSpeedLimit()).toString()) ;
		popup.add(txtSpeed,c) ;
		c.gridx = 2 ; 
		lblUnit1 = new JLabel(unitStr);
		popup.add(lblUnit1,c);
		// row 1 Rightapp.getHorizontalDesign().getRoadDesign()
		c.gridx = 3 ;
		popup.add( new JLabel("Reaction Time"),c);
		c.gridx = 4 ; 
		txtReactiontime= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getReactionTime()).toString()) ;
		popup.add(txtReactiontime,c) ;
		c.gridx = 5 ; 
		popup.add(new JLabel("sec"),c);

		// row 2 Left ===================
		c.gridx = 0 ; c.gridy = 2; 
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Max Cut"),c);
		c.gridx = 1 ; c.insets = new Insets(0,0,5,5) ;
		txtMaxcut= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMaxCut()).toString()) ;
		popup.add(txtMaxcut,c) ;
		c.gridx = 2 ; 
		lblUnit2 = new JLabel(unitStr1);
		popup.add(lblUnit2,c);
		// row 2 Right
		c.gridx = 3 ;
		popup.add( new JLabel("Deceleration"),c);
		c.gridx = 4 ; 
		txtDecel= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getVehDecel()).toString()) ;
		popup.add(txtDecel,c) ;
		c.gridx = 5 ; 
		lblUnit4 = new JLabel(unitStr1+"/s/s");
		popup.add(lblUnit4,c);

		// row 3 Left ===================
		c.gridx = 0 ; c.gridy = 3; 
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Max Fill"),c);
		c.gridx = 1 ; c.insets = new Insets(0,0,5,5) ;
		txtMaxfill= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMaxFill()).toString()) ;
		popup.add(txtMaxfill,c) ;
		c.gridx = 2 ; 
		lblUnit3 = new JLabel(unitStr1);
		popup.add(lblUnit3,c);
		// row 3 Right
		c.gridx = 3 ;
		popup.add( new JLabel("Friction Coef."),c);
		c.gridx = 4 ; 
		txtFricoef= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getFrictionCoef()).toString()) ;
		popup.add(txtFricoef,c) ;
		c.gridx = 5 ; 
		popup.add(new JLabel(" "),c);

		// row 4 Left ===================
		c.gridx = 0 ; c.gridy = 4; 
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Max Grade (%)"),c);
		c.gridx = 1 ;  c.insets = new Insets(0,0,5,5) ;
		txtMaxgrade= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getGradeLimit()*100f).toString()) ;
		popup.add(txtMaxgrade,c) ;
		//c.gridx = 2 ; 
		//frame.add(new JLabel("%"),c);
		// row 4 Right
		c.gridx = 3 ;
		popup.add( new JLabel("Side Friction Coef."),c);
		c.gridx = 4 ; 
		txtSFricoef= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getSideFrictionCoef()).toString()) ;
		popup.add(txtSFricoef,c) ;
		c.gridx = 5 ; 
		popup.add(new JLabel(" "),c);

		// row 5  ===================================
		c.gridx = 0 ; c.gridy = 5; 
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Min Grade (%)"),c);
		c.gridx = 1 ;  c.insets = new Insets(0,0,5,5) ;
		txtMingrade= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMinGrade()*100f).toString()) ;
		popup.add(txtMingrade,c) ;

		c.gridx = 2 ; c.gridy = 5; c.gridwidth=2;
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Minimum Vertical Curve Length"),c);
		c.insets = new Insets(0,0,5,5) ;
		c.gridx = 4 ; c.gridwidth=1;
		txtVCurLen= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMinVCurveLen()).toString()) ;
		popup.add(txtVCurLen,c) ;
		c.gridx = 5 ; 
		lblUnit5 = new JLabel(unitStr1);
		popup.add(lblUnit5,c);

		// row 6  ================================
		c.gridx = 2 ; c.gridy = 6; c.gridwidth=2;
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Minimum Horizontal Curve Radius"),c);
		c.insets = new Insets(0,0,5,5) ;
		c.gridx = 4 ; c.gridwidth=1;
		txtHCurRadius= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMinHCurveRadius()).toString()) ;
		popup.add(txtHCurRadius,c) ;
		c.gridx = 5 ; 
		lblUnit6 = new JLabel(unitStr1);
		popup.add(lblUnit6,c);

		// row 7  ===================
		c.gridx = 2 ; c.gridy = 7; c.gridwidth=2;
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		popup.add( new JLabel("Maximum Superelevation"),c);
		c.insets = new Insets(0,0,5,5) ;
		c.gridx = 4 ; c.gridwidth=1;
		txtMaxsuperE= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMaxSuperelevation()*100).toString()) ;
		popup.add(txtMaxsuperE,c) ;
		c.gridx = 5 ; 
		popup.add(new JLabel("%"),c);

		// row 8 
		c.gridx = 0 ; c.gridy = 8; c.gridwidth = 1 ; c.gridheight = 1 ;
		c.insets = new Insets(0,5,5,5) ; // 5-pixel margins on all sides
		JLabel lblroad = new JLabel("Road Design");
		lblroad.setForeground(Color.BLUE);
		lblroad.setFont(myFont);
		popup.add(lblroad,c);
		// row 9 Left ===================
		c.gridx = 0 ; c.gridy = 9;
		c.insets = new Insets(0,5,5,5) ;
		popup.add( new JLabel("Road Width"),c);
		c.gridx = 1 ; c.insets = new Insets(0,0,5,5) ;
		String[] roadwidthStrings = { "2", "4", "6"};
		listRoadwidth= new JComboBox(roadwidthStrings);

		popup.add(listRoadwidth,c) ;
		listRoadwidth.setSelectedIndex(ConversionUtils.CInt(app.getHorizontalDesign().getRoadDesign().getPreferredRoadLaneSizes()/2)-1) ;
		c.gridx = 2 ; 
		popup.add(new JLabel("Lanes"),c);
		// row 9 Right
		c.gridx = 3 ;
		popup.add( new JLabel("Lane Width"),c);
		c.gridx = 4 ; 
		txtLanewidth= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getPreferredLaneWidth()).toString()) ;
		popup.add(txtLanewidth,c) ;
		c.gridx = 5 ; 
		lblUnit7 = new JLabel(unitStr1);
		popup.add(lblUnit7,c);

		// row 10 Left ===================
		c.gridx = 0 ; c.gridy = 10; 
		c.insets = new Insets(0,5,5,5) ;
		popup.add( new JLabel("Road Color"),c);
		c.gridx = 1 ;c.insets = new Insets(0,0,5,5) ;
		lblRoadColor=new JLabel();
		lblRoadColor.setBackground(app.getHorizontalDesign().getRoadDesign().getPreferredPenColor());
		popup.add(lblRoadColor,c) ;

		c.gridx = 2 ; c.gridwidth = 1;

		String[] roadColorStrings = { "Edit", "Black", "Blue","Cyan", "Dark Gray", "Gray","Green", "Magenta", "Orange", "Pink","Red", "White","Yellow", "Light Gray"};
		listRoadColor = new JComboBox(roadColorStrings);

		popup.add(listRoadColor,c) ;
		//  c.gridx = 2 ; 
		//  Button btnRoadColorEdit = new Button("Edit");
		//  frame.add(btnRoadColorEdit,c);
		// row 10 Right
		c.gridx = 3 ; c.gridwidth = 1;
		popup.add( new JLabel("Shoulder Width"),c);
		c.gridx = 4 ; 
		txtShoulderwidth= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getPreferredShoulderWidth()).toString()) ;
		popup.add(txtShoulderwidth,c) ;
		c.gridx = 5 ; 
		lblUnit8 = new JLabel(unitStr1);
		popup.add(lblUnit8,c);

		// row 11
		c.gridx = 0 ; c.gridy = 11; c.gridwidth = 2 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
		JLabel lblmarker = new JLabel("Landmark / Station");
		lblmarker.setForeground(Color.BLUE);
		lblmarker.setFont(myFont);
		popup.add(lblmarker,c);
		c.gridx = 3 ; c.gridwidth = 1 ; c.insets = new Insets(0,0,5,5) ;
		JLabel lblunit = new JLabel("Unit");
		lblunit.setForeground(Color.BLUE);
		lblunit.setFont(myFont);
		popup.add(lblunit,c);
		// row 12 Left ===================
		c.gridx = 0 ; c.gridy = 12;
		c.insets = new Insets(0,5,5,5) ;
		popup.add( new JLabel("Marker Size"),c);
		c.gridx = 1 ; c.insets = new Insets(0,0,5,5) ;
		txtMarkersize = new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getElevationMarkerSize()).toString());
		popup.add(txtMarkersize,c) ;
		c.gridx = 2 ; 
		popup.add(new JLabel("Pixels"),c);
		c.gridx = 3 ; 
		String[] units = {"US Customary","Metric"};
		listUnit= new JComboBox(units);
		listUnit.setSelectedIndex(app.getHorizontalDesign().getRoadDesign().getPreferredUnit()-1) ;
		popup.add(listUnit,c);
		// row 13 Left ===================
		c.gridx = 0 ; c.gridy = 13;
		c.insets = new Insets(0,5,10,5) ;
		popup.add( new JLabel("Marker Color"),c);
		c.gridx=1 ;c.insets = new Insets(0,0,10,5) ;
		lblMarkerColor = new JLabel();
		lblMarkerColor.setBackground(app.getHorizontalDesign().getRoadDesign().getElevationMarkerColor());
		popup.add(lblMarkerColor,c) ;

		c.gridx = 2 ; c.gridwidth = 1;

		listMarkerColor = new JComboBox(roadColorStrings);

		popup.add(listMarkerColor,c) ;
		// c.gridx = 2 ; 
		// Button btnMarkerColorEdit = new Button("Edit");
		// frame.add(btnMarkerColorEdit,c);

		// ======================================================

		c.gridx = 4 ; c.gridy = 12; c.gridwidth = 2 ;
		c.insets = new Insets(0,5,5,5) ;
		JButton btn_ok = new JButton(" OK ") ;
		popup.add(btn_ok,c) ;
		c.insets = new Insets(0,0,5,5) ;
		c.gridx = 4 ; c.gridy = 13; c.gridwidth = 2 ;c.insets = new Insets(0,5,10,5) ;
		JButton btn_cancel = new JButton(" Cancel ") ;
		popup.add(btn_cancel,c) ;

		btn_ok.addActionListener(frame_ok_listener) ;
		btn_cancel.addActionListener(frame_cancel_listener) ;
		//  btnRoadColorEdit.addActionListener(frame_roadColor_listener) ;
		//  btnMarkerColorEdit.addActionListener(frame_markerColor_listener) ;
		listUnit.addItemListener(frame_unit_listener) ;
		listRoadColor.addItemListener(frame_roadColor_listener) ;
		listMarkerColor.addItemListener(frame_markerColor_listener) ;
	}
		
}
