package ru.yanygin.clusterAdminLibrary;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "ru.yanygin.clusterAdminLibrary.messages"; //$NON-NLS-1$
	
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	public static void reloadBundle(Locale targetLocale) {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, targetLocale);
	}
	
	private Messages() {
	}
	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
