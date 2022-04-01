internal sealed class TaskType(
    open val name: String,
    open val group: String,
    open val description: String
) {

    data class DetektTask(
        override val name: String,
        override val group: String,
        override val description: String
    ) : TaskType(name, group, description)

    data class AndroidTestTask(
        override val name: String,
        override val group: String,
        override val description: String
    ) : TaskType(name, group, description)
}
