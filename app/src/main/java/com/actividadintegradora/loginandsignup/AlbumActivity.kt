package com.actividadintegradora.loginandsignup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.actividadintegradora.loginandsignup.R
import com.google.firebase.auth.FirebaseAuth

class AlbumActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var listView: ListView
    private lateinit var btnSignOut: Button
    private val REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1
    private lateinit var firebaseAuth: FirebaseAuth

    // Método llamado al crear la actividad
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        // Inicialización de variables
        listView = findViewById(R.id.listView)
        btnSignOut = findViewById(R.id.btnSignOut)
        firebaseAuth = FirebaseAuth.getInstance()

        // Verifica el permiso y carga las carpetas solo si el permiso está otorgado
        if (checkReadExternalStoragePermission()) {
            val folders = getImagesFolders()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, folders)
            listView.adapter = adapter

            // Configura el listener para hacer algo cuando se selecciona una carpeta en la lista
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedFolder = folders[position]
                openImageListActivity(selectedFolder)
            }
        }

        // Configura el listener para el botón de cerrar sesión
        btnSignOut.setOnClickListener {
            signOut()
        }
    }

    // Función para verificar el permiso de lectura del almacenamiento externo
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkReadExternalStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            // Solicita permisos si no están otorgados
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            )
            return false
        }
    }

    // Función para obtener las carpetas que contienen imágenes
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getImagesFolders(): List<String> {
        // Obtiene la ruta de la carpeta de imágenes
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        Log.d("DEBUG", "Folder path: $folderPath")

        // Define las columnas que se deben recuperar de la base de datos de imágenes
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        // Realiza una consulta para obtener las carpetas que contienen imágenes
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Images.Media.DATA} like ?",
            arrayOf("$folderPath%"),
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )

        // Crea un conjunto mutable para almacenar nombres de carpetas únicos
        val folderSet = mutableSetOf<String>()

        // Procesa el cursor para extraer nombres de carpetas
        cursor?.use {
            val bucketNameColumn: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (it.moveToNext()) {
                val folderName: String = it.getString(bucketNameColumn)
                folderSet.add(folderName)
            }
        }

        // Convierte el conjunto a una lista y la devuelve
        return folderSet.toList()
    }

    // Función para abrir la actividad que muestra la lista de imágenes en una carpeta específica
    private fun openImageListActivity(folderName: String) {
        val intent = Intent(this, ImageListActivity::class.java)
        intent.putExtra("folder_name", folderName)
        startActivity(intent)
    }

    // Función para cerrar la sesión del usuario actual
    private fun signOut() {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish() // Asegúrate de finalizar la actividad actual
    }
}