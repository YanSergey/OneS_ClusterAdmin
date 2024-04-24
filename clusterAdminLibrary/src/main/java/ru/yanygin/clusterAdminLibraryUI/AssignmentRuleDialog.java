package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.AssignmentRuleInfo;
import com._1c.v8.ibis.admin.IAssignmentRuleInfo;
import java.util.UUID;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import ru.yanygin.clusterAdminLibrary.AssignmentRuleContentProvider;
import ru.yanygin.clusterAdminLibrary.AssignmentRuleLabelProvider;
import ru.yanygin.clusterAdminLibrary.Messages;
import ru.yanygin.clusterAdminLibrary.Server;

/** Диалог редактирования ТНФ. */
public class AssignmentRuleDialog extends Dialog {

  private Server server;
  private UUID clusterId;
  private UUID wsId;
  private IAssignmentRuleInfo ruleInfo;

  private Combo txtObjectType;
  private Combo txtInfoBaseName;
  private Combo txtApplicationExt;

  private Button btnRuleTypeAuto;
  private Button btnRuleTypeAssign;
  private Button btnRuleTypeDoNotAssign;
  private Spinner spinnerPriority;
  private Spinner spinnerNumber;

  /**
   * Создание диалога редактирования ТНФ.
   *
   * @param parentShell - parent shell
   * @param server - Сервер
   * @param clusterId - ID кластера
   * @param ruleInfo - IAssignmentRuleInfo правило ТНФ
   * @wbp.parser.constructor
   */
  public AssignmentRuleDialog(
      Shell parentShell, Server server, UUID clusterId, UUID wsId, IAssignmentRuleInfo ruleInfo) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

    this.server = server;
    this.clusterId = clusterId;
    this.wsId = wsId;
    this.ruleInfo = ruleInfo;
  }

  @Override
  protected void configureShell(Shell newShell) {
    newShell.setMinimumSize(new Point(300, 39));
    super.configureShell(newShell);

    if (ruleInfo == null) {
      newShell.setText(Strings.TITLE_NEW_RULE);
    } else {
      newShell.setText(Strings.TITLE_EDIT_RULE);
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

    Label lblObjectType = new Label(container, SWT.NONE);
    lblObjectType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblObjectType.setText(Strings.OBJECT_TYPE);

    txtObjectType = new Combo(container, SWT.READ_ONLY);
    txtObjectType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblRuleType = new Label(container, SWT.NONE);
    lblRuleType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblRuleType.setText(Strings.RULE_TYPE);

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayout(new RowLayout(SWT.HORIZONTAL));

    btnRuleTypeAuto = new Button(composite, SWT.RADIO);
    btnRuleTypeAuto.setText(AssignmentRuleLabelProvider.RULE_TYPE_AUTO);

    btnRuleTypeAssign = new Button(composite, SWT.RADIO);
    btnRuleTypeAssign.setText(AssignmentRuleLabelProvider.RULE_TYPE_ASSIGN);

    btnRuleTypeDoNotAssign = new Button(composite, SWT.RADIO);
    btnRuleTypeDoNotAssign.setText(AssignmentRuleLabelProvider.RULE_TYPE_DO_NOT_ASSIGN);

    Label lblInfoBaseName = new Label(container, SWT.NONE);
    lblInfoBaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblInfoBaseName.setText(Strings.INFOBASE_NAME);

    txtInfoBaseName = new Combo(container, SWT.BORDER);
    txtInfoBaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label lblApplicationExt = new Label(container, SWT.NONE);
    lblApplicationExt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblApplicationExt.setText(Strings.APPLICATION_EXT);

    txtApplicationExt = new Combo(container, SWT.BORDER);
    GridData gdTxtApplicationExt = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gdTxtApplicationExt.widthHint = 100;
    txtApplicationExt.setLayoutData(gdTxtApplicationExt);

    Label lblNumber = new Label(container, SWT.NONE);
    lblNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblNumber.setText(Strings.NUMBER);

    spinnerNumber = new Spinner(container, SWT.BORDER);
    spinnerNumber.setMinimum(1);
    spinnerNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label lblPriority = new Label(container, SWT.NONE);
    lblPriority.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPriority.setText(Strings.PRIORITY);

    spinnerPriority = new Spinner(container, SWT.BORDER);
    spinnerPriority.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    fillComboFields();
    initValueFields();

    parent.pack();

    return container;
  }

  private void fillComboFields() {
    // Заполнение выпадающих списков значениями для выбора
    AssignmentRuleLabelProvider.getObjectTypes()
        .forEach(
            (k, v) -> {
              txtObjectType.add(v);
              txtObjectType.setData(v, k);
            });

    server.getInfoBasesShort(clusterId).forEach(ib -> txtInfoBaseName.add(ib.getName()));

    String[] appExtValues = AssignmentRuleLabelProvider.getApplicationExtValues();
    for (String v : appExtValues) {
      txtApplicationExt.add(v);
      txtApplicationExt.setData(v);
    }
  }

  private void initValueFields() {
    if (ruleInfo != null) {
      this.txtObjectType.setText(
          AssignmentRuleLabelProvider.getObjectType(ruleInfo.getObjectType()));

      final int ruleType = ruleInfo.getRuleType();
      this.btnRuleTypeAuto.setSelection(ruleType == 1);
      this.btnRuleTypeAssign.setSelection(ruleType == 2);
      this.btnRuleTypeDoNotAssign.setSelection(ruleType == 0);

      this.txtInfoBaseName.setText(ruleInfo.getInfoBaseName());
      this.txtApplicationExt.setText(ruleInfo.getApplicationExt());

      this.spinnerPriority.setSelection(ruleInfo.getPriority());
      this.spinnerNumber.setSelection(AssignmentRuleContentProvider.getRuleNumber(ruleInfo));
    } else {
      this.txtObjectType.setText(AssignmentRuleLabelProvider.getObjectType(""));
      this.btnRuleTypeAuto.setSelection(true);
    }
  }

  private boolean regRule() {

    if (ruleInfo == null) {
      ruleInfo = new AssignmentRuleInfo();
    }

    String objectType = (String) txtObjectType.getData(txtObjectType.getText());
    ruleInfo.setObjectType(objectType);

    final int ruleType;
    if (btnRuleTypeAuto.getSelection()) {
      ruleType = 1;
    } else if (btnRuleTypeAssign.getSelection()) {
      ruleType = 2;
    } else {
      ruleType = 0;
    }
    ruleInfo.setRuleType(ruleType);

    ruleInfo.setInfoBaseName(txtInfoBaseName.getText());
    ruleInfo.setApplicationExt(txtApplicationExt.getText());

    ruleInfo.setPriority(spinnerPriority.getSelection());

    return server.regAssignmentRule(clusterId, wsId, ruleInfo, spinnerNumber.getSelection() - 1);
  }

  /**
   * Получает итоговую информацию о правиле ТНФ.
   *
   * @return IAssignmentRuleInfo правило ТНФ
   */
  public IAssignmentRuleInfo getRuleInfo() {
    return ruleInfo;
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
            if (regRule()) {
              close();
            }
          }
        });

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private static class Strings {

    static final String TITLE_EDIT_RULE = getString("TitleEditRule");
    static final String TITLE_NEW_RULE = getString("TitleNewRule");

    static final String NUMBER = getString("Number");
    static final String OBJECT_TYPE = getString("ObjectType");
    static final String RULE_TYPE = getString("RuleType");
    static final String INFOBASE_NAME = getString("InfoBaseName");
    static final String APPLICATION_EXT = getString("ApplicationExt");
    static final String PRIORITY = getString("Priority");

    static String getString(String key) {
      return Messages.getString("AssignmentRule." + key); // $NON-NLS-1$
    }
  }
}
