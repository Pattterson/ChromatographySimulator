import com.hp.hpl.jena.vocabulary.DB;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JavaTests {
    @Test
    public void correctJDKVersionUsed() {
        System.out.println(System.getProperty("java.version"));
        assert (System.getProperty("java.version")=="1.8.0_181");
    }

    @Test
    public void ensureJavaWorking(){
        assertEquals(3,3);
    }

}
