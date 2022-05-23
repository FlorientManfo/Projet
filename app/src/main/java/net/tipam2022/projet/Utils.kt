package net.tipam2022.projet

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import net.tipam2022.projet.entities.User

var PhoneNumber: String? = null
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