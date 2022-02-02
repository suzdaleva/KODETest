package com.manicpixie.kodetest.presentation.profile

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.manicpixie.kodetest.R
import com.manicpixie.kodetest.domain.model.User
import com.manicpixie.kodetest.presentation.main.components.RoundImage
import com.manicpixie.kodetest.ui.theme.interFont
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

@SuppressLint("SimpleDateFormat")
@Composable
fun ProfileScreen(
    user: User,
    navController: NavController
) {
    val context = LocalContext.current
    val date: Date? = SimpleDateFormat("yyyy-MM-dd").parse(user.birthday)
    val birthday = SimpleDateFormat("d MMMM yyyy").format(date)
    val currentDate = LocalDate.now()
    val birthdayDate = date!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val period: Period = Period.between(birthdayDate, currentDate)
    val suffix: String  = if (period.years.toString().endsWith("1") && period.years != 11) "год"
            else if (period.years.toString().endsWith("2") || period.years.toString().endsWith("3") || period.years.toString().endsWith("4")
        && period.years !in 12..14)  "года" else "лет"

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color(0xFFF7F7F8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(17.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(start = 10.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .size(25.dp)
                        .align(alignment = Alignment.Bottom),
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(25.dp)
                            )
                    })
            }
            RoundImage(
                image = painterResource(id = R.drawable.img),
                modifier = Modifier.size(104.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row() {
                Text(

                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.h4
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    softWrap = false,
                    text = user.userTag.lowercase(),
                    color = Color(0xFF97979B),
                    modifier = Modifier
                        .padding(top = 7.dp),
                    style = TextStyle(
                        fontFamily = interFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,

                        )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = user.position,
                color = Color(0xFF55555C),
                style = MaterialTheme.typography.h2
            )

        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 17.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    modifier = Modifier.weight(5f),
                    text = birthday,
                    style = MaterialTheme.typography.h1
                )
                Text(
                    modifier = Modifier.weight(5f),
                    textAlign = TextAlign.End,
                    text = "${period.years} $suffix",
                    style = MaterialTheme.typography.h1,
                    color = Color(0xFF97979B),
                )
            }
            if(user.phone.matches("\\d{3}-\\d{3}-\\d{4}".toRegex())) {
                val number = "+7 (${user.phone.subSequence(0, 3)}) ${user.phone.subSequence(4, 7)} ${user.phone.subSequence(8, 10)} ${user.phone.subSequence(10, 12)}"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 17.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = number,
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.clickable {
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            try{
                                startActivity(context, callIntent, null)
                            }
                            catch (e: ActivityNotFoundException){}
                        }
                    )
                }
            }
        }

    }
}
