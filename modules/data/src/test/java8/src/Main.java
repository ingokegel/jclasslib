import java.util.Arrays;
import java.util.List;

public class Main<@Test T extends @Test Object> extends @Test Object implements @Test TestInterface {

    public static final int TEST_INT = 1;
    public static final String TEST_STRING = "123";

    private List<@Test String> strings;

    private @Test Object @Test2 [] data;

    public static void main(String[] args) throws @Test Exception {
        new @Test Main();

        @Test int i = 0, j, k;
        Object o = "";
        @Test String s = (@Test String)o;
        @Test String s2 = (@Test String)o;
        @Test String s3 = (@Test String)o;

        String a = s + s2 + s3;

        try {
            TestInterface t = Arrays::<@Test int[]>sort;
            handleTest(t);
        } finally {
            elementValues();
        }


        try {
            handleTest(Main::testMethod);
        } catch (@Test Exception e) {
            e.printStackTrace();
        }

    }

    @Test2(one = "1", two = true, three = TestEnum.THREE, four = String.class, other = @Test("test"), otherArray = {@Test("x"), @Test("y")})
    private static void elementValues() {
        System.out.println();
    }

    private static void handleTest(@Test TestInterface t2) {
        t2.run(new int[0]);
    }

    @Override
    public void run(int[] data) {

    }

    public static void testMethod(int[] data) {

    }


}
