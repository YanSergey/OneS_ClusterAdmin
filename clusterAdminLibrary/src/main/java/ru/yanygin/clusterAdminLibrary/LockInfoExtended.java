package ru.yanygin.clusterAdminLibrary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;

public class LockInfoExtended implements IInfoExtended {
	
	private static final String TITLE_DESCRIPTION = Messages.getString("InfoTables.Description"); //$NON-NLS-1$
	private static final String TITLE_INFOBASE = Messages.getString("SessionInfo.Infobase"); //$NON-NLS-1$
	private static final String TITLE_CONNECTION = Messages.getString("ConnectionInfo.Connection"); //$NON-NLS-1$
	private static final String TITLE_SESSION = Messages.getString("ConnectionInfo.Session"); //$NON-NLS-1$
	private static final String TITLE_COMPUTER = Messages.getString("SessionInfo.Computer"); //$NON-NLS-1$
	private static final String TITLE_APPLICATION = Messages.getString("SessionInfo.Application"); //$NON-NLS-1$
	private static final String TITLE_HOSTNAME = Messages.getString("ConnectionInfo.Hostname"); //$NON-NLS-1$
	private static final String TITLE_PORT = Messages.getString("SessionInfo.Port"); //$NON-NLS-1$
	private static final String TITLE_LOCKED_AT = Messages.getString("LockInfo.LockedAt"); //$NON-NLS-1$
	
	Server server;
	UUID clusterId;
	UUID infobaseId;
	IObjectLockInfo lockInfo;
	List<ISessionInfo> sessionsInfo;
	List<IInfoBaseConnectionShort> connections;
	
	Map<String, String> columnsMap = new LinkedHashMap<>();
	
	public LockInfoExtended(Server server, UUID clusterId, UUID infobaseId, IObjectLockInfo lockInfo,
			List<ISessionInfo> sessionsInfo, List<IInfoBaseConnectionShort> connections,
			Map<String, String> columnsMap) {
		
		this.server = server;
		this.clusterId = clusterId;
		this.infobaseId = infobaseId;
		this.lockInfo = lockInfo;
		this.sessionsInfo = sessionsInfo;
		this.connections = connections;
		this.columnsMap = columnsMap;
		
	}
	
	public static void initColumnsName(Map<String, String> columnsMap) {
		
		columnsMap.put(TITLE_DESCRIPTION, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_INFOBASE, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CONNECTION, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_SESSION, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_COMPUTER, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_APPLICATION, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_HOSTNAME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_PORT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_LOCKED_AT, ""); //$NON-NLS-1$
		
		ClusterProvider.getCommonConfig().initLocksColumnCount(columnsMap.size());
		
	}
	
	public String[] getExtendedInfo() {
		
		var connectionNumber = ""; //$NON-NLS-1$
		var sessionNumber = ""; //$NON-NLS-1$
		var computerName = ""; //$NON-NLS-1$
		var appName = ""; //$NON-NLS-1$
		var hostName = ""; //$NON-NLS-1$
		var hostPort = ""; //$NON-NLS-1$
		var infobaseName = ""; //$NON-NLS-1$
		
		if (!lockInfo.getSid().equals(emptyUuid)) {
			ISessionInfo session = getSessionInfoFromLockConnectionId(lockInfo, sessionsInfo);
			if (session != null) {
				sessionNumber = Integer.toString(session.getSessionId());
				appName = session.getAppId();
				computerName = session.getHost();
				infobaseName = server.getInfoBaseName(clusterId, session.getInfoBaseId());
			}
			
		} else if (!lockInfo.getConnectionId().equals(emptyUuid)) {
			IInfoBaseConnectionShort connection = getConnectionInfoFromLockConnectionId(lockInfo, connections);
			
			if (connection != null) {
				connectionNumber = Integer.toString(connection.getConnId());
				appName = connection.getApplication();
				computerName = connection.getHost();
				infobaseName = server.getInfoBaseName(clusterId, connection.getInfoBaseId());
				UUID wpId = connection.getWorkingProcessId();
				
				IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, wpId);
				if (wpInfo != null) {
					hostName = wpInfo.getHostName();
					hostPort = Integer.toString(wpInfo.getMainPort());
				}
			}
		}
		
		Map<String, String> lock = new LinkedHashMap<>();
		lock.putAll(columnsMap);
		
		lock.put(TITLE_DESCRIPTION, lockInfo.getLockDescr());
		lock.put(TITLE_INFOBASE, infobaseName);
		lock.put(TITLE_CONNECTION, connectionNumber);
		lock.put(TITLE_SESSION, sessionNumber);
		lock.put(TITLE_COMPUTER, computerName);
		lock.put(TITLE_APPLICATION, server.getApplicationName(appName));
		lock.put(TITLE_HOSTNAME, hostName);
		lock.put(TITLE_PORT, hostPort);
		lock.put(TITLE_LOCKED_AT, dateToString(lockInfo.getLockedAt()));
		
		return lock.values().toArray(new String[0]);
		
	}
	
	private ISessionInfo getSessionInfoFromLockConnectionId(IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo) {
		
		for (ISessionInfo session : sessionsInfo) {
			if (session.getSid().equals(lockInfo.getSid()))
				return session;
		}
		return null;
	}
	
	private IInfoBaseConnectionShort getConnectionInfoFromLockConnectionId(IObjectLockInfo lockInfo,
			List<IInfoBaseConnectionShort> connections) {
		
		for (IInfoBaseConnectionShort connection : connections) {
			if (connection.getInfoBaseConnectionId().equals(lockInfo.getConnectionId()))
				return connection;
		}
		return null;
	}
	
}
