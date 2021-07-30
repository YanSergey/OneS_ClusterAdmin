package ru.yanygin.clusterAdminLibraryUI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.yanygin.clusterAdminLibrary.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;

public class CreateEditServerDialog extends Dialog {
	
	private Server serverParams;
	
	private String agentHost;
	private int agentPort;
	private String rasHost;
	private int rasPort;
	private boolean useLocalRas;
	private String localRasV8version;
	private String localRasPath;
	private int localRasPort;
	private boolean autoconnect;
	private boolean saveCredentials;
	private String agentUser;
	private String agentPassword;
	
	private Map<UUID, String[]> credentialsClustersCashe;
	
	private Text txtRASHost;
	private Text txtRasPort;
	
	private Text txtAgentHost;
	private Text txtAgentPort;
	private Text txtLocalRasPort;
	private Text txtLocalRASPath;
	private Label lblLocalRASPath;
	
	private Button btnAutoconnect;
	private Text txtAgentUser;
	private Text txtAgentPasswors;
//	private Group grpCredentials;
	private Table tableCredentials;
	private Button radioUseRemoteRAS;
	private Button radioUseLocalRAS;
	private Button btnSaveCredentials;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param serverParams
	 */
	public CreateEditServerDialog(Shell parentShell, Server serverParams) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);
		
		// найти способ установить заголовок окна
//		parentShell.setText("Parameters of the central server 1C:Enterprise");
		
		this.serverParams = serverParams;
	}
	
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				extractServerParameters();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		
		TabItem tabConnect = new TabItem(tabFolder, SWT.NONE);
		tabConnect.setText("Connect parameters");
		
		Composite connectContainer = new Composite(tabFolder, SWT.NONE);
		tabConnect.setControl(connectContainer);
		GridLayout gl_connectContainer = new GridLayout(2, false);
		connectContainer.setLayout(gl_connectContainer);
		
		radioUseRemoteRAS = new Button(connectContainer, SWT.RADIO);
		radioUseRemoteRAS.setSelection(true);
		radioUseRemoteRAS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectionEvent a = e;
//				tabRASVariant.setSelection(1);
			}
		});
		radioUseRemoteRAS.setBounds(0, 0, 90, 16);
		radioUseRemoteRAS.setText("Use remote RAS");
		
		radioUseLocalRAS = new Button(connectContainer, SWT.RADIO);
		radioUseLocalRAS.setBounds(0, 0, 90, 16);
		radioUseLocalRAS.setText("Use local RAS");
		
		Group grpRemoteRasParameters = new Group(connectContainer, SWT.NONE);
		grpRemoteRasParameters.setText("Remote RAS parameters");
		grpRemoteRasParameters.setLayout(new GridLayout(2, false));
		
		Label lblRASHost = new Label(grpRemoteRasParameters, SWT.NONE);
		lblRASHost.setText("Host");
		
		Label lblRasPort = new Label(grpRemoteRasParameters, SWT.NONE);
		lblRasPort.setSize(46, 15);
		lblRasPort.setText("Port");
		
		txtRASHost = new Text(grpRemoteRasParameters, SWT.BORDER);
		GridData gd_txtRASHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtRASHost.widthHint = 200;
		txtRASHost.setLayoutData(gd_txtRASHost);
		txtRASHost.setToolTipText("RAS host");
		
		txtRasPort = new Text(grpRemoteRasParameters, SWT.BORDER);
		GridData gd_txtRasPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtRasPort.widthHint = 50;
		txtRasPort.setLayoutData(gd_txtRasPort);
		txtRasPort.setToolTipText("RAS Port");
		
		Group grpLocalRasParameters = new Group(connectContainer, SWT.NONE);
		grpLocalRasParameters.setSize(417, 90);
		grpLocalRasParameters.setText("Local RAS parameters");
		grpLocalRasParameters.setLayout(new GridLayout(2, false));
		
		lblLocalRASPath = new Label(grpLocalRasParameters, SWT.NONE);
		lblLocalRASPath.setSize(124, 15);
		lblLocalRASPath.setText("Path");
		
		Label lblLocalRasPort = new Label(grpLocalRasParameters, SWT.NONE);
		lblLocalRasPort.setSize(77, 15);
		lblLocalRasPort.setText("Port");
		
		txtLocalRASPath = new Text(grpLocalRasParameters, SWT.BORDER);
		GridData gd_txtLocalRASPath = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtLocalRASPath.widthHint = 200;
		txtLocalRASPath.setLayoutData(gd_txtLocalRASPath);
		txtLocalRASPath.setSize(389, 21);
		txtLocalRASPath.setToolTipText("LocalRASPath");
		
		txtLocalRasPort = new Text(grpLocalRasParameters, SWT.BORDER);
		GridData gd_txtLocalRasPort = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtLocalRasPort.widthHint = 50;
		txtLocalRasPort.setLayoutData(gd_txtLocalRasPort);
		txtLocalRasPort.setToolTipText("local RAS port");
		
		Group grpRagentParameters = new Group(connectContainer, SWT.NONE);
		grpRagentParameters.setText("Cluster Agent Parameters");
		grpRagentParameters.setLayout(new GridLayout(2, false));
		
		Label lblAgentHost = new Label(grpRagentParameters, SWT.NONE);
		lblAgentHost.setText("Host");
		
		Label lblAgentPort = new Label(grpRagentParameters, SWT.NONE);
		lblAgentPort.setText("Port");
		
		txtAgentHost = new Text(grpRagentParameters, SWT.BORDER);
		GridData gd_txtAgentHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtAgentHost.widthHint = 200;
		txtAgentHost.setLayoutData(gd_txtAgentHost);
		txtAgentHost.setToolTipText("Agent host");
		
		txtAgentPort = new Text(grpRagentParameters, SWT.BORDER);
		GridData gd_txtAgentPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtAgentPort.widthHint = 50;
		txtAgentPort.setLayoutData(gd_txtAgentPort);
		txtAgentPort.setToolTipText("Agent Port");
		new Label(connectContainer, SWT.NONE);
		
//		Button btnCheckButton = new Button(connectContainer, SWT.CHECK);
//		btnCheckButton.setText("Check Button");
		
		btnAutoconnect = new Button(connectContainer, SWT.CHECK);
		btnAutoconnect.setText("Autoconnect to the server at startup");
		new Label(connectContainer, SWT.NONE);
		
		TabItem tabCredentials = new TabItem(tabFolder, SWT.NONE);
		tabCredentials.setText("Credentials");
		
		Composite credentialsContainer = new Composite(tabFolder, SWT.NONE);
		tabCredentials.setControl(credentialsContainer);
		credentialsContainer.setLayout(new GridLayout(1, false));
		
		btnSaveCredentials = new Button(credentialsContainer, SWT.CHECK);
		btnSaveCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSaveCredentials.setSize(155, 16);
		btnSaveCredentials.setText("Save credentials");
		
//		grpCredentials = new Group(credentialsContainer, SWT.NONE);
//		GridData gd_grpCredentials = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
//		gd_grpCredentials.widthHint = 624;
//		grpCredentials.setLayoutData(gd_grpCredentials);
//		grpCredentials.setLayout(new GridLayout(2, false));
//		grpCredentials.setText("Credentials");
		
		Group grpCentralServerCredential = new Group(credentialsContainer, SWT.NONE);
		grpCentralServerCredential.setText("Central server adminstrator");
		grpCentralServerCredential.setLayout(new GridLayout(4, false));
		GridData gd_grpCentralServerCredential = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_grpCentralServerCredential.verticalIndent = 5;
		grpCentralServerCredential.setLayoutData(gd_grpCentralServerCredential);
		
		Label lblAgentUser = new Label(grpCentralServerCredential, SWT.NONE);
		lblAgentUser.setSize(23, 15);
		lblAgentUser.setText("User");
		
		txtAgentUser = new Text(grpCentralServerCredential, SWT.BORDER);
		GridData gd_txtAgentUser = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtAgentUser.widthHint = 200;
		txtAgentUser.setLayoutData(gd_txtAgentUser);
		txtAgentUser.setSize(76, 21);
		txtAgentUser.setToolTipText("Agent host");
		
		Label lblAgentPwd = new Label(grpCentralServerCredential, SWT.NONE);
		lblAgentPwd.setSize(50, 15);
		lblAgentPwd.setText("Password");
		
		txtAgentPasswors = new Text(grpCentralServerCredential, SWT.BORDER | SWT.PASSWORD);
		GridData gd_txtAgentPasswors = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtAgentPasswors.widthHint = 100;
		txtAgentPasswors.setLayoutData(gd_txtAgentPasswors);
		txtAgentPasswors.setSize(76, 21);
		txtAgentPasswors.setToolTipText("Agent password");
		
		tableCredentials = new Table(credentialsContainer, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_tableCredentials = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_tableCredentials.heightHint = 183;
		gd_tableCredentials.verticalIndent = 5;
		tableCredentials.setLayoutData(gd_tableCredentials);
		tableCredentials.setHeaderVisible(true);
		tableCredentials.setLinesVisible(true);
		
		TableColumn tblclmnType = new TableColumn(tableCredentials, SWT.NONE);
		tblclmnType.setWidth(60);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnName = new TableColumn(tableCredentials, SWT.NONE);
		tblclmnName.setWidth(160);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnID = new TableColumn(tableCredentials, SWT.NONE);
		tblclmnID.setWidth(100);
		tblclmnID.setText("ID");
		
		TableColumn tblclmnUsername = new TableColumn(tableCredentials, SWT.NONE);
		tblclmnUsername.setWidth(100);
		tblclmnUsername.setText("Username");
		
		TableColumn tblclmnPassword = new TableColumn(tableCredentials, SWT.NONE);
		tblclmnPassword.setWidth(100);
		tblclmnPassword.setText("Password");
		new Label(container, SWT.NONE);
		
		initServerProperties();
		
		return container;
	}
	
	private void initServerProperties() {
		if (serverParams != null) {
			this.txtRASHost.setText(serverParams.rasHost);
			this.txtRasPort.setText(serverParams.getRasPortAsString());
			
			this.txtAgentHost.setText(serverParams.agentHost);
			this.txtAgentPort.setText(serverParams.getAgentPortAsString());
			
			this.radioUseRemoteRAS.setSelection(!serverParams.useLocalRas);
			this.radioUseLocalRAS.setSelection(serverParams.useLocalRas);
//			this.comboV8Versions.setText(serverParams.localRasV8version);
			this.txtLocalRasPort.setText(serverParams.getLocalRasPortAsString());
			
			this.btnAutoconnect.setSelection(serverParams.autoconnect);
			this.btnSaveCredentials.setSelection(serverParams.saveCredentials);
			this.txtAgentUser.setText(serverParams.agentUserName);
			this.txtAgentPasswors.setText(serverParams.agentPassword);
			
			serverParams.credentialsClustersCashe.forEach((uuid, userPass) -> {
				
				TableItem credentialItem = new TableItem(this.tableCredentials, SWT.NONE);
				
				String[] itemText = { "cluster", userPass[2], // clusterName
						uuid.toString(), userPass[0], // username
						userPass[1] }; // pass
				
				credentialItem.setText(itemText);
				credentialItem.setData("UUID", uuid);
				credentialItem.setChecked(false);
				
			});
		}
	}
	
	private void extractServerParameters() {
		agentHost = txtAgentHost.getText();
		agentPort = Integer.parseInt(txtAgentPort.getText());
		
		rasHost = txtRASHost.getText();
		rasPort = Integer.parseInt(txtRasPort.getText());
		
		useLocalRas 	= !radioUseRemoteRAS.getSelection();
		localRasPort 	= Integer.parseInt(txtLocalRasPort.getText());
//		localRasV8version 	= comboV8Versions.getText();
		localRasPath 	= txtLocalRASPath.getText();
		
		autoconnect 	= btnAutoconnect.getSelection();
		saveCredentials = btnSaveCredentials.getSelection();
		agentUser 		= txtAgentUser.getText();
		agentPassword 	= txtAgentPasswors.getText();
		
		credentialsClustersCashe = new HashMap<>();
		if (saveCredentials) {
			TableItem[] credentials = tableCredentials.getItems();
			for (TableItem credential : credentials) {
				UUID uuid = (UUID) credential.getData("UUID");
				credentialsClustersCashe.put(uuid, new String[] { credential.getText(3), credential.getText(4), credential.getText(1) });
			}
		}
		
	}
	
	private void saveNewServerProperties() {
		if (serverParams != null) {
			serverParams.setServerNewProperties(agentHost,
					agentPort,
					rasHost,
					rasPort,
					useLocalRas,
					localRasPort,
					localRasV8version,
					localRasPath,
					autoconnect,
					saveCredentials,
					agentUser,
					agentPassword,
					credentialsClustersCashe);
		}
	}
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveNewServerProperties();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(630, 410);
	}
}
