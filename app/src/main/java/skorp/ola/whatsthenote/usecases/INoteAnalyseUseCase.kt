package skorp.ola.whatsthenote.usecases

import skorp.ola.whatsthenote.entities.NoteEntity

interface INoteAnalyseUseCase {
    fun getRandomNote(): NoteEntity
    fun getNote(frequency: Int): NoteEntity?
}