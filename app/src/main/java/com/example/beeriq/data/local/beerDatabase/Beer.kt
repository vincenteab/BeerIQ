package com.example.beeriq.data.local.beerDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "beer_table")
class Beer (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "style")
    var style: String = "",

    @ColumnInfo(name = "brewery")
    var brewery: String = "",

    @ColumnInfo(name = "beer_full_name")
    var beerFullName: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "abv")
    var abv: Double = 0.0,

    @ColumnInfo(name = "min_ibu")
    var minIBU: Int = 0,

    @ColumnInfo(name = "max_ibu")
    var maxIBU: Int = 0,

    @ColumnInfo(name = "astringency")
    var astringency: Int = 0,

    @ColumnInfo(name = "body")
    var body: Int = 0,

    @ColumnInfo(name = "alcohol")
    var alcohol: Int = 0,

    @ColumnInfo(name = "bitter")
    var bitter: Int = 0,

    @ColumnInfo(name = "sweet")
    var sweet: Int = 0,

    @ColumnInfo(name = "sour")
    var sour: Int = 0,

    @ColumnInfo(name = "salty")
    var salty: Int = 0,

    @ColumnInfo(name = "fruits")
    var fruits: Int = 0,

    @ColumnInfo(name = "hoppy")
    var hoppy: Int = 0,

    @ColumnInfo(name = "spices")
    var spices: Int = 0,

    @ColumnInfo(name = "malty")
    var malty: Int = 0,

    @ColumnInfo(name = "review_aroma")
    var reviewAroma: Double = 0.0,

    @ColumnInfo(name = "review_appearance")
    var reviewAppearance: Double = 0.0,

    @ColumnInfo(name = "review_palate")
    var reviewPalate: Double = 0.0,

    @ColumnInfo(name = "review_taste")
    var reviewTaste: Double = 0.0,

    @ColumnInfo(name = "review_overall")
    var reviewOverall: Double = 0.0,

    @ColumnInfo(name = "num_of_reviews")
    var numOfReviews: Int = 0,
) : Serializable