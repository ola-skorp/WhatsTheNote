package skorp.ola.whatsthenote.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import skorp.ola.whatsthenote.models.NoteModel
import kotlin.math.absoluteValue
import skorp.ola.whatsthenote.R
import skorp.ola.whatsthenote.ui.theme.Purple500
import skorp.ola.whatsthenote.ui.theme.Gray

private const val NOTE_SIZE = 12
private const val LINE_SIZE = 2
private const val NOTE_PADDING = 4

@Composable
fun TrainerScreen(wrongNotes: Set<Int>, randomNote: NoteModel, onNewNote: () -> Unit, onWrongNote: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row {
            ClefColumn(randomNote, true)
            StaffColumnWithNote(randomNote, true)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row {
                (1..4).forEach {
                    PurpleErrorableButton(
                        Modifier.fillMaxWidth(it.toButtonWidthPercent()), stringResource(id = it.toNoteName()), it in wrongNotes
                    ) { if (randomNote.note == it) onNewNote() else onWrongNote(it) }
                }
            }
            Row {
                (5..7).forEach {
                    PurpleErrorableButton(
                        Modifier.fillMaxWidth(it.toButtonWidthPercent()), stringResource(id = it.toNoteName()), it in wrongNotes
                    ) { if (randomNote.note == it) onNewNote() else onWrongNote(it) }
                }
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), onClick = { onNewNote() }) {
                    Text(text = "Skip")
                }
            }
        }
    }
}

private fun Int.toButtonWidthPercent() = when (this) {
    1 -> 0.25f
    2 -> 0.33333f
    3 -> 0.5f
    5 -> 0.25f
    6 -> 0.33333f
    7 -> 0.5f
    else -> 1f
}

private fun Int.toNoteName() = when (this) {
    1 -> R.string.doo
    2 -> R.string.re
    3 -> R.string.mi
    4 -> R.string.fa
    5 -> R.string.sol
    6 -> R.string.la
    else -> R.string.si
}

@Composable
fun NoteDetectorScreen(state: LazyListState, isRecording: Boolean, notes: List<NoteModel>, record: () -> Unit, stop: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Notes(state, notes)
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter), onClick = if (isRecording) stop else record
        ) {
            Text(text = if (isRecording) stringResource(id = R.string.stop) else stringResource(id = R.string.record))
        }
    }
}

@Composable
private fun Notes(state: LazyListState, notes: List<NoteModel> = listOf(NoteModel(2, 4, false, -1.0))) {
    Row {
        ClefColumn(null, false)
        LazyRow(state = state) {
            items(notes) {
                StaffColumnWithNote(it, false)
            }
        }
    }
}

@Composable
private fun StaffColumnWithNote(note: NoteModel?, dontShowUnusedStaffs: Boolean) {
    Box {
        StaffColumn(note, note.getStaff(), dontShowUnusedStaffs)
        val blackAndWhites = note.getBAndW()
        val oct = ((note?.octave ?: 0) - 8).absoluteValue
        val padding = calculatePadding(blackAndWhites, oct)
        Note(padding)
    }
}

@Composable
private fun StaffColumn(note: NoteModel?, staff: Int, dontShowUnusedStaffs: Boolean) {
    Column {
        Staff4(note, !dontShowUnusedStaffs || staff == 4)
        Staff3(note, !dontShowUnusedStaffs || staff == 3)
        Staff2(note, !dontShowUnusedStaffs || staff == 2)
        Staff1(note, !dontShowUnusedStaffs || staff == 1)
    }
}

@Composable
private fun ClefColumn(note: NoteModel?, dontShowUnusedStaffs: Boolean) {
    val staff = note.getStaff()
    Box {
        Row {
            StaffColumn(null, staff, dontShowUnusedStaffs)
            StaffColumn(null, staff, dontShowUnusedStaffs)
            StaffColumn(null, staff, dontShowUnusedStaffs)
        }
        if (staff == 4 || staff == 0)
            Column {
                Spacer(modifier = Modifier.height(25.dp))
                TrebleClef15()
            }
        if (staff == 3 || staff == 0)
            Column {
                Spacer(modifier = Modifier.height(140.dp))
                TrebleClef()
            }
        if (staff == 2 || staff == 0)
            Column {
                Spacer(modifier = Modifier.height(235.dp))
                BassClef()
            }
        if (staff == 1 || staff == 0)
            Column {
                Spacer(modifier = Modifier.height(333.dp))
                BassClef15()
            }
    }
}

@Composable
private fun BassClef15() {
    Box {
        BassClef()
        Column {
            Spacer(modifier = Modifier.height(45.dp))
            Text("15", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BassClef() {
    val h = 50.0
    val w = 50.0
    Canvas(modifier = Modifier
        .height(h.dp)
        .width(w.dp), onDraw = {
        val path = Path()
        path.moveTo((w * 0.2).dp.toPx(), (h * 0.3).dp.toPx())
        path.lineTo((w * 0.15).dp.toPx(), (h * 0.44).dp.toPx())
        path.lineTo(0.dp.toPx(), (h * 0.15).dp.toPx())
        path.lineTo((w * 0.5).dp.toPx(), 0.dp.toPx())
        path.lineTo(w.dp.toPx(), (h * 0.33).dp.toPx())
        path.lineTo((w * 0.1).dp.toPx(), h.dp.toPx())
        drawPath(
            path, Color.Black, style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(20.dp.toPx())
            )
        )
        drawCircle(Color.Black, radius = 17f, center = Offset((w * 0.2).dp.toPx(), (h * 0.3).dp.toPx()))
        drawCircle(Color.Black, radius = 10f, center = Offset(w.dp.toPx(), (h * 0.2).dp.toPx()))
        drawCircle(Color.Black, radius = 10f, center = Offset(w.dp.toPx(), (h * 0.45).dp.toPx()))
    })
}

@Composable
private fun TrebleClef15() {
    Box {
        Text("15", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
        Column {
            Spacer(modifier = Modifier.height(17.dp))
            TrebleClef()
        }
    }
}

@Composable
private fun TrebleClef() {
    val h = 90.0
    val w = 40.0
    Canvas(modifier = Modifier
        .height(h.dp)
        .width(w.dp), onDraw = {
        val path = Path()
        path.moveTo((w * 0.55).dp.toPx(), (h * 0.68).dp.toPx())
        path.lineTo((w * 0.33).dp.toPx(), (h * 0.6).dp.toPx())
        path.lineTo((w * 0.5).dp.toPx(), (h * 0.42).dp.toPx())
        path.lineTo(w.dp.toPx(), (h * 0.6).dp.toPx())
        path.lineTo((w * 0.8).dp.toPx(), (h * 0.82).dp.toPx())
        path.lineTo(0.dp.toPx(), (h * 0.6).dp.toPx())
        path.lineTo((w * 0.5).dp.toPx(), (h * 0.3).dp.toPx())
        path.lineTo((w * 0.8).dp.toPx(), (h * 0.125).dp.toPx())
        path.lineTo((w * 0.3).dp.toPx(), 0.dp.toPx())
        path.lineTo((w * 0.5).dp.toPx(), (h * 0.25).dp.toPx())
        path.lineTo((w * 0.5).dp.toPx(), (h * 0.3).dp.toPx())
        path.lineTo((w * 0.75).dp.toPx(), (h * 0.9).dp.toPx())
        path.lineTo((w * 0.3).dp.toPx(), h.dp.toPx())
        path.lineTo((w * 0.2).dp.toPx(), (h * 0.9).dp.toPx())
        drawPath(
            path, Color.Black, style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(20.dp.toPx())
            )
        )
    })
}

private fun calculatePadding(blackAndWhites: Double, oct: Int) =
    ((blackAndWhites * NOTE_SIZE + blackAndWhites * LINE_SIZE) + oct * (NOTE_SIZE * 3.5 + LINE_SIZE * 3.5)).toInt()

private fun NoteModel?.getStaff() = when (this?.octave) {
    6, 7, 8 -> 4
    5, 4 -> 3
    3, 2 -> 2
    1, 0 -> 1
    else -> 0
}

private fun NoteModel?.getBAndW() = when (this?.note) {
    3 -> 0.5
    2 -> 1.0
    1 -> 1.5
    7 -> -1.5
    6 -> -1.0
    5 -> -0.5
    4 -> 0.0
    else -> 0.0
}

@Composable
private fun Staff4(note: NoteModel?, isActive: Boolean) {
    val la7 = note?.note == 6 && note.octave == 7
    val si7 = note?.note == 7 && note.octave == 7
    val mi8 = note?.note == 3 && note.octave == 8
    val re8 = note?.note == 2 && note.octave == 8
    val do8 = note?.note == 1 && note.octave == 8
    val do6 = note?.note == 1 && note.octave == 6
    White()
    Black(mi8)//ми8
    White() // ре8
    Black(do8 || re8 || mi8) // до8
    White() // си7
    Black(la7 || si7 || do8 || re8 || mi8) // ля7
    White() // соль7
    Staff(isActive) // ми6 фа6 соль6 ля6 си6 до7 ре7 ми7 фа7
    White() // ре6
    Black(do6) // до6
}

@Composable
private fun Staff3(note: NoteModel?, isActive: Boolean) {
    val si5 = note?.note == 7 && note.octave == 5
    val la5 = note?.note == 6 && note.octave == 5
    val do4 = note?.note == 1 && note.octave == 4
    White() //си5
    Black(si5 || la5) //ля5
    White() //соль5
    Staff(isActive) // ми4 фа4 соль4 ля4 си4 до5 ре5 ми5 фа5
    White() // ре4
    Black(do4) // до4
}

@Composable
private fun Staff2(note: NoteModel?, isActive: Boolean) {
    val do2 = note?.note == 1 && note.octave == 2
    val re2 = note?.note == 2 && note.octave == 2
    val mi2 = note?.note == 3 && note.octave == 2
    White() // си3
    Staff(isActive) // соль2 ля2 си2 до3 ре3 ми3 фа3 соль3 ля3
    White() // фа2
    Black(mi2 || re2 || do2) //ми2
    White() //ре2
    Black(do2) //до2
}

@Composable
private fun Staff1(note: NoteModel?, isActive: Boolean) {
    val mi0 = note?.note == 3 && note.octave == 0
    White() //си1
    Staff(isActive) // соль0 ля0 си0 до1 ре1 ми1 фа1 соль1 ля1
    White() // фа0
    Black(mi0) //ми0
    White()
}

@Composable
private fun Staff(isActive: Boolean) {
    repeat(5) {
        Black(isActive)
        if (it != 4)
            White()
    }
}

@Composable
private fun Black(isActive: Boolean) {
    Spacer(
        modifier = Modifier
            .width((NOTE_SIZE + NOTE_PADDING * 2).dp)
            .height(LINE_SIZE.dp)
            .background(if (isActive) Color.Black else Color.White)
    )
}

@Composable
private fun White() {
    Spacer(
        modifier = Modifier
            .width((NOTE_SIZE + NOTE_PADDING * 2).dp)
            .height(NOTE_SIZE.dp)
            .background(Color.White)
    )
}

@Composable
private fun Note(topPadding: Int) {
    Column {
        Spacer(modifier = Modifier.height(topPadding.dp))
        Spacer(
            modifier = Modifier
                .padding(horizontal = NOTE_PADDING.dp)
                .size(NOTE_SIZE.dp)
                .border(LINE_SIZE.dp, Color.Black, CircleShape)
        )
    }
}

@Composable
fun PurpleErrorableButton(modifier: Modifier, text: String, isError: Boolean, onClick: () -> Unit) {
    Button(
        modifier = modifier.height(50.dp), onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isError) Gray else Purple500)
    ) {
        Text(text = text)
    }
}