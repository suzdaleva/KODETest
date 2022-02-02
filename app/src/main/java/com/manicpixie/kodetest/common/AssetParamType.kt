package com.manicpixie.kodetest.common

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.manicpixie.kodetest.domain.model.User

class AssetParamType : NavType<User>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): User? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): User {
        return Gson().fromJson(value, User::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: User ) {
        bundle.putParcelable(key, value)
    }

    override val name: String
        get() = TODO("Not yet implemented")
}