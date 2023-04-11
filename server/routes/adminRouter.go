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

func addAdminSelfSignedRoutes(rg *gin.RouterGroup) {
	rg.POST("/product", controllers.CreateOrReplaceProduct)
	rg.DELETE("/product/:uuid", controllers.DeleteProduct)
}

func addAdminOthersSignedRoutes(rg *gin.RouterGroup) {
	rg.POST("/purchase", controllers.Purchase)
}
