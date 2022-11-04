package ru.yanygin.clusterAdminLibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.swt.SWT;

/** Свойства столбцов. */
public class ColumnProperties {

  @SerializedName("Order")
  @Expose
  private int[] order = null;

  @SerializedName("Width")
  @Expose
  private int[] width = null;

  @SerializedName("Visible")
  @Expose
  private boolean[] visible = null;

  @SerializedName("SortColumn")
  @Expose
  private int sortColumn = -1; // -1 = отсутствие сортировки

  @SerializedName("RowSortDirection")
  @Expose
  private RowSortDirection rowSortDirection = RowSortDirection.DISABLE;

  private Map<String, String> columnsMap = new LinkedHashMap<>();

  /** Направление сортировки строк. */
  public enum RowSortDirection {
    /** Выключено/Как у предыдущего столбца. */
    DISABLE,
    /** По возрастанию. */
    ASC,
    /** По убыванию. */
    DESC
  }

  /** Конструктор по-умолчанию. */
  public ColumnProperties() {
    // нужен для инициализации полей не найденных в конфиге при десериализации
  }

  /** Инициализация параметров после десериализации. */
  public void init() {
    // TODO deprecated?
    // При чтении конфиг-файла отсутствующие поля, инициализируются значением null
    if (this.rowSortDirection == null) {
      this.rowSortDirection = RowSortDirection.DISABLE;
    }
  }

  /**
   * Получить порядок столбцов.
   *
   * @return порядок столбцов
   */
  public int[] getOrder() {
    return order;
  }

  /**
   * Установить порядок столбцов.
   *
   * @param order - новый порядок столбцов
   */
  public void setOrder(int[] order) {
    this.order = order;
  }

  /**
   * Получить ширину столбцов.
   *
   * @return ширина столбцов
   */
  public int[] getWidth() {
    return width;
  }

  /**
   * Установить ширину столбца.
   *
   * @param index - номер столбца
   * @param width - новая ширина столбца
   */
  public void setWidth(int index, int width) {
    this.width[index] = width;
  }

  /**
   * Получить видимость столбцов.
   *
   * @return видимость столбцов
   */
  public boolean[] getVisible() {
    return visible;
  }

  /**
   * Установить видимость столбца.
   *
   * @param visible - новое значение видимости столбца
   */
  public void setVisible(boolean[] visible) {
    this.visible = visible;
  }

  /**
   * Получить порядок столбцов.
   *
   * @return орядок столбцов
   */
  public int getSortColumn() {
    return sortColumn;
  }

  /**
   * Выполнить сортировку столбца.
   *
   * @param sortColumn - номер столбца
   */
  public void setSortColumn(int sortColumn) {

    RowSortDirection defSortDir = Config.currentConfig.getRowSortDirection();

    if (this.sortColumn == sortColumn) { // колонка не меняется - переключаем направление

      switch (this.rowSortDirection) {
        case ASC:
          this.rowSortDirection = RowSortDirection.DESC;
          break;
        case DESC:
          this.rowSortDirection = RowSortDirection.ASC;
          break;

        default:
          this.rowSortDirection =
              (defSortDir == RowSortDirection.DISABLE) ? RowSortDirection.DESC : defSortDir;
          break;
      }

    } else { // колонка меняется - ставим направление из настроек

      if (defSortDir == RowSortDirection.ASC || defSortDir == RowSortDirection.DESC) {
        this.rowSortDirection = defSortDir;
      } else if (defSortDir == RowSortDirection.DISABLE
          && this.rowSortDirection == RowSortDirection.DISABLE) {
        this.rowSortDirection = RowSortDirection.DESC;
      }

      // TODO (нужен еще коммент или уже сделал?)
      // если по-умолчанию = как предыдущее - то менять не нужно
      //    (но надо учесть, что предыдущее может быть не ASC и не DESC), тогда ставим DESC
      // если по-умолчанию = ASC или DESC - то ставим его принудительно

    }

    this.sortColumn = sortColumn;
  }

  /**
   * Получить направление сортировки строк.
   *
   * @return направление сортировки
   */
  public RowSortDirection getSortDirection() {
    return this.rowSortDirection;
  }

  /**
   * Получить направление сортировки строк в SWT значениях.
   *
   * @return направление сортировки
   */
  public int getSortDirectionSwt() {
    int sortDirectionSwt = 0;
    switch (this.rowSortDirection) {
      case ASC:
        sortDirectionSwt = SWT.UP;
        break;

      case DESC:
        sortDirectionSwt = SWT.DOWN;
        break;

      case DISABLE:
      default:
        sortDirectionSwt = SWT.NONE;
        break;
    }

    return sortDirectionSwt;
  }

  /**
   * Установить направление сортировки строк.
   *
   * @param sortDirectionSwt направление сортировки строк в SWT значениях
   */
  public void setSortDirection(int sortDirectionSwt) {

    switch (sortDirectionSwt) {
      case SWT.UP:
        this.rowSortDirection = RowSortDirection.ASC;
        break;
        
      case SWT.DOWN:
        this.rowSortDirection = RowSortDirection.DESC;
        break;

      case SWT.NONE:
      default:
        this.rowSortDirection = RowSortDirection.DISABLE;
        break;
    }

  }

  /**
   * Get Columns Map.
   *
   * @return ColumnsMap
   */
  //  public Map<String, String> getColumnsMap() {
  //    return columnsMap;
  //  }

  /**
   * Add column.
   *
   * @param columnName - column name
   */
  //  public void addColumnInMap(String columnName) {
  //    if (columnsMap == null) {
  //      columnsMap = new LinkedHashMap<>();
  //    }
  //    columnsMap.put(columnName, ""); //$NON-NLS-1$
  //  }

  /**
   * Добавить столбцы.
   *
   * @param columnsName - имена столбцов
   */
  public void addColumnsInMap(String... columnsName) {
    for (int i = 0; i < columnsName.length; i++) {
      columnsMap.put(columnsName[i], ""); //$NON-NLS-1$
    }
    updateColumnProperties(columnsName.length);
  }

  /**
   * Подготовка map перед заполнением значениями (устанавливает порядок столбцов).
   *
   * @param data - будущая map со значениями строк
   */
  public void prepareDataMap(Map<String, CellValue> data) {
    columnsMap.forEach((k, v) -> data.put(k, null));
  }

  /**
   * Получение имен столбцов.
   *
   * @return String[] массив имен столбцов
   */
  public String[] getColumnsName() {
    return columnsMap.keySet().toArray(new String[0]);
  }

  /**
   * Получение имен столбцов для пользователей.
   *
   * @return String[] массив имен столбцов
   */
  public String[] getColumnsDescription() {

    String[] str = columnsMap.keySet().toArray(new String[0]);

    String[] names = new String[str.length];
    for (int i = 0; i < str.length; i++) {
      names[i] = Messages.getString(str[i]);
    }

    return names;
  }

  /**
   * Конструктор, инициализирует свойства столбцов.
   *
   * @param size - количество колонок
   */
  public ColumnProperties(int size) {

    // Порядок столбцов
    this.order = new int[size];
    for (int i = 0; i < this.order.length; i++) {
      this.order[i] = i;
    }

    // Ширина столбцов
    this.width = new int[size];

    // Видимость столбцов
    this.visible = new boolean[size];
    for (int i = 0; i < this.visible.length; i++) {
      this.visible[i] = true;
    }
  }

  /**
   * Обновление текущего количества столбцов.
   *
   * @param arraySize - текущее количество столбцов
   */
  public void updateColumnProperties(int arraySize) {

    // если после десериализации количество не равно текущему
    // (например, поменялся состав колонок),
    // нужно переложить в новый массив без потерь

    // Порядок столбцов
    if (order.length != arraySize) {
      int[] columnOrderTemp = order;
      order = new int[arraySize];
      System.arraycopy(columnOrderTemp, 0, order, 0, Math.min(arraySize, columnOrderTemp.length));

      if (arraySize > columnOrderTemp.length) {
        for (int i = columnOrderTemp.length; i < arraySize; i++) {
          order[i] = i;
        }
      }
    }

    // Ширина столбцов
    if (width.length != arraySize) {
      int[] columnWidthTemp = width;
      width = new int[arraySize];
      System.arraycopy(columnWidthTemp, 0, width, 0, Math.min(arraySize, columnWidthTemp.length));
    }

    // Видимость столбцов
    if (visible.length != arraySize) {
      boolean[] columnVisibleTemp = visible;
      visible = new boolean[arraySize];
      System.arraycopy(
          columnVisibleTemp, 0, visible, 0, Math.min(arraySize, columnVisibleTemp.length));
      if (arraySize > columnVisibleTemp.length) {
        for (int i = columnVisibleTemp.length; i < arraySize; i++) {
          visible[i] = true;
        }
      }
    }
  }
}
