package skorp.ola.whatsthenote.entitylayer

import skorp.ola.whatsthenote.models.NoteModel
import javax.inject.Inject

class FrequencyAnalyser @Inject constructor(): IFrequencyAnalyser {
    private val notes = calculateListOfNotes()

    override fun getRandomNote(): NoteModel = notes.random()

    override fun getNote(frequency: Int): NoteModel? {
        val pair = notes.zipWithNext().find { (a, b) -> frequency >= a.frequency && frequency <= b.frequency }
        return pair?.let {
            val medium = (it.first.frequency + it.second.frequency) / 2
            if (frequency <= medium) it.first else it.second
        }
    }

    private fun calculateListOfNotes(): List<NoteModel> {
        var last = startListOfNotes
        val list = mutableListOf<NoteModel>()
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
                NoteModel(2, 1, true, 38.891),
                NoteModel(2, 1, false, 36.708),
                NoteModel(1, 1, true, 34.648),
                NoteModel(1, 1, false, 32.703),
                NoteModel(7, 0, false, 30.868),
                NoteModel(6, 0, true, 29.135),
                NoteModel(6, 0, false, 27.500),
                NoteModel(5, 0, true, 25.957),
                NoteModel(5, 0, false, 24.500),
                NoteModel(4, 0, true, 23.125),
                NoteModel(4, 0, false, 21.827),
                NoteModel(3, 0, false, 20.602)
            )
}