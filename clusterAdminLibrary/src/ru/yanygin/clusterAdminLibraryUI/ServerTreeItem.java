package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ServerTreeItem extends TreeItem {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;

	public ServerTreeItem(Tree parent, int style, Server config) {
		super(parent, style);
		
		this.setText(new String[] { config.getServerDescription()});
		this.setData("Type", "Server");
		this.setData("ServerKey", config.getServerKey());
		this.setData("ServerConfig", config);
		
		initIcon();
		
		if (config.isConnected()) {
			this.setImage(serverIconUp);
		} else {
			this.setImage(serverIconDown);
		}
		this.setChecked(config.autoconnect);
	}
	
	private void initIcon() {
		serverIcon = getImage(getParent().getDisplay(), "/server_24.png");
		serverIconUp = getImage(getParent().getDisplay(), "/server_up_24.png");
		serverIconDown = getImage(getParent().getDisplay(), "/server_down_24.png");
	}
	
	private Image getImage(Device device, String name) {
		return new Image(device, this.getClass().getResourceAsStream(name));
	}

}
