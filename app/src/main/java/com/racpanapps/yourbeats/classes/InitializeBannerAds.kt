package com.racpanapps.yourbeats.classes

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

object InitializeBannerAds {

    fun loadAds(context : Application, activity: Activity,linearLayoutBanner : LinearLayout) {
        UnityAds.initialize(context.applicationContext, activity.getString(R.string.unityGameId))
        val bottomBanner =
            BannerView(activity, activity.getString(R.string.placementId), UnityBannerSize(350, 60))
        bottomBanner.load()
        linearLayoutBanner.addView(bottomBanner)
    }

    fun setBackground(context : Context, constraintLayout : ConstraintLayout) {
        val res = context.resources
        val drawable = res.getDrawable(R.drawable.background_gif, null) as AnimationDrawable
        constraintLayout.background = drawable
        drawable.start()
    }
}