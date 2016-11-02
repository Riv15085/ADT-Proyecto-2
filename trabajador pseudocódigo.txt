Algoritmo recomendation_engine
	Leer persona (nombre, edad, profesion, trabajo, localizacion, institucion_e, empleador, empleo)
	Leer persona.escogerAmigos
	Leer amigo = persona.amigo
	Si persona.trabajo = true Entonces
		Mientras amigo.localizacion <= persona.localizacion Hacer
			Mientras amigo.profesion != persona.profesion Hacer
				Escribir amigo = persona.amigo
			FinMientras
			Leer persona.recomendacionEmpleo = amigo.empleo
		FinMientras
		Leer print (recomendacion = persona.recomendacionEmpleo)
	FinSi
FinAlgoritmo

