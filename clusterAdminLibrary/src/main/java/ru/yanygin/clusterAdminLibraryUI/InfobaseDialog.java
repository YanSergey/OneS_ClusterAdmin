package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IInfoBaseInfo;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
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
import ru.yanygin.clusterAdminLibrary.Config;
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
  private final DateFormat dateDeniedFormat = new SimpleDateFormat("H:mm"); // $NON-NLS-1$
  private final Date emptyDate = new Date(0);
  private Font fontNormal;
  private Font fontMicro;

  private Server server;
  private UUID clusterId;
  private UUID infoBaseId;
  private boolean creationMode = false;
  private boolean deniedFriendlyEditMode = false;
  private boolean deniedFromIsEmpty = true;
  private boolean deniedToIsEmpty = true;

  private final Image tumblerOn = Helper.getImage("tumblerOn.png");
  private final Image tumblerOff = Helper.getImage("tumblerOff.png");

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

  private Combo comboSecurityLevel;
  private Combo comboServerDbType;

  private Button btnSessionsDenied;
  private Button btnSheduledJobsDenied;
  private Button btnAllowDistributeLicense;
  private Button btnExternalSessionManagerRequired;

  private Text txtDeniedMessage;
  private Text txtDeniedFromDate;
  private DateTime deniedFromDate;
  private DateTime deniedFromTime;
  private Text txtDeniedToDate;
  private Button btnDeniedSwitchMode;
  private Button btnDeniedFromClear;
  private DateTime deniedToDate;
  private DateTime deniedToTime;
  private Button btnDeniedToClear;
  private Combo comboDeniedFrom;
  private Combo comboDeniedTo;
  private Button btnPutDeniedMessage;

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
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

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
    compositeDeniedFrom.setLayout(null);

    txtDeniedFromDate = new Text(compositeDeniedFrom, SWT.BORDER);
    txtDeniedFromDate.setBounds(0, 1, 185, 21);
    txtDeniedFromDate.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            Date deniedFrom = convertStringToDate(txtDeniedFromDate.getText());
            setValueOnDateTimeFields(deniedFrom, deniedFromDate, deniedFromTime);
            comboDeniedFrom.select(deniedFromTime.getHours() * 4);
          }
        });

    deniedFromDate = new DateTime(compositeDeniedFrom, SWT.BORDER | SWT.DROP_DOWN);
    deniedFromDate.setBounds(0, 1, 91, 21);
    deniedFromDate.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            convertDeniedFromDateToClassicTextField();
          }
        });

    deniedFromTime = new DateTime(compositeDeniedFrom, SWT.BORDER | SWT.TIME);
    deniedFromTime.setBounds(96, 1, 70, 21);
    deniedFromTime.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            convertDeniedFromDateToClassicTextField();
          }
        });

    btnDeniedSwitchMode = new Button(compositeDeniedFrom, SWT.CENTER);
    btnDeniedSwitchMode.setToolTipText(Strings.SWITCH_DENIED_EDITING_MODE);
    btnDeniedSwitchMode.setBounds(215, 1, 28, 21);
    btnDeniedSwitchMode.setImage(tumblerOn);
    btnDeniedSwitchMode.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            deniedFriendlyEditMode = !deniedFriendlyEditMode;
            Config.currentConfig.setInfobaseDeniedFriendlyEditMode(deniedFriendlyEditMode);
            setDeniedFieldsState();
            setImageOnDeniedSwitchButton();
          }
        });

    btnDeniedFromClear = new Button(compositeDeniedFrom, SWT.NONE);
    btnDeniedFromClear.setText("x");
    btnDeniedFromClear.setBounds(190, 1, 21, 21);
    btnDeniedFromClear.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            deniedFromIsEmpty = true;
            setDeniedFieldsState();
            txtDeniedFromDate.setText("");
          }
        });

    comboDeniedFrom = new Combo(compositeDeniedFrom, SWT.READ_ONLY);
    comboDeniedFrom.setBounds(95, 0, 90, 21);
    comboDeniedFrom.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {

            int curTimeIndex = comboDeniedFrom.getSelectionIndex();
            int curHourIndex = curTimeIndex - (curTimeIndex % 4);
            String curHour = comboDeniedFrom.getItem(curHourIndex).substring(0, 2);

            deniedFromTime.setHours(Integer.parseInt(curHour));
            deniedFromTime.setMinutes(15 * (curTimeIndex % 4));
            deniedFromTime.setSeconds(0);

            convertDeniedFromDateToClassicTextField();
          }
        });

    Label lblDeniedTo = new Label(container, SWT.NONE);
    lblDeniedTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblDeniedTo.setText(Strings.SESSIONS_DENIED_TO);

    Composite compositeDeniedTo = new Composite(container, SWT.NONE);
    compositeDeniedTo.setLayout(null);
    compositeDeniedTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    txtDeniedToDate = new Text(compositeDeniedTo, SWT.BORDER);
    txtDeniedToDate.setBounds(0, 1, 185, 21);
    txtDeniedToDate.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            Date deniedTo = convertStringToDate(txtDeniedToDate.getText());
            setValueOnDateTimeFields(deniedTo, deniedToDate, deniedToTime);
            comboDeniedTo.select(deniedToTime.getHours() * 4);
          }
        });

    deniedToDate = new DateTime(compositeDeniedTo, SWT.BORDER | SWT.DROP_DOWN);
    deniedToDate.setBounds(0, 1, 91, 21);
    deniedToDate.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            convertDeniedToToClassicTextField();
          }
        });

    deniedToTime = new DateTime(compositeDeniedTo, SWT.BORDER | SWT.TIME);
    deniedToTime.setBounds(95, 1, 70, 21);
    deniedToTime.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            convertDeniedToToClassicTextField();
          }
        });

    comboDeniedTo = new Combo(compositeDeniedTo, SWT.READ_ONLY);
    comboDeniedTo.setBounds(95, 0, 90, 23);
    // comboDeniedTo.select(0);
    comboDeniedTo.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {

            int curTimeIndex = comboDeniedTo.getSelectionIndex();
            int curHourIndex = curTimeIndex - (curTimeIndex % 4);
            String curHour = comboDeniedTo.getItem(curHourIndex).substring(0, 2);

            deniedToTime.setHours(Integer.parseInt(curHour));
            deniedToTime.setMinutes(15 * (curTimeIndex % 4));
            deniedToTime.setSeconds(0);

            convertDeniedToToClassicTextField();
          }
        });

    btnDeniedToClear = new Button(compositeDeniedTo, SWT.NONE);
    btnDeniedToClear.setText("x");
    btnDeniedToClear.setBounds(190, 1, 21, 21);
    btnDeniedToClear.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            deniedToIsEmpty = true;
            setDeniedFieldsState();
            txtDeniedToDate.setText("");
          }
        });

    Label lblDeniedMessage = new Label(container, SWT.NONE);
    lblDeniedMessage.setText(Strings.SESSIONS_DENIED_MESSAGE);

    btnPutDeniedMessage = new Button(container, SWT.READ_ONLY);
    btnPutDeniedMessage.setText(Strings.PUT_SESSIONS_DENIED_MESSAGE);
    btnPutDeniedMessage.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {

            Date deniedFrom = getDeniedDate(deniedFromDate, deniedFromTime);
            Date deniedTo = getDeniedDate(deniedToDate, deniedToTime);

            String deniedMessage =
                String.format(
                    Config.currentConfig.getInfobaseDeniedMessagePattern(),
                    dateDeniedFormat.format(deniedFrom),
                    dateDeniedFormat.format(deniedTo));

            txtDeniedMessage.setText(deniedMessage);
          }
        });

    txtDeniedMessage = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI);
    GridData gdtxtDeniedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
    gdtxtDeniedMessage.widthHint = 200;
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

    FontData fontData = deniedFromDate.getFont().getFontData()[0];
    fontNormal =
        new Font(
            getParentShell().getDisplay(), fontData.getName(), fontData.getHeight(), SWT.NORMAL);
    fontMicro = new Font(getParentShell().getDisplay(), fontData.getName(), 1, SWT.NORMAL);

    for (int i = 0; i < 24; i++) {
      comboDeniedFrom.add(String.format("%02d:00:00", i));
      comboDeniedFrom.add("     15:00");
      comboDeniedFrom.add("     30:00");
      comboDeniedFrom.add("     45:00");

      comboDeniedTo.add(String.format("%02d:00:00", i));
      comboDeniedTo.add("     15:00");
      comboDeniedTo.add("     30:00");
      comboDeniedTo.add("     45:00");
    }
    comboDeniedFrom.select(0);
    comboDeniedTo.select(0);

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

      deniedFriendlyEditMode = Config.currentConfig.getInfobaseDeniedFriendlyEditMode();
      setImageOnDeniedSwitchButton();

      // Дата запрета входа Начальная
      Date deniedFrom = infoBaseInfo.getDeniedFrom();
      deniedFromIsEmpty = deniedFrom.equals(emptyDate);

      if (deniedFromIsEmpty) {
        deniedFromTime.setTime(deniedFromTime.getHours(), 0, 0);
      } else {
        txtDeniedFromDate.setText(convertDateToString(deniedFrom));
        setValueOnDateTimeFields(deniedFrom, deniedFromDate, deniedFromTime);
      }
      comboDeniedFrom.select(deniedFromTime.getHours() * 4);
      // Дата запрета входа начальная

      // Дата запрета входа Конечная
      Date deniedTo = infoBaseInfo.getDeniedTo();
      deniedToIsEmpty = deniedTo.equals(emptyDate);

      if (deniedToIsEmpty) {
        deniedToTime.setTime(deniedToTime.getHours() + 1, 0, 0);
      } else {
        txtDeniedToDate.setText(convertDateToString(deniedTo));
        setValueOnDateTimeFields(deniedTo, deniedToDate, deniedToTime);
      }
      comboDeniedTo.select(deniedToTime.getHours() * 4);
      // Дата запрета входа Конечная

      setDeniedFieldsState();

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
    checksDateControls.add(txtDeniedFromDate);
    checksDateControls.add(txtDeniedToDate);

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
    infoBaseInfo.setDeniedFrom(convertStringToDate(txtDeniedFromDate.getText()));
    infoBaseInfo.setDeniedTo(convertStringToDate(txtDeniedToDate.getText()));

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

  private void setValueOnDateTimeFields(Date date, DateTime dateField, DateTime timeField) {
    dateField.setDate(date.getYear() + 1900, date.getMonth(), date.getDate());
    timeField.setTime(date.getHours(), date.getMinutes(), date.getSeconds());
  }

  private void setDeniedFieldsState() {

    if (deniedFromIsEmpty) {
      deniedFromDate.setFont(fontMicro);
      deniedFromTime.setFont(fontMicro);
    } else {
      deniedFromDate.setFont(fontNormal);
      deniedFromTime.setFont(fontNormal);
    }

    if (deniedToIsEmpty) {
      deniedToDate.setFont(fontMicro);
      deniedToTime.setFont(fontMicro);
    } else {
      deniedToDate.setFont(fontNormal);
      deniedToTime.setFont(fontNormal);
    }

    deniedFromDate.setVisible(deniedFriendlyEditMode);
    deniedFromTime.setVisible(deniedFriendlyEditMode);
    comboDeniedFrom.setVisible(deniedFriendlyEditMode);
    txtDeniedFromDate.setVisible(!deniedFriendlyEditMode);

    deniedToDate.setVisible(deniedFriendlyEditMode);
    deniedToTime.setVisible(deniedFriendlyEditMode);
    comboDeniedTo.setVisible(deniedFriendlyEditMode);
    txtDeniedToDate.setVisible(!deniedFriendlyEditMode);
  }

  private void convertDeniedFromDateToClassicTextField() {

    deniedFromIsEmpty = false;
    setDeniedFieldsState();

    Date deniedDate = getDeniedDate(deniedFromDate, deniedFromTime);
    txtDeniedFromDate.setText(Helper.dateToStringReverse(deniedDate));
  }

  private void convertDeniedToToClassicTextField() {

    deniedToIsEmpty = false;
    setDeniedFieldsState();

    Date deniedDate = getDeniedDate(deniedToDate, deniedToTime);
    txtDeniedToDate.setText(Helper.dateToStringReverse(deniedDate));
  }

  private Date getDeniedDate(DateTime date, DateTime time) {
    Calendar calendar =
        new GregorianCalendar(
            date.getYear(),
            date.getMonth(),
            date.getDay(),
            time.getHours(),
            time.getMinutes(),
            time.getSeconds());
    return calendar.getTime();
  }

  private void setImageOnDeniedSwitchButton() {
    btnDeniedSwitchMode.setImage(deniedFriendlyEditMode ? tumblerOn : tumblerOff);
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
    static final String PUT_SESSIONS_DENIED_MESSAGE = getString("PutSessionsDeniedMessage");
    static final String SWITCH_DENIED_EDITING_MODE = getString("SwitchDeniedEditingMode");
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
