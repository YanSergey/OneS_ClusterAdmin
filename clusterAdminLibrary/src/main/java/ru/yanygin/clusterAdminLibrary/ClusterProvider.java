package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterProvider {

  File configFile;
  static Config commonConfig;
  private static final String DEFAULT_CONFIG_PATH = "config.json"; //$NON-NLS-1$
  private static final String TEMP_CONFIG_PATH = "config_temp.json"; //$NON-NLS-1$

  private static final Logger LOGGER = LoggerFactory.getLogger("ClusterProvider"); //$NON-NLS-1$

  public ClusterProvider() {}

  //  public static Config getCommonConfig() {
  //    return commonConfig;
  //  }

  /** Читает конфиг из файла по-умолчанию. */
  //  public void readConfig() {
  //    readConfig(DEFAULT_CONFIG_PATH);
  //  }

  /**
   * Читает конфиг из определенного файла.
   *
   * @param configPath - имя конфиг файла
   */
  //  public void readConfig(String configPath) {
  //    LOGGER.info("Start read config from file <{}>", configPath); //$NON-NLS-1$
  //
  //    if (configPath.isBlank()) {
  //      LOGGER.debug("Config path is empty, create new config in root folder"); //$NON-NLS-1$
  //      commonConfig = new Config();
  //      return;
  //    }
  //
  //    configFile = new File(configPath);
  //    if (!configFile.exists()) {
  //      LOGGER.debug("Config file not exists, create new"); //$NON-NLS-1$
  //      commonConfig = new Config();
  //      return;
  //    }
  //
  //    JsonReader jsonReader = null;
  //
  //    try {
  //      jsonReader =
  //          new JsonReader(
  //              new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
  //    } catch (FileNotFoundException excp) {
  //      LOGGER.debug("Config file read error:", excp); //$NON-NLS-1$
  //      LOGGER.debug("Create new config in root folder"); //$NON-NLS-1$
  //      configFile = new File(TEMP_CONFIG_PATH);
  //      commonConfig = new Config();
  //      return;
  //    }
  //    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  //
  //    try {
  //      commonConfig = gson.fromJson(jsonReader, Config.class);
  //      commonConfig.migrateProps();
  //    } catch (Exception excp) {
  //      LOGGER.debug("error convert config from json:", excp); //$NON-NLS-1$
  //      LOGGER.debug("Create new config in root folder"); //$NON-NLS-1$
  //      configFile = new File(TEMP_CONFIG_PATH);
  //      commonConfig = new Config();
  //      return;
  //    }
  //
  //    if (commonConfig == null) {
  //      LOGGER.debug("config is null, after read json"); //$NON-NLS-1$
  //      LOGGER.debug("Create new config in root folder"); //$NON-NLS-1$
  //      configFile = new File(TEMP_CONFIG_PATH);
  //      commonConfig = new Config();
  //    } else {
  //
  //      commonConfig.init();
  //      if (commonConfig.getLocale() != null) {
  //        LOGGER.debug("Set locale is <{}>", commonConfig.getLocale()); //$NON-NLS-1$
  //        Locale locale = Locale.forLanguageTag(commonConfig.getLocale());
  //        java.util.Locale.setDefault(locale);
  //        Messages.reloadBundle(locale); // TODO не совсем понятно как работает
  //      }
  //    }
  //    LOGGER.info("Config file read successfully"); //$NON-NLS-1$
  //  }
  //
  //  public void saveConfig() {
  //
  //    LOGGER.info("Start save config to file <{}>", configFile.getAbsolutePath()); //$NON-NLS-1$
  //
  //    // configFile = new File(configPath);
  //
  //    JsonWriter jsonWriter;
  //    try {
  //      jsonWriter =
  //          new JsonWriter(
  //              new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
  //    } catch (FileNotFoundException excp) {
  //      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
  //      return;
  //    }
  //    Gson gson =
  //        new GsonBuilder()
  //            .excludeFieldsWithoutExposeAnnotation()
  //            .setPrettyPrinting()
  //            .create();
  //    try {
  //      gson.toJson(getCommonConfig(), getCommonConfig().getClass(), jsonWriter);
  //    } catch (JsonIOException excp) {
  //      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
  //    }
  //
  //    try {
  //      jsonWriter.close();
  //    } catch (IOException excp) {
  //      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
  //    }
  //    LOGGER.info("Config file write successfully"); //$NON-NLS-1$
  //  }

  //  public Server createNewServer() {
  //    Server newServer = null;
  //
  //    if (commonConfig.isReadClipboard()) {
  //      Clipboard clipboard = new Clipboard(Display.getDefault());
  //      String clip = (String) clipboard.getContents(TextTransfer.getInstance());
  //      clipboard.dispose();
  //
  //      if (clip != null && clip.startsWith("Srvr=")) { //$NON-NLS-1$
  //        String[] srvrPart = clip.split(";"); //$NON-NLS-1$
  //        String srvr = srvrPart[0].substring(6, srvrPart[0].length() - 1);
  //        return new Server(srvr);
  //      }
  //    }
  //
  //    return new Server("Server:1541"); //$NON-NLS-1$
  //  }

  //  public void addNewServer(Server server) {
  //    commonConfig.getServers().put(server.getServerKey(), server);
  //    saveConfig();
  //  }
  //
  //  public void removeServer(Server server) {
  //    getCommonConfig().getServers().remove(server.getServerKey(), server);
  //    saveConfig();
  //  }
  //
  //  public Map<String, Server> getServers() {
  //    return getCommonConfig().getServers();
  //  }

  public List<String> findNewServers() {

    List<String> addedServers = new ArrayList<>();

    return addedServers;
  }

  //  public void connectToServers() {
  //
  //    getCommonConfig().connectAllServers();
  //  }

  //  public List<String> getConnectedServers() {
  //
  //    List<String> connectedServers = new ArrayList<>();
  //
  //    getCommonConfig()
  //        .getServers()
  //        .forEach(
  //            (server, config) -> {
  //              if (config.isConnected()) {
  //                connectedServers.add(config.getServerKey());
  //              }
  //            });
  //
  //    return connectedServers;
  //  }

  //  public void checkConnectToServers() {
  //
  //    getCommonConfig().checkConnectionAllServers();
  //  }

  //  public void close() {
  //
  //    saveConfig();
  //
  //    getServers()
  //        .forEach(
  //            (server, config) -> {
  //              if (config.isConnected()) {
  //                config.disconnectFromAgent();
  //              }
  //            });
  //  }
  //
  //  public static Map<String, String> getInstalledV8Versions() {
  //    LOGGER.debug("Get installed v8 platform versions"); //$NON-NLS-1$
  //
  //    Map<String, String> versions = new HashMap<>();
  //
  //    if (!commonConfig.isWindows()) {
  //      return versions;
  //    }
  //
  //    File v8x64CommonPath = new File("C:\\Program Files\\1cv8"); //$NON-NLS-1$
  //    File v8x86CommonPath = new File("C:\\Program Files (x86)\\1cv8"); //$NON-NLS-1$
  //
  //    FilenameFilter filter =
  //        new FilenameFilter() {
  //          @Override
  //          public boolean accept(File f, String name) {
  //            return name.matches("8.3.\\d\\d.\\d{4}"); //$NON-NLS-1$
  //          }
  //        };
  //
  //    try {
  //      if (v8x64CommonPath.exists()) {
  //        File[] v8x64dirs = v8x64CommonPath.listFiles(filter);
  //        for (File dir : v8x64dirs) {
  //          if (dir.isDirectory()) {
  //            File ras = new File(dir.getAbsolutePath().concat("\\bin\\ras.exe")); //$NON-NLS-1$
  //            if (ras.exists() && ras.isFile()) {
  //              versions.put(dir.getName().concat(" (x86_64)"), ras.getAbsolutePath());
  // //$NON-NLS-1$
  //            }
  //          }
  //        }
  //      }
  //    } catch (Exception excp) {
  //      LOGGER.error("Error read dir <{}>", v8x64CommonPath.getAbsolutePath(), excp);
  // //$NON-NLS-1$
  //    }
  //
  //    try {
  //      if (v8x86CommonPath.exists()) {
  //        File[] v8x86dirs = v8x86CommonPath.listFiles(filter);
  //        for (File dir : v8x86dirs) {
  //          if (dir.isDirectory()) {
  //            File ras = new File(dir.getAbsolutePath().concat("\\bin\\ras.exe")); //$NON-NLS-1$
  //            if (ras.exists() && ras.isFile()) {
  //              versions.put(dir.getName(), ras.getAbsolutePath()); //$NON-NLS-1$
  //            }
  //          }
  //        }
  //      }
  //    } catch (Exception excp) {
  //      LOGGER.error("Error read dir <{}>", v8x64CommonPath.getAbsolutePath(), excp);
  // //$NON-NLS-1$
  //    }
  //
  //    return versions;
  //  }
}
