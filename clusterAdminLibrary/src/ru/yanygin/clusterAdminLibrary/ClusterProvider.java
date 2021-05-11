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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ClusterProvider {
	File configFile;
	Config commonConfig;

	Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary");

	public ClusterProvider() {
		
		
	}

	public void readSavedKnownServers(String configPath) {
		LOGGER.info("start read config from file <{}>", configPath);
		
		if (configPath.isBlank()) {
			LOGGER.debug("config is blank, create new");
			commonConfig = new Config();
			return;
		}
		
		configFile = new File(configPath);
		if (!configFile.exists()) {
			LOGGER.debug("config file not exists, create new");
			commonConfig = new Config();
			return;
		}
		
		JsonReader jsonReader = null;

		try {
			jsonReader = new JsonReader(
					new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException excp) {
			LOGGER.debug("read config error {}", excp);
//			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation()
			    .create();
		
		try {
			commonConfig = gson.fromJson(jsonReader, Config.class);
		} catch (Exception excp) {
			LOGGER.debug("error convert config from json");
//			return;
		}

		if (commonConfig == null) {
			LOGGER.debug("config is null, after read json. Create new");
			commonConfig = new Config();
		}
		else {
			commonConfig.servers.forEach((server, config) -> {
				LOGGER.debug("server {} start init", server);
				config.init();
//				if (config.autoconnect) {
//					config.connect(false);
//				}

			});
		}
		LOGGER.info("end read config");
	}
	
	public void saveKnownServers() {//String configPath) {
		
//		configFile = new File(configPath);

		JsonWriter jsonWriter;
		try {
			jsonWriter = new JsonWriter(
					new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation()
			    .setPrettyPrinting() // надо сохранять в человекочитаемом-форматированном виде, но не работает
			    .create();
		gson.toJson(commonConfig, commonConfig.getClass(), jsonWriter);
		
		try {
			jsonWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
