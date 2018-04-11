package xyz.geosure.roadgis.views.frames;

import javax.swing.JFrame;

public class FrameWindow{
	JFrame frame;

	public void show() {
		
		frame.invalidate();
		frame.show() ;
		frame.toFront() ;
		frame.setResizable(false);
	}
	
	public boolean isShowing() {
		if(null == frame) {
			return false;
		}
		return frame.isShowing();
	}
	
	public void dispose() {
		if(null == frame) {

			frame.dispose();
		}
	}
}
