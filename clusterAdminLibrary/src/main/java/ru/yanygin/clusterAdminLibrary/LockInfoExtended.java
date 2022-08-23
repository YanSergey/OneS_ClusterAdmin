package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import java.util.List;
import java.util.UUID;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;

/** Расширенная информация для блокировки. */
public class LockInfoExtended extends BaseInfoExtended {

  private static final String TITLE_DESCRIPTION = "InfoTables.Description"; //$NON-NLS-1$
  private static final String TITLE_INFOBASE = "SessionInfo.Infobase"; //$NON-NLS-1$
  private static final String TITLE_CONNECTION = "ConnectionInfo.Connection"; //$NON-NLS-1$
  private static final String TITLE_SESSION = "ConnectionInfo.Session"; //$NON-NLS-1$
  private static final String TITLE_COMPUTER = "SessionInfo.Computer"; //$NON-NLS-1$
  private static final String TITLE_APPLICATION = "SessionInfo.Application"; //$NON-NLS-1$
  private static final String TITLE_HOSTNAME = "ConnectionInfo.Hostname"; //$NON-NLS-1$
  private static final String TITLE_PORT = "SessionInfo.Port"; //$NON-NLS-1$
  private static final String TITLE_LOCKED_AT = "LockInfo.LockedAt"; //$NON-NLS-1$

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(LockInfoExtended.class);

  private static final String DEFAULT_ICON_FILENAME = "lock_16.png";
  private static Image defaultIcon;

  private static final String TAB_TEXT_TEMPLATE =
      Messages.getString("TabText.LocksCount"); //$NON-NLS-1$

  private static TabItem currentTab;
  private static int itemCount;

  private IObjectLockInfo lockInfo;
  private List<ISessionInfo> sessionsInfo;
  private List<IInfoBaseConnectionShort> connections;

  /**
   * Создание расширенной информации для блокировки.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param lockInfo - lock info
   */
  public LockInfoExtended(Server server, UUID clusterId, IObjectLockInfo lockInfo) {

    this.server = server;
    this.clusterId = clusterId;
    
    this.lockInfo = lockInfo;
    this.sessionsInfo = server.getSessions(clusterId);
    this.currentIcon = defaultIcon;

    computeExtendedInfoData();
  }

  protected void computeExtendedInfoData() {

    var connectionNumber = ""; //$NON-NLS-1$
    var sessionNumber = ""; //$NON-NLS-1$
    var computerName = ""; //$NON-NLS-1$
    var appName = ""; //$NON-NLS-1$
    var hostName = ""; //$NON-NLS-1$
    var hostPort = ""; //$NON-NLS-1$
    var infobaseName = ""; //$NON-NLS-1$

    if (!lockInfo.getSid().equals(Helper.EMPTY_UUID)) {
      ISessionInfo session = getSessionInfoFromLockConnectionId(lockInfo, sessionsInfo);
      if (session != null) {
        sessionNumber = Integer.toString(session.getSessionId());
        computerName = session.getHost();
        appName = session.getAppId();
        infobaseName = server.getInfoBaseName(clusterId, session.getInfoBaseId());
      }

    } else if (!lockInfo.getConnectionId().equals(Helper.EMPTY_UUID)) {
      IInfoBaseConnectionShort connection =
          getConnectionInfoFromLockConnectionId(lockInfo, connections);

      if (connection != null) {
        connectionNumber = Integer.toString(connection.getConnId());
        computerName = connection.getHost();
        appName = connection.getApplication();
        infobaseName = server.getInfoBaseName(clusterId, connection.getInfoBaseId());

        UUID wpId = connection.getWorkingProcessId();
        IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, wpId);
        if (wpInfo != null) {
          hostName = wpInfo.getHostName();
          hostPort = Integer.toString(wpInfo.getMainPort());
        }
      }
    }

    columnProperties.prepareDataMap(data);

    putData(TITLE_DESCRIPTION, lockInfo.getLockDescr(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_INFOBASE, infobaseName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_CONNECTION, connectionNumber, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_SESSION, sessionNumber, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_COMPUTER, computerName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_APPLICATION, server.getApplicationName(appName), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_HOSTNAME, hostName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_PORT, hostPort, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_LOCKED_AT, lockInfo.getLockedAt(), CELL_VALUE_TYPE.DATE);
  }

  @Override
  public void addToTable(Table table, int index) {
    createTableItem(table, index, lockInfo.getLockedAt());
  }

  /**
   * Get LockInfo.
   *
   * @return the lockInfo
   */
  public IObjectLockInfo getLockInfo() {
    return lockInfo;
  }

  private ISessionInfo getSessionInfoFromLockConnectionId(
      IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo) {

    for (ISessionInfo session : sessionsInfo) {
      if (session.getSid().equals(lockInfo.getSid())) {
        return session;
      }
    }
    return null;
  }

  private IInfoBaseConnectionShort getConnectionInfoFromLockConnectionId(
      IObjectLockInfo lockInfo, List<IInfoBaseConnectionShort> connections) {

    return server.getConnectionInfoShort(clusterId, lockInfo.getConnectionId());
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {

    columnProperties.addColumnsInMap(
        TITLE_DESCRIPTION,
        TITLE_INFOBASE,
        TITLE_CONNECTION,
        TITLE_SESSION,
        TITLE_COMPUTER,
        TITLE_APPLICATION,
        TITLE_HOSTNAME,
        TITLE_PORT,
        TITLE_LOCKED_AT);

    defaultIcon = Helper.getImage(DEFAULT_ICON_FILENAME);
  }

  /**
   * Обновление заголовка вкладки.
   *
   * @param count - количество элементов
   */
  protected static void updateTabText(int count) {
    itemCount = count;
    currentTab.setText(String.format(TAB_TEXT_TEMPLATE, itemCount));
  }

  /** Сброс заголовка вкладки на неизвестное количество элементов. */
  protected static void resetTabTextCount() {
    currentTab.setText(String.format(TAB_TEXT_TEMPLATE, itemCount + "*"));
  }

  /**
   * Установка связи с вкладкой TabItem.
   *
   * @param tabitem вкладка Tabitem
   */
  protected static void linkTabItem(TabItem tabitem) {
    currentTab = tabitem;
  }
}
