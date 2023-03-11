package routes

import (
	"github.com/gin-gonic/gin"
	"server/controllers"
)

func addUserRoutes(rg *gin.RouterGroup) {
	rg.GET("/:id", controllers.GetUser)
}
