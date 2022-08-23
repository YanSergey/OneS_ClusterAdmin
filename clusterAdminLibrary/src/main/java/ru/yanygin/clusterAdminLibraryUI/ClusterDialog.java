package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.ClusterInfo;
import com._1c.v8.ibis.admin.IClusterInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог редактирования параметров кластера. */
public class ClusterDialog extends Dialog {

  private static final String EMPTY_STRING = ""; //$NON-NLS-1$

  private Server server;
  private UUID clusterId;

  // Controls
  private Text txtClusterName;
  private Text txtComputerName;
  private Text txtIpPort;
  private Text txtLifeTimeLimit;
  private Text txtMaxMemorySize;
  private Text txtMaxMemoryTimeLimit;
  private Text txtClusterRecyclingErrorsCountThreshold;
  private Text txtExpirationTimeout;
  private Text txtSessionFaultToleranceLevel;

  private Combo comboSecurityLevel;
  private Combo comboLoadBalancingMode;

  private Button btnClusterRecyclingKillProblemProcesses;
  private Button btnClusterRecyclingKillByMemoryWithDump;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server
   * @param clusterId - cluster ID
   */
  public ClusterDialog(Shell parentShell, Server server, UUID clusterId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
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
    gridLayout.makeColumnsEqualWidth = true;
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 12;
    gridLayout.numColumns = 2;

    Label lblClusterName = new Label(container, SWT.NONE);
    lblClusterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblClusterName.setText(Strings.CLUSTER_NAME);

    txtClusterName = new Text(container, SWT.BORDER);
    GridData gdtxtClusterName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtClusterName.widthHint = 200;
    txtClusterName.setLayoutData(gdtxtClusterName);

    Label lblComputerName = new Label(container, SWT.NONE);
    lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblComputerName.setText(Strings.COMPUTER_NAME);

    txtComputerName = new Text(container, SWT.BORDER);
    GridData gdtxtComputerName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtComputerName.widthHint = 200;
    txtComputerName.setLayoutData(gdtxtComputerName);

    Label lblIpPort = new Label(container, SWT.NONE);
    lblIpPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblIpPort.setText(Strings.IP_PORT);

    txtIpPort = new Text(container, SWT.BORDER);
    GridData gdtxtIpPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtIpPort.widthHint = 200;
    txtIpPort.setLayoutData(gdtxtIpPort);

    Label lblSecurityLevel = new Label(container, SWT.NONE);
    lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSecurityLevel.setText(Strings.SECURITY_LEVEL);

    comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
    comboSecurityLevel.setVisibleItemCount(3);
    comboSecurityLevel.setTouchEnabled(true);
    GridData gdcomboSecurityLevel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdcomboSecurityLevel.widthHint = 200;
    comboSecurityLevel.setLayoutData(gdcomboSecurityLevel);

    comboSecurityLevel.add(Strings.SECURITY_LEVEL_DISABLE);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_DISABLE, 0);
    comboSecurityLevel.add(Strings.SECURITY_LEVEL_CONNECTIONONLY);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_CONNECTIONONLY, 1);
    comboSecurityLevel.add(Strings.SECURITY_LEVEL_CONSTANTLY);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_CONSTANTLY, 2);
    comboSecurityLevel.select(0);

    Group groupWorkProcessesParams = new Group(container, SWT.NONE);
    groupWorkProcessesParams.setText(Strings.WP_RESTART);
    GridLayout glgroupWorkProcessesParams = new GridLayout(2, true);
    glgroupWorkProcessesParams.verticalSpacing = 8;
    groupWorkProcessesParams.setLayout(glgroupWorkProcessesParams);
    groupWorkProcessesParams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

    Label lblLifeTimeLimit = new Label(groupWorkProcessesParams, SWT.NONE);
    lblLifeTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblLifeTimeLimit.setText(Strings.WP_RESTART_INTERVAL);

    txtLifeTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    GridData gdtxtLifeTimeLimit = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdtxtLifeTimeLimit.widthHint = 200;
    txtLifeTimeLimit.setLayoutData(gdtxtLifeTimeLimit);

    Label lblMaxMemorySize = new Label(groupWorkProcessesParams, SWT.NONE);
    lblMaxMemorySize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblMaxMemorySize.setText(Strings.WP_ALLOWED_AMOUNT_OFMEMORY);

    txtMaxMemorySize = new Text(groupWorkProcessesParams, SWT.BORDER);
    GridData gdtxtMaxMemorySize = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdtxtMaxMemorySize.widthHint = 200;
    txtMaxMemorySize.setLayoutData(gdtxtMaxMemorySize);

    Label lblMaxMemoryTimeLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
    lblMaxMemoryTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
    lblMaxMemoryTimeLimit.setText(Strings.WP_INTERVAL_EXCEEDING_ALLOWED_AMOUNT_OFMEMORY);

    txtMaxMemoryTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
    GridData gdtxtMaxMemoryTimeLimit = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdtxtMaxMemoryTimeLimit.widthHint = 200;
    txtMaxMemoryTimeLimit.setLayoutData(gdtxtMaxMemoryTimeLimit);

    Label lblAcceptableDeviationOfNumberOfServerErrors =
        new Label(groupWorkProcessesParams, SWT.WRAP);
    lblAcceptableDeviationOfNumberOfServerErrors.setLayoutData(
        new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
    lblAcceptableDeviationOfNumberOfServerErrors.setText(
        Strings.ACCEPTABLE_DEVIATION_OFTHENUMBER_OFSERVER_ERRORS);

    txtClusterRecyclingErrorsCountThreshold = new Text(groupWorkProcessesParams, SWT.BORDER);
    GridData gdtxtClusterRecyclingErrorsCountThreshold =
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdtxtClusterRecyclingErrorsCountThreshold.widthHint = 200;
    txtClusterRecyclingErrorsCountThreshold.setLayoutData(
        gdtxtClusterRecyclingErrorsCountThreshold);

    btnClusterRecyclingKillProblemProcesses = new Button(groupWorkProcessesParams, SWT.CHECK);
    btnClusterRecyclingKillProblemProcesses.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
    btnClusterRecyclingKillProblemProcesses.setText(Strings.FORCE_SHUTDOWN_OFPROBLEMATIC_PROCESSES);

    btnClusterRecyclingKillByMemoryWithDump = new Button(groupWorkProcessesParams, SWT.CHECK);
    btnClusterRecyclingKillByMemoryWithDump.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
    btnClusterRecyclingKillByMemoryWithDump.setText(
        Strings.CLUSTER_RECYCLING_KILL_BYMEMORY_WITHDUMP);

    Label lblExpirationTimeout = new Label(container, SWT.NONE);
    lblExpirationTimeout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblExpirationTimeout.setText(Strings.SHUTDOWN_PROCESSES_STOP_AFTER);

    txtExpirationTimeout = new Text(container, SWT.BORDER);
    GridData gdtxtExpirationTimeout = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtExpirationTimeout.widthHint = 200;
    txtExpirationTimeout.setLayoutData(gdtxtExpirationTimeout);

    Label lblSessionFaultToleranceLevel = new Label(container, SWT.NONE);
    lblSessionFaultToleranceLevel.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSessionFaultToleranceLevel.setText(Strings.FAULT_TOLERANCE_LEVEL);

    txtSessionFaultToleranceLevel = new Text(container, SWT.BORDER);
    GridData gdtxtSessionFaultToleranceLevel =
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtSessionFaultToleranceLevel.widthHint = 200;
    txtSessionFaultToleranceLevel.setLayoutData(gdtxtSessionFaultToleranceLevel);

    Label lblLoadBalancingMode = new Label(container, SWT.NONE);
    lblLoadBalancingMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblLoadBalancingMode.setText(Strings.LOAD_BALANCING_MODE);

    comboLoadBalancingMode = new Combo(container, SWT.READ_ONLY);
    comboLoadBalancingMode.setVisibleItemCount(2);
    GridData gdcomboLoadBalancingMode = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdcomboLoadBalancingMode.widthHint = 200;
    comboLoadBalancingMode.setLayoutData(gdcomboLoadBalancingMode);

    comboLoadBalancingMode.add(Strings.LOAD_BALANCING_MODE_PERFORMANCEPRIORITY);
    comboLoadBalancingMode.setData(Strings.LOAD_BALANCING_MODE_PERFORMANCEPRIORITY, 0);
    comboLoadBalancingMode.add(Strings.LOAD_BALANCING_MODE_MEMORYPRIORITY);
    comboLoadBalancingMode.setData(Strings.LOAD_BALANCING_MODE_MEMORYPRIORITY, 1);
    comboLoadBalancingMode.select(0);

    initProperties();

    return container;
  }

  private void initProperties() {
    IClusterInfo clusterInfo;

    if (clusterId != null) {
      clusterInfo = server.getClusterInfo(clusterId);

      txtClusterName.setText(clusterInfo.getName());
      txtComputerName.setText(clusterInfo.getHostName());
    } else {
      clusterInfo = new ClusterInfo();

      txtClusterName.setText(EMPTY_STRING);
      txtComputerName.setText(EMPTY_STRING);
    }

    txtIpPort.setText(Integer.toString(clusterInfo.getMainPort()));
    comboSecurityLevel.select(clusterInfo.getSecurityLevel());

    txtLifeTimeLimit.setText(Integer.toString(clusterInfo.getLifeTimeLimit()));
    txtMaxMemorySize.setText(Integer.toString(clusterInfo.getMaxMemorySize())); // 8.3.15-
    txtMaxMemoryTimeLimit.setText(Integer.toString(clusterInfo.getMaxMemoryTimeLimit())); // 8.3.15-
    txtClusterRecyclingErrorsCountThreshold.setText(
        Integer.toString(clusterInfo.getClusterRecyclingErrorsCountThreshold())); // 8.3.15-
    btnClusterRecyclingKillProblemProcesses.setSelection(
        clusterInfo.isClusterRecyclingKillProblemProcesses());
    btnClusterRecyclingKillByMemoryWithDump.setSelection(
        clusterInfo.isClusterRecyclingKillByMemoryWithDump()); // 8.3.15+

    txtExpirationTimeout.setText(Integer.toString(clusterInfo.getExpirationTimeout()));
    txtSessionFaultToleranceLevel.setText(
        Integer.toString(clusterInfo.getSessionFaultToleranceLevel()));
    comboLoadBalancingMode.select(clusterInfo.getLoadBalancingMode());

    if (server.isFifteenOrMoreAgentVersion()) { // 8.3.15+
      txtMaxMemorySize.setEditable(false);
      txtMaxMemoryTimeLimit.setEditable(false);
      txtClusterRecyclingErrorsCountThreshold.setEditable(false);

      txtMaxMemorySize.setToolTipText(Strings.DEPRECATED_IN_FIFTEEN);
      txtMaxMemoryTimeLimit.setToolTipText(Strings.DEPRECATED_IN_FIFTEEN);
      txtClusterRecyclingErrorsCountThreshold.setToolTipText(Strings.DEPRECATED_IN_FIFTEEN);
    } else {
      btnClusterRecyclingKillByMemoryWithDump.setEnabled(false);
      btnClusterRecyclingKillByMemoryWithDump.setToolTipText(Strings.APPEARED_IN_FIFTEEN);
    }

    // У уже созданного кластера запрещено менять хост и порт
    if (clusterId != null) {
      txtComputerName.setEditable(false);
      txtIpPort.setEditable(false);
    }
  }

  private boolean checkVariablesFromControls() {

    var existsError = false;

    List<Text> checksTextControls = new ArrayList<>();
    checksTextControls.add(txtClusterName);
    checksTextControls.add(txtComputerName);
    checksTextControls.add(txtIpPort);

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
    checksIntControls.add(txtLifeTimeLimit);
    checksIntControls.add(txtMaxMemorySize);
    checksIntControls.add(txtMaxMemoryTimeLimit);
    checksIntControls.add(txtClusterRecyclingErrorsCountThreshold);
    checksIntControls.add(txtExpirationTimeout);
    checksIntControls.add(txtSessionFaultToleranceLevel);

    for (Text control : checksIntControls) {
      try {
        Integer.parseInt(control.getText());
        control.setBackground(Helper.getWhiteColor());
      } catch (Exception e) {
        control.setBackground(Helper.getPinkColor());
        existsError = true;
      }
    }

    return existsError;
  }

  private boolean saveNewClusterProperties() {
    if (checkVariablesFromControls()) {
      return false;
    }

    IClusterInfo clusterInfo;

    if (clusterId == null) {
      clusterInfo = new ClusterInfo();

      clusterInfo.setHostName(txtComputerName.getText());
      clusterInfo.setMainPort(Integer.parseInt(txtIpPort.getText()));
    } else {
      clusterInfo = server.getClusterInfo(clusterId);
    }

    clusterInfo.setName(txtClusterName.getText());

    clusterInfo.setSecurityLevel((int) comboSecurityLevel.getData(comboSecurityLevel.getText()));

    clusterInfo.setLifeTimeLimit(Integer.parseInt(txtLifeTimeLimit.getText()));
    clusterInfo.setClusterRecyclingKillProblemProcesses(
        btnClusterRecyclingKillProblemProcesses.getSelection());

    if (server.isFifteenOrMoreAgentVersion()) { // 8.3.15+
      clusterInfo.setClusterRecyclingKillByMemoryWithDump(
          btnClusterRecyclingKillByMemoryWithDump.getSelection());
    } else {
      clusterInfo.setMaxMemorySize(Integer.parseInt(txtMaxMemorySize.getText()));
      clusterInfo.setMaxMemoryTimeLimit(Integer.parseInt(txtMaxMemoryTimeLimit.getText()));
      clusterInfo.setClusterRecyclingErrorsCountThreshold(
          Integer.parseInt(txtClusterRecyclingErrorsCountThreshold.getText()));
    }

    clusterInfo.setExpirationTimeout(Integer.parseInt(txtExpirationTimeout.getText()));
    clusterInfo.setSessionFaultToleranceLevel(
        Integer.parseInt(txtSessionFaultToleranceLevel.getText()));
    clusterInfo.setLoadBalancingMode(
        (int) comboLoadBalancingMode.getData(comboLoadBalancingMode.getText()));

    try {
      if (server.regCluster(clusterInfo)) {
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

    Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, Strings.APPLY, false);
    buttonApply.setEnabled(clusterId != null);
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
            initProperties();
          }
        });

    Button buttonResetToProf =
        createButton(parent, IDialogConstants.RETRY_ID, Strings.RESET_TO_PROF, false);
    buttonResetToProf.addMouseTrackListener(
        new MouseTrackAdapter() {
          @Override
          public void mouseEnter(MouseEvent e) {
            comboLoadBalancingMode.setBackground(Helper.getLightGreenColor());
          }

          @Override
          public void mouseExit(MouseEvent e) {
            comboLoadBalancingMode.setBackground(Helper.getWhiteColor());
          }
        });
    buttonResetToProf.setText(Strings.RESET_TO_PROF);
    buttonResetToProf.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            resetToProf();
          }
        });
  }

  private void resetToProf() {
    comboLoadBalancingMode.select(0);
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String CLUSTER_NAME = getString("ClusterName");
    static final String COMPUTER_NAME = getString("ComputerName");
    static final String IP_PORT = getString("IPPort");
    static final String SECURITY_LEVEL = Messages.getString("Dialogs.SecurityLevel");
    static final String SECURITY_LEVEL_DISABLE = Messages.getString("Dialogs.Disable");
    static final String SECURITY_LEVEL_CONNECTIONONLY =
        Messages.getString("Dialogs.ConnectionOnly");
    static final String SECURITY_LEVEL_CONSTANTLY = Messages.getString("Dialogs.Constantly");
    static final String WP_RESTART = getString("WorkingProcessesRestart");
    static final String WP_RESTART_INTERVAL = getString("WorkingProcessesRestartInterval");
    static final String WP_ALLOWED_AMOUNT_OFMEMORY =
        getString("WorkingProcessesAllowedAmountOfMemory");
    static final String WP_INTERVAL_EXCEEDING_ALLOWED_AMOUNT_OFMEMORY =
        getString("WorkingProcessesIntervalExceedingAllowedAmountOfMemory");
    static final String ACCEPTABLE_DEVIATION_OFTHENUMBER_OFSERVER_ERRORS =
        getString("AcceptableDeviationOfTheNumberOfServerErrors");
    static final String FORCE_SHUTDOWN_OFPROBLEMATIC_PROCESSES =
        getString("ForceShutdownOfProblematicProcesses");
    static final String CLUSTER_RECYCLING_KILL_BYMEMORY_WITHDUMP =
        getString("ClusterRecyclingKillByMemoryWithDump");
    static final String SHUTDOWN_PROCESSES_STOP_AFTER = getString("ShutDownProcessesStopAfter");
    static final String FAULT_TOLERANCE_LEVEL = getString("FaultToleranceLevel");
    static final String LOAD_BALANCING_MODE = getString("LoadBalancingMode");
    static final String LOAD_BALANCING_MODE_PERFORMANCEPRIORITY =
        getString("LoadBalancingModePerformancePriority");
    static final String LOAD_BALANCING_MODE_MEMORYPRIORITY =
        getString("LoadBalancingModeMemoryPriority");
    static final String DEPRECATED_IN_FIFTEEN = Messages.getString("Dialogs.DeprecatedInFifteen");
    static final String APPEARED_IN_FIFTEEN = Messages.getString("Dialogs.AppearedInFifteen");
    static final String APPLY = Messages.getString("Dialogs.Apply");
    static final String RESET = Messages.getString("Dialogs.Reset");
    static final String RESET_TO_PROF = Messages.getString("Dialogs.ResetToPROF");

    static String getString(String key) {
      return Messages.getString("ClusterDialog." + key); //$NON-NLS-1$
    }
  }
}
