import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Endpoint {
    public static void random(Random r) {
        Field seed = null;
        try {
            seed = Random.class.getDeclaredField("seed");
            seed.setAccessible(true);
            seed.set(r, new AtomicLong((0L ^ 25214903917L) & 281474976710655L));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
