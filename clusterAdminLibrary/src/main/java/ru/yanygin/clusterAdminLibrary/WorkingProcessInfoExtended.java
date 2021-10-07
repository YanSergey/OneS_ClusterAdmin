package ru.yanygin.clusterAdminLibrary;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IWorkingProcessInfo;

public class WorkingProcessInfoExtended implements IInfoExtended {
	
	private static final String TITLE_COMPUTER = Messages.getString("SessionInfo.Computer"); //$NON-NLS-1$
	private static final String TITLE_PORT = Messages.getString("SessionInfo.Port"); //$NON-NLS-1$
	private static final String TITLE_USING = Messages.getString("WPInfo.Using"); //$NON-NLS-1$
	private static final String TITLE_ENABLES = Messages.getString("WPInfo.Enabled"); //$NON-NLS-1$
	private static final String TITLE_ACTIVE = Messages.getString("WPInfo.Active"); //$NON-NLS-1$
	private static final String TITLE_PID = Messages.getString("SessionInfo.PID"); //$NON-NLS-1$
	private static final String TITLE_MEMORY = Messages.getString("WPInfo.Memory"); //$NON-NLS-1$
	private static final String TITLE_MEMORY_EXCEEDED = Messages.getString("WPInfo.MemoryExceeded"); //$NON-NLS-1$
	private static final String TITLE_AVAILABLE_PERFORMANCE = Messages.getString("WPInfo.AvailablePerformance"); //$NON-NLS-1$
	private static final String TITLE_LICENSE = Messages.getString("SessionInfo.License"); //$NON-NLS-1$
	private static final String TITLE_STARTED_AT = Messages.getString("SessionInfo.StartedAt"); //$NON-NLS-1$
	private static final String TITLE_CONNECTIONS_COUNT = Messages.getString("WPInfo.ConnectionsCount"); //$NON-NLS-1$
	private static final String TITLE_BACK_CALL_TIME = Messages.getString("WPInfo.BackCallTime"); //$NON-NLS-1$
	private static final String TITLE_SERVER_CALL_TIME = Messages.getString("WPInfo.ServerCallTime"); //$NON-NLS-1$
	private static final String TITLE_DB_CALL_TIME = Messages.getString("WPInfo.DBCallTime"); //$NON-NLS-1$
	private static final String TITLE_CALL_TIME = Messages.getString("WPInfo.CallTime"); //$NON-NLS-1$
	private static final String TITLE_LOCK_CALL_TIME = Messages.getString("WPInfo.LockCallTime"); //$NON-NLS-1$
	private static final String TITLE_CLIENT_THREADS = Messages.getString("WPInfo.ClientThreads"); //$NON-NLS-1$
	
	Server server;
	UUID clusterId;
	IWorkingProcessInfo workingProcess;
	
	Map<String, String> columnsMap = new LinkedHashMap<>();
	
	public WorkingProcessInfoExtended(Server server, UUID clusterId, IWorkingProcessInfo workingProcess,
			Map<String, String> columnsMap) {
		
		this.server = server;
		this.clusterId = clusterId;
		this.workingProcess = workingProcess;
		this.columnsMap = columnsMap;
		
	}
	
	public static void initColumnsName(Map<String, String> columnsMap) {
		
		columnsMap.put(TITLE_COMPUTER, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_PORT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_USING, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_ENABLES, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_ACTIVE, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_PID, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_MEMORY, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_MEMORY_EXCEEDED, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_AVAILABLE_PERFORMANCE, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_LICENSE, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_STARTED_AT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CONNECTIONS_COUNT, ""); //$NON-NLS-1$
		
		columnsMap.put(TITLE_BACK_CALL_TIME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_SERVER_CALL_TIME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_DB_CALL_TIME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CALL_TIME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_LOCK_CALL_TIME, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CLIENT_THREADS, ""); //$NON-NLS-1$
		
		ClusterProvider.getCommonConfig().initWorkingProcessesColumnCount(columnsMap.size());
		
	}
	
	public String[] getExtendedInfo() {
		
		Map<String, String> wp = new LinkedHashMap<>();
		wp.putAll(columnsMap);
		
		// license
		var license = workingProcess.getLicense().isEmpty() ? "" : workingProcess.getLicense().get(0).getFullPresentation(); //$NON-NLS-1$
		
		wp.put(TITLE_COMPUTER, workingProcess.getHostName());
		wp.put(TITLE_PORT, Integer.toString(workingProcess.getMainPort()));
		wp.put(TITLE_USING, isUse());
		wp.put(TITLE_ENABLES, Boolean.toString(workingProcess.isEnable()));
		wp.put(TITLE_ACTIVE, isRunning());
		wp.put(TITLE_PID, workingProcess.getPid());
		wp.put(TITLE_MEMORY, longToStringGroup(workingProcess.getMemorySize()));
		wp.put(TITLE_MEMORY_EXCEEDED, Long.toString(workingProcess.getMemoryExcessTime()));
		wp.put(TITLE_AVAILABLE_PERFORMANCE, Integer.toString(workingProcess.getAvailablePerfomance()));
		wp.put(TITLE_LICENSE, license);
		wp.put(TITLE_STARTED_AT, dateToString(workingProcess.getStartedAt()));
		wp.put(TITLE_CONNECTIONS_COUNT, Integer.toString(workingProcess.getConnections()));
		
		wp.put(TITLE_BACK_CALL_TIME, double6ToString(workingProcess.getAvgBackCallTime()));
		wp.put(TITLE_SERVER_CALL_TIME, double6ToString(workingProcess.getAvgServerCallTime()));
		wp.put(TITLE_DB_CALL_TIME, double6ToString(workingProcess.getAvgDBCallTime()));
		wp.put(TITLE_CALL_TIME, double6ToString(workingProcess.getAvgCallTime()));
		wp.put(TITLE_LOCK_CALL_TIME, double6ToString(workingProcess.getAvgLockCallTime()));
		wp.put(TITLE_CLIENT_THREADS, double6ToString(workingProcess.getAvgThreads()));
		
		return wp.values().toArray(new String[0]);
		
	}
	
	private String isUse() {
		
		String isUse;
		switch (workingProcess.getUse()) {
			case 0:
				isUse = Messages.getString("WPInfo.NotUsed"); //$NON-NLS-1$
				break;
			case 1:
				isUse = Messages.getString("WPInfo.Used"); //$NON-NLS-1$
				break;
			case 2:
				isUse = Messages.getString("WPInfo.UsedAsReserve"); //$NON-NLS-1$
				break;
			default:
				isUse = Messages.getString("WPInfo.NotUsed"); //$NON-NLS-1$
				break;
		}
		return isUse;
	}
	
	private String isRunning() {
		
		String isRunning;
		switch (workingProcess.getRunning()) {
			case 0:
				isRunning = Messages.getString("WPInfo.ProcessIsStopped"); //$NON-NLS-1$
				break;
			case 1:
				isRunning = Messages.getString("WPInfo.ProcessIsRunning"); //$NON-NLS-1$
				break;
			default:
				isRunning = Messages.getString("WPInfo.ProcessIsStopped"); //$NON-NLS-1$
				break;
		}
		return isRunning;
	}
	
}
