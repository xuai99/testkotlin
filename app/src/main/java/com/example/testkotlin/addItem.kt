package com.example.testkotlin

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.net.URI

class addItem : AppCompatActivity() {
    lateinit var recipeName:EditText
    lateinit var recipeDescription:EditText
    lateinit var addNotes:EditText
    lateinit var ingredients:EditText
    lateinit var webSitesAdd:EditText
    lateinit var recipeType:EditText
    lateinit var addRecipe:FloatingActionButton
    lateinit var addImage:TextView
    lateinit var imageView: ImageView
    var getimageUri:String= ""
    lateinit var ref:DatabaseReference


    var REQUEST_CODE :Int = 100
    lateinit var sname : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        recipeName = findViewById(R.id.myRecipeName)
        recipeDescription = findViewById(R.id.myRecipeDirection)
        addNotes = findViewById(R.id.addNote)
        addRecipe = findViewById(R.id.myAddFRecipe)
        ingredients = findViewById(R.id.ingredientsA)
        webSitesAdd = findViewById(R.id.websitesAddressADD)
        recipeType = findViewById(R.id.recipeTypeAdd)
        addImage = findViewById(R.id.addImages)
        imageView = findViewById(R.id.myRecipeImage)
        ref = FirebaseDatabase.getInstance().getReference("Recipe")




        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {


                val data: Intent? = result.data
                sname = data?.data!!
                if(sname.toString().isNullOrEmpty()){
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    return@registerForActivityResult
                }else{
                    Picasso.get().load(sname).into(imageView)
                    Toast.makeText(applicationContext,sname.toString(),Toast.LENGTH_SHORT).show()

                    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://testkotlin-26bd0.appspot.com")
                    val uploadS = storageRef.child(sname.toString()+".jpg")
                    var uploadTask = uploadS.putFile(sname)
                    uploadTask.addOnFailureListener {
                        val imageUrl = it.toString()
                        Toast.makeText(applicationContext,imageUrl,Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        uploadS.downloadUrl.addOnSuccessListener {

                            getimageUri = it.toString()
                            Toast.makeText(applicationContext,"upload Finish",Toast.LENGTH_SHORT).show()

                        }


                }


                }
            }

        }



        addRecipe.setOnClickListener{
            val SrecipeName = recipeName.text.toString().trim()
            val SrecipeDesc = recipeDescription.text.toString().trim()
            val SaddNote = addNotes.text.toString().trim()
            val Singredients = ingredients.text.toString().trim()
            val addWebsites = webSitesAdd.text.toString().trim()
            val addrecipeType = recipeType.text.toString().trim()
            val addImageUri = getimageUri

            if(SrecipeName.isEmpty() || SrecipeDesc.isEmpty() || SaddNote.isEmpty() || Singredients.isEmpty() || addWebsites.isEmpty() || addrecipeType.isEmpty() || addImageUri.isEmpty()){
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                return@setOnClickListener
            }
            else{
                saveInfo()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

        }
        addImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

            resultLauncher.launch(intent)
        }


    }
    fun saveInfo(){
        val SrecipeName = recipeName.text.toString().trim()
        val SrecipeDesc = recipeDescription.text.toString().trim()
        val SaddNote = addNotes.text.toString().trim()
        val Singredients = ingredients.text.toString().trim()
        val addWebsites = webSitesAdd.text.toString().trim()
        val addrecipeType = recipeType.text.toString().trim()
        val addImageUri = getimageUri

        if(SrecipeName.isEmpty() || SrecipeDesc.isEmpty() || SaddNote.isEmpty() || Singredients.isEmpty() || addWebsites.isEmpty() || addrecipeType.isEmpty() || addImageUri.isEmpty()){

            Toast.makeText(this, "Error empty value", Toast.LENGTH_LONG).show();
            return
        }else {
            val data = ref.push().key.toString()
            val infomation = Info(
                data,
                SrecipeName,
                SrecipeDesc,
                SaddNote,
                Singredients,
                addWebsites,
                addrecipeType,
                addImageUri
            )
            ref.child(data).setValue(infomation).addOnCompleteListener {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
            }


        }

    }




}