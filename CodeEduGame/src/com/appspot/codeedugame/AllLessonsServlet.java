package com.appspot.codeedugame;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class AllLessonsServlet extends HttpServlet {
    private static final String PATH = "/lessons";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<String> lessonIds = getLineArray(PATH + "/lessonIds.txt");
        List<String> lessonNames = new ArrayList<String>();
        List<String> lessonContents = new ArrayList<String>();
        String lessonCss = getContents(PATH + "/lessonCss.txt");
        
        PrintWriter output;
        try {
            output = resp.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output.append(lessonCss + "\n\n");
        output.append("<h2>Contents</h2>\n");
        for (int i = 0; i < lessonIds.size(); i++) {
            lessonContents.add(getContents(PATH + "/" + lessonIds.get(i) + ".htm"));
            lessonNames.add(getLessonTitle(lessonContents.get(i)));
        }
        for (int i = 0; i < lessonIds.size(); i++) {
            output.append("<li><a href=\"#" + lessonIds.get(i) + "\"> " + lessonNames.get(i) + " </a></li>\n");
        }
        for (int i = 0; i < lessonIds.size(); i++) {
            output.append(lessonBreak());
            output.append("<a name=\"" + lessonIds.get(i) + "\"></a>\n");
            output.append(lessonContents.get(i));
        }
    }
    
    private String getContents(String fName) {
        Scanner s = refineStream(fName);
        StringBuilder builder = new StringBuilder();
        while (s.hasNext()) {
            builder.append(s.nextLine() + "\n");
        }
        return builder.toString();
    }

    private Scanner refineStream(String fName) {
        ServletContext ctx = getServletContext();
        InputStream is = ctx.getResourceAsStream(fName);
        return new Scanner(is);
    }

    private List<String> getLineArray(String fName) {
        Scanner s = refineStream(fName);
        List<String> lessons = new ArrayList<String>();
        while (s.hasNext()) {
            lessons.add(s.nextLine());
        }
        return lessons;
    }
    
    private String getLessonTitle(String contents) {
        String afterH3 = contents.substring(contents.indexOf("h3"));
        String first = afterH3.substring(afterH3.indexOf(">") + 1, afterH3.indexOf("/h3"));
        return first.substring(0, first.lastIndexOf("<"));
    }

    private String lessonBreak() {
        return "\n" 
            + "<p>--------------------------------------------------------------------------</p>"
            + "\n\n";
    }
    
    
}
