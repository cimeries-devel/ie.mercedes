package com.babasdevel.views;

import com.babasdevel.components.*;
import com.babasdevel.controllers.*;
import com.babasdevel.gallery.Icons;
import com.babasdevel.models.*;
import com.babasdevel.utils.Excel;
import com.babasdevel.utils.Printing;
import com.formdev.flatlaf.extras.components.*;
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

public class TabRegisterNotesAdmin extends Tab {
    private JPanel panel;
    private FlatComboBox<Grade> comboGrade;
    private FlatTable table;
    private JXHyperlink hyperlinkCount;
    private JXSearchField fieldSearch;
    private final Dashboard dashboard;
    private TableModel.ModelStudentReport modelStudent;
    private TableRowSorter<TableModel.ModelStudentReport> sorter;
    private FlatComboBox<String> comboExport;
    private FlatButton buttonPdf;
    private FlatButton buttonView;
    private FlatButton buttonSiagie;
    private DefaultComboBoxModel<Grade> comboModelGrade;
    private ControllerCargo controllerCargo;
    private ControllerGrade controllerGrade;
    private ControllerStudent controllerStudent;

    public TabRegisterNotesAdmin(FlatToggleButton button, Dashboard dashboard) {
        this.dashboard = dashboard;
        super.initialize(panel, button.getText(), Icons.HOME_WELCOME, button);
        this.initialize();
    }

    private void initialize() {
        controllerCargo = new ControllerCargo();
        controllerGrade = new ControllerGrade();
        controllerStudent = new ControllerStudent();
        comboModelGrade = new DefaultComboBoxModel<>();

        if (controllerCargo.allGrades(dashboard.teacherAuth, true).isEmpty()) {
            downloadData();
        } else {
            comboModelGrade.addAll(controllerGrade.all(dashboard.teacherAuth.level));
            comboGrade.setSelectedIndex(0);
        }

        comboModelGrade.addAll(dashboard.grades);
        comboGrade.setModel(comboModelGrade);
        comboGrade.setRenderer(new ComboRender());
        comboGrade.setActionCommand("grade");
        comboGrade.addActionListener(this::actionsControlsTab);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::changedTable);
        fieldSearch.addActionListener(this::filter);

        FlatLabel labelTypeReport = new FlatLabel();
        labelTypeReport.setText("Libreta: ");
        addComponentToolbar(labelTypeReport);

        comboExport = new FlatComboBox<>();
        comboExport.setActionCommand("combo_export");
        comboExport.addItem("Por alumno");
        comboExport.addItem("Por grado");
        comboExport.setSelectedIndex(0);
        comboExport.addActionListener(this::actionsToolbar);
        addComponentToolbar(comboExport);

        buttonPdf = new FlatButton();
        buttonPdf.setIcon(Icons.ICON_PDF);
        buttonPdf.setActionCommand("button_generate_pdf");
        buttonPdf.addActionListener(this::actionsToolbar);
        buttonPdf.setEnabled(false);
        addComponentToolbar(buttonPdf);

        buttonView = new FlatButton();
        buttonView.setIcon(Icons.ICON_SIAGIE);
        buttonView.setActionCommand("button_view_pdf");
        buttonView.addActionListener(this::actionsToolbar);
        buttonView.setEnabled(false);
        addComponentToolbar(buttonView);

        addComponentToolbar(new FlatToolBarSeparator());

        buttonSiagie = new FlatButton();
        buttonSiagie.setIcon(Icons.ICON_PDF_VIEW);
        buttonSiagie.setActionCommand("button_siagie");
        buttonSiagie.addActionListener(this::actionsToolbar);
        addComponentToolbar(buttonSiagie);
    }

    @Override
    public void update() {
    }

    private void actionsControlsTab(ActionEvent event) {
        Grade grade = (Grade) comboGrade.getSelectedItem();
        assert grade != null;
        downloadStudents(grade);
    }

    private void generatedTable(List<Student> students) {
        modelStudent = new TableModel.ModelStudentReport(students);//corregir
        table.setRowSorter(null);
        sorter = new TableRowSorter<>(modelStudent);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.ASCENDING)));
        table.setModel(modelStudent);
        table.setRowSorter(sorter);
        TableColumn columnId = table.getColumn("Id");
        columnId.setPreferredWidth(50);
        columnId.setMaxWidth(50);
        TableColumn columnDocument = table.getColumn("Document");
        columnDocument.setPreferredWidth(90);
        columnDocument.setMaxWidth(90);
        TableColumn columnNumberDocument = table.getColumn("Número documento");
        columnNumberDocument.setPreferredWidth(150);
        columnNumberDocument.setMaxWidth(150);
        TableColumn columnCode = table.getColumn("Código");
        columnCode.setPreferredWidth(120);
        columnCode.setMaxWidth(120);
        String search = fieldSearch.getText().trim();
        if (search.isEmpty()) countRegisters();
        if (!search.isEmpty()) filter(null);
    }

    private void actionsToolbar(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "button_generate_pdf":
            case "button_view_pdf":
                processPDF(event);
                break;
            case "button_siagie":
                Excel excel = new Excel(dashboard);
                File[] files = excel.loadBooks();
                if (files != null) {
                    excel.insertNotes(files, dashboard.permission.getLevel());
                }
                break;
            default:
                FlatComboBox combo = (FlatComboBox) event.getSource();
                if (combo.getSelectedIndex() == 0) {
                    changedTable(null);
                } else {
                    buttonPdf.setEnabled(true);
                    buttonView.setEnabled(buttonPdf.isEnabled());
                }
        }
    }

    private void changedTable(ListSelectionEvent event) {
        int index = table.getSelectedRow();
        buttonPdf.setEnabled(index != -1);
        buttonView.setEnabled(buttonPdf.isEnabled());
//        if (buttonPDF.isEnabled()){
//            System.out.println(modelStudent.students.get(table.convertColumnIndexToView(index)).getFirst_name());
//            buttonPDF.setName(String.valueOf(table.convertRowIndexToModel(index)));
//        }
//        System.out.println("index :"+table.convertRowIndexToModel(index));
//        index = table.convertRowIndexToView(index);
    }

    private void filter(ActionEvent event) {
        String search = fieldSearch.getText().toLowerCase().trim();
        RowFilter<TableModel.ModelStudentReport, Integer> filterStudent = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends TableModel.ModelStudentReport, ? extends Integer> entry) {
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

    private void processPDF(ActionEvent event) {
        Grade grade = (Grade) comboGrade.getSelectedItem();
        int row = table.getSelectedRow();
        if (row == -1 && comboExport.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(dashboard, "Debe elegir un estudiante de la tabla");
            return;
        }
        ControllerCourse controllerCourse = new ControllerCourse();
        List<Course> courses = controllerCourse.all(dashboard.teacherAuth.level);

        ControllerTutor controllerTutor = new ControllerTutor();
        Cargo cargo = controllerCargo.get(grade, controllerCourse.get("TUTORIA", dashboard.teacherAuth.level), true);
        if (controllerTutor.get(grade, true) == null) controllerTutor.getTutorOnly(cargo.getTeacher(), grade);

        Tutor tutor = controllerTutor.get(grade, true);
        Printing printing = new Printing(dashboard);
        TabViewPDF viewPDF;

        String namePdf = "";
        if (comboExport.getSelectedIndex() == 0) {
            row = table.convertRowIndexToModel(row);
            Student student = modelStudent.students.get(row);
            namePdf = String.format(
                    "%s_%s_%s_%s",
                    student.getLastNameFather(),
                    student.getLastNameMother(),
                    student.getFirstName(),
                    student.getCodeStudent());
            printing.setData(
                    grade,
                    courses,
                    dashboard.permission.getPartial(),
                    student,
                    tutor);
        } else {
            namePdf = String.format("Libretas_%s", grade.getName());
            printing.setData(
                    grade,
                    courses,
                    dashboard.permission.getPartial(),
                    modelStudent.students,
                    tutor);
        }
        if (event.getActionCommand().equals("button_generate_pdf")) {
            printing.preparePdf();
            boolean isSave = printing.savePdf(namePdf.replaceAll(" ", "_"));
            if (isSave) {
                JOptionPane.showMessageDialog(dashboard,
                        "Libreta generado correctamente");
            }
        } else {
            viewPDF = new TabViewPDF(
                    namePdf.replaceAll(" ", "_"),
                    dashboard,
                    printing.getViewer(),
                    printing,
                    namePdf);
            dashboard.insertTab(viewPDF);
        }
    }

    private void downloadData() {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected void done() {
                comboModelGrade.addAll(controllerGrade.all(dashboard.teacherAuth.level));
                comboGrade.setSelectedIndex(0);
                super.done();
            }

            @Override
            protected Integer doInBackground() throws Exception {
                dashboard.getPanelProgress().setVisible(true);
                FlatLabel label = (FlatLabel) dashboard.getPanelProgress().getComponent(0);
                label.setText("Obteniendo información de los grados");
                controllerGrade.downloadData(dashboard.teacherAuth);

                dashboard.getPanelProgress().setVisible(false);
                return 100;
            }
        };
        worker.execute();
    }

    private void downloadStudents(Grade grade) {
        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected void done() {
                dashboard.getPanelProgress().setVisible(false);
                generatedTable(controllerStudent.all(grade));
                filter(null);
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

}
