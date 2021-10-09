package org.university.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.stereotype.Component;
import org.university.entity.DayTimetable;
import org.university.entity.Lesson;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component()
public class PDFDataGenerator {

    public void generateGroupTimetable(OutputStream output, List<DayTimetable> timetables, String groupName) throws IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.setPageSize(PageSize.A4);
        document.open();
        document.add(new Paragraph(groupName));
        document.add(new Paragraph(" "));
        document.add((Element) generateGroupLessonTable(timetables));
        document.close();
    }

    private PdfPTable generateGroupLessonTable(List<DayTimetable> timetables) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLDITALIC);
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100.0f);
        table.setWidths(new float[] { 4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 4.0f });
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Start lesson", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("End lesson", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Teacher", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Course", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Classroom", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Lesson link", font));
        table.addCell(cell);
        for (DayTimetable timetable : timetables) {
            cell.setPhrase(new Phrase(timetable.getDay().toString()));
            table.addCell(cell);
            List<Lesson> lessons = timetable.getLessons();
            for (int i = 0; i < lessons.size(); i++) {
                if (i >= 1) {
                    table.addCell("");
                }
                cell.setPhrase(new Phrase(lessons.get(i).getStartLesson().toLocalTime().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getEndLesson().toLocalTime().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getTeacher().getName()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getCourse().getName()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getClassroom().getNumber().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getLessonLink()));
                table.addCell(cell);
            }
        }
        return table;
    }
    
    public void generateTeacherTimetable(OutputStream output, List<DayTimetable> timetables, String teacherName) {
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.setPageSize(PageSize.A4);
        document.open();
        document.add(new Paragraph(teacherName));
        document.add(new Paragraph(" "));
        document.add((Element) generateTeacherLessonTable(timetables));
        document.close();
    }

    private PdfPTable generateTeacherLessonTable(List<DayTimetable> timetables) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLDITALIC);
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100.0f);
        table.setWidths(new float[] { 4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 4.0f });
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Start lesson", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("End lesson", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Group", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Course", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Classroom", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Lesson link", font));
        table.addCell(cell);
        for (DayTimetable timetable : timetables) {
            cell.setPhrase(new Phrase(timetable.getDay().toString()));
            table.addCell(cell);
            List<Lesson> lessons = timetable.getLessons();
            for (int i = 0; i < lessons.size(); i++) {
                if (i >= 1) {
                    table.addCell("");
                }
                cell.setPhrase(new Phrase(lessons.get(i).getStartLesson().toLocalTime().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getEndLesson().toLocalTime().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getGroup().getName()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getCourse().getName()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getClassroom().getNumber().toString()));
                table.addCell(cell);
                cell.setPhrase(new Phrase(lessons.get(i).getLessonLink()));
                table.addCell(cell);
            }
        }
        return table;
    }
}
