package com.example.mealmenu

import android.app.Application
import com.example.mealmenu.ui.database.MealMenuRepository

class MealMenuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
         MealMenuRepository.initalize(this)
    }
}