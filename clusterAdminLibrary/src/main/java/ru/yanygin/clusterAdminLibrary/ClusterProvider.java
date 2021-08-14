package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
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

import ru.yanygin.clusterAdminLibrary.Server;

public class ClusterProvider {
	
	File configFile;
	static Config commonConfig;
	String defaultConfigPath = ".\\config.json";

	Logger LOGGER = LoggerFactory.getLogger("ClusterProvider");

	public ClusterProvider() {
		
		
	}
	
	public static Config getCommonConfig() {
		return commonConfig;
	}

	public void readConfig(String configPath) {
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

		if (getCommonConfig() == null) {
			LOGGER.debug("config is null, after read json");
			LOGGER.debug("Create new config in root folder");
			configFile = new File(defaultConfigPath);
			commonConfig = new Config();
		}
		else {
			getCommonConfig().servers.forEach((server, config) -> {
				config.init();
			});
		}
		LOGGER.info("Config file read successfully");
	}
	
	public void saveConfig() {//String configPath) {
		
		LOGGER.info("Start save config to file <{}>", configFile.getAbsolutePath());
		
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
			gson.toJson(getCommonConfig(), getCommonConfig().getClass(), jsonWriter);
		} catch (JsonIOException excp) {
			LOGGER.debug("Config file save error: {}", excp);
		}
		
		try {
			jsonWriter.close();
		} catch (IOException excp) {
			LOGGER.debug("Config file save error: {}", excp);
		}
		LOGGER.info("Config file write successfully");
		
	}
	
	public Server createNewServer() {
		return getCommonConfig().CreateNewServer();
	}
	
	public void addNewServer(Server server) {
		getCommonConfig().servers.put(server.getServerKey(), server);
		saveConfig();
	}
	
	public void removeServer(Server server) {
		getCommonConfig().servers.remove(server.getServerKey(), server);
		saveConfig();
	}
	
	public Map<String, Server> getServers() {
		return getCommonConfig().servers;
	}
	
	public List<String> findNewServers() {
				
		List<String> addedServers = new ArrayList<>();
		
		
		return addedServers;
	}

	public void connectToServers() {
		
		getCommonConfig().connectAllServers();
		
//		clusterConfig.serversMap.forEach((server, config) -> {
//			config.connect(false);
//		});
		
	}

	public List<String> getConnectedServers() {
		
		List<String> connectedServers = new ArrayList<>();
		
		getCommonConfig().servers.forEach((server, config) -> {
			if (config.isConnected())
				connectedServers.add(config.getServerKey());
		});
		
		return connectedServers;
	}
	
	public void checkConnectToServers() {
		
		getCommonConfig().checkConnectionAllServers();
		
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
	
	public void close() {

		getServers().forEach((server, config) -> {
			if (config.isConnected())
				config.disconnectFromAgent();
		});

	}	
	
	public static Map<String, String> getInstalledV8Versions() {
		
		Map<String, String> versions = new HashMap<>();
		
		String v8x64CommonPath = "C:\\Program Files\\1cv8";
		String v8x86CommonPath = "C:\\Program Files (x86)\\1cv8";
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File f, String name) {
				return name.matches("8.3.\\d\\d.\\d{4}");
			}
		};
		
		File[] v8x64dirs = new File(v8x64CommonPath).listFiles(filter);
		File[] v8x86dirs = new File(v8x86CommonPath).listFiles(filter);
		
		for (File dir : v8x64dirs) {
			if (dir.isDirectory()) {
				File ras = new File(dir.getAbsolutePath().concat("\\bin\\ras.exe"));
				if (ras.exists() && ras.isFile())
					versions.put(dir.getName().concat(" (x64)"), ras.getAbsolutePath());
			}
		}
		return versions;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
