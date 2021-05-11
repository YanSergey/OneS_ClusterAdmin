package ru.yanygin.clusterAdminApplication;

import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config.Server;
//import ru.yanygin.clusterAdminLibraryUI.ServersTree;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ClusterViewer extends ApplicationWindow {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;
	Image infobaseIcon;

	Tree serversTree;
	Composite mainForm;
	
	ClusterProvider clusterProvider = new ClusterProvider();
	private Table tableOfSessions;

	/**
	 * Create the application window.
	 */
	public ClusterViewer() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
//		addItems();
		
	}

	@Override
	public boolean close() {
		
		Map<String, Server> servers = clusterProvider.getServers();
			
		if (!servers.isEmpty()) {
			servers.forEach((server, config) -> {
				if (config.isConnected())
					config.disconnectFromAgent();
			});
		}
		
		return super.close();
	}
	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		this.mainForm = parent;
		
		ToolBar toolBar = this.getToolBarManager().createControl(parent);
		
		Menu menu = this.getMenuBarManager().getMenu();
	
		ViewerArea container = new ViewerArea(parent, SWT.NONE, menu, toolBar, clusterProvider);
		
		//Composite container = alternativeInit(parent);
		
		return container;
	}

	private Composite alternativeInit(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		// Toolbar
//		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
//		toolBar.setBounds(0, 0, 500, 23);
		ToolBar toolBar = getToolBarManager().createControl(container);
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
//				List<String> newServers = clusterProvider.findNewServers();
//				if (!newServers.isEmpty()) {
//					fillServersList();
//				}
			}
		});

		ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectToServers.setText("Connect to servers");		
		toolBarItemConnectToServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(container, SWT.NONE);
		
		serversTree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		serversTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		serversTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int button = e.button;
			}
		});
		serversTree.setHeaderVisible(true);
		serversTree.setSortDirection(SWT.DOWN);
		
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		columnServer.setText("Cluster/Infobase");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
		columnPing.setText("RAS port");
		columnPing.setWidth(60);

		Menu menu = new Menu(serversTree);
		serversTree.setMenu(menu);

		MenuItem menuItemEditServer = new MenuItem(menu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("New Item");
		
		tableOfSessions = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		tableOfSessions.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		tabItem.setControl(tableOfSessions);
		tableOfSessions.setHeaderVisible(true);
		tableOfSessions.setLinesVisible(true);
		
		Menu menu2 = new Menu(tableOfSessions);
		tableOfSessions.setMenu(menu2);
		
		MenuItem menuItemEditServer_1 = new MenuItem(menu2, SWT.NONE);
		menuItemEditServer_1.setText("Edit Server");

		TableColumn tblclmnAppID = new TableColumn(tableOfSessions, SWT.NONE);
		tblclmnAppID.setWidth(100);
		tblclmnAppID.setText("Application");

		sashForm.setWeights(new int[] {1, 2});
	
		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

//	/**
//	 * Launch the application.
//	 * @param args
//	 */
//	public static void main(String args[]) {
//		try {
//			ClusterViewer window = new ClusterViewer();
//			window.setBlockOnOpen(true);
//			window.open();
//			Display.getCurrent().dispose();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Cluster Administrating");
		// icon
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1600, 800);
	}
	
//	@Override
//	public int open() {
//		((ViewerArea) this.getContents()).open();
//		return super.open();
//	}
}
