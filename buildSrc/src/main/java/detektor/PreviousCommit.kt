package detektor

import GitClient
import Sha

class PreviousCommit: CommitShaProvider {
    override fun get(commandRunner: GitClient.CommandRunner): Sha {
        return commandRunner.executeAndParseFirst(PREV_COMMIT_CMD)
    }
    companion object {
        const val PREV_COMMIT_CMD = "git --no-pager rev-parse HEAD~1"
    }
}