package ru.yanygin.clusterAdminLibraryUI;

import java.util.List;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.Server;

/** Форма для выбора, какие найденные сервера добавлять в список. */
public class NewServersChoiseDialog extends Dialog {
  private Table table;
  private List<Server> newServers;

  /**
   * Create the dialog.
   *
   * @param parentShell - parent shell
   */
  public NewServersChoiseDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
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
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;

    table = new Table(container, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    TableColumn columnHost = new TableColumn(table, SWT.LEFT);
    columnHost.setResizable(false);
    columnHost.setWidth(200);
    columnHost.setText(Strings.HOST);

    TableColumn columnAgentPort = new TableColumn(table, SWT.LEFT);
    columnAgentPort.setResizable(false);
    columnAgentPort.setWidth(100);
    columnAgentPort.setText(Strings.PORT);

    newServers = Helper.findNewServers();

    table.removeAll();
    newServers.forEach(
        (serv) -> {
          TableItem item = new TableItem(table, SWT.NONE);
          item.setText(0, serv.getAgentHost());
          item.setText(1, serv.getAgentPortAsString());
          item.setData(serv);
        });

    columnHost.pack();
    columnAgentPort.pack();

    return container;
  }

  private void saveNewServers() {
    TableItem[] items = table.getItems();

    for (TableItem tableItem : items) {
      if (!tableItem.getChecked()) {
        newServers.remove(tableItem.getData());
      }
    }
  }

  /**
   * Получение списка серверов, выбранных пользователем.
   *
   * @return список новых серверов
   */
  public List<Server> getNewServers() {
    return newServers;
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
            saveNewServers();
            close();
          }
        });
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {
    static final String TITLE_WINDOW = Messages.getString("NewServersDialog.TitleDialog");
    static final String HOST = getString("Host");
    static final String PORT = getString("Port");

    static String getString(String key) {
      return Messages.getString("ServerDialog." + key); // $NON-NLS-1$
    }
  }
}
