package com.babasdevel.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnGroup {
    protected TableCellRenderer renderer;
    protected List<TableColumn> columns;
    protected List<ColumnGroup> groups;

    protected String text;
    protected int margin = 0;

    public ColumnGroup(String text) {
        this(text, null);
    }

    public ColumnGroup(String text, TableCellRenderer renderer) {
        this.text = text;
        this.renderer = renderer;
        this.columns = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public void add(TableColumn column) {
        columns.add(column);
    }

    public void add(ColumnGroup group) {
        groups.add(group);
    }

    public List<ColumnGroup> getColumnGroups(TableColumn column) {
        if (!contains(column)) {
            return Collections.emptyList();
        }
        List<ColumnGroup> result = new ArrayList<>();
        result.add(this);
        if (columns.contains(column)) {
            return result;
        }
        for (ColumnGroup columnGroup : groups) {
            result.addAll(columnGroup.getColumnGroups(column));
        }
        return result;
    }

    private boolean contains(TableColumn column) {
        if (columns.contains(column)) {
            return true;
        }
        for (ColumnGroup group : groups) {
            if (group.contains(column)) {
                return true;
            }
        }
        return false;
    }

    public TableCellRenderer getHeaderRenderer() {
        return renderer;
    }

    public String getHeaderValue() {
        return text;
    }

    public Dimension getSize(JTable table) {
        TableCellRenderer renderer = this.renderer;
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue() == null || getHeaderValue().trim().isEmpty() ? " "
                : getHeaderValue(), false, false, -1, -1);

        int height = comp.getPreferredSize().height;
        int width = 0;
        for (ColumnGroup columnGroup : groups) {
            width += columnGroup.getSize(table).width;
        }
        for (TableColumn tableColumn : columns) {
            width += tableColumn.getWidth();
            width += margin;
        }
        return new Dimension(width, height);
    }

    public void setColumnMargin(int margin) {
        this.margin = margin;
        for (ColumnGroup columnGroup : groups) {
            columnGroup.setColumnMargin(margin);
        }
    }
}
