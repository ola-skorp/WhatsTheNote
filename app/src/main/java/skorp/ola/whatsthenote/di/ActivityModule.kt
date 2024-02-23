package skorp.ola.whatsthenote.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import skorp.ola.whatsthenote.usecases.INoteAnalyseUseCase
import skorp.ola.whatsthenote.usecases.IRecordUseCase
import skorp.ola.whatsthenote.usecases.NoteAnalyseUseCase
import skorp.ola.whatsthenote.usecases.RecordUseCase

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindNoteAnalyseUseCase(noteAnalyseUseCase: NoteAnalyseUseCase): INoteAnalyseUseCase
    @Binds
    abstract fun bindRecordUseCase(recordUseCase: RecordUseCase): IRecordUseCase
}