package org.example.project.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.data.model.NutritionInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(viewModel: ChatViewModel) {
    val state        by viewModel.nutritionState.collectAsState()
    var foodInput    by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Analisis Nutrisi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Powered by Gemini AI", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 8.dp)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Input Card ──
            Card(shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Masukkan nama makanan",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = foodInput,
                        onValueChange = { foodInput = it },
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("Contoh: Nasi Goreng, Pizza, Sate Ayam...") },
                        shape         = RoundedCornerShape(12.dp),
                        singleLine    = true
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = { viewModel.analyzeFood(foodInput) },
                        enabled  = foodInput.isNotBlank() && !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color       = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Analisis Nutrisi")
                        }
                    }
                }
            }

            // ── Error Card ──
            state.error?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(err, modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ── Result Card ──
            AnimatedVisibility(
                visible = state.result != null,
                enter   = fadeIn() + expandVertically()
            ) {
                state.result?.let { NutritionResultCard(it) }
            }
        }
    }
}

@Composable
private fun NutritionResultCard(info: NutritionInfo) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(info.name,
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("Per 1 porsi (estimasi)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                NutrientItem("🔥", "${info.calories}", "kcal", "Kalori")
                NutrientItem("💪", "${info.protein}g", "", "Protein")
                NutrientItem("🌾", "${info.carbs}g",   "", "Karbohidrat")
                NutrientItem("🧈", "${info.fat}g",     "", "Lemak")
            }

            if (info.healthTips.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("💡 Tips Kesehatan",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(6.dp))
                info.healthTips.forEach { tip ->
                    Text("• $tip",
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun NutrientItem(emoji: String, value: String, unit: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, style = MaterialTheme.typography.titleLarge)
        Text("$value$unit",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
    }
}
