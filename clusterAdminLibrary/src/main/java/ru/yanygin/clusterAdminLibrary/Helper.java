package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  private static final DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private static final DateFormat reverseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    MessageBox messageBox =
        new MessageBox(Display.getDefault().getActiveShell()); // SWT.ICON_INFORMATION | SWT.OK
    messageBox.setText("Information");
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
   * @param bitness - разрядность платформы
   * @return список установленных версий платформы V8
   */
  public static Map<String, String> getInstalledV8Versions(String bitness) {
    LOGGER.debug("Get installed v8 platform versions"); //$NON-NLS-1$

    Map<String, String> versions = new HashMap<>();

    if (!Config.currentConfig.isWindows()) {
      return versions;
    }

    final String filterWindows = "8.3.\\d\\d.\\d{4}"; // $NON-NLS-1$
    final String filterLinux = "v8.3.\\d\\d.\\d{4}"; // $NON-NLS-1$
    String filterCurrentOs;

    Path v8runtimeDir;
    // Path v8runtimeDirX64;
    // Path v8runtimeDirX86;

    if (Config.currentConfig.isWindows()) {
      filterCurrentOs = filterWindows;
      // для 64-разрядной
      // v8runtimeDirX64 = Paths.get(System.getenv("ProgramW6432"), "1cv8");
      // v8runtimeDirX86 = Paths.get(System.getenv("ProgramFiles(x86)"), "1cv8");
      // для 32-разрядной
      // v8runtimeDirX86 = Paths.get(System.getenv("ProgramFiles"), "1cv8");

      v8runtimeDir =
          bitness.equals("x64")
              ? Paths.get(System.getenv("ProgramW6432"), "1cv8")
              : Paths.get(System.getenv("ProgramFiles(x86)"), "1cv8");

    } else if (Config.currentConfig.isLinux()) {
      // filterCurrentOs = filterLinux;
      // v8runtimeDirX64 = Paths.get("/opt", "1C", "v8.3", "x86_64");
      // v8runtimeDirX86 = Paths.get("/opt", "1C", "v8.3", "i386");
      // v8runtimeDirX64 = Paths.get("/opt", "1cv8", "x86_64");
      // v8runtimeDirX86 = Paths.get("/opt", "1cv8", "i386");

      // v8runtimeDir = Paths.get("/opt", "1C");
      return versions;

    } else if (Config.currentConfig.isMacOs()) {
      // v8runtimeDir = Paths.get("/opt", "1cv8");
      return versions;
    } else {
      return versions;
    }
    LOGGER.debug("Current v8 runtime directory <{}>", v8runtimeDir); // $NON-NLS-1$

    // File v8x64CommonPath = new File("C:\\Program Files\\1cv8"); // $NON-NLS-1$
    // File v8x86CommonPath = new File("C:\\Program Files (x86)\\1cv8"); // $NON-NLS-1$

    FilenameFilter filterVersion =
        new FilenameFilter() {
          @Override
          public boolean accept(File f, String name) {
            return Paths.get(f.getAbsolutePath(), name).toFile().isDirectory()
                && name.matches(filterCurrentOs);
          }
        };

    FilenameFilter filterBitnessLinux =
        new FilenameFilter() {
          @Override
          public boolean accept(File f, String name) {
            String bitnessPart = bitness.equals("x64") ? "x86_64" : "i386";
            return Paths.get(f.getAbsolutePath(), name).toFile().isDirectory()
                && Paths.get(f.getAbsolutePath(), name, bitnessPart).toFile().exists();
          }
        };

    try {
      if (v8runtimeDir.toFile().exists()) {
        File[] versionDirs = v8runtimeDir.toFile().listFiles(filterVersion);
        for (File dir : versionDirs) {
          LOGGER.debug("Version dir <{}>", dir); // $NON-NLS-1$

          if (Config.currentConfig.isWindows()) {
            versions.put(dir.getName(), dir.getAbsolutePath());
          } else if (dir.list(filterBitnessLinux).length > 0) {
            versions.put(dir.getName(), dir.getAbsolutePath());
          }
        }
      }
    } catch (Exception excp) {
      LOGGER.error(
          "Error read dir <{}>", v8runtimeDir.toFile().getAbsolutePath(), excp); // $NON-NLS-1$
    }

    return versions;
  }

  /**
   * Возвращает путь к исполняемому файлу RAS.
   *
   * @param version - версия платформы
   * @param bitness - разрядность версии
   * @return путь к исполняемому файлу RAS
   */
  public static String pathToRas(String version, String bitness) {

    String pathToVersion = getInstalledV8Versions(bitness).get(version);
    String osPart;

    if (Config.currentConfig.isWindows()) {
      osPart = "bin";
    } else if (Config.currentConfig.isLinux()) {
      osPart = bitness.equals("x64") ? "x86_64" : "i386";
    } else {
      return null;
    }

    return Paths.get(pathToVersion, osPart, "ras").toFile().getAbsolutePath();
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

  /**
   * Преобразует дату к строке.
   *
   * @param date - Дата
   * @return Дата строкой
   */
  public static String dateToString(Date date) {
    Date emptyDate = new Date(0);
    if (date == null || date.equals(emptyDate)) {
      return "";
    }
    return simpleDateFormat.format(date);
  }

  /**
   * Преобразует дату к строке ("yyyy-MM-dd hh:MM:ss").
   *
   * @param date - Дата
   * @return Дата строкой
   */
  public static String dateToStringReverse(Date date) {
    Date emptyDate = new Date(0);
    if (date == null || date.equals(emptyDate)) {
      return "";
    }
    return reverseDateFormat.format(date);
  }
}
