package routes

import (
	"server/controllers"

	"net/http"

	"github.com/gin-gonic/gin"
)

func addGetTokenRoute(rg *gin.RouterGroup) {
	rg.GET("/", controllers.GetUser)
}

func addUserRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}
