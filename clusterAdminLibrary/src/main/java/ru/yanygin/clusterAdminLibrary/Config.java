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
	
	public Server createNewServer() {
		return new Server("newServerAddress:1541"); //$NON-NLS-1$
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
		servers.forEach((server, config) -> {
			config.connectAndAuthenticate(false);
		});
	}
	
	public void checkConnectionAllServers() {
		servers.forEach((server, config) -> {
			config.connectAndAuthenticate(true);
		});
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
	
	
}


