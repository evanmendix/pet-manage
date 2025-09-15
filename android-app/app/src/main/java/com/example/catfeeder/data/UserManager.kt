package com.example.catfeeder.data

import com.example.catfeeder.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor() {
    var currentUser: User? = null
}
