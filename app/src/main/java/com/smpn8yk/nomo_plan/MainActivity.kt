package com.smpn8yk.nomo_plan
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.data.CalendarUiState
import com.smpn8yk.nomo_plan.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.ui.screens.DailyTrackerExpenseActivity
import com.smpn8yk.nomo_plan.ui.screens.ManageMoneyActivity
import com.smpn8yk.nomo_plan.ui.theme.CoklatKayu
import com.smpn8yk.nomo_plan.ui.theme.IjoBg
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import com.smpn8yk.nomo_plan.utils.DateUtil
import com.smpn8yk.nomo_plan.utils.getDates
import com.smpn8yk.nomo_plan.utils.getDisplayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth


class MainActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST_PROGRAM", "coba panggil onCreate")

        setContent {
            NomoPlanTheme {
                MainView(
                    context = this,
                    onClickNewPlan = {
                        navigateToManageMoneyActivity()
                    },
                    onDateClickListener = {date->
                        navigateToDailyTrackerExpenseActivity(date.dayOfMonth)
                    }
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("TEST_PROGRAM", "coba panggil onResume")
        }

    override fun onPause() {
        super.onPause()
        Log.d("TEST_PROGRAM", "coba panggil onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("TEST_PROGRAM", "coba panggil onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TEST_PROGRAM", "coba panggil onDestroy")
    }

    private fun navigateToManageMoneyActivity(){
        startActivity(Intent(this,ManageMoneyActivity::class.java))
    }

    private fun navigateToDailyTrackerExpenseActivity(date:String){
        val intent = Intent(this, DailyTrackerExpenseActivity::class.java)
        intent.putExtra("EXTRA_DATE",date)
        Log.d("TEST_PROGRAM", "navigate daily date $date" )
        startActivity(intent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    context:Context,
    onClickNewPlan:()->Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit
){
    val isMoneyPlanExists = remember { mutableStateOf(false) }

    val db: MoneyPlanDatabase = databaseBuilder<MoneyPlanDatabase>(
        context,
        MoneyPlanDatabase::class.java,
        "moneyplan-database"
    ).build()

    fun checkMoneyPlanExist(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val isExists = async { db.moneyPlanDao().getAlLMoneyPlans().isNotEmpty() }.await()
                isMoneyPlanExists.value = isExists
                return@launch
            }catch (e:Exception){
                return@launch
            }
        }
    }

    checkMoneyPlanExist()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ))
        }
    ) { padding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {

            if(isMoneyPlanExists.value){
                CalendarWidget(
                    days = DateUtil.daysOfWeek,
                    onDateClickListener = { date->
                        onDateClickListener(date)
                    }
                )
            }else{
                EmptyPlanView {
                    onClickNewPlan()
                }
            }
        }
    }
}

@Composable
fun EmptyPlanView(
    onNewPlanClicked:()->Unit)
{
    Column (modifier = Modifier
        .fillMaxSize()
        .background(IjoBg),
        verticalArrangement = Arrangement.SpaceBetween){
        Column{
            Text(
                text = ("Make Your Own Plan !"),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                fontSize = 24.sp,
                color = CoklatKayu
            )
            Image(
                painter = painterResource(id = R.drawable.ops),
                contentDescription = "Empty illustration",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 100.dp)
            )
        }
        Button(
            onClick = {
                    onNewPlanClicked()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
        ) {
            Text("Buat Perencanaan Baru !")
        }
    }
}

@Preview
@Composable
fun PreviewEmptyPlanView() {
    EmptyPlanView(
        {
            Log.d("TEST_PROGRAM", "Navigate to manage money")
        }
    )
}

@Composable
fun CalendarWidget(
    days: Array<String>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {

    val  uiState = remember { mutableStateOf(CalendarUiState.Init) }

    fun toCurrentMonth(currentMonth: YearMonth) {
        Log.d("TEST_PROGRAM","select current month $currentMonth")
        uiState.value = CalendarUiState(
            yearMonth = currentMonth,
            dates = getDates(currentMonth)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            repeat(days.size) {
                val item = days[it]
                DayItem(item, modifier = Modifier.weight(1f))
            }
        }
        Header(
            yearMonth = uiState.value.yearMonth,
            onPreviousMonthButtonClicked = {yearMonth ->
                toCurrentMonth(yearMonth)
            },
            onNextMonthButtonClicked = {yearMonth ->
                toCurrentMonth(yearMonth)
            },
        )
        Content(
            dates = uiState.value.dates,
            onDateClickListener = onDateClickListener
        )
    }
}

@Composable
fun Header(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
    Row {
        IconButton(onClick = {
            onPreviousMonthButtonClicked.invoke(yearMonth.minusMonths(1))
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.back)
            )
        }
        Text(
            text = yearMonth.getDisplayName(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(onClick = {
            onNextMonthButtonClicked.invoke(yearMonth.plusMonths(1))
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.next)
            )
        }
    }
}

@Composable
fun DayItem(day: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

@Composable
fun Content(
    dates:List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
    Column {
        var index = 0
        repeat(6) {
            if (index >= dates.size) return@repeat
            Row {
                repeat(7) {
                    val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
                    ContentItem(
                        date = item,
                        onClickListener = onDateClickListener,
                        modifier = Modifier.weight(1f)
                    )
                    index++
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (date.isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                }
            )
            .clickable {
                onClickListener(date)
            }
    ) {
        Text(
            text = date.dayOfMonth,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

@Preview
@Composable
fun PreviewCalendarPlanView(){
    CalendarWidget(
        days = DateUtil.daysOfWeek,
        onDateClickListener = { date->
            Log.d("TEST_PROGRAM","Select date $date")
        }
    )
}
