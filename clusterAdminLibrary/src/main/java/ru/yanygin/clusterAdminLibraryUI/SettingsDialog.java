package ru.yanygin.clusterAdminLibraryUI;

import java.util.Locale;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.ColumnProperties.RowSortDirection;
import ru.yanygin.clusterAdminLibrary.Config;

/** Диалог редактирования настроек сервера. */
public class SettingsDialog extends Dialog {

  private Config config;

  private Text txtHighlightDuration;

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
  private Button btnHighlightNewItems;
  private Button btnShadowSleepSessions;
  private Button btnReadClipboard;
  private Button btnCheckUpdate;
  private Button btnRowSortAsPrevious;
  private Button btnRowSortAsc;
  private Button btnRowSortDesc;
  private Button btnLoggerLevelOff;
  private Button btnLoggerLevelError;
  private Button btnLoggerLevelWarning;
  private Button btnLoggerLevelInfo;
  private Button btnLoggerLevelDebug;

  private static final String LOCALE_RU = "ru-RU"; //$NON-NLS-1$
  private Text txtIbasesFilePath;

  /**
   * Создание диалога настроек сервера.
   *
   * @param parentShell - parent shell
   */
  public SettingsDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.config = Config.currentConfig;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(Strings.TITLE_WINDOW);
  }
  
  /**
   * Create contents of the dialog.
   *
   * @param parent - parent control
   */
  @Override
  protected Control createDialogArea(Composite parent) {

    Composite container = (Composite) super.createDialogArea(parent);
    GridLayout gridLayout = (GridLayout) container.getLayout();
    gridLayout.numColumns = 3;

    Group grpShowNodesIn = new Group(container, SWT.NONE);
    grpShowNodesIn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpShowNodesIn.setText(Strings.SHOW_NODES_IN_TREE);
    grpShowNodesIn.setLayout(new GridLayout(1, false));

    btnShowWorkingServers = new Button(grpShowNodesIn, SWT.CHECK);
    btnShowWorkingServers.setText(Strings.SHOW_WORKING_SERVERS);

    btnShowWorkingProcesses = new Button(grpShowNodesIn, SWT.CHECK);
    btnShowWorkingProcesses.setText(Strings.SHOW_WORKING_PROCESSES);

    Group grpExpandNodes = new Group(container, SWT.NONE);
    grpExpandNodes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpExpandNodes.setText(Strings.EXPAND_NODES_IN_TREE);
    grpExpandNodes.setLayout(new GridLayout(1, false));

    btnExpandServers = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandServers.setText(Strings.EXPAND_SERVERS);

    btnExpandClusters = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandClusters.setText(Strings.EXPAND_CLUSTERS);

    btnExpandInfobases = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandInfobases.setText(Strings.EXPAND_INFOBASES);

    btnExpandWorkingServers = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandWorkingServers.setText(Strings.EXPAND_WORKING_SERVERS);

    btnExpandWorkingProcesses = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandWorkingProcesses.setText(Strings.EXPAND_WORKING_PROCESSES);

    Group grpShowInfo = new Group(container, SWT.NONE);
    grpShowInfo.setText(Strings.SHOW_INFO);
    grpShowInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpShowInfo.setLayout(new GridLayout(1, false));

    btnShowServerVersion = new Button(grpShowInfo, SWT.CHECK);
    btnShowServerVersion.setText(Strings.SHOW_SERVER_VERSION);

    btnShowServerDescription = new Button(grpShowInfo, SWT.CHECK);
    btnShowServerDescription.setText(Strings.SHOW_SERVER_DESCRIPTION);

    btnShowInfobaseDescription = new Button(grpShowInfo, SWT.CHECK);
    btnShowInfobaseDescription.setText(Strings.SHOW_INFOBASE_DESCRIPTION);

    btnShowLocalRasConnectInfo = new Button(grpShowInfo, SWT.CHECK);
    btnShowLocalRasConnectInfo.setText(Strings.SHOW_LOCAL_RAS_CONNECTINFO);

    Group grpLocale = new Group(container, SWT.NONE);
    grpLocale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpLocale.setText(Strings.LOCALE_TITLE);
    grpLocale.setLayout(new GridLayout(1, false));

    btnLocaleSystem = new Button(grpLocale, SWT.RADIO);
    btnLocaleSystem.setText(Strings.LOCALE_SYSTEM);

    btnLocaleEnglish = new Button(grpLocale, SWT.RADIO);
    btnLocaleEnglish.setText(Strings.LOCALE_ENGLISH);

    btnLocaleRussian = new Button(grpLocale, SWT.RADIO);
    btnLocaleRussian.setText(Strings.LOCALE_RUSSIAN);

    Group grpHighlight = new Group(container, SWT.NONE);
    grpHighlight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpHighlight.setText(Strings.HIGHLIGHT_TITLE);
    grpHighlight.setLayout(new GridLayout(2, false));

    btnHighlightNewItems = new Button(grpHighlight, SWT.CHECK);
    btnHighlightNewItems.setText(Strings.HIGHLIGHT_NEW_ITEMS);

    Label lblHighlightNewItemsColor = new Label(grpHighlight, SWT.BORDER);
    lblHighlightNewItemsColor.setBackground(new Color(0, 200, 0));
    GridData gdLblHighlightNewItemsColor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdLblHighlightNewItemsColor.heightHint = 14;
    gdLblHighlightNewItemsColor.widthHint = 14;
    lblHighlightNewItemsColor.setLayoutData(gdLblHighlightNewItemsColor);

    Label lblHighlightDuration = new Label(grpHighlight, SWT.NONE);
    lblHighlightDuration.setBounds(0, 0, 55, 15);
    lblHighlightDuration.setText(Strings.HIGHLIGHT_DURATION);

    txtHighlightDuration = new Text(grpHighlight, SWT.BORDER);
    GridData gdtxtHighlightDuration = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdtxtHighlightDuration.widthHint = 20;
    txtHighlightDuration.setLayoutData(gdtxtHighlightDuration);
    txtHighlightDuration.setBounds(0, 0, 76, 21);

    btnShadowSleepSessions = new Button(grpHighlight, SWT.CHECK);
    btnShadowSleepSessions.setText(Strings.SHADOW_SLEEP_SESSIONS);

    Label lblShadowSleepSessionsColor = new Label(grpHighlight, SWT.BORDER);
    lblShadowSleepSessionsColor.setBackground(new Color(160, 160, 160));
    GridData gdLblShadowSleepSessionsColor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdLblShadowSleepSessionsColor.heightHint = 14;
    gdLblShadowSleepSessionsColor.widthHint = 14;
    lblShadowSleepSessionsColor.setLayoutData(gdLblShadowSleepSessionsColor);

    Label lblWatchSessions = new Label(grpHighlight, SWT.NONE);
    lblWatchSessions.setText(Strings.WATCH_SESSIONS);

    Label lblWatchSessionsColor = new Label(grpHighlight, SWT.BORDER);
    lblWatchSessionsColor.setBackground(new Color(0, 128, 255));
    GridData gdLblWatchSessionsColor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdLblWatchSessionsColor.heightHint = 14;
    gdLblWatchSessionsColor.widthHint = 14;
    lblWatchSessionsColor.setLayoutData(gdLblWatchSessionsColor);

    Group grpRowSortDirection = new Group(container, SWT.NONE);
    grpRowSortDirection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpRowSortDirection.setText(Strings.ROW_SORT_DIRECTION_TITLE);
    grpRowSortDirection.setLayout(new GridLayout(1, false));

    btnRowSortAsPrevious = new Button(grpRowSortDirection, SWT.RADIO);
    btnRowSortAsPrevious.setText(Strings.ROW_SORT_DIRECTION_AS_PREVIOUS);

    btnRowSortAsc = new Button(grpRowSortDirection, SWT.RADIO);
    btnRowSortAsc.setText(Strings.ROW_SORT_DIRECTION_ASCENDING);

    btnRowSortDesc = new Button(grpRowSortDirection, SWT.RADIO);
    btnRowSortDesc.setText(Strings.ROW_SORT_DIRECTION_DESCENDING);

    Group grpLoggerLevel = new Group(container, SWT.NONE);
    grpLoggerLevel.setLayout(new GridLayout(1, false));
    grpLoggerLevel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpLoggerLevel.setText(Strings.LOGGER_LEVEL_TITLE);

    btnLoggerLevelOff = new Button(grpLoggerLevel, SWT.RADIO);
    btnLoggerLevelOff.setText("off");

    btnLoggerLevelError = new Button(grpLoggerLevel, SWT.RADIO);
    btnLoggerLevelError.setText("error");

    btnLoggerLevelWarning = new Button(grpLoggerLevel, SWT.RADIO);
    btnLoggerLevelWarning.setText("warning");

    btnLoggerLevelInfo = new Button(grpLoggerLevel, SWT.RADIO);
    btnLoggerLevelInfo.setText("info");

    btnLoggerLevelDebug = new Button(grpLoggerLevel, SWT.RADIO);
    btnLoggerLevelDebug.setText("debug");

    Group grpOther = new Group(container, SWT.NONE);
    grpOther.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpOther.setLayout(new GridLayout(1, false));

    btnReadClipboard = new Button(grpOther, SWT.CHECK);
    btnReadClipboard.setText(Strings.READ_CLIPBOARD);

    btnCheckUpdate = new Button(grpOther, SWT.CHECK);
    btnCheckUpdate.setText(Strings.CHECK_UPDATE);

    Label lblIbasesFilePath = new Label(grpOther, SWT.NONE);
    lblIbasesFilePath.setText(Strings.IBASES_PATH_TITLE);

    txtIbasesFilePath = new Text(grpOther, SWT.BORDER);
    txtIbasesFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    new Label(container, SWT.NONE);

    initProperties();

    return container;
  }

  private void initProperties() {

    btnShowWorkingServers.setSelection(config.isShowWorkingServersTree());
    btnShowWorkingProcesses.setSelection(config.isShowWorkingProcessesTree());

    btnExpandServers.setSelection(config.isExpandServersTree());
    btnExpandClusters.setSelection(config.isExpandClustersTree());
    btnExpandInfobases.setSelection(config.isExpandInfobasesTree());
    btnExpandWorkingServers.setSelection(config.isExpandWorkingServersTree());
    btnExpandWorkingProcesses.setSelection(config.isExpandWorkingProcessesTree());

    btnShowServerVersion.setSelection(config.isShowServerVersion());
    btnShowServerDescription.setSelection(config.isShowServerDescription());
    btnShowInfobaseDescription.setSelection(config.isShowInfobaseDescription());
    btnShowLocalRasConnectInfo.setSelection(config.isShowLocalRasConnectInfo());

    btnHighlightNewItems.setSelection(config.isHighlightNewItems());
    txtHighlightDuration.setText(Integer.toString(config.getHighlightNewItemsDuration()));
    btnShadowSleepSessions.setSelection(config.isShadeSleepingSessions());
    btnReadClipboard.setSelection(config.isReadClipboard());
    btnCheckUpdate.setSelection(config.checkingUpdate());

    final String locale = config.getLocale();
    if (locale == null) {
      btnLocaleSystem.setSelection(true);
    } else {
      btnLocaleEnglish.setSelection(locale.equals(Locale.ENGLISH.toLanguageTag()));
      btnLocaleRussian.setSelection(locale.equals(LOCALE_RU));
    }

    final RowSortDirection rowSortDirection = config.getRowSortDirection();
    btnRowSortAsPrevious.setSelection(rowSortDirection == RowSortDirection.DISABLE);
    btnRowSortAsc.setSelection(rowSortDirection == RowSortDirection.ASC);
    btnRowSortDesc.setSelection(rowSortDirection == RowSortDirection.DESC);

    final String loggerLevel = config.getLoggerLevel();
    btnLoggerLevelOff.setSelection(loggerLevel.equals(btnLoggerLevelOff.getText()));
    btnLoggerLevelError.setSelection(loggerLevel.equals(btnLoggerLevelError.getText()));
    btnLoggerLevelWarning.setSelection(loggerLevel.equals(btnLoggerLevelWarning.getText()));
    btnLoggerLevelInfo.setSelection(loggerLevel.equals(btnLoggerLevelInfo.getText()));
    btnLoggerLevelDebug.setSelection(loggerLevel.equals(btnLoggerLevelDebug.getText()));

    txtIbasesFilePath.setText(config.getIbasesStringPath());
  }

  private void saveProperties() {

    config.setShowWorkingServersTree(btnShowWorkingServers.getSelection());
    config.setShowWorkingProcessesTree(btnShowWorkingProcesses.getSelection());

    config.setExpandServersTree(btnExpandServers.getSelection());
    config.setExpandClustersTree(btnExpandClusters.getSelection());
    config.setExpandInfobasesTree(btnExpandInfobases.getSelection());
    config.setExpandWorkingServersTree(btnExpandWorkingServers.getSelection());
    config.setExpandWorkingProcessesTree(btnExpandWorkingProcesses.getSelection());

    config.setShowServerVersion(btnShowServerVersion.getSelection());
    config.setShowServerDescription(btnShowServerDescription.getSelection());
    config.setShowInfobaseDescription(btnShowInfobaseDescription.getSelection());
    config.setShowLocalRasConnectInfo(btnShowLocalRasConnectInfo.getSelection());

    config.setHighlightNewItems(btnHighlightNewItems.getSelection());
    config.setHighlightNewItemsDuration(Integer.parseInt(txtHighlightDuration.getText()));
    config.setShadowSleepSessions(btnShadowSleepSessions.getSelection());
    config.setReadClipboard(btnReadClipboard.getSelection());
    config.setCheckingUpdate(btnCheckUpdate.getSelection());

    if (btnLocaleSystem.getSelection()) {
      config.setLocale(null);
    } else if (btnLocaleEnglish.getSelection()) {
      config.setLocale(Locale.ENGLISH.toLanguageTag());
    } else if (btnLocaleRussian.getSelection()) {
      config.setLocale(LOCALE_RU);
    }

    if (btnRowSortAsPrevious.getSelection()) {
      config.setRowSortDirection(RowSortDirection.DISABLE);
    } else if (btnRowSortAsc.getSelection()) {
      config.setRowSortDirection(RowSortDirection.ASC);
    } else if (btnRowSortDesc.getSelection()) {
      config.setRowSortDirection(RowSortDirection.DESC);
    } else {
      config.setRowSortDirection(RowSortDirection.DISABLE);
    }

    String loggerLevel;
    if (btnLoggerLevelOff.getSelection()) {
      loggerLevel = btnLoggerLevelOff.getText();
    } else if (btnLoggerLevelError.getSelection()) {
      loggerLevel = btnLoggerLevelError.getText();
    } else if (btnLoggerLevelWarning.getSelection()) {
      loggerLevel = btnLoggerLevelWarning.getText();
    } else if (btnLoggerLevelInfo.getSelection()) {
      loggerLevel = btnLoggerLevelInfo.getText();
    } else if (btnLoggerLevelDebug.getSelection()) {
      loggerLevel = btnLoggerLevelDebug.getText();
    } else {
      loggerLevel = btnLoggerLevelOff.getText();
    }
    config.setLoggerLevel(loggerLevel);

    config.setIbasesPath(txtIbasesFilePath.getText());
  }

  /**
   * Create contents of the button bar.
   *
   * @param parent - parent composite
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    Button button =
        createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
    button.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            saveProperties();
            close();
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");

    static final String SHOW_NODES_IN_TREE = getString("ShowNodesInTree");
    static final String SHOW_WORKING_SERVERS = getString("ShowWorkingServers");
    static final String SHOW_WORKING_PROCESSES = getString("ShowWorkingProcesses");

    static final String EXPAND_NODES_IN_TREE = getString("ExpandNodesInTree");
    static final String EXPAND_SERVERS = getString("ExpandServers");
    static final String EXPAND_CLUSTERS = getString("ExpandClusters");
    static final String EXPAND_INFOBASES = getString("ExpandInfobases");
    static final String EXPAND_WORKING_SERVERS = getString("ExpandWorkingServers");
    static final String EXPAND_WORKING_PROCESSES = getString("ExpandWorkingProcesses");

    static final String SHOW_INFO = getString("ShowInfo");
    static final String SHOW_SERVER_VERSION = getString("ShowServerVersion");
    static final String SHOW_SERVER_DESCRIPTION = getString("ShowServerDescription");
    static final String SHOW_INFOBASE_DESCRIPTION = getString("ShowInfobaseDescription");
    static final String SHOW_LOCAL_RAS_CONNECTINFO = getString("ShowLocalRASConnectInfo");

    static final String LOCALE_TITLE = getString("LocaleTitle");
    static final String LOCALE_SYSTEM = getString("LocaleSystem");
    static final String LOCALE_ENGLISH = getString("LocaleEnglish");
    static final String LOCALE_RUSSIAN = getString("LocaleRussian");

    static final String HIGHLIGHT_TITLE = getString("HighlightTitle");
    static final String HIGHLIGHT_NEW_ITEMS = getString("HighlightNewItems");
    static final String HIGHLIGHT_DURATION = getString("HighlightDuration");
    static final String SHADOW_SLEEP_SESSIONS = getString("ShadowSleepSessions");
    static final String WATCH_SESSIONS = getString("WatchSessions");

    static final String ROW_SORT_DIRECTION_TITLE = getString("RowSortDirectionTitle");
    static final String ROW_SORT_DIRECTION_AS_PREVIOUS = getString("RowSortDirectionAsPrevious");
    static final String ROW_SORT_DIRECTION_ASCENDING = getString("RowSortDirectionAscending");
    static final String ROW_SORT_DIRECTION_DESCENDING = getString("RowSortDirectionDescending");

    static final String READ_CLIPBOARD = getString("ReadClipboard");
    static final String CHECK_UPDATE = getString("CheckUpdate");
    
    static final String LOGGER_LEVEL_TITLE = getString("LoggerLevelTitle");

    static final String IBASES_PATH_TITLE = getString("IbasesFilePathTitle");

    static String getString(String key) {
      return Messages.getString("SettingsDialog." + key); //$NON-NLS-1$
    }
  }
}
