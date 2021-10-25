package ru.yanygin.clusterAdminLibraryUI;

import java.util.Locale;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config;

/** Dialog for edit server settings. */
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
  private Text txtHighlightDuration;
  private Button btnHighlightNewItems;
  private Button btnShadowSleepSessions;
  private Button btnReadClipboard;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   */
  public SettingsDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    // super.configureShell(parentShell);
    // parentShell.setText("Parameters of the 1C:Enterprise infobase");

    this.config = ClusterProvider.getCommonConfig();
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
    grpShowNodesIn.setText(Messages.getString("SettingsDialog.ShowNodesInTree")); //$NON-NLS-1$
    grpShowNodesIn.setLayout(new GridLayout(1, false));

    btnShowWorkingServers = new Button(grpShowNodesIn, SWT.CHECK);
    btnShowWorkingServers.setText(
        Messages.getString("SettingsDialog.ShowWorkingServers")); //$NON-NLS-1$

    btnShowWorkingProcesses = new Button(grpShowNodesIn, SWT.CHECK);
    btnShowWorkingProcesses.setText(
        Messages.getString("SettingsDialog.ShowWorkingProcesses")); //$NON-NLS-1$

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
    btnExpandWorkingServers.setText(
        Messages.getString("SettingsDialog.ExpandWorkingServers")); //$NON-NLS-1$

    btnExpandWorkingProcesses = new Button(grpExpandNodes, SWT.CHECK);
    btnExpandWorkingProcesses.setText(
        Messages.getString("SettingsDialog.ExpandWorkingProcesses")); //$NON-NLS-1$

    Group grpShowInfo = new Group(container, SWT.NONE);
    grpShowInfo.setText(Messages.getString("SettingsDialog.ShowInfo")); //$NON-NLS-1$
    grpShowInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
    grpShowInfo.setLayout(new GridLayout(1, false));

    btnShowServerVersion = new Button(grpShowInfo, SWT.CHECK);
    btnShowServerVersion.setText(
        Messages.getString("SettingsDialog.ShowServerVersion")); //$NON-NLS-1$

    btnShowServerDescription = new Button(grpShowInfo, SWT.CHECK);
    btnShowServerDescription.setText(
        Messages.getString("SettingsDialog.ShowServerDescription")); //$NON-NLS-1$

    btnShowInfobaseDescription = new Button(grpShowInfo, SWT.CHECK);
    btnShowInfobaseDescription.setText(
        Messages.getString("SettingsDialog.ShowInfobaseDescription")); //$NON-NLS-1$

    btnShowLocalRasConnectInfo = new Button(grpShowInfo, SWT.CHECK);
    btnShowLocalRasConnectInfo.setText(
        Messages.getString("SettingsDialog.ShowLocalRASConnectInfo"));

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

    Group grpHighlight = new Group(container, SWT.NONE);
    grpHighlight.setText(Messages.getString("SettingsDialog.Highlight")); //$NON-NLS-1$
    grpHighlight.setLayout(new GridLayout(2, false));

    btnHighlightNewItems = new Button(grpHighlight, SWT.CHECK);
    btnHighlightNewItems.setText(
        Messages.getString("SettingsDialog.HighlightNewItems")); //$NON-NLS-1$
    new Label(grpHighlight, SWT.NONE);

    Label lblHighlightDuration = new Label(grpHighlight, SWT.NONE);
    lblHighlightDuration.setBounds(0, 0, 55, 15);
    lblHighlightDuration.setText(Messages.getString("SettingsDialog.HighlightDuration"));

    txtHighlightDuration = new Text(grpHighlight, SWT.BORDER);
    GridData gdtxtHighlightDuration = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdtxtHighlightDuration.widthHint = 20;
    txtHighlightDuration.setLayoutData(gdtxtHighlightDuration);
    txtHighlightDuration.setBounds(0, 0, 76, 21);

    btnShadowSleepSessions = new Button(grpHighlight, SWT.CHECK);
    btnShadowSleepSessions.setText(
        Messages.getString("SettingsDialog.ShadowSleepSessions")); //$NON-NLS-1$
    new Label(grpHighlight, SWT.NONE);

    btnReadClipboard = new Button(container, SWT.CHECK);
    btnReadClipboard.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    btnReadClipboard.setText(Messages.getString("SettingsDialog.ReadClipboard"));

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
    btnShadowSleepSessions.setSelection(config.isShadowSleepSessions());
    btnReadClipboard.setSelection(config.isReadClipboard());

    if (config.getLocale() == null) {
      btnLocaleSystem.setSelection(true);
    } else {
      btnLocaleEnglish.setSelection(config.getLocale().equals(Locale.ENGLISH.toLanguageTag()));
      btnLocaleRussian.setSelection(config.getLocale().equals("ru-RU")); //$NON-NLS-1$
    }
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

    if (btnLocaleSystem.getSelection()) {
      config.setLocale(null);
    } else if (btnLocaleEnglish.getSelection()) {
      config.setLocale(Locale.ENGLISH.toLanguageTag());
    } else if (btnLocaleRussian.getSelection()) {
      config.setLocale("ru-RU"); //$NON-NLS-1$
    }
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
}
