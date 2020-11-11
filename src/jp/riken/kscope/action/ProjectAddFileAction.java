/*
 * K-scope
 * Copyright 2012-2013 RIKEN, Japan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.riken.kscope.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.riken.kscope.Application;
import jp.riken.kscope.Message;
import jp.riken.kscope.common.EXPLORE_PANEL;
import jp.riken.kscope.data.FILE_TYPE;
import jp.riken.kscope.data.SourceFile;
import jp.riken.kscope.model.FileTreeModel;
import jp.riken.kscope.model.ProjectModel;
import jp.riken.kscope.service.AppController;
import jp.riken.kscope.service.ProjectService;
import jp.riken.kscope.utils.SwingUtils;

/**
 * XML file addition action class
 *
 * @author RIKEN
 */
public class ProjectAddFileAction extends ActionBase {

  /** Action Explorer Panel */
  private EXPLORE_PANEL panelExplore;

  /**
   * Constructor
   *
   * @param controller Application controller
   * @param panel Action explorer panel
   */
  public ProjectAddFileAction(AppController controller, EXPLORE_PANEL panel) {
    super(controller);
    panelExplore = panel;
  }

  /**
   * Check if the action is executable. <br>
   * Check before executing the action and switch the menu enable. <br>
   *
   * @return true = Action can be executed
   */
  @Override
  public boolean validateAction() {
    ProjectModel project = this.controller.getProjectModel();
    if (project == null) return false;
    if (project.getFileType() == FILE_TYPE.XCODEML_XML && panelExplore == EXPLORE_PANEL.XML) {
      return true;
    }
    if (project.getFileType() == FILE_TYPE.FORTRANLANG && panelExplore == EXPLORE_PANEL.SOURCE) {
      return true;
    }

    // Menu enable switching
    boolean enable = false;
    if (panelExplore == null || panelExplore == EXPLORE_PANEL.UNKNOWN) {
      // Action from mainframe
      EXPLORE_PANEL activePanel =
          this.controller.getMainframe().getPanelExplorerView().getSelectedEnumPanel();
      if (project.getFileType() == FILE_TYPE.XCODEML_XML) {
        // Enable only if the XML tree panel is displayed.
        enable = (activePanel == EXPLORE_PANEL.XML);
      }
      //            if (project.getFileType() == FILE_TYPE.FORTRANLANG) {
      // // Enable only when the source tree panel is displayed.
      //                enable = (activePanel == EXPLORE_PANEL.SOURCE);
      //            }
    }
    return enable;
  }

  /**
   * Action occurrence event
   *
   * @param event Event information
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    // Project information
    ProjectModel project = this.controller.getProjectModel();

    // Status message
    String message = null;
    if (project.getFileType() == FILE_TYPE.XCODEML_XML) {
      message = Message.getString("mainmenu.project.addxmlfile"); // Add XML file
    } else if (project.getFileType() == FILE_TYPE.FORTRANLANG) {
      message =
          Message.getString("projectaddfileaction.addfile.fortran.status"); // Add Fortran file
    }
    Application.status.setMessageMain(message);

    // Get the parent Frame.
    Frame frame = getWindowAncestor(event);

    String projectFolder = null;
    if (project.getProjectFolder() != null) {
      projectFolder = project.getProjectFolder().getAbsolutePath();
    }
    if (projectFolder == null) {
      projectFolder = System.getProperty("user.dir");
    }

    // Title of the file selection dialog
    String title = null;
    SwingUtils.ExtFileFilter filter = null;
    if (project.getFileType() == FILE_TYPE.XCODEML_XML) {
      title =
          Message.getString("projectaddfileaction.selectfiledialog.xml.title"); // "Select XML file"
      // XML file filter
      // Changed from FILE_TYPE to filter creation at 2013/05/14 by @hira
      // String description = Message.getString
      // ("projectaddfileaction.selectfiledialog.xml.filterdescription"); // XcodeML file (* .xml)
      // String[] exts = {"xml"};
      // filter = new SwingUtils().new ExtFileFilter(description, exts);
      filter = new SwingUtils().new ExtFileFilter(FILE_TYPE.getXcodemlFilter());
    } else if (project.getFileType() == FILE_TYPE.FORTRANLANG) {
      title =
          Message.getString(
              "projectaddfileaction.selectfiledialog.fortran.title"); // Fortran file selection
      // Source file filter
      // Changed from FILE_TYPE to filter creation at 2013/05/14 by @hira
      // String description = Message.getString
      // ("projectaddfileaction.selectfiledialog.fortran.filterdescription"); // Source file (* .f,
      // * .f90)
      // String[] exts = {"f", "f90"};
      // filter = new SwingUtils().new ExtFileFilter(description, exts);
      filter = new SwingUtils().new ExtFileFilter(FILE_TYPE.getFortranFilter());
    }

    // Display the file selection dialog.
    File[] selected = SwingUtils.showOpenFileDialog(frame, title, projectFolder, filter, true);
    if (selected == null || selected.length <= 0) {
      Application.status.setMessageMain(
          message + Message.getString("action.common.cancel.status")); // Cancel
      return;
    }

    // add a selection file
    List<File> list = java.util.Arrays.asList(selected);

    // Add folder to project
    ProjectService service = new ProjectService(project);
    service.addProjectSelectedFile(list);

    // Display the selected XML file in the XML tree.
    List<SourceFile> listSource = project.getListSelectedFile();
    List<SourceFile> xmlfiles = new ArrayList<SourceFile>();
    List<SourceFile> srcfiles = new ArrayList<SourceFile>();
    if (listSource != null && listSource.size() > 0) {
      for (SourceFile file : listSource) {
        if (FILE_TYPE.isFortranFile(file.getFile())) {
          srcfiles.add(file);
        } else if (FILE_TYPE.isXcodemlFile(file.getFile())) {
          xmlfiles.add(file);
        }
      }
    }
    FileTreeModel treeModel = null;
    List<SourceFile> setfiles = null;
    if (project.getFileType() == FILE_TYPE.XCODEML_XML) {
      // Display the selected XML file in the XML tree.
      treeModel = this.controller.getXmlTreeModel();
      setfiles = xmlfiles;
    } else if (project.getFileType() == FILE_TYPE.FORTRANLANG) {
      // Add Fortran file
      treeModel = this.controller.getSourceTreeModel();
      setfiles = srcfiles;
    }
    if (treeModel != null) {
      if (setfiles != null && setfiles.size() > 0) {
        treeModel.setSourceFile(setfiles.toArray(new SourceFile[0]));
      } else {
        treeModel.clearTreeModel();
      }
    }

    Application.status.setMessageMain(
        message + Message.getString("action.common.done.status")); // Done

    return;
  }
}
