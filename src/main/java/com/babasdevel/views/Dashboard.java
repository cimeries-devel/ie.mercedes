package com.babasdevel.views;

import com.babasdevel.cimeries.ClientPermission;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.components.Tab;
import com.babasdevel.controllers.*;
import com.babasdevel.gallery.Icons;
import com.babasdevel.models.*;
import com.babasdevel.utils.Excel;
import com.formdev.flatlaf.extras.components.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class Dashboard extends JFrame {
    private JPanel panel;
    public boolean isActivePermission;
    private FlatToggleButton buttonhome;
    private FlatToggleButton buttonNotes;
    private FlatToggleButton buttonClassroom;
    private FlatToggleButton buttonReport;
    private FlatToggleButton buttonStudents;
    private FlatTabbedPane tabbed;
    private FlatMenuBar flatMenuBar1;
    private FlatToolBar toolbarActions;
    private JXHyperlink hyperlinkEnd;
    private JXHyperlink hyperlinkPartial;
    private JXHyperlink hyperlinkUser;
    private FlatMenu consolidates;
    private FlatLabel labelDate;
    private FlatMenuItem menuItemHelp;
    private FlatMenuItem menuItemLogout;
    private FlatMenuItem menuItemClose;
    private JPanel panelProgress;
    private JXHyperlink hyperlinkLevel;
    private FlatLabel labelProgress;
    private Tab tabNotes, tabReport;
    private boolean state;
    public Teacher teacherAuth;
    public Permission permission;
    private ControllerTeacher controllerTeacher;
    public List<Grade> grades;
    public List<Skill> skills;
    public List<Section> sections;

    public Dashboard(Teacher teacherAuth) {
        this.teacherAuth = teacherAuth;
        grades = new ArrayList<>();
        skills = new ArrayList<>();
        sections = new ArrayList<>();
        this.initialize();
    }

    private void initialize() {
        controllerTeacher = new ControllerTeacher();
        setTitle("Sistema de registro de notas - Nuestra Señora de las Mercedes");
        setIconImage(Icons.ICON_APPLICATION);
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApplication(e, true);
            }
        });

        ButtonGroup groupMenu = new ButtonGroup();
        groupMenu.add(buttonhome);
        buttonhome.setSelected(true);

        ButtonGroup groupOptions = new ButtonGroup();
        groupOptions.add(buttonNotes);
        groupOptions.add(buttonClassroom);
        groupOptions.add(buttonReport);
        groupOptions.add(buttonStudents);

        this.initializeEvents();
        this.loadDataServer();
        if (teacherAuth.getSuperuser()) {
            buttonReport.doClick();
        } else {
            buttonNotes.doClick();
        }
    }

    private void initializeEvents() {
        buttonhome.setBorder(BorderFactory.createEmptyBorder());
        tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbed.putClientProperty("JTabbedPane.tabCloseCallback",
                (BiConsumer<JTabbedPane, Integer>) JTabbedPane::removeTabAt);
        tabbed.addChangeListener(b -> {
            Tab tab = (Tab) tabbed.getSelectedComponent();
            if (tab != null) {
                toolbarActions.removeAll();
                tab.getComponentsToolbar().forEach(obj -> toolbarActions.add(obj));
                toolbarActions.updateUI();
                if (!(tab instanceof TabViewPDF)) tab.option.setSelected(true);
                tab.requestFocus();
                tab.update();
            }
        });
        buttonNotes.addActionListener(actionEvent -> {
            if (existTab(tabNotes)) {
                tabNotes = new TabRegisterNotes(buttonNotes, this);
            }
            insertTab(tabNotes);
        });
        buttonReport.addActionListener(actionEvent -> {
            if (existTab(tabReport)) {
                tabReport = new TabRegisterNotesAdmin(buttonReport, this);
            }
            insertTab(tabReport);
        });
        menuItemLogout.setActionCommand("menu_item_logout");
        menuItemLogout.addActionListener(this::actionMenuItem);
        menuItemClose.setActionCommand("menu_item_close");
        menuItemClose.addActionListener(this::actionMenuItem);
        menuItemHelp.setActionCommand("menu_item_help");
        menuItemHelp.addActionListener(this::actionMenuItem);
//        buttonNotes.addChangeListener(a -> buttonNotes.setForeground(buttonNotes.isSelected() ? Color.WHITE : Color.BLACK));
//        buttonReport.addChangeListener(a -> buttonReport.setForeground(buttonReport.isSelected() ? Color.WHITE : Color.BLACK));
    }

    private void loadDataServer() {
        int code = controllerTeacher.get(teacherAuth);
        if (code == Codes.CODE_NOT_FOUNT) {
            code = controllerTeacher.getOnly(teacherAuth);
            if (code == Codes.CODE_SUCCESS) {
                teacherAuth = controllerTeacher.teacher;
            }
        } else {
            teacherAuth = controllerTeacher.teacher;
        }
        hyperlinkUser.setText(teacherAuth.getFirstName());
        ControllerPermission controllerPermission = new ControllerPermission();
        code = controllerPermission.getOnly(teacherAuth);
        if (code == Codes.CODE_SUCCESS) {
            customDashAdmin(teacherAuth.getSuperuser());
            permission = controllerPermission.object;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            isActivePermission = permission.getFinalize().after(calendar.getTime()) || permission.getFinalize().equals(calendar.getTime());
            hyperlinkLevel.setText(teacherAuth.level.getLevel());

            hyperlinkEnd.setText(new SimpleDateFormat("yyyy-MM-dd HH.mm aa").format(permission.getFinalize()));
            labelDate.setText(isActivePermission ? "Finaliza:" : "Finalizó:");
            if (permission.getPartial() == 1) {
                hyperlinkPartial.setText("Primer bimestre");
            } else if (permission.getPartial() == 2) {
                hyperlinkPartial.setText("Segundo bimestre");
            } else if (permission.getPartial() == 3) {
                hyperlinkPartial.setText("Tercer bimestre");
            } else {
                hyperlinkPartial.setText("Cuarto bimestre");
            }
        }
    }

    public void generateMenuReport() {
        ControllerClassroom controllerClassroom = new ControllerClassroom();
        controllerClassroom.all(teacherAuth.level).forEach(classroom -> {
            FlatMenu menu = new FlatMenu();
            menu.setText(classroom.getClassroom());
            consolidates.add(menu);
            for (int a = 1; a <= permission.getPartial(); a++) {
                FlatMenuItem item = new FlatMenuItem();
                item.setText("Bimestre " + a);
                item.setActionCommand(classroom.getId() + "-" + a);
                item.addActionListener(this::actionMenuItem);
                item.setEnabled(teacherAuth.getSuperuser());
                menu.add(item);
            }
        });
    }

    public void setVisibleDashboard(boolean visible) {
        pack();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(visible);
    }

    protected void insertTab(Tab tab) {
        if (!state) {
            tabbed.addTab(tab.title, tab.icon, tab);
        }
        tabbed.setSelectedComponent(tab);
    }

    protected boolean existTab(Tab tab) {
        state = tabbed.indexOfComponent(tab) != -1;
        return !state;
    }

    private void customDashAdmin(boolean isAdmin) {
        buttonReport.setVisible(isAdmin);
    }

    private void closeApplication(WindowEvent event, boolean exitApp) {
        if (JOptionPane.showConfirmDialog(
                this,
                exitApp ? "¿Desea salir de la aplicación?" : "¿Desea cerrar su sesión?",
                exitApp ? "Salir de la Aplicación" : "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            setVisible(false);
            dispose();
            if (exitApp) {
                Hibernate.close();
            } else {
                controllerTeacher.logout(teacherAuth);
                SwingUtilities.invokeLater(() -> new Login().setVisible(true));
            }
        }
    }

    private void actionMenuItem(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "menu_item_logout":
                closeApplication(null, false);
                break;
            case "menu_item_close":
                closeApplication(null, true);
                break;
            case "menu_item_help":
                break;
            default:
                generateReport(event, this);
        }
    }

    private void generateReport(ActionEvent event, Dashboard dashboard) {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            private Excel excel;
            private String nameBook;

            @Override
            protected void done() {
                boolean isSave = excel.saveBook(nameBook);
                dashboard.getPanelProgress().setVisible(false);
                if (isSave) {
                    JOptionPane.showMessageDialog(dashboard, "Excel generado correctamente.");
                }
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                FlatLabel label = (FlatLabel) dashboard.getPanelProgress().getComponent(0);
                String[] values = event.getActionCommand().split("-");
                Long idClassroom = Long.parseLong(values[0]);
                Long numPartial = Long.parseLong(values[1]);

                excel = new Excel(dashboard);
                ControllerClassroom controllerClassroom = new ControllerClassroom();
                Classroom classroom = controllerClassroom.get(idClassroom);
                label.setText(String.format("Generando consolidado de %s grado", classroom.getClassroom()));
                excel.querySet(classroom, numPartial);
                excel.createConsolidated();
                nameBook = String.format("%d_%s_bimestre", classroom.getId(), numPartial);
                return 100;
            }
        };
        worker.execute();
    }

    public JPanel getPanelProgress() {
        return panelProgress;
    }

}
