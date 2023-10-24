package com.example.myapplication

class Ingredient {
    var mac: String? = null
    var name: String? = null
    var longitude = 0.0
    var latitude = 0.0
    var date: String? = null
    var indice: String? = null
    var distance = 0.0
    var index = 1

    constructor() {}
    constructor(name: String?, mac: String?) {
        this.name = name
        this.mac = mac ///// S G K /////
    }

    constructor(name: String?, mac: String?, indice: String?) {
        this.name = name
        this.mac = mac ///// S G K /////
        this.indice = indice
    }

    constructor(name: String?, mac: String?, index: Int) {
        this.name = name
        this.mac = mac ///// S G K /////
        this.index = index
    }

}