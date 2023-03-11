package routes

import (
	"github.com/gin-gonic/gin"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	defaultRoutes := router.Group("default/")
	addDefaultRoutes(defaultRoutes)

	return router
}
