package ru.yanygin.clusterAdminLibrary;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface IInfoExtended {
	
	UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$
	DecimalFormat dFLong = new DecimalFormat("###,###"); //$NON-NLS-1$
	DecimalFormat dFDouble6 = new DecimalFormat("#,##0.000000"); //$NON-NLS-1$
	DecimalFormat dFDouble3 = new DecimalFormat("###,##0.000"); //$NON-NLS-1$

//	NumberFormat fmt = NumberFormat.getInstance();
//	fmt.setGroupingUsed(false);
//	fmt.setMaximumIntegerDigits(999);
//	fmt.setMaximumFractionDigits(999);
	
	public static void initColumnsName(Map<String, String> columnsMap) {
	}
	
	public static boolean highlightItem(Date startDateItem) {
		
		Config commonConfig = ClusterProvider.getCommonConfig();
		return commonConfig.highlightNewItems &&
				(new Date().getTime() - startDateItem.getTime() < commonConfig.highlightNewItemsDuration * 1000);
	}
	
	public String[] getExtendedInfo();
	
	default String convertUuidToString(UUID uuid) {
		return uuid.equals(emptyUuid) ? "" : uuid.toString(); //$NON-NLS-1$
	}
	
	default String dateToString(Date date) {
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
		Date emptyDate = new Date(0);
		
		return date.equals(emptyDate) ? "" : dateFormat.format(date); //$NON-NLS-1$
	}
	
	default String millisecondToString(int value) {
		return Double.toString(((double) value) / 1000);
	}
	
	default String millisecondToString(long value) {
		return Double.toString(((double) value) / 1000);
	}
	
	default String longToStringGroup(long value) {
		return dFLong.format(value);
	}
	
	default String double6ToString(double value) {
		return dFDouble6.format(value);
	}
		
	default String double3ToString(double value) {
		return dFDouble3.format((double) value / 1000);
	}
	
	default String doubleToString1(double value) {
		return String.format ("%.9f", value);
	}
}
