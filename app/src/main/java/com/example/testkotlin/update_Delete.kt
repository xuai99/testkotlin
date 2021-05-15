package com.example.testkotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.intellij.lang.annotations.RegExp


class update_Delete : AppCompatActivity() {
    lateinit var recipeName:EditText
    lateinit var addNotes:EditText
    lateinit var description: EditText
    lateinit var ingredientsU:EditText
    lateinit var webSiteU:EditText
    lateinit var recipeUtype:EditText
    lateinit var myImagetoUpdate:ImageView
    lateinit var update:TextView
    lateinit var delete: TextView
    lateinit var website:TextView
    lateinit var updateImage:TextView
    lateinit var recipeID:String
    lateinit var stringImageID:String
    lateinit var myImage:Uri
    lateinit var imageGet:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete)

        recipeName= findViewById(R.id.recipeNameU)
        addNotes = findViewById(R.id.AddNotesInfoU)
        description = findViewById(R.id.descriptionU)
        ingredientsU = findViewById(R.id.ingredientsU)
        webSiteU = findViewById(R.id.websiteAddressU)
        recipeUtype = findViewById(R.id.recipeTypeUpdate)
        update = findViewById(R.id.updateButton)
        delete = findViewById(R.id.DeleteButton)
        website = findViewById(R.id.websitebutton)
        updateImage = findViewById(R.id.UpdateImageType)
        myImagetoUpdate = findViewById(R.id.myRecipeImageUpdate)

        recipeID = intent.getStringExtra("ID").toString()
        val myRecipe: String? = intent.getStringExtra("RecipeName").toString()
        val addnotes: String? = intent.getStringExtra("addNotes").toString()
        val descriptionU:String? = intent.getStringExtra("Decription").toString()
        val ingredients:String? = intent.getStringExtra("ingredients").toString()
        val websiteName:String? = intent.getStringExtra("WebsitesA").toString()
        val recipeTy:String? =  intent.getStringExtra("recipeType").toString()
         imageGet =  intent.getStringExtra("myImageUri").toString()


        recipeName.setText(myRecipe.toString())
        addNotes.setText(addnotes.toString())
        description.setText(descriptionU.toString())
        ingredientsU.setText(ingredients.toString())
        webSiteU.setText(websiteName.toString())
        recipeUtype.setText(recipeTy.toString())

        Toast.makeText(applicationContext,imageGet,Toast.LENGTH_SHORT).show()

        myImage= imageGet?.toUri()!!
        Picasso.get().load(myImage).into(myImagetoUpdate)





        update.setOnClickListener{
            updateData()
        }
        delete.setOnClickListener{
            deleteData()
        }
        website.setOnClickListener{
            val url = websiteName.toString()

                if(isValidUrl(url).equals(true)){
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext,"wrong website url, update with a new one",Toast.LENGTH_SHORT).show()
                }


        }
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                myImage = data?.data!!
                Picasso.get().load(myImage).into(myImagetoUpdate)
                Toast.makeText(applicationContext,myImage.toString(),Toast.LENGTH_SHORT).show()

                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://testkotlin-26bd0.appspot.com")
                val uploadS = storageRef.child(myImage.toString()+".jpg")
                var uploadTask = uploadS.putFile(myImage)
                uploadTask.addOnFailureListener {
                    val imageUrl = it.toString()
                    Toast.makeText(applicationContext,imageUrl,Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    uploadS.downloadUrl.addOnSuccessListener {

                        imageGet = it.toString()
                        Toast.makeText(applicationContext,"upload complete",Toast.LENGTH_SHORT).show()

                    }

                }
            }
        }
        updateImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }
    }
    private fun updateData(){

        val dbhero = FirebaseDatabase.getInstance().getReference("Recipe")
        val  recipeUname = recipeName.text.toString().trim()
        val additonalNotesU = addNotes.text.toString().trim()
        val descriptionU = description.text.toString().trim()
        val ingredientUs = ingredientsU.text.toString().trim()
        val updateWebsiteName = webSiteU.text.toString().trim()
        val recipeType = recipeUtype.text.toString().trim()



        if(recipeUname.isEmpty() || additonalNotesU.isEmpty() || descriptionU.isEmpty() || ingredientUs.isEmpty() || updateWebsiteName.isEmpty() || recipeType.isEmpty()){

            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            return
        }
        val recipe = Info(recipeID,recipeUname,descriptionU,additonalNotesU,ingredientUs,updateWebsiteName,recipeType,imageGet)
        recipe.id?.let { dbhero.child(it).setValue(recipe) }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun deleteData(){

        val dbDeletehero = recipeID?.let { FirebaseDatabase.getInstance().getReference("Recipe").child(recipeID) }
        if (dbDeletehero != null) {
            dbDeletehero.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0!!.exists()){
                        for(h in p0.children){
                            h.ref.removeValue()
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun isValidUrl(url: String): Boolean {

        val regex ="((?:https?:)?(?:\\/?\\/))((?:[\\d\\w]|%[a-fA-f\\d]{2,2})+(?::(?:[\\d\\w]|%[a-fA-f\\d]{2,2})+)?@)?((?:[\\d\\w][-\\d\\w]{0,253}[\\d\\w]\\.)+[\\w]{2,63})(:[\\d]+)?(\\/(?:[-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)*(\\?(?:&?(?:[-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})=?)*)?(#(?:[-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)?".toRegex()

        return regex.containsMatchIn(url)
    }

}


