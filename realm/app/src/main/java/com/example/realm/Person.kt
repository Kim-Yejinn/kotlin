package com.example.realm

import io.realm.kotlin.types.RealmObject

class Person: RealmObject {
    var name: String = "Foo"
    var dog:Dog ?= null
}