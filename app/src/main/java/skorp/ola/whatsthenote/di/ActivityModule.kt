package skorp.ola.whatsthenote.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import skorp.ola.whatsthenote.entitylayer.IFrequencyAnalyser
import skorp.ola.whatsthenote.usecases.IStreamAnalyzeUseCase
import skorp.ola.whatsthenote.entitylayer.FrequencyAnalyser
import skorp.ola.whatsthenote.platform.IRecorder
import skorp.ola.whatsthenote.platform.Recorder
import skorp.ola.whatsthenote.usecases.StreamAnalyzeUseCase

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindFrequencyAnalyser(frequencyAnalyser: FrequencyAnalyser): IFrequencyAnalyser
    @Binds
    abstract fun bindStreamAnalyzeUseCase(streamAnalyzeUseCase: StreamAnalyzeUseCase): IStreamAnalyzeUseCase
    @Binds
    abstract fun bindRecorder(recorder: Recorder): IRecorder
}