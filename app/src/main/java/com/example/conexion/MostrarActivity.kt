package com.example.conexion

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MostrarActivity : AppCompatActivity() {

    private lateinit var edCarrosDeportivos: EditText
    private lateinit var btnRegresar: ImageButton
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar)

        edCarrosDeportivos = findViewById(R.id.edCorrosDeportivos)
        btnRegresar = findViewById(R.id.imageButton)

        btnRegresar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        obtenerDatos()
    }

    private fun obtenerDatos() {
        val url = "http://192.168.100.118/conexion/carros_api.php"

        val formBody = FormBody.Builder()
            .add("action", "mostrar")
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    edCarrosDeportivos.setText("Error al obtener los datos: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()

                    runOnUiThread {
                        if (responseData != null) {
                            mostrarDatos(responseData)
                        } else {
                            edCarrosDeportivos.setText("No hay datos disponibles")
                        }
                    }
                } else {
                    runOnUiThread {
                        edCarrosDeportivos.setText("Error en la respuesta: ${response.code}")
                    }
                }
            }
        })
    }

    private fun mostrarDatos(responseData: String) {
        try {
            val jsonObject = JSONObject(responseData)

            if (jsonObject.getBoolean("success")) {
                val data: JSONArray = jsonObject.getJSONArray("data")
                val stringBuilder = StringBuilder()

                for (i in 0 until data.length()) {
                    val auto = data.getJSONObject(i)
                    val modelo = auto.getString("modelo")
                    val precio = auto.getString("precio")
                    val marca = auto.getString("marca")
                    val ano = auto.getString("ano")
                    val tipo = auto.getString("tipo")

                    stringBuilder.append("Modelo: $modelo\n")
                        .append("Precio: $precio\n")
                        .append("Marca: $marca\n")
                        .append("Año: $ano\n")
                        .append("Tipo: $tipo\n\n")
                }

                edCarrosDeportivos.setText(stringBuilder.toString())
            } else {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
        }
    }
}
