package skorp.ola.whatsthenote.usecases

import org.jtransforms.fft.DoubleFFT_1D
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class StreamAnalyzeUseCase @Inject constructor(): IStreamAnalyzeUseCase {
    override fun analyse(size: Int, rate: Int, array: FloatArray): Int {
        val timePerSample = size.toDouble() / rate
        val arr = array.map { it.toDouble() }.toDoubleArray()
        DoubleFFT_1D(size.toLong()).realForward(arr)
        val pairs = arr.toList().windowed(2, 2)
        val ampls = pairs.map { sqrt(it[0].pow(2.0) + it[1].pow(2.0)) }
        val freqs = List(ampls.size) { index -> index.toDouble() / timePerSample }
        val minAmpl = ampls.maxOf { it } * 1 / 2
        val zip = ampls.zip(freqs)
        val debug2 = zip.filter { it.first > 10 && it.first > minAmpl }
        val dict = mutableMapOf<Int, Double>()
        val maxF = debug2.minByOrNull { it.second }?.second ?: 0.0
        debug2.forEach { first ->
            debug2.forEach { second ->
                val res = abs(first.second - second.second)
                if (res > maxF / 4 && res < maxF * 1.1 && first.second.toInt() % res.toInt() < 2) {
                    val addition = (first.first + second.first) / 4
                    dict[res.toInt()] = dict[res.toInt()]?.plus(addition)
                        ?: addition
                }
            }
        }
        val case1 = debug2.sortedByDescending { it.first }
        val case2 = dict.toList().sortedByDescending { it.second }
        return case2.firstOrNull()?.first ?: case1.firstOrNull()?.second?.toInt() ?: 0
    }
}