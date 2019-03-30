package com.eudycontreras.chartastic

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.charts.chart_animation.BarAnimation
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTable
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataType
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartItem
import com.eudycontreras.chartasticlibrary.properties.Gradient
import com.eudycontreras.chartasticlibrary.properties.LightSource
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.DataTableMatrix
import com.eudycontreras.chartasticlibrary.utilities.global.MatrixProperties
import com.eudycontreras.chartasticlibrary.views.RectangleView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createChart(chartView1, "Chart One")
        /*createChart(chartView2, "Chart Two")
        createChart(chartView3, "Chart Three")
        createChart(chartView4, "Chart Four")
        createChart(chartView5, "Chart Five")
        createChart(chartView6, "Chart Six")*/

        Log.d("","")
    }

    private fun createChart(view: RectangleView, name: String) {

        val color = MutableColor(ContextCompat.getColor(this, R.color.colorAccent))

        val coders = arrayOf(
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f)),
            Coder("Carlos", (-1001..1001).random(), color.adjust(1.2f))

        )

        val data = Pair<MatrixProperties, DataTableMatrix>(
            arrayOf(
                Triple("Year", DataType.NUMERIC_WHOLE, 2015)
            ),
            arrayOf(
                arrayOf(DataType.ALPHABETIC, DataType.NUMERIC_WHOLE, DataType.NUMERIC_WHOLE, DataType.NUMERIC_WHOLE, DataType.NUMERIC_WHOLE, DataType.NUMERIC_WHOLE, DataType.NUMERIC_DECIMAL, DataType.BINARY),
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
                createRecord(coders[13])
            )
        )

        val dataTable = DataTable.parseWith(data)

        val chartData = BarChartData(dataTable, "Coder", "LOC")
        chartData.zeroPoint = 0

        coders.sortBy { it.loc }

        val range = 0 until ((dataTable.getRecords().size * 1f).toInt()..dataTable.getRecords().size).random()

        for (i in 0 until  dataTable.getRecords().size) {
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
            item.backgroundOptions.padding = 0.dp
            item.backgroundOptions.color = MutableColor.rgba(90, 96, 98, 0.1f)
            item.backgroundOptions.showBackground = true
            item.gradient = Gradient(arrayOf(
                MutableColor.fromColor(color),
                MutableColor.fromColor(color).subtractGreen(95).subtractBlue(95),
                MutableColor.fromColor(color)
            ), Gradient.LEFT_TO_RIGHT)

            chartData.addBarChartItem(item)
        }

        val chart = BarChart(this, chartData)

        chart.acrossGradient = Gradient(arrayOf(
            MutableColor(255, 40, 100, 190),
            MutableColor(255, 40, 190, 100),
            MutableColor(255, 190, 190, 30),
            MutableColor(255, 190, 40, 40)
        ), Gradient.LEFT_TO_RIGHT)

        chart.barRevealAnimation = BarAnimation().apply {
            delay = 200
            duration = 450
            stagger = 40
            sequential = true
            type = BarAnimation.AnimationType.LEFT_TO_RIGHT
            interpolator = OvershootInterpolator()
            onEnd = {
                Log.d(name, "Animation done")
            }
        }

        view.setChart(chart)
        view.observeVisibility()
    }

    private fun createRecord(coder: Coder): Array<Any> {
        val reposOwned = (12..45).random()
        val projects = (4..12).random()
        val age = (24..50).random()
        val contributions = (230..530).random()
        val hourlyRate = (120..240).random()
        val married = Random.nextBoolean()

        return arrayOf(coder.name, coder.loc, reposOwned, projects, age, contributions, hourlyRate, married)
    }
}
