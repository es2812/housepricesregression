1. Inicialmente se ha considerado un modelo con todos los valores, y parámetros:
	- regParam: 0.3
	- elasticNetParam: 0.8

	- SUMMARY TRAINING:
		- MSE: 3.917456391042362E8
		- RMSE: 19792.565248199542
		- R2: 0.9374708840042042
		- R2 ajustado: 0.9270174555493038
	- SUMMARY TEST
		- MSE: 1.2557728361149893E9
		- R2: 0.8092904312227241      

	- Coeficientes == 0:
		- MS Zoning, I (all)
		- Neighborhood, GrnHill
		- Exterior 1st, Stucco 
		- Exterior 1st, AsphShn
		- Misc Feature, TenC


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

3. ElasticNet entrenada:
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
		- MS Zoning, I (all)
		- Neighborhood, GrnHill
		- Exterior 1st, AsphShn
		- Exterior 2nd, HdBoard
		- Misc Feature, TenC 


5. Programación de forward selection:
	- No regularización.
	- TODO
