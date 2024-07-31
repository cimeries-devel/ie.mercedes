package com.babasdevel.utils;

import com.babasdevel.cimeries.Codes;
import com.babasdevel.controllers.*;
import com.babasdevel.models.*;
import com.babasdevel.views.Dashboard;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Excel {
    private XSSFWorkbook book;
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
        POIXMLProperties properties = book.getProperties();
        POIXMLProperties.CoreProperties coreProperties = properties.getCoreProperties();
        coreProperties.setCreator("Aula de Innovación Pedagógica");
        coreProperties.setDescription("Plantilla de registro de notas para la institución educativa Nuestra Señora de las Mercedes");
        coreProperties.setIdentifier("cimeries");
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
        fontBold.setFontHeightInPoints((short)11);
        fontBold.setBold(true);

        Font font = book.createFont();
        font.setFontHeightInPoints((short)11);
        font.setBold(false);

        CellStyle styleLeft = book.createCellStyle();
        styleLeft.setWrapText(true);
        styleLeft.setAlignment(HorizontalAlignment.LEFT);
        styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeft.setFont(fontBold);
        styleLeft.setBorderBottom(BorderStyle.THIN);
        styleLeft.setBorderTop(BorderStyle.THIN);
        styleLeft.setBorderLeft(BorderStyle.THIN);
        styleLeft.setBorderRight(BorderStyle.THIN);

        CellStyle styleCenter = book.createCellStyle();
        styleCenter.setWrapText(true);
        styleCenter.setAlignment(HorizontalAlignment.CENTER);
        styleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenter.setFont(font);
        styleCenter.setBorderBottom(BorderStyle.THIN);
        styleCenter.setBorderTop(BorderStyle.THIN);
        styleCenter.setBorderLeft(BorderStyle.THIN);
        styleCenter.setBorderRight(BorderStyle.THIN);

        CellStyle styleCenterBold = book.createCellStyle();
        styleCenterBold.setWrapText(true);
        styleCenterBold.setAlignment(HorizontalAlignment.CENTER);
        styleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenterBold.setFont(fontBold);
        styleCenterBold.setBorderBottom(BorderStyle.THIN);
        styleCenterBold.setBorderTop(BorderStyle.THIN);
        styleCenterBold.setBorderLeft(BorderStyle.THIN);
        styleCenterBold.setBorderRight(BorderStyle.THIN);

        CellStyle styleLeftNotBold = book.createCellStyle();
        styleLeftNotBold.setAlignment(HorizontalAlignment.LEFT);
        styleLeftNotBold.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeftNotBold.setFont(font);
        styleLeftNotBold.setBorderBottom(BorderStyle.THIN);
        styleLeftNotBold.setBorderTop(BorderStyle.THIN);
        styleLeftNotBold.setBorderLeft(BorderStyle.THIN);
        styleLeftNotBold.setBorderRight(BorderStyle.THIN);

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

        List<Cargo> cargos = controllerCargo.get(dashboard.teacherAuth, true);
        cargos.forEach(cargo -> {
            XSSFSheet sheet = book.createSheet(String.format("%s | %s", cargo.getGrade().getName(), cargo.getCourse().getAbbreviation()));
            sheet.setColumnWidth(0, 4200);
            sheet.setColumnWidth(1, 3800);
            sheet.setColumnWidth(2, 3800);
            sheet.setColumnWidth(3, 3800);
            sheet.protectSheet("aip-bd");
            sheet.createFreezePane(4, 0);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getCourse().getAbbreviation());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Nivel");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(dashboard.teacherAuth.level.getLevel());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Grado:");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getGrade().getClassroom().getClassroom());

            cell = row.createCell(2);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Sección:");

            cell = row.createCell(3);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getGrade().getSection().getSection());

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Docente:");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(dashboard.teacherAuth.getFullName());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Código");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenterBold);
            cell.setCellValue("Apellidos y Nombres");
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));


            cell = sheet.getRow(0).createCell(4);
            cell.setCellStyle(styleCenterBold);
            cell.setCellValue(cargo.getCourse().getName());
            CellRangeAddress rangeCourse = new CellRangeAddress(0, 0, 4,3+cargo.getCourse().getSkills().size()*2);
            sheet.addMergedRegion(rangeCourse);

            int index = 4;
            for (Skill skill : cargo.getCourse().getSkills()) {
                cell = sheet.getRow(1).createCell(index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue(String.format("%d. %s", skill.getIndex(), skill.getName()));
                sheet.addMergedRegion(new CellRangeAddress(1, 3, index, index+1));
                sheet.setColumnWidth(index, 1500);
                sheet.setColumnWidth(index+1, 10500);

                cell = sheet.getRow(4).createCell(index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue("NL");

                cell = sheet.getRow(4).createCell(++index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue("Conclusión descriptiva");

                index++;
            }

            index = 5;
            List<Registration> registrations = controllerRegistration.all(cargo.getGrade(), true);
            if (registrations.isEmpty()) {
                controllerRegistration.downloadData(dashboard.teacherAuth, cargo.getGrade());
                registrations = controllerRegistration.all(cargo.getGrade(), true);
            }
            for (Registration registration : registrations) {
                row = sheet.createRow(index++);
                cell = row.createCell(0);
                cell.setCellStyle(styleLeftNotBold);
                cell.setCellValue(registration.getStudent().getCodeStudent());

                cell = row.createCell(1);
                cell.setCellStyle(styleLeftNotBold);
                cell.setCellValue(registration.getStudent().getFullName());
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

                for (int c = rangeCourse.getFirstColumn(); c<=rangeCourse.getLastColumn(); c++ ){
                    cell = row.createCell(c);
                    cell.setCellStyle(styleNoteUnLocked);
                }
            }

            index = 4;
            for (Skill ignore : cargo.getCourse().getSkills()) {
                DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
                CellRangeAddressList addressList = new CellRangeAddressList(5, registrations.size()+4, index, index);
                DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(new String[]{"AD", "A", "B", "C"});
                DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
                dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidation.createErrorBox("Nivel de logro", "El nivel de logro ingresado no es válido, sólo se acepta: AD, A, B o C");
                sheet.addValidationData(dataValidation);

                CellRangeAddressList addressCell = new CellRangeAddressList(5, registrations.size()+4, index+1, index+1);
                DataValidationConstraint constraintBetween = validationHelper.createNumericConstraint(
                        DataValidationConstraint.ValidationType.TEXT_LENGTH,
                        DataValidationConstraint.OperatorType.BETWEEN,
                        "11", "64");
                DataValidation dataValidationText = validationHelper.createValidation(constraintBetween, addressCell);
                dataValidationText.setShowErrorBox(true);
                dataValidationText.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidationText.createErrorBox("Conclusión descriptiva", "La conclusión descriptiva debe contener mínimo 11 y máximo 64 caracteres.");
                sheet.addValidationData(dataValidationText);
                index += 2;
            }
            for (CellRangeAddress range : sheet.getMergedRegions()) {
                RegionUtil.setBorderTop(styleLeft.getBorderTop(), range, sheet);
                RegionUtil.setBorderRight(styleLeft.getBorderRight(), range, sheet);
                RegionUtil.setBorderBottom(styleLeft.getBorderBottom(), range, sheet);
                RegionUtil.setBorderLeft(styleLeft.getBorderLeft(), range, sheet);
            }
        });
    }
    public void createTemplateForTeacherWithNotes() {
        Font fontBold = book.createFont();
        fontBold.setFontHeightInPoints((short)11);
        fontBold.setBold(true);

        Font font = book.createFont();
        font.setFontHeightInPoints((short)11);
        font.setBold(false);

        CellStyle styleLeft = book.createCellStyle();
        styleLeft.setWrapText(true);
        styleLeft.setAlignment(HorizontalAlignment.LEFT);
        styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeft.setFont(fontBold);
        styleLeft.setBorderBottom(BorderStyle.THIN);
        styleLeft.setBorderTop(BorderStyle.THIN);
        styleLeft.setBorderLeft(BorderStyle.THIN);
        styleLeft.setBorderRight(BorderStyle.THIN);

        CellStyle styleCenter = book.createCellStyle();
        styleCenter.setWrapText(true);
        styleCenter.setAlignment(HorizontalAlignment.CENTER);
        styleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenter.setFont(font);
        styleCenter.setBorderBottom(BorderStyle.THIN);
        styleCenter.setBorderTop(BorderStyle.THIN);
        styleCenter.setBorderLeft(BorderStyle.THIN);
        styleCenter.setBorderRight(BorderStyle.THIN);

        CellStyle styleCenterBold = book.createCellStyle();
        styleCenterBold.setWrapText(true);
        styleCenterBold.setAlignment(HorizontalAlignment.CENTER);
        styleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCenterBold.setFont(fontBold);
        styleCenterBold.setBorderBottom(BorderStyle.THIN);
        styleCenterBold.setBorderTop(BorderStyle.THIN);
        styleCenterBold.setBorderLeft(BorderStyle.THIN);
        styleCenterBold.setBorderRight(BorderStyle.THIN);

        CellStyle styleLeftNotBold = book.createCellStyle();
        styleLeftNotBold.setAlignment(HorizontalAlignment.LEFT);
        styleLeftNotBold.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLeftNotBold.setFont(font);
        styleLeftNotBold.setBorderBottom(BorderStyle.THIN);
        styleLeftNotBold.setBorderTop(BorderStyle.THIN);
        styleLeftNotBold.setBorderLeft(BorderStyle.THIN);
        styleLeftNotBold.setBorderRight(BorderStyle.THIN);

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

        List<Cargo> cargos = controllerCargo.get(dashboard.teacherAuth, true);
        cargos.forEach(cargo -> {
            XSSFSheet sheet = book.createSheet(String.format("%s | %s", cargo.getGrade().getName(), cargo.getCourse().getAbbreviation()));
            sheet.setColumnWidth(0, 4200);
            sheet.setColumnWidth(1, 3800);
            sheet.setColumnWidth(2, 3800);
            sheet.setColumnWidth(3, 3800);
            sheet.protectSheet("aip-bd");
            sheet.createFreezePane(4, 0);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getCourse().getAbbreviation());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Nivel");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(dashboard.teacherAuth.level.getLevel());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Grado:");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getGrade().getClassroom().getClassroom());

            cell = row.createCell(2);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Sección:");

            cell = row.createCell(3);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(cargo.getGrade().getSection().getSection());

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Docente:");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenter);
            cell.setCellValue(dashboard.teacherAuth.getFullName());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellStyle(styleLeft);
            cell.setCellValue("Código");

            cell = row.createCell(1);
            cell.setCellStyle(styleCenterBold);
            cell.setCellValue("Apellidos y Nombres");
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));


            cell = sheet.getRow(0).createCell(4);
            cell.setCellStyle(styleCenterBold);
            cell.setCellValue(cargo.getCourse().getName());
            CellRangeAddress rangeCourse = new CellRangeAddress(0, 0, 4,3+cargo.getCourse().getSkills().size()*2);
            sheet.addMergedRegion(rangeCourse);

            int index = 4;
            for (Skill skill : cargo.getCourse().getSkills()) {
                cell = sheet.getRow(1).createCell(index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue(String.format("%d. %s", skill.getIndex(), skill.getName()));
                sheet.addMergedRegion(new CellRangeAddress(1, 3, index, index+1));
                sheet.setColumnWidth(index, 1500);
                sheet.setColumnWidth(index+1, 10500);

                cell = sheet.getRow(4).createCell(index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue("NL");

                cell = sheet.getRow(4).createCell(++index);
                cell.setCellStyle(styleCenterBold);
                cell.setCellValue("Conclusión descriptiva");

                index++;
            }

            index = 5;
            List<Registration> registrations = controllerRegistration.all(cargo.getGrade(), true);
            if (registrations.isEmpty()) {
                controllerRegistration.downloadData(dashboard.teacherAuth, cargo.getGrade());
                registrations = controllerRegistration.all(cargo.getGrade(), true);
            }
            for (Registration registration : registrations) {
                row = sheet.createRow(index++);
                cell = row.createCell(0);
                cell.setCellStyle(styleLeftNotBold);
                cell.setCellValue(registration.getStudent().getCodeStudent());

                cell = row.createCell(1);
                cell.setCellStyle(styleLeftNotBold);
                cell.setCellValue(registration.getStudent().getFullName());
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

                for (int c = rangeCourse.getFirstColumn(); c<=rangeCourse.getLastColumn(); c++ ){
                    cell = row.createCell(c);
                    cell.setCellStyle(styleNoteUnLocked);
//                    Note note = controllerNote.get(
//                            cargo.getGrade(),
//                            cargo.getCourse(),
//                            skill,
//                            partial,
//                            cargo.getTeacher(),
//                            registration.getStudent(),
//                            dashboard.teacherAuth.level);
                }
            }

            index = 4;
            for (Skill ignore : cargo.getCourse().getSkills()) {
                DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
                CellRangeAddressList addressList = new CellRangeAddressList(5, registrations.size()+4, index, index);
                DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(new String[]{"AD", "A", "B", "C"});
                DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
                dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidation.createErrorBox("Nivel de logro", "El nivel de logro ingresado no es válido, sólo se acepta: AD, A, B o C");
                sheet.addValidationData(dataValidation);

                CellRangeAddressList addressCell = new CellRangeAddressList(5, registrations.size()+4, index+1, index+1);
                DataValidationConstraint constraintBetween = validationHelper.createNumericConstraint(
                        DataValidationConstraint.ValidationType.TEXT_LENGTH,
                        DataValidationConstraint.OperatorType.BETWEEN,
                        "11", "64");
                DataValidation dataValidationText = validationHelper.createValidation(constraintBetween, addressCell);
                dataValidationText.setShowErrorBox(true);
                dataValidationText.setErrorStyle(DataValidation.ErrorStyle.STOP);
                dataValidationText.createErrorBox("Conclusión descriptiva", "La conclusión descriptiva debe contener mínimo 11 y máximo 64 caracteres.");
                sheet.addValidationData(dataValidationText);
                index += 2;
            }
            for (CellRangeAddress range : sheet.getMergedRegions()) {
                RegionUtil.setBorderTop(styleLeft.getBorderTop(), range, sheet);
                RegionUtil.setBorderRight(styleLeft.getBorderRight(), range, sheet);
                RegionUtil.setBorderBottom(styleLeft.getBorderBottom(), range, sheet);
                RegionUtil.setBorderLeft(styleLeft.getBorderLeft(), range, sheet);
            }
        });
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
                Grade grade = controllerGrade.get(section, classroom, level);

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
    public void sendNotesOfTemplate(File file, Level level){
        try {
            book = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ControllerLevel controllerLevel = new ControllerLevel();
        ControllerClassroom controllerClassroom = new ControllerClassroom();
        ControllerSection controllerSection = new ControllerSection();
        ControllerSkill controllerSkill = new ControllerSkill();

        List<NoteModel> notes = new ArrayList<>();
        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            Sheet sheet = book.getSheetAt(i);
            String nameLevel = sheet.getRow(1).getCell(1).getStringCellValue();
            Level lt = controllerLevel.get(nameLevel);

            if (lt != null && Objects.equals(lt.getId(), level.getId())) {
                String classroomName = sheet.getRow(2).getCell(1).getStringCellValue();
                String sectionName = sheet.getRow(2).getCell(3).getStringCellValue();

                Classroom classroom = controllerClassroom.get(classroomName);

                Section section = controllerSection.get(sectionName);

                String nameSheet = sheet.getSheetName();
                nameSheet = nameSheet.substring(nameSheet.indexOf("|")+1).trim();
                Course course = controllerCourse.get(nameSheet, level);

                Grade grade = controllerGrade.get(section, classroom, level);

                Cargo cargo = controllerCargo.get(grade, course, true);
                if (cargo == null){
                    controllerCargo.downloadCargo(dashboard.teacherAuth, grade, course);
                    cargo = controllerCargo.get(grade, course, true);
                }

                List<Registration> registrations = controllerRegistration.all(grade, true);
                if (registrations.isEmpty()){
                    controllerRegistration.downloadData(dashboard.teacherAuth, grade);
                    registrations = controllerRegistration.all(grade, true);
                }
                for (int indexRow = 5; indexRow <= sheet.getLastRowNum(); indexRow++) {
                    Row row = sheet.getRow(indexRow);
                    Student student = controllerStudent.get(row.getCell(0).getStringCellValue());

                    for (int col = 0; col < course.getSkills().size()*2; col+=2){
                        String n = row.getCell(4+col).getStringCellValue();
                        String o = row.getCell(5+col).getStringCellValue();

                        if (!o.isEmpty() && (o.length() > 125 || o.length() < 11)) {
                            JOptionPane.showMessageDialog(dashboard,
                                    "La conclusión descriptiva ingresada, no es válida.\n" +
                                            "El valor ingresado, debe contener mínimo 11 y máximo 125 carácteres.\n" +
                                            grade.getName()+" : "+ course.getName()+"\n" +
                                            "Alumna(o): "+student.getFullName(),
                                    "Cambios no guardados",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (n.equals("C") && o.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dashboard,
                                    "La conclusión descriptiva ingresada, no es válida.\n" +
                                            "El valor ingresado, debe contener mínimo 11 y máximo 125 carácteres.\n" +
                                            grade.getName()+" : "+ course.getName()+"\n" +
                                            "Alumna(o): "+student.getFullName(),
                                    "Cambios no guardados",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (dashboard.teacherAuth.level.getLevel().equalsIgnoreCase("Primaria") && n.equals("B") && o.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dashboard,
                                    "La conclusión descriptiva ingresada, no es válida.\n" +
                                            "El valor ingresado, debe contener mínimo 11 y máximo 125 carácteres.\n" +
                                            grade.getName()+" : "+ course.getName()+"\n" +
                                            "Alumna(o): "+student.getFullName(),
                                    "Cambios no guardados",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        NoteModel note = new NoteModel();
                        note.grade = grade.getId();
                        note.course = course.getId();
                        note.teacher = cargo.getTeacher().getId();
                        note.student = student.getId();
                        note.partial = controllerPartial.get(dashboard.permission.getPartial(), course).getId();
                        note.note = n;
                        note.observation = o;
                        note.skill = controllerSkill.get(course, switch (col) {
                            case 0 -> 1;
                            case 2 -> 2;
                            case 4 -> 3;
                            case 6 -> 4;
                            default -> 5;
                        }).getId();
                        notes.add(note);
                    }
                }
            }
        }
        int code = controllerNote.post(dashboard.teacherAuth, notes);
        if (code == Codes.CODE_CREATED) {
            JOptionPane.showMessageDialog(dashboard,
                    "Las notas, han sido registradas correctamente",
                    "Notas registradas",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(dashboard,
                    "Las notas, no han sido registradas.",
                    "Notas no registradas",
                    JOptionPane.ERROR_MESSAGE);
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
    public File[] loadBooks(){
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
    public File loadBook(){
        final JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter filterExcel = new FileNameExtensionFilter("Microsoft Excel (.xlsx)", "xlsx");
        fc.addChoosableFileFilter(filterExcel);
        fc.setFileFilter(filterExcel);

        int value = fc.showOpenDialog(dashboard);
        if (value == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFile();
        }
        return null;
    }
}
