package com.manicpixie.kodetest.presentation.main

import android.annotation.SuppressLint
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.manicpixie.kodetest.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import com.manicpixie.kodetest.domain.util.UserOrder
import com.manicpixie.kodetest.presentation.main.components.*
import com.manicpixie.kodetest.ui.theme.Purple
import com.manicpixie.kodetest.ui.theme.Red
import com.manicpixie.kodetest.ui.theme.interFont
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*


@ExperimentalAnimationApi
@SuppressLint("SimpleDateFormat")
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val snackbarHostState = remember{mutableStateOf(SnackbarHostState())}
    val queryState = viewModel.searchQuery.value
    val contentState = viewModel.contentState.value

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val currentUserOrder = remember {
        mutableStateOf(contentState.userOrder)
    }
    val sortedListState = remember {
        mutableStateOf(contentState.users)
    }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val sortButtonColor = remember {
        mutableStateOf(Color(0xFFC3C3C6))
    }
    val snackbarColor = remember {
        mutableStateOf(Color(0xFF6534FF))
    }
    val isBirthdayVisible = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MainViewModel.UiEvent.ShowErrorSnackBar -> {
                    snackbarColor.value = Red
                    snackbarHostState.value.showSnackbar(message =
                        "Hе могу обновить данные.\n" +
                                "Проверь соединение с интернетом",
                        duration = SnackbarDuration.Short
                    )
                }
                is MainViewModel.UiEvent.ShowLoadingSnackBar -> {
                    snackbarColor.value = Color(0xFF6534FF)
                    snackbarHostState.value.showSnackbar(message =
                    "Секундочку, гружусь..."
                    )

                }
                is MainViewModel.UiEvent.CancelSnackBar -> {
                    snackbarHostState.value.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    Scaffold(scaffoldState = scaffoldState,
    snackbarHost = {
        SnackbarHost(
            modifier = Modifier,
            hostState = snackbarHostState.value,
            snackbar = { snackbarData: SnackbarData ->
                Card(
                    backgroundColor = snackbarColor.value,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = snackbarData.message,
                            color = Color.White,
                            style = TextStyle(
                            fontFamily = interFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ))
                    }
                }
            }
        )
    }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val focusManager = LocalFocusManager.current
            BottomDrawer(
                drawerBackgroundColor = Color.Transparent,
                drawerContent = {
                    Surface(
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    ) {
                        BottomSort(
                            userOrder = contentState.userOrder,
                            onOrderChange = {
                                isBirthdayVisible.value = it == UserOrder.Birthday
                                viewModel.onEvent(MainScreenEvent.Order(it))
                                currentUserOrder.value = it
                                sortButtonColor.value = Purple
                                coroutineScope.launch { drawerState.close() }
                            })
                    }
                },
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomTextField(
                            text = queryState.query,
                            hint = queryState.hint,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            leadingIcon = {
                                IconButton(
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .size(20.dp),
                                    content = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_search),
                                            contentDescription = "Sort",
                                            tint = queryState.tint
                                        )
                                    },
                                    onClick = { })
                            },
                            trailingIcon = {
                                IconButton(
                                    modifier = Modifier
                                        .padding(end = 14.dp)
                                        .size(20.dp),
                                    content = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.sort_icon),
                                            contentDescription = "Sort",
                                            tint = sortButtonColor.value
                                        )
                                    },
                                    onClick = {
                                        coroutineScope.launch { drawerState.open() }
                                    })
                            },
                            onValueChange = {
                                viewModel.onEvent(
                                    MainScreenEvent.EnteredQuery(
                                        it,
                                        currentUserOrder.value
                                    )
                                )
                            },
                            onFocusChange = {
                                viewModel.onEvent(MainScreenEvent.ChangeQueryFocus(it))
                            },
                            isHintVisible = queryState.isHintVisible,
                            onCancel = {
                                viewModel.onEvent(MainScreenEvent.CancelSearch(currentUserOrder.value))
                                focusManager.clearFocus()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        TabView(
                            tabs = listOf(
                                "Все",
                                "Designers",
                                "Analysts",
                                "Managers",
                                "IOS",
                                "Android",
                                "QA",
                                "Backend",
                                "Frontend",
                                "HR",
                                "PR",
                                "Back Office",
                                "Support"
                            ), selectedIndex = pagerState.currentPage,
                            onTabSelected = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(it)
                                }
                            }
                        )
                        SwipeRefresh(
                            clipIndicatorToPadding = false,
                            state = rememberSwipeRefreshState(isRefreshing),
                            onRefresh = {
                                viewModel.refresh(currentUserOrder.value)
                            }
                        ) {
                            HorizontalPager(
                                count = 13, state = pagerState) { index ->


                                if (contentState.isLoading) {
                                    LoadingScreen(modifier = Modifier.fillMaxSize())
                                }

                                if (contentState.users.isNotEmpty()) {

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 20.dp)
                                    ) {
                                        val sortedList = when (index) {
                                            0 -> contentState.users
                                            1 -> contentState.users.filter { it.department == "design" }
                                            2 -> contentState.users.filter { it.department == "analytics" }
                                            3 -> contentState.users.filter { it.department == "management" }
                                            4 -> contentState.users.filter { it.department == "ios" }
                                            5 -> contentState.users.filter { it.department == "android" }
                                            6 -> contentState.users.filter { it.department == "qa" }
                                            7 -> contentState.users.filter { it.department == "backend" }
                                            8 -> contentState.users.filter { it.department == "frontend" }
                                            9 -> contentState.users.filter { it.department == "hr" }
                                            10 -> contentState.users.filter { it.department == "pr" }
                                            11 -> contentState.users.filter { it.department == "back_office" }
                                            12 -> contentState.users.filter { it.department == "support" }
                                            else -> contentState.users
                                        }
                                        sortedListState.value = sortedList
                                        items(sortedList) { user ->
                                            val currentDate = LocalDate.now()
                                            val date: Date? = SimpleDateFormat("yyyy-MM-dd").parse(
                                                user.birthday.replaceRange(
                                                    0..3,
                                                    currentDate.year.toString()
                                                )
                                            )
                                            //sortedList[sortedList.indexOf(user) - 1].birthday
                                            val previousDate: Date? = if(sortedList.indexOf(user) != 0 ) SimpleDateFormat("yyyy-MM-dd").parse(
                                                sortedList[sortedList.indexOf(user) - 1].birthday.replaceRange(
                                                    0..3,
                                                    currentDate.year.toString()
                                                )
                                            ) else date
                                            val previousBirthdayDate =
                                                previousDate!!.toInstant().atZone(ZoneId.systemDefault())
                                                    .toLocalDate()
                                            val birthdayDate =
                                                date!!.toInstant().atZone(ZoneId.systemDefault())
                                                    .toLocalDate()
                                            if (contentState.userOrder == UserOrder.Birthday && Period.between(
                                                    currentDate,
                                                    birthdayDate
                                                ).isNegative && !Period.between(
                                                    currentDate,
                                                    previousBirthdayDate
                                                ).isNegative
                                            ) {
                                                YearDivider()
                                            }
                                            ProfileItem(
                                                user = user,
                                                onItemClick = {
                                                    val json = Uri.encode(Gson().toJson(user))
                                                    navController.currentBackStackEntry?.arguments?.putParcelable(
                                                        "user",
                                                        user
                                                    )
                                                    navController.navigate("profile_screen/$json")
                                                },
                                                isBirthdayDateVisible = isBirthdayVisible
                                            )


                                        }
                                    }

                                }

                                if (!contentState.isLoading
                                    && (sortedListState.value.isEmpty() || contentState.users.isEmpty())
                                ) {
                                    EmptySearchScreen(modifier = Modifier.fillMaxSize())
                                }
                                if (contentState.error.isNotBlank()) {
                                    ErrorScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        errorMessage = contentState.error,
                                        onClick = {
                                            viewModel.refresh(currentUserOrder.value)
                                        }
                                    )

                                }

                            }
                        }
                    }
                },
                gesturesEnabled = false,
                drawerState = drawerState
            )
        }
    }
}



@ExperimentalAnimationApi
@Composable
private fun CustomTextField(
    isHintVisible: Boolean = true,
    hint: String = "",
    text: String = "",
    onFocusChange: (FocusState) -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = true,
    leadingIcon: (@Composable() () -> Unit)? = null,
    trailingIcon: (@Composable() () -> Unit)? = null
) {

        Row(
            modifier = modifier
                .height(40.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7F7F8)),
                    verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Box(
                modifier = modifier
                    .weight(1f)
            ) {
                BasicTextField(
                    value = text,
                    cursorBrush = SolidColor(Purple),
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = textStyle,
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .fillMaxWidth()
                        .onFocusChanged { onFocusChange(it) }

                )
                if (isHintVisible) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFC3C3C6)
                    )
                }
            }
            if (trailingIcon != null) {
                AnimatedVisibility(
                    visible = true,
                    exit = fadeOut(),
                    enter = fadeIn()
                ) {
                    if(isHintVisible) {trailingIcon()}
                    else {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Transparent),
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = onCancel,
                            content = {
                                Text(
                                    text = "Отмена",
                                    style = TextStyle(
                                        color = Purple,
                                        fontFamily = interFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp
                                    )
                                )
                            })
                    }
                }
            }

    }
}

@Composable
fun TabView(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (selectedIndex: Int) -> Unit,
) {
    val inactiveColor = (Color(0xFF777777))
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        contentColor = Purple,
        backgroundColor = Color.Transparent,
        edgePadding = 0.dp,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                selectedContentColor = Color.Black,
                unselectedContentColor = inactiveColor,
                onClick = {
                    onTabSelected(index)
                }
            ) {
                Text(
                    text = tab,
                    color = if (selectedIndex == index) Color.Black else inactiveColor,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(Color.White),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = "Error",
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Постараемся быстро починить",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            modifier = Modifier.clickable { onClick() },
            text = "Попробовать снова",
            textAlign = TextAlign.Center,
            color = Purple,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun LoadingScreen(

    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        items(count = 8) { item ->
            ShimmerAnimation()
        }
    }
}


@Composable
fun EmptySearchScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_2),
            contentDescription = "Error",
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Мы никого не нашли",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Попробуй скорректировать запрос",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }
}






@Composable
fun YearDivider(
    modifier: Modifier = Modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(
                modifier = Modifier
                    .background(Color(0xFFC3C3C6))
                    .height(1.dp)
                    .width(72.dp)
            )
            Text(
                text = (LocalDate.now().year + 1).toString(),
                style = MaterialTheme.typography.body2,
                color = Color(0xFFC3C3C6)
            )
            Spacer(
                modifier = Modifier
                    .background(Color(0xFFC3C3C6))
                    .height(1.dp)
                    .width(72.dp)
            )
        }


}




fun View.isKeyboardOpen(): Boolean {
    val rect = Rect()
    getWindowVisibleDisplayFrame(rect)
    val screenHeight = rootView.height
    val keypadHeight = screenHeight - rect.bottom
    return keypadHeight > screenHeight * 0.15
}

@Composable
fun rememberIsKeyboardOpen(): State<Boolean> {
    val view = LocalView.current

    return produceState(initialValue = view.isKeyboardOpen()) {
        val viewTreeObserver = view.viewTreeObserver
        val listener = ViewTreeObserver.OnGlobalLayoutListener { value = view.isKeyboardOpen() }
        viewTreeObserver.addOnGlobalLayoutListener(listener)

        awaitDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener)  }
    }
}

fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {

    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val isKeyboardOpen by rememberIsKeyboardOpen()

        val focusManager = LocalFocusManager.current
        LaunchedEffect(isKeyboardOpen) {
            if (isKeyboardOpen) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}




