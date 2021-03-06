package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.HideableTitledPanel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import net.miginfocom.layout.Grid;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class AnalysisTraceToolWindow extends RemediationToolWindowBase {

    private final String abstractText = "<html><h3>Unreleased Resource: Unmanaged Object (controlflow)</h3>" +
        "The function <b>printButton_Click()</b> in <b>Form1.cs</b> fails to properly dispose of unmanaged system resources allocated by <b>PrintDocument()</b> on line <b>34</b>." +
        "The program fails to dispose of a managed object that utilizes unmanaged system resources.</html>";

    private void gotoLine(int lineNumber) {

        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManager fem = FileEditorManager.getInstance(project);

        Editor selectedTextEditor = fem.getSelectedTextEditor();
        CaretModel caretModel = selectedTextEditor.getCaretModel();

        caretModel.moveToLogicalPosition(new LogicalPosition(lineNumber-1,0));

        ScrollingModel scrollingModel = selectedTextEditor.getScrollingModel();
        scrollingModel.scrollToCaret(ScrollType.CENTER);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = getDefaultToolWindowContentPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        panel.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel.add(headerLabel, gridBagConstraints);

        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        panel.add(getTraceNavigationPanel(), gridBagConstraints);

        JBList<TraceItem> list = new JBList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
//        renderer.setIcon(IconLoader.getIcon("/icons/plus.png"));
        list.setCellRenderer(new TraceRenderer());

        DefaultListModel<TraceItem> listModel = new DefaultListModel<>();

        listModel.addElement(new TraceItem("Go to line: 1", "Assign"));
        listModel.addElement(new TraceItem("Go to line: 15", "Deref"));
        listModel.addElement(new TraceItem("Go to line: 35", "InCall"));
        listModel.addElement(new TraceItem("Go to line: 55", "Read"));
        listModel.addElement(new TraceItem("Go to line: 60", "Assign"));
        listModel.addElement(new TraceItem("Go to line: 99", "InCall"));

        list.setModel(listModel);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //String selectedValue = list.getSelectedValue();
                //String lineNum = selectedValue.substring(selectedValue.indexOf(":")+1).trim();
                //gotoLine(Integer.parseInt(lineNum));
            }
        });
        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5,0,5,0);
        panel.add(new JBScrollPane(list), gridBagConstraints);

        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        JLabel abstractLabel = new JLabel(abstractText);
        HideableTitledPanel hideableTitledPanel = new HideableTitledPanel("Abstract", abstractLabel, false);
        panel.add(hideableTitledPanel, gridBagConstraints);

        addContent(toolWindow, panel);
    }

    private JPanel getTraceNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        Dimension navDimension = new Dimension(35,24);

        JButton navPrevButton = new JButton(AllIcons.Actions.Left);
        navPrevButton.setPreferredSize(navDimension);
        panel.add(navPrevButton, BorderLayout.WEST);

        JLabel traceLabel = new JLabel("Trace: Multiple Paths (1 of n)");
        traceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(traceLabel, BorderLayout.CENTER);

        JButton navNextButton = new JButton(AllIcons.Actions.Right);
        navNextButton.setPreferredSize(navDimension);
        panel.add(navNextButton , BorderLayout.EAST);

        return panel;
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Analysis Trace");
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }

    class TraceItem {
        public String name;
        public String traceType;
        public TraceItem(String n, String t) {
            name = n;
            traceType = t;
        }
        @Override
        public String toString() {
            return name + " ("+traceType+")";
        }
    }

    class TraceRenderer extends JLabel
            implements ListCellRenderer {

        public TraceRenderer() {
            setOpaque(true);
//            setHorizontalAlignment(CENTER);
//            setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
            //int selectedIndex = ((Integer)value).intValue();
            TraceItem ti = (TraceItem)value;

            if(ti != null) {
                setIcon(IconLoader.getIcon("/icons/trace/"+ti.traceType+".png"));
                setText(ti.toString());
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }

}
