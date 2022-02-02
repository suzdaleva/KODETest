package com.manicpixie.kodetest.presentation.main.components

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.manicpixie.kodetest.ui.theme.KODETestTheme
import com.manicpixie.kodetest.R
import com.manicpixie.kodetest.domain.util.UserOrder
import com.manicpixie.kodetest.ui.theme.Purple


@ExperimentalComposeUiApi
@Composable
fun BottomSort(
    modifier: Modifier = Modifier,
    userOrder: UserOrder = UserOrder.Alphabetically,
    onOrderChange: (UserOrder) -> Unit,
) {


    Box(
        modifier = Modifier
            .height(228.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center,

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = R.drawable.horizontal_bar),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = userOrder is UserOrder.Alphabetically,
                    onClick = { onOrderChange(UserOrder.Alphabetically) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Purple,
                        unselectedColor = Purple
                    )
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "По алфавиту",
                    style = MaterialTheme.typography.h1
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = userOrder is UserOrder.Birthday,
                    onClick = { onOrderChange(UserOrder.Birthday) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Purple,
                        unselectedColor = Purple
                    )
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "По дате рождения",
                    style = MaterialTheme.typography.h1
                )
            }
        }

    }
}






