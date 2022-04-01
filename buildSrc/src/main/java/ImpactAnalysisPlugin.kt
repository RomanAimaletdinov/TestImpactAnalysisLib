import com.dropbox.affectedmoduledetector.AffectedModuleDetector
import com.dropbox.affectedmoduledetector.DependencyTracker
import com.dropbox.affectedmoduledetector.isRoot
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

class ImpactAnalysisPlugin : Plugin<Project> {

    companion object {

        const val TASK_GROUP_NAME = "Affected Module Detector"
    }

    override fun apply(project: Project) {
        System.err.println("ROMAN: apply: ImpactAnalysisPlugin")
        require(project.isRoot) {
            "Must be applied to root project, but was found on ${project.path} instead."
        }
        registerDetektTask(project)
        registerAffectedAndroidTests(project)

        project.gradle.projectsEvaluated {
           // AffectedModuleDetector.configure(project.gradle, project)

            filterAndroidTests(project)
            filterTaskIfAffected(project)
        }
    }

    private fun registerDetektTask(rootProject: Project) {
        registerAffectedTestTask(
            testType = TaskType.DetektTask(
                name = "runDetektByImpact",
                group = TASK_GROUP_NAME,
                description = "Run static analysis tool without auto-correction"
            ),
            rootProject = rootProject
        )
    }
    private fun registerAffectedAndroidTests(rootProject: Project) {
        registerAffectedTestTask(
            testType = TaskType.AndroidTestTask(
                name = "runAndroidTestsByImpact",
                group = TASK_GROUP_NAME,
                description = "Run affected android tests"
            ),
            rootProject = rootProject
        )
    }

    private fun registerAffectedTestTask(
        testType: TaskType,
        rootProject: Project
    ) {
        val task = rootProject.tasks.register(testType.name).get()
        task.group = testType.group
        task.description = testType.description

        val pluginIds = setOf("com.android.application", "com.android.library", "java-library", "kotlin")
        rootProject.subprojects {
            this.afterEvaluate {

                when (testType) {
                    is TaskType.AndroidTestTask -> {
                        pluginIds
                            .filterNot { it == "java-library" || it == "kotlin" }
                            .forEach {
                                withPlugin(it, task, testType, this)
                            }
                    }
                    is TaskType.DetektTask -> {
                        pluginIds.forEach {
                            withPlugin(it, task, testType, this)
                        }
                    }
                }
            }
        }
    }

    private fun filterAndroidTests(project: Project) {
//        val tracker = DependencyTracker(project, null)
//        project.tasks.configureEach {
//            if (this.name.contains("AndroidTest")) {
//                tracker.findAllDependents(project).forEach { dependentProject ->
//                    dependentProject.tasks.forEach { dependentTask ->
//                        AffectedModuleDetector.configureTaskGuard(dependentTask)
//                    }
//                }
//                AffectedModuleDetector.configureTaskGuard(this)
//            }
//        }
    }

    private fun filterTaskIfAffected(project: Project) {
        project.tasks.withType(Test::class.java).configureEach {
            AffectedModuleDetector.configureTaskGuard(this)
        }
    }

    private fun withPlugin(pluginId: String, task: Task, testType: TaskType, project: Project) {
        project
            .pluginManager
            .withPlugin(pluginId) {
                val path = getAffectedPath(testType, project)
                if (AffectedModuleDetector.isProjectProvided(project)) {
                    System.err.println("ROMAN: isProjectProvided -> task: $testType")
                    task.dependsOn(path)
                }
                project.afterEvaluate {
                    project.tasks.findByPath(path)?.onlyIf {
                        val v = AffectedModuleDetector.isProjectAffected(project)
                        System.err.println("ROMAN: afterEvaluate -> task: $testType, isAffect: $v")
                        false
                    }
                }
            }
    }

    private fun getAffectedPath(
        testType: TaskType,
        project: Project
    ): String {
        return when (testType) {
            is TaskType.DetektTask -> "${project.path}:testDebugUnitTest"
            is TaskType.AndroidTestTask -> "${project.path}:connectedDebugAndroidTest"
        }
    }
}
