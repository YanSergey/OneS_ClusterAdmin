package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.ISessionInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Messages;
import ru.yanygin.clusterAdminLibrary.SessionInfoExtended;

/** Диалоговое окно с подробной информацией о сессии. */
public class SessionInfoDialog extends Dialog {

  private SessionInfoExtended sessionExtInfo;

  private Text txtInfobaseName;
  private Text txtLastActiveAt;
  private Text txtClientIpAddress;
  private Text txtUsername;
  private Text txtApplication;
  private Text txtStartedAt;
  private Text txtComputer;
  private Text txtServer;
  private Text txtPort;
  private Text txtPid;
  private Text txtConnectionNumber;
  private Text txtLicense;
  private Text txtSessionNumber;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param sessionExtInfo - session extension info
   */
  public SessionInfoDialog(Shell parentShell, SessionInfoExtended sessionExtInfo) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.sessionExtInfo = sessionExtInfo;
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
    gridLayout.makeColumnsEqualWidth = true;
    gridLayout.numColumns = 2;

    Label lblInfobaseName = new Label(container, SWT.NONE);
    lblInfobaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfobaseName.setText(Strings.INFOBASE);

    txtInfobaseName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lbSessionNumber = new Label(container, SWT.NONE);
    lbSessionNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lbSessionNumber.setText(Strings.SESSION_N);

    txtSessionNumber = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtSessionNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblStartedAt = new Label(container, SWT.NONE);
    lblStartedAt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblStartedAt.setText(Strings.STARTED_AT);

    txtStartedAt = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtStartedAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblLastActiveAt = new Label(container, SWT.NONE);
    lblLastActiveAt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblLastActiveAt.setText(Strings.LAST_ACTIVE_AT);

    txtLastActiveAt = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtLastActiveAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblComputer = new Label(container, SWT.NONE);
    lblComputer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblComputer.setText(Strings.COMPUTER);

    txtComputer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtComputer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblClientIpAddress = new Label(container, SWT.NONE);
    lblClientIpAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblClientIpAddress.setText(Strings.CLIENT_IP_ADDRESS);

    txtClientIpAddress = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtClientIpAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblUsername = new Label(container, SWT.NONE);
    lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblUsername.setText(Strings.USERNAME);

    txtUsername = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblApplication = new Label(container, SWT.NONE);
    lblApplication.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblApplication.setAlignment(SWT.RIGHT);
    lblApplication.setText(Strings.APPLICATION);

    txtApplication = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtApplication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblServer = new Label(container, SWT.NONE);
    lblServer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblServer.setText(Strings.SERVER);
    lblServer.setAlignment(SWT.RIGHT);

    txtServer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblPort = new Label(container, SWT.NONE);
    lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPort.setText(Strings.PORT);
    lblPort.setAlignment(SWT.RIGHT);

    txtPort = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblPid = new Label(container, SWT.NONE);
    lblPid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPid.setText(Strings.PID);
    lblPid.setAlignment(SWT.RIGHT);

    txtPid = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtPid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblConnectionNumber = new Label(container, SWT.NONE);
    lblConnectionNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblConnectionNumber.setText(Strings.CONNECTION_N);
    lblConnectionNumber.setAlignment(SWT.RIGHT);

    txtConnectionNumber = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtConnectionNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblLicense = new Label(container, SWT.NONE);
    lblLicense.setText(Strings.LICENSE);
    lblLicense.setAlignment(SWT.RIGHT);
    new Label(container, SWT.NONE);

    txtLicense = new Text(container, SWT.BORDER | SWT.READ_ONLY);
    txtLicense.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 2));

    initProperties();

    return container;
  }

  private void initProperties() {
    ISessionInfo sessionInfo = sessionExtInfo.getSessionInfo();

    txtInfobaseName.setText(sessionExtInfo.getInfobaseName());
    txtSessionNumber.setText(Integer.toString(sessionInfo.getSessionId()));
    txtStartedAt.setText(dateToString(sessionInfo.getStartedAt()));
    txtLastActiveAt.setText(dateToString(sessionInfo.getLastActiveAt()));

    txtComputer.setText(sessionInfo.getHost());

    txtClientIpAddress.setText(sessionExtInfo.getClientIpAddress());
    txtUsername.setText(sessionInfo.getUserName());
    txtApplication.setText(sessionExtInfo.getApplicationName());

    txtServer.setText(sessionExtInfo.getWorkingProcessHostName());
    txtPort.setText(sessionExtInfo.getWorkingProcessPort());
    txtPid.setText(sessionExtInfo.getWorkingProcessPid());
    txtConnectionNumber.setText(sessionExtInfo.getConnectionNumber());

    txtLicense.setText(sessionExtInfo.getLicense());
  }

  /**
   * Create contents of the button bar.
   *
   * @param parent - parent composite
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    Button button =
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
    button.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            close();
          }
        });
  }

  private String dateToString(Date date) {

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
    Date emptyDate = new Date(0);

    return date.equals(emptyDate) ? "" : dateFormat.format(date); //$NON-NLS-1$
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");

    static final String INFOBASE = getString("Infobase");
    static final String SESSION_N = getString("SessionN");
    static final String STARTED_AT = getString("StartedAt");
    static final String LAST_ACTIVE_AT = getString("LastActiveAt");
    static final String COMPUTER = getString("Computer");
    static final String CLIENT_IP_ADDRESS = getString("ClientIPAddress");
    static final String USERNAME = getString("Username");
    static final String APPLICATION = getString("Application");
    static final String SERVER = getString("Server");
    static final String PORT = getString("Port");
    static final String PID = getString("PID");
    static final String CONNECTION_N = getString("ConnectionN");
    static final String LICENSE = getString("License");

    static String getString(String key) {
      return Messages.getString("SessionInfo." + key); //$NON-NLS-1$
    }
  }
}
