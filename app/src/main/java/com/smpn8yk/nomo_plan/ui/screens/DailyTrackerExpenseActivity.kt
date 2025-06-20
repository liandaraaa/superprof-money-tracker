package com.smpn8yk.nomo_plan.ui.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.data.Expense
import com.smpn8yk.nomo_plan.data.MoneyPlanWithExpenses
import com.smpn8yk.nomo_plan.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.ui.MyEventListener
import com.smpn8yk.nomo_plan.ui.theme.IjoBg
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DailyTrackerExpenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentPlanId = intent.getIntExtra("EXTRA_PLAN_ID",0)
        val selectedDate = intent.getStringExtra("EXTRA_DATE")

        Log.d("TEST_PROGRAM", "currentPlanId : $currentPlanId")
        Log.d("TEST_PROGRAM", "selectedDate : $selectedDate")

        setContent {
            NomoPlanTheme {
                DailyTrackerExpenseView(
                    context = this,
                    currentPlanId = currentPlanId,
                    selectedDate = selectedDate.orEmpty()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTrackerExpenseView(
    context: Context,
    currentPlanId: Int,
    selectedDate: String
) {

    val plansExpenses = remember {
        mutableStateOf(
            MoneyPlanWithExpenses()
        )
    }
    val showInputExpenseDialog = remember { mutableStateOf(false) }
    val showCompleteReportDialog = remember { mutableStateOf(false) }

    val db: MoneyPlanDatabase = databaseBuilder<MoneyPlanDatabase>(
        context,
        MoneyPlanDatabase::class.java,
        "moneyplan-database"
    ).build()

    fun checkMoneyPlanExist() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moneyPlanWithExpenses =
                    async { db.moneyPlanDao().getMoneyPlanWithExpenses(currentPlanId,selectedDate) }.await()
                Log.d("TEST_PROGRAM", "check moneyPLanExpenses $moneyPlanWithExpenses")
                plansExpenses.value = moneyPlanWithExpenses ?: MoneyPlanWithExpenses()
            } catch (e: Exception) {
                Log.e("TEST_PROGRAM", "error to moneyPLanExpenses ${e.message}")
            }
        }
    }

    fun saveExpense(expense: Expense) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                async { db.moneyPlanDao().insertExpense(expense) }.await()
                Log.d("TEST_PROGRAM", "Success saving expense..")
                showInputExpenseDialog.value = false
                checkMoneyPlanExist()
            } catch (e: Exception) {
                Log.d("TEST_PROGRAM", "Error saving plan ${e.message}")
            }
        }
    }


    Log.d("TEST_PROGRAM", "get plan expenses $plansExpenses")

    MyEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                checkMoneyPlanExist()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Daily Expenses Report")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showInputExpenseDialog.value = true },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    showCompleteReportDialog.value = true
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Complete Report")
            }
        },
    ) { padding ->
        if(plansExpenses.value.expenses.isEmpty()){
            Text(
                text = "Belum ada pengeluaran"
            )
        }else{
            LazyColumn(
                contentPadding = padding
            ) {
                items(plansExpenses.value.expenses) { expense ->
                    ExpenseItemView(expense)
                }
            }
        }
        InputExpenseDialogView(
            showDialog = showInputExpenseDialog.value,
            onDismiss = { expense ->
                saveExpense(expense)
            },
            planId = currentPlanId
        )
        CompleteReportDialogView(
            showDialog = showCompleteReportDialog.value,
            onDismiss = {
                showCompleteReportDialog.value = false
            },
            currentPlanExpenses = plansExpenses.value
        )
    }
}

@Composable
fun ExpenseItemView(
    expense: Expense
){
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ){
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
fun PreviewExpenseItemView(){
    ExpenseItemView(Expense(null,null,"Roti",1000))
}

@Composable
fun InputExpenseDialogView(
    showDialog: Boolean,
    onDismiss:(expense:Expense)->Unit,
    planId: Int
) {
    if(showDialog){
        Dialog(
            onDismissRequest = {},
        ) {
            InputExpenseDialogContentView(
                planId = planId,
                onOkClicked = onDismiss
            )
        }
    }

}


@Composable
fun InputExpenseDialogContentView(
    planId:Int,
    onOkClicked:(expense:Expense)->Unit
){

    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableIntStateOf(0) }

    fun getExpense():Expense{
        return Expense(
            expense_id = null,
            plan_id = planId,
            item = itemName.value,
            price = itemPrice.intValue
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = IjoBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Input Expense",
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                )
                OutlinedTextField(
                    value = itemPrice.intValue.toString(),
                    onValueChange = { value ->
                        if (value.isNotEmpty()) {
                            itemPrice.intValue = value.toInt()
                        }
                    },
                    label = {
                        Text("Harga")
                    },
                    placeholder = {
                        Text("Masukan harga item")
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
@Composable
fun CompleteReportDialogView(
    showDialog: Boolean,
    onDismiss:()->Unit,
    currentPlanExpenses: MoneyPlanWithExpenses
) {
    if(showDialog){
        Dialog(
            onDismissRequest = {},
        ) {
            CompleteReportDialogContentView(
                currentPlanExpenses = currentPlanExpenses,
                onOkClicked = onDismiss
            )
        }
    }

}


@Composable
fun CompleteReportDialogContentView(
    currentPlanExpenses:MoneyPlanWithExpenses,
    onOkClicked:()->Unit
){

    val isOverBudget = remember { mutableStateOf(false) }

    fun checkReport(){
        val totalExpenses = currentPlanExpenses.expenses.map { it.price }.reduce{acc,price-> acc+price}
        if(currentPlanExpenses.plan.budget > totalExpenses){
            isOverBudget.value = false
        }else{
            isOverBudget.value = true
        }
    }

    checkReport()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = IjoBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if(isOverBudget.value) "Pengeluaran kamu lebih besar dari budget. Yuk perbaiki, kamu bisa!" else "Kamu Hebat! Kamu sudah bisa mengatur pengeluranmu dengan baik",
            )
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