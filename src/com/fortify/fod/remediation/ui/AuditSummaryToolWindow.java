package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.ChangeActionNotifier;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AuditSummaryToolWindow extends RemediationToolWindowBase {

    private JPanel createAuditContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Audit content goes here"));
        return panel;
    }
    private JPanel createHistoryContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("History content goes here"));
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(headerLabel, BorderLayout.NORTH);

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Audit", createAuditContent());
        tab.addTab("History", createHistoryContent());

        panel.add(tab, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel,"",false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Audit Summary");
    }

    @Override
    protected void onIssueChange(String msg) {
        headerLabel.setText(msg);
    }

    @Override
    protected void onFoDProjectChange(String msg) {

    }
}
