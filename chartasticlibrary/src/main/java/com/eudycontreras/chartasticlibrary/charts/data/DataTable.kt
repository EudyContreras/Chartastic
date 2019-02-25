package com.eudycontreras.chartasticlibrary.charts.data

/**
 * Created by eudycontreras.
 */

class DataTable(var name: String? = "DataTable") {

    private val dataChangeListeners = ArrayList<DataChangeListener>()

    private val attributes = HashMap<DataTableAttribute, DataTableValue>()

    private val columns = HashMap<String,DataTableAttribute>()

    private val records = ArrayList<DataTableRow>()     // Make into linkedHashMap

    fun addDataChangeListeners(changeListener: DataChangeListener) {
        dataChangeListeners.add(changeListener)
    }

    fun removeDataChangeListener(changeListener: DataChangeListener) {
        dataChangeListeners.remove(changeListener)
    }
    
    fun getAttribute(attribute: DataTableAttribute): DataTableValue? {
        return attributes[attribute]
    }

    fun getAttribute(attribute: String?): DataTableValue? {
       return attribute?.let {
           return attributes.filter { it.key.name.contentEquals(attribute) }.map { it.value }.first()
       }
    }

    fun addAttribute(vararg attribute: Pair<DataTableAttribute, DataTableValue>) {
        for(att in attribute){
            attributes[att.first] = att.second
        }
    }

    fun removeAttribute(vararg attribute: DataTableAttribute) {
        for(att in attribute){
            attributes.remove(att)
        }
    }

    fun addColumn(vararg column: DataTableAttribute) {
        columns.putAll(column.map { Pair(it.name, it) })
    }

    fun addColumn(column: List<DataTableAttribute>) {
        columns.putAll(column.map { Pair(it.name, it) })
    }

    fun removeColumn(vararg column: DataTableAttribute) {
        for (col in column) {
            columns.remove(col.name)
        }
        for(col in column) {
            for(record in records) {
                record.recordValues.removeAll { it.attribute.contentEquals(col.name)}
            }
        }
    }

    fun removeColumn(vararg column: String) {
        for (col in column) {
            columns.remove(col)
        }
        for(col in column) {
            for(record in records) {
                record.recordValues.removeAll { it.attribute.contentEquals(col)}
            }
        }
    }

    fun addRecord(vararg record: DataTableRow) {
        records.addAll(record)
    }

    fun addRecord(record: List<DataTableRow>) {
        records.addAll(record)
    }

    fun updateRecord(key: Int, value: DataTableValue) {

    }

    fun removeRecord(vararg record: DataTableRow): Boolean {
        return records.removeAll(record)
    }

    fun removeRecord(predicate: (DataTableRow)-> Boolean): Boolean {
        return records.removeAll(predicate)
    }

    fun getValuesForColumn(column: DataTableAttribute): List<DataTableValue> {
        return getValuesForColumn(column.name)
    }

    fun getTypeForColumn(column: String): Any {
        if (columns.containsKey(column)) {
            return columns[column]?.type!!
        }
        throw Exception("Column does not exist!")
    }

    fun getValuesForColumn(column: String): List<DataTableValue> {
        val values = records.flatMap { it.recordValues.filter { rec -> rec.attribute.contentEquals(column) } }

        return values
    }

    fun getRecords(): List<DataTableRow> {
        return records
    }

    fun getRecords(predicate: (DataTableRow) -> Boolean): List<DataTableRow> {
        return records.filter(predicate)
    }

    fun getColumns(): Map<String, DataTableAttribute> {
        return columns
    }

    companion object {

        private const val TYPE_INDEX = 0
        private const val COL_INDEX = 1
        private const val ROW_INDEX = 2

        fun <T> parseWith(handler: ((T) -> DataTable)? = null): DataTableParser<T> = object : DataTableParser<T> {
            override fun create(from: T) = handler?.invoke(from)
        }

        fun parseWith(data: Pair<MatrixProperties, DataTableMatrix>, name: String? = null): DataTable {
            val dataTable = parseWith(data.second, name)
            data.first.forEach {
                val attribute = Pair(DataTableAttribute(it.first, it.second), DataTableValue(it.first, it.third.toString()))
                dataTable.addAttribute(attribute)
            }
            return dataTable
        }

        fun parseWith(matrix: DataTableMatrix, name: String? = null): DataTable {
            val dataTable = if (name == null) DataTable() else DataTable(name)

            val columns = matrix[COL_INDEX].mapIndexed { index, col ->
               DataTableAttribute(col.toString(),matrix[TYPE_INDEX][index])
            }

            val records = ArrayList<DataTableRow>()

            for(i in ROW_INDEX until matrix.size) {
                val row = matrix[i]
                val record = DataTableRow()
                for((index, rec) in row.withIndex()) {
                    record.recordValues.add(
                        DataTableValue(columns[index].name, rec.toString())
                    )
                }
                records.add(record)
            }

            dataTable.addColumn(columns)
            dataTable.addRecord(records)

            return dataTable
        }
    }
}