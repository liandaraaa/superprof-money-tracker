package com.smpn8yk.nomo_plan.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smpn8yk.nomo_plan.MainActivity
import com.smpn8yk.nomo_plan.R
import com.smpn8yk.nomo_plan.ui.theme.Putih
import java.util.Timer
import java.util.TimerTask

class SplashActivity : ComponentActivity () {
    private val time = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST_PROGRAM","ini splash activity")
        setContent{
            SplashView()
        }
        handleTimer()
    }
    private fun handleTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                navigateToMainActivity()
            }
        }, time)
    }
    private fun navigateToMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
    }
}



@Composable
fun SplashView() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Putih),
        verticalArrangement = SpaceBetween
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.nomo_splash00),
                contentDescription = "Nomo-Plan illustration",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            Text(
                "By Lala & Dayu",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSplashView(){
    SplashView()
}

