package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.wb.swt.SWTResourceManager;

import com._1c.v8.ibis.admin.ClusterInfo;
import com._1c.v8.ibis.admin.IClusterInfo;

import ru.yanygin.clusterAdminLibrary.Server;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;

public class CreateEditClusterDialog extends Dialog {
	
	private UUID clusterId;
	private Server server;
	
	private Button btnClusterRecyclingKillProblemProcesses;
	private Text txtClusterName;
	private Text txtComputerName;
	private Text txtIPPort;
	private Combo comboSecurityLevel;
	private Text txtLifeTimeLimit;
	private Text txtMaxMemorySize;
	private Text txtMaxMemoryTimeLimit;
	private Text txtClusterRecyclingErrorsCountThreshold;
	private Text txtExpirationTimeout;
	private Text txtSessionFaultToleranceLevel;
	private Combo comboLoadBalancingMode;
	private Button btnClusterRecyclingKillByMemoryWithDump;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public CreateEditClusterDialog(Shell parentShell, Server server, UUID clusterId) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterId = clusterId;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 12;
		gridLayout.numColumns = 2;
		
		Label lblClusterName = new Label(container, SWT.NONE);
		lblClusterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClusterName.setText(Messages.getString("ClusterDialog.ClusterName")); //$NON-NLS-1$
		
		txtClusterName = new Text(container, SWT.BORDER);
		txtClusterName.setToolTipText(Messages.getString("ClusterDialog.ClusterName")); //$NON-NLS-1$
		txtClusterName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblComputerName = new Label(container, SWT.NONE);
		lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComputerName.setText(Messages.getString("ClusterDialog.ComputerName")); //$NON-NLS-1$
		
		txtComputerName = new Text(container, SWT.BORDER);
		txtComputerName.setToolTipText(Messages.getString("ClusterDialog.ComputerName")); //$NON-NLS-1$
		txtComputerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblIPPort = new Label(container, SWT.NONE);
		lblIPPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIPPort.setText(Messages.getString("ClusterDialog.IPPort")); //$NON-NLS-1$
		
		txtIPPort = new Text(container, SWT.BORDER);
		txtIPPort.setToolTipText(Messages.getString("ClusterDialog.IPPort")); //$NON-NLS-1$
		txtIPPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSecurityLevel = new Label(container, SWT.NONE);
		lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityLevel.setText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		
		comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
		comboSecurityLevel.setVisibleItemCount(3);
		comboSecurityLevel.setTouchEnabled(true);
		comboSecurityLevel.setToolTipText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboSecurityLevel.add(Messages.getString("Dialogs.Disable")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.Disable"), 0); //$NON-NLS-1$
		comboSecurityLevel.add(Messages.getString("Dialogs.ConnectionOnly")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.ConnectionOnly"), 1); //$NON-NLS-1$
		comboSecurityLevel.add(Messages.getString("Dialogs.Constantly")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.Constantly"), 2); //$NON-NLS-1$
		comboSecurityLevel.select(0);
		
		Group groupWorkProcessesParams = new Group(container, SWT.NONE);
		groupWorkProcessesParams.setText(Messages.getString("ClusterDialog.RestartWorkProcesses")); //$NON-NLS-1$
		GridLayout glgroupWorkProcessesParams = new GridLayout(2, false);
		glgroupWorkProcessesParams.verticalSpacing = 8;
		groupWorkProcessesParams.setLayout(glgroupWorkProcessesParams);
		GridData gdgroupWorkProcessesParams = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gdgroupWorkProcessesParams.widthHint = 450;
		groupWorkProcessesParams.setLayoutData(gdgroupWorkProcessesParams);
		
		Label lblLifeTimeLimit = new Label(groupWorkProcessesParams, SWT.NONE);
		lblLifeTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLifeTimeLimit.setText(Messages.getString("ClusterDialog.RestartInterval")); //$NON-NLS-1$
		
		txtLifeTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtLifeTimeLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLifeTimeLimit.setToolTipText(Messages.getString("ClusterDialog.RestartInterval")); //$NON-NLS-1$
		
		Label lblMaxMemorySize = new Label(groupWorkProcessesParams, SWT.NONE);
		lblMaxMemorySize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxMemorySize.setText(Messages.getString("ClusterDialog.AllowedAmountOfMemoryWithLineBreak")); //$NON-NLS-1$
		lblMaxMemorySize.setBounds(0, 0, 35, 15);
		
		txtMaxMemorySize = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtMaxMemorySize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMaxMemorySize.setToolTipText(Messages.getString("ClusterDialog.AllowedAmountOfMemory")); //$NON-NLS-1$
		txtMaxMemorySize.setBounds(0, 0, 76, 21);
		
		Label lblMaxMemoryTimeLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
		lblMaxMemoryTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		lblMaxMemoryTimeLimit.setText(Messages.getString("ClusterDialog.IntervalExceedingAllowedAmountOfMemoryWithLineBreak")); //$NON-NLS-1$
		lblMaxMemoryTimeLimit.setBounds(0, 0, 35, 15);
		
		txtMaxMemoryTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtMaxMemoryTimeLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMaxMemoryTimeLimit.setToolTipText(Messages.getString("ClusterDialog.IntervalExceedingAllowedAmountOfMemory")); //$NON-NLS-1$
		txtMaxMemoryTimeLimit.setBounds(0, 0, 76, 21);
		
		Label lblAcceptableDeviationOfNumberOfServerErrors = new Label(groupWorkProcessesParams, SWT.WRAP);
		lblAcceptableDeviationOfNumberOfServerErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		lblAcceptableDeviationOfNumberOfServerErrors.setText(Messages.getString("ClusterDialog.AcceptableDeviationOfTheNumberOfServerErrorsWithLineBreak")); //$NON-NLS-1$
		lblAcceptableDeviationOfNumberOfServerErrors.setBounds(0, 0, 35, 30);
		
		txtClusterRecyclingErrorsCountThreshold = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtClusterRecyclingErrorsCountThreshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtClusterRecyclingErrorsCountThreshold.setToolTipText(Messages.getString("ClusterDialog.AcceptableDeviationOfTheNumberOfServerErrors")); //$NON-NLS-1$
		txtClusterRecyclingErrorsCountThreshold.setBounds(0, 0, 76, 21);
		
		btnClusterRecyclingKillProblemProcesses = new Button(groupWorkProcessesParams, SWT.CHECK);
		btnClusterRecyclingKillProblemProcesses.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
		btnClusterRecyclingKillProblemProcesses.setText(Messages.getString("ClusterDialog.ForceShutdownOfProblematicProcesses")); //$NON-NLS-1$
		
		btnClusterRecyclingKillByMemoryWithDump = new Button(groupWorkProcessesParams, SWT.CHECK);
		btnClusterRecyclingKillByMemoryWithDump.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnClusterRecyclingKillByMemoryWithDump.setText(Messages.getString("ClusterDialog.ClusterRecyclingKillByMemoryWithDump")); //$NON-NLS-1$
		
		Label lblExpirationTimeout = new Label(container, SWT.NONE);
		lblExpirationTimeout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExpirationTimeout.setText(Messages.getString("ClusterDialog.ShutDownProcessesStopAfterSecond")); //$NON-NLS-1$
		
		txtExpirationTimeout = new Text(container, SWT.BORDER);
		txtExpirationTimeout.setToolTipText(Messages.getString("ClusterDialog.ShutDownProcessesStopAfter")); //$NON-NLS-1$
		txtExpirationTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSessionFaultToleranceLevel = new Label(container, SWT.NONE);
		lblSessionFaultToleranceLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSessionFaultToleranceLevel.setText(Messages.getString("ClusterDialog.FaultToleranceLevel")); //$NON-NLS-1$
		
		txtSessionFaultToleranceLevel = new Text(container, SWT.BORDER);
		txtSessionFaultToleranceLevel.setToolTipText(Messages.getString("ClusterDialog.FaultToleranceLevel")); //$NON-NLS-1$
		txtSessionFaultToleranceLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLoadBalancingMode = new Label(container, SWT.NONE);
		lblLoadBalancingMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLoadBalancingMode.setText(Messages.getString("ClusterDialog.LoadBalancingMode")); //$NON-NLS-1$
		
		comboLoadBalancingMode = new Combo(container, SWT.READ_ONLY);
		comboLoadBalancingMode.setVisibleItemCount(2);
		comboLoadBalancingMode.setToolTipText(Messages.getString("ClusterDialog.LoadBalancingMode")); //$NON-NLS-1$
		comboLoadBalancingMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboLoadBalancingMode.add(Messages.getString("Dialogs.PerformancePriority")); //$NON-NLS-1$
		comboLoadBalancingMode.setData(Messages.getString("Dialogs.PerformancePriority"), 0); //$NON-NLS-1$
		comboLoadBalancingMode.add(Messages.getString("Dialogs.MemoryPriority")); //$NON-NLS-1$
		comboLoadBalancingMode.setData(Messages.getString("Dialogs.MemoryPriority"), 1); //$NON-NLS-1$
		comboLoadBalancingMode.select(0);

		initServerProperties();
		
		return container;
	}

	private void initServerProperties() {
		IClusterInfo clusterInfo;
		
		if (clusterId != null) {
			clusterInfo = server.getClusterInfo(clusterId);
			
			txtClusterName.setText(clusterInfo.getName());
			txtComputerName.setText(clusterInfo.getHostName());
		} else {
			clusterInfo = new ClusterInfo();
			
			txtClusterName.setText(""); //$NON-NLS-1$
			txtComputerName.setText(""); //$NON-NLS-1$
		}
		
		
		txtIPPort.setText(Integer.toString(clusterInfo.getMainPort()));
		comboSecurityLevel.select(clusterInfo.getSecurityLevel());
		
		txtLifeTimeLimit.setText(Integer.toString(clusterInfo.getLifeTimeLimit()));
		txtMaxMemorySize.setText(Integer.toString(clusterInfo.getMaxMemorySize())); // 8.3.15-
		txtMaxMemoryTimeLimit.setText(Integer.toString(clusterInfo.getMaxMemoryTimeLimit())); // 8.3.15-
		txtClusterRecyclingErrorsCountThreshold.setText(Integer.toString(clusterInfo.getClusterRecyclingErrorsCountThreshold())); // 8.3.15-
		btnClusterRecyclingKillProblemProcesses.setSelection(clusterInfo.isClusterRecyclingKillProblemProcesses());
		btnClusterRecyclingKillByMemoryWithDump.setSelection(clusterInfo.isClusterRecyclingKillByMemoryWithDump()); // 8.3.15+
					
		txtExpirationTimeout.setText(Integer.toString(clusterInfo.getExpirationTimeout()));
		txtSessionFaultToleranceLevel.setText(Integer.toString(clusterInfo.getSessionFaultToleranceLevel()));
		comboLoadBalancingMode.select(clusterInfo.getLoadBalancingMode());
		
		if (server.isFifteenOrOlderAgentVersion()) { // 8.3.15+
			txtMaxMemorySize.setEditable(false);
			txtMaxMemoryTimeLimit.setEditable(false);
			txtClusterRecyclingErrorsCountThreshold.setEditable(false);
			
			txtMaxMemorySize.setToolTipText(Messages.getString("Dialogs.DeprecatedInFifteen")); //$NON-NLS-1$
			txtMaxMemoryTimeLimit.setToolTipText(Messages.getString("Dialogs.DeprecatedInFifteen")); //$NON-NLS-1$
			txtClusterRecyclingErrorsCountThreshold.setToolTipText(Messages.getString("Dialogs.DeprecatedInFifteen")); //$NON-NLS-1$
		} else {
			btnClusterRecyclingKillByMemoryWithDump.setEnabled(false);
			
			btnClusterRecyclingKillByMemoryWithDump.setToolTipText(Messages.getString("Dialogs.AppearedInFifteen")); //$NON-NLS-1$
		}

		// У уже созданного кластера запрещено менять хост и порт
		if (clusterId != null) {
			txtComputerName.setEditable(false);
			txtIPPort.setEditable(false);
		}
		
	}

	private void resetToProf() {
		comboLoadBalancingMode.select(0);
	}
	
	private boolean checkVariablesFromControls() {
		
		var existsError = false;
		
		List<Text> checksTextControls = new ArrayList<>();
		checksTextControls.add(txtClusterName);
		checksTextControls.add(txtComputerName);
		checksTextControls.add(txtIPPort);
		
		for (Text control : checksTextControls) {
			if (control.getText().isBlank()) {
				control.setBackground(SWTResourceManager.getColor(255, 204, 204));
				existsError = true;
			} else {
				control.setBackground(SWTResourceManager.getColor(255, 255, 255));
			}			
		}

		List<Text> checksIntControls = new ArrayList<>();
		checksIntControls.add(txtIPPort);
		checksIntControls.add(txtLifeTimeLimit);
		checksIntControls.add(txtMaxMemorySize);
		checksIntControls.add(txtMaxMemoryTimeLimit);
		checksIntControls.add(txtClusterRecyclingErrorsCountThreshold);
		checksIntControls.add(txtExpirationTimeout);
		checksIntControls.add(txtSessionFaultToleranceLevel);

		for (Text control : checksIntControls) {
			try {
				Integer.parseInt(control.getText());
				control.setBackground(SWTResourceManager.getColor(255, 255, 255));
			} catch (Exception e) {
				control.setBackground(SWTResourceManager.getColor(255, 204, 204));
				existsError = true;
			}
		}
		
		return existsError;
	}
	
	private boolean saveNewClusterProperties() {
		if (checkVariablesFromControls())
			return false;
		
		IClusterInfo clusterInfo;
		
		if (clusterId == null) {
			clusterInfo = new ClusterInfo();
	
			clusterInfo.setHostName(txtComputerName.getText());
			clusterInfo.setMainPort(Integer.parseInt(txtIPPort.getText()));
		} else {		
			clusterInfo = server.getClusterInfo(clusterId);
		}
		
		clusterInfo.setName(txtClusterName.getText());
		
		clusterInfo.setSecurityLevel((int) comboSecurityLevel.getData(comboSecurityLevel.getText()));

		clusterInfo.setLifeTimeLimit(Integer.parseInt(txtLifeTimeLimit.getText()));
		clusterInfo.setClusterRecyclingKillProblemProcesses(btnClusterRecyclingKillProblemProcesses.getSelection());

		if (server.isFifteenOrOlderAgentVersion()) // 8.3.15+
			clusterInfo.setClusterRecyclingKillByMemoryWithDump(btnClusterRecyclingKillByMemoryWithDump.getSelection());
		else {
			clusterInfo.setMaxMemorySize(Integer.parseInt(txtMaxMemorySize.getText()));
			clusterInfo.setMaxMemoryTimeLimit(Integer.parseInt(txtMaxMemoryTimeLimit.getText()));
			clusterInfo.setClusterRecyclingErrorsCountThreshold(Integer.parseInt(txtClusterRecyclingErrorsCountThreshold.getText()));
		}

		clusterInfo.setExpirationTimeout(Integer.parseInt(txtExpirationTimeout.getText()));
		clusterInfo.setSessionFaultToleranceLevel(Integer.parseInt(txtSessionFaultToleranceLevel.getText()));
		clusterInfo.setLoadBalancingMode((int) comboLoadBalancingMode.getData(comboLoadBalancingMode.getText()));
		

		try {
			if (server.regCluster(clusterInfo))
				return true;
		} catch (Exception excp) {
			var messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}
		return false;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button buttonOK = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (saveNewClusterProperties())
					close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, Messages.getString("Dialogs.Apply"), false); //$NON-NLS-1$
		buttonApply.setEnabled(clusterId != null);
		buttonApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveNewClusterProperties();
			}
		});
		Button buttonReset = createButton(parent, IDialogConstants.RETRY_ID, Messages.getString("Dialogs.Reset"), false); //$NON-NLS-1$
		buttonReset.setText(Messages.getString("Dialogs.Reset")); //$NON-NLS-1$
		buttonReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initServerProperties();
			}
		});
		Button buttonResetToProf = createButton(parent, IDialogConstants.RETRY_ID, Messages.getString("Dialogs.ResetToPROF"), false); //$NON-NLS-1$
		buttonResetToProf.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				comboLoadBalancingMode.setBackground(SWTResourceManager.getLightGreenColor());
			}
			@Override
			public void mouseExit(MouseEvent e) {
				comboLoadBalancingMode.setBackground(SWTResourceManager.getWhiteColor());
			}
		});
		buttonResetToProf.setText(Messages.getString("Dialogs.ResetToPROF")); //$NON-NLS-1$
		buttonResetToProf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetToProf();
			}
		});

	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(560, 550);
	}

}
