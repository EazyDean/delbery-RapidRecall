package ca.ualberta.delbery.rapidrecall.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.ualberta.delbery.rapidrecall.model.Attempt
import ca.ualberta.delbery.rapidrecall.model.SessionSummary
import ca.ualberta.delbery.rapidrecall.ui.theme.Mist100
import ca.ualberta.delbery.rapidrecall.ui.theme.Mist300
import ca.ualberta.delbery.rapidrecall.ui.theme.Navy700
import ca.ualberta.delbery.rapidrecall.ui.theme.Navy800
import ca.ualberta.delbery.rapidrecall.ui.theme.Navy900
import ca.ualberta.delbery.rapidrecall.ui.theme.Navy950
import ca.ualberta.delbery.rapidrecall.ui.theme.Orange300
import ca.ualberta.delbery.rapidrecall.ui.theme.Rose300
import ca.ualberta.delbery.rapidrecall.ui.theme.Teal300
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenPadding = 24.dp
private val CardShape = RoundedCornerShape(24.dp)

/**
 * Root UI router. A small explicit state machine is enough for the four screens and keeps the
 * assignment free from unnecessary navigation-framework complexity.
 */
@Composable
fun RapidRecallApp(viewModel: RapidRecallViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = Navy950) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Navy800, Navy950, Navy950),
                    ),
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            when (viewModel.screen) {
                AppScreen.HOME -> HomeScreen(
                    summary = viewModel.summary,
                    onStart = viewModel::openGame,
                    onLog = viewModel::openLog,
                    onSummary = viewModel::openSummary,
                )

                AppScreen.GAME -> GameScreen(viewModel)
                AppScreen.LOG -> AttemptLogScreen(viewModel.attempts, viewModel::goHome)
                AppScreen.SUMMARY -> SummaryScreen(viewModel.summary, viewModel::goHome)
            }
        }
    }
}

@Composable
private fun HomeScreen(
    summary: SessionSummary,
    onStart: () -> Unit,
    onLog: () -> Unit,
    onSummary: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MemoryMark()
            Spacer(Modifier.width(12.dp))
            Text(
                text = "RAPIDRECALL",
                color = Teal300,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "delbery",
                color = Mist300,
                fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(46.dp))
        Text(
            text = "How sharp is\nyour recall?",
            color = Mist100,
            fontSize = 43.sp,
            lineHeight = 47.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1).sp,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Watch the digits. Hold the sequence.\nType it back before it slips away.",
            color = Mist300,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )

        Spacer(Modifier.height(34.dp))
        QuickStats(summary)
        Spacer(Modifier.height(28.dp))

        PrimaryAction(text = "Start a round", onClick = onStart)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryAction(
                text = "Attempt log",
                onClick = onLog,
                modifier = Modifier.weight(1f),
            )
            SecondaryAction(
                text = "Summary",
                onClick = onSummary,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            text = "SESSION ONLY  •  NO DATA LEAVES YOUR DEVICE",
            color = Mist300.copy(alpha = 0.68f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun MemoryMark() {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.semantics { contentDescription = "RapidRecall logo" },
    ) {
        repeat(2) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(2) { column ->
                    Box(
                        Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (row == 1 && column == 1) Orange300 else Teal300),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStats(summary: SessionSummary) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900.copy(alpha = 0.94f)),
        shape = CardShape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(value = summary.totalAttempts.toString(), label = "ATTEMPTS")
            Box(Modifier.size(width = 1.dp, height = 42.dp).background(Navy700))
            StatItem(value = summary.correctAttempts.toString(), label = "CORRECT")
            Box(Modifier.size(width = 1.dp, height = 42.dp).background(Navy700))
            StatItem(value = "${summary.accuracyPercent}%", label = "ACCURACY")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Mist100, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            label,
            color = Mist300,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun GameScreen(viewModel: RapidRecallViewModel) {
    BackHandler(onBack = viewModel::goHome)

    if (viewModel.phase == GamePhase.MEMORIZE) {
        LaunchedEffect(viewModel.playbackId) {
            delay(350)
            repeat(viewModel.selectedLength) { index ->
                viewModel.revealDigit(index)
                delay(720)
                viewModel.hideDigit()
                delay(180)
            }
            viewModel.finishPlayback()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(title = "New round", onBack = viewModel::goHome)
        when (viewModel.phase) {
            GamePhase.READY -> ReadyPhase(
                selectedLength = viewModel.selectedLength,
                onLengthSelected = viewModel::selectLength,
                onBegin = viewModel::beginSequence,
            )

            GamePhase.MEMORIZE -> MemorizePhase(
                digit = viewModel.activeDigit,
                position = viewModel.digitPosition,
                length = viewModel.selectedLength,
            )

            GamePhase.INPUT -> InputPhase(
                length = viewModel.selectedLength,
                guess = viewModel.enteredGuess,
                onGuessChanged = viewModel::updateGuess,
                onSubmit = viewModel::submitGuess,
            )

            GamePhase.FEEDBACK -> FeedbackPhase(
                attempt = requireNotNull(viewModel.feedback),
                onAgain = viewModel::playAgain,
                onHome = viewModel::goHome,
            )
        }
    }
}

@Composable
private fun ReadyPhase(
    selectedLength: Int,
    onLengthSelected: (Int) -> Unit,
    onBegin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding, vertical = 14.dp),
    ) {
        Eyebrow("SET YOUR CHALLENGE")
        Spacer(Modifier.height(10.dp))
        Text(
            "Choose your\nsequence length",
            color = Mist100,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Start small or test your limits with up to 10 digits.",
            color = Mist300,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        )
        Spacer(Modifier.height(28.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 1.dp),
        ) {
            items((1..10).toList()) { length ->
                LengthChoice(
                    length = length,
                    selected = length == selectedLength,
                    onClick = { onLengthSelected(length) },
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = CardShape,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("YOUR SEQUENCE", color = Mist300, fontSize = 10.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(selectedLength) {
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (selectedLength > 8) 9.dp else 12.dp)
                                .clip(CircleShape)
                                .background(if (it == 0) Teal300 else Navy700),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "$selectedLength ${if (selectedLength == 1) "digit" else "digits"}",
                    color = Mist100,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        InfoStrip("Each digit appears for a moment. Input starts after the final digit.")
        Spacer(Modifier.height(24.dp))
        PrimaryAction(text = "Begin sequence", onClick = onBegin)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun LengthChoice(length: Int, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Teal300 else Color.Transparent,
            contentColor = if (selected) Navy950 else Mist100,
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(listOf(if (selected) Teal300 else Navy700, if (selected) Teal300 else Navy700)),
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(52.dp),
    ) {
        Text(length.toString(), fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MemorizePhase(digit: Int?, position: Int, length: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ScreenPadding, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Eyebrow("WATCH CLOSELY")
        Spacer(Modifier.height(8.dp))
        Text("Hold the sequence", color = Mist100, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(
            if (position == 0) "Get ready…" else "Digit $position of $length",
            color = Mist300,
            fontSize = 15.sp,
        )

        Spacer(Modifier.weight(0.8f))
        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CardShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Navy700, Navy900),
                    ),
                )
                .border(1.dp, Teal300.copy(alpha = 0.28f), CardShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = digit?.toString() ?: "•",
                color = if (digit == null) Navy700 else Teal300,
                fontSize = if (digit == null) 64.sp else 104.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(36.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(length) { index ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (index < position) Teal300 else Navy700),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Text("No peeking away now.", color = Mist300, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun InputPhase(
    length: Int,
    guess: String,
    onGuessChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding, vertical = 16.dp),
    ) {
        Eyebrow("YOUR TURN")
        Spacer(Modifier.height(8.dp))
        Text("What did you see?", color = Mist100, fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("Enter all $length digits in the same order.", color = Mist300, fontSize = 15.sp)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = guess,
            onValueChange = onGuessChanged,
            label = { Text("Your sequence") },
            placeholder = { Text("Type $length digits") },
            supportingText = { Text("${guess.length} / $length digits") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        InfoStrip("Leading zeroes count. Your answer must contain exactly $length digits.")
        Spacer(Modifier.height(28.dp))
        PrimaryAction(
            text = "Check my answer",
            enabled = guess.length == length,
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
        )
    }
}

@Composable
private fun FeedbackPhase(attempt: Attempt, onAgain: () -> Unit, onHome: () -> Unit) {
    val accent = if (attempt.isCorrect) Teal300 else Rose300
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f))
                .border(1.dp, accent.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (attempt.isCorrect) "✓" else "×",
                color = accent,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            if (attempt.isCorrect) "Nailed it." else "Not this time.",
            color = Mist100,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (attempt.isCorrect) "That sequence is locked in." else "Take a breath and give it another go.",
            color = Mist300,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(26.dp))
        ComparisonCard(attempt)
        Spacer(Modifier.height(26.dp))
        PrimaryAction(text = "Play another round", onClick = onAgain)
        Spacer(Modifier.height(12.dp))
        SecondaryAction(text = "Back to home", onClick = onHome, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ComparisonCard(attempt: Attempt) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = CardShape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(22.dp)) {
            ComparisonRow(label = "TARGET", value = attempt.targetSequence, color = Teal300)
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Navy700)
            ComparisonRow(
                label = "YOUR ANSWER",
                value = attempt.userInput,
                color = if (attempt.isCorrect) Teal300 else Rose300,
            )
        }
    }
}

@Composable
private fun ComparisonRow(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Mist300, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
    }
}

@Composable
private fun AttemptLogScreen(attempts: List<Attempt>, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Attempt log", onBack = onBack)
        if (attempts.isEmpty()) {
            EmptyState(
                title = "No attempts yet",
                body = "Complete a round and its result will appear here.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        "${attempts.size} completed ${if (attempts.size == 1) "round" else "rounds"} this session",
                        color = Mist300,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                itemsIndexed(attempts.asReversed()) { reverseIndex, attempt ->
                    AttemptCard(number = attempts.size - reverseIndex, attempt = attempt)
                }
            }
        }
    }
}

@Composable
private fun AttemptCard(number: Int, attempt: Attempt) {
    val accent = if (attempt.isCorrect) Teal300 else Rose300
    val formatter = SimpleDateFormat("MMM d, h:mm:ss a", Locale.getDefault())
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (attempt.isCorrect) "✓" else "×", color = accent, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Attempt $number", color = Mist100, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        formatter.format(Date(attempt.timestampMillis)),
                        color = Mist300,
                        fontSize = 11.sp,
                    )
                }
                Text(
                    "${attempt.sequenceLength} DIGITS",
                    color = accent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 14.dp), color = Navy700)
            Row {
                LogValue("TARGET", attempt.targetSequence, Modifier.weight(1f))
                LogValue("ANSWER", attempt.userInput, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LogValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, color = Mist300, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            color = Mist100,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SummaryScreen(summary: SessionSummary, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Session summary", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(190.dp)) {
                CircularProgressIndicator(
                    progress = { summary.accuracyFraction },
                    modifier = Modifier.fillMaxSize(),
                    color = Teal300,
                    trackColor = Navy700,
                    strokeWidth = 13.dp,
                    strokeCap = StrokeCap.Round,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${summary.accuracyPercent}%",
                        color = Mist100,
                        fontSize = 43.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("ACCURACY", color = Mist300, fontSize = 10.sp, letterSpacing = 1.4.sp)
                }
            }
            Spacer(Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryMetric(
                    value = summary.totalAttempts.toString(),
                    label = "TOTAL ATTEMPTS",
                    accent = Orange300,
                    modifier = Modifier.weight(1f),
                )
                SummaryMetric(
                    value = summary.correctAttempts.toString(),
                    label = "CORRECT",
                    accent = Teal300,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(22.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = CardShape,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(22.dp)) {
                    Text("SESSION NOTE", color = Teal300, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(9.dp))
                    Text(
                        summaryMessage(summary),
                        color = Mist100,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            SecondaryAction(text = "Back to home", onClick = onBack, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun summaryMessage(summary: SessionSummary): String = when {
    summary.totalAttempts == 0 -> "No rounds completed yet. Start with four digits and build from there."
    summary.accuracyPercent == 100 -> "Perfect recall so far. Try increasing the sequence length for a tougher challenge."
    summary.accuracyPercent >= 70 -> "Strong session. A slightly longer sequence could be your next step."
    else -> "Memory improves with repetition. Keep the sequence short, find your rhythm, then level up."
}

@Composable
private fun SummaryMetric(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
    ) {
        Column(Modifier.padding(20.dp)) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.height(12.dp))
            Text(value, color = Mist100, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Mist300, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Navy800),
            contentAlignment = Alignment.Center,
        ) {
            Text("—", color = Teal300, fontSize = 28.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text(title, color = Mist100, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(body, color = Mist300, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.linearGradient(listOf(Navy700, Navy700)),
            ),
            modifier = Modifier.size(42.dp),
        ) {
            Text("‹", color = Mist100, fontSize = 30.sp, lineHeight = 30.sp)
        }
        Text(
            title,
            color = Mist100,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(42.dp))
    }
}

@Composable
private fun Eyebrow(text: String) {
    Text(
        text,
        color = Teal300,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp,
    )
}

@Composable
private fun InfoStrip(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Teal300.copy(alpha = 0.08f))
            .border(1.dp, Teal300.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(Teal300))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Mist300, fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun PrimaryAction(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Teal300,
            contentColor = Navy950,
            disabledContainerColor = Navy700,
            disabledContentColor = Mist300,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SecondaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Mist100),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(listOf(Navy700, Navy700)),
        ),
        modifier = modifier.height(54.dp),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
