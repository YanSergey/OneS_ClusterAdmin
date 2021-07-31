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

public class CreateEditClusterDialog extends Dialog {
	
//	private IClusterInfo clusterInfo;
	private UUID clusterId;
	private Server server;
	
	private Button btnClusterRecyclingKillProblemProcesses;
	private Text txtClusterName;
	private Text txtComputerName;
	private Text txtIPPort;
	private Combo comboSecurityLevel;

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
	private boolean clusterRecyclingKillByMemoryWithDump;
	
	private int expirationTimeout;
	private int faultToleranceLevel;
	private int loadBalancingMode;
	private Group groupWorkProcessesParams;
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
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
//				extractClusterVariablesFromControls();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 12;
		gridLayout.numColumns = 2;
		
		Label lblClusterName = new Label(container, SWT.NONE);
		lblClusterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClusterName.setText("Cluster name");
		
		txtClusterName = new Text(container, SWT.BORDER);
		txtClusterName.setToolTipText("Cluster name");
		txtClusterName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblComputerName = new Label(container, SWT.NONE);
		lblComputerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComputerName.setText("Computer name");
		
		txtComputerName = new Text(container, SWT.BORDER);
		txtComputerName.setToolTipText("Computer Name");
		txtComputerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblIPPort = new Label(container, SWT.NONE);
		lblIPPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIPPort.setText("IP Port");
		
		txtIPPort = new Text(container, SWT.BORDER);
		txtIPPort.setToolTipText("IP Port");
		txtIPPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSecurityLevel = new Label(container, SWT.NONE);
		lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityLevel.setText("Security level");
		
		comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
		comboSecurityLevel.setVisibleItemCount(3);
		comboSecurityLevel.setTouchEnabled(true);
		comboSecurityLevel.setToolTipText("Security level");
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboSecurityLevel.add("Disable");
		comboSecurityLevel.setData("Disable", 0);
		comboSecurityLevel.add("Connection only");
		comboSecurityLevel.setData("Connection only", 1);
		comboSecurityLevel.add("Constantly");
		comboSecurityLevel.setData("Constantly", 2);
		comboSecurityLevel.select(0);
		
		groupWorkProcessesParams = new Group(container, SWT.NONE);
		groupWorkProcessesParams.setText("Restart Work Processes");
		GridLayout gl_groupWorkProcessesParams = new GridLayout(2, false);
		gl_groupWorkProcessesParams.verticalSpacing = 8;
		groupWorkProcessesParams.setLayout(gl_groupWorkProcessesParams);
		GridData gd_groupWorkProcessesParams = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1);
		gd_groupWorkProcessesParams.widthHint = 450;
		groupWorkProcessesParams.setLayoutData(gd_groupWorkProcessesParams);
		
		Label lblLifeTimeLimit = new Label(groupWorkProcessesParams, SWT.NONE);
		lblLifeTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLifeTimeLimit.setText("Restart interval (second)");
		
		txtLifeTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtLifeTimeLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtLifeTimeLimit.setToolTipText("Restart Interval");
		
		Label lblMaxMemorySize = new Label(groupWorkProcessesParams, SWT.NONE);
		lblMaxMemorySize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxMemorySize.setText("Allowed amount\r\nof memory (KB)");
		lblMaxMemorySize.setBounds(0, 0, 35, 15);
		
		txtMaxMemorySize = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtMaxMemorySize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMaxMemorySize.setToolTipText("Allowed Amount Of Memory");
		txtMaxMemorySize.setBounds(0, 0, 76, 21);
		
		Label lblMaxMemoryTimeLimit = new Label(groupWorkProcessesParams, SWT.WRAP);
		lblMaxMemoryTimeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		lblMaxMemoryTimeLimit.setText("Interval exceeding allowed\r\namount of memory (second)");
		lblMaxMemoryTimeLimit.setBounds(0, 0, 35, 15);
		
		txtMaxMemoryTimeLimit = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtMaxMemoryTimeLimit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMaxMemoryTimeLimit.setToolTipText("Interval Exceeding Allowed Amount Of Memory");
		txtMaxMemoryTimeLimit.setBounds(0, 0, 76, 21);
		
		Label lblAcceptableDeviationOfNumberOfServerErrors = new Label(groupWorkProcessesParams, SWT.WRAP);
		lblAcceptableDeviationOfNumberOfServerErrors.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		lblAcceptableDeviationOfNumberOfServerErrors.setText("Acceptable deviation\r\nof the number of server errors (%)");
		lblAcceptableDeviationOfNumberOfServerErrors.setBounds(0, 0, 35, 30);
		
		txtClusterRecyclingErrorsCountThreshold = new Text(groupWorkProcessesParams, SWT.BORDER);
		txtClusterRecyclingErrorsCountThreshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtClusterRecyclingErrorsCountThreshold.setToolTipText("Acceptable deviation of the number of server errors");
		txtClusterRecyclingErrorsCountThreshold.setBounds(0, 0, 76, 21);
		new Label(groupWorkProcessesParams, SWT.NONE);
		
		btnClusterRecyclingKillProblemProcesses = new Button(groupWorkProcessesParams, SWT.CHECK);
		btnClusterRecyclingKillProblemProcesses.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		btnClusterRecyclingKillProblemProcesses.setText("Force shutdown of problematic processes");
		new Label(groupWorkProcessesParams, SWT.NONE);
		
		btnClusterRecyclingKillByMemoryWithDump = new Button(groupWorkProcessesParams, SWT.CHECK);
		btnClusterRecyclingKillByMemoryWithDump.setText("Cluster recycling kill by memory with dump");
		
		Label lblExpirationTimeout = new Label(container, SWT.NONE);
		lblExpirationTimeout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExpirationTimeout.setText("Shut down processes stop after (second)");
		
		txtExpirationTimeout = new Text(container, SWT.BORDER);
		txtExpirationTimeout.setToolTipText("Shut down processes stop after");
		txtExpirationTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSessionFaultToleranceLevel = new Label(container, SWT.NONE);
		lblSessionFaultToleranceLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSessionFaultToleranceLevel.setText("Fault tolerance level");
		
		txtSessionFaultToleranceLevel = new Text(container, SWT.BORDER);
		txtSessionFaultToleranceLevel.setToolTipText("Fault tolerance level");
		txtSessionFaultToleranceLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLoadBalancingMode = new Label(container, SWT.NONE);
		lblLoadBalancingMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLoadBalancingMode.setText("Load balancing mode");
		
		comboLoadBalancingMode = new Combo(container, SWT.READ_ONLY);
		comboLoadBalancingMode.setVisibleItemCount(2);
		comboLoadBalancingMode.setToolTipText("Load balancing mode");
		comboLoadBalancingMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboLoadBalancingMode.add("Performance priority"); // Приоритет по производительности
		comboLoadBalancingMode.setData("Performance priority", 0);
		comboLoadBalancingMode.add("Memory priority"); // Приоритет по памяти
		comboLoadBalancingMode.setData("Memory priority", 1);
		comboLoadBalancingMode.select(0);

		initServerProperties();

		// У уже созданного кластера запрещено менять хост и порт
		if (clusterId != null) {
			txtComputerName.setEditable(false);
			txtIPPort.setEditable(false);
		}
		
		return container;
	}

	private void initServerProperties() {
		
		if (clusterId != null) {
			
			IClusterInfo clusterInfo = server.getClusterInfo(clusterId);
			
			// Common properties
			this.txtClusterName.setText(clusterInfo.getName());
			this.txtComputerName.setText(clusterInfo.getHostName());
			this.txtIPPort.setText(Integer.toString(clusterInfo.getMainPort()));
			this.comboSecurityLevel.select(clusterInfo.getSecurityLevel());
			
			this.txtLifeTimeLimit.setText(Integer.toString(clusterInfo.getLifeTimeLimit()));
			this.txtMaxMemorySize.setText(Integer.toString(clusterInfo.getMaxMemorySize())); // 8.3.15-
			this.txtMaxMemoryTimeLimit.setText(Integer.toString(clusterInfo.getMaxMemoryTimeLimit())); // 8.3.15-
			this.txtClusterRecyclingErrorsCountThreshold.setText(Integer.toString(clusterInfo.getClusterRecyclingErrorsCountThreshold())); // 8.3.15-
			this.btnClusterRecyclingKillProblemProcesses.setSelection(clusterInfo.isClusterRecyclingKillProblemProcesses());
			this.btnClusterRecyclingKillByMemoryWithDump.setSelection(clusterInfo.isClusterRecyclingKillByMemoryWithDump()); // 8.3.15+
						
			this.txtExpirationTimeout.setText(Integer.toString(clusterInfo.getExpirationTimeout()));
			this.txtSessionFaultToleranceLevel.setText(Integer.toString(clusterInfo.getSessionFaultToleranceLevel()));
			this.comboLoadBalancingMode.select(clusterInfo.getLoadBalancingMode());
			
			if (server.isFifteen()) {
				this.txtMaxMemorySize.setEditable(false);
				this.txtMaxMemoryTimeLimit.setEditable(false);
				this.txtClusterRecyclingErrorsCountThreshold.setEditable(false);
				
				this.txtMaxMemorySize.setToolTipText("deprecated in 8.3.15");
				this.txtMaxMemoryTimeLimit.setToolTipText("deprecated in 8.3.15");
				this.txtClusterRecyclingErrorsCountThreshold.setToolTipText("deprecated in 8.3.15");
			} else {
				this.btnClusterRecyclingKillByMemoryWithDump.setEnabled(false);
				
				this.btnClusterRecyclingKillByMemoryWithDump.setToolTipText("8.3.15+");
			}
		
		}
	}

	private void resetToProf() {
		this.comboLoadBalancingMode.select(0);
	}
	
	private void saveNewClusterProperties() {

		extractClusterVariablesFromControls();
		
		IClusterInfo clusterInfo;
		
		if (clusterId == null) {
			clusterInfo = new ClusterInfo();
	
			clusterInfo.setHostName(computerName); 	// разрешено только при создании нового
			clusterInfo.setMainPort(ipPort); 		// разрешено только при создании нового
		} else {		
			clusterInfo = server.getClusterInfo(clusterId);
		}
		
		clusterInfo.setName(clusterName);
		clusterInfo.setSecurityLevel(securityLevel);

		clusterInfo.setLifeTimeLimit(wpLifeTimeLimit);
		clusterInfo.setClusterRecyclingKillProblemProcesses(clusterRecyclingKillProblemProcesses);

		if (server.isFifteen())
			clusterInfo.setClusterRecyclingKillByMemoryWithDump(clusterRecyclingKillByMemoryWithDump); // 8.3.15+
		else {
			clusterInfo.setMaxMemorySize(wpMaxMemorySize);
			clusterInfo.setMaxMemoryTimeLimit(wpMaxMemoryTimeLimit);
			clusterInfo.setClusterRecyclingErrorsCountThreshold(clusterRecyclingErrorsCountThreshold);
		}

		clusterInfo.setExpirationTimeout(expirationTimeout);
		clusterInfo.setSessionFaultToleranceLevel(faultToleranceLevel);
		clusterInfo.setLoadBalancingMode(loadBalancingMode);
		

		try {
			if (server.regCluster(clusterInfo, clusterId == null))
				close();
		} catch (Exception excp) {
			var messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}
	}

	private void extractClusterVariablesFromControls() { // TODO избавиться от метода???
		
		clusterName 	= txtClusterName.getText();
		computerName 	= txtComputerName.getText();
		ipPort 			= Integer.parseInt(txtIPPort.getText());
		securityLevel 	= (int) comboSecurityLevel.getData(comboSecurityLevel.getText());
		
		wpLifeTimeLimit			= Integer.parseInt(txtLifeTimeLimit.getText());
		wpMaxMemorySize			= Integer.parseInt(txtMaxMemorySize.getText());
		wpMaxMemoryTimeLimit	= Integer.parseInt(txtMaxMemoryTimeLimit.getText());
		clusterRecyclingErrorsCountThreshold = Integer.parseInt(txtClusterRecyclingErrorsCountThreshold.getText()); // 8.3.15-
		clusterRecyclingKillProblemProcesses = btnClusterRecyclingKillProblemProcesses.getSelection();
		
		clusterRecyclingKillByMemoryWithDump = btnClusterRecyclingKillByMemoryWithDump.getSelection();  // 8.3.15+
		
		expirationTimeout 	= Integer.parseInt(txtExpirationTimeout.getText());
		faultToleranceLevel = Integer.parseInt(txtSessionFaultToleranceLevel.getText());
		loadBalancingMode 	= (int) comboLoadBalancingMode.getData(comboLoadBalancingMode.getText());
		
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
//				close();
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
		return new Point(520, 550);
	}

}
