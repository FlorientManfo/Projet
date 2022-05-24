package net.tipam2022.projet

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

var PhoneNumber: Long = 0
var UserName: String? = null
var auth: FirebaseAuth? = null
var firebaseDatabase: FirebaseDatabase? = null
var databaseReference: DatabaseReference? = null
var storageReference: StorageReference? = null
val pattern = Regex("[6][7,5,8,9]\\d{7}")

fun showDialog(context: Context, title: String, msg: String,
               positiveBtnText: String, negativeBtnText: String?,
               positiveBtnClickListener: DialogInterface.OnClickListener,
               negativeBtnClickListener: DialogInterface.OnClickListener?): AlertDialog {
    val builder = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setCancelable(true)
        .setPositiveButton(positiveBtnText, positiveBtnClickListener)
    if (negativeBtnText != null)
        builder.setNegativeButton(negativeBtnText, negativeBtnClickListener)
    val alert = builder.create()
    alert.show()
    return alert
}

fun getFileExtension(uri: Uri?, context: Context): String? {
    val cR = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri!!))
}

@Throws(IOException::class)
fun getBytes(inputStream: InputStream): ByteArray? {
    val byteBuffer = ByteArrayOutputStream()
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var len = 0
    while (inputStream.read(buffer).also { len = it } != -1) {
        byteBuffer.write(buffer, 0, len)
    }
    return byteBuffer.toByteArray()
}

fun toggleAccessibility(window: Window, visibility: Int){
    if(visibility == View.GONE){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }else{
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}

fun String?.toBitmap(): Bitmap?{
    var imageBytes = Base64.decode(this, 0)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Bitmap.toBase64(): String? {
    if(this!=null){
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,60,this)
            var byteArray = this.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }
    return null
}