package ru.yanygin.clusterAdminLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {
	@SerializedName("Servers")
	@Expose
	public Map<String, Server> servers = new HashMap<>();
	
	@SerializedName("ExpandServers")
	@Expose
	public boolean expandServersTree;
	
	@SerializedName("ExpandClustersTree")
	@Expose
	public boolean expandClustersTree;
	
	@SerializedName("ExpandInfobasesTree")
	@Expose
	public boolean expandInfobasesTree;
	
	@SerializedName("ShowWorkingServersTree")
	@Expose
	public boolean showWorkingServersTree;
	
	@SerializedName("ExpandWorkingServersTree")
	@Expose
	public boolean expandWorkingServersTree;
	
	@SerializedName("ShowWorkingProcessesTree")
	@Expose
	public boolean showWorkingProcessesTree;
	
	@SerializedName("ExpandWorkingProcessesTree")
	@Expose
	public boolean expandWorkingProcessesTree;
	
	@SerializedName("ShowServerDescription")
	@Expose
	public boolean showServerDescription;
	
	@SerializedName("ShowServerVersion")
	@Expose
	public boolean showServerVersion;
	
	@SerializedName("ShowInfobaseDescription")
	@Expose
	public boolean showInfobaseDescription;
	
	@SerializedName("ShowLocalRasConnectInfo")
	@Expose
	public boolean showLocalRasConnectInfo;
	
	@SerializedName("Locale")
	@Expose
	public String locale;
	
	@SerializedName("SessionColumnProperties")
	@Expose
	public ColumnProperties sessionColumnProperties;
	
	@SerializedName("ConnectionColumnProperties")
	@Expose
	public ColumnProperties connectionColumnProperties;
	
	@SerializedName("LockColumnProperties")
	@Expose
	public ColumnProperties lockColumnProperties;
	
	@SerializedName("WPColumnProperties")
	@Expose
	public ColumnProperties wpColumnProperties;
	
	@SerializedName("WSColumnProperties")
	@Expose
	public ColumnProperties wsColumnProperties;
	
	@SerializedName("ShadowSleepSessions")
	@Expose
	public boolean shadowSleepSessions;
	
	@SerializedName("HighlightNewItems")
	@Expose
	public boolean highlightNewItems;
	
	@SerializedName("HighlightNewItemsDuration")
	@Expose
	public int highlightNewItemsDuration;
	@SerializedName("ReadClipboard")
	@Expose
	public boolean readClipboard;

	
	private static Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$
	
	private OSType currrentOS;
	
	private enum OSType {
		WINDOWS, MACOS, LINUX, OTHER
	}
	
	public Config() {
		this.init();
	}
	
	public void init() {
		getOperatingSystemType();
		
		this.servers.forEach((key, server) -> {
			server.init();
		});
	}
	
	private void getOperatingSystemType() {
		if (currrentOS == null) {
			String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			LOGGER.debug("Current OS is <{}>", osName); //$NON-NLS-1$

			if ((osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0)) {
				currrentOS = OSType.MACOS;
			} else if (osName.indexOf("win") >= 0) {
				currrentOS = OSType.WINDOWS;
			} else if (osName.indexOf("nux") >= 0) {
				currrentOS = OSType.LINUX;
			} else {
				currrentOS = OSType.OTHER;
			}
		}
	}
	
	public List<String> addNewServers(List<String> servers) {
		// Пакетное добавление серверов в список, предполагается для механизма импорта из списка информационных баз

		List<String> addedServers = new ArrayList<>();

		// Имя сервера, которое приходит сюда не равно Представлению сервера, выводимому в списке
		// Имя сервера. оно же Key в map и json, строка вида Server:1541, с обязательным указанием порта менеджера, к которому подключаемся
		// если порт менеджера не задан - ставим стандартный 1541
		// переделать
		for (String serverName : servers) {
			if (!this.servers.containsKey(serverName)) {
				Server serverConfig = new Server(serverName);
				this.servers.put(serverName, serverConfig);

				addedServers.add(serverName);
			}
		}

		return addedServers;
	}
	
	public void connectAllServers() {
		servers.forEach((serverKey, server) -> server.connectToServer(false, true));
	}
	
	public void checkConnectionAllServers() {
		servers.forEach((serverKey, server) -> server.connectToServer(true, true));
	}
	
	public boolean isWindows() {
		return currrentOS == OSType.WINDOWS;
	}
	
	public boolean isLinux() {
		return currrentOS == OSType.LINUX;
	}
	
	public boolean isMacOS() {
		return currrentOS == OSType.MACOS;
	}
	
	public void setSessionsColumnOrder(int[] columnOrder) {
		sessionColumnProperties.order = columnOrder;
	}

	public void setConnectionsColumnOrder(int[] columnOrder) {
		connectionColumnProperties.order = columnOrder;
	}

	public void setLocksColumnOrder(int[] columnOrder) {
		lockColumnProperties.order = columnOrder;
	}

	public void setWorkingProcessesColumnOrder(int[] columnOrder) {
		wpColumnProperties.order = columnOrder;
	}

	public void setWorkingServersColumnOrder(int[] columnOrder) {
		wsColumnProperties.order = columnOrder;
	}
	
	public void initSessionsColumnCount(int columnCount) {
		
		if (sessionColumnProperties == null) 
			sessionColumnProperties = new ColumnProperties(columnCount);
		else
			sessionColumnProperties.updateColumnProperties(columnCount);
	}
	
	public void initConnectionsColumnCount(int columnCount) {
		
		if (connectionColumnProperties == null) 
			connectionColumnProperties = new ColumnProperties(columnCount);
		else
			connectionColumnProperties.updateColumnProperties(columnCount);
	}
	
	public void initLocksColumnCount(int columnCount) {
		
		if (lockColumnProperties == null) 
			lockColumnProperties = new ColumnProperties(columnCount);
		else
			lockColumnProperties.updateColumnProperties(columnCount);
	}
	
	public void initWorkingProcessesColumnCount(int columnCount) {
		
		if (wpColumnProperties == null) 
			wpColumnProperties = new ColumnProperties(columnCount);
		else
			wpColumnProperties.updateColumnProperties(columnCount);
	}
	
	public void initWorkingServersColumnCount(int columnCount) {
		
		if (wsColumnProperties == null) 
			wsColumnProperties = new ColumnProperties(columnCount);
		else
			wsColumnProperties.updateColumnProperties(columnCount);
	}
	
	public void setSessionsColumnWidth(int index, int width) {
		sessionColumnProperties.width[index] = width;
	}

	public void setConnectionsColumnWidth(int index, int width) {
		connectionColumnProperties.width[index] = width;
	}

	public void setLocksColumnWidth(int index, int width) {
		lockColumnProperties.width[index] = width;
	}

	public void setWorkingProcessesColumnWidth(int index, int width) {
		wpColumnProperties.width[index] = width;
	}

	public void setWorkingServersColumnWidth(int index, int width) {
		wsColumnProperties.width[index] = width;
	}
	
}


