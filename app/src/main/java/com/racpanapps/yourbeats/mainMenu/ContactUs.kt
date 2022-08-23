package com.racpanapps.yourbeats.mainMenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds

class ContactUs : AppCompatActivity() {

    private lateinit var constraintLayoutContactUs : ConstraintLayout
    private lateinit var linearLayoutBannerContactUs : LinearLayout
    private lateinit var editTextSubject : EditText
    private lateinit var editTextMessage : EditText
    private lateinit var buttonSendEmail : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us)

        initializeControls()
        initializeListeners()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerContactUs)
        InitializeBannerAds.setBackground(application, constraintLayoutContactUs)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        editTextSubject.text.clear()
        editTextMessage.text.clear()
        buttonSendEmail.text = resources.getText(R.string.send_email)
    }

    private fun initializeControls() {
        constraintLayoutContactUs = findViewById(R.id.constraintLayoutContactUs)
        linearLayoutBannerContactUs = findViewById(R.id.linearLayoutBannerContactUs)
        editTextSubject = findViewById(R.id.editTextSubject)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSendEmail = findViewById(R.id.buttonSendEmail)
    }

    private fun initializeListeners() {
        editTextSubject.doOnTextChanged { text, _, _, _ ->
            checkEmailMessage(text.toString(), editTextMessage.text.toString(), buttonSendEmail)
        }
        editTextMessage.doOnTextChanged { text, _, _, _ ->
            checkEmailMessage(editTextSubject.text.toString(), text.toString(), buttonSendEmail)
        }
        buttonSendEmail.setOnClickListener {
            if (checkEmailMessage(editTextSubject.text.toString(), editTextMessage.text.toString(), buttonSendEmail)) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = resources.getString(R.string.email_type)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.email)))
                intent.putExtra(Intent.EXTRA_SUBJECT, editTextSubject.text.toString())
                intent.putExtra(Intent.EXTRA_TEXT, editTextMessage.text.toString())
                try {
                    startActivity(Intent.createChooser(intent, resources.getString(R.string.choose_email_app)))
                } catch (e : Exception) {
                    Toast.makeText(applicationContext, resources.getString(R.string.failed_to_send_email), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkEmailMessage(subject : String, message : String, send : Button) : Boolean {
        val validEmailMessage : Boolean
        if (subject.length > 3 && message.length > 3) {
            send.text = resources.getString(R.string.send_email)
            validEmailMessage = true
        } else {
            send.text = resources.getString(R.string.please_check_your_email)
            validEmailMessage = false
        }
        return validEmailMessage
    }
}