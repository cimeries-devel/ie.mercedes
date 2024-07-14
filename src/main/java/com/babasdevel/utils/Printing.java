package com.babasdevel.utils;

import com.babasdevel.controllers.*;
import com.babasdevel.mercedes.Application;
import com.babasdevel.models.*;
import com.babasdevel.views.Dashboard;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.Lists;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.List;

public class Printing {
    private JasperPrint print;
    private List<JasperPrint> prints;
    private InputStream input;
    private final Dashboard dashboard;
    private JRPdfExporter exporter;
    private int type;
    public Printing(Dashboard dashboard){
        this.dashboard = dashboard;
    }
    public void setData(Grade grade, List<Course> courses, Long partial, Student student, Tutor tutor){
        if (dashboard.teacherAuth.level.getLevel().equalsIgnoreCase("primaria")){
            setDataPrimary(grade, courses, partial, List.of(student), tutor, 1);
        } else {
            setDataSecondary(grade, courses, partial, List.of(student), tutor, 0);
        }
    }
    public void setData(Grade grade, List<Course> courses, Long partial, List<Student> students, Tutor tutor){
        if (dashboard.teacherAuth.level.getLevel().equalsIgnoreCase("primaria")){
            setDataPrimary(grade, courses, partial, students, tutor, 1);
        } else {
            setDataSecondary(grade, courses, partial, students, tutor, 1);
        }
    }
    private void setDataPrimary(Grade grade, List<Course> courses, Long partial, List<Student> students, Tutor tutor, int type){
        this.type = type;
        prints = new ArrayList<>();
        for (Student student : students) {
            List<ModelReport> objectsReport = new ArrayList<>();
            input = Application.class.getResourceAsStream("/com/babasdevel/views/jasper/A4.jasper");
            Map<String, Object> params = new HashMap<>();
            params.put("title", String.format(
                    "BOLETA DE INFORMACIÓN DEL ESTUDIANTE - %s BIMESTRE %s",
                    "1ER",
                    "2023"));
            params.put("logo_education", Application.class.getResource("/com/babasdevel/views/mde.png").toExternalForm());
            params.put("logo_school", Application.class.getResource("/com/babasdevel/views/logo.png").toExternalForm());
            params.put("dre", "Madre de Dios");
            params.put("ugel", "Tambopata");
            params.put("level", dashboard.teacherAuth.level.getLevel());
            params.put("code_module", "0206391-0");
            params.put("school", "52005 \"NUESTRA SEÑORA DE LAS MERCEDES\"");
            params.put("grade", grade.getClassroom().getClassroom());
            params.put("section", grade.getSection().getSection());
            params.put("student", String.format(
                    "%s %s, %s",
                    student.getLastNameFather(),
                    student.getLastNameMother(),
                    student.getFirstName()));
            params.put("teacher", String.format("%s %s",
                    tutor.getTeacher().getFirstName(),
                    tutor.getTeacher().getLastName()));

            ControllerPartial controllerPartial = new ControllerPartial();
            ControllerNote controllerNote = new ControllerNote();
            ControllerCargo controllerCargo = new ControllerCargo();
            for (Course course : courses) {
                ModelReport model = new ModelReport();
                model.setCourse(course.getName());
                Partial p = controllerPartial.get(partial, course);
                Cargo cargo = controllerCargo.get(grade, course, true);
                if (cargo == null) controllerCargo.downloadCargo(dashboard.teacherAuth, grade, course);
                Teacher teacher = controllerCargo.get(grade, course, true).getTeacher();
                for (Skill skill : course.getSkills()) {
                    Note note = controllerNote.get(grade, course, skill, p, teacher, student, dashboard.teacherAuth.level);
                    switch (skill.getIndex()){
                        case 1:
                            model.setSkill_1(skill.getName());
                            model.setNote_1(note==null?"-":note.getNote());
                            model.setObservation_1(note==null?"-":note.getObservation());
                            break;
                        case 2:
                            model.setSkill_2(skill.getName());
                            model.setNote_2(note==null?"-":note.getNote());
                            model.setObservation_2(note==null?"-":note.getObservation());
                            break;
                        case 3:
                            model.setSkill_3(skill.getName());
                            model.setNote_3(note==null?"-":note.getNote());
                            model.setObservation_3(note==null?"-":note.getObservation());
                            break;
                        case 4:
                            model.setSkill_4(skill.getName());
                            model.setNote_4(note==null?"-":note.getNote());
                            model.setObservation_4(note==null?"-":note.getObservation());
                            break;
                        default:
                            model.setSkill_5(skill.getName());
                            model.setNote_5(note==null?"-":note.getNote());
                            model.setObservation_5(note==null?"-":note.getObservation());
                    }
                }
                objectsReport.add(model);
            }
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(objectsReport);
            params.put("data", dataSource);
            try {
                if (type == 0){
                    JasperReport report = (JasperReport) JRLoader.loadObject(input);
                    print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
                } else {
                    JasperReport report = (JasperReport) JRLoader.loadObject(input);
                    print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
                    prints.add(print);
                }
            } catch (JRException e){
                e.printStackTrace();
            }
        }
    }
    private void setDataSecondary(Grade grade, List<Course> courses, Long partial, List<Student> students, Tutor tutor, int type){
        this.type = type;
        prints = new ArrayList<>();
        for (Student student : students) {
            input = Application.class.getResourceAsStream("/com/babasdevel/views/jasper/A4.jasper");
            Map<String, Object> params = new HashMap<>();
            params.put("logo_mde", Application.class.getResource("/com/babasdevel/views/mde.png").toExternalForm());
            params.put("logo_school", Application.class.getResource("/com/babasdevel/views/logo.png").toExternalForm());
            params.put("dre", "DRE MADRE DE DIOS");
            params.put("ugel", "UGEL Tambopata");
            params.put("nivel", dashboard.teacherAuth.level.getLevel());
            params.put("code_module", "0688283-0");
            params.put("school", "Nuestra Señora de las Mercedes");
            params.put("grade", grade.getClassroom().getClassroom());
            params.put("section", grade.getSection().getSection());
            params.put("student", String.format(
                    "%s %s, %s",
                    student.getLastNameFather(),
                    student.getLastNameMother(),
                    student.getFirstName()));
            params.put("code_student", student.getCodeStudent());
            params.put("dni", student.getNumberDocument());
            params.put("tutor", String.format("%s %s",
                    tutor.getTeacher().getFirstName(),
                    tutor.getTeacher().getLastName()));

            ControllerPartial controllerPartial = new ControllerPartial();
            ControllerNote controllerNote = new ControllerNote();
            ControllerCargo controllerCargo = new ControllerCargo();
            for (Course course : courses) {
                Cargo cargo = controllerCargo.get(grade, course, true);
                if (cargo == null) controllerCargo.downloadCargo(dashboard.teacherAuth, grade, course);
                Teacher teacher = controllerCargo.get(grade, course, true).getTeacher();
                params.put("course_"+course.getId(), course.getName());
                Partial p = controllerPartial.get(partial, course);
                for (int index = 0; index < course.getSkills().size(); index++){
                    Skill skill = course.getSkills().get(index);
                    Note note = controllerNote.get(grade, course, skill, p, teacher, student, dashboard.teacherAuth.level);
                    switch (course.getSkills().get(index).getIndex()){
                        case 1:
                            params.put("skill_c"+course.getId()+"_"+1, skill.getName());
                            params.put("nl_c"+course.getId()+"_"+1, note==null?"":note.getNote());
                            params.put("cd_c"+course.getId()+"_"+1, note==null?"":note.getObservation());
                            break;
                        case 2:
                            params.put("skill_c"+course.getId()+"_"+2, skill.getName());
                            params.put("nl_c"+course.getId()+"_"+2, note==null?"":note.getNote());
                            params.put("cd_c"+course.getId()+"_"+2, note==null?"":note.getObservation());
                            break;
                        case 3:
                            params.put("skill_c"+course.getId()+"_"+3, skill.getName());
                            params.put("nl_c"+course.getId()+"_"+3, note==null?"":note.getNote());
                            params.put("cd_c"+course.getId()+"_"+3, note==null?"":note.getObservation());
                            break;
                        case 4:
                            params.put("skill_c"+course.getId()+"_"+4, skill.getName());
                            params.put("nl_c"+course.getId()+"_"+4, note==null?"":note.getNote());
                            params.put("cd_c"+course.getId()+"_"+4, note==null?"":note.getObservation());
                        default:
                            params.put("skill_c"+course.getId()+"_"+5, skill.getName());
                            params.put("nl_c"+course.getId()+"_"+5, note==null?"":note.getNote());
                            params.put("cd_c"+course.getId()+"_"+5, note==null?"":note.getObservation());
                    }
                }
            }
            try {
                if (type == 0){
                    JasperReport report = (JasperReport) JRLoader.loadObject(input);
                    print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
                } else {
                    JasperReport report = (JasperReport) JRLoader.loadObject(input);
                    print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
                    prints.add(print);
                }
            } catch (JRException e){
                e.printStackTrace();
            }
        }
    }
    public void preparePdf(){
        exporter = new JRPdfExporter();
        exporter.setExporterInput(type==0?
                new SimpleExporterInput(print):
                SimpleExporterInput.getInstance(prints));
    }
    public boolean savePdf(String namePdf){
        boolean status = false;
        String extension = "pdf";
        File file = new File(namePdf);
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(file);
        FileNameExtensionFilter filterExcel = new FileNameExtensionFilter("Formato de documento portátil (.pdf)",extension);
        chooser.addChoosableFileFilter(filterExcel);
        chooser.setFileFilter(filterExcel);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        int select = chooser.showSaveDialog(dashboard);
        if (select == JFileChooser.APPROVE_OPTION){
            FileSystem fs = FileSystems.getDefault();
            file = chooser.getSelectedFile();
            String name = FileNameUtils.getBaseName(file.getName());
            name = name+"."+extension;
            file = new File(
                    String.format("%s%s%s",
                            file.getParentFile().getAbsolutePath(),
                            fs.getSeparator(),
                            name)
            );
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setMetadataTitle("Matricula");
            configuration.setMetadataAuthor("Ronald Huamani Ortega");
            configuration.setMetadataCreator("AIP - Nuestra Señora de las Mercedes");
            configuration.setMetadataSubject("Reporte de matricula");
            exporter.setConfiguration(configuration);
            try {
                exporter.exportReport();
            } catch (JRException ignore){
            }
            status = true;
        }
        return status;
    }
    public JasperPrint getViewer(){
        if (type != 0) {
            print = null;
            for (JasperPrint jasperPrint : prints) {
                if (print == null) {
                    print = jasperPrint;
                    continue;
                }
                print.getPages().addAll(jasperPrint.getPages());
            }
        }
        return print;
    }
}
