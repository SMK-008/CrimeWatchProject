package com.example.crimewatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.crimewatch.MainActivity
import com.example.crimewatch.ui.theme.CrimeWatchTheme
import com.example.crimewatch.ui.theme.CrimeWatchTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity:ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrimeWatchTheme{
                SplashScreen()
            }
        }

    }
    @Preview
    @Composable
    private fun SplashScreen(){
        var alpha=remember{
            androidx.compose.animation.core.Animatable(0f)
        }
        LaunchedEffect(key1=true) {
            alpha.animateTo(1f,
                animationSpec = tween(1500)
            )
            delay(2000)
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
        }
        Box (modifier = Modifier.fillMaxSize().background(
            Color(0xFFFFFFff)
        ), contentAlignment = Alignment.Center){
            Image(
                modifier = Modifier.alpha(alpha.value),
                painter = painterResource(id=R.drawable.splash2),contentDescription = null)
        }
    }
}