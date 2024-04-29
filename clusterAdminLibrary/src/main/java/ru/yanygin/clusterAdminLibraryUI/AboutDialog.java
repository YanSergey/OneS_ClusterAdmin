package ru.yanygin.clusterAdminLibraryUI;

import java.lang.module.ModuleDescriptor.Version;
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
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibrary.Helper;

/** Диалог "О программе". */
public class AboutDialog extends Dialog {

  private static final String SEGOE_FONT = "Segoe UI"; //$NON-NLS-1$
  private static final String GITHUB_LINK =
      "https://github.com/YanSergey/OneS_ClusterAdmin"; //$NON-NLS-1$
  private static final String TELEGRAM_LINK = "https://t.me/YanSergey"; //$NON-NLS-1$
  private static final String EMAIL_LINK = "mailto:yanyginsa@gmail.com"; //$NON-NLS-1$

  Config config = Config.currentConfig;

  private Link linkLatestVersion;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   */
  public AboutDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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

    Label lblMainTitle = new Label(container, SWT.NONE);
    lblMainTitle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
    lblMainTitle.setFont(SWTResourceManager.getFont(SEGOE_FONT, 20, SWT.BOLD));
    lblMainTitle.setText(Strings.MAIN_TITLE);

    Label lblDescription = new Label(container, SWT.NONE);
    lblDescription.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lblDescription.setText(Strings.DESCRIPTION);
    lblDescription.setFont(SWTResourceManager.getFont(SEGOE_FONT, 10, SWT.NORMAL));

    Link linkCurrentVersion = new Link(container, 0);
    linkCurrentVersion.setFont(SWTResourceManager.getFont(SEGOE_FONT, 10, SWT.NORMAL));
    linkCurrentVersion.setText(String.format(Strings.CURRENT_VERSION, config.getCurrentVersion()));

    linkLatestVersion = new Link(container, 0);
    linkLatestVersion.setText(Strings.CURRENT_VERSION_IS_UNKNOWN);
    linkLatestVersion.addSelectionListener(downloadLatestVersionListener);
    makeLatestVersionTitle();

    Label lblAutor = new Label(container, SWT.NONE);
    lblAutor.setText(Strings.AUTOR);

    Link linkGitHub = new Link(container, SWT.NONE);
    linkGitHub.setText(Strings.GITHUB_DESCRIPTION);
    linkGitHub.addSelectionListener(goToLinkListener);

    Link linkTelegram = new Link(container, 0);
    linkTelegram.setText(Strings.TELEGRAM_DESCRIPTION);
    linkTelegram.addSelectionListener(goToLinkListener);

    Link linkEmail = new Link(container, 0);
    linkEmail.setText(Strings.EMAIL_DESCRIPTION);
    linkEmail.addSelectionListener(goToLinkListener);

    Link linkBoosty = new Link(container, 0);
    linkBoosty.setText(Strings.BOOSTY_DESCRIPTION);
    linkBoosty.addSelectionListener(goToLinkListener);

    return container;
  }

  private void makeLatestVersionTitle() {

    final Version currentVersion = config.getCurrentVersion();
    final Version latestVersion = config.getLatestVersion();

    if (latestVersion == null) {
      linkLatestVersion.setText(Strings.CURRENT_VERSION_IS_UNKNOWN);
    } else if (currentVersion.equals(latestVersion)
        || currentVersion.compareTo(latestVersion) > 0) {
      linkLatestVersion.setText(Strings.CURRENT_VERSION_IS_LATEST);
    } else {
      linkLatestVersion.setText(String.format(Strings.CURRENT_VERSION_IS_OLD, latestVersion));
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
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
    button.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            close();
          }
        });
  }

  SelectionAdapter downloadLatestVersionListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          if (config.getLatestVersion() == null) {
            config.readUpstreamVersion();
            makeLatestVersionTitle();
          } else {
            config.runDownloadRelease(getParentShell());
          }
        }
      };

  SelectionAdapter goToLinkListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Program.launch(e.text);
        }
      };

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String MAIN_TITLE = getString("MainTitle");

    static final String CURRENT_VERSION = getString("CurrentVersion");
    static final String CURRENT_VERSION_IS_LATEST = getString("CurrentVersionIsLatest");
    static final String CURRENT_VERSION_IS_OLD = getString("CurrentVersionIsOld");
    static final String CURRENT_VERSION_IS_UNKNOWN = getString("CurrentVersionIsUnknown");

    static final String DESCRIPTION = getString("Description");
    static final String AUTOR = getString("Autor");
    static final String GITHUB_DESCRIPTION =
        String.format(getString("GitHub_Description"), GITHUB_LINK);
    static final String TELEGRAM_DESCRIPTION =
        String.format(getString("Telegram_Description"), TELEGRAM_LINK);
    static final String EMAIL_DESCRIPTION =
        String.format(getString("Email_Description"), EMAIL_LINK);
    static final String BOOSTY_DESCRIPTION =
        String.format(getString("Boosty_Description"), Helper.BOOSTY_LINK);

    static String getString(String key) {
      return Messages.getString("AboutDialog." + key); //$NON-NLS-1$
    }
  }

  @Override
  protected Point getInitialSize() {
    return new Point(500, 418);
  }
}
