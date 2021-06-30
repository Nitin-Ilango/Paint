package com.nitin.drawingapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_main.*
import petrov.kristiyan.colorpicker.ColorPicker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ib_redo.setOnClickListener {
            drawing_view.onClickRedo()
        }

        ib_undo.setOnClickListener {
            drawing_view.onClickUndo()
        }
    }

     fun showBrushSizeChooserDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_brush_size, null)
        val etBrushSize = dialogLayout.findViewById<EditText>(R.id.et_brush_size)

        with(builder) {
            setTitle("Enter Brush size")
            setPositiveButton("OK") { _, _ ->
                if(etBrushSize.text.toString().trim() != ""){
                    brush_size.text = etBrushSize.text.toString().trim()
                    drawing_view.setSizeForBrush(brush_size.text.toString().toFloat())
                }
            }

            setView(dialogLayout)
            show()
        }
    }

     fun showColorPalette(view: View) {
        val colorPicker = ColorPicker(this)
        colorPicker.setOnFastChooseColorListener(object: ColorPicker.OnFastChooseColorListener{
            override fun setOnFastChooseColorListener(position: Int, color: Int) {
                drawing_view.setColor(color)
            }

            override fun onCancel() {
                colorPicker.dismissDialog()
            }
        })
            .setColumns(5)
            .setRoundColorButton(true)
            .show()
    }

    fun changeShape(view: View){
        val ib = view as ImageButton
        drawing_view.setShape(ib.tag.toString().toInt())
        drawing_view.setStyle(ib.tag.toString().toInt())
        //0 -> pencil
        //1 -> line
        //2 -> rectangle stroke
        //3 -> rectangle fill
        //4 -> circle stroke
        //5 -> circle fill
        //6 -> oval stroke
        //7 -> oval fill
    }



}
