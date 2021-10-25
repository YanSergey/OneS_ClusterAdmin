package ru.yanygin.clusterAdminLibraryUI;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Messages. */
public class Messages {
  private static final String BUNDLE_NAME =
      "ru.yanygin.clusterAdminLibraryUI.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {}

  /**
   * Get the localized string.
   *
   * @param key - key of string
   * @return localized string
   */
  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
