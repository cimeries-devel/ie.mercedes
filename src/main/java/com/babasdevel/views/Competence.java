package com.babasdevel.views;

import com.babasdevel.models.Skill;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatTextArea;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

public class Competence {
    private JPanel panel;
    private JXLabel labelTitle;
    private FlatTextField flatTextField1;
    private FlatTextArea flatTextArea1;

    public Competence(Skill skill) {
        labelTitle.setText(skill.getName());
    }

    public JPanel getPanel() {
        return panel;
    }

}
