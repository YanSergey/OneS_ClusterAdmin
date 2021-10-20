package ru.yanygin.clusterAdminLibrary;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/** Extend info. */
public interface IInfoExtended {

  UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$
  DecimalFormat dFLong = new DecimalFormat("###,###"); //$NON-NLS-1$
  DecimalFormat dFDouble6 = new DecimalFormat("#,##0.000000"); //$NON-NLS-1$
  DecimalFormat dFDouble3 = new DecimalFormat("###,##0.000"); //$NON-NLS-1$

  // NumberFormat fmt = NumberFormat.getInstance();
  // fmt.setGroupingUsed(false);
  // fmt.setMaximumIntegerDigits(999);
  // fmt.setMaximumFractionDigits(999);

  /**
   * Init columns name.
   *
   * @param columnsMap - sample map with columns name
   */
  public static void initColumnsName(Map<String, String> columnsMap) {}

  /**
   * Check highlight for new item in list.
   *
   * @param startDateItem - date of start
   * @return true if need highlight
   */
  public static boolean highlightItem(Date startDateItem) {

    Config commonConfig = ClusterProvider.getCommonConfig();
    return commonConfig.isHighlightNewItems()
        && (new Date().getTime() - startDateItem.getTime()
            < commonConfig.getHighlightNewItemsDuration() * 1000);
  }

  /**
   * Get extended info.
   *
   * @return extended info
   */
  public String[] getExtendedInfo();

  /**
   * Convert UUID to string.
   *
   * @param uuid - UUID
   * @return string
   */
  default String convertUuidToString(UUID uuid) {
    return uuid.equals(emptyUuid) ? "" : uuid.toString(); //$NON-NLS-1$
  }

  /**
   * Cast date to string.
   *
   * @param date - date
   * @return date format to string
   */
  default String dateToString(Date date) {

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
    Date emptyDate = new Date(0);

    return date.equals(emptyDate) ? "" : dateFormat.format(date); //$NON-NLS-1$
  }

  /**
   * Cast millisecond to string.
   *
   * @param value - millisecond
   * @return millisecond to string
   */
  default String millisecondToString(int value) {
    return Double.toString(((double) value) / 1000);
  }

  /**
   * Cast millisecond to string.
   *
   * @param value - millisecond
   * @return millisecond to string
   */
  default String millisecondToString(long value) {
    return Double.toString(((double) value) / 1000);
  }

  /**
   * Cast long value to string.
   *
   * @param value - long value
   * @return long value to string
   */
  default String longToStringGroup(long value) {
    return dFLong.format(value);
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  default String double6ToString(double value) {
    return dFDouble6.format(value);
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  default String double3ToString(double value) {
    return dFDouble3.format((double) value / 1000);
  }

  /**
   * Cast double value to string.
   *
   * @param value - double value
   * @return double value to string
   */
  default String doubleToString1(double value) {
    return String.format("%.9f", value);
  }
}
