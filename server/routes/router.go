package routes

import (
	"github.com/gin-gonic/gin"
)

var db = make(map[string]string)

func NewRouter() *gin.Engine {
	router := gin.Default()

	defaultRoutes := router.Group("default/")
	addDefaultRoutes(defaultRoutes)

	return router
}
