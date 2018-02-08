package stages.vcs

import stages.vcs.services.VcsHost
import stages.vcs.services.VcsRest

class VcsRestTest extends GroovyTestCase {
    final VcsRest vcsRest = VcsRest.getInstance()
    static final VcsHost vcsHost = VcsHost.GITHUB
    static final String scheme = 'scheme'
    static final String host = 'host'
    static final String project = 'project'
    static final String repository = 'repository'

    void setUp() {
        println "setUp"
        vcsRest.vcsHost = vcsHost
        vcsRest.scheme = scheme
        vcsRest.host = host
        vcsRest.project = project
        vcsRest.repository = repository
    }

    void test_instance() {
        println "test_instance"
        assertEquals(vcsRest.getInstance(), VcsRest.getInstance())
    }

    void test_get_commits_uri() {
        println "test_get_commits_uri"
        String commitsUri = VcsRest.instance.getCommitsUri()
        String expected = "${scheme}://api.${host}/repos/${project}/${repository}/commits"
        assertToString(commitsUri, expected)
    }

    void test_get_status_uri() {
        println "test_get_status_uri"
        String statusUri = VcsRest.instance.getStatusUri()
        String expected = "${scheme}://api.${host}/repos/${project}/${repository}/statuses"
        assertToString(statusUri, expected)
    }

    void tearDown() {
        println "tearDown"
    }
}
