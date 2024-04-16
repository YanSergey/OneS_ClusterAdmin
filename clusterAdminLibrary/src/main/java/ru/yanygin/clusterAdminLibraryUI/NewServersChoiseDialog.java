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

/** Форма для выбора, какие найденные сервера добавлять в список. */
public class NewServersChoiseDialog extends Dialog {
  private Table table;
  private List<String> newServers;

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

    TableColumn tblcolumn = new TableColumn(table, SWT.LEFT);
    tblcolumn.setResizable(false);
    tblcolumn.setWidth(200);
    tblcolumn.setText(Strings.SERVER_ADDRESS);

    newServers = Helper.findNewServers();

    table.removeAll();
    newServers.forEach(
        (serv) -> {
          TableItem item = new TableItem(table, SWT.NONE);
          item.setText(serv);
        });

    tblcolumn.pack();

    return container;
  }

  private void saveNewServers() {
    TableItem[] items = table.getItems();

    for (TableItem tableItem : items) {
      if (!tableItem.getChecked()) {
        newServers.remove(tableItem.getText());
      }
    }
  }

  /**
   * Получение списка серверов, выбранных пользователем.
   *
   * @return список новых серверов
   */
  public List<String> getNewServers() {
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
    static final String TITLE_WINDOW = getString("TitleDialog");
    static final String SERVER_ADDRESS = getString("ServerAddress");

    static String getString(String key) {
      return Messages.getString("NewServersDialog." + key); // $NON-NLS-1$
    }
  }
}
