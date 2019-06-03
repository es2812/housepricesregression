/*
 *      Proyecto de Regresión Lineal sobre el precio de venta de casas con el dataset
 *      AmesHousing de Kaggle.
 *
 *      Este fichero entrena el mejor modelo encontrado y exporta el modelo y 
 *      residuos.
 *
 *      @author: Esther Cuervo Fernández
 *      @date: 3-Junio-2019
 *
 */

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.udf
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, VectorAssembler, StringIndexer, StringIndexerModel}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}

val PATH = "data/AmesHousing_modified.csv"
val SEED = 961228
val SELECTEDVARS = Array("Overall Qual", "Neighborhood", "Gr Liv Area", "MS SubClass", "Bsmt Full Bath", "Exter Qual", "Bsmt Exposure", "Overall Cond", "Garage Cars", "Bsmt Qual", "Fireplaces", "Land Contour", "Exterior 1st", "Kitchen Qual", "Garage Qual", "Screen Porch", "Year Built", "Garage Cond", "Condition 1", "Bsmt Unf SF")

/*                      
 *                      LECTURA DE DATOS
 *
 */

//Leemos los datos directamente a un DataFrame, inferSchema funciona bastante bien en este caso
val df = spark.read.format("csv").option("header","true").option("inferSchema","true").load(PATH)

/*
 *        CONVERSIÓN DE ATRIBUTOS
 *    Los años se pasan al número de años que han pasado en el momento de la venta
 *
 */

val transformed = df.withColumn("Year Built_",$"Yr Sold" - $"Year Built").drop("Year Built").withColumnRenamed("Year Built_", "Year Built")

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

val TODELETE = df.columns.diff(SELECTEDVARS).diff(Array("SalePrice"))
val selected = deleteAttributes(transformed,TODELETE)

val NUMATTR = Array("Overall Qual", "Gr Liv Area", "Bsmt Full Bath", "Overall Cond", "Garage Cars", "Fireplaces", "Screen Porch", "Year Built", "Bsmt Unf SF")
val CATATTR = selected.columns.diff(NUMATTR).diff(Array("SalePrice"))


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

//Una pipeline reune todos los StringIndexers
val pipe = new Pipeline()
pipe.setStages(indexers.toArray)
val modelTransformed = pipe.fit(transformed)
val dataIndexed = modelTransformed.transform(selected)

/*
 *    INTERACCIONES
 */

val coninteracciones = dataIndexed.withColumn("Gr Liv Area*Land Contour",col("Gr Liv Area")*col("Land Contour_index")).withColumn("Gr Liv Area*Exterior 1st",col("Gr Liv Area")*col("Exterior 1st_index"))

var input_ohe = CATATTR.map(x=> x+"_index")
val output_ohe = CATATTR.map(x=> x+"_ohe")

val ohe = new OneHotEncoderEstimator().setInputCols(input_ohe).setOutputCols(output_ohe)
val oheModel = ohe.fit(coninteracciones)
val dataOhe = oheModel.transform(coninteracciones)

val feature_cols = output_ohe++NUMATTR++Array("Gr Liv Area*Land Contour", "Gr Liv Area*Exterior 1st")
val va = new VectorAssembler().setInputCols(feature_cols).setOutputCol("features")

val dataTransformed = va.transform(dataOhe)

//Separamos datos en train y test
val split = dataTransformed.randomSplit(Array(0.66,0.34), SEED)
val train = split(0)
val test = split(1)

//Modelo de regresión lineal:
val lr = new LinearRegression()
lr.setFeaturesCol("features")
lr.setLabelCol("SalePrice")
val lm = lr.fit(train)

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
coeficientesDF.write.csv("./coefs/forwardselection")

//Resto de métricas:
val sum = lm.summary
println("SUMMARY TRAINING")
println(s"MSE: ${sum.meanSquaredError}")
println(s"RMSE: ${sum.rootMeanSquaredError}")
println(s"R2: ${sum.r2}")
println(s"R2 ajustado: ${sum.r2adj}")

val residuals = lm.transform(test).select($"SalePrice",$"prediction",$"SalePrice"-$"prediction")
residuals.write.csv("./residuals")
lm.save("model/forwardselectionfinal")

import org.apache.spark.ml.evaluation.RegressionEvaluator 
val eval = new RegressionEvaluator()
eval.setLabelCol("SalePrice")
println("SUMMARY TEST")
eval.setMetricName("mse")
print(s"MSE: ${eval.evaluate(residuals)}")
eval.setMetricName("r2")
print(s"R2: ${eval.evaluate(residuals)}")
