package skorp.ola.whatsthenote.usecases

interface IRecordUseCase {
    suspend fun record(onResult: (Int) -> Unit)
}