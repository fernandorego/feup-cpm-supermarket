package routes

import (
	"github.com/gin-gonic/gin"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	defaultRoutes := router.Group("default/")
	addDefaultRoutes(defaultRoutes)

	authRoutes := router.Group("/")
	addAuthRoutes(authRoutes)

	return router
}
