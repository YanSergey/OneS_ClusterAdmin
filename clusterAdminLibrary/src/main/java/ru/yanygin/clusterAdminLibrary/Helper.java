package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class. */
public class Helper {

  private static final Logger LOGGER = LoggerFactory.getLogger("ClusterProvider"); //$NON-NLS-1$

  /** Пустой UUID. */
  public static final UUID EMPTY_UUID =
      UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$

  private Helper() {}

  /**
   * Получает картинку из ресурсов.
   *
   * @param name - имя картинки
   * @return Image
   */
  public static Image getImage(String name) {
    return new Image(
        Display.getCurrent(),
        Helper.class.getResourceAsStream("/icons/".concat(name))); //$NON-NLS-1$
  }

  /**
   * Показывает окно с сообщение пользователю.
   *
   * @param message - текст сообщения
   */
  public static void showMessageBox(String message) {
    MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell());
    messageBox.setMessage(message);
    messageBox.open();
  }

  /**
   * Показывает диалог с вопросом и вариантами ответа ДА | НЕТ.
   *
   * @param question - текст вопроса
   * @return выбранный ответ
   */
  public static int showQuestionBox(String question) {
    var messageBox =
        new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
    messageBox.setMessage(question);

    return messageBox.open();
  }

  /**
   * Возвращает список установленных версий платформы V8.
   *
   * @param config - конфиг программы
   * @return список установленных версий платформы V8
   */
  public static Map<String, String> getInstalledV8Versions() {
    LOGGER.debug("Get installed v8 platform versions"); //$NON-NLS-1$

    Map<String, String> versions = new HashMap<>();

    if (!Config.currentConfig.isWindows()) {
      return versions;
    }

    File v8x64CommonPath = new File("C:\\Program Files\\1cv8"); //$NON-NLS-1$
    File v8x86CommonPath = new File("C:\\Program Files (x86)\\1cv8"); //$NON-NLS-1$

    FilenameFilter filter =
        new FilenameFilter() {
          @Override
          public boolean accept(File f, String name) {
            return name.matches("8.3.\\d\\d.\\d{4}"); //$NON-NLS-1$
          }
        };

    try {
      if (v8x64CommonPath.exists()) {
        File[] v8x64dirs = v8x64CommonPath.listFiles(filter);
        for (File dir : v8x64dirs) {
          if (dir.isDirectory()) {
            File ras = new File(dir.getAbsolutePath().concat("\\bin\\ras.exe")); //$NON-NLS-1$
            if (ras.exists() && ras.isFile()) {
              versions.put(dir.getName().concat(" (x86_64)"), ras.getAbsolutePath()); //$NON-NLS-1$
            }
          }
        }
      }
    } catch (Exception excp) {
      LOGGER.error("Error read dir <{}>", v8x64CommonPath.getAbsolutePath(), excp); //$NON-NLS-1$
    }

    try {
      if (v8x86CommonPath.exists()) {
        File[] v8x86dirs = v8x86CommonPath.listFiles(filter);
        for (File dir : v8x86dirs) {
          if (dir.isDirectory()) {
            File ras = new File(dir.getAbsolutePath().concat("\\bin\\ras.exe")); //$NON-NLS-1$
            if (ras.exists() && ras.isFile()) {
              versions.put(dir.getName(), ras.getAbsolutePath()); //$NON-NLS-1$
            }
          }
        }
      }
    } catch (Exception excp) {
      LOGGER.error("Error read dir <{}>", v8x64CommonPath.getAbsolutePath(), excp); //$NON-NLS-1$
    }

    return versions;
  }

  /**
   * Получает белый цвет.
   *
   * @return Color(255, 255, 255)
   */
  public static Color getWhiteColor() {
    return SWTResourceManager.getColor(255, 255, 255);
  }

  /**
   * Получает розовый цвет.
   *
   * @return Color(255, 204, 204)
   */
  public static Color getPinkColor() {
    return SWTResourceManager.getColor(255, 204, 204);
  }

  /**
   * Получает светло-зеленый цвет.
   *
   * @return Color(128, 255, 128)
   */
  public static Color getLightGreenColor() {
    return SWTResourceManager.getColor(128, 255, 128);
  }

  /**
   * Получает красный цвет.
   *
   * @return Color(255, 0, 0)
   */
  public static Color getRedColor() {
    return SWTResourceManager.getColor(255, 0, 0);
  }

  /**
   * Получает красный цвет.
   *
   * @return Color(190, 0, 30)
   */
  public static Color getDarkRedColor() {
    return SWTResourceManager.getColor(190, 0, 30);
  }

  /**
   * Получает черный цвет.
   *
   * @return Color(0, 0, 0)
   */
  public static Color getBlackColor() {
    return new Color(0, 0, 0);
  }

  /**
   * Получает синий цвет.
   *
   * @return Color(0, 0, 255)
   */
  public static Color getBlueColor() {
    return new Color(0, 0, 255);
  }

  /**
   * Получает синий цвет.
   *
   * @return Color(255, 128, 64)
   */
  public static Color getOrangeColor() {
    return new Color(255, 128, 64);
  }

  /**
   * Получает синий цвет.
   *
   * @return Color(0, 128, 128)
   */
  public static Color getTurquoiseColor() {
    return new Color(0, 128, 128);
  }
}
