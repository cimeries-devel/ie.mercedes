package com.babasdevel.components;

import com.babasdevel.models.Course;
import com.babasdevel.models.Grade;

import javax.swing.*;
import java.awt.*;

public class ComboRender extends DefaultListCellRenderer {
    private String placeholder;
    public ComboRender(){}
    public ComboRender(String placeholder){
        this.placeholder = placeholder;
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        if (value instanceof Grade){
            Grade grade = (Grade)value;
            value = grade.getName();
        } else if (value instanceof Course){
            Course course = (Course) value;
            value = course.getName();
        } else {
            if (value == null) value = placeholder;
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}