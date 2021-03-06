package ru.yanygin.clusterAdminLibraryUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;

import ru.yanygin.clusterAdminLibrary.Messages;
import ru.yanygin.clusterAdminLibrary.Server;

public class SessionInfoDialog extends Dialog {
		
	private UUID clusterId;
	private UUID sessionId;
	private ISessionInfo sessionInfo;

	private Server server;
	private Text txtInfobaseName;
	private Text txtLastActiveAt;
	private Text txtClientIPAddress;
	private Text txtUsername;
	private Text txtApplication;
	private Text txtStartedAt;
	private Text txtComputer;
	
	private Text txtServer;
	private Text txtPort;
	private Text txtPID;
	private Text txtConnectionNumber;
	private Text txtLicense;
	private Text txtSessionNumber;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */	
	public SessionInfoDialog(Shell parentShell, Server server, UUID clusterId, UUID sessionId, ISessionInfo sessionInfo) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.server = server;
		this.clusterId = clusterId;
		this.sessionId = sessionId;
		this.sessionInfo = sessionInfo;
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 2;
		
		Label lblInfobaseName = new Label(container, SWT.NONE);
		lblInfobaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfobaseName.setText(Messages.getString("SessionInfo.Infobase")); //$NON-NLS-1$
		
		txtInfobaseName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbSessionNumber = new Label(container, SWT.NONE);
		lbSessionNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbSessionNumber.setText(Messages.getString("SessionInfo.SessionN")); //$NON-NLS-1$
		
		txtSessionNumber = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtSessionNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStartedAt = new Label(container, SWT.NONE);
		lblStartedAt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStartedAt.setText(Messages.getString("SessionInfo.StartedAt")); //$NON-NLS-1$
		
		txtStartedAt = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtStartedAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));		
		
		Label lblLastActiveAt = new Label(container, SWT.NONE);
		lblLastActiveAt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLastActiveAt.setText(Messages.getString("SessionInfo.LastActiveAt")); //$NON-NLS-1$
		
		txtLastActiveAt = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtLastActiveAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblComputer = new Label(container, SWT.NONE);
		lblComputer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComputer.setText(Messages.getString("SessionInfo.Computer")); //$NON-NLS-1$
		
		txtComputer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtComputer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblClientIPAddress = new Label(container, SWT.NONE);
		lblClientIPAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClientIPAddress.setText(Messages.getString("SessionInfo.ClientIPAddress")); //$NON-NLS-1$
		
		txtClientIPAddress = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtClientIPAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText(Messages.getString("SessionInfo.Username")); //$NON-NLS-1$
		
		txtUsername = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblApplication = new Label(container, SWT.NONE);
		lblApplication.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblApplication.setAlignment(SWT.RIGHT);
		lblApplication.setText(Messages.getString("SessionInfo.Application")); //$NON-NLS-1$
		
		txtApplication = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtApplication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblServer = new Label(container, SWT.NONE);
		lblServer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServer.setText(Messages.getString("SessionInfo.Server")); //$NON-NLS-1$
		lblServer.setAlignment(SWT.RIGHT);
		
		txtServer = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText(Messages.getString("SessionInfo.Port")); //$NON-NLS-1$
		lblPort.setAlignment(SWT.RIGHT);
		
		txtPort = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPID = new Label(container, SWT.NONE);
		lblPID.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPID.setText(Messages.getString("SessionInfo.PID")); //$NON-NLS-1$
		lblPID.setAlignment(SWT.RIGHT);
		
		txtPID = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtPID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblConnectionNumber = new Label(container, SWT.NONE);
		lblConnectionNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConnectionNumber.setText(Messages.getString("SessionInfo.ConnectionN")); //$NON-NLS-1$
		lblConnectionNumber.setAlignment(SWT.RIGHT);
		
		txtConnectionNumber = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtConnectionNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLicense = new Label(container, SWT.NONE);
		lblLicense.setText(Messages.getString("SessionInfo.License")); //$NON-NLS-1$
		lblLicense.setAlignment(SWT.RIGHT);
		new Label(container, SWT.NONE);
		
		txtLicense = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtLicense.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 2));

		initInfobaseProperties();
		
		return container;
	}

	private void initInfobaseProperties() {
		if (sessionInfo == null)
			sessionInfo = server.getSessionInfo(clusterId, sessionId);
		
		if (sessionInfo == null)
			return;
		
		UUID infobaseId = sessionInfo.getInfoBaseId();
		String infobaseName = server.getInfoBaseName(clusterId, infobaseId);
		
		UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000"); //$NON-NLS-1$
		
		// connection
		var connectionNumber = ""; //$NON-NLS-1$
		if (!sessionInfo.getConnectionId().equals(emptyUuid)) {
			IInfoBaseConnectionShort connectionInfoShort = server.getConnectionInfoShort(clusterId, sessionInfo.getConnectionId());
			connectionNumber = String.valueOf(connectionInfoShort.getConnId());
		}

		// Working Process
		var wpHostName = ""; //$NON-NLS-1$
		var wpMainPort = ""; //$NON-NLS-1$
		var wpPid = ""; //$NON-NLS-1$
		if (!sessionInfo.getWorkingProcessId().equals(emptyUuid)) {
			IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, sessionInfo.getWorkingProcessId());
			wpHostName = wpInfo.getHostName();
			wpMainPort = Integer.toString(wpInfo.getMainPort());
			wpPid = wpInfo.getPid();
		}
		
		// license
		var license = sessionInfo.getLicenses().isEmpty() ? "" : sessionInfo.getLicenses().get(0).getFullPresentation(); //$NON-NLS-1$
		
		txtInfobaseName.setText(infobaseName);
		txtSessionNumber.setText(Integer.toString(sessionInfo.getSessionId()));
		txtStartedAt.setText(dateToString(sessionInfo.getStartedAt()));
		txtLastActiveAt.setText(dateToString(sessionInfo.getLastActiveAt()));
		
		txtComputer.setText(sessionInfo.getHost());
		String clientIPAddress = sessionInfo.getClientIPAddress() == null ? "" : sessionInfo.getClientIPAddress(); //8.3.17+
		txtClientIPAddress.setText(clientIPAddress);
		txtUsername.setText(sessionInfo.getUserName());
		txtApplication.setText(server.getApplicationName(sessionInfo.getAppId()));
		
		txtServer.setText(wpHostName);
		txtPort.setText(wpMainPort);
		txtPID.setText(wpPid);
		txtConnectionNumber.setText(connectionNumber);
		txtLicense.setText(license);
		
		
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
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

}
