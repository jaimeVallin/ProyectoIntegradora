package com.actividadintegradora.loginandsignup

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.actividadintegradora.loginandsignup.R

class ImageListActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var listView: ListView

    // Método llamado al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        // Inicialización de variables
        listView = findViewById(R.id.imageListView)

        // Obtiene el nombre de la carpeta de la intención
        val folderName = intent.getStringExtra("folder_name")

        // Verifica si el nombre de la carpeta no es nulo
        if (folderName != null) {
            // Obtiene la lista de rutas de imágenes en la carpeta especificada
            val imagesInFolder = getImagesInFolder(folderName)

            // Configura el adaptador de la lista con las rutas de las imágenes
            val adapter = ImageListAdapter(this, imagesInFolder)
            listView.adapter = adapter
        }
    }

    // Función para obtener la lista de rutas de imágenes en una carpeta específica
    @SuppressLint("Recycle")
    private fun getImagesInFolder(folderName: String?): List<String> {
        // Asegúrate de que folderName no sea nulo
        folderName?.let {
            // Construye la ruta completa de la carpeta de imágenes
            val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/$folderName"
            Log.d("DEBUG", "Folder path: $folderPath")

            // Define las columnas que se deben recuperar de la base de datos de imágenes
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )

            // Configura la selección para obtener solo imágenes de la carpeta específica
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(folderName)

            // Realiza una consulta para obtener las rutas de las imágenes en la carpeta
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Images.Media.DATA} like ?",
                arrayOf("$folderPath%"),
                null
            )

            // Crea una lista mutable para almacenar las rutas de las imágenes
            val imagesList = mutableListOf<String>()

            // Procesa el cursor para extraer las rutas de las imágenes
            cursor?.use {
                val dataColumn: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                while (it.moveToNext()) {
                    val imagePath: String = it.getString(dataColumn)
                    imagesList.add(imagePath)
                }
            }

            // Devuelve la lista de rutas de imágenes en la carpeta
            return imagesList
        }

        // Si folderName es nulo, puedes manejarlo de acuerdo a tus necesidades.
        return emptyList()
    }
}