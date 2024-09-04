package com.example.quickqbusiness.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Profile(modifier: Modifier = Modifier) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//    val name = db.collection()
    Text(
        text = "Profile",
        fontSize = 40.sp
    )
    Text(
        text = ""
    )
}