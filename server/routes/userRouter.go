package routes

import (
	"server/controllers"

	"github.com/gin-gonic/gin"
)

func addUserRoutes(rg *gin.RouterGroup) {

	rg.GET("/user", controllers.GetUser)

	rg.GET("/product", controllers.GetProducts)
	rg.GET("/product/:uuid", controllers.GetProduct)
}
