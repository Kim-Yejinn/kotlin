package com.example.realmtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realmtest.model.Dog
import com.example.realmtest.model.Person
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use the RealmConfiguration.Builder() for more options
        val configuration = RealmConfiguration.create(schema = setOf(Person::class, Dog::class))
        val realm = Realm.open(configuration)

        // plain old kotlin object
        val person = Person().apply {
            name = "Carlo"
            dog = Dog().apply { name = "Fido"; age = 16 }
        }

        // Persist it in a transaction
        realm.writeBlocking { // this : MutableRealm
            val managedPerson = copyToRealm(person)
        }
    }
}