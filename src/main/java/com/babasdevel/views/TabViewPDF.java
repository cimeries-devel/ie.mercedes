package com.babasdevel.views;

import com.babasdevel.components.Tab;
import com.babasdevel.gallery.Icons;
import com.babasdevel.utils.Printing;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.swing.JRViewerPanel;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabViewPDF extends Tab {
    private final Dashboard dashboard;
    private JPanel panel;
    public JasperViewer viewer;

    public TabViewPDF(String title, Dashboard dashboard, JasperPrint print, Printing printing, String namePdf) {
        this.dashboard = dashboard;
        this.viewer = new JasperViewer(print, false);
        this.viewer.dispose();
        super.initialize(panel, title, Icons.HOME_WELCOME);
        panel.setLayout(new BorderLayout());

        JPanel panelView = (JPanel) viewer.getContentPane().getComponent(0);
        panelView = (JPanel) panelView.getComponent(0);
        for (Component component : panelView.getComponents()) {
            if (component instanceof JRViewerPanel) {
                panel.add(component, BorderLayout.CENTER);
            } else if (component instanceof JRViewerToolbar) {
                JRViewerToolbar tb = (JRViewerToolbar) component;
                Component[] components = tb.getComponents();

                JButton buttonPrint = new JButton();
                buttonPrint.setIcon(Icons.ICON_PRINT);
                buttonPrint.addActionListener(actionEvent -> {
                    try {
                        print.setName(namePdf);
                        JasperPrintManager.printReport(print, true);
                    } catch (JRException ignore) {
                    }
                });
                addComponentToolbar(buttonPrint);


                JComponent c = (JComponent) components[2];
                addComponentToolbar(c);

                addComponentToolbar(new JToolBar.Separator());

                c = (JComponent) components[4];
                addComponentToolbar(c);

                c = (JComponent) components[5];
                addComponentToolbar(c);

                c = (JComponent) components[6];
                addComponentToolbar(c);

                c = (JComponent) components[7];
                addComponentToolbar(c);

                c = (JComponent) components[8];
                addComponentToolbar(c);

                addComponentToolbar(new JToolBar.Separator());

                c = (JComponent) components[10];
                addComponentToolbar(c);

                c = (JComponent) components[11];
                addComponentToolbar(c);

                c = (JComponent) components[12];
                addComponentToolbar(c);

                addComponentToolbar(new JToolBar.Separator());

                c = (JComponent) components[14];
                addComponentToolbar(c);

                c = (JComponent) components[15];
                addComponentToolbar(c);

                c = (JComponent) components[16];
                addComponentToolbar(c);

                toolbar.forEach(com -> {
                    if (com instanceof JButton || com instanceof JToggleButton) {
                        com.setPreferredSize(new Dimension(32, 32));
                        com.setMaximumSize(com.getPreferredSize());
                        com.setMinimumSize(com.getPreferredSize());
                    } else if (com instanceof JTextField || com instanceof JComboBox) {
                        com.setPreferredSize(new Dimension(75, 32));
                        com.setMaximumSize(com.getPreferredSize());
                        com.setMinimumSize(com.getPreferredSize());
                    }
                });

            } else {
                JPanel p = (JPanel) component;
                JLabel label = (JLabel) p.getComponent(0);
                label.setFont(getFont());
                panel.add(label, BorderLayout.SOUTH);
            }
        }
        this.initialize();
    }

    private void initialize() {

    }

}
