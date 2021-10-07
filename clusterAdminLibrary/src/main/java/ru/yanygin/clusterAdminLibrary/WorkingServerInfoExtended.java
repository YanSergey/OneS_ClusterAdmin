package ru.yanygin.clusterAdminLibrary;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IPortRangeInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;

public class WorkingServerInfoExtended implements IInfoExtended {
	
	private static final String TITLE_DESCRIPTION = Messages.getString("InfoTables.Description"); //$NON-NLS-1$
	private static final String TITLE_COMPUTER = Messages.getString("SessionInfo.Computer"); //$NON-NLS-1$
	private static final String TITLE_IP_PORT = Messages.getString("WSInfo.IPPort"); //$NON-NLS-1$
	private static final String TITLE_RANGE_IP_PORTS = Messages.getString("WSInfo.RangeIPPorts"); //$NON-NLS-1$
	private static final String TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT = Messages
			.getString("WSInfo.SafeWorkingProcessesMemoryLimit"); //$NON-NLS-1$
	private static final String TITLE_SAFE_CALL_MEMORY_LIMIT = Messages.getString("WSInfo.SafeCallMemoryLimit"); //$NON-NLS-1$
	private static final String TITLE_WORKING_PROCESS_MEMORY_LIMIT = Messages
			.getString("WSInfo.WorkingProcessMemoryLimit"); //$NON-NLS-1$
	private static final String TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY = Messages
			.getString("WSInfo.CriticalProcessesTotalMemory"); //$NON-NLS-1$
	private static final String TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY = Messages
			.getString("WSInfo.TemporaryAllowedProcessesTotalMemory"); //$NON-NLS-1$
	private static final String TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT = Messages
			.getString("WSInfo.TemporaryAllowedProcessesTotalMemoryTimeLimit"); //$NON-NLS-1$
	private static final String TITLE_IB_PER_PROCESS_LIMIT = Messages.getString("WSInfo.IBPerProcessLimit"); //$NON-NLS-1$
	private static final String TITLE_CONN_PER_PROCESS_LIMIT = Messages.getString("WSInfo.ConnPerProcessLimit"); //$NON-NLS-1$
	private static final String TITLE_IP_PORT_MAIN_MANAGER = Messages.getString("WSInfo.IPPortMainManager"); //$NON-NLS-1$
	private static final String TITLE_DEDICATED_MANAGERS = Messages.getString("WSInfo.DedicatedManagers"); //$NON-NLS-1$
	private static final String TITLE_MAIN_SERVER = Messages.getString("WSInfo.MainServer"); //$NON-NLS-1$
	
	Server server;
	UUID clusterId;
	IWorkingServerInfo workingServer;
	
	Map<String, String> columnsMap = new LinkedHashMap<>();
	
	public WorkingServerInfoExtended(Server server, UUID clusterId, IWorkingServerInfo workingServer,
			Map<String, String> columnsMap) {
		
		this.server = server;
		this.clusterId = clusterId;
		this.workingServer = workingServer;
		this.columnsMap = columnsMap;
		
	}
	
	public static void initColumnsName(Map<String, String> columnsMap) {
		
		columnsMap.put(TITLE_DESCRIPTION, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_COMPUTER, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_IP_PORT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_RANGE_IP_PORTS, ""); //$NON-NLS-1$
		
		columnsMap.put(TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_SAFE_CALL_MEMORY_LIMIT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_WORKING_PROCESS_MEMORY_LIMIT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT, ""); //$NON-NLS-1$
		
		columnsMap.put(TITLE_IB_PER_PROCESS_LIMIT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_CONN_PER_PROCESS_LIMIT, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_IP_PORT_MAIN_MANAGER, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_DEDICATED_MANAGERS, ""); //$NON-NLS-1$
		columnsMap.put(TITLE_MAIN_SERVER, ""); //$NON-NLS-1$
		
		ClusterProvider.getCommonConfig().initWorkingServersColumnCount(columnsMap.size());
		
	}
	
	public String[] getExtendedInfo() {
		
		Map<String, String> ws = new LinkedHashMap<>();
		ws.putAll(columnsMap);
		
		ws.put(TITLE_DESCRIPTION, workingServer.getName()); // $NON-NLS-1$
		ws.put(TITLE_COMPUTER, workingServer.getHostName()); // $NON-NLS-1$
		ws.put(TITLE_IP_PORT, Integer.toString(workingServer.getMainPort())); // $NON-NLS-1$
		ws.put(TITLE_RANGE_IP_PORTS, getPortRange()); // $NON-NLS-1$
		ws.put(TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT,
				longToStringGroup(workingServer.getSafeWorkingProcessesMemoryLimit())); // $NON-NLS-1$
		ws.put(TITLE_SAFE_CALL_MEMORY_LIMIT, longToStringGroup(workingServer.getSafeCallMemoryLimit())); // $NON-NLS-1$
		ws.put(TITLE_WORKING_PROCESS_MEMORY_LIMIT, longToStringGroup(workingServer.getWorkingProcessMemoryLimit())); // $NON-NLS-1$
		ws.put(TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY,
				longToStringGroup(workingServer.getCriticalProcessesTotalMemory())); // $NON-NLS-1$
		ws.put(TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY,
				longToStringGroup(workingServer.getTemporaryAllowedProcessesTotalMemory())); // $NON-NLS-1$
		ws.put(TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT,
				longToStringGroup(workingServer.getTemporaryAllowedProcessesTotalMemoryTimeLimit())); // $NON-NLS-1$
		ws.put(TITLE_IB_PER_PROCESS_LIMIT, Integer.toString(workingServer.getInfoBasesPerWorkingProcessLimit())); // $NON-NLS-1$
		ws.put(TITLE_CONN_PER_PROCESS_LIMIT, Integer.toString(workingServer.getConnectionsPerWorkingProcessLimit())); // $NON-NLS-1$
		ws.put(TITLE_IP_PORT_MAIN_MANAGER, Integer.toString(workingServer.getClusterMainPort())); // $NON-NLS-1$
		ws.put(TITLE_DEDICATED_MANAGERS, Boolean.toString(workingServer.isDedicatedManagers())); // $NON-NLS-1$
		ws.put(TITLE_MAIN_SERVER, Boolean.toString(workingServer.isMainServer())); // $NON-NLS-1$
		
		return ws.values().toArray(new String[0]);
		
	}
	
	private String getPortRange() {
		IPortRangeInfo portRangesInfo = workingServer.getPortRanges().get(0);
		return Integer.toString(portRangesInfo.getLowBound()).concat(":") //$NON-NLS-1$
				.concat(Integer.toString(portRangesInfo.getHighBound()));
	}
	
}
