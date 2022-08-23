package ru.yanygin.clusterAdminLibrary;

import java.text.Collator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibrary.CellValue.CELL_VALUE_TYPE;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

/** Базовый класс для расширенной информации. */
public abstract class BaseInfoExtended implements Comparable<BaseInfoExtended> {
  private static final Logger LOGGER = LoggerFactory.getLogger("clusterAdminLibrary"); //$NON-NLS-1$

  protected static final Color newItemColor = new Color(0, 200, 0);
  protected static final Color shadowItemColor = new Color(160, 160, 160);
  protected static final Color watchedSessionColor = new Color(0, 128, 255);
  protected static final Color standardColor = new Color(0, 0, 0);

  protected Server server;
  protected UUID clusterId;

  protected Map<String, CellValue> data = new LinkedHashMap<>();

  protected Image currentIcon;

  /**
   * Добавление этого экземпляра в таблицу.
   *
   * @param table - Таблица
   * @param index - индекс для вставки
   */
  public abstract void addToTable(Table table, int index);

  /**
   * Добавление этого экземпляра в таблицу на последнюю позицию.
   *
   * @param table - Таблица
   */
  public void addToTable(Table table) {
    try {
      addToTable(table, -1);
    } catch (Exception excp) {
      LOGGER.error("Error:", excp); //$NON-NLS-1$
    }
  }

  /**
   * Получение сервера.
   *
   * @return Сервер
   */
  public Server getServer() {
    return server;
  }

  /**
   * Получение Cluster ID.
   *
   * @return the clusterId
   */
  public UUID getClusterId() {
    return clusterId;
  }

  /**
   * Получение расширенной информации.
   *
   * @return the sessionData1
   */
  public Map<String, CellValue> getExtendedData() {
    // TODO не используется
    return data;
  }

  /**
   * Получение расширенной информации в виде массива строк.
   *
   * @return расширенная информация в виде массива строк
   */
  public String[] getExtendedInfo() {

    ColumnProperties columnProperties = Config.currentConfig.getColumnsProperties(this.getClass());

    String[] columnsName = columnProperties.getColumnsName();

    String[] value = new String[columnsName.length];
    int index = 0;
    for (String columnName : columnsName) {
      try {
        value[index] = data.get(columnName).value;
      } catch (Exception e) {
        value[index] = "";
      }
      index++;
    }
    return value;
  }

  /**
   * Convert UUID to string.
   *
   * @param uuid - UUID
   * @return string
   */
  public String convertUuidToString(UUID uuid) {
    // TODO не используется
    return uuid.equals(Helper.EMPTY_UUID) ? "" : uuid.toString(); //$NON-NLS-1$
  }

  @Override
  public int compareTo(BaseInfoExtended o) {

    ColumnProperties columnProperties = Config.currentConfig.getColumnsProperties(this.getClass());

    int sortColumn = columnProperties.getSortColumn();
    int sortColumnDirection = columnProperties.getSortDirectionSwt();

    if (sortColumn < 0) {
      return 0;
    }

    String sortColumnName = (String) data.keySet().toArray()[sortColumn];
    Object left;
    Object right;
    switch (sortColumnDirection) {
      case SWT.UP:
        left = this.data.get(sortColumnName).originalValue;
        right = o.data.get(sortColumnName).originalValue;
        break;

      case SWT.DOWN:
        left = o.data.get(sortColumnName).originalValue;
        right = this.data.get(sortColumnName).originalValue;
        break;

      case SWT.NONE:
      default:
        return 0;
    }

    int compareResult = 0;
    try {
      switch (this.data.get(sortColumnName).type) {
        case INT:
        case SECONDS_INT:
        case INT_GROUP:
        case DECIMAL_3_CHAR:
          // compareResult = Integer.compare((int) left, (int) right);
          if (left instanceof Integer) {
            compareResult = Integer.compare((int) left, (int) right);
          } else if (left instanceof Long) {
            compareResult = Long.compare((long) left, (long) right);
          }

          break;

        case SECONDS_LONG:
        case LONG_GROUP:
          compareResult = Long.compare((long) left, (long) right);
          break;

        case DECIMAL_6_CHAR:
          compareResult = Double.compare((double) left, (double) right);
          break;

        case DATE:
        case TEXT:
        default:
          compareResult =
              Collator.getInstance(Locale.getDefault())
                  .compare(String.valueOf(left), String.valueOf(right));
      }
    } catch (NumberFormatException excp) {
      compareResult =
          Collator.getInstance(Locale.getDefault())
              .compare(String.valueOf(left), String.valueOf(right));
    }

    return compareResult;
  }

  protected TableItem createTableItem(Table table, int index, Date startDateItem) {
    TableItem item = null;
    if (index == -1) {
      item = new TableItem(table, SWT.NONE);
    } else {
      item = new TableItem(table, SWT.NONE, index);
    }

    item.setText(getExtendedInfo());
    item.setData(ViewerArea.EXTENDED_INFO, this);
    item.setImage(currentIcon);
    item.setChecked(false);

    highlightNewItem(item, startDateItem);

    return item;
  }

  /**
   * Устанавливает подсветку нового эелемента при включенной настройке.
   *
   * @param item - строка таблицы
   * @param startDateItem - дата старта
   */
  private void highlightNewItem(TableItem item, Date startDateItem) {
    if (startDateItem == null) {
      return;
    }

    Config commonConfig = Config.currentConfig;
    if (commonConfig.isHighlightNewItems()
        && (new Date().getTime() - startDateItem.getTime()
            < commonConfig.getHighlightNewItemsDuration() * 1000)) {
      item.setForeground(newItemColor);
    }
  }

  protected abstract void computeExtendedInfoData();

  protected void putData(String title, Object value, CELL_VALUE_TYPE dataType) {

    CellValue column = new CellValue(title, title, value, dataType);
    data.put(title, column);
  }

  /** Инициализация имен колонок всех подклассов. */
  public static void init() {

    SessionInfoExtended.initColumnsName();
    ConnectionInfoExtended.initColumnsName();
    LockInfoExtended.initColumnsName();
    WorkingProcessInfoExtended.initColumnsName();
    WorkingServerInfoExtended.initColumnsName();
  }

  /**
   * Обновление заголовка вкладки с количеством элементов.
   *
   * @param clazz - класс-наследник BaseInfoExtended
   * @param count - количество элементов
   */
  public static void updateTabText(Class<? extends BaseInfoExtended> clazz, int count) {
    resetTabsTextCount();

    if (clazz == SessionInfoExtended.class) {
      SessionInfoExtended.updateTabText(count);
    } else if (clazz == ConnectionInfoExtended.class) {
      ConnectionInfoExtended.updateTabText(count);
    } else if (clazz == LockInfoExtended.class) {
      LockInfoExtended.updateTabText(count);
    } else if (clazz == WorkingProcessInfoExtended.class) {
      WorkingProcessInfoExtended.updateTabText(count);
    } else if (clazz == WorkingServerInfoExtended.class) {
      WorkingServerInfoExtended.updateTabText(count);
    }
  }

  /** Сброс заголовков вкладок на неизвестное количество элементов. */
  public static void resetTabsTextCount() {
    SessionInfoExtended.resetTabTextCount();
    ConnectionInfoExtended.resetTabTextCount();
    LockInfoExtended.resetTabTextCount();
    WorkingProcessInfoExtended.resetTabTextCount();
    WorkingServerInfoExtended.resetTabTextCount();
  }

  /**
   * Установка связи таблицы-списка со вкладкой.
   *
   * @param clazz - класс-наследник BaseInfoExtended
   * @param tabitem - вкладка, на которой находится таблица
   */
  public static void linkTabItem(Class<? extends BaseInfoExtended> clazz, TabItem tabitem) {
    if (clazz == SessionInfoExtended.class) {
      SessionInfoExtended.linkTabItem(tabitem);
    } else if (clazz == ConnectionInfoExtended.class) {
      ConnectionInfoExtended.linkTabItem(tabitem);
    } else if (clazz == LockInfoExtended.class) {
      LockInfoExtended.linkTabItem(tabitem);
    } else if (clazz == WorkingProcessInfoExtended.class) {
      WorkingProcessInfoExtended.linkTabItem(tabitem);
    } else if (clazz == WorkingServerInfoExtended.class) {
      WorkingServerInfoExtended.linkTabItem(tabitem);
    }
  }
}
