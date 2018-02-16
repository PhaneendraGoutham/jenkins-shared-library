package post.always

class Notify {
    static def complete(def steps,
                        String root) {
        try {
            def pathname = sprintf(
                '%1$s\\%2$s\\%3$s\\%4$s',
                [
                    root,
                    "${steps.pipelineSettings.gitSettings.repository}",
                    "${steps.pipelineSettings.gitSettings.gitVersion.BranchName}",
                    "${steps.BUILD_NUMBER}"
                ]
            )
            new File("${pathname}").mkdirs()

            def file = new File("${pathname}\\${steps.pipelineSettings.gitSettings.branch}.complete")
            file.write("${steps.pipelineSettings.gitSettings.version}")
        } catch (error) {
            steps.echo "${error.message}"
        }
    }
}
