package routes

import (
	"net/http"
	"server/controllers"

	"github.com/gin-gonic/gin"
)

func addClientRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}

func addClientSignedRoutes(rg *gin.RouterGroup) {
	rg.GET("/purchases/:uuid", controllers.GetPurchases)
}
