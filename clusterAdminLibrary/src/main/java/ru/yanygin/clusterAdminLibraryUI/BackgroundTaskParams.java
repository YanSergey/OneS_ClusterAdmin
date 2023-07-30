package ru.yanygin.clusterAdminLibraryUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import ru.yanygin.clusterAdminLibrary.UserPassPair;

/** Диалог параметров фоновой задачи. */
public class BackgroundTaskParams extends Dialog {

  private Map<String, String> params;
  private String title;
  private Table tableParams;
  private Map<String, String> infobasesCredentials = new HashMap<>();
  private List<String> usernames = new ArrayList<>();
  private String currentUsernameValue = "";
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
        });
  }

  @Override
  protected void configureShell(Shell newShell) {
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
    clmnParamKey.setText(Strings.TITLE_PARAMNAME);
    // tableViewerColumnParamKey.setEditingSupport(new
    // TableViewerEditingSupport(tableViewer));
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
    clmnParamValue.setText(Strings.TITLE_PARAMVALUE);
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

  /**
   * Возвращает заполненные пользователем параметры запуска задачи.
   *
   * @return параметры запуска задачи
   */
  public Map<String, String> getParams() {
    return params;
  }

  /**
   * Возвращает заполненные пользователем параметры запуска задачи.
   *
   * @return параметры запуска задачи
   */
  public void checkUsernameParam() {
    String user = params.get("v8username");
    //	    if (Objects.nonNull(user) && user.isBlank()) {
    //	      params.remove("v8username");
    //		  params.remove("v8password");
    //		}
    if (Objects.nonNull(user)) {
      params.put("v8username", "\"" + params.get("v8username") + "\"");
      params.put("v8password", "\"" + params.get("v8password") + "\"");
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
            checkUsernameParam();
            close();
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  protected boolean isUsernameParam(String e) {
    return e.equals("v8username");
  }

  protected boolean isPasswordParam(String e) {
    return e.equals("v8password");
  }

  class TableViewerEditingSupport extends EditingSupport {

    private TableViewer viewer;
    private TextCellEditor textEditor;
    private ComboBoxCellEditor usernameEditor;

    public TableViewerEditingSupport(TableViewer viewer) {
      super(viewer);
      textEditor = new TextCellEditor(viewer.getTable(), SWT.BORDER);

      usernameEditor =
          new ComboBoxCellEditor(
              viewer.getTable(), usernames.toArray(new String[0]), SWT.SINGLE | SWT.BORDER);

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
      } else {
        return textEditor;
      }
    }

    @Override
    protected Object getValue(Object element) {
      String[] e = (String[]) element;

      if (isUsernameParam(e[0])) {
        return usernames.indexOf(e[1]);
      } else {
        return e[1];
      }
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
          params.put("v8password", infobasesCredentials.getOrDefault(newParamValue, ""));

        } else if (isUsernameParam(paramKey) && valueIndex == -1) {
          // ввод нового логина вручную
          newParamValue = ((CCombo) usernameEditor.getControl()).getText();
          currentUsernameValue = newParamValue;
          if (!newParamValue.isBlank()) {
            usernames.add(newParamValue);
            usernameEditor.setItems(usernames.toArray(new String[0]));
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
                    // После редактирования пароль прячем за звездочками
                    collection.add(new String[] {k, v.isBlank() ? "" : "***"});
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
    static final String TITLE_PARAMNAME = getString("ParamName");
    static final String TITLE_PARAMVALUE = getString("ParamValue");

    static String getString(String key) {
      return Messages.getString("BackgroundTaskParams." + key);
    }
  }
}