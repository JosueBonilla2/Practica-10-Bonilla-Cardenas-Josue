package com.example.conexion


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var checkBoxRemember: CheckBox
    private lateinit var btnIngresar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnLimpiar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        checkBoxRemember = findViewById(R.id.cbRegistrar)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnSalir = findViewById(R.id.btnSalir)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val savedUser = sharedPreferences.getString("usuario", "")
        val savedPassword = sharedPreferences.getString("contrasena", "")

        if (!savedUser.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            etUsuario.setText(savedUser)
            etContrasena.setText(savedPassword)
            checkBoxRemember.isChecked = true
        }

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contrasena = etContrasena.text.toString()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                if (checkBoxRemember.isChecked) {
                    val editor = sharedPreferences.edit()
                    editor.putString("usuario", usuario)
                    editor.putString("contrasena", contrasena)
                    editor.apply()
                    Toast.makeText(this, "Usuario y contraseña almacenados", Toast.LENGTH_SHORT).show()
                }

                val updatedUser = sharedPreferences.getString("usuario", "")
                val updatedPassword = sharedPreferences.getString("contrasena", "")

                acceder(usuario, contrasena, updatedUser, updatedPassword)
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLimpiar.setOnClickListener{ limpiar() }

        btnSalir.setOnClickListener {
            finishAffinity()
        }
    }

    private fun acceder(usuario: String, contrasena: String, savedUser: String?, savedPassword: String?) {
        if (usuario == savedUser && contrasena == savedPassword) {
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }

    fun limpiar(){
        etUsuario.text.clear()
        etContrasena.text.clear()
        checkBoxRemember.isChecked = false
    }
}
