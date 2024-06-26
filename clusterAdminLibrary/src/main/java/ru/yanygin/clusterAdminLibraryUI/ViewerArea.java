package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IAssignmentRuleInfo;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibrary.AssignmentRuleContentProvider;
import ru.yanygin.clusterAdminLibrary.AssignmentRuleLabelProvider;
import ru.yanygin.clusterAdminLibrary.BackgroundTask;
import ru.yanygin.clusterAdminLibrary.BackgroundTask.V8ActionVariant;
import ru.yanygin.clusterAdminLibrary.BaseInfoExtended;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.ColumnProperties;
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibrary.ConnectionInfoExtended;
import ru.yanygin.clusterAdminLibrary.Helper;
import ru.yanygin.clusterAdminLibrary.InfoBaseInfoShortExt;
import ru.yanygin.clusterAdminLibrary.InfoBaseInfoShortExt.InfobasesSortDirection;
import ru.yanygin.clusterAdminLibrary.LockInfoExtended;
import ru.yanygin.clusterAdminLibrary.Server;
import ru.yanygin.clusterAdminLibrary.SessionInfoExtended;
import ru.yanygin.clusterAdminLibrary.WorkingProcessInfoExtended;
import ru.yanygin.clusterAdminLibrary.WorkingServerInfoExtended;

/** Основная рабочая область приложения. */
public class ViewerArea extends Composite {

  static final Logger LOGGER = LoggerFactory.getLogger("ClusterProvider"); //$NON-NLS-1$

  static final String SERVER_INFO = "ServerInfo"; //$NON-NLS-1$
  static final String ID_DATA_KEY = "ID_DATA"; //$NON-NLS-1$

  public static final String EXTENDED_INFO = "ExtendedInfo"; //$NON-NLS-1$

  static final Color deletedItemColor = new Color(150, 0, 0);

  //  Image serverAdd48Icon;
  //  Image clusterAdd48Icon;
  //  Image infobaseAdd48Icon;

  //  Image serverIcon;
  //  Image serverConnectedIcon;
  //  Image serverDisconnectIcon;
  //  Image serverConnectingIcon;
  Image workingServerIcon;
  Image infobaseIcon;
  Image infobasesIcon;
  Image clusterIcon;

  Image workingProcessesIcon;
  Image workingProcessIcon;
  Image connectActionIcon;
  Image disconnectActionIcon;
  Image lockUsersIcon;

  Image addIcon16;
  Image editIcon16;
  Image deleteIcon16;
  Image viewIcon16;
  Image updateIcon16;

  Image addIcon24;
  Image editIcon24;
  Image deleteIcon24;
  Image updateIcon24;
  Image updateAutoIcon24;

  Image favoritesIcon;
  Image sortIcon;
  Image moveUpIcon;
  Image moveDownIcon;
  Image watchSession;
  Image roubleIcon;

  Tree serversTree;
  TreeItem currentTreeItem;
  TreeItemType currentHighlightingType;
  Object currentHighlightingData;

  Menu serverMenu;
  Menu clusterMenu;
  Menu workingServerMenu;
  Menu infobaseNodeMenu;
  Menu infobaseMenu;

  Table tableSessions;
  Table tableConnections;
  Table tableLocks;
  Table tableWorkingProcesses;
  Table tableWorkingServers;
  Table currentTable;

  Table tableTasks;
  TabItem tabTask;
  Text tableTaskLog;

  ToolItem addToolbarItem;
  ToolItem editToolbarItem;
  ToolItem deleteToolbarItem;
  TableViewer tnfTableViewer;

  TreeColumn columnServer;

  FontData systemFontData = getDisplay().getSystemFont().getFontData()[0];
  Font fontNormal =
      new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.NORMAL);
  Font fontBold =
      new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.BOLD);
  Font fontItalic =
      new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.ITALIC);

  TableItem lastSelectItem = null;
  int lastSelectColumn;

  public enum TreeItemType {
    SERVER,
    CLUSTER,
    INFOBASE_NODE,
    INFOBASE,
    WORKINGPROCESS_NODE,
    WORKINGPROCESS,
    WORKINGSERVER_NODE,
    WORKINGSERVER
  }

  ClusterProvider clusterProvider;
  Config config;

  Map<TreeItemType, SelectionAdapter> toolbarCreateListeners = new EnumMap<>(TreeItemType.class);
  Map<TreeItemType, SelectionAdapter> toolbarEditListeners = new EnumMap<>(TreeItemType.class);
  Map<TreeItemType, SelectionAdapter> toolbarDeleteListeners = new EnumMap<>(TreeItemType.class);
  Map<TreeItemType, Menu> serversTreeContextMenus = new EnumMap<>(TreeItemType.class);

  // List<File> userScripts = new ArrayList<>();
  Timer taskTimer;
  Timer serverConnectionTimer;
  Timer updateListTimer;

  List<TreeItem> waitingConnectServers = new ArrayList<>();

  RefreshTablesSelectionListener refreshTablesListener;
  //  UserScriptRunner userScriptRunner;

  // @Slf4j
  /**
   * Конструктор области приложения.
   *
   * @param parent - parent composite
   * @param style - style
   * @param menu - menu
   * @param toolBar - toolBar
   * @param clusterProvider - clusterProvider
   * @param config - путь к файлу конфигурации
   */
  public ViewerArea(
      Composite parent,
      int style,
      Menu menu,
      ToolBar toolBar,
      ClusterProvider clusterProvider,
      Config config) {
    super(parent, style);

    this.clusterProvider = clusterProvider;
    this.config = config;
    this.setLayout(new FillLayout(SWT.HORIZONTAL));

    initIcon();
    initMainMenu(menu);
    initToolbar(toolBar);

    // toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT); // Для отладки
    // toolBar.setBounds(0, 0, 500, 23); // Для отладки
    
    BaseInfoExtended.init();
    
    TabFolder mainTabFolder = new TabFolder(this, SWT.BOTTOM);
    initServersTab(mainTabFolder);
    initTaskTab(mainTabFolder);

    runAutonnectAllServers();
  }

  private void initServersTab(TabFolder mainTabFolder) {
    TabItem tabServers = new TabItem(mainTabFolder, SWT.NONE);
    tabServers.setText(Strings.MENU_SERVERS);

    // инициализация таблиц управления серверами
    SashForm sashServers = new SashForm(mainTabFolder, SWT.NONE);
    tabServers.setControl(sashServers);

    initServersTree(sashServers);

    TabFolder tabFolder = new TabFolder(sashServers, SWT.NONE);
    tabFolder.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent evt) {
            TabItem currentTab = tabFolder.getSelection()[0];
            currentTable = getTable(currentTab);
            refreshCurrentList();
          }
        });

    // Таблица сеансов
    TabItem tabSessions = new TabItem(tabFolder, SWT.NONE);
    tableSessions = initTable(tabFolder, SessionInfoExtended.class);
    tabSessions.setControl(tableSessions);
    BaseInfoExtended.linkTabItem(SessionInfoExtended.class, tabSessions);
    tableSessions.setFocus();

    // Таблица соединений
    TabItem tabConnections = new TabItem(tabFolder, SWT.NONE);
    tableConnections = initTable(tabFolder, ConnectionInfoExtended.class);
    tabConnections.setControl(tableConnections);
    BaseInfoExtended.linkTabItem(ConnectionInfoExtended.class, tabConnections);

    // Таблица блокировок
    TabItem tabLocks = new TabItem(tabFolder, SWT.NONE);
    tableLocks = initTable(tabFolder, LockInfoExtended.class);
    tabLocks.setControl(tableLocks);
    BaseInfoExtended.linkTabItem(LockInfoExtended.class, tabLocks);

    // Таблица рабочих процессов
    TabItem tabWorkingProcesses = new TabItem(tabFolder, SWT.NONE);
    tableWorkingProcesses = initTable(tabFolder, WorkingProcessInfoExtended.class);
    tabWorkingProcesses.setControl(tableWorkingProcesses);
    BaseInfoExtended.linkTabItem(WorkingProcessInfoExtended.class, tabWorkingProcesses);

    // Таблица рабочих серверов
    TabItem tabWorkingServers = new TabItem(tabFolder, SWT.NONE);

    SashForm sashWorkingServers = new SashForm(tabFolder, SWT.VERTICAL);

    tableWorkingServers = initTable(sashWorkingServers, WorkingServerInfoExtended.class);

    initAssRuleTable(sashWorkingServers);
    BaseInfoExtended.linkTabItem(WorkingServerInfoExtended.class, tabWorkingServers);

    tabWorkingServers.setControl(sashWorkingServers);
    sashWorkingServers.setWeights(5, 10); // Пропорции областей

    initTableContextMenu(tnfTableViewer.getTable(), IAssignmentRuleInfo.class);

    initToolbarListeners();
    clearTabs(false); // TODO тут вроде не нужно
    BaseInfoExtended.resetTabsTextCount();
    setEnableToolbarItems();

    // Пропорции областей
    sashServers.setWeights(3, 10);
  }

  private void initTaskTab(TabFolder mainTabFolder) {
    tabTask = new TabItem(mainTabFolder, SWT.NONE);
    tabTask.setText("Task");

    SashForm sashTasks = new SashForm(mainTabFolder, SWT.NONE);
    tabTask.setControl(sashTasks);

    tableTasks = new Table(sashTasks, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tableTasks.setHeaderVisible(true);
    tableTasks.setLinesVisible(true);
    tableTasks.addMouseListener(tableTaskMouseClickListener);

    addTaskTableColumn(tableTasks, "Task", 200);
    addTaskTableColumn(tableTasks, "State", 50);
    addTaskTableColumn(tableTasks, "Start", 120, SWT.RIGHT);
    addTaskTableColumn(tableTasks, "End", 120);
    addTaskTableColumn(tableTasks, "Params", 120);

    tableTaskLog = new Text(sashTasks, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
    tableTaskLog.addListener(
        SWT.Modify,
        new Listener() {
          public void handleEvent(Event e) {
            tableTaskLog.setTopIndex(tableTaskLog.getLineCount() - 1);
          }
        });
    // Пропорции областей
    sashTasks.setWeights(7, 10);
  }

  private void initIcon() {
    LOGGER.debug("Start init icon"); //$NON-NLS-1$

    //    serverAdd48Icon = Helper.getImage("server_add_48.png"); //$NON-NLS-1$
    //    clusterAdd48Icon = Helper.getImage("cluster_add_48.png"); //$NON-NLS-1$
    //    infobaseAdd48Icon = Helper.getImage("infobase_add_48.png"); //$NON-NLS-1$

    //    serverIcon = Helper.getImage("server_24.png"); //$NON-NLS-1$
    //    serverConnectedIcon = Helper.getImage("server_connected_24.png"); //$NON-NLS-1$
    //    serverDisconnectIcon = Helper.getImage("server_disconnect_24.png"); //$NON-NLS-1$
    //    serverConnectingIcon = Helper.getImage("server_connecting_24.png"); //$NON-NLS-1$

    workingServerIcon = Helper.getImage("working_server_24.png"); // $NON-NLS-1$
    // infobaseIcon = Helper.getImage("infobase_24.png"); //$NON-NLS-1$
    infobasesIcon = Helper.getImage("infobases_24.png"); //$NON-NLS-1$
    clusterIcon = Helper.getImage("cluster_24.png"); //$NON-NLS-1$

    workingProcessesIcon = Helper.getImage("wps.png"); //$NON-NLS-1$
    workingProcessIcon = Helper.getImage("wp.png"); //$NON-NLS-1$

    connectActionIcon = Helper.getImage("connect_action_24.png"); //$NON-NLS-1$
    disconnectActionIcon = Helper.getImage("disconnect_action_24.png"); //$NON-NLS-1$
    lockUsersIcon = Helper.getImage("lock_users_16.png"); // $NON-NLS-1$

    editIcon16 = Helper.getImage("edit_16.png"); // $NON-NLS-1$
    addIcon16 = Helper.getImage("add_16.png"); // $NON-NLS-1$
    deleteIcon16 = Helper.getImage("delete_16.png"); // $NON-NLS-1$
    viewIcon16 = Helper.getImage("view_16.png"); // $NON-NLS-1$
    updateIcon16 = Helper.getImage("update_16.png"); // $NON-NLS-1$

    editIcon24 = Helper.getImage("edit_24.png"); // $NON-NLS-1$
    addIcon24 = Helper.getImage("add_24.png"); // $NON-NLS-1$
    deleteIcon24 = Helper.getImage("delete_24.png"); // $NON-NLS-1$
    updateIcon24 = Helper.getImage("update_24.png"); // $NON-NLS-1$
    updateAutoIcon24 = Helper.getImage("updateAuto_24.png"); // $NON-NLS-1$

    favoritesIcon = Helper.getImage("favorites.png"); //$NON-NLS-1$
    sortIcon = Helper.getImage("sort.png"); //$NON-NLS-1$
    moveUpIcon = Helper.getImage("move_up.png"); //$NON-NLS-1$
    moveDownIcon = Helper.getImage("move_down.png"); //$NON-NLS-1$

    watchSession = Helper.getImage("watch.png"); // $NON-NLS-1$
    roubleIcon = Helper.getImage("Rouble.png"); // $NON-NLS-1$

    LOGGER.debug("Icon init succesfully"); //$NON-NLS-1$
  }

  private void initToolbar(ToolBar toolBar) {
    // ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);

    // addItemInToolbar(toolBar, Strings.MENU_FIND_SERVERS, serverIcon, findNewServersListener)
    //  .setEnabled(false);

    toolBar.setSize(-1, 48);

    addToolbarItem =
        addItemInToolbar(toolBar, Strings.CONTEXT_MENU_ADD, addIcon24, toolbarListener);
    editToolbarItem =
        addItemInToolbar(toolBar, Strings.CONTEXT_MENU_EDIT, editIcon24, toolbarListener);
    deleteToolbarItem =
        addItemInToolbar(toolBar, Strings.CONTEXT_MENU_DELETE, deleteIcon24, toolbarListener);

    new ToolItem(toolBar, SWT.SEPARATOR);

    // addItemInToolbar(toolBar, Strings.CONTEXT_MENU_UPDATE, updateIcon, updateTablesListener);

    ToolItem refreshToolbarItem = new ToolItem(toolBar, SWT.DROP_DOWN);
    refreshToolbarItem.setText(Strings.CONTEXT_MENU_UPDATE);
    refreshToolbarItem.setImage(updateIcon24);

    refreshTablesListener = new RefreshTablesSelectionListener(refreshToolbarItem);

    toolBar.pack();
  }

  private void initMainMenu(Menu mainMenu) {

    if (mainMenu == null) {
      return;
    }

    Menu serversGroup = addItemGroupInMenu(mainMenu, Strings.MENU_SERVERS, null);
    addItemInMenu(serversGroup, Strings.MENU_FIND_SERVERS, null, findNewServersListener);
    addItemInMenu(serversGroup, Strings.MENU_CONNECT_ALL_SERVERS, null, connectAllServersListener);
    addItemInMenu(
        serversGroup, Strings.MENU_DISCONNECT_ALL_SERVERS, null, disconnectAllServersListener);

    Menu serviceGroup = addItemGroupInMenu(mainMenu, Strings.MENU_SERVICE, null);
    addItemInMenu(serviceGroup, Strings.MENU_OPEN_SETTINGS, null, openSettingsListener);
    addItemInMenu(serviceGroup, Strings.MENU_ABOUT, null, showAboutDialogListener);

    SelectionAdapter goToLinkListener =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            Program.launch(Helper.BOOSTY_LINK);
          }
        };
    addItemInMenu(serviceGroup, Strings.MENU_DONATE, roubleIcon, goToLinkListener);
  }

  private void initServersTree(SashForm sashForm) {

    serversTree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
    serversTree.setHeaderVisible(true);

    serversTree.addMouseListener(treeItemMouseClickListener);
    serversTree.addListener(
        SWT.MeasureItem,
        new Listener() {
          @Override
          public void handleEvent(Event event) {
            // отключение разворачивания дерева при даблклике
          }
        });

    initServersTreeContextMenu();

    columnServer = new TreeColumn(serversTree, SWT.LEFT);
    columnServer.setText(Strings.COLUMN_SERVER);
    columnServer.setWidth(350);

    // Заполнение списка серверов
    config
        .getServers()
        .forEach(
            (serverKey, server) -> {
              addServerItemInServersTree(server);
            });
    columnServer.pack();
  }

  private void initServersTreeContextMenu() {

    initServerMenu();
    initClusterMenu();
    initWorkingServerMenu();
    initInfobaseNodeMenu();
    initInfobaseMenu();

    // соответствие контекстных меню дерева серверов
    serversTreeContextMenus.put(TreeItemType.SERVER, serverMenu);
    serversTreeContextMenus.put(TreeItemType.CLUSTER, clusterMenu);
    serversTreeContextMenus.put(TreeItemType.INFOBASE_NODE, infobaseNodeMenu);
    serversTreeContextMenus.put(TreeItemType.INFOBASE, infobaseMenu);
    serversTreeContextMenus.put(TreeItemType.WORKINGSERVER, workingServerMenu);

    // set active menu
    serversTree.setMenu(serverMenu);
  }

  private void initServerMenu() {

    serverMenu = new Menu(serversTree);

    // установка активности элементов контекстного меню
    serverMenu.addListener(SWT.Show, setActiveConnectActionListener);

    MenuItem menuItemConnectServer =
        addItemInMenu(
            serverMenu,
            Strings.CONTEXT_MENU_CONNECT_TO_SERVER,
            connectActionIcon,
            connectToServerListener);
    menuItemConnectServer.setEnabled(false);
    menuItemConnectServer.setData("connectItem", true);

    MenuItem menuItemDisconnectServer =
        addItemInMenu(
            serverMenu,
            Strings.CONTEXT_MENU_DISCONNECT_OF_SERVER,
            disconnectActionIcon,
            disconnectFromServerListener);
    menuItemDisconnectServer.setEnabled(false);
    menuItemDisconnectServer.setData("disconnectItem", true);

    MenuItem menuItemShowConnectionError =
        addItemInMenu(
            serverMenu,
            Strings.CONTEXT_MENU_SHOW_CONNECTION_ERROR,
            null,
            showServerConnectionErrorListener);
    menuItemShowConnectionError.setEnabled(false);
    menuItemShowConnectionError.setData("connectionErrorItem", true);

    addMenuSeparator(serverMenu);

    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_ADD, addIcon16, addServerListener);
    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_EDIT, editIcon16, editServerListener);
    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_UPDATE, updateIcon16, updateServerListener);

    addMenuSeparator(serverMenu);
    MenuItem adminsItem =
        addItemInMenu(serverMenu, Strings.CONTEXT_MENU_ADMINS, null, editAdminsListener);
    adminsItem.setData("disconnectItem", true);

    addMenuSeparator(serverMenu);

    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_MOVE_UP, moveUpIcon, serversMoveUpListener);
    addItemInMenu(
        serverMenu, Strings.CONTEXT_MENU_MOVE_DOWN, moveDownIcon, serversMoveDownListener);
    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_ORGANIZE_SERVERS, sortIcon, serversSortListener);

    addMenuSeparator(serverMenu);

    addItemInMenu(serverMenu, Strings.CONTEXT_MENU_DELETE, deleteIcon16, deleteServerListener);

  }

  private void initClusterMenu() {

    clusterMenu = new Menu(serversTree);

    addItemInMenu(
        clusterMenu, Strings.CONTEXT_MENU_CREATE_CLUSTER, addIcon16, createClusterListener);
    addItemInMenu(clusterMenu, Strings.CONTEXT_MENU_EDIT_CLUSTER, editIcon16, editClusterListener);
    addItemInMenu(clusterMenu, Strings.CONTEXT_MENU_UPDATE, updateIcon16, updateClusterListener);

    addMenuSeparator(clusterMenu);
    addItemInMenu(clusterMenu, Strings.CONTEXT_MENU_ADMINS, null, editAdminsListener);

    addMenuSeparator(clusterMenu);
    addItemInMenu(
        clusterMenu, Strings.CONTEXT_MENU_DELETE_CLUSTER, deleteIcon16, deleteClusterListener);

    addMenuSeparator(clusterMenu);
    addItemInMenu(
        clusterMenu, Strings.CONTEXT_MENU_RESTART_PROCESSES, null, restartWorkingProcessesListener);

    addMenuSeparator(clusterMenu);
    addItemInMenu(
        clusterMenu, Strings.CONTEXT_MENU_APPLY_PARTIAL_RULE, null, applyAssignmentRuleListener, 0);
    addItemInMenu(
        clusterMenu, Strings.CONTEXT_MENU_APPLY_FULL_RULE, null, applyAssignmentRuleListener, 1);
  }

  private void initWorkingServerMenu() {

    workingServerMenu = new Menu(serversTree);

    addItemInMenu(
        workingServerMenu,
        Strings.CONTEXT_MENU_CREATE_WORKING_SERVER,
        addIcon16,
        createWorkingServerListenerInTree);

    addItemInMenu(
        workingServerMenu,
        Strings.CONTEXT_MENU_EDIT_WORKING_SERVER,
        editIcon16,
        editWorkingServerListenerInTree);
  }

  private void initInfobaseNodeMenu() {

    infobaseNodeMenu = new Menu(serversTree);

    addItemInMenu(
        infobaseNodeMenu, Strings.CONTEXT_MENU_CREATE_INFOBASE, addIcon16, createInfobaseListener);

    addItemInMenu(
        infobaseNodeMenu,
        Strings.CONTEXT_MENU_UPDATE_INFOBASES,
        updateIcon16,
        updateInfobasesListener);

    // группа вложенного меню
    Menu subMenuSortInfobases =
        addItemGroupInMenu(infobaseNodeMenu, Strings.CONTEXT_MENU_ORDER_INFOBASES_BY, sortIcon);

    InfobasesSortDirection infobasesSortDirection = config.getInfobasesSortDirection();

    addRadioItemInMenu(
        subMenuSortInfobases,
        Strings.CONTEXT_MENU_ORDER_INFOBASES_BYDEFAULT,
        sortInfobasesListener,
        InfobasesSortDirection.DISABLE,
        infobasesSortDirection.equals(InfobasesSortDirection.DISABLE));

    addRadioItemInMenu(
        subMenuSortInfobases,
        Strings.CONTEXT_MENU_ORDER_INFOBASES_BYNAME,
        sortInfobasesListener,
        InfobasesSortDirection.BY_NAME,
        infobasesSortDirection.equals(InfobasesSortDirection.BY_NAME));

    addRadioItemInMenu(
        subMenuSortInfobases,
        Strings.CONTEXT_MENU_ORDER_INFOBASES_BYFAFORITES_ANDNAME,
        sortInfobasesListener,
        InfobasesSortDirection.BY_FAVORITES_AND_NAME,
        infobasesSortDirection.equals(InfobasesSortDirection.BY_FAVORITES_AND_NAME));
  }

  private void initInfobaseMenu() {

    infobaseMenu = new Menu(serversTree);
    infobaseMenu.addListener(SWT.Show, setActiveInfobaseFavoritesActionListener);

    addItemInMenu(
        infobaseMenu, Strings.CONTEXT_MENU_COPY_INFOBASE, addIcon16, createInfobaseListener);

    addItemInMenu(
        infobaseMenu, Strings.CONTEXT_MENU_EDIT_INFOBASE, editIcon16, editInfobaseListener);

    addItemInMenu(
        infobaseMenu, Strings.CONTEXT_MENU_DELETE_INFOBASE, deleteIcon16, deleteInfobaseListener);

    addMenuSeparator(infobaseMenu);

    MenuItem favoritesItem =
        addItemInMenu(
            infobaseMenu,
            Strings.CONTEXT_MENU_ADD_IN_FAVORITES,
            favoritesIcon,
            addInfobaseToFavoritesListener);
    favoritesItem.setData("favoritesItem", true);

    Menu subMenuSessionManagement =
        addItemGroupInMenu(infobaseMenu, Strings.CONTEXT_MENU_SESSION_MANAGE, lockUsersIcon);

    addItemInMenu(
        subMenuSessionManagement, Strings.CONTEXT_MENU_LOCK_SESSIONS_NOW, null, lockUsersListener);

    addItemInMenu(
        subMenuSessionManagement,
        Strings.CONTEXT_MENU_TERMINATE_ALL_SESSIONS,
        null,
        terminateAllSessionsListener);

    addItemInMenu(
        subMenuSessionManagement,
        Strings.CONTEXT_MENU_TERMINATE_USERS_SESSIONS,
        null,
        terminateUsersSessionsListener);

    new UserScriptRunner(infobaseMenu);

    // Меню действий с базой
    addMenuSeparator(infobaseMenu);

    Menu subMenuInfobaseActions = addItemGroupInMenu(infobaseMenu, Strings.CONTEXT_MENU_INFOBASE_ACTIONS, null);

    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_RUN_ENTERPRISE, null, launchV8ActionListener, V8ActionVariant.RUN_ENTERPRISE);
    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_RUN_DESIGNER, null, launchV8ActionListener, V8ActionVariant.RUN_DESIGNER);

    addMenuSeparator(subMenuInfobaseActions);
    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_DUMP_CF, null, launchV8ActionListener, V8ActionVariant.DUMP_CF);
    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_LOAD_CF, null, launchV8ActionListener, V8ActionVariant.LOAD_CF);

    addMenuSeparator(subMenuInfobaseActions);
    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_DUMP_DT, null, launchV8ActionListener, V8ActionVariant.DUMP_DT);
    addItemInMenu(subMenuInfobaseActions, Strings.CONTEXT_MENU_LOAD_DT, null, launchV8ActionListener, V8ActionVariant.LOAD_DT);
  }

  private Table initTable(Composite composite, Class<?> clazz) {

    int style =
        clazz == SessionInfoExtended.class
            ? SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK
            : SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI;

    Table table = new Table(composite, style);
    // tabItem.setControl(table);
    table.setData(clazz);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    table.addKeyListener(tableKeyPressedListener);
    table.addMouseListener(tablesMouseClickListener);
    if (clazz == SessionInfoExtended.class) {
      table.addListener(SWT.Selection, switchWatchingListener);
    }
    table.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            currentTable = table;
            // currentTable = (Table) e.widget;
            LOGGER.debug("currentTable = {}", currentTable.getData().toString());
          }
        });

    final ColumnProperties columnProperties = getColumnProperties(clazz);

    String[] columnNameList = columnProperties.getColumnsDescription();
    for (String columnName : columnNameList) {
      addTableColumn(table, columnName, columnProperties);
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && table.getColumnCount() == columnOrder.length) {
      table.setColumnOrder(columnOrder);
    }

    if (clazz != IAssignmentRuleInfo.class) {
      initTableContextMenu(table, clazz);
    }

    // BaseInfoExtended.linkTabItem(clazz, tabItem);
    return table;
  }

  private void initAssRuleTable(SashForm sashWorkingServers) { // таблица с ТНФ
    tnfTableViewer =
        new TableViewer(sashWorkingServers, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tnfTableViewer.setContentProvider(new AssignmentRuleContentProvider());

    tnfTableViewer.setComparator(
        new ViewerComparator() {
          public int compare(Viewer viewer, Object e1, Object e2) {

            Table table = ((TableViewer) viewer).getTable();
            TableColumn sortColumn = table.getSortColumn();
            if (sortColumn == null) {
              return 0;
            }
            int numColumn = (int) sortColumn.getData();
            TableViewerColumn tableViewerColumn =
                (TableViewerColumn) sortColumn.getData("org.eclipse.jface.columnViewer");
            //TableViewerColumn.class.toString()

            AssignmentRuleLabelProvider labelProvider =
                (AssignmentRuleLabelProvider)
                    tableViewerColumn.getViewer().getLabelProvider(numColumn);

            final Object first;
            final Object second;
            switch (table.getSortDirection()) {
              case SWT.UP:
                first = labelProvider.getValue(e1);
                second = labelProvider.getValue(e2);
                break;

              case SWT.DOWN:
                first = labelProvider.getValue(e2);
                second = labelProvider.getValue(e1);
                break;

              case SWT.NONE:
              default:
                return 0;
            }
            if (first instanceof Integer) {
              return Integer.compare((int) first, (int) second);
            }
            if (first instanceof String) {
              return ((String) first).compareTo((String) second);
            }
            return 0;
          }
        });

    Table table = tnfTableViewer.getTable();

    table.setData(IAssignmentRuleInfo.class);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    // tableTnf.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    table.addKeyListener(tableKeyPressedListener);
    table.addMouseListener(tablesMouseClickListener);
    table.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            currentTable = table;
            // currentTable = (Table) e.widget;
            LOGGER.debug("currentTable = {}", currentTable.getData().toString());
          }
        });

    final ColumnProperties columnProperties = getColumnProperties(IAssignmentRuleInfo.class);

    String[] columnNameList = columnProperties.getColumnsName(); // TODO
    String[] columnDescList = columnProperties.getColumnsDescription();

    for (int i = 0; i < columnNameList.length; i++) {
      addTableViewerColumn(tnfTableViewer, columnNameList[i], columnDescList[i], columnProperties);
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && table.getColumnCount() == columnOrder.length) {
      table.setColumnOrder(columnOrder);
    }

  }

  private void initTableContextMenu(Table table, Class<?> clazz) {

    // Table table = getTable(tab);

    Menu tableMenu = new Menu(table);
    table.setMenu(tableMenu);

    // установка активности элементов контекстного меню

    Listener setActiveContextMenuListener =
        new Listener() {
          @Override
          public void handleEvent(Event event) {

            TableItem[] items = currentTable.getSelection();
            boolean selected = (items.length != 0);

            MenuItem[] menuItems = ((Menu) event.widget).getItems();

            for (MenuItem menuItem : menuItems) {
              if (!menuItem.getText().equals(Strings.CONTEXT_MENU_UPDATE_F5)
                  && !menuItem.getText().equals(Strings.CONTEXT_MENU_ADD_HOTKEY)) {
                menuItem.setEnabled(selected);
              }
            }
          }
        };
    tableMenu.addListener(SWT.Show, setActiveContextMenuListener);

    final SelectionAdapter refreshListener =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            currentTable = table;
            refreshCurrentList();
          }
        };
    addItemInMenu(tableMenu, Strings.CONTEXT_MENU_UPDATE_F5, updateIcon16, refreshListener);

    addItemInMenu(tableMenu, Strings.CONTEXT_MENU_COPY_CELL, null, copyCellValueInTablesListener);

    // у соединений еще есть прерывание серверного вызова (с какой то версии платформы)
    if (clazz == WorkingServerInfoExtended.class || clazz == IAssignmentRuleInfo.class) {

      final SelectionAdapter addListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              addItemInTable(table);
            }
          };
      final SelectionAdapter editListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              editItemInTable(items[0]);
            }
          };
      final SelectionAdapter deleteListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              deleteItemsFromTable(items);
            }
          };

      addMenuSeparator(tableMenu);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_ADD_HOTKEY, addIcon16, addListener);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_EDIT_HOTKEY, editIcon16, editListener);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_DELETE_HOTKEY, deleteIcon16, deleteListener);

    } else if (clazz == SessionInfoExtended.class) {
      // просмотр
      final SelectionAdapter viewListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              viewItemInTable(items[0]);
            }
          };
      // TODO для сеансов есть форма, для соединений и раб.процессов - ее нет
      // попробовать сделать генерируюмую форму со списком полей
      addMenuSeparator(tableMenu);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_VIEW_HOTKEY, viewIcon16, viewListener);
    }

    if (clazz == SessionInfoExtended.class) {
    addItemInMenu(
        tableMenu, Strings.CONTEXT_MENU_WATCH_SESSION, watchSession, switchWatchingMenuListener);
    }

    if (clazz == SessionInfoExtended.class || clazz == ConnectionInfoExtended.class) {
      final SelectionAdapter deleteListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              deleteItemsFromTable(items);
            }
          };
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_DELETE_HOTKEY, deleteIcon16, deleteListener);
    }

    if (clazz == IAssignmentRuleInfo.class) {
      // пункты увеличения и уменьшения порядка правила ТНФ
      final SelectionAdapter increaseListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              changeAssignmentRuleNumber(items[0], true);
            }
          };
      final SelectionAdapter reduseListener =
          new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              TableItem[] items = table.getSelection();
              if (items.length == 0) {
                return;
              }
              changeAssignmentRuleNumber(items[0], false);
            }
          };
      addMenuSeparator(tableMenu);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_MOVE_UP, moveUpIcon, increaseListener);
      addItemInMenu(tableMenu, Strings.CONTEXT_MENU_MOVE_DOWN, moveDownIcon, reduseListener);
    }
  }

  private void initToolbarListeners() {

    // обработчики кнопки тулбара "Добавить"
    toolbarCreateListeners.put(TreeItemType.SERVER, addServerListener);
    toolbarCreateListeners.put(TreeItemType.CLUSTER, createClusterListener);
    toolbarCreateListeners.put(TreeItemType.INFOBASE_NODE, createInfobaseListener);
    toolbarCreateListeners.put(TreeItemType.INFOBASE, createInfobaseListener);
    toolbarCreateListeners.put(TreeItemType.WORKINGSERVER_NODE, createWorkingServerListenerInTree);
    toolbarCreateListeners.put(TreeItemType.WORKINGSERVER, createWorkingServerListenerInTree);

    // обработчики кнопки тулбара "Изменить"
    toolbarEditListeners.put(TreeItemType.SERVER, editServerListener);
    toolbarEditListeners.put(TreeItemType.CLUSTER, editClusterListener);
    toolbarEditListeners.put(TreeItemType.INFOBASE, editInfobaseListener);
    toolbarEditListeners.put(TreeItemType.WORKINGSERVER, editWorkingServerListenerInTree);

    // обработчики кнопки тулбара "Удалить"
    toolbarDeleteListeners.put(TreeItemType.SERVER, deleteServerListener);
    toolbarDeleteListeners.put(TreeItemType.CLUSTER, deleteClusterListener);
    toolbarDeleteListeners.put(TreeItemType.INFOBASE, deleteInfobaseListener);

  }

  private ToolItem addItemInToolbar(
      ToolBar parent, String text, Image icon, SelectionAdapter listener) {

    ToolItem toolItem = new ToolItem(parent, SWT.PUSH | SWT.WRAP);
    toolItem.setText(text);
    toolItem.setImage(icon);
    toolItem.addSelectionListener(listener);

    return toolItem;
  }

  private void addMenuSeparator(Menu menu) {
    new MenuItem(menu, SWT.SEPARATOR);
  }

  private Menu addItemGroupInMenu(Menu parent, String text, Image icon) {
    // TODO rename createCascadeMenuGroup

    Menu subMenu = new Menu(parent);

    MenuItem groupMenu = new MenuItem(parent, SWT.CASCADE);
    groupMenu.setText(text);
    groupMenu.setImage(icon);
    groupMenu.setMenu(subMenu);

    return subMenu;
  }

  private MenuItem addItemInMenu(Menu parent, String text, Image icon, SelectionAdapter listener) {

    MenuItem menuItem = new MenuItem(parent, SWT.NONE); // SWT.BOLD
    menuItem.setText(text);
    menuItem.setImage(icon);
    menuItem.addSelectionListener(listener);

    return menuItem;
  }

  private MenuItem addItemInMenu(
      Menu parent, String text, Image icon, SelectionAdapter listener, Object data) {

    MenuItem menuItem = new MenuItem(parent, SWT.NONE); // SWT.BOLD
    menuItem.setText(text);
    menuItem.setImage(icon);
    menuItem.addSelectionListener(listener);
    menuItem.setData(data);

    return menuItem;
  }

  private MenuItem addRadioItemInMenu(
      Menu parent, String text, SelectionAdapter listener, Object data, boolean selected) {

    MenuItem menuItem = new MenuItem(parent, SWT.RADIO);
    menuItem.setText(text);
    menuItem.addSelectionListener(listener);
    menuItem.setData(data);
    menuItem.setSelection(selected);

    return menuItem;
  }

  private TreeItem addServerItemInServersTree(Server server) {
    return addServerItemInServersTree(server, -1);
  }

  private TreeItem addServerItemInServersTree(Server server, int index) {

    return addItemInNode(
        serversTree,
        index,
        server.getTreeTitle(),
        TreeItemType.SERVER,
        SERVER_INFO,
        server,
        server.getTreeImage());
  }

  private TreeItem addClusterItemInServersTree(TreeItem serverItem, IClusterInfo clusterInfo) {

    Server server = getServer(serverItem);
    if (server == null) {
      return null;
    }

    return addItemInNode(
        serverItem,
        server.getClusterTreeTitle(clusterInfo),
        TreeItemType.CLUSTER,
        ID_DATA_KEY,
        clusterInfo.getClusterId(),
        clusterIcon);
  }

  private TreeItem addInfobasesNode(TreeItem nodeItem, UUID clusterId, String title) {
    return addItemInNode(
        nodeItem, title, TreeItemType.INFOBASE_NODE, ID_DATA_KEY, clusterId, infobasesIcon);
  }

  private TreeItem addInfobaseItemInNode(TreeItem infobaseNode, InfoBaseInfoShortExt infoBaseInfo) {
    return addInfobaseItemInNode(infobaseNode, infoBaseInfo, -1);
  }

  private TreeItem addInfobaseItemInNode(
      TreeItem infobaseNode, InfoBaseInfoShortExt infoBaseInfo, int index) {

    String infobaseTitle = infoBaseInfo.getInfobaseDescription();

    TreeItem item =
        addItemInNode(
            infobaseNode,
            index,
            infobaseTitle,
            TreeItemType.INFOBASE,
            ID_DATA_KEY,
            infoBaseInfo.getInfoBaseId(),
            infoBaseInfo.getIcon());

    item.setData(InfoBaseInfoShortExt.class.getSimpleName(), infoBaseInfo);

    return item;
  }

  private TreeItem addWorkingProcessNode(TreeItem nodeItem, UUID clusterId, String title) {
    return addItemInNode(
        nodeItem,
        title,
        TreeItemType.WORKINGPROCESS_NODE,
        ID_DATA_KEY,
        clusterId,
        workingProcessesIcon);
  }

  private TreeItem addWorkingProcessItemInNode(TreeItem wpNodeItem, IWorkingProcessInfo wpInfo) {

    Server server = getServer(wpNodeItem);
    if (server == null) {
      LOGGER.error("Error get Server from wpNodeItem"); //$NON-NLS-1$
      return null;
    }

    return addItemInNode(
        wpNodeItem,
        server.getWorkingProcessTreeTitle(wpInfo),
        TreeItemType.WORKINGPROCESS,
        ID_DATA_KEY,
        wpInfo.getWorkingProcessId(),
        workingProcessIcon);
  }

  private TreeItem addWorkingServerNode(TreeItem nodeItem, UUID clusterId, String title) {
    return addItemInNode(
        nodeItem,
        title,
        TreeItemType.WORKINGSERVER_NODE,
        ID_DATA_KEY,
        clusterId,
        workingServerIcon);
  }

  private TreeItem addWorkingServerItemInNode(TreeItem wsNodeItem, IWorkingServerInfo wsInfo) {

    Server server = getServer(wsNodeItem);
    if (server == null) {
      LOGGER.error("Error get Server from wsNodeItem"); //$NON-NLS-1$
      return null;
    }

    return addItemInNode(
        wsNodeItem,
        server.getWorkingServerTreeTitle(wsInfo),
        TreeItemType.WORKINGSERVER,
        ID_DATA_KEY,
        wsInfo.getWorkingServerId(),
        workingServerIcon);
  }

  private TreeItem addItemInNode(
      Tree nodeItem,
      int index,
      String title,
      TreeItemType itemType,
      String idDataKey,
      Object idDataValue,
      Image icon) {

    TreeItem item = null;
    if (index == -1) {
      item = new TreeItem(nodeItem, SWT.NONE);
    } else {
      item = new TreeItem(nodeItem, SWT.NONE, index);
    }
    setItemProperties(item, title, itemType, idDataKey, idDataValue, icon);

    return item;
  }

  private TreeItem addItemInNode(
      TreeItem nodeItem,
      int index,
      String title,
      TreeItemType itemType,
      String idDataKey,
      Object idDataValue,
      Image icon) {

    TreeItem item = null;
    if (index == -1) {
      item = new TreeItem(nodeItem, SWT.NONE);
    } else {
      item = new TreeItem(nodeItem, SWT.NONE, index);
    }
    setItemProperties(item, title, itemType, idDataKey, idDataValue, icon);

    return item;
  }

  private TreeItem addItemInNode(
      TreeItem nodeItem,
      String title,
      TreeItemType itemType,
      String idDataKey,
      Object idDataValue,
      Image icon) {

    TreeItem item = new TreeItem(nodeItem, SWT.NONE);
    setItemProperties(item, title, itemType, idDataKey, idDataValue, icon);

    return item;
  }

  private void setItemProperties(
      TreeItem item,
      String title,
      TreeItemType itemType,
      String idDataKey,
      Object idDataValue,
      Image icon) {

    item.setText(new String[] {title});
    item.setData("Type", itemType); //$NON-NLS-1$
    item.setData(idDataKey, idDataValue);
    item.setImage(icon);
    item.setChecked(false);

    if (itemType.equals(currentHighlightingType)
        && Objects.equals(idDataValue, currentHighlightingData)) {
      highlightTreeItem(item);
    }
  }

  private void addTableColumn(Table table, String text, ColumnProperties columnProperties) {

    var newColumn = new TableColumn(table, SWT.NONE);
    newColumn.setText(text);
    newColumn.setMoveable(true);
    newColumn.setAlignment(SWT.RIGHT);

    if (columnProperties == null) {
      newColumn.setWidth(100);
      return;
    }

    final int[] columnWidth = columnProperties.getWidth();
    final boolean[] columnVisible = columnProperties.getVisible();

    int numOfColumn = table.getColumnCount() - 1;
    newColumn.setData(numOfColumn);
    if (numOfColumn == columnProperties.getSortColumn()) {
      table.setSortColumn(newColumn);
      table.setSortDirection(columnProperties.getSortDirectionSwt());
    }

    if (columnVisible != null && columnVisible[numOfColumn]) {
      newColumn.setResizable(true);
      newColumn.setWidth(columnWidth[numOfColumn] == 0 ? 100 : columnWidth[numOfColumn]);
    } else {
      newColumn.setResizable(false);
      newColumn.setWidth(0);
    }

    newColumn.addListener(SWT.Move, columnMoveListener);
    newColumn.addListener(SWT.Resize, columnResizeListener);
    newColumn.addListener(SWT.Selection, columnSortListener);
  }

  private void addTaskTableColumn(Table table, String text, int width) {
    addTaskTableColumn(table, text, width, SWT.LEFT);
  }

  private void addTaskTableColumn(Table table, String text, int width, int alignment) {

    var newColumn = new TableColumn(table, SWT.NONE);
    newColumn.setText(text);
    newColumn.setMoveable(false);
    newColumn.setAlignment(alignment);
    newColumn.setResizable(true);
    newColumn.setWidth(width);
  }

  private Server getCurrentServer() {
    return getServer(currentTreeItem);
  }

  private UUID getCurrentClusterId() {
    return getClusterId(currentTreeItem);
  }

  private UUID getCurrentWorkingProcessId() {
    return getWorkingProcessId(currentTreeItem);
  }

  private UUID getCurrentWorkingServerId() {
    return getWorkingServerId(currentTreeItem);
  }

  private Server getServer(TreeItem item) {
    if (item == null || item.isDisposed()) {
      LOGGER.error("Error get Server from serverItem"); // $NON-NLS-1$
      return null;
    }

    if (getTreeItemType(item) == TreeItemType.SERVER) {
      return (Server) item.getData(SERVER_INFO);
    }

    TreeItem parentItem = item.getParentItem();
    while (parentItem != null) {

      if (getTreeItemType(parentItem) == TreeItemType.SERVER) {
        return (Server) parentItem.getData(SERVER_INFO);
      } else {
        parentItem = parentItem.getParentItem();
      }
    }
    LOGGER.error("Error get Server from serverItem"); // $NON-NLS-1$
    return null;
  }

  private UUID getClusterId(TreeItem item) {
    return getTreeItemId(TreeItemType.CLUSTER, item);
  }

  private UUID getWorkingProcessId(TreeItem item) {
    return getTreeItemId(TreeItemType.WORKINGPROCESS, item);
  }

  private UUID getWorkingServerId(TreeItem item) {
    return getTreeItemId(TreeItemType.WORKINGSERVER, item);
  }

  private UUID getInfobaseId(TreeItem item) {
    if (getTreeItemType(item) == TreeItemType.INFOBASE) {
      return (UUID) item.getData(ID_DATA_KEY);
    } else {
      return null;
    }
  }

  private InfoBaseInfoShortExt getInfobaseShortInfoExt(TreeItem item) {
    if (getTreeItemType(item) == TreeItemType.INFOBASE) {
      return (InfoBaseInfoShortExt) item.getData(InfoBaseInfoShortExt.class.getSimpleName());
    } else {
      return null;
    }
  }

  private UUID getTreeItemId(TreeItemType itemType, TreeItem item) {
    if (item == null) {
      return null;
    }

    if (getTreeItemType(item) == itemType) {
      return (UUID) item.getData(ID_DATA_KEY);
    }

    TreeItem parentItem = item.getParentItem();
    while (parentItem != null) {

      if (getTreeItemType(parentItem) == itemType) {
        return (UUID) parentItem.getData(ID_DATA_KEY);
      } else {
        parentItem = parentItem.getParentItem();
      }
    }
    return null;
  }

  private TreeItemType getTreeItemType(TreeItem item) {
    // TODO переделать в equals?
    return (TreeItemType) item.getData("Type"); //$NON-NLS-1$
  }

  private void runAutonnectAllServers() {

    TreeItem[] serversItem = serversTree.getItems();

    for (TreeItem serverItem : serversItem) {
      Server server = getServer(serverItem);
      if (server != null && !server.isConnected() && server.getAutoconnect()) {
        connectServerItem(serverItem, true);
      }
    }
  }

  private void connectServerItem(TreeItem serverItem, boolean silentMode) {

    Server server = getServer(serverItem);
    if (server == null) {
      return;
    }

    Thread thread =
        new Thread(
            () -> {
              server.connectToServer(false, silentMode);
            });

    thread.start();

    waitingConnectServers.add(serverItem);
    if (serverConnectionTimer == null) {
      serverConnectionTimer = new Timer(true);
      serverConnectionTimer.schedule(new ServerConnectionCheckerTimer(), 1000, 1000);
    }
    server.updateTreeItemState(serverItem);
  }

  private void disconnectServerItem(TreeItem serverItem) {
    Server server = getServer(serverItem);
    if (server == null) {
      return;
    }
    server.disconnectFromAgent();
    server.updateTreeItemState(serverItem);
    saveCurrentSelectedData(serverItem);
    Arrays.stream(serverItem.getItems()).forEach(Widget::dispose);
  }

  private void updateClustersInTree(TreeItem serverItem) {

    Server server = getServer(serverItem);
    if (server == null) {
      return;
    }

    // у отключенного сервера удаляем все дочерние элементы
    if (!server.isConnected()) {
      Arrays.stream(serverItem.getItems()).forEach(Widget::dispose);
      return;
    }

    List<IClusterInfo> clusters = server.getClusters();

    // удаление несуществующих элементов
    Arrays.stream(serverItem.getItems())
        .forEach(
            clusterItem -> {
              List<IClusterInfo> foundCluster =
                  clusters.stream()
                      .filter(c -> c.getClusterId().equals(getClusterId(clusterItem)))
                      .collect(Collectors.toList());
              if (foundCluster.isEmpty()) {
                clusterItem.dispose();
              }
            });

    // добавление новых элементов или обновление существующих
    clusters.forEach(
        clusterInfo -> {
          TreeItem currentClusterItem =
              Arrays.stream(serverItem.getItems())
                  .filter(cl -> getClusterId(cl).equals(clusterInfo.getClusterId()))
                  .findFirst()
                  .orElseGet(() -> addClusterItemInServersTree(serverItem, clusterInfo));

          // Обновление дерева кластера
          updateNodesOfCluster(currentClusterItem, server);
        });

    // Разворачиваем дерево, если включена настройка
    serverItem.setExpanded(config.isExpandServersTree());
    columnServer.pack();
  }

  private void updateNodesOfCluster(TreeItem clusterItem, Server server) {

    updateInfobasesOfCluster(clusterItem, server);
    updateWorkingProcessesInCluster(clusterItem, server);
    updateWorkingServersInCluster(clusterItem, server);

    clusterItem.setExpanded(config.isExpandClustersTree());
  }

  private void updateInfobasesOfCluster(TreeItem clusterItem, Server server) {
    // Refactor this method to reduce its Cognitive Complexity from 29 to the 15 allowed.

    UUID clusterId = getClusterId(clusterItem);
    server.provideSavedInfobasesCredentialsToCluster(clusterId);
    List<InfoBaseInfoShortExt> infoBases = server.getInfoBasesShort(clusterId);
    var infobasesNodeTitle = String.format(Strings.TREE_INFOBASES_COUNT, infoBases.size());

    // поиск или создание узла инфобаз
    TreeItem infobasesNode =
        Arrays.stream(clusterItem.getItems())
            .filter(node -> node.getData("Type").equals(TreeItemType.INFOBASE_NODE))
            .findFirst()
            .orElseGet(() -> addInfobasesNode(clusterItem, clusterId, infobasesNodeTitle));

    infobasesNode.setText(new String[] {infobasesNodeTitle}); // TODO надо обновить только у старых

    // если у кластера нет инфобаз, то у узла удаляем все элементы
    if (infoBases.isEmpty()) {
      Arrays.stream(infobasesNode.getItems()).forEach(Widget::dispose);
      return;
    }

    // TODO с учетом сортировки, может быть дешевле просто удалить все и заново заполнить
    // удаление несуществующих элементов
    Arrays.stream(infobasesNode.getItems())
        .forEach(
            infobaseItem -> {
              List<InfoBaseInfoShortExt> foundItems =
                  infoBases.stream()
                      .filter(ib -> ib.getInfoBaseId().equals(getInfobaseId(infobaseItem)))
                      .collect(Collectors.toList());
              if (foundItems.isEmpty()) {
                infobaseItem.dispose();
              }
            });

    // добавление новых элементов
    infoBases.forEach(
        infoBaseInfo -> {
          TreeItem infobaseItem =
              Arrays.stream(infobasesNode.getItems())
                  .filter(ibItem -> infoBaseInfo.getInfoBaseId().equals(getInfobaseId(ibItem)))
                  .findFirst()
                  .orElseGet(() -> addInfobaseItemInNode(infobasesNode, infoBaseInfo));

          InfoBaseInfoShortExt infoBaseInfoFromItem = getInfobaseShortInfoExt(infobaseItem);

          // обновление порядкового номера базы из кластера
          // имеет смысл делать только для старых элементов
          if (infoBaseInfo.getIndex() != infoBaseInfoFromItem.getIndex()) {
            infoBaseInfoFromItem.setIndex(infoBaseInfo.getIndex());
          }
          // обновление представления инфобазы
          // имеет смысл делать только для старых элементов
          if (!infobaseItem.getText().equals(infoBaseInfoFromItem.getInfobaseDescription())) {
            infobaseItem.setText(infoBaseInfoFromItem.getInfobaseDescription());
          }
        });

    infobasesNode.setExpanded(config.isExpandInfobasesTree());
  }

  private void updateWorkingProcessesInCluster(TreeItem clusterItem, Server server) {

    UUID clusterId = getClusterId(clusterItem);
    List<IWorkingProcessInfo> workingProcesses = server.getWorkingProcesses(clusterId);
    String wpNodeTitle = WorkingProcessInfoExtended.getNodeTitle(workingProcesses.size());

    // поиск или создание узла рабочих процессов
    TreeItem workingProcessesNode =
        Arrays.stream(clusterItem.getItems())
            .filter(node -> node.getData("Type").equals(TreeItemType.WORKINGPROCESS_NODE))
            .findFirst()
            .orElseGet(() -> addWorkingProcessNode(clusterItem, clusterId, wpNodeTitle));

    if (!config.isShowWorkingProcessesTree()) {
      // TODO его в таком случае и создавать не надо шагом выше
      if (workingProcessesNode != null) {
        workingProcessesNode.dispose();
      }
      return;
    }

    workingProcessesNode.setText(new String[] {wpNodeTitle});

    // если у кластера нет рабочих процессов, то у узла удаляем все элементы
    if (workingProcesses.isEmpty()) {
      Arrays.stream(workingProcessesNode.getItems()).forEach(Widget::dispose);
      return;
    }

    // удаление несуществующих элементов
    Arrays.stream(workingProcessesNode.getItems())
        .forEach(
            workingProcessItem -> {
              UUID currentWorkingProcessId = getWorkingProcessId(workingProcessItem);
              List<IWorkingProcessInfo> foundItems =
                  workingProcesses.stream()
                      .filter(wp -> wp.getWorkingProcessId().equals(currentWorkingProcessId))
                      .collect(Collectors.toList());
              if (foundItems.isEmpty()) {
                workingProcessItem.dispose();
              }
            });

    // добавление новых элементов
    workingProcesses.forEach(
        wpInfo -> {
          TreeItem workingProcessItem =
              Arrays.stream(workingProcessesNode.getItems())
                  .filter(
                      wpItem -> wpInfo.getWorkingProcessId().equals(getWorkingProcessId(wpItem)))
                  .findFirst()
                  .orElseGet(() -> addWorkingProcessItemInNode(workingProcessesNode, wpInfo));
        });
  }

  private void updateWorkingServersInCluster(TreeItem clusterItem, Server server) {

    UUID clusterId = getClusterId(clusterItem);
    List<IWorkingServerInfo> workingServers = server.getWorkingServers(clusterId);
    String wsNodeTitle = WorkingServerInfoExtended.getNodeTitle(workingServers.size());

    // поиск или создание узла рабочих процессов
    TreeItem workingServersNode =
        Arrays.stream(clusterItem.getItems())
            .filter(node -> node.getData("Type").equals(TreeItemType.WORKINGSERVER_NODE))
            .findFirst()
            .orElseGet(() -> addWorkingServerNode(clusterItem, clusterId, wsNodeTitle));

    if (!config.isShowWorkingServersTree()) {
      // TODO его в таком случае и создавать не надо шагом выше
      if (workingServersNode != null) {
        workingServersNode.dispose();
      }
      return;
    }

    workingServersNode.setText(new String[] {wsNodeTitle});

    // если у кластера нет рабочих серверов, то у узла удаляем все элементы
    if (workingServers.isEmpty()) {
      Arrays.stream(workingServersNode.getItems()).forEach(Widget::dispose);
      return;
    }

    // удаление несуществующих элементов
    Arrays.stream(workingServersNode.getItems())
        .forEach(
            workingServerItem -> {
              UUID currentWorkingProcessId = getWorkingProcessId(workingServerItem);
              List<IWorkingServerInfo> foundItems =
                  workingServers.stream()
                      .filter(ws -> ws.getWorkingServerId().equals(currentWorkingProcessId))
                      .collect(Collectors.toList());
              if (foundItems.isEmpty()) {
                workingServerItem.dispose();
              }
            });

    // добавление новых элементов
    workingServers.forEach(
        wsInfo -> {
          TreeItem workingServerItem =
              Arrays.stream(workingServersNode.getItems())
                  .filter(wpItem -> wsInfo.getWorkingServerId().equals(getWorkingServerId(wpItem)))
                  .findFirst()
                  .orElseGet(() -> addWorkingServerItemInNode(workingServersNode, wsInfo));
        });
  }

  private void updateNode(
      TreeItem clusterItem, Class<? extends BaseInfoExtended> clazz, Server server) {

    UUID clusterId = getClusterId(clusterItem);
  }

  private void refreshCurrentList() {

    if (currentTable == null) {
      return;
    }

    Server server = getCurrentServer();
    if (server == null) {
      return;
    }

    UUID clusterId = getCurrentClusterId();
    UUID infobaseId = getInfobaseId(currentTreeItem);
    UUID workingProcessId = getCurrentWorkingProcessId();
    // TreeItemType treeItemType = getTreeItemType(currentTreeItem); // TODO currentHighlightingType

    final Class<?> clazz = (Class<?>) currentTable.getData();
    clearTabs(clazz == IAssignmentRuleInfo.class);
    List<BaseInfoExtended> list = null;



    // TODO getSessionsExtendedInfo, getConnectionsExtendedInfo и др.
    // заменить на одну?, определяемую через map
    if (clazz == SessionInfoExtended.class) {
      list =
          server.getSessionsExtendedInfo(
              currentHighlightingType, clusterId, workingProcessId, infobaseId);

    } else if (clazz == ConnectionInfoExtended.class) {
      list =
          server.getConnectionsExtendedInfo(
              currentHighlightingType, clusterId, workingProcessId, infobaseId);

    } else if (clazz == LockInfoExtended.class) {
      list = server.getLocksExtendedInfo(currentHighlightingType, clusterId, infobaseId);

    } else if (clazz == WorkingProcessInfoExtended.class) {
      list =
          server.getWorkingProcessesExtendedInfo(
              currentHighlightingType, clusterId, workingProcessId);

    } else if (clazz == WorkingServerInfoExtended.class) {
      list = server.getWorkingServersExtendedInfo(currentHighlightingType, clusterId);

      // очистка таблицы с ТНФ
      tnfTableViewer.getTable().clearAll();

    } else if (clazz == IAssignmentRuleInfo.class) {
      fillAssignmentRulesTable();
      return;
    } else {
      return;
    }

    BaseInfoExtended.updateTabText((Class<? extends BaseInfoExtended>) clazz, list.size());
    list.forEach(item -> item.addToTable(currentTable));
  }

  private void highlightTreeItem(TreeItem treeItem) {
    currentTreeItem = treeItem;
    currentTreeItem.setFont(fontBold);
    columnServer.pack();
  }

  private void setActiveContextMenuInTree(TreeItem treeItem) {
    TreeItemType currentContextMenuItem = getTreeItemType(treeItem);
    serversTree.setMenu(serversTreeContextMenus.get(currentContextMenuItem));
  }

  private void clearTabs(boolean isAssignmentRule) {

    // BaseInfoExtended.resetTabsTextCount();

    tableSessions.removeAll();
    tableConnections.removeAll();
    tableLocks.removeAll();
    tableWorkingProcesses.removeAll();
    if (!isAssignmentRule) {
      tableWorkingServers.removeAll();
    }
  }

  private void clickItemInServerTree(int mouseButton) {
    TreeItem[] item = serversTree.getSelection();
    if (item.length == 0) {
      return;
    }

    TreeItem treeItem = item[0];

    switch (mouseButton) {
      case 1: // left click
        if (treeItem.equals(currentTreeItem)) { // Objects.nonNull(treeItem) && ???
          return;
        }
        saveCurrentSelectedData(treeItem);
        setEnableToolbarItems();
        refreshCurrentList();
        break;

      case 3: // right click
        setActiveContextMenuInTree(treeItem);
        break;

      default:
        break;
    }
  }

  private Table getCurrentTable() {
    return currentTable;
  }

  private Table getTable(TabItem tab) {
    Control control = tab.getControl();
    if (control instanceof Table) {
      return (Table) control;
    }
    if (control instanceof SashForm) {
      return (Table) (((SashForm) control).getChildren())[0];
    }
    // return ((Table) tab.getControl());
    return null;
  }

  private ColumnProperties getColumnProperties(Class<?> clazz) {
    return config.getColumnsProperties(clazz);
  }

  private void saveCurrentSelectedData(TreeItem treeItem) {

    if (currentTreeItem != null && !currentTreeItem.isDisposed()) {
      currentTreeItem.setFont(fontNormal);
    }
    currentTreeItem = treeItem;

    currentHighlightingType = getTreeItemType(treeItem);
    currentHighlightingData = null;

    switch (currentHighlightingType) {
      case SERVER:
        currentHighlightingData = getCurrentServer();
        break;

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
      case WORKINGSERVER_NODE:
        currentHighlightingData = getCurrentClusterId();
        break;

      case INFOBASE:
        currentHighlightingData = getInfobaseId(treeItem);
        break;

      case WORKINGPROCESS:
        currentHighlightingData = getCurrentWorkingProcessId();
        break;

      case WORKINGSERVER:
        currentHighlightingData = getCurrentWorkingServerId();
        break;

      default:
        break;
    }
    highlightTreeItem(treeItem);
  }

  private void setEnableToolbarItems() {
    // включение/выключение активности кнопок тулбара
    addToolbarItem.setEnabled(
        serversTree.getSelection().length == 0
            || toolbarCreateListeners.get(currentHighlightingType) != null);

    editToolbarItem.setEnabled(toolbarEditListeners.get(currentHighlightingType) != null);
    deleteToolbarItem.setEnabled(toolbarDeleteListeners.get(currentHighlightingType) != null);
  }

  private void fillAssignmentRulesTable() {
    Object wsInfo = tnfTableViewer.getTable().getData(ID_DATA_KEY);
    fillAssignmentRulesTable((WorkingServerInfoExtended) wsInfo);
  }

  private void fillAssignmentRulesTable(WorkingServerInfoExtended wsInfo) {

    TableItem[] item = tableWorkingServers.getSelection();
    if (item.length == 0) {
      return;
    }
    Server server = getCurrentServer();
    UUID clusterId = getCurrentClusterId();
    UUID workingServerId = wsInfo.getWorkingServerId();
    if (server == null || clusterId == null) {
      return;
    }

    // Заполнение таблицы с ТНФ
    List<IAssignmentRuleInfo> listTnf = server.getAssignmentRules(clusterId, workingServerId);
    tnfTableViewer.setInput(listTnf);
    tnfTableViewer.getTable().setData(ID_DATA_KEY, wsInfo);
  }

  private void addTableViewerColumn(
      TableViewer tableViewer, String name, String desc, ColumnProperties columnProperties) {

    var tableViewerColumnParamKey = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn newColumn = tableViewerColumnParamKey.getColumn();
    newColumn.setText(desc);
    newColumn.setMoveable(true);
    newColumn.setAlignment(SWT.RIGHT);
    tableViewerColumnParamKey.setLabelProvider(new AssignmentRuleLabelProvider(name));

    if (columnProperties == null) {
      newColumn.setWidth(100);
      return;
    }

    final int[] columnWidth = columnProperties.getWidth();
    final boolean[] columnVisible = columnProperties.getVisible();

    Table table = tableViewer.getTable();
    int numOfColumn = table.getColumnCount() - 1;
    newColumn.setData(numOfColumn);
    if (numOfColumn == columnProperties.getSortColumn()) {
      table.setSortColumn(newColumn);
      table.setSortDirection(columnProperties.getSortDirectionSwt());
    }

    if (columnVisible != null && columnVisible[numOfColumn]) {
      newColumn.setResizable(true);
      newColumn.setWidth(columnWidth[numOfColumn] == 0 ? 100 : columnWidth[numOfColumn]);
    } else {
      newColumn.setResizable(false);
      newColumn.setWidth(0);
    }

    newColumn.addListener(SWT.Move, columnMoveListener);
    newColumn.addListener(SWT.Resize, columnResizeListener);
    newColumn.addListener(SWT.Selection, columnSortListener);
  }

  private void addItemInTable(Table table) {
    Class<?> clazz = (Class<?>) table.getData();

    if (clazz == WorkingServerInfoExtended.class) {

      Server server = getCurrentServer();
      UUID clusterId = getCurrentClusterId();

      if (server == null || clusterId == null) {
        return;
      }

      WorkingServerDialog dialog;
      try {
        dialog =
            new WorkingServerDialog(
                getParent().getDisplay().getActiveShell(), server, clusterId, null);
      } catch (Exception excp) {
        LOGGER.error(
            "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
            clusterId,
            excp);
        return;
      }

      if (dialog.open() == 0) {
        refreshCurrentList();
      }
    }

    if (clazz == IAssignmentRuleInfo.class) {

      WorkingServerInfoExtended wsInfo = (WorkingServerInfoExtended) table.getData(ID_DATA_KEY);

      Server server = getCurrentServer();
      UUID clusterId = getCurrentClusterId();
      UUID wsId = wsInfo.getWorkingServerId();
      if (server == null || clusterId == null || wsId == null) {
        return;
      }

      AssignmentRuleDialog dialog;
      try {
        dialog =
            new AssignmentRuleDialog(
                getParent().getDisplay().getActiveShell(), server, clusterId, wsId, null);
      } catch (Exception excp) {
        excp.printStackTrace();
        LOGGER.error(
            "Error init AssignmentRuleEditDialog for new rule", //$NON-NLS-1$
            excp);
        return;
      }

      if (dialog.open() == 0) {
        fillAssignmentRulesTable();
      }
    }
  
  }

  private void viewItemInTable(TableItem item) {
    BaseInfoExtended extInfo = (BaseInfoExtended) item.getData(EXTENDED_INFO);

    if (extInfo instanceof SessionInfoExtended) {

      SessionInfoExtended sessionExtInfo = (SessionInfoExtended) extInfo;

      SessionInfoDialog dialog;
      try {
        dialog = new SessionInfoDialog(getParent().getDisplay().getActiveShell(), sessionExtInfo);
      } catch (Exception excp) {
        excp.printStackTrace();
        LOGGER.error(
            "Error init SessionInfoDialog for session id {}", //$NON-NLS-1$
            sessionExtInfo.getSessionInfo().getSid(),
            excp);
        return;
      }

      dialog.open();
    }
  }

  private void editItemInTable(TableItem item) {
    BaseInfoExtended extInfo = (BaseInfoExtended) item.getData(EXTENDED_INFO);
    if (extInfo instanceof WorkingServerInfoExtended) {

      WorkingServerInfoExtended workingServerExtInfo = (WorkingServerInfoExtended) extInfo;

      WorkingServerDialog dialog;
      try {
        dialog =
            new WorkingServerDialog(
                getParent().getDisplay().getActiveShell(), workingServerExtInfo);
      } catch (Exception excp) {
        excp.printStackTrace();
        LOGGER.error(
            "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
            workingServerExtInfo.getWorkingServerId(),
            excp);
        return;
      }

      if (dialog.open() == 0) {
        // clickItemInServerTree(0); // TODO что здесь должно делаться???
        refreshCurrentList();
      }
    } else if (extInfo == null && item.getData() instanceof IAssignmentRuleInfo) {
      IAssignmentRuleInfo ruleInfo = (IAssignmentRuleInfo) item.getData();

      WorkingServerInfoExtended wsInfo =
          (WorkingServerInfoExtended) item.getParent().getData(ID_DATA_KEY);

      Server server = getCurrentServer();
      UUID clusterId = getCurrentClusterId();
      UUID wsId = wsInfo.getWorkingServerId();
      if (server == null || clusterId == null || wsId == null) {
        return;
      }

      AssignmentRuleDialog dialog;
      try {
        dialog =
            new AssignmentRuleDialog(
                getParent().getDisplay().getActiveShell(), server, clusterId, wsId, ruleInfo);
      } catch (Exception excp) {
        excp.printStackTrace();
        LOGGER.error(
            "Error init AssignmentRuleEditDialog for rule {}", //$NON-NLS-1$
            ruleInfo.getObjectType(),
            excp);
        return;
      }

      if (dialog.open() == 0) {
        refreshCurrentList();
      }

    } else {
      return;
    }
  }

  private void deleteItemsFromTable(TableItem[] selectedItems) {

    for (TableItem item : selectedItems) {

      Object extInfo = item.getData(EXTENDED_INFO);
      item.setForeground(deletedItemColor); // TODO успевает ли окраситься в красный цвет

      if (extInfo instanceof SessionInfoExtended) {
        SessionInfoExtended sessionExtInfo = (SessionInfoExtended) extInfo;

        Server server = sessionExtInfo.getServer();
        UUID clusterId = sessionExtInfo.getClusterId();
        UUID sessionId = sessionExtInfo.getSessionInfo().getSid();

        if (server.terminateSession(clusterId, sessionId)) {
          item.dispose();
        }

      } else if (extInfo instanceof ConnectionInfoExtended) {
        ConnectionInfoExtended connExtInfo = (ConnectionInfoExtended) extInfo;

        Server server = connExtInfo.getServer();
        UUID clusterId = connExtInfo.getClusterId();
        UUID processId = connExtInfo.getConnectionInfo().getWorkingProcessId();
        UUID connectionId = connExtInfo.getConnectionInfo().getInfoBaseConnectionId();
        UUID infobaseId = connExtInfo.getConnectionInfo().getInfoBaseId();

        if (server.disconnectConnection(clusterId, processId, connectionId, infobaseId)) {
          item.dispose(); // update tableConnections
        }

      } else if (extInfo instanceof WorkingServerInfoExtended) {
        WorkingServerInfoExtended wsExtInfo = (WorkingServerInfoExtended) extInfo;

        Server server = wsExtInfo.getServer();
        UUID clusterId = wsExtInfo.getClusterId();
        UUID workingServerId = wsExtInfo.getWorkingServerId();

        int answer =
            Helper.showQuestionBox(
                Messages.getString("ViewerArea.DeleteServerQuestion")); // $NON-NLS-1$

        if (answer == SWT.YES && server.unregWorkingServer(clusterId, workingServerId)) {
          item.dispose(); // update tableWorkingServers
        }
      } else if (extInfo == null && item.getData() instanceof IAssignmentRuleInfo) {
        IAssignmentRuleInfo ruleInfo = (IAssignmentRuleInfo) item.getData();

        WorkingServerInfoExtended wsInfo =
            (WorkingServerInfoExtended) item.getParent().getData(ID_DATA_KEY);

        Server server = getCurrentServer();
        UUID clusterId = getCurrentClusterId();
        UUID wsId = wsInfo.getWorkingServerId();
        if (server == null || clusterId == null || wsId == null) {
          return;
        }

        int answer =
            Helper.showQuestionBox(
                Messages.getString("ViewerArea.DeleteAssignmentRule")); // $NON-NLS-1$

        if (answer == SWT.YES
            && server.unregAssignmentRule(clusterId, wsId, ruleInfo.getAssignmentRuleId())) {
          item.dispose();
        }

      } else {
        break;
      }
    }
  }

  private void changeAssignmentRuleNumber(TableItem item, boolean increase) {
    IAssignmentRuleInfo rule = (IAssignmentRuleInfo) item.getData();

    int newRuleNumber = AssignmentRuleContentProvider.getRuleNumber(rule) + (increase ? -1 : 1);

    WorkingServerInfoExtended wsInfo =
        (WorkingServerInfoExtended) item.getParent().getData(ID_DATA_KEY);

    Server server = getCurrentServer();
    UUID clusterId = getCurrentClusterId();
    UUID wsId = wsInfo.getWorkingServerId();
    if (server == null || clusterId == null || wsId == null) {
      return;
    }

    server.regAssignmentRule(clusterId, wsId, rule, newRuleNumber - 1);
    refreshCurrentList();
  }

  private Map<String, String> fillParams(BackgroundTask backgroundTask, TreeItem infobaseItem) {
    Map<String, String> params = new LinkedHashMap<>();

    UUID clusterId = getClusterId(infobaseItem);
    UUID infobaseId = getInfobaseId(infobaseItem);
    Server server = getServer(infobaseItem);
    if (server == null) {
      LOGGER.error(
          "Error get server info {}", //$NON-NLS-1$
          backgroundTask.getScriptName());
      return params;
    }

    boolean foundEmptyParams = false;
    boolean foundUsernameParam = false;
    boolean foundPasswordParam = false;

    Pattern p = Pattern.compile("\\%.+?\\%");
    // Pattern p = Pattern.compile("[^\\%]++", Pattern.CASE_INSENSITIVE);

    Matcher m = p.matcher(backgroundTask.getScriptText());
    while (m.find()) {
      final String foundParam = m.group();
      String paramValue;

      switch (foundParam) {
        case "%v8infobase%":
          paramValue = server.getInfoBaseName(clusterId, infobaseId);
          break;

        case "%v8serverName%":
          paramValue = server.getAgentHost();
          break;

        case "%v8agentPort%":
          paramValue = server.getAgentPortAsString();
          break;

        case "%v8managerPort%":
          paramValue = server.getClusterMainPort(clusterId);
          break;

        case "%v8version%":
          paramValue = server.getV8Version();
          break;

        case "%v8username%":
          foundUsernameParam = true;
          foundEmptyParams = true;
          continue;

        case "%v8password%":
          foundPasswordParam = true;
          foundEmptyParams = true;
          continue;

        default:
          LOGGER.info("Found unknown param {}", foundParam);
          paramValue = "";
          foundEmptyParams = true;
          break;
      }

      String paramKey = foundParam.replace("%", "");
      params.put(paramKey, paramValue);
    }
    if (Boolean.TRUE.equals(foundUsernameParam)) {
      params.put("v8username", "");
    }
    if (Boolean.TRUE.equals(foundPasswordParam)) {
      params.put("v8password", "");
    }

    if (foundEmptyParams) {
      BackgroundTaskParams taskParamsDialog =
          new BackgroundTaskParams(
              getShell(),
              params,
              backgroundTask,
              server.getInfobasesCredentials());
      int dialogResult = taskParamsDialog.open();
      if (dialogResult != 0) {
        return new HashMap<>();
      }

      params = taskParamsDialog.getParams();
    }

    return params;
  }

  private void addToTasksQueue(BackgroundTask task) {
    TableItem tableItem = new TableItem(tableTasks, SWT.NONE);
    tableItem.setData(task);
    tableItem.setChecked(false);

    if (tableTasks.getSelection().length == 0 || BackgroundTask.getRunningCount() == 0) {
      tableTasks.setSelection(tableItem);
    }

    task.run();

    if (BackgroundTask.getRunningCount() == 0) {
      taskTimer = new Timer(true);
      taskTimer.scheduleAtFixedRate(new TaskChekerTimer(), 1000, 1000);
    }
  }

  //////////////////////////////////////////////////////////////////////////
  // LISTENERS

  MouseAdapter treeItemMouseClickListener =
      new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
          clickItemInServerTree(e.button);
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
          TreeItem[] items = ((Tree) e.widget).getSelection();
          if (items.length == 0) {
            return;
          }

          SelectionAdapter listener = toolbarEditListeners.get(currentHighlightingType);
          if (listener != null) {
            listener.widgetSelected(null);
          }
        }
      };

  SelectionAdapter openSettingsListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          SettingsDialog dialog;
          try {
            dialog = new SettingsDialog(getParent().getDisplay().getActiveShell());
          } catch (Exception excp) {
            LOGGER.error("Error init SettingsDialog", excp); //$NON-NLS-1$
            return;
          }
          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            config.saveConfig();
            for (TreeItem item : serversTree.getItems()) {
              updateClustersInTree(item);
            }
          }
        }
      };

  SelectionAdapter showAboutDialogListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          AboutDialog dialog;
          try {
            dialog = new AboutDialog(getParent().getDisplay().getActiveShell());
          } catch (Exception excp) {
            LOGGER.error("Error init AboutDialog", excp); //$NON-NLS-1$
            return;
          }
          dialog.open();
        }
      };

  SelectionAdapter findNewServersListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          NewServersChoiseDialog dialog;
          try {
            dialog = new NewServersChoiseDialog(getParent().getDisplay().getActiveShell());
          } catch (Exception excp) {
            LOGGER.error("Error init NewServersChoiseDialog", excp); // $NON-NLS-1$
            return;
          }

          if (dialog.open() == 0) {
            List<Server> s = dialog.getNewServers();
            if (s.isEmpty()) {
              return;
            }

            Config.currentConfig.addNewServers(s);
            s.forEach((server) -> addServerItemInServersTree(server));
          }
        }
      };

  SelectionAdapter connectAllServersListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TreeItem[] serversItem = serversTree.getItems();
          for (TreeItem serverItem : serversItem) {
            Server server = getServer(serverItem);
            if (server != null && !server.isConnected()) {
              connectServerItem(serverItem, true);
            }
          }
        }
      };

  SelectionAdapter disconnectAllServersListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TreeItem[] serversItem = serversTree.getItems();
          for (TreeItem serverItem : serversItem) {
            disconnectServerItem(serverItem);
          }
        }
      };

  Listener setActiveConnectActionListener =
      new Listener() {
        @Override
        public void handleEvent(Event event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          if (server == null) {
            return;
          }

          boolean serverIsConnected = server.isConnected();
          boolean serverIsErrorConnected = !server.getConnectionError().isBlank();

          MenuItem[] menuItems = serverMenu.getItems();

          for (MenuItem menuItem : menuItems) {
            if (Boolean.TRUE.equals(menuItem.getData("connectItem"))) {
              menuItem.setEnabled(!serverIsConnected);
            }
            if (Boolean.TRUE.equals(menuItem.getData("disconnectItem"))) {
              menuItem.setEnabled(serverIsConnected);
            }
            if (Boolean.TRUE.equals(menuItem.getData("connectionErrorItem"))) {
              menuItem.setEnabled(serverIsErrorConnected);
            }

          }
        }
      };

  SelectionAdapter connectToServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          connectServerItem(item[0], false);
        }
      };

  SelectionAdapter disconnectFromServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          disconnectServerItem(item[0]);
        }
      };

  SelectionAdapter showServerConnectionErrorListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          if (server == null) {
            return;
          }

          Helper.showMessageBox(server.getConnectionError());
        }
      };

  SelectionAdapter toolbarListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          String buttonName = ((ToolItem) event.widget).getText();

          if (serversTree.getSelection().length == 0
              && Strings.CONTEXT_MENU_ADD.equals(buttonName)) {
            addServerListener.widgetSelected(event);
            return;
          }

          if (currentHighlightingType == null) { // пустое дерево или ничего не выбрано
            return;
          }

          Map<TreeItemType, SelectionAdapter> currListener = null;
          if (Strings.CONTEXT_MENU_ADD.equals(buttonName)) {
            currListener = toolbarCreateListeners;
          } else if (Strings.CONTEXT_MENU_EDIT.equals(buttonName)) {
            currListener = toolbarEditListeners;
          } else if (Strings.CONTEXT_MENU_DELETE.equals(buttonName)) {
            currListener = toolbarDeleteListeners;
          } else {
            return;
          }

          SelectionAdapter listener = currListener.get(currentHighlightingType);
          if (listener != null) {
            listener.widgetSelected(event);
          }
        }
      };

  SelectionAdapter addServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          Server newServer = config.createNewServer();
          ServerDialog dialog;
          try {
            dialog = new ServerDialog(getParent().getDisplay().getActiveShell(), newServer);
          } catch (Exception excp) {
            LOGGER.error("Error init ServerDialog for new server", excp); //$NON-NLS-1$
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult != 0) {
            return;
          }

          config.addNewServer(newServer);
          TreeItem newServerItem = addServerItemInServersTree(newServer);
          updateClustersInTree(newServerItem);
        }
      };

  SelectionAdapter editServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          TreeItem serverItem = item[0];
          Server server = getServer(serverItem);
          if (server == null) {
            return;
          }

          ServerDialog dialog;
          try {
            dialog = new ServerDialog(getParent().getDisplay().getActiveShell(), server);
          } catch (Exception excp) {
            excp.printStackTrace();
            LOGGER.error(
                "Error init ServerDialog for server {}", //$NON-NLS-1$
                server.getTreeTitle(),
                excp);
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            // перерисовать в дереве
            serverItem.setText(new String[] {server.getTreeTitle()});
            config.saveConfig();
            updateClustersInTree(serverItem);
          }
        }
      };

  SelectionAdapter updateServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          updateClustersInTree(item[0]);
        }
      };

  SelectionAdapter serversMoveUpListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TreeItem[] selectItems = serversTree.getSelection();
          if (selectItems.length == 0) {
            return;
          }
          int currentIndex = selectItems[0].getParent().indexOf(selectItems[0]);
          if (currentIndex == 0) {
            return;
          }

          Server server = getServer(selectItems[0]);
          if (server == null) {
            return;
          }

          TreeItem newItem = addServerItemInServersTree(server, currentIndex - 1);
          serversTree.select(newItem);

          selectItems[0].dispose();
          updateClustersInTree(newItem); // TODO а если сразу за addServerItemInServersTree
        }
      };

  SelectionAdapter serversMoveDownListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TreeItem[] selectItems = serversTree.getSelection();
          if (selectItems.length == 0) {
            return;
          }
          int currentIndex = selectItems[0].getParent().indexOf(selectItems[0]);

          if (currentIndex == serversTree.getItemCount() - 1) {
            return;
          }

          Server server = getServer(selectItems[0]);
          if (server == null) {
            return;
          }

          TreeItem newItem = addServerItemInServersTree(server, currentIndex + 2);
          serversTree.select(newItem);

          selectItems[0].dispose();
          updateClustersInTree(newItem);
        }
      };

  SelectionAdapter serversSortListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          Server selectedServer = getServer(serversTree.getSelection()[0]);

          // надо разобраться с терминами currentTreeItem:
          // selectedTreeItem, clickedTreeItem, rightClickedTreeItem ???

          // запоминаем текущий подсвеченный элемент дерева
          //          saveCurrentSelectedTreeItem();

          TreeItem[] items = serversTree.getItems();
          for (int i = 1; i < items.length; i++) {
            Server secondServer = getServer(items[i]);
            if (secondServer == null) {
              continue;
            }

            for (int j = 0; j < i; j++) {
              Server firstServer = getServer(items[j]);
              if (firstServer == null) {
                continue;
              }

              if (firstServer.compareTo(secondServer) > 0) {

                TreeItem newItem = addServerItemInServersTree(secondServer, j);

                // возможно, будет лучше сначала упорядочить все, а уже потом пройтись циклом по
                //   серверам для перезаполнения дочерних элементов
                // ибо при многократном передвигании подключенного сервера  заполнение дочерних
                //   выполнится несколько лишних раз
                updateClustersInTree(newItem);

                items[i].dispose();
                items = serversTree.getItems();
                break;
              }
            }
          }
          items = serversTree.getItems();
          for (int i = 0; i < items.length; i++) {

            // updateClustersInTree(items[i]);
            // восстановление элемента, выделенного правим кликом
            Server server = getServer(items[i]);
            if (server == selectedServer) {
              serversTree.select(items[i]);
              break;
            }

            // восстановление подсвеченного (текущего) элемента

          }
        }
      };

  SelectionAdapter deleteServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          int answer =
              Helper.showQuestionBox(
                  Messages.getString("ViewerArea.DeleteServerQuestion")); //$NON-NLS-1$

          if (answer == SWT.YES) {
            final TreeItem serverItem = item[0];
            Server server = getServer(serverItem);
            config.removeServer(server);
            serverItem.dispose();
          }
        }
      };

  SelectionAdapter createClusterListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);

          ClusterDialog dialog;
          try {
            dialog = new ClusterDialog(getParent().getDisplay().getActiveShell(), server, null);
          } catch (Exception excp) {
            LOGGER.error(
                "Error init ClusterDialog for new cluster", //$NON-NLS-1$
                excp);
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            updateClustersInTree(item[0].getParentItem());
          }
        }
      };

  SelectionAdapter editClusterListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);

          ClusterDialog dialog;
          try {
            dialog =
                new ClusterDialog(getParent().getDisplay().getActiveShell(), server, clusterId);
          } catch (Exception excp) {
            LOGGER.error(
                "Error init ClusterDialog for cluster id {}", //$NON-NLS-1$
                clusterId,
                excp);
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            updateClustersInTree(item[0].getParentItem());
          }
        }
      };

  SelectionAdapter updateClusterListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          updateNodesOfCluster(item[0], server);
        }
      };

  SelectionAdapter deleteClusterListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          final TreeItem clusterItem = item[0];
          
          Server server = getServer(clusterItem);
          UUID clusterId = getClusterId(clusterItem);
          if (server == null || clusterId == null) {
            return;
          }

          int answer =
              Helper.showQuestionBox(
                  Messages.getString("ViewerArea.DeleteClusterQuestion")); //$NON-NLS-1$

          if (answer == SWT.YES && server.unregCluster(clusterId)) {
            clusterItem.dispose();
          }
        }
      };

  SelectionAdapter editAdminsListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          TreeItem treeItem = item[0];
          TreeItemType treeItemType = getTreeItemType(treeItem);
          if (treeItemType != TreeItemType.SERVER && treeItemType != TreeItemType.CLUSTER) {
            LOGGER.error("Invalid item type for AdminsDialog"); // $NON-NLS-1$
            return;
          }

          Server server = getServer(treeItem);
          UUID clusterId = getClusterId(treeItem);

          AdminsDialog dialog;
          try {
            dialog = new AdminsDialog(getParent().getDisplay().getActiveShell(), server, clusterId);
          } catch (Exception excp) {
            LOGGER.error(
                "Error init AdminsDialog for cluster id {}", //$NON-NLS-1$
                clusterId,
                excp);
            return;
          }
          dialog.open();
        }
      };

  SelectionAdapter restartWorkingProcessesListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          final TreeItem clusterItem = item[0];

          Server server = getServer(clusterItem);
          UUID clusterId = getClusterId(clusterItem);
          if (server == null || clusterId == null) {
            return;
          }

          if (!server.checkAutenticateAgent()) {
            Helper.showMessageBox(Strings.ERROR_AUTH_FOR_RESTART_WP);
            return;
          }

          Thread thread =
              new Thread(
                  () -> {
                    IClusterInfo clusterInfo = server.getClusterInfo(clusterId);

                    final int oldLifeTimeLimit = clusterInfo.getLifeTimeLimit();
                    final boolean oldRecyclingKillProcesses =
                        clusterInfo.isClusterRecyclingKillProblemProcesses();
                    final int oldExpirationTimeout = clusterInfo.getExpirationTimeout();

                    clusterInfo.setLifeTimeLimit(10);
                    clusterInfo.setClusterRecyclingKillProblemProcesses(true);
                    clusterInfo.setExpirationTimeout(20);

                    if (!server.regCluster(clusterInfo)) {
                      Helper.showMessageBox("Error setting temporary properties for the cluster");
                      return;
                    }

                    // подождать 10 секунд и вернуть все назад
                    try {
                      Thread.sleep(10000);
                    } catch (InterruptedException excp) {
                      LOGGER.error("Error: ", excp); // $NON-NLS-1$
                    }

                    clusterInfo.setLifeTimeLimit(oldLifeTimeLimit);
                    clusterInfo.setClusterRecyclingKillProblemProcesses(oldRecyclingKillProcesses);
                    clusterInfo.setExpirationTimeout(oldExpirationTimeout);

                    if (!server.regCluster(clusterInfo)) {
                      Helper.showMessageBox("Error returning cluster properties");
                    }
                  });

          thread.start();
        }
      };

  SelectionAdapter applyAssignmentRuleListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);

          if (server == null || clusterId == null) {
            return;
          }

          Object data = ((MenuItem) event.widget).getData();
          if (data instanceof Integer) {
            server.applyAssignmentRules(clusterId, (int) data);
          }
        }
      };

  SelectionAdapter createInfobaseListener =
      // TODO вызывается из контекстного меню и из тулбара
      // нет ли ошибки serversTree.getSelection() при вызове из тулбара
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);
          UUID sampleInfobaseId = getInfobaseId(item[0]);
          if (server == null || clusterId == null) {
            return;
          }

          CreateInfobaseDialog dialog;
          try {
            dialog =
                new CreateInfobaseDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, sampleInfobaseId);
          } catch (Exception excp) {
            LOGGER.error("Error in CreateInfobaseDialog", excp); //$NON-NLS-1$
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            TreeItem clusterItem =
                sampleInfobaseId == null
                    ? item[0].getParentItem()
                    : item[0].getParentItem().getParentItem();
            updateInfobasesOfCluster(clusterItem, server);

          }
        }
      };

  SelectionAdapter editInfobaseListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          final TreeItem infobaseItem = item[0];

          Server server = getServer(infobaseItem);
          UUID clusterId = getClusterId(infobaseItem);
          UUID infobaseId = getInfobaseId(infobaseItem);
          if (server == null || clusterId == null || infobaseId == null) {
            return;
          }

          InfobaseDialog dialog;
          try {
            dialog =
                new InfobaseDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
          } catch (Exception excp) {
            excp.printStackTrace();
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            InfoBaseInfoShortExt ibInfo =
                new InfoBaseInfoShortExt(
                    server.getInfoBaseShortInfo(clusterId, infobaseId), 0, false);

            // обновление представления инфобазы
            if (!infobaseItem.getText().equals(ibInfo.getInfobaseDescription())) {
              infobaseItem.setText(ibInfo.getInfobaseDescription());
            }
          }
        }
      };

  SelectionAdapter deleteInfobaseListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          final TreeItem infobaseItem = item[0];

          Server server = getServer(infobaseItem);
          UUID clusterId = getClusterId(infobaseItem);
          UUID infobaseId = getInfobaseId(infobaseItem);
          if (server == null || clusterId == null || infobaseId == null) {
            return;
          }

          DropInfobaseDialog dialog;
          try {
            dialog =
                new DropInfobaseDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
          } catch (Exception excp) {
            excp.printStackTrace();
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            final TreeItem clusterItem = infobaseItem.getParentItem().getParentItem();
            infobaseItem.dispose();
            updateInfobasesOfCluster(clusterItem, server);
          }
        }
      };

  SelectionAdapter updateInfobasesListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          if (server == null) {
            return;
          }

          updateInfobasesOfCluster(item[0].getParentItem(), server);
        }
      };

  Listener setActiveInfobaseFavoritesActionListener =
      new Listener() {
        @Override
        public void handleEvent(Event event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          if (server == null) {
            return;
          }

          InfoBaseInfoShortExt ib = getInfobaseShortInfoExt(item[0]);
          String favoritesActionTitle =
              server.infobaseIsFavorite(ib)
                  ? Strings.CONTEXT_MENU_DELETE_FROM_FAVORITES
                  : Strings.CONTEXT_MENU_ADD_IN_FAVORITES;

          MenuItem[] menuItems = ((Menu) event.widget).getItems();
          for (MenuItem menuItem : menuItems) {
            if (Boolean.TRUE.equals(menuItem.getData("favoritesItem"))) {
              menuItem.setText(favoritesActionTitle);
            }
          }
        }
      };

  SelectionAdapter addInfobaseToFavoritesListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          InfoBaseInfoShortExt ib = getInfobaseShortInfoExt(item[0]);

          if (server == null || ib == null) {
            return;
          }

          server.changeInfobaseFavoriteState(ib);
          item[0].setImage(ib.getIcon());

          if (config.getInfobasesSortDirection() == InfobasesSortDirection.BY_FAVORITES_AND_NAME) {
            sortInfobasesListener.widgetSelected(null);
          }
        }
      };

  SelectionAdapter sortInfobasesListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {

          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }
          TreeItem ibNode = item[0];
          if ((TreeItemType) ibNode.getData("Type") == TreeItemType.INFOBASE) { //$NON-NLS-1$
            ibNode = item[0].getParentItem();
          }

          TreeItem[] items = ibNode.getItems();

          if (event != null && !(event.widget.getData() instanceof InfobasesSortDirection)) {
            return;
          }

          if (event != null) {
            config.setInfobasesSortDirection((InfobasesSortDirection) event.widget.getData());
          }

          for (int i = 1; i < items.length; i++) {
            InfoBaseInfoShortExt secondIb = getInfobaseShortInfoExt(items[i]);

            for (int j = 0; j < i; j++) {
              InfoBaseInfoShortExt firstIb = getInfobaseShortInfoExt(items[j]);

              if (firstIb != null && secondIb != null && firstIb.compareTo(secondIb) > 0) {
                addInfobaseItemInNode(ibNode, secondIb, j);

                items[i].dispose();
                items = ibNode.getItems();
                break;
              }
            }
          }
        }
      };

  SelectionAdapter lockUsersListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          TreeItem selectItem = item[0];

          Server server = getServer(selectItem);
          UUID clusterId = getClusterId(selectItem);
          UUID infobaseId = getInfobaseId(selectItem);
          if (server == null) {
            return;
          }

          IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infobaseId);
          if (infoBaseInfo == null) {
            return;
          }

          infoBaseInfo.setScheduledJobsDenied(true);
          infoBaseInfo.setSessionsDenied(true);
          infoBaseInfo.setDeniedFrom(null);
          infoBaseInfo.setDeniedTo(null);
          infoBaseInfo.setDeniedMessage(""); //$NON-NLS-1$
          infoBaseInfo.setDeniedParameter(""); //$NON-NLS-1$
          infoBaseInfo.setPermissionCode(""); //$NON-NLS-1$

          server.updateInfoBase(clusterId, infoBaseInfo);
        }
      };

  SelectionAdapter terminateAllSessionsListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);
          UUID infobaseId = getInfobaseId(item[0]);
          if (server == null) {
            return;
          }

          server.terminateAllSessionsOfInfobase(clusterId, infobaseId, false);
        }
      };

  SelectionAdapter terminateUsersSessionsListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);
          UUID infobaseId = getInfobaseId(item[0]);
          if (server == null) {
            return;
          }

          server.terminateAllSessionsOfInfobase(clusterId, infobaseId, true);
        }
      };

  SelectionAdapter launchV8ActionListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] items = serversTree.getSelection();
          if (items.length == 0) {
            return;
          }

          TreeItem infobaseItem = items[0];
          BackgroundTask backgroundTask =
              new BackgroundTask((V8ActionVariant) event.widget.getData());

          Map<String, String> params = fillParams(backgroundTask, infobaseItem);
          if (!params.isEmpty()) {
            backgroundTask.setParams(params);
            addToTasksQueue(backgroundTask);
          }
        }
      };

  SelectionAdapter createWorkingServerListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          Table tableWorkingServers = getCurrentTable();
          Server server = getCurrentServer();
          UUID clusterId = getCurrentClusterId();

          if (server == null || clusterId == null) {
            return;
          }

          WorkingServerDialog dialog;
          try {
            dialog =
                new WorkingServerDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, null);
          } catch (Exception excp) {
            LOGGER.error(
                "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                clusterId,
                excp);
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            var newWorkingServerUuid = dialog.getNewWorkingServerId();
            if (newWorkingServerUuid != null) {
              WorkingServerInfoExtended workingServerInfo =
                  new WorkingServerInfoExtended(
                      server,
                      clusterId,
                      server.getWorkingServerInfo(clusterId, newWorkingServerUuid));
              workingServerInfo.addToTable(tableWorkingServers);
            }
          }
        }
      };

  SelectionAdapter createWorkingServerListenerInTree =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);
          if (server == null || clusterId == null) {
            return;
          }

          WorkingServerDialog dialog;
          try {
            dialog =
                new WorkingServerDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, null);
          } catch (Exception excp) {
            LOGGER.error(
                "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                clusterId,
                excp);
            return;
          }

          int dialogResult = dialog.open();
          if (dialogResult == 0) {
            var newWorkingServerUuid = dialog.getNewWorkingServerId();
            if (newWorkingServerUuid != null) {
              IWorkingServerInfo workingServerInfo =
                  server.getWorkingServerInfo(clusterId, newWorkingServerUuid);
              addWorkingServerItemInNode(item[0].getParentItem(), workingServerInfo);
            }
          }
        }
      };

  SelectionAdapter editWorkingServerListenerInTree =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TreeItem[] item = serversTree.getSelection();
          if (item.length == 0) {
            return;
          }

          Server server = getServer(item[0]);
          UUID clusterId = getClusterId(item[0]);
          UUID workingServerId = getWorkingServerId(item[0]);
          if (server == null || clusterId == null) {
            return;
          }

          WorkingServerDialog dialog;
          try {
            dialog =
                new WorkingServerDialog(
                    getParent().getDisplay().getActiveShell(), server, clusterId, workingServerId);
          } catch (Exception excp) {
            excp.printStackTrace();
            LOGGER.error(
                "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                workingServerId,
                excp);
            return;
          }

          dialog.open();
        }
      };

  Listener columnMoveListener =
      new Listener() {
        @Override
        public void handleEvent(Event e) {

          TableColumn column = (TableColumn) e.widget;
          Table currentTable = column.getParent();

          final Class<?> clazz = (Class<?>) currentTable.getData();
          if (clazz != null) {
            config.setColumnsOrder(clazz, currentTable.getColumnOrder());
          }

          // clusterProvider.saveConfig();
        }
      };

  Listener columnResizeListener =
      new Listener() {
        @Override
        public void handleEvent(Event e) {

          TableColumn column = (TableColumn) e.widget;
          Table currentTable = column.getParent();

          int newWidth = column.getWidth();
          TableColumn[] columns = currentTable.getColumns();

          final Class<?> clazz = (Class<?>) currentTable.getData();
          for (int i = 0; i < columns.length; i++) {
            if (columns[i].getText().equals(column.getText())) {
              config.setColumnsWidth(clazz, i, newWidth);
              break;
            }
          }
          // clusterProvider.saveConfig();
        }
      };

  Listener columnSortListener =
      new Listener() {
        public void handleEvent(Event e) {
          TableColumn column = (TableColumn) e.widget;
          Table currentTable = column.getParent();

          int numColumn = (int) column.getData();

          final Class<?> clazz = (Class<?>) currentTable.getData();
          ColumnProperties columnProperties = getColumnProperties(clazz);
          if (columnProperties == null) {
            return;
          }

          columnProperties.setSortColumn(numColumn);
          currentTable.setSortColumn(column);
          currentTable.setSortDirection(columnProperties.getSortDirectionSwt());

          // сортировка того что уже есть в списке
          TableItem[] items = currentTable.getItems();

          if (clazz == IAssignmentRuleInfo.class) {
            tnfTableViewer.refresh();
          }
          if (clazz != IAssignmentRuleInfo.class) {
            for (int i = 1; i < items.length; i++) {
              BaseInfoExtended secondString = (BaseInfoExtended) items[i].getData(EXTENDED_INFO);

              for (int j = 0; j < i; j++) {
                BaseInfoExtended firstString = (BaseInfoExtended) items[j].getData(EXTENDED_INFO);

                if (firstString.compareTo(secondString) > 0) {
                  items[i].dispose();
                  secondString.addToTable(currentTable, j);
                  items = currentTable.getItems();
                  break;
                }
              }
            }
          }
        }
      };

  //  SelectionAdapter updateTablesListener =
  //      new SelectionAdapter() {
  //        @Override
  //        public void widgetSelected(SelectionEvent e) {
  //          if (e.detail == SWT.ARROW) {
  //            ToolItem item = (ToolItem) e.widget;
  //            Rectangle rect = item.getBounds();
  //            Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
  //            menu.setLocation(pt.x, pt.y + rect.height);
  //            menu.setVisible(true);
  //          } else {
  //            System.out.println(dropdown.getText() + " Pressed");
  //            fillTabs();
  //          }
  //        }
  //      };

  SelectionAdapter copyCellValueInTablesListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TableItem[] selection = getCurrentTable().getSelection();
          if (selection.length > 0) {
            Clipboard clipboard = new Clipboard(Display.getDefault());
            clipboard.setContents(
                new Object[] {selection[0].getText(lastSelectColumn)},
                new Transfer[] {TextTransfer.getInstance()});
            clipboard.dispose();
          }
        }
      };

  SelectionAdapter switchWatchingMenuListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          TableItem[] selection = getCurrentTable().getSelection();
          if (selection.length > 0) {
            TableItem item = selection[0];
            item.setChecked(!item.getChecked());
            SessionInfoExtended ext = (SessionInfoExtended) item.getData(EXTENDED_INFO);
            ext.switchWatching(item, item.getChecked());
          }
        }
      };

  Listener switchWatchingListener =
      new Listener() {
        @Override
        public void handleEvent(Event event) {
          if (event.detail == SWT.CHECK) {
            TableItem item = (TableItem) event.item;
            SessionInfoExtended ext = (SessionInfoExtended) item.getData(EXTENDED_INFO);
            ext.switchWatching(item, item.getChecked());
          }
        }
      };

  Listener mouseSelectCellListener = // TODO не используется?
      new Listener() {
        @Override
        public void handleEvent(Event event) {
          Table currentTable = (Table) event.widget;

          Point pt = new Point(event.x, event.y);
          TableItem item = currentTable.getItem(pt);
          if (item != null) {
            for (int col = 0; col < currentTable.getColumnCount(); col++) {
              Rectangle rect = item.getBounds(col);
              if (rect.contains(pt)) {

                if (lastSelectItem != null && !lastSelectItem.isDisposed()) {
                  lastSelectItem.setForeground(lastSelectColumn, null);
                }
                item.setForeground(col, Helper.getOrangeColor());

                lastSelectItem = item;
                lastSelectColumn = col;
                break;
              }
            }
          }
        }
      };

  MouseAdapter tablesMouseClickListener =
      new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent event) {
          if (event.button != 1 && event.button != 3) {
            return;
          }
          currentTable = (Table) event.widget;

          Point pt = new Point(event.x, event.y);
          TableItem item = currentTable.getItem(pt);

          if (item == null) {
            currentTable.deselectAll();
            return;
          }

          for (int col = 0; col < currentTable.getColumnCount(); col++) {
            Rectangle rect = item.getBounds(col);
            if (rect.contains(pt)) {

              if (lastSelectItem != null && !lastSelectItem.isDisposed()) {
                lastSelectItem.setForeground(lastSelectColumn, null);
              }
              item.setForeground(col, Helper.getOrangeColor());

              // заполнение таблицы с ТНФ
              if (currentTable.equals(tableWorkingServers) && !item.equals(lastSelectItem)) {
                WorkingServerInfoExtended ws =
                    (WorkingServerInfoExtended) item.getData(EXTENDED_INFO);
                fillAssignmentRulesTable(ws);
              }

              lastSelectItem = item;
              lastSelectColumn = col;
              break;
            }
          }
        }

        @Override
        public void mouseDoubleClick(MouseEvent event) {
          currentTable = (Table) event.widget;
          TableItem[] item = currentTable.getSelection();
          if (item.length == 0) {
            return;
          }
          editItemInTable(item[0]);

        }
      };

  KeyAdapter tableKeyPressedListener =
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {

          final int keyC = 99;
          currentTable = (Table) e.widget;
          TableItem[] items = currentTable.getSelection();

          switch (e.keyCode) {
            case SWT.F5:
              refreshCurrentList();
              break;

            case SWT.INSERT:
              addItemInTable(currentTable);
              break;

            case SWT.F2:
              if (items.length == 0) {
                return;
              }
              editItemInTable(items[0]);
              break;

            case SWT.DEL:
              if (items.length == 0) {
                return;
              }
              deleteItemsFromTable(items);
              break;

            case keyC:
              if (e.stateMask == SWT.CTRL) {
                copyCellValueInTablesListener.widgetSelected(null);
              }
              break;

            default:
              break;
          }
        }
      };

  MouseAdapter tableTaskMouseClickListener =
      new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent event) {
          if (event.button != 1) {
            return;
          }
          TableItem[] tableItem = tableTasks.getSelection();
          if (tableItem.length == 0) {
            return;
          }

          BackgroundTask task = (BackgroundTask) tableItem[0].getData();

          tableItem[0].setText(task.getDescription());
          
          tableTaskLog.setText(task.getLog());
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
          // editItemInTablesListener.widgetSelected(null);
        }
      };

  private class ServerConnectionCheckerTimer extends TimerTask {

    @Override
    public void run() {

      Display.getDefault()
          .asyncExec(
              new Runnable() {
                public void run() {
                  if (serverConnectionTimer == null) {
                    return;
                  }

                  List<TreeItem> doneItems = new ArrayList<>();

                  for (TreeItem serverItem : waitingConnectServers) {
                    Server server = getServer(serverItem);
                    if (server == null) {
                      return;
                    }
                    server.updateTreeItemState(serverItem);

                    if (server.isConnected() || !server.getConnectionError().isBlank()) {
                      doneItems.add(serverItem);
                    }
                  }

                  waitingConnectServers.removeAll(doneItems);
                  if (waitingConnectServers.isEmpty()) {
                    serverConnectionTimer.cancel();
                    serverConnectionTimer = null;
                  }

                  for (TreeItem serverItem : doneItems) {
                    Server server = getServer(serverItem);
                    if (server == null) {
                      return;
                    }
                    // server.updateTreeItemState(serverItem);

                    if (server.isConnected()) {
                      updateClustersInTree(serverItem);
                    }
                    if (server.needShowConnectionError()) {
                      Helper.showMessageBox(server.getConnectionError());
                    }
                  }
                }
              });
    }
  }

  private class UpdateListTimer extends TimerTask {

    @Override
    public void run() {
      //      fillTabs();

      Display.getDefault()
          .asyncExec(
              new Runnable() {
                public void run() {
                  refreshCurrentList();
                }
              });
    }
  }

  class TaskChekerTimer extends TimerTask {

    @Override
    public void run() {

      Display.getDefault()
          .asyncExec(
              new Runnable() {
                public void run() {
                  TableItem[] items = tableTasks.getItems();
                  TableItem[] selectItems = tableTasks.getSelection();

                  BackgroundTask.resetCount();

                  for (TableItem tableItem : items) {
                    BackgroundTask task = (BackgroundTask) tableItem.getData();
                    task.update(tableItem);

                    if (selectItems.length > 0
                        && tableItem.equals(selectItems[0])
                        && (task.isRunning()
                            || tableTaskLog.getLineCount() != task.getLog().lines().count())) {
                      tableTaskLog.setText(task.getLog());
                    }
                  }

                  BackgroundTask.setTabTitle(tabTask);

                  if (BackgroundTask.getRunningCount() == 0) {
                    taskTimer.cancel();
                    taskTimer = null;
                  }
                }
              });
    }
  }

  class UserScriptRunner extends SelectionAdapter {

    // поля
    // private Menu menuUserScripts;

    public UserScriptRunner(Menu parentMenu) {
      // this.menuUserScripts = parentMenu;

      addMenuSeparator(parentMenu);
      Menu menuUserScripts = addItemGroupInMenu(parentMenu, "Пользовательские скрипты", null);
      this.fillUserScriptsItems(menuUserScripts);
    }

    public void fillUserScriptsItems(Menu subMenuUserScripts) {
      Arrays.stream(subMenuUserScripts.getItems()).forEach(Widget::dispose);
      // filter на элемент "Обновить" ???

      addItemInMenu(subMenuUserScripts, Strings.CONTEXT_MENU_UPDATE, updateIcon16, this, null);
      addMenuSeparator(subMenuUserScripts);

      // чтение каталога скриптов и создание подэлементов
      readUserScripts()
          .forEach(
              script -> addItemInMenu(subMenuUserScripts, script.getName(), null, this, script));
    }

    private List<File> readUserScripts() {
      if (!config.isWindows()) {
        return new ArrayList<>();
      }

      File userScriptsDir = new File("scripts"); // $NON-NLS-1$
      if (!userScriptsDir.exists()) {
        userScriptsDir.mkdir();
        // можно скачивать скрипты с репозитория
      }
      // userScripts.clear();
      List<File> userScripts = new ArrayList<>();
      if (userScriptsDir.exists() && userScriptsDir.isDirectory()) {
        File[] scripts = userScriptsDir.listFiles();
        for (File script : scripts) {
          if (script.isFile()) {
            userScripts.add(script);
          }
        }
      }
      return userScripts;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
      TreeItem[] items = serversTree.getSelection();
      if (items.length == 0) {
        return;
      }

      Object scriptData = event.widget.getData();
      if (scriptData == null) {
        Menu subMenuUserScripts = ((MenuItem) event.widget).getParent();
        fillUserScriptsItems(subMenuUserScripts);
        return;
      }

      TreeItem infobaseItem = items[0];
      File script = (File) scriptData;

      BackgroundTask backgroundTask = new BackgroundTask(script);
      Map<String, String> params = fillParams(backgroundTask, infobaseItem);
      if (!params.isEmpty()) {
        backgroundTask.setParams(params);
        addToTasksQueue(backgroundTask);
      }
    }
  }

  class RefreshTablesSelectionListener implements Listener {

    private ToolItem mainButton;
    private Menu dropdownMenu;
    private int refreshRate = config.getListRrefreshRate();

    public RefreshTablesSelectionListener(ToolItem mainButton) {
      this.mainButton = mainButton;
      this.mainButton.addListener(SWT.Selection, this);

      dropdownMenu = new Menu(mainButton.getParent().getShell());

      this.add(dropdownMenu, "Автообновление", SWT.CHECK, null);

      // this.add(dropdownMenu, "Задать период автообновления", SWT.PUSH);
      Menu subMenuUpdatePeriod = addItemGroupInMenu(dropdownMenu, "Период", null);
      // подменю с секундами = 1, 2, 5, 10
      this.add(subMenuUpdatePeriod, "1 сек", SWT.RADIO, 1000);
      this.add(subMenuUpdatePeriod, "2 сек", SWT.RADIO, 2000);
      this.add(subMenuUpdatePeriod, "5 сек", SWT.RADIO, 5000);
      this.add(subMenuUpdatePeriod, "10 сек", SWT.RADIO, 10000);
    }

    public void add(Menu parentMenu, String title, int style, Object data) {
      MenuItem menuItem = new MenuItem(parentMenu, style);
      menuItem.setText(title);
      menuItem.setData(data);
      if (style == SWT.CHECK) {
        menuItem.setImage(updateAutoIcon24);
        menuItem.addSelectionListener(checkItemListener);
      }
      if (style == SWT.RADIO) {
        menuItem.setSelection((int) data == refreshRate);
        menuItem.addSelectionListener(radioItemListener);
      }
    }

    SelectionAdapter checkItemListener =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            MenuItem selected = (MenuItem) event.widget;

            if (selected.getStyle() != SWT.CHECK) {
              return;
            }
            if (selected.getSelection()) {
              updateListTimer = new Timer(true);
              updateListTimer.schedule(new UpdateListTimer(), 1000, refreshRate);
              mainButton.setImage(updateAutoIcon24);
            } else {
              updateListTimer.cancel();
              updateListTimer = null;
              mainButton.setImage(updateIcon24);
            }
          }
        };

    SelectionAdapter radioItemListener =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            MenuItem selected = (MenuItem) event.widget;

            if (selected.getStyle() != SWT.RADIO) {
              return;
            }
            if (selected.getSelection()) {
              refreshRate = (int) selected.getData();
              config.setListRrefreshRate(refreshRate);
              if (updateListTimer != null) {
                updateListTimer.cancel();
                updateListTimer = new Timer(true);
                updateListTimer.schedule(new UpdateListTimer(), 1000, refreshRate);
              }
            }
          }
        };

    @Override
    public void handleEvent(Event event) {
      if (event.detail == SWT.ARROW) {
        ToolItem item = (ToolItem) event.widget;
        Rectangle rect = item.getBounds();
        Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
        dropdownMenu.setLocation(pt.x, pt.y + rect.height);
        dropdownMenu.setVisible(true);
      } else {
        refreshCurrentList();
      }
    }
  }

  private static class Strings {

    static final String MENU_SERVERS = getString("MainMenu.Servers");
    static final String MENU_FIND_SERVERS = getString("MainMenu.FindServers");
    static final String MENU_CONNECT_ALL_SERVERS = getString("MainMenu.ConnectAllServers");
    static final String MENU_DISCONNECT_ALL_SERVERS = getString("MainMenu.DisonnectAllServers");
    static final String MENU_SERVICE = getString("MainMenu.Service");
    static final String MENU_OPEN_SETTINGS = getString("MainMenu.OpenSettings");
    static final String MENU_ABOUT = getString("MainMenu.About");
    static final String MENU_DONATE = getString("MainMenu.Donate");
    static final String COLUMN_SERVER = getString("ColumnServer");
    static final String TREE_INFOBASES_COUNT = getString("InfobasesCount");

    static final String CONTEXT_MENU_UPDATE = getString("ContextMenu.Update");
    static final String CONTEXT_MENU_UPDATE_F5 = CONTEXT_MENU_UPDATE.concat("\tF5");

    static final String CONTEXT_MENU_CONNECT_TO_SERVER = getString("ContextMenu.ConnectToServer");
    static final String CONTEXT_MENU_DISCONNECT_OF_SERVER =
        getString("ContextMenu.DisconnectOfServer");
    static final String CONTEXT_MENU_SHOW_CONNECTION_ERROR =
        getString("ContextMenu.ShowConnectionError");

    static final String CONTEXT_MENU_ADD = getString("ContextMenu.Add");
    static final String CONTEXT_MENU_EDIT = getString("ContextMenu.Edit");
    static final String CONTEXT_MENU_DELETE = getString("ContextMenu.Delete");
    static final String CONTEXT_MENU_WATCH_SESSION = getString("ContextMenu.WatchSession");

    static final String CONTEXT_MENU_ADD_HOTKEY = CONTEXT_MENU_ADD.concat("\tIns");
    static final String CONTEXT_MENU_EDIT_HOTKEY = CONTEXT_MENU_EDIT.concat("\tF2");
    static final String CONTEXT_MENU_DELETE_HOTKEY = CONTEXT_MENU_DELETE.concat("\tDEL");

    static final String CONTEXT_MENU_COPY_CELL =
        getString("ContextMenu.CopyCell").concat("\tCtrl+C");

    static final String CONTEXT_MENU_MOVE_UP = getString("ContextMenu.MoveUp");
    static final String CONTEXT_MENU_MOVE_DOWN = getString("ContextMenu.MoveDown");
    static final String CONTEXT_MENU_ORGANIZE_SERVERS = getString("ContextMenu.OrganizeServers");

    static final String CONTEXT_MENU_CREATE_CLUSTER = getString("ContextMenu.CreateCluster");
    static final String CONTEXT_MENU_EDIT_CLUSTER = getString("ContextMenu.EditCluster");
    static final String CONTEXT_MENU_DELETE_CLUSTER = getString("ContextMenu.DeleteCluster");
    static final String CONTEXT_MENU_ADMINS = getString("ContextMenu.Admins");
    static final String CONTEXT_MENU_RESTART_PROCESSES = getString("ContextMenu.RestartProcesses");
    static final String CONTEXT_MENU_APPLY_PARTIAL_RULE = getString("ContextMenu.ApplyPartialRule");
    static final String CONTEXT_MENU_APPLY_FULL_RULE = getString("ContextMenu.ApplyFullRule");

    static final String CONTEXT_MENU_CREATE_WORKING_SERVER =
        getString("ContextMenu.CreateWorkingServer");
    static final String CONTEXT_MENU_EDIT_WORKING_SERVER =
        getString("ContextMenu.EditWorkingServer");
    static final String CONTEXT_MENU_EDIT_WORKING_SERVER_F2 =
        getString("ContextMenu.EditWorkingServer").concat("\tF2");
    static final String CONTEXT_MENU_DELETE_WORKING_SERVER_DEL =
        getString("ContextMenu.DeleteWorkingServer").concat("\tDEL");

    static final String CONTEXT_MENU_CREATE_INFOBASE = getString("ContextMenu.CreateInfobase");
    static final String CONTEXT_MENU_UPDATE_INFOBASES = getString("ContextMenu.UpdateInfobases");
    static final String CONTEXT_MENU_ORDER_INFOBASES_BY = getString("ContextMenu.OrderInfobasesBy");
    static final String CONTEXT_MENU_ORDER_INFOBASES_BYDEFAULT =
        getString("ContextMenu.OrderInfobasesByDefault");
    static final String CONTEXT_MENU_ORDER_INFOBASES_BYNAME =
        getString("ContextMenu.OrderInfobasesByName");
    static final String CONTEXT_MENU_ORDER_INFOBASES_BYFAFORITES_ANDNAME =
        getString("ContextMenu.OrderInfobasesByFaforitesAndName");

    static final String CONTEXT_MENU_COPY_INFOBASE = getString("ContextMenu.CopyInfobase");
    static final String CONTEXT_MENU_EDIT_INFOBASE = getString("ContextMenu.EditInfobase");
    static final String CONTEXT_MENU_DELETE_INFOBASE = getString("ContextMenu.DeleteInfobase");

    static final String CONTEXT_MENU_ADD_IN_FAVORITES = getString("ContextMenu.AddInFavorites");
    static final String CONTEXT_MENU_DELETE_FROM_FAVORITES =
        getString("ContextMenu.DeleteFromFavorites");

    static final String CONTEXT_MENU_SESSION_MANAGE = getString("ContextMenu.SessionManage");
    static final String CONTEXT_MENU_LOCK_SESSIONS_NOW = getString("ContextMenu.LockSessionsNow");
    static final String CONTEXT_MENU_TERMINATE_ALL_SESSIONS =
        getString("ContextMenu.TerminateAllSessions");
    static final String CONTEXT_MENU_TERMINATE_USERS_SESSIONS =
        getString("ContextMenu.TerminateUsersSessions");

    static final String CONTEXT_MENU_VIEW_HOTKEY = getString("ContextMenu.View").concat("\tF2");
    static final String CONTEXT_MENU_KILL_SESSION_DEL =
        getString("ContextMenu.KillSession").concat("\tDEL");
    static final String CONTEXT_MENU_KILL_CONNECTION_DEL =
        getString("ContextMenu.KillConnection").concat("\tDEL");

    static final String CONTEXT_MENU_INFOBASE_ACTIONS = getString("ContextMenu.InfobaseActions.Group");
    static final String CONTEXT_MENU_RUN_DESIGNER = getString("ContextMenu.InfobaseActions.RunDesigner");
    static final String CONTEXT_MENU_RUN_ENTERPRISE = getString("ContextMenu.InfobaseActions.RunEnterprise");
    static final String CONTEXT_MENU_DUMP_CF = getString("ContextMenu.InfobaseActions.DumpCf");
    static final String CONTEXT_MENU_LOAD_CF = getString("ContextMenu.InfobaseActions.LoadCf");
    static final String CONTEXT_MENU_DUMP_DT = getString("ContextMenu.InfobaseActions.DumpDt");
    static final String CONTEXT_MENU_LOAD_DT = getString("ContextMenu.InfobaseActions.LoadDt");

    static final String ERROR_AUTH_FOR_RESTART_WP = getString("ErrorAuthForRestartWp");

    static String getString(String key) {
      return Messages.getString("ViewerArea." + key); //$NON-NLS-1$
    }
  }
}
