package com.babasdevel.mercedes;

import com.babasdevel.common.Hibernate;
import com.babasdevel.models.ModelReport;
import com.babasdevel.views.Login;
import com.formdev.flatlaf.FlatIntelliJLaf;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
    public static boolean isProduction;
    public static void main(String[] args) {
        isProduction = Boolean.parseBoolean(args[0]);
        Hibernate.initialize(isProduction);
        initLook();
        SwingUtilities.invokeLater(()-> new Login().setVisible(true));
    }
    static private void initLook(){
        FlatIntelliJLaf.setup();
        UIManager.put( "TabbedPane.tabHeight", 28);
        UIManager.put("TabbedPane.tabInsets", new Insets(2,6,2,6));
        UIManager.put("TabbedPane.closeArc", 15);
        UIManager.put("TabbedPane.closeSize", new Dimension(16, 16));
        UIManager.put("ToggleButton.selectedBackground", new Color(60, 131, 197));
        UIManager.put("ToggleButton.pressedBackground", new Color(40, 111, 177));
        UIManager.put("ToggleButton.selectedForeground", Color.WHITE);
//        UIManager.put( "TabbedPane.selectedBackground", Color.BLUE );
//        UIManager.put("TabbedPane.hoverColor", Color.RED);
//        UIManager.put("TabbedPane.closeCrossPlainSize", 4f);
        UIManager.put( "TabbedPane.closeCrossFilledSize", 5.5f );
        UIManager.put("Table.rowHeight", 25);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);
        UIManager.put("TableHeader.background", new Color(100, 100, 100));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(194, 194, 194)));

        UIManager.put("SplitPaneDivider.style", "plain");
    }
}