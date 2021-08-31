package ru.yanygin.clusterAdminLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import ru.yanygin.clusterAdminLibraryUI.AuthenticateDialog;

public class Server {
	
	@SerializedName("Description")
	@Expose
	public String description;
	
	@SerializedName("AgentHost")
	@Expose
	public String agentHost;
	
	@SerializedName("AgentPort")
	@Expose
	public int agentPort;
	
	@SerializedName("RasHost")
	@Expose
	public String rasHost;
	
	@SerializedName("RasPort")
	@Expose
	public int rasPort;
	
	@SerializedName("UseLocalRas")
	@Expose
	public boolean useLocalRas;
	
	@SerializedName("LocalRasPort")
	@Expose
	public int localRasPort;
	
	@SerializedName("LocalRasV8version")
	@Expose
	public String localRasV8version;
	
	@SerializedName("LocalRasPath")
	@Expose
	public String localRasPath;
	
	@SerializedName("Autoconnect")
	@Expose
	public boolean autoconnect;
	
	@SerializedName("SaveCredentials")
	@Expose
	public boolean saveCredentials;
	
	@SerializedName("AgentUser")
	@Expose
	public String agentUserName;
	
	@SerializedName("AgentPassword")
	@Expose
	public String agentPassword;
	
	@SerializedName("ClustersCredentials")
	@Expose
	public Map<UUID, String[]> credentialsClustersCashe; // TODO Креды инфобаз хранить тут же или в отдельном списке?
	
	public Map<UUID, String[]> credentialsInfobasesCashe;
	
	public boolean available;
	private Process localRASProcess;
	
	public String connectionError;
	
	private static Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$
	private static UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$
	
	private IAgentAdminConnector agentConnector;
	private IAgentAdminConnection agentConnection;
	private String agentVersion = ""; //$NON-NLS-1$
	
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
	
	public String getAgentVersion() {
		return agentVersion;
	}
	
	public Server(String serverName) {
		
		calculateServerParams(serverName);
		
		this.useLocalRas		= false;
		this.localRasPort		= 0;
		this.localRasV8version	= ""; //$NON-NLS-1$
		this.localRasPath		= ""; //$NON-NLS-1$
		this.autoconnect		= false;
		this.available			= false;
//		this.agentUserName		= "";
//		this.agentPassword		= "";
		this.saveCredentials	= false;
		this.agentVersion		= ""; //$NON-NLS-1$
		
		init();
		
	}
	
	public void init() {
		
		// При чтении конфиг-файла отсутствующие поля, инициализируются значением null
		if (agentUserName == null)
			agentUserName = ""; //$NON-NLS-1$
		if (agentPassword == null)
			agentPassword = ""; //$NON-NLS-1$
		if (description == null)
			description = ""; //$NON-NLS-1$
		if (localRasV8version == null)
			localRasV8version = ""; //$NON-NLS-1$
		if (agentVersion == null)
			agentVersion = Messages.getString("Server.NotConnect"); //$NON-NLS-1$
		
		if (this.credentialsClustersCashe == null)
			this.credentialsClustersCashe = new HashMap<>();
		
		LOGGER.info("Server <{}> init done", this.getServerKey()); //$NON-NLS-1$
		
	}
	
	// Надо определиться что должно являться ключем, агент (Server:1540) или
	// менеджер (Server:1541) или RAS (Server:1545)
	public String getServerKey() {
		return agentHost.concat(":").concat(Integer.toString(agentPort)); //$NON-NLS-1$
	}
	
	public String getServerDescriptionOld() {
		var rasPortString = ""; //$NON-NLS-1$
		if (useLocalRas) {
			rasPortString = "(*".concat(Integer.toString(localRasPort)).concat(")"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			rasPortString = Integer.toString(this.rasPort);
		}
		
		var serverDescriptionPattern = isConnected() ? "%s:%s-%s (%s)" : "%s:%s-%s"; //$NON-NLS-1$ //$NON-NLS-2$
		
		return String.format(serverDescriptionPattern, agentHost, Integer.toString(agentPort), rasPortString, agentVersion);
		
	}
	
	public String getDescriptionOld() {

		String serverDescriptionPattern;
		String serverDescription;
		
		if (useLocalRas) {
			serverDescriptionPattern = isConnected() ? "(local-RAS:%s)->%s:%s (%s)" : "(local-RAS:%s)->%s:%s"; //$NON-NLS-1$ //$NON-NLS-2$
			serverDescription = String.format(serverDescriptionPattern, getLocalRasPortAsString(), agentHost, getAgentPortAsString(), agentVersion);
		} else {
			serverDescriptionPattern = isConnected() ? "%s:%s (%s)" : "%s:%s"; //$NON-NLS-1$ //$NON-NLS-2$
			serverDescription = String.format(serverDescriptionPattern, agentHost, getAgentPortAsString(), agentVersion);
		}
		return serverDescription;
		
	}
	
	public String getDescription() { // TODO
		
		var commonConfig 	= ClusterProvider.getCommonConfig();
		
		var localRasPatternPart = useLocalRas && commonConfig.showLocalRasConnectInfo ?
				String.format("(local-RAS:%s)->", getLocalRasPortAsString()) : ""; //$NON-NLS-1$ //$NON-NLS-2$
		var serverVersionPatternPart = commonConfig.showServerVersion ?
				String.format(" (%s)", agentVersion) : ""; //$NON-NLS-1$ //$NON-NLS-2$
		var serverDescriptionPatternPart = commonConfig.showServerDescription && !description.isBlank() ?
				String.format(" - <%s>", description) : ""; //$NON-NLS-1$ //$NON-NLS-2$
				
		return String.format("%s%s:%s%s%s", //$NON-NLS-1$
				localRasPatternPart, agentHost, getAgentPortAsString(), serverVersionPatternPart, serverDescriptionPatternPart);
		
	}
	
	private void readAgentVersion() {
		
		if (!isConnected())
			return;
		
		try {
			agentVersion = agentConnection.getAgentVersion();
			LOGGER.debug("Agent version of server <{}> is <{}>", this.getServerKey(), agentVersion); //$NON-NLS-1$
		} catch (Exception e) {
			agentVersion = Messages.getString("Server.UnknownAgentVersion"); //$NON-NLS-1$
			LOGGER.error("Unknown agent version of server <{}>", this.getServerKey()); //$NON-NLS-1$
		}
	}
	
	public boolean isFifteenOrOlderAgentVersion() {
		return agentVersion.compareTo("8.3.15") >= 0; //$NON-NLS-1$
	}
	
	public String getAgentPortAsString() {
		return Integer.toString(agentPort);
	}
	
	public String getRasPortAsString() {
		return Integer.toString(rasPort);
	}
	
	public String getLocalRasPortAsString() {
		return Integer.toString(localRasPort);
	}
	
	public void setServerNewProperties(String agentHost,
										int agentPort,
										String rasHost,
										int rasPort,
										boolean useLocalRas,
										int localRasPort,
										String localRasV8version,
//										String localRasPath,
										boolean autoconnect,
										boolean saveCredentials,
										String agentUser,
										String agentPassword,
										Map<UUID, String[]> credentialsClustersCashe) {
		
		this.agentHost					= agentHost;
		this.agentPort					= agentPort;
		this.rasHost					= rasHost;
		this.rasPort					= rasPort;
		this.useLocalRas				= useLocalRas;
		this.localRasPort				= localRasPort;
		this.localRasV8version			= localRasV8version;
//		this.localRasPath				= localRasPath;
		this.autoconnect				= autoconnect;
		this.saveCredentials			= saveCredentials;
		this.agentUserName				= agentUser;
		this.agentPassword				= agentPassword;
		this.credentialsClustersCashe	= credentialsClustersCashe;
		
		if (this.autoconnect)
			connectAndAuthenticate(false);
		
		LOGGER.info("Set new properties for server <{}>", this.getServerKey()); //$NON-NLS-1$
	}
	
	/**
	 * Вычисляет имя хоста, на котором запущены процессы кластера
	 * 
	 * @param serverAddress - Имя инф.базы из списка баз. Может содержать номер
	 *                      порта менеджера кластера (Если не указан, то
	 *                      по-умолчанию 1541). Примеры: Server1c, Server1c:2541
	 * @return Имя хоста, на котором запущены процессы кластера
	 */
	private String cutHostName(String serverAddress) {
		String serverName;
		String[] ar = serverAddress.split(":"); //$NON-NLS-1$
		if (ar.length > 0) {
			serverName = ar[0];
		} else {
			serverName = "localhost"; //$NON-NLS-1$
		}
		
		return serverName;
	}
	
	private int cutManagerPort(String serverAddress) {
		int port;
		String[] ar = serverAddress.split(":"); //$NON-NLS-1$
		if (ar.length == 1) {
			port = 1541;
		} else {
			port = Integer.parseInt(ar[1]);
		}
		return port;
	}
	
	private int cutRemoteRASPort(String serverAddress) {
		int port;
		String[] ar = serverAddress.split(":"); //$NON-NLS-1$
		if (ar.length == 1) {
			port = 1545;
		} else {
			port = Integer.parseInt(ar[1].substring(0, ar[1].length() - 1).concat("5")); //$NON-NLS-1$
		}
		return port;
	}
	
	private void calculateServerParams(String serverAddress) {
		
		String agentHost;
		String rasHost;
		int managerPort; // не нужен
		int agentPort;
		int rasPort;
		
		serverAddress = serverAddress.strip();
		if (serverAddress.isBlank())
			serverAddress = "localhost"; //$NON-NLS-1$
		
		String[] ar = serverAddress.split(":"); //$NON-NLS-1$
		agentHost	= ar[0];
		rasHost		= ar[0];
		
		if (ar.length == 1) {
			managerPort = 1541;
			agentPort 	= 1540;
			rasPort 	= 1545;
		} else {
			managerPort = Integer.parseInt(ar[1]);
			agentPort = managerPort - 1;
			rasPort = managerPort + 4;
		}
		
		this.agentHost 	= agentHost;
		this.rasHost 	= rasHost;
//		this.managerPort 	= managerPort;
		this.agentPort 	= agentPort;
		this.rasPort 	= rasPort;
		
		LOGGER.info("Calculate params for Server <{}> ", this.getServerKey()); //$NON-NLS-1$
		
	}
	
	/**
	 * Checks whether connection to the administration server is established
	 *
	 * @return {@code true} if connected, {@code false} overwise
	 */
	public boolean isConnected() {
		boolean isConnected = (agentConnection != null);
//		if (isConnected)
//			LOGGER.debug("Server {} is already connected", this.getServerKey()); // засоряет лог
		
		if (!isConnected)
			LOGGER.info("The connection a server <{}> is not established", this.getServerKey()); //$NON-NLS-1$
		
		return isConnected;
	}
	
	public boolean connectAndAuthenticate(boolean disconnectAfter) { // TODO здесь аутентификации не делается же?
		LOGGER.debug("Server <{}> start connection", this.getServerKey()); //$NON-NLS-1$
		
		if (isConnected())
			return true;
		
		if (!checkAndRunLocalRAS())
			return false;
		
		String rasHost 	= useLocalRas ? "localhost" : this.rasHost; //$NON-NLS-1$
		int rasPort 	= useLocalRas ? localRasPort : this.rasPort;
		
		try {
			connectToAgent(rasHost, rasPort, 20);
			
			if (disconnectAfter) {
				disconnectFromAgent();
			}
		} catch (Exception excp) {
			available = false;
			LOGGER.info("Server <{}> connection error: <{}>", this.getServerKey(), excp.getLocalizedMessage()); //$NON-NLS-1$
			return false;
		}
		return true;
		
	}
	
	/**
	 * Проверяет включено ли использование локального RAS и запускает его
	 *
	 * @return {@code true} если локальный RAS выключен либо включен и удачно запустился, {@code false} если локальный RAS включен и не удалось его запустить
	 */
	private boolean checkAndRunLocalRAS() {
		
		if (!ClusterProvider.getCommonConfig().isWindows())
			return true;
		
		if (!useLocalRas)
			return true;
		
		if (localRasV8version.isBlank() || localRasPort == 0) {
			var message = String.format(Messages.getString("Server.LocalRasParamsIsEmpty"), this.getServerKey()); //$NON-NLS-1$
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
			var message = String.format(Messages.getString("Server.LocalRasNotFound"), this.getServerKey()); //$NON-NLS-1$
			LOGGER.error(message);
			
			var messageBox = new MessageBox(Display.getDefault().getActiveShell());
			messageBox.setMessage(message);
			messageBox.open();
			
			return false;
		}

		Map<String, String> env = processBuilder.environment();

		env.put("RAS_PATH",		localRasPath); //$NON-NLS-1$
		env.put("RAS_PORT",		getLocalRasPortAsString()); //$NON-NLS-1$
		env.put("AGENT_HOST",	agentHost); //$NON-NLS-1$
		env.put("AGENT_PORT",	getAgentPortAsString()); //$NON-NLS-1$

		processBuilder.command("cmd.exe", "/c", "%RAS_PATH% cluster %AGENT_HOST%:%AGENT_PORT% --port=%RAS_PORT%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try {
			localRASProcess = processBuilder.start();
		} catch (Exception excp) {
			LOGGER.error("Error launch local RAS for server <{}>", this.getServerKey()); //$NON-NLS-1$
			LOGGER.error("Error: <{}>", processOutput, excp); //$NON-NLS-1$
			return false;
		}
		
		/////////////////////////////
//		localRASpid = localRASProcess.pid();
		
		return localRASProcess.isAlive();
	}
	
	/**
	 * Проверяет действительна ли еще авторизация на центральном сервере
	 * и если нет - запускает процесс авторизации.
	 *
	 * @param clusterId cluster ID
	 * @return boolean истекла/не истекла
	 */
	private boolean checkAutenticateAgent() {
		
		var needAuthenticate = false;
		try {
			LOGGER.debug("Gets the list administrators of server <{}>:<{}>", agentHost, agentPort); //$NON-NLS-1$
			agentConnection.getAgentAdmins();
			return true;
		} catch (Exception excp) {
			LOGGER.error("Error get the list of of server administrators: <{}>", excp.getLocalizedMessage()); //$NON-NLS-1$
			if (excp.getLocalizedMessage().contains(Messages.getString("Server.NoRightToManageCentralServer")) || //$NON-NLS-1$
					excp.getLocalizedMessage().contains(Messages.getString("Server.CentralServerAdminIsNotAuthenticated"))) // TODO учесть английский вариант //$NON-NLS-1$
				needAuthenticate = true;
		}
		
		if (needAuthenticate)
			return authenticateAgent();
		
		return false;
	}

	/**
	 * Establishes connection with the administration server of 1C:Enterprise server
	 * cluster
	 *
	 * @param address server address
	 * @param port    IP port
	 * @param timeout connection timeout (in milliseconds)
	 *
	 * @throws AgentAdminException in the case of errors.
	 */
	public void connectToAgent(String address, int port, long timeout) {
		if (isConnected()) {
			LOGGER.debug("The connection to server <{}> is already established", this.getServerKey()); //$NON-NLS-1$
			available = true;
			return;
		}
		
		LOGGER.debug("Try connect server <{}> to address:port=<{}:{}>", //$NON-NLS-1$
				this.getServerKey(), address, port);
		
		IAgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
		agentConnector = factory.createConnector(timeout);
		agentConnection = agentConnector.connect(address, port);
		
		available = true;
		readAgentVersion();
		
		LOGGER.debug("Server <{}> is connected now", this.getServerKey()); //$NON-NLS-1$
	}
	
	/**
	 * Terminates connection to the administration server
	 *
	 * @throws AgentAdminException in the case of errors.
	 */
	public void disconnectFromAgent() {
		if (!isConnected())
			return;
		
		if (useLocalRas && localRASProcess.isAlive()) {
			localRASProcess.destroy();
			LOGGER.info("Local RAS of Server <{}> is shutdown now", this.getServerKey()); //$NON-NLS-1$
		}
		
		try {
			agentConnector.shutdown();
			LOGGER.info("Server <{}> disconnected now", this.getServerKey()); //$NON-NLS-1$
		} catch (Exception excp) {
			LOGGER.info("Server <{}> disconnect error: <{}>", this.getServerKey(), excp.getLocalizedMessage()); //$NON-NLS-1$
		} finally {
			agentConnection = null;
			agentConnector = null;
		}
	}
	
	/**
	 * Authethicates a central server administrator agent.
	 * Need call of regCluster, getAgentAdmins, regAgentAdmin, unregAgentAdmin
	 * 
	 * @return {@code true} if authenticated, {@code false} overwise
	 */
	public boolean authenticateAgent() {
//		if (agentConnection == null)
//			throw new IllegalStateException("The connection is not established.");
		
		if (!isConnected())
			return false;
		
		IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
			
			LOGGER.debug("Try to autenticate the agent server <{}>", this.getServerKey()); //$NON-NLS-1$
			this.agentConnection.authenticateAgent(userName, password);
			LOGGER.debug("Authentication to the agent server <{}> was successful", this.getServerKey()); //$NON-NLS-1$
			
			// сохраняем новые user/pass после успешной авторизации
			if (saveNewUserpass) {
				this.agentUserName = userName;
				this.agentPassword = password;
				LOGGER.debug("New credentials for the agent server <{}> are saved", this.getServerKey()); //$NON-NLS-1$
			}
			
		};
		String authDescription = String.format(Messages.getString("Server.AuthenticationOfCentralServerAdministrator"), agentHost, agentPort); //$NON-NLS-1$
		
		return runAuthProcessWithRequestToUser(authDescription, agentUserName, agentPassword, authMethod);
	}
	
	/**
	 * Проверяет действительна ли еще авторизация на кластере
	 * и если нет - запускает процесс авторизации.
	 *
	 * @param clusterId cluster ID
	 * @return boolean действительна/не действительна
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
			if (excp.getLocalizedMessage().contains(Messages.getString("Server.NoRightToManageCluster")) || //$NON-NLS-1$
					excp.getLocalizedMessage().contains(Messages.getString("Server.ClusterAdminIsNotAuthenticate"))) // TODO учесть английский вариант //$NON-NLS-1$
				needAuthenticate = true;
		}
		
		if (needAuthenticate)
			return authenticateCluster(clusterId);
		
		return false;
	}

	/**
	 * Проверяет действительна ли еще авторизация в инфобазе
	 * и если нет - запускает процесс авторизации.
	 *
	 * @param clusterId cluster ID
	 * @param infobaseId infobase ID
	 * @return boolean действительна/не действительна
	 */
	private boolean checkAutenticateInfobase(UUID clusterId, UUID infobaseId) {
		
		return (getInfoBaseInfo(clusterId, infobaseId) != null);
				
	}
	
	/**
	 * Authethicates a server cluster administrator
	 * 
	 * @param clusterId cluster ID
	 * @param userName  cluster administrator name
	 * @param password  cluster administrator password
	 */
	public boolean authenticateCluster(UUID clusterId) {
//		if (agentConnection == null)
//			throw new IllegalStateException("The connection is not established.");
		
		if (!isConnected())
			return false;

		IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
			
			String clusterName = getClusterInfo(clusterId).getName();
			
			LOGGER.debug("Try to autenticate to the cluster <{}> of server <{}>", clusterName, this.getServerKey()); //$NON-NLS-1$
			agentConnection.authenticate(clusterId, userName, password);
			LOGGER.debug("Authentication to the cluster <{}> of server <{}> was successful", clusterName, //$NON-NLS-1$
					this.getServerKey());
			
			// сохраняем новые user/pass после успешной авторизации
			if (this.saveCredentials && saveNewUserpass) { // ||
				this.credentialsClustersCashe.put(clusterId, new String[] { userName, password, clusterName });
				LOGGER.debug("New credentials for the cluster <{}> of server <{}> are saved", clusterName, //$NON-NLS-1$
						this.getServerKey());
			}
			
		};
		
		String[] userAndPassword = credentialsClustersCashe.getOrDefault(clusterId, new String[] { "", "" }); //$NON-NLS-1$ //$NON-NLS-2$
		String authDescription = String.format(Messages.getString("Server.AuthenticationOfClusterAdminnistrator"), getServerKey(), getClusterInfo(clusterId).getName()); //$NON-NLS-1$
		
		return runAuthProcessWithRequestToUser(authDescription, userAndPassword[0], userAndPassword[1], authMethod);
		
	}
	
	private boolean runAuthProcessWithRequestToUser(String authDescription, String userName, String password,
			IRunAuthenticate authMethod) {
		try {
			// Сперва пытаемся авторизоваться под сохраненной учеткой (она может быть
			// инициализирована пустыми строками)
			authMethod.performAutenticate(userName, password, false);
			
		} catch (Exception excp) {
			LOGGER.debug("Autenticate to server <{}> error: <{}>", this.getServerKey(), excp.getLocalizedMessage()); //$NON-NLS-1$
			
			AuthenticateDialog authenticateDialog;
			String authExcpMessage = excp.getLocalizedMessage();
			int dialogResult;
			
			while (true) { // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена
				
				try {
					LOGGER.debug("Requesting new user credentials for the server <{}>", this.getServerKey()); //$NON-NLS-1$
					authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName,
							authDescription, authExcpMessage);
					dialogResult = authenticateDialog.open();
				} catch (Exception exc) {
					LOGGER.debug("Request new user credentials for the server <{}> failed", this.getServerKey()); //$NON-NLS-1$
					MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
					messageBox.setMessage(exc.getLocalizedMessage());
					messageBox.open();
					return false;
				}
				
				if (dialogResult == 0) {
					LOGGER.debug("The user has provided new credentials for the server <{}>", this.getServerKey()); //$NON-NLS-1$
					userName = authenticateDialog.getUsername();
					password = authenticateDialog.getPassword();
					try {
						authMethod.performAutenticate(userName, password, true);
						break;
					} catch (Exception exc) {
						LOGGER.debug("Autenticate to server <{}> error: <{}>", this.getServerKey(), //$NON-NLS-1$
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
	 * Adds infobase authentication parameters to the context of the current
	 * administration server connection
	 * 
	 * @param clusterId cluster ID
	 * @param userName  infobase administrator name
	 * @param password  infobase administrator password
	 */
	public void addInfobaseCredentials(UUID clusterId, String userName, String password) {
		if (!isConnected())
			return;
		
//		String clusterName = getClusterInfo(clusterId).getName();
		
		agentConnection.addAuthentication(clusterId, userName, password);
		LOGGER.debug("Add new infobase credentials for the cluster <{}> of server <{}>", clusterId, //$NON-NLS-1$
				this.getServerKey());
		
	}
	
	/**
	 * Gets the list of cluster descriptions registered on the central server
	 *
	 * @return list of cluster descriptions
	 */
	public List<IClusterInfo> getClusters() {
		if (!isConnected())
			return new ArrayList<>();
		
		LOGGER.debug("Get the list of cluster descriptions registered on the central server <{}>", this.getServerKey()); //$NON-NLS-1$
		
		List<IClusterInfo> clusters;
		try {
			clusters = agentConnection.getClusters();
		} catch (Exception excp) {
			LOGGER.error("Error get of the list of cluster descriptions", excp.getLocalizedMessage()); //$NON-NLS-1$
			return new ArrayList<>();
		}
		
		boolean needSaveConfig = false;
		for (IClusterInfo cluster : clusters) {
			LOGGER.debug("\tCluster: name=<{}>, ID=<{}>, host:port=<{}:{}>", //$NON-NLS-1$
					cluster.getName(), cluster.getClusterId(), cluster.getHostName(), cluster.getMainPort());
			
			// обновление имени кластера в кеше credentials
			if (saveCredentials) {
				String[] credentialClustersCashe = credentialsClustersCashe.get(cluster.getClusterId());
				if (credentialClustersCashe != null && !credentialClustersCashe[2].equals(cluster.getName())) {
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
	 * Gets the cluster descriptions
	 *
	 * @param clusterId cluster ID
	 * @return cluster descriptions
	 */
	public IClusterInfo getClusterInfo(UUID clusterId) {
		if (!isConnected())
			return null;
		
		LOGGER.debug("Get the cluster <{}> descriptions", clusterId); //$NON-NLS-1$
		
		IClusterInfo clusterInfo;
		try {
			clusterInfo = agentConnection.getClusterInfo(clusterId); //TODO debug
		} catch (Exception excp) {
			LOGGER.error("Error get the cluster descriptions", excp); //$NON-NLS-1$
			return null;
		}

		LOGGER.debug("Get the cluster descriptions succesful"); //$NON-NLS-1$
		return clusterInfo;
	}
	
	/**
	 * Gets the list of cluster manager descriptions.
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @return cluster descriptions
	 */
	public List<IClusterManagerInfo> getClusterManagers(UUID clusterId) {
		LOGGER.debug("Gets the list of cluster manager descriptions in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IClusterManagerInfo> clusterManagers;
		try { //TODO debug
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @return cluster descriptions
	 */
	public IClusterManagerInfo getClusterManagerInfo(UUID clusterId, UUID managerId) {
		LOGGER.debug("Get the cluster manager <{}> description of cluster  <{}>", managerId, clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		IClusterManagerInfo clusterManagerInfo;
		try { //TODO debug
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
	 * Central server authentication is required
	 *
	 * @return cluster descriptions
	 */
	public boolean regCluster(IClusterInfo clusterInfo) {
		if (clusterInfo.getClusterId().equals(emptyUuid))
			LOGGER.debug("Registration new cluster <{}>", clusterInfo.getName()); //$NON-NLS-1$
		else
			LOGGER.debug("Registration changes a cluster <{}>", clusterInfo.getClusterId()); //$NON-NLS-1$
		
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterInfo.getClusterId()); //$NON-NLS-1$
			return false;
		}
		
		if (!checkAutenticateAgent())
			return false;

		UUID newClusterId;
		try {
			newClusterId = agentConnection.regCluster(clusterInfo);
		} catch (Exception excp) {
			LOGGER.error("Error registraion cluster", excp); //$NON-NLS-1$
			throw excp;
		}

		if (clusterInfo.getClusterId().equals(emptyUuid))
			LOGGER.debug("Registration new cluster <{}> succesful", newClusterId); //$NON-NLS-1$
		else
			LOGGER.debug("Registration changes a cluster <{}> succesful", clusterInfo.getClusterId()); //$NON-NLS-1$
		return true;
	}
    
    /**
     * Deletes a cluster.
     * Cluster authentication is required
     *
	 * @param clusterId cluster ID
	 * 
	 */
	public boolean unregCluster(UUID clusterId) {
		LOGGER.debug("Delete a cluster <{}>", clusterId); //$NON-NLS-1$
		
		var unregSuccesful = false;
		String unregMessage = null;
		
		if (!isConnected())
			unregMessage = Messages.getString("Server.TheConnectionAClusterIsNotEstablished"); //$NON-NLS-1$
		
		if (!checkAutenticateCluster(clusterId))
			unregMessage = Messages.getString("Server.TheClusterAuthenticationError"); //$NON-NLS-1$
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @return list of cluster administrators
	 */
	public List<IRegUserInfo> getClusterAdmins(UUID clusterId) {
		LOGGER.debug("Get the list of short descriptions of infobases registered in the cluster <{}>", clusterId); //$NON-NLS-1$
		if (!isConnected())
			return new ArrayList<>();
		
		// TODO
		return null;
		
	}
	
	/**
	 * Deletes a cluster administrator.
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @return list of cluster administrators
	 */
	public void unregClusterAdmin(UUID clusterId, String name) {
		LOGGER.debug("Get the list of short descriptions of infobases registered in the cluster <{}>", clusterId); //$NON-NLS-1$
		if (!isConnected())
			return;
		
		// TODO
		return;
		
	}
	
	/**
	 * Gets the list of short descriptions of infobases registered in the cluster
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @return list of short descriptions of cluster infobases
	 */
	public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterId) {
		LOGGER.debug("Get the list of short descriptions of infobases registered in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IInfoBaseInfoShort> clusterInfoBases;
		try {
			clusterInfoBases = agentConnection.getInfoBasesShort(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of short descriptions of infobases", excp); //$NON-NLS-1$
			return new ArrayList<>();
		}
		
		clusterInfoBases.forEach(ib -> LOGGER.debug("\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr())); //$NON-NLS-1$
		
		LOGGER.debug("Get the list of short descriptions of infobases succesful"); //$NON-NLS-1$
		return clusterInfoBases;
	}
	
	/**
	 * Gets the list of full descriptions of infobases registered in the cluster
	 * Cluster authentication is required
	 * For each infobase in the cluster, infobase authentication is required
	 * If infobase authentication is not performed, only
	 * fields that correspond to short infobase description fields will be filled
	 *
	 * @param clusterId cluster ID
	 * @return list of full descriptions of cluster infobases
	 */
	public List<IInfoBaseInfo> getInfoBases(UUID clusterId) {
		LOGGER.debug("Get the list of descriptions of infobases registered in the cluster <{}>", clusterId); //$NON-NLS-1$
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IInfoBaseInfo> clusterInfoBases;
		try { // TODO For each infobase in the cluster, infobase authentication is required
			clusterInfoBases = agentConnection.getInfoBases(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of infobases", excp); //$NON-NLS-1$
			return new ArrayList<>();
		}
		
		clusterInfoBases.forEach(ib -> {
			LOGGER.debug("\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr()); //$NON-NLS-1$
		});
		
		LOGGER.debug("Get the list of descriptions of infobases succesful"); //$NON-NLS-1$
		return clusterInfoBases;
	}
	
	/**
	 * Gets a short infobase description.
	 * Cluster authentication is required
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 * @return infobase full infobase description
	 */
	public IInfoBaseInfoShort getInfoBaseShortInfo(UUID clusterId, UUID infobaseId) {
		LOGGER.debug("Get the short description for infobase <{}> of the cluster <{}>", infobaseId, clusterId); //$NON-NLS-1$
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		IInfoBaseInfoShort info;
		try {
			info = agentConnection.getInfoBaseShortInfo(clusterId, infobaseId);
		} catch (Exception excp) {
			LOGGER.error("Error get the short info for infobase", excp); //$NON-NLS-1$
			return null;
		}
		
		LOGGER.debug("Get the short description for infobase <{}> succesful", info.getName()); //$NON-NLS-1$
		return info;
	}
	
	/**
	 * Gets the full infobase description.
	 * Cluster authentication is required.
	 * Infobase authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 * @return infobase full infobase description
	 */
	public IInfoBaseInfo getInfoBaseInfo(UUID clusterId, UUID infobaseId) {
		LOGGER.debug("Get the description for infobase <{}> of the cluster <{}>", infobaseId, clusterId); //$NON-NLS-1$
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
//		addInfoBaseCredentials(clusterID, "", ""); // в добавлении пустых кредов нет необходимости
			
		IInfoBaseInfo infobaseInfo;
		
		try {
			infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
		} catch (Exception excp) {
			
			AuthenticateDialog authenticateDialog;
			String authExcpMessage = excp.getLocalizedMessage();
			int dialogResult;
			
			var userName = ""; //$NON-NLS-1$
			var authDescription = Messages.getString("Server.AuthenticationOfInfobase"); //$NON-NLS-1$
			while (true) { // пока не подойдет пароль, или пользователь не нажмет Отмена
				
				try {
					LOGGER.debug("Requesting new user credentials for the infobase <{}>", infobaseId); //$NON-NLS-1$
					authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName,
							authDescription, authExcpMessage);
					dialogResult = authenticateDialog.open();
				} catch (Exception exc) {
					LOGGER.debug("Request new user credentials for the infobase failed: <{}>", exc.getLocalizedMessage()); //$NON-NLS-1$
					var messageBox = new MessageBox(Display.getDefault().getActiveShell());
					messageBox.setMessage(exc.getLocalizedMessage());
					messageBox.open();
					return null;
				}
				
				if (dialogResult == 0) {
					LOGGER.debug("The user has provided new credentials for the infobase <{}>", infobaseId); //$NON-NLS-1$
					userName = authenticateDialog.getUsername();
					String password = authenticateDialog.getPassword();
					try {
						addInfobaseCredentials(clusterId, userName, password);
						infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
						break;
					} catch (Exception exc) {
						authExcpMessage = exc.getLocalizedMessage();
						LOGGER.debug("Autenticate to infobase <{}> error: <{}>", this.getServerKey(), authExcpMessage); //$NON-NLS-1$
						continue;
					}
				} else {
					LOGGER.debug("Autenticate to infobase <{}> abort by the user", this.getServerKey()); //$NON-NLS-1$
					return null;
				}
			}
		}
		
		return infobaseInfo;
	}
	
	/**
	 * Gets the infobase name.
	 * Cluster authentication is required
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 * @return infobase full infobase description
	 */
	public String getInfoBaseName(UUID clusterId, UUID infobaseId) {
		LOGGER.debug("Get the name for infobase <{}> of the cluster <{}>", infobaseId, clusterId); //$NON-NLS-1$
		
		if (infobaseId.equals(emptyUuid)) {
			LOGGER.debug("Infobase ID is empty"); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		
		IInfoBaseInfoShort infobaseShortInfo = getInfoBaseShortInfo(clusterId, infobaseId);
		return infobaseShortInfo == null ? "" : infobaseShortInfo.getName(); //$NON-NLS-1$
		
	}
	
	/**
	 * Creates an infobase in a cluster.
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
	 */
	public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode) {
		LOGGER.debug("Creates an infobase <{}> in a cluster <{}>", info.getName(), clusterId); //$NON-NLS-1$
		if (!isConnected())
			return emptyUuid;
		
		if (!checkAutenticateCluster(clusterId))
			return emptyUuid;
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
	 */
	public void updateInfoBaseShort(UUID clusterId, IInfoBaseInfoShort info) {
		LOGGER.debug("Changes short description infobase <{}> in the cluster <{}>", info.getInfoBaseId(), clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return;
		
		if (!checkAutenticateCluster(clusterId))
			return;
		
		try { // TODO debug
			agentConnection.updateInfoBaseShort(clusterId, info);
		} catch (Exception excp) {
			LOGGER.error("Error changes short description infobase", excp); //$NON-NLS-1$
			throw excp;
		}
	}
	
	/**
	 * Changes infobase parameters.
	 * <ul>
	 * Infobase authentication is required
	 * (Здесь не нужно авторизоваться в базе, метод необходимо вызывать сразу после getInfoBaseInfo)
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
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
	 * <ul>
	 * Infobase authentication is required
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase parameters
	 * @param dropMode   - infobase drop mode:
	 * <ul> 0 - do not delete the database
	 * <p> 1 - delete the database
	 * <p> 2 - clear the database
	 */
	public boolean dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode) {
		
		if (!checkAutenticateInfobase(clusterId, infobaseId))
			return false;
		
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
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @return List of session descriptions
	 */
	public List<ISessionInfo> getSessions(UUID clusterId) {
		LOGGER.debug("Gets the list of cluster session descriptions in the cluster <{}>", clusterId); //$NON-NLS-1$
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<ISessionInfo> sessions;
		try {
			sessions = agentConnection.getSessions(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of cluster session descriptions", excp); //$NON-NLS-1$
			return new ArrayList<>();
//			throw new IllegalStateException("Error get list of cluster session descriptions");
		}
		sessions.forEach(s -> {
			LOGGER.debug("\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
					getApplicationName(s.getAppId()), s.getSessionId());
		});
		
		LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
		return sessions;
		
	}
	
	/**
	 * Gets a session description.
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @return List of session descriptions
	 */
	public ISessionInfo getSessionInfo(UUID clusterId, UUID sid) {
		LOGGER.debug("Gets a session <{}> description in the cluster <{}>", sid, clusterId); //$NON-NLS-1$
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		ISessionInfo sessionInfo;
		try {
			sessionInfo = agentConnection.getSessionInfo(clusterId, sid); // TODO debug
		} catch (Exception excp) {
			LOGGER.error("Error get the list of cluster session descriptions", excp); //$NON-NLS-1$
			return null;
//			throw new IllegalStateException("Error get list of cluster session descriptions");
		}
		LOGGER.debug("\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
				getApplicationName(sessionInfo.getAppId()), sessionInfo.getSessionId());
		
		LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
		return sessionInfo;
		
	}
	
	/**
	 * Gets the list of infobase session descriptions.
	 * Cluster authentication is required
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 * @return Infobase sessions
	 */
	public List<ISessionInfo> getInfoBaseSessions(UUID clusterId, UUID infobaseId) {
		LOGGER.debug("Gets the list of infobase <{}> session descriptions in the cluster <{}>", infobaseId, clusterId); //$NON-NLS-1$
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();

		
		List<ISessionInfo> sessions;
		try {
			sessions = agentConnection.getInfoBaseSessions(clusterId, infobaseId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of infobase session descriptions", excp); //$NON-NLS-1$
			return new ArrayList<>();
		}
		sessions.forEach(s -> {
			LOGGER.debug("\tSession: application name=<{}>, session ID=<{}>", //$NON-NLS-1$
					getApplicationName(s.getAppId()), s.getSessionId());
		});
		
		LOGGER.debug("Get the list of cluster session descriptions succesful"); //$NON-NLS-1$
		return sessions;
		
	}
	
	public List<ISessionInfo> getWorkingProcessSessions(UUID clusterId, UUID workingProcessId) {
		if (isConnected()) {
			List<ISessionInfo> clusterSessions = agentConnection.getSessions(clusterId);
			List<ISessionInfo> wpSessions = new ArrayList<>();
//			clusterSessions.forEach(session -> {
//				if (session.getWorkingProcessId().equals(workingProcessId))
//					wpSessions.add(session);
//			});
			
			wpSessions = clusterSessions
				.stream()
				.filter(s -> s.getWorkingProcessId().equals(workingProcessId))
				.collect(Collectors.toList());
			
			return wpSessions;
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Terminates a session in the cluster with default message.
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @param sessionId infobase ID
	 * @param message   error message for user
	 */
	public void terminateSession(UUID clusterId, UUID sessionId) {
		terminateSession(clusterId, sessionId, Messages.getString("Server.TerminateSessionMessage")); //$NON-NLS-1$
	}
	
	/**
	 * Terminates a session in the cluster.
	 * Cluster authentication is required.
	 *
	 * @param clusterId cluster ID
	 * @param sessionId infobase ID
	 * @param message   error message for user
	 */
	public void terminateSession(UUID clusterId, UUID sessionId, String message) {
		LOGGER.debug("Terminates a session <{}> in the cluster <{}>", sessionId, clusterId); //$NON-NLS-1$
		if (!isConnected())
			return;
		
		if (!checkAutenticateCluster(clusterId))
			return;
		
		try {
			agentConnection.terminateSession(clusterId, sessionId, message);
		} catch (Exception excp) {
			LOGGER.error("Error terminate a session", excp); //$NON-NLS-1$
		}
		LOGGER.debug("Session <{}> in the cluster <{} is terminate>", sessionId, clusterId); //$NON-NLS-1$
	}
	
	/**
	 * Terminates all sessions for all infobases in the cluster
	 *
	 * @param clusterId cluster ID
	 */
	public void terminateAllSessions(UUID clusterId) {
		
		agentConnection.getSessions(clusterId)
			.forEach(session -> terminateSession(clusterId, session.getSid()));
		
	}
	
	/**
	 * Terminates all sessions for infobase in the cluster
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 */
	public void terminateAllSessionsOfInfobase(UUID clusterId, UUID infobaseId, boolean onlyUsersSession) {
		
		List<ISessionInfo> sessions = agentConnection.getInfoBaseSessions(clusterId, infobaseId);
		for (ISessionInfo session : sessions) {
			if (onlyUsersSession && !isUserSession(session))
				continue;
			
			agentConnection.terminateSession(clusterId, session.getSid());
		}
	}
	
	private boolean isUserSession(ISessionInfo session) {
		String appName = session.getAppId();
		return appName.equals(THIN_CLIENT) || appName.equals(THICK_CLIENT);
	}
	
	/**
	 * Gets the list of short descriptions of cluster connections.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */	
	public List<IInfoBaseConnectionShort> getConnectionsShort(UUID clusterId) {
		if (isConnected())
			return agentConnection.getConnectionsShort(clusterId);
		// TODO
		return new ArrayList<>();
	}
	
	/**
	 * Gets a short description of a connection.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param connectionId  connection ID
	 * @return short connection description
	 */	
	public IInfoBaseConnectionShort getConnectionInfoShort(UUID clusterId, UUID connectionId) {
		if (isConnected())
			return agentConnection.getConnectionInfoShort(clusterId, connectionId);
		// TODO
		return null;
	}
	
	/**
	 * Gets the list of short descriptions of infobase connections. 
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param infobaseId infobase ID
	 * @return list of short infobase connection descriptions
	 */	
	public List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(UUID clusterId, UUID infobaseId) {
		if (isConnected())
			return agentConnection.getInfoBaseConnectionsShort(clusterId, infobaseId);
		// TODO
		return new ArrayList<>();
	}
	
	/**
	 * Gets the list of infobase connection descriptions for a working process.
	 * Cluster authentication is required.
	 * Infobase authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param processId process ID
	 * @param infobaseId infobase ID
	 * @return list of infobase connection descriptions
	 */	
	public List<IInfoBaseConnectionInfo> getInfoBaseConnections(UUID clusterId, UUID processId, UUID infobaseId) {
		if (isConnected())
			return agentConnection.getInfoBaseConnections(clusterId, processId, infobaseId);
		// TODO
		return new ArrayList<>();
	}
	
	/**
	 * Closes an infobase connection.
	 * Cluster authentication is required.
	 * Infobase authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param processId working process ID
	 * @param connectionId connection ID
	 */	
	public boolean disconnectConnection(UUID clusterId, UUID processId, UUID connectionId, UUID infobaseId) {
		LOGGER.debug("Close connection in the cluster <{}>, processId <{}>, connectionId  <{}>", //$NON-NLS-1$
				clusterId, processId, connectionId);
		
		if (!isConnected())
			return false;
		
		if (!checkAutenticateCluster(clusterId))
			return false;
		
		if (!checkAutenticateInfobase(clusterId, infobaseId))
			return false;
		

		try { // TODO debug
			agentConnection.disconnect(clusterId, processId, connectionId);
		} catch (Exception excp) {
			LOGGER.error("Error close connection", excp); //$NON-NLS-1$
			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}
		return true;
	}
	
	/**
	 * Interrupt current server call.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param processId working process ID
	 * @param connectionId connection ID
	 * @return infobase full infobase description
	 */	
	public void interruptCurrentServerCall(UUID clusterId, UUID sid, String message) {
		if (isConnected())
			agentConnection.interruptCurrentServerCall(clusterId, sid, message);
		// TODO
	}
	
	public List<IInfoBaseConnectionShort> getWorkingProcessConnectionsShort(UUID clusterId, UUID workingProcessId) {
		if (isConnected()) {

			return agentConnection.getConnectionsShort(clusterId)
					.stream()
					.filter(c -> c.getWorkingProcessId().equals(workingProcessId))
					.collect(Collectors.toList());
			
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Gets the list of object locks.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IObjectLockInfo> getLocks(UUID clusterId) {
		LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IObjectLockInfo> getInfoBaseLocks(UUID clusterId, UUID infobaseId) {
		LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IObjectLockInfo> getConnectionLocks(UUID clusterId, UUID connectionId) {
		LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IObjectLockInfo> getSessionLocks(UUID clusterId, UUID infobaseId, UUID sid) {
		LOGGER.debug("Gets the list of object locks in the cluster <{}>", clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IWorkingProcessInfo> getWorkingProcesses(UUID clusterId) {
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IWorkingProcessInfo> workingProcesses;
		try { // TODO debug
			LOGGER.debug("Gets the list of descriptions of working processes registered in the cluster <{}>", clusterId); //$NON-NLS-1$
			workingProcesses = agentConnection.getWorkingProcesses(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of short descriptions of working processes", excp); //$NON-NLS-1$
			throw new IllegalStateException("Error get working processes"); //$NON-NLS-1$
		}
		workingProcesses.forEach(wp -> {
			LOGGER.debug("\tWorking process: host name=<{}>, main port=<{}>", //$NON-NLS-1$
					wp.getHostName(), wp.getMainPort());
		});
		
		LOGGER.debug("Get the list of short descriptions of working processes succesful"); //$NON-NLS-1$
		return workingProcesses;
	}
	
	/**
	 * Gets a working process description.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public IWorkingProcessInfo getWorkingProcessInfo(UUID clusterId, UUID processId) {
		LOGGER.debug("Gets a working process <{}> description in the cluster <{}>", processId, clusterId); //$NON-NLS-1$
		
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
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
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IWorkingProcessInfo> getServerWorkingProcesses(UUID clusterId, UUID serverId) {
		if (isConnected())
			return agentConnection.getServerWorkingProcesses(clusterId, serverId);
		 // TODO 
		return new ArrayList<>();
	}
	
	/**
	 * Gets the list of descriptions of working servers registered in the cluster.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public List<IWorkingServerInfo> getWorkingServers(UUID clusterId) {
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IWorkingServerInfo> workingServers;
		try { // TODO debug
			LOGGER.debug("Gets the list of descriptions of working servers registered in the cluster <{}>", clusterId); //$NON-NLS-1$
			workingServers = agentConnection.getWorkingServers(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of working servers", excp); //$NON-NLS-1$
			throw new IllegalStateException("Error get working servers"); //$NON-NLS-1$
		}
		workingServers.forEach(ws -> {
			LOGGER.debug("\tWorking server: host name=<{}>, main port=<{}>", //$NON-NLS-1$
					ws.getHostName(), ws.getMainPort());
		});
		
		LOGGER.debug("Get the list of descriptions of working servers succesful"); //$NON-NLS-1$
		return workingServers;
	}
	
	/**
	 * Gets a description of a working server registered in the cluster.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public IWorkingServerInfo getWorkingServerInfo(UUID clusterId, UUID serverId) {
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		IWorkingServerInfo workingServerInfo;
		try { // TODO debug
			LOGGER.debug("Gets the description of working server <{}> registered in the cluster <{}>", serverId, clusterId); //$NON-NLS-1$
			workingServerInfo = agentConnection.getWorkingServerInfo(clusterId, serverId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of working server", excp); //$NON-NLS-1$
			throw new IllegalStateException("Error get working server"); //$NON-NLS-1$
		}
			
		LOGGER.debug("\tWorking server: host name=<{}>, main port=<{}>", //$NON-NLS-1$
				workingServerInfo.getHostName(), workingServerInfo.getMainPort());
		
		LOGGER.debug("Get the list of short descriptions of working processes succesful"); //$NON-NLS-1$
		return workingServerInfo;
	}
	
	/**
	 * Creates a working server or changes the description of an existing one.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @return infobase full infobase description
	 */
	public boolean regWorkingServer(UUID clusterId, IWorkingServerInfo serverInfo, boolean createNew) {
		if (!isConnected())
			return false;
		
		if (!checkAutenticateCluster(clusterId))
			return false;
		
		if (createNew)
			LOGGER.debug("Registration NEW working server"); //$NON-NLS-1$
		
		try { // TODO debug
			LOGGER.debug("Registration working server <{}> registered in the cluster <{}>", serverInfo.getName(), clusterId); //$NON-NLS-1$
			agentConnection.regWorkingServer(clusterId, serverInfo);
		} catch (Exception excp) {
			LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
			throw excp;
		}
			
		LOGGER.debug("\tRegistration working server: name=<{}>, host name=<{}>, main port=<{}>", //$NON-NLS-1$
				serverInfo.getName(), serverInfo.getHostName(), serverInfo.getMainPort());
		
		LOGGER.debug("Registration working server succesful"); //$NON-NLS-1$
		return true;
	}
	
	/**
	 * Deletes a working server and removes its cluster registration.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public boolean unregWorkingServer(UUID clusterId, UUID serverId) {
		if (!isConnected())
			return false;
		
		if (!checkAutenticateCluster(clusterId))
			return false;
		
		try { // TODO debug
			LOGGER.debug("Deletes a working server <{}> from the cluster <{}>", serverId, clusterId); //$NON-NLS-1$
			agentConnection.unregWorkingServer(clusterId, serverId);
		} catch (Exception excp) {
			LOGGER.error("Error registration working server", excp); //$NON-NLS-1$
			throw excp;
		}
		
		LOGGER.debug("Registration working server succesful"); //$NON-NLS-1$
		return true;
	}
	
	/**
	 * Gets the list of cluster service descriptions.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<IClusterServiceInfo> getClusterServices(UUID clusterId) {
		return null; //TODO
		
	}
	
	/**
	 * Applies assignment rules.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void applyAssignmentRules(UUID clusterId, int full) {
		return; //TODO
	}
	
	/**
	 * Gets the list of descriptions of working server assignment rules.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<IAssignmentRuleInfo> getAssignmentRules(UUID clusterId, UUID serverId) {
		return null; //TODO
	}
	
	/**
	 * Creates an assignment rule, changes an existing one, or moves an existing rule to a new position.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public UUID regAssignmentRule(UUID clusterId, UUID serverId, IAssignmentRuleInfo info, int position) {
		return null; //TODO
	}
	
	/**
	 * Deletes an assignment rule from the list of working server rules.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void unregAssignmentRule(UUID clusterId, UUID serverId, UUID ruleId) {
		return; //TODO
	}
	
	/**
	 * Gets an assignment rule description.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public IAssignmentRuleInfo getAssignmentRuleInfo(UUID clusterId, UUID serverId, UUID ruleId) {
		return null; //TODO
	}
	
	/**
	 * Gets the list of cluster security profiles.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfile> getSecurityProfiles(UUID clusterId) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates a cluster security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfile(UUID clusterId, ISecurityProfile profile) {
		return; //TODO
	}
	
	/**
	 * Deletes a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfile(UUID clusterId, String spName) {
		return; //TODO
	}
	
	/**
	 * Gets the list of virtual directories of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileVirtualDirectory> getSecurityProfileVirtualDirectories(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates a virtual directory of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileVirtualDirectory(UUID clusterId, ISecurityProfileVirtualDirectory directory) {
		return; //TODO
	}
	
	/**
	 * Deletes a virtual directory of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileVirtualDirectory(UUID clusterId, String spName, String alias) {
		return; //TODO
	}
	
	
	/**
	 * Gets the list of allowed COM classes of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileCOMClass> getSecurityProfileComClasses(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates an allowed COM class of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileComClass(UUID clusterId, ISecurityProfileCOMClass comClass) {
		return; //TODO
	}
	
	/**
	 * Deletes an allowed COM class of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileComClass(UUID clusterId, String spName, String name) {
		return; //TODO
	}
	
	/**
	 * Gets the list of allowed add-ins of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileAddIn> getSecurityProfileAddIns(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates an allowed add-in of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileAddIn(UUID clusterId, ISecurityProfileAddIn addIn) {
		return; //TODO
	}
	
	/**
	 * Deletes an allowed add-in of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileAddIn(UUID clusterId, String spName, String name) {
		return; //TODO
	}
	
	/**
	 * Gets the list of allowed unsafe external modules of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileExternalModule> getSecurityProfileUnsafeExternalModules(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates an allowed unsafe external module of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileUnsafeExternalModule(UUID clusterId, ISecurityProfileExternalModule module) {
		return; //TODO
	}
	
	/**
	 * Deletes an allowed unsafe external module of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileUnsafeExternalModule(UUID clusterId, String spName, String name) {
		return; //TODO
	}
	
	/**
	 * Gets the list of allowed applications of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileApplication> getSecurityProfileApplications(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates an allowed application of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileApplication(UUID clusterId, ISecurityProfileApplication app) {
		return; //TODO
	}
	
	/**
	 * Deletes an allowed application of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileApplication(UUID clusterId, String spName, String name) {
		return; //TODO
	}
	
	/**
	 * Gets the list of Internet resources of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<ISecurityProfileInternetResource> getSecurityProfileInternetResources(UUID clusterId, String spName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates an Internet resource of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void createSecurityProfileInternetResource(UUID clusterId, ISecurityProfileInternetResource resource) {
		return; //TODO
	}
	
	/**
	 * Deletes an Internet resource of a security profile.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void dropSecurityProfileInternetResource(UUID clusterId, String spName, String name) {
		return; //TODO
	}
	
	/**
	 * Gets the list of resource counters.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<IResourceConsumptionCounter> getResourceConsumptionCounters(UUID clusterId) {
		return null; //TODO
	}
	
	/**
	 * Gets resource counters description.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public IResourceConsumptionCounter getResourceConsumptionCounterInfo(UUID clusterId, String counterName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates a resource counter.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void regResourceConsumptionCounter(UUID clusterId, IResourceConsumptionCounter counter) {
		return; //TODO
	}
	
	/**
	 * Deletes a resource counter
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void unregResourceConsumptionCounter(UUID clusterId, String counterName) {
		return; //TODO
	}
	
	/**
	 * Gets the list of resource limits.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public List<IResourceConsumptionLimit> getResourceConsumptionLimits(UUID clusterId) {
		return null; //TODO
	}
	
	/**
	 * Gets resource limits description
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public IResourceConsumptionLimit getResourceConsumptionLimitInfo(UUID clusterId, String limitName) {
		return null; //TODO
	}
	
	/**
	 * Creates or updates a resource limit.
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param serverId  server ID
	 * @return infobase full infobase description
	 */
	public void regResourceConsumptionLimit(UUID clusterId, IResourceConsumptionLimit limit) {
		return; //TODO
	}
	
	/**
	 * Deletes a resource limits
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param limitName  limit name
	 */
	public void unregResourceConsumptionLimit(UUID clusterId, String limitName) {
		return; //TODO
	}
	
	/**
	 * Gets the list of resource counter values
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param counterName  counterName
	 * @param object  object
	 * @return list of resource counter values
	 */
	public List<IResourceConsumptionCounterValue> getResourceConsumptionCounterValues(UUID clusterId, String counterName, String object) {
		return null; //TODO
	}
	
	/**
	 * Deletes a resource counter values
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param counterName  counterName
	 * @param object  object
	 */
	public void clearResourceConsumptionCounterAccumulatedValues(UUID clusterId, String counterName, String object) {
		return; //TODO
	}
	
	/**
	 * Gets the list of resource counter accumulated values
	 * Cluster authentication is required.
	 *
	 * @param clusterId  cluster ID
	 * @param counterName  counterName
	 * @param object  object
	 * @return ist of resource counter accumulated values
	 */
	public List<IResourceConsumptionCounterValue> getResourceConsumptionCounterAccumulatedValues(UUID clusterId, String counterName, String object) {
		return null; //TODO
	}

	
	
	
	
	
	
	
	
	
}
