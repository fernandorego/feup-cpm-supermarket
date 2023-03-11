package main

import (
	"server/db"
	"server/routes"
)

func main() {
	port := "8000"

	//Init database
	client := db.InitDB()
	defer db.DisconnectDB(client)

	//Init router
	router := routes.NewRouter()

	// Listen and Server in 0.0.0.0:8080
	if router.Run(":"+port) != nil {
		return
	}
}
