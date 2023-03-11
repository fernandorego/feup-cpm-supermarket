package routes

import (
	"github.com/gin-gonic/gin"
	"server/controllers"
)

func addAuthRoutes(rg *gin.RouterGroup) {
	rg.POST("/register", controllers.Register)
	rg.POST("/login", controllers.Login)
	rg.POST("/logout", controllers.Logout)
}
