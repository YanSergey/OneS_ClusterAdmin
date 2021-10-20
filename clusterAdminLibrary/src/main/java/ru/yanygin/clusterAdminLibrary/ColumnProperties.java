package ru.yanygin.clusterAdminLibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/** Column properties. */
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

  /**
   * Get column order.
   *
   * @return column order
   */
  public int[] getOrder() {
    return order;
  }

  /**
   * Set column order.
   *
   * @param order - new column order
   */
  public void setOrder(int[] order) {
    this.order = order;
  }

  /**
   * Get column width.
   *
   * @return column width
   */
  public int[] getWidth() {
    return width;
  }

  /**
   * Set column width.
   *
   * @param width - new column width
   */
  public void setWidth(int index, int width) {
    this.width[index] = width;
  }

  /**
   * Get column visible.
   *
   * @return column visible
   */
  public boolean[] getVisible() {
    return visible;
  }

  /**
   * Set column visible.
   *
   * @param visible - new column visible
   */
  public void setVisible(boolean[] visible) {
    this.visible = visible;
  }

  /**
   * Column properties.
   *
   * @param size - column count
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
   * Update column count by the current count.
   *
   * @param arraySize - current colimn count
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
