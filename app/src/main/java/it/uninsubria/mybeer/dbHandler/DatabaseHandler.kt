package it.uninsubria.mybeer.dbHandler

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.User

class DatabaseHandler(context: Context,
    private val db: FirebaseDatabase,
): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private lateinit var dbRef: DatabaseReference
        private const val DATABASE_NAME = "beers.db"
        private const val DATABASE_VERSION = 1
        private const val BEER_ID_COLUMN_INDEX = 0
        private const val BEER_STYLE_COLUMN_INDEX = 1
        private const val BEER_HEX_CODES_COLUMN_INDEX = 2
    }

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL("CREATE TABLE beer_categories(" +
                "id VARCHAR(32) PRIMARY KEY," +
                "beer_style TEXT)")
        db.execSQL("CREATE TABLE fav_beer(" +
                "beer_name_hex TEXT PRIMARY KEY," +
                "beer_cat_hex TEXT," +
                "beer_abv TEXT," +
                "beer_brewery TEXT," +
                "beer_desc TEXT," +
                "beer_ibu TEXT," +
                "beer_name TEXT," +
                "beer_picture_link TEXT," +
                "beer_raters TEXT," +
                "beer_style TEXT)")
        db.execSQL("CREATE TABLE user(" +
                "id VARCHAR(32) PRIMARY KEY," +
                "password VARCHAR(32)," +
                "name VARCHAR(50), " +
                "surname VARCHAR(50), " +
                "beer_id TEXT," +
                "FOREIGN KEY(beer_id) references fav_beer(beer_bane_hex))")
        initCategories(db)
        initUser(db)
    }

    private fun initUser(db: SQLiteDatabase){
        var favBeerValues = ContentValues().apply{
            put("beer_name_hex", "19feccc899d54d19cdca36e2d4244163")
            put("beer_cat_hex", "00d926738d9f033e3ac77204a4c6e5a4")
            put("beer_abv", "5.4% ABV")
            put("beer_brewery", "Southern Grist Brewing Company")
            put("beer_desc", "Brown ale brewed with lactose and vanilla beans ")
            put("beer_ibu", "N/A IBU")
            put("beer_name", "Bean There, Brown That")
            put("beer_picture_link", "https://assets.untappd.com/site/beer_logos/beer-1424812_65b25_sm.jpeg")
            put("beer_raters", "5,228 Ratings")
            put("beer_style", "Brown Ale - American")
        }
        db.insert("fav_beer", null,favBeerValues)
        favBeerValues = ContentValues().apply{
            put("beer_name_hex", "1a4992db7d026560510b29a894e1cdfb")
            put("beer_cat_hex", "00d926738d9f033e3ac77204a4c6e5a4")
            put("beer_abv", "5.6% ABV")
            put("beer_brewery", "South Gate Brewing Company")
            put("beer_desc", "English style brown ale made with pecans, chocolate malts and British English East Kent Golding hops. Distinct brown sugar and pecan in the nose and on the palate, reminiscent of pecan pie. ")
            put("beer_ibu", "N/A IBU")
            put("beer_name", "Oaktown Pecan Brown")
            put("beer_picture_link", "https://assets.untappd.com/site/assets/images/temp/badge-beer-default.png")
            put("beer_raters", "1,865 Ratings")
            put("beer_style", "Brown Ale - American")
        }
        db.insert("fav_beer", null, favBeerValues)
        val userValues = ContentValues().apply{
            put("id", "mattialun")
            put("password", "pwd1234")
            put("name", "mattia")
            put("surname", "lunardi")
            put("beer_id", "19feccc899d54d19cdca36e2d4244163")
            put("beer_id", "1a4992db7d026560510b29a894e1cdfb")
        }
        db.insert("user", null, userValues)
    }


    fun getFavBeers(user: User): ArrayList<Beer?>{
        val result: ArrayList<Beer?> = ArrayList()
        val favBeers = user.favBeers
        //println(favBeers)
        favBeers.forEach{
            (name, cat) ->
            val selection = "beer_name_hex = ?"
            val selectionArgs = arrayOf(name)
            val favBeerCursor = readableDatabase.query(
                "fav_beer",
                null,
                selection,
                selectionArgs,
                null,
                null,
                "beer_name_hex ASC"
            )
            while(favBeerCursor.moveToNext()){
                val beerNameHex = favBeerCursor.getString(0)
                val beerCatHex = favBeerCursor.getString(1)
                val beerAbv = favBeerCursor.getString(2)
                val beerBrewery = favBeerCursor.getString(3)
                val beerDesc = favBeerCursor.getString(4)
                val beerIbu = favBeerCursor.getString(5)
                val beerName = favBeerCursor.getString(6)
                val beerPictureLink = favBeerCursor.getString(7)
                val beerRaters = favBeerCursor.getString(8)
                val beerStyle = favBeerCursor.getString(9)
                result.add(Beer(beerName, beerStyle, beerBrewery, beerAbv, beerIbu, beerRaters, beerDesc, beerPictureLink, beerCatHex, beerNameHex))
            }
            favBeerCursor.close()
        }
        return result
    }

    fun addFavBeer(beer: Beer, user: User){
        val beerValues = ContentValues().apply{
            put("beer_name_hex", beer.beer_name_hex)
            put("beer_cat_hex", beer.beer_cat_hex)
            put("beer_abv", beer.beer_abv)
            put("beer_brewery", beer.beer_desc)
            put("beer_ibu", beer.beer_ibu)
            put("beer_name", beer.beer_name)
            put("beer_picture_link", beer.beer_picture_link)
            put("beer_raters", beer.beer_raters)
            put("beer_style", beer.beer_style)
        }
        writableDatabase.insert("fav_beer", null, beerValues)

        val addFavBeerToUser = "update user set beer_id = beer_id + ? where id = ?"
        val arr = arrayOf("$beer.beer_name_hex", "$user.id")
        val queryCursor = writableDatabase.rawQuery(addFavBeerToUser, arr)
        queryCursor.moveToNext()
        queryCursor.close()
    }

    fun getUser(): User {
        val userCursor = readableDatabase.query(
            "user",
            null,
            null,
            null,
            null,
            null,
            "id ASC")
        userCursor.moveToNext()

        val id = userCursor.getString(0)
        val password = userCursor.getString(1)
        val name = userCursor.getString(2)
        val surname = userCursor.getString(3)
        userCursor.close()

        val favBeerCursor = readableDatabase.query(
            "fav_beer",
            null,
            null,
            null,
            null,
            null,
            "beer_name_hex ASC")

        val favBeers: ArrayList<Pair<String?, String?>> = ArrayList()
        while(favBeerCursor.moveToNext()){
            favBeers.add(Pair<String, String>(
                    favBeerCursor.getString(0),
                    favBeerCursor.getString(1))) }
        favBeerCursor.close()

        return User(id, password, name, surname, favBeers)
    }

    private fun initCategories(db: SQLiteDatabase){

        var values = ContentValues().apply{
            put("id", "99cc1a23ef66fe5ae3b30e3f13f7cbed")
            put("beer_style", "Altbier - Sticke")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e43a1a58103aa0983e5ce16d43d28198")
            put("beer_style", "Cider - Traditional / Apfelwein")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6f7177c7d4902f8ffa2bf6ce1e058aa8")
            put("beer_style", "Cider - Basque")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9f555fc96c18adf42c8d8d495586c33b")
            put("beer_style", "Cream Ale - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "482ad4b699b1490a973a006529594665")
            put("beer_style", "Farmhouse Ale - Brett")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "826b82b202b06fa9dd4eceafeee89eab")
            put("beer_style", "Farmhouse Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ac7f0600942f520002564c8185ae18a9")
            put("beer_style", "Lager - American Pre-Prohibition")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "031cbfb392f92e583b9b9cc83db47ed1")
            put("beer_style", "Lager - Polotmav (Czech Amber)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f3403a4aa298c18e8af534d9008e1302")
            put("beer_style", "Lager - Smoked")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "42700589fbc68c11a6d5e6c389f16fbe")
            put("beer_style", "Lager - Svtl (Czech Pale)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2564f05a4cfe25acb291025a0be4da14")
            put("beer_style", "Lager - Tmav (Czech Dark)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ee2c3474803c16cb4f446b56e5b2eab6")
            put("beer_style", "Makgeolli")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6f2c09c14710c8e473e8d636085ff7da")
            put("beer_style", "Pale Ale - Fruited")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1014979fca6e8e06ffda85ed9951442f")
            put("beer_style", "Porter - Smoked")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "15fc5aa95494101521c2036de7b853b5")
            put("beer_style", "Sour - Fruited")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7874a5dec13c70877a82ac32e024ed15")
            put("beer_style", "Sour - Tomato / Vegetable Gose")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6ccf2889c5083b08a3c1bd5987e8a48f")
            put("beer_style", "Wheat Beer - Fruited")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c548e52c42648bc25efce1b68bd6af29")
            put("beer_style", "Altbier - Traditional")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ba4dd7072163519fb968991d80a0ecb0")
            put("beer_style", "Australian Sparkling Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "dc27efecde8c60f455712fc34eb52fb1")
            put("beer_style", "Barleywine - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "55b3480ba779800d031162c6ca7fbb30")
            put("beer_style", "Barleywine - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3319de3e54c91acf9c3e1fb1b378237a")
            put("beer_style", "Barleywine - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "965438172ccc4f8bbd097f5fe16a7719")
            put("beer_style", "Belgian Blonde")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9a5d9aa6db831dd326d67f9b63e4068f")
            put("beer_style", "Belgian Dubbel")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8b79611aa6add56fec815e11be153252")
            put("beer_style", "Belgian Enkel / Patersbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2f264792c7b8fc90ace58d62ac8553f7")
            put("beer_style", "Belgian Quadrupel")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a6d88c2e215051eb04ca5aa19f55c713")
            put("beer_style", "Belgian Strong Dark Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "96297dd20198f4f3afa91b26f0c79a83")
            put("beer_style", "Belgian Strong Golden Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "813f0fd0a349229ae41b42fbdb5ab74f")
            put("beer_style", "Belgian Tripel")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a24a7a60e7eabc228280415089c98c4d")
            put("beer_style", "Bire de Champagne / Bire Brut")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2c5145aa1be1cbd191d48702f52df79a")
            put("beer_style", "Farmhouse Ale - Bire de Coupage")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1cd2379899936b007eb722bfff470c69")
            put("beer_style", "Farmhouse Ale - Bire de Garde")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "36bb097a303e128e3d90863332784ca6")
            put("beer_style", "Farmhouse Ale - Bire de Mars")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "119756ed6409cd7ec51bae08ede860e4")
            put("beer_style", "Bitter - Best")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f91ba8e3bf27d7515b5783ef97753fb4")
            put("beer_style", "Bitter - Extra Special / Strong (ESB)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0a4a25cd3a6cef3c50339693400408d0")
            put("beer_style", "Bitter - Session / Ordinary")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "abe8538a3a5f2d1383f688b2e6f36cf2")
            put("beer_style", "Black & Tan")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a87d686f3500e69a085511b0d96dc381")
            put("beer_style", "Bock - Doppelbock")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1b18675a2dbb88bea3bb6f422cc3bae6")
            put("beer_style", "Bock - Eisbock")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d1bb875f8d95ee868274c13f6fc5ab6f")
            put("beer_style", "Bock - Hell / Maibock / Lentebock")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "39ca7d51f032f24db1cb0f2c428ce4f5")
            put("beer_style", "Bock - Single / Traditional")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ff754ea4090c4c221853a8d91720fc5a")
            put("beer_style", "Bock - Weizenbock")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ef2a07052df36d7a6219830175a76adc")
            put("beer_style", "Bock - Weizendoppelbock")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "689818ecc352e971c805a62bc385afaf")
            put("beer_style", "Brett Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "00d926738d9f033e3ac77204a4c6e5a4")
            put("beer_style", "Brown Ale - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ad869db0819c5e2bbf44543e74dc6d7a")
            put("beer_style", "Brown Ale - Belgian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d6af6005de8dc3bbbe0c9f5d930273d3")
            put("beer_style", "Brown Ale - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1091e6d51d0f2b24d3825f3b4fdebf1f")
            put("beer_style", "Brown Ale - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1c748702bfde70cefed717e4ea062c22")
            put("beer_style", "Brown Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "41d45f7beac3092bd6964ec74cdcbf14")
            put("beer_style", "California Common")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b0791c46a3d070c01410501643e17b02")
            put("beer_style", "Chilli / Chile Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d128c626e62ba2a1a88e307f43de726a")
            put("beer_style", "Cider - Dry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ecc8343c30adfd308d6035eb10c73708")
            put("beer_style", "Cider - Herbed / Spiced / Hopped")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8af2a120d05a599aff3587a0a658d033")
            put("beer_style", "Cider - Ice")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bfb33139008453b4a1906164da83956f")
            put("beer_style", "Cider - Other Fruit")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6c2241bcc7728522e74180ccab8e7f49")
            put("beer_style", "Cider - Perry / Poir")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f6b79377169682ae594b13811b869bce")
            put("beer_style", "Cider - Ros")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "cd78bcd8684a4d9dc55a33e676f9fc46")
            put("beer_style", "Cider - Sweet")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "91b9a2f3cdc39886c34db16299777ad6")
            put("beer_style", "Cider - Traditional / Apfelwein")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d1f2737af56c999bde8d8fe3a589d9f1")
            put("beer_style", "Corn Beer / Chicha de Jora")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "47ca4203a730f75ddfc9deefdf3fb981")
            put("beer_style", "Cream Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "da26414b99a7bece89f647be3dc74fd3")
            put("beer_style", "Dark Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f949ad3fd2dac2402038ce6eefbb9b5c")
            put("beer_style", "Farmhouse Ale - Grisette")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ee3b78c7317c540bff6624c17d8febbd")
            put("beer_style", "Farmhouse Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "17c80f62845f402faf229c82a35a08eb")
            put("beer_style", "Farmhouse Ale - Sahti")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "77c75f43f8687d4cda4c8d1694c296be")
            put("beer_style", "Farmhouse Ale - Saison")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9de24f92c0c3d21e5258c6dfd9b764d9")
            put("beer_style", "Festbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1d08758d088d02b51590bc49f56ce62f")
            put("beer_style", "Flavored Malt Beverage")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "611aece9056296bd91f0f5e0a7e4b012")
            put("beer_style", "Freeze-Distilled Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8b33f0356d2632327ef12c05b802e6fd")
            put("beer_style", "Fruit Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3cf34ff5f560e837ba2d29d869f57b82")
            put("beer_style", "Gluten-Free")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "10da5a573ca3f3cfca26a9dba1f5c4cb")
            put("beer_style", "Blonde / Golden Ale - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ae49ca56ee4b7024d22eca5195c99903")
            put("beer_style", "Blonde / Golden Ale - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "96be606f011d54d554a5e37a71c750ff")
            put("beer_style", "Blonde / Golden Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a06983e8a6a1ec27ffc6f00e2fdd4359")
            put("beer_style", "Golden Ale - Ukrainian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "46c89ac85768fd24c805fce8780d5571")
            put("beer_style", "Cider - Graff")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6adcc9289d24e374c3620e1ad5cde458")
            put("beer_style", "Grape Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e0d128289071a6290aba26eb33aad42e")
            put("beer_style", "Grodziskie / Grtzer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c97e9af027fbd3f236faeb4ec626e3cd")
            put("beer_style", "Happoshu")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "67a7b5a4cd30abf6831e0e6d05f1ffde")
            put("beer_style", "Hard Ginger Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7dc7fe01d8d5d4d1f26409d8eaf7d897")
            put("beer_style", "Hard Kombucha / Jun")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1be19eb9c087e88302365b72369ce453")
            put("beer_style", "Hard Seltzer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "186c925a1a85f0b336d20274c310de1e")
            put("beer_style", "Historical Beer - Adambier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "78cffb593be830907cacd0bf20233723")
            put("beer_style", "Historical Beer - Berliner Braunbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3cc2dd7bacbb8bb65b46045c65e95aa1")
            put("beer_style", "Historical Beer - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7720c24caaf2022cc01e6716ad88b942")
            put("beer_style", "Historical Beer - Burton Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "58d38e3207927d988d65cf7ae4b854b2")
            put("beer_style", "Historical Beer - Dampfbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c9987a8bbe202fb8339f1bd5760dcc28")
            put("beer_style", "Historical Beer - Gruit / Ancient Herbed Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "98faa0a10dbb45a80b4c23964afabb09")
            put("beer_style", "Historical Beer - Kentucky Common")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e895f725da949d8ebf25bdda4a850920")
            put("beer_style", "Historical Beer - Kottbusser")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "36aa2857d76494a9dfd01604522727be")
            put("beer_style", "Historical Beer - Kuit / Kuyt / Koyt")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ee5c38ddc09bf2e994cc193d9bb5f7d7")
            put("beer_style", "Historical Beer - Lichtenhainer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a209549098bdab0c7ba0e46bb2220bff")
            put("beer_style", "Historical Beer - Mumme")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8b4c401b608d5763af80d3ec4ab88976")
            put("beer_style", "Historical Beer - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "84ee5bfd38f622b81229a8ae6d76f031")
            put("beer_style", "Historical Beer - Steinbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "413393a2316c3ef43793147ad7940a96")
            put("beer_style", "Historical Beer - Zoigl")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9080aeae85f16bdb28343e019dc69513")
            put("beer_style", "Honey Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "4b08cdc223edcb0a7228c0694c083680")
            put("beer_style", "IPA - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ef73b06c300e924f2ffc362739b3e479")
            put("beer_style", "IPA - Belgian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "821d8036908009e363a9981590c52fc7")
            put("beer_style", "IPA - Black / Cascadian Dark Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "16ae103c9b77574811598ef6f555ca91")
            put("beer_style", "IPA - Brett")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "54fa2b934e169b86e087df0ca162b3fe")
            put("beer_style", "IPA - Brown")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ccd21c8f2ac69d4d4b71ef24361ff532")
            put("beer_style", "IPA - Brut")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "54258a571b87640a3eec5d8bc9caa85b")
            put("beer_style", "IPA - Cold")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0357e0dec83134c3b1b48ab36c4f3a99")
            put("beer_style", "IPA - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3ad9695996fa03448487dac5f3a87d5a")
            put("beer_style", "Sour - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "cb85086d7bba7de40b2e24073a242bf1")
            put("beer_style", "IPA - Fruited")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "42d1940d2610ecaafb1ce2d8bc57bc79")
            put("beer_style", "IPA - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b7dfc7f30608ef2543b0cdbda271abd3")
            put("beer_style", "IPA - Imperial / Double Black")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "92556b96878cfb60ac95bfe54c952985")
            put("beer_style", "IPA - Imperial / Double Milkshake")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3dd89aaeaa26793aafa6e60ccd69c3c9")
            put("beer_style", "IPA - Imperial / Double New England / Hazy")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "af11db2a48d9377e13ac1c77ce22559d")
            put("beer_style", "IPA - Milkshake")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e8a5e1c8db57fc3247f1d966a15e3412")
            put("beer_style", "IPA - New England / Hazy")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7c19e6f1041e9b49bba6d9c971d87f46")
            put("beer_style", "IPA - New Zealand")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e435b861710b5bfed841f0e0a69fc8a2")
            put("beer_style", "IPA - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "71f61db23b54db36ab4c22f51e487271")
            put("beer_style", "IPA - Quadruple")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "4f3387e4516696926d8bfe978bca4739")
            put("beer_style", "IPA - Red")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f08f1eaacebec13ed5cb2a6adfea026e")
            put("beer_style", "IPA - Rye")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "cb62ce29432ca1ccc8d826828295d893")
            put("beer_style", "IPA - Session")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d3ec526f58bca4dc83d612847a1e74fe")
            put("beer_style", "IPA - Sour")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "52009b497e1356bdf0f067a5a4c8d149")
            put("beer_style", "IPA - Triple")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1683e5ed98d1b71cf824fe30d7b95b0c")
            put("beer_style", "IPA - Triple New England / Hazy")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "719a961d6cd0920dc218a29ed3f556e8")
            put("beer_style", "IPA - White / Wheat")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "5897f4652d9fc5a8208708b5536adb4f")
            put("beer_style", "Grape Ale - Italian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "406b727822e75a72153763350f4305da")
            put("beer_style", "Kellerbier / Zwickelbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "58cb5e425830abbcdece49167b4d5d76")
            put("beer_style", "Klsch")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "944b9f8f25da753501be31ffb531a749")
            put("beer_style", "Koji / Ginjo Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e6bd7df86f4bdaf8befa9cbaedaa5d3d")
            put("beer_style", "Kvass")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ab37f74e297fe7161c0e1d43c869c766")
            put("beer_style", "Lager - Amber / Red")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c47f6bb5a962607121a27376a8bad149")
            put("beer_style", "Lager - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6b0096c913f87aef9d8215df5987846d")
            put("beer_style", "Lager - American Amber / Red")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bd6ca93b9a8d00dd126c8a1cc4d54420")
            put("beer_style", "Lager - American Light")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "fff2e213133a0748f07cec3bc265e343")
            put("beer_style", "Lager - Dark")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0a306b018fa04fb715af73431df59bb9")
            put("beer_style", "Lager - Dortmunder / Export")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b0db433229c87ea9eda568e237f89c25")
            put("beer_style", "Lager - Helles")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1b6e04f501058836bba92e255fbb15fa")
            put("beer_style", "Lager - IPL (India Pale Lager)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "80d32622cc81bb5d114bfb4f70c7cd98")
            put("beer_style", "Lager - Japanese Rice")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f32372dae4507fa1c263685d5a2c153e")
            put("beer_style", "Lager - Leichtbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "722ab834d7cad4c4209d5bfb5a70171f")
            put("beer_style", "Lager - Mexican")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "09e3be6d3273470552b3e9a0bd3a1cb0")
            put("beer_style", "Lager - Munich Dunkel")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b0a9dc7afbb4974d535744160891d1a5")
            put("beer_style", "Lager - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6d00ce3fd7e7363198894ee132c49c1c")
            put("beer_style", "Lager - Pale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2c1ee3dcaca47c18738b3218a13f868b")
            put("beer_style", "Lager - Rotbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3886c2a2311f40ac5cb4ada8b0682451")
            put("beer_style", "Lager - Strong")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b00d8cd69537ac5e0dcaf33bd14bc975")
            put("beer_style", "Lager - Vienna")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "971a71cbfdcf0719849de540fdcc0575")
            put("beer_style", "Lager - Winter")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "76bd1633861b97f2c1101c31e1253c79")
            put("beer_style", "Lambic - Faro")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "02389bfd835853522528109ad0d15d3e")
            put("beer_style", "Lambic - Framboise")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "23c6ba78e75230d54febc921cc1a16b0")
            put("beer_style", "Lambic - Fruit")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "053458c09bd25eeafa1233f92cf11616")
            put("beer_style", "Lambic - Gueuze")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2493271574337c4137eb585f2a2a56a8")
            put("beer_style", "Lambic - Kriek")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f8f81df1b5cbfe5f4d9873788c00296c")
            put("beer_style", "Lambic - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "5651c9155c40d7a0328dcecf32cc1f6c")
            put("beer_style", "Lambic - Traditional")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a60ee4f59dc7ee69718cedba6ebed118")
            put("beer_style", "Mrzen")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b97e4f8c6fc3678dc3b25f6607eac4d2")
            put("beer_style", "Malt Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d18bd0fad4d2f5d084db69cdd796ef28")
            put("beer_style", "Malt Liquor")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c06766d525069ff95dc404e326c09515")
            put("beer_style", "Mead - Acerglyn / Maple Wine")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9afc71d8d4c9cd5557ff4b069b98f171")
            put("beer_style", "Mead - Bochet")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "593f5eadda2fd18b175a9947ee9fbbb8")
            put("beer_style", "Mead - Braggot")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "274427c2f802dd59991bfc467df15459")
            put("beer_style", "Mead - Cyser")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ba5d41d9e30e226439e4c7e34697b2cb")
            put("beer_style", "Mead - Melomel")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ee5e10755dbe78de03f5efc0632fa426")
            put("beer_style", "Mead - Metheglin")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b397bd07a0cdb253f501ae17df8f240f")
            put("beer_style", "Mead - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9184cfbe89fc49fc780c27ccf44203d7")
            put("beer_style", "Mead - Pyment")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ddbbdcc9ae7d3380a5ea4bc4a8d3f255")
            put("beer_style", "Mead - Session / Short")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "519bac784d07c46b46132fa77d88289f")
            put("beer_style", "Mead - Traditional")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "73ae6feb562e7005826f5d3e716e2544")
            put("beer_style", "Mild - Dark")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e47a11adcb36674326a079365a1b9d15")
            put("beer_style", "Mild - Light")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a99c10caba566de8fca0d708e3047877")
            put("beer_style", "Mild - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ae3794ab22979d7cc88cfb641a61796e")
            put("beer_style", "Non-Alcoholic - IPA")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "cb40f66f99a8053d4f384ab0b67d451b")
            put("beer_style", "Non-Alcoholic - Lager")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "838831c9598fce390c8b3e4cdd0948c6")
            put("beer_style", "Non-Alcoholic - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "520463bdec484f63e993e34e76fbfa9f")
            put("beer_style", "Non-Alcoholic - Pale Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "82ea785807caa1423f1df9198c7e2760")
            put("beer_style", "Non-Alcoholic - Porter / Stout")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e9eb6acc0f97032c69f9ee4dd077786a")
            put("beer_style", "Non-Alcoholic - Shandy / Radler")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6d5e04be276bad185dc3d7fa674ca2ad")
            put("beer_style", "Non-Alcoholic - Sour")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2ce3e8b553049fda7d82f2708384b1bf")
            put("beer_style", "Non-Alcoholic - Wheat")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "40869c825972c84e116f34b8f403dda7")
            put("beer_style", "Non-Alcoholic - Cider / Perry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bcf2f330b88e46cbb04742b8842e62f1")
            put("beer_style", "Non-Alcoholic - Mead")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3c482e54e2ef1d6e55b31042df99b020")
            put("beer_style", "Old / Stock Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bff69a17852c2b36db6a0caa918ad3d8")
            put("beer_style", "Pale Ale - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "69274022785f0deab72c79f28917ef04")
            put("beer_style", "Pale Ale - Australian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ca0805e8dc85bc165a59971d23eefb44")
            put("beer_style", "Pale Ale - Belgian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "dbfecb40f77d3ccfc82bef64f44ca47c")
            put("beer_style", "Pale Ale - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f736951954015238b4ec961797391354")
            put("beer_style", "Pale Ale - Milkshake")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d520d23ca4114ad851521bbabc52dd40")
            put("beer_style", "Pale Ale - New England / Hazy")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "062b05a1289b1efd7d0796cd43e8f555")
            put("beer_style", "Pale Ale - New Zealand")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f67ea27f8247094321e51a62b6249818")
            put("beer_style", "Pale Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d73fda7a82ffc83290f1d11ebaf9a08e")
            put("beer_style", "Pale Ale - XPA (Extra Pale)")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0c9523016604bfb1d664791476417ac5")
            put("beer_style", "Pilsner - Czech / Bohemian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "5e2ac43b9bf08a47294b1458b4553591")
            put("beer_style", "Pilsner - German")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0eef44f93776e3bc28e7dced4d30f03d")
            put("beer_style", "Pilsner - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "4aa7752389a23aaf38a40fd862b4780b")
            put("beer_style", "Pilsner - Italian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "4eeafa7a0927c25709cd0d61c52f42f0")
            put("beer_style", "Pilsner - New Zealand")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3c4f795cb4f487fae3e303ace9663a62")
            put("beer_style", "Pilsner - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "1dddb1173218c1ab12f06972a89365f3")
            put("beer_style", "Porter - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bbd192020e78351bdab429c020f8e6bc")
            put("beer_style", "Porter - Baltic")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3f54bfb09ae740e552cafd7d25ae320c")
            put("beer_style", "Porter - Imperial / Double Baltic")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f855ff337cd29611a1d440e6599964f0")
            put("beer_style", "Porter - Coffee")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "419a0cc9bae4a1d6208b4e6ca3eccf5b")
            put("beer_style", "Porter - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0d847ec6638cc39761b8ef5fa5eb1c37")
            put("beer_style", "Porter - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ca6494b8c8845bf61fbbdedaf550a471")
            put("beer_style", "Porter - Imperial / Double Coffee")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a1a5ecd92533bb942bd3d0e97e296ca8")
            put("beer_style", "Porter - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b6fe17cc7b04f89f6e1f2229ea0e5f55")
            put("beer_style", "Pumpkin / Yam Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "21195d77c36bef57e8473a1082c643a7")
            put("beer_style", "Rauchbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8445450fc1faa9fe952513a41596d8b2")
            put("beer_style", "Red Ale - American Amber / Red")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "51097cb2ffee6a9b63e8283f6ea192bb")
            put("beer_style", "Red Ale - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "867241883fb4c4bbf296d70dd8d3e074")
            put("beer_style", "Red Ale - Irish")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8f1c901febd81b8191e01c32a0c4579f")
            put("beer_style", "Red Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "37d7ac8c640c296dc2258a29ecabc6af")
            put("beer_style", "Roggenbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "830aec6cc0ee531b040e5f845a9adffb")
            put("beer_style", "Root Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "edca9055ca0a8a2563e9adc4d56d83b5")
            put("beer_style", "Rye Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "cc6a880da3a66d3290b272ece9aea139")
            put("beer_style", "Rye Wine")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8192daf17f405e880cfdc33d7682d08c")
            put("beer_style", "Schwarzbier")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "32cef201640b7f688ac10f74f45b13ce")
            put("beer_style", "Scotch Ale / Wee Heavy")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f377e6c7d28e7ce36acc6f37c2ca610a")
            put("beer_style", "Scottish Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "e69d28c3f84b70abe4c278284c505d6f")
            put("beer_style", "Scottish Export Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "806dace609a0147ebb733cd6141d45f3")
            put("beer_style", "Shandy / Radler")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "274e366cb933ec4a7d0712908b52e9ae")
            put("beer_style", "Smoked Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b9d5de95a82573da6d5442c799c3fa8a")
            put("beer_style", "Sorghum / Millet Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "da0f80e0082af093d4039547af17b80b")
            put("beer_style", "Sour - Berliner Weisse")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f7628cebdcafd4d31f40839eda7f833d")
            put("beer_style", "Sour - Flanders Oud Bruin")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b073a5590402ed33e66c2ff9263b102c")
            put("beer_style", "Sour - Flanders Red Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "645c147d6bf15313f8c361472b12c1da")
            put("beer_style", "Sour - Fruited")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "583aef42e5e6a1c97f9d8cd9cec0ff7e")
            put("beer_style", "Sour - Fruited Gose")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0c9644d9f1fc5dba5a99b6d471a0c038")
            put("beer_style", "Sour - Traditional Gose")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f7642fd300d0460d8d20357cb0625c53")
            put("beer_style", "Sour - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2cd1a8add2cd9e663c52804eabac7197")
            put("beer_style", "Sour - Other Gose")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "8d231dd41d1fa9fc79d9d4577714c716")
            put("beer_style", "Sour - Smoothie / Pastry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a8870aec609158ed94d33756df8a9713")
            put("beer_style", "Sour - Fruited Berliner Weisse")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7ee74873d291360ef6eb951185a17256")
            put("beer_style", "Specialty Grain")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "4b5f2cf654463dc241b40d5d03a9283a")
            put("beer_style", "Spiced / Herbed Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ec953cad55d5c539b717ff2ad39b349c")
            put("beer_style", "Stout - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "561d849b2af1df7292204a45375df86d")
            put("beer_style", "Stout - Belgian")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "59c9fb8c654ecfc27e6e4dadde3c553b")
            put("beer_style", "Stout - Coffee")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "7cff468fc8e2234318a6bf0ae71849f9")
            put("beer_style", "Stout - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "411714e1d7f9d509e2caf10e4aa76582")
            put("beer_style", "Stout - Foreign / Export")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0314edc3c0c8e3526791ad7bcdd1ab06")
            put("beer_style", "Stout - Imperial / Double")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ae3cde9917399396736683a1441814b4")
            put("beer_style", "Stout - Imperial / Double Milk")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c9b7dc45186d1b638764f03e24941aed")
            put("beer_style", "Stout - Imperial / Double Oatmeal")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "896375aa2da53c76b6c16dfc09da2d7b")
            put("beer_style", "Stout - White / Golden")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "77e321a2f9e3b382ef1ff131a477713c")
            put("beer_style", "Stout - Imperial / Double Pastry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9af515066165aa291269bc65d1ac7d29")
            put("beer_style", "Stout - Imperial / Double Coffee")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "db5e7cf76c2fa304a02b98433d43b15d")
            put("beer_style", "Stout - Irish Dry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "d1600c65ae1a3a4c5575fed6d0271a94")
            put("beer_style", "Stout - Milk / Sweet")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0e309b1713d75a78dcbe204361b64a6d")
            put("beer_style", "Stout - Oatmeal")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "576287be8d46bfdbb80062ac23e83b88")
            put("beer_style", "Stout - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ef5ee10dc2c3647fa5b3a70e6fd6f9c5")
            put("beer_style", "Stout - Oyster")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "3028bceaffd3e32d233626c12b26f2ec")
            put("beer_style", "Stout - Pastry")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "aac3524eca239a76e56e4df36293f2a4")
            put("beer_style", "Stout - Russian Imperial")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a6140f142913b4240ce71b1facbb1688")
            put("beer_style", "Stout - White / Golden")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "ab235cb8361bcee907a2b176ce87a225")
            put("beer_style", "Strong Ale - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "fc891a41110a823a870a63af1824d252")
            put("beer_style", "Strong Ale - English")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "fef8d95b036d63d3217407405483d093")
            put("beer_style", "Strong Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "0c628980f7807ea1f1b41b7db7de4422")
            put("beer_style", "Table Beer")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "c8e10f5d9d0501e2135ea1c57311bf16")
            put("beer_style", "Traditional Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "33a20af7f0b92d2b4c0dcbbe49e9383a")
            put("beer_style", "Wheat Beer - American Pale Wheat")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b0c580bfc1de1f2468ae827c889801cb")
            put("beer_style", "Wheat Beer - Dunkelweizen")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "bcaf9295dabda58b96df32687cbccc40")
            put("beer_style", "Wheat Beer - Hefeweizen")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "2544f5e7fd4ef192bd108398fca7bcf4")
            put("beer_style", "Wheat Beer - Hefeweizen Light / Leicht")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "a3a2fec96393af96d6550ef5dea9714b")
            put("beer_style", "Wheat Beer - Hopfenweisse")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "f3317d1731f5a7f004582b541d0bb4d5")
            put("beer_style", "Wheat Beer - Kristallweizen")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "b46d00a7841017460f9bf9d8138dafc6")
            put("beer_style", "Wheat Beer - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "31296173e424032801df467f469039e0")
            put("beer_style", "Wheat Beer - Witbier / Blanche")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "5cfd74f30fb21ef8151030e3618afca1")
            put("beer_style", "Wheat Beer - Wheat Wine")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "9b9f1ec82d34c78dd2ace4babb3f5673")
            put("beer_style", "Wild Ale - American")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "eabbac3b2b8a1a59f21d58c75825bf51")
            put("beer_style", "Wild Ale - Other")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "6d49f4a9d94ec448f970136a2aa805cb")
            put("beer_style", "Winter Ale")}
        db.insert("beer_categories", null, values)
        values = ContentValues().apply{
            put("id", "710727f7a3f19f64ae75cb2e4ac491f8")
            put("beer_style", "Winter Warmer")}
        db.insert("beer_categories", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS beer_categories")
        db.execSQL("DROP TABLE IF EXISTS fav_beer")
        db.execSQL("DROP TABLE IF EXISTS user")
        onCreate(db)
    }

    fun getAllBeerCategories(): HashMap<String, String>{
        val beerCategories = HashMap<String, String>()
        val cursor = readableDatabase.query(
            "beer_categories",
            null,
            null,
            null,
            null,
            null,
            "id DESC"
        )
        while(cursor.moveToNext()){
            val beerId: String = cursor.getString(BEER_ID_COLUMN_INDEX)
            val beerCategory: String = cursor.getString(BEER_STYLE_COLUMN_INDEX)
            beerCategories[beerId] = beerCategory
        }
        cursor.close()
        return beerCategories
    }

}