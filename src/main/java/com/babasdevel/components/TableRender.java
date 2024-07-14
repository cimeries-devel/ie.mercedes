package com.babasdevel.components;

import com.formdev.flatlaf.extras.components.FlatButton;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableRender {
    public static class ButtonRenderer extends FlatButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
