package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IRegUserInfo;
import java.util.List;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог редактирования администратора. */
public class AdminsDialog extends Dialog {

  private Server server;
  private UUID clusterId;

  private Table tableAdmins;

  final Image addIcon16 = Helper.getImage("add_16.png"); // $NON-NLS-1$
  final Image editIcon16 = Helper.getImage("edit_16.png"); // $NON-NLS-1$
  final Image deleteIcon16 = Helper.getImage("delete_16.png"); // $NON-NLS-1$

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   * @param server - server params
   * @param clusterId - Id кластера
   */
  public AdminsDialog(Shell parentShell, Server server, UUID clusterId) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM);

    this.server = server;
    this.clusterId = clusterId;
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

    ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);

    ToolItem toolBarAdd = new ToolItem(toolBar, SWT.NONE);
    toolBarAdd.setImage(addIcon16);
    toolBarAdd.setText(Strings.TOOLBAR_ADD);
    toolBarAdd.addSelectionListener(addAdminListener);

    ToolItem toolBarEdit = new ToolItem(toolBar, SWT.NONE);
    toolBarEdit.setImage(editIcon16);
    toolBarEdit.setText(Strings.TOOLBAR_EDIT);
    toolBarEdit.addSelectionListener(editAdminListener);

    ToolItem toolBarDelete = new ToolItem(toolBar, SWT.NONE);
    toolBarDelete.setImage(deleteIcon16);
    toolBarDelete.setText(Strings.TOOLBAR_DELETE);
    toolBarDelete.addSelectionListener(delAdminListener);

    tableAdmins = new Table(container, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
    tableAdmins.setHeaderVisible(true);
    tableAdmins.setLinesVisible(true);
    tableAdmins.addKeyListener(keyPressListener);
    tableAdmins.addMouseListener(mouseDoubleClickListener);

    TableColumn tcName = new TableColumn(tableAdmins, SWT.NONE);
    tcName.setWidth(120);
    tcName.setText(Strings.NAME);

    TableColumn tcDescr = new TableColumn(tableAdmins, SWT.NONE);
    tcDescr.setWidth(150);
    tcDescr.setText(Strings.DESCRIPTION);

    TableColumn tcPasswordAuthAllowed = new TableColumn(tableAdmins, SWT.NONE);
    tcPasswordAuthAllowed.setWidth(110);
    tcPasswordAuthAllowed.setText(Strings.PASSWORD_AUTH_ALLOWED);

    TableColumn tcSysAuthAllowed = new TableColumn(tableAdmins, SWT.NONE);
    tcSysAuthAllowed.setWidth(80);
    tcSysAuthAllowed.setText(Strings.SYS_AUTH_ALLOWED);

    TableColumn tcSysUsername = new TableColumn(tableAdmins, SWT.NONE);
    tcSysUsername.setWidth(120);
    tcSysUsername.setText(Strings.SYS_USERNAME);

    initContextMenu();
    initProperties();

    parent.pack();

    return container;
  }

  private void initContextMenu() {
    Menu tableMenu = new Menu(tableAdmins);
    tableAdmins.setMenu(tableMenu);
    // установить активность контекстного меню

    MenuItem addMenuItem = new MenuItem(tableMenu, SWT.NONE);
    addMenuItem.setText(Strings.CONTEXT_MENU_ADD);
    addMenuItem.setImage(addIcon16);
    addMenuItem.addSelectionListener(addAdminListener);

    MenuItem editMenuItem = new MenuItem(tableMenu, SWT.NONE);
    editMenuItem.setText(Strings.CONTEXT_MENU_EDIT);
    editMenuItem.setImage(editIcon16);
    editMenuItem.addSelectionListener(editAdminListener);

    MenuItem delMenuItem = new MenuItem(tableMenu, SWT.NONE);
    delMenuItem.setText(Strings.CONTEXT_MENU_DELETE);
    delMenuItem.setImage(deleteIcon16);
    delMenuItem.addSelectionListener(delAdminListener);
  }

  private void initProperties() {
    if (server != null) {
      fillTableAdmins();
    }
  }

  private void fillTableAdmins() {

    tableAdmins.removeAll();
    List<IRegUserInfo> admins =
        clusterId == null ? server.getAgentAdmins() : server.getClusterAdmins(clusterId);

    admins.forEach(
        (userInfo) -> {
          TableItem adminItem = new TableItem(tableAdmins, SWT.NONE);
          adminItem.setText(getTableAdminsItemText(userInfo));
          adminItem.setData(userInfo);
        });
    tableAdmins.pack();
  }

  private String[] getTableAdminsItemText(IRegUserInfo userInfo) {
    return new String[] {
      userInfo.getName(),
      userInfo.getDescr(),
      String.valueOf(userInfo.isPasswordAuthAllowed()),
      String.valueOf(userInfo.isSysAuthAllowed()),
      userInfo.getSysUserName()
    };
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

  private KeyAdapter keyPressListener =
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          switch (e.keyCode) {
            case SWT.INSERT:
              addAdmin();
              break;

            case SWT.F2:
              editAdmin();
              break;

            case SWT.DEL:
              delAdmin();
              break;

            default:
              break;
          }
        }
      };

  private MouseAdapter mouseDoubleClickListener =
      new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
          editAdmin();
        }
      };

  private SelectionAdapter addAdminListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          addAdmin();
        }
      };

  private SelectionAdapter editAdminListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          editAdmin();
        }
      };

  private SelectionAdapter delAdminListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          delAdmin();
        }
      };

  private void addAdmin() {

    AdminEditDialog adminEditDialog =
        new AdminEditDialog(Display.getDefault().getActiveShell(), server, clusterId, null);

    if (adminEditDialog.open() == 0) {
      IRegUserInfo userInfo = adminEditDialog.getUserinfo();

      TableItem adminItem = new TableItem(tableAdmins, SWT.NONE);
      adminItem.setText(getTableAdminsItemText(userInfo));
      adminItem.setData(userInfo);
      getShell().pack();
    }
  }

  private void editAdmin() {
    TableItem[] admins = tableAdmins.getSelection();
    if (admins.length == 0) {
      return;
    }

    TableItem adminItem = admins[0];

    IRegUserInfo userInfo = (IRegUserInfo) adminItem.getData();

    AdminEditDialog adminEditDialog =
        new AdminEditDialog(Display.getDefault().getActiveShell(), server, clusterId, userInfo);

    if (adminEditDialog.open() == 0) {
      userInfo = adminEditDialog.getUserinfo();
      adminItem.setText(getTableAdminsItemText(userInfo));
      adminItem.setData(userInfo);
    }
  }

  private void delAdmin() {
    TableItem[] admins = tableAdmins.getSelection();
    if (admins.length == 0) {
      return;
    }

    int answer = Helper.showQuestionBox(Strings.ANSWER_DELETE);
    if (answer == SWT.YES) {
      for (TableItem adminItem : admins) {

        IRegUserInfo userInfo = (IRegUserInfo) adminItem.getData();
        boolean unregOk =
            clusterId == null
                ? server.unregAgentAdmin(userInfo.getName())
                : server.unregClusterAdmin(clusterId, userInfo.getName());

        if (unregOk) {
          adminItem.dispose();
        }
      }
      getShell().pack();
    }
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("TitleList");
    static final String NAME = getString("Name");
    static final String DESCRIPTION = getString("Description");
    static final String PASSWORD_AUTH_ALLOWED = getString("PasswordAuthAllowed");
    static final String SYS_AUTH_ALLOWED = getString("SysAuthAllowed");
    static final String SYS_USERNAME = getString("SysUsername");

    static final String ANSWER_DELETE = getString("AnswerDelete");

    static final String TOOLBAR_ADD = Messages.getString("ViewerArea.ContextMenu.Create");
    static final String TOOLBAR_EDIT = Messages.getString("ViewerArea.ContextMenu.Edit");
    static final String TOOLBAR_DELETE = Messages.getString("ViewerArea.ContextMenu.Delete");
    static final String CONTEXT_MENU_ADD = TOOLBAR_ADD.concat("\tIns");
    static final String CONTEXT_MENU_EDIT = TOOLBAR_EDIT.concat("\tF2");
    static final String CONTEXT_MENU_DELETE = TOOLBAR_DELETE.concat("\tDel");

    static String getString(String key) {
      return Messages.getString("AdminDialog." + key);
    }
  }
}
