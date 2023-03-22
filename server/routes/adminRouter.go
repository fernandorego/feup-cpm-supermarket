package routes

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func addAdminRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}
