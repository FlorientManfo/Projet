package net.tipam2022.projet

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import net.tipam2022.projet.EditProfileActivity.Constants.DATABASE_PATH_UPLOADS
import net.tipam2022.projet.databinding.ActivityEditProfileBinding
import net.tipam2022.projet.entities.User
import java.io.File
import java.lang.reflect.Method
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private var currentUser: User? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editImageProfileButton.setOnClickListener {editProfileImage()}
        binding.currentPhoneNumber.text = PhoneNumber.toString()
        binding.currentUserName.text = UserName

        binding.progress.visibility= View.VISIBLE

        getInformation(this)

        binding.topBar.setNavigationOnClickListener {
            navigateUpTo(Intent(this, MainActivity::class.java))
        }
        binding.topBar.setOnMenuItemClickListener{ menuItemListener(it)}

        binding.buttonCancel.setOnClickListener { cancel() }
        binding.buttonSave.setOnClickListener { saveNewData() }

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            OPERATION_CHOOSE_PHOTO ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ===
                    PackageManager.PERMISSION_GRANTED){
                    openGallery()
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
            OPERATION_CAPTURE_PHOTO->{
                if (grantedResults.get(0) === PackageManager.PERMISSION_GRANTED) {
                    capturePhoto()
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(mUri!!))
                    binding.profileImage!!.setImageBitmap(bitmap)
                    Glide.with(getApplicationContext()).load(bitmap).into(binding.profileImage)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    mUri = data?.data
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data)
                        Glide.with(getApplicationContext()).load(data?.data!!).into(binding.profileImage)
                    }
                }

        }
        println("current Uri------------->$mUri")
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun editProfileImage(){
        val popupMenu: PopupMenu = PopupMenu(this,binding.editImageProfileButton)
        popupMenu.menuInflater.inflate(R.menu.profile_image_option,popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(
            PopupMenu.OnMenuItemClickListener { item ->when(item.itemId) {
                R.id.openCamera->{ capturePhoto()}
                R.id.openGallery ->{
                    val checkSelfPermission = ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    }
                    else{
                        openGallery()
                    }
                }
            }
                true
            })
        // show icons on popup menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }else{
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popupMenu]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        popupMenu.show()
    }

    private fun saveNewData(){
        binding.progress.visibility = View.VISIBLE
        var email = binding.newEmail.text.toString()
        var newName = binding.newUserName.text.toString()
        var birthDay = binding.birthDay.text.toString()
        if(email!=null || newName != null || birthDay!= null || mUri != null){
            UserName = newName
            currentUser?.userName = newName
            currentUser?.birthDay = birthDay
            currentUser?.email = email
            uploadFile(mUri)
            saveLocal()
        }
        else{
            showDialog(this, "title",
                "Bad information please check fields and try again",
                "OK",null,
                {dinterface, it ->},
                {dinterface, it ->})
        }
    }

    private fun cancel(){
        binding.newEmail.setText("")
        binding.newUserName.setText("")
        binding.birthDay.setText(Date().toString())
        finish()
    }

    private fun saveLocal(){
        var sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.putString("userName", UserName)
        editor.putLong("phoneNumber", PhoneNumber)
        editor.commit()
    }


    private fun getInformation(context: Context){
        databaseReference = FirebaseDatabase.getInstance()
            .getReference(Constants.DATABASE_PATH_UPLOADS)
        //adding an event listener to fetch values

        //adding an event listener to fetch values
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //dismissing the progress dialog
                binding.progress.visibility = View.GONE

                //iterating through all the values in database
                for (postSnapshot in snapshot.children) {
                    val user: User? = postSnapshot.getValue(User::class.java)
                    if(user?.userPhoneNumber == PhoneNumber)
                        currentUser = user!!
                }

                if(currentUser?.profile == null)
                    getDefaultProfileImage()
                else
                    Glide.with(getApplicationContext()).load(currentUser?.profile.toBitmap()).into(binding.profileImage);

                binding.newEmail.setText(currentUser?.email)
                binding.newUserName.setText(currentUser?.userName)
                binding.birthDay.setText(currentUser?.birthDay)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                binding.progress.visibility = View.GONE
            }
        })
    }

    fun getDefaultProfileImage(){
        var generatedFilePath: String? = null
        storageReference!!.child("default_profile.png").downloadUrl.addOnSuccessListener(
            OnSuccessListener<Uri?> {
                // Got the download URL for 'profileImages/default_profile.png'
                val downloadUri: Uri = it
                generatedFilePath = downloadUri.toString() /// The string(file link) that you need
                println("--------->Default image url $generatedFilePath")
                Glide.with(getApplicationContext()).load(generatedFilePath).into(binding.profileImage)
            }).addOnFailureListener(OnFailureListener {
        })
    }

    private fun uploadFile(imagePath: Uri?) {
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_UPLOADS)
        //creating the upload object to store uploaded image details

        val user = User(
            PhoneNumber!!,
            UserName!!,
            currentUser?.email!!,
            currentUser?.birthDay!!,
            currentUser?.createdAt!!,
            currentUser?.profile
        )

        var filePath = imagePath
        println("filePath-------------+>$filePath")
        //checking if file is available
        if (filePath != null) {
            var newImage = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            user.profile = newImage.toBase64()
        }
        databaseReference?.child("$PhoneNumber")?.setValue(user)?.addOnCompleteListener {

            it.addOnSuccessListener {
                showDialog(this, "title",
                    "Your informations has been updated !",
                    "OK",null,
                    {_,_ ->
                        binding.progress.visibility = View.GONE
                        finish()},
                    {dinterface, it ->})
            }
            it.addOnFailureListener {
                binding.progress.visibility = View.GONE
                showDialog(this, "title",
                    "Something went wrong please try again later !",
                    "OK",null,
                    {_,_ ->
                        binding.progress.visibility = View.GONE
                        finish()},
                    {dinterface, it ->})
                println(it.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun capturePhoto(){
        val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        if(capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "net.tipam2022.projet.provider",
                capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), OPERATION_CAPTURE_PHOTO)
        } else {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
            startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
        }

    }

    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding.profileImage?.setImageBitmap(bitmap)
        }
        else {
            show("ImagePath is null")
        }
    }


    private fun show(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri?.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse(
                    "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri?.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri?.scheme, ignoreCase = true)){
            imagePath = uri?.path
        }
        renderImage(imagePath)
        println("--------->${mUri}")
    }

    private fun menuItemListener(item: MenuItem): Boolean{
        return when (item.itemId) {
            R.id.signOut -> {
                auth = FirebaseAuth.getInstance()
                // navigate to settings screen
                if (auth!!.currentUser != null) {
                    auth!!.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                }
                println("Sign out!")
                true
            }
            R.id.notification -> {
                // save profile changes
                true
            }
            else -> false
        }
    }


    object Constants {
        const val DATABASE_PATH_UPLOADS = "users"
    }
}