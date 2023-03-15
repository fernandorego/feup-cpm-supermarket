package main

import (
	"os"

	"server/db"
	"server/routes"

	"github.com/joho/godotenv"
)

func main() {
	// Get .env variables ()
	err := godotenv.Load(".env")
	if err != nil {
		panic(err)
	}

	//Init database
	client := db.InitDB()
	defer db.DisconnectDB(client)

	//Init router
	router := routes.NewRouter()

	// Listen and Server in 0.0.0.0:8080
	if router.Run(":"+os.Getenv("PORT")) != nil {
		return
	}
}
