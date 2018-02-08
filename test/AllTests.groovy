import junit.framework.Test
import junit.textui.TestRunner
import stages.vcs.VcsRestTest

class AllTests {
    static Test suite() {
        def groovyTestSuite = new GroovyTestSuite()
        groovyTestSuite.addTestSuite(VcsRestTest.class)
        return groovyTestSuite
    }
}