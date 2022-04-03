internal enum class TestTaskType(
    override val impactCommand: String,
    override var originalCommand: String,
    override val description: String
): BaseTaskType {

    DETEKT_TASK(
        impactCommand = "runDetektByImpact",
        originalCommand = "detekt",
        description = "Run static analysis tool without auto-correction."
    ),

    ANDROID_TEST(
        impactCommand = "runAffectedAndroidTests",
        originalCommand = "connectedDebugAndroidTest",
        description = "Runs all affected Android Tests. Requires a connected device."
    ),

    ASSEMBLE_ANDROID_TEST(
        impactCommand = "assembleAffectedAndroidTests",
        originalCommand = "assembleDebugAndroidTest",
        description = "Assembles all affected Android Tests.  Useful when working with device labs."
    ),

    JVM_TEST(
        impactCommand = "runAffectedUnitTests",
        originalCommand = "testDebugUnitTest",
        description = "Runs all affected unit tests."
    )
}
