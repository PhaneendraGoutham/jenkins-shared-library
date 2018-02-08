def call(String drive,
         String root,
         String leaf) {
    def directory = sprintf(
        '%1$s:\\%2$s\\%3$s',
        [
            "${drive}",
            "${root}",
            "${leaf}"?.trim()
                ? "${leaf}"
                : scm.getUserRemoteConfigs()[0].getUrl().split('/').last().replaceAll('.git', '')
        ])
    return directory
}
