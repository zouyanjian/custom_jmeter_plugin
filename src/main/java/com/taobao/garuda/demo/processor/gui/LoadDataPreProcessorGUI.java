package com.taobao.garuda.demo.processor.gui;

import com.taobao.garuda.demo.processor.LoadDataPreProcessor;
import com.taobao.garuda.util.JMeterPluginsUtils;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: wb-zouyanjian
 * Date: 13-7-1
 * Time: 下午2:06
 */
public class LoadDataPreProcessorGUI extends AbstractPreProcessorGui implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataPreProcessorGUI.class);
    private PowerTableModel tableModel;
    private JTable table;
    private JButton add;
    private JButton delete;
    private JTextField zkURLEditField;
    private JTextField entireOnlineTablesField;

    public static final String[] COLUMN_NAMES = {"fileList", "meta"};
    private Class[] columnClasses = new Class[]{String.class, String.class};;

    public LoadDataPreProcessorGUI() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
//        JPanel container = new JPanel(new BorderLayout());
        add(createTextPanel(),BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private Component createTextPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx =1.0;
        editConstraints.fill = GridBagConstraints.HORIZONTAL;

        addToPanel(mainPanel,labelConstraints,0,0,new JLabel("ZK URLS:",JLabel.RIGHT));
        addToPanel(mainPanel,editConstraints,1,0, zkURLEditField = new JTextField(80));
        addToPanel(mainPanel,labelConstraints,0,1,new JLabel("整体上线表列表:",JLabel.RIGHT));
        addToPanel(mainPanel,editConstraints,1,1, entireOnlineTablesField = new JTextField(80));

        return mainPanel;
    }

    private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
        constraints.gridx = col;
        constraints.gridy = row;
        panel.add(component, constraints);
    }

    private Component createTablePanel() {
        tableModel = new PowerTableModel(new String[]{
                COLUMN_NAMES[0], COLUMN_NAMES[1]
        }, columnClasses);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return makeScrollPane(table);
    }

    private JPanel createButtonPanel() {
        add = new JButton("添加");
        add.setActionCommand("add");
        add.addActionListener(this);
        add.setEnabled(true);

        delete = new JButton("删除");
        delete.setActionCommand("delete");
        delete.addActionListener(this);
        checkDeleteStatus();
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(add);
        buttonPanel.add(delete);
        return buttonPanel;
    }

    private void checkDeleteStatus() {
        if (table.getRowCount() > 0) {
            delete.setEnabled(true);
        } else {
            delete.setEnabled(false);
        }
    }

    private void stopTableEditing() {
        if (table.isEditing()) {
            TableCellEditor cellEditor = table.getCellEditor(
                    table.getEditingRow(), table.getEditingColumn()
            );
            cellEditor.stopCellEditing();
        }
    }

    void addRow() {
        stopTableEditing();
        tableModel.addNewRow();
        tableModel.fireTableDataChanged();
        delete.setEnabled(true);

        //Highlight (select) the appropriate row.
        int rowToSelect = tableModel.getRowCount() - 1;
        table.setRowSelectionInterval(rowToSelect, rowToSelect);
    }

    void deleteRow() {
        if (table.isEditing()) {
            TableCellEditor cellEditor = table.getCellEditor(
                    table.getEditingRow(), table.getEditingColumn()
            );
            cellEditor.cancelCellEditing();
        }
        int rowSelected = table.getSelectedRow();
        if (rowSelected >= 0) {
            tableModel.removeRow(rowSelected);
            tableModel.fireTableDataChanged();
            if (tableModel.getRowCount() == 0) {
                delete.setEnabled(false);
            } else {
                //Highlight selected row...
                int rowToSelect = rowSelected;
                if (rowSelected >= tableModel.getRowCount()) {
                    rowToSelect = rowSelected - 1;
                }
                table.setRowSelectionInterval(rowToSelect, rowToSelect);
            }

        }
    }




    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        LoadDataPreProcessor processor = new LoadDataPreProcessor();
        modifyTestElement(processor);
        processor.setComment("Data loading...");
        return processor;
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        super.configureTestElement(testElement);
        if(table.isEditing()){
            table.getCellEditor().stopCellEditing();
        }

        if(testElement instanceof  LoadDataPreProcessor) {
            LoadDataPreProcessor processor = (LoadDataPreProcessor) testElement;
            CollectionProperty rows = JMeterPluginsUtils.tableModelRowsToCollectionProperty(tableModel,LoadDataPreProcessor.DATA_PROPERTY);
            processor.setData(rows);
        }
    }

    @Override
    public String getStaticLabel() {
        return "Garuda@localNode前置处理器";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        tableModel.clearData();
        LoadDataPreProcessor processor = (LoadDataPreProcessor)element;
        JMeterProperty fileMetas = processor.getSamplerSettings();
        if(!(fileMetas instanceof NullProperty)){
            JMeterPluginsUtils.collectionPropertyToTableModelRows((CollectionProperty) fileMetas, tableModel, columnClasses);
        }else{
            LOGGER.warn("Received null property instead of collection");
        }

        zkURLEditField.setText(element.getPropertyAsString(LoadDataPreProcessor.ZK_URL));
        entireOnlineTablesField.setText(element.getPropertyAsString(LoadDataPreProcessor.ENTIREONLINE_TABLE_STR));
        checkDeleteStatus();
    }

    @Override
    public void clearGui() {
        super.clearGui();
        initFields();
    }

    private void initFields() {
        this.zkURLEditField.setText("");
        this.entireOnlineTablesField.setText("");
        this.tableModel.clearData();
        this.tableModel.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("add")) {
            addRow();
        } else if (action.equals("delete")) {
            deleteRow();
        }

    }
}
