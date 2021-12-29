package com.naveen.mobileauth

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal fun FragmentManager.addFragment(
    containerViewId: Int,
    fragment: Fragment,
    tag: String = ""
) {
    this.beginTransaction()
        .addToBackStack(tag)
        .add(containerViewId, fragment, tag)
        .commit()
}

internal fun FragmentManager.replaceFragment(
    containerViewId: Int,
    fragment: Fragment,
    tag: String = ""
) {
    this.beginTransaction()
        .addToBackStack(tag)
        .replace(containerViewId, fragment, tag)
        .commit()
}

internal fun FragmentManager.shortToast(activity: Activity, message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

internal fun FragmentManager.longToast(activity: Activity, message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}

fun Activity.shortToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

