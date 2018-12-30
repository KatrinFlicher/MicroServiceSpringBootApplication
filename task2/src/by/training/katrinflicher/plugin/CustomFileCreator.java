package by.training.katrinflicher.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;


public class CustomFileCreator  extends org.apache.tools.ant.Task{
    String fileName;
    final String SEPARATOR_SPACE = " ";
    final String SEPARATOR_COLON = ":";

    public List<Attribute> attributes = new ArrayList<Attribute>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Attribute createAttribute() {
        Attribute attribute = new Attribute();
        attributes.add(attribute);
        return attribute;
    }


    public void execute(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            for (Attribute attribute : attributes) {
                writer.println(attribute.getName() + SEPARATOR_COLON + SEPARATOR_SPACE + attribute.getValue());
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
        finally{
            if (writer!=null)
                writer.close();
        }
    }

    public class Attribute {
        public Attribute() {}

        public String name;
        public String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}