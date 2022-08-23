package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.UserPassPair;

/** Диалог для ввода логина и пароля. */
public class AuthenticateDialog extends Dialog {

  private UserPassPair currentUserPass;
  private List<UserPassPair> credentials = new ArrayList<>();
  private String authExcpMessage;
  private String authTitle;

  private Combo txtUsername;
  private Text txtPassword;

  /**
   * Создание диалога ввода имени пользователя и пароля.
   *
   * @param parentShell - parent shell
   * @param currentUserPass - текущие имя пользователя и пароль
   * @param authTitle - заголовок окна аутентификации
   * @param authExcpMessage - ошибка аутентификации для вывода пользователю
   * @wbp.parser.constructor
   */
  public AuthenticateDialog(
      Shell parentShell, UserPassPair currentUserPass, String authTitle, String authExcpMessage) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.currentUserPass = currentUserPass;
    this.authTitle = authTitle;
    this.authExcpMessage = authExcpMessage;
  }

  /**
   * Создание диалога ввода имени пользователя и пароля с возможностью выбора пользователя.
   *
   * @param parentShell - parent shell
   * @param userPass - имя пользователя и пароль
   * @param authTitle - заголовок окна аутентификации
   * @param authExcpMessage - ошибка аутентификации для вывода пользователю
   * @param credentials - список сохраненных логин/паролей
   */
  public AuthenticateDialog(
      Shell parentShell,
      UserPassPair userPass,
      String authTitle,
      String authExcpMessage,
      List<UserPassPair> credentials) {

    this(parentShell, userPass, authTitle, authExcpMessage);
    this.credentials = credentials;
  }

  @Override
  protected void configureShell(Shell newShell) {
    newShell.setMinimumSize(new Point(300, 39));
    super.configureShell(newShell);
    newShell.setText(authTitle);
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

    Label lblAuthenticateInfo = new Label(container, SWT.WRAP);
    lblAuthenticateInfo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
    lblAuthenticateInfo.setText(authTitle);

    Label lblUsername = new Label(container, SWT.NONE);
    lblUsername.setText(Strings.USERNAME);

    txtUsername = new Combo(container, SWT.NONE);
    txtUsername.setToolTipText(Strings.USERNAME);
    txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtUsername.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            String pass = (String) txtUsername.getData(txtUsername.getText());
            txtPassword.setText(pass);
          }
        });

    credentials.forEach(
        userpass -> {
          txtUsername.add(userpass.getUsername());
          txtUsername.setData(userpass.getUsername(), userpass.getPassword());
        });

    Label lblPassword = new Label(container, SWT.NONE);
    lblPassword.setText(Strings.PASSWORD);

    txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
    txtPassword.setToolTipText(Strings.PASSWORD);
    txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblAuthExcpMessage = new Label(container, SWT.WRAP);
    lblAuthExcpMessage.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
    lblAuthExcpMessage.setText(authExcpMessage);

    initProperties();

    return container;
  }

  /**
   * Получает введенный пользователем логин.
   *
   * @return логин
   */
  public UserPassPair getUserPass() {
    return currentUserPass;
  }

  private void initProperties() {
    this.txtUsername.setText(currentUserPass.getUsername());
    this.txtPassword.setText(currentUserPass.getPassword());
  }

  private void extractVariablesFromControls() {

    currentUserPass =
        new UserPassPair(
            txtUsername.getText(), txtPassword.getText(), currentUserPass.getDescription());
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
            extractVariablesFromControls();
            close();
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {

    //    static final String TITLE_WINDOW = getString("ServerParameters");
    static final String USERNAME = getString("Username"); //$NON-NLS-1$
    static final String PASSWORD = getString("Password"); //$NON-NLS-1$

    static String getString(String key) {
      return Messages.getString("Dialogs." + key); //$NON-NLS-1$
    }
  }
}
