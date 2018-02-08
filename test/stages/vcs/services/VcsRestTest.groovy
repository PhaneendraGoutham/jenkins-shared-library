package stages.vcs.services

import org.junit.*
import static groovy.test.GroovyAssert.*

class VcsRestTest {
    static VcsRest vcsRest = VcsRest.getInstance()

    @BeforeClass
    static void setUpBeforeClass() {
        vcsRest.vcsHost = VcsHost.GITHUB
        vcsRest.scheme = 'https'
        vcsRest.host = 'github.com'
        vcsRest.project = 'chanahl'
        vcsRest.repository = 'jenkins-sample-project'
    }

    @Test
    void test_instance() {
        VcsRest expected = VcsRest.getInstance()
        assertEquals(expected, vcsRest.getInstance())
    }

    @Test
    void test_get_commits_uri() {
        String expected = 'https://api.github.com/repos/chanahl/jenkins-sample-project/commits'
        String actual = vcsRest.getCommitsUri()
        assertEquals(expected, actual)
    }

    @Test
    void test_get_status_uri() {
        String expected = 'https://api.github.com/repos/chanahl/jenkins-sample-project/statuses'
        String actual = vcsRest.getStatusUri()
        assertEquals(expected, actual)
    }
}
