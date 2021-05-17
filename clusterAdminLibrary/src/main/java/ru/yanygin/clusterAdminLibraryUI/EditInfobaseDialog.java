package ru.yanygin.clusterAdminLibraryUI;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DateTime;

public class EditInfobaseDialog extends Dialog {
	
	private IInfoBaseInfo infoBaseInfo;
	private IClusterInfo clusterInfo;
	private Server server;
	
	// Controls
	private Button btnSessionsDenied;
	private Button btnSheduledJobsDenied;
	private Button btnAllowDistributeLicense;
	private Button btnExternalSessionManagerRequired;
	private Text txtInfobaseName;
	private Text txtServerDBName;
	private Text txtDatabaseDbName;
	private Text txtDatabaseDbUser;
	private Text txtDatabaseDbPassword;
	private Text txtInfobaseDescription;
	private Text txtSecurityLevel;
	private Text txtPermissionCode;
	private Text txtDeniedParameter;
	private Text txtExternalSessionManagerConnectionString;
	private Text txtSecurityProfile;
	private Text txtSafeModeSecurityProfile;
	private Text txtDeniedMessage;
	private Combo comboServerDBType;
	private DateTime deniedFromDate;
	private DateTime deniedFromTime;
	private DateTime deniedToDate;
	private DateTime deniedToTime;

	// fields of infobase
	private String infobaseName;
	private String infobaseDescription;
	
	private String serverDBName;
	private String serverDBType; // MSSQLServer, PostgreSQL, IBMDB2, OracleDatabase
	private String databaseDbName;
	private String databaseDbUser;
	private String databaseDbPassword;
	
	private int allowDistributeLicense;
	
	private boolean sessionsDenied;
	private Date sessionsDeniedFrom;
	private Date sessionsDeniedTo;
	private String deniedMessage;
	private String permissionCode;
	private String deniedParameter;
	
	private boolean sheduledJobsDenied;
	
	private String externalSessionManagerConnectionString;
	private boolean externalSessionManagerRequired;
	
	private String securityProfile;
	private String safeModeSecurityProfile;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditInfobaseDialog(Shell parentShell, Server server, IClusterInfo clusterInfo, IInfoBaseInfo infoBaseInfo) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterInfo = clusterInfo;
		this.infoBaseInfo = infoBaseInfo;
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
//		lblSecurityLevel.setToolTipText("");
		lblSecurityLevel.setText("Security level");
		
		txtSecurityLevel = new Text(container, SWT.BORDER);
		txtSecurityLevel.setEditable(false);
		txtSecurityLevel.setTouchEnabled(true);
		txtSecurityLevel.setToolTipText("Security level");
		txtSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
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
		comboServerDBType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		comboServerDBType.add("MSSQLServer");
		comboServerDBType.add("PostgreSQL");
		comboServerDBType.add("IBMDB2");
		comboServerDBType.add("OracleDatabase");
		comboServerDBType.select(0);
		
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
		new Label(container, SWT.NONE);
		
		btnSessionsDenied = new Button(container, SWT.CHECK);
		btnSessionsDenied.setText("Sessions denied");
		
		Label lblDeniedFrom = new Label(container, SWT.NONE);
		lblDeniedFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedFrom.setText("Denied from:");
		
		Composite compositeDeniedFrom = new Composite(container, SWT.NONE);
		compositeDeniedFrom.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedFromDate = new DateTime(compositeDeniedFrom, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		
		deniedFromTime = new DateTime(compositeDeniedFrom, SWT.BORDER | SWT.TIME);
		
		Label lblDeniedTo = new Label(container, SWT.NONE);
		lblDeniedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedTo.setText("Denied to:");
		
		Composite compositeDeniedTo = new Composite(container, SWT.NONE);
		compositeDeniedTo.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedToDate = new DateTime(compositeDeniedTo, SWT.NONE | SWT.DROP_DOWN);
		
		deniedToTime = new DateTime(compositeDeniedTo, SWT.BORDER | SWT.TIME);

		Label lblDeniedMessage = new Label(container, SWT.NONE);
		lblDeniedMessage.setText("Denied message:");
		
		txtDeniedMessage = new Text(container, SWT.BORDER);
		txtDeniedMessage.setToolTipText("Denied message");
		GridData gd_txtDeniedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtDeniedMessage.heightHint = 63;
		txtDeniedMessage.setLayoutData(gd_txtDeniedMessage);
		
		Label lblPermissionCode = new Label(container, SWT.NONE);
		lblPermissionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPermissionCode.setText("Permission code:");
		
		txtPermissionCode = new Text(container, SWT.BORDER);
		txtPermissionCode.setToolTipText("Permission code");
		txtPermissionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDeniedParameter = new Label(container, SWT.NONE);
		lblDeniedParameter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedParameter.setText("Denied parameter");
		
		txtDeniedParameter = new Text(container, SWT.BORDER);
		txtDeniedParameter.setToolTipText("Denied parameter");
		txtDeniedParameter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnSheduledJobsDenied = new Button(container, SWT.CHECK);
		btnSheduledJobsDenied.setText("Sheduled jobs denied");
		
		Label lblExternalSessionManagerConnectionString = new Label(container, SWT.NONE);
		lblExternalSessionManagerConnectionString.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExternalSessionManagerConnectionString.setText("External session management");
		
		txtExternalSessionManagerConnectionString = new Text(container, SWT.BORDER);
		txtExternalSessionManagerConnectionString.setToolTipText("External session management");
		txtExternalSessionManagerConnectionString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnExternalSessionManagerRequired = new Button(container, SWT.CHECK);
		btnExternalSessionManagerRequired.setText("Required use of external management");
		
		Label lblSecurityProfile = new Label(container, SWT.NONE);
		lblSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityProfile.setText("Security profile");
		
		txtSecurityProfile = new Text(container, SWT.BORDER);
		txtSecurityProfile.setToolTipText("Security profile");
		txtSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSafeModeSecurityProfile = new Label(container, SWT.NONE);
		lblSafeModeSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeModeSecurityProfile.setText("Safe mode security profile");
		
		txtSafeModeSecurityProfile = new Text(container, SWT.BORDER);
		txtSafeModeSecurityProfile.setToolTipText("Safe mode security profile");
		txtSafeModeSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initInfobaseProperties();

		
		return container;
	}

	private void initInfobaseProperties() {
		if (infoBaseInfo != null) {
			
			// Common properties
			this.txtInfobaseName.setText(infoBaseInfo.getName());
			this.txtInfobaseDescription.setText(infoBaseInfo.getDescr());
			this.txtSecurityLevel.setText(Integer.toString(infoBaseInfo.getSecurityLevel()));
			this.btnAllowDistributeLicense.setSelection(infoBaseInfo.getLicenseDistributionAllowed() == 1);
			this.btnSheduledJobsDenied.setSelection(infoBaseInfo.isScheduledJobsDenied());
			
			// DB properties
			this.txtServerDBName.setText(infoBaseInfo.getDbServerName());
			this.comboServerDBType.setText(infoBaseInfo.getDbms());
			this.txtDatabaseDbName.setText(infoBaseInfo.getDbName());
			this.txtDatabaseDbUser.setText(infoBaseInfo.getDbUser());
			this.txtDatabaseDbPassword.setText(infoBaseInfo.getDbPassword());
			
			// Lock properties
			this.btnSessionsDenied.setSelection(infoBaseInfo.isSessionsDenied());
			
			Date deniedFrom = infoBaseInfo.getDeniedFrom();
			this.deniedFromDate.setDate(1900 + deniedFrom.getYear(), deniedFrom.getMonth(), deniedFrom.getDate());
			this.deniedFromTime.setTime(deniedFrom.getHours(), deniedFrom.getMinutes(), deniedFrom.getSeconds());
			
			Date deniedTo  	= infoBaseInfo.getDeniedTo();
			this.deniedToDate.setDate(1900 + deniedTo.getYear(), deniedTo.getMonth(), deniedTo.getDate());
			this.deniedToTime.setTime(deniedTo.getHours(), deniedTo.getMinutes(), deniedTo.getSeconds());
			
			this.txtDeniedMessage.setText(infoBaseInfo.getDeniedMessage());
			this.txtPermissionCode.setText(infoBaseInfo.getPermissionCode());
			this.txtDeniedParameter.setText(infoBaseInfo.getDeniedParameter());
			
			// ExternalSessionManager properties
			this.txtExternalSessionManagerConnectionString.setText(infoBaseInfo.getExternalSessionManagerConnectionString());
			this.btnExternalSessionManagerRequired.setSelection(infoBaseInfo.getExternalSessionManagerRequired());
			
			// SecurityProfile properties			
			this.txtSecurityProfile.setText(infoBaseInfo.getSecurityProfileName());
			this.txtSafeModeSecurityProfile.setText(infoBaseInfo.getSafeModeSecurityProfileName());
			
		}
	}

	private void saveInfobaseProperties() {
		if (infoBaseInfo != null) {

			// Common properties
			infoBaseInfo.setName(infobaseName);
			infoBaseInfo.setDescr(infobaseDescription);
			infoBaseInfo.setLicenseDistributionAllowed(allowDistributeLicense);
			infoBaseInfo.setScheduledJobsDenied(sheduledJobsDenied);

			// DB properties
			infoBaseInfo.setDbServerName(serverDBName);
			infoBaseInfo.setDbms(serverDBType);
			infoBaseInfo.setDbName(databaseDbName);
			infoBaseInfo.setDbUser(databaseDbUser);
			infoBaseInfo.setDbPassword(databaseDbPassword);

			// Lock properties
			infoBaseInfo.setSessionsDenied(sessionsDenied);
			infoBaseInfo.setDeniedFrom(sessionsDeniedFrom);
			infoBaseInfo.setDeniedTo(sessionsDeniedTo);
			infoBaseInfo.setDeniedMessage(deniedMessage);
			infoBaseInfo.setPermissionCode(permissionCode);
			infoBaseInfo.setDeniedParameter(deniedParameter);

			// ExternalSessionManager properties
			infoBaseInfo.setExternalSessionManagerConnectionString(externalSessionManagerConnectionString);
			infoBaseInfo.setExternalSessionManagerRequired(externalSessionManagerRequired);

			// SecurityProfile properties
			infoBaseInfo.setSecurityProfileName(securityProfile);
			infoBaseInfo.setSafeModeSecurityProfileName(safeModeSecurityProfile);

			server.updateInfoBase(clusterInfo.getClusterId(), infoBaseInfo);
			
		}
	}

	private void extractInfobaseVariablesFromControls() {
		
		// Common properties
		infobaseName 			= txtInfobaseName.getText();
		infobaseDescription 	= txtInfobaseDescription.getText();
		allowDistributeLicense 	= btnAllowDistributeLicense.getSelection() ? 1 : 0;
		sheduledJobsDenied 		= btnSheduledJobsDenied.getSelection();
		
		// DB properties
		serverDBName 		= txtServerDBName.getText();
		serverDBType 		= comboServerDBType.getText();
		databaseDbName 		= txtDatabaseDbName.getText();
		databaseDbUser 		= txtDatabaseDbUser.getText();
		databaseDbPassword 	= txtDatabaseDbPassword.getText();
		
		// Lock properties
		sessionsDenied 		= btnSessionsDenied.getSelection();
		sessionsDeniedFrom 	= convertDateTime(deniedFromDate, deniedFromTime);
		sessionsDeniedTo 	= convertDateTime(deniedToDate, deniedToTime);
		deniedMessage 		= txtDeniedMessage.getText();
		permissionCode 		= txtPermissionCode.getText();
		deniedParameter 	= txtDeniedParameter.getText();
		
		// ExternalSessionManager properties
		externalSessionManagerConnectionString 	= txtExternalSessionManagerConnectionString.getText();
		externalSessionManagerRequired 			= btnExternalSessionManagerRequired.getSelection();
		
		// SecurityProfile properties			
		securityProfile 		= txtSecurityProfile.getText();
		safeModeSecurityProfile = txtSafeModeSecurityProfile.getText();
	}

	private Date convertDateTime(DateTime date, DateTime time) {
		
		int year = date.getYear() - 1900; // чтото не так с конвертацией
		int month = date.getMonth();
		int day = date.getDay();
		int hrs = time.getHours();
		int min = time.getMinutes();
		int sec = time.getSeconds();

		return new Date(year, month, day, hrs, min, sec);
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
				extractInfobaseVariablesFromControls();
				saveInfobaseProperties();
				close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, "Apply", false);
		buttonApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				extractInfobaseVariablesFromControls();
				saveInfobaseProperties();
			}
		});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(480, 740);
	}

}
