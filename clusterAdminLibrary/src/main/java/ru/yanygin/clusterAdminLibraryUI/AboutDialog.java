package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/** About dialog. */
public class AboutDialog extends Dialog {

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   */
  public AboutDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
  }

  /**
   * Create contents of the dialog.
   *
   * @param parent - parent composite
   */
  @Override
  protected Control createDialogArea(Composite parent) {

    Composite container = (Composite) super.createDialogArea(parent);

    Label lblMainTitle = new Label(container, SWT.NONE);
    lblMainTitle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
    lblMainTitle.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.BOLD)); //$NON-NLS-1$
    lblMainTitle.setText(Messages.getString("AboutDialog.MainTitle"));

    Label lblDescription = new Label(container, SWT.NONE);
    lblDescription.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lblDescription.setText(Messages.getString("AboutDialog.Description")); //$NON-NLS-1$
    lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));

    Label lblAutor = new Label(container, SWT.NONE);
    lblAutor.setText(Messages.getString("AboutDialog.Autor"));
    lblAutor.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));

    Link linkGitHub = new Link(container, SWT.NONE);
    linkGitHub.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            Program.launch("https://github.com/YanSergey/OneS_ClusterAdmin");
          }
        });
    linkGitHub.setText(Messages.getString("AboutDialog.GitHub"));

    Link linkTelegram = new Link(container, 0);
    linkTelegram.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            Program.launch("https://t.me/YanSergey");
          }
        });
    linkTelegram.setText(Messages.getString("AboutDialog.Telegram"));

    Link linkEmail = new Link(container, 0);
    linkEmail.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            Program.launch("mailto:yanyginsa@gmail.com");
          }
        });
    linkEmail.setText(Messages.getString("AboutDialog.Email"));

    return container;
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

  @Override
  protected Point getInitialSize() {
    return new Point(500, 330);
  }
}
