package com.example.conexion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class RegistroActivity : AppCompatActivity() {

    private lateinit var etModelo: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etMarca: EditText
    private lateinit var etAno: EditText
    private lateinit var edTipo: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnMostrar: Button

    val url = "http://192.168.100.118/conexion/carros_api.php"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etModelo = findViewById(R.id.etModelo)
        etPrecio = findViewById(R.id.etPrecio)
        etMarca = findViewById(R.id.etMarca)
        etAno = findViewById(R.id.etAno)
        edTipo = findViewById(R.id.edTipo)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnEditar = findViewById(R.id.btnEditar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnMostrar = findViewById(R.id.btnMostrar)

        btnRegistrar.setOnClickListener { registrarAuto() }
        btnBuscar.setOnClickListener { buscarAuto() }
        btnEditar.setOnClickListener { editarAuto() }
        btnEliminar.setOnClickListener { eliminarAuto() }
        btnMostrar.setOnClickListener {
            val intent = Intent(this, MostrarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registrarAuto() {
        val modelo = etModelo.text.toString()
        val precio = etPrecio.text.toString()
        val marca = etMarca.text.toString()
        val ano = etAno.text.toString()
        val tipo = edTipo.text.toString()

        if (modelo.isEmpty() || precio.isEmpty() || marca.isEmpty() || ano.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val formBody = FormBody.Builder()
            .add("action", "registrar")
            .add("modelo", modelo)
            .add("precio", precio)
            .add("marca", marca)
            .add("ano", ano)
            .add("tipo", tipo)
            .build()

        enviarPeticion(formBody, "Registro exitoso")
    }

    private fun buscarAuto() {
        val modelo = etModelo.text.toString()
        if (modelo.isEmpty()) {
            Toast.makeText(this, "Ingrese el modelo del auto para buscar", Toast.LENGTH_SHORT).show()
            return
        }

        val formBody = FormBody.Builder()
            .add("action", "buscar")
            .add("modelo", modelo)
            .build()

        client.newCall(Request.Builder().url(url).post(formBody).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistroActivity, "Error al buscar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body?.string()

                    if (responseData != null) {
                        val jsonResponse = JSONObject(responseData)

                        if (jsonResponse.getBoolean("success")) {
                            runOnUiThread {
                                try {
                                    val auto = jsonResponse.getJSONObject("data")

                                    etModelo.setText(auto.getString("modelo"))
                                    etPrecio.setText(auto.getString("precio"))
                                    etMarca.setText(auto.getString("marca"))
                                    etAno.setText(auto.getString("ano"))
                                    edTipo.setText(auto.getString("tipo"))
                                } catch (e: Exception) {
                                    Toast.makeText(this@RegistroActivity, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            runOnUiThread { Toast.makeText(this@RegistroActivity, "Auto no encontrado", Toast.LENGTH_SHORT).show() }
                        }
                    } else {
                        runOnUiThread { Toast.makeText(this@RegistroActivity, "Respuesta vacía", Toast.LENGTH_SHORT).show() }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@RegistroActivity, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
            }
        })
    }




    private fun editarAuto() {
        val modelo = etModelo.text.toString()
        val precio = etPrecio.text.toString()
        val marca = etMarca.text.toString()
        val ano = etAno.text.toString()
        val tipo = edTipo.text.toString()

        if (modelo.isEmpty() || precio.isEmpty() || marca.isEmpty() || ano.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val formBody = FormBody.Builder()
            .add("action", "editar")
            .add("modelo", modelo)
            .add("precio", precio)
            .add("marca", marca)
            .add("ano", ano)
            .add("tipo", tipo)
            .build()

        enviarPeticion(formBody, "Auto actualizado correctamente")
    }

    private fun eliminarAuto() {
        val modelo = etModelo.text.toString()
        if (modelo.isEmpty()) {
            Toast.makeText(this, "Ingrese el modelo del auto para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val formBody = FormBody.Builder()
            .add("action", "eliminar")
            .add("modelo", modelo)
            .build()

        enviarPeticion(formBody, "Auto eliminado correctamente")
    }

    private fun enviarPeticion(formBody: FormBody, mensajeExito: String) {
        client.newCall(Request.Builder().url(url).post(formBody).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@RegistroActivity, "Error en la operación", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "")
                runOnUiThread {
                    val mensaje = jsonResponse.optString("message", mensajeExito)
                    Toast.makeText(this@RegistroActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
