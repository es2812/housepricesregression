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
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, VectorAssembler, StringIndexer, StringIndexerModel}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}

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

val NUMATTR = Array("1st Flr SF", "2nd Flr SF", "3Ssn Porch", "Bedroom AbvGr", "Bsmt Full Bath", "Bsmt Half Bath", "Bsmt Unf SF", "BsmtFin SF 1", "BsmtFin SF 2",
"Enclosed Porch", "Fireplaces", "Full Bath", "Garage Area", "Garage Cars", "Gr Liv Area", "Half Bath", "Kitchen AbvGr", "Lot Area", "Low Qual Fin SF", 
"Mas Vnr Area", "Misc Val", "Open Porch SF", "Overall Cond", "Overall Qual", "Pool Area", "Screen Porch", "Total Bsmt SF", "TotRms AbvGrd", "Wood Deck SF", 
"Year Built", "Year Remod/Add")
val CATATTR = selected.columns.diff(NUMATTR).diff(Array("SalePrice"))


/*
 *        CONVERSIÓN DE ATRIBUTOS
 *    Los años se pasan al número de años que han pasado en el momento de la venta
 *
 */

val transformed = selected.withColumn("Year Built_",$"Yr Sold" - $"Year Built").drop("Year Built").withColumnRenamed("Year Built_", "Year Built").
withColumn("Year Remod/Add_", $"Yr Sold" - $"Year Remod/Add").drop("Year Remod/Add").withColumnRenamed("Year Remod/Add_","Year Remod/Add")

/*
 *    PREPARACIÓN DE PIPELINE 
 *
 */

var indexers:List[StringIndexer] = List()

for(col<-CATATTR){
  var si = new StringIndexer()
  si.setInputCol(col)
  si.setOutputCol(col+"_index")
  indexers = indexers:::List(si)
}

var input_ohe = CATATTR.map(x=> x+"_index")
val output_ohe = CATATTR.map(x=> x+"_ohe")

val ohe = new OneHotEncoderEstimator().setInputCols(input_ohe).setOutputCols(output_ohe)

val feature_cols = output_ohe++NUMATTR
val va = new VectorAssembler().setInputCols(feature_cols).setOutputCol("features")


//La pipeline de transformación se utiliza sobre la totalidad de los datos
val pipe = new Pipeline()
pipe.setStages(indexers.toArray ++ Array(ohe,va))
val modelTransformed = pipe.fit(transformed)
val dataTransformed = modelTransformed.transform(transformed)

//Separamos datos en train y test
val split = dataTransformed.randomSplit(Array(0.66,0.34), SEED)
val train = split(0)
val test = split(1)

//Modelo de regresión lineal:
val lr = new LinearRegression()
lr.setFeaturesCol("features")
lr.setLabelCol("SalePrice")

/*
 *                VALIDACIÓN CRUZADA
 *      Utilizada para entrenar regParam de LinearRegression
 *
 */

import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.ml.evaluation.RegressionEvaluator

val eval = new RegressionEvaluator()
eval.setLabelCol("SalePrice")
eval.setPredictionCol("prediction")
eval.setMetricName("mse")

val grid = new ParamGridBuilder().addGrid(lr.regParam,Array(0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1)).addGrid(lr.elasticNetParam,Array(0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1)).build()

val cv = new CrossValidator()
cv.setSeed(SEED)
cv.setNumFolds(2)
cv.setEvaluator(eval)
cv.setEstimator(lr)
cv.setEstimatorParamMaps(grid)

//fit con el set de entrenamiento
val cvModel = cv.fit(train)

//obtenemos el mejor modelo
val lm = cvModel.bestModel.asInstanceOf[LinearRegressionModel]

println(s"RegParam: ${lm.getRegParam}")
println(s"ElasticNetParam: ${lm.getElasticNetParam}")
println(s"Intercept: ${lm.intercept}")

println(s"Guardando coeficientes:")
// Preparamos los coeficientes. Hay que tener en cuenta que para cada variable
// convertida con OHE se le asignan n-1 coeficientes
// O lo que es lo mismo, tantos coeficientes como tamaño tenga el vector correspondiente
// a la variable

var coeficientes:Array[(String,String,Double)] = Array(("Intercept","",lm.intercept))
var numCoeficientes: Int = 1
import org.apache.spark.ml.linalg.SparseVector

var j = 0
for((f,fi) <- feature_cols.zipWithIndex){
  if(output_ohe contains f){ //si el nombre de la columna se encuentra en el output del OHE es categórica
    var y = dataTransformed.select(f).head
    numCoeficientes = y(0).asInstanceOf[SparseVector].size //el  tamaño del vector es el número de coeficientes asignados

    //para obtener el valor de la columna correspondiente a cada posición del array
    //utilizamos los StringIndexer de la Pipeline entrenada. El orden es el dado por el atributo labels 
    var labels = modelTransformed.stages(fi).asInstanceOf[StringIndexerModel].labels

    //asignamos los coeficientes
    for(i<- 0 to numCoeficientes){
      coeficientes = coeficientes ++ Array((f,labels(i),lm.coefficients(j+i))) 
    }
    //actualizamos el indexador del array de coeficientes
    j = j+numCoeficientes
  }
  else{
    //si es numérica sólo le corresponde un coeficiente
    coeficientes = coeficientes ++ Array((f,"",lm.coefficients(j)))
    j = j+1
  }
}

val coeficientesDF = sc.parallelize(coeficientes).toDF("Variable","Valor","Coeficiente") //con
coeficientesDF.write.csv("./coefs/3")

//Resto de métricas:
val sum = lm.summary
println("SUMMARY TRAINING")
println(s"MSE: ${sum.meanSquaredError}")
println(s"RMSE: ${sum.rootMeanSquaredError}")
println(s"R2: ${sum.r2}")
println(s"R2 ajustado: ${sum.r2adj}")

val residuals = lm.transform(test).select("SalePrice","prediction")


println("SUMMARY TEST")
eval.setMetricName("mse")
print(s"MSE: ${eval.evaluate(residuals)}")
eval.setMetricName("r2")
print(s"R2: ${eval.evaluate(residuals)}")
