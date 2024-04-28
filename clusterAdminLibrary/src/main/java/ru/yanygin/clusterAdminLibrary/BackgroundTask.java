package ru.yanygin.clusterAdminLibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Фоновый процесс. */
public class BackgroundTask {

  static final Logger LOGGER = LoggerFactory.getLogger(BackgroundTask.class.getSimpleName());

  static final String starterPath = "\"C:\\Program Files\\1cv8\\common\\1cestart.exe\"";
  static final String designerCommand = "DESIGNER";
  static final String enterpriseCommand = "ENTERPRISE";
  static final String logonCommand =
      "/S%v8serverName%:%v8managerPort%\\%v8infobase% /N%v8username% /P%v8password%";
  static final String FILEPATH_PARAM_KEY = "%v8FilePath%";

  static int countOfRunning = 0;
  static int countOfCompleted = 0;
  static int countWithError = 0;
  static Map<String, Integer> taskNamesCounter = new HashMap<>();

  static Image taskRunning = Helper.getImage("taskRunning.png"); //$NON-NLS-1$
  static Image taskCompleted = Helper.getImage("taskCompleted.png"); //$NON-NLS-1$
  static Image taskError = Helper.getImage("taskError.png"); //$NON-NLS-1$

  File script;
  String scriptText;
  String v8Command;
  String fileExtension;

  String scriptName;

  String taskName;
  Map<String, String> params;

  Process scriptProcess;
  String processOutput = ""; //$NON-NLS-1$
  int exitCode;

  TaskState state;
  TaskState stateLast;

  TaskVariant taskVariant;
  V8ActionVariant v8ActionVariant;

  enum TaskState {
    RUNNUNG,
    DONE,
    ERROR
  }

  enum TaskVariant {
    USER_SCRIPT,
    V8ACTION
  }

  public enum V8ActionVariant {
    RUN_DESIGNER,
    RUN_ENTERPRISE,
    LOAD_CF,
    SAVE_CF,
    LOAD_DT,
    SAVE_DT
  }

  Thread thread;

  Date startDate;
  Date finishDate;

  /**
   * Инициализация задачи.
   *
   * @param script - файл скрипта
   */
  public BackgroundTask(File script) {
    this.taskVariant = TaskVariant.USER_SCRIPT;

    this.script = script;

    try {
      this.scriptText = Files.readString(Path.of(script.getPath()));
    } catch (IOException excp) {
      LOGGER.error(
          "Error read script {}", //$NON-NLS-1$
          script.getName(),
          excp);
    }

    this.scriptName = script.getName();
  }

  /**
   * Инициализация задачи.
   *
   * @param v8ActionVariant - Вариант команды 1С
   */
  public BackgroundTask(V8ActionVariant v8ActionVariant) {
    this.taskVariant = TaskVariant.V8ACTION;
    this.v8ActionVariant = v8ActionVariant;

    final String launchMode;
    if (v8ActionVariant == V8ActionVariant.RUN_ENTERPRISE) {
      launchMode = enterpriseCommand;
    } else {
      launchMode = designerCommand;
    }

    final String v8Command;
    switch (v8ActionVariant) {
      case SAVE_CF:
        this.fileExtension = ".cf";
        v8Command = String.join(" ", "/DumpCfg", FILEPATH_PARAM_KEY);
        break;
      case LOAD_CF:
        this.fileExtension = ".cf";
        v8Command = String.join(" ", "/LoadCfg", FILEPATH_PARAM_KEY);
        break;
      case SAVE_DT:
        this.fileExtension = ".dt";
        v8Command = String.join(" ", "/DumpIB", FILEPATH_PARAM_KEY);
        break;
      case LOAD_DT:
        this.fileExtension = ".dt";
        v8Command = String.join(" ", "/RestoreIB", FILEPATH_PARAM_KEY);
        break;
      default:
        v8Command = "";
        break;
    }

    this.scriptText =
        String.join(
            " ",
            starterPath,
            launchMode,
            logonCommand,
            v8Command);

    this.scriptName = v8ActionVariant.toString();
  }

  private void generateTaskName() {

    int taskNumber = taskNamesCounter.getOrDefault(scriptName, 0);
    taskNumber++;
    taskNamesCounter.put(scriptName, taskNumber);

    if (taskVariant == TaskVariant.USER_SCRIPT) {
      this.taskName = String.format("%s (#%d)", scriptName, taskNumber);
    } else {
      this.taskName =
          String.format(
              "%s %s:%s/%s (#%d)",
              scriptName,
              params.get("v8serverName"),
              params.get("v8managerPort"),
              params.get("v8infobase"),
              taskNumber);
    }
  }

  /** Получить текст скрипта. */
  public String getScriptText() {
    return this.scriptText;
  }

  /** Получить имя скрипта. */
  public String getScriptName() {
    return this.scriptName;
  }

  /** Определяет, я вляется ли параметр путем к файлу. */
  public boolean isFilepathParam(String paramName) {
    return FILEPATH_PARAM_KEY.contains(paramName);
  }

  /** Получить строку для фильтра выбора файла. */
  public String getFilenameFilterExt() {
    return "*".concat(fileExtension);
  }

  /** Получить строку для фильтра выбора файла. */
  public boolean isSaveCommand() {
    return v8ActionVariant == V8ActionVariant.SAVE_CF || v8ActionVariant == V8ActionVariant.SAVE_DT;
  }

  /**
   * Установка параметров задачи.
   *
   * @param params - Параметры задачи
   */
  public void setParams(Map<String, String> params) {
    this.params = params;
    generateTaskName();
  }

  /**
   * Получить количество запущенных заданий.
   *
   * @return количество запущенных задач
   */
  public static int getRunningCount() {
    return countOfRunning;
  }

  /**
   * Установить количество запущенных заданий.
   *
   * @param count - количество
   */
  public static void setCount(int count) {
    countOfRunning = count;
  }

  /** Сбросить в 0 посчитанное количество заданий. */
  public static void resetCount() {
    countOfRunning = 0;
    countOfCompleted = 0;
    countWithError = 0;
  }

  /**
   * Считает количество запущенных/завершенных заданий.
   *
   * @param task - задача
   */
  private static void calculateCount(BackgroundTask task) {

    if (task.isRunning()) {
      countOfRunning++;
    } else if (task.exitCode != 0) {
      countWithError++;
    } else {
      countOfCompleted++;
    }
  }

  /**
   * Устанавливает заголовок вкладки с задачами в зависимости от количества запущенных/завершенных
   * заданий.
   *
   * @param tab - вкладка с задачами
   */
  public static void setTabTitle(TabItem tab) {

    String title =
        String.format(
            "Tasks (Run: %d, Completed: %d, Error: %d)",
            countOfRunning, countOfCompleted, countWithError);

    // countWithError == 0
    // ? String.format("Tasks (Run: %d, Completed: %d)", countOfRunning,
    // countOfCompleted)
    // : String.format(
    // "Tasks (Run: %d, Completed: %d, Error: %d)",
    // countOfRunning, countOfCompleted, countWithError);

    tab.setText(title);
    tab.setImage(countOfRunning == 0 ? taskCompleted : taskRunning);
  }

  /**
   * Получить имя задания.
   *
   * @return name of task
   */
  public String getTaskName() {
    return taskName;
  }

  /**
   * Получить описание задания для вывода в списке.
   *
   * @return описание задания для вывода в списке
   */
  public String[] getDescription() {
    String stateString = "";

    switch (state) {
      case DONE:
        stateString = "done";
        break;
      case ERROR:
        stateString = "error";
        break;
      case RUNNUNG:
      default:
        stateString = "run";
        break;
    }

    // String paramsAsString =
    // params.keySet().stream()
    // .map(key -> key + " = " + params.get(key))
    // .collect(Collectors.joining("\n"));

    return new String[] {
      taskName,
      stateString,
      Helper.dateToString(startDate),
      Helper.dateToString(finishDate),
      params.toString()
    };
  }

  /**
   * Задание запущено.
   *
   * @return true если задание еще запущено
   */
  public boolean isRunning() {
    return state == TaskState.RUNNUNG;
  }

  /**
   * Получить лог выполнения задачи.
   *
   * @return лог выполнения задачи
   */
  public String getLog() {
    return processOutput;
  }

  /**
   * Получить иконку задачи.
   *
   * @return Иконка визуализирующая текущее состояние задачи
   */
  public Image getIcon() {

    switch (state) {
      case DONE:
        return taskCompleted;
      case ERROR:
        return taskError;
      case RUNNUNG:
      default:
        return taskRunning;
    }
  }

  /**
   * Получить дату начала выполнения задания.
   *
   * @return дата начала выполнения задания
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Получить дату завершения задания.
   *
   * @return дата нзавершения задания
   */
  public Date getFinishDate() {
    return finishDate;
  }

  /**
   * Оновить состояние задания.
   *
   * @param tableItem - элемент таблицы содержащий объект задания
   */
  public void update(TableItem tableItem) {
    if (state != stateLast) { // обновляем только при изменении состояния
      tableItem.setText(this.getDescription());
      tableItem.setImage(this.getIcon());
      stateLast = state;
    }

    calculateCount(this);
  }

  /** Запуск фонового задания. */
  public void run() {

    thread =
        new Thread(
            () -> {
              startDate = new Date();

              ProcessBuilder processBuilder = new ProcessBuilder();
              // "cmd.exe", //$NON-NLS-1$
              // "/c", //$NON-NLS-1$
              // "chcp", //$NON-NLS-1$
              // "65001", //$NON-NLS-1$
              // script.getAbsolutePath()
              // ).inheritIO();
              // processBuilder.command(script.getAbsolutePath());

              String currentCommand =
                  taskVariant == TaskVariant.USER_SCRIPT ? script.getAbsolutePath() : scriptText;

              processBuilder.command(
                  "cmd.exe", //$NON-NLS-1$
                  "/c", //$NON-NLS-1$
                  currentCommand); // script.getAbsolutePath());

              Map<String, String> env = processBuilder.environment();
              params.forEach(env::put);

              try {
                scriptProcess = processBuilder.start();
              } catch (Exception excp) {
                LOGGER.error("Error launch user script <{}>", scriptName); //$NON-NLS-1$
                LOGGER.error("\t<{}>", processOutput, excp); //$NON-NLS-1$
                Helper.showMessageBox(excp.getLocalizedMessage());
                return;
              }
              state = TaskState.RUNNUNG;

              LOGGER.debug("Script process runnung = {}", scriptProcess.isAlive()); //$NON-NLS-1$
              LOGGER.debug("Script process parent CMD pid={}", scriptProcess.pid()); //$NON-NLS-1$
              Stream<ProcessHandle> subprocesses = scriptProcess.children();
              subprocesses.forEach(
                  subprocess ->
                      LOGGER.debug(
                          "\tsubprocess -> {}, pid={}", //$NON-NLS-1$
                          subprocess.info().command().get(),
                          subprocess.pid()));

              // Читаем вывод скрипта
              try {
                BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(
                            scriptProcess.getInputStream(),
                            // "cp866")); // windows-1251, cp866, UTF-8
                            StandardCharsets.UTF_8));

                String line;
                while ((line = reader.readLine()) != null) {
                  processOutput = processOutput.concat(System.lineSeparator()).concat(line);
                }

                exitCode = scriptProcess.waitFor();

              } catch (InterruptedException | IOException excp) {
                LOGGER.error("Error: ", excp); //$NON-NLS-1$
              }

              state = exitCode == 0 ? TaskState.DONE : TaskState.ERROR;
              finishDate = new Date();
            });

    thread.start();
  }
}
