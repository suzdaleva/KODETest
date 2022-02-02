package com.manicpixie.kodetest.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manicpixie.kodetest.R
import com.manicpixie.kodetest.domain.model.User
import com.manicpixie.kodetest.ui.theme.KODETestTheme
import com.manicpixie.kodetest.ui.theme.interFont
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileItem(
    isBirthdayDateVisible : MutableState<Boolean>,
    modifier: Modifier = Modifier,
    user: User,
    onItemClick: () -> Unit
) {

    val date: Date? = SimpleDateFormat("yyyy-MM-dd").parse(user.birthday)
    val birthday = SimpleDateFormat("d MMM").format(date).dropLast(1)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,

    ) {
        RoundImage(

            image = painterResource(id = R.drawable.img),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
            Column() {
                Row() {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.h1
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        softWrap = false,
                        text = user.userTag.lowercase(),
                        color = Color(0xFF97979B),
                        modifier = Modifier
                            .padding(top = 2.dp),
                        style = TextStyle(
                            fontFamily = interFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,

                            )
                    )
                }
                Text(
                    text = user.position,
                    color = Color(0xFF55555C),
                    style = MaterialTheme.typography.h2
                )
            }
            if(isBirthdayDateVisible.value) {
            Text(text = birthday,
                color = Color(0xFF55555C),
            style = MaterialTheme.typography.body2)
            }
        }


    }
}

@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = "Profile pic",
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .clip(CircleShape)
    )

}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KODETestTheme {
        val isVisible = remember {
            mutableStateOf(true)
        }
        ProfileItem(
            user = User(
                avatarUrl ="https://cdn.fakercloud.com/avatars/marrimo_128.jpg",
            birthday = "2004-08-02",
        department = "back_office",
        firstName = "Dee",
        id = "reichert_dee@gmail.com",
         lastName = "Reichert",
        phone = "930-728-8876",
        position = "Technician",
        userTag = "LK",
        ),
        onItemClick = {},
        isBirthdayDateVisible = isVisible)
    }
}

