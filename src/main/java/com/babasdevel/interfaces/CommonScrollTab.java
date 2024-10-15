package com.babasdevel.interfaces;

import javax.swing.*;
import java.util.ArrayList;

public interface CommonScrollTab {
    ArrayList<JComponent> getComponentsToolbar();
    void addComponentToolbar(JComponent component);
}
