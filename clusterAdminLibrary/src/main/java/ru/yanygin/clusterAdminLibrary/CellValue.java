package ru.yanygin.clusterAdminLibrary;

import java.text.DecimalFormat;
import java.util.Date;

/** Value of cell from lists. */
public class CellValue {

  public enum CELL_VALUE_TYPE {
    TEXT,
    INT,
    BOOLEAN,
    DATE,
    SECONDS_INT,
    SECONDS_LONG,
    INT_GROUP,
    LONG_GROUP,
    DECIMAL_3_CHAR,
    DECIMAL_6_CHAR
  }

  public String name;
  public String descr;
  public String value;
  public Object originalValue;
  public CELL_VALUE_TYPE type;

  DecimalFormat decimalFormatDouble3 = new DecimalFormat("###,##0.000"); //$NON-NLS-1$
  DecimalFormat decimalFormatDouble6 = new DecimalFormat("#,##0.000000"); //$NON-NLS-1$
  DecimalFormat decimalFormatLong = new DecimalFormat("###,###"); //$NON-NLS-1$

  // NumberFormat fmt = NumberFormat.getInstance();
  // fmt.setGroupingUsed(false);
  // fmt.setMaximumIntegerDigits(999);
  // fmt.setMaximumFractionDigits(999);

  /**
   * Create cell value class.
   *
   * @param name - Name of column
   * @param descr - Description of column
   * @param value - Value of cell
   * @param type - Type value of cell
   */
  public CellValue(String name, String descr, Object value, CELL_VALUE_TYPE type) {
    this.name = name;
    this.descr = descr;
    this.originalValue = value;
    this.type = type;

    switch (type) {
      case INT:
        this.value = Integer.toString((int) value);
        break;

      case BOOLEAN:
        this.value = Boolean.toString((boolean) value);
        break;

      case DATE:
        this.value = Helper.dateToString((Date) value);
        break;

      case SECONDS_INT:
        this.value = double3ToString((int) value);
        //          double d = Double.valueOf((int) value);
        //          this.value = millisecondToString((int) value);
        break;

      case SECONDS_LONG:
        this.value = double3ToString((long) value);
        //          this.value = millisecondToString((long) value);
        break;

      case INT_GROUP:
        this.value = longToStringGroup((int) value);
        break;

      case LONG_GROUP:
        this.value = longToStringGroup((long) value);
        break;

      case DECIMAL_3_CHAR:
        if (value instanceof Integer) {
          this.value = double3ToString((int) value);
        } else if (value instanceof Long) {
          this.value = double3ToString((long) value);
        }
        break;

      case DECIMAL_6_CHAR:
        this.value = double6ToString((double) value);
        break;

      case TEXT:
      default:
        this.value = (String) value;
    }
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  private String double3ToString(double value) {
    return decimalFormatDouble3.format(value / 1000);
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  private String double6ToString(double value) {
    return decimalFormatDouble6.format(value);
  }

  /**
   * Cast long value to string.
   *
   * @param value - long value
   * @return long value to string
   */
  private String longToStringGroup(long value) {
    return decimalFormatLong.format(value);
  }

  /**
   * Cast millisecond to string.
   *
   * @param value - millisecond
   * @return millisecond to string
   */
  private String millisecondToString(int value) {
    return Double.toString(((double) value) / 1000);
  }

  /**
   * Cast millisecond to string.
   *
   * @param value - millisecond
   * @return millisecond to string
   */
  private String millisecondToString(long value) {
    return Double.toString(((double) value) / 1000);
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  private String doubleToString1(double value) {
    return String.format("%.9f", value);
  }
}
