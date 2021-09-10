package ru.yanygin.clusterAdminApplication;

import org.eclipse.swt.widgets.Display;

public class ClusterAdmin {

	public static void main (String[] args) {
				
		try {
			ClusterViewer window = new ClusterViewer();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
