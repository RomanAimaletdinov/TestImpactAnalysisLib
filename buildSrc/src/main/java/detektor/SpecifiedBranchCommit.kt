package detektor

import detektor.CommitShaProvider
import detektor.wo.GitClient
import detektor.wo.Sha

class SpecifiedBranchCommit(private val branch: String) : CommitShaProvider {

    override fun get(commandRunner: GitClient.CommandRunner): Sha {
        return commandRunner.executeAndParseFirst("git rev-parse $branch")
    }
}