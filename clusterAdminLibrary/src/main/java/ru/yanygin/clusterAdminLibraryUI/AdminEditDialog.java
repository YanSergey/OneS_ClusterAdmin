package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IRegUserInfo;
import com._1c.v8.ibis.admin.RegUserInfo;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог редактирования администратора. */
public class AdminEditDialog extends Dialog {

  private Server server;
  private UUID clusterId;
  private IRegUserInfo userInfo;

  private Text txtUsername;
  private Text txtPassword;
  private Text txtPasswordConfirm;
  private Text txtSysUsername;
  private Text txtDescription;
  private Button btnPasswordAuthAllowed;
  private Button btnSysAuthAllowed;

  private boolean passIsModified = false;

  /**
   * Создание диалога редактирования администратора.
   *
   * @param parentShell - parent shell
   * @param server - Сервер
   * @param clusterId - ID кластера
   * @param userInfo - IRegUserInfo пользователя
   * @wbp.parser.constructor
   */
  public AdminEditDialog(Shell parentShell, Server server, UUID clusterId, IRegUserInfo userInfo) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
    this.userInfo = userInfo;
  }

  @Override
  protected void configureShell(Shell newShell) {
    newShell.setMinimumSize(new Point(300, 39));
    super.configureShell(newShell);

    if (userInfo == null) {
      newShell.setText(clusterId == null ? Strings.TITLE_SERVER_NEW : Strings.TITLE_CLUSTER_NEW);
    } else {
      newShell.setText(clusterId == null ? Strings.TITLE_SERVER_EDIT : Strings.TITLE_CLUSTER_EDIT);
    }
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

    Label lblUsername = new Label(container, SWT.NONE);
    lblUsername.setText(Strings.NAME);

    txtUsername = new Text(container, SWT.BORDER);
    txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblDescription = new Label(container, SWT.NONE);
    lblDescription.setText(Strings.DESCRIPTION);

    txtDescription = new Text(container, SWT.BORDER);
    txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnPasswordAuthAllowed = new Button(container, SWT.CHECK);
    btnPasswordAuthAllowed.setText(Strings.PASSWORD_AUTH_ALLOWED);
    btnPasswordAuthAllowed.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            txtPassword.setEditable(btnPasswordAuthAllowed.getSelection());
            txtPasswordConfirm.setEditable(btnPasswordAuthAllowed.getSelection());
          }
        });

    new Label(container, SWT.NONE);

    Label lblPassword = new Label(container, SWT.NONE);
    lblPassword.setText(Strings.PASSWORD);

    txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
    txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label lblPasswordConfirm = new Label(container, SWT.NONE);
    lblPasswordConfirm.setText(Strings.PASSWORD_CONFIRM);

    txtPasswordConfirm = new Text(container, SWT.BORDER | SWT.PASSWORD);
    txtPasswordConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    btnSysAuthAllowed = new Button(container, SWT.CHECK);
    btnSysAuthAllowed.setText(Strings.SYS_AUTH_ALLOWED);
    btnSysAuthAllowed.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            txtSysUsername.setEditable(btnSysAuthAllowed.getSelection());
          }
        });

    new Label(container, SWT.NONE);

    Label lblSysUsername = new Label(container, SWT.NONE);
    lblSysUsername.setText(Strings.SYS_USERNAME);

    txtSysUsername = new Text(container, SWT.BORDER);
    txtSysUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    initProperties();

    parent.pack();

    return container;
  }

  /**
   * Получает итоговую информацию об администраторе.
   *
   * @return IRegUserInfo информация об администраторе
   */
  public IRegUserInfo getUserinfo() {
    return userInfo;
  }

  private void initProperties() {
    if (userInfo != null) {
      this.txtUsername.setText(userInfo.getName());
      this.txtDescription.setText(userInfo.getDescr());

      this.btnPasswordAuthAllowed.setSelection(userInfo.isPasswordAuthAllowed());
      this.txtPassword.setText(userInfo.getPassword());
      this.txtPasswordConfirm.setText(userInfo.getPassword());

      this.btnSysAuthAllowed.setSelection(userInfo.isSysAuthAllowed());
      this.txtSysUsername.setText(userInfo.getSysUserName());
    }

    txtPassword.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            passIsModified = true;
          }
        });
    txtPasswordConfirm.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            passIsModified = true;
          }
        });
    txtPassword.setEditable(btnPasswordAuthAllowed.getSelection());
    txtPasswordConfirm.setEditable(btnPasswordAuthAllowed.getSelection());
    txtSysUsername.setEditable(btnSysAuthAllowed.getSelection());
  }

  private boolean regClusterAdmin() {
    if (passIsModified && !txtPassword.getText().equals(txtPasswordConfirm.getText())) {
      Helper.showMessageBox(Strings.PASSWORDS_NOT_MATCH);
      return false;
    }

    if (userInfo == null) {
      userInfo =
          new RegUserInfo(
              txtUsername.getText(),
              txtDescription.getText(),
              txtPassword.getText(),
              btnPasswordAuthAllowed.getSelection(),
              btnSysAuthAllowed.getSelection(),
              txtSysUsername.getText());
    } else {

      userInfo.setName(txtUsername.getText());
      userInfo.setDescr(txtDescription.getText());

      userInfo.setSysAuthAllowed(btnPasswordAuthAllowed.getSelection());
      userInfo.setPassword(txtPassword.getText());

      userInfo.setSysAuthAllowed(btnSysAuthAllowed.getSelection());
      userInfo.setSysUserName(txtSysUsername.getText());
    }

    boolean unregOk =
        clusterId == null
            ? server.regAgentAdmin(userInfo)
            : server.regClusterAdmin(clusterId, userInfo);

    return unregOk;
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
            if (regClusterAdmin()) {
              close();
            }
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {

    static final String TITLE_CLUSTER_EDIT = getString("TitleClusterEdit");
    static final String TITLE_CLUSTER_NEW = getString("TitleClusterNew");
    static final String TITLE_SERVER_EDIT = getString("TitleServerEdit");
    static final String TITLE_SERVER_NEW = getString("TitleServerNew");

    static final String NAME = getString("Name");
    static final String DESCRIPTION = getString("Description");
    static final String PASSWORD_AUTH_ALLOWED = getString("PasswordAuthAllowed");
    static final String PASSWORD = getString("Password");
    static final String PASSWORD_CONFIRM = getString("PasswordConfirm");
    static final String SYS_AUTH_ALLOWED = getString("SysAuthAllowed");
    static final String SYS_USERNAME = getString("SysUsername");

    static final String PASSWORDS_NOT_MATCH = getString("PasswordsNotMatch");

    static String getString(String key) {
      return Messages.getString("AdminDialog." + key); // $NON-NLS-1$
    }
  }
}
