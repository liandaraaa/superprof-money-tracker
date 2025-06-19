package com.smpn8yk.nomo_plan.ui.screens

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import com.smpn8yk.nomo_plan.ui.theme.CoklatKayu
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.data.MoneyPlan
import com.smpn8yk.nomo_plan.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.ui.theme.IjoBg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ManageMoneyActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db: MoneyPlanDatabase = databaseBuilder<MoneyPlanDatabase>(
            applicationContext,
            MoneyPlanDatabase::class.java,
            "moneyplan-database"
        ).build()

        setContent {
            ManageMoneyView(
                onDismissResultDialog = {moneyPlan->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            async { savePlan(db,moneyPlan) }.await()
                            Log.d("TEST_PROGRAM","Success saving plan..")
                            backToMainActivity()
                        }catch (e:Exception){
                            Log.d("TEST_PROGRAM","Error saving plan ${e.message}")
                        }
                    }
                }
            )
        }
    }

    private suspend fun savePlan(db:MoneyPlanDatabase, moneyPlan: MoneyPlan){
        db.moneyPlanDao().insert(moneyPlan)
    }

    private fun backToMainActivity(){
        this.finish()
    }
}

@Composable
fun ManageMoneyView(
    onDismissResultDialog:(moneyPlan:MoneyPlan)->Unit
){
    val showResultDialog = remember { mutableStateOf(false) }

    val days = remember { mutableIntStateOf(0) }
    val nominal = remember { mutableIntStateOf(0) }
    val budget = remember { mutableIntStateOf(0) }

    fun calculateBudget(days:Int, nominal:Int):Int{
        return nominal/days
    }

    fun getMoneyPlan():MoneyPlan{
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(days.intValue.toLong())
        return MoneyPlan(
            null,
            days.intValue,
            nominal.intValue,
            budget.intValue,
            startDate.toString(),
            endDate.toString()
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(CoklatKayu)
            .padding(16.dp)
    ) {
        Text(
            text = "Manage Your Money",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = days.value.toString(),
                onValueChange = { value->
                    if(value.isNotEmpty()){
                        days.intValue = value.toInt()
                    }
                },
                label = {
                    Text("Hari")
                },
                placeholder = {
                    Text("Masukan jumlah hari")
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            )
            OutlinedTextField(
                value = nominal.intValue.toString(),
                onValueChange = { value ->
                    if(value.isNotEmpty()){
                        nominal.intValue = value.toInt()
                    }
                },
                label = {
                    Text("Nominal")
                },
                placeholder = {
                    Text("Masukan nominal uang yang akan kamu gunakan !")
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
        Button(
            onClick = {
                budget.intValue = calculateBudget(days.intValue,nominal.intValue)
                showResultDialog.value = true
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Submit"
            )
        }
    }
}

@Composable
fun ResultDialogView(
    showDialog: Boolean,
    onDimmis:()->Unit,
    budget: Int
) {
    if(showDialog){
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
    onOkClicked:()->Unit,
    budget:Int
){
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
                text = "Result",
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
                        modifier = Modifier.padding(bottom = 12.dp))
                    Text(
                        text = "jangan melebihi batas harian, ya !")
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