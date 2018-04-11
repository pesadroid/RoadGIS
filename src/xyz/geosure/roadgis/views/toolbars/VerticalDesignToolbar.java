package xyz.geosure.roadgis.views.toolbars;
/*
 * toolbarV.java
 * Vertical curve design toolbar
 *
 * Created on March 23, 2006, 8:25 PM
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import xyz.geosure.roadgis.controllers.VerticalDesignController;

public class VerticalDesignToolbar extends JToolBar {
	final int NUM_ICONS=9;        // Number of tool items
	final int imgSize=40;       // default image size
	int status = -1;
	public VerticalDesignController parent;
	boolean constructEnabled = true;    // vertical curve construction flag
	Image img[] = new Image[NUM_ICONS] ;

	final String[] commands = {" Grade Construction ON", 
			" Grade Construction OFF", 
			" Generate Vertical Curves",
			" Modify Curve Length",
			" View Elevation Profile",
			" View Fill/Cut Profile",
			" View Mass Diagram",
			"",   //" Generate Report" ; defined in runThread0, vDrawArea
			" Create 3D Animation Model"};// switch
	
	// class construction
	public VerticalDesignToolbar()
	{
		setBackground(Color.lightGray);
	
		for (int i=0; i<NUM_ICONS; i++) {
			URL url = null;
			String str = getActionString(i);
			url = getImageResource(str);
			
			img[i] = Toolkit.getDefaultToolkit().getImage(url);

			JButton button = new JButton();
			button.setActionCommand(str);
			button.setIcon(new ImageIcon(img[i]));
			button.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { 
					//System.exit(0);
					int index = getActionIndex(e.getActionCommand());
					
					if (index==3) {
						parent.setCurvelenEdit_flag = true ;  
					} else if (index==7) {
						parent.setReport_flag = true ; 
					} else {
						parent.newstatus(index, commands[index]);
					}
				}
			});

			add(button);
		}
	}
	public String getActionString(int index)     {
		String str = "";
		switch (index) {
		case 0:
			//url = getClass().getResource("Arrow.png");
			
			if (constructEnabled) {
				str = "construction";
			} else {
				str = "constructdisabled";
			}
			break ;
		case 1:
			//url = getClass().getResource("ZoomIn.png");
			str = "constructoff";
			break ;
		case 2:
			str = "calcPVI";
			break ;
		case 3:
			str = "modCurve";
			break ;
		case 4:
			str = "elevation";
			break ;
		case 5:
			str = "fillncut";
			break ;
		case 6:
			str = "massdiagram";
			break ;
		case 7:
			str = "report";
			break ;
		case 8:
			str = "animation";
			break ;
		}
		return str;
	}

	public int getActionIndex(String action)     {
		int index = 0;
		
		if (constructEnabled) {
			if(action.equalsIgnoreCase("construction")){
				index = 0;
			}
		} else {
			if(action.equalsIgnoreCase("constructdisabled")){
				index = 0;
			}
		}
		if(action.equalsIgnoreCase("constructoff")){
			index = 1;
		}
		if(action.equalsIgnoreCase("calcPVI")){
			index = 2;
		}
		if(action.equalsIgnoreCase("modCurve")){
			index = 3;
		}
		if(action.equalsIgnoreCase("elevation")){
			index = 4;
		}
		if(action.equalsIgnoreCase("fillncut")){
			index = 5;
		}
		if(action.equalsIgnoreCase("massdiagram")){
			index = 6;
		}
		if(action.equalsIgnoreCase("report")){
			index = 7;
		}
		if(action.equalsIgnoreCase("animation")){
			index = 8;
		}

		return index;
	}
	/*
    public void paint(Graphics g) 
    {
	Rectangle r = bounds();
        int i ;
	for(i=0;i<NUM_ICONS;i++)
	{
	    if(i==status)
	    {
		g.setColor(new Color(192,192,192));
		g.fillRect(i*(imgSize+1), 0,imgSize,imgSize);
	    }
	    g.setColor(Color.black);
	    g.drawLine(i*(imgSize+1)+imgSize, 0,i*(imgSize+1)+imgSize, imgSize);

            g.drawImage(img[i], i*(imgSize+1)+4,4,this) ;
            if (i!=status) {
                // button boundary depressed
                g.setColor(Color.white);
                g.drawLine(i*(imgSize+1)+3, 3,(i+1)*(imgSize+1)-4, 3);
                g.drawLine(i*(imgSize+1)+3, 3,i*(imgSize+1)+3, (imgSize-4));
                g.setColor(Color.black);
                g.drawLine(i*(imgSize+1)+3, (imgSize-4),(i+1)*(imgSize+1)-4, (imgSize-4));
                g.drawLine((i+1)*(imgSize+1)-4, 3,(i+1)*(imgSize+1)-4, (imgSize-4));
            } else {
                // button pressed
                // button boundary depressed
                g.setColor(Color.black);
                g.drawLine(i*(imgSize+1)+3, 3,(i+1)*(imgSize+1)-4, 3);
                g.drawLine(i*(imgSize+1)+3, 3,i*(imgSize+1)+3, (imgSize-4));
                g.setColor(Color.white);
                g.drawLine(i*(imgSize+1)+3, (imgSize-4),(i+1)*(imgSize+1)-4, (imgSize-4));
                g.drawLine((i+1)*(imgSize+1)-4, 3,(i+1)*(imgSize+1)-4, (imgSize-4));
           }
	}   // i
        g.setColor(Color.black);
        g.drawString("ITS Institute, University of Minnesota", i*(imgSize+1)+20, 25) ;

    }
    //*/

	public URL getImageResource(String img) {
		URL url = null;
		//try {
		url = getClass().getResource("/resources/" + img + ".png");
		if (url==null) {
			url = getClass().getResource("/resources/" + img + ".PNG");
		}
		//} catch (IOException ioe) {
		//    System.out.println(url.toString());
		//}
		return url ;
	}

	public Dimension preferredSize()
	{
		return(new Dimension(imgSize,imgSize));
	}

	public void setConstructEnabled(boolean state) {
		constructEnabled = state ;
		repaint();
	}

	public boolean mouseDown(Event e, int x, int y)
	{
		if(x<NUM_ICONS*(imgSize+1))
		{
			int oldstatus = status;
			status = x/(imgSize+1);
			if(status<0) status = 0;
			if(status>NUM_ICONS) status = NUM_ICONS;
			//if(oldstatus!=status)
			//{
			String str = "" ;
			switch (status) {
			case 0:
				str = " Grade Construction ON" ;
				break ;
			case 1:
				str = " Grade Construction OFF" ;
				break ;
			case 2:
				str = " Generate Vertical Curves" ;
				break ;
			case 3:
				str = " Modify Curve Length" ;
				break ;
			case 4:
				str = " View Elevation Profile" ;
				break ;
			case 5:
				str = " View Fill/Cut Profile" ;
				break ;
			case 6:
				str = " View Mass Diagram" ;
				break ;
			case 7:
				str = "";     //" Generate Report" ; defined in runThread0, vDrawArea
				break ;
			case 8:
				str = " Create 3D Animation Model" ;
				break ;

			} // switch
			if (status==3) {
				parent.setCurvelenEdit_flag = true ;  
			} else if (status==7) {
				parent.setReport_flag = true ; 
			} else {
				parent.newstatus(status, str);
			}
			repaint();
			//}
		}
		return(true);
	}
}
