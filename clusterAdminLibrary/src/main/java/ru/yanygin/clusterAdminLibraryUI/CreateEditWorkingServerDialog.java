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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import ru.yanygin.clusterAdminLibrary.Server;

/** Dialog for create and edit Working Server parameters. */
public class CreateEditWorkingServerDialog extends Dialog {

  private UUID clusterId;
  private UUID workingServerId;
  private Server server;

  private Button btnIsDedicatedManagers;
  private Text txtServerName;
  private Text txtComputerName;
  private Text txtIpPort;
  private Text txtPortRange;

  private Text txtInfoBasesPerWorkingProcessLimit;
  private Text txtConnectionsPerWorkingProcessLimit;
  private Text txtIpPortMainManager;
  private Button btnIsMainServer;
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

  public UUID getNewWorkingServerId() {
    return workingServerId;
  }

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server
   * @param clusterId - cluster ID
   * @param workingServerId - working server ID
   */
  public CreateEditWorkingServerDialog(
      Shell parentShell, Server server, UUID clusterId, UUID workingServerId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    // super.configureShell(parentShell);
    // parentShell.setText("Parameters of the 1C:Enterprise infobase");

    this.server = server;
    this.clusterId = clusterId;
    this.workingServerId = workingServerId;
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
    lblServerName.setText(Messages.getString("WorkingServerDialog.ServerName")); //$NON-NLS-1$

    txtServerName = new Text(container, SWT.BORDER);
    txtServerName.setToolTipText(
        Messages.getString("WorkingServerDialog.ServerName")); //$NON-NLS-1$
    txtServerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblComputerName = new Label(container, SWT.NONE);
    lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblComputerName.setText(Messages.getString("WorkingServerDialog.ComputerName")); //$NON-NLS-1$

    txtComputerName = new Text(container, SWT.BORDER);
    txtComputerName.setToolTipText(
        Messages.getString("WorkingServerDialog.ComputerName")); //$NON-NLS-1$
    txtComputerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblIpPort = new Label(container, SWT.NONE);
    lblIpPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblIpPort.setText(Messages.getString("WorkingServerDialog.IPPort")); //$NON-NLS-1$

    txtIpPort = new Text(container, SWT.BORDER);
    txtIpPort.setToolTipText(Messages.getString("WorkingServerDialog.IPPort")); //$NON-NLS-1$
    txtIpPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblPortRange = new Label(container, SWT.NONE);
    lblPortRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPortRange.setText(Messages.getString("WorkingServerDialog.PortRange")); //$NON-NLS-1$

    txtPortRange = new Text(container, SWT.BORDER);
    txtPortRange.setToolTipText(Messages.getString("WorkingServerDialog.PortRange")); //$NON-NLS-1$
    txtPortRange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);

    Label lblSafeWorkingProcessesMemoryLimit = new Label(container, SWT.NONE);
    lblSafeWorkingProcessesMemoryLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSafeWorkingProcessesMemoryLimit.setText(
        Messages.getString("WorkingServerDialog.SafeWorkingProcessesMemoryLimit")); //$NON-NLS-1$

    txtSafeWorkingProcessesMemoryLimit = new Text(container, SWT.BORDER);
    txtSafeWorkingProcessesMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblSafeWorkingProcessesMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });

    txtSafeWorkingProcessesMemoryLimit.setToolTipText(
        Messages.getString("WorkingServerDialog.SafeWorkingProcessesMemoryLimit")); //$NON-NLS-1$
    txtSafeWorkingProcessesMemoryLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblSafeWorkingProcessesMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblSafeWorkingProcessesMemoryLimitMb =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblSafeWorkingProcessesMemoryLimitMb.minimumHeight = 8;
    lblSafeWorkingProcessesMemoryLimitMb.setLayoutData(gdlblSafeWorkingProcessesMemoryLimitMb);

    Label lblSafeCallMemoryLimit = new Label(container, SWT.NONE);
    lblSafeCallMemoryLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSafeCallMemoryLimit.setText(
        Messages.getString("WorkingServerDialog.SafeCallMemoryLimit")); //$NON-NLS-1$

    txtSafeCallMemoryLimit = new Text(container, SWT.BORDER);
    txtSafeCallMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblSafeCallMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtSafeCallMemoryLimit.setToolTipText(
        Messages.getString("WorkingServerDialog.SafeCallMemoryLimit")); //$NON-NLS-1$
    txtSafeCallMemoryLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblSafeCallMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblSafeCallMemoryLimitMb = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblSafeCallMemoryLimitMb.minimumHeight = 8;
    lblSafeCallMemoryLimitMb.setLayoutData(gdlblSafeCallMemoryLimitMb);

    Label lblWorkingProcessMemoryLimit = new Label(container, SWT.NONE);
    lblWorkingProcessMemoryLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
    lblWorkingProcessMemoryLimit.setText(
        Messages.getString("WorkingServerDialog.WorkingProcessMemoryLimit")); //$NON-NLS-1$

    txtWorkingProcessMemoryLimit = new Text(container, SWT.BORDER);
    txtWorkingProcessMemoryLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblWorkingProcessMemoryLimitMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtWorkingProcessMemoryLimit.setToolTipText(
        Messages.getString("WorkingServerDialog.WorkingProcessMemoryLimit")); //$NON-NLS-1$
    txtWorkingProcessMemoryLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    lblWorkingProcessMemoryLimitMb = new Label(container, SWT.NONE);
    GridData gdlblWorkingProcessMemoryLimitMb =
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdlblWorkingProcessMemoryLimitMb.minimumHeight = 8;
    lblWorkingProcessMemoryLimitMb.setLayoutData(gdlblWorkingProcessMemoryLimitMb);

    Label lblCriticalProcessesTotalMemory = new Label(container, SWT.NONE);
    lblCriticalProcessesTotalMemory.setText(
        Messages.getString("WorkingServerDialog.CriticalProcessesTotalMemory")); //$NON-NLS-1$
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
    txtCriticalProcessesTotalMemory.setToolTipText(
        Messages.getString("WorkingServerDialog.CriticalProcessesTotalMemory")); //$NON-NLS-1$
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
        Messages.getString(
            "WorkingServerDialog.TemporaryAllowedProcessesTotalMemoryLabel")); //$NON-NLS-1$

    txtTemporaryAllowedProcessesTotalMemory = new Text(container, SWT.BORDER);
    txtTemporaryAllowedProcessesTotalMemory.addModifyListener(
        new ModifyListener() { // NOSONAR
          @Override
          public void modifyText(ModifyEvent e) {
            lblTemporaryAllowedProcessesTotalMemoryMb.setText(convertToMegabytes((Text) e.widget));
          }
        });
    txtTemporaryAllowedProcessesTotalMemory.setToolTipText(
        Messages.getString(
            "WorkingServerDialog.TemporaryAllowedProcessesTotalMemoryToolTip")); //$NON-NLS-1$
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
        Messages.getString(
            "WorkingServerDialog.TemporaryAllowedProcessesTotalMemoryTimeLimitLabel")); //$NON-NLS-1$

    txtTemporaryAllowedProcessesTotalMemoryTimeLimit = new Text(container, SWT.BORDER);
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.setText(
                convertToMinutes((Text) e.widget));
          }
        });
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setToolTipText(
        Messages.getString(
            "WorkingServerDialog.TemporaryAllowedProcessesTotalMemoryTimeLimitToolTip")); //$NON-NLS-1$
    txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin = new Label(container, SWT.NONE);
    GridData gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.minimumHeight = 8;
    lblTemporaryAllowedProcessesTotalMemoryTimeLimitMin.setLayoutData(
        gdlblTemporaryAllowedProcessesTotalMemoryTimeLimitMin);

    Group groupWorkProcessesParams = new Group(container, SWT.NONE);
    groupWorkProcessesParams.setText(
        Messages.getString("WorkingServerDialog.WorkingProcessesParameters")); //$NON-NLS-1$
    GridLayout glgroupWorkProcessesParams = new GridLayout(2, true);
    glgroupWorkProcessesParams.verticalSpacing = 8;
    groupWorkProcessesParams.setLayout(glgroupWorkProcessesParams);
    GridData gdgroupWorkProcessesParams = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
    gdgroupWorkProcessesParams.widthHint = 424;
    groupWorkProcessesParams.setLayoutData(gdgroupWorkProcessesParams);

    Label lblInfoBasesPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.NONE);
    lblInfoBasesPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfoBasesPerWorkingProcessLimit.setText(
        Messages.getString("WorkingServerDialog.InfobasesPerWorkingProcessLimit")); //$NON-NLS-1$
    lblInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 35, 15);

    txtInfoBasesPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    txtInfoBasesPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtInfoBasesPerWorkingProcessLimit.setToolTipText(
        Messages.getString("WorkingServerDialog.InfobasesPerWorkingProcessLimit")); //$NON-NLS-1$
    txtInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 76, 21);

    Label lblConnectionsPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
    lblConnectionsPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    lblConnectionsPerWorkingProcessLimit.setText(
        Messages.getString("WorkingServerDialog.ConnectionsPerWorkingProcessLimit")); //$NON-NLS-1$
    lblConnectionsPerWorkingProcessLimit.setBounds(0, 0, 35, 15);

    txtConnectionsPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    txtConnectionsPerWorkingProcessLimit.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    txtConnectionsPerWorkingProcessLimit.setToolTipText(
        Messages.getString("WorkingServerDialog.ConnectionsPerWorkingProcessLimit")); //$NON-NLS-1$
    txtConnectionsPerWorkingProcessLimit.setBounds(0, 0, 76, 21);
    new Label(container, SWT.NONE);

    Label lblIpPortMainManager = new Label(container, SWT.NONE);
    lblIpPortMainManager.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblIpPortMainManager.setText(
        Messages.getString("WorkingServerDialog.IPPortMainClusterManager")); //$NON-NLS-1$

    txtIpPortMainManager = new Text(container, SWT.BORDER);
    txtIpPortMainManager.setToolTipText(
        Messages.getString("WorkingServerDialog.IPPortMainClusterManager")); //$NON-NLS-1$
    txtIpPortMainManager.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    btnIsDedicatedManagers = new Button(container, SWT.CHECK);
    btnIsDedicatedManagers.setText(
        Messages.getString("WorkingServerDialog.IsDedicatedManagers")); //$NON-NLS-1$
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    btnIsMainServer = new Button(container, SWT.CHECK);
    btnIsMainServer.setText(Messages.getString("WorkingServerDialog.IsMainServer")); //$NON-NLS-1$
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    initServerProperties();

    return container;
  }

  private void initServerProperties() {
    IWorkingServerInfo serverInfo;

    if (workingServerId != null) { // Редактируем существующий рабочий сервер
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

    } else { // Создаем новый рабочий сервер
      serverInfo = new WorkingServerInfo();

      txtServerName.setText(""); //$NON-NLS-1$
      txtPortRange.setText(""); //$NON-NLS-1$
      txtComputerName.setText(""); //$NON-NLS-1$

      txtInfoBasesPerWorkingProcessLimit.setText("8"); //$NON-NLS-1$
      txtConnectionsPerWorkingProcessLimit.setText("128"); //$NON-NLS-1$
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

      txtSafeWorkingProcessesMemoryLimit.setToolTipText(
          Messages.getString("Dialogs.DeprecatedInFifteen")); //$NON-NLS-1$
      txtWorkingProcessMemoryLimit.setToolTipText(
          Messages.getString("Dialogs.DeprecatedInFifteen")); //$NON-NLS-1$
    } else { // 8.3.15-
      txtCriticalProcessesTotalMemory.setEditable(false);
      txtTemporaryAllowedProcessesTotalMemory.setEditable(false);
      txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setEditable(false);

      txtCriticalProcessesTotalMemory.setToolTipText(
          Messages.getString("Dialogs.AppearedInFifteen")); //$NON-NLS-1$
      txtTemporaryAllowedProcessesTotalMemory.setToolTipText(
          Messages.getString("Dialogs.AppearedInFifteen")); //$NON-NLS-1$
      txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setToolTipText(
          Messages.getString("Dialogs.AppearedInFifteen")); //$NON-NLS-1$
    }

    if (workingServerId != null) { // У уже созданного кластера запрещено менять хост и порт
      txtServerName.setEditable(false);
      txtComputerName.setEditable(false);
      txtIpPort.setEditable(false);
    } else {
      // Почему новому серверу запрещено сразу ставить галочку Центральный сервер?
      btnIsMainServer.setEnabled(false);
      txtIpPortMainManager.setText("<auto>"); //$NON-NLS-1$
    }
    txtIpPortMainManager.setEditable(false);
  }

  private void resetToProf() {
    if (!server.isFifteenOrMoreAgentVersion()) { // 8.3.15-
      txtSafeWorkingProcessesMemoryLimit.setText("0"); //$NON-NLS-1$
      txtWorkingProcessMemoryLimit.setText("0"); //$NON-NLS-1$
    }
    txtSafeCallMemoryLimit.setText("0"); //$NON-NLS-1$
    txtInfoBasesPerWorkingProcessLimit.setText("8"); //$NON-NLS-1$
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
        control.setBackground(SWTResourceManager.getPinkColor());
        existsError = true;
      } else {
        control.setBackground(SWTResourceManager.getWhiteColor());
      }
    }

    List<Text> checksIntControls = new ArrayList<>();
    checksIntControls.add(txtIpPort);
    checksIntControls.add(txtInfoBasesPerWorkingProcessLimit);
    checksIntControls.add(txtConnectionsPerWorkingProcessLimit);

    for (Text control : checksIntControls) {
      try {
        Integer.parseInt(control.getText());
        control.setBackground(SWTResourceManager.getWhiteColor());
      } catch (Exception e) {
        control.setBackground(SWTResourceManager.getPinkColor());
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
        control.setBackground(SWTResourceManager.getWhiteColor());
      } catch (Exception e) {
        control.setBackground(SWTResourceManager.getPinkColor());
        existsError = true;
      }
    }

    try {
      String[] portRange = txtPortRange.getText().split(":"); //$NON-NLS-1$
      new PortRangeInfo(Integer.parseInt(portRange[1]), Integer.parseInt(portRange[0]));
      txtPortRange.setBackground(SWTResourceManager.getWhiteColor());
    } catch (Exception e) {
      txtPortRange.setBackground(SWTResourceManager.getPinkColor());
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
      workingServerInfo = new WorkingServerInfo();

      workingServerInfo.setHostName(txtComputerName.getText());
      workingServerInfo.setMainPort(Integer.parseInt(txtIpPort.getText()));
    } else {
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

    try {
      if (server.regWorkingServer(clusterId, workingServerInfo, workingServerId == null)) {
        workingServerId = workingServerInfo.getWorkingServerId();
        return true;
      }
    } catch (Exception excp) {
      var messageBox = new MessageBox(getParentShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
    }
    return false;
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

    Button buttonApply =
        createButton(
            parent,
            IDialogConstants.PROCEED_ID,
            Messages.getString("Dialogs.Apply"), //$NON-NLS-1$
            false);
    buttonApply.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            saveNewClusterProperties();
          }
        });

    Button buttonReset =
        createButton(
            parent,
            IDialogConstants.RETRY_ID,
            Messages.getString("Dialogs.Reset"), //$NON-NLS-1$
            false);
    buttonReset.setText(Messages.getString("Dialogs.Reset")); //$NON-NLS-1$
    buttonReset.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            initServerProperties();
          }
        });

    Button buttonResetToProf =
        createButton(
            parent,
            IDialogConstants.RETRY_ID,
            Messages.getString("Dialogs.ResetToPROF"), //$NON-NLS-1$
            false);
    buttonResetToProf.addMouseTrackListener(
        new MouseTrackAdapter() {
          @Override
          public void mouseEnter(MouseEvent e) {
            txtSafeCallMemoryLimit.setBackground(SWTResourceManager.getLightGreenColor());
            txtSafeWorkingProcessesMemoryLimit.setBackground(
                SWTResourceManager.getLightGreenColor());
            txtWorkingProcessMemoryLimit.setBackground(SWTResourceManager.getLightGreenColor());
            txtInfoBasesPerWorkingProcessLimit.setBackground(
                SWTResourceManager.getLightGreenColor());
          }

          @Override
          public void mouseExit(MouseEvent e) {
            txtSafeCallMemoryLimit.setBackground(SWTResourceManager.getWhiteColor());
            txtSafeWorkingProcessesMemoryLimit.setBackground(SWTResourceManager.getWhiteColor());
            txtWorkingProcessMemoryLimit.setBackground(SWTResourceManager.getWhiteColor());
            txtInfoBasesPerWorkingProcessLimit.setBackground(SWTResourceManager.getWhiteColor());
          }
        });
    buttonResetToProf.setText(Messages.getString("Dialogs.ResetToPROF")); //$NON-NLS-1$
    buttonResetToProf.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            resetToProf();
          }
        });
  }

  private String convertToMegabytes(Text textControl) {
    long inMb = Long.parseLong(textControl.getText()) / (1024 * 1024);
    return Long.toString(inMb)
        .concat(Messages.getString("WorkingServerDialog.Megabytes")); //$NON-NLS-1$
  }

  private String convertToMinutes(Text textControl) {
    long inMb = Long.parseLong(textControl.getText()) / (60);
    return Long.toString(inMb)
        .concat(Messages.getString("WorkingServerDialog.Minutes")); //$NON-NLS-1$
  }
}
