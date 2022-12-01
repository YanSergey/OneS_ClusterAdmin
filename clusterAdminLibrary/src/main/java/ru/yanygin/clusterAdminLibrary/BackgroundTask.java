package ru.yanygin.clusterAdminLibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Фоновый процесс. */
public class BackgroundTask {

  private static final Logger LOGGER = LoggerFactory.getLogger("Task"); // $NON-NLS-1$

  public static int countOfRunning = 0;

  String name;
  File script;
  Map<String, String> params;

  Process scriptProcess;
  String processOutput = ""; // $NON-NLS-1$
  int exitCode;
  boolean isRunning;

  Thread thread;

  Image taskRunning;
  Image taskCompleted;
  Image taskError;

  Date startDate;
  Date finishDate;

  /**
   * Инициализация задачи.
   *
   * @param name - Уникальное имя задания
   * @param script - файл скрипта
   * @param params - Переменные окружения
   */
  public BackgroundTask(String name, File script, Map<String, String> params) {
    this.name = name;
    this.script = script;
    this.params = params;

    taskRunning = Helper.getImage("taskRunning.png"); // $NON-NLS-1$
    taskCompleted = Helper.getImage("taskCompleted.png"); // $NON-NLS-1$
    taskError = Helper.getImage("taskError.png"); // $NON-NLS-1$
  }

  /**
   * Имя задания.
   *
   * @return name of task
   */
  public String getName() {
    return name;
  }

  /**
   * Имя задания.
   *
   * @return name of task
   */
  public String[] getDescription() {
    String stateString = "";
    if (isRunning) {
      stateString = "run";
    } else if (exitCode != 0) {
      stateString = "error";
    } else {
      stateString = "done";
    }

    return new String[] {
      name, stateString, Helper.dateToString(startDate), Helper.dateToString(finishDate)
    };
  }

  /**
   * Задание запущено.
   *
   * @return true если задание еще запущено
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Иконка задачи.
   *
   * @return Иконка визуализирующая текущее состояние задачи
   */
  public Image getIcon() {
    if (isRunning) {
      return taskRunning;
    } else if (exitCode != 0) {
      return taskError;
    } else {
      return taskCompleted;
    }
  }

  /**
   * Дата начала выполнения задания.
   *
   * @return дату начала выполнения задания
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Дата завершения задания.
   *
   * @return дату нзавершения задания
   */
  public Date getFinishDate() {
    return finishDate;
  }

  /** Запуск фонового задания. */
  public void run() {

    thread =
        new Thread(
            () -> {
              startDate = new Date();

              var processBuilder = new ProcessBuilder();

              Map<String, String> env = processBuilder.environment();

              params.forEach(env::put);

              processBuilder.command(
                  "cmd.exe", //$NON-NLS-1$
                  "/c", //$NON-NLS-1$
                  script.getAbsolutePath());
              try {
                scriptProcess = processBuilder.start();
              } catch (Exception excp) {
                LOGGER.error("Error launch user script <{}>", script.getName()); // $NON-NLS-1$
                LOGGER.error("\t<{}>", processOutput, excp); // $NON-NLS-1$
                Helper.showMessageBox(excp.getLocalizedMessage());
                return;
              }
              isRunning = true;

              // Дочерний процесс не сразу стартует и в лог о нем не попадает информация
              // try {
              // Thread.sleep(1000);
              // } catch (InterruptedException excp) {
              // LOGGER.error("Error: ", excp); // $NON-NLS-1$
              // }

              LOGGER.debug("Script process runnung = {}", scriptProcess.isAlive()); // $NON-NLS-1$

              // if (scriptProcess.isAlive()) {
              LOGGER.debug(
                  "Script process parent CMD pid = {}", scriptProcess.pid()); // $NON-NLS-1$
              Stream<ProcessHandle> subprocesses = scriptProcess.children();
              subprocesses.forEach(
                  subprocess ->
                      LOGGER.debug(
                          "\tsubprocess -> {}, pid = {}", //$NON-NLS-1$
                          subprocess.info().command().get(),
                          subprocess.pid()));

              // Читаем вывод скрипта
              try {
                BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(
                            scriptProcess.getInputStream(), "cp866")); // windows-1251, cp866, UTF-8
                // StandardCharsets.UTF_8)); // windows-1251, cp866, UTF-8

                String line;
                while ((line = reader.readLine()) != null) {
                  processOutput = processOutput.concat(System.lineSeparator()).concat(line);
                }

                exitCode = scriptProcess.waitFor();

              } catch (InterruptedException | IOException excp) {
                LOGGER.error("Error: ", excp); // $NON-NLS-1$
              }

              isRunning = false;
              finishDate = new Date();
            });

    thread.start();
  }

  /**
   * Получить лог выполнения задачи.
   *
   * @return лог выполнения задачи
   */
  public String getLog() {
    return processOutput;
  }

}
