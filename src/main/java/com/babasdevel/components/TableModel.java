package com.babasdevel.components;

import com.babasdevel.controllers.ControllerNote;
import com.babasdevel.controllers.ControllerSkill;
import com.babasdevel.models.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.text.WordUtils;

public class TableModel {
    public static class ModelStudent extends AbstractTableModel {
        public ControllerNote controllerNote;
        private final ControllerSkill controllerSkill;
        private Note note;
        private final Grade grade;
        public Course course;
        private final Partial partial;
        private final Teacher teacher;
        private final Level level;
        private final List<String> columns;
        private final List<Class> columnsTypes;
        private Student student;
        public List<Student> students;
        private final boolean isSuperuser;
        public ModelStudent(List<String> columns,
                            List<Class> columnTypes,
                            List<Student> students,
                            Grade grade,
                            Course course,
                            Partial partial,
                            Teacher teacher,
                            Level level,
                            boolean isSuperuser){
            controllerNote = new ControllerNote();
            controllerSkill = new ControllerSkill();
            this.isSuperuser = isSuperuser;
            this.grade = grade;
            this.course = course;
            this.partial = partial;
            this.teacher = teacher;
            this.level = level;
            this.columns = columns;
            this.columnsTypes = columnTypes;
            this.students = students;
            updateData();
        }
        @Override
        public int getRowCount(){
            return students.size();
        }
        @Override
        public int getColumnCount(){
            return columns.size();
        }
        @Override
        public String getColumnName(int col){
            return columns.get(col);
        }
        @Override
        public Class getColumnClass(int col){
            return columnsTypes.get(col);
        }
        @Override
        public Object getValueAt(int row, int col){

            student = students.get(row);
            switch (col){
                case 0:
                    return student.getId();
                case 1:
                    return student.getTypeDocument().equals("1")?"DNI":"-";
                case 2:
                    return student.getNumberDocument();
                case 3:
                    return student.getCodeStudent();
                case 4:
                    return String.format("%s %s", student.getLastNameFather(), student.getLastNameMother());
                case 5:
                    return student.getFirstName();
                case 6:
                    return student.getGender()?"Hombre":"Mujer";
                case 7:
                    return student.getBirthDate();
                default:
                    return switch (col) {
                        case 8 -> student.nl_1;
                        case 9 -> student.cd_1;
                        case 10 -> student.nl_2;
                        case 11 -> student.cd_2;
                        case 12 -> student.nl_3;
                        case 13 -> student.cd_3;
                        case 14 -> student.nl_4;
                        case 15 -> student.cd_4;
                        case 16 -> student.nl_5;
                        default -> student.cd_5;
                    };
            }
        }
        @Override
        public void setValueAt(Object value, int row, int column) {
            student = students.get(row);
            switch (column){
                case 8:
                    student.nl_1 = value.toString();
                    break;
                case 9:
                    student.cd_1 = WordUtils.capitalizeFully(value.toString());
                    int len = student.cd_1.trim().length();
                    if (len != 0)
                        if (len <= 11 || len > 64) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "La conclusión ingresa excede la longitud permitida.\nMínimo 11 y máximo 64 carácteres",
                                    "Error en el valor ingresado",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    break;
                case 10:
                    student.nl_2 = value.toString();
                    break;
                case 11:
                    student.cd_2 = WordUtils.capitalizeFully(value.toString());
                    break;
                case 12:
                    student.nl_3 = value.toString();
                    break;
                case 13:
                    student.cd_3 = WordUtils.capitalizeFully(value.toString());
                    break;
                case 14:
                    student.nl_4 = value.toString();
                    break;
                case 15:
                    student.cd_4 = WordUtils.capitalizeFully(value.toString());
                    break;
                case 16:
                    student.nl_5 = value.toString();
                    break;
                default:
                    student.cd_5 = WordUtils.capitalizeFully(value.toString());
            }
        }
        @Override
        public boolean isCellEditable(int row, int col){
            return col >= 8;
        }
        private void getNote(int numberSkill, Student student){
            Skill skill = controllerSkill.get(course, numberSkill);
            note = controllerNote.get(grade, course, skill, partial, teacher, student, level);
        }
        public void updateData(){
            this.students.forEach(s -> {
                List<Skill> skills = controllerSkill.get(course);
                for (int index = 1; index <= skills.size(); index++) {
                    switch (index){
                        case 1:
                            getNote(1, s);
                            if (note != null) {
                                s.nl_1 = note.getNote();
                                s.cd_1 = note.getObservation();
                            } else {
                                s.nl_1 = "";
                                s.cd_1 = "";
                            }
                            break;
                        case 2:
                            getNote(2, s);
                            if (note != null) {
                                s.nl_2 = note.getNote();
                                s.cd_2 = note.getObservation();
                            } else {
                                s.nl_2 = "";
                                s.cd_2 = "";
                            }
                            break;
                        case 3:
                            getNote(3, s);
                            if (note != null) {
                                s.nl_3 = note.getNote();
                                s.cd_3 = note.getObservation();
                            } else {
                                s.nl_3 = "";
                                s.cd_3 = "";
                            }
                            break;
                        case 4:
                            getNote(4, s);
                            if (note != null) {
                                s.nl_4 = note.getNote();
                                s.cd_4 = note.getObservation();
                            } else {
                                s.nl_4 = "";
                                s.cd_4 = "";
                            }
                            break;
                        default:
                            getNote(5, s);
                            if (note != null) {
                                s.nl_5 = note.getNote();
                                s.cd_5 = note.getObservation();
                            } else {
                                s.nl_5 = "";
                                s.cd_5 = "";
                            }
                            break;
                    }
                }
            });
        }
    }
    public static class ModelStudentReport extends AbstractTableModel {
        private final List<String> columns = Arrays.asList(
                "Id",
                "Document",
                "Número documento",
                "Código",
                "Apellidos",
                "Nombre",
                "Género",
                "Fecha nacimiento");
        private final List<Class> columnsTypes = Arrays.asList(
                Integer.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                Date.class
        );
        public List<Student> students;
        public ModelStudentReport(List<Student> students){
            this.students = students;
        }
        @Override
        public int getRowCount(){
            return students.size();
        }
        @Override
        public int getColumnCount(){
            return columns.size();
        }
        @Override
        public String getColumnName(int col){
            return columns.get(col);
        }
        @Override
        public Class getColumnClass(int col){
            return columnsTypes.get(col);
        }
        @Override
        public Object getValueAt(int row, int col){
            Student student = students.get(row);
            switch (col){
                case 0:
                    return row+1;
                case 1:
                    return student.getTypeDocument().equals("1")?"DNI":"-";
                case 2:
                    return student.getNumberDocument();
                case 3:
                    return student.getCodeStudent();
                case 4:
                    return String.format("%s %s", student.getLastNameFather(), student.getLastNameMother());
                case 5:
                    return student.getFirstName();
                case 6:
                    return student.getGender()?"Hombre":"Mujer";
                default:
                    return student.getBirthDate();
            }
        }
    }
}
