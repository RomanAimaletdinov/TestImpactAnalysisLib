package detektor

import GitClient
import Sha

interface CommitShaProvider {
    fun get(commandRunner: GitClient.CommandRunner): Sha

    companion object {
        fun fromString(string: String, specifiedBranch: String? = null): CommitShaProvider {
            return when (string) {
                "PreviousCommit" -> PreviousCommit()
                "ForkCommit" -> ForkCommit()
                "SpecifiedBranchCommit" -> {
                    requireNotNull(specifiedBranch) {
                        "Specified branch must be defined"
                    }
                    SpecifiedBranchCommit(specifiedBranch)
                }
                else -> throw IllegalArgumentException("Unsupported compareFrom type")
            }
        }
    }
}

