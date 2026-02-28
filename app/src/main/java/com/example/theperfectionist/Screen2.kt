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
fun Screen2(navController: NavController)
{
    Column(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {


        Text(text = "Is this your first time using our product?")


        Button(onClick = {
            navController.navigate("Bluetooth")
        }) {
            Text(text = "Yes")
        }

        Button(onClick = {
            navController.navigate("screen_3")
        }) {
            Text(text = "No")
        }


        /* Text(text = "How is your back today?")

       Button(onClick = {
            navController.navigate("good")
        }) {
            Text(text = "Good")
        }

        Button(onClick = {
            navController.navigate("bad")
        }) {
            Text(text = "Bad")
        }*/




        Button(onClick = {
            navController.navigate("settings")
        }) {
            Text(text = "Settings")
        }

       /* Button(onClick = {
            navController.navigate("screen_1")
        }) {
            Text(text = "Main menu")
        }*/




    }
}