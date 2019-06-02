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

4. Lasso pura + regParam aumentado:
	- regParam: 0.7
	- elasticNetParam: 1
	
	- SUMMARY TRAINING
		- MSE: 3.9470605880213416E8
		- RMSE: 19867.21064473154
		- R2: 0.9369983518093101
		- R2 ajustado: 0.926465926851528
	- SUMMARY TEST:
		- MSE: 1.217782485164282E9
		- R2: 0.815059885091404

5. ...
	- regParam: 0.8
	- elasticNetParam: 1
	
	- SUMMARY TRAINING
		- MSE: 3.9059268968956614E8
		- RMSE: 19763.417965766097
		- R2: 0.9376549138962843
		- R2 ajustado: 0.9272322510020318
	- SUMMARY TEST
		- MSE: 1.2236599401321478E9
		- R2: 0.8141672977776848                                                          

6. ...
	- regParam: 0.9
	- elasticNetParam: 1

	- SUMMARY TRAINING
		- MSE: 3.87108476019999E8
		- RMSE: 19675.07245272553
		- R2: 0.9382110523160933
		- R2 ajustado: 0.9278813629604861
	- SUMMARY TEST
		- MSE: 1.2447015678996618E9
		- R2: 0.8109717837145448 

7. ...
	- regParam: 1
	- elasticNetParam: 1
	
	- SUMMARY TRAINING
		- MSE: 3.916858978417026E8
		- RMSE: 19791.056006229242
		- R2: 0.9374804197027846
		- R2 ajustado: 0.9270285853971264
	- SUMMARY TEST:
		- MSE: 1.233060006146431E9
		- R2: 0.8127397445733904                                                          

8. ...
	- regParam: 1.1
	- elasticNetParam: 1
	
	- SUMMARY TRAINING
		- MSE: 3.91670998387964E8
		- RMSE: 19790.679583782967
		- R2: 0.9374827979032752
		- R2 ajustado: 0.9270313611779807
	- SUMMARY TEST:
		- MSE: 1.2325039342578652E9
		- R2: 0.8128241931512125   

9. ...
	- regParam: 1.2
	- elasticNetParam: 1
	
	- SUMMARY TRAINING
		- MSE: 3.918986906200624E8
		- RMSE: 19796.431259700887
		- R2: 0.9374464544381003
		- R2 ajustado: 0.9269889419222407
	- SUMMARY TEST:
		- MSE: 1.2285083242965117E9
		- R2: 0.8134309916348373  

10. ...
	- regParam: 1.3
	- elasticNetParam: 1
		
	- SUMMARY TRAINING
		- MSE: 3.967110434538381E8
		- RMSE: 19917.606368583503
		- R2: 0.9366783229300025
		- R2 ajustado: 0.9260923965122994
	- SUMMARY TEST
		- MSE: 1.2077024487291727E9
		- **R2: 0.8165907028846493**

11. ...
	- regParam: 1.4
	- elasticNetParam: 1
		
	- SUMMARY TRAINING
		- MSE: 3.83627589380025E8
		- RMSE: 19586.41338734647
		- R2: 0.9387666596866739
		- R2 ajustado: 0.9285298550905086
	- SUMMARY TEST
		- MSE: 1.2541108134557552E9
		- R2: 0.8095428364472401

12. ...
	- regParam: 1.5
	- elasticNetParam: 1
		
	- SUMMARY TRAINING
		- MSE: 3.8783676347870487E8
		- RMSE: 19693.571628292946
		- R2: 0.9380948055313484
		- R2 ajustado: 0.9277456823899265
	- SUMMARY TEST
		- MSE: 1.266489221231984E9
		- R2: 0.8076629735124301

13. ...
	- regParam: 0.5
	- elasticNetParam: 1

	- SUMMARY TRAINING
		- MSE: 3.824634646243013E8
		- RMSE: 19556.673148168666
		- R2: 0.938952473348954
		- R2 ajustado: 0.9287467325187424

	- SUMMARY TEST
		- MSE: 1.275207701956865E9
		- R2: 0.806338930141195

14. ...
	- regParam: 0.4

	- SUMMARY TRAINING
		- MSE: 3.8611752392347896E8
		- RMSE: 19649.873381868874
		- R2: 0.9383692247433123
		- R2 ajustado: 0.9280659781755991

	- SUMMARY TEST:
		- MSE: 1.259570451872399E9
		- R2: 0.808713701385409
15. ...
	- regParam: 0.3

	- SUMMARY TRAINING
		- MSE: 3.917669140912109E8
		- RMSE: 19793.10268985666
		- R2: 0.9374674881626286
		- R2 ajustado: 0.9270134920012064
	- SUMMARY TEST
		- MSE: 1.2222080587412643E9
		- R2: 0.8143877896262166                                                          

16. ...
	- regParam: 0.2

	- SUMMARY TRAINING
		- MSE: 3.801560479857167E8
		- RMSE: 19497.59082516906
		- R2: 0.9393207754007002
		- R2 ajustado: 0.9291766061933464

	- SUMMARY TEST
		- MSE: 1.2741490392841387E9
		- R2: 0.8064997053196268                                                          

17. ...
	- regParam: 0.1

	- SUMMARY TRAINING
		- MSE: 3.814192467221975E8
		- RMSE: 19529.957673333483
		- R2: 0.9391191478841827
		- R2 ajustado: 0.9289412711985623

	- SUMMARY TEST
		- MSE: 1.272999177807705E9
		- R2: 0.8066743305225439                                                          

18. ...
	- regParam: 0
	
	- SUMMARY TRAINING
		- MSE: 3.635638067771761E8
		- RMSE: 19067.34923310464
		- R2: 0.9419691729107054
		- R2 ajustado: 0.9322677547873879

	- SUMMARY TEST
		- MSE: 1.5296361336076756E9
		- R2: 0.7676998267226833   

---


1. Forward selection simple.
	1. -7.612288956959645E-7 -> 0.6483352003571359            
		Elegida Overall Qual
	2. 0.6483352003571359 -> 0.7256410308057214               
		Elegida Neighborhood
	3. 0.7256410308057214 -> 0.7778021760462034               
		Elegida Gr Liv Area  
	4. 0.7778021760462034 -> 0.7969875775286029               
		Elegida MS SubClass
	5. 0.7978547795204888R2 0.7969875775286029 -> 0.8127813481863242               
		Elegida Bsmt Full Bath 
	6. 0.8127813481863242 -> 0.8252483582477492               
		Elegida Exter Qual
	7. 0.8252483582477492 -> 0.8312800177691797               
		Elegida Bsmt Exposure
	8. 0.8312800177691797 -> 0.8362524715298434               
		Elegida Overall Cond
	9. 0.8362524715298434 -> 0.8412226821523066               
		Elegida Garage Cars
	10. 0.8412226821523066 -> 0.8457554114251817               
		Elegida Bsmt Qual
	11. 0.8457554114251817 -> 0.8481860230942235               
		Elegida Fireplaces
	12. 0.8481860230942235 -> 0.8505401066563936               
		Elegida Land Contour
	13. 0.8505401066563936 -> 0.8525964025953936               
		Elegida Exterior 1st
	14. 0.8525964025953936 -> 0.8547715382319983               
		Elegida Kitchen Qual
	15. 0.8547715382319983 -> 0.8569862677279183                
		Elegida Garage Qual
	16. 0.8569862677279183 -> 0.8588145700415541               
		Elegida Screen Porch
	17. 0.8588145700415541 -> 0.8599550994507006               
		Elegida Year Built
	18. 0.8599550994507006 -> 0.8610100934777516               
		Elegida Garage Cond
	19. 0.8610100934777516 -> 0.8624757370855596               
		Elegida Condition 1
	20. 0.8624757370855596 -> 0.8635196161004577               
		Elegida Bsmt Unf SF
	
	R2 test final: 0.863. R2 training: 0.9050394329070544. R2 ajustado training: 0.899614619590042

2. 
