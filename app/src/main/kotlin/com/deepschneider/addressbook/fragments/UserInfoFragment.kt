package com.deepschneider.addressbook.fragments

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.deepschneider.addressbook.databinding.UserInfoFragmentBinding
import com.deepschneider.addressbook.dto.User
import com.deepschneider.addressbook.utils.NetworkUtils
import com.deepschneider.addressbook.utils.Urls
import com.google.android.material.chip.Chip
import com.google.gson.Gson


class UserInfoFragment : Fragment() {

    private lateinit var binding: UserInfoFragmentBinding
    private lateinit var requestQueue: RequestQueue
    private lateinit var listener: FragmentActivity
    private var serverUrl: String? = null
    private val requestTag = "USER_INFO_TAG"
    private val gson = Gson()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            listener = (context as FragmentActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestQueue = Volley.newRequestQueue(listener)
        serverUrl = NetworkUtils.getServerUrl(listener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    private fun updateUserInfo() {
        requestQueue.add(object :
            JsonObjectRequest(Method.GET, serverUrl + Urls.USER_INFO, null, { response ->
                val result = gson.fromJson(response.toString(), User::class.java)
                binding.username.text = result.login.uppercase()
                binding.chipGroupRoles.removeAllViews()
                result.roles.forEach { role ->
                    val chip = Chip(listener)
                    chip.text = role
                    chip.chipBackgroundColor = ColorStateList.valueOf(
                        getThemeAccentColor(
                            listener,
                            android.R.attr.colorPrimary
                        )
                    )
                    if (resources.configuration.isNightModeActive)
                        chip.setTextColor(Color.BLACK)
                    else
                        chip.setTextColor(Color.WHITE)
                    chip.isCloseIconVisible = false
                    binding.chipGroupRoles.addView(chip)
                }
            }, { error ->
                Log.d("USER INFO ERROR", error.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return NetworkUtils.addAuthHeader(super.getHeaders(), listener)
            }
        }.also { it.tag = requestTag })
    }

    override fun onStop() {
        super.onStop()
        requestQueue.cancelAll(requestTag)
    }

    fun getThemeAccentColor(context: Context, res: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(res, typedValue, true)
        return typedValue.data
    }
}