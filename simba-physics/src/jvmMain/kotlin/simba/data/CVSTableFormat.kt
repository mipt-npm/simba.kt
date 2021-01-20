package simba.data

import hep.dataforge.io.IOFormat
import hep.dataforge.meta.*
import hep.dataforge.tables.*
import hep.dataforge.values.ValueType
import hep.dataforge.values.toMeta
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.asInputStream
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader
import java.io.StringReader


val Meta.columnsList: List<ColumnDescription>
    get() =
        items.filter { it.key.body == "columns" }
            .map{ it.key.index to (it.value.spec(ColumnDescription) ?: Meta.EMPTY.toScheme(ColumnDescription, {}))}
            .sortedBy { it.first }.map { it.second }


interface TableIOFormat : IOFormat<Table<Any>> {

}


class CSVTableIOFormat(val meta: Meta) : TableIOFormat {
    override fun toMeta() = Meta {
        IOFormat.NAME_KEY put "csv"
        IOFormat.META_KEY put {
            "version" put "0.0.1"
        }
    }

    override fun  readObject(input: Input): Table<Any> {
//        val reader = InputStreamReader(this.asInputStream())
        val text = InputStreamReader(input.asInputStream()).readText()
        val reader = StringReader(text) // FIXME()
        val records = CSVParser(reader, CSVFormat.EXCEL.withFirstRecordAsHeader())


        val columsDescriptions = meta.columnsList

        val dataList = List(columsDescriptions.size) {
            mutableListOf<Any>()
        }.apply {
            val types = columsDescriptions.map { it.type() }
            for (record in records.records) {
                types.forEachIndexed { index, kClass ->
                    val str = record.get(index)
                    val value = when (kClass) {
                        Int::class -> str.toInt()
                        Double::class -> str.toDouble()
                        else -> str
                    }
                    this@apply[index].add(value)
                }
            }
        }


        val columns = columsDescriptions.mapIndexed { indx, colDes ->
            ListColumn(
                name = colDes.name,
                meta = colDes.toMeta(),
                data = dataList[indx],
                type = colDes.type()

            )
        }

        return ColumnTable(columns)
    }

    override fun writeObject(output : Output, obj: Table<Any>) {
        TODO("Not yet implemented")
    }

}