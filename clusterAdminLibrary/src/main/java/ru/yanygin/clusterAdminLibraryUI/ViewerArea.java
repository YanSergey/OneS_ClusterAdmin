package ru.yanygin.clusterAdminLibraryUI;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
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
	
	Table tableSessions;
	Table tableConnections;
	Table tableLocks;
//	Menu tableSessionsMenu;
	
	TreeColumn columnServer;

	UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	Logger LOGGER = LoggerFactory.getLogger("ClusterProvider");
	FontData systemFontData = getDisplay().getSystemFont().getFontData()[0];
	
	enum TreeItemType {
		SERVER, CLUSTER, INFOBASE_NODE, INFOBASE, WORKINGPROCESS_NODE, WORKINGPROCESS, WORKINSERVER_NODE, WORKINGSERVER
	}
	
	static final String SERVER_CONFIG = "ServerConfig";
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
		this.clusterProvider.readSavedKnownServers(configPath);

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
		
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Заполнение списка серверов
		clusterProvider.getServers().forEach((serverKey, server) -> {
			TreeItem serverItem = addServerItemInServersTree(server);
		});
		
		// Пропорции областей
		sashForm.setWeights(new int[] {3, 10});
		
		autoconnectToServers();

	}
	
	@Override
	public void addPaintListener(PaintListener listener) { // не работает
		autoconnectToServers();

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
				autoconnectToServers();
			}
		});
	}

	private void initMainMenu(Composite parent, Menu mainMenu) {
		
		if (mainMenu == null) {
			return;
		}
		
		MenuItem toolBarItemFindNewServers = new MenuItem(mainMenu, SWT.NONE);
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

		MenuItem toolBarItemConnectAllServers = new MenuItem(mainMenu, SWT.NONE);
		toolBarItemConnectAllServers.setText("Connect to all servers");		
		toolBarItemConnectAllServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				autoconnectToServers();
				
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
		columnServer.setText("Server/Cluster");
		columnServer.setWidth(300);
		columnServer.addListener(SWT.Selection, sortListener);
//		columnServer.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				serversTree.setSortDirection(serversTree.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
//				serversTree.setSortColumn(columnServer);
//			}
//		});
		
		TreeColumn columnDescription = new TreeColumn(serversTree, SWT.LEFT);
		columnDescription.setText("");
		columnDescription.setWidth(30);
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
				Server serverConfig = getCurrentServerConfig(serverItem);// (Server) serverItem.getData("ServerConfig");

				MenuItem[] menuItems = serverMenu.getItems();

				for (MenuItem menuItem : menuItems) {
					if (menuItem == menuItemConnectServer) // menuItem.equals(menuItemConnectServer)
						menuItem.setEnabled(!serverConfig.isConnected());

					if (menuItem == menuItemDisconnectServer)
						menuItem.setEnabled(serverConfig.isConnected());
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
					LOGGER.error("Error init EditServerDialog for new server", excp);
					return;
				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					newServer = null;
					return;
				}

				clusterProvider.addNewServer(newServer);
				TreeItem newServerItem = addServerItemInServersTree(newServer);

				fillClustersInTree(newServerItem);

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
				Server serverConfig = getCurrentServerConfig(serverItem);
				CreateEditServerDialog connectionDialog;
				try {
					connectionDialog = new CreateEditServerDialog(getParent().getDisplay().getActiveShell(), serverConfig);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init EditServerDialog for server {}", serverConfig.getServerDescription(), excp);
					return;
				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult == 0) {
					// перерисовать в дереве
					serverItem.setText(new String[] { serverConfig.getServerDescription() });
					clusterProvider.saveKnownServers();
					fillClustersInTree(serverItem);
				}

			}

		});
		
		MenuItem menuItemUpdateServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemUpdateServer.setText("Update server info");
		menuItemUpdateServer.setImage(updateIcon);
		menuItemUpdateServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
//				Server config = getServerConfigFromItem(item[0]);
//				UUID clusterId = getClusterInfoFromItem(item[0]);
				
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
				Server config = getCurrentServerConfig(serverItem);
				
				clusterProvider.removeServer(config);

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
				
				Server config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditClusterDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditClusterDialog(getParent().getDisplay().getActiveShell(), config, null);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init EditClusterDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
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
				
				var config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditClusterDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditClusterDialog(getParent().getDisplay().getActiveShell(), config, clusterId);
				} catch (Exception excp) {
					excp.printStackTrace();
					LOGGER.error("Error init EditClusterDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
				}
			}
		});
		
		MenuItem menuItemUpdateCluster = new MenuItem(clusterMenu, SWT.NONE);
		menuItemUpdateCluster.setText("Update cluster info");
		menuItemUpdateCluster.setImage(updateIcon);
		menuItemUpdateCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
//				Server config = getServerConfigFromItem(item[0]);
//				UUID clusterId = getClusterInfoFromItem(item[0]);
				
				fillClustersInTree(getParentItem(item[0], TreeItemType.SERVER)); // здесь надо обновить инфу по одному кластеру
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
				
				Server config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				var messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION |SWT.YES | SWT.NO);
				messageBox.setMessage("Удаление кластера приведет к удалению его настроек и списка зарегистрированных информационных баз. Вы действительно хотите удалить кластер?");
//				messageBox.setMessage("Deleting a cluster will delete its settings and the list of registered information databases. Do you really want to delete the cluster?");
				int rc = messageBox.open();

				if (rc == SWT.YES && config.unregCluster(clusterId))
					item[0].dispose();
				
			}
		});
		
	}
	
	private void initWorkingServerMenu() {
		
		workingServerMenu = new Menu(serversTree);
		
		MenuItem menuItemCreateWorkingServer = new MenuItem(workingServerMenu, SWT.NONE);
		menuItemCreateWorkingServer.setText("Create working server");
		menuItemCreateWorkingServer.setImage(editIcon);
		menuItemCreateWorkingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				var config = getCurrentServerConfig(item[0]);
//				UUID workingServerId = getCurrentWorkingServerId(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditWorkingServerDialog editWorkingServerDialog;
				try {
					editWorkingServerDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), config, clusterId, null);
				} catch (Exception excp) {
					LOGGER.error("Error init WorkingServerDialog for cluster id {}", clusterId, excp);
					return;
				}
				
				int dialogResult = editWorkingServerDialog.open();
				if (dialogResult == 0) {
					var newWorkingServerUuid = editWorkingServerDialog.getNewWorkingServerId();
					if (newWorkingServerUuid != null) {
						IWorkingServerInfo workingServerInfo = config.getWorkingServerInfo(clusterId, newWorkingServerUuid);
						addWorkingServerItemInWSNode(item[0].getParentItem(), workingServerInfo);
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
				
				var config = getCurrentServerConfig(item[0]);
				UUID workingServerId = getCurrentWorkingServerId(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateEditWorkingServerDialog editClusterDialog;
				try {
					editClusterDialog = new CreateEditWorkingServerDialog(getParent().getDisplay().getActiveShell(), config, clusterId, workingServerId);
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
				
				var config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				
				CreateInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), config, clusterId, null);
				} catch (Exception excp) {
					LOGGER.error("Error in CreateInfobaseDialog", excp);
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
					var newInfobaseUuid = infobaseDialog.getNewInfobaseUUID();
					if (newInfobaseUuid != null) {
						IInfoBaseInfoShort infoBaseInfo = config.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
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
				
//				var config = getCurrentServerConfig(item[0]);
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
				
				Server config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID sampleInfobaseId = getCurrentInfobaseId(item[0]);

				CreateInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), config, clusterId, sampleInfobaseId);
				} catch (Exception excp) {
					LOGGER.error("Error in CreateInfobaseDialog", excp);
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
					var newInfobaseUuid = infobaseDialog.getNewInfobaseUUID();
					if (newInfobaseUuid != null) {
						IInfoBaseInfoShort infoBaseInfo = config.getInfoBaseShortInfo(clusterId, newInfobaseUuid);
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
				
				Server config = getCurrentServerConfig(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				EditInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new EditInfobaseDialog(getParent().getDisplay().getActiveShell(), config, clusterId, infobaseId);
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
				
				Server config = getCurrentServerConfig(item[0]);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
//				IInfoBaseInfo infoBaseInfo = server.clusterConnector.getInfoBaseInfo(clusterInfo.getClusterId(), infoBaseInfoShort.getInfoBaseId());
				DropInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new DropInfobaseDialog(getParent().getDisplay().getActiveShell(), config, clusterId, infobaseId);
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
				
				Server config = getCurrentServerConfig(selectItem);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(selectItem);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);

				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				IInfoBaseInfo infoBaseInfo = config.getInfoBaseInfo(clusterId, infobaseId);

				infoBaseInfo.setScheduledJobsDenied(true);
				infoBaseInfo.setSessionsDenied(true);
				infoBaseInfo.setDeniedFrom(null);
				infoBaseInfo.setDeniedTo(null);
				infoBaseInfo.setDeniedMessage("");
				infoBaseInfo.setDeniedParameter("");
				infoBaseInfo.setPermissionCode("");
				
				config.updateInfoBase(clusterId, infoBaseInfo);
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
				
				Server config = getCurrentServerConfig(item[0]);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);
				
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				config.terminateAllSessionsOfInfobase(clusterId, infobaseId, false);
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
				
				Server config = getCurrentServerConfig(item[0]);
//				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
//				IInfoBaseInfoShort infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]);
				
				UUID clusterId = getCurrentClusterId(item[0]);
				UUID infobaseId = getCurrentInfobaseId(item[0]);
				
				config.terminateAllSessionsOfInfobase(clusterId, infobaseId, true);
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
					UUID infobaseId = (UUID) item.getData(SESSION_ID);
					Server config = (Server) item.getData(SERVER_CONFIG);
					
					config.terminateSession(clusterId, infobaseId);
					
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
					
					Server config = (Server) item.getData(SERVER_CONFIG);
					UUID clusterId = (UUID) item.getData(CLUSTER_ID);
					UUID pricessId = (UUID) item.getData(WORKINGPROCESS_ID);
					UUID connectionId = (UUID) item.getData(SESSION_ID);
					
					config.disconnectConnection(clusterId, pricessId, connectionId);
					
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

		Server serverConfig = getCurrentServerConfig(serverItem);
		
//		// смена иконки сервера на вкл/выкл
//		serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);
		
		if (!serverConfig.isConnected()) {
			return;
		}
		
		List<IClusterInfo> clusters = serverConfig.getClusters();
		clusters.forEach(clusterInfo -> {
			TreeItem clusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
			
			// Пока что удалить все из списка, может лучше добавить недостающие?
			// Тут же у нас пусто?
//			TreeItem[] ibItems = clusterItem.getItems();
//			for (TreeItem treeItem : ibItems) {
//				treeItem.dispose();
//			}
			
			// Заполнение списка инфобаз
			fillInfobasesOfCluster(clusterItem, serverConfig);
			fillWorkingProcessesInCluster(clusterItem, serverConfig);
			fillWorkingServersInCluster(clusterItem, serverConfig);

		});
		
		// Разворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(!clusters.isEmpty());
	}
	
	private void updateClustersInTree(TreeItem serverItem) {

		Server serverConfig = getCurrentServerConfig(serverItem);
		TreeItem[] clustersItems = serverItem.getItems();
		
		// у отключенного кластера удаляем все дочерние элементы
		if (!serverConfig.isConnected()) {
			for (TreeItem clusterItem : clustersItems) {
				disposeTreeItemWithChildren(clusterItem);
			}
			return;
		}
		
		List<IClusterInfo> clusters = serverConfig.getClusters();
		
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
				
				// Заполнение списка инфобаз и рабочих процессов
				fillInfobasesOfCluster(clusterItem, serverConfig);
				fillWorkingProcessesInCluster(clusterItem, serverConfig);
			}
			
		});
		
		
		// Разворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(!clusters.isEmpty());
	}
	
	private void fillInfobasesOfCluster(TreeItem clusterItem, Server serverConfig) {
		
		UUID clusterId = getCurrentClusterId(clusterItem);
		List<IInfoBaseInfoShort> infoBases = serverConfig.getInfoBasesShort(clusterId); // краткая инфа - ID, имя, описание
		
		TreeItem infobaseNode = addInfobaseNodeInServersTree(clusterItem, String.format("Infobases (%s)", infoBases.size()));
		if (!infoBases.isEmpty()) {
			
			infoBases.forEach(infoBaseInfo-> {
				addInfobaseItemInInfobaseNode(infobaseNode, infoBaseInfo);
			});
			
			clusterItem.setExpanded(true);
			infobaseNode.setExpanded(true);
			
		}

	}
	
	private void fillWorkingProcessesInCluster(TreeItem clusterItem, Server serverConfig) {

		UUID clusterId = getCurrentClusterId(clusterItem);
		List<IWorkingProcessInfo> wProcesses = serverConfig.getWorkingProcesses(clusterId);
		
		if (!wProcesses.isEmpty()) {
			TreeItem workingProcessesNode = addWorkingProcessNodeInClusterItem(clusterItem, String.format("Working processes (%s)", wProcesses.size()));
			
			wProcesses.forEach(wProcess-> {
				addWorkingProcessItemInWPNode(workingProcessesNode, wProcess);
			});
			
			clusterItem.setExpanded(true);
			workingProcessesNode.setExpanded(true);
			
		}

	}
	
	private void fillWorkingServersInCluster(TreeItem clusterItem, Server serverConfig) {

		UUID clusterId = getCurrentClusterId(clusterItem);
		List<IWorkingServerInfo> wServers = serverConfig.getWorkingServers(clusterId);
		
		if (!wServers.isEmpty()) {
			TreeItem workingServersNode = addWorkingServerNodeInClusterItem(clusterItem, String.format("Working servers (%s)", wServers.size()));
			
			wServers.forEach(wServer-> {
				addWorkingServerItemInWSNode(workingServersNode, wServer);
			});
			
			clusterItem.setExpanded(true);
			workingServersNode.setExpanded(true);
			
		}

	}	

	private TreeItem addServerItemInServersTree(Server config) {
		
//		TreeItem item = new ServerTreeItem(serversTree, SWT.NONE, config);
		
		var item = new TreeItem(serversTree, SWT.NONE);
		
		item.setText(new String[] { config.getServerDescription()});
		item.setData("Type", TreeItemType.SERVER);
		item.setData("ServerKey", config.getServerKey());
		item.setData(SERVER_CONFIG, config);
		
		if (config.isConnected()) {
			item.setImage(serverIconUp);
		} else {
			item.setImage(serverIcon);
		}
		
		return item;
	}
	
	
	private TreeItem addClusterItemInServersTree(TreeItem serverItem, IClusterInfo clusterInfo) {
		var item = new TreeItem(serverItem, SWT.NONE);
		
		var itemTitle = String.format("%s (%s)", clusterInfo.getName(), clusterInfo.getMainPort());
		
		item.setText(new String[] { itemTitle});
		item.setData("Type", TreeItemType.CLUSTER);
//		item.setData("ClusterName", clusterInfo.getName()); // Зачем?
		item.setData(CLUSTER_ID, clusterInfo.getClusterId());
		item.setImage(clusterIcon);
		
		return item;
	}
	
	
	private TreeItem addInfobaseNodeInServersTree(TreeItem clusterItem, String title) {
		var item = new TreeItem(clusterItem, SWT.NONE);
		
		item.setText(new String[] { title});
		item.setData("Type", TreeItemType.INFOBASE_NODE);
		item.setImage(infobasesIcon);
		item.setChecked(false);
		
		return item;
	}
	
	
	private void addInfobaseItemInInfobaseNode(TreeItem infobaseNode, IInfoBaseInfoShort ibInfo) {
		var item = new TreeItem(infobaseNode, SWT.NONE);
		
		item.setText(new String[] { ibInfo.getName()});
		item.setData("Type", TreeItemType.INFOBASE);
//		item.setData("BaseName", ibInfo.getName()); // Зачем?
		item.setData(INFOBASE_ID, ibInfo.getInfoBaseId());
		item.setImage(0, infobaseIcon);
		item.setChecked(false);
		
//		item.setImage(1, ibInfo.isSessionsDenied() ? lockUsersIcon : null);
		
	}
	
	
	private TreeItem addWorkingProcessNodeInClusterItem(TreeItem clusterItem, String title) {
		var item = new TreeItem(clusterItem, SWT.NONE);
		
		item.setText(new String[] { title});
		item.setData("Type", TreeItemType.WORKINGPROCESS_NODE);
		item.setImage(workingProcessesIcon);
		item.setChecked(false);
		
		return item;
	}
	
	
	private void addWorkingProcessItemInWPNode(TreeItem wpNodeItem, IWorkingProcessInfo wpInfo) {
		var item = new TreeItem(wpNodeItem, SWT.NONE);
		
		var itemTitle = String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort());
		
		item.setText(new String[] { itemTitle});
		item.setData("Type", TreeItemType.WORKINGPROCESS);
		item.setData(WORKINGPROCESS_ID, wpInfo.getWorkingProcessId());
		item.setImage(workingProcessIcon);
		item.setChecked(false);
	}
	
	private TreeItem addWorkingServerNodeInClusterItem(TreeItem clusterItem, String title) {
		var item = new TreeItem(clusterItem, SWT.NONE);
		
		item.setText(new String[] { title});
		item.setData("Type", TreeItemType.WORKINGPROCESS_NODE);
		item.setImage(workingServerIcon);
		item.setChecked(false);
		
		return item;
	}
	
	
	private void addWorkingServerItemInWSNode(TreeItem wsNodeItem, IWorkingServerInfo wpInfo) {
		var item = new TreeItem(wsNodeItem, SWT.NONE);
		
		var itemTitle = String.format("%s (%s)", wpInfo.getHostName(), wpInfo.getMainPort());
		
		item.setText(new String[] { itemTitle});
		item.setData("Type", TreeItemType.WORKINGSERVER);
		item.setData(WORKINGSERVER_ID, wpInfo.getWorkingServerId());
		item.setImage(workingServerIcon);
		item.setChecked(false);
	}
	

	private void addSessionInTable(Server serverConfig, UUID clusterId, UUID infobaseId, ISessionInfo sessionInfo, List<IInfoBaseConnectionShort> connections) {
		TableItem sessionItem = new TableItem(tableSessions, SWT.NONE);

//		String infobaseName = "";
//		if (infoBaseInfo == null) {
//			infobaseName = serverConfig.getInfoBaseName(clusterId, sessionInfo.getInfoBaseId());
//		} else {
//			infobaseName = infoBaseInfo.getName();
//		}
//		UUID infobaseId = infobaseId == null ? sessionInfo.getInfoBaseId() : infobaseId;
		
		if (infobaseId == null)
			infobaseId = sessionInfo.getInfoBaseId();
		
		String infobaseName = serverConfig.getInfoBaseName(clusterId, infobaseId);
		
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
			IWorkingProcessInfo wpInfo = serverConfig.getWorkingProcessInfo(clusterId, sessionInfo.getWorkingProcessId());
			
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
							serverConfig.getApplicationName(sessionInfo.getAppId()),
							wpHostName,
							wpMainPort,
							wpPid,
							license,
							Boolean.toString(sessionInfo.getHibernate()), // уснул
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
		sessionItem.setData(SERVER_CONFIG, 	serverConfig);
		sessionItem.setData(CLUSTER_ID, 	clusterId);
//		sessionItem.setData("InfoBaseInfoShort", infoBaseInfo);
		sessionItem.setData(SESSION_ID, 	sessionInfo.getSid()); //sessionInfo.getSessionId()
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
	
	
	private void addConnectionInTable(Server serverConfig, UUID clusterId, UUID infobaseId, IInfoBaseConnectionShort connectionInfo) {
		TableItem connectionItem = new TableItem(tableConnections, SWT.NONE);

		String infobaseName = "";
//		if (infoBaseInfo == null) {
//			infobaseName = serverConfig.getInfoBaseName(clusterInfo.getClusterId(), connectionInfo.getInfoBaseId());
//		} else {
//			infobaseName = infoBaseInfo.getName();
//		}
		
		if (infobaseId == null && !connectionInfo.getInfoBaseId().equals(emptyUuid)) {
			infobaseId = connectionInfo.getInfoBaseId();
			infobaseName = serverConfig.getInfoBaseName(clusterId, infobaseId);
		}
		
		String[] itemText = {
							infobaseName,
							Integer.toString(connectionInfo.getConnId()),
							Integer.toString(connectionInfo.getSessionNumber()),
							connectionInfo.getHost(),
							serverConfig.getApplicationName(connectionInfo.getApplication()),
							connectionInfo.getConnectedAt().toString(),
							convertUuidToString(connectionInfo.getInfoBaseConnectionId()),
							convertUuidToString(connectionInfo.getWorkingProcessId())
							};

		connectionItem.setText(itemText);
		connectionItem.setData(SERVER_CONFIG, 	serverConfig);
		connectionItem.setData(CLUSTER_ID, 		clusterId);
		connectionItem.setData(WORKINGPROCESS_ID, connectionInfo.getWorkingProcessId());
		connectionItem.setData(INFOBASE_ID,		infobaseId);
		connectionItem.setData(CONNECTION_ID, 	connectionInfo.getInfoBaseConnectionId());
		connectionItem.setImage(connectionIcon);
		connectionItem.setChecked(false);
	}
	
	
	private void addLocksInTable(Server serverConfig, UUID clusterId, UUID infobaseId, IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo, List<IInfoBaseConnectionShort> connections) {
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
				IWorkingProcessInfo wpInfo = serverConfig.getWorkingProcessInfo(clusterId, wpId);
				hostName = wpInfo.getHostName();
				hostPort = Integer.toString(wpInfo.getMainPort());
			
				infobaseName = serverConfig.getInfoBaseName(clusterId, connection.getInfoBaseId());
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
							serverConfig.getApplicationName(appName),
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
//		ISessionInfo sessionFound = null;
		
		for (ISessionInfo session : sessionsInfo) {
			if (session.getSid().equals(lockInfo.getSid()))
				return session;
		}
		return null;
	}
	

	private IInfoBaseConnectionShort getConnectionInfoFromLockConnectionId(IObjectLockInfo lockInfo, List<IInfoBaseConnectionShort> connections) {
//		IInfoBaseConnectionShort connectionFound = null;
		
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
			return (Server) item.getData(SERVER_CONFIG);
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (getTreeItemType(parentItem) == TreeItemType.SERVER)
				return (Server) parentItem.getData(SERVER_CONFIG);
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
	

	private void autoconnectToServers() {
		
//		clusterProvider.connectToServers(); // кажется надо переделать соединение внутрь цикла
		TreeItem[] serversItem = serversTree.getItems();
		
		for (TreeItem serverItem : serversItem) {
			Server config = getCurrentServerConfig(serverItem);
			if (config.autoconnect)
				connectServerItem(serverItem);
		}
	}
	

	private void connectServerItem(TreeItem serverItem) {
		
		// async не работает асинхронно
		serverItem.setImage(serverIconConnecting);
		Display.getDefault().asyncExec(new Runnable() {
			
//			@Override
            public void run() {
			
				Server config = getCurrentServerConfig(serverItem);
				config.connectAndAuthenticate(false);
		
				serverItem.setImage(config.isConnected() ? serverIconUp : serverIconDown);
				serverItem.setText(new String[] { config.getServerDescription()});
				fillClustersInTree(serverItem);
			}
		});
	}
	
	private void disconnectServerItem(TreeItem serverItem) {
		Server config = getCurrentServerConfig(serverItem);
		config.disconnectFromAgent();
		serverItem.setImage(serverIconDown);
		
		TreeItem[] clusterItems = serverItem.getItems();
		for (TreeItem clusterItem : clusterItems) {
			disposeTreeItemWithChildren(clusterItem);
		}
	}
	
	private void selectItemInTree(TreeItem treeItem) {
		
		highlightTreeItem(treeItem);
		
		Server serverConfig;
		UUID clusterId;
		UUID infobaseId;
		UUID workingProcessId;

		List<ISessionInfo> sessions;
		List<IInfoBaseConnectionShort> connections;
		List<IObjectLockInfo> locks;
		
		clearTabs();
		switch (getTreeItemType(treeItem)) {
			case SERVER:
//				clearTabs();
				return;
				
			case CLUSTER:
			case INFOBASE_NODE:
			case WORKINGPROCESS_NODE:

				serverConfig = getCurrentServerConfig(treeItem);
				clusterId = getCurrentClusterId(treeItem);
				infobaseId = null;

				sessions = serverConfig.getSessions(clusterId);
				connections = serverConfig.getConnectionsShort(clusterId);
				locks = serverConfig.getLocks(clusterId);
				break;
				
			case WORKINGPROCESS:

				serverConfig = getCurrentServerConfig(treeItem);
				clusterId = getCurrentClusterId(treeItem);
				infobaseId = null;
				workingProcessId = getCurrentWorkingProcessId(treeItem);

				sessions = serverConfig.getWorkingProcessSessions(clusterId, workingProcessId);
				connections = serverConfig.getWorkingProcessConnectionsShort(clusterId, workingProcessId);
				locks = new ArrayList<>();
//				locks 		= serverConfig.getInfoBaseLocks(clusterInfo.getClusterId(), infoBaseInfo.getInfoBaseId());
				break;
				
			case INFOBASE:
				serverConfig = getCurrentServerConfig(treeItem);
				clusterId = getCurrentClusterId(treeItem);
				infobaseId = getCurrentInfobaseId(treeItem);

				sessions = serverConfig.getInfoBaseSessions(clusterId, infobaseId);
				connections = serverConfig.getInfoBaseConnectionsShort(clusterId, infobaseId);
				locks = serverConfig.getInfoBaseLocks(clusterId, infobaseId);
				break;
				
			default:
//				clearTabs();
				return;
		}

//		tableSessions.removeAll();
		tabSessions.setText(String.format("Sessions (%s)", sessions.size()));
		sessions.forEach(session -> {
			addSessionInTable(serverConfig, clusterId, infobaseId, session, connections);
		});

//		tableConnections.removeAll();
		tabConnections.setText(String.format("Connections (%s)", connections.size()));
		connections.forEach(connection -> {
			addConnectionInTable(serverConfig, clusterId, infobaseId, connection);
		});

//		tableLocks.removeAll();
		tabLocks.setText(String.format("Locks (%s)", locks.size()));
		locks.forEach(lock -> {
			addLocksInTable(serverConfig, clusterId, infobaseId, lock, sessions, connections);
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
		
		tableSessions.removeAll();
		tableConnections.removeAll();
		tableLocks.removeAll();
	}

	

}
