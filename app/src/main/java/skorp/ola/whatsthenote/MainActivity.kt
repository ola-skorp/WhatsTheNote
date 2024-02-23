package skorp.ola.whatsthenote

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import skorp.ola.whatsthenote.entities.NoteEntity
import skorp.ola.whatsthenote.ui.NoteDetectorScreen
import skorp.ola.whatsthenote.ui.TrainerScreen
import skorp.ola.whatsthenote.ui.theme.WhatsTheNoteTheme
import skorp.ola.whatsthenote.usecases.INoteAnalyseUseCase
import skorp.ola.whatsthenote.usecases.IRecordUseCase
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var recordJob: Job? = null

    @Inject
    lateinit var recorder: IRecordUseCase

    @Inject
    lateinit var noteAnalyseUseCase: INoteAnalyseUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsTheNoteTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val wrongNotes = remember { mutableStateOf(emptySet<Int>()) }
        val randomNote = remember { mutableStateOf(noteAnalyseUseCase.getRandomNote()) }
        val isRecording = remember { mutableStateOf(false) }
        val state = rememberLazyListState()
        val notes = remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
            if (it) record(isRecording, notes, state)
        })
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            TabsScreen(wrongNotes.value, randomNote.value, {
                randomNote.value = noteAnalyseUseCase.getRandomNote()
                wrongNotes.value = emptySet()
            }, {
               wrongNotes.value = wrongNotes.value + it
            }, state, isRecording.value, notes.value, { launcher.launch(Manifest.permission.RECORD_AUDIO) }, {
                recordJob?.cancel()
                isRecording.value = false
            })
        }
    }

    private fun record(isRecording: MutableState<Boolean>, notes: MutableState<List<NoteEntity>>, state: LazyListState) {
        isRecording.value = true
        recordJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                recorder.record { frequency ->
                    noteAnalyseUseCase.getNote(frequency)?.let { n ->
                        notes.value = notes.value.toMutableList().also { list -> list.add(n) }
                        lifecycleScope.launch {
                            state.scrollToItem(notes.value.size - 1)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TabsScreen(wrongNotes: Set<Int>, randomNote: NoteEntity, onNewNote: () -> Unit, onWrongNote: (Int) -> Unit, state: LazyListState, isRecording: Boolean, notes: List<NoteEntity>, record: () -> Unit, stop: () -> Unit) {
        val selectedTabIndex = remember { mutableIntStateOf(0) }
        val tabs = listOf(stringResource(id = R.string.training), stringResource(id = R.string.detector_beta))
        Column {
            TabRow(selectedTabIndex = selectedTabIndex.intValue) {
                tabs.forEachIndexed { index, s ->
                    Tab(text = { Text(s) },
                        selected = selectedTabIndex.intValue == index,
                        onClick = { selectedTabIndex.intValue = index }
                    )
                }
            }
            when (selectedTabIndex.intValue) {
                0 -> TrainerScreen(wrongNotes, randomNote, onNewNote, onWrongNote)
                1 -> NoteDetectorScreen(state, isRecording, notes, record, stop)
            }
        }
    }
}