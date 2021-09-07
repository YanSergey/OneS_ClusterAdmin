package ru.yanygin.clusterAdminLibraryUI;

import java.util.Date;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.IInfoBaseInfo;

import ru.yanygin.clusterAdminLibrary.Server;

public class EditInfobaseDialog extends Dialog {
	
	private UUID infoBaseId;
	private UUID clusterId;
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
	private Combo comboSecurityLevel;
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
	public EditInfobaseDialog(Shell parentShell, Server server, UUID clusterId, UUID infoBaseId) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterId = clusterId;
		this.infoBaseId = infoBaseId;
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
		lblInfobaseName.setText(Messages.getString("InfobaseDialog.InfobaseName")); //$NON-NLS-1$
		
		txtInfobaseName = new Text(container, SWT.BORDER);
		txtInfobaseName.setToolTipText(Messages.getString("InfobaseDialog.InfobaseName")); //$NON-NLS-1$
		txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblInfobaseDescription = new Label(container, SWT.NONE);
		lblInfobaseDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfobaseDescription.setText(Messages.getString("InfobaseDialog.Description")); //$NON-NLS-1$
		
		txtInfobaseDescription = new Text(container, SWT.BORDER);
		txtInfobaseDescription.setToolTipText(Messages.getString("InfobaseDialog.Description")); //$NON-NLS-1$
		txtInfobaseDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSecurityLevel = new Label(container, SWT.NONE);
		lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblSecurityLevel.setToolTipText("");
		lblSecurityLevel.setText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		
		comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
		comboSecurityLevel.setEnabled(false);
		comboSecurityLevel.setToolTipText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		comboSecurityLevel.setText(Messages.getString("Dialogs.DisNable")); //$NON-NLS-1$
		
		comboSecurityLevel.add(Messages.getString("Dialogs.Disable")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.Disable"), 0); //$NON-NLS-1$
		comboSecurityLevel.add(Messages.getString("Dialogs.ConnectionOnly")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.ConnectionOnly"), 1); //$NON-NLS-1$
		comboSecurityLevel.add(Messages.getString("Dialogs.Constantly")); //$NON-NLS-1$
		comboSecurityLevel.setData(Messages.getString("Dialogs.Constantly"), 2); //$NON-NLS-1$
		comboSecurityLevel.select(0);
		
		Label lblServerDBName = new Label(container, SWT.NONE);
		lblServerDBName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBName.setText(Messages.getString("InfobaseDialog.ServerDBName")); //$NON-NLS-1$
		
		txtServerDBName = new Text(container, SWT.BORDER);
		txtServerDBName.setToolTipText(Messages.getString("InfobaseDialog.ServerDBName")); //$NON-NLS-1$
		txtServerDBName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblServerDBType = new Label(container, SWT.NONE);
		lblServerDBType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBType.setText(Messages.getString("InfobaseDialog.DBMSType")); //$NON-NLS-1$
		
		comboServerDBType = new Combo(container, SWT.READ_ONLY);
		comboServerDBType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		comboServerDBType.add(Messages.getString("InfobaseDialog.MSSQLServer")); //$NON-NLS-1$
		comboServerDBType.add(Messages.getString("InfobaseDialog.PostgreSQL")); //$NON-NLS-1$
		comboServerDBType.add(Messages.getString("InfobaseDialog.IBMDB2")); //$NON-NLS-1$
		comboServerDBType.add(Messages.getString("InfobaseDialog.OracleDatabase")); //$NON-NLS-1$
		comboServerDBType.select(0);
		
		Label lblDatabaseDbName = new Label(container, SWT.NONE);
		lblDatabaseDbName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbName.setText(Messages.getString("InfobaseDialog.DatabaseDBName")); //$NON-NLS-1$
		
		txtDatabaseDbName = new Text(container, SWT.BORDER);
		txtDatabaseDbName.setToolTipText(Messages.getString("InfobaseDialog.DatabaseDBName")); //$NON-NLS-1$
		txtDatabaseDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabaseDbUser = new Label(container, SWT.NONE);
		lblDatabaseDbUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbUser.setText(Messages.getString("InfobaseDialog.DatabaseDBUser")); //$NON-NLS-1$
		
		txtDatabaseDbUser = new Text(container, SWT.BORDER);
		txtDatabaseDbUser.setToolTipText(Messages.getString("InfobaseDialog.DatabaseDBUser")); //$NON-NLS-1$
		txtDatabaseDbUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabaseDbPassword = new Label(container, SWT.NONE);
		lblDatabaseDbPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseDbPassword.setAlignment(SWT.RIGHT);
		lblDatabaseDbPassword.setText(Messages.getString("InfobaseDialog.DatabaseDBPassword")); //$NON-NLS-1$
		
		txtDatabaseDbPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtDatabaseDbPassword.setToolTipText(Messages.getString("InfobaseDialog.DatabaseDBPassword")); //$NON-NLS-1$
		txtDatabaseDbPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnAllowDistributeLicense = new Button(container, SWT.CHECK);
		btnAllowDistributeLicense.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnAllowDistributeLicense.setText(Messages.getString("InfobaseDialog.AllowDistributeLicense")); //$NON-NLS-1$
		
		btnSessionsDenied = new Button(container, SWT.CHECK);
		btnSessionsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSessionsDenied.setText(Messages.getString("InfobaseDialog.SessionsDenied")); //$NON-NLS-1$
		
		Label lblDeniedFrom = new Label(container, SWT.NONE);
		lblDeniedFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedFrom.setText(Messages.getString("InfobaseDialog.DeniedFrom")); //$NON-NLS-1$
		
		Composite compositeDeniedFrom = new Composite(container, SWT.NONE);
		compositeDeniedFrom.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedFromDate = new CDateTime(compositeDeniedFrom, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		
		deniedFromTime = new DateTime(compositeDeniedFrom, SWT.BORDER | SWT.TIME);
		
		Label lblDeniedTo = new Label(container, SWT.NONE);
		lblDeniedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedTo.setText(Messages.getString("InfobaseDialog.DeniedTo")); //$NON-NLS-1$
		
		Composite compositeDeniedTo = new Composite(container, SWT.NONE);
		compositeDeniedTo.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedToDate = new DateTime(compositeDeniedTo, SWT.NONE | SWT.DROP_DOWN);
		
		deniedToTime = new DateTime(compositeDeniedTo, SWT.BORDER | SWT.TIME);

		Label lblDeniedMessage = new Label(container, SWT.NONE);
		lblDeniedMessage.setText(Messages.getString("InfobaseDialog.DeniedMessage")); //$NON-NLS-1$
		
		txtDeniedMessage = new Text(container, SWT.BORDER);
		txtDeniedMessage.setToolTipText(Messages.getString("InfobaseDialog.DeniedMessage")); //$NON-NLS-1$
		GridData gd_txtDeniedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtDeniedMessage.heightHint = 63;
		txtDeniedMessage.setLayoutData(gd_txtDeniedMessage);
		
		Label lblPermissionCode = new Label(container, SWT.NONE);
		lblPermissionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPermissionCode.setText(Messages.getString("InfobaseDialog.PermissionCode")); //$NON-NLS-1$
		
		txtPermissionCode = new Text(container, SWT.BORDER);
		txtPermissionCode.setToolTipText(Messages.getString("InfobaseDialog.PermissionCode")); //$NON-NLS-1$
		txtPermissionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDeniedParameter = new Label(container, SWT.NONE);
		lblDeniedParameter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedParameter.setText(Messages.getString("InfobaseDialog.DeniedParameter")); //$NON-NLS-1$
		
		txtDeniedParameter = new Text(container, SWT.BORDER);
		txtDeniedParameter.setToolTipText(Messages.getString("InfobaseDialog.DeniedParameter")); //$NON-NLS-1$
		txtDeniedParameter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSheduledJobsDenied = new Button(container, SWT.CHECK);
		btnSheduledJobsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSheduledJobsDenied.setText(Messages.getString("InfobaseDialog.SheduledJobsDenied")); //$NON-NLS-1$
		
		Label lblExternalSessionManagerConnectionString = new Label(container, SWT.NONE);
		lblExternalSessionManagerConnectionString.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExternalSessionManagerConnectionString.setText(Messages.getString("InfobaseDialog.ExternalSessionManagement")); //$NON-NLS-1$
		
		txtExternalSessionManagerConnectionString = new Text(container, SWT.BORDER);
		txtExternalSessionManagerConnectionString.setToolTipText(Messages.getString("InfobaseDialog.ExternalSessionManagement")); //$NON-NLS-1$
		txtExternalSessionManagerConnectionString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnExternalSessionManagerRequired = new Button(container, SWT.CHECK);
		btnExternalSessionManagerRequired.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnExternalSessionManagerRequired.setText(Messages.getString("InfobaseDialog.RequiredUseOfExternalManagement")); //$NON-NLS-1$
		
		Label lblSecurityProfile = new Label(container, SWT.NONE);
		lblSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityProfile.setText(Messages.getString("InfobaseDialog.SecurityProfile")); //$NON-NLS-1$
		
		txtSecurityProfile = new Text(container, SWT.BORDER);
		txtSecurityProfile.setToolTipText(Messages.getString("InfobaseDialog.SecurityProfile")); //$NON-NLS-1$
		txtSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSafeModeSecurityProfile = new Label(container, SWT.NONE);
		lblSafeModeSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeModeSecurityProfile.setText(Messages.getString("InfobaseDialog.SafeModeSecurityProfile")); //$NON-NLS-1$
		
		txtSafeModeSecurityProfile = new Text(container, SWT.BORDER);
		txtSafeModeSecurityProfile.setToolTipText(Messages.getString("InfobaseDialog.SafeModeSecurityProfile")); //$NON-NLS-1$
		txtSafeModeSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initInfobaseProperties();

		
		return container;
	}

	private void initInfobaseProperties() {
		if (infoBaseId != null) {
			
			IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infoBaseId);
			if (infoBaseInfo == null) {
				close();
				return;
			}

			// Common properties
			this.txtInfobaseName.setText(infoBaseInfo.getName());
			this.txtInfobaseDescription.setText(infoBaseInfo.getDescr());
			this.comboSecurityLevel.setText(Integer.toString(infoBaseInfo.getSecurityLevel()));
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
			
			// TODO: Надо разбираться с датами/ Пустая дата не устанавливается в контрол
			Date emptyDate =  new Date(70, 0, 1, 3, 0, 0);
			Date deniedFrom = infoBaseInfo.getDeniedFrom();
			if (deniedFrom.equals(emptyDate)) {
//				this.deniedFromDate.setDate(0, 0, 0);
//				this.deniedFromDate.setTime(0, 0, 0);
//				this.deniedFromTime.setDate(0, 0, 0);
//				this.deniedFromTime.setTime(0, 0, 0);
				this.deniedFromDate.setDate(null, null, null);
				this.deniedFromDate.setTime(null, null, null);
				this.deniedFromTime.setDate(null, null, null);
				this.deniedFromTime.setTime(null, null, null);
//			} else {	
				this.deniedFromDate.setDate(1900 + deniedFrom.getYear(), deniedFrom.getMonth(), deniedFrom.getDate());
				this.deniedFromTime.setTime(deniedFrom.getHours(), deniedFrom.getMinutes(), deniedFrom.getSeconds());
			}
			
			Date deniedTo  	= infoBaseInfo.getDeniedTo();
//			if (deniedTo.equals(emptyDate)) {
//				this.deniedToDate.setDate(0, 0, 0);
//				this.deniedToDate.setTime(0, 0, 0);
//				this.deniedToTime.setDate(0, 0, 0);
//				this.deniedToTime.setTime(0, 0, 0);
//			} else {	
				this.deniedToDate.setDate(1900 + deniedTo.getYear(), deniedTo.getMonth(), deniedTo.getDate());
				this.deniedToTime.setTime(deniedTo.getHours(), deniedTo.getMinutes(), deniedTo.getSeconds());
//			}
			
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
	
	private boolean saveInfobaseProperties() {

		extractInfobaseVariablesFromControls();
		
		IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infoBaseId);
		if (infoBaseInfo == null)
			return false;
		
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
		
		return server.updateInfoBase(clusterId, infoBaseInfo);		
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
				if (saveInfobaseProperties())
					close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, Messages.getString("Dialogs.Apply"), false); //$NON-NLS-1$
		buttonApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveInfobaseProperties();
			}
		});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 745);
	}

}
