package com.babasdevel.views;

import com.babasdevel.cimeries.ClientPermission;
import com.babasdevel.cimeries.Codes;
import com.babasdevel.common.Hibernate;
import com.babasdevel.components.Tab;
import com.babasdevel.controllers.*;
import com.babasdevel.gallery.Icons;
import com.babasdevel.mercedes.Application;
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
                Hibernate.close();
                Hibernate.initialize(Application.isProduction);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(panel1, BorderLayout.NORTH);
        flatMenuBar1 = new FlatMenuBar();
        flatMenuBar1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 15, -1));
        panel1.add(flatMenuBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final FlatMenu flatMenu1 = new FlatMenu();
        flatMenu1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        flatMenu1.setText("Inicio");
        flatMenuBar1.add(flatMenu1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        menuItemLogout = new FlatMenuItem();
        menuItemLogout.setText("Cerrar sesión");
        flatMenu1.add(menuItemLogout);
        menuItemClose = new FlatMenuItem();
        menuItemClose.setText("Salir de la aplicación");
        flatMenu1.add(menuItemClose);
        final FlatMenu flatMenu2 = new FlatMenu();
        flatMenu2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        flatMenu2.setText("Reportes");
        flatMenuBar1.add(flatMenu2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        consolidates = new FlatMenu();
        consolidates.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        consolidates.setText("Consolidados");
        flatMenu2.add(consolidates);
        final Spacer spacer1 = new Spacer();
        flatMenuBar1.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final FlatMenu flatMenu3 = new FlatMenu();
        flatMenu3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        flatMenu3.setText("Ayuda");
        flatMenuBar1.add(flatMenu3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        menuItemHelp = new FlatMenuItem();
        menuItemHelp.setText("Acerca de..");
        flatMenu3.add(menuItemHelp);
        final FlatMenu flatMenu4 = new FlatMenu();
        flatMenu4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        flatMenu4.setText("Configuración");
        flatMenuBar1.add(flatMenu4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final FlatToolBar flatToolBar1 = new FlatToolBar();
        panel2.add(flatToolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 40), new Dimension(-1, 40), new Dimension(-1, 40), 0, false));
        toolbarActions = new FlatToolBar();
        panel2.add(toolbarActions, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-3947581));
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 1), new Dimension(-1, 1), new Dimension(-1, 1), 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel4.setPreferredSize(new Dimension(230, 10));
        panel.add(panel4, BorderLayout.WEST);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(229, -1), new Dimension(229, -1), new Dimension(229, -1), 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 3, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 28), new Dimension(-1, 28), new Dimension(-1, 28), 0, false));
        final FlatLabel flatLabel1 = new FlatLabel();
        flatLabel1.setIcon(new ImageIcon(getClass().getResource("/com/babasdevel/views/auth.png")));
        flatLabel1.setText("");
        panel6.add(flatLabel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hyperlinkUser = new JXHyperlink();
        hyperlinkUser.setFocusable(false);
        hyperlinkUser.setText("-");
        panel6.add(hyperlinkUser, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel6.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setBackground(new Color(-3947581));
        panel5.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 1), new Dimension(-1, 1), new Dimension(-1, 1), 0, false));
        final FlatSplitPane flatSplitPane1 = new FlatSplitPane();
        flatSplitPane1.setBackground(new Color(-3947581));
        flatSplitPane1.setDividerLocation(65);
        flatSplitPane1.setDividerSize(0);
        panel5.add(flatSplitPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.setBackground(new Color(-3947581));
        flatSplitPane1.setLeftComponent(panel8);
        buttonhome = new FlatToggleButton();
        buttonhome.setBorderPainted(true);
        buttonhome.setButtonType(FlatButton.ButtonType.tab);
        buttonhome.setFocusable(false);
        Font buttonhomeFont = this.$$$getFont$$$(null, Font.BOLD, -1, buttonhome.getFont());
        if (buttonhomeFont != null) buttonhome.setFont(buttonhomeFont);
        buttonhome.setHorizontalTextPosition(0);
        buttonhome.setIcon(new ImageIcon(getClass().getResource("/com/babasdevel/views/home.png")));
        buttonhome.setIconTextGap(1);
        buttonhome.setSelected(true);
        buttonhome.setTabSelectedBackground(new Color(-7697782));
        buttonhome.setTabUnderlineHeight(4);
        buttonhome.setTabUnderlinePlacement(2);
        buttonhome.setText("Inicio");
        buttonhome.setVerticalTextPosition(3);
        panel8.add(buttonhome, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(65, 60), new Dimension(65, 60), new Dimension(65, 60), 0, false));
        final Spacer spacer3 = new Spacer();
        panel8.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(5, 1, new Insets(15, 5, 5, 5), -1, 10));
        flatSplitPane1.setRightComponent(panel9);
        buttonNotes = new FlatToggleButton();
        buttonNotes.setButtonType(FlatButton.ButtonType.roundRect);
        Font buttonNotesFont = this.$$$getFont$$$(null, Font.BOLD, 14, buttonNotes.getFont());
        if (buttonNotesFont != null) buttonNotes.setFont(buttonNotesFont);
        buttonNotes.setText("Notas");
        panel9.add(buttonNotes, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonClassroom = new FlatToggleButton();
        buttonClassroom.setButtonType(FlatButton.ButtonType.roundRect);
        buttonClassroom.setEnabled(false);
        Font buttonClassroomFont = this.$$$getFont$$$(null, Font.BOLD, 14, buttonClassroom.getFont());
        if (buttonClassroomFont != null) buttonClassroom.setFont(buttonClassroomFont);
        buttonClassroom.setText("Aulas");
        panel9.add(buttonClassroom, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonStudents = new FlatToggleButton();
        buttonStudents.setButtonType(FlatButton.ButtonType.roundRect);
        buttonStudents.setEnabled(false);
        Font buttonStudentsFont = this.$$$getFont$$$(null, Font.BOLD, 14, buttonStudents.getFont());
        if (buttonStudentsFont != null) buttonStudents.setFont(buttonStudentsFont);
        buttonStudents.setText("Alumnos");
        panel9.add(buttonStudents, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonReport = new FlatToggleButton();
        buttonReport.setButtonType(FlatButton.ButtonType.roundRect);
        Font buttonReportFont = this.$$$getFont$$$(null, Font.BOLD, 14, buttonReport.getFont());
        if (buttonReportFont != null) buttonReport.setFont(buttonReportFont);
        buttonReport.setLabel("Libretas");
        buttonReport.setText("Libretas");
        panel9.add(buttonReport, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel9.add(spacer4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel10.setBackground(new Color(-3947581));
        panel4.add(panel10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(1, -1), new Dimension(8, 24), new Dimension(1, -1), 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel11, BorderLayout.EAST);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(panel12, BorderLayout.SOUTH);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel13.setBackground(new Color(-3947581));
        panel12.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 1), new Dimension(-1, 1), new Dimension(-1, 1), 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 5, new Insets(0, 2, 0, 2), 10, -1));
        panel12.add(panel14, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 25), new Dimension(-1, 25), new Dimension(-1, 25), 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hyperlinkLevel = new JXHyperlink();
        hyperlinkLevel.setText("-");
        panel15.add(hyperlinkLevel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final FlatLabel flatLabel2 = new FlatLabel();
        flatLabel2.setText("Nivel:");
        panel15.add(flatLabel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 3, -1));
        panel14.add(panel16, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelDate = new FlatLabel();
        labelDate.setText("Finaliza:");
        panel16.add(labelDate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hyperlinkEnd = new JXHyperlink();
        hyperlinkEnd.setText("-");
        panel16.add(hyperlinkEnd, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel17, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hyperlinkPartial = new JXHyperlink();
        hyperlinkPartial.setText("-");
        panel17.add(hyperlinkPartial, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel14.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelProgress = new JPanel();
        panelProgress.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panelProgress, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelProgress = new FlatLabel();
        labelProgress.setText("-");
        panelProgress.add(labelProgress, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final FlatLabel flatLabel3 = new FlatLabel();
        flatLabel3.setIcon(new ImageIcon(getClass().getResource("/com/babasdevel/views/loading.gif")));
        flatLabel3.setVisible(false);
        panelProgress.add(flatLabel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel18, BorderLayout.CENTER);
        tabbed = new FlatTabbedPane();
        tabbed.setTabsClosable(true);
        panel18.add(tabbed, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
