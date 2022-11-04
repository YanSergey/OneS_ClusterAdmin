package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;
import ru.yanygin.clusterAdminLibrary.Server.SaveCredentialsVariant;
import ru.yanygin.clusterAdminLibrary.UserPassPair;

/** Диалог редактирования параметров сервера. */
public class ServerDialog extends Dialog {

  private static final String UUID_DATA_KEY = "UUID"; //$NON-NLS-1$
  private static final String USER_PASS_DATA_KEY = "UserPass"; //$NON-NLS-1$
  private static final String AGENT_CREDENTIAL_PATTERN = "%s: %s %s: %s"; //$NON-NLS-1$

  private final Image hiddenPassImage = Helper.getImage("hiddenPass.png"); //$NON-NLS-1$
  private final Image visiblePassImage = Helper.getImage("visiblePass.png"); //$NON-NLS-1$

  private Server server;
  private SaveCredentialsVariant saveCredentialsVariant;
  private UserPassPair agentCredentialTemp;
  private boolean rasOnSameHost;
  private boolean showPasswordMode = false;

  // Controls
  private Text txtDescription;
  private Text txtRasHost;
  private Text txtRasPort;
  private Text txtAgentHost;
  private Text txtAgentPort;
  private Text txtLocalRasPort;
  
  private Combo comboV8Version;

  private Link txtAgentCredential;
  
  private Table tableClusterCredentials;
  private Table tableInfobasesCredentials;

  private Button btnAutoconnect;
  private Button radioUseRemoteRas;
  private Button radioUseLocalRas;
  private Button btnSaveCredentialsDisable;
  private Button btnSaveCredentialsName;
  private Button btnSaveCredentialsNamePass;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server params
   */
  public ServerDialog(Shell parentShell, Server server) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM);

    this.server = server;
    this.agentCredentialTemp = server.getAgentCredential();
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
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;

    TabFolder tabFolder = new TabFolder(container, SWT.NONE);

    TabItem tabConnect = new TabItem(tabFolder, SWT.NONE);
    tabConnect.setText(Strings.CONNECT_PARAMETERS);

    Composite connectContainer = new Composite(tabFolder, SWT.NONE);
    tabConnect.setControl(connectContainer);
    GridLayout glConnectContainer = new GridLayout(2, false);
    connectContainer.setLayout(glConnectContainer);

    Composite composite = new Composite(connectContainer, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

    Label lblDescription = new Label(composite, SWT.NONE);
    lblDescription.setText(Strings.SERVER_DESCRIPTION);

    txtDescription = new Text(composite, SWT.BORDER);
    txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnAutoconnect = new Button(connectContainer, SWT.CHECK);
    GridData gdbtnAutoconnect = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdbtnAutoconnect.horizontalIndent = 5;
    btnAutoconnect.setLayoutData(gdbtnAutoconnect);
    btnAutoconnect.setText(Strings.AUTOCONNECT_AT_STARTUP);
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
    radioUseRemoteRas.setText(Strings.USE_REMOTE_RAS);

    radioUseLocalRas = new Button(connectContainer, SWT.RADIO);
    GridData gdradioUseLocalRas = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdradioUseLocalRas.horizontalIndent = 5;
    radioUseLocalRas.setLayoutData(gdradioUseLocalRas);
    radioUseLocalRas.setBounds(0, 0, 90, 16);
    radioUseLocalRas.setText(Strings.USE_LOCAL_RAS);

    Group grpRemoteRasParameters = new Group(connectContainer, SWT.NONE);
    grpRemoteRasParameters.setText(Strings.REMOTE_RAS_PARAMETERS);
    grpRemoteRasParameters.setLayout(new GridLayout(2, false));

    Label lblRasHost = new Label(grpRemoteRasParameters, SWT.NONE);
    lblRasHost.setText(Strings.HOST);

    Label lblRasPort = new Label(grpRemoteRasParameters, SWT.NONE);
    lblRasPort.setSize(46, 15);
    lblRasPort.setText(Strings.PORT);

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

    txtRasPort = new Text(grpRemoteRasParameters, SWT.BORDER);
    GridData gdtxtRasPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtRasPort.widthHint = 50;
    txtRasPort.setLayoutData(gdtxtRasPort);

    Group grpLocalRasParameters = new Group(connectContainer, SWT.NONE);
    grpLocalRasParameters.setSize(417, 90);
    grpLocalRasParameters.setText(Strings.LOCAL_RAS_PARAMETERS);
    grpLocalRasParameters.setLayout(new GridLayout(2, false));

    Label lblV8Version = new Label(grpLocalRasParameters, SWT.NONE);
    lblV8Version.setSize(124, 15);
    lblV8Version.setText(Strings.V8_VERSION);

    Label lblLocalRasPort = new Label(grpLocalRasParameters, SWT.NONE);
    lblLocalRasPort.setSize(77, 15);
    lblLocalRasPort.setText(Strings.PORT);

    comboV8Version = new Combo(grpLocalRasParameters, SWT.READ_ONLY);
    GridData gdcomboV8Version = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gdcomboV8Version.widthHint = 140;
    comboV8Version.setLayoutData(gdcomboV8Version);
    comboV8Version.setSize(389, 21);

    txtLocalRasPort = new Text(grpLocalRasParameters, SWT.BORDER);
    GridData gdtxtLocalRasPort = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
    gdtxtLocalRasPort.widthHint = 50;
    txtLocalRasPort.setLayoutData(gdtxtLocalRasPort);

    Group grpRagentParameters = new Group(connectContainer, SWT.NONE);
    grpRagentParameters.setText(Strings.AGENT_PARAMETERS);
    grpRagentParameters.setLayout(new GridLayout(2, false));

    Label lblAgentHost = new Label(grpRagentParameters, SWT.NONE);
    lblAgentHost.setText(Strings.HOST);

    Label lblAgentPort = new Label(grpRagentParameters, SWT.NONE);
    lblAgentPort.setText(Strings.PORT);

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

    txtAgentPort = new Text(grpRagentParameters, SWT.BORDER);
    GridData gdtxtAgentPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdtxtAgentPort.widthHint = 50;
    txtAgentPort.setLayoutData(gdtxtAgentPort);
    new Label(connectContainer, SWT.NONE);

    TabItem tabCredentials = new TabItem(tabFolder, SWT.NONE);
    tabCredentials.setText(Strings.CREDENTIALS);

    Composite credentialsContainer = new Composite(tabFolder, SWT.NONE);
    tabCredentials.setControl(credentialsContainer);
    credentialsContainer.setLayout(new GridLayout(1, false));

    Group grpSaveCredentialsVariant = new Group(credentialsContainer, SWT.NONE);
    grpSaveCredentialsVariant.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    grpSaveCredentialsVariant.setText(Strings.SAVE_CREDENTIALS_VARIANT);
    grpSaveCredentialsVariant.setLayout(new GridLayout(4, false));

    btnSaveCredentialsDisable = new Button(grpSaveCredentialsVariant, SWT.RADIO);
    btnSaveCredentialsDisable.setSize(549, 16);
    btnSaveCredentialsDisable.setText(Strings.SAVE_CREDENTIALS_NONE);

    btnSaveCredentialsName = new Button(grpSaveCredentialsVariant, SWT.RADIO);
    btnSaveCredentialsName.setSize(549, 16);
    btnSaveCredentialsName.setText(Strings.SAVE_CREDENTIALS_NAME);

    btnSaveCredentialsNamePass = new Button(grpSaveCredentialsVariant, SWT.RADIO);
    btnSaveCredentialsNamePass.setSize(549, 16);
    btnSaveCredentialsNamePass.setText(Strings.SAVE_CREDENTIALS_NAMEPASS);

    Button btnShowPasswordMode = new Button(grpSaveCredentialsVariant, SWT.TOGGLE | SWT.CENTER);
    btnShowPasswordMode.setToolTipText(Strings.SHOW_PASSWORD_MODE_TOOLTIP);
    btnShowPasswordMode.setImage(hiddenPassImage);
    btnShowPasswordMode.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            showPasswordMode = !showPasswordMode;
            btnShowPasswordMode.setImage(showPasswordMode ? visiblePassImage : hiddenPassImage);
            fillCredentials();
          }
        });

    Group grpCentralServerCredential = new Group(credentialsContainer, SWT.NONE);
    grpCentralServerCredential.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    grpCentralServerCredential.setText(Strings.CENTRAL_SERVER_ADMINISTRATOR);
    grpCentralServerCredential.setLayout(null);

    txtAgentCredential = new Link(grpCentralServerCredential, SWT.NONE);
    txtAgentCredential.setBounds(8, 20, 0, 15);
    txtAgentCredential.addSelectionListener(agentCredentialClickListener);

    Group grpClustersCredentials = new Group(credentialsContainer, SWT.NONE);
    grpClustersCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    grpClustersCredentials.setText(Strings.CLUSTERS_CREDENTIALS_GROUP);
    grpClustersCredentials.setLayout(new GridLayout(1, false));

    tableClusterCredentials =
        new Table(grpClustersCredentials, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    GridData gdTableClusterCredentials = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdTableClusterCredentials.heightHint = 60;
    tableClusterCredentials.setLayoutData(gdTableClusterCredentials);
    tableClusterCredentials.setHeaderVisible(true);
    tableClusterCredentials.setLinesVisible(true);
    tableClusterCredentials.addKeyListener(credentialsKeyListener);
    tableClusterCredentials.addMouseListener(credentialsDoubleClickListener);

    TableColumn tblclmnClusterName = new TableColumn(tableClusterCredentials, SWT.NONE);
    tblclmnClusterName.setWidth(180);
    tblclmnClusterName.setText(Strings.CLUSTER_NAME);

    TableColumn tblclmnClusterId = new TableColumn(tableClusterCredentials, SWT.NONE);
    tblclmnClusterId.setWidth(40);
    tblclmnClusterId.setText(Strings.CLUSTER_ID);

    TableColumn tblclmnUsername = new TableColumn(tableClusterCredentials, SWT.NONE);
    tblclmnUsername.setWidth(200);
    tblclmnUsername.setText(Strings.USERNAME);

    TableColumn tblclmnPassword = new TableColumn(tableClusterCredentials, SWT.NONE);
    tblclmnPassword.setWidth(100);
    tblclmnPassword.setText(Strings.PASSWORD);

    Group grpInfobasesCredentials = new Group(credentialsContainer, SWT.NONE);
    grpInfobasesCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    grpInfobasesCredentials.setText(Strings.INFOBASES_CREDENTIALS_GROUP);
    grpInfobasesCredentials.setLayout(new GridLayout(1, false));

    tableInfobasesCredentials =
        new Table(grpInfobasesCredentials, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    GridData gdTableInfobasesCredentials = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gdTableInfobasesCredentials.heightHint = 100;
    tableInfobasesCredentials.setLayoutData(gdTableInfobasesCredentials);
    tableInfobasesCredentials.setHeaderVisible(true);
    tableInfobasesCredentials.setLinesVisible(true);
    tableInfobasesCredentials.addKeyListener(credentialsKeyListener);
    tableInfobasesCredentials.addMouseListener(credentialsDoubleClickListener);

    TableColumn tblclmnUsername1 = new TableColumn(tableInfobasesCredentials, SWT.NONE);
    tblclmnUsername1.setWidth(200);
    tblclmnUsername1.setText(Strings.USERNAME);

    TableColumn tblclmnPassword1 = new TableColumn(tableInfobasesCredentials, SWT.NONE);
    tblclmnPassword1.setWidth(100);
    tblclmnPassword1.setText(Strings.PASSWORD);

    initProperties();
    checkRasOnSameHost();
    setEnabledRasGroupParameters();

    // parent.pack();

    return container;
  }

  private void initProperties() {
    if (server != null) {
      txtDescription.setText(server.getDescription());

      txtRasHost.setText(server.getRasHost());
      txtRasPort.setText(server.getRasPortAsString());

      txtAgentHost.setText(server.getAgentHost());
      txtAgentPort.setText(server.getAgentPortAsString());

      radioUseRemoteRas.setSelection(!server.getUseLocalRas());
      radioUseLocalRas.setSelection(server.getUseLocalRas());

      comboV8Version.setItems(getInstalledV8Versions());
      comboV8Version.setText(server.getLocalRasV8version());

      txtLocalRasPort.setText(server.getLocalRasPortAsString());

      btnAutoconnect.setSelection(server.getAutoconnect());

      saveCredentialsVariant = server.getSaveCredentialsVariant();
      btnSaveCredentialsDisable.setSelection(
          saveCredentialsVariant.equals(SaveCredentialsVariant.DISABLE));
      btnSaveCredentialsName.setSelection(
          saveCredentialsVariant.equals(SaveCredentialsVariant.NAME));
      btnSaveCredentialsNamePass.setSelection(
          saveCredentialsVariant.equals(SaveCredentialsVariant.NAMEPASS));

      fillCredentials();
    }
  }

  private void fillCredentials() {
    fillAgentCredential();

    tableClusterCredentials.removeAll();
    server
        .getAllClustersCredentials()
        .forEach(
            (clusterId, userPass) -> {
              TableItem credentialItem = new TableItem(tableClusterCredentials, SWT.NONE);

              credentialItem.setText(getClusterCredentialItemText(clusterId, userPass));
              credentialItem.setData(UUID_DATA_KEY, clusterId);
              credentialItem.setData(USER_PASS_DATA_KEY, userPass);
            });

    tableInfobasesCredentials.removeAll();
    server
        .getInfobasesCredentials()
        .forEach(
            userPass -> {
              TableItem credentialItem = new TableItem(tableInfobasesCredentials, SWT.NONE);

              credentialItem.setText(getInfobaseCredentialItemText(userPass));
              credentialItem.setData(USER_PASS_DATA_KEY, userPass);
            });
  }

  private void fillAgentCredential() {

    txtAgentCredential.setText(
        String.format(
            AGENT_CREDENTIAL_PATTERN,
            Strings.USERNAME,
            morphToLink(agentCredentialTemp.getUsername()),
            Strings.PASSWORD,
            morphToLink(agentCredentialTemp.getPassword(showPasswordMode))));
    txtAgentCredential.pack();
  }

  private String morphToLink(String text) {
    return "<a>" + text + "</a>";
  }

  private String[] getInstalledV8Versions() {
    List<String> installedV8Versions = new ArrayList<>();
    Helper.getInstalledV8Versions().forEach((desc, path) -> installedV8Versions.add(desc));
    installedV8Versions.sort(String.CASE_INSENSITIVE_ORDER);
    return installedV8Versions.toArray(new String[0]);
  }

  private String[] getClusterCredentialItemText(UUID clusterId, UserPassPair userPass) {
    return new String[] {
      userPass.getDescription(),
      clusterId.toString(),
      userPass.getUsername(),
      userPass.getPassword(showPasswordMode)
    };
  }

  private String[] getInfobaseCredentialItemText(UserPassPair userPass) {
    return new String[] {userPass.getUsername(), userPass.getPassword(showPasswordMode)};
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

  private boolean saveNewServerProperties() {
    try {
      server.setDescription(txtDescription.getText());

      server.setAgentHost(txtAgentHost.getText());
      server.setAgentPort(Integer.parseInt(txtAgentPort.getText()));
      server.setRasHost(txtRasHost.getText());
      server.setRasPort(Integer.parseInt(txtRasPort.getText()));
      server.setUseLocalRas(!radioUseRemoteRas.getSelection());
      server.setLocalRasPort(Integer.parseInt(txtLocalRasPort.getText()));

      server.setLocalRasV8version(comboV8Version.getText());
      server.setAutoconnect(btnAutoconnect.getSelection());

      extractSaveCredentialsVariant();
      server.setSaveCredentialsVariant(saveCredentialsVariant);

      server.setAgentCredential(agentCredentialTemp);
      server.setAllClustersCredentials(extractClustersCredentials());
      server.setAllInfobasesCredentials(extractInfobasesCredentials());

      return true;

    } catch (Exception excp) {
      var messageBox = new MessageBox(getParentShell());
      messageBox.setMessage(excp.getLocalizedMessage());
      messageBox.open();
      return false;
    }
  }

  private void extractSaveCredentialsVariant() {

    if (btnSaveCredentialsName.getSelection()) {
      saveCredentialsVariant = SaveCredentialsVariant.NAME;
    } else if (btnSaveCredentialsNamePass.getSelection()) {
      saveCredentialsVariant = SaveCredentialsVariant.NAMEPASS;
    } else {
      saveCredentialsVariant = SaveCredentialsVariant.DISABLE;
    }
  }

  private Map<UUID, UserPassPair> extractClustersCredentials() {

    Map<UUID, UserPassPair> clustersCredentials = new HashMap<>();
    if (saveCredentialsVariant != SaveCredentialsVariant.DISABLE) {
      TableItem[] credentials = tableClusterCredentials.getItems();
      for (TableItem credential : credentials) {
        UUID clusterId = (UUID) credential.getData(UUID_DATA_KEY);
        UserPassPair userPass = (UserPassPair) credential.getData(USER_PASS_DATA_KEY);
        userPass.clear(saveCredentialsVariant);
        clustersCredentials.put(clusterId, userPass);
      }
    }
    return clustersCredentials;
  }

  private List<UserPassPair> extractInfobasesCredentials() {

    List<UserPassPair> infobasesCredentials = new ArrayList<>();
    if (saveCredentialsVariant != SaveCredentialsVariant.DISABLE) {
      TableItem[] credentials = tableInfobasesCredentials.getItems();
      for (TableItem credential : credentials) {
        UserPassPair userPass = (UserPassPair) credential.getData(USER_PASS_DATA_KEY);
        userPass.clear(saveCredentialsVariant);
        infobasesCredentials.add(userPass);
      }
    }
    return infobasesCredentials;
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

  private SelectionAdapter agentCredentialClickListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          AuthenticateDialog authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(),
                  agentCredentialTemp,
                  Strings.SET_NEW_USERPASS,
                  "");

          if (authenticateDialog.open() == 0) {
            agentCredentialTemp = authenticateDialog.getUserPass();
            fillAgentCredential();
          }
        }
      };

  private KeyAdapter credentialsKeyListener =
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.keyCode == SWT.DEL) {
            TableItem[] credentials = ((Table) e.widget).getSelection();
            for (TableItem credential : credentials) {
              credential.dispose();
            }
          }
        }
      };

  private MouseAdapter credentialsDoubleClickListener =
      new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
          TableItem[] credentials = ((Table) e.widget).getSelection();
          if (credentials.length == 0) {
            return;
          }

          TableItem credential = credentials[0];

          UUID clusterId = (UUID) credential.getData(UUID_DATA_KEY);
          UserPassPair userPass = (UserPassPair) credential.getData(USER_PASS_DATA_KEY);

          AuthenticateDialog authenticateDialog =
              new AuthenticateDialog(
                  Display.getDefault().getActiveShell(), userPass, Strings.SET_NEW_USERPASS, "");

          if (authenticateDialog.open() == 0) {
            userPass = authenticateDialog.getUserPass();
            credential.setData(USER_PASS_DATA_KEY, userPass);

            if (clusterId == null) {
              credential.setText(getInfobaseCredentialItemText(userPass));
            } else {
              credential.setText(getClusterCredentialItemText(clusterId, userPass));
            }
          }
        }
      };

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String CONNECT_PARAMETERS = getString("ConnectParameters");
    static final String SERVER_DESCRIPTION = getString("Description");
    static final String AUTOCONNECT_AT_STARTUP = getString("AutoconnectAtStartup");
    static final String USE_REMOTE_RAS = getString("UseRemoteRAS");
    static final String USE_LOCAL_RAS = getString("UseLocalRAS");
    static final String REMOTE_RAS_PARAMETERS = getString("RemoteRASParameters");
    static final String HOST = getString("Host");
    static final String PORT = getString("Port");
    static final String LOCAL_RAS_PARAMETERS = getString("LocalRASParameters");
    static final String V8_VERSION = getString("V8Version");
    static final String AGENT_PARAMETERS = getString("AgentParameters");
    static final String CREDENTIALS = getString("Credentials");
    static final String SAVE_CREDENTIALS_VARIANT = getString("SaveCredentialsVariant");
    static final String SAVE_CREDENTIALS_NONE = getString("SaveCredentialsNone");
    static final String SAVE_CREDENTIALS_NAME = getString("SaveCredentialsName");
    static final String SAVE_CREDENTIALS_NAMEPASS = getString("SaveCredentialsNamePass");
    static final String SHOW_PASSWORD_MODE_TOOLTIP = getString("ShowPasswordModeToolTipText");
    static final String CENTRAL_SERVER_ADMINISTRATOR = getString("CentralServerAdminstrator");
    static final String CLUSTERS_CREDENTIALS_GROUP = getString("ClustersCredentialsGroup");
    static final String CLUSTER_NAME = getString("ClusterName");
    static final String CLUSTER_ID = getString("ID");
    static final String INFOBASES_CREDENTIALS_GROUP = getString("InfobasesCredentialsGroup");
    static final String USERNAME = getString("Username"); //$NON-NLS-1$ // TODO дубль
    static final String PASSWORD = getString("Password"); //$NON-NLS-1$ // TODO дубль
    static final String SET_NEW_USERPASS = getString("SetNewUserPassword"); //$NON-NLS-1$

    static String getString(String key) {
      return Messages.getString("ServerDialog." + key); //$NON-NLS-1$
    }
  }
}
