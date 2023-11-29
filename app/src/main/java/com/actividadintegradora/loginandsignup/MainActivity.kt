package com.actividadintegradora.loginandsignup

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.actividadintegradora.loginandsignup.R
import com.actividadintegradora.loginandsignup.databinding.ActivityMainBinding
import java.io.File
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    // Nombre del álbum
    private val albumName = "WeddingSnap"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                it.resolveActivity(packageManager).also { component ->
                    createPhotoFile()
                    val photoUri: Uri =
                        FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".fileprovider", file
                        )
                    it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    currentImageUri = photoUri  // Actualizar el URI actual
                }
            }
            openCamera.launch(intent)
        }

        binding.saveToGallery.setOnClickListener {
            // Actualizar el URI después de guardar
            if (currentImageUri != null) {
                saveToGallery()
            }
                currentImageUri?.let { uri ->
                    if (!uri.toString().isNullOrEmpty()) {
                        binding.img.setImageURI(uri)
                        // Ocultar la imagen después de establecer el URI
                        binding.img.visibility = View.GONE

                    } else {
                    // Manejar el caso donde el URI está vacío
                    Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.openGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
            pickImage.launch(galleryIntent)
        }

        binding.verAlbum.setOnClickListener {
            Log.d("MainActivity", "Botón verAlbum presionado")
            val intent = Intent(this, AlbumActivity::class.java)
            startActivity(intent)
        }
    }

    private val openCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // No es necesario actualizar el URI aquí, ya que se hace en createPhotoFile()
                val bitmap = getBitmap()
                binding.img.setImageBitmap(bitmap)
            }
        }

    private lateinit var file: File

    private fun createPhotoFile() {
        // Obtener el directorio específico del álbum
        val albumDir = getAlbumStorageDir(albumName)
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", albumDir)
        currentImageUri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".fileprovider", file
        )
    }

    private fun getAlbumStorageDir(albumName: String): File {
        // Crear el directorio del álbum si no existe
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            albumName
        )

        // Crear subdirectorio "WeddingSnap" dentro del directorio del álbum
        val weddingSnapDir = File(file, "WeddingSnap")
        if (!weddingSnapDir.exists()) {
            weddingSnapDir.mkdirs()
        }

        return weddingSnapDir
    }

    private fun saveToGallery() {
        val content = createContent()
        try {
            val uri = save(content)
            clearContents(content, uri)
            Toast.makeText(this, getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
        }

    }

    private fun createContent(): ContentValues {
        val fileName = file.name
        val fileType = "image/jpg"
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, fileType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "$albumName/WeddingSnap")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    private fun save(content: ContentValues): Uri {
        var outputStream: OutputStream?
        var uri: Uri?
        application.contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
            outputStream = resolver.openOutputStream(uri!!)
        }
        outputStream.use { output ->
            getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, output)
        }
        return uri!!


    }

    private fun clearContents(content: ContentValues, uri: Uri) {
        content.clear()
        content.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(uri, content, null, null)
    }

    private fun getBitmap(): Bitmap {
        return BitmapFactory.decodeFile(file.toString())
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    binding.img.setImageURI(selectedImageUri)
                    currentImageUri = selectedImageUri  // Actualizar el URI actual
                }
            }
        }
}
