package ru.yanygin.clusterAdminLibraryUI;

import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config;

public class SettingsDialog extends Dialog {
	
	private Config config;
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
	private Button btnLocaleSystem;
	private Button btnLocaleEnglish;
	private Button btnLocaleRussian;
	private Button btnReadClipboard;
	
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
		grpShowNodesIn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpShowNodesIn.setText(Messages.getString("SettingsDialog.ShowNodesInTree")); //$NON-NLS-1$
		grpShowNodesIn.setLayout(new GridLayout(1, false));
		
		btnShowWorkingServers = new Button(grpShowNodesIn, SWT.CHECK);
		btnShowWorkingServers.setText(Messages.getString("SettingsDialog.ShowWorkingServers")); //$NON-NLS-1$
		
		btnShowWorkingProcesses = new Button(grpShowNodesIn, SWT.CHECK);
		btnShowWorkingProcesses.setText(Messages.getString("SettingsDialog.ShowWorkingProcesses")); //$NON-NLS-1$
		
		Group grpExpandNodes = new Group(container, SWT.NONE);
		grpExpandNodes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpExpandNodes.setText(Messages.getString("SettingsDialog.ExpandNodesInTree")); //$NON-NLS-1$
		grpExpandNodes.setLayout(new GridLayout(1, false));
		
		btnExpandServers = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandServers.setText(Messages.getString("SettingsDialog.ExpandServers")); //$NON-NLS-1$
		
		btnExpandClusters = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandClusters.setText(Messages.getString("SettingsDialog.ExpandClusters")); //$NON-NLS-1$
		
		btnExpandInfobases = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandInfobases.setText(Messages.getString("SettingsDialog.ExpandInfobases")); //$NON-NLS-1$
		
		btnExpandWorkingServers = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandWorkingServers.setText(Messages.getString("SettingsDialog.ExpandWorkingServers")); //$NON-NLS-1$
		
		btnExpandWorkingProcesses = new Button(grpExpandNodes, SWT.CHECK);
		btnExpandWorkingProcesses.setText(Messages.getString("SettingsDialog.ExpandWorkingProcesses")); //$NON-NLS-1$
		
		Group grpShowInfo = new Group(container, SWT.NONE);
		grpShowInfo.setText(Messages.getString("SettingsDialog.ShowInfo")); //$NON-NLS-1$
		grpShowInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpShowInfo.setLayout(new GridLayout(1, false));
		
		btnShowServerVersion = new Button(grpShowInfo, SWT.CHECK);
		btnShowServerVersion.setText(Messages.getString("SettingsDialog.ShowServerVersion")); //$NON-NLS-1$
		
		btnShowServerDescription = new Button(grpShowInfo, SWT.CHECK);
		btnShowServerDescription.setText(Messages.getString("SettingsDialog.ShowServerDescription")); //$NON-NLS-1$
		
		btnShowInfobaseDescription = new Button(grpShowInfo, SWT.CHECK);
		btnShowInfobaseDescription.setText(Messages.getString("SettingsDialog.ShowInfobaseDescription")); //$NON-NLS-1$
		
		btnShowLocalRasConnectInfo = new Button(grpShowInfo, SWT.CHECK);
		btnShowLocalRasConnectInfo.setText(Messages.getString("SettingsDialog.ShowLocalRASConnectInfo")); //$NON-NLS-1$
		new Label(container, SWT.NONE);
		
		Group grpLocale = new Group(container, SWT.NONE);
		grpLocale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpLocale.setText(Messages.getString("SettingsDialog.Locale")); //$NON-NLS-1$
		grpLocale.setLayout(new GridLayout(1, false));
		
		btnLocaleSystem = new Button(grpLocale, SWT.RADIO);
		btnLocaleSystem.setText(Messages.getString("SettingsDialog.System")); //$NON-NLS-1$
		
		btnLocaleEnglish = new Button(grpLocale, SWT.RADIO);
		btnLocaleEnglish.setText(Messages.getString("SettingsDialog.English")); //$NON-NLS-1$
		
		btnLocaleRussian = new Button(grpLocale, SWT.RADIO);
		btnLocaleRussian.setText(Messages.getString("SettingsDialog.Russian")); //$NON-NLS-1$
		
		btnReadClipboard = new Button(container, SWT.CHECK);
		btnReadClipboard.setText(Messages.getString("SettingsDialog.ReadClipboard"));
		
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
		btnReadClipboard.setSelection(config.readClipboard);
		
		if (config.locale == null) {
			btnLocaleSystem.setSelection(true);
		} else {
			btnLocaleEnglish.setSelection(config.locale.equals(Locale.ENGLISH.toLanguageTag()));
			btnLocaleRussian.setSelection(config.locale.equals("ru-RU")); //$NON-NLS-1$
		}
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
		config.readClipboard = btnReadClipboard.getSelection();
		
		if (btnLocaleSystem.getSelection())
			config.locale = null;
		else if (btnLocaleEnglish.getSelection())
			config.locale = Locale.ENGLISH.toLanguageTag();
		else if (btnLocaleRussian.getSelection())
			config.locale = "ru-RU"; //$NON-NLS-1$
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

}
