package routes

import (
	"github.com/gin-gonic/gin"
	"server/middlewares"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	defaultRoutes := router.Group("default/")
	addDefaultRoutes(defaultRoutes)

	authRoutes := router.Group("/")
	addAuthRoutes(authRoutes)

	userRoutes := router.Group("/user")
	userRoutes.Use(middlewares.AuthMiddleware)

	return router
}
