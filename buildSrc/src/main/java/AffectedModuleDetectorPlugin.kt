/*
 * Copyright (c) 2020, Dropbox, Inc. All rights reserved.
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

/**
 * This plugin creates and registers all affected test tasks.
 * Advantage is speed in not needing to skip modules at a large scale
 *
 *
 * Registers 3 tasks
 * gradlew runAffectedUnitTests - runs jvm tests
 * gradlew runAffectedAndroidTests - runs connected tests
 * gradlew assembleAffectedAndroidTests - assembles but does not run on device tests, useful when working with device labs
 *
 * configure using affected module detector block after applying the plugin
 *
 *   affectedModuleDetector {
 *     baseDir = "${project.rootDir}"
 *     pathsAffectingAllModules = [
 *         "buildSrc/"
 *     ]
 *     logFolder = "${project.rootDir}".
 *   }
 *
 *
 * To enable affected module detection, you need to pass [ENABLE_ARG] into the build as a command line parameter
 * See [AffectedModuleDetector] for additional flags
 */
class AffectedModuleDetectorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        require(project.isRoot) {
            "Must be applied to root project, but was found on ${project.path} instead."
        }

        registerSubmoduleConfiguration(project)
        val mainConfiguration = registerMainConfiguration(project)
        registerCustomTasks(project, mainConfiguration)
        registerTestTasks(project)

        project.gradle.projectsEvaluated {
            AffectedModuleDetector.configure(project.gradle, project)

            filterAndroidTests(project)
            filterJvmTests(project)
        }
    }

    private fun registerCustomTasks(
        rootProject: Project,
        mainConfiguration: AffectedModuleConfiguration
    ) {
        rootProject.afterEvaluate {
            mainConfiguration
                .customTasks
                .forEach { taskType ->
                    registerAffectedTestTask(
                        taskType = taskType,
                        rootProject = rootProject
                    )
                }
        }
    }

    private fun registerTestTasks(rootProject: Project) {
        registerAffectedTestTask(
            taskType = TestTaskType.JVM_TEST,
            rootProject = rootProject
        )
        registerAffectedTestTask(
            taskType = TestTaskType.ANDROID_TEST,
            rootProject = rootProject
        )
        registerAffectedTestTask(
            taskType = TestTaskType.ASSEMBLE_ANDROID_TEST,
            rootProject = rootProject
        )
    }

    internal fun registerAffectedTestTask(
        taskType: BaseTaskType,
        rootProject: Project
    ) {
        val task = rootProject.tasks.register(taskType.impactCommand).get()
        task.group = TASK_GROUP_NAME
        task.description = taskType.description

        rootProject.subprojects {
            project.afterEvaluate {
                val pluginIds = setOf("com.android.application", "com.android.library", "java-library", "kotlin")
                pluginIds.forEach { pluginId ->
                    if (pluginId == "java-library" || pluginId == "kotlin") {
                        if (taskType == TestTaskType.JVM_TEST) {
                            withPlugin(pluginId, task, taskType, this)
                        }
                    } else {
                        withPlugin(pluginId, task, taskType, this)
                    }
                }
            }
        }
    }

    private fun withPlugin(pluginId: String, task: Task, testType: BaseTaskType, project: Project) {
        project.pluginManager.withPlugin(pluginId) {
            getAffectedPath(testType, project)?.let { path ->
                if (AffectedModuleDetector.isProjectProvided(project)) {
                    task.dependsOn(path)
                }
                project.afterEvaluate {
                    project.tasks.findByPath(path)?.onlyIf {
                        AffectedModuleDetector.isProjectAffected(project)
                    }
                }
            }
        }
    }

    private fun getAffectedPath(
        testType: BaseTaskType,
        project: Project
    ): String? {
        val tasks = requireNotNull(
            project.extensions.findByName(AffectedTestConfiguration.name)
        ) {
            "Unable to find ${AffectedTestConfiguration.name} in $project"
        } as AffectedTestConfiguration

        return when (testType) {
            TestTaskType.ANDROID_TEST -> getPathAndTask(project, tasks.runAndroidTestTask)
            TestTaskType.ASSEMBLE_ANDROID_TEST -> getPathAndTask(project, tasks.assembleAndroidTestTask)
            TestTaskType.JVM_TEST -> getPathAndTask(project, tasks.jvmTestTask)
            else -> getPathAndTask(project, testType.originalCommand)
        }
    }

    private fun getPathAndTask(project: Project, task: String?): String? {
        return if (task.isNullOrEmpty()) {
            null
        } else {
            "${project.path}:${task}"
        }
    }

    private fun filterAndroidTests(project: Project) {
        val tracker = DependencyTracker(project, null)
        project.tasks.configureEach {
            if (this.name.contains("AndroidTest")) {
                tracker.findAllDependents(project).forEach { dependentProject ->
                    dependentProject.tasks.forEach { dependentTask ->
                        AffectedModuleDetector.configureTaskGuard(dependentTask)
                    }
                }
                AffectedModuleDetector.configureTaskGuard(this)
            }
        }
    }

    // Only allow unit tests to run if the AffectedModuleDetector says to include them
    private fun filterJvmTests(project: Project) {
        project.tasks.withType(Test::class.java).configureEach {
            AffectedModuleDetector.configureTaskGuard(this)
        }
    }

    private fun registerMainConfiguration(project: Project): AffectedModuleConfiguration {
        val configuration = AffectedModuleConfiguration()
        project.extensions.add(
            AffectedModuleConfiguration.name,
            configuration
        )
        return configuration
    }

    private fun registerSubmoduleConfiguration(project: Project): AffectedTestConfiguration {
        val configuration = AffectedTestConfiguration()
        project.subprojects {
            this.extensions.add(
                AffectedTestConfiguration.name,
                configuration
            )
        }
        return configuration
    }

    companion object {
        const val TASK_GROUP_NAME = "Affected Module Detector"
    }
}
