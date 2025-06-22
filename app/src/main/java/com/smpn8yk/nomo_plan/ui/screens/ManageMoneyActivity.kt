package com.smpn8yk.nomo_plan.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.R
import com.smpn8yk.nomo_plan.data.MoneyPlan
import com.smpn8yk.nomo_plan.data.MoneyPlanStatus
import com.smpn8yk.nomo_plan.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.ui.theme.CoklatKayu
import com.smpn8yk.nomo_plan.ui.theme.IjoBg
import com.smpn8yk.nomo_plan.ui.theme.IjoDaun
import com.smpn8yk.nomo_plan.ui.theme.IjoYes
import com.smpn8yk.nomo_plan.ui.theme.Krem
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

class ManageMoneyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db: MoneyPlanDatabase = databaseBuilder<MoneyPlanDatabase>(
            applicationContext,
            MoneyPlanDatabase::class.java,
            "moneyplan-database"
        ).build()

        setContent {
            NomoPlanTheme {
                ManageMoneyView(
                    onBackPressed = {
                        finish()
                    },
                    onDismissResultDialog = { moneyPlan ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                async { savePlan(db, moneyPlan) }.await()
                                Log.d("TEST_PROGRAM", "Success saving plan..")
                                backToMainActivity()
                            } catch (e: Exception) {
                                Log.d("TEST_PROGRAM", "Error saving plan ${e.message}")
                            }
                        }
                    }
                )
            }
        }
    }

    private suspend fun savePlan(db: MoneyPlanDatabase, moneyPlan: MoneyPlan) {
        db.moneyPlanDao().insert(moneyPlan)
    }

    private fun backToMainActivity() {
        this.finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMoneyView(
    onBackPressed:()->Unit,
    onDismissResultDialog: (moneyPlan: MoneyPlan) -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text =  "Manage Your Money"
                ) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
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
    ){paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = days.intValue.toString(),
                onValueChange = { value ->
                    if (value.isNotEmpty()) {
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
                onValueChange = { value ->
                    if (value.isNotEmpty()) {
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
        }
        ResultDialogView(
            showDialog = showResultDialog.value,
            onDimmis = {
                showResultDialog.value = false
                onDismissResultDialog(getMoneyPlan())
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
fun PreviewManageMoneyView(){
    ManageMoneyView(
        onBackPressed = {},
        onDismissResultDialog = {}
    )
}

@Preview
@Composable
fun PreviewBudgetResultDialogView(){
    ResultDialogContentView(
        onOkClicked = {},
        budget = 10000
    )
}