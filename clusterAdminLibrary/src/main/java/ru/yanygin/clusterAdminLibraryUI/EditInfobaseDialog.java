package ru.yanygin.clusterAdminLibraryUI;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

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
	private Text deniedFromDate;
	private Text deniedToDate;

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
		lblSecurityLevel.setText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		
		comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
		comboSecurityLevel.setEnabled(false);
		comboSecurityLevel.setToolTipText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
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
		compositeDeniedFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		compositeDeniedFrom.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedFromDate = new Text(compositeDeniedFrom, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		
		Label lblDeniedTo = new Label(container, SWT.NONE);
		lblDeniedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDeniedTo.setText(Messages.getString("InfobaseDialog.DeniedTo")); //$NON-NLS-1$
		
		Composite compositeDeniedTo = new Composite(container, SWT.NONE);
		compositeDeniedTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		compositeDeniedTo.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		deniedToDate = new Text(compositeDeniedTo, SWT.BORDER);

		Label lblDeniedMessage = new Label(container, SWT.NONE);
		lblDeniedMessage.setText(Messages.getString("InfobaseDialog.DeniedMessage")); //$NON-NLS-1$
		
		txtDeniedMessage = new Text(container, SWT.BORDER);
		txtDeniedMessage.setToolTipText(Messages.getString("InfobaseDialog.DeniedMessage")); //$NON-NLS-1$
		GridData gdtxtDeniedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gdtxtDeniedMessage.heightHint = 63;
		txtDeniedMessage.setLayoutData(gdtxtDeniedMessage);
		
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
			txtInfobaseName.setText(infoBaseInfo.getName());
			txtInfobaseDescription.setText(infoBaseInfo.getDescr());
			comboSecurityLevel.setText(Integer.toString(infoBaseInfo.getSecurityLevel()));
			btnAllowDistributeLicense.setSelection(infoBaseInfo.getLicenseDistributionAllowed() == 1);
			btnSheduledJobsDenied.setSelection(infoBaseInfo.isScheduledJobsDenied());
			
			// DB properties
			txtServerDBName.setText(infoBaseInfo.getDbServerName());
			comboServerDBType.setText(infoBaseInfo.getDbms());
			txtDatabaseDbName.setText(infoBaseInfo.getDbName());
			txtDatabaseDbUser.setText(infoBaseInfo.getDbUser());
			txtDatabaseDbPassword.setText(infoBaseInfo.getDbPassword());
			
			// Lock properties
			btnSessionsDenied.setSelection(infoBaseInfo.isSessionsDenied());
			deniedFromDate.setText(convertDateToString(infoBaseInfo.getDeniedFrom()));
			deniedToDate.setText(convertDateToString(infoBaseInfo.getDeniedTo()));
			
			txtDeniedMessage.setText(infoBaseInfo.getDeniedMessage());
			txtPermissionCode.setText(infoBaseInfo.getPermissionCode());
			txtDeniedParameter.setText(infoBaseInfo.getDeniedParameter());
			
			// ExternalSessionManager properties
			txtExternalSessionManagerConnectionString.setText(infoBaseInfo.getExternalSessionManagerConnectionString());
			btnExternalSessionManagerRequired.setSelection(infoBaseInfo.getExternalSessionManagerRequired());
			
			// SecurityProfile properties			
			txtSecurityProfile.setText(infoBaseInfo.getSecurityProfileName());
			txtSafeModeSecurityProfile.setText(infoBaseInfo.getSafeModeSecurityProfileName());
			
		}
	}
	
	private boolean checkVariablesFromControls() {
		
		var existsError = false;
		
		List<Text> checksTextControls = new ArrayList<>();
		checksTextControls.add(txtInfobaseName);
		checksTextControls.add(txtServerDBName);
		checksTextControls.add(txtDatabaseDbName);
		checksTextControls.add(txtDatabaseDbUser);
		
		for (Text control : checksTextControls) {
			if (control.getText().isBlank()) {
				control.setBackground(SWTResourceManager.getColor(255, 204, 204));
				existsError = true;
			} else {
				control.setBackground(SWTResourceManager.getColor(255, 255, 255));
			}			
		}
		
		List<Text> checksDateControls = new ArrayList<>();
		checksDateControls.add(deniedFromDate);
		checksDateControls.add(deniedToDate);
		
		for (Text control : checksDateControls) {
			if (control.getText().isBlank()) {
				control.setBackground(SWTResourceManager.getColor(255, 255, 255));
			} else {
				if (convertStringToDate(control.getText()).equals(new Date(0))) {
					control.setBackground(SWTResourceManager.getColor(255, 204, 204));
					existsError = true;
				}
			}			
		}
		
		return existsError;
	}
	
	private boolean saveInfobaseProperties() {

		if (checkVariablesFromControls())
			return false;
		
		IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infoBaseId);
		if (infoBaseInfo == null)
			return false;
		
		// Common properties
		infoBaseInfo.setName(txtInfobaseName.getText());
		infoBaseInfo.setDescr(txtInfobaseDescription.getText());
		infoBaseInfo.setLicenseDistributionAllowed(btnAllowDistributeLicense.getSelection() ? 1 : 0);
		infoBaseInfo.setScheduledJobsDenied(btnSheduledJobsDenied.getSelection());
		
		// DB properties
		infoBaseInfo.setDbServerName(txtServerDBName.getText());
		infoBaseInfo.setDbms(comboServerDBType.getText());
		infoBaseInfo.setDbName(txtDatabaseDbName.getText());
		infoBaseInfo.setDbUser(txtDatabaseDbUser.getText());
		infoBaseInfo.setDbPassword(txtDatabaseDbPassword.getText());
		
		// Lock properties
		infoBaseInfo.setSessionsDenied(btnSessionsDenied.getSelection());
		infoBaseInfo.setDeniedFrom(convertStringToDate(deniedFromDate.getText()));
		infoBaseInfo.setDeniedTo(convertStringToDate(deniedToDate.getText()));
		
		infoBaseInfo.setDeniedMessage(txtDeniedMessage.getText());
		infoBaseInfo.setPermissionCode(txtPermissionCode.getText());
		infoBaseInfo.setDeniedParameter(txtDeniedParameter.getText());
		
		// ExternalSessionManager properties
		infoBaseInfo.setExternalSessionManagerConnectionString(txtExternalSessionManagerConnectionString.getText());
		infoBaseInfo.setExternalSessionManagerRequired(btnExternalSessionManagerRequired.getSelection());
		
		// SecurityProfile properties
		infoBaseInfo.setSecurityProfileName(txtSecurityProfile.getText());
		infoBaseInfo.setSafeModeSecurityProfileName(txtSafeModeSecurityProfile.getText());
		
		return server.updateInfoBase(clusterId, infoBaseInfo);		
	}
	
	private Date convertStringToDate(String date) {
		
		Date emptyDate = new Date(0);
		
		if (date.isBlank())
			return emptyDate;
		
		Date convertDate;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			convertDate = dateFormat.parse(date);
		} catch (ParseException excp) {
			excp.printStackTrace();
			convertDate = emptyDate;
		}
		
		return convertDate;
	}
	
	private String convertDateToString(Date date) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date emptyDate =  new Date(0);
		
		return date.equals(emptyDate) ? "" : dateFormat.format(date);
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
