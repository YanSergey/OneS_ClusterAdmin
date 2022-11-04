package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import java.util.UUID;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;

/** Расширенная информация для соединения. */
public class ConnectionInfoExtended extends BaseInfoExtended {

  private static final String TITLE_INFOBASE = "SessionInfo.Infobase"; //$NON-NLS-1$
  private static final String TITLE_CONNECTION = "ConnectionInfo.Connection"; //$NON-NLS-1$
  private static final String TITLE_SESSION = "ConnectionInfo.Session"; //$NON-NLS-1$
  private static final String TITLE_COMPUTER = "SessionInfo.Computer"; //$NON-NLS-1$
  private static final String TITLE_APPLICATION = "SessionInfo.Application"; //$NON-NLS-1$
  private static final String TITLE_SERVER = "SessionInfo.Server"; //$NON-NLS-1$
  private static final String TITLE_RP_HOST_PORT = "ConnectionInfo.RpHostPort"; //$NON-NLS-1$
  private static final String TITLE_CONNECTED_AT = "ConnectionInfo.ConnectedAt"; //$NON-NLS-1$

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(ConnectionInfoExtended.class);

  private static final String DEFAULT_ICON_FILENAME = "connection.png";
  private static Image defaultIcon;

  private static final String TAB_TEXT_TEMPLATE =
      Messages.getString("TabText.ConnectionsCount"); //$NON-NLS-1$

  private static TabItem currentTab;
  private static int itemCount;

  private IInfoBaseConnectionShort connectionInfo;

  /**
   * Создание расширенной информации для соединения.
   *
   * @param server - server
   * @param clusterId - cluster ID
   * @param connectionInfo - connection info
   */
  public ConnectionInfoExtended(
      Server server, UUID clusterId, IInfoBaseConnectionShort connectionInfo) {

    this.server = server;
    this.clusterId = clusterId;
    this.connectionInfo = connectionInfo;
    this.currentIcon = defaultIcon;

    computeExtendedInfoData();
  }

  protected void computeExtendedInfoData() {

    String infobaseName = ""; //$NON-NLS-1$
    String wpHostName = ""; //$NON-NLS-1$
    String wpPort = ""; //$NON-NLS-1$

    if (!connectionInfo.getInfoBaseId().equals(Helper.EMPTY_UUID)) {
      infobaseName = server.getInfoBaseName(clusterId, connectionInfo.getInfoBaseId());
    }

    IWorkingProcessInfo workingProcess =
        server.getWorkingProcessInfo(clusterId, connectionInfo.getWorkingProcessId());

    if (workingProcess != null) {
      wpHostName = workingProcess.getHostName();
      wpPort = Integer.toString(workingProcess.getMainPort());
    }

    columnProperties.prepareDataMap(data);

    putData(TITLE_INFOBASE, infobaseName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_CONNECTION, connectionInfo.getConnId(), CELL_VALUE_TYPE.INT);
    putData(TITLE_SESSION, connectionInfo.getSessionNumber(), CELL_VALUE_TYPE.INT);
    putData(TITLE_COMPUTER, connectionInfo.getHost(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_APPLICATION, getApplicationName(), CELL_VALUE_TYPE.TEXT);
    putData(TITLE_SERVER, wpHostName, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_RP_HOST_PORT, wpPort, CELL_VALUE_TYPE.TEXT);
    putData(TITLE_CONNECTED_AT, connectionInfo.getConnectedAt(), CELL_VALUE_TYPE.DATE);

  }

  @Override
  public void addToTable(Table table, int index) {
    createTableItem(table, index, connectionInfo.getConnectedAt());
  }

  /**
   * Получение ConnectionInfo.
   *
   * @return the ConnectionInfo
   */
  public IInfoBaseConnectionShort getConnectionInfo() {
    return connectionInfo;
  }

  private String getApplicationName() {
    return server.getApplicationName(connectionInfo.getApplication());
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {

    columnProperties.addColumnsInMap(
        TITLE_INFOBASE,
        TITLE_CONNECTION,
        TITLE_SESSION,
        TITLE_COMPUTER,
        TITLE_APPLICATION,
        TITLE_SERVER,
        TITLE_RP_HOST_PORT,
        TITLE_CONNECTED_AT);

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
