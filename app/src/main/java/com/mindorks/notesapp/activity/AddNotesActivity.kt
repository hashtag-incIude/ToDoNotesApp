package com.mindorks.notesapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.mindorks.notesapp.BuildConfig
import com.mindorks.notesapp.R
import com.mindorks.notesapp.util.AppConstant
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddNotesActivity : AppCompatActivity() {
    val MY_PERMISSION_CODE = 124
    lateinit var editTextTitle: EditText
    lateinit var editTextDescription: EditText
    lateinit var submitButton: Button
    lateinit var imageViewAdd: ImageView
    val REQUEST_CODE_CAMERA = 1
    val REQUEST_CODE_GALLERY = 2
    var picturePath = ""
    lateinit var imageLocation: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)
        bindViews()
        clickListener()
    }

    private fun clickListener() {
        submitButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent()
                intent.putExtra(AppConstant.TITLE, editTextTitle.text.toString())
                intent.putExtra(AppConstant.DESCRIPTION, editTextDescription.text.toString())
                intent.putExtra(AppConstant.IMAGE_PATH, picturePath)
                setResult( Activity.RESULT_OK, intent)
                finish()
            }

        })
        imageViewAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkAndRequestPermissions()) {
                    //If you have already permitted the permission
                    setupDialog()

                }
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }


    private fun setupDialog() {
        val view = LayoutInflater.from(this@AddNotesActivity).inflate(R.layout.dialog_selector, null)
        val textViewCamera = view.findViewById<TextView>(R.id.textViewCamera)
        val textViewGallery = view.findViewById<TextView>(R.id.textViewGallery)
        val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create()
        textViewCamera.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        // Error occurred while creating the File
                    }

                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(this@AddNotesActivity,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile)

                        imageLocation = photoFile
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)

                    }
                }



                dialog.hide()

            }

        })
        textViewGallery.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_CODE_GALLERY)
                dialog.hide()
            }

        })
        dialog.show()
    }

    private fun bindViews() {
        editTextTitle = findViewById(R.id.editTextTitle)
        imageViewAdd = findViewById(R.id.imageViewAdd)
        editTextDescription = findViewById(R.id.editTextDescription)
        submitButton = findViewById(R.id.submit_button)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            REQUEST_CODE_CAMERA -> Glide.with(this).load(imageLocation.absoluteFile).into(imageViewAdd)
            REQUEST_CODE_GALLERY -> {
                val selectedImage = data?.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c = contentResolver.query(selectedImage, filePath, null, null, null)
                c?.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                picturePath = c.getString(columnIndex)
                c.close()
                Glide.with(this).load(picturePath).into(imageViewAdd)

            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toTypedArray<String>(), MY_PERMISSION_CODE)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                    setupDialog()
                }
            }
        }
    }

}

