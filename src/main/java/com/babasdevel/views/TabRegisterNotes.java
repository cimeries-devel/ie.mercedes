package com.babasdevel.views;

import com.babasdevel.cimeries.*;
import com.babasdevel.components.*;
import com.babasdevel.controllers.*;
import com.babasdevel.gallery.Icons;
import com.babasdevel.models.*;
import com.babasdevel.utils.Excel;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.icons.FlatDescendingSortIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class TabRegisterNotes extends Tab {
    private JPanel panel;
    private FlatComboBox<Grade> comboGrade;
    private FlatTable table;
    private FlatComboBox<Course> comboCourse;
    private JXHyperlink hyperlinkCount;
    private JXSearchField fieldSearch;
    private JXHyperlink hyperlinkTeacher;
    private final Dashboard dashboard;
    private TableModel.ModelStudent modelStudent;
    private TableRowSorter<TableModel.ModelStudent> sorter;
    private ControllerGrade controllerGrade;
    private ControllerCourse controllerCourse;
    private ControllerStudent controllerStudent;
    private ControllerCargo controllerCargo;
    private DefaultComboBoxModel<Grade> comboModelGrade;
    private SwingWorker<Integer, Integer> workerTeacher;
    private FlatComboBox<String> comboNotes;
    private FlatComboBox<String> comboInit;

    public TabRegisterNotes(FlatToggleButton button, Dashboard dashboard) {
        this.dashboard = dashboard;
        super.initialize(panel, button.getText(), Icons.HOME_WELCOME, button);
        this.initialize();
    }

    private void initialize() {
//        panel.registerKeyboardAction(
//                this::processPressKeys,
//                "D",
//                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
//                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        panel.registerKeyboardAction(
//                this::processPressKeys,
//                "A",
//                KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
//                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        panel.registerKeyboardAction(
//                this::processPressKeys,
//                "B",
//                KeyStroke.getKeyStroke(KeyEvent.VK_B, 0),
//                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        panel.registerKeyboardAction(
//                this::processPressKeys,
//                "C",
//                KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),
//                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        panel.registerKeyboardAction(
//                this::processPressKeys,
//                "DELETE",
//                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
//                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        controllerGrade = new ControllerGrade();
        controllerCourse = new ControllerCourse();
        controllerStudent = new ControllerStudent();
        controllerCargo = new ControllerCargo();
        comboModelGrade = new DefaultComboBoxModel<>();
        comboGrade.setModel(comboModelGrade);
        comboGrade.setRenderer(new ComboRender());
        comboGrade.setActionCommand("grade");
        comboGrade.addActionListener(this::loadCombo);

        comboNotes = new FlatComboBox<>();
        comboNotes.addItem("AD");
        comboNotes.addItem("A");
        comboNotes.addItem("B");
        comboNotes.addItem("C");

        table.setTableHeader(new GroupableTableHeader(table.getColumnModel()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::changedTable);

        fieldSearch.addActionListener(this::filter);

        comboCourse.setActionCommand("course");
        comboCourse.addActionListener(this::loadCombo);

        comboInit = new FlatComboBox<>();
        comboInit.setActionCommand("init_notes");
        addComponentToolbar(comboInit);

        addComponentToolbar(new FlatToolBarSeparator());

        FlatButton buttonExcel = new FlatButton();
        buttonExcel.setText("Excel");
        buttonExcel.setIcon(new FlatDescendingSortIcon());
        buttonExcel.addActionListener(event -> {
            FlatPopupMenu popupMenuExcel = new FlatPopupMenu();
            FlatMenuItem menuItemTemplate = new FlatMenuItem();
            menuItemTemplate.setActionCommand("download_template");
            menuItemTemplate.setText("Descargar plantilla");
            menuItemTemplate.addActionListener(this::actionsToolbar);
            popupMenuExcel.add(menuItemTemplate);

            FlatMenuItem menuItemSendNotes = new FlatMenuItem();
            menuItemSendNotes.setActionCommand("send_template");
            menuItemSendNotes.setText("Subir notas");
            menuItemSendNotes.addActionListener(this::actionsToolbar);
            popupMenuExcel.add(menuItemSendNotes);

            FlatMenuItem menuItemGetConsolidate = new FlatMenuItem();
            menuItemGetConsolidate.setActionCommand("generate_consolidate");
            menuItemGetConsolidate.setText("Obtener consolidado");
            menuItemGetConsolidate.addActionListener(this::actionsToolbar);
            popupMenuExcel.add(menuItemGetConsolidate);

            popupMenuExcel.show(
                    (Component) event.getSource(),
                    buttonExcel.getX(),
                    buttonExcel.getY() + buttonExcel.getHeight() - 3);
        });
        addComponentToolbar(buttonExcel);

        if (controllerCargo.allGrades(dashboard.teacherAuth, true).isEmpty()) {
            downloadData();
        } else {
            if (dashboard.teacherAuth.getSuperuser()) {
                comboModelGrade.addAll(controllerGrade.all(dashboard.teacherAuth.level));
            } else {
                comboModelGrade.addAll(controllerCargo.allGrades(dashboard.teacherAuth, true));
            }
            comboGrade.setSelectedIndex(0);
        }

        addComponentToolbar(new FlatToolBarSeparator());

        FlatButton buttonPut = new FlatButton();
        buttonPut.setIcon(Icons.ICON_DOCUMENT_SEND);
        buttonPut.setText("Guardar");
        buttonPut.setActionCommand("save_notes");
        buttonPut.addActionListener(this::actionsToolbar);
        addComponentToolbar(buttonPut);

        FlatButton buttonRefreshNotes = new FlatButton();
        buttonRefreshNotes.setIcon(Icons.ICON_UPDATE_LOW);
        buttonRefreshNotes.setActionCommand("update_notes");
        buttonRefreshNotes.addActionListener(this::actionsToolbar);
        addComponentToolbar(buttonRefreshNotes);
    }

    private void processPressKeys(ActionEvent event) {
        int index = table.getSelectedRow();
        if (index == -1) return;
        index = table.convertRowIndexToModel(index);
        Student student = modelStudent.students.get(index);
        index = table.getSelectedColumn();
        switch (event.getActionCommand()) {
            case "D":
                switch (index) {
                    case 4:
                        student.nl_1 = "AD";
                        break;
                    case 6:
                        student.nl_2 = "AD";
                        break;
                    case 8:
                        student.nl_3 = "AD";
                        break;
                    default:
                        student.nl_4 = "AD";
                }
                break;
            case "A":
                switch (index) {
                    case 4:
                        student.nl_1 = "A";
                        break;
                    case 6:
                        student.nl_2 = "A";
                        break;
                    case 8:
                        student.nl_3 = "A";
                        break;
                    default:
                        student.nl_4 = "A";
                }
                break;
            case "B":
                switch (index) {
                    case 4:
                        student.nl_1 = "B";
                        break;
                    case 6:
                        student.nl_2 = "B";
                        break;
                    case 8:
                        student.nl_3 = "B";
                        break;
                    default:
                        student.nl_4 = "B";
                }
                break;
            case "C":
                switch (index) {
                    case 4:
                        student.nl_1 = "C";
                        break;
                    case 6:
                        student.nl_2 = "C";
                        break;
                    case 8:
                        student.nl_3 = "C";
                        break;
                    default:
                        student.nl_4 = "C";
                }
                break;
            default:
                switch (index) {
                    case 4:
                        student.nl_1 = "";
                        student.cd_1 = "";
                        break;
                    case 6:
                        student.nl_2 = "";
                        student.cd_2 = "";
                        break;
                    case 8:
                        student.nl_3 = "";
                        student.cd_3 = "";
                        break;
                    case 10:
                        student.nl_4 = "";
                        student.cd_4 = "";
                        break;
                    default:
                        student.nl_1 = "";
                        student.cd_1 = "";
                        student.nl_2 = "";
                        student.cd_2 = "";
                        student.nl_3 = "";
                        student.cd_3 = "";
                        student.nl_4 = "";
                        student.cd_4 = "";
                }
        }
        table.updateUI();
    }

    private void actionsToolbar(ActionEvent event) {
        ControllerNote controllerNote = new ControllerNote();
        Grade grade = (Grade) comboGrade.getSelectedItem();
        Course course = (Course) comboCourse.getSelectedItem();
        comboNotes.setSelectedItem(comboNotes.getSelectedItem());
        switch (event.getActionCommand()) {
            case "save_notes":
                if (table.isEditing()) table.getCellEditor().stopCellEditing();
                table.clearSelection();

                ControllerSkill controllerSkill = new ControllerSkill();
                ControllerPartial controllerPartial = new ControllerPartial();
                List<NoteModel> data = new ArrayList<>();
                Teacher teacher = dashboard.teacherAuth;
                if (dashboard.teacherAuth.getSuperuser()) {
                    teacher = controllerCargo.get(grade, course, true).getTeacher();
                }
                Partial partial = controllerPartial.get(dashboard.permission.getPartial(), course);
                for (Student student : modelStudent.students) {
                    List<Skill> s = controllerSkill.getSkills(course);
                    for (int index = 0; index < s.size(); index++) {
                        NoteModel note = new NoteModel();
                        assert grade != null;
                        note.grade = grade.getId();
                        assert course != null;
                        note.course = course.getId();
                        note.teacher = teacher.getId();
                        note.student = student.getId();
                        note.partial = partial.getId();
                        switch (index) {
                            case 0:
                                if (student.nl_1.isEmpty()) continue;
                                note.observation = student.cd_1;
                                note.note = student.nl_1;
                                note.skill = s.getFirst().getId();
                                break;
                            case 1:
                                if (student.nl_2.isEmpty()) continue;
                                note.observation = student.cd_2;
                                note.note = student.nl_2;
                                note.skill = s.get(1).getId();
                                break;
                            case 2:
                                if (student.nl_3.isEmpty()) continue;
                                note.observation = student.cd_3;
                                note.note = student.nl_3;
                                note.skill = s.get(2).getId();
                                break;
                            case 3:
                                if (student.nl_4.isEmpty()) continue;
                                note.observation = student.cd_4;
                                note.note = student.nl_4;
                                note.skill = s.get(3).getId();
                                break;
                            default:
                                if (student.nl_5.isEmpty()) continue;
                                note.observation = student.cd_5;
                                note.note = student.nl_5;
                                note.skill = s.get(4).getId();
                        }
                        data.add(note);
                    }
                }
                if (data.isEmpty()) return;
                for (NoteModel nm : data) {
                    if (!nm.observation.isEmpty() && (nm.observation.length() > 64 || nm.observation.length() < 11)) {
                        JOptionPane.showMessageDialog(dashboard,
                                "La conclusión descriptiva ingresada, no es válida.\nEl valor ingresado, debe contener mínimo 11 y máximo 64 carácteres",
                                "Cambios no guardados",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (nm.note.equals("C") && nm.observation.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dashboard,
                                "Una o más notas requieren conclusión descriptiva",
                                "Cambios no guardados",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (dashboard.teacherAuth.level.getLevel().equalsIgnoreCase("Primaria") && nm.note.equals("B") && nm.observation.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dashboard,
                                "Una o más notas requieren conclusión descriptiva",
                                "Cambios no guardados",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                int code = controllerNote.post(dashboard.teacherAuth, data);
                if (code == Codes.CODE_CREATED) {
                    JOptionPane.showMessageDialog(dashboard,
                            "Las notas, ha sido registrado correctamente"
                    );
                } else if (code == Codes.NOT_UPDATE_REGISTER) {
                    JOptionPane.showMessageDialog(dashboard,
                            "La fecha para registar sus notas, ha finalizado",
                            "Registro de notas",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                break;
            case "update_notes":
                controllerNote.downloadData(dashboard.teacherAuth, grade, course);
                modelStudent.updateData();
                table.updateUI();
                break;
            case "download_template":
                Excel excel = new Excel(dashboard);
                excel.createTemplateForTeacher();
                String name = String.format("Cargo %s %s", dashboard.teacherAuth.getLastName(), dashboard.teacherAuth.getFirstName());
                if (excel.saveBook(name.replace(" ", "_"))) {
                    JOptionPane.showMessageDialog(dashboard,
                            "Plantilla de excel generado correctamente.",
                            "Generar plantilla",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "send_template":
                excel = new Excel(dashboard);
                File file = excel.loadBook();
                if (file != null) excel.sendNotesOfTemplate(file, dashboard.teacherAuth.level);
                break;
            case "generate_consolidate":
                break;
            case "init_notes":
                int index = comboInit.getSelectedIndex();
                if (index == 0 || index == -1) return;
                String[] value = JOptionPane.showInputDialog(dashboard,
                        "Ingrese la competencia",
                        "Competencia",
                        JOptionPane.INFORMATION_MESSAGE).split(":");
                switch (index) {
                    case 1:
                        for (Student student : modelStudent.students)
                            if (student.nl_1.equalsIgnoreCase(value[0])) student.cd_1 = value[1];
                        break;
                    case 2:
                        for (Student student : modelStudent.students)
                            if (student.nl_2.equalsIgnoreCase(value[0])) student.cd_2 = value[1];
                        break;
                    case 3:
                        for (Student student : modelStudent.students)
                            if (student.nl_3.equalsIgnoreCase(value[0])) student.cd_3 = value[1];
                        break;
                    case 4:
                        for (Student student : modelStudent.students)
                            if (student.nl_4.equalsIgnoreCase(value[0])) student.cd_4 = value[1];
                        break;
                    default:
                        for (Student student : modelStudent.students)
                            if (student.nl_5.equalsIgnoreCase(value[0])) student.cd_5 = value[1];
                }
                table.updateUI();
                comboInit.setSelectedIndex(0);
                break;
            default://report combo toolbar
                System.out.println("sdfsdfsdfsd tooblar");
        }
    }

//    @Override
//    public void update() {
////        ClientGrade clientGrade = new ClientGrade();
////        dashboard = (Dashboard) SwingUtilities.getWindowAncestor(this);
////        int code = clientGrade.getGrades(dashboard.teacherAuth);
////        if (code == Codes.CODE_SUCCESS){
////            comboGrade.setModel(new DefaultComboBoxModel<>(new Vector<>(clientGrade.data)));
////            comboGrade.setRenderer(new ComboRender());
////            comboGrade.setSelectedIndex(0);
////        }
//    }

    private void loadCombo(ActionEvent event) {
        FlatComboBox comboBox = (FlatComboBox) event.getSource();
        if (comboBox.getSelectedIndex() == -1) return;

        Grade grade = (Grade) comboGrade.getSelectedItem();
        Course course = (Course) comboCourse.getSelectedItem();
        if (event.getActionCommand().equals("grade")) {
            DefaultComboBoxModel<Course> comboModelCourse = new DefaultComboBoxModel<>();
            if (dashboard.teacherAuth.getSuperuser()) {
                comboModelCourse.addAll(controllerCourse.all(dashboard.teacherAuth.level));
            } else {
                ControllerCargo controllerCargo = new ControllerCargo();
                if (controllerCargo.all(grade, dashboard.teacherAuth, true).isEmpty()) {
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    controllerTeacher.getOnlyMe(dashboard.teacherAuth);
                }
                comboModelCourse.addAll(controllerCargo.all(grade, dashboard.teacherAuth, true));
            }
            comboCourse.setModel(comboModelCourse);
            comboCourse.setRenderer(new ComboRender());
            if (comboModelCourse.getIndexOf(course) == -1) course = comboCourse.getItemAt(0);
            comboCourse.setSelectedItem(course);
        } else {
            comboInit.addActionListener(this::actionsToolbar);
            comboInit.removeAllItems();
            comboInit.addItem("Inicializar competencias");
            for (Skill skill : course.getSkills()) comboInit.addItem(skill.getIndex() + " competencia");
            if (controllerStudent.all(grade).isEmpty()) {
                downloadStudents(grade, course);
            } else {
                downloadNotes(grade, course);
            }
        }
    }

    private void changedTable(ListSelectionEvent event) {
        int index = table.getSelectedRow();
        if (index == -1) return;

//        System.out.println("index :"+table.convertRowIndexToModel(index));
//        index = table.convertRowIndexToView(index);
    }

    private void customTable(List<Student> students) {
        List<String> columns = new ArrayList<>(Arrays.asList(
                "Id",
                "Document",
                "Número documento",
                "Código",
                "Apellidos",
                "Nombre",
                "Genero",
                "Fecha nacimiento"));
        List<Class> columnsTypes = new ArrayList<>(Arrays.asList(
                Integer.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                Date.class
        ));
        ControllerPartial controllerPartial = new ControllerPartial();
        ControllerSkill controllerSkill = new ControllerSkill();
        Course course = (Course) comboCourse.getSelectedItem();
        List<Skill> skills = controllerSkill.all(course);
        for (Skill skill : skills) {
            int num = skills.indexOf(skill);
            columns.add(String.format("NL %d", ++num));
            columns.add(String.format("Conclusión %d", num));
            columnsTypes.add(String.class);
            columnsTypes.add(String.class);
        }
        Grade grade = (Grade) comboGrade.getSelectedItem();
        Teacher teacher = dashboard.teacherAuth;
        if (teacher.getSuperuser()) {
            ControllerCargo controllerCargo = new ControllerCargo();
            Cargo cargo = controllerCargo.get(grade, course, true);

            if (cargo == null) {
                ControllerTeacher controllerTeacher = new ControllerTeacher();
                controllerTeacher.getOnlyMe(teacher, grade, course);
                cargo = controllerCargo.get(grade, course, true);
            }
            teacher = cargo.getTeacher();
        }
        assert grade != null;
        modelStudent = new TableModel.ModelStudent(
                columns,
                columnsTypes,
                students,
                grade,
                course,
                controllerPartial.get(dashboard.permission.getPartial(), course),
                teacher,
                dashboard.teacherAuth.level,
                dashboard.teacherAuth.getSuperuser());
        downloadTeacherCourse(grade, course);

        table.setRowSorter(null);
        table.setModel(modelStudent);
        sorter = new TableRowSorter<>(modelStudent);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.ASCENDING)));
        table.setRowSorter(sorter);

        TableColumn columnId = table.getColumn("Id");
        table.removeColumn(columnId);
        TableColumn columnDocument = table.getColumn("Document");
        table.removeColumn(columnDocument);
        TableColumn columnGender = table.getColumn("Genero");
        table.removeColumn(columnGender);
        TableColumn columnBirth = table.getColumn("Fecha nacimiento");
        table.removeColumn(columnBirth);
        TableColumn columnNumber = table.getColumn("Número documento");
        columnNumber.setPreferredWidth(135);
        columnNumber.setMaxWidth(135);
        TableColumn columnCode = table.getColumn("Código");
        columnCode.setPreferredWidth(130);
        columnCode.setMaxWidth(130);

        GroupableTableHeader header = (GroupableTableHeader) table.getTableHeader();
        for (int col = 1; col <= skills.size(); col++) {
            ColumnGroup group = new ColumnGroup("Competencia " + col);
            TableColumn columnNl = table.getColumn("NL " + col);
            columnNl.setCellEditor(new DefaultCellEditor(comboNotes));
            columnNl.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                JXHyperlink hyper = new JXHyperlink();
                hyper.setText(value.toString());
                hyper.setHorizontalAlignment(JXHyperlink.CENTER);
                hyper.setOpaque(isSelected);
                hyper.setForeground(table.getForeground());
                if (isSelected) {
                    hyper.setForeground(table.getSelectionForeground());
                    hyper.setBackground(table.getSelectionBackground());
                }
                return hyper;
            });
            columnNl.setPreferredWidth(65);
            columnNl.setMaxWidth(65);
            group.add(columnNl);
            TableColumn columnC = table.getColumn("Conclusión " + col);
            columnC.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {

                int len = value.toString().trim().length();
                JLabel label = new JLabel(value.toString());

                label.setOpaque(isSelected);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                }
                if (len > 0 && len <= 11 || len > 64) {
                    label.setForeground(Color.RED);
                    label.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
                return label;
            });
            group.add(columnC);
            header.addColumnGroup(group);
        }

        String search = fieldSearch.getText().trim();
        if (search.isEmpty()) countRegisters();
        if (!search.isEmpty()) filter(null);
    }

    private void filter(ActionEvent event) {
        String search = fieldSearch.getText().toLowerCase().trim();
        RowFilter<TableModel.ModelStudent, Integer> filterStudent = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends TableModel.ModelStudent, ? extends Integer> entry) {
                Student student = modelStudent.students.get(entry.getIdentifier());
                String full_name = student.getFirstName() + student.getLastNameFather() + student.getLastNameMother();
                return full_name.toLowerCase().contains(search);
            }
        };
        sorter.setRowFilter(filterStudent);
        countRegisters();
    }

    private void countRegisters() {
        hyperlinkCount.setText(String.valueOf(table.getRowCount()));
    }

    private void downloadData() {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected void done() {
                if (dashboard.teacherAuth.getSuperuser()) {
                    comboModelGrade.addAll(controllerGrade.all(dashboard.teacherAuth.level));
                } else {
                    comboModelGrade.addAll(controllerCargo.allGrades(dashboard.teacherAuth, true));
                }
                dashboard.getPanelProgress().setVisible(false);
                dashboard.generateMenuReport();
                comboGrade.setSelectedIndex(0);
                super.done();
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                FlatLabel label = (FlatLabel) dashboard.getPanelProgress().getComponent(0);
                label.setText("Obteniendo información de los grados");
                controllerGrade.downloadData(dashboard.teacherAuth);

                publish(30);
                label.setText("Obteniendo información de los cursos");
                controllerCourse.downloadData(dashboard.teacherAuth);

                publish(70);
                label.setText("Obteniendo información del docente a cargo");
                if (controllerCargo.all(dashboard.teacherAuth, true).isEmpty()) {
                    ControllerTeacher controllerTeacher = new ControllerTeacher();
                    controllerTeacher.getOnlyMe(dashboard.teacherAuth);
                }

                publish(100);
                return 100;
            }
        };
        worker.execute();
    }

    private void downloadStudents(Grade grade, Course course) {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected void done() {
                dashboard.getPanelProgress().setVisible(false);
                downloadNotes(grade, course);
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                FlatLabel label = (FlatLabel) dashboard.getPanelProgress().getComponent(0);
                label.setText("Obteniendo lista de estudiantes");
                ControllerRegistration controllerRegistration = new ControllerRegistration();
                controllerRegistration.downloadData(dashboard.teacherAuth, grade);
                return 100;
            }
        };
        worker.execute();
    }

    private void downloadNotes(Grade grade, Course course) {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected void done() {
                dashboard.getPanelProgress().setVisible(false);
                customTable(controllerStudent.all(grade));
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                FlatLabel label = (FlatLabel) dashboard.getPanelProgress().getComponent(0);
                label.setText("Obteniendo notas de los estudiantes");
                ControllerNote controllerNote = new ControllerNote();
                if (controllerNote.all(grade, course, dashboard.teacherAuth.level).isEmpty())
                    controllerNote.downloadData(dashboard.teacherAuth, grade, course);
                return 100;
            }
        };
        worker.execute();
    }

    private void downloadTeacherCourse(Grade grade, Course course) {
        workerTeacher = new SwingWorker<>() {
            @Override
            protected void done() {
                dashboard.getPanelProgress().setVisible(false);
                Cargo cargo = controllerCargo.get(grade, course, true);
                hyperlinkTeacher.setText(cargo.getTeacher().getFullName());
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                Cargo cargo = controllerCargo.get(grade, course, true);
                if (cargo != null) return 100;
                ControllerTeacher controllerTeacher = new ControllerTeacher();
                controllerTeacher.getOnlyMe(dashboard.teacherAuth, grade, course);
                return 100;
            }
        };
        workerTeacher.execute();
    }

}
