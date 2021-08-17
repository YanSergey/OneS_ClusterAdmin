package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.InfoBaseInfo;

import ru.yanygin.clusterAdminLibrary.Server;

public class CreateInfobaseDialog extends Dialog {
		
	private UUID clusterId;

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
	private Combo comboSecurityLevel; // Disable, Connection only, Constantly (Выключено, Только соединение, Постоянно)
	private Combo comboServerDBType; // MSSQLServer, PostgreSQL, IBMDB2, OracleDatabase
	private Combo comboLocale; // Откуда то загрузить все возможные локали
	private Combo comboDateOffset;

	private UUID newInfobaseUUID;
	
	private UUID sampleInfobaseId;

	
	public UUID getNewInfobaseUUID() {
		return newInfobaseUUID;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */	
	public CreateInfobaseDialog(Shell parentShell, Server server, UUID clusterId, UUID sampleInfobaseId) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterId = clusterId;
		this.sampleInfobaseId = sampleInfobaseId;
		
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
		txtInfobaseName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				txtDatabaseDbName.setText(((Text)e.widget).getText());
			}
		});
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
		comboSecurityLevel.setToolTipText(Messages.getString("Dialogs.SecurityLevel")); //$NON-NLS-1$
		comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		comboSecurityLevel.setText(Messages.getString("Dialogs.Disable")); //$NON-NLS-1$
		
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

		comboServerDBType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean dateOffsetEnabled = comboServerDBType.getText().equals(Messages.getString("InfobaseDialog.MSSQLServer")); //$NON-NLS-1$
				comboDateOffset.setEnabled(dateOffsetEnabled);
				comboDateOffset.setText("0"); //$NON-NLS-1$
			}
		});
		
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
		
		Label lblLocale = new Label(container, SWT.NONE);
		lblLocale.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocale.setText(Messages.getString("InfobaseDialog.Locale")); //$NON-NLS-1$
		
		comboLocale = new Combo(container, SWT.READ_ONLY);
		comboLocale.setItems(new String[] {Messages.getString("InfobaseDialog.ru_RU"), Messages.getString("InfobaseDialog.en_US"), "xx_XX", "yy_YY"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		comboLocale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboLocale.select(0);
		
		Label lblDateOffset = new Label(container, SWT.NONE);
		lblDateOffset.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDateOffset.setText(Messages.getString("InfobaseDialog.DateOffset")); //$NON-NLS-1$
		
		comboDateOffset = new Combo(container, SWT.READ_ONLY);
		comboDateOffset.setItems(new String[] {"0", "2000"}); //$NON-NLS-1$ //$NON-NLS-2$
		comboDateOffset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboDateOffset.setText("2000"); //$NON-NLS-1$
		
		btnInfobaseCreationMode = new Button(container, SWT.CHECK);
		btnInfobaseCreationMode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnInfobaseCreationMode.setText(Messages.getString("InfobaseDialog.CreateDatabaseIfNotAvailable")); //$NON-NLS-1$
		
		btnSheduledJobsDenied = new Button(container, SWT.CHECK);
		btnSheduledJobsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSheduledJobsDenied.setText(Messages.getString("InfobaseDialog.SheduledJobsDenied")); //$NON-NLS-1$
		new Label(container, SWT.NONE);

		initInfobaseProperties();
		
		return container;
	}

	private void initInfobaseProperties() {
		if (sampleInfobaseId == null) {
			
			txtInfobaseName.setText(""); //$NON-NLS-1$
			txtInfobaseDescription.setText(""); //$NON-NLS-1$
			comboSecurityLevel.select(0);
			btnAllowDistributeLicense.setSelection(false);
			btnSheduledJobsDenied.setSelection(false);
			
			// DB properties
			txtServerDBName.setText(""); //$NON-NLS-1$
			comboServerDBType.select(0);
			txtDatabaseDbName.setText(""); //$NON-NLS-1$
			txtDatabaseDbUser.setText(""); //$NON-NLS-1$
			txtDatabaseDbPassword.setText(""); //$NON-NLS-1$
			txtDatabaseDbPassword.setToolTipText(Messages.getString("InfobaseDialog.YouNeedToEnter")); //$NON-NLS-1$
			
		} else {
			
			IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, sampleInfobaseId);
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
			txtDatabaseDbPassword.setText(""); //$NON-NLS-1$
			txtDatabaseDbPassword.setToolTipText(Messages.getString("InfobaseDialog.YouNeedToEnter")); //$NON-NLS-1$
			
			txtInfobaseName.setForeground(new Color(255, 0, 0));
			txtDatabaseDbName.setForeground(new Color(255, 0, 0));
			btnInfobaseCreationMode.setForeground(new Color(255, 0, 0));
			txtDatabaseDbPassword.setForeground(new Color(255, 0, 0));
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
		
		return existsError;
	}
	
	private boolean saveInfobaseProperties() {

		if (checkVariablesFromControls())
			return false;
		
		IInfoBaseInfo infoBaseInfo = new InfoBaseInfo((int) comboSecurityLevel.getData(comboSecurityLevel.getText()));

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
		infoBaseInfo.setLocale(comboLocale.getText());
		if (comboServerDBType.getText().equals(Messages.getString("InfobaseDialog.MSSQLServer"))) //$NON-NLS-1$
			infoBaseInfo.setDateOffset(Integer.parseInt(comboDateOffset.getText()));

		try {
			newInfobaseUUID = server.createInfoBase(clusterId, infoBaseInfo,
					(btnInfobaseCreationMode.getSelection() ? 1 : 0));
		} catch (Exception excp) {
			var messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
			return false;
		}
		return true;
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
				if (saveInfobaseProperties())
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
		return new Point(400, 500);
	}

}
