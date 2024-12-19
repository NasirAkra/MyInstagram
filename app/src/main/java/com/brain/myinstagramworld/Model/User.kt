package com.brain.myinstagramworld.Model

class User {
    var image :String?=null
    var name : String?=null
    var email:String?=null
    var password: String?=null
    constructor()
    constructor(name: String?, image: String?, email: String?, password: String?) {
        this.name = name
        this.image = image
        this.email = email
        this.password = password
    }

    constructor(name: String?, email: String?, password: String?) {
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(password: String?, email: String?) {
        this.password = password
        this.email = email
    }


}