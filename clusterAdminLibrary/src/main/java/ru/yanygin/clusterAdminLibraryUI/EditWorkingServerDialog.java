package ru.yanygin.clusterAdminLibraryUI;

import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.ClusterInfo;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IPortRangeInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;

import ru.yanygin.clusterAdminLibrary.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Group;

public class EditWorkingServerDialog extends Dialog {
	
//	private IClusterInfo clusterInfo;
	private UUID clusterId;
	private UUID workingServerId;
	private Server server;
	
	private Button btnIsDedicatedManagers;
	private Text txtServerName;
	private Text txtComputerName;
	private Text txtIPPort;
	private Text txtPortRange;

	// fields of infobase
	private String clusterName;
	private String computerName;
	private int ipPort;
	private int securityLevel;
	
	private int wpLifeTimeLimit;
	private int wpMaxMemorySize;
	private int wpMaxMemoryTimeLimit;
	private int clusterRecyclingErrorsCountThreshold;
	private boolean clusterRecyclingKillProblemProcesses;
	
	private int expirationTimeout;
	private int faultToleranceLevel;
	private int loadBalancingMode;
	private Text txtInfoBasesPerWorkingProcessLimit;
	private Text txtConnectionsPerWorkingProcessLimit;
	private Text txtIPPortMainManager;
	private Button btnIsMainServer;
	private Text txtWorkingProcessMemoryLimit;
	private Text txtSafeCallMemoryLimit;
	private Text txtCriticalProcessesTotalMemory;
	private Text txtTemporaryAllowedProcessesTotalMemory;
	private Text txtTemporaryAllowedProcessesTotalMemoryTimeLimit;
	private Text txtSafeWorkingProcessesMemoryLimit;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditWorkingServerDialog(Shell parentShell, Server server, UUID clusterId, UUID workingServerId) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterId = clusterId;
		this.workingServerId = workingServerId;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
//				extractClusterVariablesFromControls();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 12;
		gridLayout.numColumns = 3;
		
		Label lblServerName = new Label(container, SWT.NONE);
		lblServerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerName.setText("Server name");
		
		txtServerName = new Text(container, SWT.BORDER);
		txtServerName.setEditable(false);
		txtServerName.setToolTipText("Server name");
		txtServerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblComputerName = new Label(container, SWT.NONE);
		lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComputerName.setText("Computer name");
		
		txtComputerName = new Text(container, SWT.BORDER);
		txtComputerName.setEditable(false);
		txtComputerName.setToolTipText("Computer Name");
		txtComputerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblIPPort = new Label(container, SWT.NONE);
		lblIPPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIPPort.setText("IP Port");
		
		txtIPPort = new Text(container, SWT.BORDER);
		txtIPPort.setEditable(false);
		txtIPPort.setToolTipText("IP Port");
		txtIPPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblPortRange = new Label(container, SWT.NONE);
		lblPortRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPortRange.setText("Port range");
		
		txtPortRange = new Text(container, SWT.BORDER);
		txtPortRange.setToolTipText("Port range");
		txtPortRange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblSafeWorkingProcessesMemoryLimit = new Label(container, SWT.NONE);
		lblSafeWorkingProcessesMemoryLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeWorkingProcessesMemoryLimit.setText("Safe working processes memory limit (byte)");
		
		txtSafeWorkingProcessesMemoryLimit = new Text(container, SWT.BORDER);
		txtSafeWorkingProcessesMemoryLimit.setToolTipText("SafeWorkingProcessesMemoryLimit");
		txtSafeWorkingProcessesMemoryLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblWPLimirMb_5 = new Label(container, SWT.NONE);
		lblWPLimirMb_5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblWPLimirMb_5.setText("20 Mb");
		
		Label lblSafeCallMemoryLimit = new Label(container, SWT.NONE);
		lblSafeCallMemoryLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeCallMemoryLimit.setText("Safe call memory limit (byte)");
		
		txtSafeCallMemoryLimit = new Text(container, SWT.BORDER);
		txtSafeCallMemoryLimit.setToolTipText("SafeCallMemoryLimit");
		txtSafeCallMemoryLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblWPLimirMb_1 = new Label(container, SWT.NONE);
		lblWPLimirMb_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblWPLimirMb_1.setText("20 Mb");
		
		Label lblWorkingProcessMemoryLimit = new Label(container, SWT.NONE);
		lblWorkingProcessMemoryLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblWorkingProcessMemoryLimit.setText("Working process memory limit  (byte)");
		
		txtWorkingProcessMemoryLimit = new Text(container, SWT.BORDER);
		txtWorkingProcessMemoryLimit.setToolTipText("WorkingProcessMemoryLimit");
		txtWorkingProcessMemoryLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblWPLimirMb = new Label(container, SWT.NONE);
		GridData gd_lblWPLimirMb = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblWPLimirMb.minimumWidth = 20;
		lblWPLimirMb.setLayoutData(gd_lblWPLimirMb);
		lblWPLimirMb.setText("20 Mb");
		
		Label lblCriticalProcessesTotalMemory = new Label(container, SWT.NONE);
		lblCriticalProcessesTotalMemory.setText("Critical processes total memory (byte)");
		lblCriticalProcessesTotalMemory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtCriticalProcessesTotalMemory = new Text(container, SWT.BORDER);
		txtCriticalProcessesTotalMemory.setToolTipText("CriticalProcessesTotalMemory");
		txtCriticalProcessesTotalMemory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblWPLimirMb_2 = new Label(container, SWT.NONE);
		lblWPLimirMb_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblWPLimirMb_2.setText("20 Mb");
		
		Label lblTemporaryAllowedProcessesTotalMemory = new Label(container, SWT.NONE);
		lblTemporaryAllowedProcessesTotalMemory.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		lblTemporaryAllowedProcessesTotalMemory.setText("Temporary allowed\r\nprocesses total memory (byte)");
		
		txtTemporaryAllowedProcessesTotalMemory = new Text(container, SWT.BORDER);
		txtTemporaryAllowedProcessesTotalMemory.setToolTipText("Temporary allowed processes total memory");
		txtTemporaryAllowedProcessesTotalMemory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblWPLimirMb_3 = new Label(container, SWT.NONE);
		lblWPLimirMb_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblWPLimirMb_3.setText("20 Mb");
		
		Label lblTemporaryAllowedProcessesTotalMemoryTimeLimit = new Label(container, SWT.NONE);
		lblTemporaryAllowedProcessesTotalMemoryTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTemporaryAllowedProcessesTotalMemoryTimeLimit.setText("Temporary allowed processes\r\ntotal memory time limit (second)");
		
		txtTemporaryAllowedProcessesTotalMemoryTimeLimit = new Text(container, SWT.BORDER);
		txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setToolTipText("Temporary allowed processes total memory time limit");
		txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblMin = new Label(container, SWT.NONE);
		lblMin.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblMin.setText("20 min");
		
		Group groupWorkProcessesParams = new Group(container, SWT.NONE);
		groupWorkProcessesParams.setText("Working processes parameters");
		GridLayout gl_groupWorkProcessesParams = new GridLayout(2, true);
		gl_groupWorkProcessesParams.verticalSpacing = 8;
		groupWorkProcessesParams.setLayout(gl_groupWorkProcessesParams);
		GridData gd_groupWorkProcessesParams = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_groupWorkProcessesParams.widthHint = 450;
		groupWorkProcessesParams.setLayoutData(gd_groupWorkProcessesParams);
		
		Label lblInfoBasesPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.NONE);
		lblInfoBasesPerWorkingProcessLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfoBasesPerWorkingProcessLimit.setText("Infobases per working process limit");
		lblInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 35, 15);
		
		txtInfoBasesPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtInfoBasesPerWorkingProcessLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtInfoBasesPerWorkingProcessLimit.setToolTipText("Infobases per working process limit");
		txtInfoBasesPerWorkingProcessLimit.setBounds(0, 0, 76, 21);
		
		Label lblConnectionsPerWorkingProcessLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
		lblConnectionsPerWorkingProcessLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblConnectionsPerWorkingProcessLimit.setText("Connections per working process limit");
		lblConnectionsPerWorkingProcessLimit.setBounds(0, 0, 35, 15);
		
		txtConnectionsPerWorkingProcessLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtConnectionsPerWorkingProcessLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtConnectionsPerWorkingProcessLimit.setToolTipText("Connections per working process limit");
		txtConnectionsPerWorkingProcessLimit.setBounds(0, 0, 76, 21);
		
		Label lblIPPortMainManager = new Label(container, SWT.NONE);
		lblIPPortMainManager.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIPPortMainManager.setText("IP Port main cluster manager");
		
		txtIPPortMainManager = new Text(container, SWT.BORDER);
		txtIPPortMainManager.setEditable(false);
		txtIPPortMainManager.setToolTipText("IP Port main manager");
		txtIPPortMainManager.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnIsDedicatedManagers = new Button(container, SWT.CHECK);
		btnIsDedicatedManagers.setText("Is dedicated managers");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnIsMainServer = new Button(container, SWT.CHECK);
		btnIsMainServer.setText("Is main server");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		initServerProperties();

		// У уже созданного кластера запрещено менять хост и порт
		if (workingServerId != null) {
			txtServerName.setEditable(false);
			txtComputerName.setEditable(false);
			txtIPPort.setEditable(false);
			txtIPPortMainManager.setEditable(false);
		} else {
			// Новому серверу запрещено сразу ставить галочку Центральный сервер?
		}
		
		return container;
	}

	private void initServerProperties() {
		
		if (workingServerId != null) {
			
			IWorkingServerInfo serverInfo = server.getWorkingServerInfo(clusterId, workingServerId);
			
			// Common properties
			this.txtServerName.setText(serverInfo.getName());
			this.txtComputerName.setText(serverInfo.getHostName());
			this.txtIPPort.setText(Integer.toString(serverInfo.getMainPort()));
			
			IPortRangeInfo portRangesInfo = serverInfo.getPortRanges().get(0);
			String portRanges = Integer.toString(portRangesInfo.getLowBound()).concat(":").concat(Integer.toString(portRangesInfo.getHighBound()));
			this.txtPortRange.setText(portRanges); // TODO
			
			this.txtInfoBasesPerWorkingProcessLimit.setText(Integer.toString(serverInfo.getInfoBasesPerWorkingProcessLimit()));
			this.txtConnectionsPerWorkingProcessLimit.setText(Integer.toString(serverInfo.getConnectionsPerWorkingProcessLimit()));
			
			this.txtSafeWorkingProcessesMemoryLimit.setText(String.valueOf(serverInfo.getSafeWorkingProcessesMemoryLimit()));
			this.txtSafeCallMemoryLimit.setText(String.valueOf(serverInfo.getSafeCallMemoryLimit()));
			this.txtCriticalProcessesTotalMemory.setText(String.valueOf(serverInfo.getCriticalProcessesTotalMemory()));
			this.txtTemporaryAllowedProcessesTotalMemory.setText(String.valueOf(serverInfo.getTemporaryAllowedProcessesTotalMemory()));
			this.txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setText(String.valueOf(serverInfo.getTemporaryAllowedProcessesTotalMemoryTimeLimit()));
			this.txtWorkingProcessMemoryLimit.setText(String.valueOf(serverInfo.getWorkingProcessMemoryLimit()));
			
			this.txtIPPortMainManager.setText(Integer.toString(serverInfo.getClusterMainPort()));
			this.btnIsDedicatedManagers.setSelection(serverInfo.isDedicatedManagers());
			this.btnIsMainServer.setSelection(serverInfo.isMainServer());
			
			if (server.agentVersion.compareTo("8.3.15") < 0) {
				this.txtCriticalProcessesTotalMemory.setEditable(false);
				this.txtTemporaryAllowedProcessesTotalMemory.setEditable(false);
				this.txtTemporaryAllowedProcessesTotalMemoryTimeLimit.setEditable(false);
			} else {
				this.txtSafeWorkingProcessesMemoryLimit.setEditable(false);
				this.txtWorkingProcessMemoryLimit.setEditable(false);
			}
			
			serverInfo.getSafeWorkingProcessesMemoryLimit(); // (8.3.15-)	// максимальный объем памяти рабочих процессов (до 8.3.15)
			
			serverInfo.getSafeCallMemoryLimit(); 							// безопасный расход памяти за один вызов (c 8.3.15 находится первым в группе)
			
			serverInfo.getWorkingProcessMemoryLimit();		 // (8.3.15-)	// объем памяти рабочих процессов, до которого сервер считается производительным (до 8.3.15)
			
			serverInfo.getCriticalProcessesTotalMemory(); 					// критический объем памяти процессов (c 8.3.15+)
			
			serverInfo.getTemporaryAllowedProcessesTotalMemory(); 			// временно допустимый объем памяти процессов (c 8.3.15+)
			
			serverInfo.getTemporaryAllowedProcessesTotalMemoryTimeLimit(); // интервал превышения допустимомо объема памяти процессов (c 8.3.15+)
			
			
			
		}
	}

	private void resetToProf() {
//		this.comboLoadBalancingMode.select(0);
	}
	
	private void saveNewClusterProperties() {
		extractClusterVariablesFromControls();
		
//		IClusterInfo clusterInfo;
//		
//		if (clusterId == null) {
//			clusterInfo = new ClusterInfo();
//	
//			clusterInfo.setHostName(computerName); 	// разрешено только при создании нового
//			clusterInfo.setMainPort(ipPort); 		// разрешено только при создании нового
//		} else {		
//			clusterInfo = server.getClusterInfo(clusterId);
//		}
//		
//		clusterInfo.setName(clusterName);
//		clusterInfo.setSecurityLevel(securityLevel);
//
//		clusterInfo.setLifeTimeLimit(wpLifeTimeLimit);
//		clusterInfo.setMaxMemorySize(wpMaxMemorySize);
//		clusterInfo.setMaxMemoryTimeLimit(wpMaxMemoryTimeLimit);
//		clusterInfo.setClusterRecyclingErrorsCountThreshold(clusterRecyclingErrorsCountThreshold);
//		clusterInfo.setClusterRecyclingKillProblemProcesses(clusterRecyclingKillProblemProcesses);
//
////		clusterInfo.setClusterRecyclingKillByMemoryWithDump(clusterRecyclingKillByMemoryWithDump);
//
//		clusterInfo.setExpirationTimeout(expirationTimeout);
//		clusterInfo.setSessionFaultToleranceLevel(faultToleranceLevel);
//		clusterInfo.setLoadBalancingMode(loadBalancingMode);
//		
//		if (server.authenticateAgent()) { // перенести в server.regCluster
//
//			try {
//				server.regCluster(clusterInfo);
//			} catch (Exception excp) {
//				excp.printStackTrace();
//				MessageBox messageBox = new MessageBox(getParentShell());
//				messageBox.setMessage(excp.getLocalizedMessage());
//				messageBox.open();
//			}
//		}
	}

	private void extractClusterVariablesFromControls() {
		
//		clusterName 	= txtServerName.getText();
//		computerName 	= txtComputerName.getText();
//		ipPort 			= Integer.parseInt(txtIPPort.getText());
//		securityLevel 	= (int) txtPortRange.getData(txtPortRange.getText());
//		
//		wpLifeTimeLimit			= Integer.parseInt(txtWpLifeTimeLimit.getText());
//		wpMaxMemorySize			= Integer.parseInt(txtInfoBasesPerWorkingProcessLimit.getText());
//		wpMaxMemoryTimeLimit	= Integer.parseInt(txtConnectionsPerWorkingProcessLimit.getText());
//		clusterRecyclingErrorsCountThreshold = Integer.parseInt(txtClusterRecyclingErrorsCountThreshold.getText());
//		clusterRecyclingKillProblemProcesses = btnIsDedicatedManagers.getSelection();
//		
////		clusterRecyclingKillByMemoryWithDump = btnClusterRecyclingKillByMemoryWithDump.getSelection(); // ?
//		
//		expirationTimeout 	= Integer.parseInt(txtExpirationTimeout.getText());
//		faultToleranceLevel = Integer.parseInt(txtSessionFaultToleranceLevel.getText());
//		loadBalancingMode 	= (int) comboLoadBalancingMode.getData(comboLoadBalancingMode.getText());
		
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
				saveNewClusterProperties();
				close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, "Apply", false);
		buttonApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveNewClusterProperties();
			}
		});
		Button buttonReset = createButton(parent, IDialogConstants.RETRY_ID, "Reset", false);
		buttonReset.setText("Reset");
		buttonReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initServerProperties();
			}
		});
		Button buttonResetToProf = createButton(parent, IDialogConstants.RETRY_ID, "Reset to PROF", false);
		buttonResetToProf.setText("Reset to PROF");
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
		return new Point(520, 580);
	}

}
