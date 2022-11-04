package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;

/** Расширенная информация для сеанса. */
public class SessionInfoExtended extends BaseInfoExtended {

  private static final String TITLE_USERNAME = "SessionInfo.Username"; //$NON-NLS-1$
  private static final String TITLE_INFOBASE = "SessionInfo.Infobase"; //$NON-NLS-1$
  private static final String TITLE_SESSION_N = "SessionInfo.SessionN"; //$NON-NLS-1$
  private static final String TITLE_CONNECTION_N = "SessionInfo.ConnectionN"; //$NON-NLS-1$
  private static final String TITLE_STARTED_AT = "SessionInfo.StartedAt"; //$NON-NLS-1$
  private static final String TITLE_LAST_ACTIVE_AT = "SessionInfo.LastActiveAt"; //$NON-NLS-1$
  private static final String TITLE_COMPUTER = "SessionInfo.Computer"; //$NON-NLS-1$
  private static final String TITLE_APPLICATION = "SessionInfo.Application"; //$NON-NLS-1$
  private static final String TITLE_SERVER = "SessionInfo.Server"; //$NON-NLS-1$
  private static final String TITLE_PORT = "SessionInfo.Port"; //$NON-NLS-1$
  private static final String TITLE_PID = "SessionInfo.PID"; //$NON-NLS-1$
  private static final String TITLE_DB_PROC_INFO = "SessionInfo.DbProcInfo"; //$NON-NLS-1$
  private static final String TITLE_DB_PROC_TOOK = "SessionInfo.DbProcTook"; //$NON-NLS-1$
  private static final String TITLE_DB_PROC_TOOK_AT = "SessionInfo.DbProcTookAt"; //$NON-NLS-1$
  private static final String TITLE_BLOCKED_BY_DBMS = "SessionInfo.BlockedByDbms"; //$NON-NLS-1$
  private static final String TITLE_BLOCKED_BY_LS = "SessionInfo.BlockedByLs"; //$NON-NLS-1$
  private static final String TITLE_DURATION_CURRENT_DBMS = "SessionInfo.DurationCurrentDbms"; //$NON-NLS-1$
  private static final String TITLE_DURATION_LAST_5_MIN_DBMS = "SessionInfo.DurationLast5MinDbms"; //$NON-NLS-1$
  private static final String TITLE_DURATION_ALL_DBMS = "SessionInfo.DurationAllDbms"; //$NON-NLS-1$
  private static final String TITLE_DBMS_BYTES_LAST_5_MIN = "SessionInfo.DbmsBytesLast5Min"; //$NON-NLS-1$
  private static final String TITLE_DBMS_BYTES_ALL = "SessionInfo.DbmsBytesAll"; //$NON-NLS-1$
  private static final String TITLE_DURATION_CURRENT = "SessionInfo.DurationCurrent"; //$NON-NLS-1$
  private static final String TITLE_DURATION_LAST_5_MIN = "SessionInfo.DurationLast5Min"; //$NON-NLS-1$
  private static final String TITLE_DURATION_ALL = "SessionInfo.DurationAll"; //$NON-NLS-1$
  private static final String TITLE_CALLS_LAST_5_MIN = "SessionInfo.CallsLast5Min"; //$NON-NLS-1$
  private static final String TITLE_CALLS_ALL = "SessionInfo.CallsAll"; //$NON-NLS-1$
  private static final String TITLE_BYTES_LAST_5_MIN = "SessionInfo.BytesLast5Min"; //$NON-NLS-1$
  private static final String TITLE_BYTES_ALL = "SessionInfo.BytesAll"; //$NON-NLS-1$
  private static final String TITLE_MEMORY_CURRENT = "SessionInfo.MemoryCurrent"; //$NON-NLS-1$
  private static final String TITLE_MEMORY_LAST_5_MIN = "SessionInfo.MemoryLast5Min"; //$NON-NLS-1$
  private static final String TITLE_MEMORY_TOTAL = "SessionInfo.MemoryTotal"; //$NON-NLS-1$
  private static final String TITLE_READ_BYTES_CURRENT = "SessionInfo.ReadBytesCurrent"; //$NON-NLS-1$
  private static final String TITLE_READ_BYTES_LAST_5_MIN = "SessionInfo.ReadBytesLast5Min"; //$NON-NLS-1$
  private static final String TITLE_READ_BYTES_TOTAL = "SessionInfo.ReadBytesTotal"; //$NON-NLS-1$
  private static final String TITLE_WRITE_BYTES_CURRENT = "SessionInfo.WriteBytesCurrent"; //$NON-NLS-1$
  private static final String TITLE_WRITE_BYTES_LAST_5_MIN = "SessionInfo.WriteBytesLast5Min"; //$NON-NLS-1$
  private static final String TITLE_WRITE_BYTES_TOTAL = "SessionInfo.WriteBytesTotal"; //$NON-NLS-1$
  private static final String TITLE_LICENSE = "SessionInfo.License"; //$NON-NLS-1$
  private static final String TITLE_IS_SLEEP = "SessionInfo.IsSleep"; //$NON-NLS-1$
  private static final String TITLE_SLEEP_AFTER = "SessionInfo.SleepAfter"; //$NON-NLS-1$
  private static final String TITLE_KILL_AFTER = "SessionInfo.KillAfter"; //$NON-NLS-1$
  private static final String TITLE_CLIENT_IP_ADDRESS = "SessionInfo.ClientIPAddress"; //$NON-NLS-1$
  private static final String TITLE_DATA_SEPARATION = "SessionInfo.DataSeparation"; //$NON-NLS-1$
  private static final String TITLE_CURRRENT_SERVICE_NAME = "SessionInfo.CurrentServiceName"; //$NON-NLS-1$
  private static final String TITLE_DURATION_CURRENT_SERVICE = "SessionInfo.DurationCurrentService"; //$NON-NLS-1$
  private static final String TITLE_DURATION_LAST_5_MIN_SERVICE = "SessionInfo.DurationLast5MinService"; //$NON-NLS-1$
  private static final String TITLE_DURATION_ALL_SERVICE = "SessionInfo.DurationAllService"; //$NON-NLS-1$
  private static final String TITLE_CPU_TIME_CURRENT = "SessionInfo.CpuTimeCurrent"; //$NON-NLS-1$
  private static final String TITLE_CPU_TIME_LAST_5_MIN = "SessionInfo.CpuTimeLast5Min"; //$NON-NLS-1$
  private static final String TITLE_CPU_TIME_ALL = "SessionInfo.CpuTimeAll"; //$NON-NLS-1$

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(SessionInfoExtended.class);

  private static final String DEFAULT_ICON_FILENAME = "user.png"; //$NON-NLS-1$
  private static final String SLEEP_ICON_FILENAME = "sleepUser.png"; //$NON-NLS-1$
  private static final String SERVICE_ICON_FILENAME = "service.png"; //$NON-NLS-1$
  private static Image defaultIcon;
  private static Image sleepUserIcon;
  private static Image serviceIcon;

  private static final String TAB_TEXT_TEMPLATE =
      Messages.getString("TabText.SessionsCount"); //$NON-NLS-1$

  private static List<String> watchedSessions = new ArrayList<>();

  private static TabItem currentTab;
  private static int itemCount;

  private ISessionInfo sessionInfo;

  private String connectionNumber = "";
  private String license = "";
  private String wpHostName = "";
  private String wpPort = "";
  private String wpPid = "";
  private String infobaseName = "";

  /**
   * Создание расширенной информации для сеанса.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param sessionInfo - session info
   */
  public SessionInfoExtended(Server server, UUID clusterId, ISessionInfo sessionInfo) {

    this.server = server;
    this.clusterId = clusterId;
    this.sessionInfo = sessionInfo;

    switch (sessionInfo.getAppId()) {
      case Server.THIN_CLIENT:
      case Server.THICK_CLIENT:
      case Server.DESIGNER:
        this.currentIcon = sessionInfo.getHibernate() ? sleepUserIcon : defaultIcon;
        break;
      case Server.SERVER_CONSOLE:
      case Server.RAS_CONSOLE:
      case Server.JOBSCHEDULER:
        this.currentIcon = serviceIcon;
        break;
      default:
        this.currentIcon = sessionInfo.getHibernate() ? sleepUserIcon : defaultIcon;
    }

    computeSecondaryInfo();
    computeExtendedInfoData();
  }

  private void computeSecondaryInfo() {

    if (!sessionInfo.getConnectionId().equals(Helper.EMPTY_UUID)) {
      IInfoBaseConnectionShort connectionInfoShort =
          server.getConnectionInfoShort(clusterId, sessionInfo.getConnectionId());
      connectionNumber =
          Objects.nonNull(connectionInfoShort)
              ? String.valueOf(connectionInfoShort.getConnId())
              : ""; //$NON-NLS-1$
    }

    license =
        sessionInfo.getLicenses().isEmpty()
            ? "" //$NON-NLS-1$
            : sessionInfo.getLicenses().get(0).getFullPresentation();

    if (!sessionInfo.getWorkingProcessId().equals(Helper.EMPTY_UUID)) {
      IWorkingProcessInfo wpInfo =
          server.getWorkingProcessInfo(clusterId, sessionInfo.getWorkingProcessId());
      if (wpInfo != null) {
        wpHostName = wpInfo.getHostName();
        wpPort = Integer.toString(wpInfo.getMainPort());
        wpPid = wpInfo.getPid();
      }
    }

    infobaseName = server.getInfoBaseName(clusterId, sessionInfo.getInfoBaseId());
  }

  protected void computeExtendedInfoData() {

    columnProperties.prepareDataMap(data);

    putData(TITLE_USERNAME, sessionInfo.getUserName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_INFOBASE, infobaseName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_SESSION_N, sessionInfo.getSessionId(), CELL_VALUE_TYPE.INT);
    putData(TITLE_CONNECTION_N, connectionNumber, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_STARTED_AT, sessionInfo.getStartedAt(), CELL_VALUE_TYPE.DATE);
    putData(TITLE_LAST_ACTIVE_AT, sessionInfo.getLastActiveAt(), CELL_VALUE_TYPE.DATE);
    putData(TITLE_COMPUTER, sessionInfo.getHost(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_APPLICATION, getApplicationName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_SERVER, wpHostName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_PORT, wpPort, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_PID, wpPid, CELL_VALUE_TYPE.TEXT);

    putData(TITLE_DB_PROC_INFO, sessionInfo.getDbProcInfo(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_DB_PROC_TOOK, sessionInfo.getDbProcTook(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(TITLE_DB_PROC_TOOK_AT, sessionInfo.getDbProcTookAt(), CELL_VALUE_TYPE.DATE);
    putData(TITLE_BLOCKED_BY_DBMS, sessionInfo.getBlockedByDbms(), CELL_VALUE_TYPE.INT);
    putData(TITLE_BLOCKED_BY_LS, sessionInfo.getBlockedByLs(), CELL_VALUE_TYPE.INT);

    putData(
        TITLE_DURATION_CURRENT_DBMS,
        sessionInfo.getDurationCurrentDbms(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_DURATION_LAST_5_MIN_DBMS,
        sessionInfo.getDurationLast5MinDbms(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_DURATION_ALL_DBMS, sessionInfo.getDurationAllDbms(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);

    putData(
        TITLE_DBMS_BYTES_LAST_5_MIN,
        sessionInfo.getDbmsBytesLast5Min(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_DBMS_BYTES_ALL, sessionInfo.getDbmsBytesAll(), CELL_VALUE_TYPE.LONG_GROUP);

    putData(
        TITLE_DURATION_CURRENT, sessionInfo.getDurationCurrent(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_DURATION_LAST_5_MIN,
        sessionInfo.getDurationLast5Min(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(TITLE_DURATION_ALL, sessionInfo.getDurationAll(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);

    putData(TITLE_CALLS_LAST_5_MIN, sessionInfo.getCallsLast5Min(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_CALLS_ALL, sessionInfo.getCallsAll(), CELL_VALUE_TYPE.INT_GROUP);

    putData(TITLE_BYTES_LAST_5_MIN, sessionInfo.getBytesLast5Min(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_BYTES_ALL, sessionInfo.getBytesAll(), CELL_VALUE_TYPE.LONG_GROUP);

    putData(TITLE_MEMORY_CURRENT, sessionInfo.getMemoryCurrent(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_MEMORY_LAST_5_MIN, sessionInfo.getMemoryLast5Min(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_MEMORY_TOTAL, sessionInfo.getMemoryTotal(), CELL_VALUE_TYPE.LONG_GROUP);

    putData(
        TITLE_READ_BYTES_CURRENT, sessionInfo.getReadBytesCurrent(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_READ_BYTES_LAST_5_MIN,
        sessionInfo.getReadBytesLast5Min(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_READ_BYTES_TOTAL, sessionInfo.getReadBytesTotal(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_WRITE_BYTES_CURRENT, sessionInfo.getWriteBytesCurrent(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_WRITE_BYTES_LAST_5_MIN,
        sessionInfo.getWriteBytesLast5Min(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(TITLE_WRITE_BYTES_TOTAL, sessionInfo.getWriteBytesTotal(), CELL_VALUE_TYPE.LONG_GROUP);

    putData(TITLE_LICENSE, license, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_IS_SLEEP, sessionInfo.getHibernate(), CELL_VALUE_TYPE.BOOLEAN);
    putData(TITLE_SLEEP_AFTER, sessionInfo.getPassiveSessionHibernateTime(), CELL_VALUE_TYPE.INT);
    putData(
        TITLE_KILL_AFTER, sessionInfo.getHibernateSessionTerminationTime(), CELL_VALUE_TYPE.INT);
    putData(TITLE_CLIENT_IP_ADDRESS, getClientIpAddress(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_DATA_SEPARATION, sessionInfo.getDataSeparation(), CELL_VALUE_TYPE.TEXT);

    putData(TITLE_CURRRENT_SERVICE_NAME, sessionInfo.getCurrentServiceName(), CELL_VALUE_TYPE.TEXT);
    putData(
        TITLE_DURATION_CURRENT_SERVICE,
        sessionInfo.getDurationCurrentService(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_DURATION_LAST_5_MIN_SERVICE,
        sessionInfo.getDurationLast5MinService(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_DURATION_ALL_SERVICE,
        sessionInfo.getDurationAllService(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);

    putData(
        TITLE_CPU_TIME_CURRENT, sessionInfo.getCpuTimeCurrent(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(
        TITLE_CPU_TIME_LAST_5_MIN,
        sessionInfo.getCpuTimeLast5Min(),
        CELL_VALUE_TYPE.DECIMAL_3_CHAR);
    putData(TITLE_CPU_TIME_ALL, sessionInfo.getCpuTimeAll(), CELL_VALUE_TYPE.DECIMAL_3_CHAR);
  }

  @Override
  public void addToTable(Table table, int index) {

    TableItem tableItem = createTableItem(table, index, sessionInfo.getStartedAt());

    if (commonConfig.isShadeSleepingSessions() && sessionInfo.getHibernate()) {
      tableItem.setForeground(shadowItemColor);
    }

    if (watchedSessions.contains(generateWatchId())) {
      tableItem.setChecked(true);
      tableItem.setForeground(watchedSessionColor);
    }
  }

  /**
   * Переключение у сеанса режима наблюдения.
   *
   * @param item - строка таблицы - сеанс
   * @param isChecked - значение
   */
  public void switchWatching(TableItem item, Boolean isChecked) {
    String watchId = generateWatchId();
    if (Boolean.TRUE.equals(isChecked)) {
      watchedSessions.add(watchId);
      item.setForeground(watchedSessionColor);
    } else {
      watchedSessions.remove(watchId);
      item.setForeground(standardColor);
    }
  }

  private String generateWatchId() {

    String[] columnsName = columnProperties.getColumnsName();

    String infobase = data.get(columnsName[1]).value;
    String sn = data.get(columnsName[2]).value;

    return infobase.concat("*").concat(sn); //$NON-NLS-1$
  }

  /**
   * Получение SessionInfo.
   *
   * @return sessionInfo
   */
  public ISessionInfo getSessionInfo() {
    return sessionInfo;
  }

  /**
   * Получение номера соединения.
   *
   * @return Номер соединения
   */
  public String getConnectionNumber() {
    return connectionNumber;
  }

  /**
   * Получение лицензии.
   *
   * @return лицензия
   */
  public String getLicense() {
    return license;
  }

  /**
   * Получение имени хоста рабочего процесса.
   *
   * @return имя хоста рабочего процесса
   */
  public String getWorkingProcessHostName() {
    return wpHostName;
  }

  /**
   * Получение порта рабочего процесса.
   *
   * @return порт рабочего процесса
   */
  public String getWorkingProcessPort() {
    return wpPort;
  }

  /**
   * Получение PID рабочего процесса.
   *
   * @return PID рабочего процесса
   */
  public String getWorkingProcessPid() {
    return wpPid;
  }

  /**
   * Получение имени информационной базы.
   *
   * @return имя информационной базы
   */
  public String getInfobaseName() {
    return infobaseName;
  }

  /**
   * Получение IP адреса клиента.
   *
   * @return IP адрес клиента
   */
  public String getClientIpAddress() {
    return sessionInfo.getClientIPAddress() == null
        ? "" //$NON-NLS-1$
        : sessionInfo.getClientIPAddress(); // 8.3.17+
  }

  /**
   * Получение наименования приложения клиента.
   *
   * @return наименование приложения клиента
   */
  public String getApplicationName() {
    return server.getApplicationName(sessionInfo.getAppId());
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {

    columnProperties.addColumnsInMap(
        TITLE_USERNAME,
        TITLE_INFOBASE,
        TITLE_SESSION_N,
        TITLE_CONNECTION_N,
        TITLE_STARTED_AT,
        TITLE_LAST_ACTIVE_AT,
        TITLE_COMPUTER,
        TITLE_APPLICATION,
        TITLE_SERVER,
        TITLE_PORT,
        TITLE_PID,
        TITLE_DB_PROC_INFO,
        TITLE_DB_PROC_TOOK,
        TITLE_DB_PROC_TOOK_AT,
        TITLE_BLOCKED_BY_DBMS,
        TITLE_BLOCKED_BY_LS,
        TITLE_DURATION_CURRENT_DBMS,
        TITLE_DURATION_LAST_5_MIN_DBMS,
        TITLE_DURATION_ALL_DBMS,
        TITLE_DBMS_BYTES_LAST_5_MIN,
        TITLE_DBMS_BYTES_ALL,
        TITLE_DURATION_CURRENT,
        TITLE_DURATION_LAST_5_MIN,
        TITLE_DURATION_ALL,
        TITLE_CALLS_LAST_5_MIN,
        TITLE_CALLS_ALL,
        TITLE_BYTES_LAST_5_MIN,
        TITLE_BYTES_ALL,
        TITLE_MEMORY_CURRENT,
        TITLE_MEMORY_LAST_5_MIN,
        TITLE_MEMORY_TOTAL,
        TITLE_READ_BYTES_CURRENT,
        TITLE_READ_BYTES_LAST_5_MIN,
        TITLE_READ_BYTES_TOTAL,
        TITLE_WRITE_BYTES_CURRENT,
        TITLE_WRITE_BYTES_LAST_5_MIN,
        TITLE_WRITE_BYTES_TOTAL,
        TITLE_LICENSE,
        TITLE_IS_SLEEP,
        TITLE_SLEEP_AFTER,
        TITLE_KILL_AFTER,
        TITLE_CLIENT_IP_ADDRESS,
        TITLE_DATA_SEPARATION,
        TITLE_CURRRENT_SERVICE_NAME,
        TITLE_DURATION_CURRENT_SERVICE,
        TITLE_DURATION_LAST_5_MIN_SERVICE,
        TITLE_DURATION_ALL_SERVICE,
        TITLE_CPU_TIME_CURRENT,
        TITLE_CPU_TIME_LAST_5_MIN,
        TITLE_CPU_TIME_ALL);

    defaultIcon = Helper.getImage(DEFAULT_ICON_FILENAME);
    sleepUserIcon = Helper.getImage(SLEEP_ICON_FILENAME);
    serviceIcon = Helper.getImage(SERVICE_ICON_FILENAME);
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
