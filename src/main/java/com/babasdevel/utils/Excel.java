package com.babasdevel.utils;

import com.babasdevel.controllers.*;
import com.babasdevel.models.*;
import com.babasdevel.views.Dashboard;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

public class Excel {
    private final XSSFWorkbook book;
    private List<Grade> grades;
    private Long numPartial;
    private final Dashboard dashboard;
    private final ControllerGrade controllerGrade;
    private final ControllerCargo controllerCargo;
    private final ControllerCourse controllerCourse;
    private final ControllerTeacher controllerTeacher;
    private final ControllerStudent controllerStudent;
    private final ControllerNote controllerNote;
    private final ControllerPartial controllerPartial;
    private final ControllerRegistration controllerRegistration;
    public Excel(Dashboard dashboard){
        this.dashboard = dashboard;
        book = new XSSFWorkbook();
        controllerGrade = new ControllerGrade();
        controllerCargo = new ControllerCargo();
        controllerCourse = new ControllerCourse();
        controllerRegistration = new ControllerRegistration();
        controllerTeacher = new ControllerTeacher();
        controllerStudent = new ControllerStudent();
        controllerNote = new ControllerNote();
        controllerPartial = new ControllerPartial();
    }
    public void createConsolidated(){
        createSchema();
        insetData();
    }
    public void createConsolidatedFull(){
        ControllerLevel controllerLevel = new ControllerLevel();
        Level level = controllerLevel.get(2L);
        for (Grade grade : controllerGrade.all(level)) {
            System.out.println(grade.getName());
        }
    }
    public void createTemplateForTeacher() {
        Font fontBold = book.createFont();
        fontBold.setBold(true);

        Font font = book.createFont();
        font.setBold(false);

        CellStyle style = book.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(fontBold);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        CellStyle styleStudent = book.createCellStyle();
        styleStudent.setWrapText(true);
        styleStudent.setAlignment(HorizontalAlignment.LEFT);
        styleStudent.setVerticalAlignment(VerticalAlignment.CENTER);
        styleStudent.setFont(font);
        styleStudent.setBorderBottom(BorderStyle.THIN);
        styleStudent.setBorderTop(BorderStyle.THIN);
        styleStudent.setBorderLeft(BorderStyle.THIN);
        styleStudent.setBorderRight(BorderStyle.THIN);

        CellStyle styleNoteUnLocked = book.createCellStyle();
        styleNoteUnLocked.setWrapText(true);
        styleNoteUnLocked.setLocked(false);
        styleNoteUnLocked.setAlignment(HorizontalAlignment.CENTER);
        styleNoteUnLocked.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNoteUnLocked.setFont(font);
        styleNoteUnLocked.setBorderBottom(BorderStyle.THIN);
        styleNoteUnLocked.setBorderTop(BorderStyle.THIN);
        styleNoteUnLocked.setBorderLeft(BorderStyle.THIN);
        styleNoteUnLocked.setBorderRight(BorderStyle.THIN);

        CellStyle styleNoteLocked = book.createCellStyle();
        styleNoteLocked.setWrapText(true);
        styleNoteLocked.setAlignment(HorizontalAlignment.CENTER);
        styleNoteLocked.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNoteLocked.setFont(font);
        styleNoteLocked.setBorderBottom(BorderStyle.THIN);
        styleNoteLocked.setBorderTop(BorderStyle.THIN);
        styleNoteLocked.setBorderLeft(BorderStyle.THIN);
        styleNoteLocked.setBorderRight(BorderStyle.THIN);

        CellStyle styleRotate = book.createCellStyle();
        styleRotate.setWrapText(true);
        styleRotate.setAlignment(HorizontalAlignment.CENTER);
        styleRotate.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRotate.setFont(fontBold);
        styleRotate.setBorderBottom(BorderStyle.THIN);
        styleRotate.setBorderTop(BorderStyle.THIN);
        styleRotate.setBorderLeft(BorderStyle.THIN);
        styleRotate.setBorderRight(BorderStyle.THIN);
//        styleRotate.setRotation((short) 90);

        CellStyle styleRotateBold = book.createCellStyle();
        styleRotateBold.setLocked(true);
        styleRotateBold.setWrapText(true);
        styleRotateBold.setAlignment(HorizontalAlignment.CENTER);
        styleRotateBold.setVerticalAlignment(VerticalAlignment.CENTER);
        styleRotateBold.setFont(fontBold);
        styleRotateBold.setBorderBottom(BorderStyle.THIN);
        styleRotateBold.setBorderTop(BorderStyle.THIN);
        styleRotateBold.setBorderLeft(BorderStyle.THIN);
        styleRotateBold.setBorderRight(BorderStyle.THIN);
//        styleRotateBold.setRotation((short) 90);

        for (Grade grade : controllerCargo.allGrades(dashboard.teacherAuth, true)) {
            int n = 4;
            int colEnd = 0;
            if (book.getSheetIndex(grade.getName()) != -1) continue;
            XSSFSheet sheet = book.createSheet(String.format("%d - %s", grade.getId(), grade.getName()));
            sheet.protectSheet("aip");
            sheet.createFreezePane(4, 0);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Institución Educativa:");
            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue("Nuestra señora de las mercedes");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,3));


            Row rowCourse = sheet.createRow(1);
            cell = rowCourse.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("Nivel:");
            cell = rowCourse.createCell(1);
            cell.setCellStyle(styleStudent);
            cell.setCellValue(dashboard.teacherAuth.level.getLevel());
            cell = rowCourse.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue("Curso:");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2,3));

            Row rowGrade = sheet.createRow(2);
            cell = rowGrade.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("Grado:");
            cell = rowGrade.createCell(1);
            cell.setCellStyle(styleStudent);
            cell.setCellValue(grade.getClassroom().getClassroom());
            cell = rowGrade.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue("Sección:");
            cell = rowGrade.createCell(3);
            cell.setCellStyle(styleStudent);
            cell.setCellValue(grade.getSection().getSection());

            int nn = n;
            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("Docente:");
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(dashboard.teacherAuth.getFullName());
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 1,3));

            for (Course course : controllerCargo.all(grade, dashboard.teacherAuth, true)) {
                cell = rowCourse.createCell(nn);
                cell.setCellStyle(style);
                cell.setCellValue(String.format("%s - %s", course.getName(), course.getAbbreviation()));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, nn, nn+course.getSkills().size()*2-1));
                nn += course.getSkills().size()*2;
                colEnd += course.getSkills().size()*2;
            }
            colEnd += 3;

            row = sheet.createRow(4);
//            row.setHeight((short) 3000);
            cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("Código");
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue("Apellidos y Nombres");
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1,3));

//            Row rowNlC = sheet.createRow(3);
            for (Course course : controllerCargo.all(grade, dashboard.teacherAuth, true)) {
                for (Skill skill : course.getSkills()) {
                    cell = rowGrade.createCell(n);
                    cell.setCellStyle(styleRotate);
                    cell.setCellValue(skill.getName());
                    sheet.addMergedRegion(new CellRangeAddress(2, 3, n, n+1));

                    Row rowNlC = sheet.getRow(4);
                    cell = rowNlC.createCell(n);
                    cell.setCellStyle(styleRotate);
                    cell.setCellValue("NL");
//                    sheet.addMergedRegion(new CellRangeAddress(4, 4, n, n));

                    cell = rowNlC.createCell(++n);
                    cell.setCellStyle(styleRotateBold);
                    cell.setCellValue("Conclusión descriptiva");
//                    sheet.addMergedRegion(new CellRangeAddress(4, 4, n, n));
                    sheet.setColumnWidth(n++, 9000);
                }
            }

            if (controllerRegistration.all(grade, true).isEmpty()) controllerRegistration.downloadData(dashboard.teacherAuth, grade);
            int m = 1;
            for (Registration registration : controllerRegistration.all(grade, true)) {
                row = sheet.createRow(m+4);
                cell = row.createCell(0);
                cell.setCellStyle(styleStudent);
                cell.setCellValue(registration.getStudent().getCodeStudent());
                cell = row.createCell(1);
                cell.setCellStyle(styleStudent);
                cell.setCellValue(registration.getStudent().getFullName());
                sheet.addMergedRegion(new CellRangeAddress(++m+3, m+3, 1,3));
                int col = 4;
                for (int a = 4; a < n; a++){
                    cell = row.createCell(col++);
                    cell.setCellStyle(styleNoteUnLocked);
                }
            }

            n = 4;
            int numRowNote = controllerRegistration.all(grade, true).size();
            for (Course course : controllerCargo.all(grade, dashboard.teacherAuth, true)) {
                for (Skill ignored : course.getSkills()){
                    DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
                    CellRangeAddressList addressList = new CellRangeAddressList(5, numRowNote+4, n, n);
                    DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(new String[]{"AD", "A", "B", "C"});
                    DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
                    dataValidation.setSuppressDropDownArrow(true);
                    dataValidation.setShowErrorBox(true);
                    sheet.addValidationData(dataValidation);
                    n += 2;
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, colEnd));
            sheet.setColumnWidth(0, 4500);
            sheet.setColumnWidth(1, 3500);
            sheet.setColumnWidth(2, 3500);
            sheet.setColumnWidth(3, 3500);
            for (CellRangeAddress range : sheet.getMergedRegions()) {
                RegionUtil.setBorderTop(style.getBorderTop(), range, sheet);
                RegionUtil.setBorderRight(style.getBorderRight(), range, sheet);
                RegionUtil.setBorderBottom(style.getBorderBottom(), range, sheet);
                RegionUtil.setBorderLeft(style.getBorderLeft(), range, sheet);
            }
        }
    }
    public void querySet(Classroom classroom, Long numPartial){
        this.numPartial = numPartial;
        ControllerGrade controllerGrade = new ControllerGrade();
        grades = controllerGrade.all(classroom, dashboard.teacherAuth.level);
    }
    private void createSchema(){
        ControllerNote controllerNote = new ControllerNote();
        for (Grade grade : grades) {
            controllerNote.downloadData(dashboard.teacherAuth, grade);
            int lastCol = 5;
            XSSFSheet sheet = book.createSheet(String.format("%d%s",
                    grade.getClassroom().getId(),
                    grade.getSection().getSection()));
            Font font = book.createFont();
            font.setBold(true);
            CellStyle style = book.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFont(font);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            Row row = sheet.createRow(1);
            if (controllerCargo.count(grade, true) != controllerCourse.count(dashboard.teacherAuth.level)) {
                controllerCargo.getCargosAdmin(dashboard.teacherAuth);
            }
            List<Cargo> cargos = controllerCargo.all(grade, true);
            for (Cargo cargo : cargos) {
                Cell cell = row.createCell(lastCol);
                cell.setCellStyle(style);
                Course course = cargo.getCourse();
                int range = course.getSkills().size();
                cell.setCellValue(cargo.getCourse().getAbbreviation());
                if (range != 1) sheet.addMergedRegion(new CellRangeAddress(1, 1, lastCol,lastCol+range-1));
                lastCol += range;
            }
            row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("Nº");

            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue("Nombre");

            cell = row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue("DNI");

            cell = row.createCell(3);
            cell.setCellStyle(style);
            cell.setCellValue("Grado");

            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue("Sec");

            cell = row.createCell(5);
            cell.setCellStyle(style);
            cell.setCellValue("NOTAS 1 BIMESTRE 2023");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, lastCol-1));

            lastCol = 5;
            row = sheet.createRow(2);
            for (Cargo cargo : cargos) {
                Course course = cargo.getCourse();
                course.cleanOfCache();
                course = controllerCourse.get(course.getId());
                for (int i = 1; i <= course.getSkills().size(); i++){
                    cell = row.createCell(lastCol);
                    cell.setCellStyle(style);
                    cell.setCellValue(String.format("%s-%d", cargo.getCourse().getAbbreviation(), i));
                    lastCol++;
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 3, 3));
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 4, 4));
        }
    }
    private void insetData(){
        for (Grade grade : grades) {
            String nameSheet = String.format("%d%s",
                    grade.getClassroom().getId(),
                    grade.getSection().getSection());
            Sheet sheet = book.getSheet(nameSheet);
            CellStyle style = book.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            CellStyle styleName = book.createCellStyle();
            styleName.setWrapText(true);
            styleName.setAlignment(HorizontalAlignment.LEFT);
            styleName.setVerticalAlignment(VerticalAlignment.CENTER);
            styleName.setBorderBottom(BorderStyle.THIN);
            styleName.setBorderTop(BorderStyle.THIN);
            styleName.setBorderLeft(BorderStyle.THIN);
            styleName.setBorderRight(BorderStyle.THIN);

            int numRow = 3;
            if (controllerRegistration.count(grade, true) == 0){
                controllerRegistration.downloadData(dashboard.teacherAuth, grade);
            }
            List<Cargo> cargos = controllerCargo.all(grade, true);
            for (Registration registration: controllerRegistration.all(grade, true)) {
                int col = 0;
                Row row = sheet.createRow(numRow++);
                Cell cell = row.createCell(col++);
                cell.setCellStyle(style);
                cell.setCellValue(numRow-3);

                cell = row.createCell(col++);
                cell.setCellStyle(styleName);
                cell.setCellValue(String.format(
                        "%s %s, %s",
                        registration.getStudent().getLastNameFather(),
                        registration.getStudent().getLastNameMother(), registration.getStudent().getFirstName()));

                cell = row.createCell(col++);
                cell.setCellStyle(style);
                cell.setCellValue(registration.getStudent().getNumberDocument());

                cell = row.createCell(col++);
                cell.setCellStyle(style);
                cell.setCellValue(grade.getClassroom().getClassroom());

                cell = row.createCell(col++);
                cell.setCellStyle(style);
                cell.setCellValue(grade.getSection().getSection());

                for (Cargo cargo : cargos) {
                    Partial partial = controllerPartial.get(numPartial, cargo.getCourse());
                    Course course = cargo.getCourse();
                    for (Skill skill : course.getSkills()) {
                        Note note = controllerNote.get(
                                grade,
                                cargo.getCourse(),
                                skill,
                                partial,
                                cargo.getTeacher(),
                                registration.getStudent(),
                                dashboard.teacherAuth.level);
                        cell = row.createCell(col++);
                        cell.setCellStyle(style);
                        cell.setCellValue(note==null?"":note.getNote());
                    }
                }
            }
            sheet.autoSizeColumn(1);
        }
    }
    public void insertNotes(File[] files, Level level){
        try {
            for (File file : files){
                Workbook book = new XSSFWorkbook(new FileInputStream(file));
                Sheet sheet = book.getSheet("Generalidades");
                Row rowData = sheet.getRow(9);
                String nameClassroom = rowData.getCell(7).getStringCellValue();
                String nameSection = rowData.getCell(9).getStringCellValue();
                ControllerSection controllerSection = new ControllerSection();
                ControllerClassroom controllerClassroom = new ControllerClassroom();
                Section section = controllerSection.get(nameSection);
                Classroom classroom = controllerClassroom.get(nameClassroom);
                Grade grade = controllerGrade.get(section, classroom);

                controllerCourse.downloadData(dashboard.teacherAuth);
                controllerCargo.downloadData(dashboard.teacherAuth);
                controllerTeacher.downloadData(dashboard.teacherAuth);
                grade.cleanOfCache();
                grade = controllerGrade.get(grade.getId());
                for (Cargo cargo : grade.getCargos()) {
                    sheet = book.getSheet(cargo.getCourse().getAbbreviation());
                    if (sheet == null){
                        twoSheets(book, grade, cargo.getTeacher(), dashboard.permission.getPartial());
                        continue;
                    }
                    DataFormatter formatter = new DataFormatter();
                    for (Row row : sheet){
                        String code = formatter.formatCellValue(row.getCell(1));
                        if (!StringUtils.isNumeric(code)) continue;
                        Student student = controllerStudent.get(String.valueOf(Long.parseLong(code)));
                        if (student == null) continue;
                        for (Skill skill : cargo.getCourse().getSkills()) {
                            Partial partial = controllerPartial.get(dashboard.permission.getPartial(), cargo.getCourse());
                            Note note = controllerNote.get(
                                    grade,
                                    cargo.getCourse(),
                                    skill,
                                    partial,
                                    cargo.getTeacher(),
                                    student,
                                    dashboard.teacherAuth.level);
                            if (note == null) continue;
                            switch (skill.getIndex()){
                                case 1:
                                    row.getCell(3).setCellValue(note.getNote());
                                    row.getCell(4).setCellValue(note.getObservation());
                                    break;
                                case 2:
                                    row.getCell(5).setCellValue(note.getNote());
                                    row.getCell(6).setCellValue(note.getObservation());
                                    break;
                                case 3:
                                    row.getCell(7).setCellValue(note.getNote());
                                    row.getCell(8).setCellValue(note.getObservation());
                                    break;
                                case 4:
                                    row.getCell(9).setCellValue(note.getNote());
                                    row.getCell(10).setCellValue(note.getObservation());
                                    break;
                                default:
                                    row.getCell(11).setCellValue(note.getNote());
                                    row.getCell(12).setCellValue(note.getObservation());
                            }
                        }
                    }
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                book.write(outputStream);
                book.close();
            }
            JOptionPane.showMessageDialog(dashboard, "Se ha registrado correctamente las notas");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void twoSheets(Workbook book, Grade grade, Teacher teacher, Long numberPartial){
        Sheet sheet_tic = book.getSheet("0006-DESEN TIC");
        Sheet sheet_auto = book.getSheet("0007-GEST AUTO");
        DataFormatter formatter = new DataFormatter();

        Course course = controllerCourse.get("TUTORIA");
        for (Row row : sheet_tic){
            String code = formatter.formatCellValue(row.getCell(1));
            if (!StringUtils.isNumeric(code)) continue;
            Student student = controllerStudent.get(String.valueOf(Long.parseLong(code)));
            if (student == null) continue;

            Partial partial = controllerPartial.get(numberPartial, course);
            Note note = controllerNote.get(
                    grade,
                    course,
                    course.getSkills().get(0),
                    partial,
                    teacher,
                    student,
                    dashboard.teacherAuth.level);
            if (note == null) continue;
            row.getCell(3).setCellValue(note.getNote());
            row.getCell(4).setCellValue(note.getObservation());
        }
        for (Row row : sheet_auto){
            String code = formatter.formatCellValue(row.getCell(1));
            if (!StringUtils.isNumeric(code)) continue;
            Student student = controllerStudent.get(String.valueOf(Long.parseLong(code)));
            if (student == null) continue;

            Partial partial = controllerPartial.get(2L, course);
            Note note = controllerNote.get(
                    grade,
                    course,
                    course.getSkills().get(1),
                    partial,
                    teacher,
                    student,
                    dashboard.teacherAuth.level);
            if (note == null) continue;
            row.getCell(3).setCellValue(note.getNote());
            row.getCell(4).setCellValue(note.getObservation());
        }
    }
    public boolean saveBook(String nameExcel){
        boolean status = false;
        String extension = "xlsx";
        File file = new File(nameExcel);
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(file);
        FileNameExtensionFilter filterExcel = new FileNameExtensionFilter("Microsoft Excel (.xlsx)",extension);
        chooser.addChoosableFileFilter(filterExcel);
        chooser.setFileFilter(filterExcel);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        int select = chooser.showSaveDialog(dashboard);
        if (select == JFileChooser.APPROVE_OPTION){
            try {
                FileSystem fs = FileSystems.getDefault();
                file = chooser.getSelectedFile();
                String name = FileNameUtils.getBaseName(file.getName());
                name = name+".xlsx";
                file = new File(
                        String.format("%s%s%s",
                                file.getParentFile().getAbsolutePath(),
                                fs.getSeparator(),
                                name)
                );
                FileOutputStream outputStream = new FileOutputStream(file);
                book.write(outputStream);
                book.close();
                outputStream.close();
                status = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return status;
    }
    public File[] loadBook(){
        final JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);

        FileNameExtensionFilter filterExcel = new FileNameExtensionFilter("Microsoft Excel (.xlsx)", "xlsx");
        fc.addChoosableFileFilter(filterExcel);
        fc.setFileFilter(filterExcel);

        int value = fc.showOpenDialog(dashboard);
        if (value == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFiles();
        }
        return null;
    }
}
