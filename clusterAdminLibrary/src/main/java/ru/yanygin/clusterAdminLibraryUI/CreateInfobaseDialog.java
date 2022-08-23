package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.InfoBaseInfo;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог создания информационной базы. */
public class CreateInfobaseDialog extends Dialog {

  private static final String DBMS_TYPE_MSSQLSERVER = "MSSQLServer"; //$NON-NLS-1$
  private static final String DBMS_TYPE_POSTGRESQL = "PostgreSQL"; //$NON-NLS-1$
  private static final String DBMS_TYPE_IBMDB2 = "IBMDB2"; //$NON-NLS-1$
  private static final String DBMS_TYPE_ORACLEDATABASE = "OracleDatabase"; //$NON-NLS-1$
  private static final String ZERO_OFFSET = "0"; //$NON-NLS-1$
  private static final String MSSQL_OFFSET = "2000"; //$NON-NLS-1$

  private Server server;
  private UUID clusterId;
  private UUID newInfobaseUuid;
  private UUID sampleInfobaseId;

  private Text txtInfobaseName;
  private Text txtServerDbName;
  private Text txtDatabaseDbName;
  private Text txtDatabaseDbUser;
  private Text txtDatabaseDbPassword;
  private Text txtInfobaseDescription;
  
  private Combo comboSecurityLevel;
  private Combo comboServerDbType;
  private Combo comboLocale; // Откуда то загрузить все возможные локали
  private Combo comboDateOffset;

  private Button btnSheduledJobsDenied;
  private Button btnAllowDistributeLicense;
  private Button btnInfobaseCreationMode;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server parameters
   * @param clusterId - cluster ID
   * @param sampleInfobaseId - infobase ID for create from sample, else null
   */
  public CreateInfobaseDialog(
      Shell parentShell, Server server, UUID clusterId, UUID sampleInfobaseId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
    this.sampleInfobaseId = sampleInfobaseId;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(Strings.TITLE_WINDOW);
  }

  /**
   * Create contents of the dialog.
   *
   * @param parent - parent composite
   */
  @Override
  protected Control createDialogArea(Composite parent) {

    Composite container = (Composite) super.createDialogArea(parent);
    GridLayout gridLayout = (GridLayout) container.getLayout();
    gridLayout.numColumns = 2;

    Label lblInfobaseName = new Label(container, SWT.NONE);
    lblInfobaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfobaseName.setText(Strings.INFOBASE_NAME);

    txtInfobaseName = new Text(container, SWT.BORDER);
    txtInfobaseName.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            txtDatabaseDbName.setText(((Text) e.widget).getText());
          }
        });
    txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblInfobaseDescription = new Label(container, SWT.NONE);
    lblInfobaseDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfobaseDescription.setText(Strings.INFOBASE_DESCRIPTION);

    txtInfobaseDescription = new Text(container, SWT.BORDER);
    txtInfobaseDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblSecurityLevel = new Label(container, SWT.NONE);
    lblSecurityLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSecurityLevel.setText(Strings.SECURITY_LEVEL);

    comboSecurityLevel = new Combo(container, SWT.READ_ONLY);
    comboSecurityLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    comboSecurityLevel.add(Strings.SECURITY_LEVEL_DISABLE);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_DISABLE, 0);
    comboSecurityLevel.add(Strings.SECURITY_LEVEL_CONNECTIONONLY);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_CONNECTIONONLY, 1);
    comboSecurityLevel.add(Strings.SECURITY_LEVEL_CONSTANTLY);
    comboSecurityLevel.setData(Strings.SECURITY_LEVEL_CONSTANTLY, 2);
    comboSecurityLevel.select(0);

    Label lblServerDbName = new Label(container, SWT.NONE);
    lblServerDbName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblServerDbName.setText(Strings.SERVER_DB_NAME);

    txtServerDbName = new Text(container, SWT.BORDER);
    txtServerDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblServerDbType = new Label(container, SWT.NONE);
    lblServerDbType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblServerDbType.setText(Strings.DBMS_TYPE);

    comboServerDbType = new Combo(container, SWT.READ_ONLY);
    comboServerDbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    comboServerDbType.add(DBMS_TYPE_MSSQLSERVER);
    comboServerDbType.add(DBMS_TYPE_POSTGRESQL);
    comboServerDbType.add(DBMS_TYPE_IBMDB2);
    comboServerDbType.add(DBMS_TYPE_ORACLEDATABASE);
    comboServerDbType.select(0);

    comboServerDbType.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            boolean dbmsTypeIsMsSql = comboServerDbType.getText().equals(DBMS_TYPE_MSSQLSERVER);
            comboDateOffset.setEnabled(dbmsTypeIsMsSql);
            comboDateOffset.setText(dbmsTypeIsMsSql ? MSSQL_OFFSET : ZERO_OFFSET);
          }
        });

    Label lblDatabaseDbName = new Label(container, SWT.NONE);
    lblDatabaseDbName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDatabaseDbName.setText(Strings.DATABASE_DB_NAME);

    txtDatabaseDbName = new Text(container, SWT.BORDER);
    txtDatabaseDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblDatabaseDbUser = new Label(container, SWT.NONE);
    lblDatabaseDbUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDatabaseDbUser.setText(Strings.DATABASE_DB_USER);

    txtDatabaseDbUser = new Text(container, SWT.BORDER);
    txtDatabaseDbUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblDatabaseDbPassword = new Label(container, SWT.NONE);
    lblDatabaseDbPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDatabaseDbPassword.setAlignment(SWT.RIGHT);
    lblDatabaseDbPassword.setText(Strings.DATABASE_DB_PASSWORD);

    txtDatabaseDbPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
    txtDatabaseDbPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnAllowDistributeLicense = new Button(container, SWT.CHECK);
    btnAllowDistributeLicense.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnAllowDistributeLicense.setText(Strings.ALLOW_DISTRIBUTE_LICENSE);

    Label lblLocale = new Label(container, SWT.NONE);
    lblLocale.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblLocale.setText(Strings.LOCALE);

    comboLocale = new Combo(container, SWT.READ_ONLY);
    comboLocale.setItems(getLocales());
    comboLocale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    comboLocale.select(0);

    Label lblDateOffset = new Label(container, SWT.NONE);
    lblDateOffset.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDateOffset.setText(Strings.DATE_OFFSET);

    comboDateOffset = new Combo(container, SWT.READ_ONLY);
    comboDateOffset.setItems(ZERO_OFFSET, MSSQL_OFFSET);
    comboDateOffset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    comboDateOffset.setText(MSSQL_OFFSET);

    btnInfobaseCreationMode = new Button(container, SWT.CHECK);
    btnInfobaseCreationMode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnInfobaseCreationMode.setText(Strings.CREATE_DATABASE_IF_NOTAVAILABLE);

    btnSheduledJobsDenied = new Button(container, SWT.CHECK);
    btnSheduledJobsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnSheduledJobsDenied.setText(Strings.SHEDULED_JOBS_DENIED);
    new Label(container, SWT.NONE);

    initProperties();

    return container;
  }

  private void initProperties() {
    if (sampleInfobaseId == null) {

      txtInfobaseName.setText(""); //$NON-NLS-1$
      txtInfobaseDescription.setText(""); //$NON-NLS-1$
      comboSecurityLevel.select(0);
      btnAllowDistributeLicense.setSelection(false);
      btnSheduledJobsDenied.setSelection(false);

      // DB properties
      txtServerDbName.setText(""); //$NON-NLS-1$
      comboServerDbType.select(0);
      txtDatabaseDbName.setText(""); //$NON-NLS-1$
      txtDatabaseDbUser.setText(""); //$NON-NLS-1$
      txtDatabaseDbPassword.setText(""); //$NON-NLS-1$
      txtDatabaseDbPassword.setToolTipText(Strings.NEED_TO_ENTER);

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
      txtServerDbName.setText(infoBaseInfo.getDbServerName());
      comboServerDbType.setText(infoBaseInfo.getDbms());
      txtDatabaseDbName.setText(infoBaseInfo.getDbName());
      txtDatabaseDbUser.setText(infoBaseInfo.getDbUser());
      txtDatabaseDbPassword.setText(""); //$NON-NLS-1$
      txtDatabaseDbPassword.setToolTipText(Strings.NEED_TO_ENTER);

      txtInfobaseName.setForeground(Helper.getRedColor());
      txtDatabaseDbName.setForeground(Helper.getRedColor());
      btnInfobaseCreationMode.setForeground(Helper.getRedColor());
      txtDatabaseDbPassword.setForeground(Helper.getRedColor());
    }
  }

  private boolean checkVariablesFromControls() {

    var existsError = false;

    List<Text> checksTextControls = new ArrayList<>();
    checksTextControls.add(txtInfobaseName);
    checksTextControls.add(txtServerDbName);
    checksTextControls.add(txtDatabaseDbName);
    checksTextControls.add(txtDatabaseDbUser);

    for (Text control : checksTextControls) {
      if (control.getText().isBlank()) {
        control.setBackground(Helper.getPinkColor());
        existsError = true;
      } else {
        control.setBackground(Helper.getWhiteColor());
      }
    }

    return existsError;
  }

  private boolean saveInfobaseProperties() {

    if (checkVariablesFromControls()) {
      return false;
    }

    IInfoBaseInfo infoBaseInfo =
        new InfoBaseInfo((int) comboSecurityLevel.getData(comboSecurityLevel.getText()));

    // Common properties
    infoBaseInfo.setName(txtInfobaseName.getText());
    infoBaseInfo.setDescr(txtInfobaseDescription.getText());
    infoBaseInfo.setLicenseDistributionAllowed(btnAllowDistributeLicense.getSelection() ? 1 : 0);
    infoBaseInfo.setScheduledJobsDenied(btnSheduledJobsDenied.getSelection());

    // DB properties
    infoBaseInfo.setDbServerName(txtServerDbName.getText());
    infoBaseInfo.setDbms(comboServerDbType.getText());
    infoBaseInfo.setDbName(txtDatabaseDbName.getText());
    infoBaseInfo.setDbUser(txtDatabaseDbUser.getText());
    infoBaseInfo.setDbPassword(txtDatabaseDbPassword.getText());
    infoBaseInfo.setLocale(comboLocale.getText());
    if (comboServerDbType.getText().equals(DBMS_TYPE_MSSQLSERVER)) {
      infoBaseInfo.setDateOffset(Integer.parseInt(comboDateOffset.getText()));
    }

    newInfobaseUuid =
        server.createInfoBase(
            clusterId, infoBaseInfo, (btnInfobaseCreationMode.getSelection() ? 1 : 0));

    return newInfobaseUuid != Helper.EMPTY_UUID;
  }

  /**
   * Возвращает ID созданной инфобазы (при создании из образца).
   *
   * @return ID инфобазы
   */
  public UUID getNewInfobaseUuid() {
    return newInfobaseUuid;
  }

  private String[] getLocales() {
    // return Locale.getAvailableLocales();
    return new String[] {"ru_RU", "en_US", "xx_XX", "yy_YY"};
  }

  /**
   * Create contents of the button bar.
   *
   * @param parent - parent composite
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    Button buttonOk =
        createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
    buttonOk.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            if (saveInfobaseProperties()) {
              close();
            }
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String INFOBASE_NAME = getString("InfobaseName");
    static final String INFOBASE_DESCRIPTION = getString("Description");
    static final String SECURITY_LEVEL = Messages.getString("Dialogs.SecurityLevel");
    static final String SECURITY_LEVEL_DISABLE = Messages.getString("Dialogs.Disable");
    static final String SECURITY_LEVEL_CONNECTIONONLY =
        Messages.getString("Dialogs.ConnectionOnly");
    static final String SECURITY_LEVEL_CONSTANTLY = Messages.getString("Dialogs.Constantly");
    static final String SERVER_DB_NAME = getString("ServerDBName");
    static final String DBMS_TYPE = getString("DBMSType");
    static final String DATABASE_DB_NAME = getString("DatabaseDBName");
    static final String DATABASE_DB_USER = getString("DatabaseDBUser");
    static final String DATABASE_DB_PASSWORD = getString("DatabaseDBPassword");
    static final String ALLOW_DISTRIBUTE_LICENSE = getString("AllowDistributeLicense");
    static final String LOCALE = getString("Locale");
    static final String DATE_OFFSET = getString("DateOffset");
    static final String CREATE_DATABASE_IF_NOTAVAILABLE = getString("CreateDatabaseIfNotAvailable");
    static final String SHEDULED_JOBS_DENIED = getString("SheduledJobsDenied");
    static final String NEED_TO_ENTER = getString("YouNeedToEnter");

    static String getString(String key) {
      return Messages.getString("InfobaseDialog." + key); //$NON-NLS-1$
    }
  }
}
