package ru.yanygin.clusterAdminLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._1c.v8.ibis.admin.AgentAdminException;
import com._1c.v8.ibis.admin.IAgentAdminConnection;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com._1c.v8.ibis.admin.client.IAgentAdminConnector;
import com._1c.v8.ibis.admin.client.IAgentAdminConnectorFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.yanygin.clusterAdminLibraryUI.AuthenticateDialog;

public class Config {
	@SerializedName("Servers")
	@Expose
	public Map<String, Server> servers = new HashMap<>(); // Надо определиться что должно являться ключем, агент (Server:1540) или менеджер (Server:1541)

	private static Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary");
	
	public Server CreateNewServer() {
		return new Server("newServerAddress:1541");
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


}


