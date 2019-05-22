# Proyecto

## Representados y elegidos atributos:

Numéricos:

- **1st Flr SF:** algunos outliers (a partir de ~2000) pero gráfico muestra relación lineal.
- **2nd Flr SF:** muchos valores 0 Q1 y Q2 son 0 (no tendrán 2ndo piso) pocos outliers a partir de ~1700. 
- **Bedroom AbvGr:** valores discretos. Ninguna instancia 7, pocas posibles outliers en 0, 5, 6 y 8.
- **Bsmt Full Bath:** Valores discretos. outliers en 3.
- **Bsmt Unf SF**: unos cuantos outliers sobre ~1700. nube de puntos con poca relación lineal.
- **BsmtFin SF 1**: *OUTLIERS*. relación con SalePrice.
- **Fireplaces:** Valores discretos. 3 y 4 posilbes outliers.
- **Full Bath:** Valores discretos. 4 posible outlier.
- **Garage Area:** Algunos 0s y posibles outliers. relación con SalePrice.
- **Garage Cars:** valores discretos. no exactamente igual a Garage Area. Outliers en 4 y 5.
- **Garage Yr Blt**: *valor incorrecto* claro por encima de 2200 (esta variable indica año). Sin dicha instancia cierta relación con SalePrice. *Finalmente no se utiliza, debido a que cuando no hay garaje este valor es NaN*.
- **Gr Liv Area**: Clara relación con SalePrice. Algunos posibles valores incorrectos/outliers (valores por encima de 4500 con precios de venta bajos).
- **Half Bath**: Valores discretos. 
- **Open Porch SF**: Varios posibles outliers. 
- **Overall Cond**: Valores discretos. Posibles outliers fuera de 4, 5, 6 y 7. Relación con SalePrice.
- **Overall Qual**: Valores discretos. Clara relación con SalePrice.
- **Total Bsmt SF**: *OUTLIERS* por encima de 5000 (con saleprice bajo).
- **TotRms AbvGrd**: Valores discretos.
- **Wood Deck SF**: *OUTLIER* en >1400. Muchos 0s.
- **Year Built**: Algunos posibles outliers con años bajos y precios altos.
- **Year Remod/Add**: Algunos posibles valores incorrectos en 1950.

Categóricos:

- **Bsmt Cond:** la mayoría valores TA. 80 Unknowns. Probablemente casas sin sótano.
- **Bsmt Exposure:** Mejor distribuido. 83 Uknowns.
- **Bsmt Qual**: Mejor distribuido. 80 Unknowns.
- **BsmtFin Type 1**: Mayoría GLQ y Unf. 
- **Central Air:** mayoría Y, N tiene precios mucho menores.
- **Electrical:** Mayoría SBrkr algunos FuseA. Precios mucho menores en no SBrkr. Podemos crear valores Sbrkr y no Sbrkr.
- **Exter Qual**: Mayoría Gd y TA, diferencias en precios.
- **Foundation:** Mayoría CBlock y PConc.
- **Garage Finish**: Mejor distribuido. Diferencias de price.
- **Garage Type:** Mayoría Attchd y Detchd. Distribuciones algo distintas.
- **Heating QC**: Mejor distribuido.
- **Kitchen Qual**: Casi todo TA y Gd. Distribuciones algo distntas.
- **Lot Shape**: Unir en dos valores, Reg o IR, con distribuciones algo distintas.
- **MS Zoning:** Mayoría RL o RM, con alguno FV. Distribuciones distintas. Juntar A, C, I y RH en un atributo debido a su poca ocurrencia y distribución no disimilar.
- **Paved Drive**: Mayoría Y pero distribuciones distintas entre Y y N/P.
- **Roof Style**: Distribuido en Gable, Hip y Other con diferencia en precio.
- **Sale Condition**: Distribuido en Normal, Partial, Abnorml y Other con diferencia en precio.
- **Sale Type**: Se podría convertir en el atributo New según si la casa es recién construida y vendida o no, ya que es donde se encuentra más diferencia en distribución de precios.

## Inicial eliminación de outliers centrada solo en claros valores incorrectos y valores ausentes:

- Garage Yr Blt: Eliminadas instancia con valor > 2019.
- Eliminada la instancia donde la suma de los SF de sótano no era igual a los SF de sótano total.
- Valores ausentes por atributo:
	- ~~Bsmt Full Bath: 2 NaN~~
	- ~~Garage Area: 1 NaN~~
	- ~~Garage Cars: 1 NaN~~
	- ~~Garage Yr Blt: 159 NaN~~
	- ~~Bsmt Cond: 79 NaN~~
    - ~~Bsmt Exposure: 82 NaN~~
	- ~~BsmtFin Type 1: 79 NaN~~
	- ~~Bsmt Qual: 79 NaN~~
	- ~~Garage Finish: 159 NaN~~
	- ~~Garage Type: 157 NaN~~

Véase el fichero `Valores ausentes.pdf`.


## Conversión de algunos atributos:

- **Electrical:** Mayoría SBrkr algunos FuseA. Precios mucho menores en no SBrkr. Podemos crear valores Sbrkr y no Sbrkr.
- **Lot Shape**: Unir en dos valores, Reg o IR, con distribuciones algo distintas.
- **MS Zoning:** Mayoría RL o RM, con alguno FV. Distribuciones distintas. Juntar A, C, I y RH en un atributo debido a su poca ocurrencia y distribución no disimilar.
- **Paved Drive**: Mayoría Y pero distribuciones distintas entre Y y N/P (juntar estas dos ultimas).
- **Roof Style**: Distribuido en Gable, Hip y Other con diferencia en precio.
- **Sale Condition**: Distribuido en Normal, Partial, Abnorml y Other con diferencia en precio.
- **Sale Type**: Se podría convertir en el atributo New según si la casa es recién construida y vendida o no, ya que es donde se encuentra más diferencia en distribución de precios.


## Consideración de nuevas variables o interacciones uniendo aquellas relacionadas:

- *Puede ser interesante considerar una variable que indique si la casa tiene piscina (se han eliminado todos los atributos al respecto)*

- Atributos que tienen que ver con **el sótano**: Bsmt Cond, Bsmt Exposure, BsmtFin Type 1, Bsmt Qual (categóricas), Bsmt Fin SF 1, Bsmt Full Bath, Bsmt Unf SF (continuas).
- Atributos que tienen que ver ocn **el garaje**: Garage Finish, Garage Type (Categóricas), Garage Area, Garage Cars y Garage Yr Blt (numéricas).
- Gr Liv Area cuenta el número de pies cuadrados en total sobre el suelo (asumimos que no cuenta sótano), y 1st Flr SF y 2nd Flr SF cuentan los pies cuadrados de plantas primera y segunda respectivamente.

