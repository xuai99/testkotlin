package com.example.testkotlin

import android.R.attr.*
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


@Suppress("DEPRECATION")
class myAdapter(private val mCtx:Context, private  val  userlist: ArrayList<Info>, private val listerner:OnitemClicklistener): RecyclerView.Adapter<myAdapter.myViewHolder>() {
    lateinit var recipeName:String
    lateinit var addNote:String
    lateinit var imageV:String
    //lateinit var decription:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)


        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val CurrentItem = userlist[position]
        holder.RecipeName.text = CurrentItem.recipename
        holder.addNotes.text = CurrentItem.addNotes
        //holder.directionS.text = CurrentItem.recipeDescripton
        imageV = CurrentItem.imageUri.toString()
        var myUri:Uri = imageV.toUri()
        Picasso.get().load(myUri).into(holder.imageView)

    }


    override fun getItemCount(): Int {

        return userlist.size
    }
   inner class myViewHolder(itemView : View): RecyclerView.ViewHolder(itemView),View.OnClickListener{


        val RecipeName: TextView = itemView.findViewById(R.id.recipeNameS)
        val addNotes: TextView = itemView.findViewById(R.id.AddNotesInfoS)
       //val directionS:TextView = itemView.findViewById(R.id.DirectionShow)
       val imageView:ImageView = itemView.findViewById(R.id.displayImage)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listerner.onItemClick(position)
            }
        }
    }
    interface OnitemClicklistener{
        fun onItemClick(position: Int)
    }

}

