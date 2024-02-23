package skorp.ola.whatsthenote.usecases

import skorp.ola.whatsthenote.entities.NoteEntity
import javax.inject.Inject

class NoteAnalyseUseCase @Inject constructor(): INoteAnalyseUseCase {
    private val notes = calculateListOfNotes()

    override fun getRandomNote(): NoteEntity = notes.random()

    override fun getNote(frequency: Int): NoteEntity? {
        val pair = notes.zipWithNext().find { (a, b) -> frequency >= a.frequency && frequency <= b.frequency }
        return pair?.let {
            val medium = (it.first.frequency + it.second.frequency) / 2
            if (frequency <= medium) it.first else it.second
        }
    }

    private fun calculateListOfNotes(): List<NoteEntity> {
        var last = startListOfNotes
        val list = mutableListOf<NoteEntity>()
        list.addAll(last)
        while (list.none { it.note == 3 && it.octave == 8 }) {
            val new = last.map { it.copy(octave = it.octave + 1, frequency = it.frequency * 2) }
            list.addAll(new)
            last = new
        }
        return list.sortedBy { it.frequency }.filter { !(it.note > 3 && it.octave == 8 || it.octave > 8) }
    }

    private val startListOfNotes
        get() =
            listOf(
                NoteEntity(2, 1, true, 38.891),
                NoteEntity(2, 1, false, 36.708),
                NoteEntity(1, 1, true, 34.648),
                NoteEntity(1, 1, false, 32.703),
                NoteEntity(7, 0, false, 30.868),
                NoteEntity(6, 0, true, 29.135),
                NoteEntity(6, 0, false, 27.500),
                NoteEntity(5, 0, true, 25.957),
                NoteEntity(5, 0, false, 24.500),
                NoteEntity(4, 0, true, 23.125),
                NoteEntity(4, 0, false, 21.827),
                NoteEntity(3, 0, false, 20.602)
            )
}