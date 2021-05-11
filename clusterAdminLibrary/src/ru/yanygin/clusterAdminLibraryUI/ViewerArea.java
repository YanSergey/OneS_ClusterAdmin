package ru.yanygin.clusterAdminLibraryUI;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewerArea extends Composite {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;
	Image infobaseIcon;
	Image infobasesIcon;
	Image clusterIcon;
	Image userIcon;
	Image connectionIcon;
	Image workingProcessesIcon;
	Image workingProcessIcon;
	Image connectActionIcon;
	Image disconnectActionIcon;
	Image editIcon;
	Image addIcon;
	Image deleteIcon;
	Image lockUsersIcon;
	
	Tree serversTree;
	Menu serverMenu;
	MenuItem menuItemConnectServer;
	MenuItem menuItemDisconnectServer;
	Menu clusterMenu;
	Menu infobaseMenu;
	
	Table tableSessions;
	Table tableConnections;
	Table tableLocks;
//	Menu tableSessionsMenu;
//	Menu tableSessionsMenu;
	
	TreeColumn columnServer;

	UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	ClusterProvider clusterProvider;

	//@Slf4j
	public ViewerArea(Composite parent, int style, Menu menu, ToolBar toolBar, ClusterProvider clusterProvider) {
		super(parent, style);
		
		this.clusterProvider = clusterProvider;
//		String configPath = "C:\\git\\OneS_ClusterAdmin\\config.json";
		String configPath = ".\\config.json";
		this.clusterProvider.readSavedKnownServers(configPath);

		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		initIcon();
		
//		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT); // ƒл€ отладки
//		toolBar.setBounds(0, 0, 500, 23); // ƒл€ отладки
		
//		initToolbar(parent, toolBar, clusterProvider);
		initMainMenu(sashForm, menu, clusterProvider);
		
		initServersTree(sashForm);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		initSessionTable(tabFolder);//sashForm);
		initConnectionsTable(tabFolder);//sashForm);
		initLocksTable(tabFolder);//sashForm);
		
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// «аполнение списка серверов
		clusterProvider.getServers().forEach((serverKey, server) -> {
			TreeItem serverItem = addServerItemInServersTree(server);
//			fillClustersInTree(serverItem); // переместить заполнение дерева из конструктора в метод открыти€ формы
		});
		
		// ѕропорции областей
		sashForm.setWeights(new int[] {3, 10});

	}
	
	@Override
	public void addPaintListener(PaintListener listener) { // не работает
		connectToAllServers();

		super.addPaintListener(listener);
	}
	
//	public void open() {
//		connectToAllServers();
//	}
	
	private void initIcon() {
		
		serverIcon = getImage(getParent().getDisplay(), 		"/icons/server_24.png");
		serverIconUp = getImage(getParent().getDisplay(), 		"/icons/server_up_24.png");
		serverIconDown = getImage(getParent().getDisplay(), 	"/icons/server_down_24.png");
		infobaseIcon = getImage(getParent().getDisplay(), 		"/icons/infobase_24.png");
		infobasesIcon = getImage(getParent().getDisplay(), 		"/icons/infobases_24.png");
		clusterIcon = getImage(getParent().getDisplay(), 		"/icons/cluster_24.png");
		userIcon = getImage(getParent().getDisplay(), 			"/icons/user.png");
		connectionIcon = getImage(getParent().getDisplay(), 	"/icons/connection.png");

		workingProcessesIcon = getImage(getParent().getDisplay(), 	"/icons/wps.png");
		workingProcessIcon = getImage(getParent().getDisplay(), 	"/icons/wp.png");

		connectActionIcon = getImage(getParent().getDisplay(), 		"/icons/connect_action_24.png");
		disconnectActionIcon = getImage(getParent().getDisplay(), 	"/icons/disconnect_action_24.png");
		
		editIcon = getImage(getParent().getDisplay(), 			"/icons/edit_16.png");
		addIcon = getImage(getParent().getDisplay(), 			"/icons/add_16.png");
		deleteIcon = getImage(getParent().getDisplay(), 		"/icons/delete_16.png");
		lockUsersIcon = getImage(getParent().getDisplay(), 		"/icons/lock_users_16.png");

	}
	
	private void initToolbar(Composite parent, ToolBar toolBar, ClusterProvider clusterProvider) {
//		ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolBar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolBar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (handCursor.isDisposed() == false) {
					handCursor.dispose();
				}
			}
		});
		
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
				connectToAllServers();
			}
		});
	}

	private void initMainMenu(Composite parent, Menu mainMenu, ClusterProvider clusterProvider) {
//		ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);
		
		if (mainMenu == null) {
			return;
//			Decorations d = new Decorations(parent, SWT.BORDER);		
//			
//			mainMenu = new Menu(d, SWT.BAR);
//			d.setMenu(mainMenu);
////			mainMenu.setLocation(parent.getLocation());
//			mainMenu.setVisible(true);
			
//			mainMenu = new Menu(parent);
//			parent.setMenu(mainMenu);
////			mainMenu.setLocation(parent.getLocation());
//			mainMenu.setVisible(true);
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
				
				connectToAllServers();
				
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
				int button = e.button; //left = 1,  right = 3
			}
		});
		
		serversTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				// кажетс€ нужно сделать, что бы была реакци€ только на левый клик мышью
				// по правому клику только устанавливать нужное меню

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
//				TreeItem serverItem = item[0];
				TreeItem treeItem = (TreeItem) event.item;
				
				selectItemInTreeHandler(treeItem);
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
		
		TreeColumn columnAutoconnect = new TreeColumn(serversTree, SWT.CENTER);
		columnAutoconnect.setText("lock");
		columnAutoconnect.setWidth(30);
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
				Server serverConfig = (Server) serverItem.getData("ServerConfig");

				MenuItem[] menuItems = serverMenu.getItems();

				for (MenuItem menuItem : menuItems) {
					if (menuItem == menuItemConnectServer)
						menuItem.setEnabled(!serverConfig.isConnected());

					if (menuItem == menuItemDisconnectServer)
						menuItem.setEnabled(serverConfig.isConnected());
				}

			}
		});
		
		serverMenu.addListener(SWT.Selection, new Listener()
	    {
	        @Override
	        public void handleEvent(Event e)
	        {
	            System.out.println("click");
	        }
	    });
		serverMenu.addListener(SWT.MenuDetect, new Listener()
	    {
	        @Override
	        public void handleEvent(Event e)
	        {
	            System.out.println("menu");
	        }
	    });
		
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
		menuItemDisconnectServer.setText("Disconnect of Server"); // Disconnect of Server??? - проверить написание
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

		MenuItem menuItemAddNewServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemAddNewServer.setText("Add Server");
		menuItemAddNewServer.setImage(addIcon);
		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				Server newServer = clusterProvider.CreateNewServer();
				EditServerDialog connectionDialog;
				try {
					connectionDialog = new EditServerDialog(getParent().getDisplay().getActiveShell(), newServer);
				} catch (Exception excp) {
					excp.printStackTrace();
					return;
				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					newServer = null;
					return;
				}

				clusterProvider.addNewServerInList(newServer);
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
				Server serverConfig = (Server) serverItem.getData("ServerConfig");
				EditServerDialog connectionDialog;
				try {
					connectionDialog = new EditServerDialog(getParent().getDisplay().getActiveShell(), serverConfig);
				} catch (Exception excp) {
					excp.printStackTrace();
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

		
		MenuItem menuItemDeleteServer = new MenuItem(serverMenu, SWT.NONE);
		menuItemDeleteServer.setText("Remove Server");
		menuItemDeleteServer.setImage(deleteIcon);
		menuItemDeleteServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = (Server) item[0].getData("ServerConfig");
				
				clusterProvider.removeServerInList(server);
				
//				item[0].dispose();

				disposeTreeItemWithChildren(item[0]);
			}
		});
		
		// Cluster Menu
		clusterMenu = new Menu(serversTree);
		
		MenuItem menuItemEditCluster = new MenuItem(clusterMenu, SWT.NONE);
		menuItemEditCluster.setText("Edit Cluster");
		menuItemEditCluster.setImage(editIcon);
		menuItemEditCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getServerConfigFromItem(item[0]); // (Server) item[0].getParentItem().getData("ServerConfig");
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);// (IClusterInfo) item[0].getData("ClusterInfo");
				
				clusterInfo = server.getClusterInfo(clusterInfo.getClusterId());
				
				EditClusterDialog editClusterDialog;
				try {
					editClusterDialog = new EditClusterDialog(getParent().getDisplay().getActiveShell(), server, clusterInfo);
				} catch (Exception excp) {
					excp.printStackTrace();
					return;
				}
				
				int dialogResult = editClusterDialog.open();
				if (dialogResult == 0) {
				}
			}
		});
		
		// Database Menu
		infobaseMenu = new Menu(serversTree);

		MenuItem menuItemNewInfobase = new MenuItem(clusterMenu, SWT.NONE);
		menuItemNewInfobase.setText("New Infobase");
		menuItemNewInfobase.setImage(addIcon);
		menuItemNewInfobase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server server = getServerConfigFromItem(item[0]);// (Server) item[0].getParentItem().getData("ServerConfig");
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);// (IClusterInfo) item[0].getData("ClusterInfo");

				CreateInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new CreateInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterInfo);
				} catch (Exception excp) {
					excp.printStackTrace();
					return;
				}
				
				int dialogResult = infobaseDialog.open();
				if (dialogResult == 0) {
					IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterInfo.getClusterId(), infobaseDialog.getNewInfobaseUUID());
					addInfobaseItemInInfobaseNode(item[0], infoBaseInfo);
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
				
				Server server = getServerConfigFromItem(item[0]);// (Server) item[0].getParentItem().getParentItem().getParentItem().getData("ServerConfig");
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);// (IClusterInfo) item[0].getParentItem().getParentItem().getData("ClusterInfo");
				IInfoBaseInfo infoBaseInfoTemp = getInfoBaseInfoFromItem(item[0]); // (IInfoBaseInfo) item[0].getData("InfoBaseInfo");
				
				IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterInfo.getClusterId(), infoBaseInfoTemp.getInfoBaseId());
				EditInfobaseDialog infobaseDialog;
				try {
					// TODO может лучше передавать InfoBaseId, а там получать infoBaseInfo
					infobaseDialog = new EditInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterInfo, infoBaseInfo);
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
				
				Server server = getServerConfigFromItem(item[0]);// (Server) item[0].getParentItem().getParentItem().getParentItem().getData("ServerConfig");
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);// (IClusterInfo) item[0].getParentItem().getParentItem().getData("ClusterInfo");
				IInfoBaseInfo infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]); //(IInfoBaseInfo) item[0].getData("InfoBaseInfo");
				
//				IInfoBaseInfo infoBaseInfo = server.clusterConnector.getInfoBaseInfo(clusterInfo.getClusterId(), infoBaseInfoShort.getInfoBaseId());
				DropInfobaseDialog infobaseDialog;
				try {
					infobaseDialog = new DropInfobaseDialog(getParent().getDisplay().getActiveShell(), server, clusterInfo, infoBaseInfoShort.getInfoBaseId());
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
		
		// —оздание вложенных подпунктов меню
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
				
				Server server = getServerConfigFromItem(selectItem);
				IClusterInfo clusterInfo = getClusterInfoFromItem(selectItem);
				IInfoBaseInfo infoBaseInfoTemp = getInfoBaseInfoFromItem(item[0]); //(IInfoBaseInfoShort) selectItem.getData("InfoBaseInfoShort");
				IInfoBaseInfo infoBaseInfo = server.getInfoBaseInfo(clusterInfo.getClusterId(), infoBaseInfoTemp.getInfoBaseId());
				
				infoBaseInfo.setScheduledJobsDenied(true);
				infoBaseInfo.setSessionsDenied(true);
				infoBaseInfo.setDeniedFrom(null);
				infoBaseInfo.setDeniedTo(null);
				infoBaseInfo.setDeniedMessage("");
				infoBaseInfo.setDeniedParameter("");
				infoBaseInfo.setPermissionCode("");
				
				server.updateInfoBase(clusterInfo.getClusterId(), infoBaseInfo);
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
				
				Server server = getServerConfigFromItem(item[0]);
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
				IInfoBaseInfo infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]); //(IInfoBaseInfoShort) item[0].getData("InfoBaseInfoShort");
				
				server.terminateAllSessionsOfInfobase(clusterInfo.getClusterId(), infoBaseInfoShort.getInfoBaseId(), false);
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
				
				Server server = getServerConfigFromItem(item[0]);
				IClusterInfo clusterInfo = getClusterInfoFromItem(item[0]);
				IInfoBaseInfo infoBaseInfoShort = getInfoBaseInfoFromItem(item[0]); //(IInfoBaseInfoShort) item[0].getData("InfoBaseInfoShort");
				
				server.terminateAllSessionsOfInfobase(clusterInfo.getClusterId(), infoBaseInfoShort.getInfoBaseId(), true);
			}
		});
		
		// set active menu
		serversTree.setMenu(serverMenu);
	}
	
	private void initSessionTable(TabFolder tabFolder) {

		TabItem tabSessions = new TabItem(tabFolder, SWT.NONE);
		tabSessions.setText("Sessions");

		tableSessions = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabSessions.setControl(tableSessions);
		tableSessions.setHeaderVisible(true);
		tableSessions.setLinesVisible(true);
		
		initSessionsTableContextMenu();
		
		addTableColumn(tableSessions, "Username", 		100);
		addTableColumn(tableSessions, "Infobase", 		100);
		addTableColumn(tableSessions, "Session N", 		80);
		addTableColumn(tableSessions, "Connection N", 	80);
		addTableColumn(tableSessions, "Started At", 	120);
		addTableColumn(tableSessions, "Last active at", 120);
		addTableColumn(tableSessions, "Computer", 		100);
		addTableColumn(tableSessions, "Application", 	100);
		
		addTableColumn(tableSessions, "rphost ID", 		100);
				
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
					
					IClusterInfo clusterInfo = (IClusterInfo) item.getData("ClusterInfo");
					ISessionInfo sessionInfo = (ISessionInfo) item.getData("SessionInfo");
					Server server = (Server) item.getData("ServerConfig");
					server.terminateSession(clusterInfo.getClusterId(), sessionInfo.getSid(), "Your session was interrupted by the administrator");
				}
				
			}
		});
	}
	
	private void initConnectionsTable(TabFolder tabFolder) {

		TabItem tabConnections = new TabItem(tabFolder, SWT.NONE);
		tabConnections.setText("Connections");
		
		tableConnections = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // | SWT.CHECK
		tabConnections.setControl(tableConnections);
		tableConnections.setHeaderVisible(true);
		tableConnections.setLinesVisible(true);
		
		initConnectionsTableContextMenu();
		
		addTableColumn(tableConnections, "Application", 	100);
		addTableColumn(tableConnections, "ConnectionID", 	100);
		addTableColumn(tableConnections, "Hostname", 		100);
		addTableColumn(tableConnections, "Infobase ID", 	100);
		addTableColumn(tableConnections, "Infobase connection ID", 100);
		addTableColumn(tableConnections, "Connected at", 	100);
		addTableColumn(tableConnections, "SessionNumber", 	100);
		addTableColumn(tableConnections, "rphost ID", 		100);
		
	}
	
	private void initConnectionsTableContextMenu() {
		// ѕока не пон€тен состав меню
	}
	
	private void initLocksTable(TabFolder tabFolder) {

		TabItem tabLocks = new TabItem(tabFolder, SWT.NONE);
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
		addTableColumn(tableLocks, "Locked Object", 100);
		
	}
	
	private void initLocksTableContextMenu() {
		// ѕока не пон€тен состав меню
	}

	private void fillServersList() {
		// TODO Auto-generated method stub
		
	}
	
	private void fillClustersInTree(TreeItem serverItem) {
		
		// ѕока что удалить все кластера из списка, может лучше добавить недостающие?
		TreeItem[] clustersItems = serverItem.getItems();
		for (TreeItem clusterItem : clustersItems) {
//			clusterItem.dispose();
			disposeTreeItemWithChildren(clusterItem);
		}

		Server server = (Server) serverItem.getData("ServerConfig");
		
//		// смена иконки сервера на вкл/выкл
//		serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);
		
		if (!server.isConnected()) {
			return;
		}
		
		List<IClusterInfo> clusters = server.getClusters();
		clusters.forEach(clusterInfo -> {
			TreeItem clusterItem = addClusterItemInServersTree(serverItem, clusterInfo);
			
			// ѕока что удалить все из списка, может лучше добавить недостающие?
			// “ут же у нас пусто?
//			TreeItem[] ibItems = clusterItem.getItems();
//			for (TreeItem treeItem : ibItems) {
//				treeItem.dispose();
//			}
			
			// «аполнение списка инфобаз
			fillInfobasesOfCluster(clusterItem, server);
			fillWorkingProcessesInCluster(clusterItem, server);

		});
		
		// –азворачиваем дерево, если список кластеров не пустой
		serverItem.setExpanded(!clusters.isEmpty());
	}
	
	private void fillInfobasesOfCluster(TreeItem clusterItem, Server server) {
		
		//debug
//		List<IInfoBaseInfo> infoBases = serverConfig.getInfoBases();
		//debug
		IClusterInfo clusterInfo = (IClusterInfo) clusterItem.getData("ClusterInfo");
//		List<IInfoBaseInfoShort> infoBases = server.getInfoBasesShort(clusterInfo.getClusterId()); // кратка€ инфа - ID, им€, описание
		List<IInfoBaseInfo> infoBases = server.getInfoBases(clusterInfo.getClusterId()); // краткой инфы недостаточно
		
		if (infoBases.size() > 0) {
			TreeItem infobaseNode = addInfobaseNodeInServersTree(clusterItem, "Infobases ("+infoBases.size()+")");
			
			infoBases.forEach(infoBaseInfo-> {
				
				//debug
	//			IInfoBaseInfo infoBasesInfo = serverConfig.clusterConnector.getInfoBaseInfo(serverConfig.clusterID, infoBaseInfo.getInfoBaseId());
	//			IInfoBaseInfoShort infoBasesShortInfo = serverConfig.clusterConnector.getInfoBaseShortInfo(serverConfig.clusterID, infoBaseInfo.getInfoBaseId());
				//debug
				
				addInfobaseItemInInfobaseNode(infobaseNode, infoBaseInfo);
			});
			clusterItem.setExpanded(true);
			infobaseNode.setExpanded(true);
			
		}

	}
	
	private void fillWorkingProcessesInCluster(TreeItem clusterItem, Server server) {

		IClusterInfo clusterInfo = (IClusterInfo) clusterItem.getData("ClusterInfo");
		List<IWorkingProcessInfo> wProcesses = server.getWorkingProcesses(clusterInfo.getClusterId());
		
		if (wProcesses.size() > 0) {
			TreeItem workingProcessesNode = addWorkingProcessNodeInClusterItem(clusterItem, "Working processes ("+wProcesses.size()+")");
			
			wProcesses.forEach(wProcess-> {
								
				addWorkingProcessItemInWPNode(workingProcessesNode, wProcess);
			});
			clusterItem.setExpanded(true);
			workingProcessesNode.setExpanded(true);
			
		}

	}

	private TreeItem addServerItemInServersTree(Server config) {
		
//		TreeItem item = new ServerTreeItem(serversTree, SWT.NONE, config);
		
		TreeItem item = new TreeItem(serversTree, SWT.NONE);
		
		item.setText(new String[] { config.getServerDescription()});
		item.setData("Type", "Server");
		item.setData("ServerKey", config.getServerKey());
		item.setData("ServerConfig", config);
		
		if (config.isConnected()) {
			item.setImage(serverIconUp);
		} else {
			item.setImage(serverIcon);
		}
		
		return item;
	}
	
	private TreeItem addClusterItemInServersTree(TreeItem serverItem, IClusterInfo clusterInfo) {
		TreeItem item = new TreeItem(serverItem, SWT.NONE);
		
		item.setText(new String[] { clusterInfo.getName()});
		item.setData("Type", "Cluster");
		item.setData("ClusterName", clusterInfo.getName());
		item.setData("ClusterInfo", clusterInfo);
		item.setImage(clusterIcon);
		
		return item;
	}
	
	private TreeItem addInfobaseNodeInServersTree(TreeItem clusterItem, String title) {
		TreeItem item = new TreeItem(clusterItem, SWT.NONE);
		
		item.setText(new String[] { title});
		item.setData("Type", "InfobaseNode");
		item.setImage(infobasesIcon);
		item.setChecked(false);
		
		return item;
	}
	
	private void addInfobaseItemInInfobaseNode(TreeItem infobaseNode, IInfoBaseInfo ibInfo) {
		TreeItem item = new TreeItem(infobaseNode, SWT.NONE);
		
		item.setText(new String[] { ibInfo.getName()});
		item.setData("Type", "Infobase");
		item.setData("BaseName", ibInfo.getName());
		item.setData("InfoBaseInfo", ibInfo);
		item.setImage(0, infobaseIcon);
		item.setChecked(false);
		
		item.setImage(1, ibInfo.isSessionsDenied() ? lockUsersIcon : null);
		
//		item.setChecked(ibInfo.isSessionsDenied());
	}
	
	private TreeItem addWorkingProcessNodeInClusterItem(TreeItem clusterItem, String title) {
		TreeItem item = new TreeItem(clusterItem, SWT.NONE);
		
		item.setText(new String[] { title});
		item.setData("Type", "WorkingProcessNode");
		item.setImage(workingProcessesIcon);
		item.setChecked(false);
		
		return item;
	}
	
	private void addWorkingProcessItemInWPNode(TreeItem infobaseNode, IWorkingProcessInfo wpInfo) {
		TreeItem item = new TreeItem(infobaseNode, SWT.NONE);
		
		item.setText(new String[] { wpInfo.getHostName() + " - " + wpInfo.getMainPort()});
		item.setData("Type", "WorkingProcessNode");
		item.setData("WorkingProcessInfo", wpInfo);
		item.setImage(workingProcessIcon);
		item.setChecked(false);
	}

	private void addSessionInTable(Server serverConfig, IClusterInfo clusterInfo, IInfoBaseInfo infoBaseInfo, ISessionInfo sessionInfo) {
		TableItem sessionItem = new TableItem(tableSessions, SWT.NONE);

		String infobaseName = "";
		if (infoBaseInfo == null) {
			infobaseName = serverConfig.getInfoBaseName(clusterInfo.getClusterId(), sessionInfo.getInfoBaseId());
		} else {
			infobaseName = infoBaseInfo.getName();
		}

		String[] itemText = {
							sessionInfo.getUserName(),
							infobaseName,
							Integer.toString(sessionInfo.getSessionId()),
							convertUuidToString(sessionInfo.getConnectionId()),
							sessionInfo.getStartedAt().toString(),
							sessionInfo.getLastActiveAt().toString(),
							sessionInfo.getHost(),
							sessionInfo.getAppId(),
							convertUuidToString(sessionInfo.getWorkingProcessId())//,
							
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
		sessionItem.setData("ClusterInfo", clusterInfo);
//		sessionItem.setData("InfoBaseInfoShort", infoBaseInfo);
		sessionItem.setData("SessionInfo", sessionInfo);
		sessionItem.setData("ServerConfig", serverConfig);
		sessionItem.setImage(userIcon);
		sessionItem.setChecked(false);
	}
	
	private void addConnectionInTable(Server serverConfig, IClusterInfo clusterInfo, IInfoBaseInfo infoBaseInfo, IInfoBaseConnectionShort connectionInfo) {
		TableItem connectionItem = new TableItem(tableConnections, SWT.NONE);

		String infobaseName = "";
		if (infoBaseInfo == null) {
			infobaseName = serverConfig.getInfoBaseName(clusterInfo.getClusterId(), connectionInfo.getInfoBaseId());
		} else {
			infobaseName = infoBaseInfo.getName();
		}

		String[] itemText = {
							connectionInfo.getApplication(),
							Integer.toString(connectionInfo.getConnId()),
							connectionInfo.getHost(),
							infobaseName,
							convertUuidToString(connectionInfo.getInfoBaseConnectionId()),
							connectionInfo.getConnectedAt().toString(),
							Integer.toString(connectionInfo.getSessionNumber()),
							convertUuidToString(connectionInfo.getWorkingProcessId())
							};

		connectionItem.setText(itemText);
		connectionItem.setData("ClusterInfo", 		clusterInfo);
		connectionItem.setData("InfoBaseInfoShort", infoBaseInfo);
		connectionItem.setData("Connection", 		connectionInfo);
		connectionItem.setImage(connectionIcon);
		connectionItem.setChecked(false);
	}
	
	private void addLocksInTable(Server serverConfig, IClusterInfo clusterInfo, IInfoBaseInfo infoBaseInfo, IObjectLockInfo lockInfo, List<ISessionInfo> sessionsInfo, List<IInfoBaseConnectionShort> connections) {
		TableItem connectionItem = new TableItem(tableLocks, SWT.NONE);

		String connectionNumber = "";
		String sessionNumber = "";
		String computerName = "";
		String appName = "";
		String hostName = "";
		String hostPort = "";
		String infobaseName = "";

		if (!lockInfo.getSid().equals(emptyUuid)) {
			ISessionInfo session = getSessionInfoFromLockConnectionId(lockInfo, sessionsInfo);
			sessionNumber = Integer.toString(session.getSessionId());
			
			appName = session.getAppId();
			computerName = session.getHost();
//			wpId = session.getWorkingProcessId();
		} else if (!lockInfo.getConnectionId().equals(emptyUuid)) {
			IInfoBaseConnectionShort connection = getConnectionInfoFromLockConnectionId(lockInfo, connections);
			connectionNumber = Integer.toString(connection.getConnId());
			
			appName = connection.getApplication();
			computerName = connection.getHost();
			
			UUID wpId = connection.getWorkingProcessId();
			IWorkingProcessInfo wpInfo = serverConfig.getWorkingProcessInfo(clusterInfo.getClusterId(), wpId);
			hostName = wpInfo.getHostName();
			hostPort = Integer.toString(wpInfo.getMainPort());
		
			infobaseName = serverConfig.getInfoBaseName(clusterInfo.getClusterId(), connection.getInfoBaseId());

		} else {
		}
		
//		infoBaseInfo == null
//		String infobaseName = infoBaseInfo.getName();
			
		String lockDescr = lockInfo.getLockDescr();
		Date lockedAt = lockInfo.getLockedAt();
		UUID lockedObject = lockInfo.getObject();				
		
		String[] itemText = {
							lockDescr,
							infobaseName,
							connectionNumber,
							sessionNumber,
							computerName,
							appName,
							hostName,
							hostPort,
							lockedAt.toString(),
							lockedObject.toString()
							};

		connectionItem.setText(itemText);
		connectionItem.setData("ClusterInfo", 		clusterInfo);
		connectionItem.setData("InfoBaseInfoShort", infoBaseInfo);
		connectionItem.setData("IObjectLockInfo", 	lockInfo);
		connectionItem.setImage(connectionIcon);
		connectionItem.setChecked(false);
	}

	private void addTableColumn(Table table, String text, int width) {
		TableColumn newColumn = new TableColumn(table, SWT.NONE);
		newColumn.setText(text);
		newColumn.setWidth(width);
		newColumn.setMoveable(true);
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

	private Server getServerConfigFromItem(TreeItem item) {
		
		if (item.getData("Type") == "Server")
			return (Server) item.getData("ServerConfig");
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (parentItem.getData("Type") == "Server")
				return (Server) parentItem.getData("ServerConfig");
			else 
				parentItem = parentItem.getParentItem();
		}
		return null;
	}

	private IClusterInfo getClusterInfoFromItem(TreeItem item) {
		
		if (item.getData("Type") == "Cluster")
			return (IClusterInfo) item.getData("ClusterInfo");
		
		TreeItem parentItem = item.getParentItem();
		while (parentItem != null) {
			
			if (parentItem.getData("Type") == "Cluster")
				return (IClusterInfo) parentItem.getData("ClusterInfo");
			else 
				parentItem = parentItem.getParentItem();
		}
		return null;
	}
	
	private IInfoBaseInfo getInfoBaseInfoFromItem(TreeItem item) {
		if (item.getData("Type") == "Infobase")
			return (IInfoBaseInfo) item.getData("InfoBaseInfo");
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
	
	private Image getImage(Device device, String name) {
		return new Image(device, this.getClass().getResourceAsStream(name));
	}

	private void connectToAllServers() {
		
//		clusterProvider.connectToServers(); // кажетс€ надо переделать соединение внутрь цикла
		TreeItem[] serversItem = serversTree.getItems();
		
		for (TreeItem serverItem : serversItem) { // TODO переделать на многопоток
			connectServerItem(serverItem);
		}
	}

	private void connectServerItem(TreeItem serverItem) {
		Server server = (Server) serverItem.getData("ServerConfig");
		server.connectAndAuthenticate(false);
		// смена иконки сервера на вкл/выкл
		serverItem.setImage(server.isConnected() ? serverIconUp : serverIconDown);

		fillClustersInTree(serverItem);
	}
	
	private void disconnectServerItem(TreeItem serverItem) {
		Server server = (Server) serverItem.getData("ServerConfig");
		server.disconnectFromAgent();
		serverItem.setImage(serverIconDown);
		
		TreeItem[] clusterItems = serverItem.getItems();
		for (TreeItem clusterItem : clusterItems) {
			disposeTreeItemWithChildren(clusterItem);
		}
	}

	private void selectItemInTreeHandler(TreeItem treeItem) {
		Server serverConfig;
		IClusterInfo clusterInfo;
		IInfoBaseInfo infoBaseInfo;
		
		List<ISessionInfo> sessions;
		List<IInfoBaseConnectionShort> connections;
		List<IObjectLockInfo> locks;
		
		switch ((String) treeItem.getData("Type")) {
		case "Server":
			serversTree.setMenu(serverMenu);

//			sessions = new ArrayList<>();
//			connections = new ArrayList<>();
//			locks = new ArrayList<>();
			tableSessions.removeAll();
			tableConnections.removeAll();
			tableLocks.removeAll();
			return;
//			break;
		case "Cluster":
		case "InfobaseNode":
			serversTree.setMenu(clusterMenu);

			serverConfig 	= getServerConfigFromItem(treeItem);
			clusterInfo 	= getClusterInfoFromItem(treeItem);
			infoBaseInfo 	= null;
			
			sessions 	= serverConfig.getSessions(clusterInfo.getClusterId());
			connections = serverConfig.getConnectionsShort(clusterInfo.getClusterId());
			locks 		= serverConfig.getLocks(clusterInfo.getClusterId());
			break;
		case "Infobase":
			serversTree.setMenu(infobaseMenu);

			serverConfig 	= getServerConfigFromItem(treeItem);
			clusterInfo 	= getClusterInfoFromItem(treeItem);
			infoBaseInfo 	= (IInfoBaseInfo) treeItem.getData("InfoBaseInfo");
			
			sessions 	= serverConfig.getInfoBaseSessions(clusterInfo.getClusterId(), infoBaseInfo.getInfoBaseId());
			connections = serverConfig.getInfoBaseConnectionsShort(clusterInfo.getClusterId(), infoBaseInfo.getInfoBaseId());
			locks 		= serverConfig.getInfoBaseLocks(clusterInfo.getClusterId(), infoBaseInfo.getInfoBaseId());
			break;
		default:
			serversTree.setMenu(null);
			tableSessions.removeAll();
			tableConnections.removeAll();
			tableLocks.removeAll();
			return;
//			break;
		}

		tableSessions.removeAll();
		sessions.forEach(session -> {
			addSessionInTable(serverConfig, clusterInfo, infoBaseInfo, session);
		});

		tableConnections.removeAll();
		connections.forEach(connection -> {
			addConnectionInTable(serverConfig, clusterInfo, infoBaseInfo, connection);
		});

		tableLocks.removeAll();
		locks.forEach(lock -> {
			addLocksInTable(serverConfig, clusterInfo, infoBaseInfo, lock, sessions, connections);
		});
	}

	

}
