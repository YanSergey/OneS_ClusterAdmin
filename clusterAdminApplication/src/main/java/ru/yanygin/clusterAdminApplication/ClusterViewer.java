package ru.yanygin.clusterAdminApplication;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

/** Application window class. */
public class ClusterViewer extends ApplicationWindow {
  // TODO
  Tree serversTree;
  Composite mainForm;

  ClusterProvider clusterProvider = new ClusterProvider();
  Config config;
  private Table tableOfSessions;

  /**
   * Create the application window.
   *
   * @param configPath - путь к файлу конфигурации
   */
  public ClusterViewer(String configPath) {
    super(null);
    createActions();
    addToolBar(SWT.FLAT | SWT.WRAP);
    addMenuBar();
    addStatusLine();
    // addItems();

    this.config = Config.readConfig(configPath);
  }

  @Override
  public boolean close() {
    config.close();
    return super.close();
  }
  
  /**
   * Create contents of the application window.
   *
   * @param parent - parent composite
   */
  @Override
  protected Control createContents(Composite parent) {
    this.mainForm = parent;

    ToolBar toolBar = this.getToolBarManager().createControl(parent);

    Menu menu = this.getMenuBarManager().getMenu();

    // ViewerArea container = new ViewerArea(parent, SWT.NONE, menu, toolBar, clusterProvider, config);
    // ViewerArea container = new ViewerArea(parent, SWT.NONE, menu, toolBar, clusterProvider);

    Composite container = alternativeInit(parent);

    return container;
  }

  private Composite alternativeInit(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    // Toolbar
    // ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
    // toolBar.setBounds(0, 0, 500, 23);

    ToolBar toolBar = getToolBarManager().createControl(container);
    final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
    toolBar.setCursor(handCursor);
    // Cursor needs to be explicitly disposed
    toolBar.addDisposeListener(
        new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            if (handCursor.isDisposed() == false) {
              handCursor.dispose();
            }
          }
        });

    ToolItem toolBarItemFindNewServers = new ToolItem(toolBar, SWT.NONE);
    toolBarItemFindNewServers.setText("Find new Servers");
    toolBarItemFindNewServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            // List<String> newServers = clusterProvider.findNewServers();
            // if (!newServers.isEmpty()) {
            // fillServersList();
            // }
          }
        });

    ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
    toolBarItemConnectToServers.setText("Connect to servers");
    toolBarItemConnectToServers.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {}
        });
    container.setLayout(new FillLayout(SWT.HORIZONTAL));

    SashForm sashForm = new SashForm(container, SWT.NONE);

    serversTree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
    serversTree.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {}
        });
    serversTree.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseDown(MouseEvent e) {
            int button = e.button;
          }
        });
    serversTree.setHeaderVisible(true);
    serversTree.setSortDirection(SWT.DOWN);

    TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
    columnServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {}
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
    menuItemEditServer.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {}
        });

    Composite composite = new Composite(sashForm, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    ToolBar toolBar1 = new ToolBar(composite, SWT.FLAT);
    toolBar1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    toolBar1.setSize(10, 16);

    ToolItem tltmNewItem = new ToolItem(toolBar1, SWT.NONE);
    tltmNewItem.setText("Item");

    ToolItem tltmNewItem1 = new ToolItem(toolBar1, SWT.NONE);
    tltmNewItem1.setText("New Item");
    // тулбар

    TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

    TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
    tabItem2.setText("New Item");

    TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
    tabItem.setText("New Item");

    tableOfSessions =
        new Table(
            tabFolder,
            SWT.BORDER
                | SWT.CHECK
                | SWT.FULL_SELECTION
                | SWT.HIDE_SELECTION
                | SWT.VIRTUAL
                | SWT.MULTI);
    tableOfSessions.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
    tabItem.setControl(tableOfSessions);
    tableOfSessions.setHeaderVisible(true);
    tableOfSessions.setLinesVisible(true);

    TableColumn tblclmnAppID = new TableColumn(tableOfSessions, SWT.NONE);
    tblclmnAppID.setWidth(100);
    tblclmnAppID.setText("Application");

    sashForm.setWeights(3, 10);

    return container;
  }

  /** Create the actions. */
  private void createActions() {
    // Create the actions
  }

  /**
   * Create the menu manager.
   *
   * @return the menu manager
   */
  @Override
  protected MenuManager createMenuManager() {
    MenuManager menuManager = new MenuManager("menu");
    return menuManager;
  }

  /**
   * Create the toolbar manager.
   *
   * @return the toolbar manager
   */
  @Override
  protected ToolBarManager createToolBarManager(int style) {
    ToolBarManager toolBarManager = new ToolBarManager(style);
    return toolBarManager;
  }

  /**
   * Create the status line manager.
   *
   * @return the status line manager
   */
  @Override
  protected StatusLineManager createStatusLineManager() {
    StatusLineManager statusLineManager = new StatusLineManager();
    return statusLineManager;
  }

  /**
   * Configure the shell.
   *
   * @param newShell - shell
   */
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("OneS Cluster Administrator");
    // icon
  }
}
