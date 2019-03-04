package com.eudycontreras.chartastic

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.charts.chartAnimation.BarAnimation
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartItem
import com.eudycontreras.chartasticlibrary.charts.data.DataTable
import com.eudycontreras.chartasticlibrary.charts.data.DataTableMatrix
import com.eudycontreras.chartasticlibrary.charts.data.MatrixProperties
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.Gradient
import com.eudycontreras.chartasticlibrary.properties.LightSource
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.views.RectangleView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        createChart(chartView1, "Chart One")
        createChart(chartView2, "Chart Two")
        createChart(chartView3, "Chart Three")
        createChart(chartView4, "Chart Four")
        createChart(chartView5, "Chart Five")
        createChart(chartView6, "Chart Six")

        Log.d("","")
    }

    private fun createChart(view: RectangleView, name: String) {

        val color = MutableColor(ContextCompat.getColor(this, R.color.colorAccent))

        val coders = arrayOf(
            Coder("Emil", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Jake", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Zara", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Mike", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Liza", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Maria", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Jose", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Lena", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Liza", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),

            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f)),
            Coder("Carlos", (1000..10_001).random(), color.adjust(1.2f))

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
                createRecord(coders[12]),
                createRecord(coders[13]),
                createRecord(coders[14]),
                createRecord(coders[15]),
                createRecord(coders[16]),
                createRecord(coders[17]),
                createRecord(coders[18]),
                createRecord(coders[19]),
                createRecord(coders[20]),
                createRecord(coders[21]),
                createRecord(coders[22]),
                createRecord(coders[23]),
                createRecord(coders[24]),
                createRecord(coders[25]),
                createRecord(coders[26]),
                createRecord(coders[27]),
                createRecord(coders[28]),
                createRecord(coders[29])
            )
        )

        val dataTable = DataTable.parseWith(data)

        val chartData = BarChartData(dataTable, "Coder", "LOC")

        val range = ((coders.size * 0.7).toInt()..coders.size).random()

        for (i in 0 until range) {
            val coder = coders[i]
            val item = BarChartItem(coder.name, coder.loc, coder)
            item.elevation = 0.dp
            item.color = color
            item.roundedTop = true
            item.roundedBottom = false
            item.elevationShadowColor = MutableColor.fromColor(color)
            item.elevationShadowPosition = LightSource.Position.TOP_LEFT_RIGHT
            item.activeColor = MutableColor.fromColor(color).subtractGreen(45).subtractBlue(45)
            item.hoverColor = MutableColor.fromColor(color).addGreen(55).addBlue(55)
            item.cornerRadius = BarChartItem.DEFAULT_ROUND_RADIUS
            if (false == true){
                item.gradient = Gradient(arrayOf(
                    MutableColor.fromColor(color),
                    MutableColor.fromColor(color).subtractGreen(95).subtractBlue(95),
                    MutableColor.fromColor(color)
                ), Gradient.LEFT_TO_RIGHT)
            }
            item.backgroundOptions.padding = 0.dp
            item.backgroundOptions.color = MutableColor.rgba(90, 96, 98, 0.15f)
            item.backgroundOptions.showBackground = true

            chartData.addBarChartItem(item)
        }

        val chart = BarChart(this, chartData)

        chart.acrossGradient = Gradient(arrayOf(
            MutableColor(255, 40, 100, 190),
            MutableColor(255, 40, 190, 100),
            MutableColor(255, 190, 190, 30),
            MutableColor(255, 190, 40, 40),
            MutableColor(255, 190, 190, 30),
            MutableColor(255, 40, 190, 100),
            MutableColor(255, 40, 100, 190)
        ), Gradient.LEFT_TO_RIGHT)

        chart.barRevealAnimation = BarAnimation().apply {
            delay = 100
            duration = 350
            stagger = 80
            sequential = true
            type = BarAnimation.AnimationType.EDGE_TO_CENTER
            interpolator = OvershootInterpolator()
            onEnd = {
                Log.d(name, "Animation done")
            }
        }

        view.setChart(chart)
        view.observeVisibility()
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
