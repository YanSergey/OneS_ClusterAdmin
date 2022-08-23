package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import java.util.UUID;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;

/** Расширенная информация для рабочего процесса. */
public class WorkingProcessInfoExtended extends BaseInfoExtended {

  private static final String TITLE_COMPUTER = "SessionInfo.Computer"; //$NON-NLS-1$
  private static final String TITLE_PORT = "SessionInfo.Port"; //$NON-NLS-1$
  private static final String TITLE_USING = "WPInfo.Using"; //$NON-NLS-1$
  private static final String TITLE_ENABLES = "WPInfo.Enabled"; //$NON-NLS-1$
  private static final String TITLE_ACTIVE = "WPInfo.Active"; //$NON-NLS-1$
  private static final String TITLE_PID = "SessionInfo.PID"; //$NON-NLS-1$
  private static final String TITLE_MEMORY = "WPInfo.Memory"; //$NON-NLS-1$
  private static final String TITLE_MEMORY_EXCEEDED = "WPInfo.MemoryExceeded"; //$NON-NLS-1$
  private static final String TITLE_AVAILABLE_PERFORMANCE = "WPInfo.AvailablePerformance"; //$NON-NLS-1$
  private static final String TITLE_LICENSE = "SessionInfo.License"; //$NON-NLS-1$
  private static final String TITLE_STARTED_AT = "SessionInfo.StartedAt"; //$NON-NLS-1$
  private static final String TITLE_CONNECTIONS_COUNT = "WPInfo.ConnectionsCount"; //$NON-NLS-1$
  private static final String TITLE_BACK_CALL_TIME = "WPInfo.BackCallTime"; //$NON-NLS-1$
  private static final String TITLE_SERVER_CALL_TIME = "WPInfo.ServerCallTime"; //$NON-NLS-1$
  private static final String TITLE_DB_CALL_TIME = "WPInfo.DBCallTime"; //$NON-NLS-1$
  private static final String TITLE_CALL_TIME = "WPInfo.CallTime"; //$NON-NLS-1$
  private static final String TITLE_LOCK_CALL_TIME = "WPInfo.LockCallTime"; //$NON-NLS-1$
  private static final String TITLE_CLIENT_THREADS = "WPInfo.ClientThreads"; //$NON-NLS-1$

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(WorkingProcessInfoExtended.class);

  private IWorkingProcessInfo workingProcessInfo;

  private static final String DEFAULT_ICON_FILENAME = "wp.png";
  private static Image defaultIcon;

  private static final String TAB_TEXT_TEMPLATE =
      Messages.getString("TabText.WorkingProcessesCount"); //$NON-NLS-1$

  private static TabItem currentTab;
  private static int itemCount;

  /**
   * Создание расширенной информации для рабочего процесса.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param workingProcess - working process
   */
  public WorkingProcessInfoExtended(
      Server server, UUID clusterId, IWorkingProcessInfo workingProcess) {

    this.server = server;
    this.clusterId = clusterId;
    this.workingProcessInfo = workingProcess;
    this.currentIcon = defaultIcon;

    computeExtendedInfoData();
  }

  protected void computeExtendedInfoData() {

    // license
    final var license =
        workingProcessInfo.getLicense().isEmpty()
            ? "" //$NON-NLS-1$
            : workingProcessInfo.getLicense().get(0).getFullPresentation();

    columnProperties.prepareDataMap(data);

    putData(TITLE_COMPUTER, workingProcessInfo.getHostName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_PORT, workingProcessInfo.getMainPort(), CELL_VALUE_TYPE.INT);
    putData(TITLE_USING, isUse(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_ENABLES, workingProcessInfo.isEnable(), CELL_VALUE_TYPE.BOOLEAN);
    putData(TITLE_ACTIVE, isRunning(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_PID, workingProcessInfo.getPid(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_MEMORY, workingProcessInfo.getMemorySize(), CELL_VALUE_TYPE.INT_GROUP);
    putData(TITLE_MEMORY_EXCEEDED, workingProcessInfo.getMemoryExcessTime(), CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_AVAILABLE_PERFORMANCE,
        workingProcessInfo.getAvailablePerfomance(),
        CELL_VALUE_TYPE.INT_GROUP);
    putData(TITLE_LICENSE, license, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_STARTED_AT, workingProcessInfo.getStartedAt(), CELL_VALUE_TYPE.DATE);
    putData(TITLE_CONNECTIONS_COUNT, workingProcessInfo.getConnections(), CELL_VALUE_TYPE.INT_GROUP);

    putData(
        TITLE_BACK_CALL_TIME, workingProcessInfo.getAvgBackCallTime(), CELL_VALUE_TYPE.DECIMAL_6_CHAR);
    putData(
        TITLE_SERVER_CALL_TIME,
        workingProcessInfo.getAvgServerCallTime(),
        CELL_VALUE_TYPE.DECIMAL_6_CHAR);
    putData(TITLE_DB_CALL_TIME, workingProcessInfo.getAvgDBCallTime(), CELL_VALUE_TYPE.DECIMAL_6_CHAR);
    putData(TITLE_CALL_TIME, workingProcessInfo.getAvgCallTime(), CELL_VALUE_TYPE.DECIMAL_6_CHAR);
    putData(
        TITLE_LOCK_CALL_TIME, workingProcessInfo.getAvgLockCallTime(), CELL_VALUE_TYPE.DECIMAL_6_CHAR);
    putData(TITLE_CLIENT_THREADS, workingProcessInfo.getAvgThreads(), CELL_VALUE_TYPE.DECIMAL_6_CHAR);
  }

  @Override
  public void addToTable(Table table, int index) {
    createTableItem(table, index, workingProcessInfo.getStartedAt());
  }

  private String isUse() {

    String isUse;
    switch (workingProcessInfo.getUse()) {
      case 1:
        isUse = Messages.getString("WPInfo.Used"); //$NON-NLS-1$
        break;
      case 2:
        isUse = Messages.getString("WPInfo.UsedAsReserve"); //$NON-NLS-1$
        break;
      case 0:
      default:
        isUse = Messages.getString("WPInfo.NotUsed"); //$NON-NLS-1$
        break;
    }
    return isUse;
  }

  private String isRunning() {

    String isRunning;
    switch (workingProcessInfo.getRunning()) {
      case 1:
        isRunning = Messages.getString("WPInfo.ProcessIsRunning"); //$NON-NLS-1$
        break;
      case 0:
      default:
        isRunning = Messages.getString("WPInfo.ProcessIsStopped"); //$NON-NLS-1$
        break;
    }
    return isRunning;
  }

  /**
   * Get WorkingProcessInfo.
   *
   * @return the workingProcessInfo
   */
  public IWorkingProcessInfo getWorkingProcessInfo() {
    return workingProcessInfo;
  }

  /**
   * Получение строки заголовка узла рабочих процессов.
   *
   * @param count - количество элементов
   * @return строку с текстом заголовка
   */
  public static String getNodeTitle(int count) {
    return String.format(TAB_TEXT_TEMPLATE, count);
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {

    columnProperties.addColumnsInMap(
        TITLE_COMPUTER,
        TITLE_PORT,
        TITLE_USING,
        TITLE_ENABLES,
        TITLE_ACTIVE,
        TITLE_PID,
        TITLE_MEMORY,
        TITLE_MEMORY_EXCEEDED,
        TITLE_AVAILABLE_PERFORMANCE,
        TITLE_LICENSE,
        TITLE_STARTED_AT,
        TITLE_CONNECTIONS_COUNT,
        TITLE_BACK_CALL_TIME,
        TITLE_SERVER_CALL_TIME,
        TITLE_DB_CALL_TIME,
        TITLE_CALL_TIME,
        TITLE_LOCK_CALL_TIME,
        TITLE_CLIENT_THREADS);

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
