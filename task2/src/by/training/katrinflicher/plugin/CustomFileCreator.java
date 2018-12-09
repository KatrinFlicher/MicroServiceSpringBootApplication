package by.training.katrinflicher.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.BuildException;


public class CustomFileCreator extends org.apache.tools.ant.Task {
    String fileName;

    public List<Line> lines = new ArrayList<>();
    public List<String> attributes = Arrays.asList("Built-By");


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Line createLine() {
        Line line = new Line();
        lines.add(line);
        return line;
    }


    public void execute(){
        for (String att: attributes){
            String valueProperty = att.concat(": " + getProject().getProperty(att));
            createLine().setFileLine(valueProperty);
        }

        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            for (Line line : lines) {
                writer.println(line.getFileLine());
            }
            writer.close();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public class Line {
        public Line() {}

        public String fileLine;
        public void setFileLine(String fileLine) { this.fileLine = fileLine; }
        public String getFileLine() { return fileLine; }
    }
}