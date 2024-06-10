package com.mohamed.aykhedmah.view.common.notification

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.notifications.NotificationResponse
import com.mohamed.aykhedmah.utils.LocaleHelper
import com.mohamed.aykhedmah.utils.ViewState
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_notification_screen.*
import javax.inject.Inject

class NotificationScreen : BaseScreen() {

    private lateinit var dialog: AlertDialog
    private lateinit var mAdapter: NotificationAdapter

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var notificationViewModel: NotificationViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_screen)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "التنبيهات"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_right_black_24dp)

        mAdapter = NotificationAdapter()
        notifications_recycler.itemAnimator = DefaultItemAnimator()
        notifications_recycler.adapter = mAdapter

        mAdapter.onItemClickListener = object :
            NotificationAdapter.OnItemClickListener {
            override fun setOnItemClickListener() {

            }
        }
        dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .build()

        configViewModel()
        getNotifications()
    }

    private fun configViewModel() {
        notificationViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
    }

    private fun getNotifications() {
        notificationViewModel.getNotifications(sharedPreferences.getString(Constants.ACCESS_TOKEN, "")!!, sharedPreferences.getString(Constants.USERID, "")!!).observe(this,
            Observer<ViewState<NotificationResponse>>
            { viewState ->
                when (viewState.status) {
                    ViewState.Status.LOADING -> {
                        dialog.show()
                    }

                    ViewState.Status.SUCCESS -> {
                        dialog.dismiss()
                        if (viewState.data!!.status == 200) {
                            if (viewState.data.data.isEmpty()){
                                notifications_recycler.visibility = View.GONE
                                empty_layout.visibility = View.VISIBLE
                            }else{
                                mListItems.addAll(viewState.data.data)
                                addAdMobBannerAds()
                                mAdapter.submitList(mListItems)
                                loadBannerAds()
                            }
                        } else {
                            Toast.makeText(baseContext, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    ViewState.Status.ERROR -> {
                        dialog.dismiss()
                        Toast.makeText(baseContext, "${viewState.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        LocaleHelper().setLocale(this, "ar")
    }
}