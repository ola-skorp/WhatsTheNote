package skorp.ola.whatsthenote.platform

interface IRecorder {
    suspend fun record(onResult: (Int) -> Unit)
}