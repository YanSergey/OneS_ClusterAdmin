package ru.yanygin.clusterAdminLibraryUI;

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
import ru.yanygin.clusterAdminLibrary.Server;

/** Dialog for drop infobase. */
public class DropInfobaseDialog extends Dialog {

  private UUID clusterId;
  private Server server;

  private int databaseDropMode;
  private UUID infobaseId;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server parameters
   * @param clusterId - cluster ID
   * @param infobaseId - infobase ID
   */
  public DropInfobaseDialog(Shell parentShell, Server server, UUID clusterId, UUID infobaseId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    // super.configureShell(parentShell);
    // parentShell.setText("Parameters of the 1C:Enterprise infobase");

    this.infobaseId = infobaseId;
    this.clusterId = clusterId;
    this.server = server;
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

    Label lblInfo = new Label(container, SWT.WRAP);
    GridData gdlblInfo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
    gdlblInfo.heightHint = 34;
    lblInfo.setLayoutData(gdlblInfo);
    lblInfo.setText(Messages.getString("Dialogs.DropInfobaseDescription")); //$NON-NLS-1$
    new Label(container, SWT.NONE);

    Button btnNotDelete = new Button(container, SWT.RADIO);
    btnNotDelete.setText(Messages.getString("Dialogs.LeaveDatabaseUnchanged")); //$NON-NLS-1$
    btnNotDelete.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            databaseDropMode = 0;
          }
        });
    new Label(container, SWT.NONE);

    Button btnDeleteTheDatabase = new Button(container, SWT.RADIO);
    btnDeleteTheDatabase.setText(
        Messages.getString("Dialogs.DeleteTheEntireDatabase")); //$NON-NLS-1$
    btnDeleteTheDatabase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            databaseDropMode = 1;
          }
        });
    new Label(container, SWT.NONE);

    Button btnClearTheDatabase = new Button(container, SWT.RADIO);
    btnClearTheDatabase.setText(Messages.getString("Dialogs.ClearTheDatabase")); //$NON-NLS-1$
    btnClearTheDatabase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            databaseDropMode = 2;
          }
        });

    return container;
  }

  private void runRemoveInfobase() {

    if (server.dropInfoBase(clusterId, infobaseId, databaseDropMode)) {
      close();
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
            runRemoveInfobase();
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }
}
