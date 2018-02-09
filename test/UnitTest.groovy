import org.junit.*
import static groovy.test.GroovyAssert.*

abstract class UnitTest implements Serializable {
    @BeforeClass
    abstract static void setUpBeforeClass()
}
