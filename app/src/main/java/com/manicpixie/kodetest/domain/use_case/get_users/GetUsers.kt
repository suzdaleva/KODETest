package com.manicpixie.kodetest.domain.use_case.get_users

import android.annotation.SuppressLint
import com.manicpixie.kodetest.common.Resource
import com.manicpixie.kodetest.data.remote.dto.toUser
import com.manicpixie.kodetest.domain.model.User
import com.manicpixie.kodetest.domain.repository.KodeRepository
import com.manicpixie.kodetest.domain.util.UserOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*
import javax.inject.Inject


class GetUsers @Inject constructor (private val repository: KodeRepository) {
    @SuppressLint("SimpleDateFormat")
    operator fun invoke(userOrder: UserOrder = UserOrder.Alphabetically) : Flow<Resource<List<User>>> = flow {
        try{
            emit(Resource.Loading<List<User>>())
            val users = when(userOrder) {
                is UserOrder.Alphabetically -> repository.getUsers().map{it.toUser()}.sortedBy { it.firstName }
                is UserOrder.Birthday -> repository.getUsers().map{it.toUser()}.sortedBy { user->
                    val currentDate = LocalDate.now()
                    var date: Date? = SimpleDateFormat("yyyy-MM-dd").parse(user.birthday.replaceRange(0..3, currentDate.year.toString()))
                    var birthdayDate = date!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    if (Period.between(currentDate, birthdayDate).isNegative) {
                        date  = SimpleDateFormat("yyyy-MM-dd").parse(user.birthday.replaceRange(0..3, (currentDate.year + 1).toString()))
                        birthdayDate = date!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                   //(Period.between(currentDate, birthdayDate)).days
                    birthdayDate
                }
            }
            emit(Resource.Success<List<User>>(users))
        }
        catch (e: HttpException) {
            emit(Resource.Error<List<User>>("Какой-то сверхразум все сломал"))
        }
        catch (e: IOException) {
            emit(Resource.Error<List<User>>("Какой-то сверхразум все сломал"))
        }
    }
}


