package xyz.geosure.roadgis.views.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.controllers.VerticalDesignController;
import xyz.geosure.roadgis.utils.print.PrintUtilities;
import xyz.geosure.roadgis.views.border;
import xyz.geosure.roadgis.views.myFrame;
import xyz.geosure.roadgis.views.toolbars.VerticalDesignToolbar;
import xyz.geosure.roadgis.views.toolbars.VerticalStatusbar;

public class VerticalAlignmentFrame extends FrameWindow{
	private RoadGISApplication app = null;


	// Java GUI
	// design dettings
	JTextField txtEle = new JTextField("0");
	JCheckBox line;          // station,landmark option
	JCheckBox curve;         // station,landmark option
	JCheckBox tangent;       // station,landmark option


	PrintUtilities vd_pu ;

	public VerticalAlignmentFrame(RoadGISApplication app) {
		this.app = app;
	}

	public void build() {
		frame = new myFrame("Vertical Design") ;//"Simulation Statistics") ;
		frame.setSize(1024, 728) ;
		//frame.setLocation(150,20) ;
		//frame.setCenter() ;
		frame.validate() ;
		frame.setVisible(true) ;
		frame.setResizable(false);


		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_open = new JMenuItem("Load Vertical Curve") ;
		JMenuItem file_save = new JMenuItem("Save Vertical Curve") ;
		JMenuItem separator = new JMenuItem("-") ;
		//     JMenuItem file_savereport = new JMenuItem("Save Report") ;
		//     JMenuItem file_pagesetup = new JMenuItem("Page Setup") ;
		JMenuItem file_print = new JMenuItem("Print") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		// file menu items
		menu_file.add(file_open) ;   // add menu items
		menu_file.add(file_save) ;   // add menu items
		menu_file.addSeparator() ;
		//     menu_file.add(file_savereport) ;   // add menu items
		//     menu_file.addSeparator() ;
		//     menu_file.add(file_pagesetup) ;   // add menu items
		menu_file.add(file_print) ;   // add menu items
		menu_file.addSeparator() ;
		menu_file.add(file_close) ;   // add menu items

		// edit menu
		JMenu menu_edit = new JMenu("Edit") ;
		JMenuItem edit_undo = new JMenuItem("Undo") ;
		JMenuItem edit_redo = new JMenuItem("Redo") ;
		JMenuItem edit_cleardesign = new JMenuItem("Clear Design") ;
		JMenuItem edit_clearcurve = new JMenuItem("Clear Curves") ;
		menu_edit.add(edit_undo) ;
		menu_edit.add(edit_redo) ;
		menu_edit.addSeparator();
		menu_edit.add(edit_cleardesign) ;
		menu_edit.add(edit_clearcurve) ;

		// view menu
		JMenu menu_view = new JMenu("View") ;
		JMenuItem view_elevation = new JMenuItem("Elevation Profile") ;
		JMenuItem view_fillcut = new JMenuItem("Fill-Cut Profile") ;
		JMenuItem view_massdiagram = new JMenuItem("Mass Diagram") ;
		JMenuItem view_stations = new JMenuItem("Station Data") ;
		JMenuItem view_report = new JMenuItem("Design Report") ;
		JMenuItem view_animation = new JMenuItem("3D Animation") ;

		menu_view.add(view_elevation) ;
		menu_view.add(view_fillcut) ;
		menu_view.add(view_massdiagram) ;
		menu_view.addSeparator();
		menu_view.add(view_stations) ;
		menu_view.add(view_report) ;
		menu_view.add(view_animation) ;

		// tool menu
		JMenu menu_tool = new JMenu("Tool") ;
		JMenuItem tool_gradeON = new JMenuItem("Grade Construction ON") ;
		JMenuItem tool_gradeOFF = new JMenuItem("Grade Construction OFF") ;
		JMenuItem tool_vAlign = new JMenuItem("Generate Vertical Curves") ;
		JMenuItem tool_curveEdit = new JMenuItem("Edit Curve Length") ;
		menu_tool.add(tool_gradeON) ;
		menu_tool.add(tool_gradeOFF) ;
		menu_tool.addSeparator() ;
		menu_tool.add(tool_vAlign) ;
		menu_tool.add(tool_curveEdit) ; // 10/9/06 edit vertical curve length

		// help menu
		JMenu menu_help = new JMenu("Help") ;
		JMenuItem help_manual = new JMenuItem("User's Manual PDF") ;
		JMenuItem help_cortona = new JMenuItem("Cortona VRML Client") ;
		JMenuItem help_about = new JMenuItem("About ROAD") ;
		//JMenuItem help_aboutJavaHelp = new JMenuItem("About JavaHelp") ;
		JMenuItem help_web_contents = new JMenuItem("Web Contents") ; 
		//JMenuItem help_javahelp = new JMenuItem("User's Guide") ;

		menu_help.add(help_web_contents) ;
		//menu_help.add(help_javahelp) ;

		menu_help.add(help_manual) ;
		menu_help.add(separator) ;
		menu_help.add(help_about) ;
		//menu_help.add(help_aboutJavaHelp) ;
		menu_help.add(separator) ;
		menu_help.add(help_cortona) ;

		// ===========================================
		menu_bar.add(menu_file) ;     // add menu
		menu_bar.add(menu_edit) ;     // add menu
		menu_bar.add(menu_view) ;     // add menu
		menu_bar.add(menu_tool) ;     // add menu
		menu_bar.add(menu_help) ;     // add menu
		frame.setJMenuBar(menu_bar) ;

		VerticalDesignToolbar tbv = new VerticalDesignToolbar();
		VerticalStatusbar sbv = new VerticalStatusbar() ;
		JPanel cm = new JPanel();
		JPanel ccv = new JPanel();
		frame.setLayout(new BorderLayout(0,0));

		//Scrollbar ss = new Scrollbar(Scrollbar.HORIZONTAL);
		ccv.setLayout(new BorderLayout(0,0));

		app.setVerticalDesign( new VerticalDesignController(app, tbv, sbv)); 
		app.getVerticalDesign().setRoadDesign(app.getHorizontalDesign().getRoadDesign()) ;
		app.getVerticalDesign().hRoadDataCount = app.getHorizontalDesign().getHorizontalAlignmentMarkCount(); 
		app.getVerticalDesign().init(); // initialization

		ccv.add("Center",app.getVerticalDesign()); 

		cm.setBackground(Color.black);
		cm.setLayout(new BorderLayout(1,1));
		cm.add("North",tbv);
		cm.add("Center",ccv);
		cm.add("South",sbv);

		frame.add("West", new border(2, Color.black));
		frame.add("East", new border(2, Color.black));
		frame.add("North", new border(2, Color.black));
		frame.add("South", new border(2, Color.black));
		frame.add("Center",cm);
		frame.invalidate() ;
		//frame.show() ;
		/*
             help_javahelp.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aev) {
                        try
                        {
                            new HelpDoc() ;
                        }
                        catch (Exception e){
                                //do nothing
                            popMessageBox("Help - User's Guide", "Error:"+e.toString()) ;
                        } // try
                    } // actionPerformed
                } // ActionListener
            ) ; // help_javahelp
		 */
		help_web_contents.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						try
						{
							//AppletContext a = myApplet.getAppletContext();
							//URL u = new URL(SHARED.CONTENTS_PATH);  
							//a.showDocument(u,"_blank");
							//_blank to open page in new window		
						}
						catch (Exception e){
							//do nothing
							app.getUIActionsHandler().popMessageBox("Help - Web Content", "Error:"+e.toString()) ;
						} // try
					} // actionPerformed
				} // ActionListener
				) ; // help_web_contents

		help_manual.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						try
						{
							//AppletContext a = myApplet.getAppletContext(); 
							//URL u = new URL(myDB.MANUAL_PATH); 
							//a.showDocument(u,"_blank");
							//_blank to open page in new window		
						}
						catch (Exception e){
							//do nothing
							app.getUIActionsHandler().setStatusBarText(1, "Error: Manual file "+e.toString()) ;
						}   // try
					}   // actionPerformed
				}   // ActionListener
				) ; // help_manual

		help_cortona.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) { 
						try
						{
							//AppletContext a = myApplet.getAppletContext(); 
							//URL u = new URL("http://www.parallelgraphics.com/developer/products/cortona/help/"); 
							//a.showDocument(u,"_blank");
							//_blank to open page in new window		
						}
						catch (Exception e){
							//do nothing
							app.getUIActionsHandler().setStatusBarText(1, "Error: Cortona help file "+e.toString()) ;
						}   // try
					}   // actionPerformed
				}   // ActionListener
				) ; // help_cortona

		help_about.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getUIActionsHandler().popAbout();
					}    // actionPerformed
				}  // ActionListener
				) ; // help_about
		/*
            help_aboutJavaHelp.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aev) {
                        popMessageBox("JavaHelp", 
                        "Note: You need to have JavaHelp package\n" +  
                        "(jh.jar, jhall.jar, jhbasic.jar, jsearch.jar)\n" +
                        "installed in your ..\\Java\\jre\\lib\\ext\\ directory\n" + 
                        "in order to view the Users' Guide. JavaHelp is\n" + 
                        "available at http://java.sun.com/products/javahelp/.") ;

                    } // actionPerformed
                } // ActionListener
            ) ; // help_aboutJavaHelp
		 */
		file_open.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().file_open(); 
						app.getVerticalDesign().setStatusBarText(0, "Load vertical curve") ;
					}   // actionPerformed
				}   // ActionListener
				) ;    // file open
		file_save.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().file_save(); 
						app.getVerticalDesign().setStatusBarText(0, "Save vertical curve") ;
					}   // actionPerformed
				}   // ActionListener
				) ;    // file save
		/*
             file_savereport.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent aev) {
                            app.getVerticalDesign().file_saveReport();  
                            app.getVerticalDesign().setStatusBarText(0, "Save Report") ;
                        } // actionPerformed
                    } // ActionListener
             ) ; // file save report
		 **/
		/*             file_pagesetup.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent aev) {

                            vd_pu = new PrintUtilities(app.getVerticalDesign()) ;
                            vd_pu.printPageSetup();
                        } // actionPerformed
                    } // ActionListener
             ) ; // file Print Page Setup
		 */
		file_print.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//hDesign.print();
						//PrintUtilities.printComponent(app.getVerticalDesign());
						//PrintUtilities pu = new PrintUtilities(app.getVerticalDesign()) ;
						vd_pu = new PrintUtilities(app.getVerticalDesign()) ;
						vd_pu.print();
					} // actionPerformed
				} // ActionListener
				) ; // file Print 
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {

						app.getHorizontalDesign().popSaveVDesignB4Close();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		edit_undo.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().edit_undo();
					} // actionPerformed
				} // ActionListener
				) ; // edit_undo
		edit_redo.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().edit_redo();
					} // actionPerformed
				} // ActionListener
				) ; // edit_redo
		edit_cleardesign.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popClearAllDesign("Edit - Clear Design","Are you sure to clear vertical curve design?");
					} // actionPerformed
				} // ActionListener
				) ; // edit_clearLandmarks
		edit_clearcurve.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popClearVCurves("Edit - Clear Curves","Are you sure to clear vertical curves?");
					} // actionPerformed
				} // ActionListener
				) ; // edit_clearCurves

		view_elevation.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().viewElevation();
						app.getVerticalDesign().sb.setStatusBarText(0, "View elevation profile") ; //Status:
					} // actionPerformed
				} // ActionListener
				) ; // view elevation profile
		view_fillcut.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().viewFillCut();
						app.getVerticalDesign().sb.setStatusBarText(0, "View fill-cut profile") ; //Status:
					} // actionPerformed
				} // ActionListener
				) ; // view fill cut profile
		view_massdiagram.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().view_MassDiagram(); 
						app.getVerticalDesign().sb.setStatusBarText(0, "View mass diagram") ; //Status:
					} // actionPerformed
				} // ActionListener
				) ; // view elevation profile
		view_stations.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popStationData();
						app.getVerticalDesign().sb.setStatusBarText(0, "View station data") ; //Status:
					} // actionPerformed
				} // ActionListener
				) ; // view stations
		view_report.addActionListener( 
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popReport();
						app.getVerticalDesign().sb.setStatusBarText(0, "Generate report") ; //Status: 

					} // actionPerformed
				} // ActionListener
				) ; // view report
		view_animation.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popAnimation3D();
						app.getVerticalDesign().sb.setStatusBarText(0, "3D animation") ; //Status:
					} // actionPerformed
				} // ActionListener
				) ; // view 3D animation

		tool_gradeON.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().tool_gradeON();
					} // actionPerformed
				} // ActionListener
				) ; // tool grade ON
		tool_gradeOFF.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().tool_gradeOFF();
					} // actionPerformed
				} // ActionListener
				) ; // tool Grade OFF
		tool_vAlign.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().vertAlign();
					} // actionPerformed
				} // ActionListener
				) ; // tool vertical cuve alignment
		tool_curveEdit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getVerticalDesign().popVertCurveLenEdit();
					} // actionPerformed
				} // ActionListener
				) ; // tool vertical cuve length modification

		//=============================
	}

}
