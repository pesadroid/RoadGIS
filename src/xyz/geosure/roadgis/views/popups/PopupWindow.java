package xyz.geosure.roadgis.views.popups;

import xyz.geosure.roadgis.views.RoadGISPopup;

public class PopupWindow {
	
	RoadGISPopup popup;

	public void show() {
		
		popup.invalidate();
		popup.show() ;
		popup.toFront() ;
		popup.setResizable(false);
	}
	
	public boolean isShowing() {
		if(null == popup) {
			return false;
		}
		return popup.isShowing();
	}
	
	public void dispose() {
		if(null == popup) {

			popup.dispose();
		}
	}
}
