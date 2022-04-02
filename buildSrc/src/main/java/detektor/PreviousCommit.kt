package detektor

import detektor.wo.GitClient
import detektor.wo.Sha

class PreviousCommit: CommitShaProvider {
    override fun get(commandRunner: GitClient.CommandRunner): Sha {
        return commandRunner.executeAndParseFirst(PREV_COMMIT_CMD)
    }
    companion object {
        const val PREV_COMMIT_CMD = "git --no-pager rev-parse HEAD~1"
    }
}