package ru.yanygin.clusterAdminLibrary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;

public class SessionInfoExtended implements IInfoExtended {
	
	private static final String TITLE_USERNAME = Messages.getString("SessionInfo.Username"); //$NON-NLS-1$
	private static final String TITLE_INFOBASE = Messages.getString("SessionInfo.Infobase"); //$NON-NLS-1$
	private static final String TITLE_SESSION_N = Messages.getString("SessionInfo.SessionN"); //$NON-NLS-1$
	private static final String TITLE_CONNECTION_N = Messages.getString("SessionInfo.ConnectionN"); //$NON-NLS-1$
	private static final String TITLE_STARTED_AT = Messages.getString("SessionInfo.StartedAt"); //$NON-NLS-1$
	private static final String TITLE_LAST_ACTIVE_AT = Messages.getString("SessionInfo.LastActiveAt"); //$NON-NLS-1$
	private static final String TITLE_COMPUTER = Messages.getString("SessionInfo.Computer"); //$NON-NLS-1$
	private static final String TITLE_APPLICATION = Messages.getString("SessionInfo.Application"); //$NON-NLS-1$
	private static final String TITLE_SERVER = Messages.getString("SessionInfo.Server"); //$NON-NLS-1$
	private static final String TITLE_PORT = Messages.getString("SessionInfo.Port"); //$NON-NLS-1$
	private static final String TITLE_PID = Messages.getString("SessionInfo.PID"); //$NON-NLS-1$
	private static final String TITLE_DB_PROC_INFO = Messages.getString("SessionInfo.DbProcInfo"); //$NON-NLS-1$
	private static final String TITLE_DB_PROC_TOOK = Messages.getString("SessionInfo.DbProcTook"); //$NON-NLS-1$
	private static final String TITLE_DB_PROC_TOOK_AT = Messages.getString("SessionInfo.DbProcTookAt"); //$NON-NLS-1$
	private static final String TITLE_BLOCKED_BY_DBMS = Messages.getString("SessionInfo.BlockedByDbms"); //$NON-NLS-1$
	private static final String TITLE_BLOCKED_BY_LS = Messages.getString("SessionInfo.BlockedByLs"); //$NON-NLS-1$
	private static final String TITLE_DURATION_CURRENT_DBMS = Messages.getString("SessionInfo.DurationCurrentDbms"); //$NON-NLS-1$
	private static final String TITLE_DURATION_LAST_5_MIN_DBMS = Messages.getString("SessionInfo.DurationLast5MinDbms"); //$NON-NLS-1$
	private static final String TITLE_DURATION_ALL_DBMS = Messages.getString("SessionInfo.DurationAllDbms"); //$NON-NLS-1$
	private static final String TITLE_DBMS_BYTES_LAST_5_MIN = Messages.getString("SessionInfo.DbmsBytesLast5Min"); //$NON-NLS-1$
	private static final String TITLE_DBMS_BYTES_ALL = Messages.getString("SessionInfo.DbmsBytesAll"); //$NON-NLS-1$
	private static final String TITLE_DURATION_CURRENT = Messages.getString("SessionInfo.DurationCurrent"); //$NON-NLS-1$
	private static final String TITLE_DURATION_LAST_5_MIN = Messages.getString("SessionInfo.DurationLast5Min"); //$NON-NLS-1$
	private static final String TITLE_DURATION_ALL = Messages.getString("SessionInfo.DurationAll"); //$NON-NLS-1$
	private static final String TITLE_CALLS_LAST_5_MIN = Messages.getString("SessionInfo.CallsLast5Min"); //$NON-NLS-1$
	private static final String TITLE_CALLS_ALL = Messages.getString("SessionInfo.CallsAll"); //$NON-NLS-1$
	private static final String TITLE_BYTES_LAST_5_MIN = Messages.getString("SessionInfo.BytesLast5Min"); //$NON-NLS-1$
	private static final String TITLE_BYTES_ALL = Messages.getString("SessionInfo.BytesAll"); //$NON-NLS-1$
	private static final String TITLE_MEMORY_CURRENT = Messages.getString("SessionInfo.MemoryCurrent"); //$NON-NLS-1$
	private static final String TITLE_MEMORY_LAST_5_MIN = Messages.getString("SessionInfo.MemoryLast5Min"); //$NON-NLS-1$
	private static final String TITLE_MEMORY_TOTAL = Messages.getString("SessionInfo.MemoryTotal"); //$NON-NLS-1$
	private static final String TITLE_READ_BYTES_CURRENT = Messages.getString("SessionInfo.ReadBytesCurrent"); //$NON-NLS-1$
	private static final String TITLE_READ_BYTES_LAST_5_MIN = Messages.getString("SessionInfo.ReadBytesLast5Min"); //$NON-NLS-1$
	private static final String TITLE_READ_BYTES_TOTAL = Messages.getString("SessionInfo.ReadBytesTotal"); //$NON-NLS-1$
	private static final String TITLE_WRITE_BYTES_CURRENT = Messages.getString("SessionInfo.WriteBytesCurrent"); //$NON-NLS-1$
	private static final String TITLE_WRITE_BYTES_LAST_5_MIN = Messages.getString("SessionInfo.WriteBytesLast5Min"); //$NON-NLS-1$
	private static final String TITLE_WRITE_BYTES_TOTAL = Messages.getString("SessionInfo.WriteBytesTotal"); //$NON-NLS-1$
	private static final String TITLE_LICENSE = Messages.getString("SessionInfo.License"); //$NON-NLS-1$
	private static final String TITLE_IS_SLEEP = Messages.getString("SessionInfo.IsSleep"); //$NON-NLS-1$
	private static final String TITLE_SLEEP_AFTER = Messages.getString("SessionInfo.SleepAfter"); //$NON-NLS-1$
	private static final String TITLE_KILL_AFTER = Messages.getString("SessionInfo.KillAfter"); //$NON-NLS-1$
	private static final String TITLE_CLIENT_IP_ADDRESS = Messages.getString("SessionInfo.ClientIPAddress"); //$NON-NLS-1$
	private static final String TITLE_DATA_SEPARATION = Messages.getString("SessionInfo.DataSeparation"); //$NON-NLS-1$
	private static final String TITLE_CURRRENT_SERVICE_NAME = Messages.getString("SessionInfo.CurrentServiceName"); //$NON-NLS-1$
	private static final String TITLE_DURATION_CURRENT_SERVICE = Messages.getString("SessionInfo.DurationCurrentService"); //$NON-NLS-1$
	private static final String TITLE_DURATION_LAST_5_MIN_SERVICE = Messages.getString("SessionInfo.DurationLast5MinService"); //$NON-NLS-1$
	private static final String TITLE_DURATION_ALL_SERVICE = Messages.getString("SessionInfo.DurationAllService"); //$NON-NLS-1$
	private static final String TITLE_CPU_TIME_CURRENT = Messages.getString("SessionInfo.CpuTimeCurrent"); //$NON-NLS-1$
	private static final String TITLE_CPU_TIME_LAST_5_MIN = Messages.getString("SessionInfo.CpuTimeLast5Min"); //$NON-NLS-1$
	private static final String TITLE_CPU_TIME_ALL = Messages.getString("SessionInfo.CpuTimeAll"); //$NON-NLS-1$
	
	Server server;
	UUID clusterId;
	UUID infobaseId;
	ISessionInfo sessionInfo;
	List<IInfoBaseConnectionShort> connections;
	
	Map<String, String> columnsMap = new LinkedHashMap<>();
	
	public SessionInfoExtended(Server server, UUID clusterId, UUID infobaseId, ISessionInfo sessionInfo,
			List<IInfoBaseConnectionShort> connections, Map<String, String> columnsMap) {
		
		this.server = server;
		this.clusterId = clusterId;
		this.infobaseId = infobaseId;
		this.sessionInfo = sessionInfo;
		this.connections = connections;
		this.columnsMap = columnsMap;
		
	}
	
	public static void initColumnsName(Map<String, String> sessionColumnsMap) {
		
		sessionColumnsMap.put(TITLE_USERNAME, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_INFOBASE, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_SESSION_N, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_CONNECTION_N, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_STARTED_AT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_LAST_ACTIVE_AT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_COMPUTER, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_APPLICATION, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_SERVER, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_PORT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_PID, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_DB_PROC_INFO, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DB_PROC_TOOK, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DB_PROC_TOOK_AT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_BLOCKED_BY_DBMS, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_BLOCKED_BY_LS, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_DURATION_CURRENT_DBMS, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_LAST_5_MIN_DBMS, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_ALL_DBMS, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DBMS_BYTES_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DBMS_BYTES_ALL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_DURATION_CURRENT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_ALL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_CALLS_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_CALLS_ALL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_BYTES_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_BYTES_ALL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_MEMORY_CURRENT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_MEMORY_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_MEMORY_TOTAL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_READ_BYTES_CURRENT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_READ_BYTES_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_READ_BYTES_TOTAL, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_WRITE_BYTES_CURRENT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_WRITE_BYTES_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_WRITE_BYTES_TOTAL, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_LICENSE, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_IS_SLEEP, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_SLEEP_AFTER, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_KILL_AFTER, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_CLIENT_IP_ADDRESS, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DATA_SEPARATION, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_CURRRENT_SERVICE_NAME, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_CURRENT_SERVICE, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_LAST_5_MIN_SERVICE, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_DURATION_ALL_SERVICE, ""); //$NON-NLS-1$
		
		sessionColumnsMap.put(TITLE_CPU_TIME_CURRENT, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_CPU_TIME_LAST_5_MIN, ""); //$NON-NLS-1$
		sessionColumnsMap.put(TITLE_CPU_TIME_ALL, ""); //$NON-NLS-1$
		
		ClusterProvider.getCommonConfig().initSessionsColumnCount(sessionColumnsMap.size());
		
	}
	
	public String[] getExtendedInfo() {
		
		if (this.infobaseId == null)
			this.infobaseId = sessionInfo.getInfoBaseId();
		
		String infobaseName = server.getInfoBaseName(clusterId, infobaseId);
		
		// connection
		var connectionNumber = ""; //$NON-NLS-1$
		if (!sessionInfo.getConnectionId().equals(emptyUuid)) {
			IInfoBaseConnectionShort connectionInfoShort = server.getConnectionInfoShort(clusterId, sessionInfo.getConnectionId());
			connectionNumber = String.valueOf(connectionInfoShort.getConnId());
		}
		
		// Working Process
		var wpHostName = ""; //$NON-NLS-1$
		var wpMainPort = ""; //$NON-NLS-1$
		var wpPid = ""; //$NON-NLS-1$
		if (!sessionInfo.getWorkingProcessId().equals(emptyUuid)) {
			IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, sessionInfo.getWorkingProcessId());
			wpHostName = wpInfo.getHostName();
			wpMainPort = Integer.toString(wpInfo.getMainPort());
			wpPid = wpInfo.getPid();
		}
		
		// license
		var license = sessionInfo.getLicenses().isEmpty() ? "" : sessionInfo.getLicenses().get(0).getFullPresentation(); //$NON-NLS-1$
		
		Map<String, String> session = new LinkedHashMap<>();
		session.putAll(columnsMap);
		
		session.put(TITLE_USERNAME, sessionInfo.getUserName());
		session.put(TITLE_INFOBASE, infobaseName);
		session.put(TITLE_SESSION_N, Integer.toString(sessionInfo.getSessionId()));
		session.put(TITLE_CONNECTION_N, connectionNumber);
		session.put(TITLE_STARTED_AT, dateToString(sessionInfo.getStartedAt()));
		session.put(TITLE_LAST_ACTIVE_AT, dateToString(sessionInfo.getLastActiveAt()));
		session.put(TITLE_COMPUTER, sessionInfo.getHost());
		session.put(TITLE_APPLICATION, server.getApplicationName(sessionInfo.getAppId()));
		session.put(TITLE_SERVER, wpHostName);
		session.put(TITLE_PORT, wpMainPort);
		session.put(TITLE_PID, wpPid);
		
		session.put(TITLE_DB_PROC_INFO, sessionInfo.getDbProcInfo());
		session.put(TITLE_DB_PROC_TOOK, double3ToString(sessionInfo.getDbProcTook()));
		session.put(TITLE_DB_PROC_TOOK_AT, dateToString(sessionInfo.getDbProcTookAt()));
		session.put(TITLE_BLOCKED_BY_DBMS, Integer.toString(sessionInfo.getBlockedByDbms()));
		session.put(TITLE_BLOCKED_BY_LS, Integer.toString(sessionInfo.getBlockedByLs()));
		
		session.put(TITLE_DURATION_CURRENT_DBMS, millisecondToString(sessionInfo.getDurationCurrentDbms()));
		session.put(TITLE_DURATION_LAST_5_MIN_DBMS, millisecondToString(sessionInfo.getDurationLast5MinDbms()));
		session.put(TITLE_DURATION_ALL_DBMS, millisecondToString(sessionInfo.getDurationAllDbms()));
		
		session.put(TITLE_DBMS_BYTES_LAST_5_MIN, longToStringGroup(sessionInfo.getDbmsBytesLast5Min()));
		session.put(TITLE_DBMS_BYTES_ALL, longToStringGroup(sessionInfo.getDbmsBytesAll()));
		
		session.put(TITLE_DURATION_CURRENT, millisecondToString(sessionInfo.getDurationCurrent()));
		session.put(TITLE_DURATION_LAST_5_MIN, millisecondToString(sessionInfo.getDurationLast5Min()));
		session.put(TITLE_DURATION_ALL, millisecondToString(sessionInfo.getDurationAll()));
		
		session.put(TITLE_CALLS_LAST_5_MIN, Long.toString(sessionInfo.getCallsLast5Min()));
		session.put(TITLE_CALLS_ALL, Integer.toString(sessionInfo.getCallsAll()));
		
		session.put(TITLE_BYTES_LAST_5_MIN, longToStringGroup(sessionInfo.getBytesLast5Min()));
		session.put(TITLE_BYTES_ALL, longToStringGroup(sessionInfo.getBytesAll()));
		
		session.put(TITLE_MEMORY_CURRENT, longToStringGroup(sessionInfo.getMemoryCurrent()));
		session.put(TITLE_MEMORY_LAST_5_MIN, longToStringGroup(sessionInfo.getMemoryLast5Min()));
		session.put(TITLE_MEMORY_TOTAL, longToStringGroup(sessionInfo.getMemoryTotal()));
		
		session.put(TITLE_READ_BYTES_CURRENT, longToStringGroup(sessionInfo.getReadBytesCurrent()));
		session.put(TITLE_READ_BYTES_LAST_5_MIN, longToStringGroup(sessionInfo.getReadBytesLast5Min()));
		session.put(TITLE_READ_BYTES_TOTAL, longToStringGroup(sessionInfo.getReadBytesTotal()));
		session.put(TITLE_WRITE_BYTES_CURRENT, longToStringGroup(sessionInfo.getWriteBytesCurrent()));
		session.put(TITLE_WRITE_BYTES_LAST_5_MIN, longToStringGroup(sessionInfo.getWriteBytesLast5Min()));
		session.put(TITLE_WRITE_BYTES_TOTAL, longToStringGroup(sessionInfo.getWriteBytesTotal()));
		
		session.put(TITLE_LICENSE, license);
		session.put(TITLE_IS_SLEEP, Boolean.toString(sessionInfo.getHibernate()));
		session.put(TITLE_SLEEP_AFTER, Integer.toString(sessionInfo.getPassiveSessionHibernateTime()));
		session.put(TITLE_KILL_AFTER, Integer.toString(sessionInfo.getHibernateSessionTerminationTime()));
		session.put(TITLE_CLIENT_IP_ADDRESS, sessionInfo.getClientIPAddress());
		session.put(TITLE_DATA_SEPARATION, sessionInfo.getDataSeparation());
		
		session.put(TITLE_CURRRENT_SERVICE_NAME, sessionInfo.getCurrentServiceName());
		session.put(TITLE_DURATION_CURRENT_SERVICE, millisecondToString(sessionInfo.getDurationCurrentService()));
		session.put(TITLE_DURATION_LAST_5_MIN_SERVICE, millisecondToString(sessionInfo.getDurationLast5MinService()));
		session.put(TITLE_DURATION_ALL_SERVICE, millisecondToString(sessionInfo.getDurationAllService()));
		
		session.put(TITLE_CPU_TIME_CURRENT, millisecondToString(sessionInfo.getCpuTimeCurrent()));
		session.put(TITLE_CPU_TIME_LAST_5_MIN, millisecondToString(sessionInfo.getCpuTimeLast5Min()));
		session.put(TITLE_CPU_TIME_ALL, millisecondToString(sessionInfo.getCpuTimeAll()));
		
		return session.values().toArray(new String[0]);
		
	}
	
}
