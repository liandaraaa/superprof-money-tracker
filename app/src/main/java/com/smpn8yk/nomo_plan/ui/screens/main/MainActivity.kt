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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.lifecycle.Lifecycle
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.data.CalendarUiState
import com.smpn8yk.nomo_plan.data.ExpenseReportStatus
import com.smpn8yk.nomo_plan.data.MoneyPlan
import com.smpn8yk.nomo_plan.data.MoneyPlanStatus
import com.smpn8yk.nomo_plan.data.MoneyPlanWithExpenses
import com.smpn8yk.nomo_plan.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.ui.MyEventListener
import com.smpn8yk.nomo_plan.ui.screens.DailyTrackerExpenseActivity
import com.smpn8yk.nomo_plan.ui.screens.ManageMoneyActivity
import com.smpn8yk.nomo_plan.ui.theme.Abu
import com.smpn8yk.nomo_plan.ui.theme.CoklatKayu
import com.smpn8yk.nomo_plan.ui.theme.IjoYes
import com.smpn8yk.nomo_plan.ui.theme.MerahNo
import com.smpn8yk.nomo_plan.ui.theme.NomoPlanTheme
import com.smpn8yk.nomo_plan.utils.DateUtil
import com.smpn8yk.nomo_plan.utils.getDates
import com.smpn8yk.nomo_plan.utils.getDisplayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.YearMonth


class MainActivity : ComponentActivity() {

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
                    onDateClickListener = { planId, selectedDate ->
                        navigateToDailyTrackerExpenseActivity(planId, selectedDate)
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

    private fun navigateToManageMoneyActivity() {
        startActivity(Intent(this, ManageMoneyActivity::class.java))
    }

    private fun navigateToDailyTrackerExpenseActivity(planId: Int, selectedDate: String) {
        val intent = Intent(this, DailyTrackerExpenseActivity::class.java)
        intent.putExtra("EXTRA_PLAN_ID", planId)
        intent.putExtra("EXTRA_DATE", selectedDate)
        Log.d("TEST_PROGRAM", "navigate daily date $planId")
        startActivity(intent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    context: Context,
    onClickNewPlan: () -> Unit,
    onDateClickListener: (planId: Int, selectedDate: String) -> Unit
) {
    val moneyPlansWithExpenses = remember {
        mutableStateListOf(
            MoneyPlanWithExpenses(
                plan = MoneyPlan(id = null)
            )
        )
    }
    val uiState = remember { mutableStateOf(CalendarUiState.Init) }

    val db: MoneyPlanDatabase = databaseBuilder(
        context,
        MoneyPlanDatabase::class.java,
        "moneyplan-database"
    ).allowMainThreadQueries()
        .build()

    fun checkMoneyPlanExist() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val plans = async { db.moneyPlanDao().getALlMoneyPlanWithExpenses() }.await()
                moneyPlansWithExpenses.clear()
                moneyPlansWithExpenses.addAll(plans)
                Log.d("TEST_PROGRAM", "get all plans from db $plans")
                Log.d("TEST_PROGRAM", "get all plans expesense $plans")
                uiState.value = CalendarUiState(
                    yearMonth = YearMonth.now(),
                    dates = getDates(YearMonth.now(), plans)
                )
                return@launch
            } catch (e: Exception) {
                return@launch
            }
        }
    }

    fun toSelectedMonth(currentMonth: YearMonth) {
        Log.d("TEST_PROGRAM", "select current month $currentMonth")
        uiState.value = CalendarUiState(
            yearMonth = currentMonth,
            dates = getDates(currentMonth, moneyPlansWithExpenses)
        )
    }

    MyEventListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                Log.d(
                    "TEST_PROGRAM", "cek on resume lifecycle" +
                            ""
                )
                checkMoneyPlanExist()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                val isEnableButton = if (moneyPlansWithExpenses.any { it.plan.id == null }) {
                    Log.d(
                        "TEST_PROGRAM",
                        "cek is have plan.id null $moneyPlansWithExpenses.any { it.plan.id == null"
                    )
                    true
                } else {
                    val isEnable = moneyPlansWithExpenses.map { it.plan }
                        .find { it.status == MoneyPlanStatus.PENDING.name } == null
                    Log.d("TEST_PROGRAM", "cek isEnbale $isEnable")
                    isEnable
                }
                val isShowWarningTextNewPlan =
                    moneyPlansWithExpenses.any { it.plan.id != null } && !isEnableButton
                if (isShowWarningTextNewPlan) {
                    Text(
                        fontSize = 11.sp,
                        text = "Kamu tidak dapat membuat perencanaan baru sebelum menyelesaikan rencana sebelumnya"
                    )
                }
                Button(
                    enabled = isEnableButton,
                    onClick = {
                        onClickNewPlan()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Buat Perencanaan Baru !"
                    )
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Log.d("TEST_PROGRAM", "show mainView : $moneyPlansWithExpenses")
            if (moneyPlansWithExpenses.any { it.plan.id != null }) {
                Log.d("TEST_PROGRAM", "show calendar widget $moneyPlansWithExpenses")
                CalendarWidget(
                    days = DateUtil.daysOfWeek,
                    onDateClickListener = { date ->
                        val currentMoneyPlan = moneyPlansWithExpenses.map { it.plan }
                            .find { it.range_dates.contains(date.dateFormat) }
                        Log.d("TEST_PROGRAM", "cek current moneyplan $currentMoneyPlan")
                        onDateClickListener(currentMoneyPlan?.id ?: 0, date.dateFormat)
                    },
                    uiState = uiState.value,
                    onSelectedMonth = { yearMonth -> toSelectedMonth(yearMonth) }
                )
            } else {
                EmptyPlanView(padding)
            }
        }
    }
}

@Composable
fun EmptyPlanView(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = "empty plan",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Ops! Kamu belum membuat perencanaan apapun",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
fun PreviewEmptyPlanView() {
    EmptyPlanView(
        padding = PaddingValues(16.dp)
    )
}

@Composable
fun CalendarWidget(
    days: Array<String>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
    uiState: CalendarUiState,
    onSelectedMonth: (YearMonth) -> Unit
) {
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
            yearMonth = uiState.yearMonth,
            onPreviousMonthButtonClicked = { yearMonth ->
                onSelectedMonth(yearMonth)
            },
            onNextMonthButtonClicked = { yearMonth ->
                onSelectedMonth(yearMonth)
            },
        )
        Content(
            dates = uiState.dates,
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
    dates: List<CalendarUiState.Date>,
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
                .padding(10.dp),
            color = when (date.status) {
                ExpenseReportStatus.EMPTY.name -> CoklatKayu
                ExpenseReportStatus.SUCCESS.name -> IjoYes
                ExpenseReportStatus.FAILED.name -> MerahNo
                else -> Abu
            }
        )
    }
}

@Preview
@Composable
fun PreviewCalendarPlanView() {
    CalendarWidget(
        days = DateUtil.daysOfWeek,
        onDateClickListener = { date ->
            Log.d("TEST_PROGRAM", "Select date $date")
        },
        uiState = CalendarUiState.Init,
        onSelectedMonth = {}
    )
}
