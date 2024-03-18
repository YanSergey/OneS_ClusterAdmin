package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.AgentAdminException;
import com._1c.v8.ibis.admin.IAgentAdminConnection;
import com._1c.v8.ibis.admin.IAssignmentRuleInfo;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IClusterManagerInfo;
import com._1c.v8.ibis.admin.IClusterServiceInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.IRegUserInfo;
import com._1c.v8.ibis.admin.IResourceConsumptionCounter;
import com._1c.v8.ibis.admin.IResourceConsumptionCounterValue;
import com._1c.v8.ibis.admin.IResourceConsumptionLimit;
import com._1c.v8.ibis.admin.ISecurityProfile;
import com._1c.v8.ibis.admin.ISecurityProfileAddIn;
import com._1c.v8.ibis.admin.ISecurityProfileApplication;
import com._1c.v8.ibis.admin.ISecurityProfileCOMClass;
import com._1c.v8.ibis.admin.ISecurityProfileExternalModule;
import com._1c.v8.ibis.admin.ISecurityProfileInternetResource;
import com._1c.v8.ibis.admin.ISecurityProfileVirtualDirectory;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com._1c.v8.ibis.admin.client.IAgentAdminConnector;
import com._1c.v8.ibis.admin.client.IAgentAdminConnectorFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.lang.Runtime.Version;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibraryUI.AuthenticateDialog;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea.TreeItemType;

/** Параметры подключения к серверу 1С Предприятие. */
public class Server implements Comparable<Server> {

  @SerializedName("Description")
  @Expose
  private String description = ""; //$NON-NLS-1$

  @SerializedName("AgentHost")
  @Expose
  private String agentHost = ""; //$NON-NLS-1$

  @SerializedName("AgentPort")
  @Expose
  private int agentPort = 0;

  @SerializedName("RasHost")
  @Expose
  private String rasHost = ""; //$NON-NLS-1$

  @SerializedName("RasPort")
  @Expose
  private int rasPort = 0;

  @SerializedName("UseLocalRas")
  @Expose
  private boolean useLocalRas = false;

  @SerializedName("LocalRasPort")
  @Expose
  private int localRasPort = 0;

  @SerializedName("V8Version")
  @Expose
  private String v8version = ""; //$NON-NLS-1$

  @SerializedName("Autoconnect")
  @Expose
  private boolean autoconnect = false;

  @Deprecated(since = "0.3.0", forRemoval = true)
  @SerializedName("SaveCredentials")
  @Expose(serialize = false, deserialize = true)
  private boolean saveCredentials = false;

  @SerializedName("SaveCredentialsVariant")
  @Expose
  private SaveCredentialsVariant saveCredentialsVariant = SaveCredentialsVariant.DISABLE;

  @Deprecated(since = "0.3.0", forRemoval = true)
  @SerializedName("AgentUser")
  @Expose(serialize = false, deserialize = true)
  private String agentUserName = ""; //$NON-NLS-1$

  @Deprecated(since = "0.3.0", forRemoval = true)
  @SerializedName("AgentPassword")
  @Expose(serialize = false, deserialize = true)
  private String agentPassword = ""; //$NON-NLS-1$

  @SerializedName("AgentCredential")
  @Expose
  private UserPassPair agentCredential = new UserPassPair();

  @Deprecated(since = "0.3.0", forRemoval = true)
  @SerializedName("ClustersCredentials")
  @Expose(serialize = false, deserialize = true)
  private Map<UUID, String[]> clustersCredentialsOld = new HashMap<>();

  @SerializedName("ClustersCredentialsV3")
  @Expose
  private Map<UUID, UserPassPair> clustersCredentialsV03 = new HashMap<>();

  @SerializedName("InfobasesCredentials")
  @Expose
  private List<UserPassPair> infobasesCredentials = new ArrayList<>();

  @SerializedName("FavoriteInfobases")
  @Expose
  private List<String> favoriteInfobases = new ArrayList<>();

  private ServerState serverState = ServerState.DISCONNECT;
  private boolean available;
  private Process localRasProcess;
  private String connectionError = ""; //$NON-NLS-1$
  boolean silentConnectionMode = false;

  @Deprecated
  private String agentVersion = Messages.getString("Server.NotConnect"); //$NON-NLS-1$

  private IAgentAdminConnector agentConnector;
  private IAgentAdminConnection agentConnection;

  private static final Logger LOGGER =
      LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$

  public static final String THIN_CLIENT = "1CV8C"; //$NON-NLS-1$
  public static final String THICK_CLIENT = "1CV8"; //$NON-NLS-1$
  public static final String WEB_CLIENT = "WebClient"; //$NON-NLS-1$
  public static final String DESIGNER = "Designer"; //$NON-NLS-1$
  public static final String SERVER_CONSOLE = "SrvrConsole"; //$NON-NLS-1$
  public static final String RAS_CONSOLE = "RAS"; //$NON-NLS-1$
  public static final String JOBSCHEDULER = "JobScheduler"; //$NON-NLS-1$
  public static final String BACKGROUND_JOB = "BackgroundJob"; //$NON-NLS-1$

  private static final String TREE_TITLE_PATTERN = "%s (%s)"; //$NON-NLS-1$

  private static Image defaultIcon = Helper.getImage("server_default_24.png"); // $NON-NLS-1$
  private static Image connectedIcon = Helper.getImage("server_connected_24.png"); // $NON-NLS-1$
  private static Image connectingIcon = Helper.getImage("server_connecting_24.png"); // $NON-NLS-1$
  private static Image connectErrorIcon = Helper.getImage("server_connectError_24.png"); // $NON-NLS-1$

  private enum ServerState {
    DISCONNECT,
    CONNECTED,
    CONNECTING,
    CONNECT_ERROR
  }

  public enum SaveCredentialsVariant {
    DISABLE,
    NAME,
    NAMEPASS
  }

  interface AuthenticateAction {
    void performAutenticate(UserPassPair userPass, boolean saveNewUserpass);
  }

  interface IGetInfobaseInfo {
    IInfoBaseInfo getInfo(String userName, String password);
  }

  /**
   * Получение описания сервера.
   *
   * @return описание сервера
   */
  public String getDescription() {
    return description;
  }

  /**
   * Установка описания сервера.
   *
   * @param description - новое описания сервера
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Получение имени приложения по Application ID.
   *
   * @param appId - application ID
   * @return имя приложения
   */
  public String getApplicationName(String appId) {
    // TODO переделать на map
    switch (appId) {
      case THIN_CLIENT:
        return Messages.getString("Server.ThinClient"); //$NON-NLS-1$
      case THICK_CLIENT:
        return Messages.getString("Server.ThickClient"); //$NON-NLS-1$
      case WEB_CLIENT:
        return Messages.getString("Server.WebClient"); //$NON-NLS-1$
      case DESIGNER:
        return Messages.getString("Server.Designer"); //$NON-NLS-1$
      case SERVER_CONSOLE:
        return Messages.getString("Server.ClusterConsole"); //$NON-NLS-1$
      case RAS_CONSOLE:
        return Messages.getString("Server.AdministrationServer"); //$NON-NLS-1$
      case JOBSCHEDULER:
        return Messages.getString("Server.JobScheduler"); //$NON-NLS-1$
      case BACKGROUND_JOB:
        return Messages.getString("Server.BackgroundJob"); //$NON-NLS-1$
      case "": //$NON-NLS-1$
        return ""; //$NON-NLS-1$
      default:
        return String.format(Messages.getString("Server.UnknownClient"), appId); //$NON-NLS-1$
    }
  }

  /**
   * Получение имени хоста агента сервера.
   *
   * @return имя хоста агента сервера
   */
  public String getAgentHost() {
    return agentHost;
  }

  /**
   * Установка имени хоста агента сервера.
   *
   * @param agentHost - новое имя хоста агента сервера
   */
  public void setAgentHost(String agentHost) {
    this.agentHost = agentHost;
  }

  /**
   * Получение порта агента сервера строкой.
   *
   * @return порт агента сервера строкой
   */
  public String getAgentPortAsString() {
    return Integer.toString(agentPort);
  }

  /**
   * Установка порта агента сервера.
   *
   * @param agentPort - порт агента сервера
   */
  public void setAgentPort(int agentPort) {
    this.agentPort = agentPort;
  }

  /**
   * Получение имени хоста RAS.
   *
   * @return имя хоста RAS
   */
  public String getRasHost() {
    return rasHost;
  }

  /**
   * Установка имени хоста RAS.
   *
   * @param rasHost - имя хоста RAS
   */
  public void setRasHost(String rasHost) {
    this.rasHost = rasHost;
  }

  /**
   * Получение имени хоста RAS строкой.
   *
   * @return имя хоста RAS строкой
   */
  public String getRasPortAsString() {
    return Integer.toString(rasPort);
  }

  /**
   * Установка порта RAS.
   *
   * @param rasPort - порт RAS
   */
  public void setRasPort(int rasPort) {
    this.rasPort = rasPort;
  }

  /**
   * Получение использования локального RAS.
   *
   * @return использование локального RAS
   */
  public boolean getUseLocalRas() {
    return useLocalRas;
  }

  /**
   * Установка использования локального RAS.
   *
   * @param useLocalRas - использовать локальный RAS
   */
  public void setUseLocalRas(boolean useLocalRas) {
    this.useLocalRas = useLocalRas;
  }

  /**
   * Получение порта локального RAS строкой.
   *
   * @return порт локального RAS строкой
   */
  public String getLocalRasPortAsString() {
    return Integer.toString(localRasPort);
  }

  /**
   * Установка порта локального RAS.
   *
   * @param localRasPort - порт локального RAS
   */
  public void setLocalRasPort(int localRasPort) {
    this.localRasPort = localRasPort;
  }

  /**
   * Получение версии платформы v8 1C-сервера.
   *
   * @return версия платформы v8 1C-сервера
   */
  public String getV8Version() {
    return this.v8version;
  }

  /**
   * Установка версии платформы v8 1C-сервера.
   *
   * @param v8version - версия платформы v8 1C-сервера
   */
  public void setV8Version(String v8version) {
    this.v8version = v8version;
  }

  /**
   * Получение строки с ошибкой подключения к серверу.
   *
   * @return ошибка подключения
   */
  public String getConnectionError() {
    return connectionError;
  }

  /**
   * Показывает, нужно ли выводить ошибку подключения.
   *
   * @return выводить ошибку подключения
   */
  public boolean needShowConnectionError() {
    return !connectionError.isBlank() && !silentConnectionMode;
  }

  /**
   * Получение ключа сервера в виде "Server:1541".
   *
   * @return ключ сервера
   */
  public String getServerKey() {
    return agentHost.concat(":").concat(Integer.toString(agentPort)); //$NON-NLS-1$
  }

  /**
   * Получение названия сервера для дерева.
   *
   * @return Название сервера
   */
  public String getTreeTitle() {

    var commonConfig = Config.currentConfig;

    var localRasPatternPart =
        useLocalRas && commonConfig.isShowLocalRasConnectInfo()
            ? String.format("(local-RAS:%s)->", getLocalRasPortAsString()) //$NON-NLS-1$
            : ""; //$NON-NLS-1$
    var serverVersionPatternPart =
        commonConfig.isShowServerVersion()
            ? String.format(" (%s)", v8version) //$NON-NLS-1$
            : ""; //$NON-NLS-1$
    var serverDescriptionPatternPart =
        commonConfig.isShowServerDescription() && !description.isBlank()
            ? String.format(" - <%s>", description) //$NON-NLS-1$
            : ""; //$NON-NLS-1$

    return String.format(
        "%s%s:%s%s%s", //$NON-NLS-1$
        localRasPatternPart,
        agentHost,
        getAgentPortAsString(),
        serverVersionPatternPart,
        serverDescriptionPatternPart);
  }

  /**
   * Получение названия кластера для дерева.
   *
   * @param clusterInfo - информация о кластере
   * @return Название сервера
   */
  public String getClusterTreeTitle(IClusterInfo clusterInfo) {

    return String.format(TREE_TITLE_PATTERN, clusterInfo.getName(), clusterInfo.getMainPort());
  }

  /**
   * Получение названия рабочего процесса для дерева.
   *
   * @param wpInfo - информация о рабочем процессе
   * @return Название сервера
   */
  public String getWorkingProcessTreeTitle(IWorkingProcessInfo wpInfo) {

    return String.format(TREE_TITLE_PATTERN, wpInfo.getHostName(), wpInfo.getMainPort());
  }

  /**
   * Получение названия рабочего сервера для дерева.
   *
   * @param wsInfo - информация о рабочем сервере
   * @return Название сервера
   */
  public String getWorkingServerTreeTitle(IWorkingServerInfo wsInfo) {

    return String.format(TREE_TITLE_PATTERN, wsInfo.getHostName(), wsInfo.getMainPort());
  }

  /**
   * Получение значения настройки "Автоподключение при старте программы".
   *
   * @return значение настройки "Автоподключение при старте программы".
   */
  public boolean getAutoconnect() {
    return autoconnect;
  }

  /**
   * Установка значения настройки "Автоподключение при старте программы".
   *
   * @param autoconnect - новое значение настройки "Автоподключение при старте программы".
   */
  public void setAutoconnect(boolean autoconnect) {
    this.autoconnect = autoconnect;
  }

  /**
   * Получение текущего варианта сохранения credentials.
   *
   * @return текущий вариант сохранения credentials
   */
  public SaveCredentialsVariant getSaveCredentialsVariant() {
    return saveCredentialsVariant;
  }

  /**
   * Установка варианта сохранения credentials.
   *
   * @param saveCredentialsVariant - вариант сохранения credentials
   */
  public void setSaveCredentialsVariant(SaveCredentialsVariant saveCredentialsVariant) {
    this.saveCredentialsVariant = saveCredentialsVariant;
  }

  /**
   * Получение логина и пароля агента сервера.
   *
   * @return логин и пароль агента сервера
   */
  public UserPassPair getAgentCredential() {
    return agentCredential;
  }

  /**
   * Установка логина и пароля агента сервера.
   *
   * @param agentUserPass - логин и пароль агента сервера
   */
  public void setAgentCredential(UserPassPair agentUserPass) {
    this.agentCredential = agentUserPass;
    this.agentCredential.clear(saveCredentialsVariant);
  }

  /**
   * Добавление новых данных доступа к кластерам.
   *
   * @param clusterId - ID кластера
   * @param userPassPair - логин/пароль для добавления
   */
  public void addClusterCredentials(UUID clusterId, UserPassPair userPassPair) {
    clustersCredentialsV03.put(clusterId, userPassPair);
  }

  /**
   * Получение данных доступа к кластерам.
   *
   * @param clusterId - ID кластера
   * @return массив с логин/паролями
   */
  public UserPassPair getClusterCredentials(UUID clusterId) {
    return clustersCredentialsV03.getOrDefault(clusterId, new UserPassPair());
  }

  /**
   * Получение данных доступа к кластерам.
   *
   * @return данные доступа
   */
  public Map<UUID, UserPassPair> getAllClustersCredentials() {
    return clustersCredentialsV03;
  }

  /**
   * Установка данных доступа к кластерам.
   *
   * @param credentials - новые данные доступа
   */
  public void setAllClustersCredentials(Map<UUID, UserPassPair> credentials) {
    this.clustersCredentialsV03 = credentials;
  }

  /**
   * Сохранение в конфиге новых данных доступа к инфобазам кластера.
   *
   * @param userPassPair - логин/пароль для добавления
   */
  public void saveInfobaseCredentials(UserPassPair userPassPair) {
    infobasesCredentials.add(userPassPair);
  }

  /**
   * Получение данных доступа к инфобазам.
   *
   * @return данные доступа
   */
  public List<UserPassPair> getInfobasesCredentials() {
    return infobasesCredentials;
  }

  /**
   * Установка данных доступа к инфобазам.
   *
   * @param credentials - новые данные доступа
   */
  public void setAllInfobasesCredentials(List<UserPassPair> credentials) {
    this.infobasesCredentials = credentials;
  }

  /**
   * Проверяет нахождение инфобазы в списке Избранного.
   *
   * @param ib - инфобаза
   * @return Истина - если инфобаза в Избранном, иначе ложь
   */
  public boolean infobaseIsFavorite(InfoBaseInfoShortExt ib) {
    return favoriteInfobases.contains(ib.getName());
  }

  /**
   * Проверяет нахождение инфобазы в списке Избранного.
   *
   * @param infobaseName - имя инфобазы
   * @return Истина - если инфобаза в Избранном, иначе ложь
   */
  public boolean infobaseIsFavorite(String infobaseName) {
    return favoriteInfobases.contains(infobaseName);
  }

  /**
   * Добавление/удаление информационной базы в избранное.
   *
   * @param ib - Экземпляр расширенной информации о инфобазе
   */
  public void changeInfobaseFavoriteState(InfoBaseInfoShortExt ib) {
    String infobaseName = ib.getName();
    if (favoriteInfobases.contains(infobaseName)) {
      favoriteInfobases.remove(infobaseName);
    } else {
      favoriteInfobases.add(infobaseName);
    }

    ib.setFavoriteState(favoriteInfobases.contains(infobaseName));
  }

  /** Конструктор, вызываемый при десериализации. */
  public Server() {
  }

  /**
   * Создание нового экземпляра.
   *
   * @param serverName - имя сервера в виде "Server" или с указанием порта менеджера "Server:2541".
   */
  public Server(String serverName) {

    computeServerParams(serverName);

    //    this.useLocalRas = false;
    //    this.localRasPort = 0;
    //    this.localRasV8version = ""; //$NON-NLS-1$
    //    this.autoconnect = false;
    //    this.available = false;
    //    this.saveCredentials = false;
    //    this.agentVersion = ""; //$NON-NLS-1$

    //    init();
  }

  /** Initializes some server parameters. */
  //  public void init() {
  //
  //    // При чтении конфиг-файла отсутствующие поля, инициализируются значением null
  //    if (agentUserName == null) {
  //      agentUserName = ""; //$NON-NLS-1$
  //    }
  //    if (agentPassword == null) {
  //      agentPassword = ""; //$NON-NLS-1$
  //    }
  //    if (description == null) {
  //      description = ""; //$NON-NLS-1$
  //    }
  //    if (localRasV8version == null) {
  //      localRasV8version = ""; //$NON-NLS-1$
  //    }
  //    if (agentVersion == null) {
  //      agentVersion = Messages.getString("Server.NotConnect"); //$NON-NLS-1$
  //    }
  //
  //    if (credentialsClustersCashe == null) {
  //      credentialsClustersCashe = new HashMap<>();
  //    }
  //
  //    this.connectionError = ""; //$NON-NLS-1$
  //
  //    LOGGER.info("Server <{}> init done", getServerKey()); //$NON-NLS-1$
  //  }

  /**
   * Перенос настроек при чтении конфиг-файла старой версии.
   *
   * @param configVersion - версия конфига, с которого осуществляется переход
   */
  public void migrateProps(String configVersion) {

    if (configVersion.equals("0.2.0")) {

      if (saveCredentials) {
        saveCredentialsVariant = SaveCredentialsVariant.NAMEPASS;
        saveCredentials = false;
      }

      if (!agentUserName.isBlank() && saveCredentialsVariant == SaveCredentialsVariant.NAMEPASS) {
        agentCredential = new UserPassPair(agentUserName, agentPassword);
        agentUserName = "";
        agentPassword = "";
      }

      clustersCredentialsOld.forEach(
          (uuid, array) ->
              clustersCredentialsV03.put(uuid, new UserPassPair(array[0], array[1], array[2])));
      clustersCredentialsOld.clear();

      LOGGER.info("Server <{}> migrate props from version 0.2.0", getServerKey()); //$NON-NLS-1$
    }

  }

  @Override
  public int compareTo(Server o) {

    Collator collator = Collator.getInstance(Locale.getDefault());

    // Сортировка по алфавиту (по возрастанию)
    String firstString = o.getServerKey();
    String secondString = getServerKey();

    return collator.compare(secondString, firstString);
  }

  private void refreshAgentVersion() {

    if (!isConnected()) {
      return;
    }
    
    String newAgentVersionString = "";
    try {
      newAgentVersionString = agentConnection.getAgentVersion();
    } catch (Exception e) {
      // для платформы 8.3.10 и ниже agentConnection.getAgentVersion() бросает AgentAdminException
      this.v8version = Messages.getString("Server.UnknownAgentVersion"); // $NON-NLS-1$
      LOGGER.error("Unknown agent version of server <{}>", this.getServerKey()); // $NON-NLS-1$
      return;
    }

    if (this.v8version.isEmpty()) {
      this.v8version = newAgentVersionString;
      LOGGER.debug(
          "Set Server <{}> version is <{}>", this.getServerKey(), this.v8version); //$NON-NLS-1$
      return;
    }

    // agentVersion = agentConnection.getAgentVersion() = [8, 3, 18, 1483]
    // agentVersion.feature() = 8
    // agentVersion.interim() = 3
    // agentVersion.update() = 18
    // agentVersion.patch() = 1483
    // для платформы 8.3.15 и ниже agentVersion.patch() = 0 всегда

    Version newAgentVersion = Version.parse(newAgentVersionString);
    Version savedV8version = Version.parse(this.v8version);

    boolean v8versionIsActual;

    if (newAgentVersion.version().size() == 3) {
      v8versionIsActual =
          savedV8version.feature() == newAgentVersion.feature()
              && savedV8version.interim() == newAgentVersion.interim()
              && savedV8version.update() == newAgentVersion.update();

    } else {
      v8versionIsActual = savedV8version.equals(newAgentVersion);
    }

    if (!v8versionIsActual) {
      this.v8version = newAgentVersionString;
      LOGGER.debug(
          "Refresh Server <{}> version is <{}>", //$NON-NLS-1$
          this.getServerKey(),
          this.v8version);
    }
  }

  private String getLocalisedMessage(Throwable excp) {

    Throwable cause = excp.getCause();
    while (cause.getCause() != null) {
      cause = cause.getCause();
      if (cause instanceof java.nio.channels.UnresolvedAddressException) {
        return Messages.getString("Server.UnresolvedAddress"); //$NON-NLS-1$
      }
    }
    return cause.getLocalizedMessage();
  }

  /**
   * Возвращает истину, если версия платформы v8 8.3.15 или выше.
   *
   * @return {@code true} если v8 версия 8.3.15 или выше
   */
  public boolean isFifteenOrMoreAgentVersion() {
    return v8version.compareTo("8.3.15") >= 0; //$NON-NLS-1$
  }

  /**
   * Вычисляет имя хоста и порты, на которых запущены процессы кластера.
   *
   * @param serverAddress - Имя сервера из списка баз. Может содержать номер порта менеджера
   *     кластера (Если не указан, то по-умолчанию 1541). Примеры: Server1c, Server1c:2541
   */
  private void computeServerParams(String serverAddress) {

    String host;
    int newAgentPort;
    int newRasPort;

    serverAddress = serverAddress.strip();
    if (serverAddress.isBlank()) {
      serverAddress = "localhost"; //$NON-NLS-1$
    }

    String[] adr = serverAddress.split(":"); //$NON-NLS-1$
    host = adr[0];

    if (adr.length == 1) {
      newAgentPort = 1540;
      newRasPort = 1545;
    } else {
      int managerPort = Integer.parseInt(adr[1]);
      newAgentPort = managerPort - 1;
      newRasPort = managerPort + 4;
    }

    this.agentHost = host;
    this.rasHost = host;
    this.agentPort = newAgentPort;
    this.rasPort = newRasPort;

    LOGGER.info("Compute params for Server <{}> ", this.getServerKey()); //$NON-NLS-1$
  }

  /**
   * Проверяет, установлено ли соединение с сервером администрирования.
   *
   * @return {@code true} установлено, {@code false} не установлено
   */
  public boolean isConnected() {
    boolean isConnected = (agentConnection != null);

    if (isConnected) {
      serverState = ServerState.CONNECTED;
    } else if (serverState != ServerState.CONNECTING) {
      serverState = ServerState.DISCONNECT;
      LOGGER.info(
          "The connection a server <{}> is not established", //$NON-NLS-1$
          this.getServerKey());
    }

    return isConnected;
  }

  /**
   * Выполняет подключение к серверу.
   *
   * @param disconnectAfter - отключиться сразу после успешного подключения
   * @param silentMode - не выводить сообщение об ошибке пользователю в интерактивном режиме
   * @return {@code true} если соединение прошло успешно
   */
  public boolean connectToServer(boolean disconnectAfter, boolean silentMode) {
    LOGGER.debug("<{}> start connection", this.getServerKey()); //$NON-NLS-1$

    if (isConnected()) {
      return true;
    }

    available = false;
    connectionError = "";
    serverState = ServerState.CONNECTING;
    silentConnectionMode = silentMode;

    // все вызовы здесь проверить на Helper.showMessageBox
    // этого тут быть не должно, потому что выполняется в отдельном потоке

    if (!checkAndRunLocalRas()) {
      serverState = ServerState.DISCONNECT;
      return false;
    }

    String currentRasHost = useLocalRas ? "localhost" : this.rasHost; //$NON-NLS-1$

    int currentRasPort = useLocalRas ? localRasPort : this.rasPort;
    //    try {
    //      Thread.sleep(5000);
    //    } catch (InterruptedException e) { // TODO Искусственная задержка подключения
    //      e.printStackTrace();
    //    }
    try {
      connectToAgent(currentRasHost, currentRasPort, 120);
      serverState = ServerState.CONNECTED;

      if (disconnectAfter) {
        disconnectFromAgent();
        serverState = ServerState.DISCONNECT;
      }
    } catch (Exception excp) {
      available = false;
      serverState = ServerState.CONNECT_ERROR;

      disconnectLocalRas();

      connectionError =
          String.format(
              "%s connection error:%n <%s>", //$NON-NLS-1$
              this.getServerKey(), getLocalisedMessage(excp));
      LOGGER.error(connectionError);

      // if (!silentMode) {
      // Helper.showMessageBox(connectionError);
      // }
      return false;
    }
    return true;
  }

  /**
   * Проверяет включено ли использование локального RAS и запускает его.
   *
   * @return {@code true} если локальный RAS выключен либо включен и удачно запустился, {@code
   *     false} если локальный RAS включен и не удалось его запустить
   */
  private boolean checkAndRunLocalRas() {

    if (!Config.currentConfig.isWindows()) {
      return true;
    }

    if (!useLocalRas) {
      return true;
    }

    if (v8version.isBlank() || localRasPort == 0) {
      connectionError =
          String.format(
              Messages.getString("Server.LocalRasParamsIsEmpty"), // $NON-NLS-1$
              this.getServerKey());
      LOGGER.error(connectionError);
      return false;
    }

    Version savedV8version = Version.parse(v8version);
    if (savedV8version.version().size() == 3) {
      connectionError =
          String.format(
              Messages.getString("Server.LocalRasParamsIsInvalid"), // $NON-NLS-1$
              v8version);
      LOGGER.error(connectionError);
      return false;
    }

    ///////////////////////////// пока только Windows
    var processBuilder = new ProcessBuilder();
    var processOutput = ""; //$NON-NLS-1$
    var localRasPath = Helper.pathToRas(v8version, "x64");
    if (localRasPath == null) {
      connectionError =
          String.format(
              Messages.getString("Server.LocalRasNotFound"), this.getServerKey()); // $NON-NLS-1$
      LOGGER.error(connectionError);
      return false;
    }

    Map<String, String> env = processBuilder.environment();

    env.put("RAS_PATH", localRasPath); //$NON-NLS-1$
    env.put("RAS_PORT", getLocalRasPortAsString()); //$NON-NLS-1$
    env.put("AGENT_HOST", agentHost); //$NON-NLS-1$
    env.put("AGENT_PORT", getAgentPortAsString()); //$NON-NLS-1$

    processBuilder.command(
        "cmd.exe", //$NON-NLS-1$
        "/c", //$NON-NLS-1$
        "\"%RAS_PATH%\" cluster %AGENT_HOST%:%AGENT_PORT% --port=%RAS_PORT%"); //$NON-NLS-1$

    LOGGER.debug(
        "Try launch local RAS <{}> <{} --port={}>", //$NON-NLS-1$
        localRasPath,
        this.getServerKey(),
        getLocalRasPortAsString());
    try {
      localRasProcess = processBuilder.start();
    } catch (Exception excp) {
      LOGGER.error("Error launch local RAS for server <{}>", this.getServerKey()); //$NON-NLS-1$
      LOGGER.error("Error: <{}>", processOutput, excp); //$NON-NLS-1$
      connectionError = excp.getLocalizedMessage();
      return false;
    }

    // Дочерний процесс RAS не сразу стартует и в лог о нем не попадает информация
    try {
      Thread.sleep(1000);
    } catch (InterruptedException excp) {
      LOGGER.error("Error: ", excp); //$NON-NLS-1$
    }

    LOGGER.debug("Local RAS runnung = {}", localRasProcess.isAlive()); //$NON-NLS-1$
    if (localRasProcess.isAlive()) {
      LOGGER.debug("Local RAS parent CMD pid = {}", localRasProcess.pid()); //$NON-NLS-1$
      Stream<ProcessHandle> subprocesses = localRasProcess.children();
      subprocesses.forEach(
          subprocess ->
              LOGGER.debug(
                  "\tsubprocess -> {}, pid = {}", //$NON-NLS-1$
                  subprocess.info().command().get(),
                  subprocess.pid()));

      return true;
    } else {
      connectionError =
          String.format("Local RAS <%s> is shutdown", this.getServerKey()); //$NON-NLS-1$
      LOGGER.error(connectionError);
      return false;
    }
  }

  /**
   * Проверяет действительна ли еще аутентификация на центральном сервере и если нет - запускает
   * процесс аутентификации.
   *
   * @param clusterId - ID кластера
   * @return boolean истекла/не истекла
   */
  private boolean checkAutenticateAgent() {

    var needAuthenticate = false;
    try {
      LOGGER.debug(
          "Gets the list administrators of server <{}>:<{}>", agentHost, agentPort); //$NON-NLS-1$
      agentConnection.getAgentAdmins();
      return true;
    } catch (Exception excp) {
      LOGGER.error(
          "Error get the list of of server administrators: <{}>", //$NON-NLS-1$
          excp.getLocalizedMessage());

      String[] rightStrings = { // TODO проверить английские варианты
        "Недостаточно прав пользователя на управление центральным сервером",
        "Администратор центрального сервера не аутентифицирован",
        "The user's rights to manage the central server are insufficient",
        "The administrator of the central server is not authenticated"
      };
      for (String rightString : rightStrings) {
        if (excp.getLocalizedMessage().contains(rightString)) {
          needAuthenticate = true;
          break;
        }
      }
    }

    if (needAuthenticate) {
      return authenticateAgent();
    }

    return false;
  }

  /**
   * Устанавливает соединение с сервером администрирования.
   *
   * @param address - адрес сервера RAS
   * @param port - IP порт сервера RAS
   * @param timeout - таймаут соединения (в миллисекундах)
   * @throws AgentAdminException в случае ошибок.
   */
  private void connectToAgent(String address, int port, long timeout) {
    if (isConnected()) {
      LOGGER.debug(
          "The connection to server <{}> is already established", //$NON-NLS-1$
          this.getServerKey());
      available = true;
      connectionError = ""; // $NON-NLS-1$
      return;
    }

    LOGGER.debug(
        "Try connect server <{}> to address:port=<{}:{}>", //$NON-NLS-1$
        this.getServerKey(),
        address,
        port);

    IAgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
    agentConnector = factory.createConnector(timeout);
    agentConnection = agentConnector.connect(address, port);

    available = true;
    connectionError = ""; //$NON-NLS-1$
    refreshAgentVersion();

    LOGGER.debug("Server <{}> is connected now", this.getServerKey()); //$NON-NLS-1$
  }

  /**
   * Отключение от сервера администрирования.
   *
   * @throws AgentAdminException в случае ошибок.
   */
  public void disconnectFromAgent() {
    if (!isConnected()) {
      return;
    }

    disconnectLocalRas();

    try {
      agentConnector.shutdown();
      LOGGER.info(
          "Server <{}> disconnected now", //$NON-NLS-1$
          this.getServerKey());
    } catch (Exception excp) {
      LOGGER.info(
          "Server <{}> disconnect error: <{}>", //$NON-NLS-1$
          this.getServerKey(),
          excp.getLocalizedMessage());
    } finally {
      agentConnection = null;
      agentConnector = null;
      serverState = ServerState.DISCONNECT;
    }
  }

  private void disconnectLocalRas() {
    if (useLocalRas && localRasProcess.isAlive()) {
      Stream<ProcessHandle> ch = localRasProcess.children();
      ch.forEach(ProcessHandle::destroy);
      localRasProcess.destroy();
      LOGGER.info(
          "Local RAS of Server <{}> is shutdown now", //$NON-NLS-1$
          this.getServerKey());
    }
  }

  /**
   * Выполняет аутентификацию администратора центрального сервера.
   *
   * <p>Необходимо вызывать перед regCluster, getAgentAdmins, regAgentAdmin, unregAgentAdmin
   *
   * @return {@code true} успешно, {@code false} не успешно
   */
  public boolean authenticateAgent() {

    if (!isConnected()) {
      return false;
    }

    AuthenticateAction authAction =
        (UserPassPair userPass, boolean saveNewUserpass) -> {
          LOGGER.debug(
              "Try to autenticate the agent server <{}>", //$NON-NLS-1$
              getServerKey());
          agentConnection.authenticateAgent(userPass.getUsername(), userPass.getPassword());
          LOGGER.debug(
              "Authentication to the agent server <{}> was successful", //$NON-NLS-1$
              getServerKey());

          // сохраняем новые user/pass после успешной авторизации
          // (если сохранение включено в настройках)
          if (saveNewUserpass && saveCredentialsVariant != SaveCredentialsVariant.DISABLE) {
            agentCredential = userPass;
            agentCredential.clear(saveCredentialsVariant);
            // agentUserName = userPass.getUsername();
            // agentPassword =
            // saveCredentialsVariant == SaveCredentialsVariant.NAMEPASS
            // ? userPass.getPassword()
            // : "";
            LOGGER.debug(
                "New credentials for the agent server <{}> are saved", //$NON-NLS-1$
                getServerKey());
          }
        };
    String authTitle =
        String.format(
            Messages.getString("Server.AuthenticationOfCentralServerAdministrator"), //$NON-NLS-1$
            agentHost,
            agentPort);

    return runAuthProcessWithRequestToUser(
        agentCredential, new ArrayList<>(), authTitle, authAction);
  }

  /**
   * Проверяет, действительна ли аутентификация в кластере, и, если нет, запускает процесс
   * аутентификации.
   *
   * @param clusterId - ID кластера
   * @return {@code true} аутентифицирован, {@code false} не аутентифицирован
   */
  private boolean checkAutenticateCluster(UUID clusterId) {

    var needAuthenticate = false;
    try {
      LOGGER.debug("Check autenticate of cluster <{}>", clusterId); //$NON-NLS-1$
      agentConnection.getClusterAdmins(clusterId);
      LOGGER.debug("Autenticate succesful"); //$NON-NLS-1$
      return true;
    } catch (Exception excp) {
      LOGGER.error("Error autenticate of cluster: <{}>", excp.getLocalizedMessage()); //$NON-NLS-1$

      String[] rightStrings = { // TODO проверить английские варианты
        "Недостаточно прав пользователя на управление кластером",
        "Администратор кластера не аутентифицирован",
        "Insufficient user rights to manage the cluster",
        "The cluster administrator is not authenticated"
      };
      for (String rightString : rightStrings) {
        if (excp.getLocalizedMessage().toLowerCase().contains(rightString.toLowerCase())) {
          needAuthenticate = true;
          break;
        }
      }
    }

    if (needAuthenticate) {
      return authenticateCluster(clusterId);
    }

    // TODO вывести ошибку пользователю
    return false;
  }

  /**
   * Проверяет, действительна ли аутентификация в инфобазе, и, если нет, запускает процесс
   * аутентификации.
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return {@code true} аутентифицирован, {@code false} не аутентифицирован
   */
  private boolean checkAutenticateInfobase(UUID clusterId, UUID infobaseId) {

    return (getInfoBaseInfo(clusterId, infobaseId) != null);
  }

  /**
   * Аутентификация на кластере.
   *
   * @param clusterId - ID кластера
   * @return {@code true} при успешной аутентификации
   */
  public boolean authenticateCluster(UUID clusterId) {

    if (!isConnected()) {
      return false;
    }

    AuthenticateAction authAction =
        (UserPassPair userPass, boolean saveNewUserpass) -> {
          IClusterInfo clusterInfo = getClusterInfo(clusterId);
          var clusterName =
              String.format(
                  "%s (%s)", clusterInfo.getName(), clusterInfo.getMainPort()); //$NON-NLS-1$

          LOGGER.debug(
              "Try to autenticate to the cluster <{}> of server <{}>", //$NON-NLS-1$
              clusterName,
              getServerKey());
          agentConnection.authenticate(clusterId, userPass.getUsername(), userPass.getPassword());
          LOGGER.debug(
              "Authentication to the cluster <{}> of server <{}> was successful", //$NON-NLS-1$
              clusterName,
              getServerKey());

          userPass.setDescription(clusterName);

          // сохраняем новые user/pass после успешной авторизации
          // (если сохранение включено в настройках)
          if (saveNewUserpass && saveCredentialsVariant != SaveCredentialsVariant.DISABLE) {

            // TODO по идее достаточно очистить пароль, если в настройках указано, что не хранить
            // но этого креда могло еще не быть в хранилище, а значит его туда надо все же вставить

            if (saveCredentialsVariant == SaveCredentialsVariant.NAME) {
              userPass.setPassword("");
            }
            clustersCredentialsV03.put(clusterId, userPass);
            // TODO clusterProvider.saveConfig();

            LOGGER.debug(
                "New credentials for the cluster <{}> of server <{}> are saved", //$NON-NLS-1$
                clusterName,
                getServerKey());
          }
        };

    UserPassPair userPass = getClusterCredentials(clusterId);
    List<UserPassPair> userPasses =
        getAllClustersCredentials().values().stream().collect(Collectors.toList());
    String authTitle =
        String.format(
            Messages.getString("Server.AuthenticationOfClusterAdminnistrator"), //$NON-NLS-1$
            getServerKey(),
            getClusterInfo(clusterId).getName());

    return runAuthProcessWithRequestToUser(userPass, userPasses, authTitle, authAction);
  }

  private boolean runAuthProcessWithRequestToUser(
      UserPassPair userPass,
      List<UserPassPair> userPassesList,
      String authTitle,
      AuthenticateAction authAction) {

    // TODO требуется рефакторинг метода
    try {
      // Сперва пытаемся авторизоваться под сохраненной учеткой
      // (она может быть инициализирована пустыми строками)
      authAction.performAutenticate(userPass, false);
    } catch (Exception excp) {
      String authExcpMessage = excp.getLocalizedMessage();
      LOGGER.debug(
          "Autenticate to server <{}> error: <{}>", //$NON-NLS-1$
          getServerKey(),
          authExcpMessage);

      AuthenticateDialog authenticateDialog;
      int dialogResult;

      // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена
      while (true) {

        try {
          LOGGER.debug(
              "Requesting new user credentials for the server <{}>", //$NON-NLS-1$
              getServerKey());
          authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(),
                  userPass,
                  authTitle,
                  authExcpMessage,
                  userPassesList);
          dialogResult = authenticateDialog.open();
        } catch (Exception exc) {
          String excpMessage = exc.getLocalizedMessage();
          LOGGER.debug(
              "Request new user credentials for the server <{}> failed: <{}>", //$NON-NLS-1$
              getServerKey(),
              excpMessage);
          Helper.showMessageBox(excpMessage);
          return false;
        }

        if (dialogResult == 0) {
          LOGGER.debug(
              "The user has provided new credentials for the server <{}>", //$NON-NLS-1$
              getServerKey());
          userPass = authenticateDialog.getUserPass();
          try {
            authAction.performAutenticate(userPass, true);
            break;
          } catch (Exception exc) {
            authExcpMessage = exc.getLocalizedMessage();
            LOGGER.debug(
                "Autenticate to server <{}> error: <{}>", //$NON-NLS-1$
                getServerKey(),
                authExcpMessage);
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Добавление всех сохраненных кредов инфобаз в кластер.
   *
   * @param clusterId - ID кластера
   */
  public void provideSavedInfobasesCredentialsToCluster(UUID clusterId) {
    if (!isConnected()) {
      return;
    }

    infobasesCredentials.forEach(
        userpass ->
            agentConnection.addAuthentication(
                clusterId, userpass.getUsername(), userpass.getPassword()));

    LOGGER.debug(
        "Provide infobase credentials for the cluster <{}> of server <{}>", //$NON-NLS-1$
        clusterId,
        this.getServerKey());
  }

  /**
   * Добавляет параметры аутентификации информационной базы в контекст текущего подключения к
   * серверу администрирования.
   *
   * @param clusterId - ID кластера
   * @param userPass - имя и пароль администратора инфобазы
   */
  public void addInfobaseCredentials(UUID clusterId, UserPassPair userPass) {
    if (!isConnected()) {
      return;
    }

    agentConnection.addAuthentication(clusterId, userPass.getUsername(), userPass.getPassword());
    LOGGER.debug(
        "Add new infobase credentials for the cluster <{}> of server <{}>", //$NON-NLS-1$
        clusterId,
        this.getServerKey());
  }

  /**
   * Получение списка кластеров зарегистрированных на центральном сервере.
   *
   * @return список кластеров
   */
  public List<IClusterInfo> getClusters() {
    if (!isConnected()) {
      return new ArrayList<>();
    }

    LOGGER.debug(
        "Get the list of cluster descriptions registered on the central server <{}>", //$NON-NLS-1$
        this.getServerKey());

    List<IClusterInfo> clusters;
    try {
      clusters = agentConnection.getClusters();
    } catch (Exception excp) {
      LOGGER.error(
          "Error get of the list of cluster descriptions", //$NON-NLS-1$
          excp);
      return new ArrayList<>();
    }

    boolean needSaveConfig = false;
    for (IClusterInfo cluster : clusters) {
      LOGGER.debug(
          "\tCluster: name=<{}>, ID=<{}>, host:port=<{}:{}>", //$NON-NLS-1$
          cluster.getName(),
          cluster.getClusterId(),
          cluster.getHostName(),
          cluster.getMainPort());

      //      // обновление имени кластера в кеше credentials
      //      if (saveCredentials) {
      //        String[] credentialClustersCashe = clustersCredentials.get(cluster.getClusterId());
      //        if (credentialClustersCashe != null
      //            && !credentialClustersCashe[2].equals(cluster.getName())) {
      //          credentialClustersCashe[2] = cluster.getName();
      //          needSaveConfig = true;
      //        }
      //      }
    }
    if (needSaveConfig) {
      // TODO надо сохранить
    }

    return clusters;
  }

  /**
   * Получение информации о кластере.
   *
   * @param clusterId - ID кластера
   * @return cluster - информация о кластере
   */
  public IClusterInfo getClusterInfo(UUID clusterId) {
    if (!isConnected()) {
      return null;
    }

    LOGGER.debug("Get the cluster <{}> descriptions", clusterId); //$NON-NLS-1$

    IClusterInfo clusterInfo;
    try {
      clusterInfo = agentConnection.getClusterInfo(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the cluster descriptions", excp); //$NON-NLS-1$
      return null;
    }

    LOGGER.debug("Get the cluster descriptions succesful"); //$NON-NLS-1$
    return clusterInfo;
  }

  /**
   * Получение порта менеджера кластера.
   *
   * @param clusterId - ID кластера
   * @return String - порт менеджера кластера
   */
  public String getClusterMainPort(UUID clusterId) {
    IClusterInfo clusterInfo = getClusterInfo(clusterId);
    return clusterInfo == null ? "0" : String.valueOf(clusterInfo.getMainPort());
  }
  /**
   * Gets the list of cluster manager descriptions.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return cluster - list of cluster manager descriptions
   */
  private List<IClusterManagerInfo> getClusterManagers(UUID clusterId) {
    LOGGER.debug(
        "Gets the list of cluster manager descriptions in the cluster <{}>", //$NON-NLS-1$
        clusterId);

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IClusterManagerInfo> clusterManagers;
    // TODO
    try {
      clusterManagers = agentConnection.getClusterManagers(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the cluster manager descriptions", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    LOGGER.debug("Get the cluster manager descriptions succesful"); //$NON-NLS-1$
    return clusterManagers;
  }

  /**
   * Gets a cluster manager description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId ID кластера
   * @param managerId - manager ID
   * @return cluster manager description, or null if the manager with the specified ID does not
   *     exist
   */
  private IClusterManagerInfo getClusterManagerInfo(UUID clusterId, UUID managerId) {
    LOGGER.debug(
        "Get the cluster manager <{}> description of cluster  <{}>", //$NON-NLS-1$
        managerId,
        clusterId);

    if (!isConnected()) {
      return null;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    IClusterManagerInfo clusterManagerInfo;
    // TODO
    try {
      clusterManagerInfo = agentConnection.getClusterManagerInfo(clusterId, managerId);
    } catch (Exception excp) {
      LOGGER.error("Error get the cluster manager description", excp); //$NON-NLS-1$
      return null;
    }

    LOGGER.debug("Get the cluster manager descriptions succesful"); //$NON-NLS-1$
    return clusterManagerInfo;
  }

  /**
   * Creates a cluster or changes the state of an existing one.
   *
   * <p>Central server authentication is required
   *
   * @param clusterInfo - cluster description
   * @return {@code true} if cluster registration successful
   */
  public boolean regCluster(IClusterInfo clusterInfo) {
    if (clusterInfo.getClusterId().equals(Helper.EMPTY_UUID)) {
      LOGGER.debug(
          "Registration new cluster <{}>", //$NON-NLS-1$
          clusterInfo.getName());
    } else {
      LOGGER.debug(
          "Registration changes a cluster <{}>", //$NON-NLS-1$
          clusterInfo.getClusterId());
    }

    if (!isConnected()) {
      LOGGER.debug(
          "The connection a cluster <{}> is not established", //$NON-NLS-1$
          clusterInfo.getClusterId());
      return false;
    }

    if (!checkAutenticateAgent()) {
      return false;
    }

    UUID newClusterId;
    try {
      newClusterId = agentConnection.regCluster(clusterInfo);
    } catch (Exception excp) {
      LOGGER.error(
          "Error registraion cluster", //$NON-NLS-1$
          excp);
      return false;
    }

    if (clusterInfo.getClusterId().equals(Helper.EMPTY_UUID)) {
      LOGGER.debug(
          "Registration new cluster <{}> succesful", //$NON-NLS-1$
          newClusterId);
    } else {
      LOGGER.debug(
          "Registration changes a cluster <{}> succesful", //$NON-NLS-1$
          clusterInfo.getClusterId());
    }
    return true;
  }

  /**
   * Удаление кластера с сервера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return {@code true} при успешном удалении кластера
   */
  public boolean unregCluster(UUID clusterId) {
    LOGGER.debug("Delete a cluster <{}>", clusterId); //$NON-NLS-1$

    var unregSuccesful = false;
    String unregMessage = null;

    if (!isConnected()) {
      unregMessage =
          Messages.getString("Server.TheConnectionAClusterIsNotEstablished"); //$NON-NLS-1$
    }

    if (!checkAutenticateCluster(clusterId)) {
      unregMessage = Messages.getString("Server.TheClusterAuthenticationError"); //$NON-NLS-1$
    }

    if (unregMessage == null) {
      try {
        agentConnection.unregCluster(clusterId);
        unregSuccesful = true;
      } catch (Exception excp) {
        LOGGER.error("Error delete a cluster", excp); //$NON-NLS-1$
        unregMessage = excp.getLocalizedMessage();
      }
    }
    if (!unregSuccesful) {
      Helper.showMessageBox(unregMessage);
    }

    return unregSuccesful;
  }

  /**
   * Получение списка администраторов кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return список администраторов кластера
   */
  private List<IRegUserInfo> getClusterAdmins(UUID clusterId) {
    LOGGER.debug(
        "Gets the list of cluster administrators in the cluster <{}>", //$NON-NLS-1$
        clusterId);
    if (!isConnected()) {
      return new ArrayList<>();
    }

    // TODO
    return null;
  }

  /**
   * Удаление администратора кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param name - имя администратора
   */
  private void unregClusterAdmin(UUID clusterId, String name) {
    LOGGER.debug(
        "Deletes a cluster administrator in the cluster <{}>", //$NON-NLS-1$
        clusterId);
    if (!isConnected()) {
      return;
    }

    // TODO
    return;
  }

  /**
   * Получение списка краткого описания инфобаз кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId -ID кластера
   * @return список краткого описания инфобаз кластера
   */
  public List<InfoBaseInfoShortExt> getInfoBasesShort(UUID clusterId) {
    LOGGER.debug(
        "Get the list of short descriptions of infobases registered in the cluster <{}>", //$NON-NLS-1$
        clusterId);

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IInfoBaseInfoShort> clusterInfoBases;
    try {
      clusterInfoBases = agentConnection.getInfoBasesShort(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of short descriptions of infobases", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    List<InfoBaseInfoShortExt> clusterInfoBasesExt = new ArrayList<>();
    clusterInfoBases.forEach(
        ib -> {
          clusterInfoBasesExt.add(
              new InfoBaseInfoShortExt(
                  ib, clusterInfoBasesExt.size(), favoriteInfobases.contains(ib.getName())));

          LOGGER.debug(
              "\tInfobase: name=<{}>, desc=<{}>", //$NON-NLS-1$
              ib.getName(),
              ib.getDescr());
        });

    LOGGER.debug("Get the list of short descriptions of infobases succesful"); //$NON-NLS-1$

    Collections.sort(clusterInfoBasesExt);

    return clusterInfoBasesExt;
  }

  /**
   * Gets the list of full descriptions of infobases registered in the cluster.
   *
   * <p>Требует аутентификации в кластере For each infobase in the cluster, infobase authentication
   * is required If infobase authentication is not performed, only fields that correspond to short
   * infobase description fields will be filled
   *
   * @param clusterId - ID кластера
   * @return list of full descriptions of cluster infobases
   */
  public List<IInfoBaseInfo> getInfoBases(UUID clusterId) {
    LOGGER.debug(
        "Get the list of descriptions of infobases registered in the cluster <{}>", //$NON-NLS-1$
        clusterId);
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IInfoBaseInfo> clusterInfoBases;
    try { // TODO For each infobase in the cluster, infobase authentication is required
      clusterInfoBases = agentConnection.getInfoBases(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of infobases", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    clusterInfoBases.forEach(
        ib ->
            LOGGER.debug(
                "\tInfobase: name=<{}>, desc=<{}>", //$NON-NLS-1$
                ib.getName(),
                ib.getDescr()));

    LOGGER.debug("Get the list of descriptions of infobases succesful"); //$NON-NLS-1$
    return clusterInfoBases;
  }

  /**
   * Gets a short infobase description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return short infobase description
   */
  public IInfoBaseInfoShort getInfoBaseShortInfo(UUID clusterId, UUID infobaseId) {
    LOGGER.debug(
        "Get the short description for infobase <{}> of the cluster <{}>", //$NON-NLS-1$
        infobaseId,
        clusterId);
    if (!isConnected()) {
      return null;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    IInfoBaseInfoShort info;
    try {
      info = agentConnection.getInfoBaseShortInfo(clusterId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the short info for infobase", excp); //$NON-NLS-1$
      return null;
    }

    LOGGER.debug(
        "Get the short description for infobase <{}> succesful", //$NON-NLS-1$
        info.getName());
    return info;
  }

  /**
   * Gets the full infobase description.
   *
   * <p>Требует аутентификации в кластере Infobase authentication is required.
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return infobase full infobase description
   */
  public IInfoBaseInfo getInfoBaseInfo(UUID clusterId, UUID infobaseId) {
    LOGGER.debug(
        "Get the description for infobase <{}> of the cluster <{}>", //$NON-NLS-1$
        infobaseId,
        clusterId);
    if (!isConnected()) {
      return null;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    // addInfoBaseCredentials(clusterID, "", ""); // в добавлении пустых кредов нет необходимости

    IInfoBaseInfo infobaseInfo;

    try {
      infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
    } catch (Exception excp) {

      AuthenticateDialog authenticateDialog;
      String authExcpMessage = excp.getLocalizedMessage();
      int dialogResult;

      UserPassPair userPass = new UserPassPair();
      var authTitle = Messages.getString("Server.AuthenticationOfInfobase"); //$NON-NLS-1$

      // пока не подойдет пароль, или пользователь не нажмет Отмена
      while (true) {

        try {
          LOGGER.debug(
              "Requesting new user credentials for the infobase <{}>", //$NON-NLS-1$
              infobaseId);
          authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(),
                  userPass,
                  authTitle,
                  authExcpMessage,
                  infobasesCredentials);
          dialogResult = authenticateDialog.open();
        } catch (Exception exc) {
          String excpMessage = exc.getLocalizedMessage();
          LOGGER.debug(
              "Request new user credentials for the infobase failed: <{}>", //$NON-NLS-1$
              excpMessage);
          Helper.showMessageBox(excpMessage);
          return null;
        }

        if (dialogResult == 0) {
          LOGGER.debug(
              "The user has provided new credentials for the infobase <{}>", //$NON-NLS-1$
              infobaseId);
          userPass = authenticateDialog.getUserPass();
          try {
            addInfobaseCredentials(clusterId, userPass);
            infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
            saveInfobaseCredentials(userPass);
            break;
          } catch (Exception exc) {
            authExcpMessage = exc.getLocalizedMessage();
            LOGGER.debug(
                "Autenticate to infobase <{}> error: <{}>", //$NON-NLS-1$
                this.getServerKey(),
                authExcpMessage);
          }
        } else {
          LOGGER.debug(
              "Autenticate to infobase <{}> abort by the user", //$NON-NLS-1$
              this.getServerKey());
          return null;
        }
      }
    }

    return infobaseInfo;
  }

  /**
   * Gets the infobase name.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return infobase full infobase description
   */
  public String getInfoBaseName(UUID clusterId, UUID infobaseId) {
    LOGGER.debug(
        "Get the name for infobase <{}> of the cluster <{}>", //$NON-NLS-1$
        infobaseId,
        clusterId);

    if (infobaseId.equals(Helper.EMPTY_UUID)) {
      LOGGER.debug("ID инфобазы is empty"); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }

    IInfoBaseInfoShort infobaseShortInfo = getInfoBaseShortInfo(clusterId, infobaseId);
    return infobaseShortInfo == null ? "" : infobaseShortInfo.getName(); //$NON-NLS-1$
  }

  /**
   * Creates an infobase in a cluster.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param info - параметры инфобазы
   * @param infobaseCreationMode - infobase creation mode: •0 - do not create a database •1 - create
   *     a database
   * @return ID созданной инфобазы или пустой UUID
   */
  public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode) {
    LOGGER.debug(
        "Creates an infobase <{}> in a cluster <{}>", //$NON-NLS-1$
        info.getName(),
        clusterId);
    
    if (!isConnected()) {
      return Helper.EMPTY_UUID;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return Helper.EMPTY_UUID;
    }

    UUID uuid;
    try {
      uuid = agentConnection.createInfoBase(clusterId, info, infobaseCreationMode);
    } catch (Exception excp) {
      LOGGER.error("Error creates an infobase", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return Helper.EMPTY_UUID;
    }

    LOGGER.debug("Creates an infobase succesful"); //$NON-NLS-1$
    return uuid;
  }

  /**
   * Изменение краткого описания информационной базы.
   *
   * <p>Требует аутентификации в кластере.
   *
   * @param clusterId - ID кластера
   * @param info - infobase parameters
   */
  private void updateInfoBaseShort(UUID clusterId, IInfoBaseInfoShort info) {
    LOGGER.debug(
        "Changes short description infobase <{}> in the cluster <{}>", //$NON-NLS-1$
        info.getInfoBaseId(),
        clusterId);

    if (!isConnected()) {
      return;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return;
    }

    try { // TODO
      agentConnection.updateInfoBaseShort(clusterId, info);
    } catch (Exception excp) {
      LOGGER.error("Error changes short description infobase", excp); //$NON-NLS-1$
      return;
    }
  }

  /**
   * Изменение параметров информационной базы.
   *
   * <p>Требует аутентификации в инфобазе (Здесь не нужно аутентифицироваться в базе, метод
   * необходимо вызывать сразу после getInfoBaseInfo)
   *
   * @param clusterId - ID кластера
   * @param info - параметры инфобазы
   * @return {@code true} при успешном изменении
   */
  public boolean updateInfoBase(UUID clusterId, IInfoBaseInfo info) {

    try {
      agentConnection.updateInfoBase(clusterId, info);
    } catch (Exception excp) {
      LOGGER.error("Error changes description infobase", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }
    return true;
  }

  /**
   * Удаление информационной базы.
   *
   * <p>Требует аутентификации в инфобазе
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @param dropMode - режим удаления инфобазы: 0 - не удалять базу данных в СУБД, 1 - удалять базу
   *     данных в СУБД, 2 - очистить базу от данных
   * @return {@code true} if drop succesful
   */
  public boolean dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode) {

    if (!checkAutenticateInfobase(clusterId, infobaseId)) {
      return false;
    }

    try {
      agentConnection.dropInfoBase(clusterId, infobaseId, dropMode);
    } catch (Exception excp) {
      LOGGER.error("Error deletes an infobase", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }
    return true;
  }

  /**
   * Gets a session description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param sid - session ID
   * @return session description
   */
  public ISessionInfo getSessionInfo(UUID clusterId, UUID sid) {
    LOGGER.debug(
        "Gets a session <{}> description in the cluster <{}>", sid, clusterId); //$NON-NLS-1$
    if (!isConnected()) {
      return null;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    ISessionInfo sessionInfo;
    try {
      sessionInfo = agentConnection.getSessionInfo(clusterId, sid); // TODO  ошибка в библиотеке
    } catch (Exception excp) {
      LOGGER.error("Error get a session description", excp); //$NON-NLS-1$
      return null;
    }
    LOGGER.debug(
        "\tappId={}, sid={}, connectionId={}, sessionId={}, userName={}", //$NON-NLS-1$
        sessionInfo.getAppId(),
        sessionInfo.getSid(),
        sessionInfo.getConnectionId(),
        sessionInfo.getSessionId(),
        sessionInfo.getUserName());

    LOGGER.debug("Get the session description succesful"); //$NON-NLS-1$
    return sessionInfo;
  }

  /**
   * Получение списка сеансов кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return список сеансов кластера
   */
  public List<ISessionInfo> getSessions(UUID clusterId) {
    LOGGER.debug(
        "Gets the list of cluster session descriptions in the cluster <{}>", //$NON-NLS-1$
        clusterId);
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<ISessionInfo> sessions;
    try {
      sessions = agentConnection.getSessions(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of cluster session descriptions", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }
    
    LOGGER.debug("Sessions:");
    sessions.forEach(
        s ->
            LOGGER.debug(
                "\tappId={}, sid={}, connectionId={}, sessionId={}, userName={}", //$NON-NLS-1$
                s.getAppId(),
                s.getSid(),
                s.getConnectionId(),
                s.getSessionId(),
                s.getUserName()));

    LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
    return sessions;
  }

  //  /**
  //   * Gets the extended list of cluster session descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @return Extended list of session descriptions
  //   */
  //  public List<SessionInfoExtended> getSessionsExtended(UUID clusterId) {
  //    return convertSessionsInfoToSessionsExtended(clusterId, getSessions(clusterId));
  //  }

  /**
   * Gets the list of infobase session descriptions.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return Infobase sessions
   */
  private List<ISessionInfo> getInfoBaseSessions(UUID clusterId, UUID infobaseId) {
    LOGGER.debug(
        "Gets the list of infobase <{}> session descriptions in the cluster <{}>", //$NON-NLS-1$
        infobaseId,
        clusterId);
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<ISessionInfo> sessions;
    try {
      sessions = agentConnection.getInfoBaseSessions(clusterId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of infobase session descriptions", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    LOGGER.debug("Sessions:");
    sessions.forEach(
        s ->
            LOGGER.debug(
                "\tappId={}, sid={}, connectionId={}, sessionId={}, userName={}", //$NON-NLS-1$
                s.getAppId(),
                s.getSid(),
                s.getConnectionId(),
                s.getSessionId(),
                s.getUserName()));

    LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
    return sessions;
  }

  //  /**
  //   * Gets the extended list of infobase session descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @param infobaseId - ID инфобазы
  //   * @return Extended list of session descriptions
  //   */
  //  public List<SessionInfoExtended> getInfoBaseSessionsExtended(UUID clusterId, UUID infobaseId)
  // {
  //    return convertSessionsInfoToSessionsExtended(
  //        clusterId, getInfoBaseSessions(clusterId, infobaseId));
  //  }

  /**
   * Gets the list of working process session descriptions.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param workingProcessId - Working process ID
   * @return Working process sessions
   */
  private List<ISessionInfo> getWorkingProcessSessions(UUID clusterId, UUID workingProcessId) {

    return getSessions(clusterId).stream()
        .filter(s -> s.getWorkingProcessId().equals(workingProcessId))
        .collect(Collectors.toList());
  }

  //  /**
  //   * Gets the extended list of infobase session descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @param workingProcessId - working process ID
  //   * @return Extended list of session descriptions
  //   */
  //  public List<SessionInfoExtended> getWorkingProcessSessionsExtended(
  //      UUID clusterId, UUID workingProcessId) {
  //    return convertSessionsInfoToSessionsExtended(
  //        clusterId, getWorkingProcessSessions(clusterId, workingProcessId));
  //  }

  /**
   * Gets the extended list of cluster session descriptions.
   *
   * @param treeItemType - tree item type
   * @param clusterId - ID кластера
   * @param workingProcessId - working process ID
   * @param infobaseId - ID инфобазы
   * @return Extended list of session descriptions
   */
  public List<BaseInfoExtended> getSessionsExtendedInfo(
      TreeItemType treeItemType, UUID clusterId, UUID workingProcessId, UUID infobaseId) {

    List<ISessionInfo> sessions;

    switch (treeItemType) {
      case SERVER:
        return new ArrayList<>();

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
        sessions = getSessions(clusterId);
        break;

      case WORKINGPROCESS:
        sessions = getWorkingProcessSessions(clusterId, workingProcessId);
        break;

      case INFOBASE:
        sessions = getInfoBaseSessions(clusterId, infobaseId);
        break;

      default:
        return new ArrayList<>();
    }

    return convertSessionsInfoToSessionsExtended(clusterId, sessions);
  }

  private List<BaseInfoExtended> convertSessionsInfoToSessionsExtended(
      UUID clusterId, List<ISessionInfo> sessions) {

    List<BaseInfoExtended> sessionsExtended = new ArrayList<>();
    sessions.forEach(
        session -> sessionsExtended.add(new SessionInfoExtended(this, clusterId, session)));
    Collections.sort(sessionsExtended);

    return sessionsExtended;
  }

  /**
   * Terminates a session in the cluster with default message.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param sessionId - ID инфобазы
   * @return sucess terminate session
   */
  public boolean terminateSession(UUID clusterId, UUID sessionId) {
    return terminateSession(
        clusterId, sessionId, Messages.getString("Server.TerminateSessionMessage")); //$NON-NLS-1$
  }

  /**
   * Terminates a session in the cluster.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param sessionId - ID инфобазы
   * @param message - error message for user
   * @return sucess terminate session
   */
  public boolean terminateSession(UUID clusterId, UUID sessionId, String message) {
    LOGGER.debug(
        "Terminates a session <{}> in the cluster <{}>", //$NON-NLS-1$
        sessionId,
        clusterId);
    if (!isConnected()) {
      return false;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }

    try {
      agentConnection.terminateSession(clusterId, sessionId, message);
    } catch (Exception excp) {
      LOGGER.error("Error terminate a session", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }
    LOGGER.debug("Terminates a session succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Terminates all sessions for all infobases in the cluster.
   *
   * @param clusterId - ID кластера
   */
  public void terminateAllSessions(UUID clusterId) {

    getSessions(clusterId).forEach(session -> terminateSession(clusterId, session.getSid()));
  }

  /**
   * Terminates all sessions for infobase in the cluster.
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @param onlyUsersSession - terminate only users sessions
   */
  public void terminateAllSessionsOfInfobase(
      UUID clusterId, UUID infobaseId, boolean onlyUsersSession) {

    List<ISessionInfo> sessions = agentConnection.getInfoBaseSessions(clusterId, infobaseId);
    for (ISessionInfo session : sessions) {
      if (onlyUsersSession && !isUserSession(session)) {
        continue;
      }

      terminateSession(clusterId, session.getSid());
    }
  }

  private boolean isUserSession(ISessionInfo session) {
    String appName = session.getAppId();
    return appName.equals(THIN_CLIENT) || appName.equals(THICK_CLIENT);
  }

  /**
   * Gets a short description of a connection.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param connectionId - connection ID
   * @return short connection description
   */
  public IInfoBaseConnectionShort getConnectionInfoShort(UUID clusterId, UUID connectionId) {
    LOGGER.debug(
        "Gets a connection <{}> short description in the cluster <{}>", //$NON-NLS-1$
        connectionId,
        clusterId);
    if (!isConnected()) {
      return null;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    IInfoBaseConnectionShort connectionInfo;
    try {
      connectionInfo = agentConnection.getConnectionInfoShort(clusterId, connectionId);
    } catch (Exception excp) {
      LOGGER.error("Error get a short description of a connection", excp); //$NON-NLS-1$
      return null;
    }
    //    LOGGER.debug(
    //        "\tConnection: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
    //        getApplicationName(sessionInfo.getAppId()),
    //        sessionInfo.getSessionId());

    LOGGER.debug("Get the short description of a connection succesful"); //$NON-NLS-1$
    return connectionInfo;
  }

  /**
   * Gets the list of short descriptions of cluster connections.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of short cluster connection descriptions
   */
  private List<IInfoBaseConnectionShort> getConnectionsShort(UUID clusterId) {
    LOGGER.debug(
        "Gets the list of short descriptions connections of cluster <{}>", //$NON-NLS-1$
        clusterId);

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IInfoBaseConnectionShort> connections;
    try {
      connections = agentConnection.getConnectionsShort(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of short descriptions connections", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return connections;
  }

  //  /**
  //   * Gets the extended list of cluster connection descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @return Extended list of session descriptions
  //   */
  //  public List<ConnectionInfoExtended> getConnectionsExtended(UUID clusterId) {
  //    return convertConnectionsInfoToConnectionsExtended(clusterId,
  // getConnectionsShort(clusterId));
  //  }

  /**
   * Gets the list of short descriptions of infobase connections.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return list of short infobase connection descriptions
   */
  private List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(
      UUID clusterId, UUID infobaseId) {

    LOGGER.debug(
        "Gets the list of short descriptions connections of cluster <{}>, infobase <{}>", //$NON-NLS-1$
        clusterId,
        infobaseId);

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IInfoBaseConnectionShort> connections;
    try {
      connections = agentConnection.getInfoBaseConnectionsShort(clusterId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of short descriptions connections", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return connections;
  }

  //  /**
  //   * Gets the extended list of infobase connection descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @param infobaseId - ID инфобазы
  //   * @return Extended list of session descriptions
  //   */
  //  public List<ConnectionInfoExtended> getInfoBaseConnectionsExtended(
  //      UUID clusterId, UUID infobaseId) {
  //    return convertConnectionsInfoToConnectionsExtended(
  //        clusterId, getInfoBaseConnectionsShort(clusterId, infobaseId));
  //  }

  /**
   * Возвращает список соединений информационной базы для рабочего процесса.
   *
   * <p>Требует аутентификации в кластере, Требует аутентификации в инфобазе.
   *
   * @param clusterId - ID кластера
   * @param workingProcessId - ID рабочего процесса
   * @param infobaseId - ID инфобазы
   * @return список соединений к инфобазе
   */
  private List<IInfoBaseConnectionInfo> getInfoBaseConnections(
      UUID clusterId, UUID workingProcessId, UUID infobaseId) {

    LOGGER.debug(
        "Gets the list of descriptions connections of cluster <{}>, infobase <{}>", //$NON-NLS-1$
        clusterId,
        infobaseId);

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IInfoBaseConnectionInfo> connections;
    try { // TODO
      connections = agentConnection.getInfoBaseConnections(clusterId, workingProcessId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions connections", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return connections;
  }

  /**
   * Gets the list of connection descriptions for a working process.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param workingProcessId - working process ID
   * @return list of connection descriptions for a working process
   */
  private List<IInfoBaseConnectionShort> getWorkingProcessConnectionsShort(
      UUID clusterId, UUID workingProcessId) {
    if (isConnected()) {

      return getConnectionsShort(clusterId).stream()
          .filter(c -> c.getWorkingProcessId().equals(workingProcessId))
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }

  //  /**
  //   * Gets the extended list of working processId connection descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @param workingProcessId - workingProcess ID
  //   * @return Extended list of session descriptions
  //   */
  //  public List<ConnectionInfoExtended> getWorkingProcessConnectionsExtended(
  //      UUID clusterId, UUID workingProcessId) {
  //    return convertConnectionsInfoToConnectionsExtended(
  //        clusterId, getWorkingProcessConnectionsShort(clusterId, workingProcessId));
  //  }

  /**
   * Gets the extended list of cluster connections descriptions.
   *
   * @param treeItemType - tree item type
   * @param clusterId - ID кластера
   * @param workingProcessId - working process ID
   * @param infobaseId - ID инфобазы
   * @return Extended list of connection descriptions
   */
  public List<BaseInfoExtended> getConnectionsExtendedInfo(
      TreeItemType treeItemType, UUID clusterId, UUID workingProcessId, UUID infobaseId) {

    List<IInfoBaseConnectionShort> sessions;

    switch (treeItemType) {
      case SERVER:
        return new ArrayList<>();

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
        sessions = getConnectionsShort(clusterId);
        break;

      case WORKINGPROCESS:
        sessions = getWorkingProcessConnectionsShort(clusterId, workingProcessId);
        break;

      case INFOBASE:
        sessions = getInfoBaseConnectionsShort(clusterId, infobaseId);
        break;

      default:
        return new ArrayList<>();
    }

    return convertConnectionsInfoToConnectionsExtended(clusterId, sessions);
  }

  private List<BaseInfoExtended> convertConnectionsInfoToConnectionsExtended(
      UUID clusterId, List<IInfoBaseConnectionShort> connections) {

    List<BaseInfoExtended> connectionsExtended = new ArrayList<>();
    connections.forEach(
        connection ->
            connectionsExtended.add(new ConnectionInfoExtended(this, clusterId, connection)));
    Collections.sort(connectionsExtended);

    return connectionsExtended;
  }

  /**
   * Closes an infobase connection.
   *
   * <p>Требует аутентификации в кластере Infobase authentication is required.
   *
   * @param clusterId - ID кластера
   * @param processId - working process ID
   * @param connectionId - connection ID
   * @param infobaseId - ID инфобазы
   * @return {@code true} if successful shutdown
   */
  public boolean disconnectConnection(
      UUID clusterId, UUID processId, UUID connectionId, UUID infobaseId) {
    LOGGER.debug(
        "Close connection in the cluster <{}>, processId <{}>, connectionId  <{}>", //$NON-NLS-1$
        clusterId,
        processId,
        connectionId);

    if (!isConnected()) {
      return false;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }
    if (!checkAutenticateInfobase(clusterId, infobaseId)) {
      return false;
    }

    try {
      agentConnection.disconnect(clusterId, processId, connectionId);
    } catch (Exception excp) {
      LOGGER.error("Error close connection", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }
    LOGGER.debug("Close connection succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Interrupt current server call.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param sid - session ID
   * @param message - interrupt message
   */
  public void interruptCurrentServerCall(UUID clusterId, UUID sid, String message) {
    if (isConnected()) {
      agentConnection.interruptCurrentServerCall(clusterId, sid, message);
    }
    // TODO
  }

  /**
   * Gets the list of object locks.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of object lock descriptions
   */
  private List<IObjectLockInfo> getLocks(UUID clusterId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try {
      locks = agentConnection.getLocks(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  //  /**
  //   * Gets the extended list of locks descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @return Extended list of session descriptions
  //   */
  //  public List<LockInfoExtended> getLocksExtended(UUID clusterId) {
  //    return convertLocksInfoToLocksExtended(clusterId, getLocks(clusterId));
  //  }

  /**
   * Gets the list of infobase object locks.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return list of object lock descriptions
   */
  private List<IObjectLockInfo> getInfoBaseLocks(UUID clusterId, UUID infobaseId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try {
      locks = agentConnection.getInfoBaseLocks(clusterId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  //  /**
  //   * Gets the extended list of infobase locks descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @param infobaseId - ID инфобазы
  //   * @return Extended list of session descriptions
  //   */
  //  public List<LockInfoExtended> getInfoBaseLocksExtended(UUID clusterId, UUID infobaseId) {
  //    return convertLocksInfoToLocksExtended(clusterId, getInfoBaseLocks(clusterId, infobaseId));
  //  }

  /**
   * Gets the list of connection object locks.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param connectionId - connection ID
   * @return list of object lock descriptions
   */
  private List<IObjectLockInfo> getConnectionLocks(UUID clusterId, UUID connectionId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO
      locks = agentConnection.getConnectionLocks(clusterId, connectionId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  /**
   * Gets the list of session object locks.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @param sid - session ID
   * @return list of object lock descriptions
   */
  private List<IObjectLockInfo> getSessionLocks(UUID clusterId, UUID infobaseId, UUID sid) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO
      locks = agentConnection.getSessionLocks(clusterId, infobaseId, sid);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  /**
   * Gets the extended list of cluster session descriptions.
   *
   * @param treeItemType - tree item type
   * @param clusterId - ID кластера
   * @param infobaseId - ID инфобазы
   * @return Extended list of session descriptions
   */
  public List<BaseInfoExtended> getLocksExtendedInfo(
      TreeItemType treeItemType, UUID clusterId, UUID infobaseId) {

    List<IObjectLockInfo> locks;

    switch (treeItemType) {
      case SERVER:
        return new ArrayList<>();

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
        locks = getLocks(clusterId);
        break;

      case WORKINGPROCESS:
        locks = new ArrayList<>();
        break;

      case INFOBASE:
        locks = getInfoBaseLocks(clusterId, infobaseId);
        break;

      default:
        return new ArrayList<>();
    }

    return convertLocksInfoToLocksExtended(clusterId, locks);
  }

  private List<BaseInfoExtended> convertLocksInfoToLocksExtended(
      UUID clusterId, List<IObjectLockInfo> locks) {

    List<BaseInfoExtended> locksExtended = new ArrayList<>();
    locks.forEach(
        connection -> locksExtended.add(new LockInfoExtended(this, clusterId, connection)));
    Collections.sort(locksExtended);

    return locksExtended;
  }

  /**
   * Получение списка рабочих процессов кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return Список рабочих процессов кластера
   */
  public List<IWorkingProcessInfo> getWorkingProcesses(UUID clusterId) {
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IWorkingProcessInfo> workingProcesses;
    try {
      LOGGER.debug(
          "Gets the list of descriptions of working processes in the cluster <{}>", //$NON-NLS-1$
          clusterId);
      workingProcesses = agentConnection.getWorkingProcesses(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of working processes", excp); //$NON-NLS-1$

      return new ArrayList<>();
    }
    workingProcesses.forEach(
        wp ->
            LOGGER.debug(
                "\tWorking process: host name=<{}>, main port=<{}>", //$NON-NLS-1$
                wp.getHostName(),
                wp.getMainPort()));

    LOGGER.debug("Get the list of descriptions of working processes succesful"); //$NON-NLS-1$
    return workingProcesses;
  }

  //  /**
  //   * Gets the extended list of infobase connection descriptions.
  //   *
  //   * @param clusterId - ID кластера
  //   * @return Extended list of session descriptions
  //   */
  //  public List<WorkingProcessInfoExtended> getWorkingProcessesExtended(UUID clusterId) {
  //    return convertWorkingProcessInfoToWorkingProcessExtended(
  //        clusterId, getWorkingProcesses(clusterId));
  //  }

  /**
   * Получение списка расширенной информации о рабочих процессах.
   *
   * @param treeItemType - тип элемента дерева, для которого идет получение списка
   * @param clusterId - ID кластера
   * @param workingProcessId - working process ID
   * @return Список расширенной информации о рабочих процессах
   */
  public List<BaseInfoExtended> getWorkingProcessesExtendedInfo(
      TreeItemType treeItemType, UUID clusterId, UUID workingProcessId) {

    List<IWorkingProcessInfo> workingProcesses;

    switch (treeItemType) {
      case SERVER:
        return new ArrayList<>();

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
        workingProcesses = getWorkingProcesses(clusterId);
        break;

      case WORKINGPROCESS:
        workingProcesses = new ArrayList<>();
        workingProcesses.add(this.getWorkingProcessInfo(clusterId, workingProcessId));
        break;

      case INFOBASE:
        // TODO отметить рп обслуживающий базу
        workingProcesses = getWorkingProcesses(clusterId);
        break;

      default:
        return new ArrayList<>();
    }

    return convertWorkingProcessInfoToWorkingProcessExtended(clusterId, workingProcesses);
  }

  private List<BaseInfoExtended> convertWorkingProcessInfoToWorkingProcessExtended(
      UUID clusterId, List<IWorkingProcessInfo> workingProcesses) {

    List<BaseInfoExtended> workingProcessesExtended = new ArrayList<>();
    workingProcesses.forEach(
        wp -> workingProcessesExtended.add(new WorkingProcessInfoExtended(this, clusterId, wp)));
    Collections.sort(workingProcessesExtended);

    return workingProcessesExtended;
  }

  /**
   * Gets a working process description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param processId - working process ID
   * @return working process description, or null if the working process with the specified ID does
   *     not exist
   */
  public IWorkingProcessInfo getWorkingProcessInfo(UUID clusterId, UUID processId) {
    LOGGER.debug(
        "Gets a working process <{}> description in the cluster <{}>", //$NON-NLS-1$
        processId,
        clusterId);

    if (!isConnected()) {
      return null;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }
    
    IWorkingProcessInfo workingProcessInfo;
    try {
      workingProcessInfo = agentConnection.getWorkingProcessInfo(clusterId, processId);
    } catch (Exception excp) {
      LOGGER.error("Error get a working process description", excp); //$NON-NLS-1$
      return null;
    }

    return workingProcessInfo;
  }

  /**
   * Gets the list of descriptions of working processes of a working server.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - working server ID
   * @return list of working process descriptions
   */
  public List<IWorkingProcessInfo> getServerWorkingProcesses(UUID clusterId, UUID serverId) {
    if (isConnected()) {
      return agentConnection.getServerWorkingProcesses(clusterId, serverId);
    }
    // TODO
    return new ArrayList<>();
  }

  /**
   * Получение списка рабочих серверов, зарегистрированных на кластере.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return Список рабочих серверов
   */
  public List<IWorkingServerInfo> getWorkingServers(UUID clusterId) {
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IWorkingServerInfo> workingServers;
    try {
      LOGGER.debug(
          "Gets the list of descriptions of working servers in the cluster <{}>", //$NON-NLS-1$
          clusterId);
      workingServers = agentConnection.getWorkingServers(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of working servers", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }
    workingServers.forEach(
        ws ->
            LOGGER.debug(
                "\tWorking server: host name=<{}>, main port=<{}>", //$NON-NLS-1$
                ws.getHostName(),
                ws.getMainPort()));

    LOGGER.debug("Get the list of descriptions of working servers succesful"); //$NON-NLS-1$
    return workingServers;
  }

  /**
   * Получение списка расширенной информации о рабочих серверах, зарегистрированных на кластере.
   *
   * @param treeItemType - тип элемента дерева, дял которого идет получение списка
   * @param clusterId - ID кластера
   * @return Список расширенной информации о рабочих серверах
   */
  public List<BaseInfoExtended> getWorkingServersExtendedInfo(
      TreeItemType treeItemType, UUID clusterId) {

    switch (treeItemType) {
      case SERVER:
        return new ArrayList<>();

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
      case WORKINGPROCESS:
      case INFOBASE:
        return convertWorkingServersInfoToWorkingServersExtended(
            clusterId, getWorkingServers(clusterId));

      default:
        return new ArrayList<>();
    }
  }

  private List<BaseInfoExtended> convertWorkingServersInfoToWorkingServersExtended(
      UUID clusterId, List<IWorkingServerInfo> workingServers) {

    List<BaseInfoExtended> workingServersExtended = new ArrayList<>();
    workingServers.forEach(
        ws -> workingServersExtended.add(new WorkingServerInfoExtended(this, clusterId, ws)));
    Collections.sort(workingServersExtended);

    return workingServersExtended;
  }

  /**
   * Получение информации о рабочем сервере кластера.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - ID рабочего сервера
   * @return информация о рабочем сервере, или null если рабочий сервер с указанным ID не существует
   */
  public IWorkingServerInfo getWorkingServerInfo(UUID clusterId, UUID serverId) {
    if (!isConnected()) {
      return null;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    IWorkingServerInfo workingServerInfo;
    try {
      LOGGER.debug(
          "Gets the description of working server <{}> in the cluster <{}>", //$NON-NLS-1$
          serverId,
          clusterId);
      workingServerInfo = agentConnection.getWorkingServerInfo(clusterId, serverId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of working server", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return null;
    }

    LOGGER.debug(
        "\tWorking server: host name=<{}>, main port=<{}>", //$NON-NLS-1$
        workingServerInfo.getHostName(),
        workingServerInfo.getMainPort());

    LOGGER.debug(
        "Get the list of short descriptions of working processes succesful"); //$NON-NLS-1$
    return workingServerInfo;
  }

  /**
   * Создание рабочего сервера или изменение существующего экземпляра.
   *
   * <p>Требует аутентификации в кластере.
   *
   * @param clusterId - ID кластера
   * @param wsInfo - объект-описание рабочего сервера
   * @param createNew - признак создания нового сервера
   * @return {@code true} при успешном создании или изменении рабочего сервера
   */
  public boolean regWorkingServer(UUID clusterId, IWorkingServerInfo wsInfo, boolean createNew) {
    if (!isConnected()) {
      return false;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }

    if (createNew) {
      LOGGER.debug("Registration NEW working server"); //$NON-NLS-1$
    }

    try {
      LOGGER.debug(
          "Registration working server <{}> registered in the cluster <{}>", //$NON-NLS-1$
          wsInfo.getName(),
          clusterId);
      agentConnection.regWorkingServer(clusterId, wsInfo);
    } catch (Exception excp) {
      LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }

    LOGGER.debug(
        "\tRegistration working server: name=<{}>, host name=<{}>, main port=<{}>", //$NON-NLS-1$
        wsInfo.getName(),
        wsInfo.getHostName(),
        wsInfo.getMainPort());

    LOGGER.debug("Registration working server succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Удаляет рабочий сервер и удаляет его регистрацию в кластере.
   *
   * <p>Требует аутентификации в кластере.
   *
   * @param clusterId - ID кластера
   * @param serverId - ID рабочего сервера
   * @return {@code true} при успешном удалении рабочего сервера
   */
  public boolean unregWorkingServer(UUID clusterId, UUID serverId) {
    if (!isConnected()) {
      return false;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }

    try {
      LOGGER.debug(
          "Deletes a working server <{}> from the cluster <{}>", //$NON-NLS-1$
          serverId,
          clusterId);
      agentConnection.unregWorkingServer(clusterId, serverId);
    } catch (Exception excp) {
      LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
      Helper.showMessageBox(excp.getLocalizedMessage());
      return false;
    }

    LOGGER.debug("Unregistration working server succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Gets the list of cluster service descriptions.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of cluster service descriptions
   */
  public List<IClusterServiceInfo> getClusterServices(UUID clusterId) {
    // TODO
    return agentConnection.getClusterServices(clusterId);
  }

  /**
   * Applies assignment rules.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param full - assigment rule application mode: 0 - partial 1 - full
   */
  public void applyAssignmentRules(UUID clusterId, int full) {
    // TODO
    agentConnection.applyAssignmentRules(clusterId, full);
  }

  /**
   * Gets the list of descriptions of working server assignment rules.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - server ID
   * @return infobase full infobase description
   */
  public List<IAssignmentRuleInfo> getAssignmentRules(UUID clusterId, UUID serverId) {
    // TODO
    return agentConnection.getAssignmentRules(clusterId, serverId);
  }

  /**
   * Creates an assignment rule, changes an existing one, or moves an existing rule to a new
   * position.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - server ID
   * @param info - assignment rule description
   * @param position - position in the rule list (starts from 0)
   * @return ID of the created rule, or null if an existing assignment rule was changed
   */
  public UUID regAssignmentRule(
      UUID clusterId, UUID serverId, IAssignmentRuleInfo info, int position) {
    // TODO
    return agentConnection.regAssignmentRule(clusterId, serverId, info, position);
  }

  /**
   * Deletes an assignment rule from the list of working server rules.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - working server ID
   * @param ruleId - assignment rule ID
   */
  public void unregAssignmentRule(UUID clusterId, UUID serverId, UUID ruleId) {
    // TODO
    agentConnection.unregAssignmentRule(clusterId, serverId, ruleId);
  }

  /**
   * Gets an assignment rule description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param serverId - server ID
   * @param ruleId - assignment rule ID
   * @return assignment rule description
   */
  public IAssignmentRuleInfo getAssignmentRuleInfo(UUID clusterId, UUID serverId, UUID ruleId) {
    // TODO
    return agentConnection.getAssignmentRuleInfo(clusterId, serverId, ruleId);
  }

  /**
   * Gets the list of cluster security profiles.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of cluster security profiles
   */
  public List<ISecurityProfile> getSecurityProfiles(UUID clusterId) {
    // TODO
    return agentConnection.getSecurityProfiles(clusterId);
  }

  /**
   * Creates or updates a cluster security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param profile -security profile
   */
  public void createSecurityProfile(UUID clusterId, ISecurityProfile profile) {
    // TODO
    agentConnection.createSecurityProfile(clusterId, profile);
  }

  /**
   * Deletes a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   */
  public void dropSecurityProfile(UUID clusterId, String spName) {
    // TODO
    agentConnection.dropSecurityProfile(clusterId, spName);
  }

  /**
   * Gets the list of virtual directories of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return list of virtual directories
   */
  public List<ISecurityProfileVirtualDirectory> getSecurityProfileVirtualDirectories(
      UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileVirtualDirectories(clusterId, spName);
  }

  /**
   * Creates or updates a virtual directory of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param directory - virtual directory
   */
  public void createSecurityProfileVirtualDirectory(
      UUID clusterId, ISecurityProfileVirtualDirectory directory) {
    // TODO
    agentConnection.createSecurityProfileVirtualDirectory(clusterId, directory);
  }

  /**
   * Deletes a virtual directory of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @param alias - virtual directory alias
   */
  public void dropSecurityProfileVirtualDirectory(UUID clusterId, String spName, String alias) {
    // TODO
    agentConnection.dropSecurityProfileVirtualDirectory(clusterId, spName, alias);
  }

  /**
   * Gets the list of allowed COM classes of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return infobase full infobase description
   */
  public List<ISecurityProfileCOMClass> getSecurityProfileComClasses(
      UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileComClasses(clusterId, spName);
  }

  /**
   * Creates or updates an allowed COM class of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param comClass - allowed COM class
   */
  public void createSecurityProfileComClass(UUID clusterId, ISecurityProfileCOMClass comClass) {
    // TODO
    agentConnection.createSecurityProfileComClass(clusterId, comClass);
  }

  /**
   * Deletes an allowed COM class of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @param name - COM class name
   */
  public void dropSecurityProfileComClass(UUID clusterId, String spName, String name) {
    // TODO
    agentConnection.dropSecurityProfileComClass(clusterId, spName, name);
  }

  /**
   * Gets the list of allowed add-ins of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return list of allowed add-ins
   */
  public List<ISecurityProfileAddIn> getSecurityProfileAddIns(UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileAddIns(clusterId, spName);
  }

  /**
   * Creates or updates an allowed add-in of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param addIn - allowed add-in
   */
  public void createSecurityProfileAddIn(UUID clusterId, ISecurityProfileAddIn addIn) {
    // TODO
    agentConnection.createSecurityProfileAddIn(clusterId, addIn);
  }

  /**
   * Deletes an allowed add-in of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName -security profile name
   * @param name -add-in name
   */
  public void dropSecurityProfileAddIn(UUID clusterId, String spName, String name) {
    // TODO
    agentConnection.dropSecurityProfileAddIn(clusterId, spName, name);
  }

  /**
   * Gets the list of allowed unsafe external modules of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return list of allowed external modules
   */
  public List<ISecurityProfileExternalModule> getSecurityProfileUnsafeExternalModules(
      UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileUnsafeExternalModules(clusterId, spName);
  }

  /**
   * Creates or updates an allowed unsafe external module of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param module - allowed external module
   */
  public void createSecurityProfileUnsafeExternalModule(
      UUID clusterId, ISecurityProfileExternalModule module) {
    // TODO
    agentConnection.createSecurityProfileUnsafeExternalModule(clusterId, module);
  }

  /**
   * Deletes an allowed unsafe external module of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @param name - external module name
   */
  public void dropSecurityProfileUnsafeExternalModule(UUID clusterId, String spName, String name) {
    // TODO
    agentConnection.dropSecurityProfileUnsafeExternalModule(clusterId, spName, name);
  }

  /**
   * Gets the list of allowed applications of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return list of allowed applications
   */
  public List<ISecurityProfileApplication> getSecurityProfileApplications(
      UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileApplications(clusterId, spName);
  }

  /**
   * Creates or updates an allowed application of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param app - allowed application
   */
  public void createSecurityProfileApplication(UUID clusterId, ISecurityProfileApplication app) {
    // TODO
    agentConnection.createSecurityProfileApplication(clusterId, app);
  }

  /**
   * Deletes an allowed application of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @param name - application name
   */
  public void dropSecurityProfileApplication(UUID clusterId, String spName, String name) {
    // TODO
    agentConnection.dropSecurityProfileApplication(clusterId, spName, name);
  }

  /**
   * Gets the list of Internet resources of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @return infobase full infobase description
   */
  public List<ISecurityProfileInternetResource> getSecurityProfileInternetResources(
      UUID clusterId, String spName) {
    // TODO
    return agentConnection.getSecurityProfileInternetResources(clusterId, spName);
  }

  /**
   * Creates or updates an Internet resource of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param resource - Internet resource
   */
  public void createSecurityProfileInternetResource(
      UUID clusterId, ISecurityProfileInternetResource resource) {
    // TODO
    agentConnection.createSecurityProfileInternetResource(clusterId, resource);
  }

  /**
   * Deletes an Internet resource of a security profile.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param spName - security profile name
   * @param name - Internet resource name
   */
  public void dropSecurityProfileInternetResource(UUID clusterId, String spName, String name) {
    // TODO
    agentConnection.dropSecurityProfileInternetResource(clusterId, spName, name);
  }

  /**
   * Gets the list of resource counters.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of cluster resource counters
   */
  public List<IResourceConsumptionCounter> getResourceConsumptionCounters(UUID clusterId) {
    // TODO
    return agentConnection.getResourceConsumptionCounters(clusterId);
  }

  /**
   * Gets resource counters description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counterName - resource counter name
   * @return cluster resource counter info
   */
  public IResourceConsumptionCounter getResourceConsumptionCounterInfo(
      UUID clusterId, String counterName) {
    // TODO
    return agentConnection.getResourceConsumptionCounterInfo(clusterId, counterName);
  }

  /**
   * Creates or updates a resource counter.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counter - resource counter info
   */
  public void regResourceConsumptionCounter(UUID clusterId, IResourceConsumptionCounter counter) {
    // TODO
    agentConnection.regResourceConsumptionCounter(clusterId, counter);
  }

  /**
   * Deletes a resource counter.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counterName - resource counter name
   */
  public void unregResourceConsumptionCounter(UUID clusterId, String counterName) {
    // TODO
    agentConnection.unregResourceConsumptionCounter(clusterId, counterName);
  }

  /**
   * Gets the list of resource limits.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @return list of cluster resource limits
   */
  public List<IResourceConsumptionLimit> getResourceConsumptionLimits(UUID clusterId) {
    // TODO
    return agentConnection.getResourceConsumptionLimits(clusterId);
  }

  /**
   * Gets resource limits description.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param limitName - resource limit name
   * @return cluster resource counter info
   */
  public IResourceConsumptionLimit getResourceConsumptionLimitInfo(
      UUID clusterId, String limitName) {
    // TODO
    return agentConnection.getResourceConsumptionLimitInfo(clusterId, limitName);
  }

  /**
   * Creates or updates a resource limit.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param limit - cluster resource limit info
   */
  public void regResourceConsumptionLimit(UUID clusterId, IResourceConsumptionLimit limit) {
    // TODO
    agentConnection.regResourceConsumptionLimit(clusterId, limit);
  }

  /**
   * Deletes a resource limits.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param limitName - resource limit name
   */
  public void unregResourceConsumptionLimit(UUID clusterId, String limitName) {
    // TODO
    agentConnection.unregResourceConsumptionLimit(clusterId, limitName);
  }

  /**
   * Gets the list of resource counter values.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counterName - resource counter name
   * @param object - object name
   * @return list of resource counter values
   */
  public List<IResourceConsumptionCounterValue> getResourceConsumptionCounterValues(
      UUID clusterId, String counterName, String object) {
    // TODO
    return agentConnection.getResourceConsumptionCounterValues(clusterId, counterName, object);
  }

  /**
   * Deletes a resource counter values.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counterName - resource counter name
   * @param object - object name
   */
  public void clearResourceConsumptionCounterAccumulatedValues(
      UUID clusterId, String counterName, String object) {
    // TODO
    agentConnection.clearResourceConsumptionCounterAccumulatedValues(
        clusterId, counterName, object);
  }

  /**
   * Gets the list of resource counter accumulated values.
   *
   * <p>Требует аутентификации в кластере
   *
   * @param clusterId - ID кластера
   * @param counterName - resource counter name
   * @param object - object name
   * @return ist of resource counter accumulated values
   */
  public List<IResourceConsumptionCounterValue> getResourceConsumptionCounterAccumulatedValues(
      UUID clusterId, String counterName, String object) {
    // TODO
    return agentConnection.getResourceConsumptionCounterAccumulatedValues(
        clusterId, counterName, object);
  }

  /**
   * Получить иконку с текущим состоянием сервера.
   *
   * @return Image - иконка текущего состояния сервера
   */
  public Image getTreeImage() {

    Image icon;

    switch (serverState) {
      case CONNECTED:
        icon = connectedIcon;
        break;

      case CONNECTING:
        icon = connectingIcon;
        break;

      case CONNECT_ERROR:
        icon = connectErrorIcon;
        break;

      case DISCONNECT:
      default:
        icon = defaultIcon;
        break;
    }

    return icon;
  }

  /**
   * Обновить элемент дерева сервера.
   *
   * @param serverItem - элемент дерева содержащий ссылку на Сервер
   */
  public void updateTreeItemState(TreeItem serverItem) {
    serverItem.setImage(getTreeImage());
    serverItem.setText(new String[] {getTreeTitle()});
  }
}
