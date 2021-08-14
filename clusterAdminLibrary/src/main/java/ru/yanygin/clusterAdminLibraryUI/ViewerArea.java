package ru.yanygin.clusterAdminLibraryUI;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
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
import org.eclipse.swt.widgets.Widget;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.IObjectLockInfo;
import com._1c.v8.ibis.admin.IPortRangeInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.IWorkingProcessInfo;
import com._1c.v8.ibis.admin.IWorkingServerInfo;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	Image robotIcon;
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
	Menu clusterMenu;
	Menu workingServerMenu;
	Menu infobaseNodeMenu;
	Menu infobaseMenu;
	
	TabItem tabSessions;
	TabItem tabConnections;
	TabItem tabLocks;
	TabItem tabWorkingProcesses;
	TabItem tabWorkingServers;
	
	Table tableSessions;
	Table tableConnections;
	Table tableLocks;
	Table tableWorkingProcesses;
	Table tableWorkingServers;
//	Menu tableSessionsMenu;
	
	TreeColumn columnServer;

	UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	Logger LOGGER = LoggerFactory.getLogger("ClusterProvider");
	FontData systemFontData = getDisplay().getSystemFont().getFontData()[0];
	
	enum TreeItemType {
		SERVER, CLUSTER, INFOBASE_NODE, INFOBASE, WORKINGPROCESS_NODE, WORKINGPROCESS, WORKINGSERVER_NODE, WORKINGSERVER
	}
	
	static final String SERVER_INFO = "ServerInfo";
	static final String CLUSTER_ID = "ClusterId";
	static final String INFOBASE_ID = "InfobaseId";
	static final String WORKINGPROCESS_ID = "WorkingProcessId";
	static final String WORKINGSERVER_ID = "WorkingServerId";
	static final String SESSION_ID = "SessionId";
	static final String CONNECTION_ID = "ConnectionId";

	ClusterProvider clusterProvider;

	//@Slf4j
	public ViewerArea(Composite parent, int style, Menu menu, ToolBar toolBar, ClusterProvider clusterProvider) {
		super(parent, style);
		
		this.clusterProvider = clusterProvider;

		String configPath = ".\\config.json";
		this.clusterProvider.readConfig(configPath);

		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		initIcon();
		
//		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT); // Для отладки
//		toolBar.setBounds(0, 0, 500, 23); // Для отладки
		
//		initToolbar(parent, toolBar);
		initMainMenu(sashForm, menu);
		
		initServersTree(sashForm);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		initSessionTable(tabFolder);
		initConnectionsTable(tabFolder);
		initLocksTable(tabFolder);
		initWorkingProcessesTable(tabFolder);
		initWorkingServersTable(tabFolder);
		
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Заполнение списка серверов
		clusterProvider.getServers().forEach((serverKey, server) -> {
			TreeItem serverItem = addServerItemInServersTree(server);
		});
		
		// Пропорции областей
		sashForm.setWeights(new int[] {3, 10});
		
		connectToAllServers(false);

	}
	
	@Override
	public void addPaintListener(PaintListener listener) { // не работает
		connectToAllServers(false);

		super.addPaintListener(listener);
	}
	
//	public void open() {
//		connectToAllServers();
//	}
	
	private void initIcon() {
		LOGGER.info("Start init icon");
		
		serverIcon				= getImage("server_24.png");
		serverIconUp			= getImage("server_up_24.png");
		serverIconDown			= getImage("server_down_24.png");
		serverIconConnecting	= getImage("server_connecting_24.png");
		workingServerIcon		= getImage("working_server_24.png");
		infobaseIcon			= getImage("infobase_24.png");
		infobasesIcon			= getImage("infobases_24.png");
		clusterIcon				= getImage("cluster_24.png");
		
		userIcon				= getImage("user.png");
		sleepUserIcon			= getImage("sleepUser.png");
		robotIcon				= getImage("robot.png");
		
		connectionIcon			= getImage("connection.png");
		locksIcon				= getImage("lock_16.png");
		
		workingProcessesIcon	= getImage("wps.png");
		workingProcessIcon		= getImage("wp.png");
		
		connectActionIcon		= getImage("connect_action_24.png");
		disconnectActionIcon	= getImage("disconnect_action_24.png");
		
		editIcon				= getImage("edit_16.png");
		addIcon					= getImage("add_16.png");
		deleteIcon				= getImage("delete_16.png");
		lockUsersIcon			= getImage("lock_users_16.png");
		updateIcon				= getImage("update.png");
		
		LOGGER.info("Start init succesfully");
	}
	
	private void initToolbar(Composite parent, ToolBar toolBar) {
//		ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);
		
//		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
//		toolBar.setCursor(handCursor);
//		// Cursor needs to be explicitly disposed
//		toolBar.addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent e) {
//				if (handCursor.isDisposed() == false) {
//					handCursor.dispose();
//				}
//			}
//		});
		
		ToolItem toolBarItemFindNewServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemFindNewServers.setText("Find new Servers");
		toolBarItemFindNewServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> newServers = clusterProvider.findNewServers();
				if (!newServers.isEmpty()) {
					fillServersList();
				}
			}
		});

		ToolItem toolBarItemConnectAllServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectAllServers.setText("Connect to all servers");		
		toolBarItemConnectAllServers.addSelectionListener(new SelectionAdapter() {
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
		mainMenuServers.setText("Servers");
		mainMenuServers.setMenu(mainMenuServersParent);
		
		MenuItem toolBarItemFindNewServers = new MenuItem(mainMenuServersParent, SWT.NONE);
		toolBarItemFindNewServers.setText("Find new Servers");
		toolBarItemFindNewServers.setEnabled(false);
		toolBarItemFindNewServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> newServers = clusterProvider.findNewServers();
				if (!newServers.isEmpty()) {
					fillServersList();
				}
			}
		});

		MenuItem toolBarItemConnectAllServers = new MenuItem(mainMenuServersParent, SWT.NONE);
		toolBarItemConnectAllServers.setText("Connect to all servers");		
		toolBarItemConnectAllServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectToAllServers(true);
			}
		});

		MenuItem toolBarItemDisonnectAllServers = new MenuItem(mainMenuServersParent, SWT.NONE);
		toolBarItemDisonnectAllServers.setText("Disconnect from all servers");		
		toolBarItemDisonnectAllServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				diconnectFromAllServers();
			}
		});
		
		MenuItem toolBarItemOpenSettings = new MenuItem(mainMenu, SWT.NONE);
		toolBarItemOpenSettings.setText("Open settings");		
		toolBarItemOpenSettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				SettingsDialog settingsDialog;
				try {
					settingsDialog = new SettingsDialog(getParent().getDisplay().getActiveShell());
				} catch (Exception excp) {
					LOGGER.error("Error init SettingsDialog", excp);
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
	}

	private void initServersTree(SashForm sashForm) {
	
		serversTree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		serversTree.setHeaderVisible(true);
		serversTree.setSortDirection(SWT.UP);
		
		serversTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;

				TreeItem serverItem = item[0];
				
				switch (e.button) {
					case 1: // left click
						selectItemInTree(serverItem);
						break;
						
					case 3: // right click
						setContestMenuInTree(serverItem);
						break;

					default:
						break;
				}
			}
		});
		
		serversTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				// нужно сделать, что бы была реакция только на левый клик мышью
				// по правому клику только устанавливать нужное меню
				// перенесено в addMouseListener

//				TreeItem[] item = serversTree.getSelection();
//				if (item.length == 0)
//					return;
//				
////				TreeItem serverItem = item[0];
//				TreeItem treeItem = (TreeItem) event.item;
//				
//				selectItemInTreeHandler(treeItem);
			}

		});
		
		initServersTreeContextMenu();
		
		
		/////////////////////////
		// сортировка не работает
		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] items = serversTree.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				TreeColumn column = (TreeColumn) e.widget;
				int index = column == columnServer ? 0 : 1;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1) };
							items[i].dispose();
							TreeItem item = new TreeItem(serversTree, SWT.NONE, j);
							item.setText(values);
							items = serversTree.getItems();
							break;
						}
					}
				}
				serversTree.setSortColumn(column);
			}
		};
		/////////////////////////
		
		
		columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Server");
		columnServer.setWidth(300);
		columnServer.addListener(SWT.Selection, sortListener);
//		columnServer.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				serversTree.setSortDirection(serversTree.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
//				serversTree.setSortColumn(columnServer);
//			}
//		});
		
//		TreeColumn columnDescription = new TreeColumn(serversTree, SWT.LEFT);
//		columnDescription.setText("");
//		columnDescription.setWidth(30);
	}
	
	private void initServersTreeContextMenu() {
		
		// Server Menu
		serverMenu = new Menu(serversTree);
		
		// установка активности элементов контекстного меню
		serverMenu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;

				TreeItem serverItem = item[0];
				Server server = getCurrentServerConfig(serverItem);
				boolean serverIsConnected = server.isConnected();

				MenuItem[] menuItems = serverMenu.getItems();

				for (MenuItem menuItem : menuItems) {
					if (menuItem == menuItemConnectServer) // menuItem.equals(menuItemConnectServer)
						menuItem.setEnabled(!serverIsConnected);

					if (menuItem == menuItemDisconnectServer)
						menuItem.setEnabled(serverIsConnected);
				}

			}
		});
		
		serverMenu.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				System.out.println("click");
			}
		});
		
		serverMenu.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event e) {
				System.out.println("menu");
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
		menuItemConnectServer.setText("Connect to Server");
		menuItemConnectServer.setImage(connectActionIcon);
		menuItemConnectServer.setEnabled(false);
		menuItemConnectServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				TreeItem serverItem = item[0];
				
				connectServerItem(serverItem);

			}
		});
		
		menuItemDisconnectServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemDisconnectServer.setText("Disconnect of Server");
		menuItemDisconnectServer.setImage(disconnectActionIcon);
		menuItemDisconnectServer.setEnabled(false);
		menuItemDisconnectServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				TreeItem serverItem = item[0];
				disconnectServerItem(serverItem);

			}
			
		});

		new MenuItem(serverMenu, SWT.SEPARATOR);

		MenuItem menuItemAddNewServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemAddNewServer.setText("Add Server");
		menuItemAddNewServer.setImage(addIcon);
		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				Server newServer = clusterProvider.createNewServer();
				CreateEditServerDialog connectionDialog;
				try {
					connectionDialog = new CreateEditServerDialog(getParent().getDisplay().getActiveShell(), newServer);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init CreateEditServerDialog for new server", excp);
					return;
				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					newServer = null;
					return;
				}

				clusterProvider.addNewServer(newServer);
				TreeItem newServerItem = addServerItemInServersTree(newServer);

//				fillClustersInTree(newServerItem);
				updateClustersInTree(newServerItem);

			}
		});
		
		MenuItem menuItemEditServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.setImage(editIcon);
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				TreeItem serverItem = item[0];
				Server server = getCurrentServerConfig(serverItem);
				CreateEditServerDialog connectionDialog;
				try {
					connectionDialog = new CreateEditServerDialog(getParent().getDisplay().getActiveShell(), server);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init CreateEditServerDialog for server {}", server.getDescription(), excp);
					return;
				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult == 0) {
					// перерисовать в дереве
					serverItem.setText(new String[] { server.getDescription() });
					clusterProvider.saveConfig();
//					fillClustersInTree(serverItem);
					updateClustersInTree(serverItem);
				}

			}

		});
		
		MenuItem menuItemUpdateServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemUpdateServer.setText("Update");
		menuItemUpdateServer.setImage(updateIcon);
		menuItemUpdateServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				updateClustersInTree(item[0]);
			}
		});

		new MenuItem(serverMenu, SWT.SEPARATOR);
		
		MenuItem menuItemDeleteServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemDeleteServer.setText("Remove Server");
		menuItemDeleteServer.setImage(deleteIcon);
		menuItemDeleteServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
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
		menuItemCreateCluster.setText("Create Cluster");
		menuItemCreateCluster.setImage(addIcon);
		menuItemCreateCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				
				CreateEditClusterDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditClusterDialog(getParent().getDisplay().getActiveShell(), server, null);
				} catch (Exception excp) {
					LOGGER.error("Error init CreateEditClusterDialog for new cluster", excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
					updateClustersInTree(item[0].getParentItem());
				}
			}
		});
		
		MenuItem menuItemEditCluster = new MenuItem(clusterMenu, SWT.NONE);
		menuItemEditCluster.setText("Edit Cluster");
		menuItemEditCluster.setImage(editIcon);
		menuItemEditCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditClusterDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditClusterDialog(getParent().getDisplay().getActiveShell(), server, clusterId);
				} catch (Exception excp) {
					LOGGER.error("Error init CreateEditClusterDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
					updateClustersInTree(item[0]);
				}
			}
		});
		
		MenuItem menuItemUpdateCluster = new MenuItem(clusterMenu, SWT.NONE);
		menuItemUpdateCluster.setText("Update");
		menuItemUpdateCluster.setImage(updateIcon);
		menuItemUpdateCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				fillChildrenItemsOfCluster(item[0], server);

			}
		});
		
		new MenuItem(clusterMenu, SWT.SEPARATOR);
		
		MenuItem menuItemDeleteCluster = new MenuItem(clusterMenu, SWT.NONE);
		menuItemDeleteCluster.setText("Delete cluster");
		menuItemDeleteCluster.setImage(deleteIcon);
		menuItemDeleteCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				var messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION |SWT.YES | SWT.NO);
				messageBox.setMessage("Удаление кластера приведет к удалению его настроек и списка зарегистрированных информационных баз. Вы действительно хотите удалить кластер?");
//				messageBox.setMessage("Deleting a cluster will delete its settings and the list of registered information databases. Do you really want to delete the cluster?");
				int rc = messageBox.open();

				if (rc == SWT.YES && server.unregCluster(clusterId))
					item[0].dispose();
				
			}
		});
		
	}
	
	private void initWorkingServerMenu() {
		
		workingServerMenu = new Menu(serversTree);
		
		MenuItem menuItemCreateWorkingServer = new MenuItem(workingServerMenu, SWT.NONE);
		menuItemCreateWorkingServer.setText("Create working server");
		menuItemCreateWorkingServer.setImage(addIcon);
		menuItemCreateWorkingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditWorkingServerDialog editWorkingServerDialog;
				try {
					editWorkingServerDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), server, clusterId, null);
				} catch (Exception excp) {
					LOGGER.error("Error init WorkingServerDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editWorkingServerDialog.open();
				if (dialogResult == 0) {
					var newWorkingServerUuid = editWorkingServerDialog.getNewWorkingServerId();
					if (newWorkingServerUuid != null) {
						IWorkingServerInfo workingServerInfo = server.getWorkingServerInfo(clusterId, newWorkingServerUuid);
						addWorkingServerItemInNode(item[0].getParentItem(), workingServerInfo);
					}
				}
			}
		});
		
		MenuItem menuItemEditWorkingServer = new MenuItem(workingServerMenu, SWT.NONE);
		menuItemEditWorkingServer.setText("Edit working server");
		menuItemEditWorkingServer.setImage(editIcon);
		menuItemEditWorkingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID workingServerId = getCurrentWorkingServerId(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditWorkingServerDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), server, clusterId, workingServerId);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init WorkingServerDialog for cluster id {}", workingServerId, excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
				}
			}
		});
	}

	private void initInfobaseNodeMenu() {

		infobaseNodeMenu = new Menu(serversTree);

		MenuItem menuItemNewInfobase = new MenuItem(infobaseNodeMenu, SWT.NONE);
		menuItemNewInfobase.setText("Create Infobase");
		menuItemNewInfobase.setImage(addIcon);
		menuItemNewInfobase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterId, null);
				} catch (Exception excp) {
					LOGGER.error("Error in CreateInfobaseDialog", excp);
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
					var newInfobaseUuid = infobaseDialog.getNewInfobaseUUID();
					if (newInfobaseUuid != null) {
						IInfoBaseInfoShort infoBaseInfo = server.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
						addInfobaseItemInInfobaseNode(item[0], infoBaseInfo);
					}
				}
			}
		});
		
		MenuItem menuItemUpdateInfobases = new MenuItem(infobaseNodeMenu, SWT.NONE);
		menuItemUpdateInfobases.setText("Update infobases");
		menuItemUpdateInfobases.setImage(updateIcon);
		menuItemUpdateInfobases.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				fillInfobasesOfCluster(item[0], server);
				
//				UUID clusterId = getCurrentClusterId(item[0]);
//				
//				CreateInfobaseDialog infobaseDialog;
//				try {
//					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), config, clusterId, null);
//				} catch (Exception excp) {
//					LOGGER.error("Error in CreateInfobaseDialog", excp);
//					return;
//				}
//				
//				int dialogResult = infobaseDialog.open();
//				if (dialogResult == 0) {
//					var newInfobaseUuid = infobaseDialog.getNewInfobaseUUID();
//					if (newInfobaseUuid != null) {
//						IInfoBaseInfoShort infoBaseInfo = config.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
//						addInfobaseItemInInfobaseNode(item[0], infoBaseInfo);
//					}
//				}
			}
		});
	}
	
	private void initInfobaseMenu() {

		infobaseMenu = new Menu(serversTree);
		
		MenuItem menuItemCopyInfobase = new MenuItem(infobaseMenu, SWT.NONE);
		menuItemCopyInfobase.setText("Create a new Infobase using this");
		menuItemCopyInfobase.setImage(addIcon);
		menuItemCopyInfobase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID sampleInfobaseId = getCurrentInfobaseId(item[0]);

				CreateInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterId, sampleInfobaseId);
				} catch (Exception excp) {
					LOGGER.error("Error in CreateInfobaseDialog", excp);
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
					var newInfobaseUuid = infobaseDialog.getNewInfobaseUUID();
					if (newInfobaseUuid != null) {
						IInfoBaseInfoShort infoBaseInfo = server.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
						addInfobaseItemInInfobaseNode(item[0].getParentItem(), infoBaseInfo);
					}
				}
			}
		});
		
		MenuItem menuItemEditInfobase = new MenuItem(infobaseMenu, SWT.NONE);
		menuItemEditInfobase.setText("Edit Infobase");
		menuItemEditInfobase.setImage(editIcon);
		menuItemEditInfobase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				EditInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new EditInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
				} catch (Exception excp) {
					excp.printStackTrace();
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
//					server.clusterConnector.updateInfoBase(server.clusterID, infoBaseInfo);
				}
			}
		});
		
		MenuItem menuItemDeleteInfobase = new MenuItem(infobaseMenu, SWT.NONE);
		menuItemDeleteInfobase.setText("Delete Infobase");
		menuItemDeleteInfobase.setImage(deleteIcon);
		menuItemDeleteInfobase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				DropInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new DropInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterId, infobaseId);
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
		infobaseMenuSessionManage.setText("Session manage");
		infobaseMenuSessionManage.setMenu(infobaseSubMenuSessionManage);
//		infobaseSubMenuSessionManage.setImage(terminateSessionIcon);
		
		MenuItem menuItemLockUserSessionsNow = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
		menuItemLockUserSessionsNow.setText("Lock sessions now");
		menuItemLockUserSessionsNow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				TreeItem selectItem = item[0];
				
				Server server = getCurrentServerConfig(selectItem);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(selectItem);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);

				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterId, infobaseId);

				infoBaseInfo.setScheduledJobsDenied(true);
				infoBaseInfo.setSessionsDenied(true);
				infoBaseInfo.setDeniedFrom(null);
				infoBaseInfo.setDeniedTo(null);
				infoBaseInfo.setDeniedMessage("");
				infoBaseInfo.setDeniedParameter("");
				infoBaseInfo.setPermissionCode("");
				
				server.updateInfoBase(clusterId, infoBaseInfo);
			}
		});		

		MenuItem menuItemTerminateAllSessions = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
		menuItemTerminateAllSessions.setText("Terminate all sessions");
		menuItemTerminateAllSessions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);
				
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				server.terminateAllSessionsOfInfobase(clusterId, infobaseId, false);
			}
		});
		
		MenuItem menuItemTerminateUserSessions = new MenuItem(infobaseSubMenuSessionManage, SWT.NONE);
		menuItemTerminateUserSessions.setText("Terminate user sessions");
		menuItemTerminateUserSessions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getCurrentServerConfig(item[0]);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);
				
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				server.terminateAllSessionsOfInfobase(clusterId, infobaseId, true);
			}
		});
	}
	
	private void initSessionTable(TabFolder tabFolder) {

		tabSessions = new TabItem(tabFolder, SWT.NONE);
		tabSessions.setText("Sessions");

		tableSessions = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabSessions.setControl(tableSessions);
		tableSessions.setHeaderVisible(true);
		tableSessions.setLinesVisible(true);
		
		initSessionsTableContextMenu();
		
		addTableColumn(tableSessions, "Username", 		140);
		addTableColumn(tableSessions, "Infobase", 		100);
		addTableColumn(tableSessions, "Session N", 		70);
		addTableColumn(tableSessions, "Connection N", 	80);
		addTableColumn(tableSessions, "Started At", 	140);
		addTableColumn(tableSessions, "Last active at", 140);
		addTableColumn(tableSessions, "Computer", 		100);
		addTableColumn(tableSessions, "Application", 	100);
		
		addTableColumn(tableSessions, "Host", 			100);
		addTableColumn(tableSessions, "Port", 			60);
		addTableColumn(tableSessions, "PID", 			60);
				
		addTableColumn(tableSessions, "License", 		60);
		addTableColumn(tableSessions, "Is sleep", 		40);
		addTableColumn(tableSessions, "Sleep after",	60);
		addTableColumn(tableSessions, "Kill after", 	60);
	}
	
	private void initSessionsTableContextMenu() {
		
		Menu tableSessionsMenu = new Menu(tableSessions);
		tableSessions.setMenu(tableSessionsMenu);
		
		MenuItem menuItemKillSession = new MenuItem(tableSessionsMenu, SWT.NONE);
		menuItemKillSession.setText("Kill session");
		menuItemKillSession.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selectedItems = tableSessions.getSelection();
				if (selectedItems.length == 0)
					return;
				
				for (TableItem item : selectedItems) {
					item.setForeground(new Color(150,0,0));
					
					UUID clusterId = (UUID) item.getData(CLUSTER_ID);
					UUID sessionId = (UUID) item.getData(SESSION_ID);
					Server server = (Server) item.getData(SERVER_INFO);
					
					server.terminateSession(clusterId, sessionId);
					
					// update tableSessions
					item.dispose();
				}
				
			}
		});
	}
	
	private void initConnectionsTable(TabFolder tabFolder) {

		tabConnections = new TabItem(tabFolder, SWT.NONE);
		tabConnections.setText("Connections");
		
		tableConnections = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabConnections.setControl(tableConnections);
		tableConnections.setHeaderVisible(true);
		tableConnections.setLinesVisible(true);
		
		initConnectionsTableContextMenu();
		
		addTableColumn(tableConnections, "Infobase", 		100);
		addTableColumn(tableConnections, "Connection", 		100);
		addTableColumn(tableConnections, "Session", 		100);
		addTableColumn(tableConnections, "Hostname", 		100);
		addTableColumn(tableConnections, "Application", 	100);
		addTableColumn(tableConnections, "Connected at", 	100);
		addTableColumn(tableConnections, "Infobase connection ID", 100);
		addTableColumn(tableConnections, "rphost ID", 		100);
		
	}
	
	private void initConnectionsTableContextMenu() {
		
		Menu tableConnectionsMenu = new Menu(tableConnections);
		tableConnections.setMenu(tableConnectionsMenu);
		
		MenuItem menuItemKillSession = new MenuItem(tableConnectionsMenu, SWT.NONE);
		menuItemKillSession.setText("Kill connection");
		menuItemKillSession.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selectedItems = tableConnections.getSelection();
				if (selectedItems.length == 0)
					return;
				
				for (TableItem item : selectedItems) {
					item.setForeground(new Color(150,0,0));
					
					Server server = (Server) item.getData(SERVER_INFO);
					UUID clusterId = (UUID) item.getData(CLUSTER_ID);
					UUID pricessId = (UUID) item.getData(WORKINGPROCESS_ID);
					UUID connectionId = (UUID) item.getData(CONNECTION_ID);
					
					server.disconnectConnection(clusterId, pricessId, connectionId);
					
					// update tableConnections
					item.dispose();
				}
				
			}
		});
	}
	
	private void initLocksTable(TabFolder tabFolder) {

		tabLocks = new TabItem(tabFolder, SWT.NONE);
		tabLocks.setText("Locks");
		
		tableLocks = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabLocks.setControl(tableLocks);
		tableLocks.setHeaderVisible(true);
		tableLocks.setLinesVisible(true);
		
		initLocksTableContextMenu();
		
		addTableColumn(tableLocks, "Description", 	250);
		addTableColumn(tableLocks, "Infobase", 		100);
		addTableColumn(tableLocks, "Connection", 	80);
		addTableColumn(tableLocks, "Session", 		80);
		addTableColumn(tableLocks, "Computer", 		100);
		addTableColumn(tableLocks, "Application", 	140);
		addTableColumn(tableLocks, "Hostname", 		100);
		addTableColumn(tableLocks, "Port", 			100);
		addTableColumn(tableLocks, "Locked at", 	120);
//		addTableColumn(tableLocks, "Locked Object", 100);
		
	}
	
	private void initLocksTableContextMenu() {
		// Пока не понятен состав меню
	}
	
	private void initWorkingProcessesTable(TabFolder tabFolder) {

		tabWorkingProcesses = new TabItem(tabFolder, SWT.NONE);
		tabWorkingProcesses.setText("Working processes");
		
		tableWorkingProcesses = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabWorkingProcesses.setControl(tableWorkingProcesses);
		tableWorkingProcesses.setHeaderVisible(true);
		tableWorkingProcesses.setLinesVisible(true);
		
		initWorkingProcessesTableContextMenu();
		
		addTableColumn(tableWorkingProcesses, "Computer", 		100);
		addTableColumn(tableWorkingProcesses, "Port", 			100);
		addTableColumn(tableWorkingProcesses, "Using", 			250);
		addTableColumn(tableWorkingProcesses, "Enabled", 		100);
		addTableColumn(tableWorkingProcesses, "Active", 		80);
		addTableColumn(tableWorkingProcesses, "PID", 			80);
		addTableColumn(tableWorkingProcesses, "Memory", 		140);
		addTableColumn(tableWorkingProcesses, "MemoryExceeded", 100);
		addTableColumn(tableWorkingProcesses, "Available performance", 	120);
		
	}
	
	private void initWorkingProcessesTableContextMenu() {
		// Пока не понятен состав меню
	}
	
	private void initWorkingServersTable(TabFolder tabFolder) {

		tabWorkingServers = new TabItem(tabFolder, SWT.NONE);
		tabWorkingServers.setText("Working processes");
		
		tableWorkingServers = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabWorkingServers.setControl(tableWorkingServers);
		tableWorkingServers.setHeaderVisible(true);
		tableWorkingServers.setLinesVisible(true);
		
		initWorkingServersTableContextMenu();
		
		addTableColumn(tableWorkingServers, "Description", 		100);
		addTableColumn(tableWorkingServers, "Computer", 		100);
		addTableColumn(tableWorkingServers, "IP Port", 			250);
		addTableColumn(tableWorkingServers, "Range IP Port", 	100);
//		addTableColumn(tableWorkingServers, "Max memory WP", 	80);
		addTableColumn(tableWorkingServers, "IB per process limit", 	80);
		addTableColumn(tableWorkingServers, "Conn per process limit", 	140);
		addTableColumn(tableWorkingServers, "IP Port main manager", 100);
		addTableColumn(tableWorkingServers, "Dedicated managers", 	120);
		addTableColumn(tableWorkingServers, "Main server", 		100);
		
	}
	
	private void initWorkingServersTableContextMenu() {
		
		Menu workingServersMenu = new Menu(tableWorkingServers);
		tableWorkingServers.setMenu(workingServersMenu);
		
		MenuItem menuItemCreateWorkingServer = new MenuItem(workingServersMenu, SWT.NONE);
		menuItemCreateWorkingServer.setText("Create working server");
		menuItemCreateWorkingServer.setImage(addIcon);
		menuItemCreateWorkingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] item = tableWorkingServers.getSelection();
				if (item.length == 0)
					return;
				
				Server server = (Server) item[0].getData(SERVER_INFO);
				UUID clusterId = (UUID) item[0].getData(CLUSTER_ID);
				
				CreateEditWorkingServerDialog editWorkingServerDialog;
				try {
					editWorkingServerDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), server, clusterId, null);
				} catch (Exception excp) {
					LOGGER.error("Error init WorkingServerDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editWorkingServerDialog.open();
				if (dialogResult == 0) {
					var newWorkingServerUuid = editWorkingServerDialog.getNewWorkingServerId();
					if (newWorkingServerUuid != null) {
						IWorkingServerInfo workingServerInfo = server.getWorkingServerInfo(clusterId, newWorkingServerUuid);
						addWorkingServerInTable(server, clusterId, workingServerInfo);
					}
				}
			}
		});
		
		MenuItem menuItemEditWorkingServer = new MenuItem(workingServersMenu, SWT.NONE);
		menuItemEditWorkingServer.setText("Edit working server");
		menuItemEditWorkingServer.setImage(editIcon);
		menuItemEditWorkingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] item = tableWorkingServers.getSelection();
				if (item.length == 0)
					return;
				
				Server server = (Server) item[0].getData(SERVER_INFO);
				UUID clusterId = (UUID) item[0].getData(CLUSTER_ID);
				UUID workingServerId = (UUID) item[0].getData(WORKINGSERVER_ID);
				
				CreateEditWorkingServerDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), server, clusterId, workingServerId);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init WorkingServerDialog for cluster id {}", workingServerId, excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
				}
			}
		});	}

	private void fillServersList() {
		// TODO Auto-generated method stub
		
	}
	
	private void fillClustersInTree(TreeItem serverItem) {
		
		// Пока что удалить все кластера из списка, может лучше добавить недостающие?
		TreeItem[] clustersItems = serverItem.getItems();
		for (TreeItem clusterItem : clustersItems) {
//			clusterItem.dispose();
			disposeTreeItemWithChildren(clusterItem);
		}

		Server server = getCurrentServerConfig(serverItem);
		
//		// смена иконки сервера на вкл/выкл
//		serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);
		
		if (!server.isConnected()) {
			return;
		}
		
		List<IClusterInfo> clusters = server.getClusters();
		clusters.forEach(clusterInfo -> {
			TreeItem clusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
			
			// Пока что удалить все из списка, может лучше добавить недостающие?
			// Тут же у нас пусто?
//			TreeItem[] ibItems = clusterItem.getItems();
//			for (TreeItem treeItem : ibItems) {
//				treeItem.dispose();
//			}
			
			// Заполнение дерева кластера
			fillChildrenItemsOfCluster(clusterItem, server);

		});
		
		// Разворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(clusterProvider.getCommonConfig().expandServersTree);
	}
	
	private void updateClustersInTreeOld(TreeItem serverItem) {

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
			List<IClusterInfo> foundCluster = clusters.stream()
													.filter(c -> c.getClusterId().equals(currentClusterId))
													.collect(Collectors.toList());

			if (foundCluster.isEmpty()) {
				disposeTreeItemWithChildren(clusterItem);
			}
		}
		
		// добавление новых элементов
//		clustersItems = serverItem.getItems();
		clusters.forEach(clusterInfo -> {
			var itemFound = false;
			for (TreeItem clusterItem : serverItem.getItems()) {
				if (getCurrentClusterId(clusterItem).equals(clusterInfo.getClusterId())) {
					itemFound = true;
					break;
				}
			}
			
			if (!itemFound) {
				TreeItem clusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
				
				// Заполнение дерева кластера
				fillChildrenItemsOfCluster(clusterItem, server);
			}
			
		});
		
		
		// Разворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(!clusters.isEmpty());
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
			List<IClusterInfo> foundCluster = clusters.stream()
													.filter(c -> c.getClusterId().equals(currentClusterId))
													.collect(Collectors.toList());

			if (foundCluster.isEmpty()) {
				disposeTreeItemWithChildren(clusterItem);
			}
		}
		
		// добавление новых элементов
		clusters.forEach(clusterInfo -> {
			var itemFound = false;
			TreeItem currentClusterItem = null;
			for (TreeItem clusterItem : serverItem.getItems()) {
				if (getCurrentClusterId(clusterItem).equals(clusterInfo.getClusterId())) {
					currentClusterItem = clusterItem;
					itemFound = true;
					break;
				}
			}
			
			if (!itemFound)
				currentClusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
				
			// Заполнение дерева кластера
			if (currentClusterItem != null)
				fillChildrenItemsOfCluster(currentClusterItem, server);
			
		});
		
		
		// Разворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(ClusterProvider.getCommonConfig().expandServersTree);
	}
	
	private void fillChildrenItemsOfCluster(TreeItem clusterItem, Server server) {
//		TreeItem[] clusterItems = clusterItem.getItems();
		
		fillInfobasesOfCluster(clusterItem, server);
		fillWorkingProcessesInCluster(clusterItem, server);
		fillWorkingServersInCluster(clusterItem, server);
		
		clusterItem.setExpanded(ClusterProvider.getCommonConfig().expandClustersTree);
	}
	
	private void fillInfobasesOfCluster(TreeItem clusterItem, Server server) {

		TreeItem infobasesNode = null;
		for (TreeItem treeItem : clusterItem.getItems()) {
			if (treeItem.getData("Type") == TreeItemType.INFOBASE_NODE) {
				infobasesNode = treeItem;
				break;
			}
		}
		
		if (infobasesNode == null) {
			infobasesNode = new TreeItem(clusterItem, SWT.NONE);
			infobasesNode.setData("Type", TreeItemType.INFOBASE_NODE);
			infobasesNode.setImage(infobasesIcon);
			infobasesNode.setChecked(false);
		}
		
		UUID clusterId = getCurrentClusterId(infobasesNode);
		List<IInfoBaseInfoShort> infoBases = server.getInfoBasesShort(clusterId);
		
		var infobasesNodeTitle = String.format("Infobases (%s)", infoBases.size());
		infobasesNode.setText(new String[] { infobasesNodeTitle });
//		infobasesNode.setText(new String[] { String.format("Infobases (%s)", infoBases.size()) });
		
		if (infoBases.isEmpty()) {
			for (TreeItem infobase : infobasesNode.getItems()) {
				infobase.dispose();
			}
			return;
		}
		
		// удаление несуществующих элементов
		for (TreeItem infobaseItem : infobasesNode.getItems()) {
			UUID currentInfobaseId = getCurrentInfobaseId(infobaseItem);
			List<IInfoBaseInfoShort> foundItems = infoBases.stream()
															.filter(c -> c.getInfoBaseId().equals(currentInfobaseId))
															.collect(Collectors.toList());

			if (foundItems.isEmpty()) {
				infobaseItem.dispose();
			}
		}
		
		// добавление новых элементов
		for (IInfoBaseInfoShort infoBaseInfo : infoBases) {
//		infoBases.forEach(infoBaseInfo -> {
			
			var itemFound = false;
			for (TreeItem infobaseItem : infobasesNode.getItems()) {
				if (getCurrentInfobaseId(infobaseItem).equals(infoBaseInfo.getInfoBaseId())) {
					itemFound = true;
					break;
				}
			}
	
			if (!itemFound)
				addInfobaseItemInInfobaseNode(infobasesNode, infoBaseInfo);
		};
		
		infobasesNode.setExpanded(ClusterProvider.getCommonConfig().expandInfobasesTree);

	}
	
	private void fillWorkingProcessesInCluster(TreeItem clusterItem, Server server) {

		TreeItem workingProcessesNode = null;
		for (TreeItem treeItem : clusterItem.getItems()) {
			if (treeItem.getData("Type") == TreeItemType.WORKINGPROCESS_NODE) {
				workingProcessesNode = treeItem;
				break;
			}
		}

		if (!ClusterProvider.getCommonConfig().showWorkingProcessesTree) {
			if (workingProcessesNode != null)
				workingProcessesNode.dispose();
			return;
		}
		
		if (workingProcessesNode == null) {
			workingProcessesNode = new TreeItem(clusterItem, SWT.NONE);
			workingProcessesNode.setData("Type", TreeItemType.WORKINGPROCESS_NODE);
			workingProcessesNode.setImage(workingProcessesIcon);
			workingProcessesNode.setChecked(false);
		}
		
		UUID clusterId = getCurrentClusterId(workingProcessesNode);
		List<IWorkingProcessInfo> wProcesses = server.getWorkingProcesses(clusterId);

		var workingProcessesNodeTitle 	= String.format("Working processes (%s)", wProcesses.size());
		workingProcessesNode.setText(new String[] { workingProcessesNodeTitle });
//		workingProcessesNode.setText(new String[] { String.format("Working processes (%s)", wProcesses.size()) });
		
		if (wProcesses.isEmpty()) {
			for (TreeItem workingProcess : workingProcessesNode.getItems()) {
				workingProcess.dispose();
			}
			return;
		}
		
		// удаление несуществующих элементов
		for (TreeItem workingProcessItem : workingProcessesNode.getItems()) {
			UUID currentWorkingProcessId = getCurrentWorkingProcessId(workingProcessItem);
			List<IWorkingProcessInfo> foundItems = wProcesses.stream()
															.filter(c -> c.getWorkingProcessId().equals(currentWorkingProcessId))
															.collect(Collectors.toList());

			if (foundItems.isEmpty()) {
				workingProcessItem.dispose();
			}
		}
		
		// добавление новых элементов
		for (IWorkingProcessInfo workingProcessInfo : wProcesses) {
//		wProcesses.forEach(workingProcessInfo -> {
			
			var itemFound = false;
			for (TreeItem workingProcessItem : workingProcessesNode.getItems()) {
				if (getCurrentWorkingProcessId(workingProcessItem).equals(workingProcessInfo.getWorkingProcessId())) {
					itemFound = true;
					break;
				}
			}
	
			if (!itemFound)
				addWorkingProcessItemInNode(workingProcessesNode, workingProcessInfo);
		};
		
	}
	
	private void fillWorkingServersInCluster(TreeItem clusterItem, Server server) {

		TreeItem workingServersNode = null;
		for (TreeItem treeItem : clusterItem.getItems()) {
			if (treeItem.getData("Type") == TreeItemType.WORKINGSERVER_NODE) {
				workingServersNode = treeItem;
				break;
			}
		}

		if (!ClusterProvider.getCommonConfig().showWorkingServersTree) {
			if (workingServersNode != null)
				workingServersNode.dispose();
			return;
		}
		
		if (workingServersNode == null) {
			workingServersNode = new TreeItem(clusterItem, SWT.NONE);
			workingServersNode.setData("Type", TreeItemType.WORKINGSERVER_NODE);
			workingServersNode.setImage(workingServerIcon);
			workingServersNode.setChecked(false);
		}

		UUID clusterId = getCurrentClusterId(workingServersNode);
		List<IWorkingServerInfo> wServers = server.getWorkingServers(clusterId);
		
		var workingServerNodeTitle = String.format("Working servers (%s)", wServers.size());
		workingServersNode.setText(new String[] { workingServerNodeTitle });
//		workingServersNode.setText(new String[] { String.format("Working servers (%s)", wServers.size()) });
		
		if (wServers.isEmpty()) {
			for (TreeItem workingServerItem : workingServersNode.getItems()) {
				workingServerItem.dispose();
			}
			return;
		}
		
		// удаление несуществующих элементов
		for (TreeItem workingServerItem : workingServersNode.getItems()) {
			UUID currentWorkingServerId = getCurrentWorkingServerId(workingServerItem);
			List<IWorkingServerInfo> foundItems = wServers.stream()
															.filter(c -> c.getWorkingServerId().equals(currentWorkingServerId))
															.collect(Collectors.toList());

			if (foundItems.isEmpty()) {
				workingServerItem.dispose();
			}
		}
		
		// добавление новых элементов
		for (IWorkingServerInfo workingServerInfo : wServers) {
//		wServers.forEach(workingServerInfo -> {
			
			var itemFound = false;
			for (TreeItem workingServerItem : workingServersNode.getItems()) {
				if (getCurrentWorkingServerId(workingServerItem).equals(workingServerInfo.getWorkingServerId())) {
					itemFound = true;
					break;
				}
			}
	
			if (!itemFound)
				addWorkingServerItemInNode(workingServersNode, workingServerInfo);
		};
		
	}	

	private TreeItem addServerItemInServersTree(Server server) {
		
		var item = new TreeItem(serversTree, SWT.NONE);
		
		item.setText(new String[] { server.getDescription()});
		item.setData("Type", TreeItemType.SERVER);
		item.setData(SERVER_INFO, server);
		
		if (server.isConnected()) {
			item.setImage(serverIconUp);
		} else {
			item.setImage(serverIcon);
		}
		
		return item;
	}
	
	private TreeItem addClusterItemInServersTree(TreeItem serverItem, IClusterInfo clusterInfo) {
		
		var clusterTitle 				= String.format("%s (%s)", clusterInfo.getName(), clusterInfo.getMainPort());
//		var infobasesNodeTitle 			= String.format("Infobases (%s)", 0);
//		var workingProcessesNodeTitle 	= String.format("Working processes (%s)", 0);
//		var workingServerNodeTitle 		= String.format("Working servers (%s)", 0);
		
		var clusterItem = new TreeItem(serverItem, SWT.NONE);
		clusterItem.setText(new String[] { clusterTitle });
		clusterItem.setData("Type", TreeItemType.CLUSTER);
//		item.setData("ClusterName", clusterInfo.getName()); // Зачем?
		clusterItem.setData(CLUSTER_ID, clusterInfo.getClusterId());
		clusterItem.setImage(clusterIcon);

//		var infobasesNodeItem = new TreeItem(clusterItem, SWT.NONE);
//		infobasesNodeItem.setText(new String[] { infobasesNodeTitle });
//		infobasesNodeItem.setData("Type", TreeItemType.INFOBASE_NODE);
//		infobasesNodeItem.setImage(infobasesIcon);
//		infobasesNodeItem.setChecked(false);

//		if (ClusterProvider.getCommonConfig().showWorkingProcessesTree) {
//			var workingProcessesNodeItem = new TreeItem(clusterItem, SWT.NONE);
//			workingProcessesNodeItem.setText(new String[] { workingProcessesNodeTitle });
//			workingProcessesNodeItem.setData("Type", TreeItemType.WORKINGPROCESS_NODE);
//			workingProcessesNodeItem.setImage(workingProcessesIcon);
//			workingProcessesNodeItem.setChecked(false);
//		}
		
//		if (ClusterProvider.getCommonConfig().showWorkingServersTree) {
//			var workingServerNodeItem = new TreeItem(clusterItem, SWT.NONE);
//			workingServerNodeItem.setText(new String[] { workingServerNodeTitle });
//			workingServerNodeItem.setData("Type", TreeItemType.WORKINGSERVER_NODE);
//			workingServerNodeItem.setImage(workingServerIcon);
//			workingServerNodeItem.setChecked(false);
//		}
		
		
		return clusterItem;
	}
		
	private void addInfobaseItemInInfobaseNode(TreeItem infobaseNode, IInfoBaseInfoShort ibInfo) {
		var item = new TreeItem(infobaseNode, SWT.NONE);
		
		item.setText(new String[] { ibInfo.getName()});
		item.setData("Type", TreeItemType.INFOBASE);
		item.setData(INFOBASE_ID, ibInfo.getInfoBaseId());
		item.setImage(0, infobaseIcon);
		item.setChecked(false);
		
//		item.setImage(1, ibInfo.isSessionsDenied() ? lockUsersIcon : null);
		
	}
	
	private void addWorkingProcessItemInNode(TreeItem wpNodeItem, IWorkingProcessInfo wpInfo) {
		var item = new TreeItem(wpNodeItem, SWT.NONE);
		
		var itemTitle = String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort());
		
		item.setText(new String[] { itemTitle});
		item.setData("Type", TreeItemType.WORKINGPROCESS);
		item.setData(WORKINGPROCESS_ID, wpInfo.getWorkingProcessId());
		item.setImage(workingProcessIcon);
		item.setChecked(false);
	}
	
	private void addWorkingServerItemInNode(TreeItem wsNodeItem, IWorkingServerInfo wpInfo) {
		var item = new TreeItem(wsNodeItem, SWT.NONE);
		
		var itemTitle = String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort());
		
		item.setText(new String[] { itemTitle});
		item.setData("Type", TreeItemType.WORKINGSERVER);
		item.setData(WORKINGSERVER_ID, wpInfo.getWorkingServerId());
		item.setImage(workingServerIcon);
		item.setChecked(false);
	}
	

	private void addSessionInTable(Server server, UUID clusterId, UUID infobaseId, ISessionInfo sessionInfo, List<IInfoBaseConnectionShort> connections) {
		TableItem sessionItem = new TableItem(tableSessions, SWT.NONE);
		
		if (infobaseId == null)
			infobaseId = sessionInfo.getInfoBaseId();
		
		String infobaseName = server.getInfoBaseName(clusterId, infobaseId);
		
		var connectionNumber = "";
		if (!sessionInfo.getConnectionId().equals(emptyUuid)) {
			List<IInfoBaseConnectionShort> thisConnection = connections.stream()
																	.filter(c -> c.getInfoBaseConnectionId().equals(sessionInfo.getConnectionId()))
																	.collect(Collectors.toList());
			if (!thisConnection.isEmpty())
				connectionNumber = String.valueOf(thisConnection.get(0).getConnId());
		}

		var wpHostName = "";
		var wpMainPort = "";
		var wpPid = "";
		if (!sessionInfo.getWorkingProcessId().equals(emptyUuid)) {
			IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, sessionInfo.getWorkingProcessId());
			
			wpHostName = wpInfo.getHostName();
			wpMainPort = Integer.toString(wpInfo.getMainPort());
			wpPid = wpInfo.getPid();
		}
		var license = "";
		if (!sessionInfo.getLicenses().isEmpty())
			license = sessionInfo.getLicenses().get(0).getFullPresentation();
		
		String[] itemText = {
							sessionInfo.getUserName(),
							infobaseName,
							Integer.toString(sessionInfo.getSessionId()),
							connectionNumber,
							sessionInfo.getStartedAt().toString(),
							sessionInfo.getLastActiveAt().toString(),
							sessionInfo.getHost(),
							server.getApplicationName(sessionInfo.getAppId()),
							wpHostName,
							wpMainPort,
							wpPid,
							license,
							Boolean.toString(sessionInfo.getHibernate()), // сеанс уснул
							Integer.toString(sessionInfo.getPassiveSessionHibernateTime()),
							Integer.toString(sessionInfo.getHibernateSessionTerminationTime())
							
//							sessionInfo.getClientIPAddress(),
//							sessionInfo.getCurrentServiceName(),
//							sessionInfo.getDataSeparation(),
//							sessionInfo.getDbProcInfo(),
//							sessionInfo.getBlockedByDbms(),
//							sessionInfo.getBlockedByLs(),
//							sessionInfo.getBytesAll(),
//							sessionInfo.getBytesLast5Min(),
//							sessionInfo.getCallsAll(),
//							sessionInfo.getCallsLast5Min(),
//							sessionInfo.getCpuTimeAll(),
//							sessionInfo.getCpuTimeCurrent(),
//							sessionInfo.getCpuTimeLast5Min(),
//							sessionInfo.getDbmsBytesAll(),
//							sessionInfo.getDbmsBytesLast5Min(),
//							sessionInfo.getDbProcTook(),
//							sessionInfo.getDbProcTookAt(),
//							sessionInfo.getDurationAll(),
//							sessionInfo.getDurationAllDbms(),
//							sessionInfo.getDurationAllService(),
//							sessionInfo.getDurationCurrent(),
//							sessionInfo.getDurationCurrentDbms(),
//							sessionInfo.getDurationCurrentService(),
//							sessionInfo.getDurationLast5Min(),
//							sessionInfo.getDurationLast5MinDbms(),
//							sessionInfo.getDurationLast5MinService(),
//							sessionInfo.getHibernate(), // уснул
//							sessionInfo.getPassiveSessionHibernateTime(),
//							sessionInfo.getHibernateSessionTerminationTime(),
//							sessionInfo.getLicenses(),
//							sessionInfo.getMemoryCurrent(),
//							sessionInfo.getMemoryLast5Min(),
//							sessionInfo.getMemoryTotal(),
//							sessionInfo.getReadBytesCurrent(),
//							sessionInfo.getReadBytesLast5Min(),
//							sessionInfo.getReadBytesTotal(),
//							sessionInfo.getWriteBytesCurrent(),
//							sessionInfo.getWriteBytesLast5Min(),
//							sessionInfo.getWriteBytesTotal()
							};

		sessionItem.setText(itemText);
		sessionItem.setData(SERVER_INFO, 	server);
		sessionItem.setData(CLUSTER_ID, 	clusterId);
		sessionItem.setData(SESSION_ID, 	sessionInfo.getSid()); //sessionInfo.getSessionId() ???
		sessionItem.setImage(userIcon);
		sessionItem.setChecked(false);
		
		switch (sessionInfo.getAppId()) {
			case Server.THIN_CLIENT:
			case Server.THICK_CLIENT:
			case Server.DESIGNER:
				sessionItem.setImage(sessionInfo.getHibernate() ? sleepUserIcon : userIcon);
				break;
			case Server.SERVER_CONSOLE:
			case Server.RAS_CONSOLE:
			case Server.JOBSCHEDULER:
				sessionItem.setImage(robotIcon);
				break;
			default:
				sessionItem.setImage(sessionInfo.getHibernate() ? sleepUserIcon : userIcon);
		}
	}
	
	
	private void addConnectionInTable(Server server, UUID clusterId, UUID infobaseId, IInfoBaseConnectionShort connectionInfo) {
		TableItem connectionItem = new TableItem(tableConnections, SWT.NONE);

		String infobaseName = "";
		if (infobaseId == null && !connectionInfo.getInfoBaseId().equals(emptyUuid)) {
			infobaseId = connectionInfo.getInfoBaseId();
			infobaseName = server.getInfoBaseName(clusterId, infobaseId);
		}
		
		String[] itemText = {
							infobaseName,
							Integer.toString(connectionInfo.getConnId()),
							Integer.toString(connectionInfo.getSessionNumber()),
							connectionInfo.getHost(),
							server.getApplicationName(connectionInfo.getApplication()),
							connectionInfo.getConnectedAt().toString(),
							convertUuidToString(connectionInfo.getInfoBaseConnectionId()),
							convertUuidToString(connectionInfo.getWorkingProcessId())
							};

		connectionItem.setText(itemText);
		connectionItem.setData(SERVER_INFO, 	server);
		connectionItem.setData(CLUSTER_ID, 		clusterId);
		connectionItem.setData(WORKINGPROCESS_ID, connectionInfo.getWorkingProcessId());
		connectionItem.setData(INFOBASE_ID,		infobaseId);
		connectionItem.setData(CONNECTION_ID, 	connectionInfo.getInfoBaseConnectionId());
		connectionItem.setImage(connectionIcon);
		connectionItem.setChecked(false);
	}
	
	
	private void addLocksInTable(Server server, UUID clusterId, UUID infobaseId, IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo, List<IInfoBaseConnectionShort> connections) {
		var lockItem = new TableItem(tableLocks, SWT.NONE);

		var connectionNumber = "";
		var sessionNumber = "";
		var computerName = "";
		var appName = "";
		var hostName = "";
		var hostPort = "";
		var infobaseName = "";

		if (!lockInfo.getSid().equals(emptyUuid)) {
			ISessionInfo session = getSessionInfoFromLockConnectionId(lockInfo, sessionsInfo);
			sessionNumber = Integer.toString(session.getSessionId());
			
			appName = session.getAppId();
			computerName = session.getHost();
//			wpId = session.getWorkingProcessId();
		} else if (!lockInfo.getConnectionId().equals(emptyUuid)) {
			IInfoBaseConnectionShort connection = getConnectionInfoFromLockConnectionId(lockInfo, connections);
			
			if (connection != null) {
				connectionNumber = Integer.toString(connection.getConnId());
				
				appName = connection.getApplication();
				computerName = connection.getHost();
				
				UUID wpId = connection.getWorkingProcessId();
				IWorkingProcessInfo wpInfo = server.getWorkingProcessInfo(clusterId, wpId);
				hostName = wpInfo.getHostName();
				hostPort = Integer.toString(wpInfo.getMainPort());
			
				infobaseName = server.getInfoBaseName(clusterId, connection.getInfoBaseId());
			}

		} else {
		}
					
		String lockDescr = lockInfo.getLockDescr();
		Date lockedAt = lockInfo.getLockedAt();
//		UUID lockedObject = lockInfo.getObject(); // Почему то всегда нули
		
		String[] itemText = {
				lockDescr,
				infobaseName,
				connectionNumber,
				sessionNumber,
				computerName,
				server.getApplicationName(appName),
				hostName,
				hostPort,
				lockedAt.toString()
				};

		lockItem.setText(itemText);
		lockItem.setData(CLUSTER_ID, 		clusterId);
		lockItem.setData(INFOBASE_ID,		infobaseId);
		lockItem.setData("IObjectLockInfo", lockInfo);
		lockItem.setImage(locksIcon);
		lockItem.setChecked(false);
	}
	
	private void addWorkingProcessInTable(Server server, UUID clusterId, IWorkingProcessInfo workingProcess) {
		TableItem connectionItem = new TableItem(tableWorkingProcesses, SWT.NONE);
		
		String isUse;
		switch (workingProcess.getUse()) {
			case 0:
				isUse = "Not used";
				break;
			case 1:
				isUse = "Used";
				break;
			case 2:
				isUse = "Used as a reserve";
				break;
			default:
				isUse = "Not used";
				break;
		}
		String isRunning;
		switch (workingProcess.getRunning()) {
			case 0:
				isRunning = "The process is stopped";
				break;
			case 1:
				isRunning = "The process is running";
				break;
			default:
				isRunning = "The process is stopped";
				break;
		}
		
		String[] itemText = {
				workingProcess.getHostName(),
				Integer.toString(workingProcess.getMainPort()),
				isUse,
				Boolean.toString(workingProcess.isEnable()),
				isRunning,
				workingProcess.getPid(),
				Integer.toString(workingProcess.getMemorySize()),
				Long.toString(workingProcess.getMemoryExcessTime()),
				Integer.toString(workingProcess.getAvailablePerfomance())
				};

		connectionItem.setText(itemText);
		connectionItem.setData(SERVER_INFO, 	server);
		connectionItem.setData(CLUSTER_ID, 		clusterId);
		connectionItem.setData(WORKINGPROCESS_ID, workingProcess.getWorkingProcessId());
		connectionItem.setImage(workingProcessIcon);
		connectionItem.setChecked(false);
	}
	
	private void addWorkingServerInTable(Server server, UUID clusterId, IWorkingServerInfo workingServer) {
		TableItem connectionItem = new TableItem(tableWorkingServers, SWT.NONE);
		
		IPortRangeInfo portRangesInfo = workingServer.getPortRanges().get(0);
		String portRanges = Integer.toString(portRangesInfo.getLowBound()).concat(":").concat(Integer.toString(portRangesInfo.getHighBound()));
	
		String[] itemText = {
				workingServer.getName(),
				workingServer.getHostName(),
				Integer.toString(workingServer.getMainPort()),
				portRanges,
				Integer.toString(workingServer.getInfoBasesPerWorkingProcessLimit()),
				Integer.toString(workingServer.getConnectionsPerWorkingProcessLimit()),
				Integer.toString(workingServer.getClusterMainPort()),
				Boolean.toString(workingServer.isDedicatedManagers()),
				Boolean.toString(workingServer.isMainServer())
				};

		connectionItem.setText(itemText);
		connectionItem.setData(SERVER_INFO, 	server);
		connectionItem.setData(CLUSTER_ID, 		clusterId);
		connectionItem.setData(WORKINGSERVER_ID, workingServer.getWorkingServerId());
		connectionItem.setImage(workingServerIcon);
		connectionItem.setChecked(false);
	}
	
	

	private void addTableColumn(Table table, String text, int width) {
		var newColumn = new TableColumn(table, SWT.NONE);
		newColumn.setText(text);
		newColumn.setWidth(width);
		newColumn.setMoveable(true);
	}
	
	private TreeItemType getTreeItemType(TreeItem item) {
		return (TreeItemType) item.getData("Type");
	}

	private ISessionInfo getSessionInfoFromLockConnectionId(IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo) {
		
		for (ISessionInfo session : sessionsInfo) {
			if (session.getSid().equals(lockInfo.getSid()))
				return session;
		}
		return null;
	}
	

	private IInfoBaseConnectionShort getConnectionInfoFromLockConnectionId(IObjectLockInfo lockInfo, List<IInfoBaseConnectionShort> connections) {
		
		for (IInfoBaseConnectionShort connection : connections) {
			if (connection.getInfoBaseConnectionId().equals(lockInfo.getConnectionId()))
				return connection;
		}
		return null;
	}

	private TreeItem getParentItem(TreeItem currentItem, TreeItemType itemType) {
		
		if (getTreeItemType(currentItem) == itemType)
			return currentItem;
		
		TreeItem parentItem = currentItem.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == itemType)
				return parentItem;
			else 
				parentItem = parentItem.getParentItem();
		}
		throw new IllegalStateException("Error get ParentItem from currentItem.");
//		return null;
	}
	

	private Server getCurrentServerConfig(TreeItem item) {
		
		if (getTreeItemType(item) == TreeItemType.SERVER)
			return (Server) item.getData(SERVER_INFO);
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == TreeItemType.SERVER)
				return (Server) parentItem.getData(SERVER_INFO);
			else 
				parentItem = parentItem.getParentItem();
		}
		throw new IllegalStateException("Error get ServerConfig from item.");
//		return null;
	}
	

	private UUID getCurrentClusterId(TreeItem item) {
		
		if (getTreeItemType(item) == TreeItemType.CLUSTER)
			return (UUID) item.getData(CLUSTER_ID);
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == TreeItemType.CLUSTER)
				return (UUID) parentItem.getData(CLUSTER_ID);
			else 
				parentItem = parentItem.getParentItem();
		}
		return null;
	}
	
	
	private UUID getCurrentWorkingProcessId(TreeItem item) {
		
		if (getTreeItemType(item) == TreeItemType.WORKINGPROCESS)
			return (UUID) item.getData(WORKINGPROCESS_ID);
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == TreeItemType.WORKINGPROCESS)
				return (UUID) parentItem.getData(WORKINGPROCESS_ID);
			else 
				parentItem = parentItem.getParentItem();
		}
		return null;
	}
	
	private UUID getCurrentWorkingServerId(TreeItem item) {
		
		if (getTreeItemType(item) == TreeItemType.WORKINGSERVER)
			return (UUID) item.getData(WORKINGSERVER_ID);
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == TreeItemType.WORKINGSERVER)
				return (UUID) parentItem.getData(WORKINGSERVER_ID);
			else 
				parentItem = parentItem.getParentItem();
		}
		return null;
	}
	
	private UUID getCurrentInfobaseId(TreeItem item) {
		if (getTreeItemType(item) == TreeItemType.INFOBASE)
			return (UUID) item.getData(INFOBASE_ID);
		else 
			return null;
	}
	
	
	private String convertUuidToString(UUID uuid) {
		return uuid.equals(emptyUuid) ? "" : uuid.toString();
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
		return new Image(getParent().getDisplay(), this.getClass().getResourceAsStream("/icons/".concat(name)));
	}
	

	private void connectToAllServers(boolean connectAll) {
		
		TreeItem[] serversItem = serversTree.getItems();
		
		for (TreeItem serverItem : serversItem) {
			Server server = getCurrentServerConfig(serverItem);
			if ((connectAll || server.autoconnect) && !server.isConnected())
				connectServerItem(serverItem);
		}
	}

	private void diconnectFromAllServers() {
		
		TreeItem[] serversItem = serversTree.getItems();
		
		for (TreeItem serverItem : serversItem) {
			disconnectServerItem(serverItem);
		}
	}
	
	private void connectServerItem(TreeItem serverItem) {
		
		// async не работает асинхронно
		serverItem.setImage(serverIconConnecting);
		Display.getDefault().asyncExec(new Runnable() {
			
//			@Override
            public void run() {
			
				Server server = getCurrentServerConfig(serverItem);
				server.connectAndAuthenticate(false);
		
				serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);
				serverItem.setText(new String[] { server.getDescription()});
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
	
	private void selectItemInTree(TreeItem treeItem) {
		
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
//				clearTabs();
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
//				locks 		= serverConfig.getInfoBaseLocks(clusterInfo.getClusterId(), infoBaseInfo.getInfoBaseId());

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
				
				workingProcesses = server.getWorkingProcesses(clusterId);
				workingServers = server.getWorkingServers(clusterId);
				break;
				
			default:
//				clearTabs();
				return;
		}

		tabSessions.setText(String.format("Sessions (%s)", sessions.size()));
		sessions.forEach(session -> {
			addSessionInTable(server, clusterId, infobaseId, session, connections);
		});

		tabConnections.setText(String.format("Connections (%s)", connections.size()));
		connections.forEach(connection -> {
			addConnectionInTable(server, clusterId, infobaseId, connection);
		});

		tabLocks.setText(String.format("Locks (%s)", locks.size()));
		locks.forEach(lock -> {
			addLocksInTable(server, clusterId, infobaseId, lock, sessions, connections);
		});

		tabWorkingProcesses.setText(String.format("Working processes (%s)", workingProcesses.size()));
		workingProcesses.forEach(workingProcess -> {
			addWorkingProcessInTable(server, clusterId, workingProcess);
		});

		tabWorkingServers.setText(String.format("Working servers (%s)", workingServers.size()));
		workingServers.forEach(workingServer -> {
			addWorkingServerInTable(server, clusterId, workingServer);
		});
	}

	private void highlightTreeItem(TreeItem treeItem) {
		if (Objects.isNull(currentTreeItem) || !currentTreeItem.equals(treeItem)) {
			
			if (Objects.nonNull(currentTreeItem) && !currentTreeItem.isDisposed()) {
				var font = new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.NORMAL);
				currentTreeItem.setFont(font);
			}
			
			var font = new Font(getDisplay(), systemFontData.getName(), systemFontData.getHeight(), SWT.BOLD);
			treeItem.setFont(font);
			
			currentTreeItem = treeItem;
		}
	}
	
	private void setContestMenuInTree(TreeItem treeItem) {
		
		switch (getTreeItemType(treeItem)) {
			case SERVER:
				serversTree.setMenu(serverMenu);
				return;
			case CLUSTER:
//			case WORKINGPROCESS_NODE:
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
		tabSessions.setText("Sessions");
		tabConnections.setText("Connections");
		tabLocks.setText("Locks");
		tabWorkingProcesses.setText("Working processes");
		tabWorkingServers.setText("Working servers");
		
		tableSessions.removeAll();
		tableConnections.removeAll();
		tableLocks.removeAll();
		tableWorkingProcesses.removeAll();
		tableWorkingServers.removeAll();
	}

	

}
