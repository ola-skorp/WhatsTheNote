package skorp.ola.whatsthenote.usecases

interface IStreamAnalyzeUseCase {
    fun analyse(size: Int, rate: Int, array: FloatArray): Int
}