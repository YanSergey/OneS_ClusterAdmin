package ru.yanygin.clusterAdminApplication;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterAdmin {

	public static void main (String[] args) {
		Logger LOGGER = LoggerFactory.getLogger("ClusterProvider"); //$NON-NLS-1$
				
		try {
			ClusterViewer window = new ClusterViewer();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
			
		} catch (Exception excp) {
			excp.printStackTrace();
			LOGGER.error("Error:", excp); //$NON-NLS-1$
		}
		
	}
	
}
