package routes

import (
	"server/controllers"

	"github.com/gin-gonic/gin"
)

func addAuthRoutes(rg *gin.RouterGroup) {
	rg.POST("/register", controllers.Register)
	rg.POST("/getToken", controllers.GenerateToken)
}
