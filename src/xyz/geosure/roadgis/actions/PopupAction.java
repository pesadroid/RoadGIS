package xyz.geosure.roadgis.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;


@SuppressWarnings("serial")
public class PopupAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;
	RoadGISPopup frame_msgbox;
	private String message, caption;

	public PopupAction(RoadGISApplication app, String caption, String message) {
		this.app = app;
		this.message = message;
		this.caption = caption;		
	}

	@Override
	public void actionPerformed(ActionEvent aev) {

		// open a frame
		frame_msgbox = new RoadGISPopup(caption) ;
		//frame_msgbox.setLocation(400,50) ;
		frame_msgbox.setSize(310,150) ;
		frame_msgbox.setCenter() ;
		frame_msgbox.validate() ;
		frame_msgbox.setVisible(true) ;
		frame_msgbox.setResizable(false);
		//frame_msgbox.show() ;
		/*
        ActionListener frame_msgbox_ok_listener = new ActionListener() {
            public void actionPerformed(ActionEvent aev) {

                frame_msgbox.dispose() ;
            }
        } ;
		 */
		frame_msgbox.setLayout(new BorderLayout(1,1)) ;
		JTextArea myTitle = new JTextArea(message, 3, 60) ;
		myTitle.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		myTitle.setForeground(new Color(0,0,218)) ;
		frame_msgbox.setBackground(new Color(200, 200, 200)) ;
		frame_msgbox.add("Center",myTitle) ;

		//Button btn_ok = new Button(" OK ") ;
		//frame_msgbox.add("South",btn_ok) ;
		//btn_ok.addActionListener(frame_msgbox_ok_listener) ;
		//frame_msgbox.invalidate();
		frame_msgbox.show() ;
		frame_msgbox.toFront() ;
	} // actionPerformed
} // ActionListener
