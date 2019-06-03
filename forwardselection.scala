/*
 *      Proyecto de Regresión Lineal sobre el precio de venta de casas con el dataset
 *      AmesHousing de Kaggle.
 *
 *      Implementación de Forward Selection
 *
 *      @author: Esther Cuervo Fernández
 *      @date: 29-Mayo-2019
 *
 */

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.udf
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, VectorAssembler, StringIndexer, StringIndexerModel}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.ml.evaluation.RegressionEvaluator
import util.control.Breaks._


val PATH = "data/AmesHousing_modified.csv"
val SEED = 961228

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

def deleteAttributes(df:DataFrame, attrs:Array[String]): DataFrame = {
    for(a<-attrs){
      if(!(df.columns contains a)){
        print(s"El DataFrame no contiene el atributo ${a}\n")
      }
    }

    val drops = attrs
    var newdf = df
    for(d<-drops){
        newdf = newdf.drop(d)   
    }
    newdf
}

val TODELETE = Array("Order", "PID", "Condition 2", "Heating", "Pool QC", "Roof Matl", "Street", "Utilities", "Lot Frontage", "Garage Yr Blt")
val selected = deleteAttributes(df,TODELETE)

val NUMATTR = Array("1st Flr SF", "2nd Flr SF", "3Ssn Porch", "Bedroom AbvGr", "Bsmt Full Bath", "Bsmt Half Bath", "Bsmt Unf SF", "BsmtFin SF 1", "BsmtFin SF 2","Enclosed Porch", "Fireplaces", "Full Bath", "Garage Area", "Garage Cars", "Gr Liv Area", "Half Bath", "Kitchen AbvGr", "Lot Area", "Low Qual Fin SF", "Mas Vnr Area", "Misc Val", "Open Porch SF", "Overall Cond", "Overall Qual", "Pool Area", "Screen Porch", "Total Bsmt SF", "TotRms AbvGrd", "Wood Deck SF", "Year Built", "Year Remod/Add")
val CATATTR = selected.columns.diff(NUMATTR).diff(Array("SalePrice"))


/*
 *        CONVERSIÓN DE ATRIBUTOS
 *    Los años se pasan al número de años que han pasado en el momento de la venta
 *
 */

val transformed = selected.withColumn("Year Built_",$"Yr Sold" - $"Year Built").drop("Year Built").withColumnRenamed("Year Built_", "Year Built").
withColumn("Year Remod/Add_", $"Yr Sold" - $"Year Remod/Add").drop("Year Remod/Add").withColumnRenamed("Year Remod/Add_","Year Remod/Add")

/*
 *   FORWARD SELECTION
 *
 */

//Empezamos por transformar todos los atributos categóricos a OHE (entrarán o no a ser features según el algoritmo)

var indexers:List[StringIndexer] = List()

for(col<-CATATTR){
  var si = new StringIndexer()
  si.setInputCol(col)
  si.setOutputCol(col+"_index")
  indexers = indexers:::List(si)
}


val input_ohe = CATATTR.map(x=> x+"_index")
val output_ohe = CATATTR.map(x=> x+"_ohe")

val ohe = new OneHotEncoderEstimator().setInputCols(input_ohe).setOutputCols(output_ohe)

//La pipeline de transformación se utiliza sobre la totalidad de los datos
var pipe = new Pipeline()
pipe.setStages(indexers.toArray ++ Array(ohe))
var modelTransformed = pipe.fit(transformed)
var dataTransformed = modelTransformed.transform(transformed)

/*
 *    FORWARD SELECTION SIMPLE
 */

//Partimos de un modelo sólo con intercept
var va = new VectorAssembler().setInputCols(Array()).setOutputCol("features")

var featureDF = va.transform(dataTransformed)

//Separamos datos en train y test
var split = featureDF.randomSplit(Array(0.66,0.34), SEED)
var train = split(0)
var test = split(1)

var lr = new LinearRegression()
lr.setLabelCol("SalePrice")
lr.setFeaturesCol("features")

//fit con el set de entrenamiento
var lm = lr.fit(train)

var residuals = lm.transform(test).select("SalePrice","prediction")

var eval = new RegressionEvaluator()
eval.setLabelCol("SalePrice")
eval.setMetricName("r2")

var mejorR2_global = eval.evaluate(residuals)
var mejormodelo:LinearRegressionModel = lm

println(s"R2 en test del modelo simple: ${mejorR2_total}")

var in_definitivo_names:Array[String] = Array()
var in_definitivo_features:Array[String] = Array()
var in_aux:Array[String] = Array()

var mejorR2_local = -99.9
var r2_local = -99.9
var mejorvariable_local = ""
var mejorvariable_local_name = ""
var mejormodelo_local:LinearRegressionModel = null

var attr_feature = ""
var numVariables = 1

val variablesSeleccionables = selected.columns.diff(Array("SalePrice"))

breakable{
while( numVariables <= variablesSeleccionables.length ){
  mejorR2_local = -99.9 //reiniciamos el máximo R2 local
  println(s"Eligiendo la ${numVariables} variable del modelo")

  for(attr_name <- variablesSeleccionables.diff(in_definitivo_names)){
    println(s"Comprobando ${attr_name}.")

    if(CATATTR.contains(attr_name)){
      //el atributo a probar es categórico
      attr_feature = attr_name+"_ohe"
      in_aux = in_definitivo_features ++ Array(attr_feature)
    }
    else{
      attr_feature = attr_name
      in_aux = in_definitivo_features ++ Array(attr_feature)
    }

    var va = new VectorAssembler().setInputCols(in_aux).setOutputCol("features")

    var featureDF = va.transform(dataTransformed)

    //Separamos datos en train y test
    var split = featureDF.randomSplit(Array(0.66,0.34), SEED)
    var train = split(0)
    var test = split(1)

    var lr = new LinearRegression()
    lr.setFeaturesCol("features")
    lr.setLabelCol("SalePrice")

    //fit con el set de entrenamiento
    var lm = lr.fit(train)

    var residuals = lm.transform(test).select("SalePrice","prediction")

    var eval = new RegressionEvaluator()
    eval.setLabelCol("SalePrice")
    eval.setMetricName("r2")
  
    r2_local = eval.evaluate(residuals)
    println(s"R2: ${r2_local}")
    if(r2_local > mejorR2_local){
      mejorR2_local = r2_local
      
      mejorvariable_local = attr_feature
      mejorvariable_local_name = attr_name

      mejormodelo_local = lm
    }
  }
  //Tras el bucle por todos los atributos, mejorR2_local contiene el mejor R2 encontrado para 
  //esta ronda de atributos a añadir, y mejorvariable_local la variable que proporciona el mejor R2
  //Sin embargo también queremos comprobar si añadir la mejor de estas variables mejora el R2 actual de manera significativa
  
  if(mejorR2_local - mejorR2_global > 0.001){
    
    println(s"R2 ${mejorR2_global} -> ${mejorR2_local}")
    println(s"Elegida ${mejorvariable_local_name}")

    mejorR2_global = mejorR2_local
    in_definitivo_features = in_definitivo_features ++ Array(mejorvariable_local)
    in_definitivo_names = in_definitivo_names ++ Array(mejorvariable_local_name)
    mejormodelo = mejormodelo_local
  }
  else{
    break
  }
  numVariables = numVariables + 1
}
}

println(s"Final de forward selection")
println(s"Variables:")
in_definitivo_names.foreach(println)
println(s"R2: ${mejorR2_global}")

/*
 *  FORWARD SELECTION CON INTERACCIÓN:
 *    Partiendo del mejor modelo encontrado en la anterior fase:
 */

val in_definitivo_indexes = Array("Overall Qual", "Neighborhood_index", "Gr Liv Area", "MS SubClass_index", "Bsmt Full Bath", "Exter Qual_index", "Bsmt Exposure_index", "Overall Cond", "Garage Cars", "Bsmt Qual_index", "Fireplaces", "Land Contour_index", "Exterior 1st_index", "Kitchen Qual_index", "Garage Qual_index", "Screen Porch", "Year Built", "Garage Cond_index", "Condition 1_index", "Bsmt Unf SF")
val in_definitivo_features = Array("Overall Qual", "Neighborhood_ohe", "Gr Liv Area", "MS SubClass_ohe", "Bsmt Full Bath", "Exter Qual_ohe", "Bsmt Exposure_ohe", "Overall Cond", "Garage Cars", "Bsmt Qual_ohe", "Fireplaces", "Land Contour_ohe", "Exterior 1st_ohe", "Kitchen Qual_ohe", "Garage Qual_ohe", "Screen Porch", "Year Built", "Garage Cond_ohe", "Condition 1_ohe", "Bsmt Unf SF")


var mejorR2_local = -99.9
var r2_local = -99.9
var mejorR2_global = 0.8635196161004577

val combinaciones = for {
    (x, idX) <- in_definitivo_indexes.zipWithIndex
    (y, idxY) <- in_definitivo_indexes.zipWithIndex
    if idX < idxY
} yield (x,y)

var dataCombined = dataTransformed
//preparación de los valores:
for((i,j) <- combinaciones){
  dataCombined = dataCombined.withColumn(i+"*"+j,col(i)*col(j))
}

val combinaciones_nombres = combinaciones.map((x)=>x._1+"*"+x._2)

var in_aux_comb:Array[String] = Array()
var in_definitivo_comb = in_definitivo_features

var mejorcombinacion_local= ""
var mejormodelo_local:LinearRegressionModel = null
var mejormodelo:LinearRegressionModel = null

var numCombinaciones = 1

breakable{
while( numCombinaciones <= combinaciones_nombres.length ){
  mejorR2_local = -99.9 //reiniciamos el máximo R2 local
  println(s"Eligiendo la ${numCombinaciones} combinacion del modelo")

  for(combinacion <- combinaciones_nombres.diff(in_definitivo_comb)){
    println(s"Comprobando ${combinacion}.")

    in_aux_comb = in_definitivo_comb ++ Array(combinacion)

    var va = new VectorAssembler().setInputCols(in_aux_comb).setOutputCol("features")

    var featureDF = va.transform(dataCombined)

    //Separamos datos en train y test
    var split = featureDF.randomSplit(Array(0.66,0.34), SEED)
    var train = split(0)
    var test = split(1)

    var lr = new LinearRegression()
    lr.setFeaturesCol("features")
    lr.setLabelCol("SalePrice")

    //fit con el set de entrenamiento
    var lm = lr.fit(train)

    var residuals = lm.transform(test).select("SalePrice","prediction")

    var eval = new RegressionEvaluator()
    eval.setLabelCol("SalePrice")
    eval.setMetricName("r2")
  
    r2_local = eval.evaluate(residuals)
    println(s"R2: ${r2_local}")
    if(r2_local > mejorR2_local){
      mejorR2_local = r2_local
      
      mejorcombinacion_local = combinacion

      mejormodelo_local = lm
    }
  }
  
  if(mejorR2_local - mejorR2_global > 0.001){
    
    println(s"R2 ${mejorR2_global} -> ${mejorR2_local}")
    println(s"Elegida ${mejorcombinacion_local}")

    mejorR2_global = mejorR2_local
    in_definitivo_comb = in_definitivo_comb ++ Array(mejorcombinacion_local)
    mejormodelo = mejormodelo_local
  }
  else{
    break
  }
  numCombinaciones = numCombinaciones+1
}
}

println("Fin de forward selection interacciones")
println(s"Variables:")
in_definitivo_comb.foreach(println)
println(s"R2: ${mejorR2_global}")
