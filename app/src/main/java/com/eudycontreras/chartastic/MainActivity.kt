package com.eudycontreras.chartastic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eudycontreras.chartasticlibrary.charts.data.ChartData
import com.eudycontreras.chartasticlibrary.charts.data.DataTable
import com.eudycontreras.chartasticlibrary.charts.data.DataTableMatrix
import com.eudycontreras.chartasticlibrary.charts.data.MatrixProperties
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = Pair<MatrixProperties, DataTableMatrix>(
            arrayOf(
                Triple("Year", Int, 2015)
            ),
            arrayOf(
                arrayOf(String, Int, Int, Int, Int, Int, Double, Boolean),
                arrayOf("Coder", "LOC", "Repos Owned", "Projects", "Age", "Contributions", "Hourly Rate", "Married"),
                createRecord("Emil"),
                createRecord("Jake"),
                createRecord("Zara"),
                createRecord("Mike"),
                createRecord("Eddie"),
                createRecord("Liza"),
                createRecord("Mark"),
                createRecord("John"),
                createRecord("Carlos"),
                createRecord("Anna"),
                createRecord("Maria")
            )
        )

        val dataTable = DataTable.parseWith(data)

        val chartData = ChartData(dataTable, "Coder", "LOC")

        Log.d("","")
    }

    private fun createRecord(name: String): Array<String> {
        val loc = (1200..80000).random().toString()
        val reposOwned = (12..45).random().toString()
        val projects = (4..12).random().toString()
        val age = (24..50).random().toString()
        val contributions = (230..530).random().toString()
        val hourlyRate = (120..240).random().toString()
        val married = Random.nextBoolean().toString()
        return arrayOf(name, loc, reposOwned, projects, age, contributions, hourlyRate, married)
    }
}
