package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ClusterProvider {
	
	File configFile;
	Config commonConfig;
	String defaultConfigPath = ".\\config.json";

	Logger LOGGER = LoggerFactory.getLogger("ClusterProvider");

	public ClusterProvider() {
		
		
	}
	
	public void readSavedKnownServers(String configPath) {
		LOGGER.info("Start read config from file <{}>", configPath);
		
		if (configPath.isBlank()) {
			LOGGER.debug("Config path is empty, create new config in root folder");
			commonConfig = new Config();
			return;
		}
		
		configFile = new File(configPath);
		if (!configFile.exists()) {
			LOGGER.debug("Config file not exists, create new");
			commonConfig = new Config();
			return;
		}
		
		JsonReader jsonReader = null;

		try {
			jsonReader = new JsonReader(
					new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException excp) {
			LOGGER.debug("Config file read error: {}", excp);
			LOGGER.debug("Create new config in root folder");
			configFile = new File(defaultConfigPath);
			commonConfig = new Config();
			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation() // ������ ���� ������ � ���������� @Expose
			    .create();
		
		try {
			commonConfig = gson.fromJson(jsonReader, Config.class);
		} catch (Exception excp) {
			LOGGER.debug("error convert config from json");
			LOGGER.debug("Create new config in root folder");
			configFile = new File(defaultConfigPath);
			commonConfig = new Config();
			return;
		}

		if (commonConfig == null) {
			LOGGER.debug("config is null, after read json");
			LOGGER.debug("Create new config in root folder");
			configFile = new File(defaultConfigPath);
			commonConfig = new Config();
		}
		else {
			commonConfig.servers.forEach((server, config) -> {
				config.init();
			});
		}
		LOGGER.info("Config file read successfully");
	}
	
	public void saveKnownServers() {//String configPath) {
		
		LOGGER.info("Start save config from file <{}>", configFile.getAbsolutePath());
		
//		configFile = new File(configPath);

		JsonWriter jsonWriter;
		try {
			jsonWriter = new JsonWriter(
					new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException excp) {
			// ���� ����� �� ������������� ��������� � ������� �� ���������
			LOGGER.debug("Config file save error: {}", excp);
			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation() // ��������� ���� ������ � ���������� @Expose
			    .setPrettyPrinting() // ���� ��������� � ����������������-��������������� ����, �� �� ��������
			    .create();
		try {
			gson.toJson(commonConfig, commonConfig.getClass(), jsonWriter);
		} catch (JsonIOException excp) {
			LOGGER.debug("Config file save error: {}", excp);
		}
		
		try {
			jsonWriter.close();
		} catch (IOException excp) {
			LOGGER.debug("Config file save error: {}", excp);
		}
		
	}
	
	public Server CreateNewServer() {
		return commonConfig.CreateNewServer();
	}
	
	public void addNewServerInList(Server server) {
		commonConfig.servers.put(server.getServerKey(), server);
		saveKnownServers();
	}
	
	public void removeServerInList(Server server) {
		commonConfig.servers.remove(server.getServerKey(), server);
		saveKnownServers();
	}
	
	public Map<String, Server> getServers() {
		return commonConfig.servers;
	}
	
	public List<String> findNewServers() {
				
		List<String> addedServers = new ArrayList<>();
		
		
		return addedServers;
	}

	public void connectToServers() {
		
		commonConfig.connectAllServers();
		
//		clusterConfig.serversMap.forEach((server, config) -> {
//			config.connect(false);
//		});
		
	}

	public List<String> getConnectedServers() {
		
		List<String> connectedServers = new ArrayList<>();
		
		commonConfig.servers.forEach((server, config) -> {
			if (config.isConnected())
				connectedServers.add(config.getServerKey());
		});
		
		return connectedServers;
	}
	
	public void checkConnectToServers() {
		
		commonConfig.checkConnectionAllServers();
		
	}

	public List<IInfoBaseInfo> getInfobases(Server server){ // not used?
		
		List<IInfoBaseInfo> infobases = new ArrayList<>();

		if (server.isConnected()) {
			
			List<IClusterInfo> clusterInfoList = server.getClusters();
			
			UUID uuid = clusterInfoList.get(0).getClusterId();
			
			infobases = server.getInfoBases(uuid);
		
		}
		
		return infobases;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
