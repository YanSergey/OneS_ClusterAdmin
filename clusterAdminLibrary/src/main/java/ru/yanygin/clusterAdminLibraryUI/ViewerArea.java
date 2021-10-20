package ru.yanygin.clusterAdminLibraryUI;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.ColumnProperties;
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibrary.ConnectionInfoExtended;
import ru.yanygin.clusterAdminLibrary.IInfoExtended;
import ru.yanygin.clusterAdminLibrary.LockInfoExtended;
import ru.yanygin.clusterAdminLibrary.Server;
import ru.yanygin.clusterAdminLibrary.SessionInfoExtended;
import ru.yanygin.clusterAdminLibrary.WorkingProcessInfoExtended;
import ru.yanygin.clusterAdminLibrary.WorkingServerInfoExtended;

/** Area for viewing servers. */
public class ViewerArea extends Composite {

  Image serverIcon;
  Image serverIconUp;
  Image serverIconDown;
  Image serverIconConnecting;
  Image workingServerIcon;
  Image infobaseIcon;
  Image infobasesIcon;
  Image clusterIcon;
  Image userIcon;
  Image sleepUserIcon;
  Image serviceIcon;
  Image connectionIcon;
  Image locksIcon;
  Image workingProcessesIcon;
  Image workingProcessIcon;
  Image connectActionIcon;
  Image disconnectActionIcon;
  Image editIcon;
  Image addIcon;
  Image deleteIcon;
  Image lockUsersIcon;
  Image updateIcon;

  Tree serversTree;
  TreeItem currentTreeItem;
  Menu serverMenu;
  MenuItem menuItemConnectServer;
  MenuItem menuItemDisconnectServer;
  MenuItem menuItemShowConnectionError;
  Menu clusterMenu;
  Menu workingServerMenu;
  Menu infobaseNodeMenu;
  Menu infobaseMenu;

  TabItem tabSessions;
  TabItem tabConnections;
  TabItem tabLocks;
  TabItem tabWorkingProcesses;
  TabItem tabWorkingServers;
  TabItem currentTabitem;

  Table tableSessions;
  Table tableConnections;
  Table tableLocks;
  Table tableWorkingProcesses;
  Table tableWorkingServers;
  // Menu tableSessionsMenu;

  Map<String, String> sessionColumnsMap = new LinkedHashMap<>();
  Map<String, String> connectionColumnsMap = new LinkedHashMap<>();
  Map<String, String> lockColumnsMap = new LinkedHashMap<>();
  Map<String, String> wpColumnsMap = new LinkedHashMap<>();
  Map<String, String> wsColumnsMap = new LinkedHashMap<>();

  TreeColumn columnServer;

  static final Logger LOGGER = LoggerFactory.getLogger("ClusterProvider"); //$NON-NLS-1$

  static final Color standardColor = new Color(0, 0, 0);
  static final Color newItemColor = new Color(0, 200, 0);
  static final Color deletedItemColor = new Color(150, 0, 0);
  static final Color shadowItemColor = new Color(160, 160, 160);
  static final Color watchedSessionColor = new Color(0, 128, 255);

  FontData systemFontData = getDisplay().getSystemFont().getFontData()[0];
  Font fontNormal =
      new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.NORMAL);
  Font fontBold =
      new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.BOLD);
  TableItem lastSelectItem = null;
  int lastSelectColumn;
  List<String> watchedSessions = new ArrayList<>();

  enum TreeItemType {
    SERVER,
    CLUSTER,
    INFOBASE_NODE,
    INFOBASE,
    WORKINGPROCESS_NODE,
    WORKINGPROCESS,
    WORKINGSERVER_NODE,
    WORKINGSERVER
  }

  static final String SERVER_INFO = "ServerInfo"; //$NON-NLS-1$
  static final String CLUSTER_ID = "ClusterId"; //$NON-NLS-1$
  static final String INFOBASE_ID = "InfobaseId"; //$NON-NLS-1$
  static final String WORKINGPROCESS_ID = "WorkingProcessId"; //$NON-NLS-1$
  static final String WORKINGSERVER_ID = "WorkingServerId"; //$NON-NLS-1$
  static final String SESSION_ID = "SessionId"; //$NON-NLS-1$
  static final String CONNECTION_ID = "ConnectionId"; //$NON-NLS-1$

  ClusterProvider clusterProvider;

  // @Slf4j
  /** Area for viewing servers. */
  public ViewerArea(
      Composite parent, int style, Menu menu, ToolBar toolBar, ClusterProvider clusterProvider) {
    super(parent, style);

    this.clusterProvider = clusterProvider;
    this.clusterProvider.readConfig();

    SashForm sashForm = new SashForm(this, SWT.NONE);

    initIcon();

    // toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT); // Для отладки
    // toolBar.setBounds(0, 0, 500, 23); // Для отладки

    // initToolbar(parent, toolBar);
    initMainMenu(sashForm, menu);

    initServersTree(sashForm);

    TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);

    tabFolder.addSelectionListener(
        new SelectionAdapter() {

          @Override
          public void widgetSelected(SelectionEvent evt) {
            currentTabitem = tabFolder.getSelection()[0];
            clickItemInServerTree(1);
          }
        });

    initSessionTable(tabFolder);
    initConnectionsTable(tabFolder);
    initLocksTable(tabFolder);
    initWorkingProcessesTable(tabFolder);
    initWorkingServersTable(tabFolder);

    this.setLayout(new FillLayout(SWT.HORIZONTAL));

    // Заполнение списка серверов
    clusterProvider
        .getServers()
        .forEach(
            (serverKey, server) -> {
              addServerItemInServersTree(server);
            });

    // Пропорции областей
    sashForm.setWeights(3, 10);

    connectToAllServers(false);
  }

  @Override
  public void addPaintListener(PaintListener listener) { // не работает
    connectToAllServers(false);

    super.addPaintListener(listener);
  }

  // public void open() {
  //  connectToAllServers();
  // }

  private void initIcon() {
    LOGGER.info("Start init icon"); //$NON-NLS-1$

    serverIcon = getImage("server_24.png"); //$NON-NLS-1$
    serverIconUp = getImage("server_up_24.png"); //$NON-NLS-1$
    serverIconDown = getImage("server_down_24.png"); //$NON-NLS-1$
    serverIconConnecting = getImage("server_connecting_24.png"); //$NON-NLS-1$
    workingServerIcon = getImage("working_server_24.png"); //$NON-NLS-1$
    infobaseIcon = getImage("infobase_24.png"); //$NON-NLS-1$
    infobasesIcon = getImage("infobases_24.png"); //$NON-NLS-1$
    clusterIcon = getImage("cluster_24.png"); //$NON-NLS-1$

    userIcon = getImage("user.png"); //$NON-NLS-1$
    sleepUserIcon = getImage("sleepUser.png"); //$NON-NLS-1$
    serviceIcon = getImage("service.png"); //$NON-NLS-1$

    connectionIcon = getImage("connection.png"); //$NON-NLS-1$
    locksIcon = getImage("lock_16.png"); //$NON-NLS-1$

    workingProcessesIcon = getImage("wps.png"); //$NON-NLS-1$
    workingProcessIcon = getImage("wp.png"); //$NON-NLS-1$

    connectActionIcon = getImage("connect_action_24.png"); //$NON-NLS-1$
    disconnectActionIcon = getImage("disconnect_action_24.png"); //$NON-NLS-1$

    editIcon = getImage("edit_16.png"); //$NON-NLS-1$
    addIcon = getImage("add_16.png"); //$NON-NLS-1$
    deleteIcon = getImage("delete_16.png"); //$NON-NLS-1$
    lockUsersIcon = getImage("lock_users_16.png"); //$NON-NLS-1$
    updateIcon = getImage("update.png"); //$NON-NLS-1$

    LOGGER.info("Icon init succesfully"); //$NON-NLS-1$
  }

  private void initToolbar(Composite parent, ToolBar toolBar) {
    // ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);

    // final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
    // toolBar.setCursor(handCursor);
    // // Cursor needs to be explicitly disposed
    // toolBar.addDisposeListener(new DisposeListener() {
    // public void widgetDisposed(DisposeEvent e) {
    // if (handCursor.isDisposed() == false) {
    // handCursor.dispose();
    // }
    // }
    // });

    ToolItem toolBarItemFindNewServers = new ToolItem(toolBar, SWT.NONE);
    toolBarItemFindNewServers.setText(
        Messages.getString("ViewerArea.FindNewServers")); //$NON-NLS-1$
    toolBarItemFindNewServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            List<String> newServers = clusterProvider.findNewServers();
            if (!newServers.isEmpty()) {
              fillServersList();
            }
          }
        });

    ToolItem toolBarItemConnectAllServers = new ToolItem(toolBar, SWT.NONE);
    toolBarItemConnectAllServers.setText(
        Messages.getString("ViewerArea.ConnectToAllServers")); //$NON-NLS-1$
    toolBarItemConnectAllServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            connectToAllServers(false);
          }
        });
  }

  private void initMainMenu(Composite parent, Menu mainMenu) {

    if (mainMenu == null) {
      return;
    }
    Menu mainMenuServersParent = new Menu(mainMenu);
    MenuItem mainMenuServers = new MenuItem(mainMenu, SWT.CASCADE);
    mainMenuServers.setText(Messages.getString("ViewerArea.Servers")); //$NON-NLS-1$
    mainMenuServers.setMenu(mainMenuServersParent);

    MenuItem toolBarItemFindNewServers = new MenuItem(mainMenuServersParent, SWT.NONE);
    toolBarItemFindNewServers.setText(
        Messages.getString("ViewerArea.FindNewServers")); //$NON-NLS-1$
    toolBarItemFindNewServers.setEnabled(false);
    toolBarItemFindNewServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            List<String> newServers = clusterProvider.findNewServers();
            if (!newServers.isEmpty()) {
              fillServersList();
            }
          }
        });

    MenuItem toolBarItemConnectAllServers = new MenuItem(mainMenuServersParent, SWT.NONE);
    toolBarItemConnectAllServers.setText(
        Messages.getString("ViewerArea.ConnectToAllServers")); //$NON-NLS-1$
    toolBarItemConnectAllServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            connectToAllServers(true);
          }
        });

    MenuItem toolBarItemDisonnectAllServers = new MenuItem(mainMenuServersParent, SWT.NONE);
    toolBarItemDisonnectAllServers.setText(
        Messages.getString("ViewerArea.DisonnectFromAllServers")); //$NON-NLS-1$
    toolBarItemDisonnectAllServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            diconnectFromAllServers();
          }
        });

    Menu mainMenuServiceParent = new Menu(mainMenu);
    MenuItem mainMenuService = new MenuItem(mainMenu, SWT.CASCADE);
    mainMenuService.setText(Messages.getString("ViewerArea.Service")); //$NON-NLS-1$
    mainMenuService.setMenu(mainMenuServiceParent);

    MenuItem toolBarItemOpenSettings = new MenuItem(mainMenuServiceParent, SWT.NONE);
    toolBarItemOpenSettings.setText(Messages.getString("ViewerArea.OpenSettings")); //$NON-NLS-1$
    toolBarItemOpenSettings.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {

            SettingsDialog settingsDialog;
            try {
              settingsDialog = new SettingsDialog(getParent().getDisplay().getActiveShell());
            } catch (Exception excp) {
              LOGGER.error("Error init SettingsDialog", excp); //$NON-NLS-1$
              return;
            }
            int dialogResult = settingsDialog.open();
            if (dialogResult == 0) {
              clusterProvider.saveConfig();
              for (TreeItem item : serversTree.getItems()) {
                updateClustersInTree(item);
              }
            }
          }
        });

    MenuItem toolBarItemOpenAbout = new MenuItem(mainMenuServiceParent, SWT.NONE);
    toolBarItemOpenAbout.setText(Messages.getString("ViewerArea.About")); //$NON-NLS-1$
    toolBarItemOpenAbout.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {

            AboutDialog aboutDialog;
            try {
              aboutDialog = new AboutDialog(getParent().getDisplay().getActiveShell());
            } catch (Exception excp) {
              LOGGER.error("Error init AboutDialog", excp); //$NON-NLS-1$
              return;
            }
            aboutDialog.open();
          }
        });
  }

  private void initServersTree(SashForm sashForm) {

    serversTree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
    serversTree.setHeaderVisible(true);
    serversTree.setSortDirection(SWT.UP);

    serversTree.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseDown(MouseEvent e) {
            clickItemInServerTree(e.button);
          }
        });

    initServersTreeContextMenu();

    columnServer = new TreeColumn(serversTree, SWT.LEFT);
    columnServer.setText(Messages.getString("ViewerArea.Server")); //$NON-NLS-1$
    columnServer.setWidth(350);

    /////////////////////////
    // сортировка не работает
    //    columnServer.addListener(SWT.Selection, sortListener);
    //    columnServer.addSelectionListener(
    //        new SelectionAdapter() {
    //          @Override
    //          public void widgetSelected(SelectionEvent e) {
    //            serversTree.setSortDirection(
    //                serversTree.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
    //            serversTree.setSortColumn(columnServer);
    //          }
    //        });
    //
    //    Listener sortListener =
    //        new Listener() {
    //          public void handleEvent(Event e) {
    //            TreeItem[] items = serversTree.getItems();
    //            Collator collator = Collator.getInstance(Locale.getDefault());
    //            TreeColumn column = (TreeColumn) e.widget;
    //            int index = column == columnServer ? 0 : 1;
    //            for (int i = 1; i < items.length; i++) {
    //              String value1 = items[i].getText(index);
    //              for (int j = 0; j < i; j++) {
    //                String value2 = items[j].getText(index);
    //                if (collator.compare(value1, value2) < 0) {
    //                  String[] values = {items[i].getText(0), items[i].getText(1)};
    //                  items[i].dispose();
    //                  TreeItem item = new TreeItem(serversTree, SWT.NONE, j);
    //                  item.setText(values);
    //                  items = serversTree.getItems();
    //                  break;
    //                }
    //              }
    //            }
    //            serversTree.setSortColumn(column);
    //          }
    //        };
    // сортировка не работает
    /////////////////////////

  }

  private void initServersTreeContextMenu() {

    // Server Menu
    serverMenu = new Menu(serversTree);

    // установка активности элементов контекстного меню
    serverMenu.addListener(
        SWT.Show,
        new Listener() {
          @Override
          public void handleEvent(Event event) {

            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            TreeItem serverItem = item[0];
            Server server = getCurrentServerConfig(serverItem);
            boolean serverIsConnected = server.isConnected();
            boolean serverIsErrorConnected = !server.getConnectionError().isBlank();

            MenuItem[] menuItems = serverMenu.getItems();

            for (MenuItem menuItem : menuItems) {
              if (menuItem == menuItemConnectServer) { //TODO menuItem.equals(menuItemConnectServer)
                menuItem.setEnabled(!serverIsConnected);
              }
              if (menuItem == menuItemDisconnectServer) {
                menuItem.setEnabled(serverIsConnected);
              }
              if (menuItem == menuItemShowConnectionError) {
                menuItem.setEnabled(serverIsErrorConnected);
              }
            }
          }
        });

    initServerMenu();
    initClusterMenu();
    initWorkingServerMenu();
    initInfobaseNodeMenu();
    initInfobaseMenu();

    // set active menu
    serversTree.setMenu(serverMenu);
  }

  private void initServerMenu() {

    menuItemConnectServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemConnectServer.setText(Messages.getString("ViewerArea.ConnectToServer")); //$NON-NLS-1$
    menuItemConnectServer.setImage(connectActionIcon);
    menuItemConnectServer.setEnabled(false);
    menuItemConnectServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {

            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            connectServerItem(item[0], false);
          }
        });

    menuItemDisconnectServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemDisconnectServer.setText(
        Messages.getString("ViewerArea.DisconnectOfServer")); //$NON-NLS-1$
    menuItemDisconnectServer.setImage(disconnectActionIcon);
    menuItemDisconnectServer.setEnabled(false);
    menuItemDisconnectServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {

            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            disconnectServerItem(item[0]);
          }
        });

    menuItemShowConnectionError = new MenuItem(serverMenu, SWT.NONE);
    menuItemShowConnectionError.setText(
        Messages.getString("ViewerArea.ShowConnectionError")); //$NON-NLS-1$
    menuItemShowConnectionError.setEnabled(false);
    menuItemShowConnectionError.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {

            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            var messageBox = new MessageBox(Display.getDefault().getActiveShell());
            messageBox.setMessage(getCurrentServerConfig(item[0]).getConnectionError());
            messageBox.open();
          }
        });

    addMenuSeparator(serverMenu);

    MenuItem menuItemAddNewServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemAddNewServer.setText(Messages.getString("ViewerArea.AddServer")); //$NON-NLS-1$
    menuItemAddNewServer.setImage(addIcon);
    menuItemAddNewServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {

            Server newServer = clusterProvider.createNewServer();
            CreateEditServerDialog connectionDialog;
            try {
              connectionDialog =
                  new CreateEditServerDialog(getParent().getDisplay().getActiveShell(), newServer);
            } catch (Exception excp) {
              excp.printStackTrace();
              LOGGER.error("Error init CreateEditServerDialog for new server", excp); //$NON-NLS-1$
              return;
            }

            int dialogResult = connectionDialog.open();
            if (dialogResult != 0) {
              return;
            }

            clusterProvider.addNewServer(newServer);
            TreeItem newServerItem = addServerItemInServersTree(newServer);
            updateClustersInTree(newServerItem);
          }
        });

    MenuItem menuItemEditServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemEditServer.setText(Messages.getString("ViewerArea.EditServer")); //$NON-NLS-1$
    menuItemEditServer.setImage(editIcon);
    menuItemEditServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {

            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            TreeItem serverItem = item[0];
            Server server = getCurrentServerConfig(serverItem);
            CreateEditServerDialog connectionDialog;
            try {
              connectionDialog =
                  new CreateEditServerDialog(getParent().getDisplay().getActiveShell(), server);
            } catch (Exception excp) {
              excp.printStackTrace();
              LOGGER.error(
                  "Error init CreateEditServerDialog for server {}", //$NON-NLS-1$
                  server.getTreeDescription(),
                  excp);
              return;
            }

            int dialogResult = connectionDialog.open();
            if (dialogResult == 0) {
              // перерисовать в дереве
              serverItem.setText(new String[] {server.getTreeDescription()});
              clusterProvider.saveConfig();
              updateClustersInTree(serverItem);
            }
          }
        });

    MenuItem menuItemUpdateServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemUpdateServer.setText(Messages.getString("ViewerArea.Update")); //$NON-NLS-1$
    menuItemUpdateServer.setImage(updateIcon);
    menuItemUpdateServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            updateClustersInTree(item[0]);
          }
        });

    addMenuSeparator(serverMenu);

    MenuItem menuItemDeleteServer = new MenuItem(serverMenu, SWT.NONE);
    menuItemDeleteServer.setText(Messages.getString("ViewerArea.RemoveServer")); //$NON-NLS-1$
    menuItemDeleteServer.setImage(deleteIcon);
    menuItemDeleteServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            TreeItem serverItem = item[0];
            Server server = getCurrentServerConfig(serverItem);
            clusterProvider.removeServer(server);
            disposeTreeItemWithChildren(serverItem);
          }
        });
  }

  private void initClusterMenu() {
    // Cluster Menu
    clusterMenu = new Menu(serversTree);

    MenuItem menuItemCreateCluster = new MenuItem(clusterMenu, SWT.NONE);
    menuItemCreateCluster.setText(Messages.getString("ViewerArea.CreateCluster")); //$NON-NLS-1$
    menuItemCreateCluster.setImage(addIcon);
    menuItemCreateCluster.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);

            CreateEditClusterDialog editClusterDialog;
            try {
              editClusterDialog =
                  new CreateEditClusterDialog(
                      getParent().getDisplay().getActiveShell(), server, null);
            } catch (Exception excp) {
              LOGGER.error(
                  "Error init CreateEditClusterDialog for new cluster", //$NON-NLS-1$
                  excp);
              return;
            }

            int dialogResult = editClusterDialog.open();
            if (dialogResult == 0) {
              updateClustersInTree(item[0].getParentItem());
            }
          }
        });

    MenuItem menuItemEditCluster = new MenuItem(clusterMenu, SWT.NONE);
    menuItemEditCluster.setText(Messages.getString("ViewerArea.EditCluster")); //$NON-NLS-1$
    menuItemEditCluster.setImage(editIcon);
    menuItemEditCluster.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);

            CreateEditClusterDialog editClusterDialog;
            try {
              editClusterDialog =
                  new CreateEditClusterDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId);
            } catch (Exception excp) {
              LOGGER.error(
                  "Error init CreateEditClusterDialog for cluster id {}", //$NON-NLS-1$
                  clusterId,
                  excp);
              return;
            }

            int dialogResult = editClusterDialog.open();
            if (dialogResult == 0) {
              updateClustersInTree(item[0]);
            }
          }
        });

    MenuItem menuItemUpdateCluster = new MenuItem(clusterMenu, SWT.NONE);
    menuItemUpdateCluster.setText(Messages.getString("ViewerArea.Update")); //$NON-NLS-1$
    menuItemUpdateCluster.setImage(updateIcon);
    menuItemUpdateCluster.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            fillChildrenItemsOfCluster(item[0], server);
          }
        });

    addMenuSeparator(clusterMenu);

    MenuItem menuItemDeleteCluster = new MenuItem(clusterMenu, SWT.NONE);
    menuItemDeleteCluster.setText(Messages.getString("ViewerArea.DeleteCluster")); //$NON-NLS-1$
    menuItemDeleteCluster.setImage(deleteIcon);
    menuItemDeleteCluster.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);

            var messageBox =
                new MessageBox(
                    Display.getDefault().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            messageBox.setMessage(
                Messages.getString("ViewerArea.DeleteClusterQuestion")); //$NON-NLS-1$
            int rc = messageBox.open();

            if (rc == SWT.YES && server.unregCluster(clusterId)) {
              item[0].dispose();
            }
          }
        });
  }

  private void initWorkingServerMenu() {

    workingServerMenu = new Menu(serversTree);

    MenuItem menuItemCreateWorkingServer = new MenuItem(workingServerMenu, SWT.NONE);
    menuItemCreateWorkingServer.setText(
        Messages.getString("ViewerArea.CreateWorkingServer")); //$NON-NLS-1$
    menuItemCreateWorkingServer.setImage(addIcon);
    menuItemCreateWorkingServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);

            CreateEditWorkingServerDialog editWorkingServerDialog;
            try {
              editWorkingServerDialog =
                  new CreateEditWorkingServerDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId, null);
            } catch (Exception excp) {
              LOGGER.error(
                  "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                  clusterId,
                  excp);
              return;
            }

            int dialogResult = editWorkingServerDialog.open();
            if (dialogResult == 0) {
              var newWorkingServerUuid = editWorkingServerDialog.getNewWorkingServerId();
              if (newWorkingServerUuid != null) {
                IWorkingServerInfo workingServerInfo =
                    server.getWorkingServerInfo(clusterId, newWorkingServerUuid);
                addWorkingServerItemInNode(item[0].getParentItem(), workingServerInfo);
              }
            }
          }
        });

    MenuItem menuItemEditWorkingServer = new MenuItem(workingServerMenu, SWT.NONE);
    menuItemEditWorkingServer.setText(
        Messages.getString("ViewerArea.EditWorkingServer")); //$NON-NLS-1$
    menuItemEditWorkingServer.setImage(editIcon);
    menuItemEditWorkingServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID workingServerId = getCurrentWorkingServerId(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);

            CreateEditWorkingServerDialog editClusterDialog;
            try {
              editClusterDialog =
                  new CreateEditWorkingServerDialog(
                      getParent().getDisplay().getActiveShell(),
                      server,
                      clusterId,
                      workingServerId);
            } catch (Exception excp) {
              excp.printStackTrace();
              LOGGER.error(
                  "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                  workingServerId,
                  excp);
              return;
            }

            editClusterDialog.open();
          }
        });
  }

  private void initInfobaseNodeMenu() {

    infobaseNodeMenu = new Menu(serversTree);

    MenuItem menuItemNewInfobase = new MenuItem(infobaseNodeMenu, SWT.NONE);
    menuItemNewInfobase.setText(Messages.getString("ViewerArea.CreateInfobase")); //$NON-NLS-1$
    menuItemNewInfobase.setImage(addIcon);
    menuItemNewInfobase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);

            CreateInfobaseDialog infobaseDialog;
            try {
              infobaseDialog =
                  new CreateInfobaseDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId, null);
            } catch (Exception excp) {
              LOGGER.error("Error in CreateInfobaseDialog", excp); //$NON-NLS-1$
              return;
            }

            int dialogResult = infobaseDialog.open();
            if (dialogResult == 0) {
              var newInfobaseUuid = infobaseDialog.getNewInfobaseUuid();
              if (newInfobaseUuid != null) {
                IInfoBaseInfoShort infoBaseInfo =
                    server.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
                addInfobaseItemInInfobaseNode(item[0], infoBaseInfo);
              }
            }
          }
        });

    MenuItem menuItemUpdateInfobases = new MenuItem(infobaseNodeMenu, SWT.NONE);
    menuItemUpdateInfobases.setText(
        Messages.getString("ViewerArea.UpdateInfobases")); //$NON-NLS-1$
    menuItemUpdateInfobases.setImage(updateIcon);
    menuItemUpdateInfobases.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            fillInfobasesOfCluster(item[0].getParentItem(), server);
          }
        });
  }

  private void initInfobaseMenu() {

    infobaseMenu = new Menu(serversTree);

    MenuItem menuItemCopyInfobase = new MenuItem(infobaseMenu, SWT.NONE);
    menuItemCopyInfobase.setText(
        Messages.getString("ViewerArea.CreateNewInfobaseUsingThis")); //$NON-NLS-1$
    menuItemCopyInfobase.setImage(addIcon);
    menuItemCopyInfobase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);
            UUID sampleInfobaseId = getCurrentInfobaseId(item[0]);

            CreateInfobaseDialog infobaseDialog;
            try {
              infobaseDialog =
                  new CreateInfobaseDialog(
                      getParent().getDisplay().getActiveShell(),
                      server,
                      clusterId,
                      sampleInfobaseId);
            } catch (Exception excp) {
              LOGGER.error("Error in CreateInfobaseDialog", excp); //$NON-NLS-1$
              return;
            }

            int dialogResult = infobaseDialog.open();
            if (dialogResult == 0) {
              var newInfobaseUuid = infobaseDialog.getNewInfobaseUuid();
              if (newInfobaseUuid != null) {
                IInfoBaseInfoShort infoBaseInfo =
                    server.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
                addInfobaseItemInInfobaseNode(item[0].getParentItem(), infoBaseInfo);
              }
            }
          }
        });

    MenuItem menuItemEditInfobase = new MenuItem(infobaseMenu, SWT.NONE);
    menuItemEditInfobase.setText(Messages.getString("ViewerArea.EditInfobase")); //$NON-NLS-1$
    menuItemEditInfobase.setImage(editIcon);
    menuItemEditInfobase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);
            UUID infobaseId = getCurrentInfobaseId(item[0]);

            EditInfobaseDialog infobaseDialog;
            try {
              infobaseDialog =
                  new EditInfobaseDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
            } catch (Exception excp) {
              excp.printStackTrace();
              return;
            }

            int dialogResult = infobaseDialog.open();
            if (dialogResult == 0) {
              // TODO обновить в дереве description инфобазы
              // server.clusterConnector.updateInfoBase(server.clusterID, infoBaseInfo);
            }
          }
        });

    MenuItem menuItemDeleteInfobase = new MenuItem(infobaseMenu, SWT.NONE);
    menuItemDeleteInfobase.setText(Messages.getString("ViewerArea.DeleteInfobase")); //$NON-NLS-1$
    menuItemDeleteInfobase.setImage(deleteIcon);
    menuItemDeleteInfobase.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);
            UUID infobaseId = getCurrentInfobaseId(item[0]);

            DropInfobaseDialog infobaseDialog;
            try {
              infobaseDialog =
                  new DropInfobaseDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
            } catch (Exception excp) {
              excp.printStackTrace();
              return;
            }

            int dialogResult = infobaseDialog.open();
            if (dialogResult == 0) {
              item[0].dispose();
            }
          }
        });

    // Создание вложенных подпунктов меню
    Menu infobaseSubMenuSessionManage = new Menu(infobaseMenu);

    MenuItem infobaseMenuSessionManage = new MenuItem(infobaseMenu, SWT.CASCADE);
    infobaseMenuSessionManage.setText(
        Messages.getString("ViewerArea.SessionManage")); //$NON-NLS-1$
    infobaseMenuSessionManage.setMenu(infobaseSubMenuSessionManage);
    // infobaseSubMenuSessionManage.setImage(terminateSessionIcon);

    MenuItem menuItemLockUserSessionsNow = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
    menuItemLockUserSessionsNow.setText(
        Messages.getString("ViewerArea.LockSessionsNow")); //$NON-NLS-1$
    menuItemLockUserSessionsNow.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            TreeItem selectItem = item[0];

            Server server = getCurrentServerConfig(selectItem);
            UUID clusterId = getCurrentClusterId(selectItem);
            UUID infobaseId = getCurrentInfobaseId(selectItem);

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
        });

    MenuItem menuItemTerminateAllSessions = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
    menuItemTerminateAllSessions.setText(
        Messages.getString("ViewerArea.TerminateAllSessions")); //$NON-NLS-1$
    menuItemTerminateAllSessions.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);
            UUID infobaseId = getCurrentInfobaseId(item[0]);

            server.terminateAllSessionsOfInfobase(clusterId, infobaseId, false);
          }
        });

    MenuItem menuItemTerminateUserSessions = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
    menuItemTerminateUserSessions.setText(
        Messages.getString("ViewerArea.TerminateUsersSessions")); //$NON-NLS-1$
    menuItemTerminateUserSessions.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TreeItem[] item = serversTree.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = getCurrentServerConfig(item[0]);
            UUID clusterId = getCurrentClusterId(item[0]);
            UUID infobaseId = getCurrentInfobaseId(item[0]);

            server.terminateAllSessionsOfInfobase(clusterId, infobaseId, true);
          }
        });
  }

  private void initSessionTable(TabFolder tabFolder) {

    tabSessions = new TabItem(tabFolder, SWT.NONE);
    tabSessions.setText(Messages.getString("ViewerArea.Sessions")); //$NON-NLS-1$

    tableSessions = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);
    tabSessions.setControl(tableSessions);
    tableSessions.setHeaderVisible(true);
    tableSessions.setLinesVisible(true);

    initSessionsTableContextMenu();

    SessionInfoExtended.initColumnsName(sessionColumnsMap);

    ColumnProperties columnProperties =
        ClusterProvider.getCommonConfig().getSessionColumnProperties();

    String[] columnNameList = sessionColumnsMap.keySet().toArray(new String[0]);
    for (String columnName : columnNameList) {
      addTableColumn(
          tableSessions, columnName, columnProperties.getWidth(), columnProperties.getVisible());
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && tableSessions.getColumnCount() == columnOrder.length) {
      tableSessions.setColumnOrder(columnOrder);
    }
  }

  private void initSessionsTableContextMenu() {

    Menu tableSessionsMenu = new Menu(tableSessions);
    tableSessions.setMenu(tableSessionsMenu);
    tableSessions.addKeyListener(keyListener);

    tableSessions.addListener(
        SWT.Selection,
        new Listener() {
          @Override
          public void handleEvent(Event event) {
            if (event.detail == SWT.CHECK) {
              TableItem item = (TableItem) event.item;
              String id = item.getText(1).concat("*").concat(item.getText(2)); //$NON-NLS-1$
              if (item.getChecked()) {
                watchedSessions.add(id);
                item.setForeground(watchedSessionColor);
              } else {
                watchedSessions.remove(id);
                item.setForeground(standardColor);
              }
            }
          }
        });

    // Выделение одной ячейки, для копирования значения из нее через CTRL+C
    // Из-за добавления этого Listener-а пропадает прямоугольник выделения строк (передвигая мышь с
    // зажатой ЛКМ)
    //    tableSessions.addListener(
    //        SWT.MouseDown,
    //        new Listener() {
    //          @Override
    //          public void handleEvent(Event event) {
    //            Table currentTable = (Table) event.widget;
    //
    //            Point pt = new Point(event.x, event.y);
    //            TableItem item = currentTable.getItem(pt);
    //            if (item != null) {
    //              for (int col = 0; col < currentTable.getColumnCount(); col++) {
    //                Rectangle rect = item.getBounds(col);
    //                if (rect.contains(pt)) {
    //
    //                  if (Objects.nonNull(lastSelectItem) && !lastSelectItem.isDisposed()) {
    //                    lastSelectItem.setForeground(lastSelectColumn, new Color(0, 0, 0));
    //                  }
    //
    //                  // System.out.println("item clicked.");
    //                  // System.out.println("column is " + col);
    //                  System.out.println(item.getText(col));
    //
    //                  // Color blue = display.getSystemColor(SWT.COLOR_BLUE);
    //                  item.setForeground(col, new Color(0, 100, 128));
    //                  // item.setFont(col, fontBold);
    //
    //                  lastSelectItem = item;
    //                  lastSelectColumn = col;
    //                  break;
    //                }
    //              }
    //            }
    //          }
    //        });

    MenuItem menuItemUpdateList = new MenuItem(tableSessionsMenu, SWT.NONE);
    menuItemUpdateList.setText(
        Messages.getString("ViewerArea.Update").concat("\tF5")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemUpdateList.setAccelerator(SWT.F5); // TODO ???
    menuItemUpdateList.setImage(updateIcon);
    menuItemUpdateList.addSelectionListener(updateItemListener);

    MenuItem menuItemViewSession = new MenuItem(tableSessionsMenu, SWT.NONE);
    menuItemViewSession.setText(
        Messages.getString("ViewerArea.ViewSession").concat("\tF2")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemViewSession.setAccelerator(SWT.F2);
    menuItemViewSession.setImage(editIcon);
    menuItemViewSession.addSelectionListener(editItemListener);

    MenuItem menuItemKillSession = new MenuItem(tableSessionsMenu, SWT.NONE);
    menuItemKillSession.setText(
        Messages.getString("ViewerArea.KillSession").concat("\tDEL")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemKillSession.setAccelerator(SWT.DEL);
    menuItemKillSession.setImage(deleteIcon);
    menuItemKillSession.addSelectionListener(deleteItemListener);
  }

  private void deleteSelectSession() {

    Table currentTable = null;

    if (currentTabitem.equals(tabSessions)) {
      currentTable = tableSessions;
    } else if (currentTabitem.equals(tabConnections)) {
      currentTable = tableConnections;
    } else if (currentTabitem.equals(tabWorkingServers)) {
      currentTable = tableWorkingServers;
    }

    if (currentTable == null) {
      return;
    }

    TableItem[] selectedItems = currentTable.getSelection();
    if (selectedItems.length == 0) {
      return;
    }

    for (TableItem item : selectedItems) {
      item.setForeground(deletedItemColor);

      if (currentTable.equals(tableSessions)) {
        Server server = (Server) item.getData(SERVER_INFO);
        UUID clusterId = (UUID) item.getData(CLUSTER_ID);
        UUID sessionId = (UUID) item.getData(SESSION_ID);

        if (server.terminateSession(clusterId, sessionId)) {
          item.dispose(); // update tableSessions
        }

      } else if (currentTable.equals(tableConnections)) {
        Server server = (Server) item.getData(SERVER_INFO);
        UUID clusterId = (UUID) item.getData(CLUSTER_ID);
        UUID pricessId = (UUID) item.getData(WORKINGPROCESS_ID);
        UUID connectionId = (UUID) item.getData(CONNECTION_ID);
        UUID infobaseId = (UUID) item.getData(INFOBASE_ID);

        if (server.disconnectConnection(clusterId, pricessId, connectionId, infobaseId)) {
          item.dispose(); // update tableConnections
        }

      } else if (currentTable.equals(tableWorkingServers)) {
        Server server = (Server) item.getData(SERVER_INFO);
        UUID clusterId = (UUID) item.getData(CLUSTER_ID);
        UUID workingServerId = (UUID) item.getData(WORKINGSERVER_ID);

        if (server.unregWorkingServer(clusterId, workingServerId)) {
          item.dispose(); // update tableWorkingServers
        }
      } else {
        break;
      }
    }
  }

  private void initConnectionsTable(TabFolder tabFolder) {

    tabConnections = new TabItem(tabFolder, SWT.NONE);
    tabConnections.setText(Messages.getString("ViewerArea.Connections")); //$NON-NLS-1$

    tableConnections =
        new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
    tabConnections.setControl(tableConnections);
    tableConnections.setHeaderVisible(true);
    tableConnections.setLinesVisible(true);

    initConnectionsTableContextMenu();

    ConnectionInfoExtended.initColumnsName(connectionColumnsMap);

    ColumnProperties columnProperties =
        ClusterProvider.getCommonConfig().getConnectionColumnProperties();

    String[] columnNameList = connectionColumnsMap.keySet().toArray(new String[0]);
    for (String columnName : columnNameList) {
      addTableColumn(
          tableConnections, columnName, columnProperties.getWidth(), columnProperties.getVisible());
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && tableConnections.getColumnCount() == columnOrder.length) {
      tableConnections.setColumnOrder(columnOrder);
    }
  }

  private void initConnectionsTableContextMenu() {

    Menu tableConnectionsMenu = new Menu(tableConnections);
    tableConnections.setMenu(tableConnectionsMenu);
    tableConnections.addKeyListener(keyListener);

    MenuItem menuItemUpdateList = new MenuItem(tableConnectionsMenu, SWT.NONE);
    menuItemUpdateList.setText(
        Messages.getString("ViewerArea.Update").concat("\tF5")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemUpdateList.setAccelerator(SWT.F5); // TODO ???
    menuItemUpdateList.setImage(updateIcon);
    menuItemUpdateList.addSelectionListener(updateItemListener);

    MenuItem menuItemKillSession = new MenuItem(tableConnectionsMenu, SWT.NONE);
    menuItemKillSession.setText(
        Messages.getString("ViewerArea.KillConnection").concat("\tDEL")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemKillSession.setAccelerator(SWT.DEL);
    menuItemKillSession.setImage(deleteIcon);
    menuItemKillSession.addSelectionListener(deleteItemListener);
  }

  private void initLocksTable(TabFolder tabFolder) {

    tabLocks = new TabItem(tabFolder, SWT.NONE);
    tabLocks.setText(Messages.getString("ViewerArea.Locks")); //$NON-NLS-1$

    tableLocks = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
    tabLocks.setControl(tableLocks);
    tableLocks.setHeaderVisible(true);
    tableLocks.setLinesVisible(true);

    initLocksTableContextMenu();

    LockInfoExtended.initColumnsName(lockColumnsMap);

    ColumnProperties columnProperties = ClusterProvider.getCommonConfig().getLockColumnProperties();

    String[] columnNameList = lockColumnsMap.keySet().toArray(new String[0]);
    for (String columnName : columnNameList) {
      addTableColumn(
          tableLocks, columnName, columnProperties.getWidth(), columnProperties.getVisible());
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && tableLocks.getColumnCount() == columnOrder.length) {
      tableLocks.setColumnOrder(columnOrder);
    }
  }

  private void initLocksTableContextMenu() {

    Menu tableLocksMenu = new Menu(tableLocks);
    tableLocks.setMenu(tableLocksMenu);
    tableLocks.addKeyListener(keyListener);

    MenuItem menuItemUpdateList = new MenuItem(tableLocksMenu, SWT.NONE);
    menuItemUpdateList.setText(
        Messages.getString("ViewerArea.Update").concat("\tF5")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemUpdateList.setAccelerator(SWT.F5); // TODO ???
    menuItemUpdateList.setImage(updateIcon);
    menuItemUpdateList.addSelectionListener(updateItemListener);
  }

  private void initWorkingProcessesTable(TabFolder tabFolder) {

    tabWorkingProcesses = new TabItem(tabFolder, SWT.NONE);
    tabWorkingProcesses.setText(Messages.getString("ViewerArea.WorkingProcesses")); //$NON-NLS-1$

    tableWorkingProcesses =
        new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
    tabWorkingProcesses.setControl(tableWorkingProcesses);
    tableWorkingProcesses.setHeaderVisible(true);
    tableWorkingProcesses.setLinesVisible(true);

    initWorkingProcessesTableContextMenu();

    WorkingProcessInfoExtended.initColumnsName(wpColumnsMap);

    ColumnProperties columnProperties = ClusterProvider.getCommonConfig().getWpColumnProperties();

    String[] columnNameList = wpColumnsMap.keySet().toArray(new String[0]);
    for (String columnName : columnNameList) {
      addTableColumn(
          tableWorkingProcesses, columnName, columnProperties.getWidth(), columnProperties.getVisible());
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && tableWorkingProcesses.getColumnCount() == columnOrder.length) {
      tableWorkingProcesses.setColumnOrder(columnOrder);
    }
  }

  private void initWorkingProcessesTableContextMenu() {

    Menu tableWorkingProcessesMenu = new Menu(tableWorkingProcesses);
    tableWorkingProcesses.setMenu(tableWorkingProcessesMenu);
    tableWorkingProcesses.addKeyListener(keyListener);

    MenuItem menuItemUpdateList = new MenuItem(tableWorkingProcessesMenu, SWT.NONE);
    menuItemUpdateList.setText(
        Messages.getString("ViewerArea.Update").concat("\tF5")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemUpdateList.setAccelerator(SWT.F5); // TODO ???
    menuItemUpdateList.setImage(updateIcon);
    menuItemUpdateList.addSelectionListener(updateItemListener);
  }

  private void initWorkingServersTable(TabFolder tabFolder) {

    tabWorkingServers = new TabItem(tabFolder, SWT.NONE);
    tabWorkingServers.setText(Messages.getString("ViewerArea.WorkingServers")); //$NON-NLS-1$

    tableWorkingServers =
        new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
    tabWorkingServers.setControl(tableWorkingServers);
    tableWorkingServers.setHeaderVisible(true);
    tableWorkingServers.setLinesVisible(true);

    initWorkingServersTableContextMenu();

    WorkingServerInfoExtended.initColumnsName(wsColumnsMap);

    ColumnProperties columnProperties = ClusterProvider.getCommonConfig().getWsColumnProperties();

    String[] columnNameList = wsColumnsMap.keySet().toArray(new String[0]);
    for (String columnName : columnNameList) {
      addTableColumn(
          tableWorkingServers, columnName, columnProperties.getWidth(), columnProperties.getVisible());
    }

    int[] columnOrder = columnProperties.getOrder();
    if (columnOrder != null && tableWorkingServers.getColumnCount() == columnOrder.length) {
      tableWorkingServers.setColumnOrder(columnOrder);
    }
  }

  private void initWorkingServersTableContextMenu() {

    Menu workingServersMenu = new Menu(tableWorkingServers);
    tableWorkingServers.setMenu(workingServersMenu);
    tableWorkingServers.addKeyListener(keyListener);

    MenuItem menuItemUpdateList = new MenuItem(workingServersMenu, SWT.NONE);
    menuItemUpdateList.setText(
        Messages.getString("ViewerArea.Update").concat("\tF5")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemUpdateList.setAccelerator(SWT.F5); // TODO ???
    menuItemUpdateList.setImage(updateIcon);
    menuItemUpdateList.addSelectionListener(updateItemListener);

    MenuItem menuItemCreateWorkingServer = new MenuItem(workingServersMenu, SWT.NONE);
    menuItemCreateWorkingServer.setText(
        Messages.getString("ViewerArea.CreateWorkingServer")); //$NON-NLS-1$
    menuItemCreateWorkingServer.setImage(addIcon);
    menuItemCreateWorkingServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent event) {
            TableItem[] item = tableWorkingServers.getSelection();
            if (item.length == 0) {
              return;
            }

            Server server = (Server) item[0].getData(SERVER_INFO);
            UUID clusterId = (UUID) item[0].getData(CLUSTER_ID);

            CreateEditWorkingServerDialog editWorkingServerDialog;
            try {
              editWorkingServerDialog =
                  new CreateEditWorkingServerDialog(
                      getParent().getDisplay().getActiveShell(), server, clusterId, null);
            } catch (Exception excp) {
              LOGGER.error(
                  "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                  clusterId,
                  excp);
              return;
            }

            int dialogResult = editWorkingServerDialog.open();
            if (dialogResult == 0) {
              var newWorkingServerUuid = editWorkingServerDialog.getNewWorkingServerId();
              if (newWorkingServerUuid != null) {
                IWorkingServerInfo workingServerInfo =
                    server.getWorkingServerInfo(clusterId, newWorkingServerUuid);
                addWorkingServerInTable(server, clusterId, workingServerInfo);
              }
            }
          }
        });

    MenuItem menuItemEditWorkingServer = new MenuItem(workingServersMenu, SWT.NONE);
    menuItemEditWorkingServer.setText(
        Messages.getString("ViewerArea.EditWorkingServer").concat("\tF2")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemEditWorkingServer.setImage(editIcon);
    menuItemEditWorkingServer.addSelectionListener(editItemListener);

    MenuItem menuItemDeleteWorkingServer = new MenuItem(workingServersMenu, SWT.NONE);
    menuItemDeleteWorkingServer.setText(
        Messages.getString("ViewerArea.DeleteWorkingServer").concat("\tDEL")); //$NON-NLS-1$ //$NON-NLS-2$
    menuItemDeleteWorkingServer.setImage(deleteIcon);
    menuItemDeleteWorkingServer.addSelectionListener(deleteItemListener);
  }

  private void fillServersList() {
    // TODO Auto-generated method stub
  }

  private void updateClustersInTree(TreeItem serverItem) {

    Server server = getCurrentServerConfig(serverItem);
    TreeItem[] clustersItems = serverItem.getItems();

    // у отключенного сервера удаляем все дочерние элементы
    if (!server.isConnected()) {
      for (TreeItem clusterItem : clustersItems) {
        disposeTreeItemWithChildren(clusterItem);
      }
      return;
    }

    List<IClusterInfo> clusters = server.getClusters();

    // удаление несуществующих элементов
    for (TreeItem clusterItem : clustersItems) {
      UUID currentClusterId = getCurrentClusterId(clusterItem);
      List<IClusterInfo> foundCluster =
          clusters.stream()
              .filter(c -> c.getClusterId().equals(currentClusterId))
              .collect(Collectors.toList());

      if (foundCluster.isEmpty()) {
        disposeTreeItemWithChildren(clusterItem);
      }
    }

    // добавление новых элементов
    clusters.forEach(
        clusterInfo -> {
          var itemFound = false;
          TreeItem currentClusterItem = null;
          for (TreeItem clusterItem : serverItem.getItems()) {
            if (getCurrentClusterId(clusterItem).equals(clusterInfo.getClusterId())) {
              currentClusterItem = clusterItem;
              itemFound = true;
              break;
            }
          }

          if (!itemFound) {
            currentClusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
          }

          // Заполнение дерева кластера
          if (currentClusterItem != null) {
            fillChildrenItemsOfCluster(currentClusterItem, server);
          }
        });

    // Разворачиваем дерево, если включена настройка
    serverItem.setExpanded(ClusterProvider.getCommonConfig().isExpandServersTree());
  }

  private void fillChildrenItemsOfCluster(TreeItem clusterItem, Server server) {

    fillInfobasesOfCluster(clusterItem, server);
    fillWorkingProcessesInCluster(clusterItem, server);
    fillWorkingServersInCluster(clusterItem, server);

    clusterItem.setExpanded(ClusterProvider.getCommonConfig().isExpandClustersTree());
  }

  private void fillInfobasesOfCluster(TreeItem clusterItem, Server server) {

    TreeItem infobasesNode = null;
    for (TreeItem treeItem : clusterItem.getItems()) {
      if (treeItem.getData("Type") == TreeItemType.INFOBASE_NODE) { //$NON-NLS-1$
        infobasesNode = treeItem;
        break;
      }
    }

    if (infobasesNode == null) {
      infobasesNode = new TreeItem(clusterItem, SWT.NONE);
      infobasesNode.setData("Type", TreeItemType.INFOBASE_NODE); //$NON-NLS-1$
      infobasesNode.setImage(infobasesIcon);
      infobasesNode.setChecked(false);
    }

    UUID clusterId = getCurrentClusterId(infobasesNode);
    List<IInfoBaseInfoShort> infoBases = server.getInfoBasesShort(clusterId);

    var infobasesNodeTitle =
        String.format(
            Messages.getString("ViewerArea.InfobasesCount"), infoBases.size()); //$NON-NLS-1$
    infobasesNode.setText(new String[] {infobasesNodeTitle});

    if (infoBases.isEmpty()) {
      for (TreeItem infobase : infobasesNode.getItems()) {
        infobase.dispose();
      }
      return;
    }

    // удаление несуществующих элементов
    for (TreeItem infobaseItem : infobasesNode.getItems()) {
      UUID currentInfobaseId = getCurrentInfobaseId(infobaseItem);
      List<IInfoBaseInfoShort> foundItems =
          infoBases.stream()
              .filter(c -> c.getInfoBaseId().equals(currentInfobaseId))
              .collect(Collectors.toList());

      if (foundItems.isEmpty()) {
        infobaseItem.dispose();
      }
    }

    // добавление новых элементов
    for (IInfoBaseInfoShort infoBaseInfo : infoBases) {
      // infoBases.forEach(infoBaseInfo -> {

      var itemFound = false;
      for (TreeItem infobaseItem : infobasesNode.getItems()) {
        if (getCurrentInfobaseId(infobaseItem).equals(infoBaseInfo.getInfoBaseId())) {
          itemFound = true;
          break;
        }
      }

      if (!itemFound) {
        addInfobaseItemInInfobaseNode(infobasesNode, infoBaseInfo);
      }
    }

    infobasesNode.setExpanded(ClusterProvider.getCommonConfig().isExpandInfobasesTree());
  }

  private void fillWorkingProcessesInCluster(TreeItem clusterItem, Server server) {

    TreeItem workingProcessesNode = null;
    for (TreeItem treeItem : clusterItem.getItems()) {
      if (treeItem.getData("Type") == TreeItemType.WORKINGPROCESS_NODE) { //$NON-NLS-1$
        workingProcessesNode = treeItem;
        break;
      }
    }

    if (!ClusterProvider.getCommonConfig().isShowWorkingProcessesTree()) {
      if (workingProcessesNode != null) {
        workingProcessesNode.dispose();
      }
      return;
    }

    if (workingProcessesNode == null) {
      workingProcessesNode = new TreeItem(clusterItem, SWT.NONE);
      workingProcessesNode.setData("Type", TreeItemType.WORKINGPROCESS_NODE); //$NON-NLS-1$
      workingProcessesNode.setImage(workingProcessesIcon);
      workingProcessesNode.setChecked(false);
    }

    UUID clusterId = getCurrentClusterId(workingProcessesNode);
    List<IWorkingProcessInfo> workingProcesses = server.getWorkingProcesses(clusterId);

    var workingProcessesNodeTitle =
        String.format(
            Messages.getString("ViewerArea.WorkingProcessesCount"), //$NON-NLS-1$
            workingProcesses.size());
    workingProcessesNode.setText(new String[] {workingProcessesNodeTitle});

    if (workingProcesses.isEmpty()) {
      for (TreeItem workingProcess : workingProcessesNode.getItems()) {
        workingProcess.dispose();
      }
      return;
    }

    // удаление несуществующих элементов
    for (TreeItem workingProcessItem : workingProcessesNode.getItems()) {
      UUID currentWorkingProcessId = getCurrentWorkingProcessId(workingProcessItem);
      List<IWorkingProcessInfo> foundItems =
          workingProcesses.stream()
              .filter(c -> c.getWorkingProcessId().equals(currentWorkingProcessId))
              .collect(Collectors.toList());

      if (foundItems.isEmpty()) {
        workingProcessItem.dispose();
      }
    }

    // добавление новых элементов
    for (IWorkingProcessInfo workingProcessInfo : workingProcesses) {
      // wProcesses.forEach(workingProcessInfo -> {

      var itemFound = false;
      for (TreeItem workingProcessItem : workingProcessesNode.getItems()) {
        if (getCurrentWorkingProcessId(workingProcessItem)
            .equals(workingProcessInfo.getWorkingProcessId())) {
          itemFound = true;
          break;
        }
      }

      if (!itemFound) {
        addWorkingProcessItemInNode(workingProcessesNode, workingProcessInfo);
      }
    }
  }

  private void fillWorkingServersInCluster(TreeItem clusterItem, Server server) {

    TreeItem workingServersNode = null;
    for (TreeItem treeItem : clusterItem.getItems()) {
      if (treeItem.getData("Type") == TreeItemType.WORKINGSERVER_NODE) { //$NON-NLS-1$
        workingServersNode = treeItem;
        break;
      }
    }

    if (!ClusterProvider.getCommonConfig().isShowWorkingServersTree()) {
      if (workingServersNode != null) {
        workingServersNode.dispose();
      }
      return;
    }

    if (workingServersNode == null) {
      workingServersNode = new TreeItem(clusterItem, SWT.NONE);
      workingServersNode.setData("Type", TreeItemType.WORKINGSERVER_NODE); //$NON-NLS-1$
      workingServersNode.setImage(workingServerIcon);
      workingServersNode.setChecked(false);
    }

    UUID clusterId = getCurrentClusterId(workingServersNode);
    List<IWorkingServerInfo> workingServers = server.getWorkingServers(clusterId);

    var workingServerNodeTitle =
        String.format(
            Messages.getString("ViewerArea.WorkingServersCount"), //$NON-NLS-1$
            workingServers.size());
    workingServersNode.setText(new String[] {workingServerNodeTitle});

    if (workingServers.isEmpty()) {
      for (TreeItem workingServerItem : workingServersNode.getItems()) {
        workingServerItem.dispose();
      }
      return;
    }

    // удаление несуществующих элементов
    for (TreeItem workingServerItem : workingServersNode.getItems()) {
      UUID currentWorkingServerId = getCurrentWorkingServerId(workingServerItem);
      List<IWorkingServerInfo> foundItems =
          workingServers.stream()
              .filter(c -> c.getWorkingServerId().equals(currentWorkingServerId))
              .collect(Collectors.toList());

      if (foundItems.isEmpty()) {
        workingServerItem.dispose();
      }
    }

    // добавление новых элементов
    for (IWorkingServerInfo workingServerInfo : workingServers) {
      // wServers.forEach(workingServerInfo -> {

      var itemFound = false;
      for (TreeItem workingServerItem : workingServersNode.getItems()) {
        if (getCurrentWorkingServerId(workingServerItem)
            .equals(workingServerInfo.getWorkingServerId())) {
          itemFound = true;
          break;
        }
      }

      if (!itemFound) {
        addWorkingServerItemInNode(workingServersNode, workingServerInfo);
      }
    }
  }

  private TreeItem addServerItemInServersTree(Server server) {

    var item = new TreeItem(serversTree, SWT.NONE);
    item.setText(new String[] {server.getTreeDescription()});
    item.setData("Type", TreeItemType.SERVER); //$NON-NLS-1$
    item.setData(SERVER_INFO, server);

    item.setImage(server.isConnected() ? serverIconUp : serverIcon);

    return item;
  }

  private TreeItem addClusterItemInServersTree(TreeItem serverItem, IClusterInfo clusterInfo) {

    var clusterTitle =
        String.format("%s (%s)", clusterInfo.getName(), clusterInfo.getMainPort()); //$NON-NLS-1$

    var clusterItem = new TreeItem(serverItem, SWT.NONE);
    clusterItem.setText(new String[] {clusterTitle});
    clusterItem.setData("Type", TreeItemType.CLUSTER); //$NON-NLS-1$
    clusterItem.setData(CLUSTER_ID, clusterInfo.getClusterId());
    clusterItem.setImage(clusterIcon);

    return clusterItem;
  }

  private void addInfobaseItemInInfobaseNode(TreeItem infobaseNode, IInfoBaseInfoShort ibInfo) {

    String infobaseTitle;
    if (ClusterProvider.getCommonConfig().isShowInfobaseDescription() && !ibInfo.getDescr().isBlank()) {
      infobaseTitle = String.format("%s (%s)", ibInfo.getName(), ibInfo.getDescr()); //$NON-NLS-1$
    } else {
      infobaseTitle = String.format("%s", ibInfo.getName()); //$NON-NLS-1$
    }

    var item = new TreeItem(infobaseNode, SWT.NONE);
    item.setText(new String[] {infobaseTitle});
    item.setData("Type", TreeItemType.INFOBASE); //$NON-NLS-1$
    item.setData(INFOBASE_ID, ibInfo.getInfoBaseId());
    item.setImage(0, infobaseIcon);
    item.setChecked(false);

    // item.setImage(1, ibInfo.isSessionsDenied() ? lockUsersIcon : null);

  }

  private void addWorkingProcessItemInNode(TreeItem wpNodeItem, IWorkingProcessInfo wpInfo) {

    var itemTitle =
        String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort()); //$NON-NLS-1$

    var item = new TreeItem(wpNodeItem, SWT.NONE);
    item.setText(new String[] {itemTitle});
    item.setData("Type", TreeItemType.WORKINGPROCESS); //$NON-NLS-1$
    item.setData(WORKINGPROCESS_ID, wpInfo.getWorkingProcessId());
    item.setImage(workingProcessIcon);
    item.setChecked(false);
  }

  private void addWorkingServerItemInNode(TreeItem wsNodeItem, IWorkingServerInfo wpInfo) {

    var itemTitle =
        String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort()); //$NON-NLS-1$

    var item = new TreeItem(wsNodeItem, SWT.NONE);
    item.setText(new String[] {itemTitle});
    item.setData("Type", TreeItemType.WORKINGSERVER); //$NON-NLS-1$
    item.setData(WORKINGSERVER_ID, wpInfo.getWorkingServerId());
    item.setImage(workingServerIcon);
    item.setChecked(false);
  }

  private void addSessionInTable(
      Server server,
      UUID clusterId,
      UUID infobaseId,
      ISessionInfo sessionInfo,
      List<IInfoBaseConnectionShort> connections) {

    SessionInfoExtended sessionInfoExtended =
        new SessionInfoExtended(
            server, clusterId, infobaseId, sessionInfo, connections, sessionColumnsMap);

    var sessionItem = new TableItem(tableSessions, SWT.NONE);
    sessionItem.setText(sessionInfoExtended.getExtendedInfo());
    sessionItem.setData(SERVER_INFO, server);
    sessionItem.setData(CLUSTER_ID, clusterId);
    sessionItem.setData(SESSION_ID, sessionInfo.getSid()); // sessionInfo.getSessionId() ???
    // agentConnection.getSessionInfo(clusterId, sid); ошибка...
    sessionItem.setData("sessionInfo", sessionInfo); //$NON-NLS-1$
    sessionItem.setImage(userIcon);
    sessionItem.setChecked(false);

    if (IInfoExtended.highlightItem(sessionInfo.getStartedAt())) {
      sessionItem.setForeground(newItemColor);
    }

    Config commonConfig = ClusterProvider.getCommonConfig();
    if (commonConfig.isShadowSleepSessions() && sessionInfo.getHibernate()) {
      sessionItem.setForeground(shadowItemColor);
    }

    String id = sessionItem.getText(1).concat("*").concat(sessionItem.getText(2)); //$NON-NLS-1$
    if (watchedSessions.contains(id)) {
      sessionItem.setChecked(true);
      sessionItem.setForeground(watchedSessionColor);
    }

    switch (sessionInfo.getAppId()) {
      case Server.THIN_CLIENT:
      case Server.THICK_CLIENT:
      case Server.DESIGNER:
        sessionItem.setImage(sessionInfo.getHibernate() ? sleepUserIcon : userIcon);
        break;
      case Server.SERVER_CONSOLE:
      case Server.RAS_CONSOLE:
      case Server.JOBSCHEDULER:
        sessionItem.setImage(serviceIcon);
        break;
      default:
        sessionItem.setImage(sessionInfo.getHibernate() ? sleepUserIcon : userIcon);
    }
  }

  private void addConnectionInTable(
      Server server,
      UUID clusterId,
      UUID infobaseId,
      IInfoBaseConnectionShort connectionInfo,
      List<IWorkingProcessInfo> workingProcesses) {

    ConnectionInfoExtended sessionInfoExtended =
        new ConnectionInfoExtended(
            server, clusterId, infobaseId, connectionInfo, workingProcesses, connectionColumnsMap);

    var connectionItem = new TableItem(tableConnections, SWT.NONE);
    connectionItem.setText(sessionInfoExtended.getExtendedInfo());
    connectionItem.setData(SERVER_INFO, server);
    connectionItem.setData(CLUSTER_ID, clusterId);
    connectionItem.setData(WORKINGPROCESS_ID, connectionInfo.getWorkingProcessId());
    connectionItem.setData(INFOBASE_ID, connectionInfo.getInfoBaseId());
    connectionItem.setData(CONNECTION_ID, connectionInfo.getInfoBaseConnectionId());
    connectionItem.setImage(connectionIcon);
    connectionItem.setChecked(false);

    if (IInfoExtended.highlightItem(connectionInfo.getConnectedAt())) {
      connectionItem.setForeground(newItemColor);
    }
  }

  private void addLocksInTable(
      Server server,
      UUID clusterId,
      UUID infobaseId,
      IObjectLockInfo lockInfo,
      List<ISessionInfo> sessionsInfo,
      List<IInfoBaseConnectionShort> connections) {

    LockInfoExtended lockInfoExtended =
        new LockInfoExtended(
            server, clusterId, infobaseId, lockInfo, sessionsInfo, connections, lockColumnsMap);

    var lockItem = new TableItem(tableLocks, SWT.NONE);
    lockItem.setText(lockInfoExtended.getExtendedInfo());
    lockItem.setData(CLUSTER_ID, clusterId);
    lockItem.setData(INFOBASE_ID, infobaseId);
    lockItem.setData("IObjectLockInfo", lockInfo); //$NON-NLS-1$
    lockItem.setImage(locksIcon);
    lockItem.setChecked(false);

    if (IInfoExtended.highlightItem(lockInfo.getLockedAt())) {
      lockItem.setForeground(newItemColor);
    }
  }

  private void addWorkingProcessInTable(
      Server server, UUID clusterId, IWorkingProcessInfo workingProcessInfo) {

    WorkingProcessInfoExtended wpInfoExtended =
        new WorkingProcessInfoExtended(server, clusterId, workingProcessInfo, wpColumnsMap);

    var wpItem = new TableItem(tableWorkingProcesses, SWT.NONE);
    wpItem.setText(wpInfoExtended.getExtendedInfo());
    wpItem.setData(SERVER_INFO, server);
    wpItem.setData(CLUSTER_ID, clusterId);
    wpItem.setData(WORKINGPROCESS_ID, workingProcessInfo.getWorkingProcessId());
    wpItem.setImage(workingProcessIcon);
    wpItem.setChecked(false);

    if (IInfoExtended.highlightItem(workingProcessInfo.getStartedAt())) {
      wpItem.setForeground(newItemColor);
    }
  }

  private void addWorkingServerInTable(
      Server server, UUID clusterId, IWorkingServerInfo workingServerInfo) {

    WorkingServerInfoExtended wsInfoExtended =
        new WorkingServerInfoExtended(server, clusterId, workingServerInfo, wsColumnsMap);

    var connectionItem = new TableItem(tableWorkingServers, SWT.NONE);
    connectionItem.setText(wsInfoExtended.getExtendedInfo());
    connectionItem.setData(SERVER_INFO, server);
    connectionItem.setData(CLUSTER_ID, clusterId);
    connectionItem.setData(WORKINGSERVER_ID, workingServerInfo.getWorkingServerId());
    connectionItem.setImage(workingServerIcon);
    connectionItem.setChecked(false);
  }

  private void addTableColumn(
      Table table, String text, int[] columnWidth, boolean[] columnVisible) { // TODO properties
    var newColumn = new TableColumn(table, SWT.NONE);
    newColumn.setText(text);
    newColumn.setMoveable(true);
    newColumn.setAlignment(SWT.RIGHT);

    int numOfColumn = table.getColumnCount() - 1;

    if (columnVisible != null && columnVisible[numOfColumn]) {
      newColumn.setResizable(true);
      newColumn.setWidth(
          // columnWidth == null //TODO нужно ли это еще
          // || columnWidth.length <= table.getColumnCount()
          // ||
          columnWidth[numOfColumn] == 0 ? 100 : columnWidth[numOfColumn]);
    } else {
      newColumn.setResizable(false);
      newColumn.setWidth(0);
    }

    newColumn.addListener(SWT.Move, columnMoveListener);
    newColumn.addListener(SWT.Resize, columnResizeListener);
    // newColumn.addControlListener(columnResizeListener);

  }

  private TreeItemType getTreeItemType(TreeItem item) {
    return (TreeItemType) item.getData("Type"); //$NON-NLS-1$
  }

  private Server getCurrentServerConfig(TreeItem item) {

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
    throw new IllegalStateException("Error get ServerConfig from item."); //$NON-NLS-1$
    // return null;
  }

  private UUID getCurrentClusterId(TreeItem item) {

    if (getTreeItemType(item) == TreeItemType.CLUSTER) {
      return (UUID) item.getData(CLUSTER_ID);
    }

    TreeItem parentItem = item.getParentItem();
    while (parentItem != null) {

      if (getTreeItemType(parentItem) == TreeItemType.CLUSTER) {
        return (UUID) parentItem.getData(CLUSTER_ID);
      } else {
        parentItem = parentItem.getParentItem();
      }
    }
    return null;
  }

  private UUID getCurrentWorkingProcessId(TreeItem item) {

    if (getTreeItemType(item) == TreeItemType.WORKINGPROCESS) {
      return (UUID) item.getData(WORKINGPROCESS_ID);
    }

    TreeItem parentItem = item.getParentItem();
    while (parentItem != null) {

      if (getTreeItemType(parentItem) == TreeItemType.WORKINGPROCESS) {
        return (UUID) parentItem.getData(WORKINGPROCESS_ID);
      } else {
        parentItem = parentItem.getParentItem();
      }
    }
    return null;
  }

  private UUID getCurrentWorkingServerId(TreeItem item) {

    if (getTreeItemType(item) == TreeItemType.WORKINGSERVER) {
      return (UUID) item.getData(WORKINGSERVER_ID);
    }

    TreeItem parentItem = item.getParentItem();
    while (parentItem != null) {

      if (getTreeItemType(parentItem) == TreeItemType.WORKINGSERVER) {
        return (UUID) parentItem.getData(WORKINGSERVER_ID);
      } else {
        parentItem = parentItem.getParentItem();
      }
    }
    return null;
  }

  private UUID getCurrentInfobaseId(TreeItem item) {
    if (getTreeItemType(item) == TreeItemType.INFOBASE) {
      return (UUID) item.getData(INFOBASE_ID);
    } else {
      return null;
    }
  }

  private void disposeTreeItemWithChildren(TreeItem item) {
    TreeItem[] childItems = item.getItems();
    for (TreeItem childItem : childItems) {
      disposeTreeItemWithChildren(childItem);
      childItem.dispose();
    }
    item.dispose();
  }

  private Image getImage(String name) {
    return new Image(
        getParent().getDisplay(),
        this.getClass().getResourceAsStream("/icons/".concat(name))); //$NON-NLS-1$
  }

  private void connectToAllServers(boolean connectAll) {

    TreeItem[] serversItem = serversTree.getItems();

    for (TreeItem serverItem : serversItem) {
      Server server = getCurrentServerConfig(serverItem);
      if ((connectAll || server.getAutoconnect()) && !server.isConnected()) {
        connectServerItem(serverItem, true);
      }
    }
  }

  private void diconnectFromAllServers() {

    TreeItem[] serversItem = serversTree.getItems();

    for (TreeItem serverItem : serversItem) {
      disconnectServerItem(serverItem);
    }
  }

  private void connectServerItem(TreeItem serverItem, boolean silentMode) {

    // async не работает асинхронно
    serverItem.setImage(serverIconConnecting);
    Display.getDefault()
        .asyncExec(
            new Runnable() {

              @Override
              public void run() {

                Server server = getCurrentServerConfig(serverItem);
                server.connectToServer(false, silentMode);

                serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);
                serverItem.setText(new String[] {server.getTreeDescription()});
                updateClustersInTree(serverItem);
              }
            });
  }

  private void disconnectServerItem(TreeItem serverItem) {
    Server server = getCurrentServerConfig(serverItem);
    server.disconnectFromAgent();
    serverItem.setImage(serverIconDown);

    TreeItem[] clusterItems = serverItem.getItems();
    for (TreeItem clusterItem : clusterItems) {
      disposeTreeItemWithChildren(clusterItem);
    }
  }

  private void fillTabs(TreeItem treeItem) {

    highlightTreeItem(treeItem);

    Server server;
    UUID clusterId;
    UUID infobaseId;
    UUID workingProcessId;

    List<ISessionInfo> sessions;
    List<IInfoBaseConnectionShort> connections;
    List<IObjectLockInfo> locks;
    List<IWorkingProcessInfo> workingProcesses;
    List<IWorkingServerInfo> workingServers;

    clearTabs();
    switch (getTreeItemType(treeItem)) {
      case SERVER:
        return;

      case CLUSTER:
      case INFOBASE_NODE:
      case WORKINGPROCESS_NODE:
        server = getCurrentServerConfig(treeItem);
        clusterId = getCurrentClusterId(treeItem);
        infobaseId = null;

        sessions = server.getSessions(clusterId);
        connections = server.getConnectionsShort(clusterId);
        locks = server.getLocks(clusterId);
        workingProcesses = server.getWorkingProcesses(clusterId);
        workingServers = server.getWorkingServers(clusterId);
        break;

      case WORKINGPROCESS:
        server = getCurrentServerConfig(treeItem);
        clusterId = getCurrentClusterId(treeItem);
        infobaseId = null;
        workingProcessId = getCurrentWorkingProcessId(treeItem);

        sessions = server.getWorkingProcessSessions(clusterId, workingProcessId);
        connections = server.getWorkingProcessConnectionsShort(clusterId, workingProcessId);
        locks = new ArrayList<>();
        // locks = serverConfig.getInfoBaseLocks(clusterInfo.getClusterId(),
        // infoBaseInfo.getInfoBaseId());

        workingProcesses = new ArrayList<>();
        workingProcesses.add(server.getWorkingProcessInfo(clusterId, workingProcessId));
        workingServers = server.getWorkingServers(clusterId);
        break;

      case INFOBASE:
        server = getCurrentServerConfig(treeItem);
        clusterId = getCurrentClusterId(treeItem);
        infobaseId = getCurrentInfobaseId(treeItem);

        sessions = server.getInfoBaseSessions(clusterId, infobaseId);
        connections = server.getInfoBaseConnectionsShort(clusterId, infobaseId);
        locks = server.getInfoBaseLocks(clusterId, infobaseId);

        // TODO отметить рп обслуживающий базу
        workingProcesses = server.getWorkingProcesses(clusterId);
        workingServers = server.getWorkingServers(clusterId);
        break;

      default:
        return;
    }

    tabSessions.setText(
        String.format(
            Messages.getString("ViewerArea.SessionsCount"), //$NON-NLS-1$
            sessions.size()));
    tabConnections.setText(
        String.format(
            Messages.getString("ViewerArea.ConnectionsCount"), //$NON-NLS-1$
            connections.size()));
    tabLocks.setText(
        String.format(
            Messages.getString("ViewerArea.LocksCount"), //$NON-NLS-1$
            locks.size()));
    tabWorkingProcesses.setText(
        String.format(
            Messages.getString("ViewerArea.WorkingProcessesCount"), //$NON-NLS-1$
            workingProcesses.size()));
    tabWorkingServers.setText(
        String.format(
            Messages.getString("ViewerArea.WorkingServersCount"), //$NON-NLS-1$
            workingServers.size()));

    if (currentTabitem.equals(tabSessions)) {
      sessions.forEach(
          session -> addSessionInTable(server, clusterId, infobaseId, session, connections));
    } else if (currentTabitem.equals(tabConnections)) {
      connections.forEach(
          connection ->
              addConnectionInTable(server, clusterId, infobaseId, connection, workingProcesses));
    } else if (currentTabitem.equals(tabLocks)) {
      locks.forEach(
          lock -> addLocksInTable(server, clusterId, infobaseId, lock, sessions, connections));
    } else if (currentTabitem.equals(tabWorkingProcesses)) {
      workingProcesses.forEach(
          workingProcess -> addWorkingProcessInTable(server, clusterId, workingProcess));
    } else if (currentTabitem.equals(tabWorkingServers)) {
      workingServers.forEach(
          workingServer -> addWorkingServerInTable(server, clusterId, workingServer));
    }
  }

  private void addMenuSeparator(Menu menu) {
    new MenuItem(menu, SWT.SEPARATOR);
  }

  private void highlightTreeItem(TreeItem treeItem) {
    if (Objects.isNull(currentTreeItem) || !currentTreeItem.equals(treeItem)) {

      if (Objects.nonNull(currentTreeItem) && !currentTreeItem.isDisposed()) {
        currentTreeItem.setFont(fontNormal);
      }
      treeItem.setFont(fontBold);
      currentTreeItem = treeItem;
    }
  }

  private void setContestMenuInTree(TreeItem treeItem) {

    switch (getTreeItemType(treeItem)) {
      case SERVER:
        serversTree.setMenu(serverMenu);
        return;
      case CLUSTER:
        // case WORKINGPROCESS_NODE:
        serversTree.setMenu(clusterMenu);
        break;
      case INFOBASE_NODE:
        serversTree.setMenu(infobaseNodeMenu);
        break;
      case INFOBASE:
        serversTree.setMenu(infobaseMenu);
        break;
      case WORKINGPROCESS:
        serversTree.setMenu(null);
        break;
      case WORKINGSERVER:
        serversTree.setMenu(workingServerMenu);
        break;
      default:
        serversTree.setMenu(null);
        return;
    }
  }

  private void clearTabs() {
    tabSessions.setText(Messages.getString("ViewerArea.Sessions")); //$NON-NLS-1$
    tabConnections.setText(Messages.getString("ViewerArea.Connections")); //$NON-NLS-1$
    tabLocks.setText(Messages.getString("ViewerArea.Locks")); //$NON-NLS-1$
    tabWorkingProcesses.setText(Messages.getString("ViewerArea.WorkingProcesses")); //$NON-NLS-1$
    tabWorkingServers.setText(Messages.getString("ViewerArea.WorkingServers")); //$NON-NLS-1$

    tableSessions.removeAll();
    tableConnections.removeAll();
    tableLocks.removeAll();
    tableWorkingProcesses.removeAll();
    tableWorkingServers.removeAll();
  }

  private void clickItemInServerTree(int mouseButton) {
    TreeItem[] item = serversTree.getSelection();
    if (item.length == 0) {
      return;
    }

    TreeItem treeItem = item[0];

    switch (mouseButton) {
      case 1: // left click
        fillTabs(treeItem);
        break;

      case 3: // right click
        setContestMenuInTree(treeItem);
        break;

      default:
        break;
    }
  }

  Listener columnMoveListener =
      new Listener() {
        @Override
        public void handleEvent(Event e) {

          TableColumn column = (TableColumn) e.widget;
          Table currentTable = column.getParent();
          Config commonConfig = ClusterProvider.getCommonConfig();

          if (currentTable.equals(tableSessions)) {
            commonConfig.setSessionsColumnOrder(tableSessions.getColumnOrder());
          } else if (currentTable.equals(tableConnections)) {
            commonConfig.setConnectionsColumnOrder(tableConnections.getColumnOrder());
          } else if (currentTable.equals(tableLocks)) {
            commonConfig.setLocksColumnOrder(tableLocks.getColumnOrder());
          } else if (currentTable.equals(tableWorkingProcesses)) {
            commonConfig.setWorkingProcessesColumnOrder(tableWorkingProcesses.getColumnOrder());
          } else if (currentTable.equals(tableWorkingServers)) {
            commonConfig.setWorkingServersColumnOrder(tableWorkingServers.getColumnOrder());
          }

          // clusterProvider.saveConfig();
        }
      };

  Listener columnResizeListener =
      new Listener() {
        @Override
        public void handleEvent(Event e) {

          TableColumn column = (TableColumn) e.widget;
          int newWidth = column.getWidth();
          Table currentTable = column.getParent();
          TableColumn[] columns = currentTable.getColumns();
          Config commonConfig = ClusterProvider.getCommonConfig();

          for (int i = 0; i < columns.length; i++) {
            if (columns[i].getText().equals(column.getText())) {
              if (currentTable.equals(tableSessions)) {
                commonConfig.setSessionsColumnWidth(i, newWidth);
              } else if (currentTable.equals(tableConnections)) {
                commonConfig.setConnectionsColumnWidth(i, newWidth);
              } else if (currentTable.equals(tableLocks)) {
                commonConfig.setLocksColumnWidth(i, newWidth);
              } else if (currentTable.equals(tableWorkingProcesses)) {
                commonConfig.setWorkingProcessesColumnWidth(i, newWidth);
              } else if (currentTable.equals(tableWorkingServers)) {
                commonConfig.setWorkingServersColumnWidth(i, newWidth);
              }
              break;
            }
          }
          // clusterProvider.saveConfig();
        }
      };

  //  ControlAdapter columnResizeListener =
  //      new ControlAdapter() {
  //        @Override
  //        public void controlResized(ControlEvent e) {
  //          TableColumn w = (TableColumn) e.widget;
  //          int width = w.getWidth();
  //          TableColumn[] columns = w.getParent().getColumns();
  //
  //          for (int i = 0; i < columns.length; i++) {
  //            if (columns[i].getText().equals(w.getText())) {
  //              ClusterProvider.getCommonConfig().setSessionsColumnWidth(i, width);
  //              break;
  //            }
  //          }
  //          // clusterProvider.saveConfig();
  //        }
  //      };

  SelectionAdapter deleteItemListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          deleteSelectSession();
        }
      };

  SelectionAdapter updateItemListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          clickItemInServerTree(1);
        }
      };

  SelectionAdapter editItemListener =
      new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {

          Table currentTable = null;

          if (currentTabitem.equals(tabSessions)) {
            currentTable = tableSessions;
          } else if (currentTabitem.equals(tabConnections)) {
            currentTable = tableConnections;
          } else if (currentTabitem.equals(tabWorkingServers)) {
            currentTable = tableWorkingServers;
          }

          if (currentTable == null) {
            return;
          }

          TableItem[] item = currentTable.getSelection();
          if (item.length == 0) {
            return;
          }

          if (currentTable.equals(tableSessions)) {

            Server server = (Server) item[0].getData(SERVER_INFO);
            UUID clusterId = (UUID) item[0].getData(CLUSTER_ID);
            UUID sessionId = (UUID) item[0].getData(SESSION_ID);
            ISessionInfo sessionInfo = (ISessionInfo) item[0].getData("sessionInfo"); //$NON-NLS-1$

            SessionInfoDialog editClusterDialog;
            try {
              editClusterDialog =
                  new SessionInfoDialog(
                      getParent().getDisplay().getActiveShell(),
                      server,
                      clusterId,
                      sessionId,
                      sessionInfo);
            } catch (Exception excp) {
              excp.printStackTrace();
              LOGGER.error(
                  "Error init SessionInfoDialog for session id {}", //$NON-NLS-1$
                  sessionId,
                  excp);
              return;
            }

            editClusterDialog.open();

          } else if (currentTable.equals(tableWorkingServers)) {
            Server server = (Server) item[0].getData(SERVER_INFO);
            UUID clusterId = (UUID) item[0].getData(CLUSTER_ID);
            UUID workingServerId = (UUID) item[0].getData(WORKINGSERVER_ID);

            CreateEditWorkingServerDialog editClusterDialog;
            try {
              editClusterDialog =
                  new CreateEditWorkingServerDialog(
                      getParent().getDisplay().getActiveShell(),
                      server,
                      clusterId,
                      workingServerId);
            } catch (Exception excp) {
              excp.printStackTrace();
              LOGGER.error(
                  "Error init WorkingServerDialog for cluster id {}", //$NON-NLS-1$
                  workingServerId,
                  excp);
              return;
            }

            int dialogResult = editClusterDialog.open();
            if (dialogResult == 0) {
              clickItemInServerTree(0);
            }
          }
        }
      };

  KeyAdapter keyListener =
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {

          final int keyC = 99;

          switch (e.keyCode) {
            case SWT.F2:
              editItemListener.widgetSelected(null);
              break;

            case SWT.F5:
              clickItemInServerTree(1);
              break;

            case SWT.DEL:
              // deleteSelectSession();
              deleteItemListener.widgetSelected(null);
              break;

            case keyC:
              // if (e.stateMask == SWT.CTRL) {
              // TableItem[] selection = tableSessions.getSelection();
              //
              // if (selection.length > 0) {
              // Clipboard clipboard = new Clipboard(Display.getDefault());
              // clipboard.setContents(new Object[] { selection[0].getText() }, new
              // Transfer[] { TextTransfer.getInstance() });
              // clipboard.dispose();
              // }
              // }
              break;

            default:
              break;
          }
        }
      };
}
