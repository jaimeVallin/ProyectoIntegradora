package com.actividadintegradora.loginandsignup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.File

// Adaptador personalizado para la lista de imágenes
class ImageListAdapter(
    private val context: Context,
    private val imagePaths: List<String>
) : ArrayAdapter<String>(context, 0, imagePaths) {

    // Método llamado para obtener la vista de un elemento en la posición dada
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        // Si la vista no existe, inflarla desde el diseño personalizado list_item_image
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false)
        }

        // Obtener referencias a los elementos de la vista
        val imageView = convertView!!.findViewById<ImageView>(R.id.imageView)
        val textView = convertView.findViewById<TextView>(R.id.textView)

        // Obtener la ruta de la imagen en la posición actual
        val imagePath = getItem(position)

        // Verificar si la ruta de la imagen no es nula
        if (imagePath != null) {
            // Cargar la imagen en el ImageView usando la biblioteca Glide
            Glide.with(context)
                .load(File(imagePath))
                .centerCrop()
                // Puedes establecer un placeholder si lo deseas
                // .placeholder(R.drawable.ic_placeholder)
                .into(imageView)

            // Establecer el nombre del archivo como texto en el TextView
            textView.text = File(imagePath).name
        }

        // Devolver la vista actualizada
        return convertView
    }
}
