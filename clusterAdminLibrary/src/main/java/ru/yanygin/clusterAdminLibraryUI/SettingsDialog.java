package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibrary.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;

public class SettingsDialog extends Dialog {
	
	private Config config;
//	private String password;
//	private String authExcpMessage;
//	private String authDescription;
	private Button btnShowWorkingServers;
	private Button btnShowWorkingProcesses;
	private Button btnExpandServers;
	private Button btnExpandClusters;
	private Button btnExpandInfobases;
	private Button btnExpandWorkingServers;
	private Button btnExpandWorkingProcesses;
	private Button btnShowServerVersion;
	private Button btnShowServerDescription;
	private Button btnShowInfobaseDescription;
	private Button btnShowLocalRasConnectInfo;
	
//	public String getUsername() {
//		return username;
//	}
//
//	public String getPassword() {
//		return password;
//	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public SettingsDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.config	= ClusterProvider.getCommonConfig();		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;
		
		Group grpShowNodesIn = new Group(container, SWT.NONE);
		grpShowNodesIn.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		grpShowNodesIn.setText("Show nodes in tree");
		grpShowNodesIn.setLayout(new GridLayout(1, false));
		
		btnShowWorkingServers = new Button(grpShowNodesIn, SWT.CHECK);
		btnShowWorkingServers.setText("Show working servers");
		
		btnShowWorkingProcesses = new Button(grpShowNodesIn, SWT.CHECK);
		btnShowWorkingProcesses.setText("Show working processes");
		
		Group grpExpandNodes = new Group(container, SWT.NONE);
		grpExpandNodes.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		grpExpandNodes.setText("Expand nodes in tree");
		grpExpandNodes.setLayout(new GridLayout(1, false));
		
		btnExpandServers = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandServers.setText("Expand servers");
		
		btnExpandClusters = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandClusters.setText("Expand clusters");
		
		btnExpandInfobases = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandInfobases.setText("Expand infobases");
		
		btnExpandWorkingServers = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandWorkingServers.setText("Expand working servers");
		
		btnExpandWorkingProcesses = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandWorkingProcesses.setText("Expand working processes");
		
		Group grpShowInfo = new Group(container, SWT.NONE);
		grpShowInfo.setText("Show info");
		grpShowInfo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		grpShowInfo.setLayout(new GridLayout(1, false));
		
		btnShowServerVersion = new Button(grpShowInfo, SWT.CHECK);
		btnShowServerVersion.setText("Show server version");
		
		btnShowServerDescription = new Button(grpShowInfo, SWT.CHECK);
		btnShowServerDescription.setText("Show server description");
		
		btnShowInfobaseDescription = new Button(grpShowInfo, SWT.CHECK);
		btnShowInfobaseDescription.setText("Show infobase description");
		
		btnShowLocalRasConnectInfo = new Button(grpShowInfo, SWT.CHECK);
		btnShowLocalRasConnectInfo.setText("Show local RAS connect info");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		initProperties();
		
		return container;
	}

	private void initProperties() {
		
		btnShowWorkingServers.setSelection(config.showWorkingServersTree);
		btnShowWorkingProcesses.setSelection(config.showWorkingProcessesTree);
		
		btnExpandServers.setSelection(config.expandServersTree);
		btnExpandClusters.setSelection(config.expandClustersTree);
		btnExpandInfobases.setSelection(config.expandInfobasesTree);
		btnExpandWorkingServers.setSelection(config.expandWorkingServersTree);
		btnExpandWorkingProcesses.setSelection(config.expandWorkingProcessesTree);
		
		btnShowServerVersion.setSelection(config.showServerVersion);
		btnShowServerDescription.setSelection(config.showServerDescription);
		btnShowInfobaseDescription.setSelection(config.showInfobaseDescription);
		btnShowLocalRasConnectInfo.setSelection(config.showLocalRasConnectInfo);
		
	}

	private void saveProperties() {
		
		config.showWorkingServersTree = btnShowWorkingServers.getSelection();
		config.showWorkingProcessesTree = btnShowWorkingProcesses.getSelection();
		
		config.expandServersTree = btnExpandServers.getSelection();
		config.expandClustersTree = btnExpandClusters.getSelection();
		config.expandInfobasesTree = btnExpandInfobases.getSelection();
		config.expandWorkingServersTree = btnExpandWorkingServers.getSelection();
		config.expandWorkingProcessesTree = btnExpandWorkingProcesses.getSelection();
		
		config.showServerVersion = btnShowServerVersion.getSelection();
		config.showServerDescription = btnShowServerDescription.getSelection();
		config.showInfobaseDescription = btnShowInfobaseDescription.getSelection();
		config.showLocalRasConnectInfo = btnShowLocalRasConnectInfo.getSelection();
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveProperties();
				close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 400);
	}

}
