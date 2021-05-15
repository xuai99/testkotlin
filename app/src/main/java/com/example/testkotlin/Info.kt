package com.example.testkotlin

class Info(var id:String?=" " ,
           var recipename:String?=" ",
           var recipeDescripton:String?=" ",
           var addNotes:String?=" ",
           var ingredients:String?="",
           var websiteName:String?="",
           var recipeType:String?="",
           var imageUri:String?="") {
    constructor():this("","",""){

    }
}