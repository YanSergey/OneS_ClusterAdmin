package ru.yanygin.clusterAdminLibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main config for applacation. */
public class Config {
  @SerializedName("Servers")
  @Expose
  private Map<String, Server> servers = new HashMap<>();

  @SerializedName("ExpandServers")
  @Expose
  private boolean expandServersTree;

  @SerializedName("ExpandClustersTree")
  @Expose
  private boolean expandClustersTree;

  @SerializedName("ExpandInfobasesTree")
  @Expose
  private boolean expandInfobasesTree;

  @SerializedName("ShowWorkingServersTree")
  @Expose
  private boolean showWorkingServersTree;

  @SerializedName("ExpandWorkingServersTree")
  @Expose
  private boolean expandWorkingServersTree;

  @SerializedName("ShowWorkingProcessesTree")
  @Expose
  private boolean showWorkingProcessesTree;

  @SerializedName("ExpandWorkingProcessesTree")
  @Expose
  private boolean expandWorkingProcessesTree;

  @SerializedName("ShowServerDescription")
  @Expose
  private boolean showServerDescription;

  @SerializedName("ShowServerVersion")
  @Expose
  private boolean showServerVersion;

  @SerializedName("ShowInfobaseDescription")
  @Expose
  private boolean showInfobaseDescription;

  @SerializedName("ShowLocalRasConnectInfo")
  @Expose
  private boolean showLocalRasConnectInfo;

  @SerializedName("Locale")
  @Expose
  private String locale;

  @SerializedName("SessionColumnProperties")
  @Expose
  private ColumnProperties sessionColumnProperties;

  @SerializedName("ConnectionColumnProperties")
  @Expose
  private ColumnProperties connectionColumnProperties;

  @SerializedName("LockColumnProperties")
  @Expose
  private ColumnProperties lockColumnProperties;

  @SerializedName("WPColumnProperties")
  @Expose
  private ColumnProperties wpColumnProperties;

  @SerializedName("WSColumnProperties")
  @Expose
  private ColumnProperties wsColumnProperties;

  @SerializedName("ShadowSleepSessions")
  @Expose
  private boolean shadowSleepSessions;

  @SerializedName("HighlightNewItems")
  @Expose
  private boolean highlightNewItems;

  @SerializedName("HighlightNewItemsDuration")
  @Expose
  private int highlightNewItemsDuration;

  @SerializedName("ReadClipboard")
  @Expose
  private boolean readClipboard;

  private static final Logger LOGGER =
      LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$

  private OsType currrentOs;

  private enum OsType {
    WINDOWS,
    MACOS,
    LINUX,
    OTHER
  }

  /**
   * Get servers.
   *
   * @return the servers
   */
  public Map<String, Server> getServers() {
    return servers;
  }

  /**
   * Expand servers in tree.
   *
   * @return the expandServersTree
   */
  public boolean isExpandServersTree() {
    return expandServersTree;
  }

  /**
   * Set expand servers in tree.
   *
   * @param expandServersTree the expandServersTree to set
   */
  public void setExpandServersTree(boolean expandServersTree) {
    this.expandServersTree = expandServersTree;
  }

  /**
   * Expand clusters in tree.
   *
   * @return the expandClustersTree
   */
  public boolean isExpandClustersTree() {
    return expandClustersTree;
  }

  /**
   * Set expand clusters in tree.
   *
   * @param expandClustersTree the expandClustersTree to set
   */
  public void setExpandClustersTree(boolean expandClustersTree) {
    this.expandClustersTree = expandClustersTree;
  }

  /**
   * Expand infobases in tree.
   *
   * @return the expandInfobasesTree
   */
  public boolean isExpandInfobasesTree() {
    return expandInfobasesTree;
  }

  /**
   * Set expand infobases in tree.
   *
   * @param expandInfobasesTree the expandInfobasesTree to set
   */
  public void setExpandInfobasesTree(boolean expandInfobasesTree) {
    this.expandInfobasesTree = expandInfobasesTree;
  }

  /**
   * Show working servers in tree.
   *
   * @return the showWorkingServersTree
   */
  public boolean isShowWorkingServersTree() {
    return showWorkingServersTree;
  }

  /**
   * Set show working servers in tree.
   *
   * @param showWorkingServersTree the showWorkingServersTree to set
   */
  public void setShowWorkingServersTree(boolean showWorkingServersTree) {
    this.showWorkingServersTree = showWorkingServersTree;
  }

  /**
   * Expand working servers in tree.
   *
   * @return the expandWorkingServersTree
   */
  public boolean isExpandWorkingServersTree() {
    return expandWorkingServersTree;
  }

  /**
   * Set expand working servers in tree.
   *
   * @param expandWorkingServersTree the expandWorkingServersTree to set
   */
  public void setExpandWorkingServersTree(boolean expandWorkingServersTree) {
    this.expandWorkingServersTree = expandWorkingServersTree;
  }

  /**
   * Show working processes in tree.
   *
   * @return the showWorkingProcessesTree
   */
  public boolean isShowWorkingProcessesTree() {
    return showWorkingProcessesTree;
  }

  /**
   * Set show working processes in tree.
   *
   * @param showWorkingProcessesTree the showWorkingProcessesTree to set
   */
  public void setShowWorkingProcessesTree(boolean showWorkingProcessesTree) {
    this.showWorkingProcessesTree = showWorkingProcessesTree;
  }

  /**
   * Expand working processes in tree.
   *
   * @return the expandWorkingProcessesTree
   */
  public boolean isExpandWorkingProcessesTree() {
    return expandWorkingProcessesTree;
  }

  /**
   * Set expand working processes in tree.
   *
   * @param expandWorkingProcessesTree the expandWorkingProcessesTree to set
   */
  public void setExpandWorkingProcessesTree(boolean expandWorkingProcessesTree) {
    this.expandWorkingProcessesTree = expandWorkingProcessesTree;
  }

  /**
   * Show server description in tree.
   *
   * @return the showServerDescription
   */
  public boolean isShowServerDescription() {
    return showServerDescription;
  }

  /**
   * Set show server description in tree.
   *
   * @param showServerDescription the showServerDescription to set
   */
  public void setShowServerDescription(boolean showServerDescription) {
    this.showServerDescription = showServerDescription;
  }

  /**
   * Show server version in tree.
   *
   * @return the showServerVersion
   */
  public boolean isShowServerVersion() {
    return showServerVersion;
  }

  /**
   * Set show server version in tree.
   *
   * @param showServerVersion the showServerVersion to set
   */
  public void setShowServerVersion(boolean showServerVersion) {
    this.showServerVersion = showServerVersion;
  }

  /**
   * Show infobase description in tree.
   *
   * @return the showInfobaseDescription
   */
  public boolean isShowInfobaseDescription() {
    return showInfobaseDescription;
  }

  /**
   * Set show infobase description in tree.
   *
   * @param showInfobaseDescription the showInfobaseDescription to set
   */
  public void setShowInfobaseDescription(boolean showInfobaseDescription) {
    this.showInfobaseDescription = showInfobaseDescription;
  }

  /**
   * Show local-RAS connection info in tree.
   *
   * @return the showLocalRasConnectInfo
   */
  public boolean isShowLocalRasConnectInfo() {
    return showLocalRasConnectInfo;
  }

  /**
   * Set show local-RAS connection info in tree.
   *
   * @param showLocalRasConnectInfo the showLocalRasConnectInfo to set
   */
  public void setShowLocalRasConnectInfo(boolean showLocalRasConnectInfo) {
    this.showLocalRasConnectInfo = showLocalRasConnectInfo;
  }

  /**
   * Get locale.
   *
   * @return the locale
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Set locale.
   *
   * @param locale the locale to set
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * Get session column properties.
   *
   * @return the sessionColumnProperties
   */
  public ColumnProperties getSessionColumnProperties() {
    return sessionColumnProperties;
  }

  /**
   * Get connection column properties.
   *
   * @return the connectionColumnProperties
   */
  public ColumnProperties getConnectionColumnProperties() {
    return connectionColumnProperties;
  }

  /**
   * Get lock column properties.
   *
   * @return the lockColumnProperties
   */
  public ColumnProperties getLockColumnProperties() {
    return lockColumnProperties;
  }

  /**
   * Get working processes column properties.
   *
   * @return the wpColumnProperties
   */
  public ColumnProperties getWpColumnProperties() {
    return wpColumnProperties;
  }

  /**
   * Get working servers column properties.
   *
   * @return the wsColumnProperties
   */
  public ColumnProperties getWsColumnProperties() {
    return wsColumnProperties;
  }

  /**
   * Shadow sleep sessions.
   *
   * @return the shadowSleepSessions
   */
  public boolean isShadowSleepSessions() {
    return shadowSleepSessions;
  }

  /**
   * Set shadow sleep sessions.
   *
   * @param shadowSleepSessions the shadowSleepSessions to set
   */
  public void setShadowSleepSessions(boolean shadowSleepSessions) {
    this.shadowSleepSessions = shadowSleepSessions;
  }

  /**
   * Highlight new items in lists.
   *
   * @return the highlightNewItems
   */
  public boolean isHighlightNewItems() {
    return highlightNewItems;
  }

  /**
   * Set highlight new items in lists.
   *
   * @param highlightNewItems the highlightNewItems to set
   */
  public void setHighlightNewItems(boolean highlightNewItems) {
    this.highlightNewItems = highlightNewItems;
  }

  /**
   * Highlight new items duration.
   *
   * @return the highlightNewItemsDuration
   */
  public int getHighlightNewItemsDuration() {
    return highlightNewItemsDuration;
  }

  /**
   * Set highlight new items duration.
   *
   * @param highlightNewItemsDuration the highlightNewItemsDuration to set
   */
  public void setHighlightNewItemsDuration(int highlightNewItemsDuration) {
    this.highlightNewItemsDuration = highlightNewItemsDuration;
  }

  /**
   * Read clipboard.
   *
   * @return the readClipboard
   */
  public boolean isReadClipboard() {
    return readClipboard;
  }

  /**
   * Set read clipboard.
   *
   * @param readClipboard the readClipboard to set
   */
  public void setReadClipboard(boolean readClipboard) {
    this.readClipboard = readClipboard;
  }

  /** Constructor for main config. */
  public Config() {
    this.init();
  }

  /** Init config. */
  public void init() {
    getOperatingSystemType();

    this.servers.forEach(
        (key, server) -> {
          server.init();
        });
  }

  private void getOperatingSystemType() {
    if (currrentOs == null) {
      String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      LOGGER.debug("Current OS is <{}>", osName); //$NON-NLS-1$

      if ((osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0)) {
        currrentOs = OsType.MACOS;
      } else if (osName.indexOf("win") >= 0) {
        currrentOs = OsType.WINDOWS;
      } else if (osName.indexOf("nux") >= 0) {
        currrentOs = OsType.LINUX;
      } else {
        currrentOs = OsType.OTHER;
      }
    }
  }

  /**
   * Add new servers in main config.
   *
   * @param servers - list of servers
   * @return list of servers
   */
  public List<String> addNewServers(List<String> servers) {
    // Пакетное добавление серверов в список, предполагается для механизма импорта из списка
    // информационных баз

    List<String> addedServers = new ArrayList<>();

    // Имя сервера, которое приходит сюда не равно Представлению сервера, выводимому в списке
    // Имя сервера. оно же Key в map и json, строка вида Server:1541, с обязательным указанием порта
    // менеджера, к которому подключаемся
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

  /** Connect to all servers in silent mode. */
  public void connectAllServers() {
    servers.forEach((serverKey, server) -> server.connectToServer(false, true));
  }

  /** Check connection to all servers in silent mode. */
  public void checkConnectionAllServers() {
    servers.forEach((serverKey, server) -> server.connectToServer(true, true));
  }

  /**
   * Is Windows.
   *
   * @return true is Windows
   */
  public boolean isWindows() {
    return currrentOs == OsType.WINDOWS;
  }

  /**
   * Is Linux.
   *
   * @return true is Linux
   */
  public boolean isLinux() {
    return currrentOs == OsType.LINUX;
  }

  /**
   * Is MacOs.
   *
   * @return true is MacOs
   */
  public boolean isMacOs() {
    return currrentOs == OsType.MACOS;
  }

  /**
   * Set sessions column order.
   *
   * @param columnOrder - new column order
   */
  public void setSessionsColumnOrder(int[] columnOrder) {
    sessionColumnProperties.setOrder(columnOrder);
  }

  /**
   * Set connection column order.
   *
   * @param columnOrder - new column order
   */
  public void setConnectionsColumnOrder(int[] columnOrder) {
    connectionColumnProperties.setOrder(columnOrder);
  }

  /**
   * Set lock column order.
   *
   * @param columnOrder - new column order
   */
  public void setLocksColumnOrder(int[] columnOrder) {
    lockColumnProperties.setOrder(columnOrder);
  }

  /**
   * Set working processes column order.
   *
   * @param columnOrder - new column order
   */
  public void setWorkingProcessesColumnOrder(int[] columnOrder) {
    wpColumnProperties.setOrder(columnOrder);
  }

  /**
   * Set working servers column order.
   *
   * @param columnOrder - new column order
   */
  public void setWorkingServersColumnOrder(int[] columnOrder) {
    wsColumnProperties.setOrder(columnOrder);
  }

  /**
   * Init sessions column count.
   *
   * @param columnCount - new column count
   */
  public void initSessionsColumnCount(int columnCount) {

    if (sessionColumnProperties == null) {
      sessionColumnProperties = new ColumnProperties(columnCount);
    } else {
      sessionColumnProperties.updateColumnProperties(columnCount);
    }
  }

  /**
   * Init connection column count.
   *
   * @param columnCount - new column count
   */
  public void initConnectionsColumnCount(int columnCount) {

    if (connectionColumnProperties == null) {
      connectionColumnProperties = new ColumnProperties(columnCount);
    } else {
      connectionColumnProperties.updateColumnProperties(columnCount);
    }
  }

  /**
   * Init lock column count.
   *
   * @param columnCount - new column count
   */
  public void initLocksColumnCount(int columnCount) {

    if (lockColumnProperties == null) {
      lockColumnProperties = new ColumnProperties(columnCount);
    } else {
      lockColumnProperties.updateColumnProperties(columnCount);
    }
  }

  /**
   * Init working processes column count.
   *
   * @param columnCount - new column count
   */
  public void initWorkingProcessesColumnCount(int columnCount) {

    if (wpColumnProperties == null) {
      wpColumnProperties = new ColumnProperties(columnCount);
    } else {
      wpColumnProperties.updateColumnProperties(columnCount);
    }
  }

  /**
   * Init working servers column count.
   *
   * @param columnCount - new column count
   */
  public void initWorkingServersColumnCount(int columnCount) {

    if (wsColumnProperties == null) {
      wsColumnProperties = new ColumnProperties(columnCount);
    } else {
      wsColumnProperties.updateColumnProperties(columnCount);
    }
  }

  /**
   * Set sessions column width.
   *
   * @param index - index of column
   * @param width - column width
   */
  public void setSessionsColumnWidth(int index, int width) {
    sessionColumnProperties.setWidth(index, width);
  }

  /**
   * Set connection column width.
   *
   * @param index - index of column
   * @param width - column width
   */
  public void setConnectionsColumnWidth(int index, int width) {
    connectionColumnProperties.setWidth(index, width);
  }

  /**
   * Set lock column width.
   *
   * @param index - index of column
   * @param width - column width
   */
  public void setLocksColumnWidth(int index, int width) {
    lockColumnProperties.setWidth(index, width);
  }

  /**
   * Set working processes column width.
   *
   * @param index - index of column
   * @param width - column width
   */
  public void setWorkingProcessesColumnWidth(int index, int width) {
    wpColumnProperties.setWidth(index, width);
  }

  /**
   * Set working servers column width.
   *
   * @param index - index of column
   * @param width - column width
   */
  public void setWorkingServersColumnWidth(int index, int width) {
    wsColumnProperties.setWidth(index, width);
  }
}
