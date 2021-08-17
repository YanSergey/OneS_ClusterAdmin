package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AuthenticateDialog extends Dialog {
	
	private Text txtUsername;
	private Text txtPassword;
	
	private String username;
	private String password;
	private String authExcpMessage;
	private String authDescription;
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public AuthenticateDialog(Shell parentShell, String username, String authDescription, String authExcpMessage) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.username 			= username;
		this.authDescription 	= authDescription;
		this.authExcpMessage 	= authExcpMessage;
		
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
		
		Label lblAuthenticateInfo = new Label(container, SWT.WRAP);
		lblAuthenticateInfo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
		lblAuthenticateInfo.setText(authDescription);
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setText(Messages.getString("Dialogs.Username")); //$NON-NLS-1$
		
		txtUsername = new Text(container, SWT.BORDER);
		txtUsername.setToolTipText(Messages.getString("Dialogs.Username")); //$NON-NLS-1$
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setText(Messages.getString("Dialogs.Password")); //$NON-NLS-1$
		
		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setToolTipText(Messages.getString("Dialogs.Password")); //$NON-NLS-1$
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblAuthExcpMessage = new Label(container, SWT.WRAP);
		lblAuthExcpMessage.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
		lblAuthExcpMessage.setText(authExcpMessage);

		initProperties();
		
		return container;
	}

	private void initProperties() {
		this.txtUsername.setText(username);
	}

	private void extractVariablesFromControls() {
		
		username = txtUsername.getText();
		password = txtPassword.getText();
		
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
				extractVariablesFromControls();
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
		return new Point(420, 240);
	}

}
