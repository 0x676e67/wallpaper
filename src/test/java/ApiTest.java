import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/8/19 下午5:20
 */
public class ApiTest {

    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 100; i++) {
            System.out.print(i + "%");
            TimeUnit.DAYS.sleep(1);
            for (int j = 0; j <= String.valueOf(i).length(); j++) {
                System.out.print("\r");

            }
        }
    }

}
