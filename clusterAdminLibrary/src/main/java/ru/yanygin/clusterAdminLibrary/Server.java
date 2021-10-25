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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibraryUI.AuthenticateDialog;

/** Server 1C Enterprise parameters. */
public class Server {

  @SerializedName("Description")
  @Expose
  private String description;

  @SerializedName("AgentHost")
  @Expose
  private String agentHost;

  @SerializedName("AgentPort")
  @Expose
  private int agentPort;

  @SerializedName("RasHost")
  @Expose
  private String rasHost;

  @SerializedName("RasPort")
  @Expose
  private int rasPort;

  @SerializedName("UseLocalRas")
  @Expose
  private boolean useLocalRas;

  @SerializedName("LocalRasPort")
  @Expose
  private int localRasPort;

  @SerializedName("LocalRasV8version")
  @Expose
  private String localRasV8version;

  @SerializedName("Autoconnect")
  @Expose
  private boolean autoconnect;

  @SerializedName("SaveCredentials")
  @Expose
  private boolean saveCredentials;

  @SerializedName("AgentUser")
  @Expose
  private String agentUserName;

  @SerializedName("AgentPassword")
  @Expose
  private String agentPassword;

  @SerializedName("ClustersCredentials")
  @Expose
  private Map<UUID, String[]>
      credentialsClustersCashe; // TODO Креды инфобаз хранить тут же или в отдельном списке?

  public Map<UUID, String[]> credentialsInfobasesCashe;

  private boolean available;
  private Process localRasProcess;
  private String connectionError;
  private String agentVersion = ""; //$NON-NLS-1$

  private IAgentAdminConnector agentConnector;
  private IAgentAdminConnection agentConnection;

  private static final Logger LOGGER =
      LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$
  private static UUID emptyUuid =
      UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$

  public static final String THIN_CLIENT = "1CV8C"; //$NON-NLS-1$
  public static final String THICK_CLIENT = "1CV8"; //$NON-NLS-1$
  public static final String DESIGNER = "Designer"; //$NON-NLS-1$
  public static final String SERVER_CONSOLE = "SrvrConsole"; //$NON-NLS-1$
  public static final String RAS_CONSOLE = "RAS"; //$NON-NLS-1$
  public static final String JOBSCHEDULER = "JobScheduler"; //$NON-NLS-1$

  interface IRunAuthenticate {
    void performAutenticate(String userName, String password, boolean saveNewUserpass);
  }

  interface IGetInfobaseInfo {
    IInfoBaseInfo getInfo(String userName, String password);
  }

  /**
   * Get the server description.
   *
   * @return server description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set new server description.
   *
   * @param description - new server description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the application name.
   *
   * @param appId - application ID
   * @return application name
   */
  public String getApplicationName(String appId) {
    switch (appId) {
      case THIN_CLIENT:
        return Messages.getString("Server.ThinClient"); //$NON-NLS-1$
      case THICK_CLIENT:
        return Messages.getString("Server.ThickClient"); //$NON-NLS-1$
      case DESIGNER:
        return Messages.getString("Server.Designer"); //$NON-NLS-1$
      case SERVER_CONSOLE:
        return Messages.getString("Server.ClusterConsole"); //$NON-NLS-1$
      case RAS_CONSOLE:
        return Messages.getString("Server.AdministrationServer"); //$NON-NLS-1$
      case JOBSCHEDULER:
        return Messages.getString("Server.JobScheduler"); //$NON-NLS-1$
      case "": //$NON-NLS-1$
        return ""; //$NON-NLS-1$
      default:
        return String.format(Messages.getString("Server.UnknownClient"), appId); //$NON-NLS-1$
    }
  }

  /**
   * Get the agent host name.
   *
   * @return agent host name
   */
  public String getAgentHost() {
    return agentHost;
  }

  /**
   * Set new agent host.
   *
   * @param agentHost - agent new host
   */
  public void setAgentHost(String agentHost) {
    this.agentHost = agentHost;
  }

  /**
   * Get agent port as string.
   *
   * @return agent port cast to string
   */
  public String getAgentPortAsString() {
    return Integer.toString(agentPort);
  }

  /**
   * Set agent port.
   *
   * @param agentPort - agent port
   */
  public void setAgentPort(int agentPort) {
    this.agentPort = agentPort;
  }

  /**
   * Get RAS host.
   *
   * @return RAS host
   */
  public String getRasHost() {
    return rasHost;
  }

  /**
   * Set RAS host.
   *
   * @param rasHost - RAS host
   */
  public void setRasHost(String rasHost) {
    this.rasHost = rasHost;
  }

  /**
   * Get RAS port as string.
   *
   * @return RAS port cast to string
   */
  public String getRasPortAsString() {
    return Integer.toString(rasPort);
  }

  /**
   * Set RAS port.
   *
   * @param rasPort - RAS port
   */
  public void setRasPort(int rasPort) {
    this.rasPort = rasPort;
  }

  /**
   * Get using local RAS.
   *
   * @return use local RAS
   */
  public boolean getUseLocalRas() {
    return useLocalRas;
  }

  /**
   * Set using local RAS.
   *
   * @param useLocalRas - use local RAS
   */
  public void setUseLocalRas(boolean useLocalRas) {
    this.useLocalRas = useLocalRas;
  }

  /**
   * Get local RAS port as string.
   *
   * @return local RAS port cast to string
   */
  public String getLocalRasPortAsString() {
    return Integer.toString(localRasPort);
  }

  /**
   * Set local RAS port.
   *
   * @param localRasPort - local RAS port
   */
  public void setLocalRasPort(int localRasPort) {
    this.localRasPort = localRasPort;
  }

  /**
   * Get local RAS v8 version.
   *
   * @return local RAS v8 version
   */
  public String getLocalRasV8version() {
    return localRasV8version;
  }

  /**
   * Set local RAS v8 version.
   *
   * @param localRasV8version - local RAS v8 version
   */
  public void setLocalRasV8version(String localRasV8version) {
    this.localRasV8version = localRasV8version;
  }

  /**
   * Get version of agent.
   *
   * @return version
   */
  public String getAgentVersion() {
    return agentVersion;
  }

  /**
   * Get connection error string.
   *
   * @return connection error
   */
  public String getConnectionError() {
    return connectionError;
  }

  /**
   * Returns the server key of the form "Server:1541".
   *
   * @return server key
   */
  public String getServerKey() {
    return agentHost.concat(":").concat(Integer.toString(agentPort)); //$NON-NLS-1$
  }

  /**
   * Get description of the server.
   *
   * @return description
   */
  public String getTreeDescription() {

    var commonConfig = ClusterProvider.getCommonConfig();

    var localRasPatternPart =
        useLocalRas && commonConfig.isShowLocalRasConnectInfo()
            ? String.format("(local-RAS:%s)->", getLocalRasPortAsString()) //$NON-NLS-1$
            : ""; //$NON-NLS-1$
    var serverVersionPatternPart =
        commonConfig.isShowServerVersion()
            ? String.format(" (%s)", agentVersion) //$NON-NLS-1$
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
   * Get the autoconnect parameters.
   *
   * @return autoconnect
   */
  public boolean getAutoconnect() {
    return autoconnect;
  }

  /**
   * Set the server to connect automatically when the program starts.
   *
   * @param autoconnect - connect automatically to server
   */
  public void setAutoconnect(boolean autoconnect) {
    this.autoconnect = autoconnect;
  }

  /**
   * Get the flag to save the server credentials.
   *
   * @return save the server credentials
   */
  public boolean getSaveCredentials() {
    return saveCredentials;
  }

  /**
   * Set the flag to save the server credentials.
   *
   * @param saveCredentials - save the server credentials
   */
  public void setSaveCredentials(boolean saveCredentials) {
    this.saveCredentials = saveCredentials;
  }

  /**
   * Get agent user name.
   *
   * @return agent user name
   */
  public String getAgentUserName() {
    return agentUserName;
  }

  /**
   * Set the agent username.
   *
   * @param agentUser - agent username
   */
  public void setAgentUserName(String agentUser) {
    this.agentUserName = agentUser;
  }

  /**
   * Get agent user password.
   *
   * @return agent user password
   */
  public String getAgentPassword() {
    return agentPassword;
  }

  /**
   * Set the agent password.
   *
   * @param agentPassword - agent password
   */
  public void setAgentPassword(String agentPassword) {
    this.agentPassword = agentPassword;
  }

  /**
   * Get credentials.
   *
   * @return credentials
   */
  public Map<UUID, String[]> getCredentials() {
    return credentialsClustersCashe;
  }

  /**
   * Set new server credentials.
   *
   * @param credentials - new credentials for the server
   */
  public void setCredentials(Map<UUID, String[]> credentials) {
    this.credentialsClustersCashe = credentials;
  }

  /**
   * Initialize new server instance.
   *
   * @param serverName - server name of the form "Server:1541".
   */
  public Server(String serverName) {

    calculateServerParams(serverName);

    this.useLocalRas = false;
    this.localRasPort = 0;
    this.localRasV8version = ""; //$NON-NLS-1$
    this.autoconnect = false;
    this.available = false;
    this.saveCredentials = false;
    this.agentVersion = ""; //$NON-NLS-1$

    init();
  }

  /** Initializes some server parameters. */
  public void init() {

    // При чтении конфиг-файла отсутствующие поля, инициализируются значением null
    if (agentUserName == null) {
      agentUserName = ""; //$NON-NLS-1$
    }
    if (agentPassword == null) {
      agentPassword = ""; //$NON-NLS-1$
    }
    if (description == null) {
      description = ""; //$NON-NLS-1$
    }
    if (localRasV8version == null) {
      localRasV8version = ""; //$NON-NLS-1$
    }
    if (agentVersion == null) {
      agentVersion = Messages.getString("Server.NotConnect"); //$NON-NLS-1$
    }

    if (credentialsClustersCashe == null) {
      credentialsClustersCashe = new HashMap<>();
    }

    this.connectionError = ""; //$NON-NLS-1$

    LOGGER.info("Server <{}> init done", getServerKey()); //$NON-NLS-1$
  }

  private void readAgentVersion() {

    if (!isConnected()) {
      return;
    }

    try {
      agentVersion = agentConnection.getAgentVersion();
      LOGGER.debug(
          "Agent version of server <{}> is <{}>", this.getServerKey(), agentVersion); //$NON-NLS-1$
    } catch (Exception e) {
      agentVersion = Messages.getString("Server.UnknownAgentVersion"); //$NON-NLS-1$
      LOGGER.error("Unknown agent version of server <{}>", this.getServerKey()); //$NON-NLS-1$
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
   * Return true if v8 version 8.3.15 or more.
   *
   * @return {@code true} if v8 version 8.3.15 or more
   */
  public boolean isFifteenOrMoreAgentVersion() {
    return agentVersion.compareTo("8.3.15") >= 0; //$NON-NLS-1$
  }

  /**
   * Вычисляет имя хоста и порты, на которых запущены процессы кластера.
   *
   * @param serverAddress - Имя сервера из списка баз. Может содержать номер порта менеджера
   *     кластера (Если не указан, то по-умолчанию 1541). Примеры: Server1c, Server1c:2541
   */
  private void calculateServerParams(String serverAddress) {

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

    LOGGER.info("Calculate params for Server <{}> ", this.getServerKey()); //$NON-NLS-1$
  }

  /**
   * Checks whether connection to the administration server is established.
   *
   * @return {@code true} if connected, {@code false} overwise
   */
  public boolean isConnected() {
    boolean isConnected = (agentConnection != null);

    if (!isConnected) {
      LOGGER.info(
          "The connection a server <{}> is not established", //$NON-NLS-1$
          this.getServerKey());
    }

    return isConnected;
  }

  /**
   * Connects to the server.
   *
   * @param disconnectAfter - disconnect after successful connection
   * @param silentMode - do not output an error to the user interactively
   * @return {@code true} if connect is succesful
   */
  public boolean connectToServer(boolean disconnectAfter, boolean silentMode) {
    LOGGER.debug("<{}> start connection", this.getServerKey()); //$NON-NLS-1$

    if (isConnected()) {
      return true;
    }

    if (!checkAndRunLocalRas()) {
      return false;
    }

    String currentRasHost = useLocalRas ? "localhost" : this.rasHost; //$NON-NLS-1$

    int currentRasPort = useLocalRas ? localRasPort : this.rasPort;

    try {
      connectToAgent(currentRasHost, currentRasPort, 20);

      if (disconnectAfter) {
        disconnectFromAgent();
      }
    } catch (Exception excp) {
      available = false;
      disconnectLocalRas();

      connectionError =
          String.format(
              "%s connection error:%n <%s>", //$NON-NLS-1$
              this.getServerKey(), getLocalisedMessage(excp));
      LOGGER.error(connectionError);

      if (!silentMode) {
        var messageBox = new MessageBox(Display.getDefault().getActiveShell());
        messageBox.setMessage(connectionError);
        messageBox.open();
      }
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

    if (!ClusterProvider.getCommonConfig().isWindows()) {
      return true;
    }

    if (!useLocalRas) {
      return true;
    }

    if (localRasV8version.isBlank() || localRasPort == 0) {
      var message =
          String.format(
              Messages.getString("Server.LocalRasParamsIsEmpty"), //$NON-NLS-1$
              this.getServerKey());
      LOGGER.error(message);

      var messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(message);
      messageBox.open();

      return false;
    }

    ///////////////////////////// пока только Windows
    var processBuilder = new ProcessBuilder();
    var processOutput = ""; //$NON-NLS-1$
    var localRasPath = ClusterProvider.getInstalledV8Versions().get(localRasV8version);
    if (localRasPath == null) {
      var message =
          String.format(
              Messages.getString("Server.LocalRasNotFound"), this.getServerKey()); //$NON-NLS-1$
      LOGGER.error(message);

      var messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(message);
      messageBox.open();

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

      var messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
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
      Stream<ProcessHandle> ch = localRasProcess.children();
      ch.forEach(
          ch1 -> {
            LOGGER.debug(
                "\tchildren -> {}, pid = {}", ch1.info().command().get(), ch1.pid()); //$NON-NLS-1$
          });

      return true;
    } else {
      connectionError =
          String.format("Local RAS <%s> is shutdown", this.getServerKey()); //$NON-NLS-1$
      LOGGER.error(connectionError);

      var messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(connectionError);
      messageBox.open();

      return false;
    }
  }

  /**
   * Проверяет действительна ли еще авторизация на центральном сервере и если нет - запускает
   * процесс авторизации.
   *
   * @param clusterId - cluster ID
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
   * Establishes connection with the administration server of 1C:Enterprise server cluster.
   *
   * @param address - server address
   * @param port - IP port
   * @param timeout - connection timeout (in milliseconds)
   * @throws AgentAdminException in the case of errors.
   */
  public void connectToAgent(String address, int port, long timeout) {
    if (isConnected()) {
      LOGGER.debug(
          "The connection to server <{}> is already established", //$NON-NLS-1$
          this.getServerKey());
      available = true;
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
    readAgentVersion();

    LOGGER.debug("Server <{}> is connected now", this.getServerKey()); //$NON-NLS-1$
  }

  /**
   * Terminates connection to the administration server.
   *
   * @throws AgentAdminException in the case of errors.
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
    }
  }

  private void disconnectLocalRas() {
    if (useLocalRas && localRasProcess.isAlive()) {
      Stream<ProcessHandle> ch = localRasProcess.children();
      ch.forEach(ch1 -> ch1.destroy());
      localRasProcess.destroy();
      LOGGER.info(
          "Local RAS of Server <{}> is shutdown now", //$NON-NLS-1$
          this.getServerKey());
    }
  }

  /**
   * Authethicates a central server administrator agent.
   *
   * <p>Need call before of regCluster, getAgentAdmins, regAgentAdmin, unregAgentAdmin
   *
   * @return {@code true} if authenticated, {@code false} overwise
   */
  public boolean authenticateAgent() {

    if (!isConnected()) {
      return false;
    }

    IRunAuthenticate authMethod =
        (String userName, String password, boolean saveNewUserpass) -> {
          LOGGER.debug(
              "Try to autenticate the agent server <{}>", //$NON-NLS-1$
              this.getServerKey());
          this.agentConnection.authenticateAgent(userName, password);
          LOGGER.debug(
              "Authentication to the agent server <{}> was successful", //$NON-NLS-1$
              this.getServerKey());

          // сохраняем новые user/pass после успешной авторизации
          if (saveNewUserpass) {
            this.agentUserName = userName;
            this.agentPassword = password;
            LOGGER.debug(
                "New credentials for the agent server <{}> are saved", //$NON-NLS-1$
                this.getServerKey());
          }
        };
    String authDescription =
        String.format(
            Messages.getString("Server.AuthenticationOfCentralServerAdministrator"), //$NON-NLS-1$
            agentHost,
            agentPort);

    return runAuthProcessWithRequestToUser(
        authDescription, agentUserName, agentPassword, authMethod);
  }

  /**
   * Checks whether authentication is still valid on the cluster and if not, starts the
   * authentication process.
   *
   * @param clusterId - cluster ID
   * @return boolean valid/not valid
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
   * Checks whether the authentication in the infobase is still valid and if not, starts the
   * authentication process.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @return boolean valid/not valid
   */
  private boolean checkAutenticateInfobase(UUID clusterId, UUID infobaseId) {

    return (getInfoBaseInfo(clusterId, infobaseId) != null);
  }

  /**
   * Authethicates a server cluster administrator.
   *
   * @param clusterId - cluster ID
   * @return boolean is authentication successful
   */
  public boolean authenticateCluster(UUID clusterId) {

    if (!isConnected()) {
      return false;
    }

    IRunAuthenticate authMethod =
        (String userName, String password, boolean saveNewUserpass) -> {
          String clusterName = getClusterInfo(clusterId).getName();

          LOGGER.debug(
              "Try to autenticate to the cluster <{}> of server <{}>", //$NON-NLS-1$
              clusterName,
              this.getServerKey());
          agentConnection.authenticate(clusterId, userName, password);
          LOGGER.debug(
              "Authentication to the cluster <{}> of server <{}> was successful", //$NON-NLS-1$
              clusterName,
              this.getServerKey());

          // сохраняем новые user/pass после успешной авторизации
          if (this.saveCredentials && saveNewUserpass) {
            this.credentialsClustersCashe.put(
                clusterId, new String[] {userName, password, clusterName});
            LOGGER.debug(
                "New credentials for the cluster <{}> of server <{}> are saved", //$NON-NLS-1$
                clusterName,
                this.getServerKey());
          }
        };

    String[] userAndPassword =
        credentialsClustersCashe.getOrDefault(
            clusterId, new String[] {"", ""}); //$NON-NLS-1$ //$NON-NLS-2$
    String authDescription =
        String.format(
            Messages.getString("Server.AuthenticationOfClusterAdminnistrator"), //$NON-NLS-1$
            getServerKey(),
            getClusterInfo(clusterId).getName());

    return runAuthProcessWithRequestToUser(
        authDescription, userAndPassword[0], userAndPassword[1], authMethod);
  }

  private boolean runAuthProcessWithRequestToUser(
      String authDescription, String userName, String password, IRunAuthenticate authMethod) {
    try {
      // Сперва пытаемся авторизоваться под сохраненной учеткой
      // (она может быть инициализирована пустыми строками)
      authMethod.performAutenticate(userName, password, false);
    } catch (Exception excp) {
      LOGGER.debug(
          "Autenticate to server <{}> error: <{}>", //$NON-NLS-1$
          this.getServerKey(),
          excp.getLocalizedMessage());

      AuthenticateDialog authenticateDialog;
      String authExcpMessage = excp.getLocalizedMessage();
      int dialogResult;

      // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена
      while (true) {

        try {
          LOGGER.debug(
              "Requesting new user credentials for the server <{}>", //$NON-NLS-1$
              this.getServerKey());
          authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(),
                  userName,
                  authDescription,
                  authExcpMessage);
          dialogResult = authenticateDialog.open();
        } catch (Exception exc) {
          LOGGER.debug(
              "Request new user credentials for the server <{}> failed", //$NON-NLS-1$
              this.getServerKey());
          MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
          messageBox.setMessage(exc.getLocalizedMessage());
          messageBox.open();
          return false;
        }

        if (dialogResult == 0) {
          LOGGER.debug(
              "The user has provided new credentials for the server <{}>", //$NON-NLS-1$
              this.getServerKey());
          userName = authenticateDialog.getUsername();
          password = authenticateDialog.getPassword();
          try {
            authMethod.performAutenticate(userName, password, true);
            break;
          } catch (Exception exc) {
            LOGGER.debug(
                "Autenticate to server <{}> error: <{}>", //$NON-NLS-1$
                this.getServerKey(),
                excp.getLocalizedMessage());
            authExcpMessage = exc.getLocalizedMessage();
            continue;
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Adds infobase authentication parameters to the context of the current administration server
   * connection.
   *
   * @param clusterId - cluster ID
   * @param userName - infobase administrator name
   * @param password - infobase administrator password
   */
  public void addInfobaseCredentials(UUID clusterId, String userName, String password) {
    if (!isConnected()) {
      return;
    }

    agentConnection.addAuthentication(clusterId, userName, password);
    LOGGER.debug(
        "Add new infobase credentials for the cluster <{}> of server <{}>", //$NON-NLS-1$
        clusterId,
        this.getServerKey());
  }

  /**
   * Gets the list of cluster descriptions registered on the central server.
   *
   * @return list of cluster descriptions
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

      // обновление имени кластера в кеше credentials
      if (saveCredentials) {
        String[] credentialClustersCashe = credentialsClustersCashe.get(cluster.getClusterId());
        if (credentialClustersCashe != null
            && !credentialClustersCashe[2].equals(cluster.getName())) {
          credentialClustersCashe[2] = cluster.getName();
          needSaveConfig = true;
        }
      }
    }
    if (needSaveConfig) {
      // TODO надо сохранить
    }

    return clusters;
  }

  /**
   * Gets the cluster descriptions.
   *
   * @param clusterId - cluster ID
   * @return cluster - descriptions
   */
  public IClusterInfo getClusterInfo(UUID clusterId) {
    if (!isConnected()) {
      return null;
    }

    LOGGER.debug("Get the cluster <{}> descriptions", clusterId); //$NON-NLS-1$

    IClusterInfo clusterInfo;
    // TODO debug
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
   * Gets the list of cluster manager descriptions.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return cluster - list of cluster manager descriptions
   */
  public List<IClusterManagerInfo> getClusterManagers(UUID clusterId) {
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
    // TODO debug
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId cluster ID
   * @param managerId - manager ID
   * @return cluster manager description, or null if the manager with the specified ID does not
   *     exist
   */
  public IClusterManagerInfo getClusterManagerInfo(UUID clusterId, UUID managerId) {
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
    // TODO debug
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
    if (clusterInfo.getClusterId().equals(emptyUuid)) {
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

    if (clusterInfo.getClusterId().equals(emptyUuid)) {
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
   * Deletes a cluster.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @return delete a cluster successful
   */
  public boolean unregCluster(UUID clusterId) {
    LOGGER.debug("Delete a cluster <{}>", clusterId); //$NON-NLS-1$

    var unregSuccesful = false;
    String unregMessage = null;

    if (!isConnected()) { // TODO
      unregMessage =
          Messages.getString("Server.TheConnectionAClusterIsNotEstablished"); //$NON-NLS-1$
    }

    if (!checkAutenticateCluster(clusterId)) {
      unregMessage = Messages.getString("Server.TheClusterAuthenticationError"); //$NON-NLS-1$
    }

    try {
      agentConnection.unregCluster(clusterId);
      unregSuccesful = true;
    } catch (Exception excp) {
      LOGGER.error("Error delete a cluster", excp); //$NON-NLS-1$
      unregMessage = excp.getLocalizedMessage();
    }
    if (!unregSuccesful) {
      var messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(unregMessage);
      messageBox.open();
    }

    return unregSuccesful;
  }

  /**
   * Gets the list of cluster administrators.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of cluster administrators
   */
  public List<IRegUserInfo> getClusterAdmins(UUID clusterId) {
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
   * Deletes a cluster administrator.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param name - administrator name
   */
  public void unregClusterAdmin(UUID clusterId, String name) {
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
   * Gets the list of short descriptions of infobases registered in the cluster.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId -cluster ID
   * @return list of short descriptions of cluster infobases
   */
  public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterId) {
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

    clusterInfoBases.forEach(
        ib ->
            LOGGER.debug(
                "\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr())); //$NON-NLS-1$

    LOGGER.debug("Get the list of short descriptions of infobases succesful"); //$NON-NLS-1$
    return clusterInfoBases;
  }

  /**
   * Gets the list of full descriptions of infobases registered in the cluster.
   *
   * <p>Cluster authentication is required For each infobase in the cluster, infobase authentication
   * is required If infobase authentication is not performed, only fields that correspond to short
   * infobase description fields will be filled
   *
   * @param clusterId - cluster ID
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
        ib -> {
          LOGGER.debug(
              "\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr()); //$NON-NLS-1$
        });

    LOGGER.debug("Get the list of descriptions of infobases succesful"); //$NON-NLS-1$
    return clusterInfoBases;
  }

  /**
   * Gets a short infobase description.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
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
   * <p>Cluster authentication is required. Infobase authentication is required.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
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

      var userName = ""; //$NON-NLS-1$
      var authDescription = Messages.getString("Server.AuthenticationOfInfobase"); //$NON-NLS-1$

      // пока не подойдет пароль, или пользователь не нажмет Отмена
      while (true) {

        try {
          LOGGER.debug(
              "Requesting new user credentials for the infobase <{}>", //$NON-NLS-1$
              infobaseId);
          authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(),
                  userName,
                  authDescription,
                  authExcpMessage);
          dialogResult = authenticateDialog.open();
        } catch (Exception exc) {
          LOGGER.debug(
              "Request new user credentials for the infobase failed: <{}>", //$NON-NLS-1$
              exc.getLocalizedMessage());
          var messageBox = new MessageBox(Display.getDefault().getActiveShell());
          messageBox.setMessage(exc.getLocalizedMessage());
          messageBox.open();
          return null;
        }

        if (dialogResult == 0) {
          LOGGER.debug(
              "The user has provided new credentials for the infobase <{}>", //$NON-NLS-1$
              infobaseId);
          userName = authenticateDialog.getUsername();
          String password = authenticateDialog.getPassword();
          try {
            addInfobaseCredentials(clusterId, userName, password);
            infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
            break;
          } catch (Exception exc) {
            authExcpMessage = exc.getLocalizedMessage();
            LOGGER.debug(
                "Autenticate to infobase <{}> error: <{}>", //$NON-NLS-1$
                this.getServerKey(),
                authExcpMessage);
            continue;
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
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @return infobase full infobase description
   */
  public String getInfoBaseName(UUID clusterId, UUID infobaseId) {
    LOGGER.debug(
        "Get the name for infobase <{}> of the cluster <{}>", //$NON-NLS-1$
        infobaseId,
        clusterId);

    if (infobaseId.equals(emptyUuid)) {
      LOGGER.debug("Infobase ID is empty"); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }

    IInfoBaseInfoShort infobaseShortInfo = getInfoBaseShortInfo(clusterId, infobaseId);
    return infobaseShortInfo == null ? "" : infobaseShortInfo.getName(); //$NON-NLS-1$
  }

  /**
   * Creates an infobase in a cluster.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @param info - infobase parameters
   * @param infobaseCreationMode - infobase creation mode: •0 - do not create a database •1 - create
   *     a database
   * @return ID of the created infobase
   */
  public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode) {
    LOGGER.debug(
        "Creates an infobase <{}> in a cluster <{}>", //$NON-NLS-1$
        info.getName(),
        clusterId);
    
    if (!isConnected()) {
      return emptyUuid;
    }
    if (!checkAutenticateCluster(clusterId)) {
      return emptyUuid;
    }

    UUID uuid;
    try {
      uuid = agentConnection.createInfoBase(clusterId, info, infobaseCreationMode);
    } catch (Exception excp) {
      LOGGER.error("Error creates an infobase", excp); //$NON-NLS-1$
      throw excp;
    }

    LOGGER.debug("Creates an infobase succesful"); //$NON-NLS-1$
    return uuid;
  }

  /**
   * Changes short infobase description.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param info - infobase parameters
   */
  public void updateInfoBaseShort(UUID clusterId, IInfoBaseInfoShort info) {
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

    try { // TODO debug
      agentConnection.updateInfoBaseShort(clusterId, info);
    } catch (Exception excp) {
      LOGGER.error("Error changes short description infobase", excp); //$NON-NLS-1$
      return;
    }
  }

  /**
   * Changes infobase parameters.
   *
   * <p>Infobase authentication is required (Здесь не нужно авторизоваться в базе, метод необходимо
   * вызывать сразу после getInfoBaseInfo)
   *
   * @param clusterId - cluster ID
   * @param info - infobase parameters
   * @return {@code true} if update succesful
   */
  public boolean updateInfoBase(UUID clusterId, IInfoBaseInfo info) {

    try { // TODO debug
      agentConnection.updateInfoBase(clusterId, info);
    } catch (Exception excp) {
      LOGGER.error("Error changes description infobase", excp); //$NON-NLS-1$
      MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }
    return true;
  }

  /**
   * Deletes an infobase.
   *
   * <p>Infobase authentication is required
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase parameters
   * @param dropMode - infobase drop mode: 0 - do not delete the database 1 - delete the database 2
   *     - clear the database
   * @return {@code true} if drop succesful
   */
  public boolean dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode) {

    if (!checkAutenticateInfobase(clusterId, infobaseId)) {
      return false;
    }

    try { // TODO debug
      agentConnection.dropInfoBase(clusterId, infobaseId, dropMode);
    } catch (Exception excp) {
      LOGGER.error("Error deletes an infobase", excp); //$NON-NLS-1$
      MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }
    return true;
  }

  /**
   * Gets the list of cluster session descriptions.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @return List of session descriptions
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
    sessions.forEach(
        s -> {
          LOGGER.debug(
              "\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
              getApplicationName(s.getAppId()),
              s.getSessionId());
        });

    LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
    return sessions;
  }

  /**
   * Gets a session description.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
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
    try { // TODO debug
      sessionInfo = agentConnection.getSessionInfo(clusterId, sid);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of cluster session descriptions", excp); //$NON-NLS-1$
      return null;
    }
    LOGGER.debug(
        "\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
        getApplicationName(sessionInfo.getAppId()),
        sessionInfo.getSessionId());

    LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
    return sessionInfo;
  }

  /**
   * Gets the list of infobase session descriptions.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @return Infobase sessions
   */
  public List<ISessionInfo> getInfoBaseSessions(UUID clusterId, UUID infobaseId) {
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
    sessions.forEach(
        s -> {
          LOGGER.debug(
              "\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
              getApplicationName(s.getAppId()),
              s.getSessionId());
        });

    LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
    return sessions;
  }

  /**
   * Gets the list of infobase session descriptions.
   *
   * <p>Cluster authentication is required
   *
   * @param clusterId - cluster ID
   * @param workingProcessId - Working process ID
   * @return Working process sessions
   */
  public List<ISessionInfo> getWorkingProcessSessions(UUID clusterId, UUID workingProcessId) {

    return getSessions(clusterId).stream()
        .filter(s -> s.getWorkingProcessId().equals(workingProcessId))
        .collect(Collectors.toList());
  }

  /**
   * Terminates a session in the cluster with default message.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param sessionId - infobase ID
   * @return sucess terminate session
   */
  public boolean terminateSession(UUID clusterId, UUID sessionId) {
    return terminateSession(
        clusterId, sessionId, Messages.getString("Server.TerminateSessionMessage")); //$NON-NLS-1$
  }

  /**
   * Terminates a session in the cluster.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param sessionId - infobase ID
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
      MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }
    LOGGER.debug("Terminates a session succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Terminates all sessions for all infobases in the cluster.
   *
   * @param clusterId - cluster ID
   */
  public void terminateAllSessions(UUID clusterId) {

    getSessions(clusterId).forEach(session -> terminateSession(clusterId, session.getSid()));
  }

  /**
   * Terminates all sessions for infobase in the cluster.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
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
   * Gets the list of short descriptions of cluster connections.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of short cluster connection descriptions
   */
  public List<IInfoBaseConnectionShort> getConnectionsShort(UUID clusterId) {
    if (isConnected()) {
      return agentConnection.getConnectionsShort(clusterId);
    }
    // TODO
    return new ArrayList<>();
  }

  /**
   * Gets a short description of a connection.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param connectionId - connection ID
   * @return short connection description
   */
  public IInfoBaseConnectionShort getConnectionInfoShort(UUID clusterId, UUID connectionId) {
    if (isConnected()) {
      return agentConnection.getConnectionInfoShort(clusterId, connectionId);
    }
    // TODO
    return null;
  }

  /**
   * Gets the list of short descriptions of infobase connections.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @return list of short infobase connection descriptions
   */
  public List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(
      UUID clusterId, UUID infobaseId) {
    if (isConnected()) {
      return agentConnection.getInfoBaseConnectionsShort(clusterId, infobaseId);
    }
    // TODO
    return new ArrayList<>();
  }

  /**
   * Gets the list of infobase connection descriptions for a working process.
   *
   * <p>Cluster authentication is required. Infobase authentication is required.
   *
   * @param clusterId - cluster ID
   * @param workingProcessId - working process ID
   * @param infobaseId - infobase ID
   * @return list of infobase connection descriptions
   */
  public List<IInfoBaseConnectionInfo> getInfoBaseConnections(
      UUID clusterId, UUID workingProcessId, UUID infobaseId) {
    if (isConnected()) {
      return agentConnection.getInfoBaseConnections(clusterId, workingProcessId, infobaseId);
    }
    // TODO
    return new ArrayList<>();
  }

  /**
   * Gets the list of connection descriptions for a working process.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param workingProcessId - working process ID
   * @return list of connection descriptions for a working process
   */
  public List<IInfoBaseConnectionShort> getWorkingProcessConnectionsShort(
      UUID clusterId, UUID workingProcessId) {
    if (isConnected()) {

      return agentConnection.getConnectionsShort(clusterId).stream()
          .filter(c -> c.getWorkingProcessId().equals(workingProcessId))
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }

  /**
   * Closes an infobase connection.
   *
   * <p>Cluster authentication is required. Infobase authentication is required.
   *
   * @param clusterId - cluster ID
   * @param processId - working process ID
   * @param connectionId - connection ID
   * @param infobaseId - infobase ID
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
      MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }
    LOGGER.debug("Close connection succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Interrupt current server call.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of object lock descriptions
   */
  public List<IObjectLockInfo> getLocks(UUID clusterId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO debug
      locks = agentConnection.getLocks(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  /**
   * Gets the list of infobase object locks.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @return list of object lock descriptions
   */
  public List<IObjectLockInfo> getInfoBaseLocks(UUID clusterId, UUID infobaseId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO debug
      locks = agentConnection.getInfoBaseLocks(clusterId, infobaseId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  /**
   * Gets the list of connection object locks.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param connectionId - connection ID
   * @return list of object lock descriptions
   */
  public List<IObjectLockInfo> getConnectionLocks(UUID clusterId, UUID connectionId) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO debug
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @param sid - session ID
   * @return list of object lock descriptions
   */
  public List<IObjectLockInfo> getSessionLocks(UUID clusterId, UUID infobaseId, UUID sid) {
    LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$

    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IObjectLockInfo> locks;
    try { // TODO debug
      locks = agentConnection.getSessionLocks(clusterId, infobaseId, sid);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of object locks", excp); //$NON-NLS-1$
      return new ArrayList<>();
    }

    return locks;
  }

  /**
   * Gets the list of descriptions of working processes registered in the cluster.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of working processes descriptions
   */
  public List<IWorkingProcessInfo> getWorkingProcesses(UUID clusterId) {
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IWorkingProcessInfo> workingProcesses;
    try { // TODO debug
      LOGGER.debug(
          "Gets the list of descriptions of working processes in the cluster <{}>", //$NON-NLS-1$
          clusterId);
      workingProcesses = agentConnection.getWorkingProcesses(clusterId);
    } catch (Exception excp) {
      LOGGER.error(
          "Error get the list of short descriptions of working processes", //$NON-NLS-1$
          excp);
      return new ArrayList<>();
    }
    workingProcesses.forEach(
        wp -> {
          LOGGER.debug(
              "\tWorking process: host name=<{}>, main port=<{}>", //$NON-NLS-1$
              wp.getHostName(),
              wp.getMainPort());
        });

    LOGGER.debug(
        "Get the list of short descriptions of working processes succesful"); //$NON-NLS-1$
    return workingProcesses;
  }

  /**
   * Gets a working process description.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * Gets the list of descriptions of working servers registered in the cluster.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of working server descriptions
   */
  public List<IWorkingServerInfo> getWorkingServers(UUID clusterId) {
    if (!isConnected()) {
      return new ArrayList<>();
    }

    if (!checkAutenticateCluster(clusterId)) {
      return new ArrayList<>();
    }

    List<IWorkingServerInfo> workingServers;
    try { // TODO debug
      LOGGER.debug(
          "Gets the list of descriptions of working servers in the cluster <{}>", //$NON-NLS-1$
          clusterId);
      workingServers = agentConnection.getWorkingServers(clusterId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of working servers", excp); //$NON-NLS-1$
      throw new IllegalStateException("Error get working servers"); //$NON-NLS-1$
    }
    workingServers.forEach(
        ws -> {
          LOGGER.debug(
              "\tWorking server: host name=<{}>, main port=<{}>", //$NON-NLS-1$
              ws.getHostName(),
              ws.getMainPort());
        });

    LOGGER.debug("Get the list of descriptions of working servers succesful"); //$NON-NLS-1$
    return workingServers;
  }

  /**
   * Gets a description of a working server registered in the cluster.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param serverId - server ID
   * @return working server description, or null if the working server with the specified ID does
   *     not exist
   */
  public IWorkingServerInfo getWorkingServerInfo(UUID clusterId, UUID serverId) {
    if (!isConnected()) {
      return null;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return null;
    }

    IWorkingServerInfo workingServerInfo;
    try { // TODO debug
      LOGGER.debug(
          "Gets the description of working server <{}> in the cluster <{}>", //$NON-NLS-1$
          serverId,
          clusterId);
      workingServerInfo = agentConnection.getWorkingServerInfo(clusterId, serverId);
    } catch (Exception excp) {
      LOGGER.error("Error get the list of descriptions of working server", excp); //$NON-NLS-1$
      throw new IllegalStateException("Error get working server"); //$NON-NLS-1$
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
   * Creates a working server or changes the description of an existing one.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param serverInfo - working server description
   * @param createNew - if create new working server
   * @return {@code true} if succes reg working server
   */
  public boolean regWorkingServer(
      UUID clusterId, IWorkingServerInfo serverInfo, boolean createNew) {
    if (!isConnected()) {
      return false;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }

    if (createNew) {
      LOGGER.debug("Registration NEW working server"); //$NON-NLS-1$
    }

    try { // TODO debug
      LOGGER.debug(
          "Registration working server <{}> registered in the cluster <{}>", //$NON-NLS-1$
          serverInfo.getName(),
          clusterId);
      agentConnection.regWorkingServer(clusterId, serverInfo);
    } catch (Exception excp) {
      LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
      return false;
    }

    LOGGER.debug(
        "\tRegistration working server: name=<{}>, host name=<{}>, main port=<{}>", //$NON-NLS-1$
        serverInfo.getName(),
        serverInfo.getHostName(),
        serverInfo.getMainPort());

    LOGGER.debug("Registration working server succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Deletes a working server and removes its cluster registration.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param serverId - server ID
   * @return infobase full infobase description
   */
  public boolean unregWorkingServer(UUID clusterId, UUID serverId) {
    if (!isConnected()) {
      return false;
    }

    if (!checkAutenticateCluster(clusterId)) {
      return false;
    }

    try { // TODO debug
      LOGGER.debug(
          "Deletes a working server <{}> from the cluster <{}>", //$NON-NLS-1$
          serverId,
          clusterId);
      agentConnection.unregWorkingServer(clusterId, serverId);
    } catch (Exception excp) {
      LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
      MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }

    LOGGER.debug("Unregistration working server succesful"); //$NON-NLS-1$
    return true;
  }

  /**
   * Gets the list of cluster service descriptions.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of cluster service descriptions
   */
  public List<IClusterServiceInfo> getClusterServices(UUID clusterId) {
    // TODO
    return agentConnection.getClusterServices(clusterId);
  }

  /**
   * Applies assignment rules.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param full - assigment rule application mode: 0 - partial 1 - full
   */
  public void applyAssignmentRules(UUID clusterId, int full) {
    // TODO
    agentConnection.applyAssignmentRules(clusterId, full);
  }

  /**
   * Gets the list of descriptions of working server assignment rules.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of cluster security profiles
   */
  public List<ISecurityProfile> getSecurityProfiles(UUID clusterId) {
    // TODO
    return agentConnection.getSecurityProfiles(clusterId);
  }

  /**
   * Creates or updates a cluster security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param profile -security profile
   */
  public void createSecurityProfile(UUID clusterId, ISecurityProfile profile) {
    // TODO
    agentConnection.createSecurityProfile(clusterId, profile);
  }

  /**
   * Deletes a security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param spName - security profile name
   */
  public void dropSecurityProfile(UUID clusterId, String spName) {
    // TODO
    agentConnection.dropSecurityProfile(clusterId, spName);
  }

  /**
   * Gets the list of virtual directories of a security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param comClass - allowed COM class
   */
  public void createSecurityProfileComClass(UUID clusterId, ISecurityProfileCOMClass comClass) {
    // TODO
    agentConnection.createSecurityProfileComClass(clusterId, comClass);
  }

  /**
   * Deletes an allowed COM class of a security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param addIn - allowed add-in
   */
  public void createSecurityProfileAddIn(UUID clusterId, ISecurityProfileAddIn addIn) {
    // TODO
    agentConnection.createSecurityProfileAddIn(clusterId, addIn);
  }

  /**
   * Deletes an allowed add-in of a security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param app - allowed application
   */
  public void createSecurityProfileApplication(UUID clusterId, ISecurityProfileApplication app) {
    // TODO
    agentConnection.createSecurityProfileApplication(clusterId, app);
  }

  /**
   * Deletes an allowed application of a security profile.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of cluster resource counters
   */
  public List<IResourceConsumptionCounter> getResourceConsumptionCounters(UUID clusterId) {
    // TODO
    return agentConnection.getResourceConsumptionCounters(clusterId);
  }

  /**
   * Gets resource counters description.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param counter - resource counter info
   */
  public void regResourceConsumptionCounter(UUID clusterId, IResourceConsumptionCounter counter) {
    // TODO
    agentConnection.regResourceConsumptionCounter(clusterId, counter);
  }

  /**
   * Deletes a resource counter.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param counterName - resource counter name
   */
  public void unregResourceConsumptionCounter(UUID clusterId, String counterName) {
    // TODO
    agentConnection.unregResourceConsumptionCounter(clusterId, counterName);
  }

  /**
   * Gets the list of resource limits.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @return list of cluster resource limits
   */
  public List<IResourceConsumptionLimit> getResourceConsumptionLimits(UUID clusterId) {
    // TODO
    return agentConnection.getResourceConsumptionLimits(clusterId);
  }

  /**
   * Gets resource limits description.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param limit - cluster resource limit info
   */
  public void regResourceConsumptionLimit(UUID clusterId, IResourceConsumptionLimit limit) {
    // TODO
    agentConnection.regResourceConsumptionLimit(clusterId, limit);
  }

  /**
   * Deletes a resource limits.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
   * @param limitName - resource limit name
   */
  public void unregResourceConsumptionLimit(UUID clusterId, String limitName) {
    // TODO
    agentConnection.unregResourceConsumptionLimit(clusterId, limitName);
  }

  /**
   * Gets the list of resource counter values.
   *
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
   * <p>Cluster authentication is required.
   *
   * @param clusterId - cluster ID
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
}
