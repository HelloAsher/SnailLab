package JavaAPI;

/**
 * Created by Asher on 2016/11/28.
 */
public class TestAPI {
    public static void main(String[] args){
//        long nanoTime = System.nanoTime();
//        long currentTimeMillis = System.currentTimeMillis();
//        System.out.println(nanoTime);
//        System.out.println(currentTimeMillis);
        System.out.println(Runtime.getRuntime().maxMemory());
        System.out.println(Runtime.getRuntime().freeMemory());
        System.out.println(Runtime.getRuntime().totalMemory());
    }
}
