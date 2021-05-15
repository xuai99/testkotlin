package com.example.testkotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class MainActivity : AppCompatActivity(), myAdapter.OnitemClicklistener {
    lateinit var floatingActionButton: FloatingActionButton;
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Info>
    private lateinit var currentSelect:String
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recipeName =  resources.getStringArray(R.array.RecipeType)

        floatingActionButton = findViewById(R.id.addItems)
        userRecyclerView = findViewById(R.id.myInformationRecycleView)
        spinner = findViewById(R.id.spinner)

        userRecyclerView.layoutManager =LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Info>()

        floatingActionButton.setOnClickListener{

            val intent = Intent(this,addItem::class.java)
            startActivity(intent)
        }

        if(spinner != null){
            val  adapter = ArrayAdapter(this,R.layout.spinner_item,recipeName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter


            spinner.onItemSelectedListener = object: AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    currentSelect = recipeName[p2]

                    if(currentSelect.equals("all")){
                        getAllData()

                    }else{
                        Toast.makeText(this@MainActivity, getString(R.string.selected_item) + " " +
                                "" + currentSelect, Toast.LENGTH_SHORT).show()
                        getData()
                    }


                }



                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    TODO("Not yet implemented")
                }
            }
        }





    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, userArrayList[position].recipename.toString(),Toast.LENGTH_SHORT).show()
        val intent = Intent(this, update_Delete::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("ID",userArrayList[position].id.toString())
        intent.putExtra("RecipeName", userArrayList[position].recipename.toString())
        intent.putExtra("addNotes", userArrayList[position].addNotes.toString())
        intent.putExtra("ingredients",userArrayList[position].ingredients.toString())
        intent.putExtra("Decription",userArrayList[position].recipeDescripton.toString())
        intent.putExtra("WebsitesA",userArrayList[position].websiteName.toString())
        intent.putExtra("recipeType",userArrayList[position].recipeType.toString())
        intent.putExtra("myImageUri",userArrayList[position].imageUri.toString())
        startActivity(intent)


    }

    private fun getData(){
        dbref = FirebaseDatabase.getInstance().getReference("Recipe")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userArrayList.clear()
                    for(userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(Info::class.java)
                        if (user != null) {
                            if(currentSelect.equals(user.recipeType.toString()))
                                userArrayList.add(user!!)
                        }
                    }
                    userRecyclerView.adapter = myAdapter(applicationContext,userArrayList,this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getAllData(){
        dbref = FirebaseDatabase.getInstance().getReference("Recipe")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userArrayList.clear()
                    for(userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(Info::class.java)
                        if (user != null) {
                                userArrayList.add(user!!)
                        }
                    }
                    userRecyclerView.adapter = myAdapter(applicationContext,userArrayList,this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}


