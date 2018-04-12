package xyz.geosure.roadgis.views.toolbars;
/*
 * statusbar.java
 *
 * Created on March 16, 2006, 8:25 PM
 * 
 * Modified and Renamed by Felix - March 2018
 */

import java.awt.BorderLayout;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.geotools.swing.control.JMapStatusBar;

import xyz.geosure.roadgis.RoadGISApplication;

public class HorizontalStatusbar{
	private static RoadGISApplication app = null;
	String statusStr = "Status:" ;
	String errorStr = "Error:" ;
	String posStr = "(X,Y):";
	String scaleStr = "Scale:";
	//Color cstat = new Color(200,200,200);
	
	int[] panelWidth = {200,200, 100,70} ;
	int panelHeight=20 ;
	private JMapStatusBar bar = null;
    JLabel statusMsg = null, errorMsg = null;;


	public HorizontalStatusbar(RoadGISApplication app)	{
		this.app = app;
		bar = JMapStatusBar.createDefaultStatusBar(app.getMapPane());
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		app.getMapFrame().add(panel, BorderLayout.SOUTH);
		
		statusMsg = new JLabel(" ", JLabel.LEFT);
		errorMsg = new JLabel(" ", JLabel.LEFT);
	    panel.add(statusMsg);
		bar.add(panel);
	}

	public void paint(Graphics g) 	{
		//Rectangle r = bounds();

		int start = 0;
		for(int i=0;i<4;i++)		{
			g.setColor(Color.lightGray);
			g.fillRect(start, 0,start,panelHeight);

			g.setColor(Color.black);
			g.drawLine(start, 0,start, panelHeight);
			switch (i) {
			case 0:
				g.drawString(statusStr, start+2, panelHeight-4);
				break ;
			case 1:
				g.drawString(errorStr, start+2, panelHeight-4);
				break ;
			case 2:
				g.drawString(posStr, start+2, panelHeight-4);
				break ;
			case 3:
				g.drawString(scaleStr, start+2, panelHeight-4);
				break ;
			}
			start += panelWidth[i] ;
		}   // i

	}

	public Dimension preferredSize()	{
		return(new Dimension(panelWidth[0],panelHeight));
	}

	public boolean mouseDown(Event e,int x,int y)	{
		return(true);
	}
	public boolean setStatusBarText(int index, String str)	{
		switch (index) {
		case 0:
			statusStr="Status:" + str;
			statusMsg.setText(statusStr);
			break ;
		case 1:
			errorStr="Error:" + str;
			errorMsg.setText(errorStr);
			break ;
		case 2:
			posStr="(X,Y):" + str;
			break ;
		case 3:
			scaleStr="Scale:" + str;
			break ;
		}
		return(true);
	}

	public JMapStatusBar getBar() {
		return bar;
	}

	private void setBar(JMapStatusBar bar) {
		this.bar = bar;
	}
}
