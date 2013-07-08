package com.taobao.garuda.demo.processor.gui;

import com.taobao.garuda.demo.processor.LoadDataPreProcessor;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
import org.apache.jmeter.testelement.TestElement;

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

    private PowerTableModel tableModel;
    private JTable table;
    private JButton add;
    private JButton delete;

    public static final String[] COLUMN_NAMES = {"fileList", "meta"};

    public LoadDataPreProcessorGUI() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        ;
//        JPanel container = new JPanel(new BorderLayout());
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private Component createTablePanel() {
        tableModel = new PowerTableModel(new String[]{
                COLUMN_NAMES[0], COLUMN_NAMES[1]
        }, new Class[]{String.class, String.class});
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

    private void stopTableEditing(){
        if(table.isEditing()){
            TableCellEditor cellEditor = table.getCellEditor(
                    table.getEditingRow(),table.getEditingColumn()
            );
            cellEditor.stopCellEditing();
        }
    }

    void addRow(){
        stopTableEditing();
        tableModel.addNewRow();
        tableModel.fireTableDataChanged();
        delete.setEnabled(true);

        //Highlight (select) the appropriate row.
        int rowToSelect = tableModel.getRowCount() - 1;
        table.setRowSelectionInterval(rowToSelect,rowToSelect);
    }

    void deletRow(){
        if(table.isEditing()){
            TableCellEditor cellEditor = table.getCellEditor(
                    table.getEditingRow(),table.getEditingColumn()
            );
            cellEditor.cancelCellEditing();
        }
        int rowSelected = table.getSelectedRow();
        if(rowSelected>=0){
            tableModel.removeRow(rowSelected);
            tableModel.fireTableDataChanged();

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
    }

    @Override
    public String getStaticLabel() {
        return "LoadDataPreProcessor.garuda@taobao";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
