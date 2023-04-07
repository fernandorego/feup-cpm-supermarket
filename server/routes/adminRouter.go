package routes

import (
	"net/http"
	"server/controllers"

	"github.com/gin-gonic/gin"
)

func addAdminRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}

func addAdminSignedRoutes(rg *gin.RouterGroup) {
	rg.POST("/product", controllers.CreateUpdateProduct)
}
