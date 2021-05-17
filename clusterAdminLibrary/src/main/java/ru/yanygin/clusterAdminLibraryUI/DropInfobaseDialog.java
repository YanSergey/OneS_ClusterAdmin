package ru.yanygin.clusterAdminLibraryUI;

import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com._1c.v8.ibis.admin.IClusterInfo;
import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DropInfobaseDialog extends Dialog {
	
	private IClusterInfo clusterInfo;
	private Server server;
	private Button btnClearTheDatabase;
	private Button btnNotDelete;
	private Button btnDeleteTheDatabase;
	
	private int databaseDropMode;
	private UUID infoBaseID;


	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public DropInfobaseDialog(Shell parentShell, Server server, IClusterInfo clusterInfo, UUID infoBaseID) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.infoBaseID = infoBaseID;
		this.clusterInfo = clusterInfo;
		this.server = server;
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
//		parent.addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent e) {
////				extractInfobaseVariablesFromControls();
//			}
//		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		Label lblInfo = new Label(container, SWT.WRAP);
		GridData gd_lblInfo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_lblInfo.heightHint = 34;
		lblInfo.setLayoutData(gd_lblInfo);
		lblInfo.setText(
				"When deleting an information database, you can choose one of 3 actions on the database that contains the information database data:");
		// lblInfo.setText("При удалении информационной базы можно выбрать одно из 3-х
		// действий над базой данных, в которой содержатся данные информационной
		// базы:");
		new Label(container, SWT.NONE);

		btnNotDelete = new Button(container, SWT.RADIO);
		btnNotDelete.setText("Leave the database and its contents unchanged");
		btnNotDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseDropMode = 0;
			}
		});
		new Label(container, SWT.NONE);
		// btnNotDelete.setText("Оставить базу данных и ее содержимое без изменений");

		btnDeleteTheDatabase = new Button(container, SWT.RADIO);
		btnDeleteTheDatabase.setText("Delete the entire database");
		// btnDeleteTheDatabase.setText("Удалить базу данных целиком");
		btnDeleteTheDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseDropMode = 1;
			}
		});
		new Label(container, SWT.NONE);

		btnClearTheDatabase = new Button(container, SWT.RADIO);
		btnClearTheDatabase.setText("Clear the database by removing the information database data from it");
//		btnClearTheDatabase.setText("Очистить базу данных, убрав из нее данные информационной базы");
		btnClearTheDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseDropMode = 2;
			}
		});
		
		return container;
	}

	private void runRemoveInfobase() {

		try {
			server.dropInfoBase(clusterInfo.getClusterId(), infoBaseID, databaseDropMode);
			close();
		} catch (Exception excp) {
			excp.printStackTrace();
			MessageBox messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}

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
				runRemoveInfobase();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 210);
	}

}
