import constants.GitFlowConstants

def call(String branch) {
    if ("${branch}" ==~ GitFlowConstants.DEFAULT_BRANCHING_MODEL) {
        return "${branch}"
    }

    if ("${branch}" ==~ GitFlowConstants.GIT_FLOW_TOPIC_BRANCHES) {
        def clean = branch.replaceFirst("${GitFlowConstants.GIT_FLOW_BRANCH_REPLACE}", "")
        return "${clean}"
    }
}
