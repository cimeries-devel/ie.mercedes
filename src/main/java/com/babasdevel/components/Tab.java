package com.babasdevel.components;

import com.babasdevel.interfaces.CommonScrollTab;
import com.formdev.flatlaf.extras.components.FlatScrollPane;
import com.formdev.flatlaf.extras.components.FlatToggleButton;

import javax.swing.*;
import java.util.ArrayList;

public class Tab extends FlatScrollPane implements CommonScrollTab {
    public final static String NONE = "none";

    public String title;
    public Icon icon;
    public FlatToggleButton option;
    public ArrayList<JComponent> toolbar;
    public void initialize(JPanel panel, String title, Icon icon, FlatToggleButton option) {
        setViewportView(panel);
        this.title = title;
        this.icon = icon;
        this.option = option;
        toolbar = new ArrayList<>();
        setBorder(BorderFactory.createEmptyBorder());
    }
    public void initialize(JPanel panel, String title, Icon icon) {
        setViewportView(panel);
        this.title = title;
        this.icon = icon;
        toolbar = new ArrayList<>();
        setBorder(BorderFactory.createEmptyBorder());
    }
    @Override
    public ArrayList<JComponent> getComponentsToolbar(){
        return toolbar;
    }
    @Override
    public void addComponentToolbar(JComponent component){
        toolbar.add(component);
    }
    public void update() {
        System.out.println("update tab father");
    }
}
