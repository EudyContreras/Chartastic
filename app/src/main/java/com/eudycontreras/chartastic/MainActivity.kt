package com.eudycontreras.chartastic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartItem
import com.eudycontreras.chartasticlibrary.charts.data.DataTable
import com.eudycontreras.chartasticlibrary.charts.data.DataTableMatrix
import com.eudycontreras.chartasticlibrary.charts.data.MatrixProperties
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.LightSource
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val color = MutableColor(ContextCompat.getColor(this, R.color.colorAccent))

        val coders = arrayOf(
            Coder("Emil", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Jake", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Zara", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Mike", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Liza", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Maria", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Jose", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Lena", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Liza", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Maria", (0..10_001).random(), color.adjust(1.2f)),
            Coder("Jose", (0..10_001).random(), color.adjust(1.2f))

        )

        val data = Pair<MatrixProperties, DataTableMatrix>(
            arrayOf(
                Triple("Year", Int, 2015)
            ),
            arrayOf(
                arrayOf(String, Int, Int, Int, Int, Int, Double, Boolean),
                arrayOf("Coder", "LOC", "Repos Owned", "Projects", "Age", "Contributions", "Hourly Rate", "Married"),
                createRecord(coders[0]),
                createRecord(coders[1]),
                createRecord(coders[2]),
                createRecord(coders[3]),
                createRecord(coders[4]),
                createRecord(coders[5]),
                createRecord(coders[6]),
                createRecord(coders[7]),
                createRecord(coders[8]),
                createRecord(coders[9]),
                createRecord(coders[10]),
                createRecord(coders[11]),
                createRecord(coders[12])
            )
        )

        val dataTable = DataTable.parseWith(data)

        val chartData = BarChartData(dataTable, "Coder", "LOC")

        for (coder in coders) {
            val item = BarChartItem<Coder>(coder.name, coder.loc)
            item.elevation = 0.dp
            item.color = color
            item.roundedTop = true
            item.roundedBottom = false
            item.elevationShadowColor = MutableColor.fromColor(color)
            item.elevationShadowPosition = LightSource.Position.TOP_LEFT_RIGHT
            item.cornerRadius = BarChartItem.DEFAULT_ROUND_RADIUS

            item.backgroundOptions.padding = 0.dp
            item.backgroundOptions.color = MutableColor.rgba(90, 97, 98, 0.2f)
            item.backgroundOptions.showBackground = true

            chartData.addBarChartItem(item)
        }

        val chart = BarChart(this, chartData)

        chartView.setChart(chart)

        Log.d("","")
    }

    private fun createRecord(coder: Coder): Array<String> {
        val reposOwned = (12..45).random().toString()
        val projects = (4..12).random().toString()
        val age = (24..50).random().toString()
        val contributions = (230..530).random().toString()
        val hourlyRate = (120..240).random().toString()
        val married = Random.nextBoolean().toString()

        return arrayOf(coder.name, coder.loc.toString(), reposOwned, projects, age, contributions, hourlyRate, married)
    }
}
