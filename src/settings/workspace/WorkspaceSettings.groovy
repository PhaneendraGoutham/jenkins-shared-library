package settings.workspace

import constants.GitFlowConstants
import constants.PipelineConstants
import settings.Settings

import java.util.regex.Pattern

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
    String branch

    void clean() {
        if (!_steps.currentBuild.currentResult == PipelineConstants.SUCCESS ||
            !_steps.currentBuild.currentResult == PipelineConstants.UNSTABLE) {
            _steps.echo "Not cleaning workspaces as result is [${_steps.currentBuild.currentResult}]."
        }

        String workspace = customWorkspace - branch
        try {
            _steps.steps.dir(workspace) {
                File workspaceDirectory = new File("${workspace}")
                String[] branchDirectories = workspaceDirectory.list()
                for (def name in branchDirectories) {
                    if (name.startsWith("${branch}")) {
                        _steps.echo "Deleting custom workspace branch directory [${name}]."
                        File branchDirectory = new File("${workspace}", "${name}")
                        branchDirectory.deleteDir()
                    }
                }
            }

            File artifactsDirectory = new File("${artifactsWorkspace}")
            if (artifactsDirectory.exists()) {
                _steps.echo "Deleting artifacts workspace [${artifactsDirectory.getAbsolutePath()}]."
                artifactsDirectory.deleteDir()
            }
        }
        catch (error) {
            _steps.echo "${error}"
        }
    }

    @Override
    protected void init() {
        branch = getBranchName()
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
                branch
            ])

        artifactsWorkspace = customWorkspace.replaceFirst(Pattern.quote('src'), 'out')
        File artifactsWorkspaceDirectory = new File("${artifactsWorkspace}")
        if (artifactsWorkspaceDirectory.exists() && artifactsWorkspaceDirectory.isDirectory()) {
            artifactsWorkspaceDirectory.deleteDir()
        }
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
