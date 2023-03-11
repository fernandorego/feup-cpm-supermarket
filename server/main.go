package main

import (
	"server/db"
	"server/routes"
)

func main() {
	defer db.DisconnectDB()
	port := "8000"

	//Init database
	db.InitDB()

	//Init router
	router := routes.NewRouter()

	// Listen and Server in 0.0.0.0:8080
	if router.Run(":"+port) != nil {
		return
	}
}
