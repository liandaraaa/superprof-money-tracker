package com.smpn8yk.nomo_plan.ui.screens.dailytrackerexpense

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smpn8yk.nomo_plan.data.local.entity.Expense
import com.smpn8yk.nomo_plan.data.local.entity.ExpenseReportStatus
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlanStatus
import com.smpn8yk.nomo_plan.ui.theme.IjoYes
import com.smpn8yk.nomo_plan.ui.theme.Krem
import com.smpn8yk.nomo_plan.ui.theme.MerahNo
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import com.smpn8yk.nomo_plan.ui.viewmodels.DailyTrackerExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyTrackerExpenseActivity : ComponentActivity() {

    private val dailyTrackerExpenseViewModel by viewModels<DailyTrackerExpenseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentPlanId = intent.getIntExtra("EXTRA_PLAN_ID", 0)
        val selectedDate = intent.getStringExtra("EXTRA_DATE")

        dailyTrackerExpenseViewModel.setCurrentPlanId(currentPlanId)

        setContent {
            NomoPlanTheme {
                DailyTrackerExpenseView(
                    viewModel = dailyTrackerExpenseViewModel,
                    currentPlanId = currentPlanId,
                    selectedDate = selectedDate.orEmpty(),
                    onBackPressed = {
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTrackerExpenseView(
    viewModel: DailyTrackerExpenseViewModel,
    currentPlanId: Int,
    selectedDate: String,
    onBackPressed: () -> Unit
) {
    val plansExpenses by viewModel.currentMoneyPlanExpenses.collectAsState()
    val saveExpenseUiState by viewModel.saveExpenseUiState.collectAsState()
    val updateStatusExpenseUiState by viewModel.updateStatusExpenseUiState.collectAsState()

    val showInputExpenseDialog = remember { mutableStateOf(false) }
    val showCompleteReportDialog = remember { mutableStateOf(false) }

    val disableCompleteButton = remember { mutableStateOf(false) }

    val isOverBudget = remember { mutableStateOf(false) }
    val totalExpenses = remember { mutableStateOf(0) }

    val currentExpenses =
        plansExpenses.expenses.filter { it.date == selectedDate }

    val isMoneyPlanInRangeDates =
        plansExpenses.plan.range_dates.find { it == selectedDate }

    if (saveExpenseUiState == "COMPLETED") {
        showInputExpenseDialog.value = false
    }

    if (updateStatusExpenseUiState == "COMPLETED") {
        showCompleteReportDialog.value = false
        disableCompleteButton.value = true
    }

    fun checkReport() {
        val total =
            plansExpenses.expenses.map { it.price }.reduce { acc, price -> acc + price }
        totalExpenses.value = total
        isOverBudget.value = total > plansExpenses.plan.budget
    }

    if(plansExpenses.expenses.isNotEmpty()){
        checkReport()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = "Daily Expenses Report",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
        floatingActionButton = {
            if (isMoneyPlanInRangeDates !== null && (currentExpenses.isEmpty() || currentExpenses.find { it.report_status == ExpenseReportStatus.EMPTY.name } != null)) {
                FloatingActionButton(
                    onClick = { showInputExpenseDialog.value = true },
                ) {
                    Icon(Icons.Filled.Add, "Floating action button.")
                }
            }
        },
        bottomBar = {
            if (isMoneyPlanInRangeDates !== null) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ){
                    Text(
                        text = "Total Expenses : Rp ${totalExpenses.value}",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Remaining Budget : Rp ${plansExpenses.plan.budget - totalExpenses.value}",
                        color = if(isOverBudget.value){
                            MerahNo
                        }else{
                            IjoYes
                        }
                    )
                    Button(
                        enabled = !disableCompleteButton.value,
                        onClick = {
                            showCompleteReportDialog.value = true
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(disableCompleteButton.value && isOverBudget.value){
                                MerahNo
                            }else{
                                IjoYes
                            }
                        )
                    ) {
                        Text(
                            text = if(!disableCompleteButton.value){
                                "Complete Report"
                            }else if(disableCompleteButton.value && isOverBudget.value){
                                "You out of your budget"
                            }else{
                                "Congrats! you on budget"
                            }
                        )
                    }
                }
            }
        },
    ) { padding ->
        if (isMoneyPlanInRangeDates == null) {
            NoMoneyPlanView(padding)
        } else {
            disableCompleteButton.value =
                currentExpenses.find { it.report_status == ExpenseReportStatus.EMPTY.name } == null
            if (currentExpenses.isEmpty()) {
                EmptyMoneyPlanView(padding)
            } else {
                MoneyPlanListView(padding, currentExpenses)
                CompleteReportDialogView(
                    showDialog = showCompleteReportDialog.value,
                    onDismiss = { isOverBudget ->
                        val newExpenseReportStatus =
                            if (isOverBudget) ExpenseReportStatus.FAILED else ExpenseReportStatus.SUCCESS
                        val newPlanStatus = MoneyPlanStatus.COMPLETE
                        viewModel.updatePlanStatus(
                            selectedDate = selectedDate,
                            reportStatus = newExpenseReportStatus,
                            planId = currentPlanId,
                            status = newPlanStatus
                        )
                    },
                    isOverBudget = isOverBudget.value
                )
            }
            InputExpenseDialogView(
                showDialog = showInputExpenseDialog.value,
                onDismiss = { expense ->
                    if (expense !== null) {
                        viewModel.saveExpense(expense)
                    } else {
                        showInputExpenseDialog.value = false
                    }
                },
                planId = currentPlanId,
                selectedDate = selectedDate
            )
        }
    }
}

@Composable
fun NoMoneyPlanView(padding: PaddingValues) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ops! Kamu belum dapat membuat pengeluaran",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tanggal yang kamu pilih tidak ada dalam perencanaanmu"
            )
        }
    }
}

@Composable
fun EmptyMoneyPlanView(padding: PaddingValues) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pengeluaranmu hari ini masih kosong",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Silahkan buat laporan pengeluaran mu sekarang juga"
            )
        }
    }
}

@Composable
fun MoneyPlanListView(padding: PaddingValues, expenses: List<Expense>) {
    LazyColumn(
        contentPadding = padding
    ) {
        items(expenses) { expense ->
            ExpenseItemView(expense)
        }
    }
}

@Composable
fun ExpenseItemView(
    expense: Expense
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = expense.item,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Rp ${expense.price}"
            )
        }
    }
}

@Preview
@Composable
fun PreviewExpenseItemView() {
    ExpenseItemView(Expense(null, null, "", "Roti", 1000, ExpenseReportStatus.EMPTY.name))
}

@Composable
fun InputExpenseDialogView(
    showDialog: Boolean,
    onDismiss: (expense: Expense?) -> Unit,
    planId: Int,
    selectedDate: String
) {
    if (showDialog) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = {},
        ) {
            InputExpenseDialogContentView(
                planId = planId,
                selectedDate = selectedDate,
                onOkClicked = onDismiss,
                onDismiss = {
                    onDismiss(null)
                }
            )
        }
    }

}


@Composable
fun InputExpenseDialogContentView(
    planId: Int,
    selectedDate: String,
    onOkClicked: (expense: Expense) -> Unit,
    onDismiss: () -> Unit
) {

    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableIntStateOf(0) }

    fun getExpense(): Expense {
        return Expense(
            expense_id = null,
            plan_id = planId,
            date = selectedDate,
            item = itemName.value,
            price = itemPrice.intValue,
            report_status = ExpenseReportStatus.EMPTY.name
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Krem
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
            Text(
                text = "Input Expense",
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = itemName.value,
                    onValueChange = { value ->
                        if (value.isNotEmpty()) {
                            itemName.value = value
                        }
                    },
                    label = {
                        Text("Item")
                    },
                    placeholder = {
                        Text("Masukan nama item")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )
                OutlinedTextField(
                    value = itemPrice.intValue.toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { value ->
                        if (value.isNotEmpty() && value.all { it.isDigit() }) {
                            itemPrice.intValue = value.toInt()
                        }
                    },
                    label = {
                        Text("Harga")
                    },
                    placeholder = {
                        Text("Masukan harga item")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
            Button(
                onClick = {
                    onOkClicked(getExpense())
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
fun PreviewInputExpenseContentViewDialog() {
    InputExpenseDialogContentView(
        planId = 0,
        onDismiss = {},
        onOkClicked = {},
        selectedDate = ""
    )
}

@Composable
fun CompleteReportDialogView(
    showDialog: Boolean,
    onDismiss: (isOverBudget: Boolean) -> Unit,
    isOverBudget: Boolean
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {},
        ) {
            CompleteReportDialogContentView(
                isOverBudget = isOverBudget,
                onOkClicked = onDismiss
            )
        }
    }

}


@Composable
fun CompleteReportDialogContentView(
    isOverBudget: Boolean,
    onOkClicked: (isOverBudget: Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) MerahNo else IjoYes
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isOverBudget) "Pengeluaran kamu lebih besar dari budget. Yuk perbaiki, kamu bisa!" else "Kamu Hebat! Kamu sudah bisa mengatur pengeluranmu dengan baik",
            )
            Button(
                onClick = {
                    onOkClicked(isOverBudget)
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