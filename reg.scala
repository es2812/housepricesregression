/*
 *      Proyecto de Regresión Lineal sobre el precio de venta de casas con el dataset
 *      AmesHousing de Kaggle.
 *
 *      @author: Esther Cuervo Fernández
 *      @date: 7-Mayo-2019
 *
 */

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.udf

val PATH = "data/AmesHousing.csv"

/*                      
 *                      LECTURA DE DATOS
 *
 */

//Leemos los datos directamente a un DataFrame, inferSchema funciona bastante bien en este caso
val df = spark.read.format("csv").option("header","true").option("inferSchema","true").load(PATH)

/*
 *                    SELECCIÓN DE ATRIBUTOS
 *
 */

def selectAttributes(df:DataFrame, attrs:Array[String]): DataFrame = {
    for(a<-attrs){
      if(!(df.columns contains a)){
        print(s"El DataFrame no contiene el atributo ${a}\n")
      }
    }

    val drops = df.columns.diff(attrs)
    var newdf = df
    for(d<-drops){
        newdf = newdf.drop(d)   
    }
    newdf
}


val NUMATTR = Array("1st Flr SF", "2nd Flr SF", "Bedroom AbvGr", "Bsmt Full Bath", "Bsmt Unf SF", "BsmtFin SF 1", "Fireplaces", "Full Bath", "Garage Area", "Garage Cars", "Garage Yr Blt", "Gr Liv Area", "Half Bath", "Lot Frontage", "Mas Vnr Area", "Open Porch SF", "Overall Cond", "Overall Qual", "Total Bsmt SF", "TotRms AbvGrd", "Wood Deck SF", "Year Built", "Year Remod/Add")
val CATATTR = Array("Bsmt Cond", "Bsmt Exposure", "Bsmt Qual", "BsmtFin Type 1", "Central Air", "Electrical", "Exter Qual", "Fireplace Qu", "Foundation", "Garage Finish", "Garage Type", "Heating QC", "Kitchen Qual", "Lot Shape", "MS Zoning", "Paved Drive", "Roof Style", "Sale Condition", "Sale Type")
val label = Array("SalePrice")
val ATTR = NUMATTR++CATATTR++label

val dataDF = selectAttributes(df,ATTR)

/*      
 *          ELIMINACIÓN DE OUTLIERS
 *
 */

//Garage Yr Blt contiene una instancia con valor 2207, claramente incorrecta (representa un año en el que se ha construido el garaje)
//Asumimos que cualquier año mayor que 2019 (año actual) es incorrecto

val filteredDF = dataDF.filter($"Garage Yr Blt" < 2020)

/*
 *        CONVERSIÓN DE NULLS
 *          En el caso de las variables categóricas convertimos valores null en "UNKNOWN"
 */

val nonaDF = filteredDF.na.fill("UNKNOWN",CATATTR)

/*
 *        CONVERSIÓN DE ATRIBUTOS
 *
 */

//Creamos funciones que mappean valores para los atributos a convertir
def electricalMap(s:String):String = {
  if(s=="SBrkr"||s=="UNKNOWN")
    s
  else
    "Other"
}

def lotshapeMap(s:String):String = {
  if(s=="Reg"||s=="UNKNOWN")
    s
  else
    "IR"
}

def mszoningMap(s:String):String = {
  if(s=="RL"||s=="RM"||s=="FV"||s=="UNKNOWN")
    s
  else
    "Other"
}

def paveddriveMap(s:String):String = {
  if(s=="Y"||s=="UNKNOWN")
    s
  else
    "N/P"
}

def roofstyleMap(s:String):String = {
  if(s=="Gable"||s=="Hip"||s=="UNKNOWN")
    s
  else
    "Other"
}

def saleconditionMap(s:String):String = {
  if(s=="Normal"||s=="Partial"||s=="Abnorml"||s=="UNKNOWN")
    s
  else
    "Other"
}

def saletypeMap(s:String):String = {
  if(s=="New"||s=="UNKNOWN")
    s
  else
    "Not New"
}

//La función udf (user defined function) transforma los mapeados que toman y devuelven una String
//en funciones que trabajan con Columnas de DataFrame, convirtiendolos básicamente en Transformers
val map1 = udf(electricalMap _)
val map2 = udf(lotshapeMap _)
val map3 = udf(mszoningMap _)
val map4 = udf(paveddriveMap _)
val map5 = udf(roofstyleMap _)
val map6 = udf(saleconditionMap _)
val map7 = udf(saletypeMap _)

//Creamos nuevas columnas con las transformaciones
val transDF = filteredDF.withColumn("Electrical_",map1('Electrical)).withColumn("Lot Shape_",map2($"Lot Shape")).withColumn("MS Zoning_",map3($"MS Zoning")).withColumn("Paved Drive_",map4($"Paved Drive")).withColumn("Roof Style_",map5($"Roof Style")).withColumn("Sale Condition_",map6($"Sale Condition")).withColumn("Sale Type_",map7($"Sale Type"))

//Eliminamos las columnas antiguas y renombramos las nuevas por comodidad
val DF = transDF.drop("Electrical").withColumnRenamed("Electrical_","Electrical").drop("Lot Shape").withColumnRenamed("Lot Shape_","Lot Shape").drop("MS Zoning").withColumnRenamed("MS Zoning_","MS Zoning").drop("Paved Drive").withColumnRenamed("Paved Drive_", "Paved Drive").drop("Roof Style").withColumnRenamed("Roof Style_","Roof Style").drop("Sale Condition").withColumnRenamed("Sale Condition_","Sale Condition").drop("Sale Type").withColumnRenamed("Sale Type_","Sale Type")

/*
 *        
 *
 */

