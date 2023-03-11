package main

import "server/routes"

func main() {
	port := "8000"
	router := routes.NewRouter()

	// Listen and Server in 0.0.0.0:8080
	if router.Run(":"+port) != nil {
		return
	}
}
