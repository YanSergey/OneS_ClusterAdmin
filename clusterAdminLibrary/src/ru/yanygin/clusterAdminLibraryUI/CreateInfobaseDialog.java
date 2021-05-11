package ru.yanygin.clusterAdminLibraryUI;

import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.InfoBaseInfo;

import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CreateInfobaseDialog extends Dialog {
		
	private IClusterInfo clusterInfo;

	private Server server;
	private Button btnSheduledJobsDenied;
	private Button btnAllowDistributeLicense;
	private Button btnInfobaseCreationMode;
	private Text txtInfobaseName;
	private Text txtServerDBName;
	private Text txtDatabaseDbName;
	private Text txtDatabaseDbUser;
	private Text txtDatabaseDbPassword;
	private Text txtInfobaseDescription;
	private Combo comboSecurityLevel;
	private Combo comboServerDBType;
	private Label lblLocale;
	private Combo comboLocale; // Откуда то загрузить все возможные локали
	private Combo comboDateOffset;

	// fields of infobase
	private String infobaseName;
	private String infobaseDescription;
	private String infobaseLocale;
	private int infobaseDateOffset;
	private int securityLevel; // Disable, Connection only, Constantly (Выключено, Только соединение, Постоянно)
	
	private String serverDBName;
	private String serverDBType; // MSSQLServer, PostgreSQL, IBMDB2, OracleDatabase
	private String databaseDbName;
	private String databaseDbUser;
	private String databaseDbPassword;
	
	private int allowDistributeLicense;
	private boolean infobaseCreationMode;
	private boolean sheduledJobsDenied;
	
	private UUID newInfobaseUUID;
	
	public UUID getNewInfobaseUUID() {
		return newInfobaseUUID;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public CreateInfobaseDialog(Shell parentShell, Server server, IClusterInfo clusterInfo) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterInfo = clusterInfo;
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Label lblInfobaseName = new Label(container, SWT.NONE);
		lblInfobaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfobaseName.setText("Infobase name");
		
		txtInfobaseName = new Text(container, SWT.BORDER);
		txtInfobaseName.setToolTipText("Infobase name");
		txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblInfobaseDescription = new Label(container, SWT.NONE);
		lblInfobaseDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfobaseDescription.setText("Description");
		
		txtInfobaseDescription = new Text(container, SWT.BORDER);
		txtInfobaseDescription.setToolTipText("Description");
		txtInfobaseDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSecurityLevel = new Label(container, SWT.NONE);
		lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityLevel.setText("Security level");
		
		comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
		comboSecurityLevel.setToolTipText("Security level");
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboSecurityLevel.setText("Disable");
		
//		securityLevels.forEach(version -> {
		comboSecurityLevel.add("Disable");
		comboSecurityLevel.setData("Disable", 0);
		comboSecurityLevel.add("Connection only");
		comboSecurityLevel.setData("Connection only", 1);
		comboSecurityLevel.add("Constantly");
		comboSecurityLevel.setData("Constantly", 2);
//		});
		comboSecurityLevel.select(0);
		
		
		Label lblServerDBName = new Label(container, SWT.NONE);
		lblServerDBName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBName.setText("Server DB name");
		
		txtServerDBName = new Text(container, SWT.BORDER);
		txtServerDBName.setToolTipText("Server DB name");
		txtServerDBName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblServerDBType = new Label(container, SWT.NONE);
		lblServerDBType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBType.setText("DBMS type");
		
		comboServerDBType = new Combo(container, SWT.READ_ONLY);
//		comboServerDBType.setItems(new String[] {"MSSQLServer", "PostgreSQL", "IBMDB2", "OracleDatabase"});
		comboServerDBType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		comboServerDBType.add("MSSQLServer");
		comboServerDBType.add("PostgreSQL");
		comboServerDBType.add("IBMDB2");
		comboServerDBType.add("OracleDatabase");
		comboServerDBType.select(0);

		comboServerDBType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean dateOffsetEnabled = comboServerDBType.getText().equals("MSSQLServer");
				comboDateOffset.setEnabled(dateOffsetEnabled);
			}
		});
		
		Label lblDatabaseDbName = new Label(container, SWT.NONE);
		lblDatabaseDbName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbName.setText("Database DB name");
		
		txtDatabaseDbName = new Text(container, SWT.BORDER);
		txtDatabaseDbName.setToolTipText("Database DB name");
		txtDatabaseDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabaseDbUser = new Label(container, SWT.NONE);
		lblDatabaseDbUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbUser.setText("Database DB user");
		
		txtDatabaseDbUser = new Text(container, SWT.BORDER);
		txtDatabaseDbUser.setToolTipText("Database DB user");
		txtDatabaseDbUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabaseDbPassword = new Label(container, SWT.NONE);
		lblDatabaseDbPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbPassword.setAlignment(SWT.RIGHT);
		lblDatabaseDbPassword.setText("Database DB password");
		
		txtDatabaseDbPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtDatabaseDbPassword.setToolTipText("Database DB password");
		txtDatabaseDbPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnAllowDistributeLicense = new Button(container, SWT.CHECK);
		btnAllowDistributeLicense.setText("Allow distribute license at 1C:Enterprise server");
		
		lblLocale = new Label(container, SWT.NONE);
		lblLocale.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocale.setText("Locale");
		
		comboLocale = new Combo(container, SWT.READ_ONLY);
		comboLocale.setItems(new String[] {"ru_RU", "en_US", "xx_XX", "yy_YY"});
		comboLocale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboLocale.select(0);
		
		Label lblDateOffset = new Label(container, SWT.NONE);
		lblDateOffset.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDateOffset.setText("Date offset");
		
		comboDateOffset = new Combo(container, SWT.READ_ONLY);
		comboDateOffset.setItems(new String[] {"0", "2000"});
		comboDateOffset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboDateOffset.setText("2000");
		new Label(container, SWT.NONE);
		
		btnInfobaseCreationMode = new Button(container, SWT.CHECK);
		btnInfobaseCreationMode.setText("Create a database if it is not available");
		new Label(container, SWT.NONE);
		
		btnSheduledJobsDenied = new Button(container, SWT.CHECK);
		btnSheduledJobsDenied.setText("Sheduled jobs denied");
		new Label(container, SWT.NONE);

		initInfobaseProperties();

		
		return container;
	}

	private void initInfobaseProperties() {

	}

	private void saveInfobaseProperties() {

		IInfoBaseInfo infoBaseInfo = new InfoBaseInfo(securityLevel);

		// Common properties
		infoBaseInfo.setName(infobaseName);
		infoBaseInfo.setDescr(infobaseDescription);
		infoBaseInfo.setLicenseDistributionAllowed(allowDistributeLicense);
		infoBaseInfo.setScheduledJobsDenied(sheduledJobsDenied);

//		if (securityLevel != infoBaseInfo.getSecurityLevel()) // не понятно как устанавливается

		// DB properties
		infoBaseInfo.setDbServerName(serverDBName);
		infoBaseInfo.setDbms(serverDBType);
		infoBaseInfo.setDbName(databaseDbName);
		infoBaseInfo.setDbUser(databaseDbUser);
		infoBaseInfo.setDbPassword(databaseDbPassword);
		infoBaseInfo.setLocale(infobaseLocale);
		infoBaseInfo.setDateOffset(infobaseDateOffset);

		try {
			newInfobaseUUID = server.createInfoBase(clusterInfo.getClusterId(), infoBaseInfo,
					(infobaseCreationMode ? 1 : 0));
		} catch (Exception excp) {
			excp.printStackTrace();
			MessageBox messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}

	}

	private void extractInfobaseVariablesFromControls() {
		
		// Common properties
		infobaseName 			= txtInfobaseName.getText();
		infobaseDescription 	= txtInfobaseDescription.getText();
		
		securityLevel 	= (int) comboSecurityLevel.getData(comboSecurityLevel.getText());
//		switch (comboSecurityLevel.getText()) {
//		case "Disable":
//			securityLevel 		= 0;
//			break;
//		case "Connection only":
//			securityLevel 		= 1;
//			break;
//		case "Constantly":
//			securityLevel 		= 2;
//			break;
//		default:
//			securityLevel 		= 0;
//			break;
//		}
		
		allowDistributeLicense 	= btnAllowDistributeLicense.getSelection() ? 1 : 0;
		sheduledJobsDenied 		= btnSheduledJobsDenied.getSelection();
		infobaseCreationMode 	= btnInfobaseCreationMode.getSelection();
		
		// DB properties
		serverDBName 		= txtServerDBName.getText();
		serverDBType 		= comboServerDBType.getText();
		databaseDbName 		= txtDatabaseDbName.getText();
		databaseDbUser 		= txtDatabaseDbUser.getText();
		databaseDbPassword 	= txtDatabaseDbPassword.getText();
				
		infobaseLocale 		= comboLocale.getText();
		infobaseDateOffset 	= Integer.parseInt(comboDateOffset.getText());
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
				extractInfobaseVariablesFromControls();
				saveInfobaseProperties();
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
		return new Point(450, 500);
	}

}
