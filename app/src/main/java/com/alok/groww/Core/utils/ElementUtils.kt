package com.alok.groww.Core.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alok.groww.R
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.TimeZone


object ElementUtils {


    class EasyElements(val context: Context) {
        fun dialog(title: String, msg: String, postiveText : String, negativeText : String ,positive: () -> Unit, negative: () -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton(postiveText) { dialog, which ->
                positive()
            }
            builder.setNegativeButton(negativeText) { dialog, which ->
                negative()
            }
            val dialog = builder.create()
            dialog.show()
        }

        fun restartApp() {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            if (context is AppCompatActivity) {
                context.finish()
            }
        }

        fun singleBtnDialog_ServerError(
            title: String = "Failure",
            msg: String = "Failed to get results, please try again",
            btnText: String = "Okay",
            positive: () -> Unit
        ) {
            if (context is Activity) {
                val activity = context

                if (!activity.isFinishing && !activity.isDestroyed) {
                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setIcon(com.google.android.material.R.drawable.mtrl_ic_error)
                    builder.setTitle(title)
                    builder.setMessage(msg)
                    builder.setCancelable(false)
                    builder.setPositiveButton(btnText) { dialog, which ->
                        positive()
                    }

                    try {
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        // Handle the exception, e.g., log an error
                        e.printStackTrace()
                    }
                }
            }
        }


        fun singleBtnDialog(title: String, msg: String, btnText: String, positive: () -> Unit) {
            if (context is Activity) {
                val activity = context

                if (!activity.isFinishing && !activity.isDestroyed) {
                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setIcon(R.drawable.logogradhd)
                    builder.setTitle(title)
                    builder.setMessage(msg)
                    builder.setCancelable(false)
                    builder.setPositiveButton(btnText) { dialog, which ->
                        positive()
                    }

                    try {
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun singleBtnDialog_InputError(title: String, msg: String, btnText: String, positive: () -> Unit) {
            if (context is Activity) {
                val activity = context

                if (!activity.isFinishing && !activity.isDestroyed) {
                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setIcon(R.drawable.logogradhd)
                    builder.setTitle(title)
                    builder.setMessage(msg)
                    builder.setCancelable(false)
                    builder.setPositiveButton(btnText) { dialog, which ->
                        positive()
                    }

                    try {
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun twoBtnDialog(
            title: String,
            msg: String,
            positiveBtnText: String,
            negativeBtnText: String,
            positive: () -> Unit,
            negative: () -> Unit
        ) {
            if (context is Activity) {
                val activity = context

                if (!activity.isFinishing && !activity.isDestroyed) {
                    val builder = AlertDialog.Builder(context)
                    builder.setIcon(R.drawable.logogradhd)
                    builder.setTitle(title)
                    builder.setMessage(msg)
                    builder.setPositiveButton(positiveBtnText) { dialog, which ->
                        positive()
                    }
                    builder.setNegativeButton(negativeBtnText) { dialog, which ->
                        negative()
                    }
                    try {
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun twoBtn(
            title: String,
            msg: String,
            positiveBtnText: String,
            negativeBtnText: String,
            positive: (isPositive: Boolean) -> Unit,
            negative: (isNegative: Boolean) -> Unit
        ) {
            if (context is Activity) {
                val activity = context

                if (!activity.isFinishing && !activity.isDestroyed) {
                    val builder = AlertDialog.Builder(context)
                    builder.setIcon(R.drawable.logogradhd)
                    builder.setTitle(title)
                    builder.setMessage(msg)
                    builder.setPositiveButton(positiveBtnText) { dialog, which ->
                        positive(true)
                    }
                    builder.setNegativeButton(negativeBtnText) { dialog, which ->
                        negative(true)
                    }
                    try {
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: WindowManager.BadTokenException) {
                        e.printStackTrace()
                    }
                }
            }
        }



        fun singleBtnDialog_ErrorConnection(title: String, msg: String, btnText: String, positive: () -> Unit) {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setIcon(R.drawable.baseline_signal_wifi_connected_no_internet_4_24)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton(btnText) { dialog, which ->
                positive()
            }
            val dialog = builder.create()
            dialog.show()
        }

        fun showActionSnackbar(rootView: View, msg: String, duration: Int,actionText : String, action: () -> Unit) {
            Snackbar.make(rootView, msg, duration)
                .setAction(actionText) {
                    action()
                }
                .show()
        }

        fun showSnackbar(rootView: View, msg: String, duration: Int) {
            val snackbar = Snackbar.make(rootView, msg, duration)
            snackbar.setTextColor(Color.BLACK)
            snackbar.setBackgroundTint(Color.WHITE)
            snackbar.show()
        }


        fun loader(visible: Int) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Loading...")
            progressDialog.setMessage("Please wait.")
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(false)
            when (visible) {
                1 -> progressDialog.show()
                0 -> progressDialog.dismiss()
            }

        }
    }



    class EasyLogging(val filename: String, val context: Context) {


        //Debug101 = Debug
        val extension = "-Debug101"


        fun lgd(msg: String) {
            Timber.tag(filename + extension).d(msg)
        }

        fun lge(msg: String) {
            Timber.tag(filename + extension).e(msg)
        }

        fun tst(msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }


    }



}