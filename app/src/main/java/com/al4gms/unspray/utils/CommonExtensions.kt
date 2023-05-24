package com.al4gms.unspray.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Activity.hideKeyboardAndClearFocus() {
    val view = currentFocus ?: View(this)
    hideKeyboardFrom(view)
    view.clearFocus()
}

fun Context.hideKeyboardFrom(view: View) {
    getSystemService(Activity.INPUT_METHOD_SERVICE)
        .let { it as InputMethodManager }
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun haveQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun haveM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun <T : Fragment> T.withArguments(action: Bundle.() -> Unit): T {
    return apply {
        val args = Bundle().apply(action)
        arguments = args
    }
}

fun <T : Fragment> T.toast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T : Fragment> T.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
fun snackbar(view: View, @StringRes message: Int) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .show()
}

fun snackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .show()
}

fun <T : Activity> T.getConnectivityManager(): ConnectivityManager {
    return if (haveM()) {
        getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    } else {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
fun <T : Fragment> T.getConnectivityManager(): ConnectivityManager {
    return if (haveM()) {
        requireActivity().getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    } else {
        requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}

fun ConnectivityManager.hasConnection(): Boolean {
    return if (haveM()) {
        getNetworkCapabilities(activeNetwork) != null
    } else {
        activeNetworkInfo?.isConnectedOrConnecting == true
    }
}

fun <T : Fragment> T.getNetworkConnectionState(): Boolean {
    return getConnectivityManager().hasConnection()
}

fun ImageView.setImageWithGlide(view: View, url: String) {
    Glide.with(view)
        .load(url)
        .into(this)
}
fun ImageView.setImageWithGlide(fragment: Fragment, url: String) {
    Glide.with(fragment)
        .load(url)
        .into(this)
}
fun ImageView.setImageWithGlide(fragment: Fragment, url: String, @DrawableRes placeholder: Int) {
    Glide.with(fragment)
        .load(url)
        .placeholder(placeholder)
        .into(this)
}

fun getDateTimeFromMillis(milliSeconds: Long, dateFormat: String?): String? {
    val simpleDateFormat = SimpleDateFormat(dateFormat)
    return simpleDateFormat.format(milliSeconds)
}

fun EditText.textChangedFlow(): Flow<String> {
    return callbackFlow {
        val textWatchListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                trySendBlocking(p0?.toString().orEmpty())
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        this@textChangedFlow.addTextChangedListener(textWatchListener)
        awaitClose {
            this@textChangedFlow.removeTextChangedListener(textWatchListener)
        }
    }
}

fun dynamicTextFlow(text: List<String>): Flow<String> {
    return flow {
        while (true) {
            text.forEach { word ->
                var currentText = ""
                word.forEach {
                    currentText += it
                    emit(currentText)
                    delay(100)
                }
                delay(1000)
            }
        }
    }
}

fun String.dynamicTextFlow(): Flow<String> {
    return flow {
        while (true) {
            var currentText = ""
            this@dynamicTextFlow.forEach {
                currentText += it
                emit(currentText)
                delay(100)
            }
            delay(1000)
        }
    }
}

fun androidx.appcompat.widget.SearchView.onQueryTextListenerFlow(): Flow<String> {
    return callbackFlow {
        val listener = object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                trySendBlocking(newText ?: "")
                return true
            }
        }
        this@onQueryTextListenerFlow.setOnQueryTextListener(listener)
        awaitClose { setOnQueryTextListener(null) }
    }
}

fun RadioGroup.checkedChangeListenerFlow(): Flow<Int> {
    return callbackFlow {
        val checkedChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedButtonId: Int ->
            trySendBlocking(checkedButtonId)
        }
        setOnCheckedChangeListener(checkedChangeListener)
        awaitClose { setOnCheckedChangeListener(null) }
    }
}

fun RadioGroup.rbText() {
    var id = 0
    this.children.forEach {
        id = it.id
        val rb = findViewById<RadioButton>(id)
        rb.text = "asa12"
    }
}

fun showLocation(latitude: Float, longitude: Float, zoomLevel: Int?): Intent? {
    val intent = Intent(Intent.ACTION_VIEW)
    var data = String.format("geo:%s,%s", latitude, longitude)
//    if (zoomLevel != null) {
//        data = String.format("%s?z=%s", data, zoomLevel)
//    }
    intent.data = Uri.parse(data)
    return intent
}

fun Int.fromDpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.densityDpi
    val pixelsInDp = density / DisplayMetrics.DENSITY_DEFAULT
    return this * pixelsInDp
}

fun Int.getEndingType(): Int {
    val xx = this % 100
    val ending: Int = if (xx <= 20) {
        when (xx) {
            1 -> ENDING_WITH_1
            in 2..4 -> ENDING_WITH_2_3_4
            else -> ENDING_OTHERS
        }
    } else {
        when (this % 10) {
            1 -> ENDING_WITH_1
            in 2..4 -> ENDING_WITH_2_3_4
            else -> ENDING_OTHERS
        }
    }
    return ending
}

const val ENDING_WITH_1 = 111111
const val ENDING_WITH_2_3_4 = 234234
const val ENDING_OTHERS = 123456

fun Fragment.findNavControllerSafely(): NavController? {
    return if (isAdded) {
        findNavController()
    } else {
        null
    }
}
