package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.fortify.fod.remediation.models.VulnFolder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.File;

public class AnalysisResultsToolWindow extends RemediationToolWindowBase {

    private VulnTabbedPane tabbedPane;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super.createToolWindowContent(project, toolWindow);

        // Webgoat
        VulnFolder[] folders = new VulnFolder[]{
                new VulnFolder("Critical","Critical"),
                new VulnFolder("High","High"),
                new VulnFolder("Medium","Medium"),
                new VulnFolder("Low","Low"),
                new VulnFolder("Info","Info"),
                new VulnFolder("All","All")
        };

        JPanel panel = getDefaultToolWindowContentPanel();

        panel.add(headerLabel, BorderLayout.NORTH);

        tabbedPane = new VulnTabbedPane();

        DefaultSingleSelectionModel tabModel = new DefaultSingleSelectionModel();
        tabbedPane.setModel(tabModel);

        for(VulnFolder f:folders) {
            tabbedPane.addTab(f.getTitle(), new AnalysisResultsTabPanel(f));
            //tabbedPane.add(getTabContents());
        }

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                JPanel selectedPanel = (JPanel)sourceTabbedPane.getSelectedComponent();
                System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
                //folderLabel = (JLabel)selectedPanel.getComponent(0);
                //folderLabel.setText(LocalTime.now().toString());
            }
        });

        panel.add(tabbedPane, BorderLayout.CENTER);

        addContent(toolWindow, panel);
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Analysis Results");
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }

    private void showFileInEditor() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        final String testFilePath = project.getBasePath()+"\\src\\com\\azl2\\bigfile.java";

        final VirtualFile testFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(testFilePath));

        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, testFile);
        int line = openFileDescriptor.getLine();

        FileEditorManager fem = FileEditorManager.getInstance(project);
        java.util.List<FileEditor> fileEditors = fem.openEditor(openFileDescriptor, true);
    }

    private void showInMemoryDocument() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/documents.html

        EditorFactory ef = EditorFactory.getInstance();
        FileDocumentManager fdm = FileDocumentManager.getInstance();
        FileEditorManager fem = FileEditorManager.getInstance(project);

        Document document = ef.createDocument("testing this thing!");
        VirtualFile file = fdm.getFile(document);

//        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, file);
//        fem.openEditor(openFileDescriptor, true);

        // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206132679/comments/206178995

        // An in-memory VF.
        LightVirtualFile vf = new LightVirtualFile("LightVirtualFile.java", "What the deuce?!??");
        OpenFileDescriptor fd = new OpenFileDescriptor(project, vf);

        //fd.navigate(true);
        fem.openEditor(fd, true);
        //fem.openFile(vf, true   );
    }

    class TestAction extends ToggleAction {

        public TestAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
            super(text, description, icon);
        }

        @Override
        public boolean isSelected(AnActionEvent anActionEvent) {
            return true;
        }

        @Override
        public void setSelected(AnActionEvent anActionEvent, boolean b) {

            System.out.println("set selected?");
        }
    }

}
