package by.training.katrinflicher;

public class JavaHeapOOMGenerator {
    public static void generateError(int value) {
        int[] nums = new int[value];
        value <<= 2;
        generateError(value);
    }
}
