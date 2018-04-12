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

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.utils.ConversionUtils;
import xyz.geosure.roadgis.utils.print.PrintText;

public class TangentDataFrame extends FrameWindow{
	private RoadGISApplication app = null;
	
	String tangentPrintStr = "" ;       // calculated minimum radius (Rv)
	

	public TangentDataFrame(RoadGISApplication app) {
		this.app = app;
	}
	public void build() {
		
		
		frame = new JFrame("View PC, PT Data") ;
		frame.setSize(350, 200);
		//Make sure we have nice window decorations.
		//      frame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// file menu
		JMenuBar menu_bar = new JMenuBar() ;
		JMenu menu_file = new JMenu("File") ;
		JMenuItem file_saveTangent = new JMenuItem("Save Data") ;
		JMenuItem file_printreport = new JMenuItem("Print") ;
		JMenuItem separator = new JMenuItem("-") ;
		JMenuItem file_close = new JMenuItem("Close") ;
		menu_file.add(file_saveTangent) ;   // add menu items
		menu_file.add(file_printreport) ;   // add menu items
		menu_file.add(separator) ;   // add menu items
		menu_file.add(file_close) ;   // add menu items

		menu_bar.add(menu_file) ;     // add menu
		frame.setJMenuBar(menu_bar) ;

		file_saveTangent.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						tangent_saveData();  
						app.getUIActionsHandler().setStatusBarText(0, "Save PC, PT Data") ;
					} // actionPerformed
				} // ActionListener
				) ; // file save report
		file_printreport.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						tangent_printData();  
						app.getUIActionsHandler().setStatusBarText(0, "Print PC, PT Data") ;
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

		String[] headers = { "ID", "POS X", "POS Y", "Curve ID", "Type" };
		int[] fieldSize = {6,10,10,12,8} ;
		tangentPrintStr = PrintText.StrFormat(0,"ID", fieldSize[0]) +
				PrintText.StrFormat(0,"POS X", fieldSize[1]) +
				PrintText.StrFormat(0,"POS Y", fieldSize[2]) +
				PrintText.StrFormat(0,"Curve ID", fieldSize[3]) +
				PrintText.StrFormat(0,"Type", fieldSize[4]) + "\n" ;
		String[][] data = new String[app.getHorizontalDesign().getRoadDesign().getNumberOfHorizontalAlignmentMarks()][headers.length];
		int i;
		float myScale = (float)app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / (float)app.getHorizontalDesign().getRoadDesign().getContourScale();
		for (i=0;i<app.getHorizontalDesign().getRoadDesign().getNumberOfHorizontalAlignmentMarks(); i++) {
			data[i][0] = ConversionUtils.CStr(i + 1) ;
			data[i][1] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getLocation().getX()/myScale*1000f)/1000f) ;
			data[i][2] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getLocation().getY()/myScale*1000f)/1000f) ;
			data[i][3] = ConversionUtils.CStr(Math.round(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getParentIndex())+1);
			int div = i / 2 ;
			int remainder = i - div*2 ;
			switch (remainder) {
			case 0: 
				data[i][4] = "PC";
				break;
			case 1:
				data[i][4] = "PT";
				break;
			default:
				data[i][4] = "None";
				break;
			}   //End Select
			for (int j=0; j<5; j++){
				tangentPrintStr += PrintText.StrFormat(0, data[i][j].toString(), fieldSize[j]) ;
			}
			tangentPrintStr += "\n" ;
		}   // for i
		JTable table = new JTable(data, headers) {
			// override isCellEditable method, , 11/13/06
			public boolean isCellEditable(int row, int column) {
				// all un-editable
				return false;
			}    // isCellEditable method
		} ;

		table.setPreferredScrollableViewportSize(new Dimension(350, 200));
		table.setColumnSelectionAllowed(true) ;
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
		//table.setRowSelectionAllowed(false);
		//table.setCellSelectionEnabled(true) ;
		//table.setColumnSelectionAllowed(false) ;

		//Add the scroll pane to this panel.
		frame.add(scrollPane);
		//Get the column model.
		javax.swing.table.TableColumnModel colModel = table.getColumnModel();
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

	public void tangent_printData(){  // print report file
		try        {
			//PrintSimpleText printReport = new PrintSimpleText(reportStr) ;
			PrintText printReport = new PrintText() ;
			printReport.print(tangentPrintStr) ;

		}
		catch (Exception e){
			//do nothing
			System.out.println("Print PC, PT Data:"+e.toString());
			app.getUIActionsHandler().setStatusBarText(1, "Error: Print PC, PT Data, "+e.toString()) ;
		} // try

	}// tangent print data


	public void tangent_saveData(){  // save report file
		try        {
			FileDialog fd=new FileDialog(new JFrame(),"Save PC, PT Data", FileDialog.SAVE);
			fd.setFile("*.txt");
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(fullpath));
				//String reportStr = generateReport();
				out.write(tangentPrintStr);
				out.flush();
				out.close();
			}
		}
		catch (Exception e){
			//do nothing
			System.out.println("Save PC, PT Data File:"+e.toString());
			app.getUIActionsHandler().setStatusBarText(1, "Error: Saving PC, PT Data, "+e.toString()) ;
		} // try

	}// tangent save data
}
