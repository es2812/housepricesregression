1. Inicialmente se ha considerado un modelo con todos los valores, y parámetros:
	- regParam: 0.3
	- elasticNetParam: 0.8

	- SUMMARY TRAINING
		- MSE: 5.997511619687761E8
		- RMSE: 24489.817516036663
		- R2: 0.9076071520980906
		- R2 ajustado: 0.8924398355152756
	- SUMMARY TEST:
		- MSE: 6.461655336908767E8
		- R2: 0.8947262889318924
	
	- Coeficientes == 0:
		- MS Zoning, I (all) 
		- Condition 1, Artery
		- Exterior 2nd, Other

2. Lasso pura:
	- regParam: Cross Validation 2 folds mse
	- elasticNetParam: 1
	
	- regParam: 0.6
	- SUMMARY TRAINING:
		- MSE: 3.922923840998569E8
		- RMSE: 19806.372310442337
		- R2: 0.9373836144143514
		- R2 ajustado: 0.9269155964934193
	- SUMMARY TEST:	
		- MSE: 1.2256000103288534E9
		- R2: 0.8138726665036431

	- Coeficientes == 0:
		- MS Zoning, I (all)
		- Neighborhood, GrnHill
		- Exterior 1st, AsphShn
		- Misc Feature, TenC

2'. Lasso pura:
	- regParam: Cross Validation 2 folds r2 
	- elasticNetParam: 1

	- MISMOS RESULTADOS QUE 2

4. ElasticNet entrenada:
	- elasticNetParam: Cross Validation 2 folds mse
	- regParam: Cross Validation 2 folds mse

	- RegParam: 0.4
	- ElasticNetParam: 0.9
	- SUMMARY TRAINING
		- MSE: 3.9287448177254206E8
		- RMSE: 19821.06157027272
		- R2: 0.9372907019495708
		- R2 ajustado: 0.9268071512038468
	- TEST TRAINING
		- MSE: 1.2286718407220218E9
		- R2: 0.81340615900102

	- Coeficientes == 0:
		- 


5. Programación de forward selection:
	- No regularización.
	- TODO
