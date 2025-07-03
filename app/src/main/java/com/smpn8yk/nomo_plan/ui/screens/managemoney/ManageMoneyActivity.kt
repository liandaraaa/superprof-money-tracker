package com.smpn8yk.nomo_plan.ui.screens.managemoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.ui.theme.IjoDaun
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import com.smpn8yk.nomo_plan.ui.viewmodels.ManageMoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class ManageMoneyActivity : ComponentActivity() {

    private val manageMoneyViewModel by viewModels<ManageMoneyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NomoPlanTheme {
                ManageMoneyView(
                    viewModel = manageMoneyViewModel,
                    onBackPressed = {
                        backToMainActivity()
                    }
                )
            }
        }
    }

    private fun backToMainActivity() {
        this.finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMoneyView(
    viewModel: ManageMoneyViewModel,
    onBackPressed: () -> Unit
) {
    val manageMoneyUiState by viewModel.manageMoneyUiState.collectAsState()
    val showResultDialog = remember { mutableStateOf(false) }

    val days = remember { mutableIntStateOf(0) }
    val nominal = remember { mutableIntStateOf(0) }
    val budget = remember { mutableIntStateOf(0) }

    fun calculateBudget(days: Int, nominal: Int): Int {
        return nominal / days
    }

    fun getRangeDates(): List<String> {
        val startDate = LocalDate.now()
        val nextDates = mutableListOf<String>()
        for (i in 0..<days.intValue) {
            nextDates.add(startDate.plusDays(i.toLong()).toString())
        }
        return nextDates
    }

    fun getMoneyPlan(): MoneyPlan {
        return MoneyPlan(
            total_days = days.intValue,
            nominal = nominal.intValue,
            budget = budget.intValue,
            range_dates = getRangeDates()
        )
    }

    if(manageMoneyUiState == "COMPLETED"){
        onBackPressed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Your Money",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    budget.intValue = calculateBudget(days.intValue, nominal.intValue)
                    showResultDialog.value = true
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Submit"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = days.intValue.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    if (value.isNotEmpty() && value.all { it.isDigit() }) {
                        days.intValue = value.toInt()
                    }
                },
                label = {
                    Text("Hari")
                },
                placeholder = {
                    Text("Masukan jumlah hari")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            OutlinedTextField(
                value = nominal.intValue.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    if (value.isNotEmpty() && value.all { it.isDigit() }) {
                        nominal.intValue = value.toInt()
                    }
                },
                label = {
                    Text("Nominal")
                },
                placeholder = {
                    Text("Masukan nominal uang yang akan kamu gunakan !")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Text(
                text = manageMoneyUiState
            )
        }
        ResultDialogView(
            showDialog = showResultDialog.value,
            onDimmis = {
                showResultDialog.value = false
                viewModel.savePlan(getMoneyPlan())
            },
            budget = budget.intValue
        )
    }
}

@Composable
fun ResultDialogView(
    showDialog: Boolean,
    onDimmis: () -> Unit,
    budget: Int
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {},
        ) {
            ResultDialogContentView(
                onOkClicked = onDimmis,
                budget = budget
            )
        }
    }
}


@Composable
fun ResultDialogContentView(
    onOkClicked: () -> Unit,
    budget: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = IjoDaun
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Result",
                color = Color.White
            )
            Card(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Rp $budget / hari",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "jangan melebihi batas harian, ya !"
                    )
                }
            }
            Button(
                onClick = {
                    onOkClicked()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "OK"
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBudgetResultDialogView() {
    ResultDialogContentView(
        onOkClicked = {},
        budget = 10000
    )
}