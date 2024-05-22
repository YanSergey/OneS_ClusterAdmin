package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IAssignmentRuleInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/** Провайдер требований назначения функциональности. */
public class AssignmentRuleLabelProvider extends ColumnLabelProvider {

  private static final String NUMBER = "AssignmentRule.Number";
  private static final String OBJECT_TYPE = "AssignmentRule.ObjectType";
  private static final String RULE_TYPE = "AssignmentRule.RuleType";
  private static final String INFOBASE_NAME = "AssignmentRule.InfoBaseName";
  private static final String APPLICATION_EXT = "AssignmentRule.ApplicationExt";
  private static final String PRIORITY = "AssignmentRule.Priority";

  private static Config commonConfig = Config.currentConfig;
  private static ColumnProperties columnProperties =
      commonConfig.getColumnsProperties(IAssignmentRuleInfo.class);

  private String columnName;

  public static final String RULE_TYPE_DO_NOT_ASSIGN =
      Messages.getString("AssignmentRule.RuleType.DoNotAssign"); // "Не назначать" = 0
  public static final String RULE_TYPE_AUTO =
      Messages.getString("AssignmentRule.RuleType.Auto"); // "Авто" = 1
  public static final String RULE_TYPE_ASSIGN =
      Messages.getString("AssignmentRule.RuleType.Assign"); // "Назначать" = 2

  static final String[] ruleType = {RULE_TYPE_DO_NOT_ASSIGN, RULE_TYPE_AUTO, RULE_TYPE_ASSIGN};
  static final Map<String, String> objectTypes = fillObjectType();

  public AssignmentRuleLabelProvider(String columnName) {
    this.columnName = columnName;
  }

  private static Map<String, String> fillObjectType() {

    Map<String, String> objectTypes = new HashMap<>();
    objectTypes.put("", Messages.getString("AssignmentRule.ObjectType.ForAll"));

    String[] ruleKeys = {
      "Connection",
      "ClientTestingService",
      "SessionDataService",
      "DataEditLockService",
      "JobService",
      "ExternalDataSourceXMLAService",
      "ExternalSessionManagerService",
      "EventLogService",
      "TimestampService",
      "AuxiliaryService",
      "ExternalDataSourceODBCService",
      "OpenID2ProviderContextService",
      "SessionReuseService",
      "TransactionLockService",
      "LicenseService",
      "FulltextSearchService",
      "SettingsService",
      "NumerationService",
      "DataBaseConfigurationUpdateService",
      "DatabaseTableNumberingService",
      "CounterService",

      // доступны с версии платформы, где добавлен дата акселератор (?)
      "DbCopiesTimeService",
      "GetSessionsService",
      "IntegrationDataService",
      "DbCopiesService",
      "DataAcceleratorService"
    };
    Arrays.sort(ruleKeys);

    for (String ruleKey : ruleKeys) {
      objectTypes.put(ruleKey, Messages.getString("AssignmentRule.ObjectType." + ruleKey));
    }
    return objectTypes;
  }

  /** Возвращает возможные значения доп. параметров. */
  public static String[] getApplicationExtValues() {
    String[] appExtValues = {
      "Designer",
      "1CV8",
      "1CV8C",
      "1CV8CDirect",
      "WebClient",
      "COMConnection",
      "WSConnection",
      "HTTPServiceConnection",
      "ODataConnection",
      "BotConnection",
      "WebServerExtension",
      "MobileClient",
      "AnalyticsSystemClient",
      "AnalyticsSystemQuery",
      "BackgroundJob.ScheduledJob.<Имя объекта конфигурации>",
      "BackgroundJob.CommonModule",
      "BackgroundJob.CommonModule.<Имя модуля>.<Имя метода>",
      "BackgroundJob.FullTextSearchIndexUpdate",
      "BackgroundJob.GenerateReport.<Полное имя объекта конфигурации>",
      "BackgroundJob.InputByString.<Полное имя объекта конфигурации>",
      "BackgroundJob.DynamicListSearch.<Полное имя формы>.<Имя таблицы формы связанной со списком>",
      "BackgroundJob.DBCopiesFilling",
      "BackgroundJob.DBCopiesNotification",
      "BackgroundJob.UpdateDataHistoryImmediatelyAfterWrite",
      "BackgroundJob.AfterWriteDataHistoryVersionsProcessing",
      "BackgroundJob.GlobalSearchFunctionsMenu",
      "BackgroundJob.GlobalSearchFullTextSearch",
      "BackgroundJob.GlobalSearchHelp",
      "BackgroundJob.GlobalSearchAllFunctions",
      "BackgroundJob.GlobalSearch.<имя модуля>.<имя метода>",
      "BackgroundJob.IntegrationServiceReceivedMessagesProcessing.<Полное имя канала сервиса интеграции>",
      "SystemBackgroundJob.DBConfigUpdate",
      "SystemBackgroundJob.RecalcTotals",
      "BackgroundJob.SendIntegrationSystemMessagesQueueProcessing.<полное имя сервиса интеграции>",
      "BackgroundJob.ReceiveIntegrationSystemMessagesQueueProcessing.<полное имя сервиса интеграции>",
      "BackgroundJob.ReceivingIntegrationSystemMessages.<полное имя сервиса интеграции>",
      "BackgroundJob.ReceivedIntegrationSystemMessagesProcessing.<полное имя канала сервиса интеграции>",
      "BackgroundJob.StandaloneExchange"
    };
    return appExtValues;
  }

  private static void putObjectType(Map<String, String> objectTypes, String key) {
    objectTypes.put(key, Messages.getString("AssignmentRule.ObjectType." + key));
  }

  public static Map<String, String> getObjectTypes() {
    return objectTypes;
  }

  public static String getObjectType(String key) {
    return objectTypes.getOrDefault(key, "unknown");
  }

  @Override
  public String getText(Object element) {
    if (!(element instanceof IAssignmentRuleInfo)) {
      return null;
    }
    return String.valueOf(getValue(element));
  }

  /**
   * Возвращает наименование правила ТНФ.
   *
   * @param element - элемент, для которого нужно вернуть наименование ТНФ
   */
  public Object getValue(Object element) {
    if (!(element instanceof IAssignmentRuleInfo)) {
      return null;
    }
    IAssignmentRuleInfo rule = (IAssignmentRuleInfo) element;

    switch (columnName) {
      case NUMBER:
        return AssignmentRuleContentProvider.getRuleNumber(rule);
      case OBJECT_TYPE:
        return objectTypes.getOrDefault(rule.getObjectType(), "unknown");
      case RULE_TYPE:
        return ruleType[rule.getRuleType()];
      case INFOBASE_NAME:
        return rule.getInfoBaseName();
      case APPLICATION_EXT:
        return rule.getApplicationExt();
      case PRIORITY:
        return rule.getPriority();
      default:
        return "UnknownColumn";
    }
  }

  /** Инициализация имен колонок. */
  protected static void initColumnsName() {
    columnProperties.addColumnsInMap(
        OBJECT_TYPE, RULE_TYPE, INFOBASE_NAME, APPLICATION_EXT, NUMBER, PRIORITY);
  }

  private static class Strings {

    static String getString(String key) {
      return Messages.getString("AssignmentRule." + key); // $NON-NLS-1$
    }
  }
}
