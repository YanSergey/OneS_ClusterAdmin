package ru.yanygin.clusterAdminLibrary;

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
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com._1c.v8.ibis.admin.client.IAgentAdminConnector;
import com._1c.v8.ibis.admin.client.IAgentAdminConnectorFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.yanygin.clusterAdminLibraryUI.AuthenticateDialog;

public class Config {
	@SerializedName("Servers")
	@Expose
	public Map<String, Server> servers = new HashMap<>(); // Надо определиться что должно являться ключем, агент (Server:1540) или менеджер (Server:1541)

	private static Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary");
	
	public Server CreateNewServer() {
		return new Server("newServerAddress:1541");
	}
	
	public List<String> addNewServers(List<String> servers) {
		// Пакетное добавление серверов в список, предполагается для механизма импорта из списка информационных баз

		List<String> addedServers = new ArrayList<>();

		// Имя сервера, которое приходит сюда не равно Представлению сервера, выводимому в списке
		// Имя сервера. оно же Key в map и json, строка вида Server:1541, с обязательным указанием порта менеджера, к которому подключаемся
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
	
	public void connectAllServers() {
		servers.forEach((server, config) -> {
			config.connectAndAuthenticate(false);
		});
	}
	
	public void checkConnectionAllServers() {
		servers.forEach((server, config) -> {
			config.connectAndAuthenticate(true);
		});
	}

	interface IRunAuthenticate {
		void performAutenticate(String userName, String password, boolean saveNewUserpass);
	}

	interface IGetInfobaseInfo {
		IInfoBaseInfo getInfo(String userName, String password);
	}

	public class Server {
		
		@SerializedName("ManagerHost")
		@Expose
		public String managerHost;
		
		@SerializedName("AgentPort")
		@Expose
		public int agentPort;
		
		@SerializedName("ManagerPort")
		@Expose
		public int managerPort;
		
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
		
		@SerializedName("AgentUser")
		@Expose
		public String agentUserName;
		
		@SerializedName("AgentPassword")
		@Expose
		public String agentPassword;
		
		@SerializedName("Autoconnect")
		@Expose
		public boolean autoconnect;
		
		public boolean available;
		
		public String connectionError;	

//		public String agentVersion;
		
		public String getAgentVersion() {
			return isConnected() ? agentConnection.getAgentVersion() : "";
		}
		
//		public ClusterConnector clusterConnector;
		
//		private final IAgentAdminConnectorFactory factory;
		private IAgentAdminConnector agentConnector;
		private IAgentAdminConnection agentConnection;

		
//		public Map<UUID, String> clustersNameCashe; // делать кеш информации кластеров или нет?
		public Map<UUID, List<IInfoBaseInfoShort>> clustersInfoBasesCashe;
		
//		public Map<UUID, Pair<String, String>> credentialsClustersCashe;
//		public Map<UUID, Pair<String, String>> credentialsInfobasesCashe;
		@SerializedName("ClustersCredentials")
		@Expose
		public Map<UUID, String[]> credentialsClustersCashe;
		public Map<UUID, String[]> credentialsInfobasesCashe;
		
		private final static String THIN_CLIENT = "1CV8C";
		private final static String THICK_CLIENT = "1CV8";
		private final static String DESIGNER = "Designer";
		private final static String SERVER_CONSOLE = "SrvrConsole";
		private final static String RAS_CONSOLE = "RAS";
		private final static String JOBSCHEDULER = "JobScheduler";
		
		public String getApplicationName(String appID) {
			switch (appID) {
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
				return String.format("Unknown client (%s)", appID);
			}
		}

		UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

		public Server(String serverName) {
//			this.managerHost = calcHostName(serverName);
//			this.managerPort = calcManagerPort(serverName);
//			this.rasPort = calcRemoteRASPort(serverName);
			calcServerParams(serverName);
			
			this.useLocalRas = false;
			this.localRasPort = 0;
			this.localRasV8version = "";
			this.autoconnect = false;
			this.available = false;
			this.agentUserName = "";
			this.agentPassword = "";
			
			init();

		}

		public void init() {
			
			this.agentUserName = "";
			this.agentPassword = "";
			
			this.clustersInfoBasesCashe = new HashMap<>();
			if (this.credentialsClustersCashe == null)
				this.credentialsClustersCashe = new HashMap<>();
			
			LOGGER.info("Server {} init", this.getServerKey());

		}
		
		// Надо определиться что должно являться ключем, агент (Server:1540) или менеджер (Server:1541)
		// Попробовать выпилить отовсюду упоминания менеджера, он нам нужен только для автоопределения, где находится агент
		public String getServerKey() {
			return managerHost.concat(":").concat(Integer.toString(agentPort));
		}

		public String getServerDescription() {
			String rasPort = "";
			if (useLocalRas) {
				rasPort = "(*".concat(Integer.toString(localRasPort)).concat(")");
			}
			else {
				rasPort = Integer.toString(this.rasPort);
			}
			
//			return managerHost.concat(":")
//					.concat(Integer.toString(agentPort))
//					.concat("-")
//					.concat(rasPort);
			
			var descriptionFormat = isConnected() ? "%s:%s-%s (%s)" : "%s:%s-%s";
			
			return String.format(descriptionFormat, managerHost, Integer.toString(agentPort), rasPort, getAgentVersion());
			
		}

		public String getManagerPortAsString() {
			return Integer.toString(managerPort);
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
		
		public void setServerNewProperties(String managerHost,
											int managerPort,
											int agentPort,
											String rasHost,
											int rasPort,
											boolean useLocalRas,
											int localRasPort,
											String localRasV8version,
											boolean autoconnect,
											String agentUser,
											String agentPassword,
											Map<UUID, String[]> credentialsClustersCashe) {
			
			this.managerHost 	= managerHost;
			this.managerPort 	= managerPort;
			this.agentPort 		= agentPort;
			this.rasHost		= rasHost;
			this.rasPort 		= rasPort;
			this.useLocalRas 	= useLocalRas;
			this.localRasPort 	= localRasPort;
			this.localRasV8version = localRasV8version;
			this.autoconnect 	= autoconnect;
			this.agentUserName 	= agentUser;
			this.agentPassword 	= agentPassword;
			this.credentialsClustersCashe 	= credentialsClustersCashe;
			
			if (this.autoconnect)
				connectAndAuthenticate(false);
			
			LOGGER.info("Server {} set new properties", this.getServerKey());
		}
		
		
		/** Вычисляет имя хоста, на котором запущены процессы кластера
		 * @param serverAddress - Имя инф.базы из списка баз. Может содержать номер порта менеджера кластера (по-умолчанию 1541).
		 *  Примеры: Server1c, Server1c:2541
		 * @return Имя хоста, на котором запущены процессы кластера
		 */
		private String calcHostName(String serverAddress) {
			String serverName;
			String[] ar = serverAddress.split(":");
			if (ar.length > 0) {
				serverName = ar[0];
			} else {
				serverName = "localhost";
			}
			
			return serverName;
		}
		
		private int calcManagerPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1541;
			} else {
				port = Integer.parseInt(ar[1]);
			}
			return port;
		}
		
		private int calcRemoteRASPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1545;
			} else {
				port = Integer.parseInt(ar[1].substring(0, ar[1].length()-1).concat("5"));
			}
			return port;
		}
		
		private void calcServerParams(String serverAddress) {
			
			String managerHost;
			String rasHost;
			int managerPort;
			int agentPort;
			int rasPort;
			
			serverAddress = serverAddress.strip();
			if (serverAddress.isBlank())
				serverAddress = "localhost";
			
			String[] ar = serverAddress.split(":");
			managerHost	= ar[0];
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
			
			this.managerHost 	= managerHost;
			this.rasHost 		= rasHost;
			this.managerPort 	= managerPort;
			this.agentPort 		= agentPort;
			this.rasPort 		= rasPort;
			
			LOGGER.info("Server {} calc params", this.getServerKey());
			
		}
		
		public boolean connectAndAuthenticate(boolean disconnectAfter) {
			LOGGER.debug("Server {} start connection", this.getServerKey());
			
			if (isConnected()) {
				return true;
			}
			
			String rasHost 	= useLocalRas ? "localhost" : this.rasHost;
			int rasPort 	= useLocalRas ? localRasPort : this.rasPort;

			try {
				connectToAgent(rasHost, rasPort, 20);
				
				if (disconnectAfter) {
					disconnectFromAgent();	
				}
				
				
//				//auth agent
//				authenticateAgent();
//				clusters = getClusters(); // а надо ли мне здесь получать кластера???
//				//auth clusters
//				clusters.forEach(clusterInfo -> {
//					try {
//						authenticateCluster(clusterInfo.getClusterId(), "", "");
//					} catch (Exception e) {
//						String clusterUser = "CAdmin";
//						String clusterPasswors = "123";
//						authenticateCluster(clusterInfo.getClusterId(), clusterUser, clusterPasswors);
//					}
//
//				});
				
			}
			catch (Exception excp) {
				available = false;
				LOGGER.info("Server {} connection error: {}", this.getServerKey(), excp.getLocalizedMessage());
				return false;
			}
			return true;

		}
		
	    /**
		 * Establishes connection with the administration server of 1C:Enterprise 
	     * server cluster
		 *
	     * @param address server address
	     * @param port IP port
	     * @param timeout connection timeout (in milliseconds)
	     *
	     * @throws AgentAdminException in the case of errors.
		 */
		public void connectToAgent(String address, int port, long timeout) {
			if (agentConnection != null) {
				LOGGER.debug("The connection to server {} is already established", this.getServerKey());
				available = true;
				return;
			}

			LOGGER.debug("Try connect server {} to {}:{}", new String[] { this.getServerKey(), address, Integer.toString(port)});

			IAgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
			agentConnector = factory.createConnector(timeout);
			agentConnection = agentConnector.connect(address, port);
			available = true;
			
			LOGGER.debug("Server {} is connected now", this.getServerKey());
		}
		
		/**
		 * Terminates connection to the administration server
	     *
	     * @throws AgentAdminException in the case of errors.
		 */
		public void disconnectFromAgent() {
	        if (!isConnected()) {
				LOGGER.info("Server {} connection is not established", this.getServerKey());
				return;
	        }

	        try {
	        	agentConnector.shutdown();
				LOGGER.info("Server {} disconnected now", this.getServerKey());
	        }
			catch (Exception excp) {
				LOGGER.info("Server {} disconnect error: {}", this.getServerKey(), excp.getLocalizedMessage());
			}
	        finally {
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
//			if (isConnected)
//				LOGGER.debug("Server {} is already connected", this.getServerKey()); // засоряет лог
			
			return isConnected;
		}
		
		/**
		 * Authethicates a central server administrator agent
		 * Need call of regCluster, getAgentAdmins, regAgentAdmin, unregAgentAdmin
		 * 
		 * @return {@code true} if authenticated, {@code false} overwise
		 */
		public boolean authenticateAgent() {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

			IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
				
				LOGGER.debug("Try to autenticate the agent server {}", this.getServerKey());
				this.agentConnection.authenticateAgent(userName, password);
				LOGGER.debug("Authentication to the agent server {} was successful", this.getServerKey());
				
				// сохраняем новые user/pass после успешной авторизации
				if (saveNewUserpass) {
					this.agentUserName = userName;
					this.agentPassword = password;
					LOGGER.debug("New credentials for the agent server {} are saved", this.getServerKey());
				}

			};
			String authDescription = "Authentication of the Central server administrator Agent";

			return runAuthProcessWithRequestToUser(authDescription, agentUserName, agentPassword, authMethod);
		}
		
		/**
		 * Authethicates a server cluster administrator
		 * 
		 * @param clusterID cluster ID
		 * @param userName cluster administrator name
		 * @param password cluster administrator password
		 */
		public boolean authenticateCluster(UUID clusterID) {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

			IRunAuthenticate authMethod = (String userName, String password, boolean saveNewUserpass) -> {
				
				String clusterName = getClusterInfo(clusterID).getName();
				
				LOGGER.debug("Try to autenticate to the cluster {} of server {}", clusterName, this.getServerKey());
				agentConnection.authenticate(clusterID, userName, password);
				LOGGER.debug("Authentication to the cluster {} of server {} was successful", clusterName, this.getServerKey());
				
				// сохраняем новые user/pass после успешной авторизации
				if (saveNewUserpass) { // && this.saveCredentialsInConfig
					this.credentialsClustersCashe.put(clusterID, new String[] {userName, password, clusterName});
					LOGGER.debug("New credentials for the cluster {} of server {} are saved", clusterName, this.getServerKey());
				}
				
			};
				
			String[] userAndPassword = credentialsClustersCashe.getOrDefault(clusterID, new String[] {"", ""});
			String authDescription = "Authentication of the server cluster administrator";
			
			return runAuthProcessWithRequestToUser(authDescription, userAndPassword[0], userAndPassword[1], authMethod);
			
		}
		
		private boolean runAuthProcessWithRequestToUser(String authDescription, String userName, String password, IRunAuthenticate authMethod) {
			try {
				// Сперва пытаемся авторизоваться под сохраненной учеткой (она может быть инициализирована пустыми строками)
				authMethod.performAutenticate(userName, password, false);
				
			} catch (Exception excp) {
				LOGGER.debug("Autenticate to server {} error: {}", this.getServerKey(), excp.getLocalizedMessage());

				AuthenticateDialog authenticateDialog;
				String authExcpMessage = excp.getLocalizedMessage();
				int dialogResult;

				while (true) { // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена

					try {
						LOGGER.debug("Requesting new user credentials for the server {}", this.getServerKey());
						authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName, authDescription, authExcpMessage);
						dialogResult = authenticateDialog.open();
					} catch (Exception exc) {
						LOGGER.debug("Request new user credentials for the server {} failed", this.getServerKey());
						MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
						messageBox.setMessage(exc.getLocalizedMessage());
						messageBox.open();
						return false;
					}

					if (dialogResult == 0) {
						LOGGER.debug("The user has provided new credentials for the server {}", this.getServerKey());
						userName = authenticateDialog.getUsername();
						password = authenticateDialog.getPassword();
						try {
							authMethod.performAutenticate(userName, password, true);
							break;
						} catch (Exception exc) {
							LOGGER.debug("Autenticate to server {} error: {}", this.getServerKey(), excp.getLocalizedMessage());
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
	     * Adds infobase authentication parameters to the context 
	     * of the current administration server connection
		 * 
		 * @param clusterID cluster ID
		 * @param userName infobase administrator name
		 * @param password infobase administrator password
		 */
		public void addInfoBaseCredentials(UUID clusterID, String userName, String password) {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

			
//			IRunAuthenticate authMethod = (String ibUserName, String ibPssword, boolean saveNewUserpass) -> {
//				
//				String clusterName = getClusterInfo(clusterID).getName();
//				
//				LOGGER.debug("Add new infobase credentials for the cluster {} of server {}", clusterName, this.getServerKey());
//				agentConnection.addAuthentication(clusterID, userName, password);
//								
//			};
//				
//			String[] userAndPassword = credentialsClustersCashe.getOrDefault(clusterID, new String[] {"", ""});
//			String authDescription = "Authethicates a server cluster administrator";
//			
//			return runAuthProcessWithRequestToUser(authDescription, userAndPassword[0], userAndPassword[1], authMethod);
			
			
			String clusterName = getClusterInfo(clusterID).getName();
			LOGGER.debug("Add new infobase credentials for the cluster {} of server {}", clusterName, this.getServerKey());

			agentConnection.addAuthentication(clusterID, userName, password);
			
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

			LOGGER.debug("Get the list of cluster descriptions registered on the central server {}", this.getServerKey());
			List<IClusterInfo> clusters = agentConnection.getClusters();
			
			// кеширование имен кластеров для окна настроек
//			clustersNameCashe.clear();
			clusters.forEach(cluster ->{
//				clustersNameCashe.put(cluster.getClusterId(), cluster.getName());
				
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
		 * @param clusterID cluster ID
		 * @return cluster descriptions
		 */
		public IClusterInfo getClusterInfo(UUID clusterID) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

			LOGGER.debug("Get the cluster {} descriptions", clusterID);
			return agentConnection.getClusterInfo(clusterID);
		}
	    
	    /**
	     * Creates a cluster or changes the state of an existing one
	     * 	Central server authentication is required
	     *
	     * @return cluster descriptions
	     */
		public UUID regCluster(IClusterInfo clusterInfo) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

			LOGGER.debug("Register new description of cluster {}", clusterInfo.getName());
			return agentConnection.regCluster(clusterInfo);
		}
	    
	    /**
	     * Deletes a cluster
	     * 	Cluster authentication is required
	     *
		 * @param clusterID cluster ID
	     * 
	     */
		public void unregCluster(UUID clusterID) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

			LOGGER.debug("Delete a cluster {}", clusterID);
			agentConnection.unregCluster(clusterID);
		}
	    
	    /**
	     * Gets the list of short descriptions of infobases registered in the cluster
	     * 	Cluster authentication is required
	     *
	     * @param clusterID cluster ID
	     * @return list of short descriptions of cluster infobases
	     */
		public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterID) {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

			if (!authenticateCluster(clusterID))
				return new ArrayList<>(); // или пустой список?

			List<IInfoBaseInfoShort> clusterInfoBases = agentConnection.getInfoBasesShort(clusterID);

			// кеширование списка инфобаз. Не дороже ли кеш, чем получать заново список?
			clustersInfoBasesCashe.put(clusterID, clusterInfoBases);

			LOGGER.debug("Get the list of short descriptions of infobases registered in the cluster {}", clusterID);
			return agentConnection.getInfoBasesShort(clusterID);
		}
	    
	    /**
	     * Gets the list of full descriptions of infobases registered in the cluster
	     * 	Cluster authentication is required
	     * 	For each infobase in the cluster, infobase authentication is required
	     * 	If infobase authentication is not performed, only fields that correspond to short infobase description fields will be filled
	     *
	     * @param clusterID cluster ID
	     * @return list of full descriptions of cluster infobases
	     */	    
		public List<IInfoBaseInfo> getInfoBases(UUID clusterID) {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

			if (!authenticateCluster(clusterID))
				return new ArrayList<>(); // или пустой список?

			LOGGER.debug("Get the list of descriptions of infobases registered in the cluster {}", clusterID);
			return agentConnection.getInfoBases(clusterID);
		}
		
	    /**
		 * Gets a short infobase description.
		 * 	Cluster authentication is required
		 *
		 * @param clusterID cluster ID
		 * @param infobaseID infobase ID
		 * @return infobase full infobase description
		 */	
		public IInfoBaseInfoShort getInfoBaseShortInfo(UUID clusterID, UUID infobaseID) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

			LOGGER.debug("Get the short description for infobase {} of the cluster {}", infobaseID, clusterID);
			return agentConnection.getInfoBaseShortInfo(clusterID, infobaseID);
		}
		
	    /**
		 * Gets the full infobase description.
		 * 	Cluster authentication is required.
		 * 	Infobase authentication is required.
		 *
		 * @param clusterID cluster ID
		 * @param infobaseID infobase ID
		 * @return infobase full infobase description
		 */
		public IInfoBaseInfo getInfoBaseInfo(UUID clusterID, UUID infobaseID) {
			if (agentConnection == null)
				throw new IllegalStateException("The connection is not established.");

//			addInfoBaseCredentials(clusterID, "", ""); // в добавлении пустых кредов нет необходимости
			
			LOGGER.debug("Get the description for infobase {} of the cluster {}", infobaseID, clusterID);
			IInfoBaseInfo infobaseInfo;
			
			try {
				infobaseInfo = agentConnection.getInfoBaseInfo(clusterID, infobaseID);
			} catch (Exception excp) {
//				IGetInfobaseInfo authMethod = (String ibUserName, String ibPssword) -> {
//					
//					String clusterName = getClusterInfo(clusterID).getName();
//					
//					LOGGER.debug("Add new infobase credentials for the cluster {} of server {}", clusterName, this.getServerKey());
//					agentConnection.addAuthentication(clusterID, ibUserName, ibPssword);
//					
//					return agentConnection.getInfoBaseInfo(clusterID, infobaseID);
//
//				};
//				String authDescription = "Authentication of the server cluster administrator";
//				
//				return runAuthProcessWithRequestToUser(authDescription, ibUserName, ibPssword, authMethod);

				AuthenticateDialog authenticateDialog;
				String authExcpMessage = excp.getLocalizedMessage();
				int dialogResult;

				while (true) { // крутимся, пока не подойдет пароль, или пользователь не нажмет Отмена

					String userName = "";
					String authDescription = "Authentication of the infobase";
					try {
						LOGGER.debug("Requesting new user credentials for the infobase <{}>", infobaseID);
						authenticateDialog = new AuthenticateDialog(Display.getDefault().getActiveShell(), userName, authDescription, authExcpMessage);
						dialogResult = authenticateDialog.open();
					} catch (Exception exc) {
						LOGGER.debug("Request new user credentials for the infobase <{}> failed", infobaseID);
						MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
						messageBox.setMessage(exc.getLocalizedMessage());
						messageBox.open();
						return null;
					}

					if (dialogResult == 0) {
						LOGGER.debug("The user has provided new credentials for the infobase <{}>", infobaseID);
						userName = authenticateDialog.getUsername();
						String password = authenticateDialog.getPassword();
						try {
							addInfoBaseCredentials(clusterID, userName, password);
							infobaseInfo = agentConnection.getInfoBaseInfo(clusterID, infobaseID);
							break;
						} catch (Exception exc) {
							LOGGER.debug("Autenticate to infobase <{}> error: {}", this.getServerKey(), excp.getLocalizedMessage());
							authExcpMessage = exc.getLocalizedMessage();
							continue;
						}
					} else {
						return null;
					}
				}
				
				
				
			}
			
			
			
			return infobaseInfo;
		}
	    
	    /**
		 * Gets the infobase name.
		 *
		 * @param clusterID cluster ID
		 * @param infobaseID infobase ID
		 * @return infobase full infobase description
		 */
	    public String getInfoBaseName(UUID clusterID, UUID infobaseID)
	    {
			LOGGER.debug("Get the name for infobase {} of the cluster {}", infobaseID, clusterID);
			
			if (infobaseID.equals(emptyUuid)) {
				LOGGER.debug("Infobase ID is empty");
				return "";
			}

			String infobaseName = "";
	    	
			// Сперва достаем из кеша
	    	List<IInfoBaseInfoShort> clusterInfoBases = clustersInfoBasesCashe.getOrDefault(clusterID, new ArrayList<>());
			for (IInfoBaseInfoShort infobase : clusterInfoBases) {
				if (infobase.getInfoBaseId().equals(infobaseID)) {
					infobaseName = infobase.getName();
					LOGGER.debug("Infobase name find in cashe");
					break;
				}
			}
			
			// В кеше не нашли, обновляем кеш списка инфобаз и снова ищем
			if (infobaseName.isBlank()) {
				LOGGER.debug("Infobase not found in cashe. Cashe update");
		    	clusterInfoBases = agentConnection.getInfoBasesShort(clusterID);
		    	clustersInfoBasesCashe.put(clusterID, clusterInfoBases);
				for (IInfoBaseInfoShort infobase : clusterInfoBases) {
					if (infobase.getInfoBaseId().equals(infobaseID)){
						infobaseName = infobase.getName();
						LOGGER.debug("Infobase name find in cashe");
						break;
					}
				}
			}
			
			return infobaseName;
	    }
	    
	    /**
	     * Creates an infobase in a cluster.
	     * 	Cluster authentication is required
	     *
	     * @param clusterId cluster ID
	     * @param info infobase parameters
	     */
	    public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode)
	    {
			if (agentConnection == null)
			{
				throw new IllegalStateException("The connection is not established.");
			}
			
			return agentConnection.createInfoBase(clusterId, info, infobaseCreationMode);
	    }
	    
	    /**
	     * Changes short infobase description.
	     * 	Infobase authentication is required
	     *
	     * @param clusterId cluster ID
	     * @param info infobase parameters
	     */
	    public void updateInfoBaseShort(UUID clusterId, IInfoBaseInfoShort info)
	    {
			if (agentConnection == null)
			{
				throw new IllegalStateException("The connection is not established.");
			}
			
			agentConnection.updateInfoBaseShort(clusterId, info);
	    }
	    	    
	    /**
	     * Changes infobase parameters.
	     * 	Infobase authentication is required
	     *
	     * @param clusterId cluster ID
	     * @param info infobase parameters
	     */
	    public void updateInfoBase(UUID clusterId, IInfoBaseInfo info)
	    {
			if (agentConnection == null)
			{
				throw new IllegalStateException("The connection is not established.");
			}
			
			agentConnection.addAuthentication(clusterId, "", "");
			
			agentConnection.updateInfoBase(clusterId, info);
	    }
	    
	    /**
	     * Deletes an infobase. 
	     * <ul>Infobase authentication is required
	     *
	     * @param clusterId cluster ID
	     * @param infobaseId infobase parameters
	     * @param dropMode - infobase drop mode:<ul>
	     * 0 - do not delete the database
	     * <p>1 - delete the database
	     * <p>2 - clear the database
	     */	    
	    public void dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode)
	    {
			if (agentConnection == null)
			{
				throw new IllegalStateException("The connection is not established.");
			}
			
			agentConnection.dropInfoBase(clusterId, infobaseId, dropMode);
	    }

		/**
		 * Gets the list of cluster session descriptions.
		 * 	Cluster authentication is required
		 *
		 * @param clusterId  cluster ID
		 * @return List of session descriptions
		 */
		public List<ISessionInfo> getSessions(UUID clusterId) {
//			if (agentConnection == null) {
//				throw new IllegalStateException("The connection is not established.");
//			}

			if (isConnected())
				return agentConnection.getSessions(clusterId);
			
			return new ArrayList<>();

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
//			if (agentConnection == null) {
//				throw new IllegalStateException("The connection is not established.");
//			}
			
			if (isConnected())
				return agentConnection.getInfoBaseSessions(clusterId, infobaseId);

			return new ArrayList<>();
		}
		
		public List<ISessionInfo> getWorkingProcessSessions(UUID clusterId, UUID wpID) {
			if (isConnected()) {
				List<ISessionInfo> clusterSessions = agentConnection.getSessions(clusterId);
				List<ISessionInfo> wpSessions = new ArrayList<>();
				clusterSessions.forEach(session -> {
					if (session.getWorkingProcessId().equals(wpID))
						wpSessions.add(session);
				});
				
				return wpSessions;
			}

			return new ArrayList<>();
		}

	    /**
	     * Terminates a session in the cluster.
	     * 	Cluster authentication is required
	     *
	     * @param clusterId cluster ID
	     * @param sessionId infobase ID
	     * @param message error message for user
	     */
		public void terminateSession(UUID clusterId, UUID sessionId, String message) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}
			
			agentConnection.terminateSession(clusterId, sessionId, message);
			
		}	    
	    
	    /**
	     * Terminates all sessions for all infobases in the cluster
	     *
	     * @param clusterId cluster ID
	     */
	    public void terminateAllSessions(UUID clusterId)
	    {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

	        List<ISessionInfo> sessions = agentConnection.getSessions(clusterId);
	        for (ISessionInfo session : sessions) {
	        	agentConnection.terminateSession(clusterId, session.getSid());
	        }
	    }
		
	    /**
	     * Terminates all sessions for infobase in the cluster
	     *
	     * @param clusterId cluster ID
	     * @param infobaseId infobase ID
	     */    
		public void terminateAllSessionsOfInfobase(UUID clusterId, UUID infobaseId, boolean onlyUsersSession) {
			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}

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
		
		public List<IInfoBaseConnectionShort> getWorkingProcessConnectionsShort(UUID clusterId, UUID wpID) {
			if (isConnected()) {
//				List<IInfoBaseConnectionShort> clusterConnections = agentConnection.getConnectionsShort(clusterId);
////				List<IInfoBaseConnectionShort> wpConnections = new ArrayList<>();
////				clusterConnections.forEach(connection -> {
////					if (connection.getWorkingProcessId().equals(wpID))
////						wpConnections.add(connection);
////				});
////				return wpConnections;
				
				return agentConnection.getConnectionsShort(clusterId)
						.stream()
						.filter(c -> c.getWorkingProcessId().equals(wpID))
						.collect(Collectors.toList());
				
			}

			return new ArrayList<>();
		}
		
		public List<IObjectLockInfo> getLocks(UUID clusterID) {
			if (isConnected())
				return agentConnection.getLocks(clusterID);

			return new ArrayList<>();
		}
		
		public List<IObjectLockInfo> getInfoBaseLocks(UUID clusterID, UUID infobaseId) {
			if (isConnected())
				return agentConnection.getInfoBaseLocks(clusterID, infobaseId);

			return new ArrayList<>();
		}
		
		public List<IObjectLockInfo> getConnectionLocks(UUID clusterID, UUID connectionId) {
			if (isConnected())
				return agentConnection.getConnectionLocks(clusterID, connectionId);

			return new ArrayList<>();
		}
		
		public List<IObjectLockInfo> getSessionLocks(UUID clusterID, UUID infobaseId, UUID sid) {
			if (isConnected())
				return agentConnection.getSessionLocks(clusterID, infobaseId, sid);

			return new ArrayList<>();
		}
		
		
		public List<IWorkingProcessInfo> getWorkingProcesses(UUID clusterID) {
			if (isConnected())
				return agentConnection.getWorkingProcesses(clusterID);

			return new ArrayList<>();
		}	
		
		
		public IWorkingProcessInfo getWorkingProcessInfo(UUID clusterID, UUID processID) {

			if (agentConnection == null) {
				throw new IllegalStateException("The connection is not established.");
			}
			
//			if (isConnected())
			return agentConnection.getWorkingProcessInfo(clusterID, processID);
		}	

		
		public List<IWorkingProcessInfo> getServerWorkingProcesses(UUID clusterID, UUID serverId) {
			if (isConnected())
				return agentConnection.getServerWorkingProcesses(clusterID, serverId);

			return new ArrayList<>();
		}	
		
		
	}
}


