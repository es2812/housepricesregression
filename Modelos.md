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
		

