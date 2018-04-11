package xyz.geosure.roadgis.views;
/*
 * aboutTextbox.java
 *
 * Created on March 16, 2006, 9:35 PM
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;

import xyz.geosure.roadgis.model.RoadDesign;

public class aboutTextbox extends Panel
{
	int panelWidth = 250 ;
	int panelHeight = 150 ;  
	Label title = new Label("Roadway Online Application for Design (ROAD)") ;
	//SHARED share = new SHARED();
	String verStr = RoadDesign.VERSION ;  
	Label ver = new Label(verStr) ; 
	Label org = new Label("MTO/CE/ITS Institute, University of Minnesota") ;

	public aboutTextbox()    {
		setBackground(new Color(128,255,255));
		setLayout(new BorderLayout(10,10));                
		title.setForeground(Color.black) ;
		title.setAlignment(title.CENTER) ;
		title.setFont(Font.getFont("TimesRoman-BOLD-15")) ;
		add(title, BorderLayout.NORTH) ;

		ver.setForeground(Color.black) ;
		ver.setAlignment(ver.CENTER) ;
		ver.setFont(Font.getFont("Arial-10")) ;
		add(ver, BorderLayout.CENTER) ;

		org.setForeground(Color.blue) ;
		org.setAlignment(org.CENTER) ;
		org.setFont(Font.getFont("TimesRoman-BOLD-12")) ;
		add(org, BorderLayout.SOUTH) ;
	}

	public void paint(Graphics g)     {

	}

	public Dimension preferredSize()    {
		return(new Dimension(panelWidth,panelHeight));
	}

	public boolean mouseDown(Event e,int x,int y)    {
		return(true);
	}

}
