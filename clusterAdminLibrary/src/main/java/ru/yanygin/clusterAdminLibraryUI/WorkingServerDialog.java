package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IPortRangeInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;
import com._1c.v8.ibis.admin.PortRangeInfo;
import com._1c.v8.ibis.admin.WorkingServerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;
import ru.yanygin.clusterAdminLibrary.WorkingServerInfoExtended;

/** Диалог редактирования параметров рабочего сервера. */
public class WorkingServerDialog extends Dialog {

  private static final String DEFAULT_PROF_VALUE = "8"; //$NON-NLS-1$
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  private static final String DEFAULT_INFOBASES_PER_WP = "8"; //$NON-NLS-1$
  private static final String DEFAULT_CONNECTIONS_PER_WP = "128"; //$NON-NLS-1$

  private Server server;
  private UUID clusterId;
  private UUID workingServerId;

  private Text txtServerName;
  private Text txtComputerName;
  private Text txtIpPort;
  private Text txtPortRange;
  private Text txtInfoBasesPerWorkingProcessLimit;
  private Text txtConnectionsPerWorkingProcessLimit;
  private Text txtIpPortMainManager;
  private Text txtWorkingProcessMemoryLimit;
  private Text txtSafeCallMemoryLimit;
  private Text txtCriticalProcessesTotalMemory;
  private Text txtTemporaryAllowedProcessesTotalMemory;
  private Text txtTemporaryAllowedProcessesTotalMemoryTimeLimit;
  private Text txtSafeWorkingProcessesMemoryLimit;
  
  private Label lblSafeWorkingProcessesMemoryLimitMb;
  private Label lblSafeCallMemoryLimitMb;
  private Label lblWorkingProcessMemoryLimitMb;
  private Label lblCriticalProcessesTotalMemoryMb;
  private Label lblTemporaryAllowedProcessesTotalMemoryMb;
  private Label lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin;

  private Button btnIsDedicatedManagers;
  private Button btnIsMainServer;

  /**
   * Диалог создания нового или редактирования рабочего сервера.
   *
   * @param parentShell - parent shell
   * @param server - server
   * @param clusterId - cluster ID
   * @param workingServerId - working server ID (null для создания нового)
   */
  public WorkingServerDialog(
      Shell parentShell, Server server, UUID clusterId, UUID workingServerId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
    this.workingServerId = workingServerId;
  }

  /**
   * Диалог редактирования рабочего сервера.
   *
   * @param parentShell - parent shell
   * @param workingServerExtInfo - расширенная информация рабочего сервера
   */
  public WorkingServerDialog(Shell parentShell, WorkingServerInfoExtended workingServerExtInfo) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = workingServerExtInfo.getServer();
    this.clusterId = workingServerExtInfo.getClusterId();
    this.workingServerId = workingServerExtInfo.getWorkingServerId();
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(Strings.TITLE_WINDOW);
  }

  /**
   * Create contents of the dialog.
   *
   * @param parent - parent composite
   */
  @Override
  protected Control createDialogArea(Composite parent) {

    Composite container = (Composite) super.createDialogArea(parent);
    GridLayout gridLayout = (GridLayout) container.getLayout();
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 12;
    gridLayout.numColumns = 3;

    Label lblServerName = new Label(container, SWT.NONE);
    lblServerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblServerName.setText(Strings.SERVER_NAME);

    txtServerName = new Text(container, SWT.BORDER);
    txtServerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblComputerName = new Label(container, SWT.NONE);
    lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblComputerName.setText(Strings.COMPUTER_NAME);

    txtComputerName = new Text(container, SWT.BORDER);
    txtComputerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblIpPort = new Label(container, SWT.NONE);
    lblIpPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblIpPort.setText(Strings.IP_PORT);

    txtIpPort = new Text(container, SWT.BORDER);
    txtIpPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblPortRange = new Label(container, SWT.NONE);
    lblPortRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPortRange.setText(Strings.PORT_RANGE);

    txtPortRange = new Text(container, SWT.BORDER);
    txtPortRange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblSafeWorkingProcessesMemoryLimit = new Label(container, SWT.NONE);
    lblSafeWorkingProcessesMemoryLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSafeWorkingProcessesMemoryLimit.setText(Strings.SAFE_WORKING_PROCESSES_MEMORY_LIMIT);

    txtSafeWorkingProcessesMemoryLimit = new Text(container, SWT.BORDER);
    txtSafeWorkingProcessesMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblSafeWorkingProcessesMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });

    txtSafeWorkingProcessesMemoryLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblSafeWorkingProcessesMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblSafeWorkingProcessesMemoryLimitMb =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblSafeWorkingProcessesMemoryLimitMb.minimumHeight = 8;
    lblSafeWorkingProcessesMemoryLimitMb.setLayoutData(gdlblSafeWorkingProcessesMemoryLimitMb);

    Label lblSafeCallMemoryLimit = new Label(container, SWT.NONE);
    lblSafeCallMemoryLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSafeCallMemoryLimit.setText(Strings.SAFE_CALL_MEMORY_LIMIT);

    txtSafeCallMemoryLimit = new Text(container, SWT.BORDER);
    txtSafeCallMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblSafeCallMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtSafeCallMemoryLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblSafeCallMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblSafeCallMemoryLimitMb = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblSafeCallMemoryLimitMb.minimumHeight = 8;
    lblSafeCallMemoryLimitMb.setLayoutData(gdlblSafeCallMemoryLimitMb);

    Label lblWorkingProcessMemoryLimit = new Label(container, SWT.NONE);
    lblWorkingProcessMemoryLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
    lblWorkingProcessMemoryLimit.setText(Strings.WORKING_PROCESS_MEMORY_LIMIT);

    txtWorkingProcessMemoryLimit = new Text(container, SWT.BORDER);
    txtWorkingProcessMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblWorkingProcessMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtWorkingProcessMemoryLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    lblWorkingProcessMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblWorkingProcessMemoryLimitMb =
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdlblWorkingProcessMemoryLimitMb.minimumHeight = 8;
    lblWorkingProcessMemoryLimitMb.setLayoutData(gdlblWorkingProcessMemoryLimitMb);

    Label lblCriticalProcessesTotalMemory = new Label(container, SWT.NONE);
    lblCriticalProcessesTotalMemory.setText(Strings.CRITICAL_PROCESSES_TOTAL_MEMORY);
    lblCriticalProcessesTotalMemory.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

    txtCriticalProcessesTotalMemory = new Text(container, SWT.BORDER);
    txtCriticalProcessesTotalMemory.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblCriticalProcessesTotalMemoryMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtCriticalProcessesTotalMemory.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblCriticalProcessesTotalMemoryMb = new Label(container, SWT.NONE);
    GridData gdlblCriticalProcessesTotalMemoryMb =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblCriticalProcessesTotalMemoryMb.minimumHeight = 8;
    lblCriticalProcessesTotalMemoryMb.setLayoutData(gdlblCriticalProcessesTotalMemoryMb);

    Label lblTemporaryAllowedProcessesTotalMemory = new Label(container, SWT.NONE);
    lblTemporaryAllowedProcessesTotalMemory.setLayoutData(
        new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
    lblTemporaryAllowedProcessesTotalMemory.setText(
        Strings.TEMPORARY_ALLOWED_PROCESSES_TOTAL_MEMORY);

    txtTemporaryAllowedProcessesTotalMemory = new Text(container, SWT.BORDER);
    txtTemporaryAllowedProcessesTotalMemory.addModifyListener(
        new ModifyListener() { // NOSONAR
          @Override
          public void modifyText(ModifyEvent e) {
            lblTemporaryAllowedProcessesTotalMemoryMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtTemporaryAllowedProcessesTotalMemory.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblTemporaryAllowedProcessesTotalMemoryMb = new Label(container, SWT.NONE);
    GridData gdlblTemporaryAllowedProcessesTotalMemoryMb =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblTemporaryAllowedProcessesTotalMemoryMb.minimumHeight = 8;
    lblTemporaryAllowedProcessesTotalMemoryMb.setLayoutData(
        gdlblTemporaryAllowedProcessesTotalMemoryMb);

    Label lblTemporaryAllowedProcessesTotalMemoryTimeLimit = new Label(container, SWT.NONE);
    lblTemporaryAllowedProcessesTotalMemoryTimeLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblTemporaryAllowedProcessesTotalMemoryTimeLimit.setText(
        Strings.TEMPORARY_ALLOWED_PROCESSES_TOTAL_MEMORY_TIME_LIMIT);

    txtTemporaryAllowedProcessesTotalMemoryTimeLimit = new Text(container, SWT.BORDER);
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.setText(
                convertToMinutes((Text) e.widget));
          }
        });
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin = new Label(container, SWT.NONE);
    GridData gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.minimumHeight = 8;
    lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.setLayoutData(
        gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin);

    Group groupWorkProcessesParams = new Group(container, SWT.NONE);
    groupWorkProcessesParams.setText(Strings.WORKING_PROCESSES_PARAMETERS);
    GridLayout glgroupWorkProcessesParams = new GridLayout(2, true);
    glgroupWorkProcessesParams.verticalSpacing = 8;
    groupWorkProcessesParams.setLayout(glgroupWorkProcessesParams);
    GridData gdgroupWorkProcessesParams = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
    gdgroupWorkProcessesParams.widthHint = 424;
    groupWorkProcessesParams.setLayoutData(gdgroupWorkProcessesParams);

    Label lblInfoBasesPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.NONE);
    lblInfoBasesPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfoBasesPerWorkingProcessLimit.setText(Strings.INFOBASES_PER_WORKING_PROCESS_LIMIT);
    lblInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 35, 15);

    txtInfoBasesPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    txtInfoBasesPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 76, 21);

    Label lblConnectionsPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
    lblConnectionsPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    lblConnectionsPerWorkingProcessLimit.setText(Strings.CONNECTIONS_PER_WORKING_PROCESS_LIMIT);
    lblConnectionsPerWorkingProcessLimit.setBounds(0, 0, 35, 15);

    txtConnectionsPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    txtConnectionsPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    txtConnectionsPerWorkingProcessLimit.setBounds(0, 0, 76, 21);
    new Label(container, SWT.NONE);

    Label lblIpPortMainManager = new Label(container, SWT.NONE);
    lblIpPortMainManager.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblIpPortMainManager.setText(Strings.IP_PORT_MAIN_CLUSTER_MANAGER);

    txtIpPortMainManager = new Text(container, SWT.BORDER);
    txtIpPortMainManager.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    btnIsDedicatedManagers = new Button(container, SWT.CHECK);
    btnIsDedicatedManagers.setText(Strings.IS_DEDICATED_MANAGERS);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    btnIsMainServer = new Button(container, SWT.CHECK);
    btnIsMainServer.setText(Strings.IS_MAIN_SERVER);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    initServerProperties();

    return container;
  }

  private void initServerProperties() {
    IWorkingServerInfo serverInfo;

    if (workingServerId == null) {
      // Создаем новый рабочий сервер
      int clusterPort = server.getClusterInfo(clusterId).getMainPort();
      serverInfo = new WorkingServerInfo(clusterPort);

      txtServerName.setText(EMPTY_STRING);
      txtPortRange.setText(EMPTY_STRING);
      txtComputerName.setText(EMPTY_STRING);

      txtInfoBasesPerWorkingProcessLimit.setText(DEFAULT_INFOBASES_PER_WP);
      txtConnectionsPerWorkingProcessLimit.setText(DEFAULT_CONNECTIONS_PER_WP);
    } else {
      // Редактируем существующий рабочий сервер
      serverInfo = server.getWorkingServerInfo(clusterId, workingServerId);

      txtServerName.setText(serverInfo.getName());
      txtComputerName.setText(serverInfo.getHostName());

      IPortRangeInfo portRangesInfo = serverInfo.getPortRanges().get(0);
      String portRanges =
          Integer.toString(portRangesInfo.getLowBound())
              .concat(":") //$NON-NLS-1$
              .concat(Integer.toString(portRangesInfo.getHighBound()));
      txtPortRange.setText(portRanges);

      txtInfoBasesPerWorkingProcessLimit.setText(
          Integer.toString(serverInfo.getInfoBasesPerWorkingProcessLimit()));
      txtConnectionsPerWorkingProcessLimit.setText(
          Integer.toString(serverInfo.getConnectionsPerWorkingProcessLimit()));
    }

    txtIpPort.setText(Integer.toString(serverInfo.getMainPort()));

    txtSafeWorkingProcessesMemoryLimit.setText(
        String.valueOf(serverInfo.getSafeWorkingProcessesMemoryLimit()));
    txtSafeCallMemoryLimit.setText(String.valueOf(serverInfo.getSafeCallMemoryLimit()));
    txtCriticalProcessesTotalMemory.setText(
        String.valueOf(serverInfo.getCriticalProcessesTotalMemory()));
    txtTemporaryAllowedProcessesTotalMemory.setText(
        String.valueOf(serverInfo.getTemporaryAllowedProcessesTotalMemory()));
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setText(
        String.valueOf(serverInfo.getTemporaryAllowedProcessesTotalMemoryTimeLimit()));
    txtWorkingProcessMemoryLimit.setText(String.valueOf(serverInfo.getWorkingProcessMemoryLimit()));

    txtIpPortMainManager.setText(Integer.toString(serverInfo.getClusterMainPort()));
    btnIsDedicatedManagers.setSelection(serverInfo.isDedicatedManagers());
    btnIsMainServer.setSelection(serverInfo.isMainServer());

    if (server.isFifteenOrMoreAgentVersion()) { // 8.3.15+
      txtSafeWorkingProcessesMemoryLimit.setEditable(false);
      txtWorkingProcessMemoryLimit.setEditable(false);

      txtSafeWorkingProcessesMemoryLimit.setToolTipText(Strings.DEPRECATED_IN_FIFTEEN);
      txtWorkingProcessMemoryLimit.setToolTipText(Strings.DEPRECATED_IN_FIFTEEN);
    } else { // 8.3.15-
      txtCriticalProcessesTotalMemory.setEditable(false);
      txtTemporaryAllowedProcessesTotalMemory.setEditable(false);
      txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setEditable(false);

      txtCriticalProcessesTotalMemory.setToolTipText(Strings.APPEARED_IN_FIFTEEN);
      txtTemporaryAllowedProcessesTotalMemory.setToolTipText(Strings.APPEARED_IN_FIFTEEN);
      txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setToolTipText(Strings.APPEARED_IN_FIFTEEN);
    }

    if (workingServerId != null) { // У уже созданного кластера запрещено менять хост и порт
      txtServerName.setEditable(false);
      txtComputerName.setEditable(false);
      txtIpPort.setEditable(false);
    } else {
      // Почему новому серверу запрещено сразу ставить галочку Центральный сервер?
      btnIsMainServer.setEnabled(false);
    }
    txtIpPortMainManager.setEditable(false);
  }

  private void resetToProf() {
    if (!server.isFifteenOrMoreAgentVersion()) { // 8.3.15-
      txtSafeWorkingProcessesMemoryLimit.setText(DEFAULT_PROF_VALUE);
      txtWorkingProcessMemoryLimit.setText(DEFAULT_PROF_VALUE);
    }
    txtSafeCallMemoryLimit.setText(DEFAULT_PROF_VALUE);
    txtInfoBasesPerWorkingProcessLimit.setText(DEFAULT_PROF_VALUE);
  }

  private boolean checkVariablesFromControls() {

    var existsError = false;

    List<Text> checksTextControls = new ArrayList<>();
    checksTextControls.add(txtServerName);
    checksTextControls.add(txtComputerName);
    checksTextControls.add(txtIpPort);
    checksTextControls.add(txtPortRange);

    for (Text control : checksTextControls) {
      if (control.getText().isBlank()) {
        control.setBackground(Helper.getPinkColor());
        existsError = true;
      } else {
        control.setBackground(Helper.getWhiteColor());
      }
    }

    List<Text> checksIntControls = new ArrayList<>();
    checksIntControls.add(txtIpPort);
    checksIntControls.add(txtInfoBasesPerWorkingProcessLimit);
    checksIntControls.add(txtConnectionsPerWorkingProcessLimit);

    for (Text control : checksIntControls) {
      try {
        Integer.parseInt(control.getText());
        control.setBackground(Helper.getWhiteColor());
      } catch (Exception e) {
        control.setBackground(Helper.getPinkColor());
        existsError = true;
      }
    }

    List<Text> checksLongControls = new ArrayList<>();
    checksLongControls.add(txtSafeCallMemoryLimit);
    checksLongControls.add(txtCriticalProcessesTotalMemory);
    checksLongControls.add(txtTemporaryAllowedProcessesTotalMemory);
    checksLongControls.add(txtTemporaryAllowedProcessesTotalMemoryTimeLimit);
    checksLongControls.add(txtSafeWorkingProcessesMemoryLimit);
    checksLongControls.add(txtWorkingProcessMemoryLimit);

    for (Text control : checksLongControls) {
      try {
        Long.parseLong(control.getText());
        control.setBackground(Helper.getWhiteColor());
      } catch (Exception e) {
        control.setBackground(Helper.getPinkColor());
        existsError = true;
      }
    }

    try {
      String[] portRange = txtPortRange.getText().split(":"); //$NON-NLS-1$
      new PortRangeInfo(Integer.parseInt(portRange[1]), Integer.parseInt(portRange[0]));
      txtPortRange.setBackground(Helper.getWhiteColor());
    } catch (Exception e) {
      txtPortRange.setBackground(Helper.getPinkColor());
      existsError = true;
    }

    return existsError;
  }

  private boolean saveNewClusterProperties() {
    if (checkVariablesFromControls()) {
      return false;
    }

    IWorkingServerInfo workingServerInfo;

    if (workingServerId == null) {
      // создание нового рабочего сервера
      int clusterPort = server.getClusterInfo(clusterId).getMainPort();
      workingServerInfo = new WorkingServerInfo(txtServerName.getText(), clusterPort);

      workingServerInfo.setHostName(txtComputerName.getText());
      workingServerInfo.setMainPort(Integer.parseInt(txtIpPort.getText()));
    } else {
      // изменение рабочего сервера
      workingServerInfo = server.getWorkingServerInfo(clusterId, workingServerId);
    }

    String[] portRange = txtPortRange.getText().split(":"); //$NON-NLS-1$
    IPortRangeInfo portRangesInfo =
        new PortRangeInfo(Integer.parseInt(portRange[1]), Integer.parseInt(portRange[0]));
    List<IPortRangeInfo> portRangeList = new ArrayList<>();
    portRangeList.add(portRangesInfo);
    workingServerInfo.setPortRanges(portRangeList);

    workingServerInfo.setInfoBasesPerWorkingProcessLimit(
        Integer.parseInt(txtInfoBasesPerWorkingProcessLimit.getText()));
    workingServerInfo.setConnectionsPerWorkingProcessLimit(
        Integer.parseInt(txtConnectionsPerWorkingProcessLimit.getText()));

    workingServerInfo.setSafeCallMemoryLimit(Long.parseLong(txtSafeCallMemoryLimit.getText()));

    workingServerInfo.setDedicatedManagers(btnIsDedicatedManagers.getSelection());
    workingServerInfo.setMainServer(btnIsMainServer.getSelection());

    if (server.isFifteenOrMoreAgentVersion()) {
      workingServerInfo.setCriticalProcessesTotalMemory(
          Long.parseLong(txtCriticalProcessesTotalMemory.getText()));
      workingServerInfo.setTemporaryAllowedProcessesTotalMemory(
          Long.parseLong(txtTemporaryAllowedProcessesTotalMemory.getText()));
      workingServerInfo.setTemporaryAllowedProcessesTotalMemoryTimeLimit(
          Long.parseLong(txtTemporaryAllowedProcessesTotalMemoryTimeLimit.getText()));
    } else {
      workingServerInfo.setSafeWorkingProcessesMemoryLimit(
          Long.parseLong(txtSafeWorkingProcessesMemoryLimit.getText()));
      workingServerInfo.setWorkingProcessMemoryLimit(
          Long.parseLong(txtWorkingProcessMemoryLimit.getText()));
    }

    if (server.regWorkingServer(clusterId, workingServerInfo, workingServerId == null)) {
      workingServerId = workingServerInfo.getWorkingServerId();
      return true;
    }
    return false;
  }

  /**
   * Получение ID нового рабочего сервера.
   *
   * @return ID нового рабочего сервера
   */
  public UUID getNewWorkingServerId() {
    return workingServerId;
  }

  private String convertToMegabytes(Text textControl) {
    long inMb = Long.parseLong(textControl.getText()) / (1024 * 1024);
    return Long.toString(inMb).concat(Strings.MEGABYTES);
  }

  private String convertToMinutes(Text textControl) {
    long inMb = Long.parseLong(textControl.getText()) / (60);
    return Long.toString(inMb).concat(Strings.MINUTES);
  }

  /**
   * Create contents of the button bar.
   *
   * @param parent - parent composite
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    Button buttonOk =
        createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
    buttonOk.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            if (saveNewClusterProperties()) {
              close();
            }
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

    Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, Strings.APPLY, false);
    buttonApply.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            saveNewClusterProperties();
          }
        });

    Button buttonReset = createButton(parent, IDialogConstants.RETRY_ID, Strings.RESET, false);
    buttonReset.setText(Strings.RESET);
    buttonReset.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            initServerProperties();
          }
        });

    Button buttonResetToProf =
        createButton(parent, IDialogConstants.RETRY_ID, Strings.RESET_TO_PROF, false);
    buttonResetToProf.setText(Strings.RESET_TO_PROF);
    buttonResetToProf.addMouseTrackListener(
        new MouseTrackAdapter() {
          @Override
          public void mouseEnter(MouseEvent e) {
            txtSafeCallMemoryLimit.setBackground(Helper.getLightGreenColor());
            txtSafeWorkingProcessesMemoryLimit.setBackground(Helper.getLightGreenColor());
            txtWorkingProcessMemoryLimit.setBackground(Helper.getLightGreenColor());
            txtInfoBasesPerWorkingProcessLimit.setBackground(Helper.getLightGreenColor());
          }

          @Override
          public void mouseExit(MouseEvent e) {
            txtSafeCallMemoryLimit.setBackground(Helper.getWhiteColor());
            txtSafeWorkingProcessesMemoryLimit.setBackground(Helper.getWhiteColor());
            txtWorkingProcessMemoryLimit.setBackground(Helper.getWhiteColor());
            txtInfoBasesPerWorkingProcessLimit.setBackground(Helper.getWhiteColor());
          }
        });
    buttonResetToProf.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            resetToProf();
          }
        });
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String SERVER_NAME = getString("ServerName");
    static final String COMPUTER_NAME = getString("ComputerName");
    static final String IP_PORT = getString("IPPort");
    static final String PORT_RANGE = getString("PortRange");
    static final String SAFE_WORKING_PROCESSES_MEMORY_LIMIT =
        getString("SafeWorkingProcessesMemoryLimit");
    static final String SAFE_CALL_MEMORY_LIMIT = getString("SafeCallMemoryLimit");
    static final String WORKING_PROCESS_MEMORY_LIMIT = getString("WorkingProcessMemoryLimit");
    static final String CRITICAL_PROCESSES_TOTAL_MEMORY = getString("CriticalProcessesTotalMemory");
    static final String TEMPORARY_ALLOWED_PROCESSES_TOTAL_MEMORY =
        getString("TemporaryAllowedProcessesTotalMemory");
    static final String TEMPORARY_ALLOWED_PROCESSES_TOTAL_MEMORY_TIME_LIMIT =
        getString("TemporaryAllowedProcessesTotalMemoryTimeLimit");
    static final String WORKING_PROCESSES_PARAMETERS = getString("WorkingProcessesParameters");
    static final String INFOBASES_PER_WORKING_PROCESS_LIMIT =
        getString("InfobasesPerWorkingProcessLimit");
    static final String CONNECTIONS_PER_WORKING_PROCESS_LIMIT =
        getString("ConnectionsPerWorkingProcessLimit");
    static final String IP_PORT_MAIN_CLUSTER_MANAGER = getString("IPPortMainClusterManager");
    static final String IS_DEDICATED_MANAGERS = getString("IsDedicatedManagers");
    static final String IS_MAIN_SERVER = getString("IsMainServer");

    static final String DEPRECATED_IN_FIFTEEN = Messages.getString("Dialogs.DeprecatedInFifteen");
    static final String APPEARED_IN_FIFTEEN = Messages.getString("Dialogs.AppearedInFifteen");
    static final String APPLY = Messages.getString("Dialogs.Apply");
    static final String RESET = Messages.getString("Dialogs.Reset");
    static final String RESET_TO_PROF = Messages.getString("Dialogs.ResetToPROF");

    static final String MEGABYTES = getString("Megabytes");
    static final String MINUTES = getString("Minutes");

    static String getString(String key) {
      return Messages.getString("WorkingServerDialog." + key); //$NON-NLS-1$
    }
  }
}
