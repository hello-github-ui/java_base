public class StringBuilderTest {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("123,");
        String substring = sb.substring(0, sb.length() - 1);
        System.out.println(substring);
    }
}
