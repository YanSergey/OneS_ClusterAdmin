package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IPortRangeInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;
import java.util.UUID;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;

/** Расширенная информация для рабочего сервера. */
public class WorkingServerInfoExtended extends BaseInfoExtended {

  private static final String TITLE_DESCRIPTION = "InfoTables.Description"; //$NON-NLS-1$
  private static final String TITLE_COMPUTER = "SessionInfo.Computer"; //$NON-NLS-1$
  private static final String TITLE_IP_PORT = "WSInfo.IPPort"; //$NON-NLS-1$
  private static final String TITLE_RANGE_IP_PORTS = "WSInfo.RangeIPPorts"; //$NON-NLS-1$
  private static final String TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT = "WSInfo.SafeWorkingProcessesMemoryLimit"; //$NON-NLS-1$
  private static final String TITLE_SAFE_CALL_MEMORY_LIMIT = "WSInfo.SafeCallMemoryLimit"; //$NON-NLS-1$
  private static final String TITLE_WORKING_PROCESS_MEMORY_LIMIT = "WSInfo.WorkingProcessMemoryLimit"; //$NON-NLS-1$
  private static final String TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY = "WSInfo.CriticalProcessesTotalMemory"; //$NON-NLS-1$
  private static final String TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY = "WSInfo.TemporaryAllowedProcessesTotalMemory"; //$NON-NLS-1$
  private static final String TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT = "WSInfo.TemporaryAllowedProcessesTotalMemoryTimeLimit"; //$NON-NLS-1$
  private static final String TITLE_IB_PER_PROCESS_LIMIT = "WSInfo.IBPerProcessLimit"; //$NON-NLS-1$
  private static final String TITLE_CONN_PER_PROCESS_LIMIT = "WSInfo.ConnPerProcessLimit"; //$NON-NLS-1$
  private static final String TITLE_IP_PORT_MAIN_MANAGER = "WSInfo.IPPortMainManager"; //$NON-NLS-1$
  private static final String TITLE_DEDICATED_MANAGERS = "WSInfo.DedicatedManagers"; //$NON-NLS-1$
  private static final String TITLE_MAIN_SERVER = "WSInfo.MainServer"; //$NON-NLS-1$

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(WorkingServerInfoExtended.class);

  private static final String DEFAULT_ICON_FILENAME = "working_server_24.png";
  private static Image defaultIcon;

  private static final String TAB_TEXT_TEMPLATE =
      Messages.getString("TabText.WorkingServersCount"); //$NON-NLS-1$

  private static TabItem currentTab;
  private static int itemCount;

  IWorkingServerInfo workingServerInfo;

  /**
   * Создание расширенной информации для рабочего сервера.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param workingServer - working server info
   */
  public WorkingServerInfoExtended(
      Server server, UUID clusterId, IWorkingServerInfo workingServer) {

    this.server = server;
    this.clusterId = clusterId;
    this.workingServerInfo = workingServer;
    this.currentIcon = defaultIcon;

    computeExtendedInfoData();
  }

  protected void computeExtendedInfoData() {

    columnProperties.prepareDataMap(data);

    putData(TITLE_DESCRIPTION, workingServerInfo.getName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_COMPUTER, workingServerInfo.getHostName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_IP_PORT, workingServerInfo.getMainPort(), CELL_VALUE_TYPE.INT);
    putData(TITLE_RANGE_IP_PORTS, getPortRange(), CELL_VALUE_TYPE.TEXT);
    putData(
        TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT,
        workingServerInfo.getSafeWorkingProcessesMemoryLimit(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_SAFE_CALL_MEMORY_LIMIT,
        workingServerInfo.getSafeCallMemoryLimit(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_WORKING_PROCESS_MEMORY_LIMIT,
        workingServerInfo.getWorkingProcessMemoryLimit(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY,
        workingServerInfo.getCriticalProcessesTotalMemory(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY,
        workingServerInfo.getTemporaryAllowedProcessesTotalMemory(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT,
        workingServerInfo.getTemporaryAllowedProcessesTotalMemoryTimeLimit(),
        CELL_VALUE_TYPE.LONG_GROUP);
    putData(
        TITLE_IB_PER_PROCESS_LIMIT,
        workingServerInfo.getInfoBasesPerWorkingProcessLimit(),
        CELL_VALUE_TYPE.INT);
    putData(
        TITLE_CONN_PER_PROCESS_LIMIT,
        workingServerInfo.getConnectionsPerWorkingProcessLimit(),
        CELL_VALUE_TYPE.INT);
    putData(TITLE_IP_PORT_MAIN_MANAGER, workingServerInfo.getClusterMainPort(), CELL_VALUE_TYPE.INT);
    putData(TITLE_DEDICATED_MANAGERS, workingServerInfo.isDedicatedManagers(), CELL_VALUE_TYPE.BOOLEAN);
    putData(TITLE_MAIN_SERVER, workingServerInfo.isMainServer(), CELL_VALUE_TYPE.BOOLEAN);
  }

  @Override
  public void addToTable(Table table, int index) {
    createTableItem(table, index, null);
  }

  private String getPortRange() {
    IPortRangeInfo portRangesInfo = workingServerInfo.getPortRanges().get(0);
    return Integer.toString(portRangesInfo.getLowBound())
        .concat(":") //$NON-NLS-1$
        .concat(Integer.toString(portRangesInfo.getHighBound()));
  }

  /**
   * Получение строки заголовка узла рабочих серверов.
   *
   * @param count - количество элементов
   * @return строку с текстом заголовка
   */
  public static String getNodeTitle(int count) {
    return String.format(TAB_TEXT_TEMPLATE, count);
  }

  /**
   * Получение UUID рабочего сервера.
   *
   * @return UUID рабочего сервера
   */
  public UUID getWorkingServerId() {
    return workingServerInfo.getWorkingServerId();
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {

    columnProperties.addColumnsInMap(
        TITLE_DESCRIPTION,
        TITLE_COMPUTER,
        TITLE_IP_PORT,
        TITLE_RANGE_IP_PORTS,
        TITLE_SAFE_WORKING_PROCESSES_MEMORY_LIMIT,
        TITLE_SAFE_CALL_MEMORY_LIMIT,
        TITLE_WORKING_PROCESS_MEMORY_LIMIT,
        TITLE_CRITICAL_PROCESSES_TOTAL_MEMORY,
        TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY,
        TITLE_TEMP_ALLOWED_PROC_TOTAL_MEMORY_TIME_LIMIT,
        TITLE_IB_PER_PROCESS_LIMIT,
        TITLE_CONN_PER_PROCESS_LIMIT,
        TITLE_IP_PORT_MAIN_MANAGER,
        TITLE_DEDICATED_MANAGERS,
        TITLE_MAIN_SERVER);

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
