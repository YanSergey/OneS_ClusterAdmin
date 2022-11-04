package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IInfoBaseInfo;
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
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог редактирования параметров информационной базы. */
public class InfobaseDialog extends Dialog {

  private static final String DBMS_TYPE_MSSQLSERVER = "MSSQLServer"; //$NON-NLS-1$
  private static final String DBMS_TYPE_POSTGRESQL = "PostgreSQL"; //$NON-NLS-1$
  private static final String DBMS_TYPE_IBMDB2 = "IBMDB2"; //$NON-NLS-1$
  private static final String DBMS_TYPE_ORACLEDATABASE = "OracleDatabase"; //$NON-NLS-1$
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$

  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
  private final Date emptyDate = new Date(0);

  private Server server;
  private UUID clusterId;
  private UUID infoBaseId;
  private boolean creationMode = false;

  // Controls
  private Text txtInfobaseName;
  private Text txtServerDbName;
  private Text txtDatabaseDbName;
  private Text txtDatabaseDbUser;
  private Text txtDatabaseDbPassword;
  private Text txtInfobaseDescription;
  private Text txtPermissionCode;
  private Text txtDeniedParameter;
  private Text txtExternalSessionManagerConnectionString;
  private Text txtSecurityProfile;
  private Text txtSafeModeSecurityProfile;
  private Text txtDeniedMessage;
  private Text deniedFromDate;
  private Text deniedToDate;

  private Combo comboSecurityLevel;
  private Combo comboServerDbType;

  private Button btnSessionsDenied;
  private Button btnSheduledJobsDenied;
  private Button btnAllowDistributeLicense;
  private Button btnExternalSessionManagerRequired;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server parameters
   * @param clusterId - cluster ID
   * @param infoBaseId - infobase ID
   */
  public InfobaseDialog(Shell parentShell, Server server, UUID clusterId, UUID infoBaseId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
    this.infoBaseId = infoBaseId;
    
    // три варианта открытия окна:
    //  - существующая база
    //  - новая база
    //  - новая база (на основе образца)
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
    comboSecurityLevel.setEnabled(false);
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

    btnSessionsDenied = new Button(container, SWT.CHECK);
    btnSessionsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnSessionsDenied.setText(Strings.SESSIONS_DENIED);

    Label lblDeniedFrom = new Label(container, SWT.NONE);
    lblDeniedFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDeniedFrom.setText(Strings.SESSIONS_DENIED_FROM);

    Composite compositeDeniedFrom = new Composite(container, SWT.NONE);
    compositeDeniedFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    compositeDeniedFrom.setLayout(new FillLayout(SWT.HORIZONTAL));

    deniedFromDate = new Text(compositeDeniedFrom, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);

    Label lblDeniedTo = new Label(container, SWT.NONE);
    lblDeniedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDeniedTo.setText(Strings.SESSIONS_DENIED_TO);

    Composite compositeDeniedTo = new Composite(container, SWT.NONE);
    compositeDeniedTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    compositeDeniedTo.setLayout(new FillLayout(SWT.HORIZONTAL));

    deniedToDate = new Text(compositeDeniedTo, SWT.BORDER);

    Label lblDeniedMessage = new Label(container, SWT.NONE);
    lblDeniedMessage.setText(Strings.SESSIONS_DENIED_MESSAGE);

    txtDeniedMessage = new Text(container, SWT.BORDER | SWT.MULTI);
    GridData gdtxtDeniedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
    gdtxtDeniedMessage.heightHint = 63;
    txtDeniedMessage.setLayoutData(gdtxtDeniedMessage);

    Label lblPermissionCode = new Label(container, SWT.NONE);
    lblPermissionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPermissionCode.setText(Strings.SESSIONS_PERMISSION_CODE);

    txtPermissionCode = new Text(container, SWT.BORDER);
    txtPermissionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblDeniedParameter = new Label(container, SWT.NONE);
    lblDeniedParameter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDeniedParameter.setText(Strings.SESSIONS_DENIED_PARAMETER);

    txtDeniedParameter = new Text(container, SWT.BORDER);
    txtDeniedParameter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnSheduledJobsDenied = new Button(container, SWT.CHECK);
    btnSheduledJobsDenied.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnSheduledJobsDenied.setText(Strings.SHEDULED_JOBS_DENIED);

    Label lblExternalSessionManagerConnectionString = new Label(container, SWT.NONE);
    lblExternalSessionManagerConnectionString.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblExternalSessionManagerConnectionString.setText(Strings.EXTERNAL_SESSION_MANAGEMENT);

    txtExternalSessionManagerConnectionString = new Text(container, SWT.BORDER);
    txtExternalSessionManagerConnectionString.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnExternalSessionManagerRequired = new Button(container, SWT.CHECK);
    btnExternalSessionManagerRequired.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnExternalSessionManagerRequired.setText(Strings.REQUIRED_USE_OF_EXTERNAL_MANAGEMENT);

    Label lblSecurityProfile = new Label(container, SWT.NONE);
    lblSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSecurityProfile.setText(Strings.SECURITY_PROFILE);

    txtSecurityProfile = new Text(container, SWT.BORDER);
    txtSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblSafeModeSecurityProfile = new Label(container, SWT.NONE);
    lblSafeModeSecurityProfile.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSafeModeSecurityProfile.setText(Strings.SAFE_MODE_SECURITY_PROFILE);

    txtSafeModeSecurityProfile = new Text(container, SWT.BORDER);
    txtSafeModeSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    initProperties();

    return container;
  }

  private void initProperties() {
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
      txtServerDbName.setText(infoBaseInfo.getDbServerName());
      comboServerDbType.setText(infoBaseInfo.getDbms());
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
      txtExternalSessionManagerConnectionString.setText(
          infoBaseInfo.getExternalSessionManagerConnectionString());
      btnExternalSessionManagerRequired.setSelection(
          infoBaseInfo.getExternalSessionManagerRequired());

      // SecurityProfile properties
      txtSecurityProfile.setText(infoBaseInfo.getSecurityProfileName());
      txtSafeModeSecurityProfile.setText(infoBaseInfo.getSafeModeSecurityProfileName());
    } else {
      int a = 0;
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

    List<Text> checksDateControls = new ArrayList<>();
    checksDateControls.add(deniedFromDate);
    checksDateControls.add(deniedToDate);

    for (Text control : checksDateControls) {
      if (control.getText().isBlank()) {
        control.setBackground(Helper.getWhiteColor());
      } else {
        if (convertStringToDate(control.getText()).equals(emptyDate)) {
          control.setBackground(Helper.getPinkColor());
          existsError = true;
        }
      }
    }

    return existsError;
  }

  private boolean saveInfobaseProperties() {

    if (checkVariablesFromControls()) {
      return false;
    }

    IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infoBaseId);
    if (infoBaseInfo == null) {
      return false;
    }

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

    // Lock properties
    infoBaseInfo.setSessionsDenied(btnSessionsDenied.getSelection());
    infoBaseInfo.setDeniedFrom(convertStringToDate(deniedFromDate.getText()));
    infoBaseInfo.setDeniedTo(convertStringToDate(deniedToDate.getText()));

    infoBaseInfo.setDeniedMessage(txtDeniedMessage.getText());
    infoBaseInfo.setPermissionCode(txtPermissionCode.getText());
    infoBaseInfo.setDeniedParameter(txtDeniedParameter.getText());

    // ExternalSessionManager properties
    infoBaseInfo.setExternalSessionManagerConnectionString(
        txtExternalSessionManagerConnectionString.getText());
    infoBaseInfo.setExternalSessionManagerRequired(
        btnExternalSessionManagerRequired.getSelection());

    // SecurityProfile properties
    infoBaseInfo.setSecurityProfileName(txtSecurityProfile.getText());
    infoBaseInfo.setSafeModeSecurityProfileName(txtSafeModeSecurityProfile.getText());

    return server.updateInfoBase(clusterId, infoBaseInfo);
  }

  private Date convertStringToDate(String date) {

    if (date.isBlank()) {
      return emptyDate;
    }

    Date convertDate;
    
    try {
      convertDate = dateFormat.parse(date);
    } catch (ParseException excp) {
      excp.printStackTrace();
      convertDate = emptyDate;
    }

    return convertDate;
  }

  private String convertDateToString(Date date) {

    return date.equals(emptyDate) ? EMPTY_STRING : dateFormat.format(date);
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

    Button buttonApply = createButton(parent, IDialogConstants.PROCEED_ID, Strings.APPLY, false);
    buttonApply.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            saveInfobaseProperties();
          }
        });
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
    static final String SESSIONS_DENIED = getString("SessionsDenied");
    static final String SESSIONS_DENIED_FROM = getString("SessionsDeniedFrom");
    static final String SESSIONS_DENIED_TO = getString("SessionsDeniedTo");
    static final String SESSIONS_DENIED_MESSAGE = getString("SessionsDeniedMessage");
    static final String SESSIONS_PERMISSION_CODE = getString("SessionsPermissionCode");
    static final String SESSIONS_DENIED_PARAMETER = getString("SessionsDeniedParameter");
    static final String SHEDULED_JOBS_DENIED = getString("SheduledJobsDenied");
    static final String EXTERNAL_SESSION_MANAGEMENT = getString("ExternalSessionManagement");
    static final String REQUIRED_USE_OF_EXTERNAL_MANAGEMENT =
        getString("RequiredUseOfExternalManagement");
    static final String SECURITY_PROFILE = getString("SecurityProfile");
    static final String SAFE_MODE_SECURITY_PROFILE = getString("SafeModeSecurityProfile");

    static final String APPLY = Messages.getString("Dialogs.Apply");

    static String getString(String key) {
      return Messages.getString("InfobaseDialog." + key); //$NON-NLS-1$
    }
  }
}
