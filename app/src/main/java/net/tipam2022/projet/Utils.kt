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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
lateinit var frManager: FragmentManager

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


fun reloadCurrentFragment(fragmentTag: String){
    var frg: Fragment? = null
    frg = frManager.findFragmentByTag(fragmentTag)!!
    val ft: FragmentTransaction = frManager.beginTransaction()
    ft.detach(frg)
    ft.attach(frg)
    ft.commit()
}