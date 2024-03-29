package ru.yanygin.clusterAdminLibraryUI;

import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог удаления информационной базы. */
public class DropInfobaseDialog extends Dialog {

  private Server server;
  private UUID clusterId;
  private UUID infobaseId;
  private int databaseDropMode;

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

    this.server = server;
    this.clusterId = clusterId;
    this.infobaseId = infobaseId;
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

    Label lblInfo = new Label(container, SWT.WRAP);
    GridData gdlblInfo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
    gdlblInfo.heightHint = 34;
    lblInfo.setLayoutData(gdlblInfo);
    lblInfo.setText(Strings.DROP_INFOBASE_DESCRIPTION);
    new Label(container, SWT.NONE);

    Button btnNotDelete = new Button(container, SWT.RADIO);
    btnNotDelete.setText(Strings.LEAVE_DATABASE_UNCHANGED);
    btnNotDelete.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            databaseDropMode = 0;
          }
        });
    new Label(container, SWT.NONE);

    Button btnDeleteTheDatabase = new Button(container, SWT.RADIO);
    btnDeleteTheDatabase.setText(Strings.DELETE_THE_ENTIRE_DATABASE);
    btnDeleteTheDatabase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            databaseDropMode = 1;
          }
        });
    new Label(container, SWT.NONE);

    Button btnClearTheDatabase = new Button(container, SWT.RADIO);
    btnClearTheDatabase.setText(Strings.CLEAR_THE_DATABASE);
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

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDropInfobaseParameters");
    static final String DROP_INFOBASE_DESCRIPTION = getString("DropInfobaseDescription");
    static final String LEAVE_DATABASE_UNCHANGED = getString("LeaveDatabaseUnchanged");
    static final String DELETE_THE_ENTIRE_DATABASE = getString("DeleteTheEntireDatabase");
    static final String CLEAR_THE_DATABASE = getString("ClearTheDatabase");

    static String getString(String key) {
      return Messages.getString("InfobaseDialog." + key); //$NON-NLS-1$
    }
  }

  @Override
  protected Point getInitialSize() {
    return new Point(500, 230);
  }
}
