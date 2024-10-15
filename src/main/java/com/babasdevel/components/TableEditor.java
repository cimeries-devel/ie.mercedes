package com.babasdevel.components;

import javax.swing.*;
import java.awt.*;

public class TableEditor {
    public static class CellEditorReport extends DefaultCellEditor {
        public CellEditorReport() {
            super(new JComboBox<>());
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
//            TableModel.ModelProduct model = (TableModel.ModelProduct) table.getModel();
//            Product product = model.products.get(table.convertRowIndexToModel(row));
            System.out.println(component);
            return component;
        }
    }
}
