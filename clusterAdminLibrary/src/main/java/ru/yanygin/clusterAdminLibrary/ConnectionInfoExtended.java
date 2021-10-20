package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Extend info for connection. */
public class ConnectionInfoExtended implements IInfoExtended {

  private static final String TITLE_INFOBASE =
      Messages.getString("SessionInfo.Infobase"); //$NON-NLS-1$
  private static final String TITLE_CONNECTION =
      Messages.getString("ConnectionInfo.Connection"); //$NON-NLS-1$
  private static final String TITLE_SESSION =
      Messages.getString("ConnectionInfo.Session"); //$NON-NLS-1$
  private static final String TITLE_COMPUTER =
      Messages.getString("SessionInfo.Computer"); //$NON-NLS-1$
  private static final String TITLE_APPLICATION =
      Messages.getString("SessionInfo.Application"); //$NON-NLS-1$
  private static final String TITLE_SERVER =
      Messages.getString("SessionInfo.Server"); //$NON-NLS-1$
  private static final String TITLE_RP_HOST_PORT =
      Messages.getString("ConnectionInfo.RpHostPort"); //$NON-NLS-1$
  private static final String TITLE_CONNECTED_AT =
      Messages.getString("ConnectionInfo.ConnectedAt"); //$NON-NLS-1$
  // private static final String TITLE_INFOBASE_CONNECTION_ID =
  // Messages.getString("ConnectionInfo.InfobaseConnectionID"); //$NON-NLS-1$

  Server server;
  UUID clusterId;
  UUID infobaseId;
  IInfoBaseConnectionShort connectionInfo;
  List<IWorkingProcessInfo> workingProcesses;

  Map<String, String> columnsMap = new LinkedHashMap<>();

  /**
   * Create extended info for working server.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   * @param connectionInfo - connection info
   * @param workingProcesses - working process info
   * @param columnsMap - columns map
   */
  public ConnectionInfoExtended(
      Server server,
      UUID clusterId,
      UUID infobaseId,
      IInfoBaseConnectionShort connectionInfo,
      List<IWorkingProcessInfo> workingProcesses,
      Map<String, String> columnsMap) {

    this.server = server;
    this.clusterId = clusterId;
    this.infobaseId = infobaseId;
    this.connectionInfo = connectionInfo;
    this.workingProcesses = workingProcesses;
    this.columnsMap = columnsMap;
  }

  /**
   * Init columns name.
   *
   * @param columnsMap - sample map with columns name
   */
  public static void initColumnsName(Map<String, String> columnsMap) {

    columnsMap.put(TITLE_INFOBASE, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_CONNECTION, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_SESSION, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_COMPUTER, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_APPLICATION, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_SERVER, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_RP_HOST_PORT, ""); //$NON-NLS-1$
    columnsMap.put(TITLE_CONNECTED_AT, ""); //$NON-NLS-1$
    // connectionColumnsMap.put(TITLE_INFOBASE_CONNECTION_ID, ""); //$NON-NLS-1$

    ClusterProvider.getCommonConfig().initConnectionsColumnCount(columnsMap.size());
  }

  @Override
  public String[] getExtendedInfo() {

    String infobaseName = ""; //$NON-NLS-1$
    if (infobaseId == null && !connectionInfo.getInfoBaseId().equals(emptyUuid)) {
      infobaseId = connectionInfo.getInfoBaseId();
      infobaseName = server.getInfoBaseName(clusterId, infobaseId);
    }

    String[] currentWorkingProcessInfo =
        getWorkingProcessInfo(connectionInfo.getWorkingProcessId());

    Map<String, String> session = new LinkedHashMap<>();
    session.putAll(columnsMap);

    session.put(TITLE_INFOBASE, infobaseName);
    session.put(TITLE_CONNECTION, Integer.toString(connectionInfo.getConnId()));
    session.put(TITLE_SESSION, Integer.toString(connectionInfo.getSessionNumber()));
    session.put(TITLE_COMPUTER, connectionInfo.getHost());
    session.put(TITLE_APPLICATION, server.getApplicationName(connectionInfo.getApplication()));
    session.put(TITLE_SERVER, currentWorkingProcessInfo[0]); //$NON-NLS-1$
    session.put(TITLE_RP_HOST_PORT, currentWorkingProcessInfo[1]);
    session.put(TITLE_CONNECTED_AT, dateToString(connectionInfo.getConnectedAt()));
    // session.put(TITLE_INFOBASE_CONNECTION_ID,
    // convertUuidToString(connectionInfo.getInfoBaseConnectionId()));

    return session.values().toArray(new String[0]);
  }

  private String[] getWorkingProcessInfo(UUID workingProcessId) {

    for (IWorkingProcessInfo workingProcess : workingProcesses) {
      if (workingProcess.getWorkingProcessId().equals(workingProcessId)) {
        return new String[] {
          workingProcess.getHostName(), Integer.toString(workingProcess.getMainPort())
        };
      }
    }
    return new String[] {"", ""};
  }
}
