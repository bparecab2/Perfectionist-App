package com.example.theperfectionist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun Screen3(navController: NavController)
{
    Column(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {


        //Text(text = "")

       /* Button(onClick = {
            navController.navigate("")
        }) {
            Text(text = "Run settings")
        }*/

        Button(onClick = {
            navController.navigate("Bluetooth")
        }) {
            Text(text = "Bluetooth")
        }


    }
}