package it.uninsubria.mybeer.dbHandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import it.uninsubria.mybeer.datamodel.Beer
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class DatabaseHandler(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {

        private const val DATABASE_NAME = "beer.db"
        private const val DATABASE_VERSION = 1
    }

    private val STRING_LENGTH = 32
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private fun genPrimaryKey() =
        ThreadLocalRandom.current()
            .ints(STRING_LENGTH.toLong(), 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table beers(" +
                "beer_id varchar(50) primary key," +
                "beer_name varchar(100) not null, " +
                "beer_style varchar(100) not null," +
                "beer_brewery varchar(100)," +
                "beer_abv float not null," +
                "beer_ibu float not null, " +
                "beer_raters integer," +
                "beer_desc text," +
                "beer_picture_link);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists beer")
        onCreate(db)
    }

    fun insertBeer(beer: Beer){
        val values = ContentValues().apply{
            put("beer_id", genPrimaryKey())
            put("beer_name", beer.beerName)
            put("beer_style", beer.beerStyle)
            put("beer_brewery", beer.beerBrewery)
            put("beer_abv", beer.beerAbv)
            put("beer_ibu", beer.beerIbu)
            put("beer_raters", beer.beerRaters)
            put("beer_desc", beer.beerDesc)
            put("beer_picture_link", beer.beerPictureLink)
        }
        writableDatabase.insert("beers", null, values)
    }

    fun updateBeer(beer: Beer){
        TODO("Not yet implemented")
    }

    fun deleteBeer(beer: Beer){
        TODO("Not yet implemented")
    }




}