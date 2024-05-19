package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IAssignmentRuleInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.IStructuredContentProvider;

/** Контент-провайдер для заполнения таблицы с ТНФ. */
public class AssignmentRuleContentProvider implements IStructuredContentProvider {

  static Map<IAssignmentRuleInfo, Integer> ruleNumbers = new HashMap<>();

  /**
   * Returns the elements in the input, which must be either an array or a <code>Collection</code> .
   */
  @Override
  public Object[] getElements(Object inputElement) {
    List<IAssignmentRuleInfo> listTnf = (List<IAssignmentRuleInfo>) inputElement;
    setRuleNumbers(listTnf);
    return listTnf.toArray();
  }

  private static void setRuleNumbers(List<IAssignmentRuleInfo> rules) {
    ruleNumbers.clear();
    for (int i = 0; i < rules.size(); i++) {
      ruleNumbers.put(rules.get(i), i + 1);
    }
  }

  public static int getRuleNumber(IAssignmentRuleInfo rule) {
    return ruleNumbers.getOrDefault(rule, 0);
  }
}
