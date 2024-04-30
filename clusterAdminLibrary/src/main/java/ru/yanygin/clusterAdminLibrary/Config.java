package ru.yanygin.clusterAdminLibrary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.module.ModuleDescriptor.Version;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.client.fluent.Request;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONObject;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibrary.ColumnProperties.RowSortDirection;
import ru.yanygin.clusterAdminLibrary.InfoBaseInfoShortExt.InfobasesSortDirection;

/** Класс с конфигурацией приложения. */
public class Config {

  @SerializedName("ConfigVersion")
  @Expose
  private String configVersion;

  @SerializedName("CheckingUpdate")
  @Expose
  private boolean checkingUpdate = false;

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
  private String locale = null; // null = как в системе

  @SerializedName("ShadeSleepingSessions")
  @Expose
  private boolean shadeSleepingSessions;

  @SerializedName("HighlightNewItems")
  @Expose
  private boolean highlightNewItems;

  @SerializedName("HighlightNewItemsDuration")
  @Expose
  private int highlightNewItemsDuration;

  @SerializedName("ReadClipboard")
  @Expose
  private boolean readClipboard;

  @SerializedName("RowSortDirection")
  @Expose
  private RowSortDirection rowSortDirection = RowSortDirection.DISABLE;

  @SerializedName("InfobasesSortDirection")
  @Expose
  private InfobasesSortDirection infobasesSortDirection = InfobasesSortDirection.DISABLE;

  @SerializedName("ListRefreshRate")
  @Expose
  private int listRefreshRate = 5000;

  @SerializedName("LoggerLevel")
  @Expose
  private String loggerLevel = "error";

  @SerializedName("RequestLogon")
  @Expose
  private boolean requestLogon = false;

  @SerializedName("Servers")
  @Expose
  private Map<String, Server> servers = new HashMap<>();

  @SerializedName("SessionColumnProperties")
  @Expose
  private ColumnProperties sessionColumnProperties = new ColumnProperties(0);

  @SerializedName("ConnectionColumnProperties")
  @Expose
  private ColumnProperties connectionColumnProperties = new ColumnProperties(0);

  @SerializedName("LockColumnProperties")
  @Expose
  private ColumnProperties lockColumnProperties = new ColumnProperties(0);

  @SerializedName("WPColumnProperties")
  @Expose
  private ColumnProperties wpColumnProperties = new ColumnProperties(0);

  @SerializedName("WSColumnProperties")
  @Expose
  private ColumnProperties wsColumnProperties = new ColumnProperties(0);

  private static final Logger LOGGER =
      (Logger) LoggerFactory.getLogger(Config.class.getSimpleName());

  private static final Logger ROOT_LOGGER =
      (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

  private static final String DEFAULT_CONFIG_PATH = "config.json"; // $NON-NLS-1$
  private static final String TEMP_CONFIG_PATH = "config_temp.json"; //$NON-NLS-1$

  public static Config currentConfig;

  private OsType currentOs = getOperatingSystemType();
  private Version currentVersion = readCurrentVersion();
  private Version latestVersion;
  private String latestVersionUrl;
  //  private File configFile;
  private String configPath;

  private enum OsType {
    WINDOWS,
    MACOS,
    LINUX,
    OTHER
  }

  /** Constructor for main config. */
  public Config() {
    // this.init();
    this.configPath = DEFAULT_CONFIG_PATH;
    currentConfig = this;
  }

  /**
   * Constructor for config.
   *
   * @param configPath - путь к файлу конфига
   */
  public Config(String configPath) {
    this.configPath = configPath;
    currentConfig = this;
  }

  /**
   * Constructor for main config.
   *
   * @param initFields - init fields
   */
  //  public Config(boolean initFields) {
  //    if (initFields) {
  //      this.init();
  //    }
  //  }

  /** Init config. */
  public void init() {
    runReadUpstreamVersion();
  }

  /** Миграция настроек из конфига предыдущей версии. */
  public void migrateProps() {

    if (configVersion == null) {
      configVersion = "0.2.0";
      servers.forEach((key, server) -> server.migrateProps(configVersion));
      configVersion = currentVersion.toString();
    }
  }

  private OsType getOperatingSystemType() {
    OsType os = null;
    String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    LOGGER.debug("Current OS is <{}>", osName); //$NON-NLS-1$

    if ((osName.indexOf("mac") >= 0) || (osName.indexOf("darwin") >= 0)) {
      os = OsType.MACOS;
    } else if (osName.indexOf("win") >= 0) {
      os = OsType.WINDOWS;
    } else if (osName.indexOf("nux") >= 0) {
      os = OsType.LINUX;
    } else {
      os = OsType.OTHER;
    }
    return os;
  }

  private Version readCurrentVersion() {

    //    MavenXpp3Reader reader = new MavenXpp3Reader();
    //    Model model;
    //
    //    if ((new File("pom.xml")).exists()) {
    //      try {
    //        model = reader.read(new FileReader("pom.xml"));
    //        // model =
    // reader.read(getClass().getResourceAsStream("/META-INF/maven/".concat("pom.xml")));
    //        return Version.parse(model.getVersion());
    //
    //      } catch (IOException | XmlPullParserException e) {
    //        LOGGER.debug("Error parse current version"); //$NON-NLS-1$
    //      }
    //    } else {
    //      LOGGER.debug("Pom-file not exist"); //$NON-NLS-1$
    //      String v = ClusterAdminLibraryMain.class.getPackage().getImplementationVersion();
    //      LOGGER.debug("PackageImplementationVersion {}", v); //$NON-NLS-1$
    //      return Version.parse(v);
    //    }

    return Version.parse("0.4.0.beta1");
  }

  private void runReadUpstreamVersion() {

    if (!checkingUpdate) {
      return;
    }

    Display.getDefault()
        .asyncExec(
            new Runnable() {

              @Override
              public void run() {
                readUpstreamVersion();
              }
            });
  }

  /** Узнать последнюю версию из релизов GitHub. */
  public void readUpstreamVersion() {

    URL url;
    HttpURLConnection conn;
    try {
      url = new URL("https://api.github.com/repos/YanSergey/OneS_ClusterAdmin/releases/latest");
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
    } catch (IOException e) {
      LOGGER.debug("Error getting a upstream version", e); //$NON-NLS-1$
      checkingUpdate = false;
      return;
    }

    final int connectionTimeout = 10000;

    conn.setRequestProperty("Content-Type", "application/json");
    conn.setConnectTimeout(connectionTimeout);
    conn.setReadTimeout(connectionTimeout);

    StringBuilder result = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      for (String line; (line = reader.readLine()) != null; ) {
        result.append(line);
      }
    } catch (IOException e) {
      LOGGER.debug("Error read github response", e); //$NON-NLS-1$
      checkingUpdate = false;
      return;
    }

    String versionsJsonString = result.toString();

    // Считываем json
    JSONObject jo = new JSONObject(versionsJsonString);
    String lastTagName = jo.getString("tag_name");
    JSONArray assets = jo.getJSONArray("assets");

    String currentOsString = "";
    switch (currentOs) {
      case WINDOWS:
        currentOsString = "windows";
        break;
      case LINUX:
        currentOsString = "linux";
        break;
      case MACOS:
        currentOsString = "macos";
        break;
      default:
        LOGGER.debug("Error get current OS"); //$NON-NLS-1$
        checkingUpdate = false;
        return;
    }

    String downloadUrl;
    for (Object object : assets) {
      downloadUrl = ((JSONObject) object).getString("browser_download_url");
      if (downloadUrl.contains(currentOsString)) {
        latestVersionUrl = downloadUrl;
        break;
      }
    }
    latestVersion = Version.parse(lastTagName);
  }

  /**
   * Запуск скачивания нового релиза.
   *
   * @param parentShell - parent shell
   */
  public void runDownloadRelease(Shell parentShell) {
    Display.getDefault()
        .asyncExec(
            new Runnable() {

              @Override
              public void run() {
                String fname = selectFileToSave(parentShell);
                if (fname == null) {
                  return;
                }

                if (downloadReleaseToFile(fname)) {
                  showDownloadedFile(fname);
                }
              }
            });
  }

  private String selectFileToSave(Shell parentShell) {

    String[] filterNames = {"Исполняемые файлы Java (*.jar)"};
    String[] filterExt = {"*.jar"};

    FileDialog dialog = new FileDialog(parentShell, SWT.SAVE);
    dialog.setFileName(new File(latestVersionUrl).getName());
    dialog.setText("Укажите расположение и имя файла");
    dialog.setFilterNames(filterNames);
    dialog.setFilterExtensions(filterExt);

    return dialog.open();
  }

  private boolean downloadReleaseToFile(String fname) {

    try {
      Request.Get(latestVersionUrl).execute().saveContent(new File(fname));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private void showDownloadedFile(String fname) {
    if (Config.currentConfig.isWindows()) {

      final String command = "explorer.exe /select,\"" + new File(fname).getAbsolutePath() + "\"";
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {

      Desktop desktop = Desktop.getDesktop();
      desktop.browseFileDirectory(new File(fname));
    }
  }

  /**
   * Создание нового сервера.
   *
   * @return новый сервер
   */
  public Server createNewServer() {
    Server newServer = null;

    if (isReadClipboard()) {
      Clipboard clipboard = new Clipboard(Display.getDefault());
      String clip = (String) clipboard.getContents(TextTransfer.getInstance());
      clipboard.dispose();

      if (clip != null && clip.startsWith("Srvr=")) { //$NON-NLS-1$
        String[] srvrPart = clip.split(";"); //$NON-NLS-1$
        String srvr = srvrPart[0].substring(6, srvrPart[0].length() - 1);
        newServer = new Server(srvr);
      } else {
        newServer = new Server("Server:1541"); //$NON-NLS-1$
      }
    } else {
      newServer = new Server("Server:1541"); //$NON-NLS-1$
    }
    // servers.put(newServer.getServerKey(), newServer);
    // TODO по идее еще рано добавлять в список серверов эту заготовку
    return newServer;
  }

  public void addNewServer(Server server) {
    servers.put(server.getServerKey(), server);
    saveConfig();
  }

  public void removeServer(Server server) {
    servers.remove(server.getServerKey(), server);
    saveConfig();
  }

  public void close() {

    saveConfig();

    servers.forEach(
        (serverKey, server) -> {
          if (server.isConnected()) {
            server.disconnectFromAgent();
          }
        });
  }

  /**
   * Добавление новых серверов в конфиг.
   *
   * @param newServers - список новых серверов
   * @return список серверов, которые были реально добавлены
   */
  public List<String> addNewServers(List<String> newServers) {
    // Пакетное добавление серверов в список, предполагается для механизма импорта из списка
    // информационных баз

    List<String> addedServers = new ArrayList<>();

    // Имя сервера, которое приходит сюда не равно Представлению сервера, выводимому в списке
    // Имя сервера. оно же Key в map и json, строка вида Server:1541, с обязательным указанием порта
    // менеджера, к которому подключаемся
    // если порт менеджера не задан - ставим стандартный 1541
    // переделать
    for (String serverName : newServers) {
      if (!servers.containsKey(serverName)) {
        Server serverConfig = new Server(serverName);
        servers.put(serverName, serverConfig);

        addedServers.add(serverName);
      }
    }

    return addedServers;
  }

  /** Подключиться ко всем серверам в тихом режиме. */
  public void connectAllServers() {
    servers.forEach((serverKey, server) -> server.connectToServer(false, true));
  }

  /** Проверить доступность всех серверов в тихом режиме. */
  public void checkConnectionAllServers() {
    servers.forEach((serverKey, server) -> server.connectToServer(true, true));
  }

  /**
   * Проверять обновление при запуске.
   *
   * @return текущее значение
   */
  public boolean checkingUpdate() {
    return checkingUpdate;
  }

  /**
   * Установка проверки обновления при запуске.
   *
   * @param checkingUpdate - новое значение
   */
  public void setCheckingUpdate(boolean checkingUpdate) {
    this.checkingUpdate = checkingUpdate;
  }

  /**
   * Получает список зарегистрированных серверов.
   *
   * @return список серверов
   */
  public Map<String, Server> getServers() {
    return servers;
  }

  /**
   * При подключении разворачивать узел сервера в дереве.
   *
   * @return значение разворачивать/не разворачивать
   */
  public boolean isExpandServersTree() {
    return expandServersTree;
  }

  /**
   * Установить разворачивание узла сервера в дереве при подключении.
   *
   * @param expand - true=разворачивать, false=не разворачивать
   */
  public void setExpandServersTree(boolean expand) {
    this.expandServersTree = expand;
  }

  /**
   * При подключении разворачивать узел кластера в дереве.
   *
   * @return значение разворачивать/не разворачивать
   */
  public boolean isExpandClustersTree() {
    return expandClustersTree;
  }

  /**
   * Установить разворачивание узла кластеров в дереве при подключении сервера.
   *
   * @param expand - true=разворачивать, false=не разворачивать
   */
  public void setExpandClustersTree(boolean expand) {
    this.expandClustersTree = expand;
  }

  /**
   * При подключении разворачивать узел инфобаз в дереве.
   *
   * @return значение разворачивать/не разворачивать
   */
  public boolean isExpandInfobasesTree() {
    return expandInfobasesTree;
  }

  /**
   * Установить разворачивание узла инфобаз в дереве при подключении сервера.
   *
   * @param expand - true=разворачивать, false=не разворачивать
   */
  public void setExpandInfobasesTree(boolean expand) {
    this.expandInfobasesTree = expand;
  }

  /**
   * Показывать узел рабочих серверов в дереве.
   *
   * @return значение
   */
  public boolean isShowWorkingServersTree() {
    return showWorkingServersTree;
  }

  /**
   * Установить показ узла рабочих серверов в дереве.
   *
   * @param show - значение показывать/не показывать
   */
  public void setShowWorkingServersTree(boolean show) {
    this.showWorkingServersTree = show;
  }

  /**
   * При подключении разворачивать узел рабочих серверов в дереве.
   *
   * @return значение разворачивать/не разворачивать
   */
  public boolean isExpandWorkingServersTree() {
    return expandWorkingServersTree;
  }

  /**
   * Установить разворачивание узла рабочих серверов в дереве при подключении сервера.
   *
   * @param expand - true=разворачивать, false=не разворачивать
   */
  public void setExpandWorkingServersTree(boolean expand) {
    this.expandWorkingServersTree = expand;
  }

  /**
   * Показывать узел рабочих процессов в дереве.
   *
   * @return значение
   */
  public boolean isShowWorkingProcessesTree() {
    return showWorkingProcessesTree;
  }

  /**
   * Установить показ узла рабочих процессов в дереве.
   *
   * @param show - значение показывать/не показывать
   */
  public void setShowWorkingProcessesTree(boolean show) {
    this.showWorkingProcessesTree = show;
  }

  /**
   * При подключении разворачивать узел рабочих процессов в дереве.
   *
   * @return the значение разворачивать/не разворачивать
   */
  public boolean isExpandWorkingProcessesTree() {
    return expandWorkingProcessesTree;
  }

  /**
   * Установить разворачивание узла рабочих процессов в дереве при подключении сервера.
   *
   * @param expand - true=разворачивать, false=не разворачивать
   */
  public void setExpandWorkingProcessesTree(boolean expand) {
    this.expandWorkingProcessesTree = expand;
  }

  /**
   * Показывать описание сервера в дереве.
   *
   * @return значение
   */
  public boolean isShowServerDescription() {
    return showServerDescription;
  }

  /**
   * Установить показ описания сервера в дереве.
   *
   * @param show значение
   */
  public void setShowServerDescription(boolean show) {
    this.showServerDescription = show;
  }

  /**
   * Показывать версию сервера в дереве.
   *
   * @return значение
   */
  public boolean isShowServerVersion() {
    return showServerVersion;
  }

  /**
   * Установить показ версии сервера в дереве.
   *
   * @param show значение
   */
  public void setShowServerVersion(boolean show) {
    this.showServerVersion = show;
  }

  /**
   * Показывать описание инфобазы в дереве.
   *
   * @return значение
   */
  public boolean isShowInfobaseDescription() {
    return showInfobaseDescription;
  }

  /**
   * Установить показ описания инфобазы в дереве.
   *
   * @param show значение
   */
  public void setShowInfobaseDescription(boolean show) {
    this.showInfobaseDescription = show;
  }

  /**
   * Показывать информацию в дереве, что сервер подключен через local-RAS.
   *
   * @return значение
   */
  public boolean isShowLocalRasConnectInfo() {
    return showLocalRasConnectInfo;
  }

  /**
   * Установить показ информации в дереве, что сервер подключен через local-RAS.
   *
   * @param show значение
   */
  public void setShowLocalRasConnectInfo(boolean show) {
    this.showLocalRasConnectInfo = show;
  }

  /**
   * Получить текущий язык приложения.
   *
   * @return текущий язык приложения
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Установить текущий язык приложения.
   *
   * @param locale - новый язык приложения
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * Затенять спящие сеансы.
   *
   * @return значение настройки
   */
  public boolean isShadeSleepingSessions() {
    return shadeSleepingSessions;
  }

  /**
   * Устанавливает настройку "Затенять спящие сеансы".
   *
   * @param shade - новое значение настройки
   */
  public void setShadowSleepSessions(boolean shade) {
    this.shadeSleepingSessions = shade;
  }

  /**
   * Подсвечивать новые строки в списках.
   *
   * @return значение настройки
   */
  public boolean isHighlightNewItems() {
    return highlightNewItems;
  }

  /**
   * Устанавливает настройку "Подсвечивать новые строки в списках".
   *
   * @param highlight - новое значение настройки
   */
  public void setHighlightNewItems(boolean highlight) {
    this.highlightNewItems = highlight;
  }

  /**
   * Получает длительность подсвечивания новых строк в списках.
   *
   * @return длительность подсветки
   */
  public int getHighlightNewItemsDuration() {
    return highlightNewItemsDuration;
  }

  /**
   * Устанавливает длительность подсвечивания новых строк в списках.
   *
   * @param duration - новая длительность подсветки
   */
  public void setHighlightNewItemsDuration(int duration) {
    this.highlightNewItemsDuration = duration;
  }

  /**
   * Читать бефер обмена.
   *
   * @return значение настройки
   */
  public boolean isReadClipboard() {
    return readClipboard;
  }

  /**
   * Установка настройки "Читать бефер обмена".
   *
   * @param read - новое значение настройки
   */
  public void setReadClipboard(boolean read) {
    this.readClipboard = read;
  }

  /**
   * Получает настройку "Направление сортировки строк по-умолчанию".
   *
   * @return значение настройки
   */
  public RowSortDirection getRowSortDirection() {
    return rowSortDirection;
  }

  /**
   * Устанавливает настройку "Направление сортировки строк по-умолчанию".
   *
   * @param sortDirection - новое значение настройки
   */
  public void setRowSortDirection(RowSortDirection sortDirection) {
    this.rowSortDirection = sortDirection;
  }

  /**
   * Получает настройку "Направление сортировки инфобаз".
   *
   * @return значение настройки
   */
  public InfobasesSortDirection getInfobasesSortDirection() {
    return infobasesSortDirection;
  }

  /**
   * Устанавливает настройку "Направление сортировки инфобаз".
   *
   * @param sortDirection - новое значение настройки
   */
  public void setInfobasesSortDirection(InfobasesSortDirection sortDirection) {
    this.infobasesSortDirection = sortDirection;
  }

  /**
   * Получить установленный уровень логирования.
   *
   * @return уровень логирования
   */
  public String getLoggerLevel() {
    return loggerLevel;
  }

  /**
   * Установить уровень логирования.
   *
   * @param level - уровень логирования
   */
  public void setLoggerLevel(String level) {
    this.loggerLevel = level;
    applyLoggerLevel();
  }

  /** Применить уровень логирования из конфига. */
  private void applyLoggerLevel() {

    ROOT_LOGGER.setLevel(Level.toLevel("info"));
    ROOT_LOGGER.info("logger level switching to <{}>", loggerLevel); // $NON-NLS-1$
    ROOT_LOGGER.setLevel(Level.toLevel(loggerLevel));

    ROOT_LOGGER.error("test logger level = error"); // $NON-NLS-1$
    ROOT_LOGGER.warn("test logger level = warn"); // $NON-NLS-1$
    ROOT_LOGGER.info("test logger level = info"); // $NON-NLS-1$
    ROOT_LOGGER.debug("test logger level = debug"); // $NON-NLS-1$
  }

  /**
   * Получить частоту обновления списка.
   *
   * @return частота обновления списка (миллисекунд)
   */
  public int getListRrefreshRate() {
    return listRefreshRate;
  }

  /**
   * Установка частоты обновления списка.
   *
   * @param refreshRate - частота обновления списка (миллисекунд)
   */
  public void setListRrefreshRate(int refreshRate) {
    this.listRefreshRate = refreshRate;
  }

  /**
   * Запрашивать логин/пароль при действиях с базой.
   *
   * @return запрашивать или нет
   */
  public boolean getRequestLogon() {
    return requestLogon;
  }

  /**
   * Установка запроса логин/пароля при действиях с базой.
   *
   * @param requestLogon - запрашивать логин/пароль
   */
  public void setRequestLogon(boolean requestLogon) {
    this.requestLogon = requestLogon;
  }

  /**
   * Получение свойства колонок списков.
   *
   * @param clazz - имя класса, идентифицирующее список-кладелец колонок
   * @return ColumnProperties - свойства колонок списка
   */
  public ColumnProperties getColumnsProperties(Class<? extends BaseInfoExtended> clazz) {
    if (clazz == SessionInfoExtended.class) {
      return sessionColumnProperties;
    } else if (clazz == ConnectionInfoExtended.class) {
      return connectionColumnProperties;
    } else if (clazz == LockInfoExtended.class) {
      return lockColumnProperties;
    } else if (clazz == WorkingProcessInfoExtended.class) {
      return wpColumnProperties;
    } else if (clazz == WorkingServerInfoExtended.class) {
      return wsColumnProperties;
    } else {
      return null;
    }
  }

  /**
   * Установка порядка колонок списков по имени класса.
   *
   * @param clazz - имя класса, идентифицирующее список-кладелец колонок
   * @param columnOrder - новый порядок колонок
   */
  public void setColumnsOrder(Class<? extends BaseInfoExtended> clazz, int[] columnOrder) {
    getColumnsProperties(clazz).setOrder(columnOrder);
  }

  /**
   * Установка ширины колонок списков по имени класса.
   *
   * @param clazz - имя класса, идентифицирующее список-кладелец колонок
   * @param index - индекс колонки
   * @param width - ширина колонки
   */
  public void setColumnsWidth(Class<? extends BaseInfoExtended> clazz, int index, int width) {
    getColumnsProperties(clazz).setWidth(index, width);
  }

  /**
   * Это ОС Windows.
   *
   * @return true если Windows
   */
  public boolean isWindows() {
    return currentOs == OsType.WINDOWS;
  }

  /**
   * Это ОС Linux.
   *
   * @return true если Linux
   */
  public boolean isLinux() {
    return currentOs == OsType.LINUX;
  }

  /**
   * Это ОС MacOs.
   *
   * @return true если MacOs
   */
  public boolean isMacOs() {
    return currentOs == OsType.MACOS;
  }

  /**
   * Возвращает текущую версию приложения.
   *
   * @return Version
   */
  public Version getCurrentVersion() {
    return currentVersion;
  }

  /**
   * Возвращает последнюю версию приложения с релизов GitHub.
   *
   * @return Version или null, если запрос не выполнялся или завершился ошибкой
   */
  public Version getLatestVersion() {
    return latestVersion;
  }

  /**
   * Возвращает путь к файлу конфига.
   *
   * @return строка с путем к файлу конфига
   */
  public String getConfigPath() {
    return configPath;
  }

  /**
   * Устанавливает путь к файлу конфига.
   *
   * @param configPath - строка с путем к файлу конфига
   */
  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }

  /**
   * Читает конфиг из файла по-умолчанию.
   *
   * @return ссылка на прочитанный конфиг
   */
  public static Config readConfig() {
    return readConfig(DEFAULT_CONFIG_PATH);
  }

  /**
   * Читает конфиг из определенного файла.
   *
   * @param configPath - имя конфиг файла
   * @return объект конфига
   */
  public static Config readConfig(String configPath) {
    if (configPath == null || configPath.isBlank()) {
      LOGGER.debug("Config path is empty, set config path in root folder"); //$NON-NLS-1$
      configPath = DEFAULT_CONFIG_PATH;
    }
    LOGGER.info("Start read config from file <{}>", configPath); //$NON-NLS-1$

    File configFile = new File(configPath);
    if (!configFile.exists()) {
      LOGGER.debug(
          "Config file not exists, create new config in folder <{}>", configPath); //$NON-NLS-1$
      return new Config(configPath);
    }

    JsonReader jsonReader = null;

    try {
      jsonReader =
          new JsonReader(
              new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
    } catch (FileNotFoundException excp) {
      LOGGER.debug("Config file read error:", excp); //$NON-NLS-1$
      LOGGER.debug("Create temp config in root folder"); //$NON-NLS-1$
      // configFile = new File(TEMP_CONFIG_PATH);
      return new Config(TEMP_CONFIG_PATH);
    }
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    Config config = null;
    try {
      config = gson.fromJson(jsonReader, Config.class);
    } catch (Exception excp) {
      LOGGER.debug("error convert config from json:", excp); //$NON-NLS-1$
      LOGGER.debug("Create temp config in root folder"); //$NON-NLS-1$
      // configFile = new File(TEMP_CONFIG_PATH);
      return new Config(TEMP_CONFIG_PATH);
    }

    if (config == null) {
      LOGGER.debug("the config is null after reading the json"); //$NON-NLS-1$
      LOGGER.debug("Create temp config in root folder"); //$NON-NLS-1$
      // configFile = new File(TEMP_CONFIG_PATH);
      config = new Config(TEMP_CONFIG_PATH);
    } else {

      config.setConfigPath(configPath);
      config.migrateProps();
      config.init();
      if (config.getLocale() != null) {
        LOGGER.debug("Set locale is <{}>", config.getLocale()); //$NON-NLS-1$
        Locale locale = Locale.forLanguageTag(config.getLocale());
        java.util.Locale.setDefault(locale);
        Messages.reloadBundle(locale); // TODO не совсем понятно как работает
      }

      config.applyLoggerLevel();
    }
    LOGGER.info("Config file read successfully"); //$NON-NLS-1$
    return config;
  }

  /** Сохранение конфига в файл. */
  public void saveConfig() {

    LOGGER.info("Start save config to file <{}>", configPath); //$NON-NLS-1$

    // configFile = new File(configPath);

    JsonWriter jsonWriter;
    try {
      jsonWriter =
          new JsonWriter(
              new OutputStreamWriter(new FileOutputStream(configPath), StandardCharsets.UTF_8));
    } catch (FileNotFoundException excp) {
      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
      return;
    }
    Gson gson =
        new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    try {
      gson.toJson(this, this.getClass(), jsonWriter);
    } catch (JsonIOException excp) {
      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
    }

    try {
      jsonWriter.close();
    } catch (IOException excp) {
      LOGGER.error("Config file save error:", excp); //$NON-NLS-1$
    }
    LOGGER.info("Config file write successfully"); //$NON-NLS-1$
  }
}
