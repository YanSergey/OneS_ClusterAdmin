package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.UserPassPair;

/** Диалог параметров фоновой задачи. */
public class BackgroundTaskParams extends Dialog {

  private Map<String, String> params;
  private String title;
  private Table tableParams;
  private Map<String, String> infobasesCredentials = new HashMap<>();
  private List<String> usernames = new ArrayList<>();
//  private List<String> usernamesForPass = new ArrayList<>();

  private String currentUsernameValue = "";
  // private int passwordFieldIndex;

  /**
   * Создание диалога ввода имени пользователя и пароля.
   *
   * @param parentShell - parent shell
   * @param params - текущие имя пользователя и пароль
   * @param title - заголовок окна аутентификации
   * @param infobasesCredentials - данные доступа к инфобазам
   */
  public BackgroundTaskParams(
      Shell parentShell,
      Map<String, String> params,
      String title,
      List<UserPassPair> infobasesCredentials) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.params = params;
    this.title = title;

    infobasesCredentials.forEach(
        up -> {
          this.infobasesCredentials.put(up.getUsername(), up.getPassword());
          this.usernames.add(up.getUsername());
//          this.usernamesForPass.add("***(" + up.getUsername() + ")");
        });
  }

  @Override
  protected void configureShell(Shell newShell) {
    // newShell.setMinimumSize(new Point(300, 39));
    super.configureShell(newShell);
    newShell.setText(Strings.TITLE_WINDOW + this.title);
  }

  /**
   * Create contents of the dialog.
   *
   * @param parent - parent composite
   */
  @Override
  protected Control createDialogArea(Composite parent) {

    Composite container = (Composite) super.createDialogArea(parent);

    TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
    tableParams = tableViewer.getTable();
    tableParams.setLinesVisible(true);
    tableParams.setHeaderVisible(true);
    tableParams.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    tableViewer.setContentProvider(new ParamsContentProvider());

    TableViewerColumn tableViewerColumnParamKey = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn clmnParamKey = tableViewerColumnParamKey.getColumn();
    clmnParamKey.setMoveable(true);
    clmnParamKey.setWidth(120);
    clmnParamKey.setText("Param name");
    // tableViewerColumnParamKey.setEditingSupport(new TableViewerEditingSupport(tableViewer));
    tableViewerColumnParamKey.setLabelProvider(
        new ColumnLabelProvider() {
          @Override
          public String getText(Object element) {
            String[] p = (String[]) element;
            return p[0];
          }
        });

    TableViewerColumn tableViewerColumnParamValue = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn clmnParamValue = tableViewerColumnParamValue.getColumn();
    clmnParamValue.setMoveable(true);
    clmnParamValue.setWidth(280);
    clmnParamValue.setText("Value");
    tableViewerColumnParamValue.setEditingSupport(new TableViewerEditingSupport(tableViewer));
    tableViewerColumnParamValue.setLabelProvider(
        new ColumnLabelProvider() {
          @Override
          public String getText(Object element) {
            String[] p = (String[]) element;
            return p[1];
          }
        });
    tableViewer.setInput(params);

    return container;
  }

  private boolean extractVariablesFromControls() {
    boolean foundEmptyParams = false;
    // params.clear();
    for (TableItem tableItem : tableParams.getItems()) {
      if (tableItem.getText(1).isBlank()) {
        tableItem.setBackground(Helper.getPinkColor());
        foundEmptyParams = true;
      } else {
        tableItem.setBackground(Helper.getWhiteColor());
        // params.put(tableItem.getText(0), tableItem.getText(1));
      }
    }

    return !foundEmptyParams;
  }

  /**
   * Возвращает заполненные пользователем параметры запуска задачи.
   *
   * @return параметры запуска задачи
   */
  public Map<String, String> getParams() {
    return params;
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
            if (extractVariablesFromControls()) {
              close();
            }
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  protected boolean isUsernameParam(String e) {
    return e.equals("username");
  }

  protected boolean isPasswordParam(String e) {
    return e.equals("password");
  }

  class TableViewerEditingSupport extends EditingSupport {

    private TableViewer viewer;
    private TextCellEditor textEditor;
    private ComboBoxCellEditor usernameEditor;
//    private ComboBoxCellEditor passwordEditor;

    public TableViewerEditingSupport(TableViewer viewer) {
      super(viewer);
      textEditor = new TextCellEditor(viewer.getTable(), SWT.BORDER);

      usernameEditor =
          new ComboBoxCellEditor(
              viewer.getTable(), usernames.toArray(new String[0]), SWT.SINGLE | SWT.BORDER);

//      passwordEditor =
//          new ComboBoxCellEditor(
//              viewer.getTable(), usernamesForPass.toArray(new String[0]), SWT.SINGLE | SWT.BORDER);

      this.viewer = viewer;
    }

    @Override
    protected boolean canEdit(Object element) {
      String paramKey = ((String[]) element)[0];
      if (isPasswordParam(paramKey)) {
        return !currentUsernameValue.isBlank();
      }
      return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
      String[] e = (String[]) element;

      if (isUsernameParam(e[0])) {
        return usernameEditor;
//      } else if (isPasswordParam(e[0])) {
//        return passwordEditor;
      } else {
        return textEditor;
      }
    }

    @Override
    protected Object getValue(Object element) {
      String[] e = (String[]) element;

      if (isUsernameParam(e[0])) {
        return usernames.indexOf(e[1]);
//      } else if (isPasswordParam(e[0])) {
//        // return params.get("password");
//        // return usernamesForPass.indexOf(e[1]);
//        return usernames.indexOf(currentUsernameValue);
      } else {
        return e[1];
      }
    }

    // @Override
    protected void setValue1(Object element, Object value) {
      String[] e = (String[]) element;
      String stringValue = "";

      if (isUsernameParam(e[0]) && (Integer) value >= 0) {
        // это выбор логина из списка
        stringValue = usernames.get((Integer) value);
        currentUsernameValue = stringValue;
        // установка пароля
        params.put("password", infobasesCredentials.getOrDefault(stringValue, ""));

      } else if (isUsernameParam(e[0]) && (Integer) value == -1) {
        // это ввод нового логина вручную
        stringValue = ((CCombo) usernameEditor.getControl()).getText();
        currentUsernameValue = stringValue;
        if (!stringValue.isBlank()) {
          usernames.add(stringValue);
          usernameEditor.setItems(usernames.toArray(new String[0]));

          // сброс пароля
          params.put("password", "");
        }
        
      } else if (isPasswordParam(e[0]) && (Integer) value >= 0) {
        // это выбор пароля (по списку логинов)
        String credId = usernames.get((Integer) value);
        stringValue = infobasesCredentials.getOrDefault(credId, "");

      } else if (isPasswordParam(e[0]) && (Integer) value == -1) {
        // это ввод нового пароля вручную
        stringValue = ((CCombo) usernameEditor.getControl()).getText();
        infobasesCredentials.put(currentUsernameValue, stringValue);

      } else {
        stringValue = (String) value;
      }
      params.put(e[0], stringValue);
      // e[1] = stringValue;
      viewer.refresh(true);
    }

    @Override
    protected void setValue(Object element, Object value) {
      String paramKey = ((String[]) element)[0];
      String newParamValue = "";
      Integer valueIndex = null;

      // текстовое поле
      if (value instanceof String) {
        newParamValue = (String) value;
      }

      // поле с выпадающим списком
      if (value instanceof Integer) {
        valueIndex = (Integer) value;

        if (isUsernameParam(paramKey) && valueIndex >= 0) {
          // выбор логина из списка
          newParamValue = usernames.get(valueIndex);
          currentUsernameValue = newParamValue;
          // установка пароля
          params.put("password", infobasesCredentials.getOrDefault(newParamValue, ""));

        } else if (isUsernameParam(paramKey) && valueIndex == -1) {
          // ввод нового логина вручную
          newParamValue = ((CCombo) usernameEditor.getControl()).getText();
          currentUsernameValue = newParamValue;
          if (!newParamValue.isBlank()) {
            usernames.add(newParamValue);
            usernameEditor.setItems(usernames.toArray(new String[0]));
            // сброс пароля
            params.put("password", "");
          }

        } else if (isPasswordParam(paramKey) && valueIndex >= 0) {
          // выбор пароля (по списку логинов)
          String credId = usernames.get(valueIndex);
          newParamValue = infobasesCredentials.getOrDefault(credId, "");

        } else if (isPasswordParam(paramKey) && valueIndex == -1) {
          // ввод нового пароля вручную
          newParamValue = ((CCombo) usernameEditor.getControl()).getText();
          if (!currentUsernameValue.isBlank()) {
            infobasesCredentials.put(currentUsernameValue, newParamValue);
          }
        }
      }

      params.put(paramKey, newParamValue);
      viewer.refresh(true);
    }
  }

  class ParamsContentProvider implements IStructuredContentProvider {

    /**
     * Returns the elements in the input, which must be either an array or a <code>Collection</code>
     * .
     */
    @Override
    public Object[] getElements(Object inputElement) {
      if (inputElement instanceof Map<?, ?>) {


        List<String[]> collection = new ArrayList<>();
        ((Map<String, String>) inputElement)
            .forEach(
                (k, v) -> {
                  if (isPasswordParam(k)) {
                    // passwordParam = new String[] {k, v};
                    collection.add(new String[] {k, v.isBlank() ? "" : "***"});
                    // passwordFieldIndex = collection.size() - 1;
                  } else {
                    collection.add(new String[] {k, v});
                  }
                });

        return collection.toArray();
      }
      return new Object[0];
    }
  }

  private static class Strings {

    static final String TITLE_WINDOW = getString("Title");
    static final String USERNAME = getString("Username"); //$NON-NLS-1$
    static final String PASSWORD = getString("Password"); //$NON-NLS-1$

    static String getString(String key) {
      return Messages.getString("BackgroundTaskParams." + key); // $NON-NLS-1$
    }
  }
}
