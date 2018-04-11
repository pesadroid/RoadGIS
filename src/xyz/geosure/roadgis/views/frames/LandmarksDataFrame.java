package xyz.geosure.roadgis.views.frames;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.print.PrintText;

public class LandmarksDataFrame extends FrameWindow{

	private RoadGISApplication app = null;
	
	private String landmarkPrintStr = "";   // save & print landmark data
	private JTable stationTable = new JTable();


	public LandmarksDataFrame(RoadGISApplication app) {
		this.app = app;
	}
	public void build() {
		frame = new JFrame("View Landmark Data") ;
		frame.setSize(350, 200);
		//Make sure we have nice window decorations.
		//      frame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_saveStation = new JMenuItem("Save Data to File") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_saveStation) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		JMenu menu_data = new JMenu("Data") ;
		JMenuItem data_saveElevation = new JMenuItem("Update Elevation") ;
		menu_data.add(data_saveElevation) ; // save elevation data

		menu_bar.add(menu_file) ;     // add menu
		menu_bar.add(menu_data) ;     // add menu
		frame.setJMenuBar(menu_bar) ;

		file_saveStation.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						landmark_saveData();  
						app.getHorizontalDesign().getHorizontalStatusBar().setStatusBarText(0, "Save Landmark Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		data_saveElevation.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						TableModel model = stationTable.getModel() ;
						//System.out.println(" column ="+model.getColumnCount()) ;
						//System.out.println(" rows="+model.getRowCount()) ;

						for (int i=0; i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount(); i++) {
							String valX = (String)model.getValueAt(i, 1) ;
							String valY= (String)model.getValueAt(i, 2) ;
							String valStr = (String)model.getValueAt(i, 3) ;
							//System.out.println(i+" data="+valStr) ;
							
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setLocation(new GeometryFactory().createPoint(new Coordinate( Double.parseDouble(valX), Double.parseDouble(valY))));
							app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).setElevation(Double.parseDouble(valStr)) ;
						}
						app.getHorizontalDesign().getHorizontalStatusBar().setStatusBarText(0, "Update Elevation Data") ;
					} // actionPerformed
				} // ActionListener
				) ; //  save elevation    
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						landmark_printData();  
						app.getHorizontalDesign().getHorizontalStatusBar().setStatusBarText(0, "Print Landmark Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_close.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						frame.dispose();
					} // actionPerformed
				} // ActionListener
				) ; // file Close

		String[] headers = { "ID", "POS X", "POS Y", "Elevation", "Type" };
		int[] fieldSize = {6,10,10,12,8} ;
		landmarkPrintStr = PrintText.StrFormat(0,"ID", fieldSize[0]) +
				PrintText.StrFormat(0,"POS X", fieldSize[1]) +
				PrintText.StrFormat(0,"POS Y", fieldSize[2]) +
				PrintText.StrFormat(0,"Elavation", fieldSize[3]) +
				PrintText.StrFormat(0,"Type", fieldSize[4]) + "\n" ;
		String[][] data = new String[app.getHorizontalDesign().getRoadDesign().getElevationMarkCount()][headers.length];
		int i;
		float myScale = (float)app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / (float)app.getHorizontalDesign().getRoadDesign().getContourScale();
		for (i=0;i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount(); i++) {
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getLocation().getX()/myScale*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getLocation().getY()/myScale*1000f)/1000f) ;
			data[i][3] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getElevation()*1000f)/1000f);
			switch (app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getSegmentType()) {
			case LINE:
				data[i][4] = "Line";
				break;
			case CURVE:
				data[i][4] = "Curve";
				break;
			case TANGENT:
				data[i][4] = "Tangent";
				break;
			default:
				data[i][4] = "None";
				break;
			}   //End Select
			for (int j=0; j<5; j++){
				landmarkPrintStr += PrintText.StrFormat(0, data[i][j].toString(), fieldSize[j]) ;
			}
			landmarkPrintStr += "\n" ;
		}   // for i
		stationTable = new JTable(data, headers) {
			// override isCellEditable method, 11/13/06
			public boolean isCellEditable(int row, int column) {
				if (column <= 3) {
					return true ;
				} else {
					return false ;
				}    
			}    // isCellEditable method
		} ;
		stationTable.setPreferredScrollableViewportSize(new Dimension(350, 200));
		stationTable.setColumnSelectionAllowed(true) ;

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(stationTable);

		//Add the scroll pane to this panel.
		frame.add(scrollPane);
		//Get the column model.
		TableColumnModel colModel = stationTable.getColumnModel();
		//Get the column at index pColumn, and set its preferred width.
		colModel.getColumn(0).setPreferredWidth(24);   

		//Display the window.
		Dimension screen = app.getToolkit().getDefaultToolkit().getScreenSize();
		double top = 0.5*(screen.getWidth()-frame.getWidth());
		double left = 0.5*(screen.getHeight()-frame.getHeight());
		int x = new Double(top).intValue();
		int y = new Double(left).intValue();
		frame.setLocation(x, y);

	}
	

	public void landmark_printData(){  // print report file
		try        {
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(landmarkPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print Landmark Data:"+e.toString());
			app.getHorizontalDesign().getHorizontalStatusBar().setStatusBarText(1, "Error: Print Landmark Data, "+e.toString()) ;
		} // try

	}// landmark print data
	

	public void landmark_saveData(){  // save report file
		try        {
			FileDialog fd=new FileDialog(new JFrame(),"Save Landmark Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				//String reportStr = generateReport();
				out.write(landmarkPrintStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			//do nothing
			System.out.println("Save Landmark Data File:"+e.toString());
			app.getHorizontalDesign().getHorizontalStatusBar().setStatusBarText(1, "Error: Saving Landmark Data, "+e.toString()) ;
		} // try

	}// landmark save data
}
