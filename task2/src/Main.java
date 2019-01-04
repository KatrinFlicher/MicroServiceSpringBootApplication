import by.training.katrinflicher.JavaHeapOOMGenerator;
import by.training.katrinflicher.StackOverFlowErrorGenerator;
import by.training.katrinflicher.MetaSpaceOOMGenerator;
import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;

public class Main {
        @Command(name = "1")
        public void overflowStack() {
            StackOverFlowErrorGenerator.generateError();}

        @Command(name = "2")
        public void overflowHeap() {
            JavaHeapOOMGenerator.generateError(1000);}

        @Command(name = "3")
        public void overflowMetaSpace() {
            MetaSpaceOOMGenerator.generateError();}

        @Command(name = "4")
        public void exit() {
            System.exit(0);}

        public static void main(String[] args) throws IOException {
            ShellFactory.createConsoleShell("\n\nIf you run app with command 'java -jar', please add VM options on your own (Recommended = 20m).\n\n\nPlease, select memory which you'd like to overflow:" +
                            "\n1)Stack\n2)Heap\n3)Metaspace(will be created about 3000-4000 classes. Don't worry! I include required VM options.)\n4)Exit\nEnter required number\n",
                    "", new Main()).commandLoop();
        }

}
