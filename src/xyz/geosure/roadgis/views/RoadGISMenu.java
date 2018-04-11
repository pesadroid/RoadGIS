package xyz.geosure.roadgis.views;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.actions.AddLayerAction;
import xyz.geosure.roadgis.actions.HorizontalCurveCreateAction;
import xyz.geosure.roadgis.actions.HorizontalLineCreateAction;
import xyz.geosure.roadgis.actions.EditDeleteAction;
import xyz.geosure.roadgis.actions.EditRedoAction;
import xyz.geosure.roadgis.actions.EditUndoAction;
import xyz.geosure.roadgis.actions.FileCloseAction;
import xyz.geosure.roadgis.actions.FileExitAction;
import xyz.geosure.roadgis.actions.FileImportAction;
import xyz.geosure.roadgis.actions.FilePrintAction;
import xyz.geosure.roadgis.actions.FileSaveAction;
import xyz.geosure.roadgis.actions.HelpAboutAction;
import xyz.geosure.roadgis.actions.InterpolatePointsAction;
import xyz.geosure.roadgis.actions.PopupAction;

public class RoadGISMenu {
	RoadGISApplication app;

	private JMenuBar menu_bar = null;

	JMenuItem edit_undo  ;               // menu item handled by hDesign methods
	JMenuItem edit_redo  ;
	JMenuItem edit_delete  ;


	public RoadGISMenu(RoadGISApplication app) {
		this.app = app;

		menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_open = new JMenuItem("Open Design") ;
		JMenuItem file_save = new JMenuItem("Save Design") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close Design") ;
		JMenuItem file_import = new JMenuItem("Import Contour") ;
		//      JMenuItem file_pagesetup = new JMenuItem("Page Setup") ; //page setup functino is pretty much cover under print
		JMenuItem file_print = new JMenuItem("Print") ;
		JMenuItem file_exit = new JMenuItem("Exit") ;
		// file menu items
		menu_file.add(file_open) ;   // add menu items
		menu_file.add(file_save) ;   // add menu items
		menu_file.addSeparator() ;
		menu_file.add(file_close) ;   // add menu items
		menu_file.addSeparator() ;
		menu_file.add(file_import) ;
		menu_file.addSeparator() ;
		//      menu_file.add(file_pagesetup) ;
		menu_file.add(file_print) ;
		menu_file.addSeparator() ;
		menu_file.add(file_exit) ;

		// edit menu
		JMenu menu_edit = new JMenu("Edit") ;
		edit_undo = new JMenuItem("Undo") ;
		edit_redo = new JMenuItem("Redo") ;
		edit_delete = new JMenuItem("Delete") ;
		JMenuItem edit_clearLandmarks = new JMenuItem("Clear Landmarks") ;
		JMenuItem edit_clearAll = new JMenuItem("Clear All") ;
		JMenuItem edit_unselectAll = new JMenuItem("Unselect All") ;
		JMenuItem edit_selectAll = new JMenuItem("Select All") ;
		menu_edit.add(edit_undo) ;
		menu_edit.add(edit_redo) ;
		menu_edit.add(edit_delete) ;
		menu_edit.addSeparator();
		menu_edit.add(edit_clearLandmarks) ;
		menu_edit.add(edit_clearAll) ;
		menu_edit.addSeparator();
		menu_edit.add(edit_selectAll) ;
		menu_edit.add(edit_unselectAll) ;


		// edit menu_layers
		JMenu menu_layers = new JMenu("Layers") ;
		JMenuItem layer_add = new JMenuItem("Add Layer") ;
		layer_add.addActionListener(new AddLayerAction(app)) ; // coords_import
		menu_layers.add(layer_add) ;
		
		menu_layers.addSeparator() ;
		
		JMenuItem layer_interpolate = new JMenuItem("Create Interpolated Layer") ;
		layer_interpolate.addActionListener(new InterpolatePointsAction(app)) ; // coords_import
		menu_layers.add(layer_interpolate) ;
		
		// edit menu
		JMenu menu_design = new JMenu("Design") ;
		JMenu menu_hdesign = new JMenu("Horizontal Design") ;
		
		
		JMenu menu_vdesign = new JMenu("Vertical Design") ;
		menu_design.add(menu_hdesign) ;
		menu_design.add(menu_vdesign) ;

		// view menu
		JMenu menu_view = new JMenu("View") ;
		JMenuItem view_reset = new JMenuItem("Reset (1:1)") ;
		view_reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_zoomin = new JMenuItem("Zoom In 10%") ;
		view_zoomin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_zoomout = new JMenuItem("Zoom Out 10%") ;
		view_zoomout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_zoomin5 = new JMenuItem("Zoom In 50%") ;
		view_zoomin5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_zoomout5 = new JMenuItem("Zoom Out 50%") ;
		view_zoomout5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_landmarks = new JMenuItem("Station Landmarks");

		JMenuItem view_tangents = new JMenuItem("PC, PT Data");
		JMenuItem view_roadOnly = new JMenuItem("Road Only");
		view_roadOnly.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		JMenuItem view_road = new JMenuItem("Road Design");
		view_reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		//view_landmarks.setEnabled(false);

		menu_view.add(view_reset) ;
		menu_view.add(view_zoomin) ;
		menu_view.add(view_zoomout) ;
		menu_view.add(view_zoomin5) ;
		menu_view.add(view_zoomout5) ;
		menu_view.addSeparator();
		menu_hdesign.add(view_landmarks) ;
		menu_hdesign.add(view_tangents) ;
		menu_view.addSeparator();
		menu_hdesign.add(view_road) ;
		menu_hdesign.add(view_roadOnly) ;

		// settings menu
		JMenu menu_settings = new JMenu("Settings") ;
		JMenuItem settings_design = new JMenuItem("Road Design") ;
		JMenuItem settings_contour = new JMenuItem("Contour Image") ;
		menu_settings.add(settings_design) ;
		menu_settings.add(settings_contour) ;
		// option menu
		JMenu menu_tool = new JMenu("Tool") ;
		JMenuItem tool_line = new JMenuItem("Create Line") ;
		JMenuItem tool_curve = new JMenuItem("Create Curve") ;
		JMenuItem tool_modify = new JMenuItem("Modify End Point") ;
		JMenuItem tool_station = new JMenuItem("Place Landmark") ;
		JMenuItem tool_insert = new JMenuItem("Insert Landmark") ;
		JMenuItem tool_halign = new JMenuItem("Align Curve") ;
		JMenuItem tool_property = new JMenuItem("Show Properties") ;
		JMenuItem tool_checkStation = new JMenuItem("Check Station Data") ;
		menu_tool.add(tool_line) ;
		menu_tool.add(tool_curve) ;
		menu_tool.add(tool_modify) ;
		menu_tool.add(tool_station) ;
		menu_tool.add(tool_insert) ;
		menu_tool.addSeparator() ;
		menu_tool.add(tool_halign) ; 
		menu_tool.add(tool_checkStation) ;
		menu_tool.add(tool_property) ;
		// help menu
		JMenu menu_help = new JMenu("Help") ;
		JMenuItem help_web_contents = new JMenuItem("Web Contents") ; 
		JMenuItem help_manual = new JMenuItem("User's Manual PDF") ;
		JMenuItem help_about = new JMenuItem("About ROAD") ;
		//JMenuItem help_javahelp = new JMenuItem("User's Guide") ; 
		//JMenuItem help_aboutJavaHelp = new JMenuItem("About JavaHelp") ;

		menu_help.add(help_web_contents) ;
		//menu_help.add(help_javahelp) ;
		menu_help.add(help_manual) ;
		menu_help.addSeparator() ;
		menu_help.add(help_about) ;
		//menu_help.add(help_aboutJavaHelp) ; // 11/26/06 added

		/*
		            // Find the HelpSet file and create the HelpSet object:
		            String helpHS = "javahelp/road.hs" ;    //"http://128.101.111.90/Road/javahelp/road.hs" ;
		            HelpSet hs = null ;
		            HelpBroker hb = null ;
		            ClassLoader cl = this.getClass().getClassLoader();
		            try {
		                URL hsURL = HelpSet.findHelpSet(cl, helpHS);
		                hs = new HelpSet(null, hsURL);
		            } catch (Exception ee) {
		                // Say what the exception really is
		                System.out.println( "HelpSet " + ee.getMessage()) ;
		                System.out.println("HelpSet "+ helpHS +" not found") ;
		            }
		            // Create a HelpBroker object:
		            hb = hs.createHelpBroker();

		            help_javahelp.addActionListener(new CSH.DisplayHelpFromSource( hb ));
		 */

		// ===========================================
		menu_bar.add(menu_file) ;     // add menu
		menu_bar.add(menu_edit) ;     // add menu
		menu_bar.add(menu_view) ;     // add menu
		menu_bar.add(menu_layers) ;     // add menu
		menu_bar.add(menu_design) ;     // add menu		
		menu_bar.add(menu_settings) ; // add menu
		menu_bar.add(menu_tool) ;   // add menu
		menu_bar.add(menu_help) ;     // add menu


		/*
		             help_javahelp.addActionListener(
		                new ActionListener() {
		                    public void actionPerformed(ActionEvent aev) {
		                        try
		                        {
		                            new HelpDoc() ; // open javaHelp document
		                        }
		                        catch (Exception e){
		                                //do nothing
		                            app.getHorizontalDesign().popMessageBox("Help - User's Guide", "Error:"+e.toString()) ;
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
							//AppletContext a = getAppletContext();
							//URL u = new URL(SHARED.CONTENTS_PATH);  
							//a.showDocument(u,"_blank");
							//_blank to open page in new window		
						}
						catch (Exception e){
							//do nothing
							new PopupAction(app, "Help - Web Content", "Error:"+e.toString()) ;
						} // try
					} // actionPerformed
				} // ActionListener
				) ; // help_web_contents

		help_manual.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						try
						{
							//AppletContext a = getAppletContext();
							//URL u = new URL(SHARED.MANUAL_PATH);  
							//a.showDocument(u,"_blank");
							//_blank to open page in new window		
						}
						catch (Exception e){
							//do nothing
						} // try
					} // actionPerformed
				} // ActionListener
				) ; // help_manual
		/*
		            help_aboutJavaHelp.addActionListener(
		                new ActionListener() {
		                    public void actionPerformed(ActionEvent aev) {
		                        app.getHorizontalDesign().popMessageBox("JavaHelp", 
		                        "Note: You need to have JavaHelp package\n" +  
		                        "(jh.jar, jhall.jar, jhbasic.jar, jsearch.jar)\n" +
		                        "installed in your ..\\Java\\jre\\lib\\ext\\ directory\n" + 
		                        "in order to view the Users' Guide. JavaHelp is\n" + 
		                        "available at http://java.sun.com/products/javahelp/.") ;

		                    } // actionPerformed
		                } // ActionListener
		            ) ; // help_aboutJavaHelp
		 */
		help_about.addActionListener(new HelpAboutAction(app)) ; // help_about
		
		file_import.addActionListener(new FileImportAction(app)) ; // file open
		file_save.addActionListener( new FileSaveAction(app)) ; // file save
		file_close.addActionListener(new FileCloseAction(app)); // file Close
		/*            file_pagesetup.addActionListener(
		                    new ActionListener() {
		                        public void actionPerformed(ActionEvent aev) {
		                            app.getHorizontalDesign().printPageSetup();

		                        } // actionPerformed
		                    } // ActionListener
		             ) ; // file Print Page Setup
		 */
		file_print.addActionListener(new FilePrintAction(app)) ; // file Print
		file_exit.addActionListener(new FileExitAction(app)) ; // file Exit
		edit_undo.addActionListener(new EditUndoAction(app)) ; // edit_undo
		edit_redo.addActionListener(new EditRedoAction(app)) ; // edit_redo
		edit_delete.addActionListener(new EditDeleteAction(app)) ; // edit_delete
		edit_clearLandmarks.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().popClearLandmarks("Edit - Clear All Landmarks","Are you sure to clear all station landmarks?");
					} // actionPerformed
				} // ActionListener
				) ; // edit_clearLandmarks
		edit_clearAll.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().popClearAll("Edit - Clear All Design","Are you sure to clear all design?");
					} // actionPerformed
				} // ActionListener
				) ; // edit_clearAll
		edit_selectAll.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().edit_selectAll();
					} // actionPerformed
				} // ActionListener
				) ; // edit_unselect All
		edit_unselectAll.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().edit_unselectAll() ;
					} // actionPerformed
				} // ActionListener
				) ; // edit_unselect All
		view_reset.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().view_RESET();
					} // actionPerformed
				} // ActionListener
				) ; // view reset
		view_landmarks.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().popLandmarkData();
					} // actionPerformed
				} // ActionListener
				) ; // view landmarks
		view_tangents.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().popTangentData(); 
					} // actionPerformed
				} // ActionListener
				) ; // view PC PT data
		view_road.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().viewRoadDesign();  
					} // actionPerformed
				} // ActionListener
				) ; // view horizontal road design only
		view_roadOnly.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().viewRoadOnly();  
					} // actionPerformed
				} // ActionListener
				) ; // view horizontal road design only

		view_zoomin.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().changeDrawScale(0.1f);
					} // actionPerformed
				} // ActionListener
				) ; // view zoom in
		view_zoomout.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().changeDrawScale(-0.1f);
					} // actionPerformed
				} // ActionListener
				) ; // view zoom out
		view_zoomin5.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().changeDrawScale(0.5f);
					} // actionPerformed
				} // ActionListener
				) ; // view zoom in 50%
		view_zoomout5.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().changeDrawScale(-0.5f);
					} // actionPerformed
				} // ActionListener
				) ; // view zoom out 50%
		tool_line.addActionListener(new HorizontalLineCreateAction(app));// tool_line
		tool_curve.addActionListener(new HorizontalCurveCreateAction(app)); // tool_curve
		
		tool_modify.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().newstatus(6, " Modify End Point") ;
						//app.getHorizontalDesign().repaint() ;
					} // actionPerformed
				} // ActionListener
				) ; // tool_modify 
		tool_station.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().newstatus(8, " Set Station/Landmark") ;
						//app.getHorizontalDesign().repaint() ;
					} // actionPerformed
				} // ActionListener
				) ; // tool_station
		tool_insert.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						//app.getHorizontalDesign().newstatus(9, " Insert Station/Landmark") ;
						//app.getHorizontalDesign().repaint() ;
					} // actionPerformed
				} // ActionListener
				) ; // tool_insert
		tool_halign.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().tool_curvehAlignMarks();
					} // actionPerformed
				} // ActionListener
				) ; // tool_halign
		tool_property.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().tool_property() ;
					} // actionPerformed
				} // ActionListener
				) ; // tool_property
		tool_checkStation.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().tool_checkStation() ; 
					} // actionPerformed
				} // ActionListener
				) ; // toolcheckStation

		settings_design.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {

						app.getHorizontalDesign().popSettingsDesign(); 

					} // actionPerformed
				} // ActionListener
				) ; // settings_design
		settings_contour.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						app.getHorizontalDesign().popSettingsContour();
					} // actionPerformed
				} // ActionListener
				) ; // settings_contour
	}

	public JMenuBar getMenuBar() {
		return menu_bar;
	}

	public JMenuItem getDeleteMenu() {
		return edit_delete;
	}

	public JMenuItem getRedoMenu() {
		return edit_redo;
	}

	public JMenuItem getUndoMenu() {
		return edit_undo;
	}

	

}
