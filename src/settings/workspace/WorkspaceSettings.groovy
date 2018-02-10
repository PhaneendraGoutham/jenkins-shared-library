package settings.workspace

import constants.GitFlowConstants
import settings.Settings

class WorkspaceSettings extends Settings {
    private String _drive
    private String _root
    private String _leaf

    WorkspaceSettings(def steps,
                      String drive,
                      String root,
                      String leaf) {
        super(steps)
        _drive = drive
        _root = root
        _leaf = leaf
    }

    String customWorkspace
    String artifactsWorkspace

    @Override
    protected void init() {
        customWorkspace = sprintf(
            '%1$s:\\%2$s\\%3$s\\%4$s',
            [
                "${_drive}",
                "${_root}",
                "${_leaf}"?.trim()
                    ? "${_leaf}"
                    : _steps.scm.getUserRemoteConfigs()[0].getUrl()
                    .split('/')
                    .last()
                    .replaceAll('.git', ''),
                getBranchName()
            ])
        artifactsWorkspace = customWorkspace.replaceFirst("${_root}", "tmp")
    }

    private String getBranchName() {
        String branchName = "${_steps.BRANCH_NAME}"
        if ("${branchName}" ==~ GitFlowConstants.DEFAULT_BRANCHING_MODEL) {
            return "${branchName}"
        }

        if ("${branchName}" ==~ GitFlowConstants.GIT_FLOW_TOPIC_BRANCHES) {
            def clean = branchName.replaceFirst("${GitFlowConstants.GIT_FLOW_BRANCH_REPLACE}", "")
            return "${clean}"
        }
    }
}
