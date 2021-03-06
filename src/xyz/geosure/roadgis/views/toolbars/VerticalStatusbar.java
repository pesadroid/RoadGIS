package xyz.geosure.roadgis.views.toolbars;
/*
 * statusbarV.java
 *
 * Created on March 16, 2006, 8:25 PM
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

import java.awt.*;
import java.net.URL;

import xyz.geosure.roadgis.controllers.VerticalDesignController;

public class VerticalStatusbar extends Panel
{
    String statusStr = "Status: " ;
    String errorStr = "Error: " ;
    //Color cstat = new Color(200,200,200);
    public VerticalDesignController parent ;
    int[] panelWidth = {250,550} ;
    int panelHeight=20 ;


    public VerticalStatusbar()
    {
	setBackground(Color.lightGray);
    }

    public void paint(Graphics g) 
    {
	Rectangle r = bounds();

        int start = 0;
	for(int i=0;i<2;i++)
	{
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
             }
            start += panelWidth[i] ;
	}   // i
        
    }

    public Dimension preferredSize()
    {
	return(new Dimension(panelWidth[0],panelHeight));
    }

    public boolean mouseDown(Event e,int x,int y)
    {
	return(true);
    }
    public boolean setStatusBarText(int index, String str)
    {
        switch (index) {
        case 0:
            statusStr = "Status: " + str;
            break ;
        case 1:
            errorStr = "Error: " + str;
            break ;
         }
         repaint();
	 return(true);
    }
}
