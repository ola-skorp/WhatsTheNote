package skorp.ola.whatsthenote.platform

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import skorp.ola.whatsthenote.usecases.IStreamAnalyzeUseCase
import javax.inject.Inject

class Recorder @Inject constructor(private val streamAnalyzeUseCase: IStreamAnalyzeUseCase): IRecorder{
    @SuppressLint("MissingPermission")
    private val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, 2048)

    override suspend fun record(onResult: (Int) -> Unit) {
        val fBuffer = FloatArray(SIZE)
        recorder.startRecording()
        withContext(Dispatchers.Default) {
            try {
                while (isActive) {
                    recorder.read(fBuffer, 0, SIZE, AudioRecord.READ_BLOCKING)
                    val result = streamAnalyzeUseCase.analyse(SIZE, RATE, fBuffer)
                    onResult(result)
                }
            } finally {
                recorder.stop()
            }
        }
    }

    companion object{
        private const val SIZE = 16182
        private const val RATE = 44100
    }
}