package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Server;

/** Dialog for create and edit Server parameters. */
public class CreateEditServerDialog extends Dialog {

  private Server serverParams;
  private boolean rasOnSameHost;

  private Text txtRasHost;
  private Text txtRasPort;

  private Text txtAgentHost;
  private Text txtAgentPort;
  private Text txtLocalRasPort;
  private Combo comboV8Version;

  private Button btnAutoconnect;
  private Text txtAgentUser;
  private Text txtAgentPasswors;
  private Table tableCredentials;
  private Button radioUseRemoteRas;
  private Button radioUseLocalRas;
  private Button btnSaveCredentials;
  private Text txtDescription;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param serverParams - server params
   */
  public CreateEditServerDialog(Shell parentShell, Server serverParams) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM);

    // найти способ установить заголовок окна
    // parentShell.setText("Parameters of the central server 1C:Enterprise");

    this.serverParams = serverParams;
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
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;

    TabFolder tabFolder = new TabFolder(container, SWT.NONE);

    TabItem tabConnect = new TabItem(tabFolder, SWT.NONE);
    tabConnect.setText(Messages.getString("ServerDialog.ConnectParameters")); //$NON-NLS-1$

    Composite connectContainer = new Composite(tabFolder, SWT.NONE);
    tabConnect.setControl(connectContainer);
    GridLayout glconnectContainer = new GridLayout(2, false);
    connectContainer.setLayout(glconnectContainer);

    Composite composite = new Composite(connectContainer, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

    Label lblDescription = new Label(composite, SWT.NONE);
    lblDescription.setText(Messages.getString("ServerDialog.Description")); //$NON-NLS-1$

    txtDescription = new Text(composite, SWT.BORDER);
    txtDescription.setToolTipText(Messages.getString("ServerDialog.Description")); //$NON-NLS-1$
    txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnAutoconnect = new Button(connectContainer, SWT.CHECK);
    GridData gdbtnAutoconnect = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdbtnAutoconnect.horizontalIndent = 5;
    btnAutoconnect.setLayoutData(gdbtnAutoconnect);
    btnAutoconnect.setText(Messages.getString("ServerDialog.AutoconnectAtStartup")); //$NON-NLS-1$
    new Label(connectContainer, SWT.NONE);

    radioUseRemoteRas = new Button(connectContainer, SWT.RADIO);
    GridData gdradioUseRemoteRas = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdradioUseRemoteRas.horizontalIndent = 5;
    radioUseRemoteRas.setLayoutData(gdradioUseRemoteRas);
    radioUseRemoteRas.setSelection(true);
    radioUseRemoteRas.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            setEnabledRasGroupParameters();
          }
        });
    radioUseRemoteRas.setBounds(0, 0, 90, 16);
    radioUseRemoteRas.setText(Messages.getString("ServerDialog.UseRemoteRAS")); //$NON-NLS-1$

    radioUseLocalRas = new Button(connectContainer, SWT.RADIO);
    GridData gdradioUseLocalRas = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdradioUseLocalRas.horizontalIndent = 5;
    radioUseLocalRas.setLayoutData(gdradioUseLocalRas);
    radioUseLocalRas.setBounds(0, 0, 90, 16);
    radioUseLocalRas.setText(Messages.getString("ServerDialog.UseLocalRAS")); //$NON-NLS-1$

    Group grpRemoteRasParameters = new Group(connectContainer, SWT.NONE);
    grpRemoteRasParameters.setText(
        Messages.getString("ServerDialog.RemoteRASParameters")); //$NON-NLS-1$
    grpRemoteRasParameters.setLayout(new GridLayout(2, false));

    Label lblRasHost = new Label(grpRemoteRasParameters, SWT.NONE);
    lblRasHost.setText(Messages.getString("ServerDialog.Host")); //$NON-NLS-1$

    Label lblRasPort = new Label(grpRemoteRasParameters, SWT.NONE);
    lblRasPort.setSize(46, 15);
    lblRasPort.setText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$

    txtRasHost = new Text(grpRemoteRasParameters, SWT.BORDER);
    txtRasHost.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            if (rasOnSameHost) {
              txtAgentHost.setText(((Text) e.widget).getText());
            }
            checkRasOnSameHost();
          }
        });
    GridData gdtxtRasHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdtxtRasHost.widthHint = 200;
    txtRasHost.setLayoutData(gdtxtRasHost);
    txtRasHost.setToolTipText(Messages.getString("ServerDialog.Host")); //$NON-NLS-1$

    txtRasPort = new Text(grpRemoteRasParameters, SWT.BORDER);
    GridData gdtxtRasPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtRasPort.widthHint = 50;
    txtRasPort.setLayoutData(gdtxtRasPort);
    txtRasPort.setToolTipText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$

    Group grpLocalRasParameters = new Group(connectContainer, SWT.NONE);
    grpLocalRasParameters.setSize(417, 90);
    grpLocalRasParameters.setText(
        Messages.getString("ServerDialog.LocalRASParameters")); //$NON-NLS-1$
    grpLocalRasParameters.setLayout(new GridLayout(2, false));

    Label lblV8Version = new Label(grpLocalRasParameters, SWT.NONE);
    lblV8Version.setSize(124, 15);
    lblV8Version.setText(Messages.getString("ServerDialog.V8Version")); //$NON-NLS-1$

    Label lblLocalRasPort = new Label(grpLocalRasParameters, SWT.NONE);
    lblLocalRasPort.setSize(77, 15);
    lblLocalRasPort.setText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$

    comboV8Version = new Combo(grpLocalRasParameters, SWT.READ_ONLY);
    GridData gdcomboV8Version = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdcomboV8Version.widthHint = 140;
    comboV8Version.setLayoutData(gdcomboV8Version);
    comboV8Version.setSize(389, 21);
    comboV8Version.setToolTipText(Messages.getString("ServerDialog.V8Version")); //$NON-NLS-1$

    txtLocalRasPort = new Text(grpLocalRasParameters, SWT.BORDER);
    GridData gdtxtLocalRasPort = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
    gdtxtLocalRasPort.widthHint = 50;
    txtLocalRasPort.setLayoutData(gdtxtLocalRasPort);
    txtLocalRasPort.setToolTipText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$

    Group grpRagentParameters = new Group(connectContainer, SWT.NONE);
    grpRagentParameters.setText(Messages.getString("ServerDialog.AgentParameters")); //$NON-NLS-1$
    grpRagentParameters.setLayout(new GridLayout(2, false));

    Label lblAgentHost = new Label(grpRagentParameters, SWT.NONE);
    lblAgentHost.setText(Messages.getString("ServerDialog.Host")); //$NON-NLS-1$

    Label lblAgentPort = new Label(grpRagentParameters, SWT.NONE);
    lblAgentPort.setText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$

    txtAgentHost = new Text(grpRagentParameters, SWT.BORDER);
    txtAgentHost.addModifyListener(
        new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent e) {
            checkRasOnSameHost();
          }
        });
    GridData gdtxtAgentHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdtxtAgentHost.widthHint = 200;
    txtAgentHost.setLayoutData(gdtxtAgentHost);
    txtAgentHost.setToolTipText(Messages.getString("ServerDialog.Host")); //$NON-NLS-1$

    txtAgentPort = new Text(grpRagentParameters, SWT.BORDER);
    GridData gdtxtAgentPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtAgentPort.widthHint = 50;
    txtAgentPort.setLayoutData(gdtxtAgentPort);
    txtAgentPort.setToolTipText(Messages.getString("ServerDialog.Port")); //$NON-NLS-1$
    new Label(connectContainer, SWT.NONE);

    TabItem tabCredentials = new TabItem(tabFolder, SWT.NONE);
    tabCredentials.setText(Messages.getString("ServerDialog.Credentials")); //$NON-NLS-1$

    Composite credentialsContainer = new Composite(tabFolder, SWT.NONE);
    tabCredentials.setControl(credentialsContainer);
    credentialsContainer.setLayout(new GridLayout(1, false));

    btnSaveCredentials = new Button(credentialsContainer, SWT.CHECK);
    btnSaveCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnSaveCredentials.setSize(155, 16);
    btnSaveCredentials.setText(Messages.getString("ServerDialog.SaveCredentials")); //$NON-NLS-1$

    Group grpCentralServerCredential = new Group(credentialsContainer, SWT.NONE);
    grpCentralServerCredential.setText(
        Messages.getString("ServerDialog.CentralServerAdminstrator")); //$NON-NLS-1$
    grpCentralServerCredential.setLayout(new GridLayout(4, false));
    GridData gdgrpCentralServerCredential = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
    gdgrpCentralServerCredential.verticalIndent = 5;
    grpCentralServerCredential.setLayoutData(gdgrpCentralServerCredential);

    Label lblAgentUser = new Label(grpCentralServerCredential, SWT.NONE);
    lblAgentUser.setSize(23, 15);
    lblAgentUser.setText(Messages.getString("ServerDialog.User")); //$NON-NLS-1$

    txtAgentUser = new Text(grpCentralServerCredential, SWT.BORDER);
    GridData gdtxtAgentUser = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtAgentUser.widthHint = 200;
    txtAgentUser.setLayoutData(gdtxtAgentUser);
    txtAgentUser.setSize(76, 21);
    txtAgentUser.setToolTipText(Messages.getString("ServerDialog.User")); //$NON-NLS-1$

    Label lblAgentPwd = new Label(grpCentralServerCredential, SWT.NONE);
    lblAgentPwd.setSize(50, 15);
    lblAgentPwd.setText(Messages.getString("ServerDialog.Password")); //$NON-NLS-1$

    txtAgentPasswors = new Text(grpCentralServerCredential, SWT.BORDER | SWT.PASSWORD);
    GridData gdtxtAgentPasswors = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtAgentPasswors.widthHint = 100;
    txtAgentPasswors.setLayoutData(gdtxtAgentPasswors);
    txtAgentPasswors.setSize(76, 21);
    txtAgentPasswors.setToolTipText(Messages.getString("ServerDialog.Password")); //$NON-NLS-1$

    tableCredentials = new Table(credentialsContainer, SWT.BORDER | SWT.FULL_SELECTION);
    GridData gdtableCredentials = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
    gdtableCredentials.heightHint = 183;
    gdtableCredentials.verticalIndent = 5;
    tableCredentials.setLayoutData(gdtableCredentials);
    tableCredentials.setHeaderVisible(true);
    tableCredentials.setLinesVisible(true);

    TableColumn tblclmnType = new TableColumn(tableCredentials, SWT.NONE);
    tblclmnType.setWidth(60);
    tblclmnType.setText(Messages.getString("ServerDialog.Type")); //$NON-NLS-1$

    TableColumn tblclmnName = new TableColumn(tableCredentials, SWT.NONE);
    tblclmnName.setWidth(160);
    tblclmnName.setText(Messages.getString("ServerDialog.Name")); //$NON-NLS-1$

    TableColumn tblclmnId = new TableColumn(tableCredentials, SWT.NONE);
    tblclmnId.setWidth(100);
    tblclmnId.setText(Messages.getString("ServerDialog.ID")); //$NON-NLS-1$

    TableColumn tblclmnUsername = new TableColumn(tableCredentials, SWT.NONE);
    tblclmnUsername.setWidth(100);
    tblclmnUsername.setText(Messages.getString("ServerDialog.Username")); //$NON-NLS-1$

    TableColumn tblclmnPassword = new TableColumn(tableCredentials, SWT.NONE);
    tblclmnPassword.setWidth(100);
    tblclmnPassword.setText(Messages.getString("ServerDialog.Password")); //$NON-NLS-1$
    new Label(container, SWT.NONE);

    initServerProperties();
    checkRasOnSameHost();
    setEnabledRasGroupParameters();

    return container;
  }

  private void initServerProperties() {
    if (serverParams != null) {
      this.txtDescription.setText(serverParams.getDescription());

      this.txtRasHost.setText(serverParams.getRasHost());
      this.txtRasPort.setText(serverParams.getRasPortAsString());

      this.txtAgentHost.setText(serverParams.getAgentHost());
      this.txtAgentPort.setText(serverParams.getAgentPortAsString());

      this.radioUseRemoteRas.setSelection(!serverParams.getUseLocalRas());
      this.radioUseLocalRas.setSelection(serverParams.getUseLocalRas());
      List<String> installedV8Versions = new ArrayList<>();
      ClusterProvider.getInstalledV8Versions()
          .forEach(
              (desc, path) -> {
                installedV8Versions.add(desc);
              });
      installedV8Versions.sort(String.CASE_INSENSITIVE_ORDER);
      comboV8Version.setItems(installedV8Versions.toArray(new String[0]));

      this.comboV8Version.setText(serverParams.getLocalRasV8version());

      this.txtLocalRasPort.setText(serverParams.getLocalRasPortAsString());

      this.btnAutoconnect.setSelection(serverParams.getAutoconnect());
      this.btnSaveCredentials.setSelection(serverParams.getSaveCredentials());
      this.txtAgentUser.setText(serverParams.getAgentUserName());
      this.txtAgentPasswors.setText(serverParams.getAgentPassword());

      serverParams.getCredentials().forEach(
          (uuid, userPass) -> {
            TableItem credentialItem = new TableItem(this.tableCredentials, SWT.NONE);

            String[] itemText = {
              "cluster", //$NON-NLS-1$
              userPass[2], // clusterName
              uuid.toString(),
              userPass[0], // username
              userPass[1] // pass
            };

            credentialItem.setText(itemText);
            credentialItem.setData("UUID", uuid); //$NON-NLS-1$
            credentialItem.setChecked(false);
          });
    }
  }

  private boolean saveNewServerProperties() {
    try {
      serverParams.setDescription(txtDescription.getText());

      Map<UUID, String[]> credentialsClustersCashe = new HashMap<>();
      if (btnSaveCredentials.getSelection()) {
        TableItem[] credentials = tableCredentials.getItems();
        for (TableItem credential : credentials) {
          UUID uuid = (UUID) credential.getData("UUID"); //$NON-NLS-1$
          credentialsClustersCashe.put(
              uuid,
              new String[] {credential.getText(3), credential.getText(4), credential.getText(1)});
        }
      }

      serverParams.setAgentHost(txtAgentHost.getText());
      serverParams.setAgentPort(Integer.parseInt(txtAgentPort.getText()));
      serverParams.setRasHost(txtRasHost.getText());
      serverParams.setRasPort(Integer.parseInt(txtRasPort.getText()));
      serverParams.setUseLocalRas(!radioUseRemoteRas.getSelection());
      serverParams.setLocalRasPort(Integer.parseInt(txtLocalRasPort.getText()));

      serverParams.setLocalRasV8version(comboV8Version.getText());
      serverParams.setAutoconnect(btnAutoconnect.getSelection());
      serverParams.setSaveCredentials(btnSaveCredentials.getSelection());
      serverParams.setAgentUserName(txtAgentUser.getText());
      serverParams.setAgentPassword(txtAgentPasswors.getText());
      serverParams.setCredentials(credentialsClustersCashe);

      //      LOGGER.info("Set new properties for server <{}>", serverParams.getServerKey()); //
      //$NON-NLS-1$

      return true;

    } catch (Exception excp) {
      var messageBox = new MessageBox(getParentShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
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
            if (saveNewServerProperties()) {
              close();
            }
          }
        });
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private void checkRasOnSameHost() {
    rasOnSameHost = txtAgentHost.getText().equals(txtRasHost.getText());
  }

  private void setEnabledRasGroupParameters() {
    txtRasHost.setEnabled(radioUseRemoteRas.getSelection());
    txtRasPort.setEnabled(radioUseRemoteRas.getSelection());
    comboV8Version.setEnabled(!radioUseRemoteRas.getSelection());
    txtLocalRasPort.setEnabled(!radioUseRemoteRas.getSelection());
  }
}
