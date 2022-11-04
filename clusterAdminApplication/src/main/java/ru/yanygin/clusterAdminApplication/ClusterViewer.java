package ru.yanygin.clusterAdminApplication;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

/** Application window class. */
public class ClusterViewer extends ApplicationWindow {

  Composite mainForm;

  ClusterProvider clusterProvider = new ClusterProvider();
  Config config;

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

    ViewerArea container = new ViewerArea(parent, SWT.NONE, menu, toolBar, clusterProvider, config);

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

  /** Return the initial size of the window. */
  @Override
  protected Point getInitialSize() {
    return new Point(1600, 800);
  }

}
