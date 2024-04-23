package skorp.ola.whatsthenote.entitylayer

import skorp.ola.whatsthenote.models.NoteModel

interface IFrequencyAnalyser {
    fun getRandomNote(): NoteModel
    fun getNote(frequency: Int): NoteModel?
}