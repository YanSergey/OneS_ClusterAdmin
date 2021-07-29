package ru.yanygin.clusterAdminLibrary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import com._1c.v8.ibis.admin.IRegUserInfo;
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
	
	private static Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary");
	private UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	private IAgentAdminConnector agentConnector;
	private IAgentAdminConnection agentConnection;
	public String agentVersion = "";
	
	public static final String THIN_CLIENT = "1CV8C";
	public static final String THICK_CLIENT = "1CV8";
	public static final String DESIGNER = "Designer";
	public static final String SERVER_CONSOLE = "SrvrConsole";
	public static final String RAS_CONSOLE = "RAS";
	public static final String JOBSCHEDULER = "JobScheduler";
	
	interface IRunAuthenticate {
		void performAutenticate(String userName, String password, boolean saveNewUserpass);
	}
	
	interface IGetInfobaseInfo {
		IInfoBaseInfo getInfo(String userName, String password);
	}
	
	public String getApplicationName(String appId) {
		switch (appId) {
			case THIN_CLIENT:
				return "Thin client";
			case THICK_CLIENT:
				return "Thick client";
			case DESIGNER:
				return "Designer";
			case SERVER_CONSOLE:
				return "Cluster Console";
			case RAS_CONSOLE:
				return "Administration Server";
			case JOBSCHEDULER:
				return "Job scheduler";
			case "":
				return "";
			default:
				return String.format("Unknown client (%s)", appId);
		}
	}
	
	public void getAgentVersion() {
		
		if (!isConnected())
			return;
		
		try {
			agentVersion = agentConnection.getAgentVersion();
			LOGGER.debug("Agent version of server <{}> is <{}>", this.getServerKey(), agentVersion);
		} catch (Exception e) {
			agentVersion = "Unknown";
			LOGGER.error("Unknown agent version of server <{}>", this.getServerKey());
		}
	}
	
	public Server(String serverName) {
		
		calculateServerParams(serverName);
		
		this.useLocalRas		= false;
		this.localRasPort		= 0;
		this.localRasV8version	= "";
		this.localRasPath		= "";
		this.autoconnect		= false;
		this.available			= false;
		this.agentUserName		= "";
		this.agentPassword		= "";
		this.saveCredentials	= false;
		
		init();
		
	}
	
	public void init() {
		
//		this.agentUserName = "";// Зачем?
//		this.agentPassword = "";
		
		if (this.credentialsClustersCashe == null)
			this.credentialsClustersCashe = new HashMap<>();
		
		LOGGER.info("Server <{}> init done", this.getServerKey());
		
	}
	
	// Надо определиться что должно являться ключем, агент (Server:1540) или
	// менеджер (Server:1541) или RAS (Server:1545)
	public String getServerKey() {
		return agentHost.concat(":").concat(Integer.toString(agentPort));
	}
	
	public String getServerDescriptionOld() {
		var rasPortString = "";
		if (useLocalRas) {
			rasPortString = "(*".concat(Integer.toString(localRasPort)).concat(")");
		} else {
			rasPortString = Integer.toString(this.rasPort);
		}
		
		var serverDescriptionPattern = isConnected() ? "%s:%s-%s (%s)" : "%s:%s-%s";
		
		return String.format(serverDescriptionPattern, agentHost, Integer.toString(agentPort), rasPortString, agentVersion);
		
	}
	
	public String getServerDescription() {

		String serverDescriptionPattern;
		String serverDescription;
		
		if (useLocalRas) {
			serverDescriptionPattern = isConnected() ? "(local-RAS:%s)->%s:%s (%s)" : "(local-RAS:%s)->%s:%s";
			serverDescription = String.format(serverDescriptionPattern, getLocalRasPortAsString(), agentHost, getAgentPortAsString(), agentVersion);
		} else {
			serverDescriptionPattern = isConnected() ? "%s:%s (%s)" : "%s:%s";
			serverDescription = String.format(serverDescriptionPattern, agentHost, getAgentPortAsString(), agentVersion);
		}
		return serverDescription;
		
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
										String localRasPath,
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
		this.localRasPath				= localRasPath;
		this.autoconnect				= autoconnect;
		this.saveCredentials			= saveCredentials;
		this.agentUserName				= agentUser;
		this.agentPassword				= agentPassword;
		this.credentialsClustersCashe	= credentialsClustersCashe;
		
		if (this.autoconnect)
			connectAndAuthenticate(false);
		
		LOGGER.info("Set new properties for server <{}>", this.getServerKey());
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
		String[] ar = serverAddress.split(":");
		if (ar.length > 0) {
			serverName = ar[0];
		} else {
			serverName = "localhost";
		}
		
		return serverName;
	}
	
	private int cutManagerPort(String serverAddress) {
		int port;
		String[] ar = serverAddress.split(":");
		if (ar.length == 1) {
			port = 1541;
		} else {
			port = Integer.parseInt(ar[1]);
		}
		return port;
	}
	
	private int cutRemoteRASPort(String serverAddress) {
		int port;
		String[] ar = serverAddress.split(":");
		if (ar.length == 1) {
			port = 1545;
		} else {
			port = Integer.parseInt(ar[1].substring(0, ar[1].length() - 1).concat("5"));
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
			serverAddress = "localhost";
		
		String[] ar = serverAddress.split(":");
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
		
		LOGGER.info("Calculate params for Server <{}> ", this.getServerKey());
		
	}
	
	public boolean connectAndAuthenticate(boolean disconnectAfter) {
		LOGGER.debug("Server <{}> start connection", this.getServerKey());
		
		if (isConnected()) {
			return true;
		}
		
		if (!checkAndRunLocalRAS())
			return false;
		
		String rasHost 	= useLocalRas ? "localhost" : this.rasHost;
		int rasPort 	= useLocalRas ? localRasPort : this.rasPort;
		
		try {
			connectToAgent(rasHost, rasPort, 20);
			
			if (disconnectAfter) {
				disconnectFromAgent();
			}
		} catch (Exception excp) {
			available = false;
			LOGGER.info("Server <{}> connection error: <{}>", this.getServerKey(), excp.getLocalizedMessage());
			return false;
		}
		return true;
		
	}
	
	private boolean checkAndRunLocalRAS() {
		if (!useLocalRas)
			return true;
		
		if (localRasPath.isBlank() || localRasPort == 0) {
			var message = String.format("Local RAS path or port for Server %s is empty", this.getServerKey());
			LOGGER.error(message);
			
			var messageBox = new MessageBox(Display.getDefault().getActiveShell());
			messageBox.setMessage(message);
			messageBox.open();
			
			return false;
		}
		
		///////////////////////////// пока только Windows
		var processBuilder = new ProcessBuilder();
		var processOutput = "";

		Map<String, String> env = processBuilder.environment();

		env.put("RAS_PATH",		localRasPath);
		env.put("RAS_PORT",		getLocalRasPortAsString());
		env.put("AGENT_HOST",	agentHost);
		env.put("AGENT_PORT",	getAgentPortAsString());

		processBuilder.command("cmd.exe", "/c", "%RAS_PATH% cluster %AGENT_HOST%:%AGENT_PORT% --port=%RAS_PORT%");
		try {
			localRASProcess = processBuilder.start();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(localRASProcess.getInputStream(), "windows-1251"));
			// TODO utf-8 ???
//			String line;
//			while ((line = reader.readLine()) != null) {
//				processOutput = processOutput.concat(System.lineSeparator()).concat(line);
//			}
//			int exitCode = process.waitFor();
//			if (exitCode != 0) {
//				LOGGER.error("Error launch local RAS for server <{}>", this.getServerKey());
//				LOGGER.error("Error: <{}>", processOutput);
//			}
		} catch (Exception excp) {
			LOGGER.error("Error launch local RAS for server <{}>", this.getServerKey());
			LOGGER.error("Error: <{}>", processOutput, excp);
			return false;
		}
		
		/////////////////////////////
//		localRASpid = localRASProcess.pid();
		
		return true;
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
		if (agentConnection != null) {
			LOGGER.debug("The connection to server <{}> is already established", this.getServerKey());
			available = true;
			return;
		}
		
		LOGGER.debug("Try connect server <{}> to address:port=<{}:{}>",
				this.getServerKey(), address, port);
		
		IAgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
		agentConnector = factory.createConnector(timeout);
		agentConnection = agentConnector.connect(address, port);
		available = true;
		getAgentVersion();
		
		LOGGER.debug("Server <{}> is connected now", this.getServerKey());
	}
	
	/**
	 * Terminates connection to the administration server
	 *
	 * @throws AgentAdminException in the case of errors.
	 */
	public void disconnectFromAgent() {
		if (!isConnected()) {
			LOGGER.info("Server <{}> connection is not established", this.getServerKey());
			return;
		}
		
		if (useLocalRas && localRASProcess.isAlive()) {
			localRASProcess.destroy();
		}
		
		try {
			agentConnector.shutdown();
			LOGGER.info("Server <{}> disconnected now", this.getServerKey());
		} catch (Exception excp) {
			LOGGER.info("Server <{}> disconnect error: <{}>", this.getServerKey(), excp.getLocalizedMessage());
		} finally {
			agentConnection = null;
			agentConnector = null;
		}
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
		
		return isConnected;
	}
	
	/**
	 * Authethicates a central server administrator agent.
	 * Need call of regCluster, getAgentAdmins, regAgentAdmin, unregAgentAdmin
	 * 
	 * @return {@code true} if authenticated, {@code false} overwise
	 */
	public boolean authenticateAgent() {
		if (agentConnection == null)
			throw new IllegalStateException("The connection is not established.");
		
		IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
			
			LOGGER.debug("Try to autenticate the agent server <{}>", this.getServerKey());
			this.agentConnection.authenticateAgent(userName, password);
			LOGGER.debug("Authentication to the agent server <{}> was successful", this.getServerKey());
			
			// сохраняем новые user/pass после успешной авторизации
			if (saveNewUserpass) {
				this.agentUserName = userName;
				this.agentPassword = password;
				LOGGER.debug("New credentials for the agent server <{}> are saved", this.getServerKey());
			}
			
		};
		String authDescription = "Authentication of the Central server administrator Agent";
		
		return runAuthProcessWithRequestToUser(authDescription, agentUserName, agentPassword, authMethod);
	}
	

	/**
	 * Проверяет не истекла ли авторизация на кластере
	 * и если истекла запускает процесс авторизации.
	 *
	 * @param clusterId cluster ID
	 * @return boolean истекла/не истекла
	 */
	private boolean checkAutenticateCluster(UUID clusterId) {
		
		var needAuthenticateCluster = false;
		try {
			LOGGER.debug("Gets the list of cluster <{}> administrators", clusterId);
			agentConnection.getClusterAdmins(clusterId);
			return true;
		} catch (Exception excp) {
			LOGGER.error("Error get the list of of cluster administrators <{}>", excp.getLocalizedMessage());
			if (excp.getLocalizedMessage().contains("Недостаточно прав пользователя на управление кластером") ||
					excp.getLocalizedMessage().contains("Администратор кластера не аутентифицирован")) // TODO учесть английский вариант
				needAuthenticateCluster = true;
		}
		
		if (needAuthenticateCluster)
			return authenticateCluster(clusterId);
		
		return false;
	}
	
	/**
	 * Authethicates a server cluster administrator
	 * 
	 * @param clusterId cluster ID
	 * @param userName  cluster administrator name
	 * @param password  cluster administrator password
	 */
	public boolean authenticateCluster(UUID clusterId) {
		if (agentConnection == null)
			throw new IllegalStateException("The connection is not established.");
		
		IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
			
			String clusterName = getClusterInfo(clusterId).getName();
			
			LOGGER.debug("Try to autenticate to the cluster <{}> of server <{}>", clusterName, this.getServerKey());
			agentConnection.authenticate(clusterId, userName, password);
			LOGGER.debug("Authentication to the cluster <{}> of server <{}> was successful", clusterName,
					this.getServerKey());
			
			// сохраняем новые user/pass после успешной авторизации
			if (this.saveCredentials && saveNewUserpass) { // ||
				this.credentialsClustersCashe.put(clusterId, new String[] { userName, password, clusterName });
				LOGGER.debug("New credentials for the cluster <{}> of server <{}> are saved", clusterName,
						this.getServerKey());
			}
			
		};
		
		String[] userAndPassword = credentialsClustersCashe.getOrDefault(clusterId, new String[] { "", "" });
		String authDescription = "Authentication of the server cluster administrator";
		
		return runAuthProcessWithRequestToUser(authDescription, userAndPassword[0], userAndPassword[1], authMethod);
		
	}
	
	private boolean runAuthProcessWithRequestToUser(String authDescription, String userName, String password,
			IRunAuthenticate authMethod) {
		try {
			// Сперва пытаемся авторизоваться под сохраненной учеткой (она может быть
			// инициализирована пустыми строками)
			authMethod.performAutenticate(userName, password, false);
			
		} catch (Exception excp) {
			LOGGER.debug("Autenticate to server <{}> error: <{}>", this.getServerKey(), excp.getLocalizedMessage());
			
			AuthenticateDialog authenticateDialog;
			String authExcpMessage = excp.getLocalizedMessage();
			int dialogResult;
			
			while (true) { // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена
				
				try {
					LOGGER.debug("Requesting new user credentials for the server <{}>", this.getServerKey());
					authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName,
							authDescription, authExcpMessage);
					dialogResult = authenticateDialog.open();
				} catch (Exception exc) {
					LOGGER.debug("Request new user credentials for the server <{}> failed", this.getServerKey());
					MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
					messageBox.setMessage(exc.getLocalizedMessage());
					messageBox.open();
					return false;
				}
				
				if (dialogResult == 0) {
					LOGGER.debug("The user has provided new credentials for the server <{}>", this.getServerKey());
					userName = authenticateDialog.getUsername();
					password = authenticateDialog.getPassword();
					try {
						authMethod.performAutenticate(userName, password, true);
						break;
					} catch (Exception exc) {
						LOGGER.debug("Autenticate to server <{}> error: <{}>", this.getServerKey(),
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
		if (agentConnection == null)
			throw new IllegalStateException("The connection is not established.");
		
//		String clusterName = getClusterInfo(clusterId).getName();
		LOGGER.debug("Add new infobase credentials for the cluster <{}> of server <{}>", clusterId,
				this.getServerKey());
		
		agentConnection.addAuthentication(clusterId, userName, password);
		
	}
	
	/**
	 * Gets the list of cluster descriptions registered on the central server
	 *
	 * @return list of cluster descriptions
	 */
	public List<IClusterInfo> getClusters() {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		LOGGER.debug("Get the list of cluster descriptions registered on the central server <{}>", this.getServerKey());
		List<IClusterInfo> clusters = agentConnection.getClusters();
		
		// кеширование имен кластеров для окна настроек
//		clustersNameCashe.clear();
		clusters.forEach(cluster -> {
//			clustersNameCashe.put(cluster.getClusterId(), cluster.getName());
			
			LOGGER.debug("\tCluster: name=<{}>, ID=<{}>, host:port=<{}:{}>",
					cluster.getName(), cluster.getClusterId(), cluster.getHostName(), cluster.getMainPort());
			
			// обновление имени кластера в кеше credentials
			String[] credentialClustersCashe = credentialsClustersCashe.get(cluster.getClusterId());
			if (credentialClustersCashe != null)
				credentialClustersCashe[2] = cluster.getName();
		});
		
		return clusters;
	}
	
	/**
	 * Gets the cluster descriptions
	 *
	 * @param clusterId cluster ID
	 * @return cluster descriptions
	 */
	public IClusterInfo getClusterInfo(UUID clusterId) {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		LOGGER.debug("Get the cluster <{}> descriptions", clusterId);
		return agentConnection.getClusterInfo(clusterId);
	}
	
	/**
	 * Creates a cluster or changes the state of an existing one.
	 * Central server authentication is required
	 *
	 * @return cluster descriptions
	 */
	public UUID regCluster(IClusterInfo clusterInfo) {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		LOGGER.debug("Register new description of cluster <{}>", clusterInfo.getName());
		return agentConnection.regCluster(clusterInfo);
	}
    
    /**
     * Deletes a cluster.
     * Cluster authentication is required
     *
	 * @param clusterId cluster ID
	 * 
	 */
	public boolean unregCluster(UUID clusterId) {
		var unregSuccesful = false;
		String unregMessage = null;
		
		if (!isConnected())
			unregMessage = "The connection a cluster is not established.";
		
		if (!checkAutenticateCluster(clusterId))
			unregMessage = "The cluster autentication error.";
		
		try {
			LOGGER.debug("Delete a cluster <{}>", clusterId);
			agentConnection.unregCluster(clusterId);
			unregSuccesful = true;
		} catch (Exception excp) {
			LOGGER.error("Error delete a cluster", excp);
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
	 * Gets the list of short descriptions of infobases registered in the cluster
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @return list of short descriptions of cluster infobases
	 */
	public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterId) {
		LOGGER.debug("Get the list of short descriptions of infobases registered in the cluster <{}>", clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return new ArrayList<>();
		}
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IInfoBaseInfoShort> clusterInfoBases;
		try {
			clusterInfoBases = agentConnection.getInfoBasesShort(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of short descriptions of infobases", excp);
			throw new IllegalStateException("Error get infobases short info");
		}
		
		clusterInfoBases.forEach(ib -> LOGGER.debug("\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr()));
		
		LOGGER.debug("Get the list of short descriptions of infobases succesful");
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
		LOGGER.debug("Get the list of descriptions of infobases registered in the cluster <{}>", clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return new ArrayList<>();
		}
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IInfoBaseInfo> clusterInfoBases;
		try {
			clusterInfoBases = agentConnection.getInfoBases(clusterId); // TODO For each infobase in the cluster, infobase authentication is required
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of infobases", excp);
			throw new IllegalStateException("Error get infobases info");
		}
		
		clusterInfoBases.forEach(ib -> {
			LOGGER.debug("\tInfobase: name=<{}>, desc=<{}>", ib.getName(), ib.getDescr());
		});
		
		LOGGER.debug("Get the list of descriptions of infobases succesful");
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
		LOGGER.debug("Get the short description for infobase <{}> of the cluster <{}>", infobaseId, clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return null;
		}
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		IInfoBaseInfoShort info;
		try {
			info = agentConnection.getInfoBaseShortInfo(clusterId, infobaseId);
		} catch (Exception excp) {
			LOGGER.error("Error get the short info for infobase", excp);
			return null;
		}
		
		LOGGER.debug("Get the short description for infobase <{}> succesful", info.getName());
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
		LOGGER.debug("Get the description for infobase <{}> of the cluster <{}>", infobaseId, clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return null;
		}
		
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
			
			while (true) { // пока не подойдет пароль, или пользователь не нажмет Отмена
				
				var userName = "";
				var authDescription = "Authentication of the infobase";
				try {
					LOGGER.debug("Requesting new user credentials for the infobase <{}>", infobaseId);
					authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName,
							authDescription, authExcpMessage);
					dialogResult = authenticateDialog.open();
				} catch (Exception exc) {
					LOGGER.debug("Request new user credentials for the infobase failed: <{}>", exc.getLocalizedMessage());
					var messageBox = new MessageBox(Display.getDefault().getActiveShell());
					messageBox.setMessage(exc.getLocalizedMessage());
					messageBox.open();
					return null;
				}
				
				if (dialogResult == 0) {
					LOGGER.debug("The user has provided new credentials for the infobase <{}>", infobaseId);
					userName = authenticateDialog.getUsername();
					String password = authenticateDialog.getPassword();
					try {
						addInfobaseCredentials(clusterId, userName, password);
						infobaseInfo = agentConnection.getInfoBaseInfo(clusterId, infobaseId);
						break;
					} catch (Exception exc) {
						authExcpMessage = exc.getLocalizedMessage();
						LOGGER.debug("Autenticate to infobase <{}> error: <{}>", this.getServerKey(), authExcpMessage);
						continue;
					}
				} else {
					LOGGER.debug("Autenticate to infobase <{}> abort by the user", this.getServerKey());
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
		LOGGER.debug("Get the name for infobase <{}> of the cluster <{}>", infobaseId, clusterId);
		
		if (infobaseId.equals(emptyUuid)) {
			LOGGER.debug("Infobase ID is empty");
			return "";
		}
		
		IInfoBaseInfoShort infobaseShortInfo = getInfoBaseShortInfo(clusterId, infobaseId);
		return infobaseShortInfo == null ? "" : infobaseShortInfo.getName();
		
	}
	
	/**
	 * Creates an infobase in a cluster.
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
	 */
	public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode) {
		LOGGER.debug("Creates an infobase <{}> in a cluster <{}>", info.getName(), clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return emptyUuid;
		}
		
		if (!checkAutenticateCluster(clusterId))
			return emptyUuid;
		
		UUID uuid;
		try {
			uuid = agentConnection.createInfoBase(clusterId, info, infobaseCreationMode);
		} catch (Exception excp) {
			LOGGER.error("Error creates an infobase", excp);
			return emptyUuid;
		}
		
		LOGGER.debug("Creates an infobase succesful");
		return uuid;
	}
	
	/**
	 * Changes short infobase description.
	 * Infobase authentication is required
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
	 */
	public void updateInfoBaseShort(UUID clusterId, IInfoBaseInfoShort info) {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		agentConnection.updateInfoBaseShort(clusterId, info);
	}
	
	/**
	 * Changes infobase parameters.
	 * <ul>
	 * Infobase authentication is required
	 *
	 * @param clusterId cluster ID
	 * @param info      infobase parameters
	 */
	public void updateInfoBase(UUID clusterId, IInfoBaseInfo info) {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		agentConnection.addAuthentication(clusterId, "", "");
		
		agentConnection.updateInfoBase(clusterId, info);
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
	public void dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode) {
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
		agentConnection.dropInfoBase(clusterId, infobaseId, dropMode);
	}
	
	/**
	 * Gets the list of cluster session descriptions.
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @return List of session descriptions
	 */
	public List<ISessionInfo> getSessions(UUID clusterId) {
		LOGGER.debug("Gets the list of cluster session descriptions in the cluster <{}>", clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return new ArrayList<>();
		}
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<ISessionInfo> sessions;
		try {
			sessions = agentConnection.getSessions(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of cluster session descriptions", excp);
			return new ArrayList<>();
//			throw new IllegalStateException("Error get list of cluster session descriptions");
		}
		sessions.forEach(s -> {
			LOGGER.debug("\tSession: application name=<{}>, session ID=<{}>",
					getApplicationName(s.getAppId()), s.getSessionId());
		});
		
		LOGGER.debug("Get the list of cluster session descriptions succesful");
		return sessions;
		
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
		LOGGER.debug("Gets the list of infobase <{}> session descriptions in the cluster <{}>", infobaseId, clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return new ArrayList<>();
		}
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();

		
		List<ISessionInfo> sessions;
		try {
			sessions = agentConnection.getInfoBaseSessions(clusterId, infobaseId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of infobase session descriptions", excp);
			return new ArrayList<>();
//			throw new IllegalStateException("Error get list of cluster session descriptions");
		}
		sessions.forEach(s -> {
			LOGGER.debug("\tSession: application name=<{}>, session ID=<{}>",
					getApplicationName(s.getAppId()), s.getSessionId());
		});
		
		LOGGER.debug("Get the list of cluster session descriptions succesful");
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
	 *
	 * @param clusterId cluster ID
	 * @param sessionId infobase ID
	 * @param message   error message for user
	 */
	public void terminateSession(UUID clusterId, UUID sessionId) {
		terminateSession(clusterId, sessionId, "Your session was interrupted by the administrator");
	}
	
	/**
	 * Terminates a session in the cluster.
	 * Cluster authentication is required
	 *
	 * @param clusterId cluster ID
	 * @param sessionId infobase ID
	 * @param message   error message for user
	 */
	public void terminateSession(UUID clusterId, UUID sessionId, String message) {
		LOGGER.debug("Terminates a session <{}> in the cluster <{}>", sessionId, clusterId);
		if (!isConnected()) {
			LOGGER.debug("The connection a cluster <{}> is not established", clusterId);
			return;
		}
		
		if (!checkAutenticateCluster(clusterId))
			return;
		
		try {
			agentConnection.terminateSession(clusterId, sessionId, message);
		} catch (Exception excp) {
			LOGGER.error("Error terminate a session", excp);
		}
	}
	
	/**
	 * Terminates all sessions for all infobases in the cluster
	 *
	 * @param clusterId cluster ID
	 */
	public void terminateAllSessions(UUID clusterId) {
		
//		List<ISessionInfo> sessions = agentConnection.getSessions(clusterId);
//		for (ISessionInfo session : sessions) {
////			agentConnection.terminateSession(clusterId, session.getSid());
//			terminateSession(clusterId, session.getSid());
//		}
		
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
	
	public List<IInfoBaseConnectionShort> getConnectionsShort(UUID clusterId) {
		if (isConnected())
			return agentConnection.getConnectionsShort(clusterId);
		
		return new ArrayList<>();
	}
	
	public List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(UUID clusterId, UUID infobaseId) {
		if (isConnected())
			return agentConnection.getInfoBaseConnectionsShort(clusterId, infobaseId);
		
		return new ArrayList<>();
	}
	
	public List<IInfoBaseConnectionInfo> getInfoBaseConnections(UUID clusterId, UUID processId, UUID infobaseId) {
		if (isConnected())
			return agentConnection.getInfoBaseConnections(clusterId, processId, infobaseId);
		
		return new ArrayList<>();
	}
	
	public void disconnectConnection(UUID clusterId, UUID processId, UUID connectionId) {
		if (isConnected())
			agentConnection.disconnect(clusterId, processId, connectionId);
		
	}
	
	public List<IInfoBaseConnectionShort> getWorkingProcessConnectionsShort(UUID clusterId, UUID workingProcessId) {
		if (isConnected()) {
//			List<IInfoBaseConnectionShort> clusterConnections = agentConnection.getConnectionsShort(clusterId);
////			List<IInfoBaseConnectionShort> wpConnections = new ArrayList<>();
////			clusterConnections.forEach(connection -> {
////				if (connection.getWorkingProcessId().equals(wpID))
////					wpConnections.add(connection);
////			});
////			return wpConnections;
			
			return agentConnection.getConnectionsShort(clusterId)
					.stream()
					.filter(c -> c.getWorkingProcessId().equals(workingProcessId))
					.collect(Collectors.toList());
			
		}
		
		return new ArrayList<>();
	}
	
	public List<IObjectLockInfo> getLocks(UUID clusterId) {
		if (isConnected())
			return agentConnection.getLocks(clusterId);
		
		return new ArrayList<>();
	}
	
	public List<IObjectLockInfo> getInfoBaseLocks(UUID clusterId, UUID infobaseId) {
		if (isConnected())
			return agentConnection.getInfoBaseLocks(clusterId, infobaseId);
		
		return new ArrayList<>();
	}
	
	public List<IObjectLockInfo> getConnectionLocks(UUID clusterId, UUID connectionId) {
		if (isConnected())
			return agentConnection.getConnectionLocks(clusterId, connectionId);
		
		return new ArrayList<>();
	}
	
	public List<IObjectLockInfo> getSessionLocks(UUID clusterId, UUID infobaseId, UUID sid) {
		if (isConnected())
			return agentConnection.getSessionLocks(clusterId, infobaseId, sid);
		
		return new ArrayList<>();
	}
	
	public List<IWorkingProcessInfo> getWorkingProcesses(UUID clusterId) {
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IWorkingProcessInfo> workingProcesses;
		try {
			LOGGER.debug("Gets the list of descriptions of working processes registered in the cluster <{}>", clusterId);
			workingProcesses = agentConnection.getWorkingProcesses(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of short descriptions of working processes", excp);
			throw new IllegalStateException("Error get working processes");
		}
		workingProcesses.forEach(wp -> {
			LOGGER.debug("\tWorking process: host name=<{}>, main port=<{}>", wp.getHostName(), wp.getMainPort());
		});
		
		LOGGER.debug("Get the list of short descriptions of working processes succesful");
		return workingProcesses;
	}
	
	public IWorkingProcessInfo getWorkingProcessInfo(UUID clusterId, UUID processId) {
		
		if (agentConnection == null) {
			throw new IllegalStateException("The connection is not established.");
		}
		
//		if (isConnected())
		return agentConnection.getWorkingProcessInfo(clusterId, processId);
	}
	
	public List<IWorkingProcessInfo> getServerWorkingProcesses(UUID clusterId, UUID serverId) {
		if (isConnected())
			return agentConnection.getServerWorkingProcesses(clusterId, serverId);
		
		return new ArrayList<>();
	}
	
	public List<IWorkingServerInfo> getWorkingServers(UUID clusterId) {
		if (!isConnected())
			return new ArrayList<>();
		
		if (!checkAutenticateCluster(clusterId))
			return new ArrayList<>();
		
		List<IWorkingServerInfo> workingServers;
		try {
			LOGGER.debug("Gets the list of descriptions of working servers registered in the cluster <{}>", clusterId);
			workingServers = agentConnection.getWorkingServers(clusterId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of working servers", excp);
			throw new IllegalStateException("Error get working servers");
		}
		workingServers.forEach(ws -> {
			LOGGER.debug("\tWorking server: host name=<{}>, main port=<{}>", ws.getHostName(), ws.getMainPort());
		});
		
		LOGGER.debug("Get the list of descriptions of working servers succesful");
		return workingServers;
	}
	
	public IWorkingServerInfo getWorkingServerInfo(UUID clusterId, UUID serverId) {
		if (!isConnected())
			return null;
		
		if (!checkAutenticateCluster(clusterId))
			return null;
		
		IWorkingServerInfo workingServerInfo;
		try {
			LOGGER.debug("Gets the description of working server <{}> registered in the cluster <{}>", serverId, clusterId);
			workingServerInfo = agentConnection.getWorkingServerInfo(clusterId, serverId);
		} catch (Exception excp) {
			LOGGER.error("Error get the list of descriptions of working server", excp);
			throw new IllegalStateException("Error get working server");
		}
			
		LOGGER.debug("\tWorking server: host name=<{}>, main port=<{}>", workingServerInfo.getHostName(), workingServerInfo.getMainPort());
		
		LOGGER.debug("Get the list of short descriptions of working processes succesful");
		return workingServerInfo;
	}
	
}
