package constants

class GitFlowConstants {
    static final String DEVELOP = 'develop'
    static final String FEATURE = 'feature'
    static final String HOTFIX = 'hotfix'
    static final String MASTER = 'master'
    static final String RELEASE = 'release'

    static final String DEFAULT_BRANCHING_MODEL = ~/(develop|master)/
    static final String GIT_FLOW_TOPIC_BRANCHES = ~/(feature\\/(.*)|release\\/(.*)|hotfix\\/(.*))/
    static final String GIT_FLOW_BRANCH_REPLACE = ~/(feature|release|hotfix)\\//
}
